package com.foodapp.lien.foodapp;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

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
public class FavouritesFragment extends Fragment {

    private static final int FILTER_REQUEST_CODE = 200;

    private LinearLayout dishLLT;
    private ViewPager mViewPager;
    private ScrollView favouriteScroll;

    private Context thisContext;
    private ProgressDialog progress;
    private ConnectionDetector cdObj;

    private int restauratPos = 0;
    private ArrayList<RestaurantModel> restaurants = new ArrayList<RestaurantModel>();
    private ArrayList<DishModel> dishes = new ArrayList<DishModel>();


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
    public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_favourites, container, false);

        //Mapping Linear layout
        dishLLT = (LinearLayout) view.findViewById(R.id.dishLLT);

        //Mapping scrollview
        favouriteScroll = (ScrollView) view.findViewById(R.id.favouriteScroll);

        //Mapping viewpager
        mViewPager = (ViewPager) view.findViewById(R.id.pager);

        mViewPager.setClipToPadding(false);
        mViewPager.setPadding(110, 0, 110, 0);
        mViewPager.setPageMargin(60);
        mViewPager.setPageMarginDrawable(R.color.colorBacker);

        if (cdObj.isConnectingToInternet()) {
            new getFavourites().execute();
        } else {
            Generalfunction.Simple1ButtonDialog(thisContext.getResources().getString(R.string.Internet_Message), thisContext);
        }

        return view;
    }



    public class getFavourites extends AsyncTask<String, Void, String> {

        String token;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            SharedPreferences prefs = getActivity().getSharedPreferences("MY_PREFS", getActivity().MODE_PRIVATE);
            token = prefs.getString("token", "");
            Log.d("token", token);

            progress= new ProgressDialog(thisContext);
            progress.setMessage("Loading...");
            progress.setCancelable(false);
            progress.show();
        }

        @Override
        protected String doInBackground(String... params) {
            return WebFunctions.getFollowers_Favourites(token, "favourited_restaurants");
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);

            if (s.equals("error")) {
                Snackbar snackbar1 = Snackbar.make(favouriteScroll, "You have no favourites at the moment", Snackbar.LENGTH_LONG);
                snackbar1.show();
            } else {
                restaurants = ParsingFunctions.parseRestaurantArray(s);
                new getFavouritesDishes().execute();
            }
        }
    }



    public class getFavouritesDishes extends AsyncTask<String, Void, String> {

        String token;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            SharedPreferences prefs = getActivity().getSharedPreferences("MY_PREFS", getActivity().MODE_PRIVATE);
            token = prefs.getString("token", "");
            Log.d("token", token);

        }

        @Override
        protected String doInBackground(String... params) {
            return WebFunctions.getFavouritesDishes(token);
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);

            if (!s.equals("error")) {
                dishes = ParsingFunctions.parseDishArray(s);

                loadDishData(dishes);
                loadRestaurantData(restaurants);
            }
            progress.dismiss();
        }
    }



    //Display Restaurant
    public void loadRestaurantData(final ArrayList<RestaurantModel> restArray) {
        CustomPagerAdapter mCustomPagerAdapter = new CustomPagerAdapter(thisContext, restArray);
        mViewPager.setAdapter(mCustomPagerAdapter);

    }



    //Display Dishes
    public void loadDishData (final ArrayList<DishModel> dishes) {
        dishLLT.removeAllViews();
        LayoutInflater layoutInflater = (LayoutInflater) thisContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        for (int i = 0; i < dishes.size(); i++) {
            final int j = i;

            View view = layoutInflater.inflate(R.layout.dish_result, null );
            TextView callView = (TextView) view.findViewById(R.id.restaurantCallResultTv);
            TextView gpsView = (TextView) view.findViewById(R.id.restaurantAddressResultTv);
            ImageView dishImageDetail = (ImageView) view.findViewById(R.id.dishImageDetail);
            ImageView heartImageDetail = (ImageView) view.findViewById(R.id.heartImageDetail);
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
            votes.setText(dishes.get(i).votes +" "+thisContext.getResources().getString(R.string.votetext));

            if (dishes.get(i).favourited_by) {
                heartImageDetail.setImageDrawable(getResources().getDrawable(R.drawable.solid_heart, thisContext.getTheme()));
            } else {
                heartImageDetail.setImageDrawable(getResources().getDrawable(R.drawable.heart, thisContext.getTheme()));
            }

            String strDishImage=dishes.get(i).image;
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
                        ActivityCompat.requestPermissions(getActivity(),
                                new String[]{Manifest.permission.CALL_PHONE},
                                2);
                    } else {
                        Intent intent = new Intent(Intent.ACTION_CALL);
                        intent.setData(Uri.parse("tel:" + restaurants.get(restauratPos).phone_number));
                        startActivity(intent);
                    }
                }
            });

            gpsView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Generalfunction.OpenMap(restaurants.get(restauratPos).latitude, restaurants.get(restauratPos).longitude,getActivity());
                }
            });

            dishImageDetail.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(thisContext, DetailActivity.class);
                    intent.putExtra(Constant.ID, dishes.get(j).id);
                    startActivity(intent);
                }
            });

            heartImageDetail.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if((dishes.get(j).favourited_by)) {
                        new FavoriteUnfavorite_Dish().execute(dishes.get(j).id, "unfavourite");
                    }
                    else{
                        new FavoriteUnfavorite_Dish().execute(dishes.get(j).id, "favourite");
                    }
                }
            });

            dishLLT.addView(view);
        }
    }


//Display Restaurants
    class CustomPagerAdapter extends PagerAdapter {

        Context mContext;
        LayoutInflater mLayoutInflater;
        ArrayList<RestaurantModel> mRestList;

        public CustomPagerAdapter(Context context, ArrayList<RestaurantModel> array) {
            mContext = context;
            mRestList = array;
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
                    Intent intent = new Intent(thisContext, RestaurantProfileActivity.class);
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

                    if (ActivityCompat.checkSelfPermission(thisContext, Manifest.permission.CALL_PHONE)
                            != PackageManager.PERMISSION_GRANTED) {
                        // Request missing location permission.
                        ActivityCompat.requestPermissions(getActivity(),
                                new String[]{Manifest.permission.CALL_PHONE},
                                2);
                    } else {
                        Intent intent = new Intent(Intent.ACTION_CALL);
                        intent.setData(Uri.parse("tel:" + restaurants.get(j).phone_number));
                        startActivity(intent);
                    }


                }
            });

            ivRestorantfavourite.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if( restaurants.get(j).favourited_by) {
                        new UnFavorite_Restaurant().execute( restaurants.get(j).id, "unfavourite");
                    }
                    else{
                        new UnFavorite_Restaurant().execute( restaurants.get(j).id, "favourite");
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


    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {

        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.menu_result, menu);
        menu.findItem(R.id.actionMapResult).setVisible(false);

    }



    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();

        if (id == R.id.actionFilterResult) {
            Intent intent = new Intent(thisContext, FilterActivity.class);
            intent.putExtra("sort", Constant.mSTR_SORT_PRICE);
            startActivityForResult(intent, FILTER_REQUEST_CODE);
        }

        return super.onOptionsItemSelected(item);
    }



    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == getActivity().RESULT_OK) {
            if (requestCode == FILTER_REQUEST_CODE) {
                if (data != null) {
                }
            }
        }
    }


    //Favorite/unfavorite Dish
    public class FavoriteUnfavorite_Dish extends AsyncTask<String, Void, String> {

        ProgressDialog progress;
        String token;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            SharedPreferences prefs = getActivity().getSharedPreferences("MY_PREFS", getActivity().MODE_PRIVATE);
            token = prefs.getString("token", "");
            Log.d("token", token);

            progress= new ProgressDialog(thisContext);
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

            if(result.equalsIgnoreCase("true")){
                if (cdObj.isConnectingToInternet()) {
                    new getFavourites().execute();
                } else {
                    Generalfunction.Simple1ButtonDialog(thisContext.getResources().getString(R.string.Internet_Message), thisContext);
                }
            }
            else{
                Generalfunction.Simple1ButtonDialog(result,thisContext);
            }

        }
    }



    //Favorite/unfavorite Restaurant
    public class UnFavorite_Restaurant extends AsyncTask<String, Void, String> {

        ProgressDialog progress;
        String token;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            SharedPreferences prefs = getActivity().getSharedPreferences("MY_PREFS", getActivity().MODE_PRIVATE);
            token = prefs.getString("token", "");
            Log.d("token", token);

            progress= new ProgressDialog(thisContext);
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
                if (cdObj.isConnectingToInternet()) {
                    new getFavourites().execute();
                } else {
                    Generalfunction.Simple1ButtonDialog(thisContext.getResources().getString(R.string.Internet_Message), thisContext);
                }
            }
            else{
                Generalfunction.Simple1ButtonDialog(result,thisContext);
            }

        }
    }

}
