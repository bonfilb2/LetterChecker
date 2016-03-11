package com.example.brian.letterchecker;

import android.app.AlertDialog;
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
import java.lang.reflect.Array;
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
public class BackgroundWorker extends AsyncTask<Void,Void,User> {
    Context context;
    //AlertDialog alertDialog;
    User user;
    String type;
    ArrayList<String> quizResults;

    ProgressDialog progressDialog;

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
    // Second constructor for an array of letters, no progress dialog
    public BackgroundWorker (String type, User user, Context context, ArrayList<String> quizResults) {
        this.type = type;
        this.user = user;
        this.context = context;
        this.quizResults = quizResults;
    }

    @Override
    protected User doInBackground(Void... params) {
        String login_url = "http://student.computing.dcu.ie/~bonfilb2/login.php";
        String register_url = "http://student.computing.dcu.ie/~bonfilb2/register.php";
        String quiz_url = "http://student.computing.dcu.ie/~bonfilb2/quiz.php";
        if(type.equals("login")) {
            try {
                String user_name = user.username;
                String password = user.password;
                URL url = new URL(login_url);
                HttpURLConnection httpURLConnection = (HttpURLConnection)url.openConnection();
                httpURLConnection.setRequestMethod("POST");
                httpURLConnection.setDoOutput(true);
                httpURLConnection.setDoInput(true);
                OutputStream outputStream = httpURLConnection.getOutputStream();
                BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(outputStream, "UTF-8"));
                String post_data = URLEncoder.encode("user_name","UTF-8")+"="+URLEncoder.encode(user_name,"UTF-8")+"&"
                        +URLEncoder.encode("password","UTF-8")+"="+URLEncoder.encode(password,"UTF-8");
                bufferedWriter.write(post_data);
                bufferedWriter.flush();
                bufferedWriter.close();
                outputStream.close();
                InputStream inputStream = httpURLConnection.getInputStream();
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream,"iso-8859-1"));
                String result="";
                String line="";
                // Build a string from the returned php script message
                while((line = bufferedReader.readLine())!= null) {
                    result += line;
                }
                bufferedReader.close();
                inputStream.close();
                httpURLConnection.disconnect();
                // Check if php script did not return this message, if it didn't login wasn't a success
                if(!result.equals("Login successful")) {
                    // Set user to null to indicate a failed login
                    user = null;
                }
                return user;
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else if(type.equals("register")) {
            try {
                String name = user.name;
                String surname = user.surname;
                String sclass = user.sclass;
                String username = user.username;
                String password = user.password;
                URL url = new URL(register_url);
                HttpURLConnection httpURLConnection = (HttpURLConnection)url.openConnection();
                httpURLConnection.setRequestMethod("POST");
                httpURLConnection.setDoOutput(true);
                httpURLConnection.setDoInput(true);
                OutputStream outputStream = httpURLConnection.getOutputStream();
                BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(outputStream, "UTF-8"));
                String post_data = URLEncoder.encode("name","UTF-8")+"="+URLEncoder.encode(name,"UTF-8")+"&"
                        + URLEncoder.encode("surname","UTF-8")+"="+URLEncoder.encode(surname,"UTF-8")+"&"
                        + URLEncoder.encode("class","UTF-8")+"="+URLEncoder.encode(sclass,"UTF-8")+"&"
                        + URLEncoder.encode("username","UTF-8")+"="+URLEncoder.encode(username,"UTF-8")+"&"
                        + URLEncoder.encode("password","UTF-8")+"="+URLEncoder.encode(password,"UTF-8");
                bufferedWriter.write(post_data);
                bufferedWriter.flush();
                bufferedWriter.close();
                outputStream.close();
                InputStream inputStream = httpURLConnection.getInputStream();
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream,"iso-8859-1"));
                String result="";
                String line="";
                // Build a string from the returned php script message
                while((line = bufferedReader.readLine())!= null) {
                    result += line;
                }
                bufferedReader.close();
                inputStream.close();
                httpURLConnection.disconnect();
                // Check if php script did not return this message, if it didn't register wasn't a success
                System.out.println(result);
                if(!result.equals("Insert successful")) {
                    user = null;
                }
                return user;
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else if(type.equals("quiz")) {
            try {
                URL url = new URL(quiz_url);
                HttpURLConnection httpURLConnection = (HttpURLConnection)url.openConnection();
                httpURLConnection.setRequestMethod("POST");
                httpURLConnection.setDoOutput(true);
                httpURLConnection.setDoInput(true);
                OutputStream outputStream = httpURLConnection.getOutputStream();
                BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(outputStream, "UTF-8"));
                StringBuilder stringBuilder = new StringBuilder();
                // Add in date to start of string
                String date = new SimpleDateFormat("dd-MM-yyyy").format(new Date());
                stringBuilder.append(URLEncoder.encode("date", "UTF-8")+"="+URLEncoder.encode(date,"UTF-8"));
                String username = user.username;
                stringBuilder.append(URLEncoder.encode("username", "UTF-8")+"="+URLEncoder.encode(username,"UTF-8"));
                for(int i = 0; i < quizResults.size()-1; i = i+2)
                {
                    stringBuilder.append(URLEncoder.encode(""+quizResults.get(i),"UTF-8")+"="+URLEncoder.encode(quizResults.get(i+1),"UTF-8"));
                    System.out.println("letter: " + quizResults.get(i));
                    System.out.println("attempt: " + quizResults.get(i+1));
                    stringBuilder.append("&");
                }
                // Remove the last &
                if (stringBuilder.length() > 0)
                {
                    stringBuilder.deleteCharAt(stringBuilder.length()-1);
                }
                String post_data = ""+stringBuilder;
                bufferedWriter.write(post_data);
                bufferedWriter.flush();
                bufferedWriter.close();
                outputStream.close();
                InputStream inputStream = httpURLConnection.getInputStream();
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream,"iso-8859-1"));
                String result="";
                String line="";
                // Build a string from the returned php script message
                while((line = bufferedReader.readLine())!= null) {
                    result += line;
                }
                bufferedReader.close();
                inputStream.close();
                httpURLConnection.disconnect();
                //System.out.println(result);
                if(!result.equals("Insert successful")) {
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
    protected void onPreExecute() {
        //alertDialog = new AlertDialog.Builder(context).create();
        //alertDialog.setTitle("Login Status");
    }

    @Override
    protected void onPostExecute(User returnUser) {
        // Call the AsyncResponse to send the user back to another class
        delegate.processFinish(returnUser);
        super.onPostExecute(returnUser);
        //progressDialog.dismiss();
    }

    @Override
    protected void onProgressUpdate(Void... values) {
        super.onProgressUpdate(values);
    }
}
