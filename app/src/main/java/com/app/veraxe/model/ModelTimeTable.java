package com.app.veraxe.model;

/**
 * Created by hemanta on 26-11-2016.
 */

public class ModelTimeTable {

    String PeriodName;
    String daysArray;
    String day;
    String classname;
    String subject;
    String time;
    String teacher_name;


    public String getPeriodName() {
        return PeriodName;
    }

    public void setPeriodName(String periodName) {
        PeriodName = periodName;
    }

    public String getDaysArray() {
        return daysArray;
    }

    public void setDaysArray(String daysArray) {
        this.daysArray = daysArray;
    }

    public String getDay() {
        return day;
    }

    public void setDay(String day) {
        this.day = day;
    }

    public String getClassname() {
        return classname;
    }

    public void setClassname(String classname) {
        this.classname = classname;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getTeacher_name() {
        return teacher_name;
    }

    public void setTeacher_name(String teacher_name) {
        this.teacher_name = teacher_name;
    }


}
