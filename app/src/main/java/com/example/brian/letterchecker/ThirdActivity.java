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
    private int i = 0;
    private long time;
    private long timeTaken;
    private long timeAllowed = 60000;   //get from teacher
    private long totalTime;
    private int attempts;
    private int attemptsAllowed = 1;    //get from teacher
    private int successAttempt;
    private GestureOverlayView gestures;
    private Intent finish;  // not sure if this is how to end activity?
    private resultHolder results;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.second_layout);
        getQuizInfo();      //will be called to get info about quiz
        time = SystemClock.elapsedRealtime();   // get time quiz starts
        finish = new Intent (this,MainActivity.class);  // go back to menu after condition
        TypedArray ta = getResources().obtainTypedArray(R.array.alphabetId);
        results =new resultHolder(ta);
        //ta.recycle();

        listOfLetters = GestureLibraries.fromRawResource(this, R.raw.gesture); //abc is the file containing gestures
        if (!listOfLetters.load()) {    //if you can't load the file, exit
            finish();
        }

        gestures = (GestureOverlayView) findViewById(R.id.gesturesOverlay);
        gestures.setGestureStrokeAngleThreshold(90.0f);        // otherwise ignores straight lines
        gestures.setFadeOffset(1000);        //to prevent gestures from disappearing too quickly
        gestures.setGestureStrokeLengthThreshold(0.000000001f);        //to allow small dots to be made; ie for letter i or j
        gestures.addOnGesturePerformedListener(this);

    }

    public void getQuizInfo(){
        //will get the info for the quiz such as attempts allowed, max time etc

    }


    //when a gesture is made:
    public void onGesturePerformed(GestureOverlayView overlay, Gesture gesture) {

        ArrayList<Prediction> predictions = listOfLetters.recognize(gesture);   //input is tested against entire library of accepted gestures, list ordered by most probable to least
        Prediction prediction = predictions.get(0); //get first prediction (most probable)
        Log.v("prediction 1:", " " + predictions.get(0));
        Log.v("prediction 2:", " " + predictions.get(1));
        Log.v("value of i:", " " + i);

        //fix this -----------------------------------------------
        ImageView currentImage = (ImageView) findViewById(R.id.imageView);
        Resources res = getResources();
        TypedArray currentLetter = res.obtainTypedArray(R.array.alphabet);    //obtain info from arrays.xml
        TypedArray letterName = res.obtainTypedArray(R.array.alphabetId); //same as above
        Log.v("prediction i:", " " + predictions.get(0).toString().equals(letterName.getString(i)));
        //---------------------------------------------------------

        //Sorting out + updating time information  ---------------- need to make method for this
        timeTaken = SystemClock.elapsedRealtime() - time;
        time = SystemClock.elapsedRealtime();
        totalTime = totalTime + timeTaken/1000;             //time in milliseconds divide by 1000 for value in seconds
        Log.v("timeAllowed: ", "" + timeAllowed/1000 + " time taken : " + totalTime);
        //---------------------------------------------------------

        //conditions to end activity, having this within onGesturePerformed allows students to submit last effort
        if(totalTime > timeAllowed/1000){
            // save totalTime if not exceeding timeAllowed

            /***************************
             *   FINAL RESULT HERE
             *
             * **************************/
            ArrayList<String> quizResults = results.finalResult();

            Log.v("value of arrayList", quizResults + "");

            String type = "quiz";

            SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
            String username = sharedPreferences.getString("username", "");
            String password = sharedPreferences.getString("password", "");

            User user = new User(username, password);

            // Asynctask for server requests
            BackgroundWorker backgroundWorker = new BackgroundWorker(type, user, this, quizResults);
            backgroundWorker.delegate = this;
            backgroundWorker.execute();
        }

        Log.v("attempts allowed : ", + attemptsAllowed + " attempts made: " + attempts);

        attempts++; // gesture is inputted, increase attempts

        //Test whether gesture input is acceptable ----------------
        if (predictions.get(0).toString().equals(letterName.getString(i))) {
            successAttempt++;
        }
        //after exceeding attempts allowed, move to next letter
        if(attempts > attemptsAllowed) {

            results.incrementIndex(i, successAttempt);
            i++;
            attempts = 0;
            successAttempt=0;
        }

        if (i == currentLetter.length())
            i = 0;              // go back to index first letter, just used for testing

        currentImage.setImageDrawable(currentLetter.getDrawable(i));

        if(letterName.getString(i).equals("a") || letterName.getString(i).equals("p") || letterName.getString(i).equals("t") || letterName.getString(i).equals("i") || letterName.getString(i).equals("n")) // j, k x, etc                                                       // if multiple strokes required
            overlay.setGestureStrokeType(1);
        else
            overlay.setGestureStrokeType(0);

        //currentLetter.recycle();  // Set up for garbage collection
        //letterName.recycle();
    }

    @Override
    public void processFinish(User returnUser) {
        // if returnUser == null something failed
        startActivity(finish);
    }
}
