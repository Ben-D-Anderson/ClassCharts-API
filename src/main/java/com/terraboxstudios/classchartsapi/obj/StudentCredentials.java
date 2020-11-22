package com.terraboxstudios.classchartsapi.obj;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.terraboxstudios.classchartsapi.exception.CodeException;
import com.terraboxstudios.classchartsapi.exception.DateFormatException;
import com.terraboxstudios.classchartsapi.http.HttpRequest;
import com.terraboxstudios.classchartsapi.http.HttpResponse;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.io.IOException;

@AllArgsConstructor (access = AccessLevel.PRIVATE)
public final class StudentCredentials {

    @Getter
    private final String code;
    @Getter
    private final String dateOfBirth;
    private final static String dateRegex = "^\\d{2}/\\d{2}/\\d{4}$";

    public static StudentCredentials from(String code, String dateOfBirth) throws DateFormatException, CodeException, IOException {
        HttpRequest httpRequest = new HttpRequest.Builder
                ("https://www.classcharts.com/student/checkpupilcode/" + code, "POST")
                .build();
        HttpResponse httpResponse = httpRequest.execute();
        JsonObject jsonObject = JsonParser.parseString(httpResponse.getContent()).getAsJsonObject();
        if (jsonObject.get("success").getAsInt() != 1 || !jsonObject.get("data").getAsJsonObject().get("has_dob").getAsBoolean()) throw new CodeException("No user with a date of birth found for that code.");
        if (!dateOfBirth.matches(dateRegex)) throw new DateFormatException("Date of birth provided does not follow format dd/MM/yyyy");
        return new StudentCredentials(code, dateOfBirth);
    }

}
