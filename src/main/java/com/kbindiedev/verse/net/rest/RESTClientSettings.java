package com.kbindiedev.verse.net.rest;

public class RESTClientSettings {

    private boolean enforceSSL = false;
    private boolean preserveCookies = true;

    public void setEnforceSSL(boolean enforceSSL) { this.enforceSSL = enforceSSL; }
    public void setPreserveCookies(boolean preserveCookies) { this.preserveCookies = preserveCookies; }

    public boolean shouldEnforceSSL() { return enforceSSL; }
    public boolean shouldPreserveCookies() { return preserveCookies; }

}
