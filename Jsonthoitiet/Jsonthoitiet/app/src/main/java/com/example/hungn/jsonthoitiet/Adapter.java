package com.example.hungn.jsonthoitiet;

import android.content.Context;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

/**
 * Created by hungn on 05/29/17.
 */

public class Adapter extends ArrayAdapter<thoitiet> {


    public Adapter(@NonNull Context context, @LayoutRes int resource, @NonNull List<thoitiet> objects) {
        super(context, resource, objects);
    }
    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        thoitiet tt = getItem(position);
        if(convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.activity_main, parent, false);
        }

        TextView tv_city = (TextView) convertView.findViewById(R.id.tv_city);
        TextView tv_thoitiet = (TextView) convertView.findViewById(R.id.tv_thoitiet);
        TextView tv_nhietdo = (TextView) convertView.findViewById(R.id.tv_nhietdo);
        ImageView imgtt = (ImageView) convertView.findViewById(R.id.imgtt);

        tv_city.setText(tt.getTv_city());
        tv_thoitiet.setText(tt.getTv_thoitiet());
        tv_nhietdo.setText(tt.getTv_nhietdo());

        int imgResource=getContext().getResources().getIdentifier(tt.getImgtt(), "drawable",
                getContext().getPackageName());
        imgtt.setImageResource(imgResource);
        return convertView;
    }
}
