package com.foodapp.lien.foodapp;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

import model.RestaurantModel;
import parsing.ParsingFunctions;
import parsing.WebFunctions;
import utility.ConnectionDetector;
import utility.Generalfunction;

public class RestaurantsFragment extends Fragment {

    private Context thisContext;
    private LinearLayoutManager mLayoutManager;
    private RecyclerView mRecyclerView;

    private ArrayList<RestaurantModel> restaurants = new ArrayList<RestaurantModel>();

    private MyAdapter mAdapter;

    private ConnectionDetector cdObj;
    private RestaurantModel restaurant = new RestaurantModel();



    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        thisContext = this.getActivity();

        //connection detector initialization
        cdObj = new ConnectionDetector(thisContext);
    }



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_follow, container, false);

        mRecyclerView = (RecyclerView) view.findViewById(R.id.my_recycler_view);
        if (cdObj.isConnectingToInternet()) {
            new getRestaturant().execute();
        }
        else{
            Generalfunction.Simple1ButtonDialog(thisContext.getResources().getString(R.string.Internet_Message), thisContext);
        }

        return view;
    }



    //Asynck task for getting Restaurant list
    public class getRestaturant extends AsyncTask<String, Void, String> {

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
            return WebFunctions.getRestaurantlist( token);
        }

        @Override
        protected void onPostExecute(String rest) {
            super.onPostExecute(rest);

            Log.d("Follow fragment", "onPostExecute: "+rest);
            if (rest.equals("error")) {
                Toast.makeText(thisContext, "It seems there is a problem. Please try again", Toast.LENGTH_SHORT).show();
            } else {

                restaurants = ParsingFunctions.parseRestaurantFollowers(rest).restaurants;


                //Log.d("Count", restaurant.dishes.size() + "");
                if (mAdapter != null) {
                    mAdapter.notifyDataSetChanged();
                } else {

                    mLayoutManager = new LinearLayoutManager(thisContext);
                    mRecyclerView.setLayoutManager(mLayoutManager);

                    MyAdapter mAdapter = new MyAdapter(thisContext, restaurants);
                    mRecyclerView.setAdapter(mAdapter);
                }
            }

            //supportInvalidateOptionsMenu();
            progress.dismiss();
        }
    }



    public class MyAdapter extends RecyclerView.Adapter<MyAdapter.ViewHolder> {

        private ArrayList<RestaurantModel> mDataset;
        private Context context;
        boolean[] checkBoxState;

        public class ViewHolder extends RecyclerView.ViewHolder {
            // each data item is just a string in this case
            TextView followerName;
            TextView followersView;
            CheckBox checkFollow;
            ImageView followImage;

            public ViewHolder(View itemView) {
                super(itemView);

                followerName = (TextView) itemView.findViewById(R.id.followerName);
                followersView = (TextView) itemView.findViewById(R.id.followersView);
                checkFollow = (CheckBox) itemView.findViewById(R.id.checkFollow);
                followImage = (ImageView) itemView.findViewById(R.id.followerImage);
            }
        }

        @Override
        public MyAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            // create a new view
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.follow_row, parent, false);
            ViewHolder vh = new ViewHolder(v);
            return vh;
        }

        // Provide a suitable constructor (depends on the kind of dataset)
        public MyAdapter(Context context, ArrayList<RestaurantModel> restaurants) {
            mDataset = restaurants;
            this.context = context;
            checkBoxState=new boolean[mDataset.size()];
        }

        // Replace the contents of a view (invoked by the layout manager)
        @Override
        public void onBindViewHolder(ViewHolder holder, final int position) {
            // - get element from your dataset at this position
            // - replace the contents of the view with that element

            Typeface lato = Typeface.createFromAsset(getActivity().getAssets(), "fonts/lato.ttf");

            holder.followerName.setTypeface(lato);
            holder.followersView.setTypeface(lato);

            holder.followerName.setText(restaurants.get(position).name);
            holder.followersView.setText(restaurants.get(position).followers_count+" followers");

            if(restaurants.get(position).followed_by){
                holder.checkFollow.setChecked(true);
                checkBoxState[position]=true;
            }
            else{
                holder.checkFollow.setChecked(false);
                checkBoxState[position]=false;
            }

            holder.checkFollow.setOnClickListener(new View.OnClickListener() {

                public void onClick(View v) {
                    if(((CheckBox)v).isChecked()) {
                        checkBoxState[position] = false;
                        new FollowRestaurant().execute(restaurants.get(position).id, "follow");
                    }
                    else{
                        checkBoxState[position] = true;
                        new FollowRestaurant().execute(restaurants.get(position).id, "unfollow");
                    }

                }
            });

        }

        // Return the size of your dataset (invoked by the layout manager)
        @Override
        public int getItemCount() {
            return restaurants.size();
        }
    }




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
                if (restaurant.favourited_by) {
                    restaurant.favourited_by = false;
                } else {
                    restaurant.favourited_by = true;
                }
            }
            else{
                Generalfunction.Simple1ButtonDialog(result,thisContext);
            }


            Intent intent = new Intent("update_restaurant");
            intent.putExtra("id", restaurant.id);
            LocalBroadcastManager.getInstance(thisContext).sendBroadcast(intent);
        }

    }

}
