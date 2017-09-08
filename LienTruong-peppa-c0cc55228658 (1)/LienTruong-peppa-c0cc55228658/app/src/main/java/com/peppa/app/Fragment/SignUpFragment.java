package com.peppa.app.Fragment;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
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
import com.peppa.app.utility.Constant;
import com.peppa.app.utility.Generalfunction;
import com.peppa.app.utility.GlobalVar;

public class SignUpFragment extends Fragment {

    Context context;

    private CallbackManager callbackManager;
    private LoginButton loginButton;
    private EditText firstName, userID, lastName, email, password;
    private ScrollView accountScroll;
    private Button createBTN;

    ConnectionDetector cdObj;
    private String strValidateMsg = "";


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

        //Mapping - Textview
        TextView fnameTV = (TextView) v.findViewById(R.id.fnameTV);
        TextView lnameTV = (TextView) v.findViewById(R.id.lNameTV);
        TextView emailTV = (TextView) v.findViewById(R.id.emailTV);
        TextView usernameTV = (TextView) v.findViewById(R.id.uNameTV);
        TextView passwordTV = (TextView) v.findViewById(R.id.passwordTV);

        //Mapping - facebook login button
        loginButton = (LoginButton) v.findViewById(R.id.fbAccountButton);
        loginButton.setBackgroundResource(R.drawable.pink_round_button);

        //Set facebook permission
        loginButton.setReadPermissions(Arrays.asList("public_profile", "email"));
        loginButton.setFragment(this);

        //Load font
        Generalfunction.loadFont(getActivity(), fnameTV, "lato_bold.ttf");
        Generalfunction.loadFont(getActivity(), lnameTV, "lato_bold.ttf");
        Generalfunction.loadFont(getActivity(), emailTV, "lato_bold.ttf");
        Generalfunction.loadFont(getActivity(), usernameTV, "lato_bold.ttf");
        Generalfunction.loadFont(getActivity(), passwordTV, "lato_bold.ttf");

        //Login with facebook account
        loginButton.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {

            @Override
            public void onSuccess(LoginResult loginResult) {

                if (loginResult.getAccessToken() != null) {

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
                Generalfunction.Simple1ButtonDialog(e.toString(), getActivity());
            }
        });

        // define widget with view
        bindViews(v);

        return v;
    }

    private void bindViews(View v) {

        //Mapping - Scrollview
        accountScroll = (ScrollView) v.findViewById(R.id.accoutScrollView);

        //Mapping - Edittext
        firstName = (EditText) v.findViewById(R.id.firstNameED);
        lastName = (EditText) v.findViewById(R.id.lastNameED);
        userID = (EditText) v.findViewById(R.id.userED);
        email = (EditText) v.findViewById(R.id.mailED);
        password = (EditText) v.findViewById(R.id.passwordED);

        //Mapping - Button
        createBTN = (Button) v.findViewById(R.id.createBTN);

        createBTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                new registerUser().execute(firstName.getText().toString().trim(), lastName.getText().toString().trim(), userID.getText().toString().trim()
                        , email.getText().toString().trim(), password.getText().toString().trim());

            }
        });

        //Password Action Button Listener
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


    //Hide - Hide keyboard
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
        AppEventsLogger.activateApp(context);
    }


    @Override
    public void onPause() {
        super.onPause();
        AppEventsLogger.deactivateApp(context);
    }


    //Call - Login with facebook API
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
            // call web function
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

                // Register is successfully done
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
                // TODO Auto-generated catch block
                // e.printStackTrace();
                Generalfunction.Simple1ButtonDialog(getResources().getString(R.string.Facebook_Without_Email), context);

                //facebook logout
                try {
                    LoginManager.getInstance().logOut();
                } catch (Exception e1) {
                }
            }
        }
    }


    //Call - Register (create account) API
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

                        GlobalVar.setMyStringPref(context, Constant.loginUserpwd, password.getText().toString());
                        GlobalVar.setMyStringPref(context, Constant.loginUserEmail, email.getText().toString());
                        GlobalVar.setMyBooleanPref(context, Constant.Login_facebook, false);

                        //Start - Navigation drawer home activity
                        Intent intent = new Intent(context, NavigationalSearchActivity.class);
                        startActivity(intent);
                        getActivity().finish();
                    } else {
                        // show message
                        if (aVoid.contains("errors")) {
                            JSONArray st = jsonObject.getJSONArray("errors");
                            for (int i = st.length() - 1; i >= 0; i--) {
                                if (i == 0) {
                                    response = st.getString(i) + "\n\n" + response;
                                } else {
                                    response = st.getString(i) + "\n\n" + response;
                                }
                            }
                            //Toast.makeText(getActivity(), response, Toast.LENGTH_SHORT).show();
                        } else {
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

    @Override
    public void onStart() {
        super.onStart();
        //logout facebook
        try {
            LoginManager.getInstance().logOut();
        } catch (Exception e) {
        }
    }
}