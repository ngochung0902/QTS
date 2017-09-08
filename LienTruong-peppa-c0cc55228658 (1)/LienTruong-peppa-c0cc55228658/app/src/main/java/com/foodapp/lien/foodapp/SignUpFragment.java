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
import android.text.InputFilter;
import android.text.Spanned;
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
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.appevents.AppEventsLogger;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Arrays;

import parsing.WebFunctions;
import utility.ConnectionDetector;
import utility.Constant;
import utility.Generalfunction;
import utility.GlobalVar;

public class SignUpFragment extends Fragment {

    Context context;
    private CallbackManager callbackManager;
    private LoginButton loginButton;
    EditText firstName, userID, lastName, email, password;
    ScrollView accountScroll;
    Button createBTN;

    ConnectionDetector cdObj;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        context = this.getActivity();

        // Facebook initialization
        FacebookSdk.sdkInitialize(context.getApplicationContext());
        callbackManager = CallbackManager.Factory.create();
        cdObj = new ConnectionDetector(context);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        //inflate view
        View v = inflater.inflate(R.layout.create_account, container, false);

        //Mapping Textview
        TextView fnameTV = (TextView) v.findViewById(R.id.fnameTV);
        TextView lnameTV = (TextView) v.findViewById(R.id.lNameTV);
        TextView emailTV = (TextView) v.findViewById(R.id.emailTV);
        TextView usernameTV = (TextView) v.findViewById(R.id.uNameTV);
        TextView passwordTV = (TextView) v.findViewById(R.id.passwordTV);

        // load font
        Constant.loadFont(getActivity(), fnameTV, "lato_bold.ttf");
        Constant.loadFont(getActivity(), lnameTV, "lato_bold.ttf");
        Constant.loadFont(getActivity(), emailTV, "lato_bold.ttf");
        Constant.loadFont(getActivity(), usernameTV, "lato_bold.ttf");
        Constant.loadFont(getActivity(), passwordTV, "lato_bold.ttf");

        //Mapping facebook login button
        loginButton = (LoginButton) v.findViewById(R.id.fbAccountButton);
        loginButton.setBackgroundResource(R.drawable.pink_round_button);

        // set facebook permission
        loginButton.setReadPermissions(Arrays.asList("public_profile", "email"));
        loginButton.setFragment(this);

        // login with facebook account
        loginButton.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {

            @Override
            public void onSuccess(LoginResult loginResult) {

                if (loginResult.getAccessToken() != null) {
                    //Log.d("Token", loginResult.getAccessToken().getUserId());
                    GraphRequest request = GraphRequest.newMeRequest(loginResult.getAccessToken(),
                            new GraphRequest.GraphJSONObjectCallback() {
                                @Override
                                public void onCompleted(JSONObject object, GraphResponse response) {

                                    JSONObject jsonObject;
                                    try {
                                        jsonObject = new JSONObject(object.toString());

                                        //Register account with facebook
                                        new registerProviderUser().execute(jsonObject.get("first_name").toString(),
                                                jsonObject.get("last_name").toString(), jsonObject.get("email").toString(),
                                                jsonObject.get("id").toString());
                                    } catch (JSONException e) {
                                        // TODO Auto-generated catch block
                                        e.printStackTrace();
                                        //Handle exception
                                        Generalfunction.Simple1ButtonDialog(getActivity().getResources().getString(R.string.facebook_mobile), getActivity());
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
                Generalfunction.Simple1ButtonDialog(e.toString(), getActivity());
            }
        });

        // define widget with view
        bindViews(v);

        return v;
    }

    private void bindViews(View v) {

        //Mapping scrollview
        accountScroll = (ScrollView) v.findViewById(R.id.accoutScrollView);

        //Mapping Edittext
        firstName = (EditText) v.findViewById(R.id.firstNameED);
        lastName = (EditText) v.findViewById(R.id.lastNameED);
        userID = (EditText) v.findViewById(R.id.userED);
        email = (EditText) v.findViewById(R.id.mailED);
        password = (EditText) v.findViewById(R.id.passwordED);

        //Mapping Button
        createBTN = (Button) v.findViewById(R.id.createBTN);

        createBTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                // Check Validation and register account
                firstName.getText().toString().trim();
                lastName.getText().toString().trim();
                userID.getText().toString().trim();
                password.getText().toString().trim();
                email.getText().toString().trim();
                if (Generalfunction.isValidUserName(firstName.getText().toString()) && Generalfunction.isValidUserName(lastName.getText().toString())) {
                    if (userID.getText().toString().length() > 2 && userID.getText().toString().length() < 9
                            && Generalfunction.isValidUserID(userID.getText().toString())) {
                        if (password.getText().toString().length() > 5 && Generalfunction.isValidUserID(password.getText().toString())) {
                            if (android.util.Patterns.EMAIL_ADDRESS.matcher(email.getText().toString()).matches()) {

                                if (cdObj.isConnectingToInternet()) {
                                    // Register Account with simple
                                    new registerUser().execute(firstName.getText().toString(), lastName.getText().toString(), userID.getText().toString(), email.getText().toString(), password.getText().toString());
                                } else {
                                    Generalfunction.Simple1ButtonDialog(context.getResources().getString(R.string.Internet_Message), context);
                                }

                            } else {
                                Snackbar snackbar1 = Snackbar.make(accountScroll, "The email address you've entered is not valid", Snackbar.LENGTH_LONG);
                                snackbar1.show();
                            }
                        } else {
                            Snackbar snackbar1 = Snackbar.make(accountScroll, "Password should be at least 6 characters", Snackbar.LENGTH_LONG);
                            snackbar1.show();
                        }
                    } else if (Generalfunction.CheckWspace(userID.getText().toString())) {
                        Snackbar snackbar1 = Snackbar.make(accountScroll, "No space are allowed in usernames", Snackbar.LENGTH_LONG);
                        snackbar1.show();
                    } else {
                        Snackbar snackbar1 = Snackbar.make(accountScroll, "Username should be between 3 and 8 characters long", Snackbar.LENGTH_LONG);
                        snackbar1.show();
                    }

                }
                else if(firstName.getText().toString().length() > 1 && lastName.getText().toString().length() > 1 &&
                        firstName.getText().toString().length() < 16 && lastName.getText().toString().length() < 16 ){
                    Snackbar snackbar1 = Snackbar.make(accountScroll, "First and Last name should be letters", Snackbar.LENGTH_LONG);
                    snackbar1.show();
                }
                else {
                    Snackbar snackbar1 = Snackbar.make(accountScroll, "First and Last name should each be between 2 and 15 characters long", Snackbar.LENGTH_LONG);
                    snackbar1.show();

                }

            }
        });

        // password action button listener
        password.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    focusOnView();
                    return true;
                }
                return false;
            }
        });
    }

   /* private boolean Validateion(){
        boolean isValidate=true;

        String strFname,strLname,strEmail,strUsername,strPassword;

        strFname=firstName.getText()

        return isValidate;
    }*/

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
                accountScroll.scrollTo(0, createBTN.getBottom());
            }
        });
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

    // Asynctask for register account with facebook
    public class registerProviderUser extends AsyncTask<String, Void, String> {

        ProgressDialog progress;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            progress = new ProgressDialog(getActivity());
            progress.setMessage("Registering....");
            progress.setCancelable(false);
            progress.show();
        }

        @Override
        protected String doInBackground(String... params) {
            // call webfunction
            return WebFunctions.registerUserProvider(params[0], params[1], params[2], params[3]);
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);

            progress.dismiss();

            JSONObject jsonObject;
            String response = "";
            try {
                jsonObject = new JSONObject(s);

                // Register is successfully done
                if (jsonObject.has("authentication_token")) {
                    response = jsonObject.getString("authentication_token");
                    SharedPreferences.Editor editor = context.getSharedPreferences("MY_PREFS", context.MODE_PRIVATE).edit();
                    editor.putString("token", response);
                    editor.commit();

                    Intent intent = new Intent(context, NavigationalSearchActivity.class);
                    startActivity(intent);
                    getActivity().finish();
                } else {
                    // show message
                    response = jsonObject.getString("message");
                    Generalfunction.Simple1ButtonDialog(response, getActivity());

                }
            } catch (JSONException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();

            }
        }
    }

    // Asynctask for register account with simple
    public class registerUser extends AsyncTask<String, Void, String> {

        ProgressDialog progress;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            progress = new ProgressDialog(getActivity());
            progress.setMessage("Registering....");
            progress.setCancelable(false);
            progress.show();
        }

        @Override
        protected String doInBackground(String... params) {
            // call webfunction
            return WebFunctions.registerUser(params[0], params[1], params[2], params[3], params[4]);
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

                    // Register is successfully done
                    if (jsonObject.has("authentication_token")) {
                        response = jsonObject.getString("authentication_token");
                        SharedPreferences.Editor editor = context.getSharedPreferences("MY_PREFS", context.MODE_PRIVATE).edit();
                        editor.putString("token", response);
                        editor.commit();

                        GlobalVar.setMyStringPref(context,Constant.loginUserpwd,password.getText().toString());
                        GlobalVar.setMyStringPref(context,Constant.loginUserEmail,email.getText().toString());

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
                    e.printStackTrace();
                }
            }

        }
    }


}