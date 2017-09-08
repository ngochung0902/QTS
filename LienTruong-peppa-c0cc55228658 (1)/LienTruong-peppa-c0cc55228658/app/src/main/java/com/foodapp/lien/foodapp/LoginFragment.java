package com.foodapp.lien.foodapp;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.appevents.AppEventsLogger;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Arrays;

import parsing.WebFunctions;
import utility.ConnectionDetector;
import utility.Generalfunction;
import utility.Constant;
import utility.GlobalVar;

public class LoginFragment extends Fragment {

    private Context context;

    private CallbackManager callbackManager;
    private LoginButton loginButton;

    private EditText userED, passwordED;
    private ScrollView accountScroll;
    private Button loginBTN;

    private ConnectionDetector cdObj;


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        context = this.getActivity();

        // Facebook initialization
        FacebookSdk.sdkInitialize(context.getApplicationContext());
        callbackManager = CallbackManager.Factory.create();

        //connection detector initialization
        cdObj = new ConnectionDetector(context);
    }



    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable final ViewGroup container, @Nullable Bundle savedInstanceState) {

        //inflate view
        View v = inflater.inflate(R.layout.activity_login, container, false);

        // Mapping Textview
        TextView emailTV = (TextView) v.findViewById(R.id.emailTV);
        TextView passwordTV = (TextView) v.findViewById(R.id.passwordTV);
        final TextView resetPassword = (TextView) v.findViewById(R.id.resetPassword);

        //Mapping edittext
        userED = (EditText) v.findViewById(R.id.mailED);
        passwordED = (EditText) v.findViewById(R.id.passwordED);
        accountScroll = (ScrollView) v.findViewById(R.id.accoutScrollView);

        // loadfont
        Constant.loadFont(getActivity(), emailTV, "lato_bold.ttf");
        Constant.loadFont(getActivity(), passwordTV, "lato_bold.ttf");

        //Mapping facebook login button
        loginButton = (LoginButton) v.findViewById(R.id.fbButton);
        loginButton.setBackgroundResource(R.drawable.pink_round_button);

        // set facebook permission
        loginButton.setReadPermissions(Arrays.asList("public_profile", "email"));
        loginButton.setFragment(this);

        loginButton.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {

                if (loginResult.getAccessToken() != null) {

                    // login with facebook account
                    new loginProviderUser().execute(loginResult.getAccessToken().getUserId());
                }
            }

            @Override
            public void onCancel() {
                Toast.makeText(context, "Login Canceled", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onError(FacebookException e) {
                Generalfunction.Simple1ButtonDialog(e.toString(), getActivity());
                //Log.e("Error", e.toString());
            }
        });

        //Mapping Button
        loginBTN = (Button) v.findViewById(R.id.loginBTN);

        loginBTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                if (!Generalfunction.isEmptyCheck(userED.getText().toString())) {
                    if (passwordED.getText().toString().length() > 5 && Generalfunction.isValidUserID(passwordED.getText().toString())) {

                        // Check internet connection and perform operation
                        if (cdObj.isConnectingToInternet()) {
                            // login with simple
                            new logINUser().execute(userED.getText().toString(), passwordED.getText().toString());
                        } else {
                            Generalfunction.Simple1ButtonDialog(context.getResources().getString(R.string.Internet_Message), context);
                        }
                    } else {
                        Snackbar snackbar1 = Snackbar.make(accountScroll, "Password should be at least 6 characters", Snackbar.LENGTH_LONG);
                        snackbar1.show();
                    }

                } else {
                    Snackbar snackbar1 = Snackbar.make(accountScroll, "The email/username you've entered is not valid", Snackbar.LENGTH_LONG);
                    snackbar1.show();
                }

            }
        });

        // Keboard action item listener
        passwordED.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    focusOnView();
                    return true;
                }
                return false;
            }
        });

        // reset password
        resetPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showPopUp();
            }
        });

        return v;
    }



    // Pop up for reset password
    public void showPopUp() {

        LayoutInflater li = LayoutInflater.from(context);
        View promptsView = li.inflate(R.layout.reset_password_popup, null);

        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);

        final AlertDialog alertDialog = alertDialogBuilder.create();

        alertDialog.setView(promptsView);

        final EditText editEmailAdd = (EditText) promptsView.findViewById(R.id.editEmailAdd);
        Button resetButton = (Button) promptsView.findViewById(R.id.resetButton);

        resetButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                if (Generalfunction.isEmptyCheck(editEmailAdd.getText().toString())) {
                    //Toast.makeText(context,"Please enter email address",Toast.LENGTH_SHORT).show();
                    Snackbar snackbar1 = Snackbar.make(accountScroll, "Email address isn't a valid one!", Snackbar.LENGTH_SHORT);
                    snackbar1.show();
                } else if (!Generalfunction.isEmailValid(editEmailAdd.getText().toString())) {
                    Snackbar snackbar1 = Snackbar.make(accountScroll, "Email address isn't a valid one!", Snackbar.LENGTH_SHORT);
                    snackbar1.show();
                } else {
                    alertDialog.dismiss();
                    // Reset password
                    // Check internet connection and perform operation
                    if (cdObj.isConnectingToInternet()) {
                        new resetPassword().execute(editEmailAdd.getText().toString());
                    } else {
                        Generalfunction.Simple1ButtonDialog(context.getResources().getString(R.string.Internet_Message), context);
                    }
                }

            }
        });

        // show popup (dialog)
        alertDialog.show();
    }



    // hide keyboard
    private final void focusOnView() {
        View view = getActivity().getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
        new Handler().post(new Runnable() {
            @Override
            public void run() {
                accountScroll.fullScroll(View.FOCUS_DOWN);
            }
        });
    }



    // Asynctask for login account with facebook account
    public class loginProviderUser extends AsyncTask<String, Void, String> {

        ProgressDialog progress;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            progress = new ProgressDialog(getActivity());
            progress.setMessage("Signing in....");
            progress.setCancelable(false);
            progress.show();
        }

        @Override
        protected String doInBackground(String... params) {
            //call webfunction
            return WebFunctions.logInUserFacebook(params[0]);
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);

            progress.dismiss();

            JSONObject jsonObject;
            String response = "";
            try {
                jsonObject = new JSONObject(s);

                // login is successfully done
                if (jsonObject.has("authentication_token")) {
                    response = jsonObject.getString("authentication_token");
                    SharedPreferences.Editor editor = context.getSharedPreferences("MY_PREFS", context.MODE_PRIVATE).edit();
                    editor.putString("token", response);
                    editor.commit();

                    Intent intent = new Intent(context, NavigationalSearchActivity.class);
                    startActivity(intent);
                    getActivity().finish();

                } else {

                    response = jsonObject.getString("messages");
                    Log.d("login", "onPostExecute: "+response);

                    Generalfunction.Simple1ButtonDialog(response, getActivity());
                }
            } catch (JSONException e) {
                // TODO Auto-generated catch block
                //{"errors":["Username is invalid"]}
                e.printStackTrace();
            }
        }
    }



    // Asynctask for login account with simple account
    public class logINUser extends AsyncTask<String, Void, String> {

        ProgressDialog progress;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            progress = new ProgressDialog(getActivity());
            progress.setMessage("Signing In....");
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

                        GlobalVar.setMyStringPref(context,Constant.loginUserpwd,passwordED.getText().toString());
                        GlobalVar.setMyStringPref(context,Constant.loginUserEmail,userED.getText().toString());

                        Intent intent = new Intent(context, NavigationalSearchActivity.class);
                        startActivity(intent);
                        getActivity().finish();

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
                            //Toast.makeText(getActivity(), response, Toast.LENGTH_SHORT).show();
                        }
                        else {
                            response = jsonObject.getString("message");
                        }
                        Generalfunction.Simple1ButtonDialog(response, getActivity());

                    }
                } catch (JSONException e) {
                    // TODO Auto-generated catch block
                    //{"errors":["Username is invalid"]}
                    e.printStackTrace();
                }
            }
        }
    }



    // Asynctask for reset password
    public class resetPassword extends AsyncTask<String, Void, String> {

        ProgressDialog progress;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            progress = new ProgressDialog(getActivity());
            progress.setMessage("Requesting....");
            progress.setCancelable(false);
            progress.show();
        }

        @Override
        protected String doInBackground(String... params) {

            //call webfunction
            return WebFunctions.resetPassword(params[0]);
        }

        @Override
        protected void onPostExecute(String aVoid) {
            super.onPostExecute(aVoid);

            Log.d("resetpwd", "onPostExecute: " + aVoid);
            progress.dismiss();
            Generalfunction.Simple1ButtonDialog(aVoid, context);
            //Toast.makeText(getActivity(), aVoid, Toast.LENGTH_SHORT).show();
        }
    }



    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        callbackManager.onActivityResult(requestCode, resultCode, data);
        Log.e("data", data.toString());
    }



    @Override
    public void onResume() {
        super.onResume();

        // Logs 'install' and 'app activate' App Events.
        AppEventsLogger.activateApp(context);
    }



    @Override
    public void onPause() {
        super.onPause();

        // Logs 'app deactivate' App Event.
        AppEventsLogger.deactivateApp(context);
    }


}
