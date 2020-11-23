package com.terraboxstudios.classchartsapi.enums;

import lombok.Getter;

public enum DisplayDate {

    ISSUE("issue_date"),
    DUE("due_date");

    @Getter
    private final String displayDate;

    DisplayDate(String displayDate) {
        this.displayDate = displayDate;
    }

}
