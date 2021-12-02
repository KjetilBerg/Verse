package com.kbindiedev.verse.net.rest;

import com.kbindiedev.verse.profiling.Assertions;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;

/** Class responsible for the nitty-gritty details of dealing with REST requests {@see RestApiConnection} */
public class RestRequest {

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
    //Aka this implementation only handles single-entry "field-name: field-value" pairs.

    private RestApiConnection origin;
    private String method;
    private HashMap<String, String> headers;
    private StringBuilder path;
    private HashMap<String, String> parameters;

    //intentionally package-private
    RestRequest(RestApiConnection origin) {
        this.origin = origin;
        method = "GET";
        headers = new HashMap<>();
        path = new StringBuilder();
        parameters = new HashMap<>();
    }

    /**
     * Set this request's method (ex. GET, POST, HEAD, PUT, DELETE ...)
     * @param method - This request's new method.
     * @return self, for chaining calls.
     */
    public RestRequest method(String method) {
        this.method = method;
        return this;
    }

    /**
     * Set a header of this request.
     * If this header is already set, then the value will be appended onto the existing header, preceded by a comma.
     *      ex. if the value was 'foo', and the new parameter value is 'bar', then the new value will be 'foo, bar'.
     * @param key - The key (field-name) of the header to set.
     * @param value - The value (field-value) to set the header-value as, or append to the header-value if it exists.
     * @return self, for chaining calls.
     */
    public RestRequest header(String key, String value) {
        if (headers.containsKey(key)) {
            String old = headers.get(key);
            headers.put(key, old + ", " + value);
        } else {
            headers.put(key, value);
        }
        return this;
    }

    /**
     * Append to the end of the origin's url, like a path. Appending multiple times will concatenate all provided inputs
     * @param toAppend - Path to append to the end of the current path.
     * @return self, for chaining calls.
     */
    public RestRequest path(String toAppend) {
        path.append(toAppend);
        return this;
    }

    //TODO: update javadoc
    /**
     * Set a query- or body-option for this request.
     * The type will depend on the request's method, calculated during the build step, or the origin class's
     * configuration {@see RestApiConnection#forceQueryParamsOnly}.
     *      if 'forceQueryParamsOnly' is false and the method is one of: POST, PUT, OPTIONS,
     *      then the parameters will be sent as body payload (format: param1=value1&param2=value2).
     *      Otherwise they will be sent as query-string parameters (e.g. ?param1=value1&param2=value2).
     * If this parameter is already set, then the old value will be overwritten by the new value.
     * @param key - The key of the parameter to set the value for.
     * @param value - The value of set the parameter to.
     * @return self, for chaining calls.
     */
    public RestRequest param(String key, String value) {
        parameters.put(key, value);
        return this;
    }

    //TODO: set domain ? (replace from origin ?????)

    /**
     * Build and execute the request.
     * @return
     */
    //public RestResponse execute() {
    public RestResponse execute() throws IOException {

        //stringify params
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
            return null;
        }
        if (sb.length() > 0) sb.setLength(sb.length() - 1);
        String params = sb.toString();

        //construct path
        String fullUrl = origin.getBaseUrl() + path;
        if (params.length() > 0 && !shouldPutParamsInBody()) fullUrl += "?" + params;

        HttpURLConnection connection = (HttpURLConnection) new URL(fullUrl).openConnection();

        //TODO: do some reshuffling. This provides good info: https://stackoverflow.com/questions/10116961/can-you-explain-the-httpurlconnection-connection-process

        connection.setConnectTimeout(5000);
        connection.setReadTimeout(5000);    //TODO: timeouts

        connection.setRequestMethod(method);

        connection.connect(); //connection is established automatically. this is just for semantics

        //connection.setRequestProperty("Content-Type", "application/json");
        //connection.setRequestProperty("host", "localhost:8080");
        //connection.setRequestProperty("cache-control", "max-age=0");
        //connection.setRequestProperty("upgrade-insecure-requests", "1");
        //connection.setRequestProperty("accept-encoding", "gzip, deflate, br");
        //connection.setRequestProperty("User-Agent", "Mozilla/5.0 ( compatible ) ");
        //connection.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/96.0.4664.45 Safari/537.36");
        //connection.setRequestProperty("Accept", "*/*");

        //TODO: consider setting User-Agent to "Verse Engine" or something

        if (params.length() > 0 && shouldPutParamsInBody()) {
            connection.setDoOutput(true);

            try (OutputStream stream = connection.getOutputStream()) {
                for (int i = 0; i < params.length(); ++i) stream.write((byte)params.charAt(i));     //TODO: support setting custom data in body?
                stream.flush();
            } catch (IOException e) {
                e.printStackTrace();
                //TODO: throw e; or pass to RestResponse
                return null;
            }
        }

        return new RestResponse(connection);
    }

    private boolean shouldPutParamsInBody() {
        //return true;    //TODO: temp, also javadoc
        return false;
    }



}
