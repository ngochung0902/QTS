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

import java.io.File;
import java.util.ArrayList;

import model.DishModel;
import model.RestaurantModel;
import parsing.ParsingFunctions;
import parsing.WebFunctions;
import utility.ConnectionDetector;
import utility.Constant;
import utility.GPSTracker;
import utility.Generalfunction;

@TargetApi(21)
public class ProfileActivity extends AppCompatActivity implements AppBarLayout.OnOffsetChangedListener {

    private static final float PERCENTAGE_TO_SHOW_TITLE_AT_TOOLBAR = 0.9f;
    private static final float PERCENTAGE_TO_HIDE_TITLE_DETAILS = 0.3f;
    private static final int ALPHA_ANIMATIONS_DURATION = 200;
    private static final int PERMISSION_REQUEST_CODE = 2;
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 3;
    private static final int CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE = 100;

    private boolean mIsTheTitleVisible = false;
    private boolean mIsTheTitleContainerVisible = true;
    private boolean mIsFollowed = false;
    private boolean mIsMAIN = true;

    Context thisContext;

    String stringLatitude, stringLogitude;

    private String PHONE_NUMBER = "";
    private String BUTTON_ACTION = "";
    private String DISH_BUTTON_ACTION = "";

    private LinearLayout mTitleContainer;
    private AppBarLayout mAppBarLayout;
    private Toolbar mToolbar;
    private TextView mObjectName, mObjectUser_Address, mTitle, mRate, preFollowersView, postFollowersView;
    Button preFollowBTN, postFollowBTN;

    Uri selectedImageUri, fileUri;
    String fileName;
    LinearLayout postFollowLLT, favouriteLLT;
    RecyclerView mRecyclerView;
    ImageView heartImageDetail;

    MyAdapter mAdapter;
    RestaurantModel restaurant = new RestaurantModel();
    ArrayList<DishModel> dishes = new ArrayList<DishModel>();

    ConnectionDetector cdObj;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        thisContext = this;

        //inialize connection detector
        cdObj=new ConnectionDetector(thisContext);

        LocalBroadcastManager.getInstance(this).registerReceiver(mMessageReceiver,
                new IntentFilter("update_restaurant"));


        if (getIntent().getExtras() != null) {
            mIsMAIN = getIntent().getExtras().getBoolean(Constant.isMyprofile);
        }


        bindActivity();
        mRecyclerView = (RecyclerView) findViewById(R.id.my_recycler_view);

        mRecyclerView.setHasFixedSize(true);

        GridLayoutManager gridLayoutManager = new GridLayoutManager(this, 2);
        //mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.setLayoutManager(gridLayoutManager);

        mToolbar.setTitle("");
        mAppBarLayout.addOnOffsetChangedListener(this);

        setSupportActionBar(mToolbar);
        startAlphaAnimation(mTitle, 0, View.INVISIBLE);
        //getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        Log.d("Activity", "onCreate: profile");
        if (!mIsMAIN) {

            // Check internet connection and perform operation
            if (cdObj.isConnectingToInternet()) {
                new getRestaturant().execute(getIntent().getStringExtra(Constant.OtheruserId));
            }
            else{
                Generalfunction.Simple1ButtonDialog(thisContext.getResources().getString(R.string.Internet_Message), thisContext);
            }
        }
        else{
            if (cdObj.isConnectingToInternet()) {
                new getRestaturant().execute();
            }
            else{
                Generalfunction.Simple1ButtonDialog(thisContext.getResources().getString(R.string.Internet_Message), thisContext);
            }
        }
    }

    private void bindActivity() {
        mToolbar = (Toolbar) findViewById(R.id.main_toolbar);
        mTitle = (TextView) findViewById(R.id.main_title);
        mTitleContainer = (LinearLayout) findViewById(R.id.main_linearlayout_title);
        mAppBarLayout = (AppBarLayout) findViewById(R.id.main_appbar);
        mObjectName = (TextView) findViewById(R.id.largeTitleProfileTv);
        mRate = (TextView) findViewById(R.id.dishRatingDetailTv);
        mObjectUser_Address = (TextView) findViewById(R.id.smallFontTv);
        postFollowLLT = (LinearLayout) findViewById(R.id.postFollowLLT);
        favouriteLLT = (LinearLayout) findViewById(R.id.favouriteLLT);

        preFollowBTN = (Button) findViewById(R.id.preFollowBTN);
        postFollowBTN = (Button) findViewById(R.id.postFollowBTN);
        preFollowersView = (TextView) findViewById(R.id.preFollowersView);
        postFollowersView = (TextView) findViewById(R.id.postFollowersView);

        heartImageDetail = (ImageView) findViewById(R.id.heartImageDetail);

        Typeface playfair = Typeface.createFromAsset(getAssets(), "fonts/playfair.otf");
        Typeface lato = Typeface.createFromAsset(getAssets(), "fonts/lato.ttf");
        Typeface latom = Typeface.createFromAsset(getAssets(), "fonts/lato_medium.ttf");
        Typeface latob = Typeface.createFromAsset(getAssets(), "fonts/lato_bold.ttf");
        mTitle.setTypeface(playfair);
        mObjectName.setTypeface(playfair);
        mObjectUser_Address.setTypeface(lato);
        mRate.setTypeface(latom);
        preFollowersView.setTypeface(latob);
        postFollowersView.setTypeface(latob);
        preFollowBTN.setTypeface(lato);
        postFollowBTN.setTypeface(lato);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (ActivityCompat.checkSelfPermission(thisContext, Manifest.permission.CAMERA)
                        != PackageManager.PERMISSION_GRANTED) {
                    // Request missing location permission.
                    ActivityCompat.requestPermissions(ProfileActivity.this,
                            new String[]{Manifest.permission.CAMERA}, PERMISSION_REQUEST_CODE);
                } else {
                    final File root = new File(Environment.getExternalStorageDirectory() + File.separator + "FoodApp" + File.separator);
                    root.mkdirs();
                    String fname = "img_" + System.currentTimeMillis() + ".jpg";
                    final File sdImageMainDirectory = new File(root, fname);
                    fileUri = Uri.fromFile(sdImageMainDirectory);
                    Constant.openImageIntent(ProfileActivity.this, fileUri);
                }
            }
        });
    }

    @Override
    public void onOffsetChanged(AppBarLayout appBarLayout, int offset) {
        int maxScroll = appBarLayout.getTotalScrollRange();
        float percentage = (float) Math.abs(offset) / (float) maxScroll;

        handleAlphaOnTitle(percentage);
        handleToolbarTitleVisibility(percentage);
    }






   /* public class favourtiteDish extends AsyncTask<String, Void, Boolean> {

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
        protected Boolean doInBackground(String... params) {
            return WebFunctions.FavouriteDish(params[0], token, params[1]);
        }

        @Override
        protected void onPostExecute(Boolean result) {
            super.onPostExecute(result);

            progress.dismiss();

            if (DISH_BUTTON_ACTION.equals("FAVOURITE")) {
                //restaurant.favourited_by = false;
            } else {
                //restaurant.favourited_by = true;
            }

            new getRestaturant().execute(getIntent().getStringExtra("id"));
            Toast.makeText(thisContext, "Action has been performed successfully", Toast.LENGTH_SHORT).show();
        }

    }
*/
    public class follow_favourtiteRestaturant extends AsyncTask<String, Void, Boolean> {

        ProgressDialog progress;
        String token;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            SharedPreferences prefs = getSharedPreferences("MY_PREFS", MODE_PRIVATE);
            token = prefs.getString("token", "");
            Log.d("token", token);

            progress= new ProgressDialog(thisContext);
            progress.setMessage("Loading...");
            progress.setCancelable(false);
            progress.show();
        }

        @Override
        protected Boolean doInBackground(String... params) {
            return WebFunctions.FollowRest(params[0], token, params[1]);
        }

        @Override
        protected void onPostExecute(Boolean result) {
            super.onPostExecute(result);

            progress.dismiss();

            if (result) {
                if (BUTTON_ACTION.equals("FOLLOW")) {
                    if (restaurant.followed_by) {
                        restaurant.followed_by = false;
                        Drawable heart = ContextCompat.getDrawable(thisContext, R.drawable.pink_outline_bg);
                        preFollowBTN.setText("Follow");
                        preFollowBTN.setBackground(heart);
                        preFollowBTN.setTextColor(Color.parseColor("#F4BFAD"));
                        postFollowBTN.setText("Follow");
                        postFollowBTN.setBackground(heart);
                        postFollowBTN.setTextColor(Color.parseColor("#F4BFAD"));
                    } else {
                        restaurant.followed_by = true;
                        Drawable heart = ContextCompat.getDrawable(thisContext, R.drawable.pink_round_button);
                        preFollowBTN.setText("Unfollow");
                        preFollowBTN.setBackground(heart);
                        preFollowBTN.setTextColor(Color.parseColor("#ffffff"));
                        postFollowBTN.setText("Unfollow");
                        postFollowBTN.setBackground(heart);
                        postFollowBTN.setTextColor(Color.parseColor("#ffffff"));
                    }
                } else if (BUTTON_ACTION.equals("FAVOURITE")) {
                    if (restaurant.favourited_by) {
                        restaurant.favourited_by = false;
                    } else {
                        restaurant.favourited_by = true;
                    }

                    supportInvalidateOptionsMenu();
                }
                Toast.makeText(thisContext, "Action has been performed successfully", Toast.LENGTH_SHORT).show();
            }
        }
    }

    public class getRestaturant extends AsyncTask<String, Void, String> {

        ProgressDialog progress;
        String token;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            SharedPreferences prefs = getSharedPreferences("MY_PREFS", MODE_PRIVATE);
            token = prefs.getString("token", "");
            Log.d("token", token);

            progress= new ProgressDialog(thisContext);
            progress.setMessage("Loading...");
            progress.setCancelable(false);
            progress.show();
        }

        @Override
        protected String doInBackground(String... params) {
            return WebFunctions.getRestaurant(params[0], token);
        }

        @Override
        protected void onPostExecute(String rest) {
            super.onPostExecute(rest);

            if (rest.equals("error")) {
                Toast.makeText(thisContext, "It seems there is a problem. Please try again", Toast.LENGTH_SHORT).show();
            } else {
                restaurant = ParsingFunctions.parseRestaurantModel(rest);
                loadDataRest(restaurant);

                //Log.d("Count", restaurant.dishes.size() + "");
                if (mAdapter != null) {
                    dishes.clear();
                    dishes.addAll(restaurant.dishes);
                    mAdapter.notifyDataSetChanged();
                } else {
                    dishes = restaurant.dishes;
                    mAdapter = new MyAdapter(dishes);
                    mRecyclerView.setAdapter(mAdapter);
                }
            }

            supportInvalidateOptionsMenu();
            progress.dismiss();
        }
    }

    public class getBackgroundRestaurant extends AsyncTask<String, Void, String> {

        String token;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            SharedPreferences prefs = getSharedPreferences("MY_PREFS", MODE_PRIVATE);
            token = prefs.getString("token", "");
            Log.d("token", token);

        }

        @Override
        protected String doInBackground(String... params) {
            return WebFunctions.getRestaurant(params[0], token);
        }

        @Override
        protected void onPostExecute(String rest) {
            super.onPostExecute(rest);

            if (!rest.equals("error")) {
                restaurant = ParsingFunctions.parseRestaurantModel(rest);
                loadDataRest(restaurant);

                //Log.d("Count", restaurant.dishes.size() + "");
                if (mAdapter != null) {
                    dishes.clear();
                    dishes.addAll(restaurant.dishes);
                    mAdapter.notifyDataSetChanged();
                } else {
                    dishes = restaurant.dishes;
                    mAdapter = new MyAdapter(dishes);
                    mRecyclerView.setAdapter(mAdapter);
                }

            }
        }
    }

    public void openMap() {
        GPSTracker gpsTracker = new GPSTracker(this);

        /*if (gpsTracker.getIsGPSTrackingEnabled()) {
            stringLatitude = String.valueOf(gpsTracker.getLatitude());
            stringLogitude = String.valueOf(gpsTracker.getLongitude());
        }*/

        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("http://maps.google.com/maps?" + "saddr=" + stringLatitude + "," + stringLogitude + "&daddr=" + restaurant.latitude + "," + restaurant.longitude));
        intent.setClassName("com.google.android.apps.maps", "com.google.android.maps.MapsActivity");
        startActivity(intent);
    }

    public void loadDataRest (final RestaurantModel restaurant) {
        mRate.setText(restaurant.rating);
        PHONE_NUMBER = restaurant.phone_number;
        postFollowersView.setText(restaurant.followers_count + " FOLLOWERS");
        mObjectName.setText(restaurant.name);
        mTitle.setText(restaurant.name);
        preFollowersView.setText(restaurant.followers_count + " FOLLOWERS");
        mObjectUser_Address.setText(restaurant.address);
        mObjectUser_Address.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ActivityCompat.checkSelfPermission(thisContext, Manifest.permission.ACCESS_COARSE_LOCATION)
                        != PackageManager.PERMISSION_GRANTED)  {
                    ActivityCompat.requestPermissions(ProfileActivity.this,
                            new String[]{Manifest.permission.ACCESS_COARSE_LOCATION},
                            LOCATION_PERMISSION_REQUEST_CODE);
                } else {
                    openMap();
                }

            }
        });

        if (restaurant.followed_by) {
            Drawable heart = ContextCompat.getDrawable(thisContext, R.drawable.pink_round_button);
            preFollowBTN.setText("Unfollow");
            preFollowBTN.setBackground(heart);
            preFollowBTN.setTextColor(Color.parseColor("#ffffff"));
            postFollowBTN.setText("Unfollow");
            postFollowBTN.setBackground(heart);
            postFollowBTN.setTextColor(Color.parseColor("#ffffff"));
        }

        preFollowBTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                BUTTON_ACTION = "FOLLOW";
                if (restaurant.followed_by) {
                    new follow_favourtiteRestaturant().execute(restaurant.id, "unfollow");
                } else {
                    new follow_favourtiteRestaturant().execute(restaurant.id, "follow");
                }
            }
        });
        postFollowBTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                BUTTON_ACTION = "FOLLOW";
                if (restaurant.followed_by) {
                    new follow_favourtiteRestaturant().execute(restaurant.id, "unfollow");
                } else {
                    new follow_favourtiteRestaturant().execute(restaurant.id, "follow");
                }
            }
        });
        preFollowersView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(thisContext, FollowActivity.class);
                startActivity(intent);
            }
        });
        postFollowersView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(thisContext, FollowActivity.class);
                startActivity(intent);
            }
        });
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
            // - get element from your dataset at this position
            // - replace the contents of the view with that element
            //holder.mTextView.setText(mDataset[position]);

            if (!mIsMAIN) {
                holder.rateThisDishImage.setVisibility(View.GONE);
            }



            if (!mDataset.get(position).average_rating.equals("")) {
                holder.ratingGridTv.setText(mDataset.get(position).average_rating);
            } else {
                holder.ratingGridTv.setText("0");
            }
            Constant.loadFont(thisContext, holder.ratingGridTv, "lato.ttf");

            holder.dishNameGridTv.setText(mDataset.get(position).name);
            //holder.dishVotesGridTv.setText();
            //holder.percentageGridTv.setText("");

            if (mDataset.get(position).favourited_by) {
                holder.heartImageGrid.setImageDrawable(getResources().getDrawable(R.drawable.solid_heart, thisContext.getTheme()));
            } else {
                holder.heartImageGrid.setImageDrawable(getResources().getDrawable(R.drawable.heart, thisContext.getTheme()));
            }

            /*holder.favouriteLLT.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mDataset.get(position).favourited_by) {
                        new favourtiteDish().execute(mDataset.get(position).id, "unfavourite");
                        DISH_BUTTON_ACTION = "FAVOURITE";
                    } else {
                        DISH_BUTTON_ACTION = "UNFAVOURITE";
                        new favourtiteDish().execute(mDataset.get(position).id, "favourite");
                    }
                }
            });*/

            holder.rateThisDishImage.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(ProfileActivity.this, RateActivity.class);
                    startActivity(intent);
                }
            });

            holder.foodImageGrid.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(ProfileActivity.this, DetailActivity.class);
                    intent.putExtra("dish", mDataset.get(position));
                    startActivity(intent);
                }
            });

            holder.rateThisDishImage.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(ProfileActivity.this, RateActivity.class);
                    intent.putExtra("dish", mDataset.get(position));
                    intent.putExtra("restaurant", restaurant.name);
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

    public class ProfileAdapter extends BaseAdapter {
        private Context mContext;
        LayoutInflater inflater;

        public ProfileAdapter(Context c) {
            mContext = c;
            this.inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        }

        public int getCount() {
            return 20;
        }

        public Object getItem(int position) {
            return null;
        }

        public long getItemId(int position) {
            return 0;
        }

        // create a new ImageView for each item referenced by the Adapter
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder holder;


            if (convertView == null) {
                holder = new ViewHolder();
                convertView = inflater.inflate(R.layout.grid_list_item, parent, false);
                holder.rateNumber = (TextView) convertView.findViewById(R.id.ratingGridTv);
                holder.rateThisDishImage = (ImageView) convertView.findViewById(R.id.rateThisDishImage);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }
            holder.rateNumber.setText("94");
            Typeface lato = Typeface.createFromAsset(getAssets(), "fonts/lato.ttf");
            holder.rateNumber.setTypeface(lato);



            return convertView;
        }

        class ViewHolder {

            TextView rateNumber;
            ImageView rateThisDishImage;
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_profile, menu);

        MenuItem itemCall = menu.findItem(R.id.actionCallNummber);
        MenuItem itemCallB = menu.findItem(R.id.actionCallProfileB);
        MenuItem itemFilter = menu.findItem(R.id.actionFilterProfile);
        MenuItem itemFilterB = menu.findItem(R.id.actionFilterProfileB);
        MenuItem itemfavourited = menu.findItem(R.id.favouriteFilledProfile);
        MenuItem itemToFavouriteWhite = menu.findItem(R.id.favouriteWhiteProfile);
        MenuItem itemToFavourite = menu.findItem(R.id.favouriteProfile);

        itemCall.setVisible(!mIsTheTitleVisible);
        itemCallB.setVisible(mIsTheTitleVisible);
        itemFilter.setVisible(!mIsTheTitleVisible);
        itemFilterB.setVisible(mIsTheTitleVisible);
        itemfavourited.setVisible(restaurant.favourited_by);
        itemToFavouriteWhite.setVisible(!mIsTheTitleVisible && !restaurant.favourited_by);
        itemToFavourite.setVisible(mIsTheTitleVisible && !restaurant.favourited_by);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.actionFilterProfile || id == R.id.actionFilterProfileB) {
            Intent intent = new Intent(ProfileActivity.this, FilterActivity.class);
            startActivity(intent);
        } else if (id == R.id.actionCallNummber || id == R.id.actionCallNummber) {
            if (ActivityCompat.checkSelfPermission(ProfileActivity.this, Manifest.permission.CALL_PHONE)
                    != PackageManager.PERMISSION_GRANTED) {
                // Request missing location permission.
                ActivityCompat.requestPermissions(ProfileActivity.this,
                        new String[]{Manifest.permission.CALL_PHONE},
                        2);
            } else if (!PHONE_NUMBER.equals("")) {
                Intent intent = new Intent(Intent.ACTION_CALL);
                intent.setData(Uri.parse("tel:" + PHONE_NUMBER));
                startActivity(intent);
            }

        } else if (id == android.R.id.home) {
            finish();
        } else if (id == R.id.favouriteFilledProfile) {
            BUTTON_ACTION = "FAVOURITE";
            new follow_favourtiteRestaturant().execute(restaurant.id, "unfavourite");
        } else if (id == R.id.favouriteWhiteProfile) {
            BUTTON_ACTION = "FAVOURITE";
            new follow_favourtiteRestaturant().execute(restaurant.id, "favourite");
        } else if (id == R.id.favouriteProfile) {
            BUTTON_ACTION = "FAVOURITE";
            new follow_favourtiteRestaturant().execute(restaurant.id, "favourite");
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if (requestCode == PERMISSION_REQUEST_CODE) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Constant.openImageIntent(ProfileActivity.this, fileUri);
            }
            else {
                // Your app will not have this permission. Turn off all functions
                // that require this permission or it will force close like your
                // original question
            }
        } else if (requestCode == LOCATION_PERMISSION_REQUEST_CODE){
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                openMap();
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            if (requestCode == CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE) {
                selectedImageUri = (data == null ? true : MediaStore.ACTION_IMAGE_CAPTURE.equals(data.getAction())) ? fileUri : (data == null ? null : data.getData());
                Intent intent  = new Intent(thisContext, PostActivity.class);
                if (selectedImageUri != null) {
                    intent.putExtra("filename", selectedImageUri.toString());
                    intent.putExtra("isCamera", false);
                } else {
                    intent.putExtra("filename", fileUri.getPath());
                    intent.putExtra("isCamera", true);
                }
                intent.putExtra("isCameraOrGallery", true);
                startActivity(intent);
            }
        }
    }

    private void handleToolbarTitleVisibility(float percentage) {

        Resources r = thisContext.getResources();
        int px = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,60,r.getDisplayMetrics());

        LayoutParams params = new LayoutParams( LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);

        if (percentage >= PERCENTAGE_TO_SHOW_TITLE_AT_TOOLBAR) {

            if (!mIsTheTitleVisible) {
                startAlphaAnimation(mTitle, ALPHA_ANIMATIONS_DURATION, View.VISIBLE);
                mIsTheTitleVisible = true;
                mToolbar.setBackgroundColor(ContextCompat.getColor(ProfileActivity.this, android.R.color.white));
                invalidateOptionsMenu();
                startAlphaAnimation(postFollowLLT, ALPHA_ANIMATIONS_DURATION, View.VISIBLE);
                //postFollowLLT.setVisibility(View.VISIBLE);
                //params.setMargins(0, px, 0, 0);
                //mRecyclerView.setLayoutParams(params);

            }

        } else {

            if (mIsTheTitleVisible) {
                startAlphaAnimation(mTitle, ALPHA_ANIMATIONS_DURATION, View.INVISIBLE);
                mIsTheTitleVisible = false;
                mToolbar.setBackgroundColor(ContextCompat.getColor(ProfileActivity.this, android.R.color.transparent));
                invalidateOptionsMenu();
                startAlphaAnimation(postFollowLLT, ALPHA_ANIMATIONS_DURATION, View.INVISIBLE);
                //postFollowLLT.setVisibility(View.GONE);
                //params.setMargins(0, 0, 0, 0);
                //mRecyclerView.setLayoutParams(params);

            }
        }
    }

    private void handleAlphaOnTitle(float percentage) {
        if (percentage >= PERCENTAGE_TO_HIDE_TITLE_DETAILS) {
            if (mIsTheTitleContainerVisible) {
                startAlphaAnimation(mTitleContainer, ALPHA_ANIMATIONS_DURATION, View.INVISIBLE);
                mIsTheTitleContainerVisible = false;
            }

        } else {

            if (!mIsTheTitleContainerVisible) {
                startAlphaAnimation(mTitleContainer, ALPHA_ANIMATIONS_DURATION, View.VISIBLE);
                mIsTheTitleContainerVisible = true;
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

    public static final Drawable getDrawable(Context context, int id) {
        final int version = Build.VERSION.SDK_INT;
        if (version >= 21) {
            return ContextCompat.getDrawable(context, id);
        } else {
            return context.getResources().getDrawable(id);
        }
    }

    private BroadcastReceiver mMessageReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String id = intent.getStringExtra("id");
            new getBackgroundRestaurant().execute(id);
        }
    };

    @Override
    protected void onDestroy() {
        // Unregister since the activity is about to be closed.
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mMessageReceiver);
        super.onDestroy();
    }
}