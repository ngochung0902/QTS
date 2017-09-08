package com.foodapp.lien.foodapp;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.style.ScaleXSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import model.DishCommentModel;
import model.DishModel;
import parsing.ParsingFunctions;
import parsing.WebFunctions;
import utility.ConnectionDetector;
import utility.Constant;
import utility.GPSTracker;
import utility.Generalfunction;
import utility.MyGPSTracker;

@TargetApi(21)
public class DetailActivity extends AppCompatActivity {

    Context thisContext;
    ConnectionDetector cdObj;

    private TextView dishName, restaurantName, dishRating, votes, description, address, callView, comment, dishPriceTv;
    private LinearLayout commentsLLT;
    private ViewPager viewPager;
    private ImageView ivFavorite;

    DishModel dish = new DishModel();
    private String strID = "";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        thisContext = this;

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        cdObj = new ConnectionDetector(thisContext);

        //Mapping Linear layout
        commentsLLT = (LinearLayout) findViewById(R.id.dishCommentsLLT);

        //Mapping Textview
        dishName = (TextView) findViewById(R.id.dishNameDetailTv);
        restaurantName = (TextView) findViewById(R.id.restaurantNameDetailTv);
        dishRating = (TextView) findViewById(R.id.dishRatingDetailTv);
        votes = (TextView) findViewById(R.id.voteCountDetailTv);
        description = (TextView) findViewById(R.id.descriptionTv);
        address = (TextView) findViewById(R.id.addressLinkDetailTv);
        callView = (TextView) findViewById(R.id.callLinkDetailTv);
        comment = (TextView) findViewById(R.id.commentTv);
        dishPriceTv = (TextView) findViewById(R.id.dishPriceTv);

        //Mapping Imageview
        ivFavorite = (ImageView) findViewById(R.id.heartImageDetail);

        //Mapping viewpager
        viewPager = (ViewPager) findViewById(R.id.viewpager);
        viewPager.setAdapter(new DetailImageAdapter(thisContext));

        //set typeface
        Typeface playfair = Typeface.createFromAsset(getAssets(), "fonts/playfair.otf");
        Typeface lato = Typeface.createFromAsset(getAssets(), "fonts/lato.ttf");

        dishName.setTypeface(playfair);
        restaurantName.setTypeface(lato);
        dishRating.setTypeface(lato);
        votes.setTypeface(lato);
        description.setTypeface(lato);
        address.setTypeface(lato);
        callView.setTypeface(lato);
        comment.setTypeface(lato);
        //username.setTypeface(lato);

        if (getIntent().getExtras() != null) {
            //dish id
            strID = getIntent().getExtras().getString(Constant.ID);
        }

        if (cdObj.isConnectingToInternet()) {
            new getDish().execute();
        } else {
            Generalfunction.Simple1ButtonDialog(thisContext.getResources().getString(R.string.Internet_Message), thisContext);
        }
    }


    // Async task for get dish detail useing dish id
    public class getDish extends AsyncTask<String, Void, String> {

        ProgressDialog progress;
        String token;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            SharedPreferences prefs = getSharedPreferences("MY_PREFS", MODE_PRIVATE);
            token = prefs.getString("token", "");
            Log.d("token", token);

            progress = new ProgressDialog(thisContext);
            progress.setMessage("Loading...");
            progress.setCancelable(false);
            progress.show();
        }

        @Override
        protected String doInBackground(String... params) {
            return WebFunctions.getDish(strID, token);
        }

        @Override
        protected void onPostExecute(String rest) {
            super.onPostExecute(rest);

            if (rest.equals("error")) {
                Toast.makeText(thisContext, "It seems there is a problem. Please try again", Toast.LENGTH_SHORT).show();
            } else {
                dish = ParsingFunctions.parseDishModel(rest);
                loadDishData();
            }
            progress.dismiss();
        }
    }


    public void loadDishData() {

        address.setText(dish.rest_address);
        if (!dish.average_rating.equals("")) {
            dishRating.setText(dish.average_rating);
        } else {
            dishRating.setText("0");
        }

        dishName.setText(dish.name);

        String str = dish.rest_name;
        if (str.length() > 21) {
            str = str.substring(0, 18) + "...";
        }

        restaurantName.setText(str);
        description.setText(dish.description);
        dishPriceTv.setText("$" + dish.price);

        votes.setText(dish.votes + " " + thisContext.getResources().getString(R.string.votetext));

        if (dish.favourited_by) {
            ivFavorite.setImageDrawable(getResources().getDrawable(R.drawable.solid_heart, thisContext.getTheme()));
        } else {
            ivFavorite.setImageDrawable(getResources().getDrawable(R.drawable.heart, thisContext.getTheme()));
        }

        restaurantName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(DetailActivity.this, RestaurantProfileActivity.class);
                intent.putExtra(Constant.ID, dish.rest_id);
                startActivity(intent);
            }
        });

        callView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ActivityCompat.checkSelfPermission(thisContext, Manifest.permission.CALL_PHONE)
                        != PackageManager.PERMISSION_GRANTED) {
                    // Request missing location permission.
                    ActivityCompat.requestPermissions(DetailActivity.this,
                            new String[]{Manifest.permission.CALL_PHONE},
                            2);
                } else {
                    Intent intent = new Intent(Intent.ACTION_CALL);
                    intent.setData(Uri.parse("tel:" + dish.rest_phone_number));
                    startActivity(intent);
                }
            }
        });

        address.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String dLAT = dish.rest_latitude;
                String dLONG = dish.rest_longitude;
                Generalfunction.OpenMap(dLAT, dLONG, thisContext);
            }
        });

        ivFavorite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (dish.favourited_by) {
                    new FavoriteUnfavorite_Dish().execute(dish.id, "unfavourite");
                } else {
                    new FavoriteUnfavorite_Dish().execute(dish.id, "favourite");
                }
            }
        });

        loadCommentData();
    }


    public class FavoriteUnfavorite_Dish extends AsyncTask<String, Void, String> {

        ProgressDialog progress;
        String token;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            SharedPreferences prefs = getSharedPreferences("MY_PREFS", MODE_PRIVATE);
            token = prefs.getString("token", "");
            Log.d("token", token);

            progress = new ProgressDialog(thisContext);
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

            if (result.equalsIgnoreCase("true")) {

                if (dish.favourited_by) {
                    ivFavorite.setImageDrawable(getResources().getDrawable(R.drawable.heart, thisContext.getTheme()));
                    dish.favourited_by = false;
                } else {
                    ivFavorite.setImageDrawable(getResources().getDrawable(R.drawable.solid_heart, thisContext.getTheme()));
                    dish.favourited_by = true;
                }

            } else {
                Generalfunction.Simple1ButtonDialog(result, thisContext);
            }

        }
    }


    public void loadCommentData() {
        commentsLLT.removeAllViews();
        ArrayList<DishCommentModel> dishCommentlist = new ArrayList<>();
        dishCommentlist = dish.dishCommentlist;
        for (int i = 0; i < dishCommentlist.size(); i++) {

            final int j = i;

            LayoutInflater layoutInflater = (LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View view = layoutInflater.inflate(R.layout.comment_row, null);

            TextView userNameTV = (TextView) view.findViewById(R.id.userNameTV);
            TextView dishRatingTV = (TextView) view.findViewById(R.id.dishRatingTV);
            TextView dishCommentTV = (TextView) view.findViewById(R.id.dishCommentTV);
            ImageView ivuserphoto = (ImageView) view.findViewById(R.id.ivuserprofilephoto);
            ImageView ivuserisFavorite = (ImageView) view.findViewById(R.id.heartImage);

            Constant.loadFont(this, userNameTV, "lato_medium.ttf");
            Constant.loadFont(this, dishRatingTV, "lato_medium.ttf");
            Constant.loadFont(this, dishCommentTV, "lato.ttf");

            userNameTV.setText(dishCommentlist.get(i).user_Name);
            dishCommentTV.setText(dishCommentlist.get(i).userComment);
            dishRatingTV.setText(dishCommentlist.get(i).userRatingforDish);

            String userphotoUrl = dishCommentlist.get(i).userpProfileImage;
            if (!Generalfunction.isEmptyCheck(userphotoUrl)) {

                Picasso.with(this).load(userphotoUrl)
                        .placeholder(R.drawable.image_loading).error(R.drawable.img_not_available)
                        .into(ivuserphoto);
            } else {
                Picasso.with(thisContext).load(R.drawable.no_picture_sign)
                        .into(ivuserphoto);
            }

            if (dishCommentlist.get(i).favourited_by) {
                ivuserisFavorite.setImageDrawable(getResources().getDrawable(R.drawable.solid_heart, thisContext.getTheme()));
            } else {
                ivuserisFavorite.setImageDrawable(getResources().getDrawable(R.drawable.heart, thisContext.getTheme()));
            }

            final ArrayList<DishCommentModel> finalDishCommentlist = dishCommentlist;
            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intentOprofile = new Intent(thisContext, UserProfileActivity.class);
                    intentOprofile.putExtra(Constant.isMyprofile, false);
                    intentOprofile.putExtra(Constant.OtheruserId, finalDishCommentlist.get(j).userId);
                    startActivity(intentOprofile);
                }
            });

            commentsLLT.addView(view);
        }
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();

        if (id == android.R.id.home) {
            finish();
        }
        return super.onOptionsItemSelected(item);
    }


    public class DetailImageAdapter extends PagerAdapter {

        private Context _activity;
        private ArrayList<String> _imagePaths;
        private LayoutInflater inflater;

        // constructor
        public DetailImageAdapter(Context activity) {
            this._activity = activity;

        }

        @Override
        public int getCount() {
            return 3;
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view == ((RelativeLayout) object);
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            final ImageView imgDisplay;

            inflater = (LayoutInflater) _activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View viewLayout = inflater.inflate(R.layout.detail_pager_item, container, false);

            imgDisplay = (ImageView) viewLayout.findViewById(R.id.detailImage);

            imgDisplay.setImageDrawable(getResources().getDrawable(R.drawable.img_not_available));

            ((ViewPager) container).addView(viewLayout);

            return viewLayout;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            ((ViewPager) container).removeView((RelativeLayout) object);

        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if (requestCode == 2) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {

            } else {
                // Your app will not have this permission. Turn off all functions
                // that require this permission or it will force close like your
                // original question
            }
        }
    }

}

