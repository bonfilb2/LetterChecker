package com.example.brian.letterchecker;

/**
 * Created by Brian on 05/03/16.
 */
public interface AsyncResponse {
    // Used to return a user from BackgroundWorker when it's finished to another class like login or register
    void processFinish(User returnUser);
}
