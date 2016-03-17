package com.example.brian.letterchecker;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
/**
 * Created by Brian on 03/03/16.
 */
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
