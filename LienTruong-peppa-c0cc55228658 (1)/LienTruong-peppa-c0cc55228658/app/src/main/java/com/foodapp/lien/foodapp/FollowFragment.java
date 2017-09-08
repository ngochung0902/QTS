package com.foodapp.lien.foodapp;

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
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

import model.FollowerMModel;
import parsing.ParsingFunctions;
import parsing.WebFunctions;

public class FollowFragment extends Fragment {

    Context thisContext;
    LinearLayoutManager mLayoutManager;
    RecyclerView mRecyclerView;
    int type = 1;
    String restaurant_id = "";
    ArrayList<FollowerMModel> followers = new ArrayList<FollowerMModel>();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        thisContext = this.getActivity();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_follow, container, false);

        Bundle bundle = this.getArguments();
        type = bundle.getInt("current_page");
        restaurant_id = bundle.getString("id");

        mRecyclerView = (RecyclerView) view.findViewById(R.id.my_recycler_view);

        mLayoutManager = new LinearLayoutManager(thisContext);
        mRecyclerView.setLayoutManager(mLayoutManager);

        if (type == 2) {
            new getFollowers().execute(restaurant_id);
        }

        return view;
    }

    public class getFollowers extends AsyncTask<String, Void, String> {

        ProgressDialog progress;
        String token;

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
            return WebFunctions.getFollowers(token, params[0]);
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);

            followers = ParsingFunctions.parseFollowModel(s);
            MyAdapter mAdapter = new MyAdapter(thisContext, followers);
            mRecyclerView.setAdapter(mAdapter);

            progress.dismiss();
        }
    }

    public class MyAdapter extends RecyclerView.Adapter<MyAdapter.ViewHolder> {

        private ArrayList<FollowerMModel> mDataset;
        private Context context;

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
        public MyAdapter(Context context, ArrayList<FollowerMModel> restaurants) {
            mDataset = restaurants;
            this.context = context;
        }

        // Replace the contents of a view (invoked by the layout manager)
        @Override
        public void onBindViewHolder(ViewHolder holder, int position) {
            // - get element from your dataset at this position
            // - replace the contents of the view with that element

            Typeface lato = Typeface.createFromAsset(getActivity().getAssets(), "fonts/lato.ttf");

            holder.followerName.setTypeface(lato);
            holder.followersView.setTypeface(lato);

            holder.followerName.setText("@" + mDataset.get(position).name);

        }

        // Return the size of your dataset (invoked by the layout manager)
        @Override
        public int getItemCount() {
            return mDataset.size();
        }
    }
}
