package com.foodapp.lien.foodapp;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import model.UserModel;
import parsing.ParsingFunctions;
import parsing.WebFunctions;
import utility.ConnectionDetector;
import utility.Constant;
import utility.Generalfunction;
import utility.GlobalVar;
import utility.LetterSpacingTextView;

public class SettingActivity extends AppCompatActivity {

    Context thisContext;

    private ImageView ivCoverimage, ivProfilephoto;
    private TextView tvFname,tvLname,tvUsername,tvEmail;
    private TextView tvfL_name,tvUname;
    private EditText etFname,etLname,etUsername,etEmail,etPassword;
    private ScrollView scrollView;
    private EditText etoldPassword;
    private EditText etnewPassword;
    Button btnDone, btnEdit, btnUpdate;

    LinearLayout llNonEdit, llEdit;

    String strUserid="";
    String strValidateMessage="";

    private ConnectionDetector cdObj;
    UserModel userModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);

        thisContext=this;

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        cdObj=new ConnectionDetector(thisContext);

        // Mapping Imageview
        ivCoverimage= (ImageView) findViewById(R.id.ivCoverimage);
        ivProfilephoto= (ImageView) findViewById(R.id.ivprofilephoto);

        //MApping Textview
        tvFname= (TextView)findViewById(R.id.tvfname_edit);
        tvLname= (TextView)findViewById(R.id.tvlname_edit);
        tvUsername= (TextView)findViewById(R.id.tvusername_edit);
        tvEmail= (TextView)findViewById(R.id.tvemail_edit);

        tvfL_name= (TextView) findViewById(R.id.userFNameSettingTv);
        tvUname= (TextView) findViewById(R.id.restaurantNameDetailTv);

        //Mapping Edittext
        etFname=(EditText)findViewById(R.id.etfname);
        etLname=(EditText)findViewById(R.id.etlname);
        etUsername=(EditText)findViewById(R.id.etusername);
        etEmail=(EditText)findViewById(R.id.etemail);

        scrollView = (ScrollView)findViewById(R.id.scrollView);
        llNonEdit = (LinearLayout)findViewById(R.id.nonEditLLT);
        llEdit = (LinearLayout) findViewById(R.id.editLLT);

        //Edit view
        btnUpdate = (Button) findViewById(R.id.updateButton);
        etPassword = (EditText) findViewById(R.id.passwordEdit);
        etPassword.setFocusable(false);
        etPassword.setClickable(true);
        etPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showPopUp();
            }
        });

        btnUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                UpdateUserDetail();

            }
        });

        //Non Editview
        btnEdit = (Button)findViewById(R.id.editButton);
        btnEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                DisplayEditview();

            }
        });

        if (cdObj.isConnectingToInternet()) {
            new getUser().execute();
        }
        else{
            Generalfunction.Simple1ButtonDialog(thisContext.getResources().getString(R.string.Internet_Message), thisContext);
        }
    }



    // Async task for get user detail
    public class getUser extends AsyncTask<String, Void, String> {

        ProgressDialog progress;
        String token;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            SharedPreferences prefs = thisContext.getSharedPreferences("MY_PREFS", thisContext.MODE_PRIVATE);
            token = prefs.getString("token", "");
            Log.d("token", token);

            progress = new ProgressDialog(thisContext);
            progress.setMessage("Loading...");
            progress.setCancelable(false);

            //start progress dialog
            progress.show();
        }

        @Override
        protected String doInBackground(String... params) {
            return WebFunctions.getUser(token);
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);

            //dismiss progress dialor
            progress.dismiss();

            DisplayNoneditview();

            //Parse usermodel
            userModel = ParsingFunctions.parseUserModel(result,thisContext);

            if (!userModel.first_name.equals("")) {
                SharedPreferences.Editor editor = thisContext.getSharedPreferences("MY_PREFS", thisContext.MODE_PRIVATE).edit();
                editor.putString("first_name", userModel.first_name);
                editor.commit();
            }

            Generalfunction.SaveUserDetail(thisContext,userModel.first_name,userModel.last_name,userModel.username,userModel.id,userModel.email);

            strUserid=userModel.id;
            tvFname.setText(userModel.first_name);
            tvLname.setText(userModel.last_name);
            tvUsername.setText(userModel.username);
            tvEmail.setText(userModel.email);

            etFname.setText(userModel.first_name);
            etLname.setText(userModel.last_name);
            etUsername.setText(userModel.username);
            etEmail.setText(userModel.email);

            tvfL_name.setText(userModel.first_name+" "+userModel.last_name);
            tvUname.setText(userModel.username);

            String userCoverphotoUrl = userModel.coverPhoto;
            String userprofilephotoUrl = userModel.profilePhoto;

            if (!Generalfunction.isEmptyCheck(userCoverphotoUrl)) {
                Picasso.with(thisContext)
                        .load(userModel.coverPhoto)
                        .placeholder(R.drawable.no_picture_sign) //this is optional the image to display while the url image is downloading
                        .error(R.drawable.no_picture_sign)         //this is also optional if some error has occurred in downloading the image this image would be displayed
                        .into(ivCoverimage);
            }
            else{
                Picasso.with(thisContext).load(R.drawable.no_picture_sign)
                        .into(ivCoverimage);
            }

            if (!Generalfunction.isEmptyCheck(userprofilephotoUrl)) {
                Picasso.with(thisContext).load(userModel.profilePhoto)
                        .placeholder(R.drawable.no_picture_sign).error(R.drawable.no_picture_sign)
                        .into(ivProfilephoto);

            }
            else {
                Picasso.with(thisContext).load(R.drawable.img_not_available)
                        .into(ivProfilephoto);
            }
        }
    }

    private void DisplayEditview(){

        llNonEdit.setVisibility(View.GONE);
        llEdit.setVisibility(View.VISIBLE);
        btnEdit.setVisibility(View.GONE);
        scrollView.fullScroll(ScrollView.FOCUS_UP);

    }

    private void DisplayNoneditview(){

        llNonEdit.setVisibility(View.VISIBLE);
        llEdit.setVisibility(View.GONE);
        btnEdit.setVisibility(View.VISIBLE);
    }

    private void UpdateUserDetail(){
        if(CheckValidattion()){
            if (cdObj.isConnectingToInternet()) {
                new UpdateregisterUser().execute(etFname.getText().toString(), etLname.getText().toString(), etUsername.getText().toString(), etEmail.getText().toString());
            }
            else{
                Generalfunction.Simple1ButtonDialog(thisContext.getResources().getString(R.string.Internet_Message), thisContext);
            }
        }
        else{
            Snackbar snackbar1 = Snackbar.make(scrollView, strValidateMessage, Snackbar.LENGTH_LONG);
            snackbar1.show();
        }
    }

    private boolean CheckValidattion(){
        boolean isValidate=true;

        String strfname=etFname.getText().toString();
        String strlname=etLname.getText().toString();
        String strusername=etUsername.getText().toString();
        String stremail=etEmail.getText().toString();

        if(Generalfunction.isEmptyCheck(strfname)){
            isValidate=false;
            strValidateMessage="First should each be between 2 and 15 characters long";
        }
        else if(Generalfunction.isEmptyCheck(strlname)){
            isValidate=false;
            strValidateMessage="Last name should each be between 2 and 15 characters long";
        }
        else if(Generalfunction.isEmptyCheck(strusername)){
            isValidate=false;
            strValidateMessage="Username should be between 3 and 8 characters long";
        }
        else if(Generalfunction.isEmptyCheck(stremail)){
            isValidate=false;
            strValidateMessage="The email address you've entered is not valid";
        }

        return isValidate;
    }

    public class UpdateregisterUser extends AsyncTask<String, Void, String> {

        ProgressDialog progress;
        String token;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            SharedPreferences prefs = thisContext.getSharedPreferences("MY_PREFS", thisContext.MODE_PRIVATE);
            token = prefs.getString("token", "");
            Log.d("token", token);

            progress= new ProgressDialog(thisContext);
            progress.setMessage("Loading...");
            progress.setCancelable(false);
            progress.show();
        }

        @Override
        protected String doInBackground(String... params) {
            // call webfunction
            return WebFunctions.updateregisterUser(token,strUserid, params[0], params[1], params[2], params[3]);
        }

        @Override
        protected void onPostExecute(String aVoid) {
            super.onPostExecute(aVoid);

            progress.dismiss();

            if (!aVoid.equals("")) {

                if(aVoid.equalsIgnoreCase("true")){

                    Generalfunction.Simple1ButtonDialog("User has successfully been updated.", thisContext);
                    new getUser().execute();

                }
                else{

                    Generalfunction.Simple1ButtonDialog(aVoid, thisContext);
                }

            }

        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
       // getMenuInflater().inflate(R.menu.menu_setting, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.actionTickSetting) {
            UpdateUserDetail();
            /*Intent intent = new Intent(SettingActivity.this, ProfileActivity.class);
            startActivity(intent);*/
        }else if (id == R.id.actionEditSetting) {
            /*Intent intent = new Intent(SettingActivity.this, ProfileActivity.class);
            startActivity(intent);*/
        }
        else if (id == android.R.id.home) {
            finish();
        }

        return super.onOptionsItemSelected(item);
    }

    static AlertDialog alertDialog;
    public void showPopUp () {
        LayoutInflater li = LayoutInflater.from(thisContext);
        View promptsView = li.inflate(R.layout.password_popup, null);

        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(thisContext);

        alertDialogBuilder.setView(promptsView);

        etoldPassword = (EditText) promptsView .findViewById(R.id.editOldPassword);
        etnewPassword = (EditText) promptsView .findViewById(R.id.editNewPassword);
        btnDone = (Button) promptsView .findViewById(R.id.doneButton);
        btnDone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(etoldPassword.getText().toString().length() > 1){

                    if(etnewPassword.getText().toString().length() > 1){
                        //  alertDialog.dismiss();
                        focusOnView();
                        new UpdateRegisterPwd().execute(etoldPassword.getText().toString(),etnewPassword.getText().toString());


                    }
                    else {
                        etnewPassword.requestFocus();
                    }
                }
                else{
                    //Snackbar snackbar1 = Snackbar.make(scrollView, "Password should be at least 6 characters", Snackbar.LENGTH_LONG);
                    etoldPassword.requestFocus();
                    // oldPassword.setError("Password should be at least 6 characters");
                    //snackbar1.show();
                }

            }
        });

        // create alert dialog
        alertDialog = alertDialogBuilder.create();
        alertDialog.show();
        // show it
        //alertDialogBuilder.show();
    }

    public class UpdateRegisterPwd extends AsyncTask<String, Void, String> {

        ProgressDialog progress;
        String token;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            SharedPreferences prefs = thisContext.getSharedPreferences("MY_PREFS", thisContext.MODE_PRIVATE);
            token = prefs.getString("token", "");
            Log.d("token", token);

            progress= new ProgressDialog(thisContext);
            progress.setMessage("Loading...");
            progress.setCancelable(false);
            progress.show();
        }

        @Override
        protected String doInBackground(String... params) {
            // call webfunction
            return WebFunctions.updateregisterPwd(token, params[0], params[1]);
        }

        @Override
        protected void onPostExecute(String aVoid) {
            super.onPostExecute(aVoid);

            progress.dismiss();

            if (!aVoid.equals("")) {

                if(aVoid.equalsIgnoreCase("true")){

                    alertDialog.dismiss();
                    Generalfunction.Simple1ButtonDialogClick("User has successfully been updated.", thisContext);
                    GlobalVar.setMyStringPref(thisContext, Constant.loginUserpwd,etnewPassword.getText().toString());

                    //new getUser().execute();

                }
                else{

                    Generalfunction.Simple1ButtonDialog(aVoid, thisContext);
                }

            }

        }
    }

    // hide keyboard
    private final void focusOnView() {
        View view = getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager) thisContext.getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
        new Handler().post(new Runnable() {
            @Override
            public void run() {
                scrollView.fullScroll(View.FOCUS_DOWN);
            }
        });
    }



}
