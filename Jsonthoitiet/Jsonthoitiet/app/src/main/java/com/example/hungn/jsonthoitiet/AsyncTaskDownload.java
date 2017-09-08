package com.example.hungn.jsonthoitiet;

import android.os.AsyncTask;
import android.util.Log;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;

import javax.net.ssl.HttpsURLConnection;

/**
 * Created by hungn on 05/29/17.
 */

public class AsyncTaskDownload extends AsyncTask<String, Void, String>{

    private Downloadinterface downloadinterface = null;
    public void SetLister (Downloadinterface _downloadinterface)
    {
        downloadinterface = _downloadinterface;
    }

    @Override
    protected String doInBackground(String... jsonUrl) {
        String dataUrl = jsonUrl[0]; InputStream inputStream = null; String strResult = "";
        try {
            URL url = new URL(dataUrl);
            HttpsURLConnection connection = (HttpsURLConnection) url.openConnection();
            inputStream = new BufferedInputStream(connection.getInputStream());
            strResult = convertInputStreamToString(inputStream);//;string return json
        }catch (Exception e) {
            Log.i("Demo download json", "Error download json");
            e.printStackTrace();
        }
        Log.i("Weather","Json:"+ strResult);
        return strResult;
    }
    @Override
    protected void onPostExecute(String strResult) {
        super.onPostExecute(strResult);
        if(downloadinterface != null) {
            downloadinterface.OnFinisheDownload(strResult);
        }
    }
    private String convertInputStreamToString(InputStream inputStream) {
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
        String line="", result = "";
        try {
            while ( (line = bufferedReader.readLine() ) != null) {
                result += line;
            }
        }catch (Exception e) {
            Log.i("Weather", "Error!");
            e.printStackTrace();
        }
        return result;
    }
}
