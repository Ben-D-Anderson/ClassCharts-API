package com.terraboxstudios.classchartsapi.utils;

public class DateValidator {

    private final static String dateRegex = "^\\d{2}/\\d{2}/\\d{4}$";

    public static boolean isDateValid(String dateStr) {
        return dateStr.matches(dateRegex) && Integer.parseInt(dateStr.split("/")[1]) <= 12;
    }

}
