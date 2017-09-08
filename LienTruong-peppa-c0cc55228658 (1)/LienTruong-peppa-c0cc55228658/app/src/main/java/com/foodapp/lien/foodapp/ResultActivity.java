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
import android.graphics.Typeface;
import android.location.Location;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.util.ArrayList;

import model.DishModel;
import model.RestaurantModel;
import parsing.ParsingFunctions;
import parsing.WebFunctions;
import utility.ConnectionDetector;
import utility.GPSTracker;
import utility.Constant;
import utility.Generalfunction;
import utility.MyGPSTracker;

@TargetApi(21)
public class ResultActivity extends AppCompatActivity implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {

    LinearLayout dishLLT;
    Context thisContext;
    private static final int PERMISSION_REQUEST_CODE = 2;
    private static final int FILTER_REQUEST_CODE = 200;
    private static final int CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE = 100;
    Uri selectedImageUri, fileUri;
    String fileName;


    ViewPager mViewPager;
    RecyclerView mRecyclerView;
    LinearLayoutManager mLayoutManager;

    ArrayList<RestaurantModel> restaurants = new ArrayList<RestaurantModel>();
    ArrayList<DishModel> Disheslist = new ArrayList<DishModel>();
    MyAdapter mAdapter;

    String dish_id = "";
    String DISH_BUTTON_ACTION = "";
    String current_sort_selection = "";
    String current_search = "";

    ProgressDialog progress;
    ConnectionDetector cdObj;
    GoogleApiClient mGoogleApiClient;
    String strCurrentLatitude,strCurrentlongitude;
    private MyGPSTracker gps;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // set layout in contentview
        setContentView(R.layout.activity_result);

        //set toolbar and actionbar
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.back);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        thisContext = this;
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        cdObj=new ConnectionDetector(thisContext);

        // Google api client
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addApi(LocationServices.API)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build();

        //  LocalBroadcastManager.getInstance(thisContext).registerReceiver(mMessageReceiver, new IntentFilter("update_restaurant"));

        //Mapping textview
        TextView tt = (TextView) findViewById(R.id.toolbartitle);
        TextView restaurant = (TextView) findViewById(R.id.restaurantTv);
        TextView dish = (TextView) findViewById(R.id.dishTv);

        Typeface latomedium = Typeface.createFromAsset(getAssets(), "fonts/lato_medium.ttf");
        tt.setTypeface(latomedium);
        restaurant.setTypeface(latomedium);
        dish.setTypeface(latomedium);

        //RecyclerView mRecyclerView = (RecyclerView) findViewById(R.id.my_recycler_view);
        dishLLT = (LinearLayout) findViewById(R.id.dishLLT);

        //Mapping pager which have a reasturant
        mViewPager = (ViewPager) findViewById(R.id.pager);

        //Mapping recycle view which have Dishes
        mRecyclerView = (RecyclerView)findViewById(R.id.recycler_view);

        //Mapping floating action button
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (ActivityCompat.checkSelfPermission(thisContext, Manifest.permission.CAMERA)!= PackageManager.PERMISSION_GRANTED) {
                    // Request missing location permission.
                    ActivityCompat.requestPermissions(ResultActivity.this,new String[]{Manifest.permission.CAMERA}, Constant.PERMISSION_REQUEST_CODE_Camera);
                } else {
                    final File root = new File(Environment.getExternalStorageDirectory() + File.separator + "FoodApp" + File.separator);
                    root.mkdirs();
                    String fname = "img_" + System.currentTimeMillis() + ".jpg";
                    final File sdImageMainDirectory = new File(root, fname);
                    fileUri = Uri.fromFile(sdImageMainDirectory);
                    Constant.openImageIntent(ResultActivity.this, fileUri);
                }
            }
        });

        mViewPager.setClipToPadding(false);
        mViewPager.setPadding(110, 0, 110, 0);
        mViewPager.setPageMargin(60);
        mViewPager.setPageMarginDrawable(R.color.colorBacker);

        current_search = getIntent().getStringExtra(Constant.Search);

    }

    @Override
    protected void onStart() {
        // Check internet connection and perform operation
        if (cdObj.isConnectingToInternet()) {
            new getSearchResult().execute(current_search, Constant.mSTR_SORT_PRICE, Constant.mSTR_DIRECTION_ASCENDING);
        }
        super.onStart();
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Google api client
        mGoogleApiClient.connect();

        if(cdObj.isConnectingToInternet()) {
            // ivNointernet.setVisibility(View.GONE);
            mGoogleApiClient.connect();
        }
        else{
            Generalfunction.Simple1ButtonDialog(thisContext.getResources().getString(R.string.Internet_Message), thisContext);
        }

        try {
            gps = new MyGPSTracker(this);
            if (gps.canGetLocation()) {
                Location loc = gps.LocationUpdateAtRegularInterval();
                //System.out.println("location2323"+loc);
                if (loc != null) {
                    strCurrentLatitude = String.valueOf(loc.getLatitude());
                    strCurrentlongitude =String.valueOf(loc.getLongitude());
                }
                //AddDriverLocation()
            } else {
                gps.showSettingsAlert();
            }

        } catch (Exception e) {
            //Toast.makeText(MapScreenDefault.this, "Exception e"+e, Toast.LENGTH_SHORT).show();
        }

    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        Location mLastLocation = LocationServices.FusedLocationApi.getLastLocation(
                mGoogleApiClient);
        if (mLastLocation != null) {
            strCurrentLatitude=String.valueOf(mLastLocation.getLatitude());
            strCurrentlongitude=String.valueOf(mLastLocation.getLatitude());
        }
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }


    // Async task which show search result
    public class getSearchResult extends AsyncTask<String, Void, String> {

        String token, local_current_selectio = "";

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
            local_current_selectio = params[1];
            return WebFunctions.getSearchResult(params[0], params[1], params[2], token);
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);

            progress.dismiss();

            if (s.equals("error")) {
                Toast.makeText(ResultActivity.this, "error", Toast.LENGTH_SHORT).show();

            } else {
                current_sort_selection = local_current_selectio;

                restaurants=new ArrayList<>();
                Disheslist=new ArrayList<>();

                restaurants = ParsingFunctions.parseSearchOBJ(s).restaurants;
                Disheslist = ParsingFunctions.parseSearchOBJ(s).dishes;

                if(restaurants.size()>0){
                    //shaow reasturant list
                    loadRestaurantData(restaurants);
                }

                if(Disheslist.size()>0){
                    // Show dish list

                    loadDishData(Disheslist);

                    /*if (mAdapter != null) {
                        mAdapter.notifyDataSetChanged();
                    } else {

                        mLayoutManager = new LinearLayoutManager(thisContext);
                        mRecyclerView.setLayoutManager(mLayoutManager);

                        MyAdapter mAdapter = new MyAdapter(thisContext);
                        mRecyclerView.setAdapter(mAdapter);
                    }*/
                    //loadDishData(dishes);
                }

            }
        }
    }



    // show restaurant list
    public void loadRestaurantData(final ArrayList<RestaurantModel> restArray) {

        CustomPagerAdapter mCustomPagerAdapter = new CustomPagerAdapter(thisContext, restArray);
        mViewPager.setAdapter(mCustomPagerAdapter);

    }

    /***************** bind Restaurant list detail with addapter ********************/

    class CustomPagerAdapter extends PagerAdapter {

        Context mContext;
        LayoutInflater mLayoutInflater;
        ArrayList<RestaurantModel> restaurants;

        public CustomPagerAdapter(Context context, ArrayList<RestaurantModel> restaurant) {
            mContext = context;
            restaurants = restaurant;
            mLayoutInflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        }

        @Override
        public int getCount() {
            return restaurants.size();
        }

        @Override
        public float getPageWidth(int position) {
            if (position == 0 || position == (getCount() - 1)) {
                return 0.98f;
            }
            return 0.98f;
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view == ((LinearLayout) object);
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            final int j = position;
            View itemView = mLayoutInflater.inflate(R.layout.restaurant_result, container, false);

            TextView callNumberView = (TextView) itemView.findViewById(R.id.restaurantNumberResultTv);
            TextView addressView = (TextView) itemView.findViewById(R.id.addressResultTv);
            TextView restaurantName = (TextView) itemView.findViewById(R.id.restaurantNameResultTv);
            TextView rateNumber = (TextView) itemView.findViewById(R.id.dishRatingRResultTv);
            TextView votesView = (TextView) itemView.findViewById(R.id.dishRatingPercentRResultTv);
            ImageView ivRestorantPhoto=(ImageView) itemView.findViewById(R.id.imageViewRestauarntResult);
            ImageView ivRestorantfavourite=(ImageView) itemView.findViewById(R.id.heartImageRResult);

            Constant.loadFont(mContext, callNumberView, "lato_medium.ttf");
            Constant.loadFont(mContext, votesView, "lato_medium.ttf");
            Constant.loadFont(mContext, rateNumber, "lato_bold.ttf");
            Constant.loadFont(mContext, addressView, "lato_medium.ttf");
            Constant.loadFont(mContext, restaurantName, "lato_medium.ttf");

            callNumberView.setText(restaurants.get(position).phone_number);
            addressView.setText(restaurants.get(position).address);
            restaurantName.setText(restaurants.get(position).name);
            rateNumber.setText(restaurants.get(position).rating);

            if (restaurants.get(position).favourited_by) {
                ivRestorantfavourite.setImageDrawable(getResources().getDrawable(R.drawable.solid_heart, mContext.getTheme()));
            } else {
                ivRestorantfavourite.setImageDrawable(getResources().getDrawable(R.drawable.heart, mContext.getTheme()));
            }

            LinearLayout restaurantView = (LinearLayout) itemView.findViewById(R.id.restauarentView);
            LinearLayout callView = (LinearLayout) itemView.findViewById(R.id.callRestauratBTN);
            restaurantView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(ResultActivity.this, RestaurantProfileActivity.class);
                    intent.putExtra(Constant.ID, restaurants.get(j).id);
                    startActivity(intent);
                }
            });

            String strRestorantProfile=restaurants.get(position).restaurant_image;
            if(!Generalfunction.isEmptyCheck(strRestorantProfile)){
                Picasso.with(thisContext).load(strRestorantProfile)
                        .placeholder(R.drawable.image_loading).error(R.drawable.img_not_available)
                        .into(ivRestorantPhoto);

            }
            else{
                Picasso.with(thisContext).load(R.drawable.no_picture_sign)
                        .into(ivRestorantPhoto);
            }

            callView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    if (ActivityCompat.checkSelfPermission(ResultActivity.this, Manifest.permission.CALL_PHONE)
                            != PackageManager.PERMISSION_GRANTED) {
                        // Request missing location permission.
                        ActivityCompat.requestPermissions(ResultActivity.this,
                                new String[]{Manifest.permission.CALL_PHONE},
                                2);
                    } else {
                        Intent intent = new Intent(Intent.ACTION_CALL);
                        intent.setData(Uri.parse("tel:" + restaurants.get(j).phone_number));
                        startActivity(intent);
                    }

                }
            });

            container.addView(itemView);

            return itemView;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView((LinearLayout) object);
        }
    }

    /***************** Show dish list detail ********************/

    public void loadDishData (final ArrayList<DishModel> dishes) {
        dishLLT.removeAllViews();
        LayoutInflater layoutInflater = (LayoutInflater) thisContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        for (int i = 0; i < dishes.size(); i++) {
            final int j = i;

            View view = layoutInflater.inflate(R.layout.dish_result, null );
            TextView callView = (TextView) view.findViewById(R.id.restaurantCallResultTv);
            TextView gpsView = (TextView) view.findViewById(R.id.restaurantAddressResultTv);
            ImageView dishImageDetail = (ImageView) view.findViewById(R.id.dishImageDetail);
            ImageView ivfavourite = (ImageView) view.findViewById(R.id.heartImageDetail);
            TextView restaurantName = (TextView) view.findViewById(R.id.restaurantNameDetailTv);
            TextView rateNumber = (TextView) view.findViewById(R.id.dishRatingDetailTv);
            TextView dishName = (TextView) view.findViewById(R.id.dishNameDetailTv);
            TextView votes = (TextView) view.findViewById(R.id.voteCountDetailTv);
            TextView dishPrice = (TextView) view.findViewById(R.id.dishPriceTv);

            Constant.loadFont(thisContext, callView, "lato_medium.ttf");
            Constant.loadFont(thisContext, gpsView, "lato.ttf");
            Constant.loadFont(thisContext, restaurantName, "lato_medium.ttf");
            Constant.loadFont(thisContext, rateNumber, "lato.ttf");
            Constant.loadFont(thisContext, dishName, "playfair_regular.otf");
            Constant.loadFont(thisContext, votes, "lato.ttf");
            Constant.loadFont(thisContext, dishPrice, "lato.ttf");

            //callView.setText(dishes.get(i).phone_number);
            restaurantName.setText(dishes.get(i).rest_name);
            dishName.setText(dishes.get(i).name);
            dishPrice.setText("$" + dishes.get(i).price);
            rateNumber.setText(dishes.get(i).average_rating);
            gpsView.setText(dishes.get(i).rest_address);
           // Log.d("address", dishes.get(i).rest_address + " ----");
           // Log.d("name", dishes.get(i).rest_name + " ----");
            votes.setText(dishes.get(i).votes +" "+thisContext.getResources().getString(R.string.votetext));

            if (Disheslist.get(i).favourited_by) {
                ivfavourite.setImageDrawable(getResources().getDrawable(R.drawable.solid_heart, thisContext.getTheme()));
            } else {
                ivfavourite.setImageDrawable(getResources().getDrawable(R.drawable.heart, thisContext.getTheme()));
            }

            String strDishImage=Disheslist.get(i).image;
            if(!Generalfunction.isEmptyCheck(strDishImage)){
                Picasso.with(thisContext).load(strDishImage)
                        .placeholder(R.drawable.image_loading).error(R.drawable.img_not_available)
                        .into(dishImageDetail);
            }
            else{
                Picasso.with(thisContext).load(R.drawable.img_not_available)
                        .into(dishImageDetail);
            }

            callView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (ActivityCompat.checkSelfPermission(thisContext, Manifest.permission.CALL_PHONE)
                            != PackageManager.PERMISSION_GRANTED) {
                        // Request missing location permission.
                        ActivityCompat.requestPermissions(ResultActivity.this,
                                new String[]{Manifest.permission.CALL_PHONE},
                                2);
                    } else {
                        Intent intent = new Intent(Intent.ACTION_CALL);
                        intent.setData(Uri.parse("tel:" + dishes.get(j).rest_phone_number));
                        startActivity(intent);
                    }
                }
            });

            gpsView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (ActivityCompat.checkSelfPermission(thisContext, Manifest.permission.ACCESS_COARSE_LOCATION)
                            != PackageManager.PERMISSION_GRANTED)  {
                        ActivityCompat.requestPermissions(ResultActivity.this,
                                new String[]{Manifest.permission.ACCESS_COARSE_LOCATION},
                                Constant.PERMISSION_REQUEST_CODE_Location);
                    } else {
                        Generalfunction.OpenMap(dishes.get(j).rest_latitude, dishes.get(j).rest_longitude,thisContext);
                    }

                }
            });

            dishImageDetail.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //Log.d("dish id", "onClick: "+dishes.get(j).id);
                    Intent intent = new Intent(thisContext, DetailActivity.class);

                    //we pass dish id
                    intent.putExtra(Constant.ID, dishes.get(j).id);
                    startActivity(intent);
                }
            });

            dishLLT.addView(view);
        }
    }


    public class MyAdapter extends RecyclerView.Adapter<MyAdapter.ViewHolder> {

        private Context context;

        public class ViewHolder extends RecyclerView.ViewHolder {
            // each data item is just a string in this case

            TextView callView,gpsView,restaurantName,rateNumber,dishName,votes,dishPrice;
            ImageView dishImageDetail,heartImageDetail;
            LinearLayout resultHeartLLT;

            public ViewHolder(View view) {
                super(view);

                callView = (TextView) view.findViewById(R.id.restaurantCallResultTv);
                gpsView = (TextView) view.findViewById(R.id.restaurantAddressResultTv);
                dishImageDetail = (ImageView) view.findViewById(R.id.dishImageDetail);
                heartImageDetail = (ImageView) view.findViewById(R.id.heartImageDetail);
                resultHeartLLT = (LinearLayout) view.findViewById(R.id.resultHeartLLT);
                restaurantName = (TextView) view.findViewById(R.id.restaurantNameDetailTv);
                rateNumber = (TextView) view.findViewById(R.id.dishRatingDetailTv);
                dishName = (TextView) view.findViewById(R.id.dishNameDetailTv);
                votes = (TextView) view.findViewById(R.id.voteCountDetailTv);
                dishPrice = (TextView) view.findViewById(R.id.dishPriceTv);
            }
        }

        @Override
        public MyAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            // create a new view
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.dish_result, parent, false);
            ViewHolder vh = new ViewHolder(v);
            return vh;
        }

        // Provide a suitable constructor (depends on the kind of dataset)
        public MyAdapter(Context context) {
            this.context = context;
        }

        // Replace the contents of a view (invoked by the layout manager)
        @Override
        public void onBindViewHolder(ViewHolder holder, final int position) {
            // - get element from your dataset at this position
            // - replace the contents of the view with that element

            Constant.loadFont(thisContext, holder.callView, "lato_medium.ttf");
            Constant.loadFont(thisContext, holder.gpsView, "lato.ttf");
            Constant.loadFont(thisContext, holder.restaurantName, "lato_medium.ttf");
            Constant.loadFont(thisContext, holder.rateNumber, "lato.ttf");
            Constant.loadFont(thisContext, holder.dishName, "playfair_regular.otf");
            Constant.loadFont(thisContext, holder.votes, "lato.ttf");
            Constant.loadFont(thisContext, holder.dishPrice, "lato.ttf");

            //callView.setText(dishes.get(i).phone_number);
            holder.restaurantName.setText(Disheslist.get(position).rest_name);
            holder.dishName.setText(Disheslist.get(position).name);
            holder.dishPrice.setText("$" + Disheslist.get(position).price);
            holder.rateNumber.setText(Disheslist.get(position).average_rating);
            holder.gpsView.setText(Disheslist.get(position).rest_address);
          //  Log.d("address", disheslist.get(i).address + " ----");
           // Log.d("name", disheslist.get(i).restaurant_name + " ----");

            String strDishImage=Disheslist.get(position).image;
            if(!Generalfunction.isEmptyCheck(strDishImage)){
                Picasso.with(thisContext).load(strDishImage)
                        .placeholder(R.drawable.image_loading).error(R.drawable.img_not_available)
                        .into(holder.dishImageDetail);
            }
            else{
                Picasso.with(thisContext).load(R.drawable.no_picture_sign)
                        .into(holder.dishImageDetail);
            }

            holder.callView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (ActivityCompat.checkSelfPermission(thisContext, Manifest.permission.CALL_PHONE)
                            != PackageManager.PERMISSION_GRANTED) {
                        // Request missing location permission.
                        ActivityCompat.requestPermissions(ResultActivity.this,
                                new String[]{Manifest.permission.CALL_PHONE},
                                2);
                    } else {
                        Intent intent = new Intent(Intent.ACTION_CALL);
                        intent.setData(Uri.parse("tel:" + Disheslist.get(position).rest_phone_number));
                        startActivity(intent);
                    }
                }
            });

            holder.gpsView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (ActivityCompat.checkSelfPermission(thisContext, Manifest.permission.ACCESS_COARSE_LOCATION)
                            != PackageManager.PERMISSION_GRANTED)  {
                        ActivityCompat.requestPermissions(ResultActivity.this,
                                new String[]{Manifest.permission.ACCESS_COARSE_LOCATION},
                                Constant.PERMISSION_REQUEST_CODE_Location);
                    } else {
                        Generalfunction.OpenMap(Disheslist.get(position).rest_latitude, Disheslist.get(position).rest_longitude,thisContext);
                    }

                }
            });

            holder.dishImageDetail.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(thisContext, DetailActivity.class);
                    intent.putExtra("dish", Disheslist.get(position).id);
                    startActivity(intent);
                }
            });

        }

        // Return the size of your dataset (invoked by the layout manager)
        @Override
        public int getItemCount() {
            return Disheslist.size();
        }
    }



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_result, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.actionMapResult) {
            Intent intent = new Intent(ResultActivity.this, MapsActivity.class);
            intent.putExtra("dishes", Disheslist);
            startActivity(intent);
        } else if (id == R.id.actionFilterResult) {
            Intent intent = new Intent(ResultActivity.this, FilterActivity.class);
            if (current_sort_selection.equals(Constant.mSTR_SORT_DISTANCE)) {
                intent.putExtra("sort", Constant.mSTR_SORT_DISTANCE);
            } else if (current_sort_selection.equals(Constant.mSTR_SORT_PRICE)) {
                intent.putExtra("sort", Constant.mSTR_SORT_PRICE);
            } else if (current_sort_selection.equals(Constant.mSTR_SORT_RATING)) {
                intent.putExtra("sort", Constant.mSTR_SORT_RATING);
            }
            startActivityForResult(intent, FILTER_REQUEST_CODE);
        } else if (id == android.R.id.home) {
            finish();
        }

        return super.onOptionsItemSelected(item);
    }

    // Check Request permission

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if (requestCode == Constant.PERMISSION_REQUEST_CODE_Camera) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Constant.openImageIntent(ResultActivity.this, fileUri);
            }
            else if (requestCode == Constant.PERMISSION_REQUEST_CODE_Location){
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    //openMap();
                    Toast.makeText(ResultActivity.this, "open map", Toast.LENGTH_SHORT).show();
                }
            }
            else {
            }
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
                Intent intent  = new Intent(thisContext, PostActivity.class);

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

            } else if (requestCode == FILTER_REQUEST_CODE) {
                if (data != null) {
                    new getSearchResult().execute(current_search, data.getExtras().getString("sort"), Constant.mSTR_DIRECTION_ASCENDING);
                }
            }
        }
    }

    /*public void openMap(String dLAT, String dLONG) {

        Log.d("Resultactivity", "openMap: ");
        if(strCurrentLatitude==null){
            Log.d("Resultactivity", "openMap: current latititude null");
        }
        else{
            Log.d("Resultactivity", "openMap: dtal long");
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("http://maps.google.com/maps?" + "saddr=" + strCurrentLatitude + "," + strCurrentlongitude + "&daddr=" + dLAT + "," + dLONG));
            intent.setClassName("com.google.android.apps.maps", "com.google.android.maps.MapsActivity");
            startActivity(intent);
        }

    }*/

  /*  private BroadcastReceiver mMessageReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            new getBackgroundsSearchResult().execute(current_search, current_search, Constant.mSTR_DIRECTION_ASCENDING);
        }
    };*/



/*
 // broadcast manager update restaurant
 public class getBackgroundsSearchResult extends AsyncTask<String, Void, String> {

     String token = "";

     @Override
     protected void onPreExecute() {
         super.onPreExecute();

         SharedPreferences prefs = thisContext.getSharedPreferences("MY_PREFS", thisContext.MODE_PRIVATE);
         token = prefs.getString("token", "");
         Log.d("token", token);

     }

     @Override
     protected String doInBackground(String... params) {
         return WebFunctions.getSearchResult(params[0], params[1], params[2], token);
     }

     @Override
     protected void onPostExecute(String s) {
         super.onPostExecute(s);

         if (!s.equals("error")) {
             restaurants = ParsingFunctions.parseSearchOBJ(s).restaurants;
             dishes = ParsingFunctions.parseSearchOBJ(s).dishes;
             loadRestaurantData(restaurants);
             loadDishData(dishes);
         }
     }
 }
*/

/*
    //Async task which show restaurant
    public class getRestaturant extends AsyncTask<String, Void, String> {

        String token;
        int index;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            SharedPreferences prefs = getSharedPreferences("MY_PREFS", MODE_PRIVATE);
            token = prefs.getString("token", "");
            Log.d("token", token);

        }

        @Override
        protected String doInBackground(String... params) {
            index = Integer.parseInt(params[1]);
            return WebFunctions.getRestaurant(params[0], token);
        }

        @Override
        protected void onPostExecute(String rest) {
            super.onPostExecute(rest);

            RestaurantModel restaurant = ParsingFunctions.parseRestaurantModel(rest);

            Log.d("Index", index + "");
            dishes.get(index).phone_number = restaurant.phone_number;
            dishes.get(index).latitude = restaurant.latitude;
            dishes.get(index).longitude = restaurant.longitude;
            dishes.get(index).restaurant_name = restaurant.name;
            dishes.get(index).address  = restaurant.address;
            Log.d("rest_address", dishes.get(index).address);
            Log.d("rest_name", dishes.get(index).restaurant_name);
            if (index == (dishes.size() - 1)) {

                // Show dish list
                loadDishData(restaurant.dishes);

                //shaow reasturant list
                loadRestaurantData(restaurants);

            }

            progress.dismiss();
        }
    }
*/


}