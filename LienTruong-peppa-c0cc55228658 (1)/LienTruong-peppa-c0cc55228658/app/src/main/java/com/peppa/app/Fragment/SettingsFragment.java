package com.peppa.app.Fragment;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
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
import com.peppa.app.Activity.FilterActivity;
import com.peppa.app.Activity.MainActivity;
import com.peppa.app.Activity.MapsActivity;
import com.peppa.app.Activity.NavigationalSearchActivity;
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


public class SettingsFragment extends Fragment {

    Context thisContext;

    public static ImageView ivCoverimage, ivProfilephoto;
    private TextView tvFname,tvLname,tvUsername,tvEmail;
    private TextView tvfL_name,tvUname;
    private EditText etFname,etLname,etUsername,etEmail,etPassword;
    private ScrollView scrollView;
    private EditText etoldPassword;
    private EditText etnewPassword;
    private Button btnDone, btnEdit, btnUpdate;

    private LinearLayout llNonEdit, llEdit;

    private String strUserid="";
    private String strValidateMessage="";

    private ConnectionDetector cdObj;
    private UserModel userModel;
    private Uri fileUri;

    private String userCoverphotoUrl ;
    private String userprofilephotoUrl ;

    private Toolbar toolbar;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        thisContext = this.getActivity();
        cdObj=new ConnectionDetector(thisContext);
        Log.d(Constant.TAG, "onCreate: setting");
        setHasOptionsMenu(true);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_settings, container, false);


        //Set Toolbar

        Toolbar toolbar = (Toolbar) getActivity().findViewById(R.id.toolbar);
        toolbar.setTitle(getResources().getString(R.string.empty));
        toolbar.setBackgroundColor(Color.TRANSPARENT);

        toolbar.setNavigationIcon(getResources().getDrawable(R.mipmap.back));
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i=new Intent(getActivity(),NavigationalSearchActivity.class);
                startActivity(i);
                getActivity().finish();
            }
        });

//        AppBarLayout.LayoutParams layoutParams = (AppBarLayout.LayoutParams) toolbar.getLayoutParams();
//        layoutParams.height = 150;
//        toolbar.setLayoutParams(layoutParams);
//        //toolbar.setFitsSystemWindows(true);
//        toolbar.setPadding(0, 35, 0, 0);//for tab otherwise give space in tab
//        toolbar.setContentInsetsAbsolute(0, 0);
//
//
//        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
//            getActivity().getWindow().getDecorView().setSystemUiVisibility(view.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
//            getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
//            getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
//        }




        // Mapping Imageview
        ivCoverimage   = (ImageView) view.findViewById(R.id.ivCoverimage);
        ivProfilephoto = (ImageView) view.findViewById(R.id.ivprofilephoto);

        //Mapping Textview
        tvFname    = (TextView) view.findViewById(R.id.tvfname_edit);
        tvLname    = (TextView) view.findViewById(R.id.tvlname_edit);
        tvUsername = (TextView) view.findViewById(R.id.tvusername_edit);
        tvEmail    = (TextView) view.findViewById(R.id.tvemail_edit);

        //Up portion
        tvfL_name  = (TextView) view.findViewById(R.id.userFNameSettingTv);
        tvUname    = (TextView) view.findViewById(R.id.restaurantNameDetailTv);

        //Mapping Edittext
        etFname    = (EditText)view.findViewById(R.id.etfname);
        etLname    = (EditText)view.findViewById(R.id.etlname);
        etUsername = (EditText)view.findViewById(R.id.etusername);
        etEmail    = (EditText)view.findViewById(R.id.etemail);
        etPassword = (EditText) view.findViewById(R.id.passwordEdit);

        //Mapping linear layout
        llNonEdit  = (LinearLayout) view.findViewById(R.id.nonEditLLT);
        llEdit     = (LinearLayout) view.findViewById(R.id.editLLT);

        //Mapping scrollview
        scrollView = (ScrollView) view.findViewById(R.id.scrollView);

        //Mapping button
        btnUpdate  = (Button) view.findViewById(R.id.updateButton);

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
        });

        //Non Editview
        btnEdit = (Button) view.findViewById(R.id.editButton);
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

        etEmail.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    btnUpdate.setFocusable(true);
                    btnUpdate.setFocusableInTouchMode(true);///add this line
                    btnUpdate.requestFocus();
                    Generalfunction.hideKeyboard(getActivity());
                    return true;
                }
                return false;
            }
        });

        //Cover photo
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

        Generalfunction.Refreshvalue_Filter(thisContext);
        GlobalVar.setMyBooleanPref(thisContext,Constant.Settingfragment_Refresh,false);

        if(GlobalVar.getMyBooleanPref(thisContext,Constant.Login_facebook)){
            etPassword.setVisibility(View.GONE);
        }

        //set font
        Typeface playfair = Typeface.createFromAsset(getActivity().getAssets(), "fonts/playfair.otf");
        Typeface lato = Typeface.createFromAsset(getActivity().getAssets(), "fonts/lato.ttf");
        tvUname.setTypeface(lato);
        tvfL_name.setTypeface(playfair);

        return view;
    }


    @Override
    public void onStart() {
        super.onStart();

        Log.d(Constant.TAG, "onStart: setting fragment image url ");

        //user update detail from Seeting - Myuserprofile - setting - updated detils - Back on my user profile - back
        if(GlobalVar.getMyBooleanPref(thisContext,Constant.Settingfragment_Refresh)){
            GlobalVar.setMyBooleanPref(thisContext,Constant.Settingfragment_Refresh,false);
            if (cdObj.isConnectingToInternet()) {
                new getUser().execute();
            }
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

            Generalfunction.SaveUserDetail(thisContext,userModel.first_name,userModel.last_name,userModel.username,userModel.id,userModel.email,userModel.profilePhoto,userModel.coverPhoto);

            ((NavigationalSearchActivity) thisContext).DisplayNavigationDetail();

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
            userCoverphotoUrl = userModel.coverPhoto;
            userprofilephotoUrl = userModel.profilePhoto;

            Log.d(Constant.TAG, "image onPostExecute: "+userprofilephotoUrl);

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
                        DisplayMessage(Message,thisContext);
//                        new getUser().execute();
                    } else if (jsonObject.getString("success").toLowerCase().equalsIgnoreCase("no")) {
                        Message = jsonObject.getString("message");
                        Generalfunction.Simple1ButtonDialogClick2(Message, getActivity());
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }


            }

            }
        }




    //Display - Edit view
    private void DisplayEditview(){

        llNonEdit.setVisibility(View.GONE);
        llEdit.setVisibility(View.VISIBLE);
        btnEdit.setVisibility(View.GONE);
        scrollView.fullScroll(ScrollView.FOCUS_UP);


    }


    //Display - Non Editview
    private void DisplayNoneditview(){

        llNonEdit.setVisibility(View.VISIBLE);
        llEdit.setVisibility(View.GONE);
        btnEdit.setVisibility(View.VISIBLE);

    }


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

                        //Call - API
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

       alertDialog = alertDialogBuilder.create();   // create alert dialog
       alertDialog.show();    // show

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
                        DisplayMessage(Message,thisContext);

                    } else if (jsonObject.getString("success").toLowerCase().equalsIgnoreCase("no")) {
                        Message = jsonObject.getString("message");
                        Generalfunction.Simple1ButtonDialogClick2(Message,getActivity());
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }


            }

        }
    }
    private void DisplayMessage(final String strMessage,final Context context) {
        android.app.AlertDialog.Builder alertDialogBuilder = new android.app.AlertDialog.Builder(context);
        alertDialogBuilder.setTitle(getResources().getString(R.string.app_name));
        alertDialogBuilder.setMessage(strMessage);
        alertDialogBuilder.setCancelable(false);

        alertDialogBuilder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
//                Intent intent = new Intent(RateActivity.this, UserProfileActivity.class);
//                intent.putExtra(Constant.isMyprofile, true);
//                startActivity(intent);
//                alertDialog.dismiss();
//                if(Generalfunction.isCompare(getResources().getString(R.string.Internet_Message),strMessage)){
                dialog.dismiss();
                new getUser().execute();

//                }
            }
        });
        android.app.AlertDialog alertDialog = alertDialogBuilder.create();
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

                scrollView.fullScroll(View.FOCUS_DOWN);
            }
        });
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.bottom_nav, menu);




    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        //option menu select item event
        int id = item.getItemId();
        if (id == R.id.menu_logout) {
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
                            SharedPreferences.Editor editor = getActivity().getSharedPreferences("MY_PREFS", getActivity().MODE_PRIVATE).edit();
                            editor.putString("token", "");
                            editor.clear();
                            editor.commit();

                            GlobalVar.clearMyStringPref(thisContext);
                            Intent intent = new Intent(thisContext, MainActivity.class);
                            startActivity(intent);
                            getActivity().finish();
                        }
                    })
                    .setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            // do nothing
                        }
                    })
                    .show();

            //startActivityForResult(intent, FILTER_REQUEST_CODE);
        }

        return super.onOptionsItemSelected(item);
    }



}
