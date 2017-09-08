package com.foodapp.lien.foodapp;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Parcelable;
import android.provider.MediaStore;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class Global {

    public static  String FACEBOOK_APP_ID = "810416655730097";
    private static final int CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE = 100;
    public static boolean isProductionBuild = false;

    public static String mSTR_SORT_PRICE = "price";
    public static String mSTR_SORT_RATING = "rating";
    public static String mSTR_SORT_DISTANCE = "distance";
    public static String mSTR_DIRECTION_DESCENDING = "desc";
    public static String mSTR_DIRECTION_ASCENDING = "asc";

    public static String globalURL = "https://peppa.herokuapp.com/api/v1/";
    public static String globalDishURL = globalURL + "dishes/";
    public static String globalRestURL = globalURL + "restaurants/";
    public static String globalUserURL = globalURL + "users/";

    public static String signUpURL = globalURL + "signup";
    public static String signInURL = globalURL + "signin";
    public static String providerSignURL = globalURL + "authentications";
    public static String resetPassURL = globalURL + "forget_password";
    public static String getuserURL = globalUserURL + "show";

    public static void loadFont(Context  context, TextView view, String fontName) {
        Typeface tf = Typeface.createFromAsset(context.getAssets(), "fonts/" + fontName);
        view.setTypeface(tf);
    }

    public static void openImageIntent(Activity context, Uri fileUri) {

        // Camera.
        final List<Intent> cameraIntents = new ArrayList<Intent>();
        final Intent captureIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
        final PackageManager packageManager = context.getPackageManager();
        final List<ResolveInfo> listCam = packageManager.queryIntentActivities(captureIntent, 0);
        for(ResolveInfo res : listCam) {
            final String packageName = res.activityInfo.packageName;
            final Intent intent = new Intent(captureIntent);
            intent.setComponent(new ComponentName(res.activityInfo.packageName, res.activityInfo.name));
            intent.setPackage(packageName);
            intent.putExtra(MediaStore.EXTRA_OUTPUT, fileUri);
            cameraIntents.add(intent);
        }

        // File System.
        final Intent galleryIntent = new Intent();
        galleryIntent.setType("image/*");
        galleryIntent.setAction(Intent.ACTION_GET_CONTENT);

        // Chooser of file System options.
        final Intent chooserIntent = Intent.createChooser(galleryIntent, "Select Source");

        // Add the camera options.
        chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, cameraIntents.toArray(new Parcelable[cameraIntents.size()]));

        context.startActivityForResult(chooserIntent, CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE);
    }

    public String getExtension(String path) {
        return  path.substring(path.lastIndexOf("/")+1);
    }

 }
