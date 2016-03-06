package com.example.brian.letterchecker;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by Brian on 27/02/2016.
 */
public class UserLocalStore {

    public static final String SP_NAME = "userDetails";
    SharedPreferences userData;

    public UserLocalStore(Context context) {
        // File where shared preference data comes from
        userData = context.getSharedPreferences(SP_NAME, 0);
    }

    public void storeUserData(User user) {
        // Edit what is in shared preference by passing in a user
        SharedPreferences.Editor spEditor = userData.edit();
        spEditor.putString("name", user.name);
        spEditor.putString("surname", user.surname);
        spEditor.putString("class", user.sclass);
        spEditor.putString("username", user.username);
        spEditor.putString("password", user.password);
        spEditor.commit();
    }

    public void setUserLoggedIn(boolean loggedIn) {
        SharedPreferences.Editor spEditor = userData.edit();
        // loggedIn is set to true, put in shared preference
        spEditor.putBoolean("loggedIn", loggedIn);
        spEditor.commit();
    }

    public boolean isEmpty() {
        // Check if shared preference file contains the username key
        if(userData.contains("username")) {
            // Username does show up so it's not empty
            return false;
        }
        else {
            // Username doesn't show up so it must be an empty file
            return true;
        }
    }

    public void clearUserData() {
        // Clear the shared preference file and commit the changes
        SharedPreferences.Editor spEditor = userData.edit();
        spEditor.clear();
        spEditor.commit();
    }
}
