package com.example.zutr.models;

public class Session {

    public static final String QUESTION_TIME = "000";
    public static final String NO_TUTOR_YET = "404";

    public static final String KEY_SUBJECT = "subject";
    public static final String KEY_QUESTION = "question";

    public static final String KEY_TUTOR_UID = "tutor_id";
    public static final String KEY_STUDENT_UID = "student_id";
    public static final String KEY_WAGE = "wage";

    public static final String PATH = "session";

    public static final int SESSION_INPERSON = 103456;
    public static final int SESSION_VIDEO = 100023;
    public static final int SESSION_CALL = 102345;
    public static final int SESSION_TEXT = 102875;


    private String question;
    private String student_id;
    private String tutor_id;
    private String time_ended;
    private String time_started;
    private Double wage;
    private int session_type;


    private String subject; //future implementation


    public Session() {

    }

    public Session(String student_id, Double wage, String subject, int sessiontype) {
        this.student_id = student_id;
        this.tutor_id = NO_TUTOR_YET;
        this.time_ended = time_ended;
        this.time_started = time_started;
        this.wage = wage;
        this.subject = subject;
        this.session_type = sessiontype;
    }

    public Session(String student_id, Double wage, String subject, String question) {
        this.student_id = student_id;
        this.wage = wage;
        this.subject = subject;
        this.question = question;


        this.tutor_id = NO_TUTOR_YET;
        this.time_ended = QUESTION_TIME;
        this.time_started = QUESTION_TIME;

    }

    public String getStudent_id() {
        return student_id;
    }

    public void setStudent_id(String student_username) {
        this.student_id = student_username;
    }

    public String getTutor_id() {
        return tutor_id;
    }

    public void setTutor_id(String tutor_id) {
        this.tutor_id = tutor_id;
    }

    public String getTime_ended() {
        return time_ended;
    }

    public void setTime_ended(String time_ended) {
        this.time_ended = time_ended;
    }

    public String getTime_started() {
        return time_started;
    }

    public void setTime_started(String time_started) {
        this.time_started = time_started;
    }

    public Double getWage() {
        return wage;
    }

    public void setWage(Double wage) {
        this.wage = wage;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }


    public String getDuration() {

        //TODO Parse time create minus time ended to get time engaged
        return null;
    }

    public String getQuestion() {
        return question;
    }

    public void setQuestion(String question) {
        this.question = question;
    }

    public boolean hasSessionStarted() {
        return (time_started != null && !time_started.isEmpty());
    }

    public boolean isSessionQuestion() {
        return (hasSessionStarted() && time_started.equals(QUESTION_TIME));
    }
}
