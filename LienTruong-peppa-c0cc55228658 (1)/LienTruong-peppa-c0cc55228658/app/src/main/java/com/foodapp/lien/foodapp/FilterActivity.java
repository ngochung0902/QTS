package com.foodapp.lien.foodapp;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.RadioButton;
import android.widget.TextView;

import utility.Constant;

public class FilterActivity extends AppCompatActivity {

    RadioButton distanceBTN, ratingBTN, priceBTN;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_filter);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        TextView location = (TextView) findViewById(R.id.locationTv);
        TextView sort = (TextView) findViewById(R.id.sortByTv);
        TextView sbrating = (TextView) findViewById(R.id.ratingRadio);
        TextView sbprice = (TextView) findViewById(R.id.priceRadio);
        TextView sbdistance = (TextView) findViewById(R.id.distanceRadio);
        TextView price = (TextView) findViewById(R.id.priceRangeTv);
        TextView pricemin = (TextView) findViewById(R.id.priceMin);
        TextView pricemax = (TextView) findViewById(R.id.priceMax);
        TextView distance = (TextView) findViewById(R.id.distanceTv);
        TextView one = (TextView) findViewById(R.id.radioButton1);
        TextView five = (TextView) findViewById(R.id.radioButton5);
        TextView ten = (TextView) findViewById(R.id.radioButton10);
        TextView any = (TextView) findViewById(R.id.radioButtonAny);
        TextView veg = (TextView) findViewById(R.id.vegetarianCheck);
        TextView vegan = (TextView) findViewById(R.id.veganCheck);
        TextView gf = (TextView) findViewById(R.id.glutenFreeCheck);
        TextView tt = (TextView) findViewById(R.id.toolbar_title);

        TextView rating = (TextView) findViewById(R.id.ratingTv);
        TextView opennow = (TextView) findViewById(R.id.openNowTv);

        ratingBTN = (RadioButton) findViewById(R.id.ratingRadio);
        priceBTN = (RadioButton) findViewById(R.id.priceRadio);
        distanceBTN = (RadioButton) findViewById(R.id.distanceRadio);

        Typeface latomedium = Typeface.createFromAsset(getAssets(), "fonts/lato_medium.ttf");

        tt.setTypeface(latomedium);
        location.setTypeface(latomedium);
        sort.setTypeface(latomedium);
        sbrating.setTypeface(latomedium);
        sbprice.setTypeface(latomedium);
        sbdistance.setTypeface(latomedium);
        price.setTypeface(latomedium);
        pricemin.setTypeface(latomedium);
        pricemax.setTypeface(latomedium);
        distance.setTypeface(latomedium);
        one.setTypeface(latomedium);
        five.setTypeface(latomedium);
        ten.setTypeface(latomedium);
        any.setTypeface(latomedium);
        veg.setTypeface(latomedium);
        vegan.setTypeface(latomedium);
        gf.setTypeface(latomedium);
        rating.setTypeface(latomedium);
        opennow.setTypeface(latomedium);

        try {
            Intent intent = getIntent();
            if (intent.getExtras().getString("sort").equals(Constant.mSTR_SORT_DISTANCE)) {
                distanceBTN.setChecked(true);
            } else if (intent.getExtras().getString("sort").equals(Constant.mSTR_SORT_RATING)) {
                ratingBTN.setChecked(true);
            } else if (intent.getExtras().getString("sort").equals(Constant.mSTR_SORT_PRICE)) {
                priceBTN.setChecked(true);
            }
        }
        catch(Exception e){}
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_filter, menu);
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.actionTickFilter) {
            Intent returnIntent = new Intent();
            if(distanceBTN.isChecked()) {
                returnIntent.putExtra("sort", Constant.mSTR_SORT_DISTANCE);
            } else if(ratingBTN.isChecked()) {
                returnIntent.putExtra("sort", Constant.mSTR_SORT_RATING);
            } else if(priceBTN.isChecked()) {
                returnIntent.putExtra("sort", Constant.mSTR_SORT_PRICE);
            }
            setResult(Activity.RESULT_OK,returnIntent);
            finish();
        } else if (id == android.R.id.home) {
            finish();
        }

        return super.onOptionsItemSelected(item);
    }
}
