package com.example.brian.letterchecker;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;

import java.util.ArrayList;

/**
 * Portions of this page are modifications based on work created and shared by the Android Open Source Project and used according to terms described in the Creative Commons 2.5 Attribution License.
 * http://developer.android.com/guide/topics/ui/layout/gridview.html
 */

public class LetterMenu extends Activity implements AsyncResponse{
    private Intent practice;
    private ArrayList<Integer> attempts;
    private ArrayList<Integer> success;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.letter_menu_layout);
        practice = new Intent(this, PracticeModeAnimationScreen.class);

        Bundle extras = getIntent().getExtras();

        if(extras!=null) {
            attempts = extras.getIntegerArrayList("attempts");
            success = extras.getIntegerArrayList("success");
        }

        GridView gridview = (GridView) findViewById(R.id.gridMenu);
        gridview.setAdapter(new MenuGrid(LetterMenu.this));

        gridview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
                practice.putExtra("i", position);
                practice.putIntegerArrayListExtra("success", success);
                practice.putIntegerArrayListExtra("attempts", attempts);
                finish();
                startActivity(practice);
            }
        });
    }
    @Override
    public void processFinish(User returnUser) {
        // Do something with returnedUser
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Bundle extras = getIntent().getExtras();

        if(extras!=null) {
            // Send data to database after finished with practice
            String type = "practice";

            SharedPreferences sharedPreferences = getSharedPreferences("userDetails", 0);
            String username = sharedPreferences.getString("username", "");
            String password = sharedPreferences.getString("password", "");

            User user = new User(username, password);

            // Asynctask for server requests
            BackgroundWorker backgroundWorker = new BackgroundWorker(type, user, this, attempts, success);
            backgroundWorker.delegate = this;
            backgroundWorker.execute();
        }
    }
}
