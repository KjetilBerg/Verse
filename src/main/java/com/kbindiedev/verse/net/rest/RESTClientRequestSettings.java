package com.kbindiedev.verse.net.rest;

/** Defines settings for RESTClientRequests */
public class RESTClientRequestSettings {

    //See relevant setters below for documentation.
    int bufferSize = 8192;                      //RESTClientResponse buffer size for stream
    int connectTimeout = 5000;
    int readTimeout = 5000;
    boolean sendClientCookies = true;
    boolean reportCookies = true;
    boolean followRedirectsSameProtocol = true;
    boolean paramsInQueryOnly = false;
    boolean enforceSSL = true;
    boolean defaultCacheSockets = true;         //default behaviour of RESTClientResponse .disconnect
    boolean shouldDrainSockets = false;         //default behaviour of RESTClientResponse .disconnect


    /**
     * Set the size of the buffers (in bytes) (one per request) to be used in buffering a response to that request.
     * @param bufferSize - The size (in bytes) of the buffer to be used for buffering a response.
     */
    public void setBufferSize(int bufferSize) { this.bufferSize = bufferSize; }

    /**
     * Set the connect timeout.
     * The connect timeout is defined as the amount of time, in milliseconds, a connection will be attempted to be made
     *      before throwing a SocketTimeoutException.
     * @param timeout - The timeout, in milliseconds.
     */
    public void setConnectTimeout(int timeout) { this.connectTimeout = timeout; }

    /**
     * Set the read timeout.
     * The read timeout is defined as the amount of time, in milliseconds, a socket will wait for data to become
     *      available before throwing a SocketTimeoutException (after connection) (includes 'between reads').
     * @param timeout - The timeout, in milliseconds.
     */
    public void setReadtimeout(int timeout) { this.readTimeout = timeout; }

    /**
     * Set the cookie settings.
     * The default configuration is: {@code sendClientCookies} equals {@code true} and {@code reportCookies} equals {@code true}.
     * @param sendClientCookies - Whether or not 'client' cookies should be sent in the 'Cookie' header.
     *                          Note: does not affect other cookies (for example user-defined cookies).
     * @param reportCookies - Whether the generated RESTClientResponse should report cookies it receives in the
     *                      'Set-Cookie' header(s) to its client's {@see RESTClient.reportSetCookie()} method.
     */
    public void setCookieSettings(boolean sendClientCookies, boolean reportCookies) {
        this.sendClientCookies = sendClientCookies; this.reportCookies = reportCookies;
    }

    //note: future implementations may add some similar field that allows cross-protocol redirects.
    //this is currently not considered due to the java implementation not allowing it.
    /**
     * Set whether or not to follow redirect-responses of the same protocol (http->http or https->https).
     * Responses wanting to redirect cross-protocol (for example http->https or https->http) will not be followed.
     * The default configuration is: {@code follow} equals {@code true}.
     * @param follow - Whether or not this request should follow redirects for the same protocol (code 3xx).
     */
    public void setFollowRedirectsSameProtocol(boolean follow) {
        this.followRedirectsSameProtocol = follow;
    }

    /**
     * Set whether REST-parameters should ALWAYS be put in query string instead of body as payload.
     * @param paramsInQueryOnly - Whether REST-parameters should ALWAYS be put in query string (and never in body).
     */
    public void setParamsInQueryOnly(boolean paramsInQueryOnly) { this.paramsInQueryOnly = paramsInQueryOnly; }

    /**
     * Set whether SSL should be enforced upon executing a request.
     * If the request is configured to be a non-SSL request upon execution, it will be cancelled
     *      and an IOException will be thrown (no request will be made).
     * @param enforceSSL - Whether or not ONLY SSL requests should be allowed (true = non-SSL requests will never be sent).
     */
    public void setEnforceSSL(boolean enforceSSL) { this.enforceSSL = enforceSSL; }

    /**
     * Set whether the default behaviour of disconnecting after a request should be to try and cache the socket.
     * Note: this setting may be affected by the {@code shouldDrainSockets} setting ({@see RESTClientResponse#disconnect(boolean, boolean)}).
     * @param defaultCacheSockets - Whether or not sockets should be tried to be cached upon disconnect.
     */
    public void setDefaultCacheSockets(boolean defaultCacheSockets) { this.defaultCacheSockets = defaultCacheSockets; }

    /**
     * Set whether or not responses should be drained by default before disconnecting after executing a request.
     * Note: this setting may affect the {@code defaultCacheSockets} setting ({@see RESTClientResponse#disconnect(boolean, boolean)}).
     * @param shouldDrainSockets - Whether or not a response should be drained before disconnecting.
     */
    public void setShouldDrainSockets(boolean shouldDrainSockets) { this.shouldDrainSockets = shouldDrainSockets; }

}
