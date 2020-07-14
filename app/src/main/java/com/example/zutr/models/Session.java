package com.example.zutr.models;

import android.text.format.DateUtils;

import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.sql.Time;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class Session {

    public static final String QUESTION_TIME = "000";

    private String student_username;
    private String tutor_username;
    private String time_ended;
    private String time_started;
    private Double wage;
    private String subject; //future implementation

    private String question;


    public Session() {

    }

    public Session(String student_username, String tutor_username, String time_ended, String time_started, Double wage, String subject) {
        this.student_username = student_username;
        this.tutor_username = tutor_username;
        this.time_ended = time_ended;
        this.time_started = time_started;
        this.wage = wage;
        this.subject = subject;
    }

    public Session(String student_username, String tutor_username, Double wage, String subject, String question) {
        this.student_username = student_username;
        this.tutor_username = tutor_username;
        this.time_ended = QUESTION_TIME;
        this.time_started = QUESTION_TIME;
        this.wage = wage;
        this.subject = subject;
        this.question = question;
    }

    public String getStudent_username() {
        return student_username;
    }

    public void setStudent_username(String student_username) {
        this.student_username = student_username;
    }

    public String getTutor_username() {
        return tutor_username;
    }

    public void setTutor_username(String tutor_username) {
        this.tutor_username = tutor_username;
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
}
