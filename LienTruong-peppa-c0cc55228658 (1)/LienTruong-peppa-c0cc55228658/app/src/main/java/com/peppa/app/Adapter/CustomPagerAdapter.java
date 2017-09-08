package com.peppa.app.Adapter;

import android.content.Context;
import android.content.Intent;
import android.support.v4.view.PagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.peppa.app.R;
import com.peppa.app.Activity.RestaurantProfileActivity;

import java.util.ArrayList;

import com.peppa.app.model.RestaurantModel;
import com.peppa.app.utility.Constant;
import com.peppa.app.utility.Generalfunction;

/**
 * Created by Rup barad on 09-01-2017.
 */

    /* ****************
     * Display Restaurant list with addapter - Start
     ************ */

  public  class CustomPagerAdapter extends PagerAdapter {

        Context mContext;
        LayoutInflater mLayoutInflater;
        ArrayList<RestaurantModel> restaurants;

        public CustomPagerAdapter(Context context,ArrayList<RestaurantModel> restaurant) {   //, ArrayList<RestaurantModel> restaurant
            mContext = context;
            restaurants=restaurant;
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
            TextView addressView    = (TextView) itemView.findViewById(R.id.addressResultTv);
            TextView restaurantName = (TextView) itemView.findViewById(R.id.restaurantNameResultTv);
            TextView rateNumber     = (TextView) itemView.findViewById(R.id.dishRatingRResultTv);
            TextView votesView      = (TextView) itemView.findViewById(R.id.dishRatingPercentRResultTv);
            TextView gpsView        = (TextView) itemView.findViewById(R.id.addressResultTv);

            ImageView ivRestorantPhoto = (ImageView) itemView.findViewById(R.id.imageViewRestauarntResult);
            ImageView ivCall           =(ImageView)itemView.findViewById(R.id.phoneImageRResult);

            LinearLayout restaurantView = (LinearLayout) itemView.findViewById(R.id.restauarentView);
            RelativeLayout callView     = (RelativeLayout) itemView.findViewById(R.id.callRestauratBTN);

            Generalfunction.loadFont(mContext, callNumberView, "lato_medium.ttf");
            Generalfunction.loadFont(mContext, votesView, "lato_medium.ttf");
            Generalfunction.loadFont(mContext, rateNumber, "lato_bold.ttf");
            Generalfunction.loadFont(mContext, addressView, "lato_medium.ttf");
            Generalfunction.loadFont(mContext, restaurantName, "lato_medium.ttf");

            callNumberView.setText(restaurants.get(position).phone_number);
            addressView.setText(restaurants.get(position).address);
            restaurantName.setText(restaurants.get(position).name);
            rateNumber.setText(restaurants.get(position).rating);

            restaurantView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(mContext, RestaurantProfileActivity.class);
                    intent.putExtra(Constant.ID, restaurants.get(j).id);

                    mContext.startActivity(intent);
                }
            });

            //Display Restaurant image
            String strRestorantProfile = restaurants.get(position).restaurant_image;
            Generalfunction.DisplayImage_picasso(strRestorantProfile, mContext, Constant.case1, ivRestorantPhoto,Constant.Ph_restaurant_coverimage);

            //Display Phone Image
            Generalfunction.DisplayCall_image(ivCall,restaurants.get(position).phone_number,mContext);
            callView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Generalfunction.RestaurantCall(restaurants.get(j).phone_number,mContext);
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
