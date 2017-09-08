package com.weather_json;

import java.util.List;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class MyArrayAdapter extends ArrayAdapter<Weather> {

	public MyArrayAdapter(Context context, int textViewResourceId, List<Weather> objects) {
		super(context, textViewResourceId, objects);
		
	}
	
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		Weather weather = getItem(position);
		
		if(convertView == null) {
			convertView = LayoutInflater.from(getContext()).inflate(R.layout.list_item, parent, false);
		}
		
		TextView tvNameCity = (TextView) convertView.findViewById(R.id.tvNameCity);
		TextView tvWeather = (TextView) convertView.findViewById(R.id.tvWeather);
		TextView tvTemperature = (TextView) convertView.findViewById(R.id.tvTemperature);
		ImageView imageviewIcon = (ImageView) convertView.findViewById(R.id.imageViewIcon);
		
		tvNameCity.setText(weather.getCityName());
		tvWeather.setText(weather.getWeather());
		tvTemperature.setText(weather.getTemperature());
		
		String uri = "drawable/" + weather.getPathImage();
		int imageResource = convertView.getContext().getApplicationContext().getResources().getIdentifier(uri, null, convertView.getContext().getApplicationContext().getPackageName());
		Drawable res = convertView.getContext().getResources().getDrawable(imageResource);
		imageviewIcon.setImageDrawable(res);
		
		return convertView;
	}	
}
