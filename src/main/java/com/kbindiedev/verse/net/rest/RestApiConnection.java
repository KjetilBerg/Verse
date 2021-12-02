package com.kbindiedev.verse.net.rest;

import java.io.*;
import java.net.*;
import java.util.*;

/** A class that allows the user to define an api-endpoint that they then can execute REST requests towards easily. */
public class RestApiConnection {

    //TODO: options
    private static final boolean enforceSSL = false;

    private URL urlBase;
    private CookieManager cookieManager;

    public RestApiConnection(String urlBase) {
        if (urlBase == null) throw new IllegalArgumentException("urlBase must not be null");
        if (enforceSSL && urlBase.startsWith("http://")) throw new IllegalArgumentException("urlBase has protocol 'http', however enforceSSL is true for urlBase: " + urlBase);
        if (!urlBase.startsWith("http://") && !urlBase.startsWith("https://")) throw new IllegalArgumentException("urlBase must have protocol 'http' or 'https'");

        try { this.urlBase = new URL(urlBase); } catch (MalformedURLException e) { throw new RuntimeException(e); } //should never happen due to protocol checks

        cookieManager = new CookieManager();
    }

    /** Initialize a new request. */
    public RestRequest newRequest() { return new RestRequest(this); }   //TODO: consider rename

    //TODO: follow redirects ?
    //TODO: manage cookies
    //TODO: preserve cookies

    public String getBaseUrl() { return urlBase.toString(); } //TODO: prettify

}
