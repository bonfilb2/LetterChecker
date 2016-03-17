package com.example.brian.letterchecker;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by Noel Gallagher on 04-Mar-16.
 */
public class QuizMode extends Activity implements ASResponseGet{

    @Override
    protected void onStart() {
        super.onStart();

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
        Button b = (Button)findViewById(R.id.quiz_start);
        b.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(QuizMode.this,ThirdActivity.class);
                startActivity(i);
            }
        });

    }

    @Override
    public void processFinish(String attempt) {
        // do something with attempt
        System.out.println("QM: "+attempt);
    }
}