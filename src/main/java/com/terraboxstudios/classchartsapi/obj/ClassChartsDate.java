package com.terraboxstudios.classchartsapi.obj;

import com.terraboxstudios.classchartsapi.exception.DateFormatException;
import com.terraboxstudios.classchartsapi.utils.DateValidator;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.SneakyThrows;

import java.text.SimpleDateFormat;
import java.util.Date;

@AllArgsConstructor (access = AccessLevel.PRIVATE)
public class ClassChartsDate {

    @Getter
    private final String date;
    private static final SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy");

    public static ClassChartsDate from(String date) throws DateFormatException {
        if (DateValidator.isDateValid(date)) {
            return new ClassChartsDate(date);
        }
        throw new DateFormatException("Date of birth provided does not follow format " + format.toPattern());
    }

    public static ClassChartsDate from(Date date) {
        return new ClassChartsDate(format.format(date));
    }

    @SneakyThrows
    public String getTimetableDate() {
        return new SimpleDateFormat("yyyy-MM-dd").format(format.parse(date));
    }

}
