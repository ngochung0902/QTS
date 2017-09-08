package com.peppa.app.utility;

import android.content.Context;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.peppa.app.R;

import java.io.IOException;
import java.util.List;
import java.util.Locale;
/**
 * Created by Rup barad on 04-10-2016.
 */
public class LocationAddress {
    private static final String TAG = "LocationAddress";

    public static void getAddressFromLocation(final double latitude, final double longitude,
                                              final Context context, final Handler handler) {
        Thread thread = new Thread() {
            @Override
            public void run() {

                String result = null;

                try {
                    Geocoder geocoder = new Geocoder(context, Locale.getDefault());


                    Log.d(TAG, "location: latitude"+latitude+" longitude: "+longitude);
                    List<Address> addressList = geocoder.getFromLocation(latitude, longitude, 1);
                    if (addressList != null && addressList.size() > 0) {
                       // Log.d(TAG, "run:addresslist "+addressList);
                        Address address = addressList.get(0);
                        StringBuilder sb = new StringBuilder();

                        sb.append(address.getLocality());

                        String strAdmin=address.getAdminArea();

                        if(!Generalfunction.isEmptyCheck(""+strAdmin)){
                            sb.append(",");
                            sb.append(strAdmin.substring(0, Math.min(strAdmin.length(), 3)).trim());
                        }

                        if(Generalfunction.isEmptyCheck(sb.toString())){
                            for (int i = 0; i < address.getMaxAddressLineIndex(); i++) {
                                sb=new StringBuilder();
                                if(i==1){
                                    sb.append(address.getAddressLine(i));
                                    break;
                                }
                            }
                        }

                        //Toast.makeText(context, "Address:"+address.getAddressLine(1).toString()+ " \n "+sb.toString(), Toast.LENGTH_SHORT).show();
                        result = sb.toString();
                    }

                } catch (IOException e) {
                    Log.e(TAG, "Unable connect to Geocoder", e);
                } finally {

                    Message message = Message.obtain();
                    message.setTarget(handler);

                    if(result == null){
                        result=context.getResources().getString(R.string.location);
                    }

                    message.what = 1;
                    Bundle bundle = new Bundle();
                    bundle.putString("address", result.toUpperCase());
                    message.setData(bundle);

                    message.sendToTarget();

                }
            }
        };
        thread.start();
    }


}
