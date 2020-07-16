package com.example.zutr.models;

import java.io.Serializable;

public class Session implements Serializable {

    public static final String QUESTION_TIME = "000";
    public static final String NO_TUTOR_YET = "404";

    public static final String KEY_SUBJECT = "subject";
    public static final String KEY_QUESTION = "question";
    public static final String KEY_ANSWER = "answer";

    public static final String KEY_TUTOR_UID = "tutor_id";
    public static final String KEY_STUDENT_UID = "student_id";
    public static final String KEY_WAGE = "wage";

    public static final String PATH = "session";


    public static final int SESSION_VIDEO = 100023;
    public static final int SESSION_CALL = 102345;
    public static final int SESSION_TEXT = 102875;


    private String question;
    private String answer;
    private String studentId;
    private String tutorId;
    private String timeEnded;
    private String timeStart;
    private Double wage;
    private int sessionType;


    private String subject; //future implementation


    public Session() {

    }

    public int getSessionType() {
        return sessionType;
    }

    public void setSessionType(int sessionType) {
        this.sessionType = sessionType;
    }

    public Session(String studentId, Double wage, String question, int sessiontype) {
        this.studentId = studentId;
        this.wage = wage;
        this.question = question;
        this.sessionType = sessiontype;

        this.tutorId = NO_TUTOR_YET;
        this.timeEnded = QUESTION_TIME;
        this.timeStart = QUESTION_TIME;
    }

    public Session(String studentId, Double wage, String subject, String question) {
        this.studentId = studentId;
        this.wage = wage;
        this.subject = subject;
        this.question = question;


        this.tutorId = NO_TUTOR_YET;
        this.timeEnded = QUESTION_TIME;
        this.timeStart = QUESTION_TIME;

    }

    public String getStudentId() {
        return studentId;
    }

    public void setStudentId(String student_username) {
        this.studentId = student_username;
    }

    public String getTutorId() {
        return tutorId;
    }

    public void setTutorId(String tutorId) {
        this.tutorId = tutorId;
    }

    public String getTimeEnded() {
        return timeEnded;
    }

    public void setTimeEnded(String timeEnded) {
        this.timeEnded = timeEnded;
    }

    public String getTimeStart() {
        return timeStart;
    }

    public void setTimeStart(String timeStart) {
        this.timeStart = timeStart;
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
        return (timeStart != null && !timeStart.isEmpty());
    }

    public boolean isSessionQuestion() {
        return (hasSessionStarted() && timeStart.equals(QUESTION_TIME));
    }

    public boolean isFinished() {
        return answer != null;
    }

    public String getAnswer() {
        return answer;
    }

    public void setAnswer(String answer) {
        this.answer = answer;
    }

}
