package com.example.zutr.models;

public class Tutor extends User {


    public static final String PATH = "zutr";

    public Tutor() {

    }

    public Tutor(String username, String first_name, String last_name, String email, String address) {
        super(username, first_name, last_name, email, address);
    }


}
