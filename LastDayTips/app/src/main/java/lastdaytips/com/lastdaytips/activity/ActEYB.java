package lastdaytips.com.lastdaytips.activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.webkit.WebView;

import lastdaytips.com.lastdaytips.R;

public class ActEYB extends AppCompatActivity {
    private WebView wv_eyb;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_act_eyb);
        wv_eyb = (WebView) findViewById(R.id.wv_eyb);
        wv_eyb.loadUrl("file:///android_assets/1EstablishYourBasics.html");
    }
}
