package com.terraboxstudios.classchartsapi.http;

import lombok.Getter;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.util.List;
import java.util.Map;

@Getter
public class HttpResponse {

    private final String content, responseMessage;
    private final int responseCode;
    private final Map<String, List<String>> responseHeaders;


    protected HttpResponse(HttpURLConnection httpURLConnection) throws IOException {
        this.responseMessage = httpURLConnection.getResponseMessage();
        this.responseCode = httpURLConnection.getResponseCode();

        this.responseHeaders = httpURLConnection.getHeaderFields();

        BufferedReader in = new BufferedReader(
                new InputStreamReader(httpURLConnection.getInputStream()));
        String inputLine;
        StringBuilder content = new StringBuilder();
        while ((inputLine = in.readLine()) != null) {
            content.append(inputLine);
        }
        in.close();
        httpURLConnection.disconnect();

        this.content = content.toString();

    }

}
