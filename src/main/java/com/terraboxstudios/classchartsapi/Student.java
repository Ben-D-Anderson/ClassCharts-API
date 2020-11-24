package com.terraboxstudios.classchartsapi;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.terraboxstudios.classchartsapi.enums.DisplayDate;
import com.terraboxstudios.classchartsapi.exception.*;
import com.terraboxstudios.classchartsapi.http.HttpHeader;
import com.terraboxstudios.classchartsapi.http.HttpRequest;
import com.terraboxstudios.classchartsapi.http.HttpResponse;
import com.terraboxstudios.classchartsapi.obj.Homework;
import com.terraboxstudios.classchartsapi.utils.DateValidator;

import java.io.IOException;
import java.net.HttpCookie;
import java.net.URLDecoder;
import java.util.*;

public class Student {

    private final List<String> cookies = new LinkedList<>();
    private HttpHeader authorizationHeader;
    private int studentId;

    public Student(StudentCredentials studentCredentials) throws IOException, IDRetrievalException, LoginException, ServerException {
        login(studentCredentials);
        loadStudentId();
    }

    private void login(StudentCredentials studentCredentials) throws IOException, LoginException, ServerException {
        Map<String, String> params = new HashMap<>();
        params.put("_method", "POST");
        params.put("code", studentCredentials.getCode());
        params.put("dob", studentCredentials.getDateOfBirth());
        params.put("recaptcha-token", "no-token-available");
        params.put("remember_me", "1");
        HttpRequest httpRequest = new HttpRequest.Builder("https://www.classcharts.com/student/login", "POST")
                .setParams(params)
                .setFollowRedirects(false)
                .build();
        HttpResponse httpResponse = httpRequest.execute();
        if (httpResponse.getResponseCode() != 302) {
            throw new LoginException("Login credentials invalid.");
        }
        for (String str : httpResponse.getResponseHeaders().get("Set-Cookie")) {
            cookies.add(str.split(";")[0]);
        }
        authorizationHeader = new HttpHeader("Authorization", "Basic " + JsonParser.parseString(URLDecoder.decode(cookies.get(0), "UTF-8").split("=")[1]).getAsJsonObject().get("session_id").getAsString());
    }

    private void loadStudentId() throws IDRetrievalException, IOException, ServerException {
        Map<String, String> params = new HashMap<>();
        params.put("include_data", "false");
        HttpRequest.Builder httpRequestBuilder = new HttpRequest.Builder("https://www.classcharts.com/apiv2student/ping", "POST")
                .setParams(params)
                .setFollowRedirects(false)
                .setHeader(authorizationHeader);
        cookies.forEach(cookie -> httpRequestBuilder.setCookie(new HttpCookie(cookie.split("=")[0], cookie.split("=")[1])));
        HttpRequest httpRequest = httpRequestBuilder.build();
        HttpResponse httpResponse = httpRequest.execute();
        JsonObject elem = JsonParser.parseString(httpResponse.getContent()).getAsJsonObject();
        if (elem.get("success").getAsInt() != 1) {
            throw new IDRetrievalException("ID could not be retrieved.");
        }
        this.studentId = elem.get("data").getAsJsonObject().get("user").getAsJsonObject().get("id").getAsInt();
    }

    private List<Homework> homeworkRequester(HttpRequest.Builder httpRequestBuilder) throws IOException, ServerException, HomeworkRetrievalException {
        cookies.forEach(cookie -> httpRequestBuilder.setCookie(new HttpCookie(cookie.split("=")[0], cookie.split("=")[1])));
        HttpRequest httpRequest = httpRequestBuilder.build();
        HttpResponse httpResponse = httpRequest.execute();
        JsonObject jsonObject = JsonParser.parseString(httpResponse.getContent()).getAsJsonObject();
        if (jsonObject.get("success").getAsInt() != 1) {
            throw new HomeworkRetrievalException("Homework could not be retrieved.");
        }
        List<Homework> homeworkList = new LinkedList<>();
        jsonObject.get("data").getAsJsonArray().iterator().forEachRemaining(obj -> homeworkList.add(new Gson().fromJson(obj, Homework.class)));
        return homeworkList;
    }

    public List<Homework> getHomework() throws IOException, HomeworkRetrievalException, ServerException {
        HttpRequest.Builder httpRequestBuilder = new HttpRequest.Builder("https://www.classcharts.com/apiv2student/homeworks/" + this.studentId, "GET")
                .setFollowRedirects(false)
                .setHeader(authorizationHeader);
        return homeworkRequester(httpRequestBuilder);
    }

    public List<Homework> getHomework(String fromDate, String toDate) throws IOException, HomeworkRetrievalException, DateFormatException, ServerException {
        return getHomework(DisplayDate.ISSUE, fromDate, toDate);
    }

    public List<Homework> getHomework(DisplayDate displayDate, String fromDate, String toDate) throws IOException, HomeworkRetrievalException, DateFormatException, ServerException {
        if (!DateValidator.isDateValid(fromDate) || !DateValidator.isDateValid(toDate)) throw new DateFormatException("From-Date or To-Date do not follow format dd/MM/yyyy");
        Map<String, String> params = new HashMap<>();
        params.put("display_date", displayDate.getDisplayDate());
        params.put("from", fromDate.split("/")[2] + "-" + fromDate.split("/")[1] + "-" + fromDate.split("/")[0]);
        params.put("to", toDate.split("/")[2] + "-" + toDate.split("/")[1] + "-" + toDate.split("/")[0]);
        HttpRequest.Builder httpRequestBuilder = new HttpRequest.Builder("https://www.classcharts.com/apiv2student/homeworks/" + this.studentId, "GET")
                .setFollowRedirects(false)
                .setParams(params)
                .setHeader(authorizationHeader);
        return homeworkRequester(httpRequestBuilder);
    }

}
