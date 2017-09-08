package com.peppa.app.Fragment;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.app.ActionBar;
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
import android.view.Window;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.peppa.app.Activity.FilterActivity;
import com.peppa.app.Activity.MapsActivity;
import com.peppa.app.Activity.NavigationalSearchActivity;
import com.peppa.app.Adapter.CustomPagerAdapter;
import com.peppa.app.Adapter.DishAdapter;
import java.util.ArrayList;

import com.peppa.app.R;
import com.peppa.app.model.DishModel;
import com.peppa.app.model.RestaurantModel;
import com.peppa.app.parsing.ParsingFunctions;
import com.peppa.app.parsing.WebFunctions;
import com.peppa.app.utility.ConnectionDetector;
import com.peppa.app.utility.Constant;
import com.peppa.app.utility.Generalfunction;
import com.peppa.app.utility.GlobalVar;

public class FavouritesFragment extends Fragment {

    private Context thisContext;
    private ProgressDialog progress;
    private ConnectionDetector cdObj;

    private ViewPager mViewPager;
    private NestedScrollView favouriteScroll;
    private RecyclerView mRecyclerView;
    private LinearLayout ll_LoadmoreData;

    private ArrayList<RestaurantModel> restaurants = new ArrayList<RestaurantModel>();
    private ArrayList<DishModel> dishes = new ArrayList<DishModel>();

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
    LinearLayout statusBarBackgroundLinearLayout;
    ProgressBar progressBar;

    public FavouritesFragment() {
        // Required empty public constructor
    }


    public static FavouritesFragment newInstance(String param1, String param2) {
        FavouritesFragment fragment = new FavouritesFragment();
        return fragment;
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        thisContext = getActivity();
        cdObj = new ConnectionDetector(thisContext);
        setHasOptionsMenu(true);
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_favourites, container, false);




        //set toolbar text and text style
        Toolbar toolbar = (Toolbar) getActivity().findViewById(R.id.toolbar);
        toolbar.setTitle(getResources().getString(R.string.favoriteTital));
        toolbar.setTitleTextColor(Color.BLACK);
//     AppBarLayout.LayoutParams layoutParams = (AppBarLayout.LayoutParams) toolbar.getLayoutParams();
//        layoutParams.height = 150;
//        toolbar.setLayoutParams(layoutParams);
//        //toolbar.setFitsSystemWindows(true);
//         toolbar.setPadding(0, 35, 0, 0);//for tab otherwise give space in tab
//        toolbar.setContentInsetsAbsolute(0, 0);
//
//
//        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
//            getActivity().getWindow().getDecorView().setSystemUiVisibility(view.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
//        }


      //  toolbar.setLogo(getResources().getDrawable(R.mipmap.back));

        for (int i = 0; i < toolbar.getChildCount(); i++) {
            View view1 = toolbar.getChildAt(i);
            if (view1 instanceof TextView) {
                TextView textView = (TextView) view1;
                Generalfunction.SetToolbartitalstyle(thisContext, textView, false);  //set font
            }
        }

        //Mapping scrollview
        favouriteScroll = (NestedScrollView) view.findViewById(R.id.favouriteScroll);

        //Mapping viewpager
        mViewPager = (ViewPager) view.findViewById(R.id.pager);
        mViewPager.setClipToPadding(false);
        mViewPager.setPadding(110, 0, 110, 0);
        mViewPager.setPageMargin(60);
        mViewPager.setPageMarginDrawable(R.color.colorBacker);

        //Mapping recycler view
        mRecyclerView = (RecyclerView) view.findViewById(R.id.linear_recyclerview);
        mRecyclerView.setVisibility(View.VISIBLE);
        progressBar=(ProgressBar)view.findViewById(R.id.progressBar1);

        //check internet connection and get favorite restaurants
        if (cdObj.isConnectingToInternet()) {
            new getFavourites().execute();
        } else {
            Generalfunction.Simple1ButtonDialog(thisContext.getResources().getString(R.string.Internet_Message), thisContext);
        }

        //Refresh global variable value
        GlobalVar.setMyStringPref(thisContext, Constant.Location_Mapview, "");
        Generalfunction.Refreshvaluefalse(getActivity());
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
        ll_LoadmoreData = (LinearLayout) view.findViewById(R.id.loadmore);
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

        return view;
    }


    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.menu_result, menu);

        // Option menu with it's visisbility logic
        MenuItem itemMap = menu.findItem(R.id.actionMapResult);
        MenuItem itemFilter = menu.findItem(R.id.actionFilterResult);

        if (dishes.size() > 0) {
            itemMap.setVisible(true);
            itemFilter.setVisible(true);
        } else {
            itemMap.setVisible(false);
            if(isFirsttime) {
                itemFilter.setVisible(false);
                isFirsttime = false ;
            }
        }
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        //option menu select item event
        int id = item.getItemId();
        if (id == R.id.actionFilterResult) {
            Intent intent = new Intent(getActivity(), FilterActivity.class);
            intent.putExtra(Constant.Filter_Done, "favorite");
            startActivity(intent);      //startActivityForResult(intent, FILTER_REQUEST_CODE);
        }
        if (id == R.id.actionMapResult) {
            if(dishes.size()>0) {
                GlobalVar.setMyStringPref(thisContext, Constant.Location_Mapview, "Favourites");
                Intent intent = new Intent(getActivity(), MapsActivity.class);
                intent.putExtra("dishes", dishes);

                startActivity(intent);
            }
        }
        else if (id == android.R.id.home) {

            // Close - Activity
            Intent i=new Intent(getActivity(),NavigationalSearchActivity.class);
            startActivity(i);
            getActivity().finish();

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
            Generalfunction.Refreshvaluefalse(getActivity());
        }
        GlobalVar.setMyBooleanPref(thisContext, Constant.Filter_FavouriteRefresh, false);
    }


    //Call - Get favourite restaurant API
    public class getFavourites extends AsyncTask<String, Void, String> {
        String token;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            SharedPreferences prefs = getActivity().getSharedPreferences("MY_PREFS", getActivity().MODE_PRIVATE);
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

                    if (arrayList_restaurant.size() > 0) {
                        restaurants.addAll(arrayList_restaurant);
                        mCustomPagerAdapter.notifyDataSetChanged();

                        if (arrayList_restaurant.size() >= Constant.Favorite_limit) {
                            isRestaurantLoading = true;
                        }
                    }
                } else {
                    restaurants = ParsingFunctions.parseRestaurantArray(s);

                    if (restaurants.size() >= Constant.Favorite_limit) {
                        isRestaurantLoading = true;
                    }

                    loadRestaurantData(restaurants);
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

            SharedPreferences prefs = getActivity().getSharedPreferences("MY_PREFS", getActivity().MODE_PRIVATE);
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

                //Get Dish arraylist
                if(Current_Dish_page <= 1){
                    dishes = new ArrayList<>();
                }

                if(FavDishlist.size() >0){
                    dishes.addAll(FavDishlist);
                }

                Log.d(Constant.TAG, "onPostExecute: " + Current_Dish_page + "is first time "+isFirsttime);

                if(Current_Dish_page <= 1){

                    getActivity().supportInvalidateOptionsMenu();
                    mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
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
