package com.terraboxstudios.classchartsapi.obj;

import com.terraboxstudios.classchartsapi.exception.CodeException;
import com.terraboxstudios.classchartsapi.exception.DateFormatException;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor (access = AccessLevel.PRIVATE)
public final class StudentCredentials {

    @Getter
    private final String code;
    @Getter
    private final String dateOfBirth;
    private final static String dateRegex = "^\\d{2}-\\d{2}-\\d{4}$";
    private final static int codeLength = 8;

    public static StudentCredentials from(String code, String dateOfBirth) throws DateFormatException, CodeException {
        if (code.length() != codeLength) throw new CodeException("Code does not match length requirement (" + codeLength + ")");
        if (!dateOfBirth.matches(dateRegex)) throw new DateFormatException("Date of birth provided does not follow format dd/MM/yyyy");
        return new StudentCredentials(code, dateOfBirth);
    }

}
