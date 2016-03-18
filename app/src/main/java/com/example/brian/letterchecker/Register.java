package com.example.brian.letterchecker;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class Register extends AppCompatActivity implements AsyncResponse {

    Button btnRegister;
    private EditText etName, etSurname, etClass, etUsername, etPassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        etName = (EditText) findViewById(R.id.etName);
        etSurname = (EditText) findViewById(R.id.etSurname);
        etClass = (EditText) findViewById(R.id.etClass);
        etUsername = (EditText) findViewById(R.id.etUsername);
        etPassword = (EditText) findViewById(R.id.etPassword);
        btnRegister = (Button) findViewById(R.id.btnRegister);

    }

    public void OnRegister(View view) {
        String str_name = etName.getText().toString();
        String str_surname = etSurname.getText().toString();
        String str_class = etClass.getText().toString();
        String str_username = etUsername.getText().toString();
        String str_password = etPassword.getText().toString();
        String type = "register";

        // Create a new user with the data we want to send to the database
        User user = new User(str_name, str_surname, str_class, str_username, str_password);

        // Asynctask for server requests
        BackgroundWorker backgroundWorker = new BackgroundWorker(type, user, this);
        backgroundWorker.delegate = this;
        backgroundWorker.execute();
    }

    @Override
    public void processFinish(User returnUser) {
        if(returnUser != null) {
            // Register successful, move to login screen
            startActivity(new Intent(this, Login.class));
        }
        else {
            // Show an error message if couldn't register
            AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
            dialogBuilder.setMessage("Error registering.");
            dialogBuilder.setPositiveButton("OK", null);
            dialogBuilder.show();
        }
    }
}
