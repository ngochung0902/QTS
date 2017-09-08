package lastdaytips.com.lastdaytips.activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.DisplayMetrics;
import android.widget.ImageView;
import android.widget.ListView;

import java.util.ArrayList;

import lastdaytips.com.lastdaytips.R;
import lastdaytips.com.lastdaytips.adapter.AdapterLVMenu;
import lastdaytips.com.lastdaytips.helper.QTSHelp;
import lastdaytips.com.lastdaytips.model.ItemMenu;

public class ActMenu extends AppCompatActivity {
    private ImageView img_lastday;
    private ListView lv_menu;
    private AdapterLVMenu adapter;
    private ArrayList<ItemMenu> arr = new ArrayList<>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.act_menu);
        initUI();
        QTSHelp.setLayoutView(img_lastday,QTSHelp.GetWidthDevice(this),QTSHelp.GetWidthDevice(this)*300/1024);
        dataarr();
        adapter = new AdapterLVMenu(this,arr);
        lv_menu.setAdapter(adapter);
    }

    private void dataarr() {
        arr.add((new ItemMenu("Establish Your Basics")));
        arr.add((new ItemMenu("Fortify Your Position")));
        arr.add((new ItemMenu("Hunt for Upgrades")));
        arr.add((new ItemMenu("How to Find Oak")));
        arr.add((new ItemMenu("How to Find Steel")));
    }

    private void initUI() {
        img_lastday = (ImageView) findViewById(R.id.img_lastday);
        lv_menu = (ListView) findViewById(R.id.lv_menu);
        getWidthHeight();
    }

    private void getWidthHeight() {
        DisplayMetrics displaymetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displaymetrics);
        int height = displaymetrics.heightPixels;
        int wwidth = displaymetrics.widthPixels;
        QTSHelp.SetHeightDevice(this,height);
        QTSHelp.SetWidthDevice(this,wwidth);
    }
}
