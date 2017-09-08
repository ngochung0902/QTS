package com.peppa.app.Fragment;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.peppa.app.R;

import java.util.ArrayList;

import com.peppa.app.listener.EndlessRecyclerViewScrollListener;
import com.peppa.app.model.FollowerModel;
import com.peppa.app.parsing.ParsingFunctions;
import com.peppa.app.parsing.WebFunctions;
import com.peppa.app.utility.ConnectionDetector;
import com.peppa.app.utility.Constant;
import com.peppa.app.utility.Generalfunction;
import com.peppa.app.utility.GlobalVar;

public class RestaurantsFragment extends Fragment {

    private Context thisContext;
    private ConnectionDetector cdObj;

    private LinearLayoutManager mLayoutManager;
    private RecyclerView mRecyclerView;

    private ArrayList<FollowerModel> restaurantslist = new ArrayList<FollowerModel>();
    private ArrayList<FollowerModel> restaurants_searchresult = new ArrayList<FollowerModel>();

    private MyAdapter mAdapter;

    private int iSelectedPos;
    private int Current_page=1;
    private String strSearch="";

    private EndlessRecyclerViewScrollListener scrollListener;
    private boolean isFirsttime = true;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        thisContext = this.getActivity();

        //connection detector initialization
        cdObj = new ConnectionDetector(thisContext);
    }



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_follow, container, false);


        //Mapping recycleview
        mRecyclerView = (RecyclerView) view.findViewById(R.id.my_recycler_view);

        return view;
    }


    private void InitialWebAPI(){

        Current_page=1;

        restaurantslist = new ArrayList<>();
        restaurants_searchresult = new ArrayList<>();

        callWebApi();

        //Define layout of recycle
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
        mRecyclerView.setLayoutManager(linearLayoutManager);

        // Retain an instance so that you can call `resetState()` for fresh searches
        scrollListener = new EndlessRecyclerViewScrollListener(linearLayoutManager) {
            @Override
            public void onLoadMore(int page, int totalItemsCount, RecyclerView view) {
                // Triggered only when new data needs to be appended to the list
                // Add whatever code is needed to append new items to the bottom of the list

                Log.d(Constant.TAG, "onLoadMore: new recycler view" + page);

                Current_page = page;
                callWebApi();
            }
        };

        // Adds the scroll listener to RecyclerView
        mRecyclerView.addOnScrollListener(scrollListener);

    }

    @Override
    public void onStart() {
        if(GlobalVar.getMyBooleanPref(thisContext,Constant.Filter_FollowRefresh)){

            /*user go restaurant profile -> follow/unfollow restaurant then this is call*/
            InitialWebAPI();
        }
        else{
            if(isFirsttime) {
                isFirsttime = false;
                //Call - First time Api
                InitialWebAPI();
            }
        }
        super.onStart();
    }


    private void callWebApi(){
        if (cdObj.isConnectingToInternet()) {

            new getRestaturant().execute();
        }
        else{
            Generalfunction.Simple1ButtonDialog(thisContext.getResources().getString(R.string.Internet_Message), thisContext);
        }
    }


    // Bind view with Recyclerview
    public class MyAdapter extends RecyclerView.Adapter<MyAdapter.ViewHolder> {

        private Context context;

        public class ViewHolder extends RecyclerView.ViewHolder {
            TextView followerName;
            TextView followersView;
            Button btnFollow,btnUnfollow;
            ImageView followImage;

            public ViewHolder(View itemView) {
                super(itemView);

                //Mapping textview
                followerName = (TextView) itemView.findViewById(R.id.followerName);
                followersView = (TextView) itemView.findViewById(R.id.followersView);

                //Mapping Button
                btnFollow = (Button) itemView.findViewById(R.id.btnfollow);
                btnUnfollow = (Button) itemView.findViewById(R.id.btnunfollow);

                //Mapping imageview
                followImage = (ImageView) itemView.findViewById(R.id.followerImage);
            }
        }


        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            // create a new view
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.follow_row, parent, false);
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

            //set text typeface
            Typeface lato = Typeface.createFromAsset(getActivity().getAssets(), "fonts/lato.ttf");
            holder.followerName.setTypeface(lato);
            holder.followersView.setTypeface(lato);

            holder.followerName.setText(restaurants_searchresult.get(position).Rest_name);
            holder.followersView.setText(restaurants_searchresult.get(position).Rest_followerscount+" followers");

            if(restaurants_searchresult.get(position).isRest_follow){
                DisplayUnFollowButton(holder);
            }
            else{
                DisplayFollowButton(holder);
            }

            holder.btnFollow.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    iSelectedPos=position;
                    new FollowRestaurant().execute(restaurants_searchresult.get(position).Rest_id, "follow");
                }
            });

            holder.btnUnfollow.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    iSelectedPos=position;
                    new FollowRestaurant().execute(restaurants_searchresult.get(position).Rest_id, "unfollow");
                }
            });

        }

        // Return the size of your dataset (invoked by the layout manager)
        @Override
        public int getItemCount() {
            return restaurants_searchresult.size();
        }
    }



    //Async task for getting Restaurant list
    public class getRestaturant extends AsyncTask<String, Void, String> {

        ProgressDialog progress;
        String token;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            SharedPreferences prefs = getActivity().getSharedPreferences("MY_PREFS", getActivity().MODE_PRIVATE);
            token = prefs.getString("token", "");
            Log.d("token", token);

            if(Current_page<=1) {
                progress = new ProgressDialog(thisContext);
                progress.setMessage("Loading...");
                progress.setCancelable(false);
                progress.show();
            }
        }

        @Override
        protected String doInBackground(String... params) {
            if(Generalfunction.isEmptyCheck(strSearch)) {
                return WebFunctions.getRestaurantlist( token,Current_page);
            }
            else{
                return WebFunctions.getSearchRestaurantlist( token,Current_page,strSearch);
            }

        }

        @Override
        protected void onPostExecute(String rest) {
            super.onPostExecute(rest);

            Log.d("Follow fragment", "onPostExecute: "+rest);
            if (rest.equals("error")) {
                Toast.makeText(thisContext, "It seems there is a problem. Please try again", Toast.LENGTH_SHORT).show();
            } else {

                if(Current_page>1){
                 ArrayList<FollowerModel>  RestList=new ArrayList<>();
                    RestList=ParsingFunctions.parseRestaurantFollowers(rest).Followlist;
                    if(RestList.size()>0){
                        restaurantslist.addAll(RestList);
                    }
                }
                else {
                    restaurantslist = ParsingFunctions.parseRestaurantFollowers(rest).Followlist;
                }

                restaurants_searchresult = restaurantslist ;

                if(Current_page>1){
                    if (mAdapter != null) {
                        mAdapter.notifyDataSetChanged();
                    } else {
                        mAdapter = new MyAdapter(thisContext);
                        mRecyclerView.setAdapter(mAdapter);
                    }
                }
                else{
                    mAdapter = new MyAdapter(thisContext);
                    mRecyclerView.setAdapter(mAdapter);
                }

            }

            if(progress!=null){
                progress.dismiss();
            }
        }

    }

    //Async task for follow/unfollow restaurant
    public class FollowRestaurant extends AsyncTask<String, Void, String> {

        ProgressDialog progress;
        String token;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            SharedPreferences prefs = thisContext.getSharedPreferences("MY_PREFS", thisContext.MODE_PRIVATE);
            token = prefs.getString("token", "");
            Log.d("token", token);

            progress = new ProgressDialog(thisContext);
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

                Generalfunction.RefreshvalueTrue(thisContext);

                if (restaurants_searchresult.get(iSelectedPos).isRest_follow) {
                    restaurants_searchresult.get(iSelectedPos).isRest_follow = false;

                    Follow_unfollowSaveinMainArry(false);

                } else {
                    restaurants_searchresult.get(iSelectedPos).isRest_follow = true;

                    Follow_unfollowSaveinMainArry(true);

                }

            }
            else{
                Generalfunction.Simple1ButtonDialog(result,thisContext);
            }
        }

    }


    //Upadte UI for update follow/unfollow
    void Follow_unfollowSaveinMainArry(boolean flag){

        if(restaurantslist.size() == restaurants_searchresult.size()){

            restaurantslist.get(iSelectedPos).isRest_follow = flag;

        }
        else {

            //Update main arraylist
            String userid = restaurants_searchresult.get(iSelectedPos).Rest_id;
            for (FollowerModel d : restaurantslist) {
                if (d.id != null && d.id.toLowerCase().contains(userid)) {
                    d.isRest_follow = flag;
                }
            }
        }

        if(mAdapter!=null){
            mAdapter.notifyDataSetChanged();
        }

    }




    //This is call by parent activity of caurrent fragment for search restaurant function
    public void SerachAndFilterRestaurant(String strSearchword) {

        strSearch = strSearchword;

        InitialWebAPI();

    }


    //Display follow button
    private void DisplayFollowButton(MyAdapter.ViewHolder holder){
        holder.btnFollow.setVisibility(View.VISIBLE);
        holder.btnUnfollow.setVisibility(View.GONE);
    }


    //Display unfollow button
    private void DisplayUnFollowButton(MyAdapter.ViewHolder holder){
        holder.btnFollow.setVisibility(View.GONE);
        holder.btnUnfollow.setVisibility(View.VISIBLE);
    }


    private void ResetEndlessScroll(){
        restaurants_searchresult.clear();
        mAdapter.notifyDataSetChanged();

    }

}
