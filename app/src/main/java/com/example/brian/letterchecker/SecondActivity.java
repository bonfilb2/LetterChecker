package com.example.brian.letterchecker;

import android.app.Activity;
import android.gesture.Gesture;
import android.gesture.GestureLibraries;
import android.gesture.GestureLibrary;
import android.gesture.GestureOverlayView;
import android.gesture.Prediction;
import android.os.Bundle;
import android.widget.Toast;

import java.util.ArrayList;

/**
 * Created by Brian on 22/02/2016.
 */
public class SecondActivity extends Activity implements GestureOverlayView.OnGesturePerformedListener {

    private GestureLibrary listOfLetters; // library of gestures to check, found in res/raw
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.second_layout);

        listOfLetters = GestureLibraries.fromRawResource(this, R.raw.gesture); //abc is the file containing gestures
        if (!listOfLetters.load()) {    //if you can't load the file, exit
            finish();
        }

        GestureOverlayView gestures = (GestureOverlayView) findViewById(R.id.gesturesOverlay);
        gestures.addOnGesturePerformedListener(this);
    }

    public void onGesturePerformed(GestureOverlayView overlay, Gesture gesture) {
        ArrayList<Prediction> predictions = listOfLetters.recognize(gesture);   //input is tested against entire library of accepted gestures, list ordered by most probable to least
        Prediction prediction = predictions.get(0); //get first prediction (most probable)

        if (prediction.score > 4.0) {       //if input is correct, display message, 4.0 used to ensure stricter, use lower value if comparing against larger library
            //change background image if gesture matches (currently only works for a)
            LinearLayout layout=(LinearLayout)findViewById(R.id.secondLay);
            layout.setBackgroundResource(R.mipmap.b);
        }

    }
}
