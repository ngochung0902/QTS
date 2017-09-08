package com.peppa.app.Activity;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LayerDrawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.CountDownTimer;
import android.os.Handler;
import android.provider.MediaStore;
import android.support.design.widget.BottomNavigationView;

import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.login.LoginManager;
import com.peppa.app.Adapter.CustomPagerAdapter;
import com.peppa.app.Adapter.DishAdapter;
import com.peppa.app.Fragment.SettingsFragment;
import com.peppa.app.R;
import com.peppa.app.model.DishModel;
import com.peppa.app.model.RestaurantModel;
import com.peppa.app.model.UserprofileModel;
import com.peppa.app.parsing.BadgeDrawable;
import com.peppa.app.parsing.ParsingFunctions;
import com.peppa.app.parsing.WebFunctions;
import com.peppa.app.utility.ConnectionDetector;
import com.peppa.app.utility.Constant;
import com.peppa.app.utility.Generalfunction;
import com.peppa.app.utility.GlobalVar;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class FavouritesActivity extends AppCompatActivity implements View.OnClickListener {
    private Context thisContext;
    private ProgressDialog progress;
    private ConnectionDetector cdObj;

    private ViewPager mViewPager;
    private NestedScrollView favouriteScroll;
    private RecyclerView mRecyclerView;
    private LinearLayout ll_LoadmoreData;

    private ArrayList<RestaurantModel> restaurants = new ArrayList<RestaurantModel>();
    private ArrayList<DishModel> dishes = new ArrayList<DishModel>();
    RestaurantModel res1=new RestaurantModel();

    private String strFilterdata = "";
    private boolean IsFilter = false;

    //Restaurant page addapter
    private CustomPagerAdapter mCustomPagerAdapter;

    //Dish list addapter
    private DishAdapter mAdapter;

    private int Current_Restaurant_page = 1;
    private boolean isRestaurantLoading = false;

    private int Current_Dish_page = 1;
    private boolean isDishLoading = false;

    boolean isFirsttime = true;
    UserprofileModel usermodel;

    ImageView tvnavSearch, tvnavMyprofile, tvnavFavourite, tvnavSetting, tvnavLogout,tvnavpost;
    private Fragment fragment;
// BottomNavigationView mBottomNav;
    private int caseNumber = 0;
    private Uri fileUri;
    String username;
    private LinearLayoutManager mLinearLayoutManager;
    private LayerDrawable notify;
    int count=2;
    TextView badge;
    SharedPreferences sharedPreferences;
    public static final String MyPREFERENCES = "MyPrefs" ;
    ProgressBar progressBar;
    String activated,Message;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        thisContext = this;
        setContentView(R.layout.activity_favourites);
        cdObj = new ConnectionDetector(thisContext);

        sharedPreferences = getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);

        toolbar.setTitle(getResources().getString(R.string.favoriteTital));
        setSupportActionBar(toolbar);
        toolbar.setTitleTextColor(Color.BLACK);
        toolbar.setBackgroundColor(getResources().getColor(android.R.color.white));


        //  toolbar.setLogo(getResources().getDrawable(R.mipmap.back));

        for (int i = 0; i < toolbar.getChildCount(); i++) {
            View view1 = toolbar.getChildAt(i);
            if (view1 instanceof TextView) {
                TextView textView = (TextView) view1;
                Generalfunction.SetToolbartitalstyle(thisContext, textView, false);  //set font
            }
        }

        //Mapping scrollview
        mViewPager = (ViewPager)findViewById(R.id.pager);
        favouriteScroll = (NestedScrollView) findViewById(R.id.favouriteScroll);


        //Mapping viewpager

        mViewPager.setClipToPadding(false);
        mViewPager.setPadding(110, 0, 110, 0);
        mViewPager.setPageMargin(60);
        mViewPager.setPageMarginDrawable(R.color.colorBacker);

        mRecyclerView = (RecyclerView) findViewById(R.id.linear_recyclerview);
        mRecyclerView.setVisibility(View.VISIBLE);
        progressBar=(ProgressBar)findViewById(R.id.progressBar1);

        //Mapping recycler view

//        mLinearLayoutManager = new LinearLayoutManager(this);
//        mRecyclerView.setLayoutManager(mLinearLayoutManager);

        usermodel=new UserprofileModel();
      //  new VerifyingUser().execute();

        //Mapping textview
//
//        mBottomNav=(BottomNavigationView)findViewById(R.id.navigation);

        //Mapping - Textview (Navigation list)
        tvnavSearch = (ImageView) findViewById(R.id.tv_navsearch);
        tvnavMyprofile = (ImageView) findViewById(R.id.tv_navmyprofile);
        tvnavFavourite = (ImageView) findViewById(R.id.tv_navfavourite);
        tvnavSetting = (ImageView) findViewById(R.id.tv_navsetting);
//        tvnavLogout = (ImageView) findViewById(R.id.tv_navlogout);
        tvnavpost=(ImageView) findViewById(R.id.tv_navpost);

        tvnavSearch.setOnClickListener(this);
        tvnavMyprofile.setOnClickListener(this);
        tvnavFavourite.setOnClickListener(this);
        tvnavSetting.setOnClickListener(this);
//        tvnavLogout.setOnClickListener(this);
        tvnavpost.setOnClickListener(this);


        //check internet connection and get favorite restaurants
        if (cdObj.isConnectingToInternet()) {
            new getFavourites().execute();
        } else {
            Generalfunction.Simple1ButtonDialog(thisContext.getResources().getString(R.string.Internet_Message), thisContext);
        }

        //Refresh global variable value
        GlobalVar.setMyStringPref(thisContext, Constant.Location_Mapview, "");
        Generalfunction.Refreshvaluefalse(thisContext);
        Generalfunction.Refreshvalue_Filter(thisContext);


        //Set - viewpager listener
        mViewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                //Log.d(Constant.TAG, "pager onPageScrolled: "+position);
            }

            @Override
            public void onPageSelected(int position) {
                if (isRestaurantLoading) {
                    if (position + 1 == restaurants.size()) {
                        Current_Restaurant_page = Current_Restaurant_page + 1;
                        new getFavourites().execute();  //Call api for load next item
                    }
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {
                //Log.d(Constant.TAG, "pager onPageScrollStateChanged: "+state);
            }

        });


        //Set - load more data visibility and load data function
        ll_LoadmoreData = (LinearLayout) findViewById(R.id.loadmore);
        ll_LoadmoreData.setVisibility(View.GONE);
        ll_LoadmoreData.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ll_LoadmoreData.setVisibility(View.GONE);
                progressBar.setVisibility(View.VISIBLE);
                new getFavouritesDishes().execute();
//                if (isDishLoading) {
//                    Recycleview_update();
//                }
            }
        });
    }




    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);

        // Inflate the menu;
     getMenuInflater().inflate(R.menu.menu_result, menu);

        // Option menu with it's visisbility logic
        MenuItem itemMap = menu.findItem(R.id.actionMapResult);
        MenuItem itemFilter = menu.findItem(R.id.actionFilterResult);

        if (dishes.size() > 0) {

            itemMap.setVisible(true);
            itemFilter.setVisible(true);
        } else {
            itemMap.setVisible(false);
            if (isFirsttime) {
                itemFilter.setVisible(false);
                isFirsttime = false;
            }
        }
        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        //option menu select item event
        int id = item.getItemId();
        if (id == R.id.actionFilterResult) {
            Intent intent = new Intent(thisContext, FilterActivity.class);
            intent.putExtra(Constant.Filter_Done, "favorite");
            startActivity(intent);      //startActivityForResult(intent, FILTER_REQUEST_CODE);
        }
        if (id == R.id.actionMapResult) {
            if(dishes.size()>0) {
                GlobalVar.setMyStringPref(thisContext, Constant.Location_Mapview, "Favourites");
                Intent intent = new Intent(thisContext, MapsActivity.class);
                intent.putExtra("dishes", dishes);

                startActivity(intent);
            }
        }
        else if (id == android.R.id.home) {

            // Close - Activity
            Intent i=new Intent(thisContext,NavigationalSearchActivity.class);
            startActivity(i);
            finish();

        }
        return super.onOptionsItemSelected(item);
    }


    @Override
    public void onStart() {
        super.onStart();
        if (GlobalVar.getMyBooleanPref(thisContext, Constant.IsScreenRefresh) || GlobalVar.getMyBooleanPref(thisContext, Constant.IsPost_Dish) || GlobalVar.getMyBooleanPref(thisContext, Constant.Filter_FavouriteRefresh)) {
            strFilterdata = GlobalVar.getMyStringPref(thisContext, Constant.Filter_selecteddone);
            IsFilter = true;
            if (Generalfunction.isEmptyCheck(strFilterdata)) {
                strFilterdata = "";
                IsFilter = false;
            }
            Current_Dish_page = 1;
            new getFavouritesDishes().execute();
            Generalfunction.Refreshvaluefalse(thisContext);
        }
        GlobalVar.setMyBooleanPref(thisContext, Constant.Filter_FavouriteRefresh, false);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {

            case R.id.tv_navsearch:
                tvnavSearch.setImageDrawable(getResources().getDrawable(R.mipmap.search_pink));
                tvnavFavourite.setImageDrawable(getResources().getDrawable(R.mipmap.star1));
                tvnavMyprofile.setImageDrawable(getResources().getDrawable(R.mipmap.profilee));
//                tvnavLogout.setImageDrawable(getResources().getDrawable(R.mipmap.logout));
                tvnavSetting.setImageDrawable(getResources().getDrawable(R.mipmap.settings1));
//                tvnavpost.setImageDrawable(getResources().getDrawable(R.mipmap.postgrey1));
                Intent intent2 = new Intent(FavouritesActivity.this, NavigationalSearchActivity.class);
                startActivity(intent2);

                break;

            case R.id.tv_navmyprofile:
                tvnavSearch.setImageDrawable(getResources().getDrawable(R.mipmap.search2));
                tvnavFavourite.setImageDrawable(getResources().getDrawable(R.mipmap.star1));
                tvnavMyprofile.setImageDrawable(getResources().getDrawable(R.mipmap.myprofile_pink));
//                tvnavLogout.setImageDrawable(getResources().getDrawable(R.mipmap.logout));
                tvnavSetting.setImageDrawable(getResources().getDrawable(R.mipmap.settings1));
//                tvnavpost.setImageDrawable(getResources().getDrawable(R.mipmap.postgrey1));
//
                Intent intent1 = new Intent(thisContext, UserProfileActivity.class);
                intent1.putExtra(Constant.isMyprofile, true);
                startActivity(intent1);

                break;

            case R.id.tv_navfavourite:
                tvnavSearch.setImageDrawable(getResources().getDrawable(R.mipmap.search2));
                tvnavFavourite.setImageDrawable(getResources().getDrawable(R.mipmap.favourite_pink));
                tvnavMyprofile.setImageDrawable(getResources().getDrawable(R.mipmap.profilee));
//                tvnavLogout.setImageDrawable(getResources().getDrawable(R.mipmap.logout));
                tvnavSetting.setImageDrawable(getResources().getDrawable(R.mipmap.settings1));
//                tvnavpost.setImageDrawable(getResources().getDrawable(R.mipmap.postgrey1));

                Intent intent4 = new Intent(FavouritesActivity.this, FavouritesActivity.class);
                startActivity(intent4);
                break;

            case R.id.tv_navsetting:
                tvnavSearch.setImageDrawable(getResources().getDrawable(R.mipmap.search2));
                tvnavFavourite.setImageDrawable(getResources().getDrawable(R.mipmap.star1));
                tvnavMyprofile.setImageDrawable(getResources().getDrawable(R.mipmap.profilee));
//                tvnavLogout.setImageDrawable(getResources().getDrawable(R.mipmap.logout));
                tvnavSetting.setImageDrawable(getResources().getDrawable(R.mipmap.settings_pink));
//                tvnavpost.setImageDrawable(getResources().getDrawable(R.mipmap.postgrey1));

                Intent intent3 = new Intent(FavouritesActivity.this, SettingActivity.class);
                startActivity(intent3);
                break;

//            case R.id.tv_navlogout:
//                tvnavSearch.setImageDrawable(getResources().getDrawable(R.mipmap.search2));
//                tvnavFavourite.setImageDrawable(getResources().getDrawable(R.mipmap.star1));
//                tvnavMyprofile.setImageDrawable(getResources().getDrawable(R.mipmap.profilee));
//                tvnavLogout.setImageDrawable(getResources().getDrawable(R.mipmap.logout_pink));
//                tvnavSetting.setImageDrawable(getResources().getDrawable(R.mipmap.settings1));
////                tvnavpost.setImageDrawable(getResources().getDrawable(R.mipmap.postgrey1));
//                SharedPreferences prefs = getSharedPreferences("MY_PREFS", MODE_PRIVATE);
//               username = prefs.getString("username", "");
////
//                new android.support.v7.app.AlertDialog.Builder(thisContext)
//                        .setTitle("Log out?")
//                        .setMessage("Are you sure you want to log out of @" +username + "? Doing this will keep all of your settings same.")
//                        .setPositiveButton("LOG OUT", new DialogInterface.OnClickListener() {
//                            public void onClick(DialogInterface dialog, int which) {
//                                try {
//                                    LoginManager.getInstance().logOut();
//                                } catch (Exception e) {
//                                }
//                                SharedPreferences.Editor editor = getSharedPreferences("MY_PREFS", MODE_PRIVATE).edit();
//                                editor.putString("token", "");
//                                editor.clear();
//                                editor.commit();
//
//                                GlobalVar.clearMyStringPref(thisContext);
//                                Intent intent = new Intent(thisContext, MainActivity.class);
//                                startActivity(intent);
//                                finish();
//                            }
//                        })
//                        .setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
//                            public void onClick(DialogInterface dialog, int which) {
//                                // do nothing
//                            }
//                        })
//                        .show();
//                break;

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
//                                    ActivityCompat.requestPermissions(FavouritesActivity.this, new String[]{Manifest.permission.CAMERA}, Constant.PERMISSION_REQUEST_CODE_Camera);
//                                }
//                                else if (ActivityCompat.checkSelfPermission(thisContext, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
//                                    // Request missing storage permission.
//                                    ActivityCompat.requestPermissions(FavouritesActivity.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, Constant.PERMISSION_REQUEST_CODE_STORAGE);
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
//                else {
//                    if (ActivityCompat.checkSelfPermission(thisContext, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
//                        // Request missing camera permission.
//                        ActivityCompat.requestPermissions(FavouritesActivity.this, new String[]{Manifest.permission.CAMERA}, Constant.PERMISSION_REQUEST_CODE_Camera);
//                    } else if (ActivityCompat.checkSelfPermission(thisContext, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
//                        // Request missing storage permission.
//                        ActivityCompat.requestPermissions(FavouritesActivity.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, Constant.PERMISSION_REQUEST_CODE_STORAGE);
//                    } else {
//                        openImageIntent();
//                    }
//                }

                break;

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

            progress = new ProgressDialog(FavouritesActivity.this);
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
                            ActivityCompat.requestPermissions(FavouritesActivity.this, new String[]{Manifest.permission.CAMERA}, Constant.PERMISSION_REQUEST_CODE_Camera);
                        }
                        else if (ActivityCompat.checkSelfPermission(thisContext, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                            // Request missing storage permission.
                            ActivityCompat.requestPermissions(FavouritesActivity.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, Constant.PERMISSION_REQUEST_CODE_STORAGE);
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
            progress = new ProgressDialog(FavouritesActivity.this);
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


    //Call - Get favourite restaurant API
    public class getFavourites extends AsyncTask<String, Void, String> {
        String token;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            SharedPreferences prefs = thisContext.getSharedPreferences("MY_PREFS", thisContext.MODE_PRIVATE);
            token = prefs.getString("token", "");
            Generalfunction.DisplayLog("token "+ token);

            if (Current_Restaurant_page <= 1) {
                progress = new ProgressDialog(thisContext);
                progress.setMessage("Loading...");
                progress.setCancelable(false);
                progress.show();
            }
        }

        @Override
        protected String doInBackground(String... params) {
            return WebFunctions.getFavouritesRestaurants(token, "favourited_restaurants", Current_Restaurant_page);
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);

            if (s.equals("error")) {
                Snackbar snackbar1 = Snackbar.make(favouriteScroll, "You have no favourites at the moment", Snackbar.LENGTH_LONG);
                snackbar1.show();
            } else {

                isRestaurantLoading = false;
                if (Current_Restaurant_page > 1) {
                    ArrayList<RestaurantModel> arrayList_restaurant = new ArrayList<>();
                    arrayList_restaurant = ParsingFunctions.parseRestaurantArray(s);

                    res1=ParsingFunctions.parseDishModel1(s);
                    badge = (TextView) findViewById(R.id.badge);
                    if(res1.un_rated.equals("0")){
                        badge.setVisibility(View.GONE);
                    }
                    else {



                        badge.setText(res1.un_rated);
                    }
                    SharedPreferences.Editor editor = sharedPreferences.edit();



                    editor.putString("badge", res1.un_rated);
                    editor.commit();

                    if (arrayList_restaurant.size() > 0) {
                        restaurants.addAll(arrayList_restaurant);
                        mCustomPagerAdapter.notifyDataSetChanged();

                        if (arrayList_restaurant.size() >= Constant.Favorite_limit) {
                            isRestaurantLoading = true;
                        }
                    }
                } else {

                    restaurants = ParsingFunctions.parseRestaurantArray(s);
res1=ParsingFunctions.parseDishModel1(s);



                    if (restaurants.size() >= Constant.Favorite_limit) {
                        isRestaurantLoading = true;
                    }

                    loadRestaurantData(restaurants);
                    badge = (TextView) findViewById(R.id.badge);

                    if(res1.un_rated.equals("0")){
                        badge.setVisibility(View.GONE);
                    }
                    else {



                        badge.setText(res1.un_rated);
                    }
                    SharedPreferences.Editor editor = sharedPreferences.edit();



                    editor.putString("badge", res1.un_rated);
                    editor.commit();
                    try {
                        new getFavouritesDishes().execute();
                    } catch (Exception e) {
                    }
                }
            }
        }
    }


    //Call - Get Favourite Dish API
    public class getFavouritesDishes extends AsyncTask<String, Void, String> {
        String token;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            SharedPreferences prefs = thisContext.getSharedPreferences("MY_PREFS", thisContext.MODE_PRIVATE);
            token = prefs.getString("token", "");
            Generalfunction.DisplayLog("token "+ token);

        }

        @Override
        protected String doInBackground(String... params) {
            return WebFunctions.getFavouritesDishes(token, IsFilter, strFilterdata, Current_Dish_page);
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);


            if (!s.equals("error")) {
                ArrayList<DishModel> FavDishlist = new ArrayList<>();
                FavDishlist = ParsingFunctions.parseDishArray(s, thisContext,strFilterdata);
                res1=ParsingFunctions.parseDishModel1(s);
                badge = (TextView) findViewById(R.id.badge);
                if(res1.un_rated.equals("0")){
                    badge.setVisibility(View.GONE);
                }
                else {



                    badge.setText(res1.un_rated);
                }
                SharedPreferences.Editor editor = sharedPreferences.edit();



                editor.putString("badge", res1.un_rated);
                editor.commit();


                //Get Dish arraylist
                if(Current_Dish_page <= 1){
                    dishes = new ArrayList<>();
                }

                if(FavDishlist.size() >0){
                    dishes.addAll(FavDishlist);
                }

                Log.d(Constant.TAG, "onPostExecute: " + Current_Dish_page + "is first time "+isFirsttime);

                if(Current_Dish_page <= 1){

                  supportInvalidateOptionsMenu();
                    mRecyclerView.setLayoutManager(new LinearLayoutManager(thisContext));
                    mAdapter = new DishAdapter(thisContext, mRecyclerView, dishes);
                    mRecyclerView.setAdapter(mAdapter);
                    mRecyclerView.setNestedScrollingEnabled(false);
                }
                else {
                    mAdapter = new DishAdapter(thisContext, mRecyclerView, dishes);
                    mRecyclerView.setAdapter(mAdapter);
                    mAdapter.setLoaded();
                }


                //set load more data function
                ll_LoadmoreData.setVisibility(View.GONE);
                isDishLoading = false;

                if (FavDishlist.size() >= Constant.Favorite_limit) {
                    isDishLoading = true;
                    Current_Dish_page = Current_Dish_page + 1;
                    ll_LoadmoreData.setVisibility(View.VISIBLE);
                }


            }

            //dismiss progress dialog
            if(progress.isShowing()){
                progress.dismiss();
            }
            if(progressBar.getVisibility()==View.VISIBLE){
                progressBar.setVisibility(View.GONE);
            }
        }
    }


    //Display Restaurant
    public void loadRestaurantData(final ArrayList<RestaurantModel> restArray) {
        mCustomPagerAdapter = new CustomPagerAdapter(thisContext, restArray);
        mViewPager.setAdapter(mCustomPagerAdapter);
    }


    //pagination (load next data from API)
    private void Recycleview_update() {
        dishes.add(null);      //Add null value for diaplay Progrss dialog view
        mAdapter.notifyItemInserted(dishes.size() - 1);

        //Load more data for reyclerview
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                try {
                    //Log.e("haint", "Load More 2");
                    dishes.remove(dishes.size() - 1);    //Remove loading item
                    mAdapter.notifyItemRemoved(dishes.size());

                    //Call api for load next item by recycle view
                    new getFavouritesDishes().execute();

                } catch (Exception e) {
                }
            }

        }, 5000);

    }



}