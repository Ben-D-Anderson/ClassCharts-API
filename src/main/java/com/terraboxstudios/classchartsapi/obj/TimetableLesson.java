package com.terraboxstudios.classchartsapi.obj;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class TimetableLesson {

    private String date, note;
    private int id;
    private long key;
    @Getter (value=AccessLevel.NONE)
    private String teacher_name, lesson_name, subject_name, period_name, room_name, period_number, start_time, end_time;
    @Getter (value=AccessLevel.NONE)
    private boolean is_alternative_lesson;

    public String getTeacherName() {
        return teacher_name;
    }
    public String getLessonName() {
        return lesson_name;
    }
    public String getSubjectName() {
        return subject_name;
    }
    public String getPeriodName() {
        return period_name;
    }
    public String getRoomName() {
        return room_name;
    }
    public String getPeriodNumber() {
        return period_number;
    }
    public String getStartTime() {
        return start_time;
    }
    public String getEndTime() {
        return end_time;
    }
    public boolean isAlternativeLesson() {
        return is_alternative_lesson;
    }

    @Override
    public String toString() {
        return "TimetableLesson{" +
                "date='" + date + '\'' +
                ", note='" + note + '\'' +
                ", id=" + id +
                ", key=" + key +
                ", teacher_name='" + teacher_name + '\'' +
                ", lesson_name='" + lesson_name + '\'' +
                ", subject_name='" + subject_name + '\'' +
                ", period_name='" + period_name + '\'' +
                ", room_name='" + room_name + '\'' +
                ", period_number='" + period_number + '\'' +
                ", start_time='" + start_time + '\'' +
                ", end_time='" + end_time + '\'' +
                ", is_alternative_lesson=" + is_alternative_lesson +
                '}';
    }
}
