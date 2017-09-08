package com.weather_json;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ListView;

public class MainActivity extends Activity {
	private ListView myListView;
	private MyArrayAdapter myAdapter;
	private String jsonStr = "";
	ArrayList<Weather> weatherArray;
	HttpAsyncTask httpAsyncTask;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		myListView = (ListView) findViewById(R.id.mylistView);
		
		httpAsyncTask = new HttpAsyncTask(MainActivity.this);
        httpAsyncTask.execute("https://api.myjson.com/bins/4z4r4");
                
		//create ArrayList
		weatherArray = new ArrayList<Weather>();
		/*weatherArray.add(new Weather("Ha Noi", "Sunny", "32°C", "sunny"));
		weatherArray.add(new Weather("Quang Binh", "Clou", "31°C", "sunny"));
		weatherArray.add(new Weather("Hue", "Sunny", "35°C", "sunny"));
		weatherArray.add(new Weather("Quang Binh", "Sunny", "30°C", "sunny"));
		weatherArray.add(new Weather("Nha Trang", "Sunny", "29°C", "sunny"));
		weatherArray.add(new Weather("Phan Thiet", "Sunny", "28°C", "sunny"));*/
		
		//create adapter
		myAdapter = new MyArrayAdapter(MainActivity.this, R.layout.list_item, weatherArray);
		
		myListView.setAdapter(myAdapter);		
	}

	public void Update() {
		jsonStr = httpAsyncTask.strResult;
        Log.i("----Json result:", jsonStr);
        try {
            JSONObject jsonRootObject = new JSONObject(jsonStr);           
            JSONArray jsonArray = jsonRootObject.optJSONArray("Weather");
            for(int i = 0; i < jsonArray.length(); i++) {
                   JSONObject jsonObject = jsonArray.getJSONObject(i);
                   String cityName = jsonObject.optString("cityName").toString();
                   String weather = jsonObject.optString("weather").toString();
                   String temperature = jsonObject.optString("temperature").toString();
                   String icon = jsonObject.optString("icon").toString();
                   weatherArray.add(new Weather(cityName, weather, temperature, icon));
            }
            
            myAdapter.notifyDataSetChanged();
	     } catch (JSONException e) {
	            e.printStackTrace();
	     }
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
}
