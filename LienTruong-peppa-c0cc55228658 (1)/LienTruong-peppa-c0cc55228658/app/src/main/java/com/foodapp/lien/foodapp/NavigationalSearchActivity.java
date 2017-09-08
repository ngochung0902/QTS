package com.foodapp.lien.foodapp;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.content.pm.Signature;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Parcelable;
import android.provider.MediaStore;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Base64;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.FacebookSdk;
import com.facebook.login.LoginManager;

import java.io.File;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;

import model.UserModel;
import parsing.ParsingFunctions;
import parsing.WebFunctions;
import utility.ConnectionDetector;
import utility.Constant;
import utility.Generalfunction;
import utility.GlobalVar;

public class NavigationalSearchActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, View.OnClickListener {

    private static final int CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE = 100;
    private static final int PERMISSION_REQUEST_CODE = 2;

    private DrawerLayout mDrawerLayout;
    private Toolbar toolbar;
    private TextView tv_navFLname, tv_navUsername;

    private Context mContext;
    private boolean isSearch = true;

    private Boolean doubleBackToExitPressedOnce = false;
    private Uri selectedImageUri, fileUri;
    private String fileName;

    private UserModel userModel;
    private ConnectionDetector cdObj;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mContext = this;

        // set layout
        setContentView(R.layout.activity_navigational_search);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        //Mapping drawer layout
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);

        //Mapping textview (Navigation list)
        TextView tvnavSearch = (TextView) findViewById(R.id.tv_navsearch);
        TextView tvnavMyprofile = (TextView) findViewById(R.id.tv_navmyprofile);
        TextView tvnavFavourite = (TextView) findViewById(R.id.tv_navfavourite);
        TextView tvnavSetting = (TextView) findViewById(R.id.tv_navsetting);
        TextView tvnavLogout = (TextView) findViewById(R.id.tv_navlogout);

        //Mapping drawer layout
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        //Mapping navigation view
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        View headerLayout = navigationView.getHeaderView(0);
        tv_navFLname = (TextView) headerLayout.findViewById(R.id.tv_navFLname);
        tv_navUsername = (TextView) headerLayout.findViewById(R.id.tv_navUsername);

        //Mapping floating action button
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);

        // set text-typeface of navigation list
        Typeface latom = Typeface.createFromAsset(getAssets(), "fonts/lato_medium.ttf");
        tvnavSearch.setTypeface(latom);
        tvnavMyprofile.setTypeface(latom);
        tvnavFavourite.setTypeface(latom);
        tvnavSetting.setTypeface(latom);
        tvnavLogout.setTypeface(latom);

        // set type face of navigation userprofile text
        Typeface playfair = Typeface.createFromAsset(getAssets(), "fonts/playfair.otf");
        tv_navFLname.setTypeface(playfair);
        tv_navUsername.setTypeface(latom);

        //Onclick listener
        tvnavSearch.setOnClickListener(this);
        tvnavMyprofile.setOnClickListener(this);
        tvnavFavourite.setOnClickListener(this);
        tvnavSetting.setOnClickListener(this);
        tvnavLogout.setOnClickListener(this);
        // FAB click listener
        fab.setOnClickListener(this);

        // set fragment using fragment manager
        Fragment fragment = new SearchFragment();
        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction().replace(R.id.container, fragment).commit();

        // Check internet connection and perform operation
        cdObj = new ConnectionDetector(mContext);
        if (cdObj.isConnectingToInternet()) {
            //get user detail useing token
            new getUser().execute();
        } else {
            tv_navUsername.setText(GlobalVar.getMyStringPref(mContext, Constant.loginUserName));
            tv_navFLname.setText(GlobalVar.getMyStringPref(mContext, Constant.loginUserfirstName) + " " + GlobalVar.getMyStringPref(mContext, Constant.loginUserlastName));
            Generalfunction.Simple1ButtonDialog(mContext.getResources().getString(R.string.Internet_Message), mContext);
        }

        // Set backgrund color of toolbar
        toolbar.setBackgroundColor(getResources().getColor(android.R.color.transparent));

        int PERMISSION_ALL = 1;
        String[] PERMISSIONS = {Manifest.permission.READ_CONTACTS, Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.READ_SMS, Manifest.permission.CAMERA,
                Manifest.permission.READ_SMS, Manifest.permission.CALL_PHONE};

        if (!hasPermissions(this, PERMISSIONS)) {
            ActivityCompat.requestPermissions(this, PERMISSIONS, PERMISSION_ALL);
        }

    }



    public static boolean hasPermissions(Context context, String... permissions) {

        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && context != null && permissions != null) {
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

        switch (v.getId()) {

            case R.id.tv_navsearch:
                changeFragment(R.id.tv_navsearch);
                break;

            case R.id.tv_navmyprofile:
                Intent intent = new Intent(mContext, UserProfileActivity.class);
                intent.putExtra(Constant.isMyprofile, true);
                startActivity(intent);
                break;

            case R.id.tv_navfavourite:
                changeFragment(R.id.tv_navfavourite);
                break;

            case R.id.tv_navsetting:
                changeFragment(R.id.tv_navsetting);
                break;

            case R.id.tv_navlogout:
                new AlertDialog.Builder(mContext)
                        .setTitle("Log out?")
                        .setMessage("Are you sure you want to log out of @" + tv_navUsername.getText().toString() + "? Doing this will keep all of your settings same.")
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


                                GlobalVar.clearMyStringPref(mContext);
                                Intent intent = new Intent(NavigationalSearchActivity.this, MainActivity.class);
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
                break;

            case R.id.fab:
                if (ActivityCompat.checkSelfPermission(mContext, Manifest.permission.CAMERA)
                        != PackageManager.PERMISSION_GRANTED) {
                    // Request missing location permission.
                    ActivityCompat.requestPermissions(NavigationalSearchActivity.this,
                            new String[]{Manifest.permission.CAMERA}, PERMISSION_REQUEST_CODE);
                } else {
                    openImageIntent();
                }
                break;
        }
    }



    // Async task for get user detail
    public class getUser extends AsyncTask<String, Void, String> {

        ProgressDialog progress;
        String token;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            SharedPreferences prefs = getSharedPreferences("MY_PREFS", MODE_PRIVATE);
            token = prefs.getString("token", "");
            Log.d("token", token);

            progress = new ProgressDialog(mContext);
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

            //Parse usermodel
            userModel = ParsingFunctions.parseUserModel(result, mContext);

            if (!userModel.first_name.equals("")) {
                SharedPreferences.Editor editor = getSharedPreferences("MY_PREFS", MODE_PRIVATE).edit();
                editor.putString("first_name", userModel.first_name);
                editor.commit();
            }

            Fragment fragment = new SearchFragment();
            FragmentManager fragmentManager = getSupportFragmentManager();
            fragmentManager.beginTransaction()
                    .replace(R.id.container, fragment).commit();

            tv_navUsername.setText(GlobalVar.getMyStringPref(mContext, Constant.loginUserName));
            tv_navFLname.setText(GlobalVar.getMyStringPref(mContext, Constant.loginUserfirstName) + " " + GlobalVar.getMyStringPref(mContext, Constant.loginUserlastName));
        }
    }



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.navigational_search, menu);
        return true;
    }



    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {

        MenuItem itemProfile = menu.findItem(R.id.actionProfileSearch);
        MenuItem itemSettings = menu.findItem(R.id.actionEditSettings);

        itemProfile.setVisible(isSearch);
        itemSettings.setVisible(false);

        return true;
    }



    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();

        if (id == R.id.actionProfileSearch) {
           /* Intent intent = new Intent(NavigationalSearchActivity.this, ProfileActivity.class);
            startActivity(intent);*/
        } else if (id == R.id.actionEditSettings) {
            Intent intent = new Intent("edit_profile");
            LocalBroadcastManager.getInstance(mContext).sendBroadcast(intent);
        }

        return super.onOptionsItemSelected(item);
    }



    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_profile) {
            Fragment fragment = new SettingsFragment();
            FragmentManager fragmentManager = getSupportFragmentManager();
            fragmentManager.beginTransaction()
                    .replace(R.id.container, fragment).commit();

            isSearch = false;
            invalidateOptionsMenu();

        } else if (id == R.id.nav_search) {
            Fragment fragment = new SearchFragment();
            FragmentManager fragmentManager = getSupportFragmentManager();
            fragmentManager.beginTransaction()
                    .replace(R.id.container, fragment).commit();
        } else if (id == R.id.nav_logout) {
            LoginManager.getInstance().logOut();
            SharedPreferences.Editor editor = getSharedPreferences("MY_PREFS", MODE_PRIVATE).edit();
            editor.putString("token", "");
            editor.commit();
            Intent intent = new Intent(NavigationalSearchActivity.this, MainActivity.class);
            startActivity(intent);
            finish();
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;

    }



    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if (requestCode == PERMISSION_REQUEST_CODE) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                openImageIntent();
            } else {
                // Your app will not have this permission. Turn off all functions
                // that require this permission or it will force close like your
                // original question
            }
        }
    }




    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {

            if (requestCode == CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE) {

                boolean isCamera = false;
                if (data == null) {
                    isCamera = true;
                }

                Log.d("navigationResult  ", data + " ");

                selectedImageUri = (data == null ? true : MediaStore.ACTION_IMAGE_CAPTURE.equals(data.getAction())) ? fileUri : (data == null ? null : data.getData());
                Intent intent = new Intent(mContext, PostActivity.class);

                if (selectedImageUri != null) {
                    intent.putExtra("filename", selectedImageUri.toString());

                    intent.putExtra("isCamerabutgallery", false);

                    if (isCamera) {
                        intent.putExtra("isCamerabutgallery", true);
                        Log.d("NAvigation", "onActivityResult: is camera");
                    }

                    intent.putExtra("isCamera", false);
                    Log.d("NAvigation", "onActivityResult: is gallery");


                } else {
                    intent.putExtra("filename", fileUri.getPath());
                    intent.putExtra("isCamera", true);
                    Log.d("NAvigation", "onActivityResult: is camera");
                }
                intent.putExtra("isCameraOrGallery", true);
                startActivity(intent);


                Log.d("FileName", fileUri.getPath().toString());
                //Log.d("Log", " -  " + isCamera);
                if (selectedImageUri != null) {
                    Log.d("File path Url", selectedImageUri.toString());
                }
            }
        }
    }




    private void openImageIntent() {

        // Determine URI of camera image to save.
        final File root = new File(Environment.getExternalStorageDirectory() + File.separator + "FoodApp" + File.separator);
        root.mkdirs();
        String fname = "img_" + System.currentTimeMillis() + ".jpg";
        final File sdImageMainDirectory = new File(root, fname);
        fileUri = Uri.fromFile(sdImageMainDirectory);

        // Camera.
        final List<Intent> cameraIntents = new ArrayList<Intent>();
        final Intent captureIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
        final PackageManager packageManager = getPackageManager();
        final List<ResolveInfo> listCam = packageManager.queryIntentActivities(captureIntent, 0);
        for (ResolveInfo res : listCam) {
            final String packageName = res.activityInfo.packageName;
            final Intent intent = new Intent(captureIntent);
            intent.setComponent(new ComponentName(res.activityInfo.packageName, res.activityInfo.name));
            intent.setPackage(packageName);
            intent.putExtra(MediaStore.EXTRA_OUTPUT, fileUri);
            cameraIntents.add(intent);
        }

        // File System.
        final Intent galleryIntent = new Intent();
        galleryIntent.setType("image/*");
        galleryIntent.setAction(Intent.ACTION_GET_CONTENT);

        // Chooser of file System options.
        final Intent chooserIntent = Intent.createChooser(galleryIntent, "Select Source");

        // Add the camera options.
        chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, cameraIntents.toArray(new Parcelable[cameraIntents.size()]));

        startActivityForResult(chooserIntent, CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE);
    }



    public String getExtension(String path) {
        return path.substring(path.lastIndexOf("/") + 1);
    }



    public void changeFragment(int id) {
        Fragment fragment = null;

        switch (id) {

            case R.id.tv_navfavourite:
                fragment = new FavouritesFragment();
                break;

            case R.id.tv_navsearch:
                fragment = new SearchFragment();
                break;

            case R.id.tv_navmyprofile:
                fragment = new SettingsFragment();
                break;

            case R.id.tv_navsetting:
                fragment = new SettingsFragment();
                break;
        }

        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction()
                .replace(R.id.container, fragment).commit();
        mDrawerLayout.closeDrawers();
        toolbar.setBackgroundColor(getResources().getColor(android.R.color.white));

    }



    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
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
            }, 2000);
        }
    }



    public void DisplayNavigationDetail() {
        tv_navUsername.setText(GlobalVar.getMyStringPref(mContext, Constant.loginUserName));
        tv_navFLname.setText(GlobalVar.getMyStringPref(mContext, Constant.loginUserfirstName) + " " + GlobalVar.getMyStringPref(mContext, Constant.loginUserlastName));
    }



    @Override
    protected void onStart() {
        DisplayNavigationDetail();
        super.onStart();
    }


}

