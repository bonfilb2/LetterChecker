package com.example.brian.letterchecker;

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

/**
 * Created by Brian on 15/03/16.
 */

/**
 * Portions of this page dealing with http connections was taken from Programming Knowledge blog site, I take no credit for its design.
 * programmingknowledgeblog.blogspot.ie
 */

public class BGWorkerGet extends AsyncTask<Void,Void,String> {

    Context context;
    String date;


    // Create a new ASResponseGet
    public ASResponseGet delegate = null;

    public BGWorkerGet (String date, Context context) {
        // Constructor takes in the current date and a context
        this.date = date;
        this.context = context;
    }

    @Override
    protected String doInBackground(Void... params) {
        String attempt_url = "http://student.computing.dcu.ie/~bonfilb2/attempt.php";
        try {
            URL url = new URL(attempt_url);
            HttpURLConnection httpURLConnection = (HttpURLConnection)url.openConnection();
            httpURLConnection.setRequestMethod("POST");
            httpURLConnection.setDoOutput(true);
            httpURLConnection.setDoInput(true);
            OutputStream outputStream = httpURLConnection.getOutputStream();
            BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(outputStream, "UTF-8"));
            String post_data = URLEncoder.encode("date", "UTF-8")+"="+URLEncoder.encode(date,"UTF-8");
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

            return result;
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }

    @Override
    protected void onPostExecute(String attempt) {
        // Call the AsyncResponse to send the user back to another class
        delegate.processFinish(attempt);
        super.onPostExecute(attempt);
    }

    @Override
    protected void onProgressUpdate(Void... values) {
        super.onProgressUpdate(values);
    }
}
