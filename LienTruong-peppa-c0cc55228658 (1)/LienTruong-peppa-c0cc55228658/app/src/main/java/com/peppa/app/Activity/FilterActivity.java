package com.peppa.app.Activity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.CheckBox;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.peppa.app.R;
import com.yahoo.mobile.client.android.util.rangeseekbar.RangeSeekBar;

import com.peppa.app.utility.ConnectionDetector;
import com.peppa.app.utility.Constant;
import com.peppa.app.utility.Generalfunction;
import com.peppa.app.utility.GlobalVar;

public class FilterActivity extends AppCompatActivity {

    private Context thisContext;
    private ConnectionDetector cdObj;

    private CheckBox cbShowfavorite;
    private CheckBox cbEntry,cbMain,cbDessert,cbDrink;
    private CheckBox cbVegeterian,cbVegen,cbGluten;
    private RangeSeekBar<Integer> seekBar;
    private TextView pricemin,pricemax;
    private RadioGroup rgSortby,rgRating;
    private RadioButton rbRating, rbPrice,rbDistance;
    private RadioButton rbAll,rbFiftyhigher,rbSeventyfivehigher,rbNintyhigher;

    private int iMinprice=1;
    private int iMaxprice=100;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_filter);

        thisContext=this;
        cdObj = new ConnectionDetector(thisContext);

        //Set - Toolbar
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setHomeAsUpIndicator(R.mipmap.back_white);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        //Mapping - Textview
        TextView sort           = (TextView) findViewById(R.id.sortByTv);
        TextView price          = (TextView) findViewById(R.id.priceRangeTv);
        TextView tvfavoritefirst=(TextView)findViewById(R.id.tvfavoritefirst);

        TextView veg   = (TextView) findViewById(R.id.vegetarianCheck);
        TextView vegan = (TextView) findViewById(R.id.veganCheck);
        TextView gf    = (TextView) findViewById(R.id.glutenFreeCheck);
        TextView tt    = (TextView) findViewById(R.id.toolbar_title);

        TextView tvratinglbl=(TextView) findViewById(R.id.tvrating);
//        TextView tvdistance=(TextView) findViewById(R.id.tvdistance);

        TextView tventry  = (TextView) findViewById(R.id.tventrylabel);
        TextView tvmain   = (TextView) findViewById(R.id.tvmainlabel);
        TextView tvdesert = (TextView) findViewById(R.id.tvdesertlabel);
        TextView tvdrink  = (TextView) findViewById(R.id.tvdrinklabel);

        pricemin = (TextView) findViewById(R.id.priceMin);
        pricemax = (TextView) findViewById(R.id.priceMax);

        //Mapping - Radio group
        rgSortby = (RadioGroup)findViewById(R.id.rgsortby);
        rgRating = (RadioGroup)findViewById(R.id.rgrating);

        //Mapping - Radio button
        rbRating   = (RadioButton) findViewById(R.id.ratingRadio);
        rbPrice    = (RadioButton) findViewById(R.id.priceRadio);
        rbDistance = (RadioButton) findViewById(R.id.distanceRadio);

        rbAll               = (RadioButton) findViewById(R.id.rb_all);
        rbFiftyhigher       = (RadioButton) findViewById(R.id.rb__fifty);
        rbSeventyfivehigher = (RadioButton) findViewById(R.id.rb_seventyfive);
        rbNintyhigher       = (RadioButton) findViewById(R.id.rb_ninety);

        //Maping - Range Seek Bar
        seekBar = (RangeSeekBar)findViewById(R.id.rangeSeekbar);

        //Mapping - Checkbox
        cbShowfavorite = (CheckBox)findViewById(R.id.checkshowfavorite);
        cbEntry        = (CheckBox)findViewById(R.id.checkentry);
        cbMain         = (CheckBox)findViewById(R.id.checkmain);
        cbDessert      = (CheckBox)findViewById(R.id.checkdessert);
        cbDrink        = (CheckBox)findViewById(R.id.checkdrink);

        cbVegeterian   = (CheckBox)findViewById(R.id.checkVegita);
        cbVegen        = (CheckBox)findViewById(R.id.checkVegan);
        cbGluten       = (CheckBox)findViewById(R.id.checkGluten);


        //Set - Toolbar Title Style
        Generalfunction.SetToolbartitalstyle(thisContext,tt,true);

        //Set - Font type
        Typeface latomedium = Typeface.createFromAsset(getAssets(), "fonts/lato_medium.ttf");
        sort.setTypeface(latomedium);
        rbRating.setTypeface(latomedium);
        rbPrice.setTypeface(latomedium);
        rbDistance.setTypeface(latomedium);
        rbAll.setTypeface(latomedium);
        rbFiftyhigher.setTypeface(latomedium);
        rbSeventyfivehigher.setTypeface(latomedium);
        rbNintyhigher.setTypeface(latomedium);

        price.setTypeface(latomedium);
        pricemin.setTypeface(latomedium);
        pricemax.setTypeface(latomedium);

        tvfavoritefirst.setTypeface(latomedium);
        veg.setTypeface(latomedium);
        vegan.setTypeface(latomedium);
        gf.setTypeface(latomedium);
        tventry.setTypeface(latomedium);
        tvmain.setTypeface(latomedium);
        tvdesert.setTypeface(latomedium);
        tvdrink.setTypeface(latomedium);
        tvratinglbl.setTypeface(latomedium);
//        tvdistance.setTypeface(latomedium);

        rbDistance.setVisibility(View.VISIBLE);

        //Set - Favorite checkbox Enable/Disable
        try {
            Intent intent = getIntent();
            cbShowfavorite.setEnabled(true);
            if(intent.getExtras().getString(Constant.Filter_Done).equalsIgnoreCase("favorite")){
                cbShowfavorite.setChecked(true);
                cbShowfavorite.setEnabled(false);
            }
            else if(intent.getExtras().getString(Constant.Filter_Done).equalsIgnoreCase("Restaurant")){
                rbDistance.setVisibility(View.GONE);
            }
        }
        catch(Exception e){}

        iMaxprice = GlobalVar.getMyIntPref(thisContext,Constant.Filter_MaxPrice);
        iMinprice = GlobalVar.getMyIntPref(thisContext,Constant.Filter_MinPrice);

        seekBar.setRangeValues(iMinprice,iMaxprice);
        seekBar.setSelectedMinValue(iMinprice);
        seekBar.setSelectedMaxValue(iMaxprice);
        pricemin.setText("$"+iMinprice);
        pricemax.setText("$"+iMaxprice);

        seekBar.setOnRangeSeekBarChangeListener(new RangeSeekBar.OnRangeSeekBarChangeListener<Integer>() {
            @Override
            public void onRangeSeekBarValuesChanged(RangeSeekBar<?> bar, Integer minValue, Integer maxValue) {
                pricemin.setText("$"+minValue);
                pricemax.setText("$"+maxValue);
            }
        });

        // Get notice while dragging
        seekBar.setNotifyWhileDragging(true);

        //Set - Second time show previous selected item

        //Sort by
        if(GlobalVar.getMyStringPref(thisContext,Constant.Filter_sortby).equalsIgnoreCase(getResources().getString(R.string.rating_label))){
            rbRating.setChecked(true);
        }
        else if(GlobalVar.getMyStringPref(thisContext,Constant.Filter_sortby).equalsIgnoreCase(getResources().getString(R.string.price_label))){
            rbPrice.setChecked(true);
        }
        else if(GlobalVar.getMyStringPref(thisContext,Constant.Filter_sortby).equalsIgnoreCase(getResources().getString(R.string.distance_label))){
            rbDistance.setChecked(true);
        }

        //Show favourite
        if(GlobalVar.getMyBooleanPref(thisContext,Constant.Filter_isshowfavorite)){
            cbShowfavorite.setChecked(true);
        }

        //Price range
        if(!Generalfunction.isEmptyCheck(GlobalVar.getMyStringPref(thisContext,Constant.Filter_priceMax))){
            pricemax.setText("$"+GlobalVar.getMyStringPref(thisContext,Constant.Filter_priceMax));

            Integer imax=Integer.parseInt(GlobalVar.getMyStringPref(thisContext,Constant.Filter_priceMax));
            seekBar.setSelectedMaxValue(imax);
        }
        if(!Generalfunction.isEmptyCheck(GlobalVar.getMyStringPref(thisContext,Constant.Filter_priceMin))){
            pricemin.setText("$"+GlobalVar.getMyStringPref(thisContext,Constant.Filter_priceMin));

            Integer imin=Integer.parseInt(GlobalVar.getMyStringPref(thisContext,Constant.Filter_priceMin));
            seekBar.setSelectedMinValue(imin);
        }

        //Rating
        if(GlobalVar.getMyStringPref(thisContext,Constant.Filter_Rating).equalsIgnoreCase(getResources().getString(R.string.zero))){
            rbAll.setChecked(true);
        }
        else if(GlobalVar.getMyStringPref(thisContext,Constant.Filter_Rating).equalsIgnoreCase(getResources().getString(R.string.fifty))){
            rbFiftyhigher.setChecked(true);
        }
        else if(GlobalVar.getMyStringPref(thisContext,Constant.Filter_Rating).equalsIgnoreCase(getResources().getString(R.string.seventyfive))){
            rbSeventyfivehigher.setChecked(true);
        }
        else if(GlobalVar.getMyStringPref(thisContext,Constant.Filter_Rating).equalsIgnoreCase(getResources().getString(R.string.ninty))){
            rbNintyhigher.setChecked(true);
        }

        //Tags
        if(GlobalVar.getMyBooleanPref(thisContext,Constant.Filter_isTag_entry)){
            cbEntry.setChecked(true);
        }
        if(GlobalVar.getMyBooleanPref(thisContext,Constant.Filter_isTag_main)){
            cbMain.setChecked(true);
        }
        if(GlobalVar.getMyBooleanPref(thisContext,Constant.Filter_isTag_dessert)){
            cbDessert.setChecked(true);
        }
        if(GlobalVar.getMyBooleanPref(thisContext,Constant.Filter_isTag_drink)){
            cbDrink.setChecked(true);
        }
        if(GlobalVar.getMyBooleanPref(thisContext,Constant.Filter_isTag_vegeterian)){
            cbVegeterian.setChecked(true);
        }
        if(GlobalVar.getMyBooleanPref(thisContext,Constant.Filter_isTag_vegen)){
            cbVegen.setChecked(true);
        }
        if(GlobalVar.getMyBooleanPref(thisContext,Constant.Filter_isTag_gluten)){
            cbGluten.setChecked(true);
        }

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
            FilterDone();

        } else if (id == android.R.id.home) {
            finish();
        }

        return super.onOptionsItemSelected(item);
    }



    private void FilterDone(){

        //Sort by
        int selectedId = rgSortby.getCheckedRadioButtonId();
        RadioButton radioSelectedButton=(RadioButton)findViewById(selectedId);
        String strSort = null;
        if(radioSelectedButton!=null) {
            strSort = radioSelectedButton.getText().toString();
        }
        else{

        }

        //Price range Min and Max price
        String strMinPrice=pricemin.getText().toString().replace("$", "");
        String strMaxPrice=pricemax.getText().toString().replace("$", "");

        //Rating
        String strSelectedRating="0";
        if(rbAll.isChecked()){
            strSelectedRating=getResources().getString(R.string.zero);
        }
        else if(rbFiftyhigher.isChecked()){
            strSelectedRating=getResources().getString(R.string.fifty);
        }
        else if(rbSeventyfivehigher.isChecked()){
            strSelectedRating=getResources().getString(R.string.seventyfive);
        }
        else if(rbNintyhigher.isChecked()){
            strSelectedRating=getResources().getString(R.string.ninty);
        }

        //Tag
        String strTag="";


        if(cbEntry.isChecked()){
            strTag=strTag+"&meals[]="+getResources().getString(R.string.entry_label);
        }

        if(cbMain.isChecked()){
            strTag=strTag+"&meals[]="+getResources().getString(R.string.Main_label);
        }

        if(cbDessert.isChecked()){
            strTag=strTag+"&meals[]="+getResources().getString(R.string.Dessert_label);
        }

        if(cbDrink.isChecked()){
            strTag=strTag+"&meals[]="+getResources().getString(R.string.Drink_label);
        }

        if(cbVegeterian.isChecked()){
            strTag=strTag+"&dietarian[]="+getResources().getString(R.string.Vegetarian_label);
        }

        if(cbVegen.isChecked()){
            strTag=strTag+"&dietarian[]="+getResources().getString(R.string.Vegan_label);
        }

        if(cbGluten.isChecked()){
            strTag=strTag+"&dietarian[]="+getResources().getString(R.string.Gluten_free_label);
        }


        String strTag1 = "";

        if (cbEntry.isChecked()) {
            strTag1 = strTag1 + getResources().getString(R.string.entry_label) + ",";
        }
        if (cbMain.isChecked()) {
            strTag1 = strTag1 + getResources().getString(R.string.Main_label) + ",";
        }
        if (cbDessert.isChecked()) {
            strTag1 = strTag1 + getResources().getString(R.string.Dessert_label) + ",";
        }
        if (cbDrink.isChecked()) {
            strTag1 = strTag1 + getResources().getString(R.string.Drink_label) + ",";
        }
        if (cbVegeterian.isChecked()) {
            strTag1 = strTag1 + getResources().getString(R.string.Vegetarian_label1) + ",";
        }
        if (cbVegen.isChecked()) {
            strTag1 = strTag1 + getResources().getString(R.string.Vegan_label) + ",";
        }
        if (cbGluten.isChecked()) {
            strTag1 = strTag1 + getResources().getString(R.string.Gluten_free_label) + ",";

        }

        int lastIndex = strTag.lastIndexOf(",");
        if(lastIndex != -1){
            strTag1 = strTag.substring(0, lastIndex);
        }











        strTag=strTag.toLowerCase();

        String strFilterVal= "sort_by="+strSort+"&rating="+strSelectedRating+"&max_price="+strMaxPrice+"&min_price="+strMinPrice+strTag;  //"&favourites_first="+cbShowfavorite.isChecked()+

        Generalfunction.DisplayLog("FilterDone: "+strFilterVal);

        //Save - Second time it's use for show selected item
        Generalfunction.Savedataoffilter(thisContext,strSort,strMaxPrice,strMinPrice,strSelectedRating,
               cbShowfavorite.isChecked(),
               cbEntry.isChecked(),cbMain.isChecked(),cbDessert.isChecked(),cbDrink.isChecked(),
               cbVegeterian.isChecked(),cbVegen.isChecked(),cbGluten.isChecked(),strFilterVal,getResources().getString(R.string.anyno),strTag1);

        Generalfunction.RefreshvalueTrue(thisContext);

        finish();

    }



}
