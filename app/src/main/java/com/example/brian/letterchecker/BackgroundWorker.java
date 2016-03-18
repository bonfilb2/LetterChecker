package com.example.brian.letterchecker;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

/**
 * Created by Brian on 03/03/16.
 */

/**
 * Portions of this page dealing with http connections was taken from Programming Knowledge blog site, I take no credit for its design.
 * programmingknowledgeblog.blogspot.ie
 */

public class BackgroundWorker extends AsyncTask<Void,Void,User> {
    Context context;
    private User user;
    private String type;
    private int attemptsAllowed;
    private long timeTaken;
    private long timeAllowed;
    private ArrayList<String> quizResults;
    private ArrayList<Integer> attempts;
    private ArrayList<Integer> success;

    private ProgressDialog progressDialog;

    // Create a new AsyncResponse
    public AsyncResponse delegate = null;

    public BackgroundWorker (String type, User user, Context context) {
        // Constructor takes in the type of server operation, a user and a context
        this.type = type;
        this.user = user;
        this.context = context;
        progressDialog = new ProgressDialog(context);
        progressDialog.setCancelable(false);
        progressDialog.setTitle("Connecting to server");
        progressDialog.setMessage("Please wait...");
        progressDialog.show();
    }
    // Second constructor (quiz) for an array of letters, no progress dialog
    public BackgroundWorker (String type, User user, Context context, int attemptsAllowed, long timeAllowed, long timeTaken, ArrayList<String> quizResults) {
        this.type = type;
        this.user = user;
        this.context = context;
        this.attemptsAllowed = attemptsAllowed;
        this.timeAllowed = timeAllowed;
        this.timeTaken = timeTaken;
        this.quizResults = quizResults;
    }
    // Third constructor (practice) for
    public BackgroundWorker (String type, User user, Context context, ArrayList<Integer> attempts, ArrayList<Integer> success) {
        this.type = type;
        this.user = user;
        this.context = context;
        this.attempts = attempts;
        this.success = success;
    }

    @Override
    protected User doInBackground(Void... params) {
        String login_url = "http://student.computing.dcu.ie/~bonfilb2/login.php";
        String register_url = "http://student.computing.dcu.ie/~bonfilb2/register.php";
        String quiz_url = "http://student.computing.dcu.ie/~bonfilb2/quiz.php";
        String practice_url = "http://student.computing.dcu.ie/~bonfilb2/practice.php";
        if (type.equals("login")) {
            try {
                String user_name = user.username;
                String password = user.password;
                URL url = new URL(login_url);
                HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
                httpURLConnection.setRequestMethod("POST");
                httpURLConnection.setDoOutput(true);
                httpURLConnection.setDoInput(true);
                OutputStream outputStream = httpURLConnection.getOutputStream();
                BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(outputStream, "UTF-8"));
                String post_data = URLEncoder.encode("user_name", "UTF-8") + "=" + URLEncoder.encode(user_name, "UTF-8") + "&"
                        + URLEncoder.encode("password", "UTF-8") + "=" + URLEncoder.encode(password, "UTF-8");
                bufferedWriter.write(post_data);
                bufferedWriter.flush();
                bufferedWriter.close();
                outputStream.close();
                InputStream inputStream = httpURLConnection.getInputStream();
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream, "iso-8859-1"));
                String result = "";
                String line = "";
                // Build a string from the returned php script message
                while ((line = bufferedReader.readLine()) != null) {
                    result += line;
                }
                bufferedReader.close();
                inputStream.close();
                httpURLConnection.disconnect();
                // Check if php script did not return this message, if it didn't login wasn't a success
                if (!result.equals("Login successful")) {
                    // Set user to null to indicate a failed login
                    user = null;
                }
                progressDialog.dismiss();
                return user;
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else if (type.equals("register")) {
            try {
                String name = user.name;
                String surname = user.surname;
                String sclass = user.sclass;
                String username = user.username;
                String password = user.password;
                URL url = new URL(register_url);
                HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
                httpURLConnection.setRequestMethod("POST");
                httpURLConnection.setDoOutput(true);
                httpURLConnection.setDoInput(true);
                OutputStream outputStream = httpURLConnection.getOutputStream();
                BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(outputStream, "UTF-8"));
                String post_data = URLEncoder.encode("name", "UTF-8") + "=" + URLEncoder.encode(name, "UTF-8") + "&"
                        + URLEncoder.encode("surname", "UTF-8") + "=" + URLEncoder.encode(surname, "UTF-8") + "&"
                        + URLEncoder.encode("class", "UTF-8") + "=" + URLEncoder.encode(sclass, "UTF-8") + "&"
                        + URLEncoder.encode("username", "UTF-8") + "=" + URLEncoder.encode(username, "UTF-8") + "&"
                        + URLEncoder.encode("password", "UTF-8") + "=" + URLEncoder.encode(password, "UTF-8");
                bufferedWriter.write(post_data);
                bufferedWriter.flush();
                bufferedWriter.close();
                outputStream.close();
                InputStream inputStream = httpURLConnection.getInputStream();
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream, "iso-8859-1"));
                String result = "";
                String line = "";
                // Build a string from the returned php script message
                while ((line = bufferedReader.readLine()) != null) {
                    result += line;
                }
                bufferedReader.close();
                inputStream.close();
                httpURLConnection.disconnect();
                // Check if php script did not return this message, if it didn't register wasn't a success
                if (!result.equals("Insert successful")) {
                    user = null;
                }
                progressDialog.dismiss();
                return user;
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else if (type.equals("quiz")) {
            try {
                URL url = new URL(quiz_url);
                HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
                httpURLConnection.setRequestMethod("POST");
                httpURLConnection.setDoOutput(true);
                httpURLConnection.setDoInput(true);
                OutputStream outputStream = httpURLConnection.getOutputStream();
                BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(outputStream, "UTF-8"));
                StringBuilder stringBuilder = new StringBuilder();
                // Add in date to start of string
                String date = new SimpleDateFormat("dd-MM-yyyy").format(new Date());
                stringBuilder.append(URLEncoder.encode("date", "UTF-8") + "=" + URLEncoder.encode(date, "UTF-8"));
                stringBuilder.append("&");
                String username = user.username;
                stringBuilder.append(URLEncoder.encode("username", "UTF-8") + "=" + URLEncoder.encode(username, "UTF-8"));
                stringBuilder.append("&");
                stringBuilder.append(URLEncoder.encode("attempts", "UTF-8") + "=" + URLEncoder.encode("" + attemptsAllowed, "UTF-8"));
                stringBuilder.append("&");
                stringBuilder.append(URLEncoder.encode("time", "UTF-8") + "=" + URLEncoder.encode("" + timeAllowed, "UTF-8"));
                stringBuilder.append("&");
                stringBuilder.append(URLEncoder.encode("time_taken", "UTF-8") + "=" + URLEncoder.encode("" + timeTaken, "UTF-8"));
                stringBuilder.append("&");
                for (int i = 0; i < quizResults.size() - 1; i = i + 2) {
                    stringBuilder.append(URLEncoder.encode(quizResults.get(i), "UTF-8") + "=" + URLEncoder.encode(quizResults.get(i + 1), "UTF-8"));
                    stringBuilder.append("&");
                }
                // Remove the last &
                if (stringBuilder.length() > 0) {
                    stringBuilder.deleteCharAt(stringBuilder.length() - 1);
                }
                String post_data = "" + stringBuilder;
                bufferedWriter.write(post_data);
                bufferedWriter.flush();
                bufferedWriter.close();
                outputStream.close();
                InputStream inputStream = httpURLConnection.getInputStream();
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream, "iso-8859-1"));
                String result = "";
                String line = "";
                // Build a string from the returned php script message
                while ((line = bufferedReader.readLine()) != null) {
                    result += line;
                }
                bufferedReader.close();
                inputStream.close();
                httpURLConnection.disconnect();
                if (!result.equals("Insert successful")) {
                    user = null;
                }
                return user;
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else if (type.equals("practice")) {
            try {
                URL url = new URL(practice_url);
                HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
                httpURLConnection.setRequestMethod("POST");
                httpURLConnection.setDoOutput(true);
                httpURLConnection.setDoInput(true);
                OutputStream outputStream = httpURLConnection.getOutputStream();
                BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(outputStream, "UTF-8"));
                StringBuilder stringBuilder = new StringBuilder();
                // Add in date to start of string
                String date = new SimpleDateFormat("dd-MM-yyyy").format(new Date());
                stringBuilder.append(URLEncoder.encode("date", "UTF-8") + "=" + URLEncoder.encode(date, "UTF-8"));
                stringBuilder.append("&");
                String username = user.username;
                stringBuilder.append(URLEncoder.encode("username", "UTF-8") + "=" + URLEncoder.encode(username, "UTF-8"));
                stringBuilder.append("&");
                // Get the total number of attempts
                int total = 0;
                for (int i = 0; i < attempts.size(); i++) {
                    total = total + attempts.get(i);
                }
                stringBuilder.append(URLEncoder.encode("attempts", "UTF-8") + "=" + URLEncoder.encode("" + total, "UTF-8"));
                stringBuilder.append("&");
                // Letter information
                stringBuilder.append(URLEncoder.encode("s", "UTF-8") + "=" + URLEncoder.encode("" + attempts.get(0), "UTF-8"));
                stringBuilder.append("&");
                stringBuilder.append(URLEncoder.encode("s_success", "UTF-8") + "=" + URLEncoder.encode("" + success.get(0), "UTF-8"));
                stringBuilder.append("&");
                stringBuilder.append(URLEncoder.encode("a", "UTF-8") + "=" + URLEncoder.encode("" + attempts.get(1), "UTF-8"));
                stringBuilder.append("&");
                stringBuilder.append(URLEncoder.encode("a_success", "UTF-8") + "=" + URLEncoder.encode("" + success.get(1), "UTF-8"));
                stringBuilder.append("&");
                stringBuilder.append(URLEncoder.encode("t", "UTF-8") + "=" + URLEncoder.encode("" + attempts.get(2), "UTF-8"));
                stringBuilder.append("&");
                stringBuilder.append(URLEncoder.encode("t_success", "UTF-8") + "=" + URLEncoder.encode("" + success.get(2), "UTF-8"));
                stringBuilder.append("&");
                stringBuilder.append(URLEncoder.encode("i", "UTF-8") + "=" + URLEncoder.encode("" + attempts.get(3), "UTF-8"));
                stringBuilder.append("&");
                stringBuilder.append(URLEncoder.encode("i_success", "UTF-8") + "=" + URLEncoder.encode("" + success.get(3), "UTF-8"));
                stringBuilder.append("&");
                stringBuilder.append(URLEncoder.encode("p", "UTF-8") + "=" + URLEncoder.encode("" + attempts.get(4), "UTF-8"));
                stringBuilder.append("&");
                stringBuilder.append(URLEncoder.encode("p_success", "UTF-8") + "=" + URLEncoder.encode("" + success.get(4), "UTF-8"));
                stringBuilder.append("&");
                stringBuilder.append(URLEncoder.encode("n", "UTF-8") + "=" + URLEncoder.encode("" + attempts.get(5), "UTF-8"));
                stringBuilder.append("&");
                stringBuilder.append(URLEncoder.encode("n_success", "UTF-8") + "=" + URLEncoder.encode("" + success.get(5), "UTF-8"));

                String post_data = "" + stringBuilder;
                bufferedWriter.write(post_data);
                bufferedWriter.flush();
                bufferedWriter.close();
                outputStream.close();
                InputStream inputStream = httpURLConnection.getInputStream();
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream, "iso-8859-1"));
                String result = "";
                String line = "";
                // Build a string from the returned php script message
                while ((line = bufferedReader.readLine()) != null) {
                    result += line;
                }
                bufferedReader.close();
                inputStream.close();
                httpURLConnection.disconnect();
                if (!result.equals("Insert successful")) {
                    user = null;
                }
                return user;
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    @Override
    protected void onPostExecute(User returnUser) {
        // Call the AsyncResponse to send the user back to another class
        delegate.processFinish(returnUser);
        super.onPostExecute(returnUser);
    }

    @Override
    protected void onProgressUpdate(Void... values) {
        super.onProgressUpdate(values);
    }
}
