package com.peppa.app.Activity;

import android.Manifest;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.ComponentName;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Parcelable;
import android.provider.MediaStore;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.Html;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.appevents.AppEventsLogger;
import com.facebook.login.LoginManager;
import com.facebook.share.Sharer;
import com.facebook.share.model.ShareLinkContent;
import com.facebook.share.widget.ShareDialog;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import com.peppa.app.R;
import com.peppa.app.parsing.WebFunctions;
import com.peppa.app.utility.ConnectionDetector;
import com.peppa.app.utility.Constant;
import com.peppa.app.utility.Generalfunction;
import com.peppa.app.utility.GlobalVar;

public class RateActivity extends AppCompatActivity implements View.OnClickListener {

    private Context thisContext;
    private ConnectionDetector cdObj;

    private TextView rateNumberRateTv, lengthView;
    private ImageView dishImageRate;
    private Button doneRateBTN;
    private EditText etComment;
    private ScrollView scrollview;
    private CheckBox cb_Starter, cb_Main, cb_Dessert, cb_Drink;
    private CheckBox cb_Vegetarian, cb_vegen, cb_Gluten;
    private TextView tvDishname;

    private TextView tvPrice;
    private EditText etPrice, etDishname;
    private ImageView ivEditdishname, ivEditdishprice;

    private String Dishname = "", Dishprice = "", DishRestaurantName = "", Dishid = "", Dishimage = "",tempimage="";
    private String strValidateMessage = "";

    private Uri fileUri;
    private String strImgContenttype;

    private boolean IsReposted;
    private String strComment;
    private String strRate = "0";
    private ArrayList<String> disheTaglist = new ArrayList<String>();

    private String strWebDishName = "", strWebDishPrice = "", strWebImage = "", strWebImageid = "";
    private ArrayList<String> arry_list_Tags = new ArrayList<>();

    private CallbackManager callbackManager;
    private LoginManager loginManager;

    private CheckBox checkFacebook;
    private CheckBox checkInstagram;

    private ShareDialog shareDialog;

    private TextView tvFacebookContent;
    Bitmap bmRoted;
    float angle=0;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        facebookSDKInitialize();
        setContentView(R.layout.activity_rate);
        thisContext = this;
        cdObj = new ConnectionDetector(thisContext);


        try {
            Intent intent = getIntent();
            Dishname           = intent.getExtras().getString("dishname");
            Dishprice          = intent.getExtras().getString("dishprice");
            DishRestaurantName = intent.getExtras().getString("restaurantname");
            Dishid             = intent.getExtras().getString("dishid");
            Dishimage          = Generalfunction.Isnull(intent.getExtras().getString("dishimage"));
            IsReposted         = intent.getExtras().getBoolean("reposted");
            strComment         = intent.getExtras().getString("comment");
            strRate            = intent.getExtras().getString("rate");
            disheTaglist       = (ArrayList<String>) getIntent().getExtras().getSerializable("taglist");
            strWebImageid      = intent.getExtras().getString("imageId");


        } catch (Exception e) {
        }

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("");
        setSupportActionBar(toolbar);

        getSupportActionBar().setHomeAsUpIndicator(R.mipmap.back_white);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        lengthView          = (TextView) findViewById(R.id.editLengthView);
        tvDishname          = (TextView) findViewById(R.id.dishNameRateTv);
        TextView restaurant = (TextView) findViewById(R.id.restaurantNameDetailTv);
        rateNumberRateTv    = (TextView) findViewById(R.id.rateNumberRateTv);
        tvPrice             = (TextView) findViewById(R.id.tvdishprice);

        etComment           = (EditText) findViewById(R.id.howWasThisDishRateEt);
        etPrice             = (EditText) findViewById(R.id.etdishprice);
        etDishname          = (EditText) findViewById(R.id.etdishname);

        dishImageRate       = (ImageView) findViewById(R.id.dishImageRate);
        ivEditdishname      = (ImageView) findViewById(R.id.ivdishnameEdit);
        ivEditdishprice     = (ImageView) findViewById(R.id.ivdishpriceEdit);

        doneRateBTN         = (Button) findViewById(R.id.doneRateButton);

        scrollview          = (ScrollView) findViewById(R.id.ScrollViewRate);

        cb_Starter          = (CheckBox) findViewById(R.id.checkStarter);
        cb_Main             = (CheckBox) findViewById(R.id.checkMain);
        cb_Dessert          = (CheckBox) findViewById(R.id.checkDessert);
        cb_Drink            = (CheckBox) findViewById(R.id.checkDrink);
        cb_Vegetarian       = (CheckBox) findViewById(R.id.checkVegita);
        cb_vegen            = (CheckBox) findViewById(R.id.checkVegan);
        cb_Gluten           = (CheckBox) findViewById(R.id.checkGluten);

        checkFacebook       = (CheckBox) findViewById(R.id.checkfacebook);
        checkInstagram      = (CheckBox) findViewById(R.id.checkinstagram);

        SeekBar seekBarRate = (SeekBar) findViewById(R.id.seekBarRate);

        tvFacebookContent   = new TextView(thisContext);
        tvDishname.setText(Dishname);
        restaurant.setText(DishRestaurantName);

        tvPrice.setText("- $" + Dishprice);

        Typeface playfair = Typeface.createFromAsset(getAssets(), "fonts/playfair.otf");
        Typeface lato     = Typeface.createFromAsset(getAssets(), "fonts/lato.ttf");

        tvDishname.setTypeface(playfair);
        restaurant.setTypeface(lato);
        tvPrice.setTypeface(lato);
        if (cdObj.isConnectingToInternet()) {

            //Get - Dish Detail
            new getDish().execute();

        } else {
            Generalfunction.Simple1ButtonDialog(thisContext.getResources().getString(R.string.Internet_Message), thisContext);
        }







        etPrice.setVisibility(View.GONE);
        etDishname.setVisibility(View.GONE);

        etDishname.setText(Dishname);
        etPrice.setText("$" + Dishprice);

        shareDialog = new ShareDialog(this);  // intialize facebook shareDialog.</p>

        seekBarRate.setMax(100);

        seekBarRate.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                try {
                    Log.d(Constant.TAG, "setseekbarProgress: "+progress);
                    int MIN = 5;
                    if (progress < MIN) {
                        rateNumberRateTv.setText("" + 0);
                    } else {
                        if (progress % 5 == 0) {
                            float decimalProgress = (float) progress / 10;
                            rateNumberRateTv.setText("" + decimalProgress);
                            if (progress == 100) {
                                rateNumberRateTv.setText("" + (progress / 10));
                            }

                        }
                    }
                }
                catch(Exception e){e.printStackTrace();}

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        //set - Dish tag
        if (disheTaglist.size() > 0) {
            for (int i = 0; i < disheTaglist.size(); i++) {

                //vertical tag
                if (disheTaglist.get(i).toLowerCase().equalsIgnoreCase(getResources().getString(R.string.Vegetarian_label).toLowerCase())) {
                    cb_Vegetarian.setChecked(true);
                }
                if (disheTaglist.get(i).toLowerCase().equalsIgnoreCase(getResources().getString(R.string.Vegan_label).toLowerCase())) {
                    cb_vegen.setChecked(true);
                }
                if (disheTaglist.get(i).toLowerCase().equalsIgnoreCase(getResources().getString(R.string.Gluten_free_label).toLowerCase())) {
                    cb_Gluten.setChecked(true);
                }

                //horizontal tag
                if (disheTaglist.get(i).toLowerCase().equalsIgnoreCase(getResources().getString(R.string.entry_label).toLowerCase())) {
                    cb_Starter.setChecked(true);
                }
                if (disheTaglist.get(i).toLowerCase().equalsIgnoreCase(getResources().getString(R.string.Main_label).toLowerCase())) {
                    cb_Main.setChecked(true);
                }
                if (disheTaglist.get(i).toLowerCase().equalsIgnoreCase(getResources().getString(R.string.Dessert_label).toLowerCase())) {
                    cb_Dessert.setChecked(true);
                }
                if (disheTaglist.get(i).toLowerCase().equalsIgnoreCase(getResources().getString(R.string.Drink_label).toLowerCase())) {
                    cb_Drink.setChecked(true);
                }
            }
        }

        doneRateBTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (Validation()) {
                    Generalfunction.RefreshvalueTrue(thisContext);
                    if (cdObj.isConnectingToInternet()) {
                        new RateDish().execute(Dishid, rateNumberRateTv.getText().toString(), etComment.getText().toString());
                    } else {
                        Generalfunction.Simple1ButtonDialog(thisContext.getResources().getString(R.string.Internet_Message), thisContext);
                    }
                } else {
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

        ivEditdishname.setOnClickListener(this);
        ivEditdishprice.setOnClickListener(this);

        etDishname.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    Generalfunction.hideKeyboard(RateActivity.this);
                    etDishname.setVisibility(View.GONE);
                    tvDishname.setVisibility(View.VISIBLE);
                    tvDishname.setText(etDishname.getText().toString().trim());
                    strWebDishName = etDishname.getText().toString().trim();
                    return true;
                }
                return false;
            }
        });

        etPrice.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    Generalfunction.hideKeyboard(RateActivity.this);
                    etPrice.setVisibility(View.GONE);
                    tvPrice.setVisibility(View.VISIBLE);
                    tvPrice.setText("- " + etPrice.getText().toString().trim());
                    strWebDishPrice = etPrice.getText().toString().trim();
                    if (strWebDishPrice.contains("$")) {
                        strWebDishPrice = strWebDishPrice.replace("$", "");
                    }

                    return true;
                }
                return false;
            }
        });

        etPrice.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.toString().length() < 1) {
                    etPrice.setText("$");
                } else {
                    if (s.toString().charAt(0) != '$') {
                        String str = etPrice.getText().toString().replace("$", "");
                        etPrice.setText("$" + str);
                        etPrice.setSelection(etPrice.getText().toString().length());
                    }
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        checkInstagram.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    // perform logic
                    ShareOnInstagram();
                } else {
                    checkInstagram.setChecked(false);
                }
            }
        });

        checkInstagram.setVisibility(View.GONE);
        checkFacebook.setVisibility(View.VISIBLE);

        if (!Generalfunction.isEmptyCheck(Dishimage)) {
            Intent shareIntent = new Intent(Intent.ACTION_SEND);
            shareIntent.setType("image/*"); // set mime type
            //following logic is to avoide option menu, If you remove following logic then android will display list of application which support image/* mime type
            PackageManager pm = getPackageManager();
            List<ResolveInfo> activityList = pm.queryIntentActivities(shareIntent, 0);
            for (final ResolveInfo app : activityList) {
                if ((app.activityInfo.name).contains("instagram")) {
                    checkInstagram.setVisibility(View.VISIBLE);
                }
            }
        }

        //Set comment text - if user rate same dish 2 time then display previous comment
        if (IsReposted) {
            etComment.setText(strComment);
            if (!Generalfunction.isEmptyCheck(strRate)) {
                float progress = 0;
                progress = (Float.valueOf(strRate)) * 10 ;
                // Log.d(Constant.TAG, "onCreate: "+progress+ " int : "+(int) progress);
                try{
                    seekBarRate.setProgress((int) progress);
                }
                catch(Exception e){e.printStackTrace();}
            }
        }


    }




   /* public double getConvertedValue(int intVal){
        double floatVal = 0;
        floatVal = .5f * intVal;
        return floatVal;
    }*/

    @Override
    public void onClick(View v) {
        switch (v.getId()) {

            case R.id.ivdishnameEdit:
                tvDishname.setVisibility(View.GONE);
                etDishname.setVisibility(View.VISIBLE);
                etDishname.requestFocus();

                etDishname.setSelection(etDishname.getText().toString().length());
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.showSoftInput(etDishname, InputMethodManager.SHOW_IMPLICIT);
                break;

            case R.id.ivdishpriceEdit:
                tvPrice.setVisibility(View.GONE);
                etPrice.setVisibility(View.VISIBLE);
                etPrice.requestFocus();

                etPrice.setSelection(etPrice.getText().toString().length());
                InputMethodManager imm1 = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm1.showSoftInput(etPrice, InputMethodManager.SHOW_IMPLICIT);
                break;

        }
    }


    //Check validation
    private boolean Validation() {
        boolean isValidate = true;

        arry_list_Tags = new ArrayList<>();

        if (cb_Starter.isChecked()) {
            arry_list_Tags.add(getResources().getString(R.string.entry_label));
        }

        if (cb_Main.isChecked()) {
            arry_list_Tags.add(getResources().getString(R.string.Main_label));
        }

        if (cb_Dessert.isChecked()) {
            arry_list_Tags.add(getResources().getString(R.string.Dessert_label));
        }

        if (cb_Drink.isChecked()) {
            arry_list_Tags.add(getResources().getString(R.string.Drink_label));
        }

        if (cb_Vegetarian.isChecked()) {
            arry_list_Tags.add(getResources().getString(R.string.Vegetarian_label));
        }

        if (cb_vegen.isChecked()) {
            arry_list_Tags.add(getResources().getString(R.string.Vegan_label));
        }

        if (cb_Gluten.isChecked()) {
            arry_list_Tags.add(getResources().getString(R.string.Gluten_free_label));
        }

        String strcontentdesription="" ;
        if (!Generalfunction.isEmptyCheck(strcontentdesription)) {
            strcontentdesription = strcontentdesription+"My thoughts: " + etComment.getText().toString().trim() + "\n<br>My rating: " + rateNumberRateTv.getText().toString();
        } else {
            strcontentdesription = strcontentdesription+"My rating: " + rateNumberRateTv.getText().toString();
        }

        if(arry_list_Tags.size()>0){
            StringBuilder commaSepValueBuilder = new StringBuilder();
            for(int i=0;i<arry_list_Tags.size();i++){
                commaSepValueBuilder.append(arry_list_Tags.get(i));
                //if the value is not the last element of the list
                //then append the comma(,) as well
                if ( i != arry_list_Tags.size()-1){
                    commaSepValueBuilder.append(", ");
                }
            }
            if(!Generalfunction.isEmptyCheck(commaSepValueBuilder.toString())){
                strcontentdesription=strcontentdesription+"\n<br>My Tag: " + commaSepValueBuilder.toString();
            }
        }

        tvFacebookContent=new TextView(thisContext);
        tvFacebookContent.setText(Html.fromHtml(strcontentdesription));

        return isValidate;
    }


    //Call - Rate Dish API
    public class RateDish extends AsyncTask<String, Void, String> {

        ProgressDialog progress;
        String token;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            SharedPreferences prefs = getSharedPreferences("MY_PREFS", MODE_PRIVATE);
            token = prefs.getString("token", "");
            Log.d("token", token);

            progress = new ProgressDialog(thisContext);
            progress.setMessage(getResources().getString(R.string.progressMsg));
            progress.setCancelable(false);
            progress.show();
        }

        @Override
        protected String doInBackground(String... params) {
            return WebFunctions.RateDish_Image(params[0], params[1], token, params[2], arry_list_Tags, strWebDishName, strWebDishPrice, strWebImage, strWebImageid);
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);

            progress.dismiss();
            if (!result.equals("")) {
                JSONObject jsonObject;
                String Message = "";
                try {
                    Log.d("rup", "onPostExecute: " + result);
                    jsonObject = new JSONObject(result);

                    if (jsonObject.getString("success").toLowerCase().equalsIgnoreCase("yes")) {
                        Message = jsonObject.getString("message");
                        DisplayMessage(Message);
                    } else if (jsonObject.getString("success").toLowerCase().equalsIgnoreCase("no")) {
                        Message = jsonObject.getString("message");
                        Generalfunction.Simple1ButtonDialogClick2(Message, RateActivity.this);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }


            }
//
//            boolean response=false;
//            JSONObject jsonObject;
//
//            try {
//                Log.d(Constant.TAG, "RateDish_Image:Response "+result);
//                jsonObject = new JSONObject(result);
//                if (jsonObject.has("success")) {
//                    response = true;
//                } else {
//                    response = false;
//                }
//            } catch (JSONException e) {
//                // TODO Auto-generated catch block
//                e.printStackTrace();
//            }
//
//
//            if (response) {
//
//                Toast.makeText(thisContext, "Dish has been rated.", Toast.LENGTH_SHORT).show();
//                Generalfunction.RefreshvalueTrue(thisContext);
//                if (checkFacebook.isChecked()) {
//                    PostImageonFacebook();
//                } else {
//                    finish();
//                }
//            } else {
//                Toast.makeText(thisContext, "Please try again.", Toast.LENGTH_SHORT).show();
//            }
        }
    }

    private void DisplayMessage(String strMessage) {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(RateActivity.this);
        alertDialogBuilder.setTitle(getResources().getString(R.string.app_name));
        alertDialogBuilder.setMessage(strMessage);
        alertDialogBuilder.setCancelable(false);

        alertDialogBuilder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
//                Intent intent = new Intent(RateActivity.this, UserProfileActivity.class);
//                intent.putExtra(Constant.isMyprofile, true);
//                startActivity(intent);

                finish();
            }
        });
        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }

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

            return WebFunctions.getDish1(Dishid, token);
        }

        @Override
        protected void onPostExecute(String rest) {
            super.onPostExecute(rest);
            progress.dismiss();

            try{
                if(rest!=null){
                    JSONObject json = new JSONObject(rest);

                    String status1 = json.getString("success");

                        String url=json.getString("url");
                        Generalfunction.DisplayImage_picasso(url, thisContext, Constant.case2, dishImageRate, Constant.Ph_dish);


                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

            @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_rate, menu);
        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.actionedit) {
            new AlertDialog.Builder(RateActivity.this)
                    .setTitle("Are You Sure")
                    .setMessage("You want to edit this image?")
                    .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                            UpdateImage();

                        }
                    })
                    .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            // do nothing
                            dialog.dismiss();
                        }
                    })
                    .setIcon(android.R.drawable.ic_dialog_alert)
                    .show();
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
        else if (id == R.id.action_rotate) {

            if (bmRoted != null) {
                angle+=90;
                Bitmap bitmap = rotateImage(bmRoted,angle);
                if (bitmap != null) {
                    bmRoted = bitmap;
                    dishImageRate.setImageBitmap(bmRoted);
                    strWebImage = Generalfunction.bitmapToBase64(bmRoted);
                }
          }
            else{
                angle+=90;

                Bitmap image=((BitmapDrawable)dishImageRate.getDrawable()).getBitmap();
                image=rotateImage(image,angle);
                if (image != null) {
                    bmRoted = image;
                    dishImageRate.setImageBitmap(bmRoted);
                    strWebImage = Generalfunction.bitmapToBase64(bmRoted);
                }
            }
        }

        return super.onOptionsItemSelected(item);
    }
    public static Bitmap rotateImage(Bitmap sourceImage, float angle)
    {
        Matrix matrix = new Matrix();
        matrix.postRotate(angle);
        return Bitmap.createBitmap(sourceImage, 0, 0, sourceImage.getWidth(), sourceImage.getHeight(), matrix, true);
    }


    //Update Image
    private void UpdateImage() {
        if (ActivityCompat.checkSelfPermission(thisContext, Manifest.permission.CAMERA)!= PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(RateActivity.this,new String[]{Manifest.permission.CAMERA}, Constant.PERMISSION_REQUEST_CODE_Camera);
        }
        else if (ActivityCompat.checkSelfPermission(thisContext, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            // Request missing storage permission.
            ActivityCompat.requestPermissions(RateActivity.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, Constant.PERMISSION_REQUEST_CODE_STORAGE);
        }else {
            openImageIntent();
        }
    }


    private void openImageIntent() {
        // Determine URI of camera image to save.
        fileUri = Generalfunction.getCameraUri();

        // Camera.
        final List<Intent> cameraIntents = new ArrayList<Intent>();
        final Intent captureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        final PackageManager packageManager = getPackageManager();
        final List<ResolveInfo> listCam = packageManager.queryIntentActivities(captureIntent, 0);
        for (ResolveInfo res : listCam) {
            final String packageName = res.activityInfo.packageName;
            final Intent intent = new Intent(captureIntent);
            intent.setComponent(new ComponentName(res.activityInfo.packageName, res.activityInfo.name));
            intent.setPackage(packageName);
            intent.putExtra(MediaStore.EXTRA_OUTPUT, fileUri);
            cameraIntents.add(intent);
        }

        // File System.
        final Intent galleryIntent = new Intent();
        galleryIntent.setType("image/*");
        galleryIntent.setAction(Intent.ACTION_GET_CONTENT);

        // Chooser of file System options.
        final Intent chooserIntent = Intent.createChooser(galleryIntent, "Select Source");

        // Add the camera options.
        chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, cameraIntents.toArray(new Parcelable[cameraIntents.size()]));

        startActivityForResult(chooserIntent, Constant.CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE);
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {

        if (requestCode == Constant.PERMISSION_REQUEST_CODE_Camera) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                openImageIntent();
            } else {
                Generalfunction.HandleApppermission(requestCode,permissions,grantResults,thisContext);
            }
        }
        else {
            Generalfunction.HandleApppermission(requestCode,permissions,grantResults,thisContext);
        }

    }



    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {

            if (requestCode == Constant.CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE) {

                Uri selectedImageUri;
                boolean flagIscameragallery;
                boolean flagIscamera = false;
                boolean iscamerabutgallery = false;
                String strFilename;

                // boolean isCamera = false;
                if (data == null) {
                    flagIscamera = true;
                }

                Log.d("navigationResult  ", data + " ");

                selectedImageUri = (data == null ? true : MediaStore.ACTION_IMAGE_CAPTURE.equals(data.getAction())) ? fileUri : (data == null ? null : data.getData());

                if (selectedImageUri != null) {

                    strFilename = selectedImageUri.toString();
                    flagIscameragallery = false;

                    if (flagIscamera) {
                        iscamerabutgallery = true;
                    }
                    flagIscamera = false;

                } else {
                    strFilename = fileUri.getPath();
                    flagIscamera = true;
                }
                flagIscameragallery = true;

                GetSelectedPhotobitmap(strFilename, flagIscameragallery, flagIscamera, iscamerabutgallery);

            } else {
                callbackManager.onActivityResult(requestCode, resultCode, data);
                Log.d(Constant.TAG, "facebook: onActivityResult: request cod:" + requestCode + " result code:" + requestCode);
            }
        }
    }


    /**
     * image methodology when image select from
     * gallery
     * or
     * camera
     */
    private void GetSelectedPhotobitmap(String Filename, boolean flag_Cam_Gallery, boolean flagIscamera, boolean isCamerabutgallery) {

        Uri imageUri = null;

        if (flag_Cam_Gallery) {
            if (flagIscamera) {
                File imgFile = new File(Filename);
                imageUri = Uri.fromFile(imgFile);
            } else {
                imageUri = Uri.parse(Filename);
            }
        } else {
        }

        Generalfunction.DisplayLog("onCreate: Uri  " + imageUri);

        //Get Image mime type
        ContentResolver cR = thisContext.getContentResolver();
        MimeTypeMap mime = MimeTypeMap.getSingleton();
        strImgContenttype = mime.getExtensionFromMimeType(cR.getType(imageUri));

        if (strImgContenttype == null) {
            String filePath = imageUri.getPath();
            strImgContenttype = filePath.substring(filePath.lastIndexOf(".") + 1);
        }
        Generalfunction.DisplayLog("onCreate: Imagetype " + strImgContenttype);

        //Get bitmap with Decode and center Rotation
        Bitmap bitmapDecode = Generalfunction.decodeFile(imageUri, thisContext);
        Generalfunction.DisplayLog("onCreate: bitmapdecode " + bitmapDecode);


        // rotate bitmap when it is a not right
       bmRoted = Generalfunction.rotateBitmap(bitmapDecode, imageUri.getPath());
        if (bmRoted == null) {
            bmRoted = bitmapDecode;
            Generalfunction.DisplayLog("onCreate: bitmapRotate" + bmRoted);
        }

        //When Decode and rotate time we don't have bitmap then get bitmap from ImageView
        if (bmRoted == null) {
            bmRoted = ((BitmapDrawable) dishImageRate.getDrawable()).getBitmap();
            Generalfunction.DisplayLog("onCreate: bitmap from imageview" + bmRoted);

        }

        //Set bitmap in Final Bitmap
        if (bmRoted != null) {
            dishImageRate.setImageBitmap(bmRoted);
            strWebImage = Generalfunction.bitmapToBase64(bmRoted);

        }

        //Temporary stored Image delete
        try {
            if (isCamerabutgallery) {
                File imgFile = new File(imageUri.getPath());
                boolean flag = imgFile.delete();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }


    //Share - share image with instagram
    private void ShareOnInstagram() {

        //String str=Generalfunction.Isnull(Dishname+ " is a Dish of "+DishRestaurantName+"\n "+etComment.getText().toString());
        Log.d(Constant.TAG, "ShareOnInstagram: ");

        Bitmap image = BitmapFactory.decodeResource(getResources(), R.mipmap.food_image);
        image        = ((BitmapDrawable) dishImageRate.getDrawable()).getBitmap();

        GlobalVar.setMyBooleanPref(thisContext, Constant.bitmapToimage, true);

        File file = Generalfunction.SaveBitmap(image);
        Uri uri   = Uri.fromFile(file);

        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.setType("image/*"); // set mime type
        shareIntent.putExtra(Intent.EXTRA_STREAM, uri); // set uri
        shareIntent.setPackage("com.instagram.android");
        startActivity(shareIntent);

    }


    //Share - share image with facebook
    protected void facebookSDKInitialize() {
        // Initialize the facebook sdk and then callback manager will handle the login responses.
        FacebookSdk.sdkInitialize(getApplicationContext());
        callbackManager = CallbackManager.Factory.create();
    }


    @Override
    protected void onResume() {
        super.onResume();
        // Logs 'install' and 'app activate' App Events.
        AppEventsLogger.activateApp(this);
    }


    @Override
    protected void onPause() {
        super.onPause();
        // Logs 'app deactivate' App Event.
        AppEventsLogger.deactivateApp(this);
    }


    private void PostImageonFacebook() {

        if (ShareDialog.canShow(ShareLinkContent.class)) {
            String strImageurl = Constant.Peppa_Website+"restaurant-image.jpg";
            if (!Generalfunction.isEmptyCheck(Dishimage)) {
                strImageurl=Dishimage;
            }
           // Log.d(Constant.TAG, "facebook: PostImageonFacebook content: "+strcontentdesription+ " image url: "+strImageurl+ " content url: "+Constant.Peppa_Website+"dishes/" + Dishid);
            String strTital=Dishname + " from " + DishRestaurantName.toUpperCase();
            if(strTital.length()<24){
                int i=26-strTital.length();
                strTital=strTital+String.format("%"+i+"s", "");
            }
            String dishurl=Constant.Peppa_Website+"dishes/"+Dishid;
            ShareLinkContent linkContent = new ShareLinkContent.Builder()
                    .setContentTitle(strTital)
                    .setImageUrl(Uri.parse(strImageurl))
                    .setContentDescription(tvFacebookContent.getText().toString())
                    .setContentUrl(Uri.parse(dishurl))
                    .build();

            shareDialog.registerCallback(callbackManager, new FacebookCallback<Sharer.Result>() {
                @Override
                public void onSuccess(Sharer.Result result) {
                    Toast.makeText(RateActivity.this, "You shared this post", Toast.LENGTH_SHORT).show();
                    checkFacebook.setEnabled(false);
                    Log.d(Constant.TAG, "facebook onSuccess: ");
                    finish();
                }

                @Override
                public void onCancel() {
                    Toast.makeText(RateActivity.this, "Shared post is cancel", Toast.LENGTH_SHORT).show();
                    Log.d(Constant.TAG, "facebook oncancel: ");
                    finish();
                }

                @Override
                public void onError(FacebookException e) {
                    e.printStackTrace();
                    Log.d(Constant.TAG, "facebook error: onError: ");
                    finish();
                }
            });
            shareDialog.show(linkContent);  // Show facebook ShareDialog
        } else {
            finish();
        }

    }



}