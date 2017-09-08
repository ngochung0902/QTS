package com.peppa.app.Fragment;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import com.peppa.app.Activity.ResultActivity;
import com.peppa.app.R;
import com.peppa.app.parsing.WebFunctions;
import com.peppa.app.utility.ClearableAutocomplete;
import com.peppa.app.utility.ClearableEdittext;
import com.peppa.app.utility.ConnectionDetector;
import com.peppa.app.utility.Constant;
import com.peppa.app.utility.GPSTracker;
import com.peppa.app.utility.Generalfunction;
import com.peppa.app.utility.GlobalVar;
import com.peppa.app.utility.LocationAddress;


public class SearchFragment extends Fragment {

    private Context thisContext;
    private ConnectionDetector cdObj;

    private TextView tvSuburb;
    private RelativeLayout rl_Serchlocation;
    private ClearableAutocomplete myAutoComplete;
    private ClearableEdittext etsearchView;
    private LinearLayout search;

    private ListView lv;
    private ArrayAdapter<String> adapter;  // Listview Adapter

    private ArrayList<String> array_listLocation = new ArrayList<>();

    private AlertDialog AlertdialogLocation;
    private String strSearchloc="";
    private Button allres;
    private FrameLayout frame;


    public SearchFragment() {
        // Required empty public constructor
    }

    public static SearchFragment newInstance(String param1, String param2) {
        SearchFragment fragment = new SearchFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        thisContext = this.getActivity();

        //inialize connection detector
        cdObj = new ConnectionDetector(thisContext);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        //inflate view
        View view = inflater.inflate(R.layout.fragment_search, container, false);

        //toolbar
        Toolbar toolbar = (Toolbar) getActivity().findViewById(R.id.toolbar);
        toolbar.setTitle(getResources().getString(R.string.empty));
        toolbar.setBackgroundColor(getResources().getColor(android.R.color.transparent));
      //  toolbar.setLogo(getResources().getDrawable(R.mipmap.back));

        //Mapping textview
        TextView tvGreetingSearch = (TextView) view.findViewById(R.id.tvsearchGreetingText);
        TextView tvQuestionSearch = (TextView) view.findViewById(R.id.tvSearchquestion);
        TextView tvlocationSearch = (TextView) view.findViewById(R.id.tvSearchlocation);
        tvSuburb                  = (TextView) view.findViewById(R.id.tvSearchlocation);

        //Mapping Relative layout
        rl_Serchlocation = (RelativeLayout) view.findViewById(R.id.rl_serchlocation);
        search=(LinearLayout)view. findViewById(R.id.search);
        allres=(Button)view.findViewById(R.id.allres);
        frame=(FrameLayout)view.findViewById(R.id.frame);

        //set First name with greeting
        String strfirstname = GlobalVar.getMyStringPref(thisContext, Constant.loginUserfirstName);
        String strGreeting = "Sup";

        if (!Generalfunction.isEmptyCheck(strfirstname)) {
            strGreeting = strGreeting + ", "
                    + GlobalVar.getMyStringPref(thisContext, Constant.loginUserfirstName).substring(0, 1).toUpperCase()
                    + GlobalVar.getMyStringPref(thisContext, Constant.loginUserfirstName).substring(1).toLowerCase();
        }
        tvGreetingSearch.setText(strGreeting);

        //set Font
        Generalfunction.loadFont(thisContext, tvGreetingSearch, "playfair.otf");
        Generalfunction.loadFont(thisContext, tvQuestionSearch, "lato_medium.ttf");
        Generalfunction.loadFont(thisContext, tvlocationSearch, "lato_medium.ttf");

        //Mapping edittext
        etsearchView = (ClearableEdittext) view.findViewById(R.id.etSearchitem);

        search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (cdObj.isConnectingToInternet()) {

                    GlobalVar.setMyStringPref(thisContext, Constant.Location_Search,"");

                    //Start - Result Activity
                    Intent intent = new Intent(getActivity(), ResultActivity.class);
                    intent.putExtra(Constant.Search, etsearchView.getText().toString().trim());
                    intent.putExtra(Constant.SerchLocation, tvSuburb.getText().toString());
                    startActivity(intent);

                } else {
                    Generalfunction.Simple1ButtonDialog(thisContext.getResources().getString(R.string.Internet_Message), thisContext);
                }

            }
        });

        etsearchView.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    HideKeyboard(true);

                  //  Generalfunction.hideKeyboard(getActivity());

                    // Check internet connection and perform operation
//                    if (cdObj.isConnectingToInternet()) {
//
//                        GlobalVar.setMyStringPref(thisContext, Constant.Location_Search,"");
//
//                        //Start - Result Activity
//                        Intent intent = new Intent(getActivity(), ResultActivity.class);
//                        intent.putExtra(Constant.Search, etsearchView.getText().toString().trim());
//                        intent.putExtra(Constant.SerchLocation, tvSuburb.getText().toString());
//                        startActivity(intent);
                        return true;
//                    } else {
//                        Generalfunction.Simple1ButtonDialog(thisContext.getResources().getString(R.string.Internet_Message), thisContext);
//                    }
//
//                    //Hide keyboard
//                    InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
//                    imm.hideSoftInputFromWindow(etsearchView.getWindowToken(), InputMethodManager.RESULT_UNCHANGED_SHOWN);
                }
                return false;
            }
        });

        //search location (pop up)
        rl_Serchlocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                showPopUp();
            }
        });

        //current location
        UpdateCurrentLocation();
       frame.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
if(getActivity().getCurrentFocus()!=null) {
    imm.hideSoftInputFromWindow(getActivity().getCurrentFocus().getWindowToken(), 0);
    return true;
}
                else{
    return true;
                }
            }
        });

        allres.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (cdObj.isConnectingToInternet()) {

                    GlobalVar.setMyStringPref(thisContext, Constant.Location_Search,"");

                    //Start - Result Activity
                    Intent intent = new Intent(getActivity(), ResultActivity.class);
                //intent.putExtra(Constant.Search, etsearchView.getText().toString().trim());
                    intent.putExtra(Constant.SerchLocation, tvSuburb.getText().toString());
                    startActivity(intent);

                } else {
                    Generalfunction.Simple1ButtonDialog(thisContext.getResources().getString(R.string.Internet_Message), thisContext);
                }

            }
        });

        return view;
    }


    private class GeocoderHandler extends Handler {
        @Override
        public void handleMessage(Message message) {
            String locationAddress;
            switch (message.what) {
                case 1:
                    Bundle bundle = message.getData();
                    locationAddress = bundle.getString("address");
                    break;
                default:
                    locationAddress = null;
            }
            tvSuburb.setText(locationAddress);
        }
    }


    //Show - Location Change small window
    public void showPopUp() {

        HideKeyboard(true);

        // inflate layout
        LayoutInflater li = LayoutInflater.from(thisContext);
        View promptsView = li.inflate(R.layout.auto_complete_sv, null);

        //Mapping alertdialog
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getActivity());
        alertDialogBuilder.setTitle(thisContext.getResources().getString(R.string.locationpopuptital));
        alertDialogBuilder.setView(promptsView);

        //Mapping autocomplete text
        myAutoComplete = (ClearableAutocomplete) promptsView.findViewById(R.id.suburb_name);
        lv = (ListView) promptsView.findViewById(R.id.list_view);


        myAutoComplete.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.toString().length() == 2) {
                    Log.d("post user", "onTextChanged: ");
                    if (cdObj.isConnectingToInternet()) {

                        new getSearchLocation().execute(s.toString().trim());
                    }
                }
                else {

                    lv.setVisibility(View.VISIBLE);
                    if (array_listLocation.size() > 0) {
                        adapter.getFilter().filter(s);
                    }

                    if(array_listLocation.size()==0 && s.toString().length()>2){
                        if(!Generalfunction.isCompare(s.toString().substring(0,2),strSearchloc)){
                            if (cdObj.isConnectingToInternet()) {
                                new getSearchLocation().execute(s.toString().trim());
                            }
                        }
                        strSearchloc=s.toString().substring(0,2);
                    }
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        myAutoComplete.setText(tvSuburb.getText().toString());
        myAutoComplete.setSelection(myAutoComplete.getText().length());




        myAutoComplete.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE) {

                    //Hide keyboard
                    HideKeyboard(false);


                    if(!Generalfunction.isEmptyCheck(myAutoComplete.getText().toString())){


                        tvSuburb.setText(myAutoComplete.getText().toString());
                    }

                    AlertdialogLocation.dismiss();
                    return true;
                }
                return false;
            }
        });

        alertDialogBuilder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                //Hide keyboard
                InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(myAutoComplete.getWindowToken(), InputMethodManager.RESULT_UNCHANGED_SHOWN);

                if(!Generalfunction.isEmptyCheck(myAutoComplete.getText().toString())) {
                    Boolean contains=array_listLocation.contains(myAutoComplete.getText().toString());
                    if(contains==true){
                        Log.e("true",""+contains);
                        tvSuburb.setText(myAutoComplete.getText().toString());
                    }
                    else{

                        tvSuburb.setText("Select from list");
                    }

                }

                AlertdialogLocation.dismiss();

            }
        });


        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                myAutoComplete.setText("" + parent.getAdapter().getItem(position));
                lv.setVisibility(View.GONE);
                myAutoComplete.setSelection(myAutoComplete.getText().length());

            }
        });

        // show it
        AlertdialogLocation = alertDialogBuilder.create();
        AlertdialogLocation.show();

        //Alert dialog Window Resize depend on keyboard visible and hide
        AlertdialogLocation.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);

        myAutoComplete.requestFocus();
        InputMethodManager imm = (InputMethodManager)thisContext.getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0);

    }



    //Call - Search Location API
    public class getSearchLocation extends AsyncTask<String, Void, String> {

        //ProgressDialog progress;
        String token;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            SharedPreferences prefs = thisContext.getSharedPreferences("MY_PREFS", thisContext.MODE_PRIVATE);
            token = prefs.getString("token", "");
            Log.d("token", token);

        }

        @Override
        protected String doInBackground(String... params) {
            return WebFunctions.getLocationlist(params[0], token);
        }

        @Override
        protected void onPostExecute(String rest) {
            super.onPostExecute(rest);

            Generalfunction.DisplayLog(rest);

            if (rest.toLowerCase().contains("sorry")) {
            } else {
                try {
                    array_listLocation = new ArrayList<>();
                    JSONArray jsonArry = new JSONArray(rest);
                    if (jsonArry != null) {

                        for (int i = 0; i < jsonArry.length(); i++) {

                            JSONObject jobject = jsonArry.getJSONObject(i);
                            String strName = jobject.getString("name");
                            array_listLocation.add(strName);

                        }

                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            if (array_listLocation.size() > 0) {
                // Adding items to listview
                adapter = new ArrayAdapter<String>(getActivity(), R.layout.list_item, R.id.product_name, array_listLocation);
                lv.setAdapter(adapter);
            }

        }
    }


    //Update - update current location
    public void UpdateCurrentLocation(){
        thisContext=getActivity();
        String strCurrentLatitude, strCurrentLongitude;
        strCurrentLatitude = GlobalVar.getMyStringPref(thisContext, Constant.CurrentLatitude);
        strCurrentLongitude = GlobalVar.getMyStringPref(thisContext, Constant.CurrentLongitude);

        if (strCurrentLatitude == null || Generalfunction.isEmptyCheck(strCurrentLatitude)) {
            GPSTracker gpsTracker = new GPSTracker(thisContext);

            if (gpsTracker.getIsGPSTrackingEnabled()) {
                strCurrentLatitude = String.valueOf(gpsTracker.getLatitude());
                strCurrentLongitude = String.valueOf(gpsTracker.getLongitude());
            }
        }
        Generalfunction.SetsearchLocationParameter(thisContext, strCurrentLatitude, strCurrentLongitude);
        try {
            LocationAddress locationAddress = new LocationAddress();
            // tvCurrentLoc.setText("Current latitude: "+strCurrentLatitude+"\nCurrent longitude: "+strCurrentLongitude);
            locationAddress.getAddressFromLocation(Double.parseDouble(strCurrentLatitude), Double.parseDouble(strCurrentLongitude),
                    thisContext, new GeocoderHandler());

        } catch (Exception e) {
            tvSuburb.setText(thisContext.getResources().getString(R.string.location));
            e.printStackTrace();
        }
    }


    @Override
    public void onStart() {
        super.onStart();
        Generalfunction.Refreshvalue_Filter(thisContext);
        GlobalVar.setMyStringPref(thisContext, Constant.Search_location_with_search, "");
    }



    //Hide - hide keyboard
    private void HideKeyboard(boolean flag){

        /*true for edittext
          false for autocomplete relative layout location*/
        InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        if(flag)
          imm.hideSoftInputFromWindow(etsearchView.getWindowToken(), InputMethodManager.RESULT_UNCHANGED_SHOWN);
        else
          imm.hideSoftInputFromWindow(myAutoComplete.getWindowToken(), InputMethodManager.RESULT_UNCHANGED_SHOWN);
    }

}
