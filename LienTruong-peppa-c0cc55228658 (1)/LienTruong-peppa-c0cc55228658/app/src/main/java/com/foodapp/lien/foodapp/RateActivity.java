package com.foodapp.lien.foodapp;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import model.DishModel;
import parsing.WebFunctions;
import utility.ConnectionDetector;
import utility.Generalfunction;

public class RateActivity extends AppCompatActivity {

    TextView rateNumberRateTv, lengthView;
    ImageView dishImageRate;
  //  DishModel dishModel;
    Button doneRateBTN;
    Context thisContext;

    EditText etComment;
    ScrollView scrollview;
    ConnectionDetector cdObj;

    String Dishname="",Dishprice="",DishRestaurantName="",Dishid="",Dishimage="";
    String strValidateMessage="";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rate);

        thisContext = this;
        cdObj=new ConnectionDetector(thisContext);
        //String restaturant = "";

        try {
            Intent intent = getIntent();
            Dishname=intent.getExtras().getString("dishname");
            Dishprice = intent.getExtras().getString("dishprice");
            DishRestaurantName = intent.getExtras().getString("restaurantname");
            Dishid = intent.getExtras().getString("dishid");
            Dishimage=intent.getExtras().getString("dishimage");
        }
        catch(Exception e){

        }

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        lengthView = (TextView) findViewById(R.id.editLengthView);
        etComment = (EditText) findViewById(R.id.howWasThisDishRateEt);
        TextView dish = (TextView) findViewById(R.id.dishNameRateTv);
        TextView restaurant = (TextView) findViewById(R.id.restaurantNameDetailTv);
        rateNumberRateTv = (TextView) findViewById(R.id.rateNumberRateTv);
        dishImageRate = (ImageView)findViewById(R.id.dishImageRate);
        doneRateBTN = (Button) findViewById(R.id.doneRateButton);
        scrollview=(ScrollView)findViewById(R.id.ScrollViewRate);

        dish.setText(Dishname);
        restaurant.setText(DishRestaurantName+" - $"+Dishprice);

        SeekBar seekBarRate = (SeekBar) findViewById(R.id.seekBarRate);

        Typeface playfair = Typeface.createFromAsset(getAssets(), "fonts/playfair.otf");
        Typeface lato = Typeface.createFromAsset(getAssets(), "fonts/lato.ttf");

        dish.setTypeface(playfair);
        restaurant.setTypeface(lato);

        if(!Generalfunction.isEmptyCheck(Dishimage)){
            Picasso.with(this)
                    .load(Dishimage)
                    .placeholder(R.drawable.no_picture_sign) //this is optional the image to display while the url image is downloading
                    .error(R.drawable.no_picture_sign)         //this is also optional if some error has occurred in downloading the image this image would be displayed
                    .into(dishImageRate);
        }
        else{
            Picasso.with(thisContext).load(R.drawable.no_picture_sign)
                    .into(dishImageRate);
        }

        seekBarRate.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                rateNumberRateTv.setText(progress + "");
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        dishImageRate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Intent intent = new Intent(RateActivity.this, PostActivity.class);
                //startActivity(intent);
            }
        });

        doneRateBTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(Validation()){
                    if (cdObj.isConnectingToInternet()) {
                        new RateDish().execute(Dishid, rateNumberRateTv.getText().toString());
                    }
                    else{
                        Generalfunction.Simple1ButtonDialog(thisContext.getResources().getString(R.string.Internet_Message), thisContext);
                    }
                }
                else{
                    Snackbar snackbar1 = Snackbar.make(scrollview, strValidateMessage, Snackbar.LENGTH_LONG);
                    snackbar1.show();
                }
            }
        });

        etComment.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                lengthView.setText(String.valueOf(s.length()) + "/150");
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }

    private boolean Validation(){
        boolean isValidate=true;

        if(Generalfunction.isEmptyCheck(etComment.getText().toString())){
            strValidateMessage="please type dish comment";
            etComment.requestFocus();
            isValidate=false;
        }

        return isValidate;
    }


    public class RateDish extends AsyncTask<String, Void, Boolean> {

        ProgressDialog progress;
        String token;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            SharedPreferences prefs = getSharedPreferences("MY_PREFS", MODE_PRIVATE);
            token = prefs.getString("token", "");
            Log.d("token", token);

            progress= new ProgressDialog(thisContext);
            progress.setMessage("Loading...");
            progress.setCancelable(false);
            progress.show();
        }

        @Override
        protected Boolean doInBackground(String... params) {
            return WebFunctions.rateDish(params[0], params[1], token,etComment.getText().toString());
        }

        @Override
        protected void onPostExecute(Boolean s) {
            super.onPostExecute(s);

            progress.dismiss();

            if (s) {
                Toast.makeText(thisContext, "Dish has been rated.", Toast.LENGTH_SHORT).show();
                finish();
            } else {
                Toast.makeText(thisContext, "Please try again.", Toast.LENGTH_SHORT).show();
            }
        }
    }

    /*@Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_rate, menu);
        return true;
    }*/

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.actionTickRate) {
            finish();
        } else if (id == R.id.actionDeleteRate) {
            new AlertDialog.Builder(RateActivity.this)
                    .setTitle("Are You Sure")
                    .setMessage("You want to delete this entry?")
                    .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            finish();
                        }
                    })
                    .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            // do nothing
                        }
                    })
                    .setIcon(android.R.drawable.ic_dialog_alert)
                    .show();
        } else if (id == android.R.id.home) {
            finish();
        }

        return super.onOptionsItemSelected(item);
    }
}
