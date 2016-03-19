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
import android.graphics.drawable.AnimationDrawable;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import java.util.ArrayList;
import java.io.File;
import java.util.Timer;
import java.util.TimerTask;


/**
 * Created by Brian on 22/02/2016.
 */
public class PracticeMode extends Activity implements GestureOverlayView.OnGesturePerformedListener {

    private GestureLibrary listOfLetters; // library of gestures to check, found in res/raw
    private int i;
    private GestureOverlayView gestures;
    private boolean result;
    private ImageView img;
    private ImageView currentImage;
    private TypedArray currentLetter;
    private TypedArray sounds;
    private TypedArray letterName;
    private Intent finish;
    private resultHolder practiceAttempts;
    private resultHolder practiceSuccess;
    private ArrayList<Integer> attempts;
    private ArrayList<Integer> success;
    private MediaPlayer m, playSound;
    private Button backButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Bundle extras = getIntent().getExtras();
        i = extras.getInt("i");
        attempts = extras.getIntegerArrayList("attempts");
        success = extras.getIntegerArrayList("success");
        backButton = (Button) findViewById(R.id.button3);
        m = new MediaPlayer();

        Log.v("Attempts: ", attempts+"");
        Log.v("Success: ", success+"");

        Resources res = getResources();
        currentLetter = res.obtainTypedArray(R.array.prac_alpha);
        letterName = res.obtainTypedArray(R.array.alphabetId);
        sounds = res.obtainTypedArray(R.array.letterSounds);

        playSound = MediaPlayer.create(this, sounds.getResourceId(i,0));

        practiceAttempts = new resultHolder(letterName);
        practiceSuccess = new resultHolder(letterName);

        if(attempts==null || success==null)
        {
            attempts = practiceAttempts.getPracticeResults();
            success = practiceSuccess.getPracticeResults();
        }

        practiceAttempts.addArrayLists(attempts, practiceAttempts);
        practiceSuccess.addArrayLists(success, practiceSuccess);


        super.onCreate(savedInstanceState);
        setContentView(R.layout.second_layout);

        img = (ImageView) findViewById(R.id.imageResult);
        currentImage = (ImageView) findViewById(R.id.imageView);
        currentImage.setImageDrawable(currentLetter.getDrawable(i));

        listOfLetters = GestureLibraries.fromRawResource(this, R.raw.gesture); //abc is the file containing gestures
        if (!listOfLetters.load()) {    //if you can't load the file, exit
            finish();
        }


        gestures = (GestureOverlayView) findViewById(R.id.gesturesOverlay);
        gestures.setGestureStrokeAngleThreshold(90.0f);        // otherwise ignores straight lines
        gestures.setFadeOffset(1000);        //to prevent gestures from disappearing too quickly
        gestures.setGestureStrokeLengthThreshold(0.000000001f);        //to allow small dots to be made; ie for letter i or j
        gestures.addOnGesturePerformedListener(this);
        if(letterName.getString(i).equals("a") || letterName.getString(i).equals("p") || letterName.getString(i).equals("t") || letterName.getString(i).equals("i") || letterName.getString(i).equals("n")) // j, k x, etc                                                       // if multiple strokes required
            gestures.setGestureStrokeType(1);
        else
            gestures.setGestureStrokeType(0);

    }

    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.button:
                playSound.start();
                break;
            case R.id.button2: {
                m.release();
                playSound.release();
                finish = new Intent(this, PracticeModeAnimationScreen.class);
                finish();
                finish.putExtra("attempts", attempts);
                finish.putExtra("i", i);
                finish.putExtra("success", success);
                startActivity(finish);
                overridePendingTransition(0, 0);
                break;
            }
            case R.id.button3: {
                backToMenu();
                m.release();
                playSound.release();
                break;
            }
        }
    }


    public void resultAni(boolean result) {
        if(result) {
            img.setBackgroundResource(R.drawable.correct_ani);
        }
        else{
            img.setBackgroundResource(R.drawable.incorrect_ani);
        }

        finish = getIntent();

        finish.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);

        final AnimationDrawable resultAnimation = (AnimationDrawable) img.getBackground();
        resultAnimation.start();
        m.start();

        gestures.removeAllOnGesturePerformedListeners(); // if accidentally draw gesture instead of button press, do not evaluate

        Timer timer = new Timer();
        TimerTask timerTask = new TimerTask() {
            @Override
            public void run() {
                resultAnimation.stop();
                attempts = practiceAttempts.getPracticeResults();
                success = practiceSuccess.getPracticeResults();
                currentLetter.recycle();
                letterName.recycle();

                //   m.release();
                //  playSound.release();

                // Can only update a view in the thread it was created in.
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        //gestures.clear(true);       // ignore user input if they tried to draw on animation
                        img.setVisibility(View.GONE);
                    }
                });

                finish();
                finish.putExtra("attempts", attempts);
                finish.putExtra("i", i);
                finish.putExtra("success", success);
                startActivity(finish);
                overridePendingTransition(0, 0);


            }
        };

        timer.schedule(timerTask,750); // use value to inc/dec time for animation to appear for


    }



    //when a gesture is made:
    public void onGesturePerformed(GestureOverlayView overlay, Gesture gesture) {
        currentImage = (ImageView) findViewById(R.id.imageView);
        ArrayList<Prediction> predictions = listOfLetters.recognize(gesture);   //input is tested against entire library of accepted gestures, list ordered by most probable to least
        Prediction prediction = predictions.get(0); //get first prediction (most probable)

        practiceAttempts.incrementIndex(i, 1);

        //Test whether gesture input is acceptable ----------------
        if (predictions.get(0).toString().equals(letterName.getString(i)) && prediction.score > 1.0) {      // if top prediction matches what you're looking for
            result = true;
            practiceSuccess.incrementIndex(i, 1);
            m = MediaPlayer.create(this, R.raw.correct);
        }
        else {
            result = false;
            m = MediaPlayer.create(this, R.raw.incorrect);
        }

        resultAni(result);
    }
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        backToMenu();
    }
    public void backToMenu(){
        m.release();
        playSound.release();
        finish = new Intent(this, LetterMenu.class);
        finish();
        finish.putExtra("attempts", attempts);
        finish.putExtra("i", i);
        finish.putExtra("success", success);
        startActivity(finish);
        overridePendingTransition(0, 0);
    }

}
