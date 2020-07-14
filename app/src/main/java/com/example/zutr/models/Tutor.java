package com.example.zutr.models;

public class Tutor extends User {


    public static final String KEY_USERNAME = "username";
    public static final String KEY_FIRSTNAME = "first_name";
    public static final String KEY_LASTNAME = "last_name";
    public static final String KEY_EMAIL = "email";
    public static final String KEY_ADDRESS = "address";
    public static final String PATH = "zutr";

    public Tutor() {

    }

    public Tutor(String username, String first_name, String last_name, String email, String address) {
        super(username, first_name, last_name, email, address);
    }
}
