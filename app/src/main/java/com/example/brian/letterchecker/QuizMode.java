package com.example.brian.letterchecker;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by Noel Gallagher on 04-Mar-16.
 */
public class QuizMode extends Activity implements ASResponseGet {

    // Default quiz settings if no quiz activity was set on website
    private int timeAllowed = 120000;
    private int attemptsAllowed = 3;

    @Override
    protected void onStart() {
        super.onStart();

        // Get current date to see if there was a quiz activity set
        String date = new SimpleDateFormat("dd-MM-yyyy").format(new Date());

        // Asynctask for server requests
        BGWorkerGet bgWorkerGet = new BGWorkerGet(date, this);
        bgWorkerGet.delegate = this;
        bgWorkerGet.execute();

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.quiz_layout);
        Button b = (Button) findViewById(R.id.quiz_start);
        b.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(QuizMode.this, ThirdActivity.class);
                // Pass into next activity
                intent.putExtra("timeAllowed", timeAllowed);
                intent.putExtra("attemptsAllowed", attemptsAllowed);
                startActivity(intent);
            }
        });

    }

    @Override
    public void processFinish(String attempt) {
        // If returned string from database is not null
        if (attempt != null) {
            // Search for "attempts" in string, if comma is reached then stop
            int att = attempt.indexOf("attempts") + 10;
            int att_fin = attempt.indexOf(",", att);
            int attempts = Integer.parseInt(attempt.substring(att, att_fin));

            // Search for "time" in string, if } is reached then stop
            int tim = attempt.indexOf("time") + 6;
            int tim_fin = attempt.indexOf("}", tim);
            int time = Integer.parseInt(attempt.substring(tim, tim_fin));

            // Overwrite defaults with new values gotten from the database
            timeAllowed = time * 60000;
            attemptsAllowed = attempts;

        }
    }
}