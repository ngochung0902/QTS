package com.peppa.app.Adapter;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.peppa.app.Activity.DetailActivity;
import com.peppa.app.Activity.PostActivity_;
import com.peppa.app.R;
import com.peppa.app.listener.OnLoadMoreListener;

import java.util.ArrayList;

import com.peppa.app.model.DishModel;

import com.peppa.app.utility.Constant;
import com.peppa.app.utility.Generalfunction;
import com.peppa.app.utility.GlobalVar;

/*
 *  Created by Rup barad on 09-01-2017.
 *  Display Dish list with addapter
 *  Dish adapter which display Dishview and loading view
 */



public class DishAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private final int VIEW_TYPE_ITEM = 0;
    private final int VIEW_TYPE_LOADING = 1;

    private OnLoadMoreListener mOnLoadMoreListener;
    private boolean isLoading;
    private int visibleThreshold = 5;
    private int lastVisibleItem, totalItemCount;

    ArrayList<DishModel> Disheslist;
    Context thisContext;

    public DishAdapter(Context context,RecyclerView mRecyclerView, ArrayList<DishModel> disheslist) {
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
