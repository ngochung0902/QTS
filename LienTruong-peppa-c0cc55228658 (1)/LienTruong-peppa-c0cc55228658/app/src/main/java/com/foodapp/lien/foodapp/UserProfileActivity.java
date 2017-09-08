package com.foodapp.lien.foodapp;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
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
import android.view.animation.AlphaAnimation;
import android.widget.BaseAdapter;
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
import model.UserprofileModel;
import parsing.ParsingFunctions;
import parsing.WebFunctions;
import utility.ConnectionDetector;
import utility.Constant;
import utility.GPSTracker;
import utility.Generalfunction;
import utility.GlobalVar;

@TargetApi(21)
public class UserProfileActivity extends AppCompatActivity implements AppBarLayout.OnOffsetChangedListener {

    private static final float PERCENTAGE_TO_SHOW_TITLE_AT_TOOLBAR = 0.9f;
    private static final float PERCENTAGE_TO_HIDE_TITLE_DETAILS = 0.3f;
    private static final int ALPHA_ANIMATIONS_DURATION = 200;
    private static final int PERMISSION_REQUEST_CODE = 2;
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 3;
    private static final int CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE = 100;

    private Context mContext;
    private MyAdapter mAdapter;
    private ConnectionDetector cdObj;

    private LinearLayout ll_Followers, ll_Following;
    private AppBarLayout mAppBarLayout;
    private Toolbar mToolbar;
    private TextView tvTitalname;
    private TextView tvFlname, tvUsername;
    private TextView tvFollowercount, tvFollowingcount;
    private ImageView ivuserCoverphoto, ivuserProfilephoto;
    private ImageView ivUserphoto, ivBack;
    private ImageView ivFilterW, ivSettingW;
    private Button btnFollow, btnUnfollow;
    private RecyclerView mRecyclerView;
    private CollapsingToolbarLayout collapsingToolbar;

    private Uri selectedImageUri, fileUri;
    private String fileName;
    private int selectedPos = 0;
    private String strOtheruserId;

    private boolean mIsTheTitleVisible = false;
    private boolean mIsFollowed = false;
    private boolean mIsMAIN = true;

    private UserprofileModel UserprofileModel = new UserprofileModel();
    private ArrayList<DishModel> dishes = new ArrayList<DishModel>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_userprofile);

        mContext = this;

        //inialize connection detector
        cdObj = new ConnectionDetector(mContext);

        if (getIntent().getExtras() != null) {
            //check it is a my profile or other profile
            mIsMAIN = getIntent().getExtras().getBoolean(Constant.isMyprofile);   //MisMain = true -> My user profile otherwise other user profile
        }

        if (!mIsMAIN) {
            strOtheruserId = getIntent().getExtras().getString(Constant.OtheruserId);
            if (strOtheruserId.equalsIgnoreCase(GlobalVar.getMyStringPref(mContext, Constant.loginUserID))) {
                //Other user id but it's a login id these time display my user profile
                mIsMAIN = true;
            }
        }

        //Mapping Toolbar
        mToolbar = (Toolbar) findViewById(R.id.maintoolbar);

        //Mapping textview
        tvTitalname = (TextView) findViewById(R.id.tvUnametital);
        tvFlname = (TextView) findViewById(R.id.tvUserflname);
        tvUsername = (TextView) findViewById(R.id.tvUseraddress);
        tvFollowercount = (TextView) findViewById(R.id.tvfollowerscount);
        tvFollowingcount = (TextView) findViewById(R.id.tvfollowingcount);

        //Mapping Linear layout
        ll_Followers = (LinearLayout) findViewById(R.id.ll_userpf_follow);
        ll_Following = (LinearLayout) findViewById(R.id.ll_userpf_following);

        //Mapping imageview
        ivuserCoverphoto = (ImageView) findViewById(R.id.ivusercoverphoto);
        ivuserProfilephoto = (ImageView) findViewById(R.id.ivuserprofilephoto);
        ivUserphoto = (ImageView) findViewById(R.id.ivUserProfile);
        ivBack = (ImageView) findViewById(R.id.ivbackarrow);
        ivFilterW = (ImageView) findViewById(R.id.ivfilterW);
        ivSettingW = (ImageView) findViewById(R.id.ivsettingW);

        //Mapping button
        btnFollow = (Button) findViewById(R.id.btnfollow);
        btnUnfollow = (Button) findViewById(R.id.btnunfollow);

        //Mapping appbar layout
        mAppBarLayout = (AppBarLayout) findViewById(R.id.main_appbar);

        //Mapping floating action button
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);

        //Mapping recycleview
        mRecyclerView = (RecyclerView) findViewById(R.id.rv_mydishes);
        mRecyclerView.setHasFixedSize(true);

        GridLayoutManager gridLayoutManager = new GridLayoutManager(this, 2);
        mRecyclerView.setLayoutManager(gridLayoutManager);

        mToolbar.setTitle("");
        mAppBarLayout.addOnOffsetChangedListener(this);

        setSupportActionBar(mToolbar);
        collapsingToolbar = (CollapsingToolbarLayout) findViewById(R.id.collapsing_toolbar);

        //set font
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
        ivUserphoto.setVisibility(View.GONE);
        //ivBack.setVisibility(View.GONE);

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (ActivityCompat.checkSelfPermission(mContext, Manifest.permission.CAMERA)
                        != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(UserProfileActivity.this,
                            new String[]{Manifest.permission.CAMERA}, PERMISSION_REQUEST_CODE);
                } else {
                    final File root = new File(Environment.getExternalStorageDirectory() + File.separator + "FoodApp" + File.separator);
                    root.mkdirs();
                    String fname = "img_" + System.currentTimeMillis() + ".jpg";
                    final File sdImageMainDirectory = new File(root, fname);
                    fileUri = Uri.fromFile(sdImageMainDirectory);
                    Constant.openImageIntent(UserProfileActivity.this, fileUri);
                }
            }
        });

        ivBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        ivFilterW.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(UserProfileActivity.this, FilterActivity.class);
                startActivity(intent);
            }
        });

        ivSettingW.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(UserProfileActivity.this, SettingActivity.class);
                startActivity(intent);
            }
        });
    }

    @Override
    protected void onStart() {
        CallUserwebapi();
        super.onStart();
    }

    private void CallUserwebapi() {
        if (!mIsMAIN) {
            //Other user profile
            if (cdObj.isConnectingToInternet()) {    // Check internet connection and perform operation
                new getUserProfile().execute(strOtheruserId);
            } else {
                Generalfunction.Simple1ButtonDialog(mContext.getResources().getString(R.string.Internet_Message), mContext);
            }
        } else {
            //My user profile
            if (cdObj.isConnectingToInternet()) {
                new getUserProfile().execute("");
            } else {
                Generalfunction.Simple1ButtonDialog(mContext.getResources().getString(R.string.Internet_Message), mContext);
            }
        }
    }

    //Handle toolbar visisbility with animation
    @Override
    public void onOffsetChanged(AppBarLayout appBarLayout, int offset) {
        int maxScroll = appBarLayout.getTotalScrollRange();
        float percentage = (float) Math.abs(offset) / (float) maxScroll;

        if (percentage >= PERCENTAGE_TO_SHOW_TITLE_AT_TOOLBAR) {

            //Handle toolbar visisbility with animation
            if (!mIsTheTitleVisible) {
                startAlphaAnimation(tvTitalname, ALPHA_ANIMATIONS_DURATION, View.VISIBLE);
                mIsTheTitleVisible = true;
                mToolbar.setBackgroundColor(ContextCompat.getColor(UserProfileActivity.this, android.R.color.white));
                invalidateOptionsMenu();

                tvTitalname.setVisibility(View.VISIBLE);
                ivUserphoto.setVisibility(View.VISIBLE);
                ivBack.setVisibility(View.GONE);
                ivFilterW.setVisibility(View.GONE);
                ivSettingW.setVisibility(View.GONE);
            }

        } else {
            if (mIsTheTitleVisible) {
                startAlphaAnimation(tvTitalname, ALPHA_ANIMATIONS_DURATION, View.INVISIBLE);
                mIsTheTitleVisible = false;
                mToolbar.setBackgroundColor(ContextCompat.getColor(UserProfileActivity.this, android.R.color.transparent));
                invalidateOptionsMenu();

                tvTitalname.setVisibility(View.GONE);
                ivUserphoto.setVisibility(View.GONE);
                ivBack.setVisibility(View.VISIBLE);
                ivFilterW.setVisibility(View.VISIBLE);
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

        itemFilterB.setVisible(mIsTheTitleVisible);
        itemSettingB.setVisible(mIsTheTitleVisible);
        if (!mIsMAIN) {
            itemSettingB.setVisible(false);
        }
        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.actionFilterProfile || id == R.id.actionFilterProfileB) {
            Intent intent = new Intent(UserProfileActivity.this, FilterActivity.class);
            startActivity(intent);
        } else if (id == android.R.id.home) {
            finish();
        } else if (id == R.id.actionsettingB) {
            Intent intent = new Intent(UserProfileActivity.this, SettingActivity.class);
            startActivity(intent);
        }
        return super.onOptionsItemSelected(item);
    }

    //get my user data
    public class getUserProfile extends AsyncTask<String, Void, String> {

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
            return WebFunctions.getUserprofile(params[0], token);
        }

        @Override
        protected void onPostExecute(String rest) {
            super.onPostExecute(rest);

            //Log.d("getuser", "onPostExecute: "+rest);
            if (rest.equals("error")) {
                Toast.makeText(mContext, "It seems there is a problem. Please try again", Toast.LENGTH_SHORT).show();
            } else {
                UserprofileModel = ParsingFunctions.parseUserprofileModel(rest);
                loadDataRest(UserprofileModel);

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

            supportInvalidateOptionsMenu();
            progress.dismiss();
        }
    }


    public void loadDataRest(final UserprofileModel userprofile) {

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

        //PHONE_NUMBER = userprofile.phone_number;
        tvFollowercount.setText(userprofile.followers_count + " " + mContext.getResources().getString(R.string.followers));
        tvFlname.setText(userprofile.name.toUpperCase());
        tvTitalname.setText(userprofile.name.toUpperCase());
        tvFollowingcount.setText(userprofile.followings_count + " " + mContext.getResources().getString(R.string.following));
        tvUsername.setText(userprofile.username.toUpperCase());

        String userCoverphotoUrl = userprofile.cover_image;
        String userprofilephotoUrl = userprofile.profile_image;

        if (!Generalfunction.isEmptyCheck(userCoverphotoUrl)) {
            Picasso.with(this)
                    .load(userprofile.cover_image)
                    .placeholder(R.drawable.no_picture_sign) //this is optional the image to display while the url image is downloading
                    .error(R.drawable.no_picture_sign)         //this is also optional if some error has occurred in downloading the image this image would be displayed
                    .into(ivuserCoverphoto);
        } else {
            Picasso.with(mContext).load(R.drawable.no_picture_sign)
                    .into(ivuserCoverphoto);
        }

        if (!Generalfunction.isEmptyCheck(userprofilephotoUrl)) {
            Picasso.with(this).load(userprofile.profile_image)
                    .placeholder(R.drawable.no_picture_sign).error(R.drawable.no_picture_sign)
                    .into(ivuserProfilephoto);

            Picasso.with(this).load(userprofile.profile_image)
                    .placeholder(R.drawable.no_picture_sign).error(R.drawable.no_picture_sign)
                    .into(ivUserphoto);
        } else {

            Picasso.with(mContext).load(R.drawable.no_picture_sign)
                    .into(ivuserProfilephoto);
            Picasso.with(mContext).load(R.drawable.no_picture_sign)
                    .into(ivUserphoto);
        }

        tvFollowercount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mContext, FollowActivity.class);
                startActivity(intent);
            }
        });
        tvFollowingcount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mContext, FollowActivity.class);
                startActivity(intent);
            }
        });
    }


    //Load User Dishes
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
            if (!mIsMAIN) {
                holder.rateThisDishImage.setVisibility(View.GONE);
            } else {

                if (mDataset.get(position).IsRatingGiven) {
                    holder.rateThisDishImage.setVisibility(View.GONE);
                    holder.heartImageGrid.setVisibility(View.VISIBLE);
                    holder.ratingGridTv.setVisibility(View.VISIBLE);
                    holder.percentageGridTv.setVisibility(View.VISIBLE);
                } else {
                    holder.rateThisDishImage.setVisibility(View.VISIBLE);
                    holder.heartImageGrid.setVisibility(View.GONE);
                    holder.ratingGridTv.setVisibility(View.GONE);
                    holder.percentageGridTv.setVisibility(View.GONE);
                }
            }
            if (!mIsMAIN) {
                if (!mDataset.get(position).rating.equals("")) {
                    holder.ratingGridTv.setText(mDataset.get(position).rating);
                } else {
                    holder.ratingGridTv.setText("0");
                }
            } else {
                if (!mDataset.get(position).average_rating.equals("")) {
                    holder.ratingGridTv.setText(mDataset.get(position).average_rating);
                } else {
                    holder.ratingGridTv.setText("0");
                }
            }

            Constant.loadFont(mContext, holder.ratingGridTv, "lato.ttf");

            holder.dishNameGridTv.setText(mDataset.get(position).name);


            if (mDataset.get(position).favourited_by) {
                holder.heartImageGrid.setImageDrawable(getResources().getDrawable(R.drawable.solid_heart, mContext.getTheme()));
            } else {
                holder.heartImageGrid.setImageDrawable(getResources().getDrawable(R.drawable.heart, mContext.getTheme()));
            }

            String strfoodimage = mDataset.get(position).image;

            if (!Generalfunction.isEmptyCheck(strfoodimage)) {
                Picasso.with(mContext).load(strfoodimage)
                        .centerCrop()
                        .fit()
                        .placeholder(R.drawable.no_picture_sign).error(R.drawable.no_picture_sign)
                        // .resize(100, 100) // resizes the image to these dimensions (in pixel)
                        //.centerCrop()
                        .into(holder.foodImageGrid);
            } else {
                Picasso.with(mContext).load(R.drawable.no_picture_sign)
                        .fit()
                        .centerInside()
                        .into(holder.foodImageGrid);
            }

            holder.favouriteLLT.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    if (mIsMAIN) {
                        selectedPos = position;
                        if (mDataset.get(position).favourited_by) {
                            new favourtiteDish().execute(mDataset.get(position).id, mContext.getResources().getString(R.string.unfavourite_api));

                        } else {
                            new favourtiteDish().execute(mDataset.get(position).id, mContext.getResources().getString(R.string.favourite_api));
                        }
                    }
                }
            });


            holder.foodImageGrid.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(UserProfileActivity.this, DetailActivity.class);
                    intent.putExtra(Constant.ID, mDataset.get(position).id);
                    startActivity(intent);
                }
            });

            holder.rateThisDishImage.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(UserProfileActivity.this, RateActivity.class);
                    intent.putExtra("dishid", mDataset.get(position).id);
                    intent.putExtra("dishname", mDataset.get(position).name);
                    intent.putExtra("dishprice", mDataset.get(position).price);
                    intent.putExtra("restaurantname", mDataset.get(position).rest_name);
                    intent.putExtra("dishimage", mDataset.get(position).image);
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
            String Message = "";
            try {
                JSONObject jsonObject = new JSONObject(result);
                if (jsonObject.has("success")) {
                    if (jsonObject.getString("success").equalsIgnoreCase("Yes")) {
                        response = true;
                    }
                }
                Message = jsonObject.getString("message");

            } catch (JSONException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }

            if (response) {

                CallUserwebapi();
                Generalfunction.Simple1ButtonDialog(Message, mContext);
            }
        }

    }



 //Profile is as a Other user profile these time Call this for follow
    public class follow_User extends AsyncTask<String, Void, String> {

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
            return WebFunctions.FollowUser(params[0], token, params[1]);
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);

            progress.dismiss();

            if (result.equalsIgnoreCase("true")) {

                if (btnFollow.getVisibility() == View.VISIBLE) {
                    btnFollow.setVisibility(View.GONE);
                    btnUnfollow.setVisibility(View.VISIBLE);
                } else {
                    btnFollow.setVisibility(View.VISIBLE);
                    btnUnfollow.setVisibility(View.GONE);
                }
            } else {
                Generalfunction.Simple1ButtonDialog(result, mContext);
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
                selectedImageUri = (data == null ? true : MediaStore.ACTION_IMAGE_CAPTURE.equals(data.getAction())) ? fileUri : (data == null ? null : data.getData());
                Intent intent = new Intent(mContext, PostActivity.class);

                if (selectedImageUri != null) {
                    intent.putExtra("filename", selectedImageUri.toString());
                    if (isCamera) {
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


    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if (requestCode == PERMISSION_REQUEST_CODE) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Constant.openImageIntent(UserProfileActivity.this, fileUri);
            } else {
                // Your app will not have this permission. Turn off all functions
                // that require this permission or it will force close like your
                // original question
            }
        } else if (requestCode == LOCATION_PERMISSION_REQUEST_CODE) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                //openMap();
                Toast.makeText(UserProfileActivity.this, "open map", Toast.LENGTH_SHORT).show();
            }
        }
    }

}