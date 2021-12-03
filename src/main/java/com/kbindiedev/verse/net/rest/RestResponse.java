package com.kbindiedev.verse.net.rest;

import com.kbindiedev.verse.profiling.Assertions;
import com.kbindiedev.verse.profiling.EngineWarning;
import com.sun.istack.internal.Nullable;

import java.io.*;
import java.net.HttpCookie;
import java.net.HttpURLConnection;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

//TODO: write contract of what RestResponse is responsible for (specifically regarding cookie handling)
public class RestResponse {

    //according to HTTP 1.1, ISO-8859-1 is the default charset,
    //      however people have noted that a lot of implementations sway from this.
    //many implementations apparently use cp1252 due to its similarity to ISO-8859-1.
    //UTF-8 is also mentioned to be common.
    private static final String DEFAULT_CHARSET = "cp1252";

    private RestRequest request;
    private HttpURLConnection connection;

    private int status;
    private String reason;
    private HashMap<String, List<String>> headers;
    private String charset;                         //will be parsed from first received 'Content-Type' header.
    private boolean didProvideCharset;

    private BufferedInputStream responseStream;
    private @Nullable String contentString = null;  //cached content

    /**
     * Details this RestResponse's streaming strategy.
     * Describing this is necessary to avoid hiccups, since only one strategy may be used for this object.
     *
     * STREAM_MODE = the user reads the stream as they please (read as input-stream).
     * STRING_CACHE_MODE = the stream is saved to a string.
     */
    private enum RestResponseStrategy { STREAM_MODE, STRING_CACHE_MODE }
    private @Nullable RestResponseStrategy streamingStategy = null;

    /** Create a RestResponse object. It is assumed that {@code connection} is in or entering the 'connected' state. */
    RestResponse(RestRequest request, HttpURLConnection connection) throws IOException {
        this.request = request;
        this.connection = connection;

        //immediate info
        status = connection.getResponseCode();
        reason = connection.getResponseMessage();

        responseStream = new BufferedInputStream(connection.getInputStream(), 8192);    //TODO: size

        //store headers
        headers = new HashMap<>();
        connection.getHeaderFields().entrySet().stream().filter(m -> m.getKey() != null)
                .forEach(e -> headers.put(e.getKey(), e.getValue()));

        //try parse charset (leave as DEFAULT_CHARSET if failure)
        charset = DEFAULT_CHARSET;
        List<String> ct1 = headers.get("Content-Type");
        if (ct1 != null) {
            String ct2 = ct1.get(0);
            int index = ct2.indexOf("charset=");
            if (index >= 0) { charset = ct2.substring(index + 8); didProvideCharset = true; }
        }

        //report cookies
        if (request.shouldReportCookies()) {
            List<String> cookieHeaders = headers.get("Set-Cookie");
            if (cookieHeaders != null) {

                //Due to the old and deprecated Set-Cookie2 header, HttpCookie.parse returns List<HttpCookie>
                List<HttpCookie> cookies = cookieHeaders.stream().map(HttpCookie::parse).flatMap(Collection::stream).collect(Collectors.toList());

                //the proxy may still determine whether or not to preserve the cookies
                cookies.forEach(cookie -> request.getApiProxy().reportSetCookie(this, cookie));
            }
        }


        //disconnect(true, false); //TODO: set to default. temp for testing
    }

    /** @return the request object that created this response object. */
    public RestRequest getRequest() { return request; }

    /**
     * Set the charset (used in {@see #contentAsString()} and {@see getCharset()}).
     * If the server responded with its own charset, then that will be overwritten.
     * The value of {@see didProvideCharset()} remains unchanged after this operation.
     * @param charset - The new charset.
     */
    public void setCharset(String charset) { this.charset = charset; }

    /** @return the charset associated with this response. */
    public String getCharset() { return charset; }

    /** @return true if the server responded with a charset, false if we had to use the DEFAULT_CHARSET instead. */
    public boolean didProvideCharset() { return didProvideCharset; }

    /** @return the status code the server responded with. */
    public int getStatusCode() { return status; }

    /** @return the status reason, if any, the server responded with. may be null. */
    public String getReason() { return reason; }

    /**
     * Get the response from the server in InputStream (byte stream) form.
     * A SocketTimeoutException may be thrown when reading from the returned input stream if the read timeout
     *      defined by request configuration expires before data is available to read (also between read calls).
     * Unless specified otherwise by {@see #configure(Object)}, the stream will automatically close upon reading a -1. //TODO: here
     * @return an InputStream that reads binary data of a REST response to a REST request.
     */
    public InputStream stream() {
        if (streamingStategy == null) streamingStategy = RestResponseStrategy.STREAM_MODE;
        if (streamingStategy != RestResponseStrategy.STREAM_MODE)
            throw new IllegalStateException("cannot use .stream() after .contentAsString() has been called!");
        return responseStream;
    }

    /**
     * Read the contents of the response into a string.
     * This will close the connection upon disconnect, with current disconnect-settings. //TODO: link to settings
     * This method is incompatible with {@see #stream()}.
     *      If either method is run, then the other will throw an exception.
     * Once this method runs successfully, the result is cached, and so no further exceptions will be thrown.
     * @throws IllegalStateException - if {@see #stream()} has been called before this.
     * @throws IOException - if an I/O-error occurs.
     * @return the stringified value of the response stream.
     */
    public String contentAsString() throws IOException {
        if (streamingStategy == null) streamingStategy = RestResponseStrategy.STRING_CACHE_MODE;
        if (streamingStategy != RestResponseStrategy.STRING_CACHE_MODE)
            throw new IllegalStateException("cannot use .contentAsString() after .stream() has been called!");
        if (contentString == null) {
            contentString = streamToString(responseStream, charset, false);
            responseStream.close();
        }
        return contentString;
    }

    //TODO: auto close stream
    //TODO: verse exceptions ?


    //TODO: move another class
    //https://stackoverflow.com/questions/309424/how-do-i-read-convert-an-inputstream-into-a-string-in-java?page=1&tab=votes#tab-top
    //also throws UnsupportedEncodingException (sub of IOException)
    //does not close stream
    private String streamToString(InputStream stream, String charset, boolean buffer) throws IOException {
        if (buffer) stream = new BufferedInputStream(stream);
        ByteArrayOutputStream buf = new ByteArrayOutputStream();
        int d;
        while ((d = stream.read()) != -1) buf.write((byte)d);
        return buf.toString(charset);
    }


    //TODO: last statement, check RestApiConnection correctly spelled
    /*
     * There is a TON of conflicting documentation of this regarding the java JDK implementation
     *      of HttpURLConnection.disconnect().
     *
     * The official HttpURLConnection seems to indicate that .disconnect() means "do not cache".
     *
     * https://developer.android.com/reference/java/net/HttpURLConnection?hl=zh-cn seems to indicate that
     *       .disconnect() specifically means "do cache" ("may return the socket to a pool ...").
     *
     * https://docs.oracle.com/javase/6/docs/technotes/guides/net/http-keepalive.html seems to indicate that
     *       the connection is put into cache regardless (and that .disconnect() is unnecessary).
     *
     * https://stackoverflow.com/questions/4767553/safe-use-of-httpurlconnection seems to indicate that
     *       .disconnect() will "cache" the socket only if the InputStream is closed.
     *
     *
     * This is my interpretation / assumption:
     *       calling .disconnect() when the InputStream is closed, will cache the socket,
     *       and otherwise will close the InputStream and not cache the socket.
     *
     *       If this is wrong, then the fault is not breaking (just less efficient).
     *       Regardless, the Verse engine acts in accordance to its own definitions, and do not specifically
     *       adhere to the java JDK implementation. (see RestApiConnection configuration regarding caching).
     */

    /**
     * {@code drainStream} defaults to {@code false}
     * {@code cacheSocket} defaults to {@code true}
     * @see #disconnect(boolean, boolean)
     */
    public void disconnect() { disconnect(false, true); }

    /**
     * Close this connection to the server.
     * This will close the InputStream from {@see #stream()} if it is open.
     *
     * The socket may be cached depending on the provided values.
     *
     * Note that the value of {@code cacheSocket} will be overwritten to {@code true} if:
     *      - the stream from {@see #stream()} was closed prior to.disconnect being called (will be cached).//TODO: see global http.Keepalive settings
     *      - {@code drainStream} is true.
     * @param drainStream - Whether or not to drain the stream of all data (read until end).
     *                    Does nothing if the socket is already closed or drained.
     * @param cacheSocket - Whether or not to cache the socket for future connections to the same server.
     *                    Note: value may be overwritten. see other comment details.
     */
    public void disconnect(boolean drainStream, boolean cacheSocket) {

        //TODO: if closed, cacheSocket = true;
        if (drainStream) cacheSocket = true;

        //newer versions of java do not require streams to be drained to be cached (unsure version, but after java 6).
        if (drainStream) drain();   //TODO: if .drain() calls .disconnect(), have some private version with boolean: disconnect=false

        if (cacheSocket) {
            try { connection.getInputStream().close(); } catch (IOException e) {
                Assertions.warn("unable to close connection inputstream: %s", e);
            }
        }

        connection.disconnect();
    }

    //TODO: clean
    /** Drain the stream of this response {@see #stream()}. Will move onto the errorStream if stream unavailable. */
    public void drain() {
        InputStream stream;

        try {
            stream = connection.getInputStream();
        } catch (IOException e) {
            new EngineWarning("connection .getInputStream caused an IOException: %s", e).print(); //TODO: Assertions.info ?
            stream = connection.getErrorStream();
        }

        if (stream == null) return;

        try {
            while (stream.read() != -1);
        } catch (IOException e) {
            System.out.println("ERR: " + e);
            //Assertions.warn("unable to drain stream: %s", e); //TODO: stream "closed" tracker
        }
    }

    //TODO: stream.close() should be redirected to RestResponse.disconnect(); (also allows user to read stream elsewhere)



}
