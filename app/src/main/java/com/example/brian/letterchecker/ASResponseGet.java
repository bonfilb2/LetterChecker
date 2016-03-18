package com.example.brian.letterchecker;

/**
 * Created by Brian on 15/03/16.
 */
public interface ASResponseGet {
    // Used to return a string from BGWorkerGet when it's finished to QuizMode class
    void processFinish(String attempt);
}