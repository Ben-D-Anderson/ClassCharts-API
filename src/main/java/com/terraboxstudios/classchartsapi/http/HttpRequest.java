package com.terraboxstudios.classchartsapi.http;

import com.terraboxstudios.classchartsapi.exception.ServerException;

import java.io.*;
import java.net.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class HttpRequest {

    private final String url, method;
    private final boolean followRedirects;
    private final Map<String, String> params;
    private final List<HttpCookie> cookies;
    private final List<HttpHeader> headers;

    private HttpRequest(HttpRequest.Builder builder) {
        this.url = builder.url;
        this.followRedirects = builder.followRedirects;
        this.method = builder.method;
        this.cookies = builder.cookies;
        this.headers = builder.headers;
        this.params = builder.params;
    }

    public HttpResponse execute() throws IOException, ServerException {
        String queryString = "";
        if (this.params != null && !this.params.isEmpty()) {
            queryString = getParamsString(this.params);
        }
        HttpURLConnection httpURLConnection = (HttpURLConnection) new URL(this.method.equals("GET") ? this.url + (queryString != null ? "?" + queryString : "") : this.url).openConnection();
        httpURLConnection.setRequestMethod(this.method);
        httpURLConnection.setRequestProperty("accept", "*/*");
        httpURLConnection.setInstanceFollowRedirects(this.followRedirects);
        assert queryString != null;
        httpURLConnection.setRequestProperty("Content-Length", String.valueOf(queryString.length()));

        if (headers.size() > 0) {
            for (HttpHeader httpHeader : headers) {
                httpURLConnection.setRequestProperty(httpHeader.getName(), httpHeader.getValue());
            }
        }

        if (cookies.size() > 0) {
            StringBuilder cookieString = new StringBuilder();
            for (HttpCookie httpCookie : cookies) {
                cookieString.append(httpCookie.getName()).append("=").append(httpCookie.getValue());
                if (!httpCookie.equals(cookies.get(cookies.size() - 1))) cookieString.append("; ");
            }
            httpURLConnection.setRequestProperty("Cookie", cookieString.toString());
        }

        httpURLConnection.setConnectTimeout(5000);
        httpURLConnection.setReadTimeout(5000);

        if (this.method.equals("POST")) {
            httpURLConnection.setDoOutput(true);
            DataOutputStream out = new DataOutputStream(httpURLConnection.getOutputStream());
            out.writeBytes(queryString);
            out.flush();
            out.close();
        }

        httpURLConnection.connect();

        if (String.valueOf(httpURLConnection.getResponseCode()).charAt(0) == '5') {
            throw new ServerException("HTTP error code " + httpURLConnection.getResponseCode() + " was returned.");
        }

        return new HttpResponse(httpURLConnection);

    }

    private String getParamsString(Map<String, String> params) throws UnsupportedEncodingException {
        StringBuilder result = new StringBuilder();

        for (Map.Entry<String, String> entry : params.entrySet()) {
            result.append(URLEncoder.encode(entry.getKey(), "UTF-8"));
            result.append("=");
            result.append(URLEncoder.encode(entry.getValue(), "UTF-8"));
            result.append("&");
        }

        String resultString = result.toString();
        return resultString.length() > 0
                ? resultString.substring(0, resultString.length() - 1)
                : resultString;
    }

    public static class Builder {

        private boolean followRedirects;
        private final String url, method;
        private Map<String, String> params;
        private final List<HttpCookie> cookies = new ArrayList<HttpCookie>();
        private final List<HttpHeader> headers = new ArrayList<HttpHeader>();

        public Builder(String url, String method) {
            this.url = url;
            this.method = method.toUpperCase();
        }

        public Builder setCookie(HttpCookie httpCookie) {
            this.cookies.add(httpCookie);
            return this;
        }

        public Builder setHeader(HttpHeader header) {
            this.headers.add(header);
            return this;
        }

        public Builder setFollowRedirects(boolean followRedirects) {
            this.followRedirects = followRedirects;
            return this;
        }

        public Builder setParams(Map<String, String> params) {
            this.params = params;
            return this;
        }

        public HttpRequest build() {
            return new HttpRequest(this);
        }

    }

}
