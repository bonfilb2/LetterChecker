
package com.example.brian.letterchecker;

import android.app.Activity;
import android.content.Intent;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import java.util.ArrayList;

/**
 * Created by Noel Gallagher on 11-Mar-16.
 */

public class PracticeModeAnimationScreen extends Activity {
    private Intent goToPractice;
    private int i;
    private ImageView animationHolder;
    private ImageView backgroundImage;
    private TypedArray currentLetter;
    private TypedArray letterName;
    private ArrayList<Integer> attempts;
    private ArrayList<Integer> success;

    @Override
    protected void onCreate(Bundle savedInstanceState) {


        Bundle extras = getIntent().getExtras();
        i = extras.getInt("i");
        attempts = extras.getIntegerArrayList("attempts");
        success = extras.getIntegerArrayList("success");

        Log.v("Attempts: ", attempts + "");
        Log.v("Success: ", success+"");

        Resources res = getResources();
        letterName = res.obtainTypedArray(R.array.alphabetId);
        currentLetter = res.obtainTypedArray(R.array.prac_alpha);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.practice_mode_animation_screen);

        animationHolder = (ImageView) findViewById(R.id.bg_image);
        backgroundImage = (ImageView) findViewById(R.id.bg_animation);
        backgroundImage.setImageDrawable(currentLetter.getDrawable(i));

        animationHolder.setBackgroundResource(R.drawable.correct_ani);  // this will show the animation for the letter chosen in menu

        AnimationDrawable resultAnimation = (AnimationDrawable) animationHolder.getBackground();
        resultAnimation.start();

        goToPractice = new Intent(this, PracticeMode.class);
        goToPractice.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);



        backgroundImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                backgroundImage.setVisibility(View.GONE);
                currentLetter.recycle();
                letterName.recycle();
                finish();
                goToPractice.putExtra("i", i);
                goToPractice.putExtra("attempts", attempts);
                goToPractice.putExtra("success", success);
                startActivity(goToPractice);
                overridePendingTransition(0, 0);
            }
        });
    }
}
