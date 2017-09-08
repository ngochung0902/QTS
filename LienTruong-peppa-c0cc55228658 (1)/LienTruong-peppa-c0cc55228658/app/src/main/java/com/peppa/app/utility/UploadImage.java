package com.peppa.app.utility;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.ImageView;

import com.peppa.app.Activity.NavigationalSearchActivity;

import org.json.JSONException;
import org.json.JSONObject;

import com.peppa.app.parsing.WebFunctions;

/**
 * Created by Rup barad on 18-10-2016.
 */
public class UploadImage {

    Context context;
    String  Id, image, type;
    ImageView imageView;
    Bitmap bitmap;

    public UploadImage(Context mcontext, String id, String image , int type, ImageView imageview, Bitmap bitmap){

        this.context=mcontext;
        this.Id=id;
        this.image=image;
        this.type=String.valueOf(type);
        this.imageView=imageview;
        this.bitmap=bitmap;
    }

    public void Upload_Image(){

        new ImageUpload().execute(Id,image,type);
    }

    class ImageUpload extends AsyncTask<String, Void, String> {


        ProgressDialog progress;
        String token;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            SharedPreferences prefs = context.getSharedPreferences("MY_PREFS", context.MODE_PRIVATE);
            token = prefs.getString("token", "");
            Log.d("token", token);

            progress = new ProgressDialog(context);
            progress.setMessage("Processing....");
            progress.setCancelable(false);
            progress.show();
        }

        @Override
        protected String doInBackground(String... params) {
            // call webfunction
            return WebFunctions.PostUserImage(token, params[0], params[1], params[2]);
        }

        @Override
        protected void onPostExecute(String aVoid) {
            super.onPostExecute(aVoid);

            progress.dismiss();

            Log.d(Constant.TAG, "on post : post api - : " + aVoid);
            String Message = "";

            if (!aVoid.equals("")) {
                JSONObject jsonObject;

                try {
                    Log.d("rup", "onPostExecute: " + aVoid);
                    jsonObject = new JSONObject(aVoid);

                    if (jsonObject.getString("success").toLowerCase().equalsIgnoreCase("yes")) {
                        Message = jsonObject.getString("message");

                        try{
                            imageView.setImageBitmap(bitmap);
                            if(type.equalsIgnoreCase(String.valueOf(Constant.case_imageupload_profile))){
                                String img_url=jsonObject.getString("image_url");
                                //Log.d(Constant.TAG, "image url onPostExecute: "+img_url);
                                GlobalVar.setMyStringPref(context, Constant.loginUserProfilePhoto,img_url);

                                if(((Activity)context) instanceof NavigationalSearchActivity){
                                    //Log.d(Constant.TAG, "image url update: "+img_url);
                                    ((NavigationalSearchActivity)context).DisplayNavigationDetail();
                                }
                            }
                        }catch (Exception e){
                            e.printStackTrace();
                        }



                    } else if (jsonObject.getString("success").toLowerCase().equalsIgnoreCase("no")) {
                        Message = jsonObject.getString("message");
                    } else {
                        Message = jsonObject.getString("message");
                    }

                } catch (JSONException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }

                Generalfunction.Simple1ButtonDialog(Message,context);



            }
        }
    }
}
