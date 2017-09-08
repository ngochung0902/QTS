package com.foodapp.lien.foodapp;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.TextView;

import utility.ConnectionDetector;
import utility.Constant;
import utility.GPSTracker;
import utility.Generalfunction;
import utility.GlobalVar;
import utility.LocationAddress;


public class SearchFragment extends Fragment {

    private Context mContext;
    private ConnectionDetector cdObj;

    private TextView tvSuburb;

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

        mContext = this.getActivity();

        //inialize connection detector
        cdObj=new ConnectionDetector(mContext);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState) {

        //inflate view
        View view = inflater.inflate(R.layout.fragment_search, container, false);

        //Mapping textview
        TextView tvGreetingSearch = (TextView) view.findViewById(R.id.tvsearchGreetingText);
        TextView tvQuestionSearch = (TextView) view.findViewById(R.id.tvSearchquestion);
        TextView tvlocationSearch = (TextView) view.findViewById(R.id.tvSearchlocation);
        tvSuburb = (TextView) view.findViewById(R.id.tvSearchlocation);

        // set firstname with greeting
        String strGreeting=GlobalVar.getMyStringPref(mContext, Constant.loginUserfirstName)+ " "+GlobalVar.getMyStringPref(mContext, Constant.loginUserlastName);
        tvGreetingSearch.setText(strGreeting.toUpperCase());

        //set font
        Constant.loadFont(mContext, tvGreetingSearch, "playfair.otf");
        Constant.loadFont(mContext, tvQuestionSearch, "lato_medium.ttf");
        Constant.loadFont(mContext, tvlocationSearch, "lato_medium.ttf");

        //Mapping edittext
        final EditText etsearchView = (EditText) view.findViewById(R.id.etSearchitem);

        etsearchView.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    if (!etsearchView.getText().toString().equals("")) {

                        // Check internet connection and perform operation
                        if (cdObj.isConnectingToInternet()) {
                            Intent intent = new Intent(getActivity(), ResultActivity.class);
                            intent.putExtra(Constant.Search, etsearchView.getText().toString());
                            startActivity(intent);
                        }
                        else{
                            Generalfunction.Simple1ButtonDialog(mContext.getResources().getString(R.string.Internet_Message), mContext);
                        }
                        return true;
                    }
                }
                return false;
            }
        });

        // search location
        tvSuburb.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showPopUp();
            }
        });

        //Current Location
        if (ActivityCompat.checkSelfPermission(mContext, Manifest.permission.ACCESS_COARSE_LOCATION)
                != PackageManager.PERMISSION_GRANTED)  {
            ActivityCompat.requestPermissions((Activity) mContext,
                    new String[]{Manifest.permission.ACCESS_COARSE_LOCATION},
                    Constant.PERMISSION_REQUEST_CODE_Location);
        } else {
            GPSTracker gpsTracker = new GPSTracker(mContext);

            if (gpsTracker.getIsGPSTrackingEnabled()) {
                double latitude = gpsTracker.getLatitude();
                double longitude = gpsTracker.getLongitude();
                LocationAddress locationAddress = new LocationAddress();
                locationAddress.getAddressFromLocation(latitude, longitude,
                        getActivity(), new GeocoderHandler());
            }

        }

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

    // search resturant using suburb popup (Dilaog)
    public void showPopUp() {

        // inflate layout
        LayoutInflater li = LayoutInflater.from(getActivity());
        View promptsView = li.inflate(R.layout.auto_complete_sv, null);

        //Mapping alertdialog
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getActivity());
        alertDialogBuilder.setTitle(mContext.getResources().getString(R.string.locationpopuptital));
        alertDialogBuilder.setView(promptsView);

        //Mapping autocomplete text
        AutoCompleteTextView myAutoComplete = (AutoCompleteTextView) promptsView.findViewById(R.id.suburb_name);

        String[] name_mov = {"Sydney","Melbourne", "Brisbane", "BrPerth", "BrAdelade", "Brlue Mountain"};

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity(),android.R.layout.simple_dropdown_item_1line, name_mov);
        myAutoComplete.setAdapter(adapter);
       // myAutoComplete.setThreshold(2);

        // show it
        alertDialogBuilder.show();
    }

}
