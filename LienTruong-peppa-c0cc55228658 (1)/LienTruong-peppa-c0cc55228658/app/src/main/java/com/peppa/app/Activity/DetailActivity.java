package com.peppa.app.Activity;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
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
import android.provider.MediaStore;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import com.facebook.login.LoginManager;
import com.kassisdion.library.ViewPagerWithIndicator;
import com.peppa.app.Fragment.SettingsFragment;
import com.peppa.app.R;
import com.peppa.app.model.DishCommentModel;
import com.peppa.app.model.DishImageArray;
import com.peppa.app.model.DishModel;
import com.peppa.app.model.UserModel;
import com.peppa.app.parsing.BadgeDrawable;
import com.peppa.app.parsing.ParsingFunctions;
import com.peppa.app.parsing.WebFunctions;
import com.peppa.app.utility.ConnectionDetector;
import com.peppa.app.utility.Constant;
import com.peppa.app.utility.Generalfunction;
import com.peppa.app.utility.GlobalVar;
import com.sembozdemir.viewpagerarrowindicator.library.ViewPagerArrowIndicator;

import org.json.JSONException;
import org.json.JSONObject;


public class DetailActivity extends AppCompatActivity implements View.OnClickListener, AppBarLayout.OnOffsetChangedListener {

    Context thisContext;
    ConnectionDetector cdObj;

    private TextView dishName, restaurantName, dishRating, votes, description, address, callView, comment, dishPriceTv;
    private LinearLayout commentsLLT;
    private ViewPager viewPager;
    private ImageView ivVegeterian, ivVegen, ivGluten;
    private ImageView ivEntree, ivMain, ivDesert, ivDrink;

    DishModel dish = new DishModel();
    private String strID = "",strOtherusrid;
String profile="";
    private ImageView ivcallRestaurant;
    private LinearLayout ll_LoadmoreData;

    private int Current_DishComment_page = 1;
    private boolean isDishCommentLoading = false;
    private LayoutInflater mlayoutInflater;

    private boolean isFromRestaurantProfile=false;
    private ViewPagerArrowIndicator viewPagerArrowIndicator;
    private ViewPagerWithIndicator mViewPagerWithIndicator;
    ImageView leftNav;
    ImageView rightNav;
    ImageView tvnavSearch, tvnavMyprofile, tvnavFavourite, tvnavSetting, tvnavLogout,tvnavpost,mapaddress;
    private Fragment fragment;
    BottomNavigationView mBottomNav;
    private int caseNumber = 0;
    private Uri fileUri;
  String username;
    private static final float PERCENTAGE_TO_SHOW_TITLE_AT_TOOLBAR = 0.9f;
    private static final float PERCENTAGE_TO_HIDE_TITLE_DETAILS = 0.3f;

    private boolean mIsTheTitleVisible = false;
    private boolean mIsTheTitleContainerVisible = true;
    private AppBarLayout mAppBarLayout;
    private Toolbar mToolbar;
    private CollapsingToolbarLayout collapsingToolbarLayout;
    private ImageView ivpost, ivBack, ivtrash, ivrest, ivFavorite,ivFavouritefi;
    private TextView tvTitalname;
    private LayerDrawable notify;
    TextView badge;
    SharedPreferences sharedPreferences;
    public static final String MyPREFERENCES = "MyPrefs" ;
String view;
    String activated,Message;
    ProgressDialog progress;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);
        thisContext = this;
        sharedPreferences = getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);




        //Set - Toolbar
        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        mToolbar.setTitle("");
        setSupportActionBar(mToolbar);
        getSupportActionBar().setHomeAsUpIndicator(R.mipmap.back);
        //getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        //Define connection detactor
        cdObj = new ConnectionDetector(thisContext);


        //Mapping - Linear
        commentsLLT    = (LinearLayout) findViewById(R.id.dishCommentsLLT);

        //Mapping - Textview
        dishName         = (TextView) findViewById(R.id.dishNameDetailTv);
        restaurantName   = (TextView) findViewById(R.id.restaurantNameDetailTv);
        dishRating       = (TextView) findViewById(R.id.dishRatingDetailTv);
        votes            = (TextView) findViewById(R.id.voteCountDetailTv);
        description      = (TextView) findViewById(R.id.descriptionTv);
//        address          = (TextView) findViewById(R.id.addressLinkDetailTv);
//        callView         = (TextView) findViewById(R.id.callLinkDetailTv);
        comment          = (TextView) findViewById(R.id.commentTv);
        dishPriceTv      = (TextView) findViewById(R.id.dishPriceTv);
mapaddress=(ImageView)findViewById(R.id.mapImageDetail);
        //Mapping - Viewpager


        viewPager        = (ViewPager) findViewById(R.id.viewpager);

        viewPager.setAdapter(new DetailImageAdapter(thisContext));


        mBottomNav=(BottomNavigationView)findViewById(R.id.navigation);

        //Mapping - Textview (Navigation list)
        tvnavSearch = (ImageView) findViewById(R.id.tv_navsearch);
        tvnavMyprofile = (ImageView) findViewById(R.id.tv_navmyprofile);
        tvnavFavourite = (ImageView) findViewById(R.id.tv_navfavourite);
        tvnavSetting = (ImageView) findViewById(R.id.tv_navsetting);
        tvnavpost=(ImageView)findViewById(R.id.tv_navpost);
        tvTitalname = (TextView) findViewById(R.id.tvUnametital);
        ivBack = (ImageView) findViewById(R.id.ivbackarrow);
//        ivpost = (ImageView) findViewById(R.id.ivpost);
        ivrest = (ImageView) findViewById(R.id.ivrest);
        ivFavorite = (ImageView) findViewById(R.id.ivFavourite);
       // ivFavouritefi = (ImageView) findViewById(R.id.ivFavouritefi);
        ivtrash = (ImageView) findViewById(R.id.ivtrash);


        //Mapping collapsing toolbar layout
        mAppBarLayout = (AppBarLayout) findViewById(R.id.appbar);
        mAppBarLayout.addOnOffsetChangedListener(this);
        tvTitalname.setVisibility(View.GONE);
        tvnavSearch.setOnClickListener(this);
        tvnavMyprofile.setOnClickListener(this);
        tvnavFavourite.setOnClickListener(this);
        tvnavSetting.setOnClickListener(this);
        tvnavpost.setOnClickListener(this);
//        ivpost.setOnClickListener(this);
        ivrest.setOnClickListener(this);
        ivFavorite.setOnClickListener(this);
        ivtrash.setOnClickListener(this);
     mapaddress.setOnClickListener(this);
        ivBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });


//        tvnavLogout.setOnClickListener(this);







        //Mapping - Imageview
        ivVegeterian     = (ImageView) findViewById(R.id.ivvegeterian);
        ivVegen          = (ImageView) findViewById(R.id.ivvegen);
        ivGluten         = (ImageView) findViewById(R.id.ivgluten);

        ivEntree         = (ImageView) findViewById(R.id.iventree);
        ivMain           = (ImageView) findViewById(R.id.ivmain);
        ivDesert         = (ImageView) findViewById(R.id.ivdesert);
        ivDrink          = (ImageView) findViewById(R.id.ivdrink);

        ivcallRestaurant = (ImageView) findViewById(R.id.ivcallRestaurant);
        ivcallRestaurant.setOnClickListener(this);

        //Set - Text Typeface
        Typeface playfair = Typeface.createFromAsset(getAssets(), "fonts/playfair.otf");
        Typeface lato     = Typeface.createFromAsset(getAssets(), "fonts/lato.ttf");
        dishName.setTypeface(playfair);
        restaurantName.setTypeface(lato);
        dishRating.setTypeface(lato);
        votes.setTypeface(lato);
        description.setTypeface(lato);
//        address.setTypeface(lato);
//        callView.setTypeface(lato);
        comment.setTypeface(lato);

        //Set - Image Visibility for dish tag (type) is re-set
        ivVegeterian.setVisibility(View.GONE);
        ivVegen.setVisibility(View.GONE);
        ivGluten.setVisibility(View.GONE);
        ivEntree.setVisibility(View.GONE);
        ivMain.setVisibility(View.GONE);
        ivDesert.setVisibility(View.GONE);
        ivDrink.setVisibility(View.GONE);
      //  new VerifyingUser().execute();

        if (getIntent().getExtras() != null) {

            //Get - Dish Id
            strID = getIntent().getExtras().getString(Constant.ID);
            profile=getIntent().getExtras().getString(Constant.isMyprofile);
            strOtherusrid=getIntent().getExtras().getString(Constant.OtheruserId);
            view=getIntent().getExtras().getString("results");

            try {
                String str = getIntent().getExtras().getString(Constant.Screen_Previous);
                if(Generalfunction.isCompare(Constant.Screen_RP,str)){
                    isFromRestaurantProfile=true;
                }
            }catch(Exception e){
                Log.d(Constant.TAG, "Exception: detail intent get extra ");
            }

        }


        //Check - Internet Connection
        if (cdObj.isConnectingToInternet()) {

            //Get - Dish Detail
            new getDish().execute();

        } else {
            Generalfunction.Simple1ButtonDialog(thisContext.getResources().getString(R.string.Internet_Message), thisContext);
        }


        //Set - Pagination for comments
        ll_LoadmoreData = (LinearLayout)findViewById(R.id.loadmore);
        ll_LoadmoreData.setVisibility(View.GONE);
        ll_LoadmoreData.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ll_LoadmoreData.setVisibility(View.GONE);
                if(isDishCommentLoading){
                    Current_DishComment_page=Current_DishComment_page+1;
                    if (cdObj.isConnectingToInternet()) {
                        new getDish().execute();
                    } else {
                        Generalfunction.Simple1ButtonDialog(thisContext.getResources().getString(R.string.Internet_Message), thisContext);
                    }
                }
            }
        });

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_detail, menu);

        MenuItem itemFavorite = menu.findItem(R.id.favouriteDetail);
        MenuItem itemFavoritefilled = menu.findItem(R.id.favouriteFilledDetail);
        MenuItem itemTrashB=menu.findItem(R.id.actionTrash);
        MenuItem itemres=menu.findItem(R.id.actiondetail);
        MenuItem itemcallB = menu.findItem(R.id.actionCallBlack);
        MenuItem itemAddresspin = menu.findItem(R.id.actionAddresspin);
//        MenuItem itemadd=menu.findItem(R.id.action_quickpost);
        if (Generalfunction.isEmptyCheck(dish.rest_phone_number)) {
            itemcallB.setIcon(getResources().getDrawable(R.mipmap.nocall));
        } else {
            itemcallB.setIcon(getResources().getDrawable(R.mipmap.phone));
        }
        if(mIsTheTitleVisible) {

            Intent intent = getIntent();
            if (profile != null) {
                if (intent.getExtras().getString(Constant.isMyprofile).equalsIgnoreCase("profile")) {
                    if (strOtherusrid != null) {
                        if (strOtherusrid.equalsIgnoreCase(GlobalVar.getMyStringPref(thisContext, Constant.loginUserID))) {
                            itemTrashB.setVisible(true);
                        } else {
                            itemTrashB.setVisible(false);
                        }

                    }

                } else {
                    itemTrashB.setVisible(false);
                }
            }

            if (dish.favourited_by) {
                itemFavorite.setVisible(false);
                itemFavoritefilled.setVisible(true);
            } else {
                itemFavorite.setVisible(true);
                itemFavoritefilled.setVisible(false);
            }
            itemcallB.setVisible(mIsTheTitleVisible);
            itemAddresspin.setVisible(mIsTheTitleVisible);
        }
        else{
            itemFavorite.setVisible(false);
            itemres.setVisible(false);
            itemFavoritefilled.setVisible(false);
            itemTrashB.setVisible(false);
            itemAddresspin.setVisible(false);
            itemcallB.setVisible(false);
//            itemadd.setVisible(false);

        }

        return true;
    }




    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {

            finish();

        } else if (id == R.id.favouriteDetail) {

            Favorite_UnfavoriteDish();

        } else if (id == R.id.favouriteFilledDetail) {

            Favorite_UnfavoriteDish();

        }
        else if (id == R.id.actionTrash) {

            //Start - Setting Activity
            DisplaySimple3buttondialog(getResources().getString(R.string.delete_dish),DetailActivity.this);
        }
        else if (id == R.id.actiondetail) {
            Intent intent = new Intent(DetailActivity.this, RestaurantProfileActivity.class);
            intent.putExtra(Constant.ID, dish.rest_id);
            startActivity(intent);

            //Start - Setting Activity
           // DisplaySimple3buttondialog(getResources().getString(R.string.delete_dish),DetailActivity.this);
        }

        else if (id == R.id.actionAddresspin) {
            Generalfunction.OpenMap(dish.rest_address, thisContext, dish.rest_address);
        }
        else if (id == R.id.actionCallBlack) {
            Generalfunction.RestaurantCall(dish.rest_phone_number, thisContext);

        }


//        else if(id == R.id.action_quickpost){
//
//            GlobalVar.setMyBooleanPref(thisContext, Constant.Isquickpost, true);
//            GlobalVar.setMyStringPref(thisContext, Constant.Post_PreviousScreen, Constant.Screen_DishDetail);
//            GlobalVar.setMyStringPref(thisContext,Constant.Peppa_RestID,dish.rest_id);
//
//            Intent intent = new Intent(DetailActivity.this, PostActivity_.class);
//            intent.putExtra(Constant.Quick_RestId, dish.rest_id);
//            intent.putExtra(Constant.Quick_RestName, dish.rest_name);
//            intent.putExtra(Constant.Quick_DishName, dish.name);
//            intent.putExtra(Constant.Quick_DishPrice, dish.price);
//            intent.putExtra(Constant.Quick_DishID, dish.id);
//            intent.putExtra(Constant.Quick_Dishimage, dish.image);
//            intent.putExtra(Constant.Quick_RestAddress,dish.rest_address);
//            intent.putExtra("latitude",dish.rest_latitude);
//            intent.putExtra("longitude",dish.rest_longitude);
//            startActivity(intent);
//            finish();
//        }

        return super.onOptionsItemSelected(item);
    }


    //Make - Dish favourite/unfavourite
    private void Favorite_UnfavoriteDish() {

        if (dish.favourited_by) {
            new FavoriteUnfavorite_Dish().execute(dish.id, "unfavourite");
        } else {
            new FavoriteUnfavorite_Dish().execute(dish.id, "favourite");
        }

    }
    public void DisplaySimple3buttondialog(final String Message, final Context context){

        final Activity mActivity=((Activity)context);

        new AlertDialog.Builder(context)
                .setTitle("Are You Sure")
                .setMessage(Message)
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        if(Message.equalsIgnoreCase(context.getResources().getString(R.string.delete_dish))){


                            new ProfileDishdel().execute();
                        }

                    }
                })
                .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        // do nothing
                        dialog.dismiss();
                    }
                })
              //  .setIcon(android.R.drawable.ic_dialog_alert)
                .show();
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
                Intent intent2 = new Intent(DetailActivity.this, NavigationalSearchActivity.class);
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


                Intent intent4 = new Intent(DetailActivity.this, FavouritesActivity.class);
                startActivity(intent4);
                break;

            case R.id.tv_navsetting:
                tvnavSearch.setImageDrawable(getResources().getDrawable(R.mipmap.search2));
                tvnavFavourite.setImageDrawable(getResources().getDrawable(R.mipmap.star1));
                tvnavMyprofile.setImageDrawable(getResources().getDrawable(R.mipmap.profilee));
//                tvnavLogout.setImageDrawable(getResources().getDrawable(R.mipmap.logout));
                tvnavSetting.setImageDrawable(getResources().getDrawable(R.mipmap.settings_pink));
//                tvnavpost.setImageDrawable(getResources().getDrawable(R.mipmap.postgrey1));

                Intent intent3 = new Intent(DetailActivity.this, SettingActivity.class);
                startActivity(intent3);
                break;


            case R.id.ivFavourite:

                Favorite_UnfavoriteDish();
//                ivFavorite.setVisibility(View.GONE);
//                ivFavouritefi.setVisibility(View.VISIBLE);
                break;

            case R.id.ivcallRestaurant:
                Generalfunction.RestaurantCall(dish.rest_phone_number, thisContext);
                break;
            case R.id.mapImageDetail:
                Generalfunction.OpenMap(dish.rest_address, thisContext, dish.rest_address);
                break;

            case R.id.ivtrash:

                //Start - Setting Activity
                DisplaySimple3buttondialog(getResources().getString(R.string.delete_dish),DetailActivity.this);
                break;

            case R.id.ivrest:
                Intent intent5 = new Intent(DetailActivity.this, RestaurantProfileActivity.class);
                intent5.putExtra(Constant.ID, dish.rest_id);
                startActivity(intent5);
                break;


//            case R.id.ivpost:

//                GlobalVar.setMyBooleanPref(thisContext, Constant.Isquickpost, true);
//                GlobalVar.setMyStringPref(thisContext, Constant.Post_PreviousScreen, Constant.Screen_DishDetail);
//                GlobalVar.setMyStringPref(thisContext,Constant.Peppa_RestID,dish.rest_id);
//
//                Intent intent = new Intent(DetailActivity.this, PostActivity_.class);
//                intent.putExtra(Constant.Quick_RestId, dish.rest_id);
//                intent.putExtra(Constant.Quick_RestName, dish.rest_name);
//                intent.putExtra(Constant.Quick_DishName, dish.name);
//                intent.putExtra(Constant.Quick_DishPrice, dish.price);
//                intent.putExtra(Constant.Quick_DishID, dish.id);
//                intent.putExtra(Constant.Quick_Dishimage, dish.image);
//                intent.putExtra(Constant.Quick_RestAddress,dish.rest_address);
//                intent.putExtra("latitude",dish.rest_latitude);
//                intent.putExtra("longitude",dish.rest_longitude);
//                startActivity(intent);
//                finish();
//                break;


            case R.id.tv_navpost:
                new VerifyingUser().execute();
                tvnavpost.setImageDrawable(getResources().getDrawable(R.mipmap.post));
                tvnavSearch.setImageDrawable(getResources().getDrawable(R.mipmap.search2));
                tvnavFavourite.setImageDrawable(getResources().getDrawable(R.mipmap.star1));
                tvnavMyprofile.setImageDrawable(getResources().getDrawable(R.mipmap.profilee));
//                tvnavLogout.setImageDrawable(getResources().getDrawable(R.mipmap.logout));
                tvnavSetting.setImageDrawable(getResources().getDrawable(R.mipmap.settings1));

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
//                                GlobalVar.setMyBooleanPref(thisContext, Constant.Isquickpost, true);
//                                GlobalVar.setMyStringPref(thisContext, Constant.Post_PreviousScreen, Constant.Screen_DishDetail);
//                                GlobalVar.setMyStringPref(thisContext,Constant.Peppa_RestID,dish.rest_id);
//
//                                Intent intent = new Intent(DetailActivity.this, PostActivity_.class);
//                                intent.putExtra(Constant.Quick_RestId, dish.rest_id);
//                                intent.putExtra(Constant.Quick_RestName, dish.rest_name);
//                                intent.putExtra(Constant.Quick_DishName, dish.name);
//                                intent.putExtra(Constant.Quick_DishPrice, dish.price);
//                                intent.putExtra(Constant.Quick_DishID, dish.id);
//                                intent.putExtra(Constant.Quick_Dishimage, dish.image);
//                                intent.putExtra(Constant.Quick_RestAddress,dish.rest_address);
//                                intent.putExtra("latitude",dish.rest_latitude);
//                                intent.putExtra("longitude",dish.rest_longitude);
//                                startActivity(intent);
//                                finish();
//                            }
//                            else{
//                                DisplayMessage1(Message);
//                            }
//                        }
//                    }.start();
//                }
//                else {
//
//                    GlobalVar.setMyBooleanPref(thisContext, Constant.Isquickpost, true);
//                    GlobalVar.setMyStringPref(thisContext, Constant.Post_PreviousScreen, Constant.Screen_DishDetail);
//                    GlobalVar.setMyStringPref(thisContext, Constant.Peppa_RestID, dish.rest_id);
//
//                    Intent intent = new Intent(DetailActivity.this, PostActivity_.class);
//                    intent.putExtra(Constant.Quick_RestId, dish.rest_id);
//                    intent.putExtra(Constant.Quick_RestName, dish.rest_name);
//                    intent.putExtra(Constant.Quick_DishName, dish.name);
//                    intent.putExtra(Constant.Quick_DishPrice, dish.price);
//                    intent.putExtra(Constant.Quick_DishID, dish.id);
//                    intent.putExtra(Constant.Quick_Dishimage, dish.image);
//                    intent.putExtra(Constant.Quick_RestAddress, dish.rest_address);
//                    intent.putExtra("latitude", dish.rest_latitude);
//                    intent.putExtra("longitude", dish.rest_longitude);
//                    startActivity(intent);
//                    finish();
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

            progress = new ProgressDialog(DetailActivity.this);
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
                        GlobalVar.setMyBooleanPref(thisContext, Constant.Isquickpost, true);
                        GlobalVar.setMyStringPref(thisContext, Constant.Post_PreviousScreen, Constant.Screen_DishDetail);
                        GlobalVar.setMyStringPref(thisContext,Constant.Peppa_RestID,dish.rest_id);

                        Intent intent = new Intent(DetailActivity.this, PostActivity_.class);
                        intent.putExtra(Constant.Quick_RestId, dish.rest_id);
                        intent.putExtra(Constant.Quick_RestName, dish.rest_name);
                        intent.putExtra(Constant.Quick_DishName, dish.name);
                        intent.putExtra(Constant.Quick_DishPrice, dish.price);
                        intent.putExtra(Constant.Quick_DishID, dish.id);
                        intent.putExtra(Constant.Quick_Dishimage, dish.image);
                        intent.putExtra(Constant.Quick_RestAddress,dish.rest_address);
                        intent.putExtra("latitude",dish.rest_latitude);
                        intent.putExtra("longitude",dish.rest_longitude);
                        startActivity(intent);
                        finish();
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
            progress = new ProgressDialog(DetailActivity.this);
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
                mToolbar.setBackgroundColor(ContextCompat.getColor(DetailActivity.this, android.R.color.white));
                invalidateOptionsMenu();
            }
        } else {
            if (mIsTheTitleVisible) {
                mIsTheTitleVisible = false;
                mToolbar.setBackgroundColor(ContextCompat.getColor(DetailActivity.this, android.R.color.transparent));
                invalidateOptionsMenu();
            }
        }
        if (mIsTheTitleVisible) {
            tvTitalname.setVisibility(View.VISIBLE);
            ivBack.setVisibility(View.VISIBLE);

            ivBack.setImageDrawable(getResources().getDrawable(R.mipmap.back, thisContext.getTheme()));

//            ivpost.setVisibility(View.GONE);
            ivrest.setVisibility(View.GONE);
            ivFavorite.setVisibility(View.GONE);
            ivtrash.setVisibility(View.GONE);
            ivcallRestaurant.setVisibility(View.GONE);
            mapaddress.setVisibility(View.GONE);
            //ivFavouritefi.setVisibility(View.GONE);


        } else {

            if (getIntent().getExtras() != null) {
                if (strOtherusrid != null) {
                    if (!getIntent().getExtras().getString(Constant.OtheruserId).equalsIgnoreCase(GlobalVar.getMyStringPref(DetailActivity.this, Constant.loginUserID))) {

                        ivtrash.setVisibility(View.GONE);
                    }
                } else if (getIntent().getExtras().getString(Constant.isMyprofile).equalsIgnoreCase("profile")) {


                    ivtrash.setVisibility(View.VISIBLE);

                } else {
                    ivtrash.setVisibility(View.GONE);
                }
            }
            ivBack.setImageDrawable(getResources().getDrawable(R.mipmap.back_white, thisContext.getTheme()));
            tvTitalname.setVisibility(View.GONE);

            ivBack.setVisibility(View.VISIBLE);
//            ivpost.setVisibility(View.VISIBLE);
            ivrest.setVisibility(View.VISIBLE);
            ivFavorite.setVisibility(View.VISIBLE);
            ivcallRestaurant.setVisibility(View.VISIBLE);
            mapaddress.setVisibility(View.VISIBLE);





         if (dish.favourited_by) {
                    ivFavorite.setImageDrawable(getResources().getDrawable(R.mipmap.favouritefilled_white_action, thisContext.getTheme()));
                } else {
                    ivFavorite.setImageDrawable(getResources().getDrawable(R.mipmap.favourite_white_action, thisContext.getTheme()));
                }



        }
    }



    public class ProfileDishdel extends AsyncTask<String, Void, String> {

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

            return WebFunctions.Post_Dishdel(token, strID);
        }

        @Override
        protected void onPostExecute(String rest) {
            super.onPostExecute(rest);
            progress.dismiss();
            if (!rest.equals("")) {
                JSONObject jsonObject;
                String Message = "";
                try {
                    Log.d("rup", "onPostExecute: " + rest);
                    jsonObject = new JSONObject(rest);

                    if (jsonObject.getString("success").toLowerCase().equalsIgnoreCase("yes")) {
                        Message = jsonObject.getString("message");
                        DisplayMessage(Message);
                    } else if (jsonObject.getString("success").toLowerCase().equalsIgnoreCase("no")) {
                        Message = jsonObject.getString("message");
                        Generalfunction.Simple1ButtonDialogClick2(Message, DetailActivity.this);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }


            }
        }
    }
    private void DisplayMessage(String strMessage) {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(DetailActivity.this);
        alertDialogBuilder.setTitle(getResources().getString(R.string.app_name));
        alertDialogBuilder.setMessage(strMessage);
        alertDialogBuilder.setCancelable(false);

        alertDialogBuilder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Intent intent = new Intent(DetailActivity.this, UserProfileActivity.class);
                intent.putExtra(Constant.isMyprofile, true);
                startActivity(intent);

                finish();
            }
        });
        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }


            //Call - Get Dish Detail API using Async Task
    public class getDish extends AsyncTask<String, Void, String> {

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
            if(view!=null){

            }
            else{
                view="";
            }
        }

        @Override
        protected String doInBackground(String... params) {

            return WebFunctions.getDish(strID, token,Current_DishComment_page,strOtherusrid,view);
        }

        @Override
        protected void onPostExecute(String rest) {
            super.onPostExecute(rest);

            if (rest.contains("error")) {
                Toast.makeText(thisContext, "It seems there is a problem. Please try again", Toast.LENGTH_SHORT).show();
            } else {

                isDishCommentLoading=false;
                ll_LoadmoreData.setVisibility(View.GONE);
                ArrayList<DishCommentModel> dishCommentlist=new ArrayList<>();

                if(Current_DishComment_page==1) {
                    dish = ParsingFunctions.parseDishModel(rest);
                    loadDishData();
                    viewPager.setAdapter(new DetailImageAdapter(thisContext));
                    dishCommentlist=dish.dishCommentlist;
                    if (dish.favourited_by) {
                        ivFavorite.setImageDrawable(getResources().getDrawable(R.mipmap.favouritefilled_white_action, thisContext.getTheme()));
                    } else {
                        ivFavorite.setImageDrawable(getResources().getDrawable(R.mipmap.favourite_white_action, thisContext.getTheme()));
                    }
                    badge = (TextView) findViewById(R.id.badge);
                    if(dish.un_rated.equals("0")){
                        badge.setVisibility(View.GONE);
                    }
                    else {



                        badge.setText(dish.un_rated);
                    }

                    SharedPreferences.Editor editor = sharedPreferences.edit();



                    editor.putString("badge", dish.un_rated);
                    editor.commit();
//                    notify=(LayerDrawable)((ImageView) findViewById(R.id.tv_navmyprofile)).getDrawable();
////        if(un_rated!=null) {
//                    setBadgeCount(thisContext, notify, dish.un_rated);

                }
                else{
                    dishCommentlist=ParsingFunctions.parseDishComment(rest);
                    if(dishCommentlist.size()>0) {
                        loadCommentData(dishCommentlist);
                    }
                }

                if(dishCommentlist.size()==Constant.Dish_Comment_limit){
                    isDishCommentLoading=true;
                    ll_LoadmoreData.setVisibility(View.VISIBLE);
                }

            }
            progress.dismiss();
        }
    }



    //Show - Dish Information Which is Get from Dish API
    public void loadDishData() {

        //address.setText(dish.rest_address);
        if (!dish.average_rating.equals("")) {
            dishRating.setText(dish.average_rating);
        } else {
            dishRating.setText("0");
        }

        dishName.setText("" + dish.name);

        String str = dish.rest_name;
        if (str.length() > 21) {
            str = str.substring(0, 18) + "...";
        }

        restaurantName.setText(str);
        description.setText(dish.description);
        dishPriceTv.setText(" -  $" + dish.price);

        votes.setText(dish.votes + " " + thisContext.getResources().getString(R.string.votetext));

        restaurantName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!isFromRestaurantProfile) {
                    Intent intent = new Intent(DetailActivity.this, RestaurantProfileActivity.class);
                    intent.putExtra(Constant.ID, dish.rest_id);
                    startActivity(intent);
                }
            }
        });

        Generalfunction.DisplayCall_image_white(ivcallRestaurant, dish.rest_phone_number, thisContext);   //Display Phone Image

//        callView.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Generalfunction.RestaurantCall(dish.rest_phone_number, thisContext);
//            }
//        });
//
//        address.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                String dLAT = dish.rest_latitude;
//                String dLONG = dish.rest_longitude;
////                Generalfunction.OpenMap(dLAT, dLONG, thisContext, address.getText().toString());
//                Generalfunction.OpenMap(dish.rest_address, thisContext, address.getText().toString());
//
//            }
//        });

        supportInvalidateOptionsMenu();

        loadCommentData(dish.dishCommentlist);                                                                          //Display dish Comment data

        //Show - Tag Image according to Dish Type
        if (dish.dishTaglist.size() > 0) {
            for (int i = 0; i < dish.dishTaglist.size(); i++) {
                if (dish.dishTaglist.get(i).toLowerCase().equalsIgnoreCase(getResources().getString(R.string.Vegetarian_label).toLowerCase())) {
                    ivVegeterian.setVisibility(View.VISIBLE);
                }
                if (dish.dishTaglist.get(i).toLowerCase().equalsIgnoreCase(getResources().getString(R.string.Vegan_label).toLowerCase())) {
                    ivVegen.setVisibility(View.VISIBLE);
                }
                if (dish.dishTaglist.get(i).toLowerCase().equalsIgnoreCase(getResources().getString(R.string.Gluten_free_label).toLowerCase())) {
                    ivGluten.setVisibility(View.VISIBLE);
                }

                if (dish.dishTaglist.get(i).toLowerCase().equalsIgnoreCase(getResources().getString(R.string.entry_label).toLowerCase())) {
                    ivEntree.setVisibility(View.VISIBLE);
                }
                if (dish.dishTaglist.get(i).toLowerCase().equalsIgnoreCase(getResources().getString(R.string.Main_label).toLowerCase())) {
                    ivMain.setVisibility(View.VISIBLE);
                }
                if (dish.dishTaglist.get(i).toLowerCase().equalsIgnoreCase(getResources().getString(R.string.Dessert_label).toLowerCase())) {
                    ivDesert.setVisibility(View.VISIBLE);
                }
                if (dish.dishTaglist.get(i).toLowerCase().equalsIgnoreCase(getResources().getString(R.string.Drink_label).toLowerCase())) {
                    ivDrink.setVisibility(View.VISIBLE);
                }

            }
        }
    }


    //Show - Dish Comment
    public void loadCommentData(ArrayList<DishCommentModel> dishCommentlist) {

        if(Current_DishComment_page==1){
            commentsLLT.removeAllViews();
            mlayoutInflater = (LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        }

        for (int i = 0; i < dishCommentlist.size(); i++) {

            final int j = i;
            View view = mlayoutInflater.inflate(R.layout.comment_row, null);

            //Mapping Textview
            TextView userNameTV    = (TextView) view.findViewById(R.id.userNameTV);
            TextView dishRatingTV  = (TextView) view.findViewById(R.id.dishRatingTV);
            TextView dishCommentTV = (TextView) view.findViewById(R.id.dishCommentTV);

            //Mapping Imageview
            ImageView ivuserphoto = (ImageView) view.findViewById(R.id.ivuserprofilephoto);

            //Set - Font type face
            Generalfunction.loadFont(this, userNameTV, "lato_medium.ttf");
            Generalfunction.loadFont(this, dishRatingTV, "lato_medium.ttf");
            Generalfunction.loadFont(this, dishCommentTV, "lato.ttf");

            userNameTV.setText(dishCommentlist.get(i).userFirstname);
            dishCommentTV.setText(dishCommentlist.get(i).userComment);
            dishRatingTV.setText(dishCommentlist.get(i).userRatingforDish);

            String userphotoUrl = dishCommentlist.get(i).userpProfileImage;

            //Show - Commented user image
            Generalfunction.DisplayImage_picasso(userphotoUrl, thisContext, Constant.case2, ivuserphoto, Constant.Ph_userprofilepic );

            final ArrayList<DishCommentModel> finalDishCommentlist = dishCommentlist;
            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intentOprofile = new Intent(thisContext, UserProfileActivity.class);
                    intentOprofile.putExtra(Constant.isMyprofile, false);
                    intentOprofile.putExtra(Constant.OtheruserId, finalDishCommentlist.get(j).userId);
                    startActivity(intentOprofile);
                }
            });

            commentsLLT.addView(view);
        }
    }


    //Show - All Dish Image
    public class DetailImageAdapter extends PagerAdapter {

        private Context _activity;
        private LayoutInflater inflater;
        private ArrayList<DishImageArray> DishImagearrylist = new ArrayList<>();
        private boolean flag_noImage = false;

        // constructor
        public DetailImageAdapter(Context activity) {
            ArrayList<DishImageArray> tempimagelist = new ArrayList<DishImageArray>(dish.dishImagelist);
            this._activity = activity;
            this.DishImagearrylist = tempimagelist;
            if (tempimagelist.size() == 0) {
                flag_noImage = true;
                DishImagearrylist.add(null);
            }
        }

        @Override
        public int getCount() {
            return DishImagearrylist.size();
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view == ((RelativeLayout) object);
        }

        @Override
        public Object instantiateItem(ViewGroup container, final int position)
        {
            final ImageView imgDisplay;

            inflater = (LayoutInflater) _activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View viewLayout = inflater.inflate(R.layout.detail_pager_item, container, false);

            imgDisplay = (ImageView) viewLayout.findViewById(R.id.detailImage);
            leftNav = (ImageView)viewLayout. findViewById(R.id.left_nav);
            rightNav = (ImageView) viewLayout.findViewById(R.id.right_nav);
            TextView tvUsername = (TextView) viewLayout.findViewById(R.id.tvusername);

            Typeface lato = Typeface.createFromAsset(getAssets(), "fonts/lato.ttf");
            tvUsername.setTypeface(lato);

            tvUsername.setText(thisContext.getResources().getString(R.string.empty));

            if (flag_noImage) {
                Generalfunction.DisplayImage_picasso("", thisContext, Constant.case2, imgDisplay, Constant.Ph_dish);
            } else {
                String strDishImage = DishImagearrylist.get(position).dish_image_url;
                Generalfunction.DisplayImage_picasso(strDishImage, thisContext, Constant.case1, imgDisplay, Constant.Ph_dish);

                tvUsername.setText("@" + DishImagearrylist.get(position).imagepost_username);

                tvUsername.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        if(!Generalfunction.isEmptyCheck(Generalfunction.Isnull(DishImagearrylist.get(position).user_id))) {
                            Intent intentOprofile = new Intent(thisContext, UserProfileActivity.class);
                            intentOprofile.putExtra(Constant.isMyprofile, false);
                            intentOprofile.putExtra(Constant.OtheruserId, DishImagearrylist.get(position).user_id);
                            startActivity(intentOprofile);
                        }
                    }
                });




                if(DishImagearrylist.size()>1)
                {
                    rightNav.setVisibility(View.VISIBLE);
                    leftNav.setVisibility(View.VISIBLE);
                    if(position==0)
                    {
                        leftNav.setVisibility(View.GONE);
                        rightNav.setVisibility(View.VISIBLE);
                    }
                    else if(position==viewPager.getAdapter().getCount() - 1)
                    {
                        leftNav.setVisibility(View.VISIBLE);
                        rightNav.setVisibility(View.GONE);
                    }



                    rightNav.setOnClickListener(new View.OnClickListener(){
                        @Override
                        public void onClick(View v)
                        {
//                            if (!isLastPage()) {

                                if (viewPager.getCurrentItem() < viewPager.getRight()){
                                    //leftNav.setVisibility(View.VISIBLE);
                                    viewPager.setCurrentItem(viewPager.getCurrentItem() + 1, true);

                          }
                        }
                    });

                    leftNav.setOnClickListener(new View.OnClickListener()
                    {
                        @Override
                        public void onClick(View v)
                        {
//                            if (!isFirstPage()) {

                                if (viewPager.getCurrentItem() > viewPager.getLeft()){

                                    viewPager.setCurrentItem(viewPager.getCurrentItem() - 1, true);
                          }
                        }
                    });
                }
                else
                {
                    leftNav.setVisibility(View.GONE);
                    rightNav.setVisibility(View.GONE);
                }
            }







            ((ViewPager) container).addView(viewLayout);

            return viewLayout;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            ((ViewPager) container).removeView((RelativeLayout) object);
        }
    }
    private boolean isLastPage() {
        return viewPager.getCurrentItem() == viewPager.getAdapter().getCount() - 1;
    }

    private boolean isFirstPage() {
        return viewPager.getCurrentItem() == 0;
    }

    //Make - Dish Favorite/Unfavorite
    public class FavoriteUnfavorite_Dish extends AsyncTask<String, Void, String> {

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
            return WebFunctions.FavouriteDish(params[0], token, params[1]);
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);

            progress.dismiss();

            if (result.equalsIgnoreCase("true")) {
                GlobalVar.setMyBooleanPref(thisContext, Constant.Filter_FavouriteRefresh, true);
                if (dish.favourited_by) {
                    dish.favourited_by = false;
                } else {
                    dish.favourited_by = true;
                    Toast.makeText(getApplicationContext(),"Added to favourites",Toast.LENGTH_SHORT).show();
                }
                supportInvalidateOptionsMenu();

            } else {
                Generalfunction.Simple1ButtonDialog(result, thisContext);
            }

        }
    }

    // Check Request permission

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

    }