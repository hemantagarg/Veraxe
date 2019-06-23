package com.app.veraxe.model;

import com.google.gson.annotations.SerializedName;

public class ModelExam {

    @SerializedName(value = "subjectId",alternate = "examId")
    private String id;
    @SerializedName(value = "subjectName",alternate = "examName")
    private String name;
    private String examMarks;
    private String studentMarks;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getExamMarks() {
        return examMarks;
    }

    public void setExamMarks(String examMarks) {
        this.examMarks = examMarks;
    }

    public String getStudentMarks() {
        return studentMarks;
    }

    public void setStudentMarks(String studentMarks) {
        this.studentMarks = studentMarks;
    }
}
