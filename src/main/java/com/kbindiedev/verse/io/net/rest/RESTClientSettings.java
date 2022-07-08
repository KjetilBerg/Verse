package com.kbindiedev.verse.io.net.rest;

public class RESTClientSettings {

    private boolean preserveCookies = true;

    /** Set whether or not to preserve cookies (listen to 'Set-Cookie' headers). */
    public void setPreserveCookies(boolean preserveCookies) { this.preserveCookies = preserveCookies; }

    /** @return whether or not cookies should be preserved (listen to 'Set-Cookie' headers). */
    public boolean shouldPreserveCookies() { return preserveCookies; }

}
