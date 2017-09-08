package com.peppa.app.Fragment;

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
import android.view.WindowManager;
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
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.appevents.AppEventsLogger;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Arrays;

import com.peppa.app.Activity.NavigationalSearchActivity;
import com.peppa.app.R;
import com.peppa.app.parsing.WebFunctions;
import com.peppa.app.utility.ConnectionDetector;
import com.peppa.app.utility.Generalfunction;
import com.peppa.app.utility.Constant;
import com.peppa.app.utility.GlobalVar;

public class LoginFragment extends Fragment {

    private Context context;
    private ConnectionDetector cdObj;

    private EditText userED, passwordED;
    private ScrollView accountScroll;
    private Button loginBTN;

    private CallbackManager callbackManager;
    private LoginButton loginButton;


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        context = this.getActivity();

        //Facebook initialization
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

        //Mapping Edittext
        userED = (EditText) v.findViewById(R.id.mailED);
        passwordED = (EditText) v.findViewById(R.id.passwordED);
        accountScroll = (ScrollView) v.findViewById(R.id.accoutScrollView);


        //Mapping Button
        loginBTN = (Button) v.findViewById(R.id.loginBTN);

        // loadfont
        Generalfunction.loadFont(getActivity(), emailTV, "lato_bold.ttf");
        Generalfunction.loadFont(getActivity(), passwordTV, "lato_bold.ttf");

        //Mapping facebook login button
        loginButton = (LoginButton) v.findViewById(R.id.fbButton);
        loginButton.setBackgroundResource(R.drawable.pink_round_button);

        //Set facebook permission
        loginButton.setReadPermissions(Arrays.asList("public_profile", "email"));
        loginButton.setFragment(this);

        loginButton.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(final LoginResult loginResult) {

                if (loginResult.getAccessToken() != null) {

                    GraphRequest request = GraphRequest.newMeRequest(loginResult.getAccessToken(),
                            new GraphRequest.GraphJSONObjectCallback() {
                                @Override
                                public void onCompleted(JSONObject object, GraphResponse response) {

                                    JSONObject jsonObject;
                                    try {
                                        jsonObject = new JSONObject(object.toString());

                                        //Register account with facebook
                                        new loginProviderUser().execute(jsonObject.get("first_name").toString(),
                                                jsonObject.get("last_name").toString(), jsonObject.get("email").toString(), loginResult.getAccessToken().getUserId());

                                    } catch (JSONException e) {
                                        // TODO Auto-generated catch block
                                        e.printStackTrace();
                                        //Handle exception
                                        Generalfunction.Simple1ButtonDialog(getActivity().getResources().getString(R.string.facebook_mobile), getActivity());
                                        onStart();
                                    }
                                }
                            });
                    Bundle parameters = new Bundle();
                    parameters.putString("fields", "id,first_name,last_name,email");
                    request.setParameters(parameters);
                    request.executeAsync();

                }
            }

            @Override
            public void onCancel() {
                Toast.makeText(context, "Login Canceled", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onError(FacebookException e) {
                Generalfunction.Simple1ButtonDialog("hhh" + e.toString(), getActivity());
                //Log.e("Error", e.toString());
            }
        });

        loginBTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (cdObj.isConnectingToInternet()) {
                    // login with simple
                    new logINUser().execute(userED.getText().toString(), passwordED.getText().toString());
                } else {
                    Generalfunction.Simple1ButtonDialog(context.getResources().getString(R.string.Internet_Message), context);
                }
            }
        });

        //Set - Keboard action item listener
        passwordED.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    Generalfunction.hideKeyboard(getActivity());
                    focusOnView();
                    return true;
                }
                return false;
            }
        });

        //Set - Reset password
        resetPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showPopUp();
            }
        });

        return v;
    }


    @Override
    public void onStart() {
        super.onStart();
        //logout facebook
        try {
            LoginManager.getInstance().logOut();
        } catch (Exception e) {
        }
    }


    //Display - Pop up for reset password
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

                    Snackbar snackbar1 = Snackbar.make(accountScroll, "Email address isn't a valid one!", Snackbar.LENGTH_SHORT);
                    snackbar1.show();

                } else if (!Generalfunction.isEmailValid(editEmailAdd.getText().toString())) {

                    Snackbar snackbar1 = Snackbar.make(accountScroll, "Email address isn't a valid one!", Snackbar.LENGTH_SHORT);
                    snackbar1.show();

                } else {
                    alertDialog.dismiss();

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


    //Hide keyboard
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


    //Call - Login with facebook API
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
            return WebFunctions.registerUserProvider(params[0], params[1], params[2], params[3], context);
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

                    GlobalVar.setMyBooleanPref(context, Constant.Login_facebook, true);

                    //Start - Navigation drawer home activity
                    Intent intent = new Intent(context, NavigationalSearchActivity.class);
                    startActivity(intent);
                    getActivity().finish();
                } else {

                    StringBuilder sb = new StringBuilder();
                    try {
                        response = jsonObject.getString("messages");
                        if (s.contains("errors")) {
                            JSONObject jerror = jsonObject.getJSONObject("errors");
                            JSONArray jbaseArray = jerror.getJSONArray("base");

                            for (int i = 0; i < jbaseArray.length(); i++) {

                                sb.append("\n" + jbaseArray.get(i));

                            }
                        }
                    } catch (Exception e) {
                    }

                    //Display proper error message
                    Generalfunction.Simple1ButtonDialog(response + " \n" + sb.toString(), getActivity());

                    //facebook logout
                    try {
                        LoginManager.getInstance().logOut();
                    } catch (Exception e) {
                    }

                }


            } catch (JSONException e) {
                //e.printStackTrace();
                Generalfunction.Simple1ButtonDialog(getResources().getString(R.string.Facebook_Without_Email), context);

                //facebook logout
                try {
                    LoginManager.getInstance().logOut();
                } catch (Exception e1) {
                }
            }
        }
    }


    //Call - Login with simple account API
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

                        GlobalVar.setMyStringPref(context, Constant.loginUserpwd, passwordED.getText().toString());
                        GlobalVar.setMyStringPref(context, Constant.loginUserEmail, userED.getText().toString());
                        GlobalVar.setMyBooleanPref(context, Constant.Login_facebook, false);

                        //Start - Navigation drawer home activity
                        Intent intent = new Intent(context, NavigationalSearchActivity.class);
                        startActivity(intent);
                        getActivity().finish();

                    } else {

                        // show message
                        if (aVoid.contains("errors")) {
                            JSONArray st = jsonObject.getJSONArray("errors");
                            for (int i = 0; i < st.length(); i++) {
                                if (i == 0) {
                                    response = st.getString(i) + "\n" + response;
                                } else {
                                    response = st.getString(i) + "\n" + response;
                                }
                            }
                        } else {
                            response = jsonObject.getString("message");
                        }
                        Generalfunction.Simple1ButtonDialog(response, getActivity());

                    }
                } catch (JSONException e) {
                    // TODO Auto-generated catch block
                    Generalfunction.Simple1ButtonDialog(getResources().getString(R.string.API_Exception), getActivity());
                }
            }
        }
    }


    //Call - Reset password/forgot password API
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

            progress.dismiss();
            Generalfunction.Simple1ButtonDialog(aVoid, context);
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
