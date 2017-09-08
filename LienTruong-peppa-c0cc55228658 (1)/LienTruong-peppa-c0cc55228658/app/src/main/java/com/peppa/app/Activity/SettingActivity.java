package com.peppa.app.Activity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.facebook.login.LoginManager;
import com.peppa.app.R;
import com.peppa.app.model.UserModel;
import com.peppa.app.parsing.ParsingFunctions;
import com.peppa.app.parsing.WebFunctions;
import com.peppa.app.utility.ConnectionDetector;
import com.peppa.app.utility.Constant;
import com.peppa.app.utility.Generalfunction;
import com.peppa.app.utility.GlobalVar;

import org.json.JSONException;
import org.json.JSONObject;

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

    // case number 1 -> cover photo & 2 -> profile photo
    private int caseNumber=0;
    private Uri fileUri;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);

        thisContext=this;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
//            window.setStatusBarColor(getResources()
//                    .getColor(R.color.themeToolbarColor));
        }


        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("");
//       AppBarLayout.LayoutParams layoutParams = (AppBarLayout.LayoutParams) toolbar.getLayoutParams();
//        layoutParams.height = 150;
//        toolbar.setLayoutParams(layoutParams);
//     toolbar.setFitsSystemWindows(true);
//        toolbar.setPadding(0, 35, 0, 0);//for tab otherwise give space in tab
//        toolbar.setContentInsetsAbsolute(0, 0);
        setSupportActionBar(toolbar);

        getSupportActionBar().setHomeAsUpIndicator(R.mipmap.back_white);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        cdObj = new ConnectionDetector(thisContext);

        // Mapping Imageview
        ivCoverimage   = (ImageView) findViewById(R.id.ivCoverimage);
        ivProfilephoto = (ImageView) findViewById(R.id.ivprofilephoto);

        //MApping Textview
        tvFname    = (TextView)findViewById(R.id.tvfname_edit);
        tvLname    = (TextView)findViewById(R.id.tvlname_edit);
        tvUsername = (TextView)findViewById(R.id.tvusername_edit);
        tvEmail    = (TextView)findViewById(R.id.tvemail_edit);

        //Up portion
        tvfL_name  = (TextView) findViewById(R.id.userFNameSettingTv);
        tvUname    = (TextView) findViewById(R.id.restaurantNameDetailTv);

        //Mapping Edittext
        etFname    = (EditText)findViewById(R.id.etfname);
        etLname    = (EditText)findViewById(R.id.etlname);
        etUsername = (EditText)findViewById(R.id.etusername);
        etEmail    = (EditText)findViewById(R.id.etemail);

        scrollView = (ScrollView)findViewById(R.id.scrollView);

        llNonEdit  = (LinearLayout)findViewById(R.id.nonEditLLT);
        llEdit     = (LinearLayout) findViewById(R.id.editLLT);

        //Edit view
        btnUpdate  = (Button) findViewById(R.id.updateButton);
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

            //Call - Get login user API
            new getUser().execute();

        }
        else{
            Generalfunction.Simple1ButtonDialog(thisContext.getResources().getString(R.string.Internet_Message), thisContext);
        }

        etEmail.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    btnUpdate.setFocusable(true);
                    btnUpdate.setFocusableInTouchMode(true);///add this line
                    btnUpdate.requestFocus();
                    Generalfunction.hideKeyboard(SettingActivity.this);
                    return true;
                }
                return false;
            }
        });


        //Coverphoto
//        ivCoverimage.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                if(!Generalfunction.isEmptyCheck(GlobalVar.getMyStringPref(thisContext,Constant.loginUserID))) {
//                    Generalfunction.DisplaySimple2buttondialog(thisContext.getResources().getString(R.string.image_update_cover), thisContext);
//                }
//            }
//        });


        //Profile photo
        ivProfilephoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!Generalfunction.isEmptyCheck(GlobalVar.getMyStringPref(thisContext,Constant.loginUserID))) {
                    Generalfunction.DisplaySimple2buttondialog(thisContext.getResources().getString(R.string.image_update_photo), thisContext);
                }
            }
        });

        if(GlobalVar.getMyBooleanPref(thisContext,Constant.Login_facebook)){
            etPassword.setVisibility(View.GONE);
        }

        //set font
        Typeface playfair = Typeface.createFromAsset(getAssets(), "fonts/playfair.otf");
        Typeface lato = Typeface.createFromAsset(getAssets(), "fonts/lato.ttf");
        tvUname.setTypeface(lato);
        tvfL_name.setTypeface(playfair);
//        Typeface playfair = Typeface.createFromAsset(getAssets(), "fonts/playfair.otf");
//        Typeface lato = Typeface.createFromAsset(getAssets(), "fonts/lato.ttf");
//        tvFname.setTypeface(playfair);
//        tvLname.setTypeface(playfair);
//        tvUname.setTypeface(lato);

    }


    //Display Editview
    private void DisplayEditview(){

        llNonEdit.setVisibility(View.GONE);
        llEdit.setVisibility(View.VISIBLE);
        btnEdit.setVisibility(View.GONE);
        scrollView.fullScroll(ScrollView.FOCUS_UP);

    }


    //Display Non Editview
    private void DisplayNoneditview(){

        llNonEdit.setVisibility(View.VISIBLE);
        llEdit.setVisibility(View.GONE);
        btnEdit.setVisibility(View.VISIBLE);
    }


    //Call - Update User detail API
    private void UpdateUserDetail(){
        if(CheckValidattion()){
            if (cdObj.isConnectingToInternet()) {
                Generalfunction.RefreshvalueTrue(thisContext);
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


    //Call - Get user information API
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

            progress.dismiss();

            DisplayNoneditview();

            //Parse user model
            userModel = ParsingFunctions.parseUserModel(result,thisContext);

            if (!userModel.first_name.equals("")) {
                SharedPreferences.Editor editor = thisContext.getSharedPreferences("MY_PREFS", thisContext.MODE_PRIVATE).edit();
                editor.putString("first_name", userModel.first_name);
                editor.commit();
            }

            Generalfunction.SaveUserDetail(thisContext,userModel.first_name,userModel.last_name,userModel.username,userModel.id,userModel.email,userModel.profilePhoto,userModel.coverPhoto);

            strUserid = userModel.id;
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

            String userCoverphotoUrl   = userModel.coverPhoto;
            String userprofilephotoUrl = userModel.profilePhoto;

            Generalfunction.DisplayImage_picasso(userCoverphotoUrl,thisContext,Constant.case1,ivCoverimage,Constant.Ph_user_coverimage);
            Generalfunction.DisplayImage_picasso(userprofilephotoUrl,thisContext,Constant.case2,ivProfilephoto,Constant.Ph_userprofilepic);
        }
    }


    //Check - Validation
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


    //Call- Update login user Detail API
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
        protected void onPostExecute(String result) {
            super.onPostExecute(result);

            progress.dismiss();

            if (!result.equals("")) {
                JSONObject jsonObject;
                String Message = "";
                try {
                    Log.d("rup", "onPostExecute: " + result);
                    jsonObject = new JSONObject(result);

                    if (jsonObject.getString("success").toLowerCase().equalsIgnoreCase("yes")) {
                        Message = jsonObject.getString("message");
                        Generalfunction.Simple1ButtonDialogClick(Message, SettingActivity.this);
//                        DisplayMessage(Message);
                       new getUser().execute();
                    } else if (jsonObject.getString("success").toLowerCase().equalsIgnoreCase("no")) {
                        Message = jsonObject.getString("message");
                        Generalfunction.Simple1ButtonDialogClick2(Message, SettingActivity.this);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }


            }
        }
    }

//    private void DisplayMessage(final String strMessage) {
//        android.app.AlertDialog.Builder alertDialogBuilder = new android.app.AlertDialog.Builder(SettingActivity.this);
//        alertDialogBuilder.setTitle(getResources().getString(R.string.app_name));
//        alertDialogBuilder.setMessage(strMessage);
//        alertDialogBuilder.setCancelable(false);
//
//        alertDialogBuilder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
//            @Override
//            public void onClick(DialogInterface dialog, int which) {
////                Intent intent = new Intent(RateActivity.this, UserProfileActivity.class);
////                intent.putExtra(Constant.isMyprofile, true);
////                startActivity(intent);
////                alertDialog.dismiss();
////                if(Generalfunction.isCompare(getResources().getString(R.string.Internet_Message),strMessage)){
//                    finish();
////                }
//            }
//        });
//        android.app.AlertDialog alertDialog = alertDialogBuilder.create();
//        alertDialog.show();
//    }
    //Display - change password window
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

                        focusOnView();
                        new UpdateRegisterPwd().execute(etoldPassword.getText().toString(),etnewPassword.getText().toString());

                    }
                    else {
                        etnewPassword.requestFocus();
                    }
                }
                else{
                    etoldPassword.requestFocus();
                }

            }
        });

        // create alert dialog
        alertDialog = alertDialogBuilder.create();

        // show it
        alertDialog.show();

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.bottom_nav, menu);
        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if(id==R.id.menu_logout){
String uname = userModel.username;
            uname=uname.substring(0,1).toUpperCase() + uname.substring(1).toLowerCase();
                new android.support.v7.app.AlertDialog.Builder(thisContext)
                        .setTitle("Log out?")
                        .setMessage("We'd hate to see you leave " +uname + ", Are you sure you want to log out?")
                        .setPositiveButton("LOG OUT", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                try {
                                    LoginManager.getInstance().logOut();
                                } catch (Exception e) {
                                }
                                SharedPreferences.Editor editor = getSharedPreferences("MY_PREFS", MODE_PRIVATE).edit();
                                editor.putString("token", "");
                                editor.clear();
                                editor.commit();

                                GlobalVar.clearMyStringPref(thisContext);
                                Intent intent = new Intent(thisContext, MainActivity.class);
                                startActivity(intent);
                                finish();
                            }
                        })
                        .setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                // do nothing
                            }
                        })
                        .show();
        }

        if (id == android.R.id.home) {
            finish();
        }
        return super.onOptionsItemSelected(item);

    }


    //Call - Update/Change Password API
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
        protected void onPostExecute(String result) {
            super.onPostExecute(result);

            progress.dismiss();

            if (!result.equals("")) {
                JSONObject jsonObject;
                String Message = "";
                try {
                    Log.d("rup", "onPostExecute: " + result);
                    jsonObject = new JSONObject(result);

                    if (jsonObject.getString("success").toLowerCase().equalsIgnoreCase("yes")) {
                        Message = jsonObject.getString("message");
                        Generalfunction.Simple1ButtonDialogClick(Message, SettingActivity.this);
                       // DisplayMessage(Message);
                       new getUser().execute();
                    } else if (jsonObject.getString("success").toLowerCase().equalsIgnoreCase("no")) {
                        Message = jsonObject.getString("message");
                        Generalfunction.Simple1ButtonDialogClick2(Message, SettingActivity.this);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
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

   /* Change User cover photo and profile photo in setting
     using camera
     &
     gallery
     */


    //Cover photo
    public void UpdateCoverphoto(){
        caseNumber=1;
        fileUri=Generalfunction.getCameraUri();
        Generalfunction.OpenImageIntent(thisContext,fileUri);
    }


    //Profile photo
    public void UpdateProfilephoto(){
        caseNumber=2;
        fileUri=Generalfunction.getCameraUri();
        Generalfunction.OpenImageIntent(thisContext,fileUri);
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if (requestCode == Constant.PERMISSION_REQUEST_CODE_Camera) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                fileUri=Generalfunction.getCameraUri();
                Generalfunction.OpenImageIntent(thisContext,fileUri);
            } else {
                Generalfunction.HandleApppermission(requestCode,permissions,grantResults,thisContext);
                // Your app will not have this permission. Turn off all functions
                // that require this permission or it will force close like your
                // original question
            }
        }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (resultCode == RESULT_OK) {

            if (requestCode == Constant.CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE) {

                Uri selectedImageUri;
                boolean flagIscameragallery;
                boolean flagIscamera=false ;
                boolean iscamerabutgallery=false;
                String strFilename;

                // boolean isCamera = false;
                if (data == null) {
                    flagIscamera = true;
                }

                Log.d("navigationResult  ", data + " ");

                selectedImageUri = (data == null ? true : MediaStore.ACTION_IMAGE_CAPTURE.equals(data.getAction())) ? fileUri : (data == null ? null : data.getData());

                if (selectedImageUri != null) {

                    strFilename=selectedImageUri.toString();
                    flagIscameragallery=false;

                    if (flagIscamera) {
                        iscamerabutgallery=true;
                    }
                    flagIscamera=false;

                } else {
                    strFilename=fileUri.getPath();
                    flagIscamera=true;
                }
                flagIscameragallery=true;

                if (caseNumber == 1) {

                    ImageView imageview=ivCoverimage;

                    Generalfunction.GetSelectedPhotobitmap_uplaod(strFilename,flagIscameragallery,flagIscamera,iscamerabutgallery,imageview,thisContext,
                            GlobalVar.getMyStringPref(thisContext,Constant.loginUserID),Constant.case_imageupload_cover);

                }
                else if(caseNumber == 2){

                    ImageView imageview=ivProfilephoto;

                    Generalfunction.GetSelectedPhotobitmap_uplaod(strFilename,flagIscameragallery,flagIscamera,iscamerabutgallery,imageview,thisContext,
                            GlobalVar.getMyStringPref(thisContext,Constant.loginUserID),Constant.case_imageupload_profile);

                }

                GlobalVar.setMyBooleanPref(thisContext,Constant.Setting_Refresh,true);
                GlobalVar.setMyBooleanPref(thisContext,Constant.Settingfragment_Refresh,true);
            }

        }
    }


}
