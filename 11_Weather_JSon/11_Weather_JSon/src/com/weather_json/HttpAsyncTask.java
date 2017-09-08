package com.weather_json;

import android.app.Activity;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

public class HttpAsyncTask extends AsyncTask<String, Void, String> {
       Activity contextMain;
       public String strResult = "";
       
       public HttpAsyncTask(Activity _context) {
              contextMain = _context;
       }
    @Override
    protected String doInBackground(String... urls) {

        return GetStringFromURL(urls[0]);
    }
   
    // onPostExecute displays the results of the AsyncTask.
    @Override
    protected void onPostExecute(String result) {
        Toast.makeText(contextMain.getBaseContext(), "Received!", Toast.LENGTH_LONG).show();
        strResult = result;
        ((MainActivity) contextMain ).Update();
   }
   
   public static String GetStringFromURL(String url){
        InputStream inputStream = null;
        String result = "";
        try {
            // create HttpClient
            HttpClient httpclient = new DefaultHttpClient();       
            // make GET request to the given URL
            HttpResponse httpResponse = httpclient.execute(new HttpGet(url));    
            // receive response as inputStream
            inputStream = httpResponse.getEntity().getContent();   
            // convert inputstream to string
            if(inputStream != null) {
                 result = convertInputStreamToString(inputStream);
            } else {
                 result = "Did not work!";
            }
         } catch (Exception e) {
             Log.d("InputStream", e.getLocalizedMessage());
         }
         
       return result;
   }
      
   private static String convertInputStreamToString(InputStream inputStream){
        BufferedReader bufferedReader = 
                 new BufferedReader( new InputStreamReader(inputStream));
        String line = "", result = "";
       
        try {
             while((line = bufferedReader.readLine()) != null) {
                   result += line;
             }
                    
            inputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }       
        return result;
    }
}