package com.foodapp.lien.foodapp;

import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import model.DishModel;
import model.MapModel;

@TargetApi(21)
public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private LinearLayoutManager mLayoutManager;

    private Context thisContext;

    private ArrayList<DishModel> dishes = new ArrayList<DishModel>();
    private ArrayList<MapModel> maps = new ArrayList<MapModel>();
    private List<String> list = new ArrayList<String>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.

        thisContext = this;
        TextView location = (TextView) findViewById(R.id.mapTV);
        Typeface latomedium = Typeface.createFromAsset(getAssets(), "fonts/lato_medium.ttf");
        location.setTypeface(latomedium);
        RecyclerView mRecyclerView = (RecyclerView) findViewById(R.id.my_recycler_view);

        dishes = (ArrayList<DishModel>)getIntent().getExtras().getSerializable("dishes");

        for (int i = 0; i < dishes.size(); i++) {
            list.add(dishes.get(i).rest_id);
        }

        for (int i = 0; i < dishes.size(); i++) {
            MapModel map = new MapModel();
            map.longitude = dishes.get(i).rest_longitude;
            map.latitude = dishes.get(i).rest_latitude;
            map.restaturant_id = dishes.get(i).rest_id;
            map.count = Collections.frequency(list, map.restaturant_id);
            maps.add(map);
        }

        if (getIntent().getExtras() != null && getIntent().getExtras().getBoolean("isGPS")) {
            mRecyclerView.setVisibility(View.GONE);
        }

        mRecyclerView.setHasFixedSize(true);

        mLayoutManager = new LinearLayoutManager(this);
            mRecyclerView.setLayoutManager(mLayoutManager);

        MyAdapter mAdapter = new MyAdapter(thisContext, dishes);
            mRecyclerView.setAdapter(mAdapter);

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
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

        // Add a marker and move the camera
        try {
            for (int i = 0; i < maps.size(); i++) {
                LatLng sydney = new LatLng(Double.parseDouble(maps.get(i).latitude), Double.parseDouble(maps.get(i).longitude));
                mMap.addMarker(new MarkerOptions().position(sydney).title(maps.get(i).count + "")
                        .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN)));
            }
            LatLng sydney = new LatLng(-34, 151);
            mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));

            mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
                @Override
                public boolean onMarkerClick(Marker marker) {
                    for (int j = 0; j < dishes.size(); j++) {
                        if (marker.getTitle().equals(Collections.frequency(list, dishes.get(j).rest_id) + "")) {
                            marker.setIcon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED));
                            //Log.d("j", j + " " + dishes.get(j).restaurant_name);
                            mLayoutManager.scrollToPositionWithOffset(j, 0);
                            marker.showInfoWindow();
                            return true;
                        }
                    }

                    return false;
                }
            });
        }catch(Exception e){
            Log.d("Tag", "onMapReady: "+e);
        }
    }



    public class MyAdapter extends RecyclerView.Adapter<MyAdapter.ViewHolder> {

        private ArrayList<DishModel> mDataset;
        private Context context;

        public class ViewHolder extends RecyclerView.ViewHolder {
            // each data item is just a string in this case
            TextView ratingText;
            TextView addressMapTV;
            TextView restaurantName;
            ImageView mapHeartImage;

            public ViewHolder(View itemView) {
                super(itemView);

                ratingText = (TextView) itemView.findViewById(R.id.dishRatingMapTV);
                addressMapTV = (TextView) itemView.findViewById(R.id.addressMapTV);
                restaurantName = (TextView) itemView.findViewById(R.id.restaurantNameMapTV);
                mapHeartImage = (ImageView) itemView.findViewById(R.id.heartImageGrid);

            }
        }

        @Override
        public MyAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            // create a new view
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.map_row, parent, false);
            ViewHolder vh = new ViewHolder(v);
            return vh;
        }

        // Provide a suitable constructor (depends on the kind of dataset)
        public MyAdapter(Context context, ArrayList<DishModel> restaurants) {
            mDataset = restaurants;
            this.context = context;
        }

        // Replace the contents of a view (invoked by the layout manager)
        @Override
        public void onBindViewHolder(ViewHolder holder, int position) {
            // - get element from your dataset at this position
            // - replace the contents of the view with that element
            holder.ratingText.setText(mDataset.get(position).average_rating);
            holder.restaurantName.setText(mDataset.get(position).name);
            holder.addressMapTV.setText(mDataset.get(position).rest_name);

            if (mDataset.get(position).favourited_by) {
                holder.mapHeartImage.setImageDrawable(getResources().getDrawable(R.drawable.solid_heart, context.getTheme()));
            } else {
                holder.mapHeartImage.setImageDrawable(getResources().getDrawable(R.drawable.heart, context.getTheme()));
            }

            Typeface lato = Typeface.createFromAsset(getAssets(), "fonts/lato.ttf");
            Typeface latob = Typeface.createFromAsset(getAssets(), "fonts/lato_bold.ttf");

            holder.ratingText.setTypeface(latob);
            holder.addressMapTV.setTypeface(lato);
            holder.restaurantName.setTypeface(lato);

        }

        // Return the size of your dataset (invoked by the layout manager)
        @Override
        public int getItemCount() {
            return mDataset.size();
        }
    }


}
