package com.example.brian.letterchecker;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.gesture.Gesture;
import android.gesture.GestureLibraries;
import android.gesture.GestureLibrary;
import android.gesture.GestureOverlayView;
import android.gesture.Prediction;
import android.graphics.drawable.AnimationDrawable;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.SystemClock;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by Brian on 22/02/2016.
 */
public class ThirdActivity extends Activity implements GestureOverlayView.OnGesturePerformedListener, AsyncResponse {

    private GestureLibrary listOfLetters; // library of gestures to check, found in res/raw
    private int i = 0;                  // start of letter cycle s=0, a=1...
    private long time;                  // system time, used during calculations
    private long timeTaken;             // time taken to finish the letter
    private long timeAllowed = 60000;   // get from teacher
    private long totalTime;             // total time of the activity since beginning
    private int attempts;               // the amount of attempts for a letter
    private int attemptsAllowed = 1;    // get from teacher
    private int successAttempt;         // increment if input was successful
    private GestureOverlayView gestures; // transparent overlay for user input
    private Intent finish;  // the activity to move to when finished
    private ImageView animationHolder;  // used to display correct/incorrect animations
    private resultHolder results;       // used to create results at end of activity
    private boolean currentGestureInput;    // used to determine what animation to show

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.second_layout);

        //getQuizInfo();      //will be called to get info about quiz

        time = SystemClock.elapsedRealtime();   // get time quiz starts
        finish = new Intent (this,MainActivity.class);  // go back to menu after completion

        TypedArray ta = getResources().obtainTypedArray(R.array.alphabetId);    // pass all letter names for calculating results
        results =new resultHolder(ta);

        animationHolder = (ImageView) findViewById(R.id.imageResult);

        listOfLetters = GestureLibraries.fromRawResource(this, R.raw.gesture); //abc is the file containing gestures
        if (!listOfLetters.load()) {    //if you can't load the file, exit
            finish();
        }

        gestures = (GestureOverlayView) findViewById(R.id.gesturesOverlay); //set gestureoveraly
        gestures.setGestureStrokeAngleThreshold(90.0f);        // otherwise ignores straight lines
        gestures.setFadeOffset(1000);        //to prevent gestures from disappearing too quickly
        gestures.setGestureStrokeLengthThreshold(0.000000001f);        //to allow small dots to be made; ie for letter i or j
        gestures.addOnGesturePerformedListener(this);   // listen to gesture input on this overlay 

    }

    public void getQuizInfo(){
        //will get the info for the quiz such as attempts allowed, max time etc

    }


    // feedback through animation
    public void resultAni(boolean result) {
        if(result) {
            animationHolder.setBackgroundResource(R.drawable.correct_ani);
        } else {
            animationHolder.setBackgroundResource(R.drawable.incorrect_ani);
        }


        final AnimationDrawable resultAnimation = (AnimationDrawable) animationHolder.getBackground();
        animationHolder.setVisibility(View.VISIBLE);
        resultAnimation.start();

        // Show animation until completion
        Timer timer = new Timer();
        TimerTask timerTask = new TimerTask(){
            @Override
            public void run() {
                resultAnimation.stop();

                // Can only update a view in the thread it was created in.
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        animationHolder.setVisibility(View.INVISIBLE);
                    }
                });

            }
        };

        timer.schedule(timerTask, 750); // how long to wait until animation disappears

    }


    //when a gesture is input:
    public void onGesturePerformed(GestureOverlayView overlay, Gesture gesture) {

        ArrayList<Prediction> predictions = listOfLetters.recognize(gesture);   //input is tested against entire library of accepted gestures, list ordered by most probable to least
        
        ImageView currentImage = (ImageView) findViewById(R.id.imageView);      // used to display image of letter
        Resources res = getResources();                                        
        TypedArray currentLetter = res.obtainTypedArray(R.array.alphabet);    // Used to get the current image
        TypedArray letterName = res.obtainTypedArray(R.array.alphabetId); // Used to get current letter name
        Log.v("We expect : ", " " + letterName.getString(i));
        Log.v("Input evaluated to: ", " " + predictions.get(0).toString());
        //---------------------------------------------------------

        //Sorting out + updating time information  ---------------- 
        timeTaken = SystemClock.elapsedRealtime() - time;       // current time - time since last input was made 
        time = SystemClock.elapsedRealtime();                   // get current time
        totalTime = totalTime + timeTaken/1000;             // store the total time of the activity 
        Log.v("Time since start: ", "" + totalTime);
        //---------------------------------------------------------

        Log.v("attempts allowed : ", + attemptsAllowed + " attempts made: " + attempts);

        attempts++; // gesture is inputted, increase attempts

        //Test whether gesture input is acceptable ----------------
        if (predictions.get(0).toString().equals(letterName.getString(i))) {        // does the most probable input match the expected?
            successAttempt++;   // if so, success 
            currentGestureInput=true;
        }
        else
            currentGestureInput=false;

        //after exceeding attempts allowed, move to next letter
        if(attempts > attemptsAllowed) {
            results.incrementIndex(i, successAttempt);
            i++;        // move to next letter 
            attempts = 0;   // reset for next letter
            successAttempt=0;   // reset for next letter 
        }
        //conditions to end activity, having this within onGesturePerformed allows students to submit last effort
        if(totalTime > timeAllowed/1000 || i == letterName.length()){
            // save totalTime if not exceeding timeAllowed

            /***************************
             *   FINAL RESULT HERE
             *
             * **************************/
            ArrayList<String> quizResults = results.finalResult();

            Log.v("value of arrayList", quizResults + "");

            String type = "quiz";

            SharedPreferences sharedPreferences = getSharedPreferences("userDetails", 0);
            String username = sharedPreferences.getString("username", "");
            String password = sharedPreferences.getString("password", "");
            System.out.println("user: " + username +" password: " + password);

            User user = new User(username, password);

            // Asynctask for server requests
            BackgroundWorker backgroundWorker = new BackgroundWorker(type, user, this, attemptsAllowed, timeAllowed/1000, totalTime, quizResults);
            backgroundWorker.delegate = this;
            backgroundWorker.execute();
        }

        if (i == letterName.length())   // reset index to prevent out of bounds exception
            i = 0;

        currentImage.setImageDrawable(currentLetter.getDrawable(i));    // Move to next image
        
        //Does next gesture require multiple inputs?
        if(letterName.getString(i).equals("a") || letterName.getString(i).equals("p") || letterName.getString(i).equals("t") || letterName.getString(i).equals("i") || letterName.getString(i).equals("n")) // j, k x, etc                                                       // if multiple strokes required
            overlay.setGestureStrokeType(1);
        else
            overlay.setGestureStrokeType(0);
        
        //update UI to provide feedback
        resultAni(currentGestureInput);
    }

    @Override
    public void processFinish(User returnUser) {
        // if returnUser == null something failed
        startActivity(finish);
    }
}
