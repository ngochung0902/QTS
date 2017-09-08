package lastdaytips.com.lastdaytips.activity;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.ImageView;

import lastdaytips.com.lastdaytips.R;
import lastdaytips.com.lastdaytips.helper.QTSHelp;

public class ActMain extends AppCompatActivity {
    ImageView img_background,img_btstart;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.act_main);
        initUI();
        QTSHelp.setLayoutView(img_background,QTSHelp.GetWidthDevice(this),QTSHelp.GetWidthDevice(this)*405/540);
        QTSHelp.setLayoutView(img_btstart,QTSHelp.GetWidthDevice(this),QTSHelp.GetHeightDevice(this)*240/667);
        img_btstart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(ActMain.this,ActMenu.class);
                startActivity(i);
            }
        });
    }

    private void initUI() {
        img_btstart = (ImageView) findViewById(R.id.img_btstart);
        img_background = (ImageView) findViewById(R.id.img_background);
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
