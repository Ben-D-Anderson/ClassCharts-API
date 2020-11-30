package com.terraboxstudios.classchartsapi.obj;

import com.google.gson.JsonArray;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor (access = AccessLevel.PRIVATE)
public final class Homework {

    private String lesson, subject, teacher, title, description;
    private int id;
    @Getter (value=AccessLevel.NONE)
    private String homework_type, meta_title, issue_date, due_date, completion_time_unit, completion_time_value;
    private Status status;

    public String getHomeworkType() {
        return homework_type;
    }
    public String getMetaTitle() {
        return meta_title;
    }
    public String getIssueDate() {
        return issue_date;
    }
    public String getDueDate() {
        return due_date;
    }
    public String getCompletionTimeUnit() {
        return completion_time_unit;
    }
    public String getCompletionTimeValue() {
        return completion_time_value;
    }

    @Override
    public String toString() {
        return "Homework{" +
                "lesson='" + lesson + '\'' +
                ", subject='" + subject + '\'' +
                ", teacher='" + teacher + '\'' +
                ", homeworkType='" + homework_type + '\'' +
                ", title='" + title + '\'' +
                ", metaTitle='" + meta_title + '\'' +
                ", description='" + description + '\'' +
                ", issueDate='" + issue_date + '\'' +
                ", dueDate='" + due_date + '\'' +
                ", completionTimeUnit='" + completion_time_unit + '\'' +
                ", completionTimeValue='" + completion_time_value + '\'' +
                ", id=" + id +
                ", status=" + status.toString() +
                '}';
    }

    @Getter
    public final static class Status {
        @Getter (value=AccessLevel.NONE)
        private String state, allow_attachments, ticked;
        @Getter (value=AccessLevel.NONE)
        private boolean has_feedback;
        private JsonArray attachments;
        private int id;

        public boolean isAttachmentsAllowed() {
            return !allow_attachments.equalsIgnoreCase("no");
        }
        public boolean isCompleted() {
            return state != null && state.equalsIgnoreCase("completed");
        }
        public boolean isTicked() {
            return ticked.equalsIgnoreCase("yes");
        }
        public boolean hasFeedback() {
            return has_feedback;
        }

        @Override
        public String toString() {
            return "Status{" +
                    "completed=" + isCompleted() +
                    ", attachmentsAllowed=" + isAttachmentsAllowed() +
                    ", ticked=" + isTicked() +
                    ", attachments=" + attachments +
                    ", hasFeedback=" + hasFeedback() +
                    ", id=" + id +
                    '}';
        }

    }

}
