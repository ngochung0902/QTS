package com.peppa.app.Activity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;

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
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;


import com.peppa.app.Adapter.CustomPagerAdapter;
import com.peppa.app.Adapter.DishAdapter;

import java.util.ArrayList;

import com.peppa.app.R;
import com.peppa.app.listener.OnLoadMoreListener;
import com.peppa.app.model.DishModel;
import com.peppa.app.model.RestaurantModel;
import com.peppa.app.parsing.ParsingFunctions;
import com.peppa.app.parsing.WebFunctions;
import com.peppa.app.utility.ConnectionDetector;
import com.peppa.app.utility.Constant;
import com.peppa.app.utility.Generalfunction;
import com.peppa.app.utility.GlobalVar;

public class ResultActivity extends AppCompatActivity {

    private Context thisContext;
    private ConnectionDetector cdObj;

    private ViewPager mViewPager;
    private RecyclerView mRecyclerView;
    private ProgressDialog progress;

    //Restaurant page addapter
    private CustomPagerAdapter mCustomPagerAdapter;

    //Dish list addapter
    private DishAdapter1 mAdapter;

    private ArrayList<RestaurantModel> restaurants = new ArrayList<RestaurantModel>();
    private ArrayList<DishModel> Disheslist = new ArrayList<DishModel>();
    private String current_search = "", str_location = "";
    private String strFilterdata = "";

    //pagination
    private int dish_page = 1;
    private LinearLayout ll_LoadmoreData;

    private boolean isRestaurant_DishLoading=false;   //API call (Pagination related)
    private boolean isRestaurantLoading = false; //Restaurant
ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // bind layout
        setContentView(R.layout.activity_result);

        thisContext = this;

        //set toolbar and actionbar
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setHomeAsUpIndicator(R.mipmap.back);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        cdObj = new ConnectionDetector(thisContext);

        //Mapping textview
        TextView tt         = (TextView) findViewById(R.id.toolbartitle);
        TextView restaurant = (TextView) findViewById(R.id.restaurantTv);
        TextView dish       = (TextView) findViewById(R.id.dishTv);

        //set text typeface
        Generalfunction.SetToolbartitalstyle(thisContext,tt,false);   //toolbar title
        Typeface latomedium = Typeface.createFromAsset(getAssets(), "fonts/lato_medium.ttf");
        restaurant.setTypeface(latomedium);
        dish.setTypeface(latomedium);

        //Mapping pager which have a reasturant
        mViewPager = (ViewPager) findViewById(R.id.pager);

        //Mapping recycle view which have Dishes
        mRecyclerView = (RecyclerView) findViewById(R.id.linear_recyclerview);
        mRecyclerView.setVisibility(View.VISIBLE);
progressBar=(ProgressBar)findViewById(R.id.progressBar1);
        //Mapping view pager which have restaurants
        mViewPager.setClipToPadding(false);
        mViewPager.setPadding(110, 0, 110, 0);
       mViewPager.setPageMargin(60);
        mViewPager.setPageMarginDrawable(R.color.colorBacker);

        //Get data by intent
        current_search = getIntent().getStringExtra(Constant.Search);          //search word of search
        str_location   = getIntent().getStringExtra(Constant.SerchLocation);     //search location of search

        //Refresh global value
        GlobalVar.setMyBooleanPref(thisContext, Constant.IsScreenRefresh, true);
        Generalfunction.Refreshvalue_Filter(thisContext);
        GlobalVar.setMyBooleanPref(thisContext, Constant.IsPost_Dish, false);

        tt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        mRecyclerView.setFocusable(false);
        mViewPager.setFocusable(true);

        ll_LoadmoreData = (LinearLayout)findViewById(R.id.loadmore);
        ll_LoadmoreData.setVisibility(View.GONE);
        ll_LoadmoreData.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ll_LoadmoreData.setVisibility(View.GONE);
                progressBar.setVisibility(View.VISIBLE);
                new getSearchResult_Next().execute(current_search);
               // Recycleview_update();

            }
        });

        mViewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                //Log.d(Constant.TAG, "pager onPageScrolled: "+position);
            }

            @Override
            public void onPageSelected(int position) {
                if(isRestaurant_DishLoading) {
                    if(isRestaurantLoading) {
                        if (position + 1 == restaurants.size()) {
                            if(current_search==null){
                                current_search="";
                            }

                            new getSearchResult_Next().execute(current_search);  //Call api for load next dish item
                        }
                    }
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {
                //Log.d(Constant.TAG, "pager onPageScrollStateChanged: "+state);
            }
        });

    }


    //Display option menu
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_result, menu);

        MenuItem itemMap = menu.findItem(R.id.actionMapResult);
        MenuItem itemFilter=menu.findItem(R.id.actionFilterResult);

        if (Disheslist.size() > 0) {
            itemMap.setVisible(true);
            itemFilter.setVisible(true);
        } else {
            itemMap.setVisible(false);
            itemFilter.setVisible(false);
        }
        return true;
    }


    //Handle option menu event
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.actionMapResult) {
            if(Disheslist.size()>0) {
                Intent intent = new Intent(ResultActivity.this, MapsActivity.class);
                intent.putExtra("dishes", Disheslist);
//                intent.putExtra("restaurants",restaurants);

                startActivity(intent);
            }

        } else if (id == R.id.actionFilterResult) {
            Intent intent = new Intent(ResultActivity.this, FilterActivity.class);
            intent.putExtra(Constant.Filter_Done, "result");
            startActivity(intent);
        } else if (id == android.R.id.home) {
            Intent i=new Intent(ResultActivity.this,NavigationalSearchActivity.class);
            startActivity(i);
            finish();
        }
        return super.onOptionsItemSelected(item);
    }


    // Check Request permission
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {

        Generalfunction.HandleApppermission(requestCode,permissions,grantResults,thisContext);

    }


    @Override
    protected void onStart() {

        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        strFilterdata = GlobalVar.getMyStringPref(thisContext, Constant.Filter_selecteddone);

        Generalfunction.hideKeyboard(ResultActivity.this);
        GlobalVar.setMyStringPref(thisContext, Constant.Location_Mapview, str_location);

        // Check internet connection and perform operation
        if (cdObj.isConnectingToInternet()) {
            if (GlobalVar.getMyBooleanPref(thisContext, Constant.IsScreenRefresh) || GlobalVar.getMyBooleanPref(thisContext, Constant.IsPost_Dish)) {  //            //Search is perform in 2 way (1-> WOrd , 2-> Location)
                //call api
                if(current_search==null){
                    current_search="";
                }
                new getSearchResult().execute(current_search);

                //reset global value
                GlobalVar.setMyBooleanPref(thisContext, Constant.IsScreenRefresh, false);
                GlobalVar.setMyBooleanPref(thisContext, Constant.IsPost_Dish, false);
            }
        }
        super.onStart();
    }


    //Call - Search Restaurant/dishes/location API
    public class getSearchResult extends AsyncTask<String, Void, String> {
        String token;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            SharedPreferences prefs = thisContext.getSharedPreferences("MY_PREFS", thisContext.MODE_PRIVATE);
            token = prefs.getString("token", "");
            Generalfunction.DisplayLog("token"+token);

            progress = new ProgressDialog(thisContext);
            progress.setMessage("Loading...");
            progress.setCancelable(false);
            progress.show();


        }

        @Override
        protected String doInBackground(String... params) {
            dish_page = 1;
            return WebFunctions.getSearchResult(params[0], strFilterdata, token, thisContext, str_location,dish_page);
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
//            progress.dismiss();

            if (s.equals("error")) {
                Toast.makeText(ResultActivity.this, "error", Toast.LENGTH_SHORT).show();

            } else {

                restaurants = new ArrayList<>();
                Disheslist = new ArrayList<>();

                String Message = ParsingFunctions.parseSearchOBJ(s, thisContext,strFilterdata).Message;
                restaurants = ParsingFunctions.parseSearchOBJ(s, thisContext,strFilterdata).restaurants;
                Disheslist = ParsingFunctions.parseSearchOBJ(s, thisContext,strFilterdata).dishes;

                //Display alert message
                if (restaurants.size()==0 && Disheslist.size()==0) {
                    if (Generalfunction.isEmptyCheck(Message)) {
                        Message = "Restaurant and dishes are not available";
                    }
                    Generalfunction.Simple1ButtonDialog(Message, thisContext);
                }

                //Display Restaurant and Dish
                DisplayRestaurant_Dish(restaurants,Disheslist,true);

                if (Disheslist.size() > 0) {
                    //Display Map icon and filter of actionbar based on dishlist
                    supportInvalidateOptionsMenu();
                }
            }
            if(progress.isShowing()){

                progress.dismiss();
            }
        }
    }


    // Display Restaurant list
    public void loadRestaurantData() {
        try {
            mCustomPagerAdapter = new CustomPagerAdapter(thisContext,restaurants);   //restaurant Array
            mViewPager.setAdapter(mCustomPagerAdapter);
        }
        catch(Exception e){
            e.printStackTrace();
        }
    }


    //pagination (load next data from API)
    private void Recycleview_update() {

       Disheslist.add(null); //Add null value for diaplay Progrss dialog view



        mAdapter.notifyItemInserted(Disheslist.size() - 1);



        //Load more data for reyclerview
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                try {
                    Disheslist.remove(Disheslist.size() - 1);    //Remove loading item
                    mAdapter.notifyItemRemoved(Disheslist.size());

                    //Call api for load next item by recycle view
                    if(current_search==null){
                        current_search="";
                    }


                    new getSearchResult_Next().execute(current_search);


                } catch (Exception e) {}
            }

        }, 5000);

    }


    //Call - get next Restaurant/Dishes API
    public class getSearchResult_Next extends AsyncTask<String, Void, String> {
        String token;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            SharedPreferences prefs = thisContext.getSharedPreferences("MY_PREFS", thisContext.MODE_PRIVATE);
            token = prefs.getString("token", "");
            Generalfunction.DisplayLog("token "+token);
        }

        @Override
        protected String doInBackground(String... params) {
            return WebFunctions.getSearchResult(params[0], strFilterdata, token, thisContext, str_location,  dish_page);
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
//progressBar.setVisibility(View.GONE);
            if (s.equals("error")) {
                Toast.makeText(ResultActivity.this, "error", Toast.LENGTH_SHORT).show();
            }
            else
            {
                ArrayList<DishModel> Arraylist_dish = new ArrayList<>();
                Arraylist_dish = ParsingFunctions.parseSearchOBJ(s, thisContext,strFilterdata).dishes;

                ArrayList<RestaurantModel> arrayList_restaurant=new ArrayList<>();
                arrayList_restaurant = ParsingFunctions.parseSearchOBJ(s, thisContext,strFilterdata).restaurants;

                DisplayRestaurant_Dish(arrayList_restaurant,Arraylist_dish,false);

            }
            if(progressBar.getVisibility()==View.VISIBLE){
                progressBar.setVisibility(View.GONE);
            }
        }

    }


    //Display Restaurant and Dishes
    private void DisplayRestaurant_Dish(ArrayList<RestaurantModel> restaurantlist,ArrayList<DishModel> disheslist,boolean isFirsttime){

        Log.d(Constant.TAG, "pager DisplayRestaurant_Dish: "+disheslist.size());
        //Call Next API Logic
        isRestaurant_DishLoading=false;
        ll_LoadmoreData.setVisibility(View.GONE);
        isRestaurantLoading=false;

        if(disheslist.size() >= Constant.Result_limit || restaurantlist.size() >= Constant.Result_limit){
            dish_page = dish_page + 1;
            isRestaurant_DishLoading=true;

            if(disheslist.size() >= Constant.Result_limit) {
                //Log.d(Constant.TAG, "pager DisplayRestaurant_Dish: "+disheslist.size());
                ll_LoadmoreData.setVisibility(View.VISIBLE);
            }

            if(restaurantlist.size()>=Constant.Result_limit){
                isRestaurantLoading=true;
            }
        }


        //Display Restaurants
        if(restaurantlist.size()>0){
            if(isFirsttime){
                loadRestaurantData();
            }
            else{
                restaurants.addAll(restaurantlist);
                mCustomPagerAdapter.notifyDataSetChanged();
            }
        }


        //Display Dishes
        if(disheslist.size()>0){

            if(isFirsttime){
                supportInvalidateOptionsMenu();
                Generalfunction.Refreshvaluefalse(thisContext);
                mRecyclerView.setLayoutManager(new LinearLayoutManager(ResultActivity.this));

                Disheslist = FilterData();

                mAdapter = new DishAdapter1(thisContext,mRecyclerView,Disheslist);
                mRecyclerView.setAdapter(mAdapter);

                mRecyclerView.setNestedScrollingEnabled(false);
            }
            else{
                Disheslist.addAll(disheslist);
                Generalfunction.DisplayLog("onPostExecute: on Load search next");

                Disheslist = FilterData();

                if (mAdapter != null) {
                    mAdapter.notifyDataSetChanged();
                }
                else {
                    mAdapter = new DishAdapter1(thisContext,mRecyclerView,Disheslist);
                    mRecyclerView.setAdapter(mAdapter);
                }
                mAdapter.setLoaded();
            }

        }
        else{
            Generalfunction.DisplayLog("onPostExecute Filter time");
            mAdapter = new DishAdapter1(thisContext,mRecyclerView,Disheslist);
            mRecyclerView.setAdapter(mAdapter);
        }



    }


    private ArrayList<DishModel> FilterData(){

        //show favorite first
        if (GlobalVar.getMyBooleanPref(thisContext, Constant.Filter_isshowfavorite)) {
            Disheslist = Generalfunction.ShowFavoritefirstFilter(Disheslist);
        }

        return Disheslist;
    }

    public class DishAdapter1 extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

        private final int VIEW_TYPE_ITEM = 0;
        private final int VIEW_TYPE_LOADING = 1;

        private OnLoadMoreListener mOnLoadMoreListener;
        private boolean isLoading;
        private int visibleThreshold = 5;
        private int lastVisibleItem, totalItemCount;

        ArrayList<DishModel> Disheslist;
        Context thisContext;

        public DishAdapter1(Context context,RecyclerView mRecyclerView, ArrayList<DishModel> disheslist) {
            Disheslist=disheslist;
            thisContext=context;
            final LinearLayoutManager linearLayoutManager = (LinearLayoutManager) mRecyclerView.getLayoutManager();
            mRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
                @Override
                public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                    super.onScrolled(recyclerView, dx, dy);

                    totalItemCount = linearLayoutManager.getItemCount();
                    lastVisibleItem = linearLayoutManager.findLastVisibleItemPosition();

                    if (!isLoading && totalItemCount <= (lastVisibleItem + visibleThreshold)) {
                        if (mOnLoadMoreListener != null) {
                            mOnLoadMoreListener.onLoadMore();
                        }
                        isLoading = true;
                        setOnLoadMoreListener(null);
                    }
                }
            });
        }

        public void setOnLoadMoreListener(OnLoadMoreListener mOnLoadMoreListener) {
            this.mOnLoadMoreListener = mOnLoadMoreListener;
        }

        @Override
        public int getItemViewType(int position) {
            return Disheslist.get(position) == null ? VIEW_TYPE_LOADING : VIEW_TYPE_ITEM;
        }

        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            if (viewType == VIEW_TYPE_ITEM) {
                View view = LayoutInflater.from(thisContext).inflate(R.layout.dish_result, parent, false);
                return new DishViewHolder(view);
            } else if (viewType == VIEW_TYPE_LOADING) {
                View view = LayoutInflater.from(thisContext).inflate(R.layout.layout_loading_item, parent, false);
                return new LoadingViewHolder(view);
            }
            return null;
        }

        @Override
        public void onBindViewHolder(final RecyclerView.ViewHolder holder, final int position) {
            if (holder instanceof DishViewHolder) {

                final DishViewHolder dishViewHolder = (DishViewHolder) holder;

                String str = Disheslist.get(position).rest_name;
                if (str.length() > 20) {
                    str = str.substring(0, 18) + "..";
                }

                dishViewHolder.restaurantName.setText(str + " - $" + Disheslist.get(position).price);
                dishViewHolder.rateNumber.setText(Disheslist.get(position).average_rating);
                dishViewHolder.gpsView.setText(Disheslist.get(position).rest_address);
                dishViewHolder.votes.setText(Disheslist.get(position).votes + " " + thisContext.getResources().getString(R.string.votetext));
                dishViewHolder.tvdistance.setText(Disheslist.get(position).distance);

                String strdishname = Disheslist.get(position).name;
                if(strdishname.length() > 25){
                    strdishname = strdishname.substring(0, 22) + "..";
                }

                dishViewHolder.dishName.setText(strdishname);

                //Display Dish image
                String strDishImage = Disheslist.get(position).image;
                Generalfunction.DisplayImage_picasso(strDishImage, thisContext, Constant.case2, dishViewHolder.dishImage,Constant.Ph_dish);

                //Display Phone Image
                Generalfunction.DisplayCall_image(dishViewHolder.ivCall,Disheslist.get(position).rest_phone_number,thisContext);
                dishViewHolder.callView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Generalfunction.RestaurantCall(Disheslist.get(position).rest_phone_number,thisContext);
                    }
                });

                dishViewHolder.gpsView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
//                    Generalfunction.OpenMap(Disheslist.get(position).rest_latitude, Disheslist.get(position).rest_longitude, thisContext,dishViewHolder.gpsView.getText().toString());
                        Generalfunction.OpenMap(Disheslist.get(position).rest_address, thisContext,dishViewHolder.gpsView.getText().toString());
                    }
                });

                dishViewHolder.dishImage.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(thisContext, DetailActivity.class);
                        intent.putExtra(Constant.ID, Disheslist.get(position).id);    //pass dish id
                        intent.putExtra(Constant.isMyprofile,"faviourite");
                        intent.putExtra("results","results");
                        thisContext.startActivity(intent);
                    }
                });

                dishViewHolder.ivQuickpost.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        GlobalVar.setMyBooleanPref(thisContext, Constant.Isquickpost, true);
                        GlobalVar.setMyStringPref(thisContext, Constant.Post_PreviousScreen, Constant.Screen_result);

                        Intent intent = new Intent(thisContext, PostActivity_.class);
                        intent.putExtra(Constant.Quick_RestId, Disheslist.get(position).rest_id);
                        intent.putExtra(Constant.Quick_RestName, Disheslist.get(position).rest_name);
                        intent.putExtra(Constant.Quick_DishName, Disheslist.get(position).name);
                        intent.putExtra(Constant.Quick_DishPrice, Disheslist.get(position).price);
                        intent.putExtra(Constant.Quick_DishID, Disheslist.get(position).id);
                        intent.putExtra(Constant.Quick_Dishimage, Disheslist.get(position).image);
                        intent.putExtra(Constant.Quick_RestAddress,Disheslist.get(position).rest_address);
                        intent.putExtra("latitude",Disheslist.get(position).rest_latitude);
                        intent.putExtra("longitude",Disheslist.get(position).rest_longitude);
                        thisContext.startActivity(intent);
                    }
                });

            } else if (holder instanceof LoadingViewHolder) {
                LoadingViewHolder loadingViewHolder = (LoadingViewHolder) holder;
                loadingViewHolder.progressBar.setIndeterminate(true);
            }
        }

        @Override
        public int getItemCount() {
            return Disheslist == null ? 0 : Disheslist.size();
        }

        public void setLoaded() {
            isLoading = false;
        }


        //Dish view
        public class DishViewHolder extends RecyclerView.ViewHolder {
            public TextView callView, gpsView, restaurantName, rateNumber, dishName;
            public TextView votes;
            public TextView tvdistance;
            public ImageView dishImage;
            public ImageView ivQuickpost;
            public ImageView ivCall;

            public DishViewHolder(View view) {
                super(view);

                //Mapping Textview
                callView       = (TextView) view.findViewById(R.id.tvcallRestaurant);
                gpsView        = (TextView) view.findViewById(R.id.restaurantAddressResultTv);
                restaurantName = (TextView) view.findViewById(R.id.restaurantNameDetailTv);
                rateNumber     = (TextView) view.findViewById(R.id.dishRatingDetailTv);
                dishName       = (TextView) view.findViewById(R.id.dishNameDetailTv);
                votes          = (TextView) view.findViewById(R.id.voteCountDetailTv);
                tvdistance     = (TextView) view.findViewById(R.id.tvdistance);

                //Mapping Imageview
                dishImage   = (ImageView) view.findViewById(R.id.dishImageDetail);
                ivQuickpost = (ImageView) view.findViewById(R.id.postScreenBTN);
                ivCall      = (ImageView)view.findViewById(R.id.ivcallRestaurant);

                //set text typeface
                Generalfunction.loadFont(thisContext, callView, "lato_medium.ttf");
                Generalfunction.loadFont(thisContext, gpsView, "lato.ttf");
                Generalfunction.loadFont(thisContext, restaurantName, "lato_medium.ttf");
                Generalfunction.loadFont(thisContext, rateNumber, "lato.ttf");
                Generalfunction.loadFont(thisContext, dishName, "playfair_regular.otf");
                Generalfunction.loadFont(thisContext, votes, "lato.ttf");
            }
        }

        //Loading view
        class LoadingViewHolder extends RecyclerView.ViewHolder {
            public ProgressBar progressBar;

            public LoadingViewHolder(View itemView) {
                super(itemView);
                progressBar = (ProgressBar) itemView.findViewById(R.id.progressBar1);
            }
        }

    }

}