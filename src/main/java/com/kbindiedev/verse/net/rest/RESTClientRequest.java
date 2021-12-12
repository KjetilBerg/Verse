package com.kbindiedev.verse.net.rest;

import com.kbindiedev.verse.profiling.Assertions;
import com.kbindiedev.verse.util.IOutputStreamWriter;

import java.io.*;
import java.net.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/*
 * TODO FUTURE:
 * java (HttpURLConnection) does not allow cross-protocol redirection. implement a solution that does.
 * consider headers (esp. cookies). consider some .redirectHandler ? query parameters same probably
 * see https://stackoverflow.com/questions/1884230/httpurlconnection-doesnt-follow-redirect-from-http-to-https (answer by Nathan)
 * leaving this blank for now since there is no good reason a proper game should use http instead of https (even regarding redirects)
 *
 * Notice: after coming back, I've realized there are some other problems regarding redirection.
 * Specifically, cookies are not considered for redirect calls (because "global cookie handler" should not be a part of this implementation).
 * It should be RESTClientResponse's responsibility to handle redirects in that case.
 * For now: RESTClientRequest sets the "followRedirects" flag and does nothing else.
 */

/*
 * TODO FUTURE:
 * a custom implementation of redirection seems to be necessary regardless, because there seem to be no way of getting the
 * 'Set-Cookie' header of requests that also end up in redirects (besides global CookieHandler, but this feels messy).
 * These cookies should be considered.
 */

/*
 * TODO FUTURE:
 * support different body encodings (ex. multipart/form-data in addition to the default x-www-form-urlencoded)
 * see https://stackoverflow.com/questions/8659808/how-does-http-file-upload-work for details.
 * I don't see why it would be necessary for the game client to upload files specifically, so therefore this is
 * not implemented (yet). A user could still define their own BodyWriter and use that to write multipart/form-data,
 * thus allowing file uploads.
 */

/*
 * TODO FUTURE:
 * currently, https is the only SSL protocol that is supported. consider adding more? (e.g. SFTP).
 * I don't think these are particularly useful in a game engine, but why not?
 */

/*
 * TODO FUTURE:
 * consider making all fields immutable after the request has been executed. I suppose there is nothing wrong
 * with executing about a single URI multiple times does not matter, however changing that URI feels really messy.
 */

/*
 * TODO FUTURE:
 * Use different streaming modes (e.g. Chunked or FixedLength). This link provides some good info (in general):
 * https://stackoverflow.com/questions/2793150/how-to-use-java-net-urlconnection-to-fire-and-handle-http-requests
 */

/*
 * TODO FUTURE:
 * allow user to "install" SSL-certificates to be trusted by this program. Again, see link mentioned:
 * https://stackoverflow.com/questions/2793150/how-to-use-java-net-urlconnection-to-fire-and-handle-http-requests
 */

/*
 * TODO FUTURE:
 * Provide some DefaultHeaderProvider or some client- or request setting to include certain headers automatically.
 * Maybe some client.setHeaderForAllFuture(key, value)  ?? (would probably require ability to erase said header later...)
 * Headers that come to mind:
 * "Host"               (e.g. authority target and port (some setting to derive from URL ?))
 * "Accept"             (is this really relevant? empty would imply "all", no?)
 * "User-Agent"         (because "Verse Engine" is cool :)) (alternatively mimic browsers? don't really see how that's useful for a game engine)
 * "Content-Length"
 */

/*
 * TODO FUTURE (doubtfully):
 * Support HTTP 2. HTTP 1.xxx is the only currently supported version(s).
 */

/*
 * TODO FUTURE (unlikely):
 * RESTClientRequestSettings getters and describe defaults in setters' javadoc
 */

/*
 * TODO BUGS (known bugs):
 * - cookies presented in a redirect will be ignored if that redirect was followed (due to java limitations: cannot set HttpURLConnection cookie handler)
 */


/**
 * The RESTClientResponse class is responsible for handling a HttpURLConnection after any request data has been written.
 *      In other words: parse the response (like getting status code or body content).
 *
 * Note that RESTClientResponse is responsible for notifying the owning client of any cookies it encounters via
 *      any 'Set-Cookie' header. See the settings section below (in this comment) for further details.
 *
 * Any RESTClientResponse SHOULD exist in accordance to the generating request's settings {@see RESTClientRequest#getSettings()}.
 * Specifically, these fields should be considered:
 * - bufferSize = the amount of bytes that should be buffered from the response body.
 * - reportCookies = whether or not you should call the request's client's reportSetCookie method (report cookies).
 * - defaultCacheSockets = whether or not to "cache by default" when .disconnect() is called without parameters.
 * - shouldDrainSockets = whether or not to "drain by default" when .disconnect() is called without parameters.
 */

/**
 * The RESTClientRequest class is responsible for handling the nitty-gritty details of dealing with REST requests.
 * All RESTClientRequests must be generated from a {@see RESTClient}.
 *
 * RESTClientRequest's purpose is to generate RESTClientResponse objects. These are made by creating a HttpURLConnection,
 * writing its body or other content, and then passing it to a mentioned RESTClientResponse object (do not read / parse
 * the response content in the request class). Note that all details should be set by RESTClientRequest. This includes
 * but is not limited to: full URL, headers, content body.
 *
 * Note that RESTClientRequest should ask its generating client for any cookies to send along in its request, unless
 * its own "request settings" dictates otherwise. See the settings section below (in this comment) for further details.
 *
 * //TODO see TODO FUTURE about redirects. This section may not apply in the future
 * Note that RESTClientRequest should not handle redirects itself, but for this version of RESTClientRequest,
 *      the internal "setFollowRedirects" on HttpURLConnection should be set according to the followRedirectsSameProtocol setting.
 *
 * Any RESTClientRequest SHOULD exist in accordance to its settings {@see RESTClientRequest#getSettings()}.
 * These settings may be overwritten by the user, but should be derived from the client upon generation.
 * Specifically, these fields should be considered:
 * - connectTimeout = the maximum amount of time to wait before aborting the request.
 *                      This must be set by the RESTClientRequest before passing the HttpURLConnection onto RESTClientResponse.
 * - readTimeout = the maximum amount of time to wait before data becomes available (including "between reads").
 *                      This must be set by the RESTClientRequest before passing the HttpURLConnection onto RESTClientResponse.
 * - sendClientCookies = whether or not you should ask the client for additional cookies to append to headers.
 *                      User-defined cookies (not from client) should be unaffected by this setting.
 * - followRedirectsSameProtocol = whether or not redirects (code 3xx) should be followed for the same protocol
 *                      (http->http or https->https). cross-protocol should not be considered here.
 *                      This must be set/handled by the RESTClientRequest before passing the final HttpURLConnection onto RESTClientResponse.
 * - paramsInQueryOnly = whether or not parameters defined should ONLY appear in the query-string.
 * - enforceSSL = whether or not all requests MUST be SSL. If a request is non-ssl and this setting is true, then the request
 *                      MUST be aborted.
 */


public class RESTClientRequest implements Cloneable {

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
    private String root;
    private StringBuilder path;
    private HashMap<String, String> parameters;
    private ArrayList<HttpCookie> userCookies;           //cookies the user has defined using addCookie().

    private RESTClientRequestSettings settings;

    private long unixCreation;
    private long unixExecution;

    private boolean customBodyWriter = false;
    private IOutputStreamWriter bodyWriter = stream -> {
        String params = getParamsAsString();
        for (int i = 0; i < params.length(); ++i) stream.write((byte)params.charAt(i));
        stream.flush();
    };

    //caching (getParamsAsString() and getTargetURL() and getTargetURI())
    private boolean paramsDirty = true;     //describes that builtParams requires rebuilding
    private boolean URXDirty = true;        //describes that builtRequestURL and builtRequestURI requires rebuilding
    private String builtParams = "";
    private URL builtRequestURL = null;
    private URI builtRequestURI = null;

    //intentionally package-private
    /** Initializes fields for the request */
    RESTClientRequest(RESTClient client) {
        this.client = client;

        method = "GET";
        headers = new HashMap<>();
        root = "";
        path = new StringBuilder();
        parameters = new HashMap<>();

        userCookies = new ArrayList<>();

        settings = client.getRequestSettings(); //derive from client

        unixCreation = System.currentTimeMillis();
        unixExecution = -1;
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

    /**
     * Set this request's internal "request settings" {@see RESTClientRequestSettings}.
     *      Any existing settings will be overwritten.
     * The original settings are set to the client's settings upon request creation.
     * The settings-object you provided SHOULD NOT (but may) be changed after calling this method.
     * @param settings - The new settings.
     * @return self, for chaining calls.
     */
    public RESTClientRequest setRequestSettings(RESTClientRequestSettings settings) {
        this.settings = settings;
        return this;
    }

    /** @return this request's currently set settings. used in {@see RESTClientResponse} */
    protected RESTClientRequestSettings getSettings() { return settings; }

    /** @return the unix timestamp for when this request was created. */
    public long getUnixCreation() { return unixCreation; }

    /** @return the unix timestamp for when this request was executed, or -1 if it has not been executed yet. */
    public long getUnixExecution() { return unixExecution; }

    /** @return whether or not the request has been executed. */
    public boolean hasExecuted() { return unixExecution != -1; }

    /** @return the client that generated this request. */
    public RESTClient getClient() { return client; }

    /**
     * @return the current stated request URL (target) to be used in {@see #execute()}.
     * @throws MalformedURLException - If the current stated target URL is malformed.
     * @throws URISyntaxException - If the current stated target URI has a syntax error.
     */
    public URL getTargetURL() throws MalformedURLException, URISyntaxException {
        if (URXDirty) buildRequestURX();
        return builtRequestURL;
    }

    /**
     * @return the current stated request URI (target).
     * @throws MalformedURLException - If the current stated target URL is malformed.
     * @throws URISyntaxException - If the current stated target URI has a syntax error.
     */
    public URI getTargetURI() throws MalformedURLException, URISyntaxException {
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

    /** @return the map containing all headers. changes to this map will affect request headers.
     *      note: does not contain 'cookie' header. Use {@see getCookiesToSend()} for that. */
    public HashMap<String, String> getHeaders() { return headers; }

    /** @return the current PATH to be applied to client's base url. */
    public String getPath() { return path.toString(); }

    /** @return the map containing all parameters. changes to this map will affect request parameters. */
    public HashMap<String, String> getParameters() { return parameters; }

    /**
     * Get a list of cookies gotten from this request's client's cookie store based on the current stated target URI.
     * These cookies will only be added to the 'Cookie' header upon request execution if
     *      {@code settings.sendClientCookies} equals {@code true}.
     *      Any cookies you have added using {@see #addCookie()} will also be sent, but not returned by this method.
     * @return the cookies from this request's client's cookie store based on the current stated target URI.
     * @throws MalformedURLException - If the current stated target URL is malformed.
     * @throws URISyntaxException - If the current stated target URI has a syntax error.
     */
    public List<HttpCookie> getClientCookies() throws MalformedURLException, URISyntaxException {
        return client.getCookies(getTargetURI());
    }

    /**
     * Get a list of all cookies the user has manually added to this request.
     * These cookies will be added to the 'Cookie' header upon request execution.
     * @return a list of all cookies the user has added using {@see #addCookie()}.
     */
    public List<HttpCookie> getUserCookies() {
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
     * @throws MalformedURLException - If the current stated target URL is malformed.
     * @throws URISyntaxException - If the current stated target URI has a syntax error.
     * @see #getClientCookies()
     * @see #getUserCookies()
     */
    public List<HttpCookie> getCookies(boolean sendOnly) throws MalformedURLException, URISyntaxException {
        List<HttpCookie> list = new ArrayList<>(getUserCookies());
        if (!sendOnly || settings.sendClientCookies) list.addAll(getClientCookies());
        return list;
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
     * Set this request's root.
     * The "root" comes between this request's client's "base" and this request's "path" values.
     * The existing root will be overwritten.
     * @param root - The root.
     * @return self, for chaining calls.
     */
    public RESTClientRequest root(String root) {
        this.root = root;
        URXDirty = true;
        return this;
    }

    /**
     * Append to the end of the RESTClient's origin url, like a path. Appending multiple times will concatenate all provided inputs
     * @param toAppend - Path to append to the end of the current path.
     * @return self, for chaining calls.
     */
    public RESTClientRequest path(String toAppend) {
        path.append(toAppend);
        URXDirty = true;
        return this;
    }

    /**
     * Set a query- or body-option for this request.
     * The type will depend on the request's method that is calculated during the build step
     *      or the RESTClient class's configuration {@see RESTClient#forceQueryParamsOnly}.
     *      if 'paramsInQueryOnly' is false and the method is one of: POST, PUT, OPTIONS,
     *      then the parameters will be sent as body payload (x-www-form-urlencoded) (format: param1=value1&param2=value2).
     *      Otherwise they will be sent as query-string parameters (in other words: ?param1=value1&param2=value2).
     * If this parameter is already set, then the old value will be overwritten by the new value.
     * @param key - The key of the parameter to set the value for.
     * @param value - The value of set the parameter to.
     * @return self, for chaining calls.
     */
    public RESTClientRequest param(String key, String value) {
        parameters.put(key, value);
        paramsDirty = true;
        URXDirty = true;
        return this;
    }

    /**
     * Build and execute the request.
     * If the request has already been executed, an IOException will be thrown.
     *      If you wish the execute the request again, use {@see #clone()} to create a new one.
     * @return a response to this request ({@see RESTClientResponse}).
     * @throws MalformedURLException - If the current stated target URL is malformed.
     * @throws URISyntaxException - If the current stated target URI has a syntax error.
     * @throws IOException - If there was an I/O error during the request-connect or request-write step,
     *      or the request already has been executed.
     */
    public RESTClientResponse execute() throws MalformedURLException, URISyntaxException, IOException {

        //one request = one execution
        if (hasExecuted()) throw new IOException(String.format("request already executed at unix: %d, current unix: %d", unixExecution, System.currentTimeMillis()));

        //get the url
        URL url = getTargetURL(); //will also build the url.
        URI uri = getTargetURI();

        //check SSL flag
        if (settings.enforceSSL) {
            if (!url.getProtocol().equals("https")) throw new IOException(String.format("client settings enforceSSL=true, however URL protocol is not https. url: %s", url.toString()));
        }

        //open a connection
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();

        //set settings
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

        //execute request now
        unixExecution = System.currentTimeMillis();

        //connection is established automatically, so this is just for semantics
        //note: calling .connect before .setDoOutput will cause an IOException, so it has been disabled (unnecessary).
        //connection.connect();

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

    /*
     * Cloning fields:
     * client, method, headers, root, path, parameters, userCookies, settings, unixCreation, unixExecution,
     * customBodyWriter, bodyWriter, paramsDirty, URXDirty, builtParams, builtRequestURL, builtRequestURI
     *
     * identical; don't clone:
     * client, bodyWriter
     *
     * immutable or primitive; don't clone:
     * method, root, customBodyWriter, paramsDirty, URXDirty, builtParams, unixCreation, unixExecution
     *
     * other:
     * settings - Settings are supposed to be handled as immutable once set; don't clone.
     * builtRequestURL, builtRequestURI - handled as immutable internally.
     *
     * clone (deep):
     * headers, path, parameters, userCookies
     *
     * extra:
     * reset unixCreation and unixExecution
     */

    /**
     * Clone this request.
     * If the request you cloned were already executed, then this new clone is "reset" so it can be executed freely.
     * Note that {@code unixCreation} is set to the current timestamp and {@code unixExecution} is set to {@code -1}.
     * Any changes made to either the original request or this new clone will <em>NOT</em> affect the other.
     * The cloned request will be assigned the bodyWriter the original request was using upon cloning.
     * This clone's "request settings"-object is the same as to the original.
     *      As per the settings definitions, a settings object should not change after it has been set {@see #setRequestSettings}.
     *      If it does then this new clone's settings will also change accordingly.
     * @return a copy of this request.
     */
    @SuppressWarnings("unchecked")
    @Override
    public RESTClientRequest clone() throws CloneNotSupportedException {
        RESTClientRequest copy = (RESTClientRequest)super.clone();

        copy.headers = (HashMap<String, String>)headers.clone();
        copy.path = new StringBuilder().append(path);
        copy.parameters = (HashMap<String, String>)parameters.clone();
        copy.userCookies = (ArrayList<HttpCookie>)userCookies.clone();

        copy.unixCreation = System.currentTimeMillis();
        copy.unixExecution = -1;

        return copy;
    }

    /**
     * Build and store {@code builtRequestURI} and {@code builtRequestURL} based on object state.
     * Does nothing if {@code URXDirty} equals {@code false}.
     */
    private void buildRequestURX() throws MalformedURLException, URISyntaxException {
        if (!URXDirty) return;
        String params = getParamsAsString();

        //construct path
        String url = client.getBase() + root + path;
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

}