package com.example.brian.letterchecker;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class Login extends AppCompatActivity implements AsyncResponse {

    Button btnLogin;
    EditText etUsername, etPassword;

    UserLocalStore userLocalStore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        etUsername = (EditText) findViewById(R.id.etUsername);
        etPassword = (EditText) findViewById(R.id.etPassword);
        btnLogin = (Button) findViewById(R.id.btnLogin);
        userLocalStore = new UserLocalStore(this);

    }

    public void OnLogin(View view) {
        String username = etUsername.getText().toString();
        String password = etPassword.getText().toString();
        String type = "login";

        // Create a new user with the data we want to send to the database
        User user = new User(username, password);

        // Asynctask for server requests
        BackgroundWorker backgroundWorker = new BackgroundWorker(type, user, this);
        backgroundWorker.delegate = this;
        backgroundWorker.execute();
    }

    // Move from the login screen to the register screen
    public void OnReg(View view) {
        startActivity(new Intent(this, Register.class));
    }

    @Override
    public void processFinish(User returnUser) {
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
        if (returnUser != null) {
            // Login successful, store username and password in shared preference file
            userLocalStore = new UserLocalStore(this);
            userLocalStore.storeUserData(returnUser);
            // Move to main menu
            startActivity(new Intent(this, MainActivity.class));
        } else {
            // Wrong username and/or password, show an error message
            dialogBuilder.setMessage("Error logging in.");
            dialogBuilder.setPositiveButton("OK", null);
            dialogBuilder.show();
        }
    }
}
