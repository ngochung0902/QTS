package com.example.hungn.jsonthoitiet;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.ListView;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements Downloadinterface {

    private Adapter adapter;
    private ListView lv_thoitiet;
    private ArrayList<thoitiet> arr;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_lv);
        //1
        lv_thoitiet = (ListView)this.findViewById(R.id.lv_thoitiet);
        //2
        arr = new ArrayList<>();
        AsyncTaskDownload asyncTaskDownload = new AsyncTaskDownload();
        asyncTaskDownload.SetLister(this);
        asyncTaskDownload.execute("https://api.myjson.com/bins/4z4r4");
        //3
        adapter = new Adapter(MainActivity.this, R.layout.activity_main, arr);
        //4
        lv_thoitiet.setAdapter(adapter);
    }
    @Override
    public void OnFinisheDownload(String result) {
        Log.i("hehe", result);
        ParseJson(result);
    }
    public void ParseJson(String jsonString) {
        try{
            JSONObject jsonRootObject = new JSONObject(jsonString);
            JSONArray jsonArray = jsonRootObject.optJSONArray("Weather");
            for (int i=0; i < jsonArray.length(); i++) {
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                String tv_city = jsonObject.optString("cityName");
                String tv_thoitiet = jsonObject.optString("weather");
                String tv_nhietdo = jsonObject.optString("temperature");
                String imgtt = jsonObject.optString("icon");
                arr.add(new thoitiet(tv_city, tv_nhietdo, tv_thoitiet, imgtt));
            }
            adapter.notifyDataSetChanged();
        }catch (Exception e) {
            e.printStackTrace();
        }
    }
}
