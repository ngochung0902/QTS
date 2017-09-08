package com.peppa.app.Activity;

import android.Manifest;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.ComponentName;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LayerDrawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.provider.MediaStore;
import android.support.annotation.ColorInt;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.CoordinatorLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.KeyCharacterMap;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.Surface;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.AlphaAnimation;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import com.facebook.login.LoginManager;
import com.peppa.app.Fragment.SettingsFragment;
import com.peppa.app.R;
import com.peppa.app.model.DishModel;
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


//@TargetApi(21)
public class UserProfileActivity extends AppCompatActivity implements AppBarLayout.OnOffsetChangedListener, View.OnClickListener {

    //Scroll time animation with image and text
    private static final float PERCENTAGE_TO_SHOW_TITLE_AT_TOOLBAR = 0.9f;
    private static final int ALPHA_ANIMATIONS_DURATION = 200;

    private Context thisContext;
    private ConnectionDetector cdObj;

    private LinearLayout ll_Following;
    private AppBarLayout mAppBarLayout;
    private Toolbar mToolbar;
    private TextView tvTitalname;
    private TextView tvFlname, tvUsername;
    private TextView tvFollowercount, tvFollowingcount;
    private ImageView ivuserCoverphoto, ivuserProfilephoto;
    private ImageView ivUserphoto, ivBack;
    private ImageView ivFilterW, ivSettingW,ivsearch;
    private Button btnFollow, btnUnfollow;
    private RecyclerView mRecyclerView;
    private LinearLayout llFollow;

    private MyAdapter mAdapter;
    TextView badge;

    private Uri fileUri;
    private String strOtheruserId;

    private boolean mIsTheTitleVisible = false;
    private boolean mIsFollowed = false;
    private boolean mIsMAIN = true;

    private UserprofileModel UserprofileModel = new UserprofileModel();
    private ArrayList<DishModel> dishes = new ArrayList<DishModel>();

    private String strFilterdata = "";

    private UserprofileModel DefaultUserprofileModel = new UserprofileModel();

    private boolean flagFilter = false;
    ImageView tvnavSearch, tvnavMyprofile, tvnavFavourite, tvnavSetting, tvnavLogout,tvnavpost;
    private Fragment fragment;
//    BottomNavigationView mBottomNav;
    private int caseNumber = 0;
    String imageUri1;
    private LayerDrawable notify;
    int count=2;
    String un_rated;
    SharedPreferences sharedPreferences;
    public static final String MyPREFERENCES = "MyPrefs" ;

String Message,activated;
    ProgressDialog progress;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_userprofile);

        thisContext = this;


        //inialize connection detector
        cdObj = new ConnectionDetector(thisContext);
      sharedPreferences = getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);


        if (getIntent().getExtras() != null) {
            //check it is a my profile or other profile
            mIsMAIN = getIntent().getExtras().getBoolean(Constant.isMyprofile);   //MisMain = true -> My user profile otherwise other user profile
            imageUri1=getIntent().getStringExtra("image");


        }

        if (!mIsMAIN) {
            strOtheruserId = getIntent().getExtras().getString(Constant.OtheruserId);
            if (strOtheruserId.equalsIgnoreCase(GlobalVar.getMyStringPref(thisContext, Constant.loginUserID))) {
                //Other user id but it's a login id these time display my user profile
                mIsMAIN = true;
            }
        }

        //Mapping Toolbar
        mToolbar = (Toolbar) findViewById(R.id.maintoolbar);

        //Mapping Textview
        tvTitalname = (TextView) findViewById(R.id.tvUnametital);
        tvFlname = (TextView) findViewById(R.id.tvUserflname);
        tvUsername = (TextView) findViewById(R.id.tvUseraddress);
        tvFollowercount = (TextView) findViewById(R.id.tvfollowerscount);
        tvFollowingcount = (TextView) findViewById(R.id.tvfollowingcount);

        //Mapping Linear layout
        ll_Following = (LinearLayout) findViewById(R.id.ll_userpf_following);

        //Mapping Imageview
        ivuserCoverphoto = (ImageView) findViewById(R.id.ivusercoverphoto);
        ivuserProfilephoto = (ImageView) findViewById(R.id.ivuserprofilephoto);
        ivUserphoto = (ImageView) findViewById(R.id.ivUserProfile);
      //  ivBack = (ImageView) findViewById(R.id.ivbackarrow);
        ivFilterW = (ImageView) findViewById(R.id.ivfilterW);
        ivSettingW = (ImageView) findViewById(R.id.ivsettingW);
//        ivsearch=(ImageView) findViewById(R.id.ivsearch);

        //Mapping Button
        btnFollow = (Button) findViewById(R.id.btnfollow);
        btnUnfollow = (Button) findViewById(R.id.btnunfollow);

        //Mapping Appbar Layout
        mAppBarLayout = (AppBarLayout) findViewById(R.id.main_appbar);

        //Mapping Recyclerview
        mRecyclerView = (RecyclerView) findViewById(R.id.rv_mydishes);
        mRecyclerView.setHasFixedSize(true);

        GridLayoutManager gridLayoutManager = new GridLayoutManager(this, 2);
        mRecyclerView.setLayoutManager(gridLayoutManager);

        mToolbar.setTitle("");
        mAppBarLayout.addOnOffsetChangedListener(this);
//
      setSupportActionBar(mToolbar);
//

        //set - font
        Typeface playfair = Typeface.createFromAsset(getAssets(), "fonts/playfair.otf");
        Typeface lato = Typeface.createFromAsset(getAssets(), "fonts/lato.ttf");
        Typeface latob = Typeface.createFromAsset(getAssets(), "fonts/lato_bold.ttf");

        tvTitalname.setTypeface(playfair);
        tvFlname.setTypeface(playfair);
        tvUsername.setTypeface(lato);
        tvFollowercount.setTypeface(latob);
        tvFollowingcount.setTypeface(latob);
        btnFollow.setTypeface(lato);

        tvTitalname.setVisibility(View.GONE);
        ivUserphoto.setVisibility(View.VISIBLE);


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


//        ivBack.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//
//                // Close - Activity
//                finish();
//            }
//        });

        ivFilterW.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                flagFilter = true;

                //Start - Filter Activity
                Intent intent = new Intent(UserProfileActivity.this, FilterActivity.class);
                intent.putExtra(Constant.Filter_Done, "userprofile");
                startActivity(intent);
            }
        });

        ivSettingW.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //Start - Setting Activity
                Intent intent = new Intent(UserProfileActivity.this, SettingActivity.class);
                startActivity(intent);
            }
        });
//        ivsearch.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//
//            }
//        });

        //Mapping Linear layout
        llFollow = (LinearLayout) findViewById(R.id.llFollow);

        //Refresh global value
        Generalfunction.Refreshvaluefalse(thisContext);
        Generalfunction.Refreshvalue_Filter(thisContext);
        GlobalVar.setMyBooleanPref(thisContext, Constant.IsPost_Dish, false);  //It's true when user post Dish from at any place

        //Display setting icon when user profile as a my user profile
        if (!mIsMAIN) {
            ivSettingW.setVisibility(View.GONE);
        } else {
            ivSettingW.setVisibility(View.VISIBLE);
        }
//new VerifyingUser().execute();

        //Call - User profile API
        CallUserwebapi();

        //Set - toolbar tital
        for (int i = 0; i < mToolbar.getChildCount(); i++) {
            View view1 = mToolbar.getChildAt(i);
            if (view1 instanceof TextView) {
                TextView textView = (TextView) view1;
                //set font
                Log.d(Constant.TAG, "User: onCreate: ");
                Generalfunction.SetToolbartitalstyle(thisContext, textView, false);
            }
        }



    }




    @Override
    protected void onStart() {

        //Update - call API for update UI
        GlobalVar.setMyBooleanPref(thisContext, Constant.IsResturant_Userprofile, false);

        if (GlobalVar.getMyBooleanPref(thisContext, Constant.IsScreenRefresh) || GlobalVar.getMyBooleanPref(thisContext, Constant.IsPost_Dish) || GlobalVar.getMyBooleanPref(thisContext, Constant.Filter_FavouriteRefresh)
                || GlobalVar.getMyBooleanPref(thisContext, Constant.Filter_FollowRefresh) || GlobalVar.getMyBooleanPref(thisContext, Constant.Setting_Refresh)) {

            strFilterdata = GlobalVar.getMyStringPref(thisContext, Constant.Filter_selecteddone);


            if (GlobalVar.getMyBooleanPref(thisContext, Constant.Filter_FavouriteRefresh) || GlobalVar.getMyBooleanPref(thisContext, Constant.Filter_FollowRefresh) ||
                    GlobalVar.getMyBooleanPref(thisContext, Constant.Setting_Refresh)) {

                GlobalVar.setMyBooleanPref(thisContext, Constant.Filter_FavouriteRefresh, false);
                GlobalVar.setMyBooleanPref(thisContext, Constant.Setting_Refresh, false);
                //new VerifyingUser().execute();

                CallUserwebapi();
            } else if (flagFilter) {
                CallUserwebapi();
                //FilterUserprofile();
            } else {
                CallUserwebapi();
            }

            Generalfunction.Refreshvaluefalse(thisContext);
        }

        if (GlobalVar.getMyBooleanPref(thisContext, Constant.bitmapToimage)) {
            String file_path = Constant.Filepath_Image;
            File dir = new File(file_path);
            if (dir.exists()) {
                Generalfunction.DeletefIle(dir);
            }
        }

        super.onStart();
    }
    private boolean hasSoftKeys(){
        boolean hasSoftwareKeys = true;

        if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.LOLLIPOP){
            Display d = this.getWindowManager().getDefaultDisplay();

            DisplayMetrics realDisplayMetrics = new DisplayMetrics();
            d.getRealMetrics(realDisplayMetrics);

            int realHeight = realDisplayMetrics.heightPixels;
            int realWidth = realDisplayMetrics.widthPixels;

            DisplayMetrics displayMetrics = new DisplayMetrics();
            d.getMetrics(displayMetrics);

            int displayHeight = displayMetrics.heightPixels;
            int displayWidth = displayMetrics.widthPixels;

            hasSoftwareKeys = (realWidth - displayWidth) > 0 || (realHeight - displayHeight) > 0;
        }else{
            boolean hasMenuKey = ViewConfiguration.get(this).hasPermanentMenuKey();
            boolean hasBackKey = KeyCharacterMap.deviceHasKey(KeyEvent.KEYCODE_BACK);
            hasSoftwareKeys = !hasMenuKey && !hasBackKey;
        }
        return hasSoftwareKeys;
    }

    private Rect resolveNavigationBarDimenIssueAboveLollipop() {
        Rect rect = new Rect(0,0,0,0);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            WindowManager manager = (WindowManager) getSystemService(Context.WINDOW_SERVICE);
            switch (manager.getDefaultDisplay().getRotation()) {
                case Surface.ROTATION_90:
                    rect.right += getNavBarWidth();
                    break;
                case Surface.ROTATION_180:
                    rect.top += getNavBarHeight();
                    break;
                case Surface.ROTATION_270:
                    rect.left += getNavBarWidth();
                    break;
                default:
                    rect.bottom += getNavBarHeight();
            }
        }
        return rect;
    }

    private int getNavBarWidth() {
        return getNavBarDimen("navigation_bar_width");
    }

    private int getNavBarHeight() {
        return getNavBarDimen("navigation_bar_height");
    }

    private int getNavBarDimen(String resourceString) {
        Resources resources = getResources();
        int resourceId = resources.getIdentifier(resourceString, "dimen", "android");
        if (resourceId > 0) {
            return resources.getDimensionPixelSize(resourceId);
        }
        return 0;
    }

    //Call - API
    private void CallUserwebapi() {

        if (cdObj.isConnectingToInternet()) {    // Check internet connection and perform operation
            if (!mIsMAIN) {
                //Call - Other user profile API
                new getUserProfile().execute(strOtheruserId);
            } else {
                //Call - My user profile API
                new getUserProfile().execute("");
            }
        } else {
            Generalfunction.Simple1ButtonDialog(thisContext.getResources().getString(R.string.Internet_Message), thisContext);
        }

    }


    //Handle toolbar visibility with animation
    @Override
    public void onOffsetChanged(AppBarLayout appBarLayout, int offset) {
        int maxScroll = appBarLayout.getTotalScrollRange();
        float percentage = (float) Math.abs(offset) / (float) maxScroll;

        if (percentage >= PERCENTAGE_TO_SHOW_TITLE_AT_TOOLBAR) {

            //Handle toolbar visibility with animation
            if (!mIsTheTitleVisible) {
                startAlphaAnimation(tvTitalname, ALPHA_ANIMATIONS_DURATION, View.VISIBLE);
                mIsTheTitleVisible = true;
                mToolbar.setBackgroundColor(ContextCompat.getColor(UserProfileActivity.this, android.R.color.white));

                invalidateOptionsMenu();

                tvTitalname.setVisibility(View.VISIBLE);
                ivUserphoto.setVisibility(View.VISIBLE);
               // ivBack.setVisibility(View.GONE);
                ivFilterW.setVisibility(View.VISIBLE);
                ivSettingW.setVisibility(View.GONE);
//                ivsearch.setVisibility(View.GONE);
            }

        } else {
            if (mIsTheTitleVisible) {
                startAlphaAnimation(tvTitalname, ALPHA_ANIMATIONS_DURATION, View.INVISIBLE);
                mIsTheTitleVisible = false;
                mToolbar.setBackgroundColor(ContextCompat.getColor(UserProfileActivity.this, android.R.color.transparent));

                invalidateOptionsMenu();

                tvTitalname.setVisibility(View.GONE);
                ivUserphoto.setVisibility(View.VISIBLE);

               // ivBack.setVisibility(View.GONE);
                ivFilterW.setVisibility(View.VISIBLE);
//                ivsearch.setVisibility(View.VISIBLE);

                if (!mIsMAIN) {
                    ivSettingW.setVisibility(View.GONE);
                } else {
                    ivSettingW.setVisibility(View.VISIBLE);
                }
            }
        }
    }


    public static void startAlphaAnimation(View v, long duration, int visibility) {
        AlphaAnimation alphaAnimation = (visibility == View.VISIBLE)
                ? new AlphaAnimation(0f, 1f)
                : new AlphaAnimation(1f, 0f);

        alphaAnimation.setDuration(duration);
       alphaAnimation.setFillAfter(true);
        v.startAnimation(alphaAnimation);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_userprofile, menu);

        MenuItem itemFilterB = menu.findItem(R.id.actionFilterProfileB);
        MenuItem itemSettingB = menu.findItem(R.id.actionsettingB);
//        MenuItem itemsearchB=menu.findItem(R.id.search);


        itemFilterB.setVisible(mIsTheTitleVisible);
        itemSettingB.setVisible(mIsTheTitleVisible);
//        itemsearchB.setVisible(mIsTheTitleVisible);


        if (!mIsMAIN) {
            itemSettingB.setVisible(false);
        }

//        if (dishes.size() == 0) {
//            itemFilterB.setVisible(true);
//        }
        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();

        if (id == R.id.actionFilterProfileB) {
            flagFilter = true;

            //Start - Filter Activity
            Intent intent = new Intent(UserProfileActivity.this, FilterActivity.class);
            intent.putExtra(Constant.Filter_Done, "userprofile");
            startActivity(intent);
        } else if (id == android.R.id.home) {

            // Close - Activity
            finish();

        } else if (id == R.id.actionsettingB) {

            //Start - Setting Activity
            Intent intent = new Intent(UserProfileActivity.this, SettingActivity.class);
            startActivity(intent);
        }
//        else if(id==R.id.search){
//
//        }

        return super.onOptionsItemSelected(item);
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
                Intent intent2 = new Intent(UserProfileActivity.this, NavigationalSearchActivity.class);
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

                Intent intent4 = new Intent(UserProfileActivity.this, FavouritesActivity.class);
                startActivity(intent4);
                break;

            case R.id.tv_navsetting:
                tvnavSearch.setImageDrawable(getResources().getDrawable(R.mipmap.search2));
                tvnavFavourite.setImageDrawable(getResources().getDrawable(R.mipmap.star1));
                tvnavMyprofile.setImageDrawable(getResources().getDrawable(R.mipmap.profilee));
//                tvnavLogout.setImageDrawable(getResources().getDrawable(R.mipmap.logout));
                tvnavSetting.setImageDrawable(getResources().getDrawable(R.mipmap.settings_pink));
//                tvnavpost.setImageDrawable(getResources().getDrawable(R.mipmap.postgrey1));

                Intent intent3 = new Intent(UserProfileActivity.this, SettingActivity.class);
                startActivity(intent3);
                break;

//            case R.id.tv_navlogout:
//                tvnavSearch.setImageDrawable(getResources().getDrawable(R.mipmap.search2));
//                tvnavFavourite.setImageDrawable(getResources().getDrawable(R.mipmap.star1));
//                tvnavMyprofile.setImageDrawable(getResources().getDrawable(R.mipmap.profilee));
//                tvnavLogout.setImageDrawable(getResources().getDrawable(R.mipmap.logout_pink));
//                tvnavSetting.setImageDrawable(getResources().getDrawable(R.mipmap.settings1));
////                tvnavpost.setImageDrawable(getResources().getDrawable(R.mipmap.postgrey1));
//
////
//                new android.support.v7.app.AlertDialog.Builder(thisContext)
//                        .setTitle("Log out?")
//                        .setMessage("Are you sure you want to log out of @" +UserprofileModel.username + "? Doing this will keep all of your settings same.")
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
//                                    ActivityCompat.requestPermissions(UserProfileActivity.this, new String[]{Manifest.permission.CAMERA}, Constant.PERMISSION_REQUEST_CODE_Camera);
//                                }
//                                else if (ActivityCompat.checkSelfPermission(thisContext, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
//                                    // Request missing storage permission.
//                                    ActivityCompat.requestPermissions(UserProfileActivity.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, Constant.PERMISSION_REQUEST_CODE_STORAGE);
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
//                        ActivityCompat.requestPermissions(UserProfileActivity.this, new String[]{Manifest.permission.CAMERA}, Constant.PERMISSION_REQUEST_CODE_Camera);
//                    } else if (ActivityCompat.checkSelfPermission(thisContext, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
//                        // Request missing storage permission.
//                        ActivityCompat.requestPermissions(UserProfileActivity.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, Constant.PERMISSION_REQUEST_CODE_STORAGE);
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

            progress = new ProgressDialog(UserProfileActivity.this);
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
                            ActivityCompat.requestPermissions(UserProfileActivity.this, new String[]{Manifest.permission.CAMERA}, Constant.PERMISSION_REQUEST_CODE_Camera);
                        }
                        else if (ActivityCompat.checkSelfPermission(thisContext, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                            // Request missing storage permission.
                            ActivityCompat.requestPermissions(UserProfileActivity.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, Constant.PERMISSION_REQUEST_CODE_STORAGE);
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
            progress = new ProgressDialog(UserProfileActivity.this);
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



    //Call - User information API
    public class getUserProfile extends AsyncTask<String, Void, String> {

        ProgressDialog progress;
        String token;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            SharedPreferences prefs = getSharedPreferences("MY_PREFS", MODE_PRIVATE);
            token = prefs.getString("token", "");
            Log.d("token", token);

            progress = new ProgressDialog(thisContext);
            progress.setMessage("Loading...");
            progress.setCancelable(false);
            progress.show();
            if(strFilterdata.equals("")){
                strFilterdata="null";
            }
        }

        @Override
        protected String doInBackground(String... params) {
            return WebFunctions.getUserprofile(params[0], token, strFilterdata);
        }

        @Override
        protected void onPostExecute(String rest) {
            super.onPostExecute(rest);

            if (rest.equals("error")) {
                Toast.makeText(thisContext, "It seems there is a problem. Please try again", Toast.LENGTH_SHORT).show();
            } else {
                UserprofileModel = ParsingFunctions.parseUserprofileModel(rest, thisContext,strFilterdata);

                DefaultUserprofileModel = UserprofileModel;


                DisplayUserInformation(UserprofileModel);

                if (mAdapter != null) {
                    dishes.clear();
                    dishes.addAll(UserprofileModel.dishes);
                    mAdapter.notifyDataSetChanged();
                } else {
                    dishes = UserprofileModel.dishes;
                    mAdapter = new MyAdapter(dishes);
                    mRecyclerView.setAdapter(mAdapter);
                }
            }

            if (dishes.size() > 0) {
                ivFilterW.setVisibility(View.VISIBLE);
            } else {
                ivFilterW.setVisibility(View.VISIBLE);
            }

           // FilterUserprofile();

            supportInvalidateOptionsMenu();

            progress.dismiss();
        }
    }


    //Display user information
    public void DisplayUserInformation(final UserprofileModel userprofile) {

        if (!mIsMAIN) {


            if (userprofile.followed_by) {
                btnFollow.setVisibility(View.GONE);
                btnUnfollow.setVisibility(View.VISIBLE);
            } else {
                btnFollow.setVisibility(View.VISIBLE);
                btnUnfollow.setVisibility(View.GONE);
            }

            ll_Following.setVisibility(View.GONE);

            btnFollow.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    new follow_User().execute(userprofile.id, "follow");

                }
            });
            btnUnfollow.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    new follow_User().execute(userprofile.id, "unfollow");

                }
            });

        } else {

            ll_Following.setVisibility(View.VISIBLE);
            btnFollow.setVisibility(View.GONE);
            btnUnfollow.setVisibility(View.GONE);
        }
        un_rated=userprofile.un_rated_dishes;
        badge = (TextView) findViewById(R.id.badge);

        if(un_rated.equals("0")){
            badge.setVisibility(View.GONE);
        }
        else {



            badge.setText(un_rated);
        }
        SharedPreferences.Editor editor = sharedPreferences.edit();



        editor.putString("badge", un_rated);
        editor.commit();

//        notify=(LayerDrawable)((ImageView) findViewById(R.id.tv_navmyprofile)).getDrawable();
////        if(un_rated!=null) {
//        setBadgeCount(thisContext, notify, un_rated);
        //Set text
        tvFollowercount.setText(userprofile.followers_count + " " + thisContext.getResources().getString(R.string.followers));
        tvFlname.setText("" + userprofile.name);
        tvTitalname.setText(userprofile.name);
        tvFollowingcount.setText(userprofile.followings_count + " " + thisContext.getResources().getString(R.string.following));
        tvUsername.setText(userprofile.username.toUpperCase());

        String userCoverphotoUrl = userprofile.cover_image;
        String userprofilephotoUrl = userprofile.profile_image;


//        }

        //Set - cover and profile image
      //  Generalfunction.DisplayImage_picasso(userCoverphotoUrl, thisContext, Constant.case2, ivuserCoverphoto, Constant.Ph_user_coverimage);
        Generalfunction.DisplayImage_picasso(userprofilephotoUrl, thisContext, Constant.case2, ivuserProfilephoto, Constant.Ph_userprofilepic);
        Generalfunction.DisplayImage_picasso(userprofilephotoUrl, thisContext, Constant.case2, ivUserphoto, Constant.Ph_userprofilepic);


        tvFollowercount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //Start - Follow Activity
                Intent intent = new Intent(thisContext, FollowActivity.class);
                if (mIsMAIN) {
                    GlobalVar.setMyStringPref(thisContext, Constant.SelectedID_for_Follow, "");
                    intent.putExtra(Constant.Followers_Name, "");
                } else {
                    GlobalVar.setMyStringPref(thisContext, Constant.SelectedID_for_Follow, strOtheruserId);
                    intent.putExtra(Constant.Followers_Name, userprofile.username);
                }

                GlobalVar.setMyBooleanPref(thisContext, Constant.IsFollow_followers, true);

                intent.putExtra(Constant.IsFollow_User_Following, false);

                startActivity(intent);
            }
        });

        tvFollowingcount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mIsMAIN) {
                    GlobalVar.setMyStringPref(thisContext, Constant.SelectedID_for_Follow, "");
                } else {
                    GlobalVar.setMyStringPref(thisContext, Constant.SelectedID_for_Follow, strOtheruserId);
                }
                GlobalVar.setMyBooleanPref(thisContext, Constant.IsFollow_followers, false);

                //Start - Follow Activity
                Intent intent = new Intent(thisContext, FollowActivity.class);
                intent.putExtra(Constant.IsFollow_User_Following, true);
                startActivity(intent);
            }
        });
    }


    //Load User Dishes
    public class MyAdapter extends RecyclerView.Adapter<MyAdapter.ViewHolder> {

        private ArrayList<DishModel> mDataset;
        int color=Color.parseColor("#1A1A1A");

        public class ViewHolder extends RecyclerView.ViewHolder {

            public TextView ratingGridTv;
            public TextView percentageGridTv;
            public TextView dishNameGridTv;
            public TextView dishVotesGridTv;
            public ImageView rateThisDishImage;
            public ImageView foodImageGrid;
            public ImageView ivQuickPost;
            LinearLayout llRating;


            public ViewHolder(View itemView) {
                super(itemView);

                rateThisDishImage = (ImageView) itemView.findViewById(R.id.rateThisDishImage);
                foodImageGrid = (ImageView) itemView.findViewById(R.id.foodImageGrid);
                ivQuickPost = (ImageView) itemView.findViewById(R.id.postScreenBTN);

                ratingGridTv = (TextView) itemView.findViewById(R.id.ratingGridTv);
                percentageGridTv = (TextView) itemView.findViewById(R.id.percentageGridTv);
                dishNameGridTv = (TextView) itemView.findViewById(R.id.dishNameGridTv);
                dishVotesGridTv = (TextView) itemView.findViewById(R.id.dishVotesGridTv);

                llRating = (LinearLayout) itemView.findViewById(R.id.llrating);
            }
        }

        // Provide a suitable constructor (depends on the kind of dataset)
        public MyAdapter(ArrayList<DishModel> myDataset) {
            mDataset = myDataset;
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            // create a new view
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.grid_list_item, parent, false);
            ViewHolder vh = new ViewHolder(v);
            return vh;
        }

        // Replace the contents of a view (invoked by the layout manager)
        @Override
        public void onBindViewHolder(final ViewHolder holder, final int position) {

            //Set - Display/Hide rate this dish image, my user profile display personal rating and other user profile display dish average rating, Display/Hide quick post image
            if (!mIsMAIN) {

                holder.rateThisDishImage.setVisibility(View.GONE);

                if (!Generalfunction.isEmptyCheck(Generalfunction.Isnull(mDataset.get(position).rating))) {
                    holder.ratingGridTv.setText(mDataset.get(position).rating);
                } else {
                    holder.ratingGridTv.setText("0");
                }

            } else {
                ////if (!mDataset.get(position).average_rating.equals("") && !mDataset.get(position).rating.equals("null") && !Generalfunction.isEmptyCheck(mDataset.get(position).average_rating)) {
                if (!Generalfunction.isEmptyCheck(Generalfunction.Isnull(mDataset.get(position).rating))) {
                    holder.ratingGridTv.setText(mDataset.get(position).rating);
                } else {
                    holder.ratingGridTv.setText("0");
                }
//                Log.e("rating",""+mDataset.get(position).IsRatingGiven);

                if (mDataset.get(position).IsRatingGiven) {
                    holder.rateThisDishImage.setVisibility(View.GONE);
                    holder.llRating.setVisibility(View.VISIBLE);
                    holder.ivQuickPost.setVisibility(View.VISIBLE);
                } else {
                    holder.rateThisDishImage.setVisibility(View.VISIBLE);
                    holder.llRating.setVisibility(View.GONE);
                    holder.ivQuickPost.setVisibility(View.INVISIBLE);
                }
            }

            //Set - rating text style
            Generalfunction.loadFont(thisContext, holder.ratingGridTv, "lato.ttf");

            //Set - name and image
            holder.dishNameGridTv.setText(mDataset.get(position).name);
            String strfoodimage = mDataset.get(position).image;
            Log.e("image",mDataset.get(position).image);
            strfoodimage=Generalfunction.Isnull(mDataset.get(position).image);
            if(!Generalfunction.isEmptyCheck(mDataset.get(position).image))
            {
//
                Generalfunction.DisplayImage_picasso(mDataset.get(position).image, thisContext, Constant.case2, holder.foodImageGrid, Constant.Ph_dish);
           }
            else{
                holder.foodImageGrid.setBackgroundColor(color);
            }
            holder.foodImageGrid.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    //Start - Dish Detail Activity
                    Intent intent = new Intent(UserProfileActivity.this, DetailActivity.class);
                    intent.putExtra(Constant.ID, mDataset.get(position).id);
                    intent.putExtra(Constant.isMyprofile, "profile");
                    intent.putExtra(Constant.OtheruserId,strOtheruserId);
                    startActivity(intent);

                }
            });


            holder.rateThisDishImage.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    //Start - Give Rate Activity
                    Intent intent = new Intent(UserProfileActivity.this, RateActivity.class);
                    intent.putExtra("dishid", mDataset.get(position).id);
                    intent.putExtra("dishname", mDataset.get(position).name);
                    intent.putExtra("dishprice", mDataset.get(position).price);
                    intent.putExtra("restaurantname", mDataset.get(position).rest_name);
                    intent.putExtra("restaurantid", mDataset.get(position).rest_id);
                    intent.putExtra("dishimage", mDataset.get(position).image);
                    intent.putExtra("reposted", mDataset.get(position).IsReposted);
                    intent.putExtra("comment", mDataset.get(position).DishComment);
                    intent.putExtra("rate", mDataset.get(position).rating);
                    intent.putExtra("taglist", mDataset.get(position).dishTaglist);
                    intent.putExtra("imageId", mDataset.get(position).imageId);
//                    intent.putExtra("imageuri",imageUri1);
                    startActivity(intent);

                }
            });

            holder.ivQuickPost.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    GlobalVar.setMyBooleanPref(thisContext, Constant.Isquickpost, true);
                    GlobalVar.setMyStringPref(thisContext, Constant.Post_PreviousScreen, Constant.Screen_UP);

                    //Start - Dish Post Activity
                    Intent intent = new Intent(UserProfileActivity.this, PostActivity_.class);
                    intent.putExtra(Constant.Quick_RestId, mDataset.get(position).rest_id);
                    intent.putExtra(Constant.Quick_RestName, mDataset.get(position).rest_name);
                    intent.putExtra(Constant.Quick_DishName, mDataset.get(position).name);
                    intent.putExtra(Constant.Quick_DishPrice, mDataset.get(position).price);
                    intent.putExtra(Constant.Quick_DishID, mDataset.get(position).id);
                    intent.putExtra(Constant.Quick_Dishimage, mDataset.get(position).image);
                    intent.putExtra(Constant.Quick_RestAddress,mDataset.get(position).rest_address);
                    intent.putExtra("latitude",mDataset.get(position).rest_latitude);
                    intent.putExtra("longitude",mDataset.get(position).rest_longitude);
                    startActivity(intent);

                }
            });

        }

        // Return the size of your dataset (invoked by the layout manager)
        @Override
        public int getItemCount() {
            return mDataset.size();
        }

    }


    //Call Follow/Unfollow user API
    public class follow_User extends AsyncTask<String, Void, String> {

        ProgressDialog progress;
        String token;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            SharedPreferences prefs = getSharedPreferences("MY_PREFS", MODE_PRIVATE);
            token = prefs.getString("token", "");
            Log.d("token", token);

            progress = new ProgressDialog(thisContext);
            progress.setMessage("Loading...");
            progress.setCancelable(false);
            progress.show();
        }

        @Override
        protected String doInBackground(String... params) {
            return WebFunctions.FollowUser(params[0], token, params[1]);
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);

            progress.dismiss();

            if (result.equalsIgnoreCase("true")) {
                Log.d(Constant.TAG, "Filter_FollowRefresh onPostExecute: ");
                GlobalVar.setMyBooleanPref(thisContext, Constant.Filter_FollowRefresh, true);
                CallUserwebapi();
            } else {
                Generalfunction.Simple1ButtonDialog(result, thisContext);
            }

        }
    }


    //Get - Filtered dish Array
    private void FilterDish() {

        try {
            ArrayList<DishModel> Dishlist = new ArrayList<>();

            Dishlist = DefaultUserprofileModel.dishes;

            //Max min price filter
            Dishlist = Generalfunction.PriceFilter(Dishlist, thisContext);

            //Rating filter
            Dishlist = Generalfunction.RatingFilter(Dishlist, thisContext);

            //Tag filter
            Dishlist = TagFilter(Dishlist);

            /* sort by */
            //sort by rating
            if (GlobalVar.getMyStringPref(thisContext, Constant.Filter_sortby).equalsIgnoreCase(getResources().getString(R.string.rating_label))) {
                Collections.sort(Dishlist, Generalfunction.SortComparatorRating);
            }

            //sort by price
            else if (GlobalVar.getMyStringPref(thisContext, Constant.Filter_sortby).equalsIgnoreCase(getResources().getString(R.string.price_label))) {
                Collections.sort(Dishlist, SortComparatorPrice);

            }

            //sort by distance
            else if (GlobalVar.getMyStringPref(thisContext, Constant.Filter_sortby).equalsIgnoreCase(getResources().getString(R.string.distance_label))) {
                Collections.sort(Dishlist, Generalfunction.SortComparatorDistance);
            }

            //show favorite first
            if (GlobalVar.getMyBooleanPref(thisContext, Constant.Filter_isshowfavorite)) {
                Dishlist = Generalfunction.ShowFavoritefirstFilter(Dishlist);
            }

            mAdapter = new MyAdapter(Dishlist);
            mRecyclerView.setAdapter(mAdapter);

        } catch (Exception e) {
            e.printStackTrace();
        }

    }


    //Price Filter
    public static Comparator<DishModel> SortComparatorPrice = new Comparator<DishModel>() {
        @Override
        public int compare(DishModel d1, DishModel d2) {

            Float Sort1 = Float.valueOf(d1.price);
            Float Sort2 = Float.valueOf(d2.price);
            int result = Float.compare(Sort1, Sort2);

            return result;

        }
    };


    //Tag filter
    private ArrayList<DishModel> TagFilter(ArrayList<DishModel> Dishlist) {

        ArrayList<DishModel> arrayfilter = new ArrayList<>();
        boolean IsEntree = GlobalVar.getMyBooleanPref(thisContext, Constant.Filter_isTag_entry);
        boolean IsMain = GlobalVar.getMyBooleanPref(thisContext, Constant.Filter_isTag_main);
        boolean IsDesert = GlobalVar.getMyBooleanPref(thisContext, Constant.Filter_isTag_dessert);
        boolean IsDrink = GlobalVar.getMyBooleanPref(thisContext, Constant.Filter_isTag_drink);

        boolean IsVegetarian = GlobalVar.getMyBooleanPref(thisContext, Constant.Filter_isTag_vegeterian);
        boolean IsVegen = GlobalVar.getMyBooleanPref(thisContext, Constant.Filter_isTag_vegen);
        boolean IsGluten = GlobalVar.getMyBooleanPref(thisContext, Constant.Filter_isTag_gluten);
        String filtertags = GlobalVar.getMyStringPref(thisContext, Constant.Filter_selecteddone1);

        ArrayList filter = new ArrayList();

        ArrayList sample = new ArrayList(Arrays.asList(filtertags.split(",")));
        for (int i = 0; i < sample.size(); i++) {

            filter.add(sample.get(i).toString().toUpperCase());

        }


        if (IsEntree || IsMain || IsDesert || IsDrink || IsVegetarian || IsVegen || IsGluten)
        {

            for (DishModel d : Dishlist)
            {


                String strTagname = "";
                if((IsEntree|| IsMain|| IsDesert||IsDrink)&& IsVegetarian){
                    boolean contains=d.dishTaglist.contains(filter);
                    Log.e("dishes",""+d.dishTaglist);
                    Toast.makeText(getApplicationContext(),""+contains,Toast.LENGTH_SHORT).show();


                    if(contains== true){
                        arrayfilter.add(d);
                        break;
                    }
                }


               else if (IsVegetarian || IsVegen || IsGluten)
                    {


                       if (d.dishTaglist.size()==filter.size())
                        {

                        boolean containsAll = d.dishTaglist.containsAll(filter) && d.dishTaglist.containsAll(filter);
                        boolean compare = d.dishTaglist.equals(filter);


                        if (containsAll == true )
                        {
                            arrayfilter.add(d);
                            break;
                        }
                    }
                }
                else
                    {
                        for (int i = 0; i < d.dishTaglist.size(); i++)
                        {
                         strTagname=d.dishTaglist.get(i).toString();

                            if (IsEntree) {
                                if (Generalfunction.isCompare(strTagname, getResources().getString(R.string.entry_label))) {


                                    arrayfilter.add(d);
                                    break;
                                }
                            }

                            if (IsMain) {
                                if (Generalfunction.isCompare(strTagname, getResources().getString(R.string.Main_label))) {
                                    arrayfilter.add(d);
                                    break;
                                }
                            }

                            if (IsDesert) {
                                if (Generalfunction.isCompare(strTagname, getResources().getString(R.string.Dessert_label))) {
                                    arrayfilter.add(d);
                                    break;
                                }
                            }

                            if (IsDrink) {
                                if (Generalfunction.isCompare(strTagname, getResources().getString(R.string.Drink_label))) {
                                    arrayfilter.add(d);
                                    break;
                                }
                            }





                        }
                    }

            }
        }

         else
        {
           arrayfilter = Dishlist;
        }

        return arrayfilter;
    }


    //Filter dishes acording to selection
    private void FilterUserprofile(){
        if (flagFilter) {
            flagFilter = false;
            if (!Generalfunction.isEmptyCheck(strFilterdata)) {
                FilterDish();
                Log.d(Constant.TAG, "onStart: filter "+strFilterdata);
            }
        }
    }

}