package com.kbindiedev.verse.net.rest;

import com.sun.istack.internal.Nullable;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpCookie;
import java.net.HttpURLConnection;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class RestResponse {

    //according to HTTP 1.1, ISO-8859-1 is the default charset, however many implementations may sway from this.
    //many implementations also use cp1252 due to its similarity to ISO-8859-1.
    //UTF-8 is probably also very common.
    private static final String DEFAULT_ENCODING = "cp1252";

    private HttpURLConnection connection;

    private int status;
    private String reason;
    private HashMap<String, List<String>> headers;
    private @Nullable String encoding = null;

    RestResponse(HttpURLConnection connection) {
        this.connection = connection;
        try { handle(); } catch (IOException e) { e.printStackTrace(); }
    }

    /**
     * Get the response from the server in InputStream (binary) form.
     * A SocketTimeoutException may be thrown when reading from the returned input stream if the read timeout
     *      defined by request configuration expires before data is available to read (also between read calls).
     * Unless specified otherwise by {@see #configure(Object)}, the stream will automatically close upon reading a -1.
     * @throws java.io.IOException - If an IO error occurs whilst creating the input stream (ex. already closed).
     * @return an InputStream that reads binary data of a REST response to a REST request.
     */
    public InputStream stream() throws IOException {
        return connection.getInputStream();
    }

    public RestResponse configure(Configuration config) {
        //TODO: here.
        return this;
    }

    //TODO: auto close stream
    //TODO: verse exceptions ?

    //TODO: temp (works)
    private void parseStreamToString(InputStream stream) throws IOException {
        StringBuilder sb = new StringBuilder();
        int d;
        while ((d = stream.read()) != -1) {
            sb.append((char)d);
        }
        System.out.println("parsed: " + sb.toString());
    }

    private void handle() throws IOException {
        //TODO: handle cookies

        //immediate info
        status = connection.getResponseCode();
        reason = connection.getResponseMessage();

        //store headers
        headers = new HashMap<>();
        connection.getHeaderFields().entrySet().stream().filter(m -> m.getKey() != null)
                .forEach(e -> headers.put(e.getKey(), e.getValue()));

        //parse encoding
        List<String> ct1 = headers.get("Content-Type");
        if (ct1 != null) {
            String ct2 = ct1.get(0);
            int index = ct2.indexOf("charset=");
            if (index >= 0) {
                try { encoding = ct2.substring(index + 8); } catch (StringIndexOutOfBoundsException ignore) {}
            }
        }

        //set default encoding
        if (encoding == null) encoding = DEFAULT_ENCODING;

        //TODO: if preserve cookies
        //check cookie headers
        List<String> cookieHeaders = headers.get("Set-Cookie");
        if (cookieHeaders != null) {
            ArrayList<HttpCookie> cookies = new ArrayList<>();
            cookieHeaders.forEach(cookieHeader -> {
                List<HttpCookie> c = HttpCookie.parse(cookieHeader);
                cookies.addAll(c);
            });

            //TODO: something like this
            //cookies.forEach(cookie -> cookieManager.add(cookie));

            System.out.println(cookies);
        }

        System.out.println("CHARSET = " + encoding);


        parseStreamToString(connection.getInputStream());
        connection.getInputStream().close();

        connection.disconnect();
    }

    public static final Configuration DEFAULT_STREAM, DEFAULT_STRINGIFY;

    static {
        DEFAULT_STREAM = new Configuration();
        DEFAULT_STREAM.setCloseOnEnd(false);
        DEFAULT_STRINGIFY = new Configuration();
        DEFAULT_STRINGIFY.setParseString(true);
    }


    public static class Configuration {

        private int bufferSize = 8192;
        private boolean parseString = false;
        private boolean closeOnEnd = true;
        private boolean autocloseConnection = true;

        /**
         * Set the size of the buffer used for buffering the response content.
         * The default size is 8192 (8KiB).
         * @param size - The buffer size in bytes.
         */
        public void setBufferSize(int size) { bufferSize = size; }

        /**
         * Define whether the response content should be immediately parsed into a string.
         * Note: RestResponse.stream() will throw an IOException if this is {@code true}.
         * The default value is false.
         * @param state - Whether or not to immediately parse the response to a string.
         */
        public void setParseString(boolean state) { parseString = state; }

        /**
         * Define whether the connection should be closed upon running RestResponse.end().
         * If {@code false}, then you will have to call RestResponse.stream().close() yourself.
         *      This will also close the connection (though details will still be available to RestResponse).
         * The default value is true.
         * @param state - Whether or not the connection should be closed upon running RestResponse.end().
         */
        public void setCloseOnEnd(boolean state) { parseString = state; }

        //TODO: look into: https://stackoverflow.com/questions/4767553/safe-use-of-httpurlconnection
        //think this should always be true (any reason not to?)
        /**
         * Define whether to automatically close the response stream and connection upon reaching the end of
         *      the response stream.
         * The default value is true.
         * @param state - Whether or not to automatically close the response stream and connection upon reaching
         *                  the end of the response stream.
         */
        //public void setAutocloseConnection(boolean state) { autocloseConnection = state; }

    }



}
