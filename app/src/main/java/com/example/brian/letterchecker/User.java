package com.example.brian.letterchecker;

/**
 * Created by Brian on 27/02/2016.
 */
public class User {
    // Class to contain user information that will be sent to the database and stored in shared preference file
    String name, surname, sclass, username, password;

    public User (String name, String surname, String sclass, String username, String password) {
        this.name = name;
        this.surname = surname;
        this.sclass = sclass;
        this.username = username;
        this.password = password;
    }

    public User(String username, String password) {
        this.username = username;
        this.password = password;
        this.sclass = "";
        this.name = "";
        this.surname = "";
    }

}
