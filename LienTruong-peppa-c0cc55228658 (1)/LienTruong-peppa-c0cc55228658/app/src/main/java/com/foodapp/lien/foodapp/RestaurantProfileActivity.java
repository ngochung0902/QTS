package com.foodapp.lien.foodapp;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.graphics.Palette;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;

import model.DishModel;
import model.RestaurantModel;
import parsing.ParsingFunctions;
import parsing.WebFunctions;
import utility.ConnectionDetector;
import utility.Constant;
import utility.Generalfunction;

@TargetApi(21)
public class RestaurantProfileActivity extends AppCompatActivity  implements AppBarLayout.OnOffsetChangedListener, View.OnClickListener {

    private static final float PERCENTAGE_TO_SHOW_TITLE_AT_TOOLBAR = 0.9f;
    private static final float PERCENTAGE_TO_HIDE_TITLE_DETAILS = 0.3f;
    private static final int ALPHA_ANIMATIONS_DURATION = 200;
    private static final int PERMISSION_REQUEST_CODE = 2;
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 3;
    private static final int CallPERMISSION_REQUEST_CODE = 4;
    private static final int CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE = 100;

    private boolean mIsTheTitleVisible = false;
    private boolean mIsTheTitleContainerVisible = true;
    private boolean mIsFollowed = false;

    Context mContext;

    LinearLayout ll_followers,ll_following;

    String stringLatitude, stringLogitude;

    private AppBarLayout mAppBarLayout;
    private Toolbar mToolbar;

    private TextView tvTitalname;
    private TextView tvFlname,tvUseraddress;
    private TextView tvFollowercount,tvFollowingcount;
    private TextView tvrestaurantvotes,tvrestaurantRating;

    private ImageView ivuserCoverphoto;
    private ImageView ivUserphoto,ivBack,ivFilterW,ivCallW,ivFavorite;
    private ImageView ivisUserFavourite;

    private Button btnFollow,btnUnfollow;


    Uri selectedImageUri, fileUri;
    String fileName;
    //LinearLayout postFollowLLT, favouriteLLT;
    RecyclerView mRecyclerView;
    //ImageView heartImageDetail;

    MyAdapter mAdapter;
    RestaurantModel ProfileModel = new RestaurantModel();
    ArrayList<DishModel> dishes = new ArrayList<DishModel>();

    ConnectionDetector cdObj;
    CollapsingToolbarLayout collapsingToolbarLayout;
    private int selectedPos=0;
    private String strId,strWebmethod="";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_restaurantprofile);

        mContext = this;

       // LocalBroadcastManager.getInstance(this).registerReceiver(mMessageReceiver,new IntentFilter("update_restaurant"));

        if (getIntent().getExtras() != null) {
            //check it is a my profile or other profile
            strId=getIntent().getExtras().getString(Constant.ID);
        }

        // widget mapping
        Initializewidget();

        //Mapping recycleview
        mRecyclerView = (RecyclerView) findViewById(R.id.rv_mydishes);
        mRecyclerView.setHasFixedSize(true);

        GridLayoutManager gridLayoutManager = new GridLayoutManager(this, 2);
        mRecyclerView.setLayoutManager(gridLayoutManager);

        mToolbar.setTitle("");
        mAppBarLayout.addOnOffsetChangedListener(this);

        setSupportActionBar(mToolbar);
        ActionBar actionBar = getSupportActionBar();
       // actionBar.setDisplayHomeAsUpEnabled(true);

        collapsingToolbarLayout = (CollapsingToolbarLayout) findViewById(R.id.collapsing_toolbar);


        /*//Toolbar text appearance
        collapsingToolbarLayout.setCollapsedTitleTextAppearance(R.style.collapsedappbar);
        collapsingToolbarLayout.setExpandedTitleTextAppearance(R.style.expandedappbar);*/

    }

    private void Initializewidget() {

        mToolbar = (Toolbar) findViewById(R.id.maintoolbar);

        //Mapping textview
        tvTitalname = (TextView) findViewById(R.id.tvUnametital);
        tvFlname= (TextView) findViewById(R.id.tvUserflname);
        tvUseraddress= (TextView) findViewById(R.id.tvUseraddress);
        tvFollowercount= (TextView) findViewById(R.id.tvfollowerscount);
        tvFollowingcount= (TextView) findViewById(R.id.tvfollowingcount);
        tvrestaurantvotes= (TextView) findViewById(R.id.tvrestaurantvotes);
        tvrestaurantRating= (TextView) findViewById(R.id.dishRatingDetailTv);

        //Mapping Linear layout
        ll_followers=(LinearLayout)findViewById(R.id.ll_userpf_follow);
        ll_following=(LinearLayout)findViewById(R.id.ll_userpf_following);

        //Mapping imageview
        ivuserCoverphoto = (ImageView) findViewById(R.id.ivusercoverphoto);
        ivUserphoto=(ImageView) findViewById(R.id.ivUserProfile);
        ivBack=(ImageView) findViewById(R.id.ivbackarrow);
        ivisUserFavourite=(ImageView) findViewById(R.id.ivFavourite);
        ivFilterW=(ImageView) findViewById(R.id.ivfilterW);
        ivCallW=(ImageView)findViewById(R.id.ivCallW);
        ivFavorite=(ImageView)findViewById(R.id.ivFavouriteW);

        //Mapping button
        btnFollow=(Button)findViewById(R.id.btnfollow);
        btnUnfollow=(Button)findViewById(R.id.btnunfollow);

        //Mapping appbar layout
        mAppBarLayout = (AppBarLayout) findViewById(R.id.main_appbar);

        //Mapping floating action button
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);

        //set font
        Typeface playfair = Typeface.createFromAsset(getAssets(), "fonts/playfair.otf");
        Typeface lato = Typeface.createFromAsset(getAssets(), "fonts/lato.ttf");
        Typeface latom = Typeface.createFromAsset(getAssets(), "fonts/lato_medium.ttf");
        Typeface latob = Typeface.createFromAsset(getAssets(), "fonts/lato_bold.ttf");

        tvTitalname.setTypeface(playfair);
        tvFlname.setTypeface(playfair);
        tvUseraddress.setTypeface(lato);

        tvFollowercount.setTypeface(latob);
        tvFollowingcount.setTypeface(latob);
        btnFollow.setTypeface(lato);

        tvTitalname.setVisibility(View.GONE);
        ivUserphoto.setVisibility(View.GONE);

        //ivBack.setVisibility(View.GONE);

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (ActivityCompat.checkSelfPermission(mContext, Manifest.permission.CAMERA)
                        != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(RestaurantProfileActivity.this,
                            new String[]{Manifest.permission.CAMERA}, PERMISSION_REQUEST_CODE);
                } else {
                    final File root = new File(Environment.getExternalStorageDirectory() + File.separator + "FoodApp" + File.separator);
                    root.mkdirs();
                    String fname = "img_" + System.currentTimeMillis() + ".jpg";
                    final File sdImageMainDirectory = new File(root, fname);
                    fileUri = Uri.fromFile(sdImageMainDirectory);
                    Constant.openImageIntent(RestaurantProfileActivity.this, fileUri);
                }
            }
        });

        ivBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        ivCallW.setOnClickListener(this);
        ivFilterW.setOnClickListener(this);

        CallUserwebapi();

    }

    @Override
    protected void onStart() {

        super.onStart();
    }

    private void CallUserwebapi(){

        //inialize connection detector
        cdObj=new ConnectionDetector(mContext);
        if (cdObj.isConnectingToInternet()) {
            //strId="45";
            new getRestaurantProfile().execute(strId);
        }
        else{
            Generalfunction.Simple1ButtonDialog(mContext.getResources().getString(R.string.Internet_Message), mContext);
        }
    }

    //get my user data
    public class getRestaurantProfile extends AsyncTask<String, Void, String> {

        ProgressDialog progress;
        String token;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            SharedPreferences prefs = getSharedPreferences("MY_PREFS", MODE_PRIVATE);
            token = prefs.getString("token", "");
            Log.d("token", token);

            progress= new ProgressDialog(mContext);
            progress.setMessage("Loading...");
            progress.setCancelable(false);
            progress.show();
        }

        @Override
        protected String doInBackground(String... params) {
            return WebFunctions.getRestaurantprofile(params[0], token);
        }

        @Override
        protected void onPostExecute(String rest) {
            super.onPostExecute(rest);

            //Log.d("getuser", "onPostExecute: "+rest);
            if (rest.equals("error")) {
                Toast.makeText(mContext, "It seems there is a problem. Please try again", Toast.LENGTH_SHORT).show();
            } else {
                ProfileModel = ParsingFunctions.parseRestaurantModel(rest);

                supportInvalidateOptionsMenu();

                loadDataRest(ProfileModel);

                if (mAdapter != null) {
                    dishes.clear();
                    dishes.addAll(ProfileModel.dishes);
                    mAdapter.notifyDataSetChanged();
                } else {
                    dishes = ProfileModel.dishes;
                    mAdapter = new MyAdapter(dishes);
                    mRecyclerView.setAdapter(mAdapter);
                }

                Log.d("Restaurant profile", "onPostExecute: "+dishes.size());

                TextView emptyView = (TextView) findViewById(R.id.empty_view);

               /* if(dishes.size()<1) {
                    Log.d("Restaurant profile", "onPostExecute: "+dishes.size());
                    emptyView.setVisibility(View.GONE);
                    mRecyclerView.setVisibility(View.GONE);
                }
                else{
                    emptyView.setVisibility(View.GONE);
                    mRecyclerView.setVisibility(View.VISIBLE);
                }*/
            }

            //supportInvalidateOptionsMenu();
            progress.dismiss();
        }
    }



    public void loadDataRest (final RestaurantModel restaurantprofile) {

        if (restaurantprofile.followed_by) {
            btnFollow.setVisibility(View.GONE);
            btnUnfollow.setVisibility(View.VISIBLE);
        }
        else{
            btnFollow.setVisibility(View.VISIBLE);
            btnUnfollow.setVisibility(View.GONE);
        }
        ll_following.setVisibility(View.GONE);

        tvFollowercount.setText(restaurantprofile.followers_count + " "+mContext.getResources().getString(R.string.followers));
        tvFlname.setText(restaurantprofile.name);
        tvTitalname.setText(restaurantprofile.name);
        tvFollowingcount.setVisibility(View.GONE);
        tvUseraddress.setText(restaurantprofile.address);

        tvrestaurantvotes.setText(restaurantprofile.votes+ " "+ mContext.getResources().getString(R.string.votetext));
        tvrestaurantRating.setText(restaurantprofile.rating);

        String userCoverphotoUrl=restaurantprofile.restaurant_image;

        if(!Generalfunction.isEmptyCheck(userCoverphotoUrl)){
            Picasso.with(this)
                    .load(userCoverphotoUrl)
                    .placeholder(R.drawable.image_loading) //this is optional the image to display while the url image is downloading
                    .error(R.drawable.img_not_available)         //this is also optional if some error has occurred in downloading the image this image would be displayed
                    .into(ivuserCoverphoto);

            Picasso.with(this).load(userCoverphotoUrl)
                    .placeholder(R.drawable.image_loading).error(R.drawable.img_not_available)
                    .into(ivUserphoto);
        }
        else{
            Picasso.with(mContext).load(R.drawable.no_picture_sign)
                    .into(ivuserCoverphoto);
            Picasso.with(mContext).load(R.drawable.no_picture_sign)
                    .into(ivUserphoto);
        }

        if (restaurantprofile.favourited_by) {
            ivisUserFavourite.setImageDrawable(getResources().getDrawable(R.drawable.solid_heart, mContext.getTheme()));
            ivFavorite.setImageDrawable(getResources().getDrawable(R.drawable.star_filled, mContext.getTheme()));
        } else {
            ivFavorite.setImageDrawable(getResources().getDrawable(R.drawable.star_white, mContext.getTheme()));
            ivisUserFavourite.setImageDrawable(getResources().getDrawable(R.drawable.heart, mContext.getTheme()));
        }

        tvFollowercount.setOnClickListener(this);
        tvUseraddress.setOnClickListener(this);
        ivFavorite.setOnClickListener(this);
        btnFollow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                strWebmethod="Follow_unflollow";
                new follow_Favourite_Restaurant().execute(restaurantprofile.id, "follow");

            }
        });
        btnUnfollow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                strWebmethod="Follow_unflollow";
                new follow_Favourite_Restaurant().execute(restaurantprofile.id, "unfollow");

            }
        });

       /* ivisUserFavourite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                strWebmethod="Favourite_unfavourite";

                if(restaurantprofile.favourited_by) {
                    new follow_Favourite_Restaurant().execute(restaurantprofile.id, "unfavourite");
                }
                else{
                    new follow_Favourite_Restaurant().execute(restaurantprofile.id, "favourite");
                }

            }
        });*/

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.ivFavouriteW:
                Favourite_Unfavorite();
                 //Toast.makeText(mContext,"Favourite is click",Toast.LENGTH_SHORT).show();
                break;
            case R.id.tvfollowerscount:
                Intent intent = new Intent(mContext, FollowActivity.class);
                intent.putExtra(Constant.follower_following,Constant.Restaurant);
                startActivity(intent);
                break;
            case R.id.tvUseraddress:
               Generalfunction.OpenMap(ProfileModel.latitude,ProfileModel.longitude,mContext);
                break;
            case R.id.ivCallW:
                PhoneCall();
                break;
            case R.id.ivfilterW:
                FilterCall();
                break;

        }
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
                //startAlphaAnimation(mTitleContainer, ALPHA_ANIMATIONS_DURATION, View.INVISIBLE);
                mIsTheTitleContainerVisible = false;
            }

        } else {

            if (!mIsTheTitleContainerVisible) {
                // startAlphaAnimation(mTitleContainer, ALPHA_ANIMATIONS_DURATION, View.VISIBLE);
                mIsTheTitleContainerVisible = true;
            }
        }
    }

    private void handleToolbarTitleVisibility(float percentage) {

        Resources r = mContext.getResources();
        int px = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,60,r.getDisplayMetrics());

        LayoutParams params = new LayoutParams( LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);

        if (percentage >= PERCENTAGE_TO_SHOW_TITLE_AT_TOOLBAR) {

            if (!mIsTheTitleVisible) {
                //startAlphaAnimation(tvTitalname, ALPHA_ANIMATIONS_DURATION, View.VISIBLE);
                mIsTheTitleVisible = true;
                mToolbar.setBackgroundColor(ContextCompat.getColor(RestaurantProfileActivity.this, android.R.color.white));
                invalidateOptionsMenu();
                //startAlphaAnimation(postFollowLLT, ALPHA_ANIMATIONS_DURATION, View.VISIBLE);

            }

        } else {

            if (mIsTheTitleVisible) {
                //startAlphaAnimation(mTitle, ALPHA_ANIMATIONS_DURATION, View.INVISIBLE);
                mIsTheTitleVisible = false;
                mToolbar.setBackgroundColor(ContextCompat.getColor(RestaurantProfileActivity.this, android.R.color.transparent));
                invalidateOptionsMenu();
                //startAlphaAnimation(postFollowLLT, ALPHA_ANIMATIONS_DURATION, View.INVISIBLE);
            }
        }

        // Log.d("visi", "handleToolbarTitleVisibility: "+mIsTheTitleVisible);
        if(mIsTheTitleVisible){
            tvTitalname.setVisibility(View.VISIBLE);
            ivUserphoto.setVisibility(View.VISIBLE);
            ivBack.setVisibility(View.GONE);
            ivFilterW.setVisibility(View.GONE);
            ivCallW.setVisibility(View.GONE);
            ivFavorite.setVisibility(View.GONE);
        }
        else{
            tvTitalname.setVisibility(View.GONE);
            ivUserphoto.setVisibility(View.GONE);
            ivBack.setVisibility(View.VISIBLE);
            ivFilterW.setVisibility(View.VISIBLE);
            ivCallW.setVisibility(View.VISIBLE);
            ivFavorite.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_restaurant, menu);

        MenuItem itemFilterB = menu.findItem(R.id.actionFilterProfileB);
       // MenuItem itemcallW = menu.findItem(R.id.actionCallWhite);
        MenuItem itemcallB = menu.findItem(R.id.actionCallBlack);
        MenuItem itemFavBB= menu.findItem(R.id.actionFavBborder);
        //MenuItem itemFavWB= menu.findItem(R.id.actionFavWborder);
        MenuItem itemFavourite= menu.findItem(R.id.actionFav);
        //MenuItem itemFilterB = menu.findItem(R.id.actionFilterProfileB);

        if(mIsTheTitleVisible){
           // itemcallW.setVisible(!mIsTheTitleVisible);
            itemcallB.setVisible(mIsTheTitleVisible);
            itemFilterB.setVisible(mIsTheTitleVisible);
            itemFavourite.setVisible(ProfileModel.favourited_by);
           // itemFavWB.setVisible(!mIsTheTitleVisible && !ProfileModel.favourited_by);
            itemFavBB.setVisible(mIsTheTitleVisible && !ProfileModel.favourited_by);
        }
        else{
            //itemcallW.setVisible(false);
            itemcallB.setVisible(false);
            itemFilterB.setVisible(false);
            itemFavourite.setVisible(false);
           // itemFavWB.setVisible(false );
            itemFavBB.setVisible(false);
        }

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.actionFilterProfile || id == R.id.actionFilterProfileB) {
            FilterCall();

        } else if (id == R.id.actionCallBlack) {
            PhoneCall();

        } else if (id == android.R.id.home) {
            finish();
        } else if (id == R.id.actionFav) {
            Favourite_Unfavorite();
        } else if (id == R.id.favouriteWhiteProfile) {
            // BUTTON_ACTION = "FAVOURITE";
            //new follow_favourtiteRestaturant().execute(restaurant.id, "favourite");
        } else if (id == R.id.actionFavBborder) {
            // BUTTON_ACTION = "FAVOURITE";
            Favourite_Unfavorite();
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if (requestCode == PERMISSION_REQUEST_CODE) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Constant.openImageIntent(RestaurantProfileActivity.this, fileUri);
            }
            else {
                // Your app will not have this permission. Turn off all functions
                // that require this permission or it will force close like your
                // original question
            }
        } else if (requestCode == LOCATION_PERMISSION_REQUEST_CODE){
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                //openMap();
                Toast.makeText(RestaurantProfileActivity.this, "open map", Toast.LENGTH_SHORT).show();
            }
        }
        else if(requestCode==CallPERMISSION_REQUEST_CODE){
            PhoneCall();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            if (requestCode == CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE) {
                boolean isCamera=false;
                if (data == null) {
                    isCamera = true;
                }
                selectedImageUri = (data == null ? true : MediaStore.ACTION_IMAGE_CAPTURE.equals(data.getAction())) ? fileUri : (data == null ? null : data.getData());
                Intent intent  = new Intent(mContext, PostActivity.class);

                if (selectedImageUri != null) {
                    intent.putExtra("filename", selectedImageUri.toString());
                    if(isCamera){
                        intent.putExtra("isCamerabutgallery", true);
                        //Log.d("NAvigation", "onActivityResult: is camera");
                    }
                    intent.putExtra("isCamera", false);
                    //Log.d("NAvigation", "onActivityResult: is gallery");
                } else {
                    intent.putExtra("filename", fileUri.getPath());
                    intent.putExtra("isCamera", true);
                    // Log.d("NAvigation", "onActivityResult: is camera");
                }
                intent.putExtra("isCameraOrGallery", true);
                startActivity(intent);
            }
        }
    }



    public class MyAdapter extends RecyclerView.Adapter<MyAdapter.ViewHolder> {

        private ArrayList<DishModel> mDataset;

        public class ViewHolder extends RecyclerView.ViewHolder {
            // each data item is just a string in this case
            public TextView ratingGridTv;
            public TextView percentageGridTv;
            public TextView dishNameGridTv;
            public TextView dishVotesGridTv;
            public ImageView rateThisDishImage;
            public ImageView heartImageGrid;
            public ImageView foodImageGrid;
            public LinearLayout favouriteLLT;

            public ViewHolder(View itemView) {
                super(itemView);

                rateThisDishImage = (ImageView) itemView.findViewById(R.id.rateThisDishImage);
                foodImageGrid = (ImageView) itemView.findViewById(R.id.foodImageGrid);
                heartImageGrid = (ImageView) itemView.findViewById(R.id.heartImageGrid);
                ratingGridTv = (TextView) itemView.findViewById(R.id.ratingGridTv);
                percentageGridTv = (TextView) itemView.findViewById(R.id.percentageGridTv);
                dishNameGridTv = (TextView) itemView.findViewById(R.id.dishNameGridTv);
                dishVotesGridTv = (TextView) itemView.findViewById(R.id.dishVotesGridTv);
                favouriteLLT = (LinearLayout) itemView.findViewById(R.id.favouriteLLT);
            }
        }

        // Provide a suitable constructor (depends on the kind of dataset)
        public MyAdapter(ArrayList<DishModel> myDataset) {
            mDataset = myDataset;
        }

        @Override
        public MyAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            // create a new view
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.grid_list_item, parent, false);
            ViewHolder vh = new ViewHolder(v);
            return vh;
        }

        // Replace the contents of a view (invoked by the layout manager)
        @Override
        public void onBindViewHolder(ViewHolder holder, final int position) {

            holder.rateThisDishImage.setVisibility(View.GONE);


            if (!mDataset.get(position).average_rating.equals("")) {
                holder.ratingGridTv.setText(mDataset.get(position).average_rating);
            } else {
                holder.ratingGridTv.setText("0");
            }
            Constant.loadFont(mContext, holder.ratingGridTv, "lato.ttf");

            holder.dishNameGridTv.setText(mDataset.get(position).name);


            if (mDataset.get(position).favourited_by) {
                holder.heartImageGrid.setImageDrawable(getResources().getDrawable(R.drawable.solid_heart, mContext.getTheme()));
            } else {
                holder.heartImageGrid.setImageDrawable(getResources().getDrawable(R.drawable.heart, mContext.getTheme()));
            }

            String strfoodimage=mDataset.get(position).image;
            if(!Generalfunction.isEmptyCheck(strfoodimage)){
                Picasso.with(mContext).load(strfoodimage)
                        .centerCrop()
                        .fit()
                        .placeholder(R.drawable.no_picture_sign).error(R.drawable.no_picture_sign)
                        //.resize(500, 500) // resizes the image to these dimensions (in pixel)
                        //.centerInside()
                        .into(holder.foodImageGrid);
            }
            else{
                Picasso.with(mContext).load(R.drawable.no_picture_sign)
                        .fit()
                        .centerInside()
                        .into(holder.foodImageGrid);
            }


            holder.favouriteLLT.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    selectedPos = position;
                    if (mDataset.get(position).favourited_by) {
                        new favourtiteDish().execute(mDataset.get(position).id, mContext.getResources().getString(R.string.unfavourite_api));

                    } else {
                        new favourtiteDish().execute(mDataset.get(position).id, mContext.getResources().getString(R.string.favourite_api));
                    }
                }
            });

            holder.rateThisDishImage.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(RestaurantProfileActivity.this, RateActivity.class);
                    startActivity(intent);
                }
            });

            holder.foodImageGrid.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(RestaurantProfileActivity.this, DetailActivity.class);
                    intent.putExtra(Constant.ID, mDataset.get(position).id);
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




    // Recycle view item related async task function (here, performed favourite unfaourite function)
    public class favourtiteDish extends AsyncTask<String, Void, String> {

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
            progress.show();
        }

        @Override
        protected String doInBackground(String... params) {
            return WebFunctions.Favourite_UnfavouriteDish(params[0], token, params[1]);
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);

            progress.dismiss();
            //Log.d("favourite", "onPostExecute: "+result);
            Boolean response = false;
            String Message= "";
            try {
                JSONObject jsonObject = new JSONObject(result);
                if (jsonObject.has("success")) {
                    if(jsonObject.getString("success").equalsIgnoreCase("Yes")) {
                        response = true;
                    }
                }
                Message=jsonObject.getString("message");

            } catch (JSONException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }

            if(response){

                CallUserwebapi();
                Generalfunction.Simple1ButtonDialog(Message,mContext);

            }
            //Toast.makeText(mContext, "Action has been performed successfully", Toast.LENGTH_SHORT).show();
        }

    }


    public class follow_Favourite_Restaurant extends AsyncTask<String, Void, String> {

        ProgressDialog progress;
        String token;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            SharedPreferences prefs = getSharedPreferences("MY_PREFS", MODE_PRIVATE);
            token = prefs.getString("token", "");
            Log.d("token", token);

            progress= new ProgressDialog(mContext);
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

            if(result.equalsIgnoreCase("true")){

                if(strWebmethod.equalsIgnoreCase("Follow_unflollow")) {
                    if (btnFollow.getVisibility() == View.VISIBLE) {
                        btnFollow.setVisibility(View.GONE);
                        btnUnfollow.setVisibility(View.VISIBLE);
                    } else {
                        btnFollow.setVisibility(View.VISIBLE);
                        btnUnfollow.setVisibility(View.GONE);
                    }
                }
                else if(strWebmethod.equalsIgnoreCase("Favourite_unfavourite")){
                    if (ProfileModel.favourited_by) {
                        ProfileModel.favourited_by = false;
                    } else {
                        ProfileModel.favourited_by = true;
                    }

                    if (ProfileModel.favourited_by) {
                        ivisUserFavourite.setImageDrawable(getResources().getDrawable(R.drawable.solid_heart, mContext.getTheme()));
                        ivFavorite.setImageDrawable(getResources().getDrawable(R.drawable.star_filled, mContext.getTheme()));
                    } else {
                        ivisUserFavourite.setImageDrawable(getResources().getDrawable(R.drawable.heart, mContext.getTheme()));
                        ivFavorite.setImageDrawable(getResources().getDrawable(R.drawable.star_white, mContext.getTheme()));
                    }

                    supportInvalidateOptionsMenu();
                    Toast.makeText(mContext, "Action has been performed successfully", Toast.LENGTH_SHORT).show();

                }
            }
            else{
                Generalfunction.Simple1ButtonDialog(result,mContext);
            }

        }
    }


    private void Favourite_Unfavorite(){

        strWebmethod="Favourite_unfavourite";

        if(ProfileModel.favourited_by) {
            new follow_Favourite_Restaurant().execute(ProfileModel.id, "unfavourite");
        }
        else{
            new follow_Favourite_Restaurant().execute(ProfileModel.id, "favourite");
        }
    }

    private void PhoneCall(){
        if (ActivityCompat.checkSelfPermission(RestaurantProfileActivity.this, Manifest.permission.CALL_PHONE)
                != PackageManager.PERMISSION_GRANTED) {
            // Request missing location permission.
            ActivityCompat.requestPermissions(RestaurantProfileActivity.this,
                    new String[]{Manifest.permission.CALL_PHONE},
                    2);
        } else {
            Intent intent = new Intent(Intent.ACTION_CALL);
            intent.setData(Uri.parse("tel:" + ProfileModel.phone_number));
            startActivity(intent);
        }
    }

    private void FilterCall(){
        Intent intent = new Intent(RestaurantProfileActivity.this, FilterActivity.class);
        startActivity(intent);
    }




}