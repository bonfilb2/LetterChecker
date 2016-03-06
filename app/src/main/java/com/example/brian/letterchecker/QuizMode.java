package com.example.brian.letterchecker;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

/**
 * Created by Noel Gallagher on 04-Mar-16.
 */
public class QuizMode extends Activity{

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.quiz_layout);
        Button b = (Button)findViewById(R.id.quiz_start);
        b.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(QuizMode.this,ThirdActivity.class);
                startActivity(i);
            }
        });

    }

}