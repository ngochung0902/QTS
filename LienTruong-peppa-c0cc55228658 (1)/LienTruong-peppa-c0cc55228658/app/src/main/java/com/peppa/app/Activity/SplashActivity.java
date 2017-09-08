package com.peppa.app.Activity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import me.wangyuwei.loadingview.LoadingView;

import com.peppa.app.R;
import com.peppa.app.parsing.WebFunctions;
import com.peppa.app.utility.ConnectionDetector;
import com.peppa.app.utility.Constant;
import com.peppa.app.utility.Generalfunction;
import com.peppa.app.utility.GlobalVar;

public class SplashActivity extends AppCompatActivity {

    protected int SPLASH_TIME_OUT = 3000;

    Context thisContext;

    ConnectionDetector cdObj;

    private LoadingView mLoadingView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        thisContext = this;

        TextView tvappname = (TextView)findViewById(R.id.tvappname);
        mLoadingView       = (LoadingView) this.findViewById(R.id.loading_view);
        if (mLoadingView != null) {
            mLoadingView.start();
        }

        Generalfunction.loadFont(thisContext, tvappname, "lato_medium.ttf");

        SharedPreferences prefs = getSharedPreferences("MY_PREFS", MODE_PRIVATE);
        String fbToken = prefs.getString("token", "");

        cdObj=new ConnectionDetector(thisContext);

        if (!fbToken.equals("")) {
            new Handler().postDelayed(new Runnable() {

                @Override
                public void run() {

                    if(GlobalVar.getMyBooleanPref(thisContext,Constant.Login_facebook)){
                        String str=GlobalVar.getMyStringPref(thisContext,Constant.Login_facebook_uid);
                        if(!Generalfunction.isEmptyCheck(Generalfunction.Isnull(str))){
                            new loginProviderUser().execute(str);
                        }
                        else{
                            NextActivity();
                        }
                    }
                    else {
                        String strloginpwd = GlobalVar.getMyStringPref(thisContext, Constant.loginUserpwd);
                        if (Generalfunction.isEmptyCheck(strloginpwd)) {
                            NextActivity();
                        } else {
                            if (cdObj.isConnectingToInternet()) {
                                new logINUser().execute(GlobalVar.getMyStringPref(thisContext, Constant.loginUserEmail), strloginpwd);
                            } else {
                                Generalfunction.Simple1ButtonDialogClick(thisContext.getResources().getString(R.string.Internet_Message), thisContext);
                            }

                        }
                    }
                }
            }, SPLASH_TIME_OUT);

        } else {
            new Handler().postDelayed(new Runnable() {

                @Override
                public void run() {
                    NextActivity();
                }
            }, SPLASH_TIME_OUT);
        }
    }




    // Asynctask for login account with simple account
    //Check and take  it access token
    public class logINUser extends AsyncTask<String, Void, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(String... params) {

            // call webfunction
            return WebFunctions.logInUser(params[0], params[1]);
        }

        @Override
        protected void onPostExecute(String aVoid) {
            super.onPostExecute(aVoid);


            if (!aVoid.equals("")) {
                JSONObject jsonObject;
                String response = "";
                try {
                    jsonObject = new JSONObject(aVoid);

                    // login is successfully done
                    if (jsonObject.has("authentication_token")) {
                        response = jsonObject.getString("authentication_token");
                        Log.d("response", "onPost responce which save in preference Execute: " + response);
                        SharedPreferences.Editor editor = thisContext.getSharedPreferences("MY_PREFS", thisContext.MODE_PRIVATE).edit();
                        editor.putString("token", response);
                        editor.commit();

                        //Start - Navigation drawer home activity
                        Intent intent = new Intent(thisContext, NavigationalSearchActivity.class);
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
                        NextActivity();
                    }
                } catch (JSONException e) {
                    // TODO Auto-generated catch block
                    //{"errors":["Username is invalid"]}
                    e.printStackTrace();
                    Generalfunction.Simple1ButtonDialogClick(getResources().getString(R.string.API_Exception),SplashActivity.this);

                }
            }
        }
    }


    // Asynctask for login account with facebook account
    public class loginProviderUser extends AsyncTask<String, Void, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

        }

        @Override
        protected String doInBackground(String... params) {
            //call webfunction
            return WebFunctions.logInUserFacebook(params[0]);
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);

            JSONObject jsonObject;
            String response = "";
            try {
                jsonObject = new JSONObject(s);

                //{"success":"Yes","message":"You have succesfully signed in.","authentication_token":"5876047fc1881aaf6a407ba98424386d"}

                String success=jsonObject.getString("success");
                boolean flag=false;

                if(Generalfunction.isCompare(success,"yes")){
                    // login is successfully done
                    if (jsonObject.has("authentication_token")) {
                        response = jsonObject.getString("authentication_token");
                        SharedPreferences.Editor editor = thisContext.getSharedPreferences("MY_PREFS", thisContext.MODE_PRIVATE).edit();
                        editor.putString("token", response);
                        editor.commit();

                        //Start - Navigation drawer home activity
                        Intent intent = new Intent(thisContext, NavigationalSearchActivity.class);
                        startActivity(intent);
                        finish();
                    }
                    else{
                        flag=true;
                    }
                }
                else{
                    flag=true;
                }

                if(flag){
                    NextActivity();
                }

            } catch (JSONException e) {

            }
        }
    }

    private void NextActivity(){

        //Start - Create account/Login activity
        Intent i = new Intent(SplashActivity.this, MainActivity.class);
        startActivity(i);
        finish();
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        mLoadingView.stop();
    }

}
