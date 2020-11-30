package com.terraboxstudios.classchartsapi.student;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.terraboxstudios.classchartsapi.exception.CodeException;
import com.terraboxstudios.classchartsapi.exception.ServerException;
import com.terraboxstudios.classchartsapi.http.HttpRequest;
import com.terraboxstudios.classchartsapi.http.HttpResponse;
import com.terraboxstudios.classchartsapi.obj.ClassChartsDate;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.io.IOException;

@Getter
@AllArgsConstructor (access = AccessLevel.PRIVATE)
public final class StudentCredentials {

    private final String code;
    private final String dateOfBirth;

    public static StudentCredentials from(String code, ClassChartsDate classChartsDate) throws CodeException, IOException, ServerException {
        HttpRequest httpRequest = new HttpRequest.Builder
                ("https://www.classcharts.com/student/checkpupilcode/" + code, "POST")
                .build();
        HttpResponse httpResponse = httpRequest.execute();
        JsonObject jsonObject = JsonParser.parseString(httpResponse.getContent()).getAsJsonObject();
        if (jsonObject.get("success").getAsInt() != 1 || !jsonObject.get("data").getAsJsonObject().get("has_dob").getAsBoolean()) throw new CodeException("No user with a date of birth found for that code.");
        return new StudentCredentials(code, classChartsDate.getDate());
    }

}
