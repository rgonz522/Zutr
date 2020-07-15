package com.example.zutr.models;

public class Session {

    public static final String QUESTION_TIME = "000";

    public static final String KEY_SUBJECT = "subject";
    public static final String KEY_QUESTION = "question";
    public static final String KEY_TIMEEND = "time_ended";
    public static final String KEY_TIMESTART = "time_ended";
    public static final String KEY_TUTOREMAIL = "tutor_email";
    public static final String KEY_STUDENT_EMAIL = "student_email";
    public static final String KEY_WAGE = "wage";

    public static final String PATH = "session";

    public static final int SESSION_INPERSON = 103456;
    public static final int SESSION_VIDEO = 100023;
    public static final int SESSION_CALL = 102345;
    public static final int SESSION_TEXT = 102875;


    private String question;
    private String student_email;
    private String tutor_email;
    private String time_ended;
    private String time_started;
    private Double wage;
    private int session_type;


    private String subject; //future implementation


    public Session() {

    }

    public Session(String student_email, String tutor_email, Double wage, String subject, int sessiontype) {
        this.student_email = student_email;
        this.tutor_email = tutor_email;
        this.time_ended = time_ended;
        this.time_started = time_started;
        this.wage = wage;
        this.subject = subject;
        this.session_type = sessiontype;
    }

    public Session(String student_email, String tutor_email, Double wage, String subject, String question) {
        this.student_email = student_email;
        this.tutor_email = tutor_email;
        this.time_ended = QUESTION_TIME;
        this.time_started = QUESTION_TIME;
        this.wage = wage;
        this.subject = subject;
        this.question = question;
    }

    public String getStudent_email() {
        return student_email;
    }

    public void setStudent_email(String student_username) {
        this.student_email = student_username;
    }

    public String getTutor_email() {
        return tutor_email;
    }

    public void setTutor_email(String tutor_email) {
        this.tutor_email = tutor_email;
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
        return (time_started.equals(QUESTION_TIME));
    }
}
