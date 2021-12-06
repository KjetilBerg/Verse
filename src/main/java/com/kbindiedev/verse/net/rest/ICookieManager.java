package com.kbindiedev.verse.net.rest;

import java.net.HttpCookie;
import java.net.URI;
import java.util.List;

public interface ICookieManager {

    /** Deal with a {@see RESTClientResponse} setCookie event. */
    void reportSetCookie(RESTClientResponse origin, HttpCookie cookie);

    /** @return a list of cookies that belong to a given URI. These will not necessarily be sent. */
    List<HttpCookie> getCookies(URI uri);

}
