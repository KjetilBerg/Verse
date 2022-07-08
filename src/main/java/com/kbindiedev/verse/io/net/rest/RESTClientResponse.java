package com.kbindiedev.verse.io.net.rest;

import com.kbindiedev.verse.profiling.Assertions;
import com.kbindiedev.verse.util.CallbackInputStream;
import com.kbindiedev.verse.util.StreamUtil;
import com.sun.istack.internal.Nullable;

import java.io.*;
import java.net.*;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

/**
 * The RESTClientResponse class is responsible for handling a HttpURLConnection after any request data has been written.
 *      In other words: parse the response (like getting status code or body content).
 *
 * Note that RESTClientResponse is responsible for notifying the owning client of any cookies it encounters via
 *      any 'Set-Cookie' header. See the settings section below (in this comment) for further details.
 *
 * //TODO see TODO FUTURE in RESTClientRequest (not implemented). This section does not apply for now
 * Note that RESTClientResponse should handle any redirects (3xx) in accordance to the generating request's redirect settings.
 *      Any countered cookies should be handled as normal (see section about cookies above).
 *
 * Any RESTClientResponse SHOULD exist in accordance to the generating request's settings {@see RESTClientRequest#getSettings()}.
 * Specifically, these fields should be considered:
 * - bufferSize = the amount of bytes that should be buffered from the response body.
 * - reportCookies = whether or not you should call the request's client's reportSetCookie method (report cookies).
 * - defaultCacheSockets = whether or not to "cache by default" when .disconnect() is called without parameters.
 * - shouldDrainSockets = whether or not to "drain by default" when .disconnect() is called without parameters.
 */
public class RESTClientResponse implements Closeable {

    //according to HTTP 1.1, ISO-8859-1 is the default charset,
    //      however people have noted that a lot of implementations sway from this.
    //many implementations apparently use cp1252 due to its similarity to ISO-8859-1.
    //UTF-8 is also mentioned to be common.
    private static final String DEFAULT_CHARSET = "cp1252";

    private RESTClientRequest request;
    private HttpURLConnection connection;

    private int status;
    private String reason;
    private HashMap<String, List<String>> headers;
    private String charset;         //will be parsed from first received 'Content-Type' header or set to default.
    private boolean didProvideCharset;

    private long unixCommunicationEstablished;  //gets set upon the first sign of communication.

    private URL destinationURL;     //final destination URL (after considering all redirects, etc)
    private URI destinationURI;     //final destination URI (after considering all redirects, etc)

    private boolean shouldTryCache; //from request settings: .disconnect behaviour
    private boolean shouldDrain;    //from request settings: .disconnect behaviour

    private InputStream responseStream; //will be a CallbackInputStream of a BufferedInputStream
    private @Nullable String contentString = null;  //cached content
    private boolean disconnected = false;
    private boolean socketClosed = false;

    /**
     * Details this RESTClientResponse's streaming strategy.
     * Describing this is necessary to avoid hiccups, since only one strategy may be used for this object.
     *
     * STREAM_MODE = the user reads the stream as they please (read as input-stream).
     * STRING_CACHE_MODE = the stream is saved to a string.
     */
    private enum RestResponseStrategy { STREAM_MODE, STRING_CACHE_MODE }
    private @Nullable RestResponseStrategy streamingStategy = null;

    /** Create a RESTClientResponse object. It is assumed that {@code connection} is in or entering the 'connected' state. */
    RESTClientResponse(RESTClientRequest request, HttpURLConnection connection) throws IOException {
        this.request = request;
        this.connection = connection;

        RESTClientRequestSettings requestSettings = request.getSettings();

        //immediate info
        status = connection.getResponseCode();
        reason = connection.getResponseMessage();

        //communication established (got status and reason)
        unixCommunicationEstablished = System.currentTimeMillis();

        //urls
        destinationURL = connection.getURL();
        try { destinationURI = new URI(destinationURL.toString()); } catch (URISyntaxException e) {
            Assertions.error("uri-creation fault from URL source: HttpURLConnection.getURL(). url was: %s", destinationURL.toString());
        }   //TODO: Assertions.fatal

        //prepare stream
        InputStream targetStream;
        if (status < 400) targetStream = connection.getInputStream(); else targetStream = connection.getErrorStream();
        BufferedInputStream bis = new BufferedInputStream(targetStream, requestSettings.bufferSize);
        CallbackInputStream cis = new CallbackInputStream(bis);
        cis.setCallback(CallbackInputStream.Event.POST_CLOSE, () -> { socketClosed = true; this.close(); });
        responseStream = cis;

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
        if (requestSettings.reportCookies) {
            List<String> cookieHeaders = headers.get("Set-Cookie");
            if (cookieHeaders != null) {

                //Due to the old and deprecated Set-Cookie2 header, HttpCookie.parse returns List<HttpCookie>
                List<HttpCookie> cookies = cookieHeaders.stream().map(HttpCookie::parse).flatMap(Collection::stream).collect(Collectors.toList());

                //the client may still determine whether or not to preserve the cookies
                cookies.forEach(cookie -> request.getClient().reportSetCookie(this, cookie));
            }
        }

        //set behaviour settings
        shouldDrain = requestSettings.shouldDrainSockets;
        shouldTryCache = requestSettings.defaultCacheSockets;

    }

    /** @return the request object that created this response object. */
    public RESTClientRequest getRequest() { return request; }

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

    /** @return the timestamp, in unix milliseconds, of when communication was established (status code was received). */
    public long getUnixCommunicationEstablished() { return unixCommunicationEstablished; }

    /** @return the URL of the site I am connected to (will reflect/update upon redirects). */
    public URL getDestinationURL() { return destinationURL; }

    /** @return the URI of the site I am connected to (will reflect/update upon redirects). */
    public URI getDestinationURI() { return destinationURI; }

    //Note: per HttpURLConnection, this is the InputStream if status < 400, otherwise the error stream.
    /**
     * Get the response (body content) from the server as an InputStream (streaming bytes).
     * A SocketTimeoutException may be thrown when reading from the returned input stream if the read timeout
     *      defined by request configuration expires before data is available to read (also between read calls).
     * Upon closing the provided stream with .close(), this RESTClientResponse's .disconnect() method
     *      will automatically be run.
     * @return an InputStream of the response data that came from the server (response to RESTClientRequest).
     */
    public InputStream stream() {
        if (streamingStategy == null) streamingStategy = RestResponseStrategy.STREAM_MODE;
        if (streamingStategy != RestResponseStrategy.STREAM_MODE)
            throw new IllegalStateException("cannot use .stream() after .contentAsString() has been called!");
        return responseStream;
    }

    /**
     * Read the contents of the response into a string.
     * This will close the connection (and disconnect) upon finishing the stream.
     * This method is incompatible with {@see #stream()}.
     *      If either method is run, then the other will throw an exception.
     * Once this method runs successfully, the result is cached, and so any further calls to this method
     *      will return the cached content and no further exceptions will be thrown.
     * @return the stringified value of the response stream.
     * @throws IllegalStateException - if {@see #stream()} has been called before this.
     * @throws IOException - if an I/O-error occurs.
     */
    public String contentAsString() throws IOException {
        if (streamingStategy == null) streamingStategy = RestResponseStrategy.STRING_CACHE_MODE;
        if (streamingStategy != RestResponseStrategy.STRING_CACHE_MODE)
            throw new IllegalStateException("cannot use .contentAsString() after .stream() has been called!");
        if (contentString == null) {
            contentString = StreamUtil.streamToString(responseStream, charset, false);
            responseStream.close();
        }
        return contentString;
    }

    @Override
    public void close() {
        disconnect();
    }


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
     *       adhere to the java JDK implementation. (see .disconnect() comments regarding caching).
     */

    /**
     * The default value of {@code drainStream} gets set to the request's setting
     *      {@see RESTClientRequestSetting} regarding draining upon creation.
     * The default value of {@code cacheSocket} gets set to the request's setting
     *      {@see RESTClientRequestSetting} regarding caching upon creation.
     * @see #disconnect(boolean, boolean)
     */
    public void disconnect() { disconnect(shouldDrain, shouldTryCache); }

    //Note (per java): This socket-caching is determined by the global system setting: "http.Keepalive".
    /**
     * Close this connection to the server.
     * This will also close the InputStream from {@see #stream()} if it is open.
     * After running this method, running it again will have no effect.
     *
     * Whenever the InputStream from {@see #stream()} is closed, this method is automatically run.
     *      Thereby it is not necessary to run this method if you close the stream yourself, however,
     *      it is recommended you do so regardless for semantic reasons.
     *
     * The socket may be cached depending on the provided values.
     *
     * Note that the value of {@code cacheSocket} will be overwritten to {@code true} if:
     *      - the stream from {@see #stream()} was closed prior to .disconnect being called (will be cached).
     *      - {@code drainStream} is true (due to previous point).
     * @param drainStream - Whether or not to drain the stream of all data (read until end).
     *                    Does nothing if the socket is already closed or drained.
     * @param cacheSocket - Whether or not to cache the socket for future connections to the same server.
     *                    Note: value may be overwritten. see other comment details.
     */
    public void disconnect(boolean drainStream, boolean cacheSocket) {

        if (disconnected) return;
        disconnected = true;

        if (socketClosed) cacheSocket = true;
        if (drainStream) cacheSocket = true;

        //newer versions of java do not require streams to be drained to be cached (unsure version, but after java 6).
        try { if (drainStream) drain(false); } catch (IOException e) {
            Assertions.warn("error whilst draining stream: %s", e);
        }

        //cache the socket by closing stream. if already closed, then no need to try again.
        if (cacheSocket && !socketClosed) {
            try { responseStream.close(); } catch (IOException e) {
                Assertions.warn("unable to close the connection's InputStream: %s", e);
            }
        }

        connection.disconnect(); //will close the stream if not already closed.
    }

    /**
     * Drain the stream of this response {@see #stream()}.
     * @param close - Whether or not to close the stream after draining it.
     * @throws IOException - If there was an I/O error during the drainage or closing process.
     */
    public void drain(boolean close) throws IOException {

        if (socketClosed) return;

        while (responseStream.read() != -1);
        if (close) responseStream.close();
    }

}
