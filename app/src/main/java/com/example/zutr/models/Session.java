package com.example.zutr.models;

public class Session {

    private String student_id;
    private String tutor_id;
    private String time_ended;
    private String time_started;
    private Double wage;
    private String subject;     //future implementation




    public Session(){

    }

    public Session(String student_id, String tutor_id, String time_ended, String time_started, Double wage) {
        this.student_id = student_id;
        this.tutor_id = tutor_id;
        this.time_ended = time_ended;
        this.time_started = time_started;
        this.wage = wage;
    }


    public String getStudent_id() {
        return student_id;
    }

    public void setStudent_id(String student_id) {
        this.student_id = student_id;
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
}
