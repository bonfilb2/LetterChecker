package com.example.brian.letterchecker;

import android.app.Activity;
import android.content.Intent;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.gesture.Gesture;
import android.gesture.GestureLibraries;
import android.gesture.GestureLibrary;
import android.gesture.GestureOverlayView;
import android.gesture.Prediction;
import android.os.Bundle;
import android.os.SystemClock;
import android.util.Log;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;

/**
 * Created by Brian on 22/02/2016.
 */
public class ThirdActivity extends Activity implements GestureOverlayView.OnGesturePerformedListener {

    private GestureLibrary listOfLetters; // library of gestures to check, found in res/raw
    private int i = 0;
    private long time;
    private long timeTaken;
    private long timeAllowed = 60000;   //get from teacher
    private long totalTime;
    private int attempts;
    private int attemptsAllowed = 1;    //get from teacher
    private String attemptResult;
    private int successAttempt;

    private Intent finish;  // not sure if this is how to end activity?


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.second_layout);
        getQuizInfo();      //will be called to get info about quiz
        time = SystemClock.elapsedRealtime();   // get time quiz starts
        finish = new Intent (this,MainActivity.class);  // go back to menu after condition


        listOfLetters = GestureLibraries.fromRawResource(this, R.raw.gesture); //abc is the file containing gestures
        if (!listOfLetters.load()) {    //if you can't load the file, exit
            finish();
        }

        GestureOverlayView gestures = (GestureOverlayView) findViewById(R.id.gesturesOverlay);
        gestures.setGestureStrokeAngleThreshold(90.0f);        // otherwise ignores straight lines
        gestures.setFadeOffset(500);        //to prevent gestures from disappearing too quickly
        gestures.setGestureStrokeLengthThreshold(0.000001f);        //to allow small dots to be made; ie for letter i or j
        gestures.addOnGesturePerformedListener(this);

    }



    public void getQuizInfo(){
        //will get the info for the quiz such as attempts allowed, max time etc

    }

    //when a gesture is made:
    public void onGesturePerformed(GestureOverlayView overlay, Gesture gesture) {
        attempts++; // gesture is inputted, increase attempts
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
        TextView t = (TextView)findViewById(R.id.textView4);
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
            startActivity(finish);
        }

        Log.v("attempts allowed : ", + attemptsAllowed + " attempts made: " + attempts);

        //Test whether gesture input is acceptable ----------------
        if (predictions.get(0).toString().equals(letterName.getString(i)) || attempts > attemptsAllowed){       // if top prediction matches what you're looking for

            if (prediction.score > 1.0 || attempts > attemptsAllowed) {       //if input is correct, display message, 4.0 used to ensure stricter, use lower value if comparing against larger library
                //change background image if gesture matches (currently only works for a)

                successAttempt++;

                //after exceeding attempts allowed, move to next letter
                if(attempts > attemptsAllowed) {
                    //results for current letter all in one string, can break up if easier:
                    attemptResult = "Letter : " + letterName.getString(i) + " results: "+ successAttempt + "/" + (attemptsAllowed+1); // 0 attemptsAllowed = 1
                    Log.v("attemptResult", attemptResult);
                    //move to next letter, reset variables
                    i++;
                    attempts = 0;
                    successAttempt=0;
                }

                if (i == currentLetter.length())
                    i = 0;              // go back to index first letter, just used for testing

                currentImage.setImageDrawable(currentLetter.getDrawable(i));
                t.setText(letterName.getString(i));
                if(letterName.getString(i).equals("t") || letterName.getString(i).equals("i")) // j, k x, etc                                                       // if multiple strokes required
                    overlay.setGestureStrokeType(1);
                else
                    overlay.setGestureStrokeType(0);
            }
        }

        currentLetter.recycle();  // Set up for garbage collection
        letterName.recycle();
    }
}
