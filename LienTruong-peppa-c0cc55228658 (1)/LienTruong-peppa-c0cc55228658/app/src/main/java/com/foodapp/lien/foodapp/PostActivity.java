package com.foodapp.lien.foodapp;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;

import model.DishModel;
import model.SearchDishModel;
import model.SearchResturantModel;
import parsing.ParsingFunctions;
import parsing.WebFunctions;
import utility.ConnectionDetector;
import utility.Constant;
import utility.Generalfunction;
import utility.GlobalVar;

public class PostActivity extends AppCompatActivity {

    private LinearLayout llDone;
    private ImageView postImage;
    private AutoCompleteTextView acRestaurant,acDish,acPrice;
    private EditText etDescription;
    private ScrollView accountScroll;

    private ConnectionDetector cdObj;
    private Context mContext;

    private String strValidateMessage,strPhotourl="";
    private ArrayList<SearchResturantModel> array_listobject=new ArrayList<>();
    private ArrayList<String> array_list=new ArrayList<>();

    private ArrayList<SearchDishModel> array_dishlistobject=new ArrayList<>();
    private ArrayList<String> array_dishlist=new ArrayList<>();

    private ArrayAdapter<String> myAutoCompleteAdapter;

    private ArrayAdapter<String> myAutoCompleteDishAdapter;

    private String strSelectedRestId="",strselectedDishid="",strSelectedDishname="",strselectedResturantname="",strselectedDishprice;
    private String strWebrestId,strwebDishname,strwebDishprice,strwebphoto,strwebdescription;

    private boolean flagPrice=true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);

        mContext=this;
        cdObj=new ConnectionDetector(mContext);

        Log.d("post activity", "onCreate: post ");

        toolbar.setTitle("");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        postImage = (ImageView) findViewById(R.id.postImage);

        accountScroll = (ScrollView) findViewById(R.id.ScrollViewRat);

        acRestaurant=(AutoCompleteTextView)findViewById(R.id.acrestaurantInput);
        acDish=(AutoCompleteTextView)findViewById(R.id.acdishInput);
        acPrice=(AutoCompleteTextView)findViewById(R.id.acpriceInput);
        etDescription=(EditText)findViewById(R.id.etdescription);
        llDone=(LinearLayout)findViewById(R.id.lldone);

        Bundle bundle = getIntent().getExtras();

        if(bundle != null) {

            Uri imageUri = null;

            if(bundle.getBoolean("isCameraOrGallery")) {
                if (bundle.getBoolean("isCamera")) {
                    File imgFile = new  File(bundle.getString("filename"));
                    imageUri = Uri.fromFile(imgFile);
                    postImage.setImageURI(imageUri  );
                    //imageLoader.displayImage(imageUri.toString(), postImage);
                } else {
                    imageUri = Uri.parse(bundle.getString("filename"));
                    postImage.setImageURI(imageUri  );
                }

            } else {

            }

            Bitmap bitmapDecode = Generalfunction.decodeFile(imageUri.getPath());

            Log.d("bitmap decode1", "onCreate: "+bitmapDecode);
            Bitmap bmRoted = Generalfunction.rotateBitmap(bitmapDecode, imageUri.getPath());
            if (bmRoted == null) {
                bmRoted = bitmapDecode;
            }

            Log.d("bitmap roted1", "onCreate: "+bmRoted);
            if(bmRoted != null){
                postImage.setImageBitmap(bmRoted);
            }

            try {
                if (bundle.getBoolean("isCamerabutgallery")) {
                    File imgFile = new  File(imageUri.getPath());
                    boolean flag = imgFile.delete();
                    Log.d("posted file", "delete file : " + flag);
                }
            }
            catch(Exception e){
                e.printStackTrace();
            }

        }

        acRestaurant.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(s.toString().length()==2){
                    acPrice.setText("$");
                    Log.d("post user", "onTextChanged: ");
                    if (cdObj.isConnectingToInternet()) {
                        new getSearchRestaurant().execute(s.toString());
                    }
                }

            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        acDish.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(s.toString().length()==2){
                    Log.d("post user", "onTextChanged: ");
                    if (cdObj.isConnectingToInternet()) {
                        new getSearchRestaurantDish().execute(s.toString());
                    }
                }
                if(Generalfunction.isCompare(acDish.getText().toString(),strSelectedDishname)){
                    acPrice.setEnabled(false);
                }
                else{
                    acPrice.setEnabled(true);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });


        if (!cdObj.isConnectingToInternet()) {
            Generalfunction.Simple1ButtonDialog(mContext.getResources().getString(R.string.Internet_Message), mContext);
        }

        llDone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(Validation()){
                    if (cdObj.isConnectingToInternet()) {
                        new Post().execute();
                    }
                    else{
                        Generalfunction.Simple1ButtonDialog(mContext.getResources().getString(R.string.Internet_Message), mContext);
                    }
                }
                else{
                    Snackbar snackbar1 = Snackbar.make(accountScroll, strValidateMessage, Snackbar.LENGTH_LONG);
                    snackbar1.show();
                }
            }
        });

        acRestaurant.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_NEXT) {
                    acDish.requestFocus();
                    return true;
                }
                return false;
            }
        });

        acDish.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_NEXT) {
                    if(Generalfunction.isCompare(acDish.getText().toString(),strSelectedDishname)){
                        etDescription.requestFocus();
                    }
                    else{
                        acPrice.requestFocus();
                    }
                    return true;
                }
                return false;
            }
        });

        acPrice.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_NEXT) {
                    etDescription.requestFocus();
                    return true;
                }
                return false;
            }
        });

        if(flagPrice){
            acPrice.setFocusable(true);
        }

    }



    private boolean Validation(){
        boolean isValidate=true;

        String strRestaurantName,strDishname,strPrice,strDescripton;
        strRestaurantName=acRestaurant.getText().toString();
        strDishname=acDish.getText().toString();
        strPrice=acPrice.getText().toString();
        strDescripton=etDescription.getText().toString();

        if(Generalfunction.isCompare(strRestaurantName,strselectedResturantname)){
            strWebrestId=strSelectedRestId;
        }
        else{
            strWebrestId="";
        }

        if(Generalfunction.isCompare(strDishname,strSelectedDishname)){
            strwebDishname=strSelectedDishname;
            strwebDishprice=strselectedDishprice;
        }
        else{
            strwebDishname=strDishname;
            strwebDishprice=strPrice.replace("$","");
        }

        strwebdescription=strDescripton;

        if(Generalfunction.isEmptyCheck(strRestaurantName)){
            isValidate=false;
            strValidateMessage="Please type restaurant name";
        }
        else if(Generalfunction.isEmptyCheck(strwebDishname)){
            isValidate=false;
            strValidateMessage="Please type dish name";
        }
        else if(Generalfunction.isEmptyCheck(strwebDishprice)){
            isValidate=false;
            strValidateMessage="Please type price of dish";
        }
        else if(Generalfunction.isEmptyCheck(strwebdescription)){
            isValidate=false;
            strValidateMessage="Please type description of dish";
        }

        return isValidate;
    }



    // Asynctask for register account with simple
    public class Post extends AsyncTask<String, Void, String> {

        ProgressDialog progress;
        String token;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            SharedPreferences prefs = getSharedPreferences("MY_PREFS", MODE_PRIVATE);
            token = prefs.getString("token", "");
            Log.d("token", token);

            progress = new ProgressDialog(mContext);
            progress.setMessage("Registering....");
            progress.setCancelable(false);
            progress.show();
        }

        @Override
        protected String doInBackground(String... params) {
            // call webfunction
            return WebFunctions.Post(token,strWebrestId,strwebDishname,strwebDishprice,strwebdescription,strPhotourl);
        }

        @Override
        protected void onPostExecute(String aVoid) {
            super.onPostExecute(aVoid);

            progress.dismiss();

            if (!aVoid.equals("")) {
                JSONObject jsonObject;

                String Message= "";
                try {
                    Log.d("rup", "onPostExecute: "+aVoid);
                    jsonObject = new JSONObject(aVoid);

                    if(jsonObject.getString("success").toLowerCase().equalsIgnoreCase("yes")) {
                        Message=jsonObject.getString("message");
                        Generalfunction.Simple1ButtonDialogClick(Message, mContext);
                    }
                    else if(jsonObject.getString("success").toLowerCase().equalsIgnoreCase("no")) {
                        Message=jsonObject.getString("errors");
                        Generalfunction.Simple1ButtonDialog(Message, mContext);
                    }
                    else{
                        Message=jsonObject.getString("message");
                        Generalfunction.Simple1ButtonDialog(Message, mContext);
                    }

                } catch (JSONException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }

            }

        }
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.actionTickPost) {
            Intent intent = new Intent(PostActivity.this, RateActivity.class);
            startActivity(intent);
            finish();
        } else if (id == android.R.id.home) {
            finish();
        }

        return super.onOptionsItemSelected(item);
    }



    //get my user data
    public class getSearchRestaurant extends AsyncTask<String, Void, String> {

        //ProgressDialog progress;
        String token;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            SharedPreferences prefs = getSharedPreferences("MY_PREFS", MODE_PRIVATE);
            token = prefs.getString("token", "");
            Log.d("token", token);

        }

        @Override
        protected String doInBackground(String... params) {
            return WebFunctions.getRestaurantlist(params[0], token);
        }

        @Override
        protected void onPostExecute(String rest) {
            super.onPostExecute(rest);

            array_list=new ArrayList<>();

            if(rest.toLowerCase().contains("sorry")){
                //Generalfunction.Simple1ButtonDialog(aVoid, getActivity());
            }
            else{
                try {
                    JSONObject posOBJ = new JSONObject(rest);
                    JSONArray ResturantsArray = posOBJ.getJSONArray("restaurants");

                    for (int j = 0; j < ResturantsArray.length(); j++) {

                        SearchResturantModel DishOBJ = new SearchResturantModel();
                        JSONObject object = ResturantsArray.getJSONObject(j);

                        DishOBJ. Restid= object.getString("id");
                        DishOBJ.strRestName = object.getString("name");

                        array_listobject.add(DishOBJ);
                        array_list.add(DishOBJ.strRestName);

                    }
                }catch (JSONException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }

                if(array_list.size()>0){
                    myAutoCompleteAdapter = new ArrayAdapter<String>(
                            mContext,
                            android.R.layout.simple_dropdown_item_1line,
                            array_list);

                    acRestaurant.setAdapter(myAutoCompleteAdapter);
                    acRestaurant.setThreshold(1);

                    acRestaurant.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                            strSelectedRestId=array_listobject.get(position).Restid;
                            Log.d("select rest id", "onItemClick: "+strSelectedRestId);
                            strselectedResturantname=array_listobject.get(position).strRestName;
                        }
                    });
                }
            }
        }
    }


    public class getSearchRestaurantDish extends AsyncTask<String, Void, String> {

        //ProgressDialog progress;
        String token;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            SharedPreferences prefs = getSharedPreferences("MY_PREFS", MODE_PRIVATE);
            token = prefs.getString("token", "");
            Log.d("token", token);

        }

        @Override
        protected String doInBackground(String... params) {
            return WebFunctions.getRestaurantDishlist(params[0], token);
        }

        @Override
        protected void onPostExecute(String rest) {
            super.onPostExecute(rest);

            array_list=new ArrayList<>();

            if(rest.toLowerCase().contains("sorry")){
                //Generalfunction.Simple1ButtonDialog(aVoid, getActivity());
            }
            else{
                try {
                    JSONObject posOBJ = new JSONObject(rest);
                    JSONArray ResturantsArray = posOBJ.getJSONArray("dishes");

                    for (int j = 0; j < ResturantsArray.length(); j++) {

                        SearchDishModel DishOBJ = new SearchDishModel();
                        JSONObject object = ResturantsArray.getJSONObject(j);

                        DishOBJ. id= object.getString("id");
                        DishOBJ.strDishName = object.getString("name");
                        DishOBJ.strDishprice=object.getString("price");

                        array_dishlistobject.add(DishOBJ);
                        array_dishlist.add(DishOBJ.strDishName);

                    }
                }catch (JSONException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }

                if(array_dishlist.size()>0){
                    myAutoCompleteDishAdapter = new ArrayAdapter<String>(
                            mContext,
                            android.R.layout.simple_dropdown_item_1line,
                            array_dishlist);

                    acDish.setAdapter(myAutoCompleteDishAdapter);
                    acDish.setThreshold(2);

                    acDish.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                            strselectedDishprice=array_dishlistobject.get(position).strDishprice;
                            acPrice.setText("$"+strselectedDishprice);
                            strselectedDishid=array_dishlistobject.get(position).id;
                            strSelectedDishname=array_dishlistobject.get(position).strDishName;
                            etDescription.requestFocus();
                            acPrice.setEnabled(false);
                        }
                    });

                }
            }
        }
    }

}
