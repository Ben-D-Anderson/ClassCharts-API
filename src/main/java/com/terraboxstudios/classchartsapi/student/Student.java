package com.terraboxstudios.classchartsapi.student;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.terraboxstudios.classchartsapi.enums.DisplayDate;
import com.terraboxstudios.classchartsapi.exception.*;
import com.terraboxstudios.classchartsapi.http.HttpHeader;
import com.terraboxstudios.classchartsapi.http.HttpRequest;
import com.terraboxstudios.classchartsapi.http.HttpResponse;
import com.terraboxstudios.classchartsapi.obj.ClassChartsDate;
import com.terraboxstudios.classchartsapi.obj.Homework;

import java.io.IOException;
import java.net.HttpCookie;
import java.net.URLDecoder;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class Student {

    private List<String> cookies;
    private HttpHeader authorizationHeader;
    private final StudentCredentials studentCredentials;
    private int studentId;

    public Student(StudentCredentials studentCredentials) throws IOException, IDRetrievalException, LoginException, ServerException {
        this.studentCredentials = studentCredentials;
        reauthenticate();
    }

    public void reauthenticate() throws LoginException, ServerException, IOException, IDRetrievalException {
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
        cookies = new LinkedList<>();
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

    public List<Homework> getHomework(ClassChartsDate fromDate, ClassChartsDate toDate) throws IOException, HomeworkRetrievalException, ServerException {
        return getHomework(DisplayDate.ISSUE, fromDate, toDate);
    }

    public List<Homework> getHomework(DisplayDate displayDate, ClassChartsDate fromDate, ClassChartsDate toDate) throws IOException, HomeworkRetrievalException, ServerException {
        Map<String, String> params = new HashMap<>();
        params.put("display_date", displayDate.getDisplayDate());
        params.put("from", fromDate.getDate().split("/")[2] + "-" + fromDate.getDate().split("/")[1] + "-" + fromDate.getDate().split("/")[0]);
        params.put("to", toDate.getDate().split("/")[2] + "-" + toDate.getDate().split("/")[1] + "-" + toDate.getDate().split("/")[0]);
        HttpRequest.Builder httpRequestBuilder = new HttpRequest.Builder("https://www.classcharts.com/apiv2student/homeworks/" + this.studentId, "GET")
                .setFollowRedirects(false)
                .setParams(params)
                .setHeader(authorizationHeader);
        return homeworkRequester(httpRequestBuilder);
    }

    public void markHomeworkAsSeen(Homework homework) throws IOException, ServerException, HomeworkSeenException {
        Map<String, String> params = new HashMap<>();
        params.put("pupil_id", String.valueOf(this.studentId));
        HttpRequest.Builder httpRequestBuilder = new HttpRequest.Builder("https://www.classcharts.com/apiv2student/markhomeworkasseen/" + homework.getId(), "POST")
                .setParams(params)
                .setFollowRedirects(false)
                .setHeader(authorizationHeader);
        cookies.forEach(cookie -> httpRequestBuilder.setCookie(new HttpCookie(cookie.split("=")[0], cookie.split("=")[1])));
        HttpRequest httpRequest = httpRequestBuilder.build();
        HttpResponse httpResponse = httpRequest.execute();
        JsonObject elem = JsonParser.parseString(httpResponse.getContent()).getAsJsonObject();
        if (elem.get("success").getAsInt() != 1) {
            throw new HomeworkSeenException("Homework could not be marked as seen.");
        }
    }

    /**
     * This method will toggle the tick on homework
     * meaning that if the homework has already been ticked then
     * it will be un-ticked and if it isn't ticked then it will
     * become ticked.
     */
    public void tickHomework(Homework homework) throws IOException, ServerException, HomeworkTickException {
        HttpRequest.Builder httpRequestBuilder = new HttpRequest.Builder("https://www.classcharts.com/apiv2student/homeworkticked/" + homework.getStatus().getId(), "POST")
                .setFollowRedirects(false)
                .setHeader(authorizationHeader);
        cookies.forEach(cookie -> httpRequestBuilder.setCookie(new HttpCookie(cookie.split("=")[0], cookie.split("=")[1])));
        HttpRequest httpRequest = httpRequestBuilder.build();
        HttpResponse httpResponse = httpRequest.execute();
        JsonObject elem = JsonParser.parseString(httpResponse.getContent()).getAsJsonObject();
        if (elem.get("success").getAsInt() != 1) {
            throw new HomeworkTickException("Homework could not be ticked.");
        }
    }

}
