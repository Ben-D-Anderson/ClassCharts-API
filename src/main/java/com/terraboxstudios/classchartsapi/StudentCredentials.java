package com.terraboxstudios.classchartsapi;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.terraboxstudios.classchartsapi.exception.CodeException;
import com.terraboxstudios.classchartsapi.exception.DateFormatException;
import com.terraboxstudios.classchartsapi.exception.ServerException;
import com.terraboxstudios.classchartsapi.http.HttpRequest;
import com.terraboxstudios.classchartsapi.http.HttpResponse;
import com.terraboxstudios.classchartsapi.utils.DateValidator;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import java.io.IOException;

@Getter
@AllArgsConstructor (access = AccessLevel.PRIVATE)
public final class StudentCredentials {

    private final String code;
    private final String dateOfBirth;

    public static StudentCredentials from(String code, String dateOfBirth) throws DateFormatException, CodeException, IOException, ServerException {
        HttpRequest httpRequest = new HttpRequest.Builder
                ("https://www.classcharts.com/student/checkpupilcode/" + code, "POST")
                .build();
        HttpResponse httpResponse = httpRequest.execute();
        JsonObject jsonObject = JsonParser.parseString(httpResponse.getContent()).getAsJsonObject();
        if (jsonObject.get("success").getAsInt() != 1 || !jsonObject.get("data").getAsJsonObject().get("has_dob").getAsBoolean()) throw new CodeException("No user with a date of birth found for that code.");
        if (!DateValidator.isDateValid(dateOfBirth)) throw new DateFormatException("Date of birth provided does not follow format dd/MM/yyyy");
        return new StudentCredentials(code, dateOfBirth);
    }

}
