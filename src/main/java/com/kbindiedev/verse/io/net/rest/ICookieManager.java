package com.kbindiedev.verse.io.net.rest;

import java.net.HttpCookie;
import java.net.URI;
import java.util.List;

public interface ICookieManager {

    /**
     * Deal with a {@see RESTClientResponse} setCookie event.
     * These are called when a {@see RESTClientResponse} receives a 'Set-Cookie' header while its
     *      corresponding request's "settings" dictate that cookies should be reported ({@see RESTClientRequestSettings}).
     */
    void reportSetCookie(RESTClientResponse origin, HttpCookie cookie);

    /** @return a list of cookies that belong to a given URI. These may, but will not necessarily be sent. */
    List<HttpCookie> getCookies(URI uri);

}
