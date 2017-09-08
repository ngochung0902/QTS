package com.foodapp.lien.foodapp;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.facebook.AccessToken;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import parsing.WebFunctions;
import utility.ConnectionDetector;
import utility.Constant;
import utility.Generalfunction;
import utility.GlobalVar;

public class SplashActivity extends AppCompatActivity {

    protected int SPLASH_TIME_OUT = 2500;

    Context context;

    ConnectionDetector cdObj;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        context = this;

        SharedPreferences prefs = getSharedPreferences("MY_PREFS", MODE_PRIVATE);
        String fbToken = prefs.getString("token", "");

        cdObj=new ConnectionDetector(context);

        if (!fbToken.equals("")) {
            new Handler().postDelayed(new Runnable() {

                @Override
                public void run() {
                    String strloginpwd=GlobalVar.getMyStringPref(context, Constant.loginUserpwd);
                    if(Generalfunction.isEmptyCheck(strloginpwd)){
                        Intent i = new Intent(SplashActivity.this, MainActivity.class);
                        startActivity(i);
                        finish();
                    }
                    else{
                        if (cdObj.isConnectingToInternet()) {
                            new logINUser().execute(GlobalVar.getMyStringPref(context, Constant.loginUserEmail), strloginpwd);
                        }
                        else{
                            Generalfunction.Simple1ButtonDialogClick(context.getResources().getString(R.string.Internet_Message), context);
                        }

                    }
                }
            }, SPLASH_TIME_OUT);

        } else {
            new Handler().postDelayed(new Runnable() {

                @Override
                public void run() {
                    Intent i = new Intent(SplashActivity.this, MainActivity.class);
                    startActivity(i);
                    finish();
                }
            }, SPLASH_TIME_OUT);
        }
    }


    // Asynctask for login account with simple account
    //Check and take  it access token
    public class logINUser extends AsyncTask<String, Void, String> {

        ProgressDialog progress;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            progress = new ProgressDialog(context);
            progress.setMessage("Please wait...");
            progress.setCancelable(false);
            progress.show();
        }

        @Override
        protected String doInBackground(String... params) {

            // call webfunction
            return WebFunctions.logInUser(params[0], params[1]);
        }

        @Override
        protected void onPostExecute(String aVoid) {
            super.onPostExecute(aVoid);

            progress.dismiss();

            if (!aVoid.equals("")) {
                JSONObject jsonObject;
                String response = "";
                try {
                    jsonObject = new JSONObject(aVoid);

                    // login is successfully done
                    if (jsonObject.has("authentication_token")) {
                        response = jsonObject.getString("authentication_token");
                        Log.d("response", "onPost responce which save in preference Execute: " + response);
                        SharedPreferences.Editor editor = context.getSharedPreferences("MY_PREFS", context.MODE_PRIVATE).edit();
                        editor.putString("token", response);
                        editor.commit();

                        Intent intent = new Intent(context, NavigationalSearchActivity.class);
                        startActivity(intent);
                        finish();

                    } else {

                        // show message
                        if(aVoid.contains("errors")){
                            JSONArray st = jsonObject.getJSONArray("errors");
                            for(int i=0;i<st.length();i++)
                            {
                                if (i == 0) {
                                    response = st.getString(i) + "\n" + response;
                                } else {
                                    response = st.getString(i) + "\n" + response;
                                }
                            }
                        }
                        else {
                            response = jsonObject.getString("message");
                        }
                        Intent i = new Intent(SplashActivity.this, MainActivity.class);
                        startActivity(i);
                        finish();

                    }
                } catch (JSONException e) {
                    // TODO Auto-generated catch block
                    //{"errors":["Username is invalid"]}
                    e.printStackTrace();
                }
            }
        }
    }


}
