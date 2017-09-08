package com.peppa.app.Activity;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.ProgressDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LayerDrawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.provider.MediaStore;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.login.LoginManager;
import com.peppa.app.Fragment.FavouritesFragment;
import com.peppa.app.Fragment.SearchFragment;
import com.peppa.app.Fragment.SettingsFragment;
import com.peppa.app.R;
import com.peppa.app.listener.OnLoadMoreListener;

import java.util.ArrayList;
import java.util.List;

import com.peppa.app.model.DishModel;
import com.peppa.app.model.RestaurantModel;
import com.peppa.app.model.UserModel;
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

@TargetApi(21)
public class RestaurantProfileActivity extends AppCompatActivity implements AppBarLayout.OnOffsetChangedListener, View.OnClickListener {

    private static final float PERCENTAGE_TO_SHOW_TITLE_AT_TOOLBAR = 0.9f;
    private static final float PERCENTAGE_TO_HIDE_TITLE_DETAILS = 0.3f;

    private boolean mIsTheTitleVisible = false;
    private boolean mIsTheTitleContainerVisible = true;

    private Context thisContext;
    private ConnectionDetector cdObj;

    private LinearLayout ll_followers, ll_following;
    private AppBarLayout mAppBarLayout;
    private Toolbar mToolbar;
    private TextView tvTitalname;
    private TextView tvFlname, tvUseraddress;
    private TextView tvFollowercount, tvFollowingcount;
    private TextView tvrestaurantvotes, tvrestaurantRating;
    private ImageView ivuserCoverphoto;
    private ImageView ivUserphoto, ivBack, ivFilterW, ivCallW, ivFavorite;
    private Button btnFollow, btnUnfollow;
    private RecyclerView mRecyclerView;
    private CollapsingToolbarLayout collapsingToolbarLayout;
    private LinearLayout ll_restDetail;
    private ImageView ivAddresspin;
    private ImageView ivadditem;
    private Uri selectedImageUri, fileUri;

    private DishAdapter mAdapter;

    private RestaurantModel ProfileModel = new RestaurantModel();
    private ArrayList<DishModel> Disheslist = new ArrayList<DishModel>();
    private String strId, strWebmethod = "";
    private String strFilterdata = "";
    private boolean isShowAgain = false;

    //pagination
    private int dish_page = 1;

    private Menu menu;
    int followersCount = 0;
    private int caseNumber = 0;

    private LayerDrawable notify;
    int count=2;
  TextView badge;
    SharedPreferences sharedPreferences;
    public static final String MyPREFERENCES = "MyPrefs" ;

    ImageView tvnavSearch, tvnavMyprofile, tvnavFavourite, tvnavSetting, tvnavLogout,tvnavpost;
String activated,Message;
    ProgressDialog progress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_restaurantprofile);

        thisContext = this;
        sharedPreferences = getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);

        if (getIntent().getExtras() != null) {
            strId = getIntent().getExtras().getString(Constant.ID);   //Get restaurant id from intent
        }

        GlobalVar.setMyStringPref(thisContext, Constant.SelectedID_for_Follow, strId);

        // widget mapping
        Initializewidget();

    }

    private void Initializewidget() {

        //Mapping toolbar
        mToolbar = (Toolbar) findViewById(R.id.maintoolbar);
        mToolbar.setTitle("");
        setSupportActionBar(mToolbar);


        //Mapping textview

//        mBottomNav=(BottomNavigationView)findViewById(R.id.navigation);

        //Mapping - Textview (Navigation list)
        tvnavSearch = (ImageView) findViewById(R.id.tv_navsearch);
        tvnavMyprofile = (ImageView) findViewById(R.id.tv_navmyprofile);
        tvnavFavourite = (ImageView) findViewById(R.id.tv_navfavourite);
        tvnavSetting = (ImageView) findViewById(R.id.tv_navsetting);
//        tvnavLogout = (ImageView) findViewById(R.id.tv_navlogout);
        tvnavpost=(ImageView) findViewById(R.id.tv_navpost);

        tvTitalname = (TextView) findViewById(R.id.tvUnametital);
        tvFlname = (TextView) findViewById(R.id.tvUserflname);
        tvUseraddress = (TextView) findViewById(R.id.tvUseraddress);
        tvFollowercount = (TextView) findViewById(R.id.tvfollowerscount);
        tvFollowingcount = (TextView) findViewById(R.id.tvfollowingcount);
        tvrestaurantvotes = (TextView) findViewById(R.id.tvrestaurantvotes);
        tvrestaurantRating = (TextView) findViewById(R.id.dishRatingDetailTv);

        //Mapping Linear layout
        ll_followers = (LinearLayout) findViewById(R.id.ll_userpf_follow);
        ll_following = (LinearLayout) findViewById(R.id.ll_userpf_following);
        ll_restDetail = (LinearLayout) findViewById(R.id.ll_restDetail);

        //Mapping imageview
        ivuserCoverphoto = (ImageView) findViewById(R.id.ivusercoverphoto);
        ivUserphoto = (ImageView) findViewById(R.id.ivUserProfile);
        ivBack = (ImageView) findViewById(R.id.ivbackarrow);
        ivFilterW = (ImageView) findViewById(R.id.ivfilterW);
        ivCallW = (ImageView) findViewById(R.id.ivCallW);
        ivFavorite = (ImageView) findViewById(R.id.ivFavouriteW);
        ivAddresspin = (ImageView) findViewById(R.id.ivAddresspin);
//        ivadditem = (ImageView) findViewById(R.id.itemadd);

        //Mapping button
        btnFollow = (Button) findViewById(R.id.btnfollow);
        btnUnfollow = (Button) findViewById(R.id.btnunfollow);

        //Mapping appbar layout
        mAppBarLayout = (AppBarLayout) findViewById(R.id.main_appbar);
        mAppBarLayout.addOnOffsetChangedListener(this);

        //Mapping collapsing toolbar layout
        collapsingToolbarLayout = (CollapsingToolbarLayout) findViewById(R.id.collapsing_toolbar);

        //Mapping recycleview
        mRecyclerView = (RecyclerView) findViewById(R.id.rv_mydishes);

        //set font
        Typeface playfair = Typeface.createFromAsset(getAssets(), "fonts/playfair.otf");
        Typeface lato = Typeface.createFromAsset(getAssets(), "fonts/lato.ttf");
        Typeface latob = Typeface.createFromAsset(getAssets(), "fonts/lato_bold.ttf");

        tvTitalname.setTypeface(playfair);
        tvFlname.setTypeface(playfair);
        tvUseraddress.setTypeface(lato);
        tvFollowercount.setTypeface(latob);
        tvFollowingcount.setTypeface(latob);
        btnFollow.setTypeface(lato);

        tvTitalname.setVisibility(View.GONE);
        ivUserphoto.setVisibility(View.GONE);

        ivBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        ivCallW.setOnClickListener(this);
        ivFilterW.setOnClickListener(this);


        tvnavSearch.setOnClickListener(this);
        tvnavMyprofile.setOnClickListener(this);
        tvnavFavourite.setOnClickListener(this);
        tvnavSetting.setOnClickListener(this);
//        tvnavLogout.setOnClickListener(this);
        tvnavpost.setOnClickListener(this);
       // new VerifyingUser().execute();

        // fab.setOnClickListener(this);

        //Set - fragment using fragment manager



        //Refresh global value
        Generalfunction.Refreshvaluefalse(thisContext);
        Generalfunction.Refreshvalue_Filter(thisContext);

        CallRestaurantWebapi();
//        notify=(LayerDrawable)((ImageView) findViewById(R.id.tv_navmyprofile)).getDrawable();
//        setBadgeCount(this,notify, String.valueOf(count));

//        fragment = new SearchFragment();
//        FragmentManager fragmentManager = getSupportFragmentManager();
//        fragmentManager.beginTransaction().replace(R.id.container, fragment).commit();



    }


    @Override
    protected void onStart() {

        GlobalVar.setMyBooleanPref(thisContext, Constant.IsResturant_Userprofile, true);


        //Update - call API for update UI
        if (GlobalVar.getMyBooleanPref(thisContext, Constant.IsScreenRefresh) || GlobalVar.getMyBooleanPref(thisContext, Constant.IsPost_Dish)) {
            strFilterdata = GlobalVar.getMyStringPref(thisContext, Constant.Filter_selecteddone);
            boolean flag = GlobalVar.getMyBooleanPref(thisContext, Constant.Filter_isshowfavorite);
            Generalfunction.DisplayLog("onStart: " + flag);
            isShowAgain = true;

            if (GlobalVar.getMyBooleanPref(thisContext, Constant.IsScreenRefresh)) {
                CallRestaurantWebapi();
            }
            GlobalVar.setMyBooleanPref(thisContext, Constant.IsScreenRefresh, false);
        }
        super.onStart();
    }




    private void CallRestaurantWebapi() {
        //inialize connection detector
        cdObj = new ConnectionDetector(thisContext);

        if (cdObj.isConnectingToInternet()) {
            new getRestaurantProfile().execute(strId);
        } else {
            Generalfunction.Simple1ButtonDialog(thisContext.getResources().getString(R.string.Internet_Message), thisContext);
        }
    }


    //Call - Get Restaurant information API
    public class getRestaurantProfile extends AsyncTask<String, Void, String> {

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
            dish_page = 1;
            return WebFunctions.getRestaurantprofile(params[0], token, strFilterdata, dish_page);
        }

        @Override
        protected void onPostExecute(String rest) {
            super.onPostExecute(rest);

            if (rest.equals("error")) {
                Toast.makeText(thisContext, "It seems there is a problem. Please try again", Toast.LENGTH_SHORT).show();
            } else {
                ProfileModel = ParsingFunctions.parseRestaurantModel(rest, thisContext, strFilterdata);

                loadDataRest(ProfileModel);
                Disheslist = new ArrayList<>();
                Disheslist = ProfileModel.dishes;

                if (Disheslist.size() > 0) {
                    dish_page = dish_page + 1;
                }

                supportInvalidateOptionsMenu();
                Generalfunction.Refreshvaluefalse(thisContext);
            }

            GridLayoutManager gridLayoutManager = new GridLayoutManager(RestaurantProfileActivity.this, 2);
            mRecyclerView.setLayoutManager(gridLayoutManager);

            if (!Generalfunction.isEmptyCheck(strFilterdata)) {
                //show favorite first
                if (GlobalVar.getMyBooleanPref(thisContext, Constant.Filter_isshowfavorite)) {
                    Disheslist = Generalfunction.ShowFavoritefirstFilter(Disheslist);
                }
            }

            mAdapter = new DishAdapter();
            mRecyclerView.setAdapter(mAdapter);

            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (Disheslist.size() >= Constant.RP_dish_limit) {
                        //pagination
                        Recycleview_update();
                    }

                    if (Disheslist.size() == 0) {
                        ivUserphoto.setFocusable(true);
                    }
                }
            });
//

            progress.dismiss();
        }
    }


    //Display Restaurant information
    public void loadDataRest(final RestaurantModel restaurantprofile) {

        if (restaurantprofile.followed_by) {
            btnFollow.setVisibility(View.GONE);
            btnUnfollow.setVisibility(View.VISIBLE);
        } else {
            btnFollow.setVisibility(View.VISIBLE);
            btnUnfollow.setVisibility(View.GONE);
        }

        ll_following.setVisibility(View.GONE);

        try {
            followersCount = Integer.parseInt(restaurantprofile.followers_count);
        } catch (Exception e) {
            followersCount = 0;
        }
        tvFollowercount.setText(followersCount + " " + thisContext.getResources().getString(R.string.followers));
        tvFlname.setText(restaurantprofile.name);
        tvTitalname.setText(restaurantprofile.name);
        tvFollowingcount.setVisibility(View.GONE);
        tvUseraddress.setText(restaurantprofile.address);

        tvrestaurantvotes.setText(restaurantprofile.votes + " " + thisContext.getResources().getString(R.string.votetext));
        tvrestaurantRating.setText(restaurantprofile.rating);
        badge = (TextView) findViewById(R.id.badge);
        if(restaurantprofile.un_rated.equals("0")){
            badge.setVisibility(View.GONE);
        }
        else {



            badge.setText(restaurantprofile.un_rated);
        }
        SharedPreferences.Editor editor = sharedPreferences.edit();



        editor.putString("badge", restaurantprofile.un_rated);
        editor.commit();

        //Display Restaurant image
        String Restaurant_image = restaurantprofile.restaurant_image;
        if (!isShowAgain) {
            Generalfunction.DisplayImage_picasso(Restaurant_image, this, Constant.case1, ivuserCoverphoto, Constant.Ph_restaurant_coverimage);
            Generalfunction.DisplayImage_picasso(Restaurant_image, this, Constant.case1, ivUserphoto, Constant.Ph_restaurant_coverimage);
        }

        //Display favourite/unfavourite image
        if (restaurantprofile.favourited_by) {
            ivFavorite.setImageDrawable(getResources().getDrawable(R.mipmap.favouritefilled_white_action, thisContext.getTheme()));
        } else {
            ivFavorite.setImageDrawable(getResources().getDrawable(R.mipmap.favourite_white_action, thisContext.getTheme()));
        }

        tvFollowercount.setOnClickListener(this);
        ivFavorite.setOnClickListener(this);
        ivAddresspin.setOnClickListener(this);
//        ivadditem.setOnClickListener(this);

        btnFollow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                strWebmethod = "Follow_unflollow";
                new follow_Favourite_Restaurant().execute(restaurantprofile.id, "follow");

            }
        });

        btnUnfollow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                strWebmethod = "Follow_unflollow";
                new follow_Favourite_Restaurant().execute(restaurantprofile.id, "unfollow");
            }
        });

        //Display Phone Image
        Generalfunction.DisplayCall_image_white(ivCallW, ProfileModel.phone_number, thisContext);
    }


    //Click event of widget(view)
    @Override
    public void onClick(View v) {

        switch (v.getId()) {

            case R.id.ivFavouriteW:
                Favourite_Unfavorite();
                break;
            case R.id.tvfollowerscount:
                GlobalVar.setMyBooleanPref(thisContext, Constant.IsFollow_followers, true);
                GlobalVar.setMyStringPref(thisContext, Constant.SelectedID_for_Follow, strId);

                Intent intent = new Intent(thisContext, FollowActivity.class);
                intent.putExtra(Constant.IsFollow_User_Following, false);
                intent.putExtra(Constant.Followers_Name, ProfileModel.name);
                startActivity(intent);
                break;
            case R.id.ivAddresspin:
//                Generalfunction.OpenMap(ProfileModel.latitude, ProfileModel.longitude, thisContext, tvUseraddress.getText().toString());
                Generalfunction.OpenMap(ProfileModel.address,  thisContext, tvUseraddress.getText().toString());
                break;
            case R.id.ivCallW:
                PhoneCall();
                break;
            case R.id.ivfilterW:
                FilterCall();
                break;
//            case R.id.itemadd:
//                caseNumber=0;
//                if (ActivityCompat.checkSelfPermission(thisContext, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
//                    // Request missing camera permission.
//                    ActivityCompat.requestPermissions(RestaurantProfileActivity.this, new String[]{Manifest.permission.CAMERA}, Constant.PERMISSION_REQUEST_CODE_Camera);
//                }
//                else if (ActivityCompat.checkSelfPermission(thisContext, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
//                    // Request missing storage permission.
//                    ActivityCompat.requestPermissions(RestaurantProfileActivity.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, Constant.PERMISSION_REQUEST_CODE_STORAGE);
//                }
//                else {
//                    openImageIntent();
//                }
//                break;
            case R.id.tv_navsearch:
                tvnavSearch.setImageDrawable(getResources().getDrawable(R.mipmap.search_pink));
                tvnavFavourite.setImageDrawable(getResources().getDrawable(R.mipmap.star1));
                tvnavMyprofile.setImageDrawable(getResources().getDrawable(R.mipmap.profilee));
//                tvnavLogout.setImageDrawable(getResources().getDrawable(R.mipmap.logout));
                tvnavSetting.setImageDrawable(getResources().getDrawable(R.mipmap.settings1));
//                tvnavpost.setImageDrawable(getResources().getDrawable(R.mipmap.postgrey1));
                Intent intent2 = new Intent(RestaurantProfileActivity.this, NavigationalSearchActivity.class);
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

                Intent intent4 = new Intent(RestaurantProfileActivity.this, FavouritesActivity.class);
                startActivity(intent4);
                break;

            case R.id.tv_navsetting:
                tvnavSearch.setImageDrawable(getResources().getDrawable(R.mipmap.search2));
                tvnavFavourite.setImageDrawable(getResources().getDrawable(R.mipmap.star1));
                tvnavMyprofile.setImageDrawable(getResources().getDrawable(R.mipmap.profilee));
//                tvnavLogout.setImageDrawable(getResources().getDrawable(R.mipmap.logout));
                tvnavSetting.setImageDrawable(getResources().getDrawable(R.mipmap.settings_pink));
//                tvnavpost.setImageDrawable(getResources().getDrawable(R.mipmap.postgrey1));

                Intent intent3 = new Intent(RestaurantProfileActivity.this, SettingActivity.class);
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
//                username = prefs.getString("username", "");
//
////
//                new AlertDialog.Builder(thisContext)
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
//                                    ActivityCompat.requestPermissions(RestaurantProfileActivity.this, new String[]{Manifest.permission.CAMERA}, Constant.PERMISSION_REQUEST_CODE_Camera);
//                                }
//                                else if (ActivityCompat.checkSelfPermission(thisContext, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
//                                    // Request missing storage permission.
//                                    ActivityCompat.requestPermissions(RestaurantProfileActivity.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, Constant.PERMISSION_REQUEST_CODE_STORAGE);
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
//                        ActivityCompat.requestPermissions(RestaurantProfileActivity.this, new String[]{Manifest.permission.CAMERA}, Constant.PERMISSION_REQUEST_CODE_Camera);
//                    } else if (ActivityCompat.checkSelfPermission(thisContext, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
//                        // Request missing storage permission.
//                        ActivityCompat.requestPermissions(RestaurantProfileActivity.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, Constant.PERMISSION_REQUEST_CODE_STORAGE);
//                    } else {
//                        openImageIntent();
//                    }
//                }

                break;

        }
    }



    //Call - Favourite/unFavourite API
    private void Favourite_Unfavorite() {
        strWebmethod = "Favourite_unfavourite";
        if (ProfileModel.favourited_by) {
            new follow_Favourite_Restaurant().execute(ProfileModel.id, "unfavourite");
        } else {
            new follow_Favourite_Restaurant().execute(ProfileModel.id, "favourite");
        }
    }


    //Make - Call
    private void PhoneCall() {
        Generalfunction.RestaurantCall(ProfileModel.phone_number, thisContext);
    }


    //Satrt - Filter activity
    private void FilterCall() {
        Intent intent = new Intent(RestaurantProfileActivity.this, FilterActivity.class);
        intent.putExtra(Constant.Filter_Done, "Restaurant");
        startActivity(intent);
    }


    @Override
    public void onOffsetChanged(AppBarLayout appBarLayout, int offset) {
        int maxScroll = appBarLayout.getTotalScrollRange();
        float percentage = (float) Math.abs(offset) / (float) maxScroll;

        handleAlphaOnTitle(percentage);
        handleToolbarTitleVisibility(percentage);
    }


    private void handleAlphaOnTitle(float percentage) {
        if (percentage >= PERCENTAGE_TO_HIDE_TITLE_DETAILS) {
            if (mIsTheTitleContainerVisible) {
                mIsTheTitleContainerVisible = false;
            }
        } else {
            if (!mIsTheTitleContainerVisible) {
                mIsTheTitleContainerVisible = true;
            }
        }
    }


    private void handleToolbarTitleVisibility(float percentage) {
        if (percentage >= PERCENTAGE_TO_SHOW_TITLE_AT_TOOLBAR) {
            if (!mIsTheTitleVisible) {
                mIsTheTitleVisible = true;
                mToolbar.setBackgroundColor(ContextCompat.getColor(RestaurantProfileActivity.this, android.R.color.white));
                invalidateOptionsMenu();
            }
        } else {
            if (mIsTheTitleVisible) {
                mIsTheTitleVisible = false;
                mToolbar.setBackgroundColor(ContextCompat.getColor(RestaurantProfileActivity.this, android.R.color.transparent));
                invalidateOptionsMenu();
            }
        }
        if (mIsTheTitleVisible) {
            tvTitalname.setVisibility(View.VISIBLE);
            ivBack.setVisibility(View.GONE);
            ivUserphoto.setVisibility(View.VISIBLE);
            ll_restDetail.setVisibility(View.GONE);
            if (Generalfunction.isEmptyCheck(Generalfunction.Isnull(ProfileModel.restaurant_image))) {
                ivBack.setVisibility(View.VISIBLE);
                ivBack.setImageDrawable(getResources().getDrawable(R.mipmap.back, thisContext.getTheme()));
                ivUserphoto.setVisibility(View.GONE);
            }
            ivFilterW.setVisibility(View.GONE);
            ivCallW.setVisibility(View.GONE);
            ivFavorite.setVisibility(View.GONE);
            ivAddresspin.setVisibility(View.GONE);
//            ivadditem.setVisibility(View.GONE);

        } else {
            ivBack.setImageDrawable(getResources().getDrawable(R.mipmap.back_white, thisContext.getTheme()));
            tvTitalname.setVisibility(View.GONE);
            ivUserphoto.setVisibility(View.GONE);
            ivBack.setVisibility(View.VISIBLE);
            ivFilterW.setVisibility(View.VISIBLE);
            ivCallW.setVisibility(View.VISIBLE);
            ivFavorite.setVisibility(View.VISIBLE);
            ll_restDetail.setVisibility(View.VISIBLE);
            ivAddresspin.setVisibility(View.VISIBLE);
//            ivadditem.setVisibility(View.VISIBLE);

        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        // Inflate the menu;
        getMenuInflater().inflate(R.menu.menu_restaurant, menu);
        MenuItem itemFilterB = menu.findItem(R.id.actionFilterProfileB);
        MenuItem itemcallB = menu.findItem(R.id.actionCallBlack);
        MenuItem itemFavBB = menu.findItem(R.id.actionFavBborder);
        MenuItem itemFavourite = menu.findItem(R.id.actionFav);
        MenuItem itemAddresspin = menu.findItem(R.id.actionAddresspin);
//        MenuItem itemadd=menu.findItem(R.id.action_quickpost);

        //Call
        if (Generalfunction.isEmptyCheck(ProfileModel.phone_number)) {
            itemcallB.setIcon(getResources().getDrawable(R.mipmap.nocall));
        } else {
            itemcallB.setIcon(getResources().getDrawable(R.mipmap.phone));
        }

        if (mIsTheTitleVisible) {
            itemcallB.setVisible(mIsTheTitleVisible);
            itemFilterB.setVisible(mIsTheTitleVisible);
            itemFavourite.setVisible(ProfileModel.favourited_by);
            itemFavBB.setVisible(mIsTheTitleVisible && !ProfileModel.favourited_by);
            itemAddresspin.setVisible(mIsTheTitleVisible);
//            itemadd.setVisible(mIsTheTitleVisible);


        } else {
            itemcallB.setVisible(false);
            itemFilterB.setVisible(false);
            itemFavourite.setVisible(false);
            itemFavBB.setVisible(false);
            itemAddresspin.setVisible(false);
//            itemadd.setVisible(false);
//            itemadd.setVisible(false);

        }

        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.actionFilterProfileB) {
            FilterCall();

        } else if (id == R.id.actionCallBlack) {
            PhoneCall();

        } else if (id == android.R.id.home) {
            finish();
        } else if (id == R.id.actionFav) {
            Favourite_Unfavorite();
        } else if (id == R.id.actionFavBborder) {
            // BUTTON_ACTION = "FAVOURITE";
            Favourite_Unfavorite();
        } else if (id == R.id.actionAddresspin) {
            Generalfunction.OpenMap(ProfileModel.address, thisContext, tvUseraddress.getText().toString());
        }
//        else if(id == R.id.action_quickpost){
//            caseNumber=0;
//
//            if (ActivityCompat.checkSelfPermission(thisContext, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
//                // Request missing camera permission.
//                ActivityCompat.requestPermissions(RestaurantProfileActivity.this, new String[]{Manifest.permission.CAMERA}, Constant.PERMISSION_REQUEST_CODE_Camera);
//            }
//            else if (ActivityCompat.checkSelfPermission(thisContext, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
//                // Request missing storage permission.
//                ActivityCompat.requestPermissions(RestaurantProfileActivity.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, Constant.PERMISSION_REQUEST_CODE_STORAGE);
//            }
//            else {
//                openImageIntent();
//            }
////
//        }

        return super.onOptionsItemSelected(item);
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
                        int position = 0;
                        GlobalVar.setMyBooleanPref(thisContext, Constant.Isquickpost, true);
                        GlobalVar.setMyStringPref(thisContext, Constant.Post_PreviousScreen, Constant.Screen_DishDetail);
                        GlobalVar.setMyStringPref(thisContext,Constant.Peppa_RestID,Disheslist.get(position).rest_id);

                        Intent intent = new Intent(thisContext, PostActivity_.class);
                        intent.putExtra(Constant.Quick_RestId, Disheslist.get(position).rest_id);
                        intent.putExtra(Constant.Quick_RestName, Disheslist.get(position).rest_name);

                        intent.putExtra(Constant.Quick_RestAddress,Disheslist.get(position).rest_address);
                        intent.putExtra("latitude",Disheslist.get(position).rest_latitude);
                        intent.putExtra("longitude",Disheslist.get(position).rest_longitude);
                        intent.putExtra("filename", strFilename);
                        intent.putExtra("isCamera", flagIscamera);
                        intent.putExtra("isCamerabutgallery", iscamerabutgallery);
                        intent.putExtra("isCameraOrGallery", flagIscameragallery);

//



                        startActivity(intent);
                    }

                }

            } catch (Exception e) {
                e.printStackTrace();
            }
        }

    }


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
        int position = 0;
//
            GlobalVar.setMyBooleanPref(thisContext, Constant.Isquickpost, true);
            GlobalVar.setMyStringPref(thisContext, Constant.Post_PreviousScreen, Constant.Screen_DishDetail);
            GlobalVar.setMyStringPref(thisContext,Constant.Peppa_RestID,Disheslist.get(position).rest_id);


            i.putExtra(Constant.Quick_RestId, Disheslist.get(position).rest_id);
       i.putExtra(Constant.Quick_RestName, Disheslist.get(position).rest_name);

        i.putExtra(Constant.Quick_RestAddress,Disheslist.get(position).rest_address);
         i.putExtra("latitude",Disheslist.get(position).rest_latitude);
           i.putExtra("longitude",Disheslist.get(position).rest_longitude);



        Intent[] intentArray = {intent_camrea, i};

        //Add the camera options.
        chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, intentArray);

        startActivityForResult(chooserIntent, Constant.CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE);

    }



    //Call - Make Favorite/Follow restaurant API
    public class follow_Favourite_Restaurant extends AsyncTask<String, Void, String> {

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
            return WebFunctions.follow_Favourite_Restaurant(params[0], token, params[1]);
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);

            progress.dismiss();

            if (result.equalsIgnoreCase("true")) {

                if (strWebmethod.equalsIgnoreCase("Follow_unflollow")) {
                    GlobalVar.setMyBooleanPref(thisContext, Constant.Filter_FollowRefresh, true);

                    Generalfunction.DisplayLog("followers count : " + followersCount);

                    if (btnFollow.getVisibility() == View.VISIBLE) {
                        btnFollow.setVisibility(View.GONE);
                        btnUnfollow.setVisibility(View.VISIBLE);
                        followersCount = followersCount + 1;
                        Generalfunction.DisplayLog("followers count : " + followersCount);
                        tvFollowercount.setText(followersCount + " " + thisContext.getResources().getString(R.string.followers));
                    } else {
                        btnFollow.setVisibility(View.VISIBLE);
                        btnUnfollow.setVisibility(View.GONE);
                        Generalfunction.DisplayLog("followers count unfollow click: " + followersCount);
                        if (followersCount > 0) {
                            followersCount = followersCount - 1;
                        }
                        Generalfunction.DisplayLog("followers count : " + followersCount);
                        tvFollowercount.setText(followersCount + " " + thisContext.getResources().getString(R.string.followers));
                    }
                } else if (strWebmethod.equalsIgnoreCase("Favourite_unfavourite")) {
                    GlobalVar.setMyBooleanPref(thisContext, Constant.Filter_FavouriteRefresh, true);
                    if (ProfileModel.favourited_by) {
                        ProfileModel.favourited_by = false;
                    } else {
                        ProfileModel.favourited_by = true;
                       Toast.makeText(thisContext, "Added to favourites", Toast.LENGTH_SHORT).show();
                    }

                    if (ProfileModel.favourited_by) {
                        ivFavorite.setImageDrawable(getResources().getDrawable(R.mipmap.favouritefilled_white_action, thisContext.getTheme()));
                    } else {
                        ivFavorite.setImageDrawable(getResources().getDrawable(R.mipmap.favourite_white_action, thisContext.getTheme()));
                    }

                    supportInvalidateOptionsMenu();


                }
            } else {
                Generalfunction.Simple1ButtonDialog(result, thisContext);
            }
        }
    }



    /*
     * Display Dish list with addapter - Start
     */

    //Dish view
    public class DishViewHolder extends RecyclerView.ViewHolder {
        public TextView ratingGridTv;
        public TextView percentageGridTv;
        public TextView dishNameGridTv;
        public TextView dishVotesGridTv;
        public ImageView rateThisDishImage;
        public ImageView foodImageGrid;
        public LinearLayout favouriteLLT;
        public ImageView ivQuickPost;
        public ImageView addnew;

        public DishViewHolder(View view) {
            super(view);

            rateThisDishImage = (ImageView) itemView.findViewById(R.id.rateThisDishImage);
            foodImageGrid = (ImageView) itemView.findViewById(R.id.foodImageGrid);
            ratingGridTv = (TextView) itemView.findViewById(R.id.ratingGridTv);
            percentageGridTv = (TextView) itemView.findViewById(R.id.percentageGridTv);
            dishNameGridTv = (TextView) itemView.findViewById(R.id.dishNameGridTv);
            dishVotesGridTv = (TextView) itemView.findViewById(R.id.dishVotesGridTv);
            favouriteLLT = (LinearLayout) itemView.findViewById(R.id.favouriteLLT);
            ivQuickPost = (ImageView) itemView.findViewById(R.id.postScreenBTN);
            addnew = (ImageView) itemView.findViewById(R.id.addnew);
        }
    }


    //Loading view
    static class LoadingViewHolder extends RecyclerView.ViewHolder {
        public ProgressBar progressBar;

        public LoadingViewHolder(View itemView) {
            super(itemView);
            progressBar = (ProgressBar) itemView.findViewById(R.id.progressBar1);
        }
    }


    //Dish adapter which display Dish view and loading view
    class DishAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

        private final int VIEW_TYPE_ITEM = 0;
        private final int VIEW_TYPE_LOADING = 1;

        private OnLoadMoreListener mOnLoadMoreListener;
        private boolean isLoading;
        private int visibleThreshold = 5;
        private int lastVisibleItem, totalItemCount;

        public DishAdapter() {

            final GridLayoutManager gridLayoutManager = (GridLayoutManager) mRecyclerView.getLayoutManager();

            mRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
                @Override
                public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                    super.onScrolled(recyclerView, dx, dy);

                    totalItemCount = gridLayoutManager.getItemCount();
                    lastVisibleItem = gridLayoutManager.findLastVisibleItemPosition();

                    if (!isLoading && totalItemCount <= (lastVisibleItem + visibleThreshold)) {
                        if (mOnLoadMoreListener != null) {
                            mOnLoadMoreListener.onLoadMore();
                        }
                        isLoading = true;
                    }
                }
            });
        }

        public void setOnLoadMoreListener(OnLoadMoreListener mOnLoadMoreListener) {
            this.mOnLoadMoreListener = mOnLoadMoreListener;
        }

        @Override
        public int getItemViewType(int position) {
            return Disheslist.get(position) == null ? VIEW_TYPE_LOADING : VIEW_TYPE_ITEM;//mUsers.get(position) == null ? VIEW_TYPE_LOADING : VIEW_TYPE_ITEM;
        }

        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            if (viewType == VIEW_TYPE_ITEM) {
                View view = LayoutInflater.from(RestaurantProfileActivity.this).inflate(R.layout.grid_list_item, parent, false);
                return new DishViewHolder(view);
            } else if (viewType == VIEW_TYPE_LOADING) {
                View view = LayoutInflater.from(RestaurantProfileActivity.this).inflate(R.layout.layout_loading_item, parent, false);
                return new LoadingViewHolder(view);
            }
            return null;
        }

        @Override
        public void onBindViewHolder(final RecyclerView.ViewHolder holder, final int position) {
            if (holder instanceof DishViewHolder) {

                final DishViewHolder dishViewHolder = (DishViewHolder) holder;

                dishViewHolder.rateThisDishImage.setVisibility(View.GONE);
                dishViewHolder.dishNameGridTv.setText(Disheslist.get(position).name);

                Generalfunction.loadFont(thisContext, dishViewHolder.ratingGridTv, "lato.ttf");
                if (!Disheslist.get(position).average_rating.equals("")) {
                    dishViewHolder.ratingGridTv.setText(Disheslist.get(position).average_rating);
                } else {
                    dishViewHolder.ratingGridTv.setText("0");
                }

                String strfoodimage = Disheslist.get(position).image;
                Generalfunction.DisplayImage_picasso(strfoodimage, thisContext, Constant.case2, dishViewHolder.foodImageGrid, Constant.Ph_dish);

                dishViewHolder.rateThisDishImage.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(RestaurantProfileActivity.this, RateActivity.class);
                        startActivity(intent);
                    }
                });




                dishViewHolder.foodImageGrid.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(RestaurantProfileActivity.this, DetailActivity.class);
                        intent.putExtra(Constant.Screen_Previous, Constant.Screen_RP);
                        intent.putExtra(Constant.ID, Disheslist.get(position).id);
                        intent.putExtra(Constant.isMyprofile,"restaurant");
                        intent.putExtra("results","results");
                        startActivity(intent);
                    }
                });

                dishViewHolder.ivQuickPost.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        GlobalVar.setMyBooleanPref(thisContext, Constant.Isquickpost, true);
                        GlobalVar.setMyStringPref(thisContext, Constant.Post_PreviousScreen, Constant.Screen_RP);
                        GlobalVar.setMyStringPref(thisContext, Constant.Peppa_RestID, Disheslist.get(position).rest_id);

                        Intent intent = new Intent(RestaurantProfileActivity.this, PostActivity_.class);
                        intent.putExtra(Constant.Quick_RestId, Disheslist.get(position).rest_id);
                        intent.putExtra(Constant.Quick_RestName, Disheslist.get(position).rest_name);
                        intent.putExtra(Constant.Quick_DishName, Disheslist.get(position).name);
                        intent.putExtra(Constant.Quick_DishPrice, Disheslist.get(position).price);
                        intent.putExtra(Constant.Quick_DishID, Disheslist.get(position).id);


                        intent.putExtra(Constant.Quick_Dishimage, Disheslist.get(position).image);
                        intent.putExtra(Constant.Quick_RestAddress,Disheslist.get(position).rest_address);

                        intent.putExtra("latitude",Disheslist.get(position).rest_latitude);
                        intent.putExtra("longitude",Disheslist.get(position).rest_longitude);
                        startActivity(intent);

                    }
                });
            } else if (holder instanceof LoadingViewHolder) {
                LoadingViewHolder loadingViewHolder = (LoadingViewHolder) holder;
                loadingViewHolder.progressBar.setIndeterminate(true);
            }
        }

        @Override
        public int getItemCount() {
            return Disheslist == null ? 0 : Disheslist.size();//mUsers == null ? 0 : mUsers.size();
        }

        public void setLoaded() {
            isLoading = false;
        }
    }


    /*
     * Display Dish list with addapter - End
     */


    public class VerifyingUser extends AsyncTask<String, Void, String> {

        ProgressDialog progress;
        String token;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            SharedPreferences prefs = getSharedPreferences("MY_PREFS", MODE_PRIVATE);

            token = prefs.getString("token", "");
            Log.d("token", token);

            progress = new ProgressDialog(RestaurantProfileActivity.this);
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
                            ActivityCompat.requestPermissions(RestaurantProfileActivity.this, new String[]{Manifest.permission.CAMERA}, Constant.PERMISSION_REQUEST_CODE_Camera);
                        }
                        else if (ActivityCompat.checkSelfPermission(thisContext, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                            // Request missing storage permission.
                            ActivityCompat.requestPermissions(RestaurantProfileActivity.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, Constant.PERMISSION_REQUEST_CODE_STORAGE);
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
            progress = new ProgressDialog(RestaurantProfileActivity.this);
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


    private void Recycleview_update() {

        //pagination (load next data from API)
        mAdapter.setOnLoadMoreListener(new OnLoadMoreListener() {
            @Override
            public void onLoadMore() {

                //Add null value for diaplay Progrss dialog view
                Disheslist.add(null);
                mAdapter.notifyItemInserted(Disheslist.size() - 1);

                //Load more data for reyclerview
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {

                        try {

                            Log.d("haint", "Load More 2");
                            //Remove loading item
                            Disheslist.remove(Disheslist.size() - 1);
                            mAdapter.notifyItemRemoved(Disheslist.size());

                            //Call api for load next dish item
                            new getRestaurantProfile_Next().execute(strId);

                        } catch (Exception e) {
                            //Log.d("onscroll ", "run() returned: " + e);
                        }
                    }

                }, 5000);
            }
        });
    }


    //Call - Get next Restaurant dishes API
    public class getRestaurantProfile_Next extends AsyncTask<String, Void, String> {

        String token;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            SharedPreferences prefs = thisContext.getSharedPreferences("MY_PREFS", thisContext.MODE_PRIVATE);
            token = prefs.getString("token", "");
            Log.d("token", token);
        }

        @Override
        protected String doInBackground(String... params) {
            return WebFunctions.getRestaurantprofile(params[0], token, strFilterdata, dish_page);
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);

            if (s.equals("error")) {
                Toast.makeText(RestaurantProfileActivity.this, "error", Toast.LENGTH_SHORT).show();

            } else {

                ArrayList<DishModel> Arraylist_dish = new ArrayList<>();
                ProfileModel = ParsingFunctions.parseRestaurantModel(s, thisContext, strFilterdata);
                Arraylist_dish = ProfileModel.dishes;


                if (Arraylist_dish.size() > 0) {

                    dish_page = dish_page + 1;
                    Disheslist.addAll(Arraylist_dish);

                    mAdapter.notifyDataSetChanged();
                    mAdapter.setLoaded();
                }
                else{


                }

            }
        }

    }


}