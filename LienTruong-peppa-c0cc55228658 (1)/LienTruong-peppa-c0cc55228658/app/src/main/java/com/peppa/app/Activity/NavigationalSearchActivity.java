package com.peppa.app.Activity;

import android.Manifest;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LayerDrawable;
import android.location.Location;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.TypedValue;
import android.view.Display;
import android.view.KeyCharacterMap;
import android.view.KeyEvent;
import android.view.Surface;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.facebook.login.LoginManager;
import com.peppa.app.Fragment.FavouritesFragment;
import com.peppa.app.R;
import com.peppa.app.Fragment.SearchFragment;
import com.peppa.app.Fragment.SettingsFragment;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;

import java.io.File;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

import com.peppa.app.model.UserModel;
import com.peppa.app.parsing.BadgeDrawable;
import com.peppa.app.parsing.ParsingFunctions;
import com.peppa.app.parsing.WebFunctions;
import com.peppa.app.utility.ConnectionDetector;
import com.peppa.app.utility.Constant;
import com.peppa.app.utility.Generalfunction;
import com.peppa.app.utility.GlobalVar;

import org.json.JSONException;
import org.json.JSONObject;


public class NavigationalSearchActivity extends AppCompatActivity
        implements View.OnClickListener, GoogleApiClient.OnConnectionFailedListener, GoogleApiClient.ConnectionCallbacks {

    private Context thisContext;
    private UserModel userModel;
    private ConnectionDetector cdObj;

    private DrawerLayout mDrawerLayout;
    private Toolbar toolbar;
    private TextView tv_navFLname, tv_navUsername;
    private FloatingActionButton fab;
    private ImageView ivUserprofile;
    ProgressDialog progress;

    //Navigation
ImageView tvnavSearch, tvnavMyprofile, tvnavFavourite, tvnavSetting, tvnavLogout,tvnavpost;

    private Boolean doubleBackToExitPressedOnce = false;
    private Uri fileUri;

    //Current Location
    private GoogleApiClient mGoogleApiClient;
    private static final String TAG = "Navigation";

    private int caseNumber = 0;     // case number 1 -> cover photo & 2 -> profile photo
    private Fragment fragment;
    private Activity activity;


    private static final String SELECTED_ITEM = "arg_selected_item";

  BottomNavigationView mBottomNav;
    private int mSelectedItem;
  LayerDrawable notify;
    int count=2;
    public static final String MyPREFERENCES = "MyPrefs" ;

TextView badge;
    String Message,activated;

    @Override
    protected void onCreate(Bundle savedInstanceState) {


        super.onCreate(savedInstanceState);

        thisContext = this;

        //Set layout
        setContentView(R.layout.activity_navigational_search);


        //Check Gps Avialble
        boolean IsGpsEnable = Generalfunction.CheckGpsAvailable(thisContext);
        if (!IsGpsEnable) {
            Generalfunction.GpsAlertMessage(thisContext, NavigationalSearchActivity.this);
        }

        buildGoogleApiClient();


//        if (Build.VERSION.SDK_INT >= 21) {
//            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
//        }

        //Mapping - Toolbar
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setHomeAsUpIndicator(R.mipmap.back);

        toolbar.setBackgroundColor(getResources().getColor(android.R.color.transparent));


        mBottomNav=(BottomNavigationView)findViewById(R.id.navigation);

        //Mapping - Textview (Navigation list)
        tvnavSearch = (ImageView) findViewById(R.id.tv_navsearch);
        tvnavMyprofile = (ImageView) findViewById(R.id.tv_navmyprofile);
        tvnavFavourite = (ImageView) findViewById(R.id.tv_navfavourite);
        tvnavSetting = (ImageView) findViewById(R.id.tv_navsetting);
//        tvnavLogout = (ImageView) findViewById(R.id.tv_navlogout);
        tvnavpost=(ImageView) findViewById(R.id.tv_navpost);


      //  new VerifyingUser().execute();

        fragment = new SearchFragment();
        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction().replace(R.id.container, fragment).commit();



        tvnavSearch.setOnClickListener(this);
        tvnavMyprofile.setOnClickListener(this);
        tvnavFavourite.setOnClickListener(this);
        tvnavSetting.setOnClickListener(this);
//        tvnavLogout.setOnClickListener(this);
        tvnavpost.setOnClickListener(this);

        //Mapping - Floating action button

        cdObj = new ConnectionDetector(thisContext);

        if (cdObj.isConnectingToInternet()) {

            //Call - get user API
            new getUser().execute();


        } else {
            tv_navUsername.setText(GlobalVar.getMyStringPref(thisContext, Constant.loginUserName));
            tv_navFLname.setText(GlobalVar.getMyStringPref(thisContext, Constant.loginUserfirstName) + " " + GlobalVar.getMyStringPref(thisContext, Constant.loginUserlastName));
            Generalfunction.Simple1ButtonDialog(thisContext.getResources().getString(R.string.Internet_Message), thisContext);
        }

        //Onclick listener


       // fab.setOnClickListener(this);

        //Set - fragment using fragment manager






        SharedPreferences pre = getSharedPreferences(MyPREFERENCES, MODE_PRIVATE);
        String count = pre.getString("badge", "");
        badge = (TextView) findViewById(R.id.badge);
        if(count.equals("0")){
            badge.setVisibility(View.GONE);
        }
        else {



            badge.setText(count);
        }


        //Check - Internet connection and perform operation


        //Set - Background color of toolbar


        String[] PERMISSIONS = { Manifest.permission.ACCESS_COARSE_LOCATION,
                                 Manifest.permission.WRITE_EXTERNAL_STORAGE,
                                 Manifest.permission.CAMERA,
                                 Manifest.permission.CALL_PHONE};

        if (!hasPermissions(this, PERMISSIONS)) {
            ActivityCompat.requestPermissions(this, PERMISSIONS, Constant.PERMISSION_ALL);
        }


        clearNavitemBackground(1);

//


        try {
            if (GlobalVar.getMyBooleanPref(thisContext, Constant.bitmapToimage)) {
                String file_path = Constant.Filepath_Image;
                File dir = new File(file_path);
                if (dir.exists()) {
                    Generalfunction.DeletefIle(dir);
                }
            }
        } catch (Exception e) {
        }



    }

//

    //Builds a GoogleApiClient.
    protected synchronized void buildGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)    //Use the addApi() method to request the LocationServices API.
                .build();

    }


    @Override
    protected void onStart() {
        super.onStart();
        Generalfunction.RefreshScreenValue(thisContext);
        DisplayNavigationDetail();
        mGoogleApiClient.connect();
    }


    protected void onStop() {
        if (mGoogleApiClient.isConnected()) {
            mGoogleApiClient.disconnect();
        }
        super.onStop();
    }


    public static boolean hasPermissions(Context context, String... permissions) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && context != null && permissions != null) {
            for (String permission : permissions) {
                if (ActivityCompat.checkSelfPermission(context, permission) == PackageManager.PERMISSION_GRANTED) {
                    return true;
                }
            }
        }
        return false;
    }




    @Override
    public void onClick(View v) {

      //  fab.setVisibility(View.GONE);
        Generalfunction.colorizeToolbar(toolbar, getResources().getColor(R.color.app_greycolor), NavigationalSearchActivity.this);

        switch (v.getId()) {

            case R.id.tv_navsearch:
                toolbar.setBackgroundColor(getResources().getColor(android.R.color.transparent));
                changeFragment(R.id.tv_navsearch);
                break;

            case R.id.tv_navmyprofile:
//
                Intent intent = new Intent(thisContext,UserProfileActivity.class);
                intent.putExtra(Constant.isMyprofile, true);
                startActivity(intent);

                break;

            case R.id.tv_navfavourite:
                changeFragment(R.id.tv_navfavourite);
                break;

            case R.id.tv_navsetting:
                changeFragment(R.id.tv_navsetting);
                break;

//

            case R.id.tv_navpost:
                new VerifyingUser().execute();
                tvnavpost.setImageDrawable(getResources().getDrawable(R.mipmap.post));
                tvnavSearch.setImageDrawable(getResources().getDrawable(R.mipmap.search2));
                tvnavFavourite.setImageDrawable(getResources().getDrawable(R.mipmap.star1));
                tvnavMyprofile.setImageDrawable(getResources().getDrawable(R.mipmap.profilee));
//                tvnavLogout.setImageDrawable(getResources().getDrawable(R.mipmap.logout));
                tvnavSetting.setImageDrawable(getResources().getDrawable(R.mipmap.settings1));

                caseNumber = 0;

//                if(activated.equalsIgnoreCase("false")) {
//                    new VerifyingUser().execute();
//                    new CountDownTimer(500, 500) {
//                        @Override
//                        public void onTick(long millisUntilFinished) {
//
//                        }
//
//                        @Override
//                        public void onFinish() {
//                            if(activated.equalsIgnoreCase("true")){
//                                if (ActivityCompat.checkSelfPermission(thisContext, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
//                                    // Request missing camera permission.
//                                    ActivityCompat.requestPermissions(NavigationalSearchActivity.this, new String[]{Manifest.permission.CAMERA}, Constant.PERMISSION_REQUEST_CODE_Camera);
//                                }
//                                else if (ActivityCompat.checkSelfPermission(thisContext, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
//                                    // Request missing storage permission.
//                                    ActivityCompat.requestPermissions(NavigationalSearchActivity.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, Constant.PERMISSION_REQUEST_CODE_STORAGE);
//                                }
//                                else {
//                                    openImageIntent();
//                                }
//                            }
//                            else{
//                                DisplayMessage1(Message);
//                            }
//                        }
//                    }.start();
//                }
//                else{
//                    if (ActivityCompat.checkSelfPermission(thisContext, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
//                        // Request missing camera permission.
//                        ActivityCompat.requestPermissions(NavigationalSearchActivity.this, new String[]{Manifest.permission.CAMERA}, Constant.PERMISSION_REQUEST_CODE_Camera);
//                    }
//                    else if (ActivityCompat.checkSelfPermission(thisContext, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
//                        // Request missing storage permission.
//                        ActivityCompat.requestPermissions(NavigationalSearchActivity.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, Constant.PERMISSION_REQUEST_CODE_STORAGE);
//                    }
//                    else {
//                        openImageIntent();
//                    }
//                }



                break;
        }
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Generalfunction.DisplayLog("Connection failed: ConnectionResult.getErrorCode() = " + connectionResult.getErrorCode());

    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {

        Generalfunction.DisplayLog("connected");

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }

        Location mLastLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
        if (mLastLocation != null) {
            GlobalVar.setMyStringPref(thisContext, Constant.CurrentLatitude, String.valueOf(mLastLocation.getLatitude()));
            GlobalVar.setMyStringPref(thisContext, Constant.CurrentLongitude, String.valueOf(mLastLocation.getLongitude()));
        }

    }

    @Override
    public void onConnectionSuspended(int i) {
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {

        if (requestCode == Constant.PERMISSION_REQUEST_CODE_Camera) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                openImageIntent();
            } else {
                Generalfunction.HandleApppermission(requestCode,permissions,grantResults,thisContext);
            }
        }
        else {
            Generalfunction.HandleApppermission(requestCode,permissions,grantResults,thisContext);
        }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {

            try {
                if (requestCode == Constant.CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE) {

                    Uri selectedImageUri;
                    boolean flagIscameragallery;
                    boolean flagIscamera = false;
                    boolean iscamerabutgallery = false;
                    String strFilename;

                    if (data == null) {
                        flagIscamera = true;
                    }

                    selectedImageUri = (data == null ? true : MediaStore.ACTION_IMAGE_CAPTURE.equals(data.getAction())) ? fileUri : (data == null ? null : data.getData());

                    Generalfunction.DisplayLog("onActivityResult: selected image uri " + selectedImageUri);

                    if (selectedImageUri != null) {
                        strFilename = selectedImageUri.toString();
                        flagIscameragallery = false;

                        if (flagIscamera) {
                            iscamerabutgallery = true;
                        }
                        flagIscamera = false;

                    } else {
                        strFilename = fileUri.getPath();
                        flagIscamera = true;
                    }

                    flagIscameragallery = true;

                    if (caseNumber == 1) {

                        ImageView imageview = SettingsFragment.ivCoverimage;

                        Generalfunction.GetSelectedPhotobitmap_uplaod(strFilename, flagIscameragallery, flagIscamera, iscamerabutgallery, imageview, thisContext,
                                GlobalVar.getMyStringPref(thisContext, Constant.loginUserID), Constant.case_imageupload_cover);

                    } else if (caseNumber == 2) {

                        ImageView imageview = SettingsFragment.ivProfilephoto;

                        Generalfunction.GetSelectedPhotobitmap_uplaod(strFilename, flagIscameragallery, flagIscamera, iscamerabutgallery, imageview, thisContext,
                                GlobalVar.getMyStringPref(thisContext, Constant.loginUserID), Constant.case_imageupload_profile);

                    } else {

                        Intent intent = new Intent(thisContext, PostActivity_.class);
                        intent.putExtra("filename", strFilename);
                        intent.putExtra("isCamera", flagIscamera);
                        intent.putExtra("isCamerabutgallery", iscamerabutgallery);
                        intent.putExtra("isCameraOrGallery", flagIscameragallery);
                        startActivity(intent);
                    }

                }

            } catch (Exception e) {
                e.printStackTrace();
            }
        }

    }
    public class VerifyingUser extends AsyncTask<String, Void, String> {

        ProgressDialog progress;
        String token;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            SharedPreferences prefs = getSharedPreferences("MY_PREFS", MODE_PRIVATE);

            token = prefs.getString("token", "");
            Log.d("token", token);

            progress = new ProgressDialog(NavigationalSearchActivity.this);
            progress.setMessage("Loading...");
            progress.setCancelable(false);
            progress.show();
        }

        @Override
        protected String doInBackground(String... params) {
            // call webfunction
            return WebFunctions.VerifiedUser(token);
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);

            progress.dismiss();

            if (!result.equals("")) {
                JSONObject jsonObject;

                try {
                    Log.d("rup", "onPostExecute: " + result);
                    jsonObject = new JSONObject(result);
                    activated = jsonObject.getString("activated");
                    if(activated.equalsIgnoreCase("false")) {
                        Message = jsonObject.getString("message");
                    }
                    if(activated.equalsIgnoreCase("true")){
                        if (ActivityCompat.checkSelfPermission(thisContext, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                            // Request missing camera permission.
                            ActivityCompat.requestPermissions(NavigationalSearchActivity.this, new String[]{Manifest.permission.CAMERA}, Constant.PERMISSION_REQUEST_CODE_Camera);
                        }
                        else if (ActivityCompat.checkSelfPermission(thisContext, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                            // Request missing storage permission.
                            ActivityCompat.requestPermissions(NavigationalSearchActivity.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, Constant.PERMISSION_REQUEST_CODE_STORAGE);
                        }
                        else {
                            openImageIntent();
                        }
                    }
                    else{
                        DisplayMessage1(Message);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }


            }
        }
    }
    private void DisplayMessage1(String strMessage) {
        android.app.AlertDialog.Builder alertDialogBuilder = new android.app.AlertDialog.Builder(this);
        alertDialogBuilder.setTitle(getResources().getString(R.string.app_name));
        alertDialogBuilder.setMessage(strMessage);
        alertDialogBuilder.setCancelable(false);

        alertDialogBuilder.setPositiveButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        alertDialogBuilder.setNegativeButton("Resend email", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                new Post1().execute();

            }
        });
        android.app.AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }

    private class Post1 extends AsyncTask<String, Void, String> {
        String token;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progress = new ProgressDialog(NavigationalSearchActivity.this);
            progress.setMessage("Sending email");
            progress.setCancelable(false);
            progress.show();

            SharedPreferences prefs = getSharedPreferences("MY_PREFS", MODE_PRIVATE);

            token = prefs.getString("token", "");
            Log.d("token", token);


        }

        @Override
        protected String doInBackground(String... params) {
            return WebFunctions.post_email(token);
        }

        @Override
        protected void onPostExecute(String aVoid) {
            super.onPostExecute(aVoid);
            progress.dismiss();
            if (!aVoid.equals("")) {
                JSONObject jsonObject;
                String Message = "";
                try {
                    Log.d("rup", "onPostExecute: " + aVoid);
                    jsonObject = new JSONObject(aVoid);


                    Message = jsonObject.getString("message");
                    Toast.makeText(getApplicationContext(),Message,Toast.LENGTH_LONG).show();


                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        }
    }
    private void openImageIntent() {

        fileUri = Generalfunction.getCameraUri();

        Intent intent_camrea = null;

        // Camera.
        final List<Intent> cameraIntents = new ArrayList<Intent>();
        final Intent captureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        final PackageManager packageManager = getPackageManager();
        final List<ResolveInfo> listCam = packageManager.queryIntentActivities(captureIntent, 0);
        for (ResolveInfo res : listCam)
        {
            final String packageName = res.activityInfo.packageName;
            //final Intent
            intent_camrea = new Intent(captureIntent);
            intent_camrea.setComponent(new ComponentName(res.activityInfo.packageName, res.activityInfo.name));
            intent_camrea.setPackage(packageName);
            intent_camrea.putExtra(MediaStore.EXTRA_OUTPUT, fileUri);
            cameraIntents.add(intent_camrea);
        }

        // File System.
        final Intent galleryIntent = new Intent();
        galleryIntent.setType("image/*");
        galleryIntent.setAction(Intent.ACTION_GET_CONTENT);

        // Chooser of file System options.
        final Intent chooserIntent = Intent.createChooser(galleryIntent, "Select Source");

        Intent i = new Intent(this, PostActivity_.class);
        Intent[] intentArray = {intent_camrea, i};

        //Add the camera options.
        chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, intentArray);

        startActivityForResult(chooserIntent, Constant.CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE);

    }


    public void changeFragment(int id) {
        fragment = null;

        switch (id) {

            case R.id.tv_navsearch:

                //fab.setVisibility(View.VISIBLE);

                fragment = new SearchFragment();
                clearNavitemBackground(1);

                break;

            case R.id.tv_navfavourite:

                fragment = new FavouritesFragment();
                clearNavitemBackground(3);

                break;

            case R.id.tv_navsetting:

                fragment = new SettingsFragment();
                clearNavitemBackground(4);

                break;
        }

        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction().replace(R.id.container, fragment).commit();
       // mDrawerLayout.closeDrawers();
        toolbar.setBackgroundColor(getResources().getColor(android.R.color.white));

    }


    @Override
    public void onBackPressed() {
//
            if (doubleBackToExitPressedOnce) {
                finish();
                return;
            }
            doubleBackToExitPressedOnce = true;
            Toast.makeText(this, "Pressing Back button again will exit App", Toast.LENGTH_SHORT).show();
            new Handler().postDelayed(new Runnable() {

                public void run() {
                    doubleBackToExitPressedOnce = false;

                }
            }, 3000);
        }
    //}
//    @Override
//    public void onBackPressed() {
////        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
////        if (drawer.isDrawerOpen(GravityCompat.START)) {
////            drawer.closeDrawer(GravityCompat.START);
////        } else {
//
//            if (doubleBackToExitPressedOnce) {
//                Intent a = new Intent(Intent.ACTION_MAIN);
//                a.addCategory(Intent.CATEGORY_HOME);
//                a.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//                startActivity(a);
//            } else {
//                Toast.makeText(this, "Pressing Back button again will exit App", Toast.LENGTH_SHORT).show();
//                this.doubleBackToExitPressedOnce = true;
//            }
//        }
   // }


    /* Change User cover photo and profile photo in setting
     using camera
     &
     gallery
     */

    //Update - Cover photo
    public void UpdateCoverphoto() {
        caseNumber = 1;
        fileUri = Generalfunction.getCameraUri();
        Generalfunction.OpenImageIntent(thisContext, fileUri);
    }


    //Update - Profile photo
    public void UpdateProfilephoto() {
        caseNumber = 2;
        fileUri = Generalfunction.getCameraUri();
        Generalfunction.OpenImageIntent(thisContext, fileUri);
    }


    //Clear Navigation item Background
    private void clearNavitemBackground(int caseitem) {

        tvnavSearch.setBackgroundColor(getResources().getColor(R.color.white));
        tvnavMyprofile.setBackgroundColor(getResources().getColor(R.color.white));
        tvnavFavourite.setBackgroundColor(getResources().getColor(R.color.white));
        tvnavSetting.setBackgroundColor(getResources().getColor(R.color.white));
//        tvnavLogout.setBackgroundColor(getResources().getColor(R.color.white));

    if (caseitem == 1) {
        tvnavSearch.setImageDrawable(getResources().getDrawable(R.mipmap.search_pink));
        tvnavFavourite.setImageDrawable(getResources().getDrawable(R.mipmap.star1));
        tvnavMyprofile.setImageDrawable(getResources().getDrawable(R.mipmap.profilee));


//        tvnavLogout.setImageDrawable(getResources().getDrawable(R.mipmap.logout));
        tvnavSetting.setImageDrawable(getResources().getDrawable(R.mipmap.settings1));


    }


   else if (caseitem == 3) {
        tvnavSearch.setImageDrawable(getResources().getDrawable(R.mipmap.search2));
        tvnavFavourite.setImageDrawable(getResources().getDrawable(R.mipmap.favourite_pink));
        tvnavMyprofile.setImageDrawable(getResources().getDrawable(R.mipmap.profilee));
//        tvnavLogout.setImageDrawable(getResources().getDrawable(R.mipmap.logout));
        tvnavSetting.setImageDrawable(getResources().getDrawable(R.mipmap.settings1));
//        tvnavpost.setImageDrawable(getResources().getDrawable(R.mipmap.postgrey1));

//
    }
    else if (caseitem == 4) {
        tvnavSearch.setImageDrawable(getResources().getDrawable(R.mipmap.search2));
        tvnavFavourite.setImageDrawable(getResources().getDrawable(R.mipmap.star1));
        tvnavMyprofile.setImageDrawable(getResources().getDrawable(R.mipmap.profilee));
//        tvnavLogout.setImageDrawable(getResources().getDrawable(R.mipmap.logout));
        tvnavSetting.setImageDrawable(getResources().getDrawable(R.mipmap.settings_pink));
//        tvnavpost.setImageDrawable(getResources().getDrawable(R.mipmap.postgrey1));
//
    }



        else {
        tvnavSearch.setImageDrawable(getResources().getDrawable(R.mipmap.search_pink));

//

        }

    }



    //Get - user detail from user API
    public class getUser extends AsyncTask<String, Void, String> {

        ProgressDialog progress;
        String token;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            SharedPreferences prefs = getSharedPreferences("MY_PREFS", MODE_PRIVATE);
            token = prefs.getString("token", "");
            Generalfunction.DisplayLog(token);

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

            //Dismiss - Progress dialog
            progress.dismiss();

            Generalfunction.DisplayLog("onPostExecute: " + result);

            //Parse usermodel
            userModel = ParsingFunctions.parseUserModel(result, thisContext);
            SharedPreferences pre = getSharedPreferences(MyPREFERENCES, MODE_PRIVATE);
            String count = pre.getString("badge", "");
            badge = (TextView) findViewById(R.id.badge);
            if(userModel.un_rated.equals("0")){
                badge.setVisibility(View.GONE);
            }
            else {



                badge.setText(userModel.un_rated);
            }

            if (!userModel.first_name.equals("")) {
                SharedPreferences.Editor editor = getSharedPreferences("MY_PREFS", MODE_PRIVATE).edit();
                editor.putString("first_name", userModel.first_name);
                editor.putString("username",userModel.username);
                editor.commit();
            } else {
                GlobalVar.setMyStringPref(thisContext, Constant.loginUserName, "");
                GlobalVar.setMyStringPref(thisContext, Constant.loginUserfirstName, "");
                GlobalVar.setMyStringPref(thisContext, Constant.loginUserlastName, "");

            }

            new Handler().post(new Runnable() {
                public void run() {
                    try {
                        fragment = new SearchFragment();
                        FragmentManager fragmentManager = getSupportFragmentManager();
                        fragmentManager.beginTransaction().replace(R.id.container, fragment).commit();
                        Log.e("fragment",""+fragment);

                    } catch (Exception e) {
                    }
                }
            });

            DisplayNavigationDetail();

        }
    }

    public void DisplayNavigationDetail() {

//        tv_navUsername.setText("" + GlobalVar.getMyStringPref(thisContext, Constant.loginUserName));
//        tv_navFLname.setText("" + GlobalVar.getMyStringPref(thisContext, Constant.loginUserfirstName) + " " + GlobalVar.getMyStringPref(thisContext, Constant.loginUserlastName));
//
//        Generalfunction.DisplayImage_picasso(GlobalVar.getMyStringPref(thisContext, Constant.loginUserProfilePhoto), thisContext, Constant.case2, ivUserprofile, Constant.Ph_userprofilepic);

        if (fragment instanceof SearchFragment) {
           // fab.setVisibility(View.VISIBLE);

        } else {
           // fab.setVisibility(View.GONE);
        }

        Generalfunction.DisplayLog("DisplayNavigationDetail: fragment: " + fragment);


//        notify=(LayerDrawable)((ImageView) findViewById(R.id.tv_navmyprofile)).getDrawable();
//        setBadgeCount(thisContext,notify,String.valueOf(count));


        if (fragment instanceof SettingsFragment) {
            mBottomNav.setVisibility(View.GONE);
            Generalfunction.colorizeToolbar(toolbar, getResources().getColor(R.color.white), NavigationalSearchActivity.this);
        }
        else if(fragment instanceof FavouritesFragment){
            Generalfunction.colorizeToolbar(toolbar, getResources().getColor(R.color.app_greycolor), NavigationalSearchActivity.this);
        }


        else {
            Generalfunction.colorizeToolbar(toolbar, getResources().getColor(R.color.white), NavigationalSearchActivity.this);
        }
    }

}

