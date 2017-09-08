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
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Base64;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.webkit.MimeTypeMap;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.peppa.app.Adapter.GooglePlacesAutocompleteAdapter;
import com.peppa.app.Fragment.SettingsFragment;
import com.peppa.app.R;
import com.peppa.app.model.AddressModel;
import com.peppa.app.model.PlacePredictionModel;
import com.peppa.app.model.SearchDishModel;
import com.peppa.app.parsing.WebFunctions;
import com.peppa.app.utility.ConnectionDetector;
import com.peppa.app.utility.Constant;
import com.peppa.app.utility.Generalfunction;
import com.peppa.app.utility.GlobalVar;
import com.peppa.app.utility.SimpleTextWatcher;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

/**
 * Created by Trieu Tuan on 5/18/2017.
 * Copyright (C) SFR Software.
 */

public class PostActivity_ extends AppCompatActivity {
    private Context mContext;
    private ConnectionDetector cdObj;
    private ProgressDialog progress;
    private Bitmap webBitmap = null;
    private boolean flagIsNextdish=false;
    public static boolean flagPrice = true;
    private boolean flagQuickpost = false;

    //
    private String strValidateMessage;
    private String strSelectedRestaurantName;
    private String strSelectedRestaurantAddress;
    double selectedRestaurantLatitude;
 double selectedRestaurantLongitude;
    private String strSelectedDishName;
    private String strSelectedDishPrice;
    private String strSelectedGooglePlaceId;

    //final value
    private String strRestaurantName = "";
    private String strRestaurantAddress = "";
    private String strGooglePlaceId = "";
 double restaurantLatitude = 0.0f;
 double restaurantLongitude = 0.0f;
    private String strDishName = "";
    private String strDishPrice = "";
    private String strPhotoBase64 = "";
    String strRestaurant_id;
    //Views
    private Button btnDone;
    private Button llNextDish;
    private ImageView postImage;
    private AutoCompleteTextView acrestaurantInput;
    private AutoCompleteTextView acrestAddress;
    private AutoCompleteTextView acdishInput;
    private AutoCompleteTextView acpriceInput;
    private ScrollView accountScroll;
    private SearchDishModel DishSelected ;
    private Uri fileUri;
    int caseNumber=0;
    String strimage;

    AddressModel placeDetail;
    double latitude,longitude;
    String Quickpost="false";
    float angle=0;
    String imageIntentURI;
    byte imageInByte[];


    public static final String MyPREFERENCES = "MyPrefs" ;
    public static String newlatitude = "latitude";
    public static String newLongitude = "longitude";
    public static final String Email = "emailKey";

int flag;
    private final Charset UTF8_CHARSET = Charset.forName("UTF-8");
    SharedPreferences sharedpreferences;
    String Message = "";
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post);


        mContext = this;
        cdObj = new ConnectionDetector(mContext);
        //Set toolbar
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("");
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setHomeAsUpIndicator(R.mipmap.back_white);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        sharedpreferences = getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);

        bindViews();

        //Google Places API
        boolean IsGpsEnable = Generalfunction.CheckGpsAvailable(mContext);
        if (!IsGpsEnable) {
            Generalfunction.GpsAlertMessage(mContext, PostActivity_.this);
        }

        //check internet connection
        if (!cdObj.isConnectingToInternet()) {
            Generalfunction.Simple1ButtonDialog(mContext.getResources().getString(R.string.Internet_Message), mContext);
        }
    }

    private void bindViews() {
        btnDone = (Button) findViewById(R.id.btnDone);
        llNextDish = (Button) findViewById(R.id.llnextDish);
        postImage = (ImageView) findViewById(R.id.postImage);
        accountScroll = (ScrollView) findViewById(R.id.ScrollViewRat);

        acrestaurantInput = (AutoCompleteTextView) findViewById(R.id.acrestaurantInput);
        acrestAddress = (AutoCompleteTextView) findViewById(R.id.acrestAddress);
        acdishInput = (AutoCompleteTextView) findViewById(R.id.acdishInput);
        acpriceInput = (AutoCompleteTextView) findViewById(R.id.acpriceInput);
      //  new VerifyingUser().execute();
        SharedPreferences pre = getSharedPreferences(MyPREFERENCES, MODE_PRIVATE);


      String flag1= pre.getString("flag", "");

if(flag1.equals("1")){

        acrestaurantInput.setEnabled(false);
        acrestAddress.setEnabled(false);
        acdishInput.setFocusable(true);
    acdishInput.requestFocus();
        acdishInput.setEnabled(true);
        acdishInput.setCursorVisible(true);


}
        acrestaurantInput.setAdapter(new GooglePlacesAutocompleteAdapter(this, android.R.layout.simple_list_item_1));
        acrestaurantInput.setOnItemClickListener(new AdapterView.OnItemClickListener()
        {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id)
            {
                final PlacePredictionModel predict = (PlacePredictionModel) parent.getAdapter().getItem(position);
                acrestaurantInput.setText(predict.mainText);

                strSelectedRestaurantName = predict.mainText;
                strSelectedGooglePlaceId = predict.place_id;


                new Thread(new Runnable()
                {
                    @Override
                    public void run()
                    {
                       placeDetail = WebFunctions.getPlaceDetails(predict.place_id);
                        if (placeDetail != null)
                        {
                            strSelectedRestaurantAddress = placeDetail.formattedAddress;
                            selectedRestaurantLatitude = placeDetail.latitude;
                            selectedRestaurantLongitude = placeDetail.longitude;


                            SharedPreferences.Editor editor = sharedpreferences.edit();

                            String newlat = placeDetail.latitude+"";
                            String  newLong = placeDetail.longitude+"";
                            editor.putString(newlatitude, newlat);
                            editor.putString(newLongitude, newLong);
                            editor.commit();


                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    acrestAddress.setText(placeDetail.formattedAddress);

                                }
                            });
                        }
                    }
                }).start();


                acdishInput.requestFocus();
                acdishInput.setText("");
                acpriceInput.setText("");

                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(acrestaurantInput.getWindowToken(), InputMethodManager.RESULT_UNCHANGED_SHOWN);
            }
        });

        acrestaurantInput.addTextChangedListener(new SimpleTextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (Generalfunction.isEmptyCheck(s.toString())) {

                    acdishInput.setText("");
                    acrestAddress.setText("");
                    acpriceInput.setText("");

                    strRestaurantName = "";
                    strRestaurantAddress = "";
                    strGooglePlaceId = "";
                    restaurantLatitude = 0.0f;
                    restaurantLongitude = 0.0f;
                    strDishName = "";
                    strDishPrice = "";
                }
            }
        });

        acdishInput.setAdapter(new DishAutoCompleteAdapter(this, android.R.layout.simple_list_item_1));
        acdishInput.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//                SearchDishModel selected = (SearchDishModel) parent.getItemAtPosition(position);
                //Store values
                strSelectedDishPrice = DishSelected.strDishprice;
                strSelectedDishName = DishSelected.strDishName;
                Log.e("myDish name", "name:"+ DishSelected.strDishName);
                //Invalidate view
                acdishInput.setText(strSelectedDishName);
                acpriceInput.setText(String.format("$%s", strSelectedDishPrice));
                acpriceInput.setEnabled(false);

                //Hide keyboard
                Generalfunction.hideKeyboard(PostActivity_.this);
                btnDone.requestFocus();
                Generalfunction.hideKeyboard(PostActivity_.this);

                //Focus Done button
                btnDone.getParent().requestChildFocus(btnDone, btnDone);
                btnDone.setFocusable(true);
                btnDone.setFocusableInTouchMode(true);///add this line
            }
        });

        acdishInput.addTextChangedListener(new SimpleTextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (!Generalfunction.isCompare(s.toString(), strSelectedDishName)) {
                    acpriceInput.setEnabled(true);
                    if (acdishInput.getText().toString().length() < 1) {
                        acpriceInput.setText("$");
                    }
                }
            }
        });

        acpriceInput.addTextChangedListener(new SimpleTextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.toString().length() < 1) {
                    acpriceInput.setText("$");
                } else {
                    if (s.toString().charAt(0) != '$') {
                        String str = acpriceInput.getText().toString().replace("$", "");
                        acpriceInput.setText(String.format("$%s", str));
                        acpriceInput.setSelection(acpriceInput.getText().toString().length());
                    }
                }
            }
        });



         /*keyboard action of autocomplete for
              @Restaurant
              @Dish
              @Address
              @price
        */

        //Restaurant
        acrestaurantInput.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_NEXT) {
                    acdishInput.requestFocus();
                    return true;
                }
                return false;
            }
        });

        //Dish
        acdishInput.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_NEXT) {
                    acpriceInput.requestFocus();
                    acpriceInput.setSelection(acpriceInput.getText().length());
                    return true;
                }
                return false;
            }
        });

        //Dish price
        acpriceInput.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                   // btnDone.requestFocus();
                    Generalfunction.hideKeyboard(PostActivity_.this);
//                    btnDone.getParent().requestChildFocus(btnDone, btnDone);
//                    btnDone.setFocusable(true);
//                    btnDone.setFocusableInTouchMode(true);///add this line
                    return true;
                }
                return false;
            }
        });

        //price enable and disabled
        if (flagPrice) {
            acpriceInput.setFocusable(true);
        }




        btnDone.setVisibility(View.VISIBLE);
        llNextDish.setVisibility(View.VISIBLE);
        View vline = (View) findViewById(R.id.vline);
        vline.setVisibility(View.VISIBLE);




        /*Check screen like
           @Post
           @quick post
           @next dish
           and set functionality of view
           for that screen
        */
        btnDone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {



                    PostDish_API();
                    flagIsNextdish = false;
                    flag = 0;
                    SharedPreferences.Editor editor = sharedpreferences.edit();


                    editor.putString("flag", String.valueOf(flag));

                    editor.commit();

            }
        });

        Bundle bundle = getIntent().getExtras();

        //Quick post (plus icon)
        if (GlobalVar.getMyBooleanPref(mContext, Constant.Isquickpost))
        {
            btnDone.setVisibility(View.VISIBLE);
            btnDone.setText(getResources().getString(R.string.btn_quickpost_name));
            llNextDish.setVisibility(View.GONE);
            vline.setVisibility(View.GONE);
            flagQuickpost = true;

            if (bundle != null) {
                byte imageBytes[];
                strSelectedRestaurantName = bundle.getString(Constant.Quick_RestName);
                strSelectedDishName = bundle.getString(Constant.Quick_DishName);
                strSelectedDishPrice = bundle.getString(Constant.Quick_DishPrice);
     strimage= bundle.getString(Constant.Quick_Dishimage);

                acrestaurantInput.setFocusable(false);
                acrestAddress.setFocusable(false);
                acrestAddress.setEnabled(false);



         double latitude  = Double.parseDouble(bundle.getString("latitude"));
            double   longitude= Double.parseDouble(bundle.getString("longitude"));
                SharedPreferences.Editor editor = sharedpreferences.edit();


                editor.putString(newlatitude, String.valueOf(latitude));
                editor.putString(newLongitude, String.valueOf(longitude));
                editor.commit();

                strSelectedRestaurantAddress=bundle.getString(Constant.Quick_RestAddress);
                if(strimage!=null) {
                    Generalfunction.DisplayImage_picasso(strimage, mContext, Constant.case2, postImage, Constant.Ph_dish);
//                    Bitmap bitmap=((BitmapDrawable)postImage.getDrawable()).getBitmap();
//                    webBitmap=bitmap;
                   //getBitmapFromURL(strimage);
                }
                else{
                    boolean flagIscameragallery = bundle.getBoolean("isCameraOrGallery");
                    boolean flagIscamera = bundle.getBoolean("isCamera");
                    String strFilename = bundle.getString("filename");

                    Log.e("A", strFilename + " ");
                    GetSelectedPhotobitmap(strFilename, flagIscameragallery, flagIscamera, bundle.getBoolean("isCamerabutgallery"));
                    Quickpost="true";
                }

                acrestaurantInput.setText(strSelectedRestaurantName);
                acrestAddress.setText(strSelectedRestaurantAddress);

                acrestaurantInput.setEnabled(false);

                if(strSelectedDishName!=null) {
                    acdishInput.setFocusable(false);
                    acdishInput.setText(strSelectedDishName);
                    acdishInput.setEnabled(false);
                }
                else{
                    acdishInput.setFocusable(true);
                    acdishInput.setEnabled(true);
                }
                if(strSelectedDishPrice!=null) {
                    acpriceInput.setText(String.format("$%s", strSelectedDishPrice));
                }
                else{
                    acpriceInput.setText(String.format("$%s", ""));
                }


            }
            GlobalVar.setMyBooleanPref(mContext, Constant.Isquickpost, false);
        }

        //Next dish
        else if (GlobalVar.getMyBooleanPref(mContext, Constant.IsPost_Nextdish)) {
            GlobalVar.setMyBooleanPref(mContext, Constant.IsPost_Nextdish, false);
            strSelectedRestaurantName = GlobalVar.getMyStringPref(mContext, Constant.Quick_RestName);
            acrestaurantInput.setText(strSelectedRestaurantName);

            if (!Generalfunction.isEmptyCheck(GlobalVar.getMyStringPref(mContext, Constant.Quick_RestAddress))) {
                acrestAddress.setText(GlobalVar.getMyStringPref(mContext, Constant.Quick_RestAddress));
            }

            if (ActivityCompat.checkSelfPermission(mContext, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(PostActivity_.this, new String[]{Manifest.permission.CAMERA}, Constant.PERMISSION_REQUEST_CODE_Camera);
            } else {
                openImageIntent();
            }

            GlobalVar.setMyBooleanPref(mContext, Constant.IsPost_Nextdish, false);
        }

        // Post :Fab(Floating action button) camera action
        else {
            if (bundle != null) {
                boolean flagIscameragallery = bundle.getBoolean("isCameraOrGallery");
                boolean flagIscamera = bundle.getBoolean("isCamera");
                String strFilename = bundle.getString("filename");

                Log.e("A", strFilename + " ");
                GetSelectedPhotobitmap(strFilename, flagIscameragallery, flagIscamera, bundle.getBoolean("isCamerabutgallery"));

//                if(flag==1) {
//                    acrestaurantInput.setEnabled(false);
//                    acrestAddress.setEnabled(false);
//                    acdishInput.setFocusable(true);
//                }
            }
        }



        //Click on Next item as a button and Perform action
        llNextDish.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                flagIsNextdish = true;

                flag=1;
                SharedPreferences.Editor editor = sharedpreferences.edit();


                editor.putString("flag", String.valueOf(flag));

                editor.commit();


                    PostDish_API();


            }
        });

    }

    /**
     * image methodology when image select from
     * gallery
     * or
     * camera
     */






    private void GetSelectedPhotobitmap(String Filename, boolean flag_Cam_Gallery, boolean flagIscamera, boolean isCamerabutgallery) {
        Uri imageUri = null;
        if (flag_Cam_Gallery)
        {
            if (flagIscamera)
            {
                File imgFile = new File(Filename);
                imageUri = Uri.fromFile(imgFile);
            }
            else {
                imageUri = Uri.parse(Filename);
            }
        } else {
            return;
        }

        Generalfunction.DisplayLog("onCreate: Uri  " + imageUri);

        //Get Image mime type
        ContentResolver cR = mContext.getContentResolver();
        MimeTypeMap mime = MimeTypeMap.getSingleton();
        String strImgContenttype = mime.getExtensionFromMimeType(cR.getType(imageUri));

        if (strImgContenttype == null) {
            String filePath = imageUri.getPath();
            strImgContenttype = filePath.substring(filePath.lastIndexOf(".") + 1);
        }
        Generalfunction.DisplayLog("onCreate: Imagetype " + strImgContenttype);

        //Get bitmap with Decode and center Rotation
        Bitmap bitmapDecode = Generalfunction.decodeFile(imageUri, mContext);
        Generalfunction.DisplayLog("onCreate: bitmapdecode " + bitmapDecode);

        // rotate bitmap when it is a not right
        imageIntentURI=Filename;

        Bitmap bmRoted = Generalfunction.rotateBitmap(bitmapDecode, imageUri.getPath());
        if (bmRoted == null) {
            bmRoted = bitmapDecode;
            Generalfunction.DisplayLog("onCreate: bitmapRotate" + bmRoted);
        }

        //When Decode and rotate time we don't have bitmap then get bitmap from ImageView
        if (bmRoted == null) {
            bmRoted = ((BitmapDrawable) postImage.getDrawable()).getBitmap();
            Generalfunction.DisplayLog("onCreate: bitmap from imageview" + bmRoted);
        }

        //Set bitmap in Final Bitmap
        if (bmRoted != null) {
            postImage.setImageBitmap(bmRoted);

            webBitmap = bmRoted;
            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            bmRoted.compress(Bitmap.CompressFormat.JPEG, 100, stream);
            imageInByte = stream.toByteArray();
            strPhotoBase64 = Base64.encodeToString(imageInByte, Base64.DEFAULT);




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




    private void openImageIntent() {
        if (ActivityCompat.checkSelfPermission(mContext, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(PostActivity_.this, new String[]{Manifest.permission.CAMERA}, Constant.PERMISSION_REQUEST_CODE_Camera);
        } else if (ActivityCompat.checkSelfPermission(mContext, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(PostActivity_.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, Constant.PERMISSION_REQUEST_CODE_STORAGE);
        } else {
            // Determine URI of camera image to save.
         fileUri = Generalfunction.getCameraUri();
         Intent intent=null;
            // Camera.
            final List<Intent> cameraIntents = new ArrayList<>();
            final Intent captureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
           // captureIntent.putExtra(MediaStore.EXTRA_SCREEN_ORIENTATION, ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
            final PackageManager packageManager = getPackageManager();
            final List<ResolveInfo> listCam = packageManager.queryIntentActivities(captureIntent, 0);
            for (ResolveInfo res : listCam) {
                final String packageName = res.activityInfo.packageName;
            intent = new Intent(captureIntent);
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
            Intent i = new Intent(this, PostActivity_.class);
            flag = 0;
            SharedPreferences.Editor editor = sharedpreferences.edit();


            editor.putString("flag", String.valueOf(flag));

            editor.commit();
            Intent[] intentArray = {intent, i};

            //Add the camera options.
            chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, intentArray);

            startActivityForResult(chooserIntent, Constant.CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_post, menu);
        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_camera) {
            if (ActivityCompat.checkSelfPermission(mContext, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(PostActivity_.this, new String[]{Manifest.permission.CAMERA}, Constant.PERMISSION_REQUEST_CODE_Camera);  // Request missing location permission.
            } else {
                openImageIntent();

            }

        } else if (id == android.R.id.home) {
            flag=0;
            SharedPreferences.Editor editor = sharedpreferences.edit();


            editor.putString("flag", String.valueOf(flag));

            editor.commit();
            finish();
        } else if (id == R.id.action_rotate) {

            if (webBitmap != null) {


                angle+=90;
                Bitmap bitmap = rotateImage(webBitmap,angle);
                if (bitmap != null) {
                    webBitmap = bitmap;
                    postImage.setImageBitmap(webBitmap);
                }
            }
            else{
                angle+=90;

                Bitmap image=((BitmapDrawable)postImage.getDrawable()).getBitmap();
                image=rotateImage(image,angle);
                if (image != null) {
                    webBitmap = image;
                    postImage.setImageBitmap(webBitmap);
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

    private void showProgressDialog() {
        progress = new ProgressDialog(mContext);
        progress.setMessage("Saving to your profile");
        progress.setCancelable(false);
        progress.show();
    }

    private void hideProgressDialog() {
        if (progress != null) {
            progress.dismiss();
        }
    }

    //Validate Details and post dish or give a message
    private boolean Validation() {
        boolean isValidate = true;

        String restaurantNameContent, dishNameContent, priceContent;

        restaurantNameContent = acrestaurantInput.getText().toString();
        dishNameContent = acdishInput.getText().toString();
        priceContent = acpriceInput.getText().toString();




    if (Generalfunction.isCompare(restaurantNameContent, strSelectedRestaurantName)) {
        strRestaurantName = strSelectedRestaurantName;
        strRestaurantAddress = strSelectedRestaurantAddress;
        restaurantLatitude = selectedRestaurantLatitude;
        restaurantLongitude = selectedRestaurantLongitude;
        strGooglePlaceId = strSelectedGooglePlaceId;
    } else {
        strRestaurantName = "";
        acrestaurantInput.setText("");
        acrestAddress.setText("");
    }

    if (Generalfunction.isCompare(dishNameContent, strSelectedDishName)) {
        strDishName = strSelectedDishName;
        strDishPrice = strSelectedDishPrice;
    } else {
        strDishName = dishNameContent;
        strDishPrice = priceContent.replace("$", "");
    }

    if (priceContent.contains("$")) {
        strDishPrice = priceContent.replace("$", "");
    } else {
        strDishPrice = priceContent;
    }

    strDishPrice = Generalfunction.Isnull(strDishPrice);

    if (!Generalfunction.isEmptyCheck(strDishPrice)) {
        if (Double.parseDouble(strDishPrice) < 0.5) {
            strDishPrice = "";
        }
    }

    if (Generalfunction.isEmptyCheck(strRestaurantName)) {
        isValidate = false;
        strValidateMessage = "please type restaurant name";
        acrestaurantInput.setFocusable(true);
        acrestaurantInput.requestFocus();
    } else if (Generalfunction.isEmptyCheck(strDishPrice)) {
        isValidate = false;
        strValidateMessage = "price should be greater than 0";
        acpriceInput.setFocusable(true);
        acpriceInput.requestFocus();
    }
    else if (strRestaurantName.length() > 50) {
        isValidate = false;
        strValidateMessage = "please enter less than 50 characters for Restaurant name";
        acrestaurantInput.setFocusable(true);
        acrestaurantInput.requestFocus();

    }
    else if (Generalfunction.isEmptyCheck(strDishName)) {
        isValidate = false;
        strValidateMessage = "please type dish name";
        acdishInput.setFocusable(true);
        acdishInput.requestFocus();
    } else {
        //all validations are true

        if (webBitmap != null) {


        } else {

            Bitmap bitmap = ((BitmapDrawable) postImage.getDrawable()).getBitmap();
            //strPhotoBase64=Generalfunction.bitmapToBase64(bitmap);

            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream);
            imageInByte = stream.toByteArray();
            strPhotoBase64 = Base64.encodeToString(imageInByte, Base64.DEFAULT);
        }

        //Check decimal places
        if (strDishPrice.contains(".")) {
            int i = strDishPrice.lastIndexOf('.');
            if (i != -1 && strDishPrice.substring(i + 1).length() > 2) {
                isValidate = false;
                strValidateMessage = "Price should only allow two decimal places";
                acpriceInput.setFocusable(true);
                acpriceInput.requestFocus();
            }
        }
    }


     return isValidate;
    }









    //Check validation and call - Post API
    private void PostDish_API() {
      //  Log.d(Constant.TAG, "POST: PostDish_API: ");

        llNextDish.setEnabled(false);
        btnDone.setEnabled(false);


            if (Validation()) {
                if (cdObj.isConnectingToInternet()) {
                    new Post().execute();  //post Dish
                } else {
                    Generalfunction.Simple1ButtonDialog(mContext.getResources().getString(R.string.Internet_Message), mContext);
                }
            } else {
                hideProgressDialog();
                Snackbar snackbar1 = Snackbar.make(accountScroll, strValidateMessage, Snackbar.LENGTH_LONG);
                snackbar1.show();
                llNextDish.setEnabled(true);
                btnDone.setEnabled(true);
            }



    }

    //Call - Post dish API
    private class Post extends AsyncTask<String, Void, String> {
        String token;

        @Override
        protected void onPreExecute()
        {
            super.onPreExecute();

            showProgressDialog();
            SharedPreferences prefs = getSharedPreferences("MY_PREFS", MODE_PRIVATE);

            token = prefs.getString("token", "");
            Log.d("token", token);

             SharedPreferences pre = getSharedPreferences(MyPREFERENCES, MODE_PRIVATE);
             latitude = Double.parseDouble(pre.getString(newlatitude, ""));
             longitude= Double.parseDouble(pre.getString(newLongitude, ""));


            strRestaurantName = acrestaurantInput.getText().toString();
            strRestaurantAddress=acrestAddress.getText().toString();



        }

        @Override
        protected String doInBackground(String... params)
        {
            return WebFunctions.Post_Dish(token, strRestaurantName, strRestaurantAddress, strGooglePlaceId, latitude,
                    longitude, strDishName, strDishPrice, strPhotoBase64,Quickpost);
        }

        @Override
        protected void onPostExecute(String aVoid) {
            super.onPostExecute(aVoid);
            hideProgressDialog();
            Generalfunction.RefreshvalueTrue(mContext);
            Log.d(Constant.TAG, "on post : post api - : " + aVoid);

            Log.e("LatLong2--->",latitude+"  "+longitude);



            if (!aVoid.equals("")) {
                JSONObject jsonObject;
                String Message = "";
                try {
                    Log.d("rup", "onPostExecute: " + aVoid);
                    jsonObject = new JSONObject(aVoid);

                    if (jsonObject.getString("success").toLowerCase().equalsIgnoreCase("yes")) {
                        Message = jsonObject.getString("message");
                        Generalfunction.RefreshvalueTrue(mContext);
                        JSONObject objectdish = new JSONObject(aVoid).getJSONObject("dish");

                        strRestaurantName = objectdish.getString("restaurant_name");
                        strRestaurant_id=objectdish.getString("restaurant_id");
                        try {
                            GlobalVar.setMyBooleanPref(mContext, Constant.IsPost_Dish, true);
                            Message = "This has been added to your profile, please head there when you’re ready to rate this item";

                            if (flagIsNextdish) {
                                GlobalVar.setMyBooleanPref(mContext, Constant.IsPost_Nextdish, true);
                                GlobalVar.setMyStringPref(mContext, Constant.Quick_RestName, strRestaurantName);

                                if (Generalfunction.isEmptyCheck(strRestaurantAddress)) {
                                    strRestaurantAddress = acrestAddress.getText().toString();
                                }

                                GlobalVar.setMyStringPref(mContext, Constant.Quick_RestAddress, strRestaurantAddress);
                                Generalfunction.Simple1ButtonDialogClick(Message, mContext);
                            } else {
                                Log.d(Constant.TAG, "post: onPostExecute: else");
                                if (flagQuickpost) {
                                    Log.d(Constant.TAG, "post: onPostExecute: else quick post");
                                    DisplayMessage(Message);
                                } else {
                                    //this is simple post
                                    DisplayMessage(Message);
                                }
                            }

                        } catch (Exception e) {
                            e.printStackTrace();
                        }


                    } else if (jsonObject.getString("success").toLowerCase().equalsIgnoreCase("no")) {
                        Message = jsonObject.getString("message");
                        String mail_confirmation=jsonObject.getString("mail_confirmation");
                        if(mail_confirmation.equals("true")){
                            DisplayMessage1(Message);
                        }
                        //Message = Message + "\n" + jsonObject.getString("errors");
                        else {

                            Generalfunction.Simple1ButtonDialog(Message, mContext);
                        }
                    } else {
                        Message = jsonObject.getString("message");
                        Generalfunction.Simple1ButtonDialog(Message, mContext);
                    }

                } catch (JSONException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                    Generalfunction.Simple1ButtonDialog(getResources().getString(R.string.API_Exception) + " " + aVoid, mContext);
                }

            } else {
                Generalfunction.Simple1ButtonDialog(getResources().getString(R.string.API_Exception), mContext);
            }

            llNextDish.setEnabled(true);
            btnDone.setEnabled(true);
        }
    }

    //Display message and perform Action for next screen
    private void DisplayMessage(String strMessage) {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(mContext);
        alertDialogBuilder.setTitle(mContext.getResources().getString(R.string.app_name));
        alertDialogBuilder.setMessage(strMessage);
        alertDialogBuilder.setCancelable(false);

        alertDialogBuilder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if(Quickpost.equals("true")){
                  Intent i=new Intent(PostActivity_.this,RestaurantProfileActivity.class);
                    i.putExtra(Constant.ID, strRestaurant_id);
                    startActivity(i);
                    finish();
                }
                else {


                    Intent intent = new Intent(PostActivity_.this, UserProfileActivity.class);
                    intent.putExtra(Constant.isMyprofile, true);
                    intent.putExtra("image",  imageIntentURI );
                    startActivity(intent);
                    finish();
                }
            }
        });
        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }
    private void DisplayMessage1(String strMessage) {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(mContext);
        alertDialogBuilder.setTitle(mContext.getResources().getString(R.string.app_name));
        alertDialogBuilder.setMessage(strMessage);
        alertDialogBuilder.setCancelable(false);

        alertDialogBuilder.setPositiveButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        alertDialogBuilder.setNegativeButton("Resend email", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                new Post1().execute();

            }
        });
        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }

    private class Post1 extends AsyncTask<String, Void, String> {
        String token;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progress = new ProgressDialog(mContext);
            progress.setMessage("Sending email");
            progress.setCancelable(false);
            progress.show();

            SharedPreferences prefs = getSharedPreferences("MY_PREFS", MODE_PRIVATE);

            token = prefs.getString("token", "");
            Log.d("token", token);


        }

        @Override
        protected String doInBackground(String... params) {
            return WebFunctions.post_email(token);
        }

        @Override
        protected void onPostExecute(String aVoid) {
            super.onPostExecute(aVoid);
       progress.dismiss();
            if (!aVoid.equals("")) {
                JSONObject jsonObject;
                String Message = "";
                try {
                    Log.d("rup", "onPostExecute: " + aVoid);
                    jsonObject = new JSONObject(aVoid);


                        Message = jsonObject.getString("message");
                        Toast.makeText(getApplicationContext(),Message,Toast.LENGTH_LONG).show();


                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        }
    }

            public class DishAutoCompleteAdapter extends ArrayAdapter implements Filterable {
        private ArrayList<SearchDishModel> dishModels = new ArrayList<>();
        private Filter mFilter;
        private String token;
        @Override
        public int getCount() {
            return dishModels.size();
        }

        @Override
        public String getItem(int index) {
            if (dishModels.size() > 0 && index <= dishModels.size()) {
                DishSelected = dishModels.get(index);
                return String.valueOf(dishModels.get(index).strDishName);
            } else {
                return "";
            }
        }

        public DishAutoCompleteAdapter(@NonNull Context context, @LayoutRes int resource) {
            super(context, resource);
            SharedPreferences prefs = context.getSharedPreferences("MY_PREFS", MODE_PRIVATE);
            token = prefs.getString("token", "");
        }

        @NonNull
        @Override
        public Filter getFilter() {
            if (mFilter == null) {
                mFilter = new Filter() {
                    @Override
                    protected FilterResults performFiltering(CharSequence constraint) {
                        FilterResults filterResults = new FilterResults();
                        if (constraint != null) {
                            // Retrieve the autocomplete results.
                            dishModels = autocomplete(constraint.toString());

                            // Assign the data to the FilterResults
                            filterResults.values = dishModels;
                            filterResults.count = dishModels.size();
                        }
                        return filterResults;
                    }

                    @Override
                    protected void publishResults(CharSequence charSequence, FilterResults results) {
                        if (results != null && results.count > 0) {
                            notifyDataSetChanged();
                        } else {
                            notifyDataSetInvalidated();
                        }
                    }
                };
            }
            return mFilter;
        }

        private ArrayList<SearchDishModel> autocomplete(String s) {
            strRestaurantName = acrestaurantInput.getText().toString();
            String rest = WebFunctions.getRestaurantDishlist(s, token,strRestaurantName);
            ArrayList<SearchDishModel> dishModels = new ArrayList<>();
            Log.e("RestaurantAdd", "rest:" +rest);
            if (!rest.toLowerCase().contains("sorry")) {
                try {
                    JSONObject posOBJ = new JSONObject(rest);
                    JSONArray dishesArr = posOBJ.getJSONArray("dishes");

                    for (int j = 0; j < dishesArr.length(); j++) {
                        SearchDishModel DishOBJ = new SearchDishModel();
                        JSONObject object = dishesArr.getJSONObject(j);

                        DishOBJ.id = object.getString("id");
                        DishOBJ.strDishName = object.getString("name");
                        DishOBJ.strDishprice = object.getString("price");

                        dishModels.add(DishOBJ);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            return dishModels;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK)
        {

            try {
                if (requestCode == Constant.CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE) {

                    Uri selectedImageUri;
                    boolean flagIscameragallery;
                    boolean flagIscamera = false;
                    boolean iscamerabutgallery = false;
                    String strFilename;

                    if (data == null)
                    {
                        flagIscamera = true;
                    }

                    selectedImageUri = (data == null ? true : MediaStore.ACTION_IMAGE_CAPTURE.equals(data.getAction())) ? fileUri : (data == null ? null : data.getData());

                    Generalfunction.DisplayLog("onActivityResult: selected image uri " + selectedImageUri);

                    if (selectedImageUri != null)
                    {
                        strFilename = selectedImageUri.toString();
                        flagIscameragallery = false;

                        if (flagIscamera)
                        {
                            iscamerabutgallery = true;
                        }
                        flagIscamera = false;

                    } else {

                        strFilename = fileUri.getPath();
                        flagIscamera = true;
                    }

                    flagIscameragallery = true;

                    if (caseNumber == 1)
                    {

                        ImageView imageview = SettingsFragment.ivCoverimage;

                        Generalfunction.GetSelectedPhotobitmap_uplaod(strFilename, flagIscameragallery, flagIscamera, iscamerabutgallery, imageview, PostActivity_.this,
                                GlobalVar.getMyStringPref(PostActivity_.this, Constant.loginUserID), Constant.case_imageupload_cover);

                    } else if (caseNumber == 2)
                    {

                        ImageView imageview = SettingsFragment.ivProfilephoto;

                        Generalfunction.GetSelectedPhotobitmap_uplaod(strFilename, flagIscameragallery, flagIscamera, iscamerabutgallery, imageview, PostActivity_.this,
                                GlobalVar.getMyStringPref(PostActivity_.this, Constant.loginUserID), Constant.case_imageupload_profile);

                    }
                    else
                    {
//                        try
//                        {
//                            Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), selectedImageUri);
//                            ByteArrayOutputStream stream = new ByteArrayOutputStream();
//                            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream);
//                            imageInByte = stream.toByteArray();
//                           strPhotoBase64 = Base64.encodeToString(imageInByte, Base64.DEFAULT);
//                            postImage.setImageBitmap(bitmap);
//webBitmap=bitmap;
//                        }
//                        catch (IOException e)
//                        {
//                            e.printStackTrace();
//                        }
//                        catch (Exception e)
//                        {
//                        }
        GetSelectedPhotobitmap(strFilename, flagIscameragallery, flagIscamera, iscamerabutgallery);

//
                    }

                }

            } catch (Exception e) {
                e.printStackTrace();
            }
        }

    }
}
