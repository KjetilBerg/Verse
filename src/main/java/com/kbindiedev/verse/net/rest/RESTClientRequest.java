package com.kbindiedev.verse.net.rest;

import com.kbindiedev.verse.profiling.Assertions;
import com.kbindiedev.verse.util.IOutputStreamWriter;
import com.sun.istack.internal.Nullable;

import java.io.*;
import java.net.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/** Class responsible for the nitty-gritty details of dealing with REST requests {@see RESTClient} */
public class RESTClientRequest {

    //Note on headers, from HTTP RFC 2616:
    /*
    * Multiple message-header fields with the same field-name MAY be present in a message if and only if
    * the entire field-value for that header field is defined as a comma-separated list [i.e., #(values)].
    * It MUST be possible to combine the multiple header fields into one "field-name: field-value" pair,
    * without changing the semantics of the message, by appending each subsequent field-value to the first,
    * each separated by a comma. The order in which header fields with the same field-name are received is
    * therefore significant to the interpretation of the combined field value, and thus a proxy MUST NOT
    * change the order of these field values when a message is forwarded.
    */
    //Aka this implementation has all its "sending-headers" values stored as comma separated strings.

    private RESTClient client;

    private String method;
    private HashMap<String, String> headers;
    private StringBuilder path;
    private HashMap<String, String> parameters;
    private ArrayList<HttpCookie> userCookies;           //cookies the user has defined using addCookie().

    //TODO: make visible to user
    private Settings settings;

    private long unixCreation;
    private long unixExecution;

    private boolean customBodyWriter = false;
    private IOutputStreamWriter bodyWriter = stream -> {
        String params = getParamsAsString();
        for (int i = 0; i < params.length(); ++i) stream.write((byte)params.charAt(i));
        stream.flush();
    };

    //caching (getParamsAsString() and getURL() and getURI())
    private boolean paramsDirty = true;     //describes that builtParams requires rebuilding
    private boolean URXDirty = true;        //describes that builtRequestURL and builtRequestURI requires rebuilding
    private String builtParams = "";
    private URL builtRequestURL = null;
    private URI builtRequestURI = null;

    //intentionally package-private
    RESTClientRequest(RESTClient client) {
        this.client = client;

        method = "GET";
        headers = new HashMap<>();
        path = new StringBuilder();
        parameters = new HashMap<>();

        userCookies = new ArrayList<>();

        settings = new Settings();

        unixCreation = System.currentTimeMillis();
    }

    /** @return the client that created this request */
    public RESTClient getClient() { return client; }

    /** @return whether this request's generated response should report cookies received in the 'Set-Cookie' header
     *              to {@see RESTClient#reportSetCookie()}. */
    protected boolean shouldReportCookies() { return settings.reportCookies; }

    /** @return the unix timestamp for when this request was created. */
    public long getUnixCreation() { return unixCreation; }

    /** @return the unix timestamp for when this request was executed. */
    public long getUnixExecution() { return unixExecution; }

    /**
     * @return the current stated request URL to be used in {@see #execute()}.
     * @throws MalformedURLException - If the current stated URL is malformed.
     * @throws URISyntaxException - If the current stated URI has a syntax error.
     */
    public URL getURL() throws MalformedURLException, URISyntaxException {
        if (URXDirty) buildRequestURX();
        return builtRequestURL;
    }

    /**
     * @return the current stated request URI.
     * @throws MalformedURLException - If the current stated URL is malformed.
     * @throws URISyntaxException - If the current stated URI has a syntax error.
     */
    public URI getURI() throws MalformedURLException, URISyntaxException {
        if (URXDirty) buildRequestURX();
        return builtRequestURI;
    }

    /** @return the current stated parameters-list on the stringified form: key1=value1&key2=value2. does not contain "?"*/
    public String getParamsAsString() {
        if (paramsDirty) buildParams();
        return builtParams;
    }

    /** @return the currently set request METHOD. */
    public String getMethod() { return method; }

    //TODO: immutable map ?
    /** @return the map containing all headers. changes to this map will affect request headers.
     *      note: does not contain 'cookie' header. Use {@see getCookiesToSend()} for that. */
    public HashMap<String, String> getHeaders() { return headers; }

    /** @return the current PATH to be applied to client's base url. */
    public String getPath() { return path.toString(); }

    /** @return the map containing all parameters. changes to this map will affect request parameters. */
    public HashMap<String, String> getParameters() { return parameters; }

    /**
     * Get a list of cookies gotten from this request's client's cookie store based on the current stated URI.
     * These cookies will be added to the 'Cookie' header upon request execution if
     *      {@code settings.sendClientCookies} equals {@code true}.
     *      Any cookies you have added using {@see #addCookie()} will also be sent, but not returned by this method.
     * @return the cookies from this request's client's cookie store based on the current stated URI.
     * @throws MalformedURLException - If the current stated URL is malformed.
     * @throws URISyntaxException - If the current stated URI has a syntax error.
     */
    public List<HttpCookie> getClientCookiesToSend() throws MalformedURLException, URISyntaxException {
        return client.getCookies(getURI());
    }

    /**
     * Get a list of all cookies the user has manually added to this request.
     * These cookies will be added to the 'Cookie' header upon request execution.
     * @return a list of all cookies the user has added using {@see #addCookie()}.
     */
    public List<HttpCookie> getUserCookiesToSend() {
        return userCookies;
    }

    /**
     * Get a list of cookies associated with this request.
     * If the provided value {@code sendOnly} is true, then this method will only return the cookies that will
     *      be sent in the 'Cookie' header upon request execution.
     *      Otherwise all cookies (from client cookie store and user-defined) will be returned.
     * @param sendOnly - if true, will only return cookies that will be sent in the 'Cookie' header.
     *                 Otherwise, will return all cookies associated with this request.
     * @return a list of cookies associated with this request: all cookies, or only the ones that will be sent
     *          in the 'Cookie' header if {@code settings.sendCookies} is true.
     * @throws MalformedURLException - If the current stated URL is malformed.
     * @throws URISyntaxException - If the current stated URI has a syntax error.
     * @see #getClientCookiesToSend()
     * @see #getUserCookiesToSend()
     */
    public List<HttpCookie> getCookiesToSend(boolean sendOnly) throws MalformedURLException, URISyntaxException {
        List<HttpCookie> list = new ArrayList<>(getUserCookiesToSend());
        if (!sendOnly || settings.sendClientCookies) list.addAll(getClientCookiesToSend());
        return list;
    }

    /**
     * Set a custom body-writer for this request.
     * The default body writer writes all request parameters {@see #getParamsAsString()} to the body
     *      if {@see shouldPutParamsInBody()} returns {@code true}.
     * Note that if {@see shouldPutParamsInBody()} returns {@code true}, then the provided parameters will
     *      <em>NOT</em> be written to the body. You must do this yourself if you want the server to receive
     *      the provided parameters.
     * The bodyWriter will be called after the URXs have been formed and headers have been applied.
     * @param bodyWriter - The body writer object.
     */
    public void setCustomBodyWriter(IOutputStreamWriter bodyWriter) {
        this.bodyWriter = bodyWriter;
        customBodyWriter = true;
    }

    //TODO: disallow change of headers etc after request executed ?

    /**
     * Set this request's cookie configuration.
     * The default configuration is:
     *      {@code sendClientCookies} equals {@code true}.
     *      {@code reportCookies} equals {@code true}.
     * @param sendClientCookies - If true, then during {@see #execute()} I will add cookies the gotten by this
     *                          request's client's cookie store by my then-to-be URI {@see RESTClient.getCookies(URI)}
     *                          to the 'Cookie' header before executing the request.
     *                          Note that all user-defined cookies {@see #addCookie()} will be added to the
     *                          'Cookie' header regardless.
     * @param reportCookies - If true, then the generated RESTClientResponse shall report all cookies it receives
     *                      in the 'Set-Cookie' header to the {@see RESTClient#reportSetCookie()} method.
     *                      Note that the RESTClient may still decide to not store the reported cookies.
     * @return self, for chaining calls.
     */
    public RESTClientRequest cookieConfig(boolean sendClientCookies, boolean reportCookies) {
        settings.sendClientCookies = sendClientCookies;
        settings.reportCookies = reportCookies;
        return this;
    }

    /*
    * TODO FUTURE:
    * java (HttpURLConnection) does not allow cross-protocol redirection. implement a solution that does.
    * consider headers (esp. cookies). consider some .redirectHandler ? query parameters same probably
    * see https://stackoverflow.com/questions/1884230/httpurlconnection-doesnt-follow-redirect-from-http-to-https (answer by Nathan)
    * leaving this blank for now since there is no good reason a proper game should use http instead of https (even regarding redirects)
    */
    /**
     * Set this request's option regarding the following of redirects.
     * The default configuration is:
     *      {@code follow} equals {@code true}.
     * Redirects will <em>NOT</em> be followed if the original request protocol does not match the
     *      redirect location's protocol (in other words, HTTPS -> HTTP and HTTP -> HTTPS do not work).
     * @param follow - Whether or not this request should follow http redirects (code 3xx).
     * @return self, for chaining calls.
     */
    public RESTClientRequest followRedirects(boolean follow) {
        settings.followRedirectsSameProtocol = follow;
        return this;
    }

    /**
     * Set this request's method (ex. GET, POST, HEAD, PUT, DELETE ...).
     * @param method - This request's new method.
     * @return self, for chaining calls.
     */
    public RESTClientRequest method(String method) {
        this.method = method;
        return this;
    }

    /**
     * Set a header of this request.
     * If this header is already set, then the value will be appended onto the existing header, preceded by a comma.
     *      ex. if the value was 'foo', and the new parameter value is 'bar', then the new value will be 'foo, bar'.
     * You cannot set the "Cookie" header. To add custom cookies for this request only, use {@see #addCookie()}.
     * @param key - The key (field-name) of the header to set.
     * @param value - The value (field-value) to set the header-value as, or append to the header-value if it exists.
     * @return self, for chaining calls.
     * @throws IllegalArgumentException - If the key parameter equals "Cookie". Use addCookie to append custom cookies.
     */
    public RESTClientRequest header(String key, String value) {
        if (key.equals("Cookie")) throw new IllegalArgumentException("you cannot set the 'Cookie' header using .header(). use .addCookie() instead.");
        if (headers.containsKey(key)) {
            String old = headers.get(key);
            headers.put(key, old + ", " + value);
        } else {
            headers.put(key, value);
        }
        return this;
    }

    /**
     * Append to the end of the RESTClient's origin url, like a path. Appending multiple times will concatenate all provided inputs
     * @param toAppend - Path to append to the end of the current path.
     * @return self, for chaining calls.
     */
    public RESTClientRequest path(String toAppend) {
        path.append(toAppend);
        return this;
    }

    //TODO: update javadoc
    /**
     * Set a query- or body-option for this request.
     * The type will depend on the request's method, calculated during the build step, or the RESTClient class's
     * configuration {@see RESTClient#forceQueryParamsOnly}.
     *      if 'paramsInQueryOnly' is false and the method is one of: POST, PUT, OPTIONS,
     *      then the parameters will be sent as body payload (format: param1=value1&param2=value2).
     *      Otherwise they will be sent as query-string parameters (in other words: ?param1=value1&param2=value2).
     * If this parameter is already set, then the old value will be overwritten by the new value.
     * @param key - The key of the parameter to set the value for.
     * @param value - The value of set the parameter to.
     * @return self, for chaining calls.
     */
    public RESTClientRequest param(String key, String value) {
        parameters.put(key, value);
        return this;
    }

    /**
     * Add a custom cookie to be sent with this request.
     * This cookie will <em>NOT</em> be recorded by this request's client's cookie store.
     * Note that this cookie will be sent regardless of the value of {@code settings.sendClientCookies}
     * @param cookie - A cookie to send with this request.
     * @return self, for chaining calls.
     */
    public RESTClientRequest addCookie(HttpCookie cookie) {
        userCookies.add(cookie);
        return this;
    }

    //TODO: set domain ? (replace from client origin ?????)
    //TODO: do proper url building (?) (e.g. if client base contains path or something?)

    /**
     * Build and execute the request.
     * @return self, for chaining calls.
     * @throws MalformedURLException - If the current stated URL is malformed.
     * @throws URISyntaxException - If the current stated URI has a syntax error.
     * @throws IOException - If there was an I/O error during the request-connect or request-write step.
     */
    //public RESTClientResponse execute() {
    public RESTClientResponse execute() throws MalformedURLException, URISyntaxException, IOException {

        //get the url
        URL url = getURL(); //will also build the url.
        URI uri = getURI();

        //open a connection
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();

        //TODO: shuffing probably ok, but check again. This provides good info: https://stackoverflow.com/questions/10116961/can-you-explain-the-httpurlconnection-connection-process

        connection.setConnectTimeout(settings.connectTimeout);
        connection.setReadTimeout(settings.readTimeout);
        connection.setInstanceFollowRedirects(settings.followRedirectsSameProtocol);

        connection.setRequestMethod(method);

        //do headers
        getHeaders().forEach(connection::setRequestProperty);

        //do cookies
        ArrayList<HttpCookie> cookieList = new ArrayList<>(userCookies); //will add all userCookies
        if (settings.sendClientCookies) cookieList.addAll(client.getCookies(uri));
        if (cookieList.size() > 0) {
            String cookies = cookieList.stream().map(HttpCookie::toString).collect(Collectors.joining("; "));
            connection.setRequestProperty("Cookie", cookies);
        }

        //TODO: auto-set "Host" header ?
        //TODO: auto-set "Content-Type" header ?
        //TODO: auto-set "Accept" header ?
        //TODO: auto-set "User-Agent" header ?      "Verse Engine" ?
        //TODO: some "default header provider" method or interface to set headers.

        //connection.setRequestProperty("Content-Type", "application/json");
        //connection.setRequestProperty("host", "localhost:8080");
        //connection.setRequestProperty("User-Agent", "Mozilla/5.0 ( compatible ) ");
        //connection.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/96.0.4664.45 Safari/537.36");
        //connection.setRequestProperty("Accept", "*/*");

        unixExecution = System.currentTimeMillis();
        connection.connect(); //connection is established automatically. this is just for semantics

        //call body writer (default = write params if they belong in body)
        if (customBodyWriter || shouldPutParamsInBody()) {
            connection.setDoOutput(true);
            try (OutputStream stream = connection.getOutputStream()) {
                bodyWriter.write(stream);
                stream.flush();
            }
        }

        return new RESTClientResponse(this, connection);
    }

    /**
     * Build and store {@code builtRequestURI} and {@code builtRequestURL} based on object state.
     * Does nothing if {@code URXDirty} equals {@code false}.
     */
    private void buildRequestURX() throws MalformedURLException, URISyntaxException {
        if (!URXDirty) return;
        String params = getParamsAsString();

        //construct path
        String url = client.getBaseUrl() + path;
        if (params.length() > 0 && !shouldPutParamsInBody()) url += "?" + params;

        builtRequestURL = new URL(url);
        builtRequestURI = new URI(url);
        URXDirty = false;
    }

    /**
     * Build and store {@code builtParams} based on parameters state.
     * Does nothing if {@code paramsDirty} equals {@code false}.
     * The {@code builtParams} is generated by collecting all parameters on the form: key1=value1&key2=value2.
     * The {@code builtParams} does not contain "?".
     */
    private void buildParams() {
        if (!paramsDirty) return;
        StringBuilder sb = new StringBuilder();
        try {
            for (Map.Entry<String, String> entry : parameters.entrySet()) {
                sb.append(URLEncoder.encode(entry.getKey(), "UTF-8"));
                sb.append("=");
                sb.append(URLEncoder.encode(entry.getValue(), "UTF-8"));
                sb.append("&");
            }
        } catch (UnsupportedEncodingException e) {
            Assertions.error("UTF-8 not recognized as supported encoding. system fault?");  //TODO: Assertions.fatal
        }
        if (sb.length() > 0) sb.setLength(sb.length() - 1);
        builtParams = sb.toString();
        paramsDirty = false;
    }

    /**
     * Check whether parameters defined by {@see #param(String, String)} should be put in request 'body' as a payload.
     *      If it is decided that parameters should <em>not</em> be put in the body as a payload, then they
     *      should be put in the query string instead.
     * If the 'paramsInQueryOnly' setting is true, then this method will always return false.
     * Otherwise, this method will return true if the request's METHOD is one of: POST, PUT or OPTIONS.
     * @return whether or not the parameters defined by {@see #param(String, String)} should be put in
     *      request 'body' as a payload.
     */
    private boolean shouldPutParamsInBody() {
        if (settings.paramsInQueryOnly) return false;

        switch (method) {
            case "POST":
            case "PUT":
            case "OPTIONS":
                return true;
            default:
                return false;
        }
    }

    private static class Settings {

        //the amount of time, in milliseconds, a connection will be attempted to be made,
        //  before throwing a SocketTimeoutException (after .execute())
        private int connectTimeout = 5000;

        //the maximum amount of time, in milliseconds, the socket will wait for data to become available
        //  before throwing a SocketTimeoutException (after connection) (includes 'between reads')
        private int readTimeout = 5000;

        //should this request set the 'Cookie' header with the client's values ?
        private boolean sendClientCookies = true;

        //should the RESTResponse consider the 'Set-Cookie' header ?
        private boolean reportCookies = true;

        //should I follow redirects of the same protocol (http, https) ?
        private boolean followRedirectsSameProtocol = true;

        //should I ALWAYS put parameters in query string and NEVER in body ?
        private boolean paramsInQueryOnly = false;

    }

}
