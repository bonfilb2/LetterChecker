package com.example.brian.letterchecker;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    @Override
    protected void onStart() {
        super.onStart();
        UserLocalStore userLocalStore = new UserLocalStore(this);

        if(userLocalStore.isEmpty()) {
            // if shared preferences file is empty go to login
            startActivity(new Intent(this, Login.class));
        }
        // stay on main activity there's already a login saved
    }

    public void moveActivity(View v){

        switch(v.getId()){
            case R.id.secondActivityBtn:
                startActivity(new Intent(this, SecondActivity.class));
                break;
            case R.id.thirdActivityBtn:
                startActivity(new Intent(this, QuizMode.class));
                break;
            case R.id.btnSettings:
                startActivity(new Intent(this, Settings.class));
        }
    }

}
