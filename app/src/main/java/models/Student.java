package models;

public class Student {



    public static final String KEY_USERNAME = "username";
    public static final String KEY_FIRSTNAME = "first_name";
    public static final String KEY_LASTNAME= "last_name";
    public static final String KEY_EMAIL= "email";
    public static final String KEY_ADDRESS = "address";

    private String username;
    private String first_name;
    private String last_name;
    private String email;
    private String address;


    public Student(){

    }


    public Student( String username, String first_name,
                   String last_name, String email, String address) {

        this.username = username;
        this.first_name = first_name;
        this.last_name = last_name;
        this.email = email;
        this.address = address;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getFirst_name() {
        return first_name;
    }

    public void setFirst_name(String first_name) {
        this.first_name = first_name;
    }

    public String getLast_name() {
        return last_name;
    }

    public void setLast_name(String last_name) {
        this.last_name = last_name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }
}
