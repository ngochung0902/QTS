package com.inducesmile.androidloginwithretrofit;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        String myLoginEmailAddress = getLoginEmailAddress();
        TextView loginInformation = (TextView)findViewById(R.id.login_email);
        if(myLoginEmailAddress != null || !myLoginEmailAddress.equals("")){
            loginInformation.setText("Welcome!!! You have logged in as " + myLoginEmailAddress);
        }else {
            loginInformation.setText("Your login email is missing");
        }
    }

    private String getLoginEmailAddress(){
        String storedEmail = "";
        Intent mIntent = getIntent();
        Bundle mBundle = mIntent.getExtras();
        if(mBundle != null){
            storedEmail = mBundle.getString("EMAIL");
        }
        return storedEmail;
    }
}
