package com.terraboxstudios.classchartsapi.obj;

import com.terraboxstudios.classchartsapi.exception.DateFormatException;
import com.terraboxstudios.classchartsapi.utils.DateValidator;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.text.SimpleDateFormat;
import java.util.Date;

@AllArgsConstructor (access = AccessLevel.PRIVATE)
public class ClassChartsDate {

    @Getter
    private final String date;

    public static ClassChartsDate from(String date) throws DateFormatException {
        if (DateValidator.isDateValid(date)) {
            return new ClassChartsDate(date);
        }
        throw new DateFormatException("Date of birth provided does not follow format dd/MM/yyyy");
    }

    public static ClassChartsDate from(Date date) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd/MM/yyyy");
        String formattedDate = simpleDateFormat.format(date);
        return new ClassChartsDate(formattedDate);
    }

}
