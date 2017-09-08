package com.peppa.app.Activity;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.ComponentName;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.Bitmap;
import android.graphics.Matrix;
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
import android.text.TextWatcher;
import android.util.Log;
import android.view.GestureDetector;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
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
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;

import java.util.ArrayList;
import java.util.List;

import com.peppa.app.Adapter.GooglePlacesAutocompleteAdapter;
import com.peppa.app.R;
import com.peppa.app.model.RestaurantLocationModel;
import com.peppa.app.model.SearchDishModel;
import com.peppa.app.model.SearchResturantModel;
import com.peppa.app.parsing.WebFunctions;
import com.peppa.app.utility.ConnectionDetector;
import com.peppa.app.utility.Constant;
import com.peppa.app.utility.Generalfunction;
import com.peppa.app.utility.GlobalVar;

public class PostActivity extends AppCompatActivity {

    private ConnectionDetector cdObj;
    private Context mContext;

    private ImageView postImage;
    private AutoCompleteTextView acRestaurant, acDish, acPrice;
    private ScrollView accountScroll;
    private Button btnDone;
    private Button llNextDish;

    private ArrayList<SearchResturantModel> array_listobject = new ArrayList<>();
    private ArrayList<SearchDishModel> array_dishlistobject = new ArrayList<>();

    private String strValidateMessage;
    private String strSelectedRestId = "", strSelectedDishname = "", strselectedResturantname = "", strselectedDishprice, strSelectedDishid;
    private String strWebRestId, strWebRestName;
    private String strwebDishname, strwebDishprice, strwebDishid;

    private String WebPhoto = "";
    private String strImgContenttype;
    private Uri fileUri;
    private Bitmap webBitmap = null;

    private boolean flagPrice = true;
    private boolean flagIsNextdish;
    private boolean flagQuickpost = false;

    private AlertDialog alertDialog;
    private String strPreviousScreen = "";

    private AutoCompleteTextView acRestAddress;
    private ArrayList<String> addresslist = new ArrayList<>();

    private TextView tvPlus;
    private AutoCompleteTextView etaddressNew;

    private int selectedrestPos = 0;
    private String strWebAddress = "", strAddressId = "", strWebPhonenumber = "";

    private String strSearchdish;
    private String strquickRestid = ""; //Quick post time user select other address

    //dialog
    private ProgressDialog progress;

    private GestureDetector gestureDetector;
    View.OnTouchListener gestureListener;

    private RestaurantNameAutocompleteAdapter addpterRestaurant;  //Restaurant Addapter
    private ArrayList RestaurantResultList = new ArrayList();

    private DishNameAutocompleteAdapter addpterDish;   //Dish addpater
    private ArrayList DishResultList = new ArrayList();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post);

        mContext = this;
        cdObj = new ConnectionDetector(mContext);


        //Set toolbar
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("");
        setSupportActionBar(toolbar);
        getSupportActionBar().setHomeAsUpIndicator(R.mipmap.back_white);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        //Mapping imageview
        postImage = (ImageView) findViewById(R.id.postImage);

        //Mapping scrollview
        accountScroll = (ScrollView) findViewById(R.id.ScrollViewRat);

        //Mapping autocompletetextview
        acRestaurant = (AutoCompleteTextView) findViewById(R.id.acrestaurantInput);
        acDish = (AutoCompleteTextView) findViewById(R.id.acdishInput);
        acPrice = (AutoCompleteTextView) findViewById(R.id.acpriceInput);
        acRestAddress = (AutoCompleteTextView) findViewById(R.id.acrestAddress);

        //Mapping linear layout
        llNextDish = (Button) findViewById(R.id.llnextDish);

        //Mapping button
        btnDone = (Button) findViewById(R.id.btnDone);


        etaddressNew = (AutoCompleteTextView) findViewById(R.id.etrestaddressnew);

        //Mapping textview
        tvPlus = (TextView) findViewById(R.id.tvplus);

        etaddressNew.setVisibility(View.GONE);

        //Google Places API
        boolean IsGpsEnable = Generalfunction.CheckGpsAvailable(mContext);
        if (!IsGpsEnable) {
            Generalfunction.GpsAlertMessage(mContext, PostActivity.this);
        }

        //check internet connection
        if (!cdObj.isConnectingToInternet()) {
            Generalfunction.Simple1ButtonDialog(mContext.getResources().getString(R.string.Internet_Message), mContext);
        }


        //It is useful when we post dish. it's give previous screen
        strPreviousScreen = GlobalVar.getMyStringPref(mContext, Constant.Post_PreviousScreen);
        GlobalVar.setMyStringPref(mContext, Constant.Post_PreviousScreen, "");

        /*
         * auto populate operation of
         * Restaurant,
         * Dish,
         * Dish price
         */

        //Restaurant
        acRestaurant.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.toString().length() == 2) {
                    acPrice.setText("$");
                    if (cdObj.isConnectingToInternet()) {
                        try {
                            new getSearchRestaurant().execute(s.toString().trim());
                        }catch(Exception e){}
                    }
                } else if (s.toString().length() > 2) {
                    if (addpterRestaurant != null) {
                        addpterRestaurant.getFilter().filter(s);
                    }
                }

                if (Generalfunction.isEmptyCheck(acRestaurant.getText().toString())) {
                    acDish.setText("");
                    acPrice.setText("");
                    strWebRestId = "";
                    strwebDishid = "";
                    strWebRestName = "";
                    acRestAddress.setText("");
                    etaddressNew.setText("");
                    acRestAddress.setVisibility(View.GONE);
                    etaddressNew.setVisibility(View.VISIBLE);
                    tvPlus.setVisibility(View.GONE);
                    tvPlus.setText("+");
                    etaddressNew.setHint(getResources().getString(R.string.hintAddress));
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });


        //Dish
        acDish.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.toString().length() == 2) {
                    if (cdObj.isConnectingToInternet()) {
                        if (!Generalfunction.isEmptyCheck(strSelectedRestId)) {
                            try {
                                new getSearchRestaurantDish().execute(s.toString().trim(), strSelectedRestId);
                            }catch(Exception e){}
                        }
                    }
                } else {
                    if (DishResultList.size() == 0 && s.toString().length() > 2) {
                        if (!Generalfunction.isCompare(s.toString().substring(0, 2), strSearchdish)) {
                            if (cdObj.isConnectingToInternet()) {
                                if (!Generalfunction.isEmptyCheck(strSelectedRestId)) {
                                    try {
                                        new getSearchRestaurantDish().execute(s.toString().trim(), strSelectedRestId);
                                    }
                                    catch(Exception e){}
                                }
                            }
                        }
                        strSearchdish = s.toString().substring(0, 2);
                    }
                }
                if (!Generalfunction.isCompare(acDish.getText().toString(), strSelectedDishname)) {
                    acPrice.setEnabled(true);
                    if (acDish.getText().toString().length() < 1) {
                        acPrice.setText("$");
                    }
                }
                if (addpterDish != null) {
                    addpterDish.getFilter().filter(s);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });


        //price
        acPrice.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.toString().length() < 1) {
                    acPrice.setText("$");
                } else {
                    if (s.toString().charAt(0) != '$') {
                        String str = acPrice.getText().toString().replace("$", "");
                        acPrice.setText("$" + str);
                        acPrice.setSelection(acPrice.getText().toString().length());
                    }
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });


        //Click on Done as a button and Perform action
        btnDone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(Constant.TAG, "POST: onClick: ");
                flagIsNextdish = false;
                PostDish_API();

            }
        });

        /*keyboard action of autocomplete for
              @Restaurant
              @Dish
              @Address
              @price
        */

        //Restaurant
        acRestaurant.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_NEXT) {
                    if (Generalfunction.isEmptyCheck(acRestAddress.getText().toString())) {
                        etaddressNew.requestFocus();
                    } else {
                        acDish.requestFocus();
                    }

                    return true;
                }
                return false;
            }
        });


        //Dish
        acDish.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_NEXT) {
                    acPrice.requestFocus();
                    acPrice.setSelection(acPrice.getText().length());
                    return true;
                }
                return false;
            }
        });


        //Restaurant address
        etaddressNew.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_NEXT) {
                    if (Generalfunction.isEmptyCheck(acDish.getText().toString())) {
                        acDish.requestFocus();
                    } else {
                        acPrice.requestFocus();
                    }
                    return true;
                }
                return false;
            }
        });


        //Dish price
        acPrice.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    btnDone.requestFocus();
                    Generalfunction.hideKeyboard(PostActivity.this);
                    btnDone.getParent().requestChildFocus(btnDone, btnDone);
                    btnDone.setFocusable(true);
                    btnDone.setFocusableInTouchMode(true);///add this line
                    return true;
                }
                return false;
            }
        });

        //price enable and disabled
        if (flagPrice) {
            acPrice.setFocusable(true);
        }


        /*Check screen like
           @Post
           @quick post
           @next dish
           and set functionality of view
           for that screen
        */

        btnDone.setVisibility(View.VISIBLE);
        llNextDish.setVisibility(View.VISIBLE);
        View vline = (View) findViewById(R.id.vline);
        vline.setVisibility(View.VISIBLE);

        Bundle bundle = getIntent().getExtras();

        //Quick post (plus icon)
        if (GlobalVar.getMyBooleanPref(mContext, Constant.Isquickpost)) {
           /* if (ActivityCompat.checkSelfPermission(mContext, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(PostActivity.this, new String[]{Manifest.permission.CAMERA}, Constant.PERMISSION_REQUEST_CODE);
            } else {
                openImageIntent();
            }*/

            btnDone.setVisibility(View.VISIBLE);
            btnDone.setText(getResources().getString(R.string.btn_quickpost_name));
            llNextDish.setVisibility(View.GONE);
            vline.setVisibility(View.GONE);
            flagQuickpost = true;

            if (bundle != null) {

                strSelectedRestId = bundle.getString(Constant.Quick_RestId);
                strselectedResturantname = bundle.getString(Constant.Quick_RestName);

                strSelectedDishname = bundle.getString(Constant.Quick_DishName);
                strselectedDishprice = bundle.getString(Constant.Quick_DishPrice);
                strSelectedDishid = bundle.getString(Constant.Quick_DishID);
                String strimage = bundle.getString(Constant.Quick_Dishimage);
                Generalfunction.DisplayImage_picasso(strimage, mContext, Constant.case2, postImage,Constant.Ph_dish);

                acRestaurant.setText(strselectedResturantname);
                acDish.setText(strSelectedDishname);
                acPrice.setText("$" + strselectedDishprice);

                acRestaurant.setEnabled(false);
                acDish.setEnabled(false);
                strquickRestid = strSelectedRestId;

                if (cdObj.isConnectingToInternet()) {
                    new getSearchRestaurant().execute(strselectedResturantname.toString().trim());
                }
            }

            GlobalVar.setMyBooleanPref(mContext, Constant.Isquickpost, false);
        }


        //Next dish
        else if (GlobalVar.getMyBooleanPref(mContext, Constant.IsPost_Nextdish)) {
            GlobalVar.setMyBooleanPref(mContext, Constant.IsPost_Nextdish, false);
            strSelectedRestId = GlobalVar.getMyStringPref(mContext, Constant.Quick_RestId);
            strselectedResturantname = GlobalVar.getMyStringPref(mContext, Constant.Quick_RestName);

            acRestaurant.setText(strselectedResturantname);
            if (!Generalfunction.isEmptyCheck(GlobalVar.getMyStringPref(mContext, Constant.Quick_RestAddress))) {
                acRestAddress.setText(GlobalVar.getMyStringPref(mContext, Constant.Quick_RestAddress));
            }

            if (ActivityCompat.checkSelfPermission(mContext, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(PostActivity.this, new String[]{Manifest.permission.CAMERA}, Constant.PERMISSION_REQUEST_CODE_Camera);
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
                GetSelectedPhotobitmap(strFilename, flagIscameragallery, flagIscamera, bundle.getBoolean("isCamerabutgallery"));
            }
        }


        //Click on Next item as a button and Perform action
        llNextDish.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                flagIsNextdish = true;
                PostDish_API();
            }
        });

        //Select Restaurant address
        acRestAddress.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (addresslist.size() > 0) {

                    AlertDialog.Builder builder = new AlertDialog.Builder(PostActivity.this);
                    builder.setTitle("where's it located? \n");

                    builder.setSingleChoiceItems(addresslist.toArray(new String[addresslist.size()]), -1, new DialogInterface.OnClickListener() {

                        @Override
                        public void onClick(DialogInterface arg0, int position) {
                            // TODO Auto-generated method stub
                            //acRestAddress.setText(addresslist.toArray(new String[addresslist.size()])[arg1]);
                            Log.d(Constant.TAG, "onClick: builder choice : arrylist size: " + array_listobject.size() + " selectedposition: " + selectedrestPos + " Position: " + position);
                            if (array_listobject.size() > 0) {
                                if (!flagQuickpost) {
                                    acDish.setText("");
                                }
                                strSelectedRestId = array_listobject.get(selectedrestPos).RestLocationlist.get(position).locationID;
                                strAddressId = array_listobject.get(selectedrestPos).RestLocationlist.get(position).locationID;
                                acRestAddress.setText(array_listobject.get(selectedrestPos).RestLocationlist.get(position).locationAddress);
                            }
                            arg0.dismiss();
                            Log.d(Constant.TAG, "onClick: builder choice");
                            if (!Generalfunction.isCompare(tvPlus.getText().toString(), "+")) {
                                tvPlus.setText("+");
                                etaddressNew.setVisibility(View.GONE);
                                Generalfunction.hideKeyboard(PostActivity.this);
                                etaddressNew.setText("");
                            }

                        }
                    });

                    AlertDialog dialog = builder.create();
                    dialog.show();
                }
            }
        });


        //write new restaurant address function
        tvPlus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                etaddressNew.setHint(getResources().getString(R.string.hintNewAddress));
                if (Generalfunction.isCompare(tvPlus.getText().toString(), "+")) {
                    tvPlus.setText("-");
                    etaddressNew.setVisibility(View.VISIBLE);
                } else {
                    tvPlus.setText("+");
                    etaddressNew.setVisibility(View.GONE);
                    Generalfunction.hideKeyboard(PostActivity.this);
                    etaddressNew.setText("");
                }
            }
        });


        //suppose Restaurant id a no available
        if (Generalfunction.isEmptyCheck(strSelectedRestId)) {
            acRestAddress.setVisibility(View.GONE);
            etaddressNew.setVisibility(View.VISIBLE);
            tvPlus.setVisibility(View.GONE);
        }

        etaddressNew.setAdapter(new GooglePlacesAutocompleteAdapter(this, android.R.layout.simple_list_item_1));
        etaddressNew.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
            }
        });

        // Gesture detection
        gestureDetector = new GestureDetector(this, new MyGestureDetector());
        gestureListener = new View.OnTouchListener() {
            public boolean onTouch(View v, MotionEvent event) {
                return gestureDetector.onTouchEvent(event);
            }
        };

        btnDone.setOnTouchListener(gestureListener);


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
        ContentResolver cR = mContext.getContentResolver();
        MimeTypeMap mime = MimeTypeMap.getSingleton();
        strImgContenttype = mime.getExtensionFromMimeType(cR.getType(imageUri));

        if (strImgContenttype == null) {
            String filePath = imageUri.getPath();
            strImgContenttype = filePath.substring(filePath.lastIndexOf(".") + 1);
        }
        Generalfunction.DisplayLog("onCreate: Imagetype " + strImgContenttype);

        //Get bitmap with Decode and center Rotation
        Bitmap bitmapDecode = Generalfunction.decodeFile(imageUri, mContext);
        Generalfunction.DisplayLog("onCreate: bitmapdecode " + bitmapDecode);

        // rotate bitmap when it is a not right
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
                ActivityCompat.requestPermissions(PostActivity.this, new String[]{Manifest.permission.CAMERA}, Constant.PERMISSION_REQUEST_CODE_Camera);  // Request missing location permission.
            } else {
                openImageIntent();
            }

        } else if (id == android.R.id.home) {
            finish();
        } else if (id == R.id.action_rotate) {
            if (webBitmap != null) {
                Bitmap bitmap = rotateImage(webBitmap);
                if (bitmap != null) {
                    webBitmap = bitmap;
                    postImage.setImageBitmap(webBitmap);
                }
            }
        }
        return super.onOptionsItemSelected(item);
    }


    //Validate Details and post dish or give a message
    private boolean Validation() {
        boolean isValidate = true;

        String strRestaurantName, strDishname, strPrice;
        strRestaurantName = acRestaurant.getText().toString();
        strDishname = acDish.getText().toString();
        strPrice = acPrice.getText().toString();

        if (Generalfunction.isCompare(strRestaurantName, strselectedResturantname)) {
            strWebRestId = strSelectedRestId;
            strWebRestName = strselectedResturantname;
        } else {
            strWebRestId = "";
            strWebRestName = "";
        }

        if (Generalfunction.isCompare(strDishname, strSelectedDishname)) {
            strwebDishname = strSelectedDishname;
            strwebDishid = strSelectedDishid;
        } else {
            strwebDishname = strDishname;
            strwebDishid = "";
        }

        if (strPrice.contains("$")) {
            strwebDishprice = strPrice.replace("$", "");
        } else {
            strwebDishprice = strPrice;
        }

        strwebDishprice = Generalfunction.Isnull(strwebDishprice);

        if (!Generalfunction.isEmptyCheck(strwebDishprice)) {
            if (Double.parseDouble(strwebDishprice) < 0.5) {
                strwebDishprice = "";
            }
        }

        if (Generalfunction.isEmptyCheck(strRestaurantName)) {
            isValidate = false;
            strValidateMessage = "please type restaurant name";
            acRestaurant.setFocusable(true);
            acRestaurant.requestFocus();
        } else if (Generalfunction.isEmptyCheck(strwebDishprice)) {
            isValidate = false;
            strValidateMessage = "price should be greater than 0";
            acPrice.setFocusable(true);
            acPrice.requestFocus();
        } else if (strRestaurantName.length() > 30) {
            isValidate = false;
            strValidateMessage = "please enter less than 30 characters for Restaurant name";
            acRestaurant.setFocusable(true);
            acRestaurant.requestFocus();

        } else if (Generalfunction.isEmptyCheck(strwebDishname)) {
            isValidate = false;
            strValidateMessage = "please type dish name";
            acDish.setFocusable(true);
            acDish.requestFocus();
        }
        else{
            //all validations are true

            if (webBitmap != null) {
                WebPhoto = Generalfunction.bitmapToBase64(webBitmap);    //convert bitmap to base 64 format
            }

            //address logic: if address is selected then address id become restaurant id
            if (!Generalfunction.isEmptyCheck(acRestAddress.getText().toString())) {
                if (!Generalfunction.isEmptyCheck(strAddressId)) {
                    strWebRestId = strAddressId;
                }
            }

            //user type new Address for restaurant
            strWebAddress = "";
            if (!Generalfunction.isEmptyCheck(etaddressNew.getText().toString().trim())) {
                strWebAddress = etaddressNew.getText().toString().trim();
                strWebPhonenumber = "";
                strWebRestId = "";
            }

            //Create new Restaurant: Restaurant is not choosed
            if (Generalfunction.isEmptyCheck(strWebRestId)) {
                if (Generalfunction.isEmptyCheck(strWebAddress)) {
                    strWebAddress = acRestAddress.getText().toString();
                }
                strWebRestName = acRestaurant.getText().toString();
            }

            if (!Generalfunction.isEmptyCheck(strquickRestid)) {
                if (!Generalfunction.isCompare(strquickRestid, strWebRestId)) {
                    strwebDishid = "";
                }
            }

            //Check decimal places
            if(strwebDishprice.contains(".")){
                int i = strwebDishprice.lastIndexOf('.');
                if(i != -1 && strwebDishprice.substring(i + 1).length() > 2) {
                    isValidate = false;
                    strValidateMessage = "Price should only allow two decimal places";
                    acPrice.setFocusable(true);
                    acPrice.requestFocus();
                }
            }
        }

        return isValidate;
    }


    //Call - Search Restaurant list API
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

            array_listobject = new ArrayList<>();
            addresslist = new ArrayList<String>();
            RestaurantResultList = new ArrayList();

            if (addpterRestaurant != null) {
                addpterRestaurant.notifyDataSetChanged();
            }

            if (rest.toLowerCase().contains("sorry")) {
                //Generalfunction.Simple1ButtonDialog(aVoid, getActivity());
            } else {
                try {
                    JSONObject posOBJ = new JSONObject(rest);
                    JSONArray ResturantsArray = posOBJ.getJSONArray("restaurants");

                    for (int j = 0; j < ResturantsArray.length(); j++) {

                        SearchResturantModel DishOBJ = new SearchResturantModel();
                        JSONObject object = ResturantsArray.getJSONObject(j);

                        DishOBJ.Restid = object.getString("id");
                        DishOBJ.strRestName = object.getString("name");

                        JSONArray locArry = object.getJSONArray("locations");
                        if (locArry != null) {
                            for (int l = 0; l < locArry.length(); l++) {
                                RestaurantLocationModel restLoc = new RestaurantLocationModel();
                                JSONObject objectLoc = locArry.getJSONObject(l);
                                restLoc.locationID = Generalfunction.Isnull(objectLoc.getString("id"));
                                restLoc.locationAddress = Generalfunction.Isnull(objectLoc.getString("address"));
                                DishOBJ.RestLocationlist.add(restLoc);
                            }
                        }

                        array_listobject.add(DishOBJ);
                        RestaurantResultList.add(DishOBJ.strRestName);


                    }
                } catch (JSONException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }

            }

            //Set restaurant auto complete and according to action set other deatil
            DefaultSetRestaurantDetailAddapter();

            //Quick post time address is set
            if (flagQuickpost) {
                if (array_listobject.size() > 0) {
                    selectedrestPos = 0;
                    // Log.d(Constant.TAG, "filter onPostExecute: "+array_listobject.get(0).RestLocationlist.size());
                    for (int j = 0; j < RestaurantResultList.size(); j++) {
                        for (int l = 0; l < array_listobject.get(j).RestLocationlist.size(); l++) {
                            if (Generalfunction.isCompare(acRestaurant.getText().toString(), array_listobject.get(j).strRestName)) {
                                addresslist.add(array_listobject.get(j).RestLocationlist.get(l).locationAddress);
                                if (Generalfunction.isCompare(strSelectedRestId, array_listobject.get(j).RestLocationlist.get(l).locationID)) {
                                    acRestAddress.setText(array_listobject.get(j).RestLocationlist.get(l).locationAddress);
                                    strAddressId = array_listobject.get(j).RestLocationlist.get(l).locationID;
                                }
                            }
                        }
                    }
                }
            }


        }
    }


    //Refresh Data
    private void RefreshDishData() {
        acDish.setText("");
        acPrice.setText("");
    }


    //Call - Search dish list according to restaurant API
    public class getSearchRestaurantDish extends AsyncTask<String, Void, String> {

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
            return WebFunctions.getRestaurantDishlist(params[0], token, params[1]);
        }

        @Override
        protected void onPostExecute(String rest) {
            super.onPostExecute(rest);

            DishResultList = new ArrayList<>();
            array_dishlistobject = new ArrayList<>();

            if (rest.toLowerCase().contains("sorry")) {
                //Generalfunction.Simple1ButtonDialog(aVoid, getActivity());
            } else {
                try {
                    JSONObject posOBJ = new JSONObject(rest);
                    JSONArray ResturantsArray = posOBJ.getJSONArray("dishes");

                    for (int j = 0; j < ResturantsArray.length(); j++) {

                        SearchDishModel DishOBJ = new SearchDishModel();
                        JSONObject object = ResturantsArray.getJSONObject(j);

                        DishOBJ.id = object.getString("id");
                        DishOBJ.strDishName = object.getString("name");
                        DishOBJ.strDishprice = object.getString("price");

                        array_dishlistobject.add(DishOBJ);
                        DishResultList.add(DishOBJ.strDishName);

                    }
                } catch (JSONException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }

            addpterDish = new DishNameAutocompleteAdapter(PostActivity.this, android.R.layout.simple_list_item_1, DishResultList);
            acDish.setAdapter(addpterDish);
            acDish.setThreshold(2);

            acDish.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                    String str = (String) parent.getItemAtPosition(position);

                    //Exact detail
                    performRestaurantDishFilter(str);

                    Generalfunction.hideKeyboard(PostActivity.this);
                    btnDone.requestFocus();
                    Generalfunction.hideKeyboard(PostActivity.this);

                    //Focus Done button
                    btnDone.getParent().requestChildFocus(btnDone, btnDone);
                    btnDone.setFocusable(true);
                    btnDone.setFocusableInTouchMode(true);///add this line

                }
            });

        }
    }


    private void openImageIntent() {
        if (ActivityCompat.checkSelfPermission(mContext, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(PostActivity.this, new String[]{Manifest.permission.CAMERA}, Constant.PERMISSION_REQUEST_CODE_Camera);
        }
        else if (ActivityCompat.checkSelfPermission(mContext, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(PostActivity.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, Constant.PERMISSION_REQUEST_CODE_STORAGE);
        }
        else {

            // Determine URI of camera image to save.
            fileUri = Generalfunction.getCameraUri();

            // Camera.
            final List<Intent> cameraIntents = new ArrayList<Intent>();
            final Intent captureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            captureIntent.putExtra(MediaStore.EXTRA_SCREEN_ORIENTATION, ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
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
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {

        if (requestCode == Constant.PERMISSION_REQUEST_CODE_Camera) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                openImageIntent();
            } else {
                Generalfunction.HandleApppermission(requestCode,permissions,grantResults,mContext);
            }
        }
        else {
            Generalfunction.HandleApppermission(requestCode,permissions,grantResults,mContext);
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

            }
        }
    }


   /*post dish using
         next item
             &
         Done button
     */

    //Call - Post dish API
    public class Post extends AsyncTask<String, Void, String> {

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
            //Log.d(Constant.TAG, "Post: doInBackground: Restid: "+strWebRestId+" webdishname:"+strwebDishname+" dishid: :"+strwebDishid);
            //return "";
            Log.d(Constant.TAG, "Post: doInBackground: " + strWebRestId);
            return WebFunctions.PostDish_Image(token, strWebRestId.trim(), strwebDishname.trim(), strwebDishprice.trim(), strwebDishid.trim(), strWebRestName.trim(), WebPhoto,
                    strWebAddress.trim(), strWebPhonenumber.trim());
        }

        @Override
        protected void onPostExecute(String aVoid) {
            super.onPostExecute(aVoid);

            hideProgressDialog();
            Generalfunction.RefreshvalueTrue(mContext);
            Log.d(Constant.TAG, "on post : post api - : " + aVoid);

            if (!aVoid.equals("")) {
                JSONObject jsonObject;

                String Message = "";
                try {
                    Log.d("rup", "onPostExecute: " + aVoid);
                    jsonObject = new JSONObject(aVoid);

                    if (jsonObject.getString("success").toLowerCase().equalsIgnoreCase("yes")) {
                        Message = jsonObject.getString("message");
                        Generalfunction.RefreshvalueTrue(mContext);
                        Log.d(Constant.TAG, "Post onPostExecute before: " + strWebRestId);
                        JSONObject objectdish = new JSONObject(aVoid).getJSONObject("dish");
                        strWebRestId = objectdish.getString("restaurant_id");
                        Log.d(Constant.TAG, "Post onPostExecute after: " + strWebRestId);
                        strWebRestName = objectdish.getString("restaurant_name");
                        try {
                            GlobalVar.setMyBooleanPref(mContext, Constant.IsPost_Dish, true);
                            Message = "This has been added to your profile, please head there when you’re ready to rate this item";
                            if (flagIsNextdish) {
                                GlobalVar.setMyBooleanPref(mContext, Constant.IsPost_Nextdish, true);
                                GlobalVar.setMyStringPref(mContext, Constant.Quick_RestId, strWebRestId);
                                GlobalVar.setMyStringPref(mContext, Constant.Quick_RestName, strWebRestName);

                                if (Generalfunction.isEmptyCheck(strWebAddress)) {
                                    strWebAddress = acRestAddress.getText().toString();
                                }

                                GlobalVar.setMyStringPref(mContext, Constant.Quick_RestAddress, strWebAddress);
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
                        Message = Message +"\n" +jsonObject.getString("errors");
                        Generalfunction.Simple1ButtonDialog(Message, mContext);
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

        alertDialogBuilder.setNegativeButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                try {
                    ((Activity) mContext).finish();
                    if (alertDialog != null) {
                        alertDialog.dismiss();
                    }


                    if (flagQuickpost) {

                        //quick post time -> when dish create/update successfully then it's go to Restaurant profile
                        if (!Generalfunction.isCompare(Constant.Screen_RP, strPreviousScreen)) {

                            //start - Restaurant profile
                            Intent intent = new Intent(PostActivity.this, RestaurantProfileActivity.class);
                            intent.putExtra(Constant.ID, strWebRestId);
                            startActivity(intent);
                        } else {

                            //This is happen while new restaurant create with new address
                            if (!Generalfunction.isCompare(strWebRestId, GlobalVar.getMyStringPref(mContext, Constant.Peppa_RestID))) {
                                Intent intent = new Intent(PostActivity.this, RestaurantProfileActivity.class);
                                intent.putExtra(Constant.ID, strWebRestId);
                                startActivity(intent);
                            }
                        }

                    } else {

                        //start - User profile
                        if (!Generalfunction.isCompare(Constant.Screen_UP, strPreviousScreen)) {
                            Log.d(Constant.TAG, "display user ");
                            Intent intent = new Intent(PostActivity.this, UserProfileActivity.class);
                            intent.putExtra(Constant.isMyprofile, true);
                            startActivity(intent);
                        }
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
        });

        alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }


    /*rotate image with 90 degree*/
    public static Bitmap rotateImage(Bitmap src) {
        Matrix matrix = new Matrix();  // create new matrix

        matrix.postRotate(90); // setup rotation degree
        Bitmap bmp = Bitmap.createBitmap(src, 0, 0, src.getWidth(), src.getHeight(), matrix, true);
        return bmp;
    }


    /*
    start Autocomplete Restaurat Name Action
    */

    private void DefaultSetRestaurantDetailAddapter() {

        addpterRestaurant = new RestaurantNameAutocompleteAdapter(this, android.R.layout.simple_list_item_1, RestaurantResultList);
        acRestaurant.setAdapter(addpterRestaurant);
        acRestaurant.setThreshold(2);

        acRestaurant.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                String str = (String) parent.getItemAtPosition(position);
                Log.d(Constant.TAG, "onItemClick selected : " + str + " Edittext value " + acRestaurant.getText().toString());
                if (Generalfunction.isEmptyCheck(str)) {
                    str = acRestaurant.getText().toString();
                }
                performRestaurantFilter(str);

                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(acRestaurant.getWindowToken(), InputMethodManager.RESULT_UNCHANGED_SHOWN);

            }
        });
    }

    //Restaurant: On item cick choose selected item detail
    private void performRestaurantFilter(String str) {

        for (int i = 0; i < array_listobject.size(); i++) {

            int position = i;

            if (array_listobject.get(i).strRestName.equalsIgnoreCase(str)) {
                selectedrestPos = position;
                strSelectedRestId = array_listobject.get(position).Restid;
                strselectedResturantname = array_listobject.get(position).strRestName;

                Log.d("select rest id", "onItemClick: " + strSelectedRestId + " rest name: " + strselectedResturantname + "Position: " + position);

                strAddressId = array_listobject.get(position).RestLocationlist.get(0).locationID;
                acDish.requestFocus();

                addresslist = new ArrayList<String>();
                for (int l = 0; l < array_listobject.get(position).RestLocationlist.size(); l++) {
                    addresslist.add(array_listobject.get(position).RestLocationlist.get(l).locationAddress);
                }

                RefreshDishData();

                if (addresslist.size() > 0) {
                    acRestAddress.setVisibility(View.VISIBLE);
                    etaddressNew.setVisibility(View.GONE);
                    tvPlus.setVisibility(View.VISIBLE);
                    etaddressNew.setText("");
                } else {
                    acRestAddress.setVisibility(View.GONE);
                    etaddressNew.setVisibility(View.VISIBLE);
                    tvPlus.setVisibility(View.GONE);
                }

                acRestaurant.setText(strselectedResturantname);
                acRestAddress.setText(array_listobject.get(position).RestLocationlist.get(0).locationAddress);

                break;
            }
        }

    }

    /*
    end Autocomplete Restaurat Name Action
    */



    /*
    Autocomplete Restaurat Dish Action
   */

    private void performRestaurantDishFilter(String str) {

        for (int i = 0; i < array_dishlistobject.size(); i++) {

            int position = i;

            if (array_dishlistobject.get(i).strDishName.equalsIgnoreCase(str)) {
                strselectedDishprice = array_dishlistobject.get(position).strDishprice;

                acPrice.setText("$" + Generalfunction.Isnull(strselectedDishprice));
                acDish.setText(array_dishlistobject.get(position).strDishName);
                strSelectedDishid = array_dishlistobject.get(position).id;
                strSelectedDishname = array_dishlistobject.get(position).strDishName;
            }
        }
    }


     /*
     end Autocomplete Googlemap location list
   */


    //Check validation and call - Post API
    private void PostDish_API() {

        Log.d(Constant.TAG, "POST: PostDish_API: ");
        showProgressDialog();
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


    //some phone take slow click event so it handle by gesture
    class MyGestureDetector extends GestureDetector.SimpleOnGestureListener {
        @Override
        public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
            return false;
        }

        @Override
        public boolean onDown(MotionEvent e) {
            flagIsNextdish = false;

            //Call - Post API
            PostDish_API();

            Log.d(Constant.TAG, "POST: onDown: working gesture");
            return true;
        }
    }


    //Autocomplete - Restaurant name
    class RestaurantNameAutocompleteAdapter extends ArrayAdapter implements Filterable {

        public RestaurantNameAutocompleteAdapter(Context context, int textViewResourceId, ArrayList Resultlist) {
            super(context, textViewResourceId);
        }

        @Override
        public int getCount() {
            return RestaurantResultList.size();
        }

        @Override
        public String getItem(int index) {
            if (RestaurantResultList.size() > 0 && index <= RestaurantResultList.size()) {
                return String.valueOf(RestaurantResultList.get(index));
            } else {
                return "";
            }
        }

        @Override
        public Filter getFilter() {
            Filter filter = new Filter() {
                @Override
                protected FilterResults performFiltering(CharSequence constraint) {
                    FilterResults filterResults = new FilterResults();
                    if (constraint != null) {

                        // Retrieve the autocomplete results.
                        RestaurantResultList.clear();
                        for (SearchResturantModel people : array_listobject) {
                            if (people.strRestName.toLowerCase().contains(constraint.toString().toLowerCase())) {
                                RestaurantResultList.add(people.strRestName);
                                //Log.d(Constant.TAG, "filter: publishResults: "+RestaurantResultList.size());
                            }
                        }
                        filterResults = new FilterResults();

                        // Assign the data to the FilterResults
                        filterResults.values = RestaurantResultList;
                        filterResults.count = RestaurantResultList.size();
                    }
                    return filterResults;
                }

                @Override
                protected void publishResults(CharSequence constraint, FilterResults results) {
                    //Log.d(Constant.TAG, "filter: publishResults: "+results);
                    if (results != null && results.count > 0) {
                        notifyDataSetChanged();
                    } else {
                        notifyDataSetInvalidated();
                    }
                }
            };
            return filter;
        }
    }


    //Autocomplete - Dish name
    class DishNameAutocompleteAdapter extends ArrayAdapter implements Filterable {

        public DishNameAutocompleteAdapter(Context context, int textViewResourceId, ArrayList Resultlist) {
            super(context, textViewResourceId);
        }

        @Override
        public int getCount() {
            return DishResultList.size();
        }

        @Override
        public String getItem(int index) {
            if (DishResultList.size() > 0 && index <= DishResultList.size()) {
                return String.valueOf(DishResultList.get(index));
            } else {
                return "";
            }
        }

        @Override
        public Filter getFilter() {
            Filter filter = new Filter() {
                @Override
                protected FilterResults performFiltering(CharSequence constraint) {
                    FilterResults filterResults = new FilterResults();
                    if (constraint != null) {
                        // Retrieve the autocomplete results.
                        DishResultList.clear();
                        for (SearchDishModel people : array_dishlistobject) {
                            if (people.strDishName.toLowerCase().contains(constraint.toString().toLowerCase())) {
                                DishResultList.add(people.strDishName);
                            }
                        }

                        filterResults = new FilterResults();

                        // Assign the data to the FilterResults
                        filterResults.values = DishResultList;
                        filterResults.count = DishResultList.size();
                    }
                    return filterResults;
                }

                @Override
                protected void publishResults(CharSequence constraint, FilterResults results) {
                    if (results != null && results.count > 0) {
                        notifyDataSetChanged();
                    } else {
                        notifyDataSetInvalidated();
                    }
                }
            };
            return filter;
        }
    }



}