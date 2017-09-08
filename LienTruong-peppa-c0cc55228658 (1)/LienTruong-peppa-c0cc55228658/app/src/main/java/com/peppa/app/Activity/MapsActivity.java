package com.peppa.app.Activity;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Base64;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;



import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.LatLngBounds;
import com.peppa.app.R;
import com.peppa.app.model.DishModel;
import com.peppa.app.model.MapModel;
import com.peppa.app.utility.Constant;
import com.peppa.app.utility.Generalfunction;
import com.peppa.app.utility.GlobalVar;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;



@TargetApi(21)
public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private Context thisContext;

    private GoogleMap mMap;
    private LinearLayoutManager mLayoutManager;

    private RecyclerView mRecyclerView;
    private ImageView ivClose;

    private ArrayList<DishModel> dishes = new ArrayList<DishModel>();
    private ArrayList<DishModel> dishes_search = new ArrayList<DishModel>();
    private ArrayList<MapModel> maps = new ArrayList<MapModel>();
    private List<String> list = new ArrayList<String>();
    List<Marker> markersList = new ArrayList<Marker>();

    private CameraUpdate cu;
    Marker previousMarker = null;

    private boolean Ismorethan1restaurants = false;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        thisContext = this;

        //Mapping - Textview
        TextView tvMapTital   = (TextView) findViewById(R.id.mapTV);
        mRecyclerView       = (RecyclerView) findViewById(R.id.my_recycler_view);
        ivClose             = (ImageView) findViewById(R.id.ivclose);

        //Mapping - Edittext
        EditText etRedosearch = (EditText) findViewById(R.id.etredosearch);

        //Set - Font Typeface
        Typeface latomedium = Typeface.createFromAsset(getAssets(), "fonts/lato_medium.ttf");
        tvMapTital.setTypeface(latomedium);

        //Set - Map Tital
        String strLoc = GlobalVar.getMyStringPref(thisContext, Constant.Location_Search);
        if (!Generalfunction.isEmptyCheck(GlobalVar.getMyStringPref(thisContext, Constant.Location_Mapview))) {
            strLoc = GlobalVar.getMyStringPref(thisContext, Constant.Location_Mapview);
        }
        tvMapTital.setText("" + strLoc);

        //Get - Dishes
        dishes = (ArrayList<DishModel>) getIntent().getExtras().getSerializable("dishes");

        Generalfunction.DisplayLog("onCreate: Dish size is: "+dishes.size());

        try {
            for (int i = 0; i < dishes.size(); i++) {
                if(dishes.get(i)==null){
                    dishes.remove(i);
                    continue;
                }
                Generalfunction.DisplayLog("onCreate: dishes number"+i+" restID: "+dishes.get(i).rest_id);
                list.add(""+dishes.get(i).rest_id);
                Log.e("count",""+dishes.get(i).rest_id);
            }
        }
        catch(Exception e){
            Generalfunction.DisplayLog("onCreate: exception: "+e.toString());
        }

        String strRestaurantID = "";

        for (int i = 0; i < dishes.size(); i++) {
            MapModel map = new MapModel();
            map.restaturant_id = dishes.get(i).rest_id;
            map.longitude      = dishes.get(i).rest_longitude;
            map.latitude       = dishes.get(i).rest_latitude;
            map.count          = Collections.frequency(list,dishes.get(i).rest_id);
            map.rest_name      = dishes.get(i).rest_name;
            maps.add(map);
Log.e("latlon",""+dishes.get(i).rest_latitude+dishes.get(i).rest_longitude);
            Generalfunction.DisplayLog("MapActivity more than one restaurant: "+Ismorethan1restaurants+" restaurant id: "+strRestaurantID+ " latitude: "+dishes.get(i).rest_latitude);

            if(!Generalfunction.isEmptyCheck(strRestaurantID)) {
                if (!Generalfunction.isCompare(strRestaurantID, dishes.get(i).rest_id)) {
                    Ismorethan1restaurants = true;
                }
            }
            strRestaurantID = dishes.get(i).rest_id;
            Generalfunction.DisplayLog("MapActivity : Restaurant id: "+strRestaurantID);
        }

        if (getIntent().getExtras() != null && getIntent().getExtras().getBoolean("isGPS")) {
            mRecyclerView.setVisibility(View.GONE);
        }

        mRecyclerView.setHasFixedSize(true);
        mLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLayoutManager);

        MyAdapter mAdapter = new MyAdapter(thisContext, dishes);
        mRecyclerView.setAdapter(mAdapter);

        //Get- SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        ivClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        etRedosearch.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    Generalfunction.hideKeyboard(MapsActivity.this);
                    return true;
                }
                return false;
            }
        });

        etRedosearch.setVisibility(View.GONE);

    }




    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        markersList = new ArrayList<>();

        View CustomMarkericon = ((LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(R.layout.custom_marker_layout, null);
        TextView tvMarkerNum = (TextView) CustomMarkericon.findViewById(R.id.num_txt);
        LatLng sydney = null;

        //Add - Custom Marker icon with Daynamic text and Move Camera
        try {
            for (int i = 0; i < maps.size(); i++) {

                String latitude=Generalfunction.Isnull(maps.get(i).latitude);

                if(!Generalfunction.isEmptyCheck(latitude)) {
                    sydney = new LatLng(Double.parseDouble(maps.get(i).latitude), Double.parseDouble(maps.get(i).longitude));


                    tvMarkerNum.setText("" + maps.get(i).count);

                    Marker marker = mMap.addMarker(new MarkerOptions()
                            .position(sydney)
                            .title(maps.get(i).rest_name + "")
                            .snippet(""+maps.get(i).restaturant_id)
                            .icon(BitmapDescriptorFactory.fromBitmap(Generalfunction.createDrawableFromView(this, CustomMarkericon)))
                    );

                    markersList.add(marker);
                }
            }

            //Bound - Marker and zoom
            LatLngBounds.Builder builder = new LatLngBounds.Builder();
            for (Marker marker : markersList) {
                builder.include(marker.getPosition());
            }

            int MapPadding = 100;                         /* initialize the padding for map boundary */

            LatLngBounds bounds = builder.build();        /* create the bounds from latlngBuilder to set into map camera*/

            if (dishes.size() < 3) {
                MapPadding = 50;
                cu = CameraUpdateFactory.newLatLngBounds(bounds, 0);
            } else {
                cu = CameraUpdateFactory.newLatLngBounds(bounds, 15);
            }

            mMap.setPadding(MapPadding, MapPadding, MapPadding, MapPadding);

            final LatLng finalSydney = sydney;
            mMap.setOnMapLoadedCallback(new GoogleMap.OnMapLoadedCallback() {
                @Override
                public void onMapLoaded() {

                    if (Ismorethan1restaurants) {
                        mMap.animateCamera(cu);
                        Generalfunction.DisplayLog("MapActivity: onMapLoaded: more than status");
                    } else {
                        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(finalSydney, 17));
                        Generalfunction.DisplayLog("MapActivity: onMapLoaded: more than status else");
                    }
                }
            });


            //Set - Marker listener
            mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
                @Override
                public boolean onMarkerClick(Marker marker) {

                    if (previousMarker != null) {

                        for (int j = 0; j < dishes.size(); j++) {

                            Generalfunction.DisplayLog("MapActivity : MarkerClick: " + previousMarker.getSnippet() + " Restaurant id: " + dishes.get(j).rest_id);

                            if (Generalfunction.isCompare(previousMarker.getSnippet(), dishes.get(j).rest_id)) {
                                View CustomMarkericon = ((LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(R.layout.custom_marker_layout, null);
                                TextView tvMarkerNum = (TextView) CustomMarkericon.findViewById(R.id.num_txt);
                                ImageView ivMarker = (ImageView) CustomMarkericon.findViewById(R.id.ivmarker);
                                ImageView ivMarker_o = (ImageView) CustomMarkericon.findViewById(R.id.ivmarker_o);

                                ivMarker_o.setVisibility(View.GONE);
                                ivMarker.setVisibility(View.VISIBLE);

                                tvMarkerNum.setText("" + maps.get(j).count);

                                Generalfunction.DisplayLog("MapActivity: onMarkerClick: previoue:");

                                //Upadet marker icon
                                previousMarker.setIcon(BitmapDescriptorFactory.fromBitmap(Generalfunction.createDrawableFromView(thisContext, CustomMarkericon)));
                            }
                        }

                    }

                    for (int j = 0; j < dishes.size(); j++) {

                        if (Generalfunction.isCompare(marker.getSnippet(), dishes.get(j).rest_id)) {

                            Generalfunction.DisplayLog("MapActivity: onMarkerClick: Tital is " + marker.getSnippet());
                            Generalfunction.DisplayLog("MapActivity: onMarkerClick: " + dishes.get(j).rest_name);

                            View CustomMarkericon = ((LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(R.layout.custom_marker_layout, null);
                            TextView tvMarkerNum = (TextView) CustomMarkericon.findViewById(R.id.num_txt);
                            ImageView ivMarker = (ImageView) CustomMarkericon.findViewById(R.id.ivmarker);
                            ImageView ivMarker_o = (ImageView) CustomMarkericon.findViewById(R.id.ivmarker_o);

                            ivMarker_o.setVisibility(View.VISIBLE);
                            ivMarker.setVisibility(View.GONE);

                            tvMarkerNum.setText("" + maps.get(j).count);

                            //Upadet - Custom Marker Icon
                            marker.setIcon(BitmapDescriptorFactory.fromBitmap(Generalfunction.createDrawableFromView(thisContext, CustomMarkericon)));
                            previousMarker = marker;

                            Generalfunction.DisplayLog("MapActivity :onMarkerClick: return true");
                            SerachAndFilterPeoplelist(dishes.get(j).rest_id);

                            marker.showInfoWindow();

                            return true;
                        }
                    }

                    return false;
                }
            });

            //Show Marker Information window
            DisplayInfowindow();

        } catch (Exception e) {
            Generalfunction.DisplayLog("MapActivity: onMarkerClick exception : " + e);
        }

    }


    //Show - Listview According to Marker Click
    public void SerachAndFilterPeoplelist(String strSearch) {

        strSearch = strSearch.toLowerCase();
        dishes_search = new ArrayList<>();

        //selected marker restaurant dishes
        for (DishModel d : dishes) {
            if (d.rest_id != null && d.rest_id.toLowerCase().equalsIgnoreCase(strSearch)) {
                d.MarkerclickDish=true;
                dishes_search.add(d);
            }
        }

        //Other restaurant dishes (without marker restaurant didhes)
        for (DishModel d1 : dishes) {
            if (!(d1.rest_id != null && d1.rest_id.toLowerCase().equalsIgnoreCase(strSearch))) {
                d1.MarkerclickDish=false;
                dishes_search.add(d1);
            }
        }

        //Display list with search word
        MyAdapter mAdapter = new MyAdapter(thisContext, dishes_search);
        mRecyclerView.setAdapter(mAdapter);
    }


    public class MyAdapter extends RecyclerView.Adapter<MyAdapter.ViewHolder> {

        private ArrayList<DishModel> mDataset;
        private Context context;

        public class ViewHolder extends RecyclerView.ViewHolder {
            TextView ratingText;
            TextView tvSubtitle;
            TextView tvTiltle;
            LinearLayout llRow;
            ImageView ivDish;

            public ViewHolder(View itemView) {
                super(itemView);
                ratingText = (TextView) itemView.findViewById(R.id.dishRatingMapTV);
                tvSubtitle = (TextView) itemView.findViewById(R.id.tvsubtitle);
                tvTiltle = (TextView) itemView.findViewById(R.id.tvtitle);
                llRow = (LinearLayout) itemView.findViewById(R.id.ll_row);
                ivDish = (ImageView) itemView.findViewById(R.id.dishMapImage);
            }
        }

        @Override
        public MyAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.map_row, parent, false);
            ViewHolder vh = new ViewHolder(v);
            return vh;
        }

        public MyAdapter(Context context, ArrayList<DishModel> restaurants) {
            mDataset = restaurants;
            this.context = context;
        }

        // Replace the contents of a view (invoked by the layout manager)
        @Override
        public void onBindViewHolder(ViewHolder holder, final int position) {
            // - get element from your dataset at this position
            // - replace the contents of the view with that element
            String strrate = mDataset.get(position).average_rating;
            if (!(strrate == null || Generalfunction.isEmptyCheck(strrate))) {
                if (strrate.length() < 3) {
                    if (strrate.length() < 2 && strrate.length() > 0) {
                        strrate = " " + strrate;
                    } else {
                        strrate = "  " + strrate;
                    }
                }
            }

            Generalfunction.DisplayLog("onBindViewHolder: " + strrate);

            holder.ratingText.setText(strrate);
            holder.tvTiltle.setText(mDataset.get(position).name);
            holder.tvSubtitle.setText(mDataset.get(position).rest_name);

            Generalfunction.DisplayImage_picasso(mDataset.get(position).image, thisContext,Constant.case1, holder.ivDish,Constant.Ph_userprofilepic);

            Typeface lato = Typeface.createFromAsset(getAssets(), "fonts/lato.ttf");
            Typeface latob = Typeface.createFromAsset(getAssets(), "fonts/lato_bold.ttf");

            holder.ratingText.setTypeface(latob);
            holder.tvSubtitle.setTypeface(lato);
            holder.tvTiltle.setTypeface(lato);

            holder.llRow.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    Intent intent = new Intent(thisContext, DetailActivity.class);
                    intent.putExtra(Constant.ID, mDataset.get(position).id);
                    intent.putExtra(Constant.isMyprofile, "maps");
                    //we pass dish id
                    startActivity(intent);

                }
            });

            if(mDataset.get(position).MarkerclickDish){
                holder.tvSubtitle.setTextColor(context.getResources().getColor(R.color.colorPrimary));
            }
            else{
                holder.tvSubtitle.setTextColor(context.getResources().getColor(R.color.colorMarkergrey));
            }

        }

        // Return the size of your dataset (invoked by the layout manager)
        @Override
        public int getItemCount() {
            return mDataset.size();
        }

    }



    //Show - Marker click time open small info window
    private void DisplayInfowindow(){

        mMap.setInfoWindowAdapter(new GoogleMap.InfoWindowAdapter() {

            // Use default InfoWindow frame
            @Override
            public View getInfoWindow(Marker marker)   {
                return null;
            }

            // Defines the contents of the InfoWindow
            @Override
            public View getInfoContents(Marker marker) {

                // Getting view from the layout file info_window_layout
                View v = getLayoutInflater().inflate(R.layout.list_item, null);

                String str=marker.getTitle();
                TextView tvLat = (TextView)  v.findViewById(R.id.product_name);
                tvLat.setText(str);
                tvLat.setTextColor(Color.BLACK);

                return v;
            }

        });
    }


}