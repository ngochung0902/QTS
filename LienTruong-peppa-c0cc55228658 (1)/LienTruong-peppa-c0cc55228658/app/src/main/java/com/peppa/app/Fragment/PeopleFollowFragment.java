package com.peppa.app.Fragment;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
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
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.peppa.app.Activity.UserProfileActivity;
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

public class PeopleFollowFragment extends Fragment {

    private Context thisContext;
    private ConnectionDetector cdObj;

    private RecyclerView mRecyclerView;
    private MyAdapter mAdapter;

    private static ArrayList<FollowerModel> Peoplelist = new ArrayList<>();
    private static ArrayList<FollowerModel> Peoplelist_searchresult = new ArrayList<>();

    private int Current_page=1;
    private int iSelectedPos;
    private EndlessRecyclerViewScrollListener scrollListener;

    private String strSearch="";
    View view;

    private boolean isFirsttime = true;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        thisContext = this.getActivity();

        cdObj = new ConnectionDetector(thisContext);
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        view     = inflater.inflate(R.layout.fragment_follow, container, false);


        //Mapping - Recyclerview
        mRecyclerView = (RecyclerView) view.findViewById(R.id.my_recycler_view);

        return view;
    }


    private void InitialWebAPI(){

        Current_page=1;

        Peoplelist = new ArrayList<>();
        Peoplelist_searchresult = new ArrayList<>();

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
            InitialWebAPI();
        }
        else{
            if(isFirsttime) {
                isFirsttime = false;
                InitialWebAPI();
            }
        }

        super.onStart();
    }





    private void callWebApi(){

        //Check - Internet connection
        if (cdObj.isConnectingToInternet()) {
            Generalfunction.DisplayLog("People fragment my profile get user list && Current page: "+Current_page);

            new getUserlist().execute();

        }
        else {
            Generalfunction.Simple1ButtonDialog(thisContext.getResources().getString(R.string.Internet_Message), thisContext);
        }
    }



    //Get - Following user list
    public class getUserlist extends AsyncTask<String, Void, String> {

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
            Log.d(Constant.TAG, "recycle do in background  "+Current_page);
            Log.d(Constant.TAG, "onLoadMore: new recycler view" + Current_page);
            if(Generalfunction.isEmptyCheck(strSearch)) {
                return WebFunctions.getUserlist(token,Current_page);
            }
            else{
                return WebFunctions.getSearchUserlist(token,Current_page,strSearch);
            }

        }

        @Override
        protected void onPostExecute(String rest) {
            super.onPostExecute(rest);

            if(progress!=null) {
                progress.dismiss();
            }

            Log.d(Constant.TAG, "onPostExecute: "+Peoplelist.size() + " search people list " +Peoplelist_searchresult.size()+ " current page: "+Current_page);

            Peoplelist_searchresult = new ArrayList<>();

            if (rest.equals("error")) {
                Toast.makeText(thisContext, "It seems there is a problem. Please try again", Toast.LENGTH_SHORT).show();
            } else {
                if(Current_page > 1){
                    ArrayList<FollowerModel>  PeopleList=new ArrayList<>();
                    PeopleList = ParsingFunctions.parseUserFollow(rest);
                    Log.d(Constant.TAG, "onPostExecute: "+PeopleList.size());
                    if(PeopleList.size()>0){
                        Peoplelist.addAll(PeopleList);
                    }
                }
                else{
                    Peoplelist = new ArrayList<>();
                    Peoplelist = ParsingFunctions.parseUserFollow(rest);
                    Log.d(Constant.TAG, "onPostExecute: "+Peoplelist.size());
                }

                Peoplelist_searchresult = Peoplelist ;
                Log.d(Constant.TAG, "onPostExecute: "+Peoplelist.size() + " search people list " +Peoplelist_searchresult.size());

                if(Current_page > 1){
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
            LinearLayout people;

            public ViewHolder(View itemView) {
                super(itemView);

                //Mapping textview
                followerName  = (TextView) itemView.findViewById(R.id.followerName);
                followersView = (TextView) itemView.findViewById(R.id.followersView);

                //Mapping button
                btnFollow     = (Button) itemView.findViewById(R.id.btnfollow);
                btnUnfollow   = (Button) itemView.findViewById(R.id.btnunfollow);

                //Mapping imageview
                followImage   = (ImageView) itemView.findViewById(R.id.followerImage);
                people=(LinearLayout)itemView.findViewById(R.id.people);

            }
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

            // create a new view
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.follow_row, parent, false);
            ViewHolder vh = new ViewHolder(v);
            return vh;
        }

        public MyAdapter(Context context) {
            this.context = context;
        }

        @Override
        public void onBindViewHolder(final ViewHolder holder, final int position) {

            String strimage = Peoplelist_searchresult.get(position).profileimage;

            //Display user image
            Generalfunction.DisplayImage_picasso(strimage,context,Constant.case2,holder.followImage,Constant.Ph_userprofilepic);

            //set text typeface
            Typeface lato = Typeface.createFromAsset(getActivity().getAssets(), "fonts/lato.ttf");
            holder.followerName.setTypeface(lato);
            holder.followersView.setTypeface(lato);

            holder.followerName.setText(Peoplelist_searchresult.get(position).username);
            holder.followersView.setText(Peoplelist_searchresult.get(position).followersCount + " followers");

            if (Peoplelist_searchresult.get(position).isfollowing) {
                DisplayUnFollowButton(holder);
            } else {
                DisplayFollowButton(holder);
            }

            holder.btnFollow.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    iSelectedPos=position;
                    new FollowUser().execute(Peoplelist_searchresult.get(position).id, "follow");
                }
            });

            holder.btnUnfollow.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    iSelectedPos=position;
                    new FollowUser().execute(Peoplelist_searchresult.get(position).id, "unfollow");
                }
            });

            holder.followerName.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intentOprofile = new Intent(thisContext, UserProfileActivity.class);
                    intentOprofile.putExtra(Constant.isMyprofile, false);
                    intentOprofile.putExtra(Constant.OtheruserId, Peoplelist_searchresult.get(position).id);
                    startActivity(intentOprofile);
                }
            });
            holder.people.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intentOprofile = new Intent(thisContext, UserProfileActivity.class);
                    intentOprofile.putExtra(Constant.isMyprofile, false);
                    intentOprofile.putExtra(Constant.OtheruserId, Peoplelist_searchresult.get(position).id);
                    startActivity(intentOprofile);
                }
            });
            holder.followImage.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intentOprofile = new Intent(thisContext, UserProfileActivity.class);
                    intentOprofile.putExtra(Constant.isMyprofile, false);
                    intentOprofile.putExtra(Constant.OtheruserId, Peoplelist_searchresult.get(position).id);
                    startActivity(intentOprofile);
                }
            });
        }


        @Override
        public int getItemCount() {
            // Return the size of your dataset (invoked by the layout manager)
            return Peoplelist_searchresult.size();
        }

    }


    //Search function
    //this is also call from follow activity
    public void SerachAndFilterPeoplelist(String strSearchword) {

        strSearch=strSearchword;

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


    //Call - Follow/UnFollow User API
    public class FollowUser extends AsyncTask<String, Void, String> {

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
            return WebFunctions.FollowUser(params[0], token, params[1]);
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);

            if (result.equalsIgnoreCase("true")) {
                Generalfunction.RefreshvalueTrue(thisContext);
                GlobalVar.setMyBooleanPref(thisContext,Constant.Filter_FollowRefresh,true);

                if (Peoplelist_searchresult.get(iSelectedPos).isfollowing) {
                    Peoplelist_searchresult.get(iSelectedPos).isfollowing = false;
                    Follow_unfollowSaveinMainArry(false);

                } else {
                    Peoplelist_searchresult.get(iSelectedPos).isfollowing = true;
                    Follow_unfollowSaveinMainArry(true);

                }

            } else {
                Generalfunction.Simple1ButtonDialog(result, thisContext);
            }

            progress.dismiss();
        }
    }


    //Upadte UI for update follow/unfollow
    void Follow_unfollowSaveinMainArry(boolean flag){

        if(Peoplelist.size() == Peoplelist_searchresult.size()){

            Peoplelist.get(iSelectedPos).isfollowing = flag;

        }
        else {

            //Update main arraylist
            String userid = Peoplelist_searchresult.get(iSelectedPos).id;
            for (FollowerModel d : Peoplelist) {
                if (d.id != null && d.id.toLowerCase().contains(userid)) {
                    d.isfollowing = flag;
                }
            }
        }

        if(mAdapter!=null){
            mAdapter.notifyDataSetChanged();
        }

    }


}
