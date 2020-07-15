package com.example.zutr.models;

public class Student extends User {


    public static final String PATH = "student";

    public Student() {

    }

    public Student(String username, String first_name, String last_name, String email, String address) {
        super(username, first_name, last_name, email, address);
    }


}
