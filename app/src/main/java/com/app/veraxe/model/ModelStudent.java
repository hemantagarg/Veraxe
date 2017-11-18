package com.app.veraxe.model;

/**
 * Created by hemanta on 12-11-2016.
 */
public class ModelStudent {

    private String student_id;
    private String name;
    private String school_id;
    private String leave_type_name;
    private String student_remark;
    private String teacher_remark;
    private String leave_type_id, lat, lng, address;

    public String getSection_name() {
        return section_name;
    }

    public void setSection_name(String section_name) {
        this.section_name = section_name;
    }

    private String section_name;
    private String gender, day, month, year, time;
    private String start_date;
    private String end_date;

    public String getClass_name() {
        return class_name;
    }

    public void setClass_name(String class_name) {
        this.class_name = class_name;
    }

    private String class_name;

    public String getStart_date() {
        return start_date;
    }

    public void setStart_date(String start_date) {
        this.start_date = start_date;
    }

    public String getEnd_date() {
        return end_date;
    }

    public void setEnd_date(String end_date) {
        this.end_date = end_date;
    }

    public String getCraeted_on() {
        return craeted_on;
    }

    public void setCraeted_on(String craeted_on) {
        this.craeted_on = craeted_on;
    }

    private String craeted_on;
    private String avtar, orgiginalImage = "";
    private int rowType;

    public int getIsVideo() {
        return isVideo;
    }

    public void setIsVideo(int isVideo) {
        this.isVideo = isVideo;
    }

    private int isVideo;
    private String id;

    public String getIs_approved() {
        return is_approved;
    }

    public void setIs_approved(String is_approved) {
        this.is_approved = is_approved;
    }

    private String is_approved;
    private String subject_id, icon, image;
    private String text;
    private String date_start;
    private String date_end;
    private String title;
    private String attn_status;

    public String getVehicle_no() {
        return vehicle_no;
    }

    public void setVehicle_no(String vehicle_no) {
        this.vehicle_no = vehicle_no;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getGps() {
        return gps;
    }

    public void setGps(String gps) {
        this.gps = gps;
    }

    public String getImei() {
        return imei;
    }

    public void setImei(String imei) {
        this.imei = imei;
    }

    public String getImsi() {
        return imsi;
    }

    public void setImsi(String imsi) {
        this.imsi = imsi;
    }

    private String vehicle_no, color, type, gps, imei, imsi;
    private String add_status, from, student_name;

    public String getBook_media_title() {
        return book_media_title;
    }

    public void setBook_media_title(String book_media_title) {
        this.book_media_title = book_media_title;
    }

    public String getRef_no() {
        return ref_no;
    }

    public void setRef_no(String ref_no) {
        this.ref_no = ref_no;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getPublisher() {
        return publisher;
    }

    public void setPublisher(String publisher) {
        this.publisher = publisher;
    }

    public String getIssue_date() {
        return issue_date;
    }

    public void setIssue_date(String issue_date) {
        this.issue_date = issue_date;
    }

    private String book_media_title, ref_no, subject, category, author, publisher, issue_date;


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
    private String section, logo;


    public String getStudent_class_id() {
        return student_class_id;
    }

    public void setStudent_class_id(String student_class_id) {
        this.student_class_id = student_class_id;
    }

    String student_class_id;

    public String getThubnail() {
        return thubnail;
    }

    public void setThubnail(String thubnail) {
        this.thubnail = thubnail;
    }

    private String thubnail = "";

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

    public String getFile_type() {
        return file_type;
    }

    public void setFile_type(String file_type) {
        this.file_type = file_type;
    }

    String file_type = "";
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

    public String getDay() {
        return day;
    }

    public void setDay(String day) {
        this.day = day;
    }

    public String getMonth() {
        return month;
    }

    public void setMonth(String month) {
        this.month = month;
    }

    public String getYear() {
        return year;
    }

    public void setYear(String year) {
        this.year = year;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getLeave_type_name() {
        return leave_type_name;
    }

    public void setLeave_type_name(String leave_type_name) {
        this.leave_type_name = leave_type_name;
    }

    public String getStudent_remark() {
        return student_remark;
    }

    public void setStudent_remark(String student_remark) {
        this.student_remark = student_remark;
    }

    public String getTeacher_remark() {
        return teacher_remark;
    }

    public void setTeacher_remark(String teacher_remark) {
        this.teacher_remark = teacher_remark;
    }

    public String getLeave_type_id() {
        return leave_type_id;
    }

    public void setLeave_type_id(String leave_type_id) {
        this.leave_type_id = leave_type_id;
    }

    public String getLat() {
        return lat;
    }

    public void setLat(String lat) {
        this.lat = lat;
    }

    public String getLng() {
        return lng;
    }

    public void setLng(String lng) {
        this.lng = lng;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }
}
