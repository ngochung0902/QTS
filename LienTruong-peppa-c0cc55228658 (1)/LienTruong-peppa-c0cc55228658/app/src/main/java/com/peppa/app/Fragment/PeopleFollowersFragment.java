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
import com.peppa.app.listener.EndlessRecyclerOnScrollListener;

import java.util.ArrayList;

import com.peppa.app.model.FollowerModel;
import com.peppa.app.parsing.ParsingFunctions;
import com.peppa.app.parsing.WebFunctions;
import com.peppa.app.utility.ConnectionDetector;
import com.peppa.app.utility.Constant;
import com.peppa.app.utility.Generalfunction;
import com.peppa.app.utility.GlobalVar;

public class PeopleFollowersFragment extends Fragment {

    private Context thisContext;
    private ConnectionDetector cdObj;

    private LinearLayoutManager mLayoutManager;
    private RecyclerView mRecyclerView;
    private MyAdapter mAdapter;

    private static ArrayList<FollowerModel> Peoplelist = new ArrayList<>();
    private static ArrayList<FollowerModel> Peoplelist_searchresult = new ArrayList<>();

    private boolean IsMyuserprofile;

    private String strUserID="";
    private int Current_page=1;
    private boolean IsRestauarntFollowers=false;
    private int iSelectedPos;
    private String strSearch="";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        thisContext = this.getActivity();

        //connection detector initialization
        cdObj = new ConnectionDetector(thisContext);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_follow, container, false);

        //Mapping recycleview
        mRecyclerView = (RecyclerView) view.findViewById(R.id.my_recycler_view);

        //Decide followers is for Restaurant or User
        if(GlobalVar.getMyBooleanPref(thisContext,Constant.IsResturant_Userprofile)){
            IsRestauarntFollowers=true;
        }
        else{
            IsRestauarntFollowers=false;
        }

        //Decide this is for my user profile or other user profile
        if(Generalfunction.isEmptyCheck(GlobalVar.getMyStringPref(thisContext,Constant.SelectedID_for_Follow))){
            IsMyuserprofile=true;
        }
        else{
            IsMyuserprofile=false;
        }

        strUserID=GlobalVar.getMyStringPref(thisContext,Constant.SelectedID_for_Follow);
        if(Generalfunction.isEmptyCheck(strUserID) && !IsRestauarntFollowers){
             strUserID=GlobalVar.getMyStringPref(thisContext,Constant.loginUserID);
        }

        callWebApi();

        //Define layout of recycle
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
        mRecyclerView.setLayoutManager(linearLayoutManager);

        //add listener for pagination purpose
        mRecyclerView.setOnScrollListener(new EndlessRecyclerOnScrollListener(linearLayoutManager) {
            @Override
            public void onLoadMore(int current_page) {
                //Log.d(TAG, "Restaurant onLoadMore: ");
                Current_page=current_page;
                callWebApi();
            }
        });

        return view;

    }


    //Call - API
    private void callWebApi(){
        //check connection
        if (cdObj.isConnectingToInternet()) {
            new getFollowers_People().execute(strUserID);
        }
        else {
            Generalfunction.Simple1ButtonDialog(thisContext.getResources().getString(R.string.Internet_Message), thisContext);
        }
    }



    //Call - Get Followers of Restaurant/User API
    public class getFollowers_People extends AsyncTask<String, Void, String> {

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
            if(IsRestauarntFollowers) {
                return WebFunctions.getRestaurantFollowers(token, strUserID, Current_page);
            }
            else{
                return WebFunctions.getUserlist_otheruserprofile(token, strUserID, thisContext, Current_page);
            }
        }

        @Override
        protected void onPostExecute(String rest) {
            super.onPostExecute(rest);

            if(progress != null) {
                progress.dismiss();
            }

            if (rest.equals("error")) {
                Toast.makeText(thisContext, "It seems there is a problem. Please try again", Toast.LENGTH_SHORT).show();
            } else {

                if(Current_page>1){
                    ArrayList<FollowerModel>  RestList=new ArrayList<>();
                    RestList=ParsingFunctions.parseUserFollow_Otheruser(rest).Followlist;
                    if(RestList.size()>0){
                        Peoplelist.addAll(RestList);
                    }
                }
                else{
                    Peoplelist_searchresult = new ArrayList<>();
                    Peoplelist = ParsingFunctions.parseUserFollow_Otheruser(rest).Followlist;
                }

                Display_SortbyFollow();
            }

            if(Current_page<=1) {
                progress.dismiss();
            }
        }
    }


    //Display followers by
    private void Display_SortbyFollow(){

        Peoplelist_searchresult=new ArrayList<>();
        ArrayList<FollowerModel>peoplelist_nonselected=new ArrayList<>();

        for(FollowerModel d : Peoplelist){
            if (d.isfollow) {
                Peoplelist_searchresult.add(d);
            } else {
                peoplelist_nonselected.add(d);
            }
        }

        if(peoplelist_nonselected.size()>0){
            Peoplelist_searchresult.addAll(peoplelist_nonselected);
        }

        if (mAdapter != null) {
            mAdapter.notifyDataSetChanged();
        } else {
            mLayoutManager = new LinearLayoutManager(thisContext);
            mRecyclerView.setLayoutManager(mLayoutManager);

            mAdapter = new MyAdapter(thisContext, Peoplelist_searchresult);
            mRecyclerView.setAdapter(mAdapter);
        }

        if(!Generalfunction.isEmptyCheck(strSearch)){
            SerachAndFilterPeoplelist(strSearch);
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
                btnFollow   = (Button) itemView.findViewById(R.id.btnfollow);
                btnUnfollow = (Button) itemView.findViewById(R.id.btnunfollow);

                //Mapping imageview
                followImage = (ImageView) itemView.findViewById(R.id.followerImage);
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


        public MyAdapter(Context context, ArrayList<FollowerModel> restaurants) {
            // Provide a suitable constructor (depends on the kind of dataset)
            this.context = context;
        }


        @Override
        public void onBindViewHolder(final ViewHolder holder, final int position) {
            // Replace the contents of a view (invoked by the layout manager)

            //Display user image
            String strimage = Peoplelist_searchresult.get(position).profileimage;
            Generalfunction.DisplayImage_picasso(strimage,context,Constant.case2,holder.followImage,Constant.Ph_userprofilepic);

            //set text typeface
            Typeface lato = Typeface.createFromAsset(getActivity().getAssets(), "fonts/lato.ttf");
            holder.followerName.setTypeface(lato);
            holder.followersView.setTypeface(lato);

            holder.followerName.setText(Peoplelist_searchresult.get(position).username);
            holder.followersView.setText(Peoplelist_searchresult.get(position).followersCount + " followers");


            if (Peoplelist_searchresult.get(position).isfollow) {
                DisplayUnFollowButton(holder);
            } else {
                DisplayFollowButton(holder);
            }


            if(GlobalVar.getMyBooleanPref(thisContext,Constant.IsFollow_followers)){
                if(!IsMyuserprofile){
                    hideBothButton(holder);
                }
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

        // Return the size of your dataset (invoked by the layout manager)
        @Override
        public int getItemCount() {
            return Peoplelist_searchresult.size();
        }
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


    //Hide both button
    private void hideBothButton(MyAdapter.ViewHolder holder){
        holder.btnFollow.setVisibility(View.GONE);
        holder.btnUnfollow.setVisibility(View.GONE);
    }


    //Call - Follow/Unfollow user API
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

            progress.dismiss();

            if (result.equalsIgnoreCase("true")) {
                Generalfunction.RefreshvalueTrue(thisContext);
                GlobalVar.setMyBooleanPref(thisContext,Constant.Filter_FollowRefresh,true);
                //callWebApi();

                if (Peoplelist_searchresult.get(iSelectedPos).isfollow) {
                    Peoplelist_searchresult.get(iSelectedPos).isfollow = false;

                    //Main
                    Follow_unfollowSaveinMainArry(false);

                } else {
                    Peoplelist_searchresult.get(iSelectedPos).isfollow = true;

                    //Main
                    Follow_unfollowSaveinMainArry(true);

                }

            } else {
                Generalfunction.Simple1ButtonDialog(result, thisContext);
            }
        }
    }


    //Upadte UI for update follow/unfollow
    void Follow_unfollowSaveinMainArry(boolean flag){

        if(Peoplelist.size() == Peoplelist_searchresult.size()){
            Peoplelist.get(iSelectedPos).isfollow = flag;
        }
        else {
            //Update main arraylist
            String userid = Peoplelist_searchresult.get(iSelectedPos).id;
            for (FollowerModel d : Peoplelist) {
                if (d.id != null && d.id.toLowerCase().contains(userid)) {
                    d.isfollow = flag;
                }
            }
        }

        if(mAdapter!=null){
            mAdapter.notifyDataSetChanged();
        }

    }


    //This is call by parent activity of caurrent fragment for search people function
    public void SerachAndFilterPeoplelist(String strSearchword) {

        strSearch=strSearchword;
        Peoplelist_searchresult = new ArrayList<>();
        ArrayList<FollowerModel> arry_searchlist = new ArrayList<>();

        for (FollowerModel d : Peoplelist) {
            Log.d(Constant.TAG, "username people followers : " + d.username);
            if (d.username != null && d.username.toLowerCase().contains(strSearchword)) {
                arry_searchlist.add(d);
            }
        }

        Peoplelist_searchresult=arry_searchlist;

        if(mAdapter != null){
            mAdapter.notifyDataSetChanged();
        }
        else {
            mAdapter = new MyAdapter(thisContext, Peoplelist_searchresult);
            mRecyclerView.setAdapter(mAdapter);
        }

    }



}
