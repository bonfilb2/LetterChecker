package com.example.brian.letterchecker;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;

public class Settings extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
    }

    public void clearData(View v) {
        switch(v.getId()) {
            case R.id.clearData:
                UserLocalStore userLocalStore = new UserLocalStore(this);
                userLocalStore.clearUserData();
                break;
        }
    }

}
