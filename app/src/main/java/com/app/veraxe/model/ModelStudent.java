package com.app.veraxe.model;

import android.graphics.Bitmap;

/**
 * Created by hemanta on 12-11-2016.
 */
public class ModelStudent {

    private String student_id;
    private String name;
    private String school_id;
    private String gender;
    private String avtar,orgiginalImage="";
    private int rowType;

    public int getIsVideo() {
        return isVideo;
    }

    public void setIsVideo(int isVideo) {
        this.isVideo = isVideo;
    }

    private int isVideo;
    private String id;
    private String subject_id,icon,image;
    private String text;
    private String date_start;
    private String date_end;
    private String title;
    private String attn_status;
    private String add_status,from,student_name;


    public String getDepartment_id() {
        return department_id;
    }

    public void setDepartment_id(String department_id) {
        this.department_id = department_id;
    }

    public String getDepartment_label() {
        return department_label;
    }

    public void setDepartment_label(String department_label) {
        this.department_label = department_label;
    }

    public String getFeedback_type_id() {
        return feedback_type_id;
    }

    public void setFeedback_type_id(String feedback_type_id) {
        this.feedback_type_id = feedback_type_id;
    }

    public String getFeedback_type_label() {
        return feedback_type_label;
    }

    public void setFeedback_type_label(String feedback_type_label) {
        this.feedback_type_label = feedback_type_label;
    }

    public String getMessage_text() {
        return message_text;
    }

    public void setMessage_text(String message_text) {
        this.message_text = message_text;
    }

    public String getStatus_id() {
        return status_id;
    }

    public void setStatus_id(String status_id) {
        this.status_id = status_id;
    }

    public String getStatus_name() {
        return status_name;
    }

    public void setStatus_name(String status_name) {
        this.status_name = status_name;
    }

    private String department_id;
    private String department_label;
    private String feedback_type_id;
    private String feedback_type_label;
    private String message_text;
    private String status_id;
    private String status_name;

    public String getStream_name() {
        return stream_name;
    }

    public void setStream_name(String stream_name) {
        this.stream_name = stream_name;
    }

    private String stream_name;

    public String getClassname() {
        return classname;
    }

    public void setClassname(String classname) {
        this.classname = classname;
    }

    public String getSection() {
        return section;
    }

    public void setSection(String section) {
        this.section = section;
    }

    private String classname;
    private String section,logo;


    public String getStudent_class_id() {
        return student_class_id;
    }

    public void setStudent_class_id(String student_class_id) {
        this.student_class_id = student_class_id;
    }

    String student_class_id;

    public Bitmap getThubnail() {
        return thubnail;
    }

    public void setThubnail(Bitmap thubnail) {
        this.thubnail = thubnail;
    }

    private Bitmap thubnail;

    public String getFilename() {
        return filename;
    }

    public void setFilename(String filename) {
        this.filename = filename;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    String filename;
    String url;


    public String getAttn_status() {
        return attn_status;
    }

    public void setAttn_status(String attn_status) {
        this.attn_status = attn_status;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public String getAttn_date() {
        return attn_date;
    }

    public void setAttn_date(String attn_date) {
        this.attn_date = attn_date;
    }

    public String getAttendance_name() {
        return attendance_name;
    }

    public void setAttendance_name(String attendance_name) {
        this.attendance_name = attendance_name;
    }

    String remark;
    String attn_date;
    String attendance_name;


    public String getStudent_role() {
        return student_role;
    }

    public void setStudent_role(String student_role) {
        this.student_role = student_role;
    }

    String student_role;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getDatetime() {
        return datetime;
    }

    public void setDatetime(String datetime) {
        this.datetime = datetime;
    }

    String description;
    String datetime;
    int selection;


    public int getSelectedPosition() {
        return selectedPosition;
    }

    public void setSelectedPosition(int selectedPosition) {
        this.selectedPosition = selectedPosition;
    }

    public int getSelection() {
        return selection;
    }

    public void setSelection(int selection) {
        this.selection = selection;
    }

    int selectedPosition;

    public String getRollno() {
        return rollno;
    }

    public void setRollno(String rollno) {
        this.rollno = rollno;
    }

    String rollno;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getSubject_id() {
        return subject_id;
    }

    public void setSubject_id(String subject_id) {
        this.subject_id = subject_id;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getDate_start() {
        return date_start;
    }

    public void setDate_start(String date_start) {
        this.date_start = date_start;
    }

    public String getDate_end() {
        return date_end;
    }

    public void setDate_end(String date_end) {
        this.date_end = date_end;
    }

    public String getSubject_name() {
        return subject_name;
    }

    public void setSubject_name(String subject_name) {
        this.subject_name = subject_name;
    }

    String subject_name;


    public int getRowType() {
        return rowType;
    }

    public void setRowType(int rowType) {
        this.rowType = rowType;
    }

    public String getStudent_id() {
        return student_id;
    }

    public void setStudent_id(String student_id) {
        this.student_id = student_id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSchool_id() {
        return school_id;
    }

    public void setSchool_id(String school_id) {
        this.school_id = school_id;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getAvtar() {
        return avtar;
    }

    public void setAvtar(String avtar) {
        this.avtar = avtar;
    }


    public String getLogo() {
        return logo;
    }

    public void setLogo(String logo) {
        this.logo = logo;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    public String getAdd_status() {
        return add_status;
    }

    public void setAdd_status(String add_status) {
        this.add_status = add_status;
    }

    public String getFrom() {
        return from;
    }

    public void setFrom(String from) {
        this.from = from;
    }

    public String getStudent_name() {
        return student_name;
    }

    public void setStudent_name(String student_name) {
        this.student_name = student_name;
    }

    public String getOrgiginalImage() {
        return orgiginalImage;
    }

    public void setOrgiginalImage(String orgiginalImage) {
        this.orgiginalImage = orgiginalImage;
    }
}
