package utility;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.net.Uri;
import android.provider.MediaStore;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

import com.foodapp.lien.foodapp.PostActivity;
import com.foodapp.lien.foodapp.R;

import java.io.File;
import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by AtharvaSystem on 13-09-2016.
 * General Function which is use in acticity
 */
public class Generalfunction {

    public static boolean isValidUserID(String w) {
        return w.matches("[A-Za-z0-9]+");
    }

    public static boolean isValidUserName(String w) {
        return w.matches("[A-Za-z]+");
    }

    public boolean isValidEmailAddress(String email) {
        String ePattern = "^[a-zA-Z0-9.!#$%&'*+/=?^_`{|}~-]+@((\\[[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}\\])|(([a-zA-Z\\-0-9]+\\.)+[a-zA-Z]{2,}))$";
        java.util.regex.Pattern p = java.util.regex.Pattern.compile(ePattern);
        java.util.regex.Matcher m = p.matcher(email);
        return m.matches();
    }

    //check if it only consists of whitespace
    public static boolean CheckWspace(String string){

        boolean isWhitespace = string.contains(" ");
       // Log.d("checkspace", "CheckWspace: "+isWhitespace);
        return isWhitespace;
    }

    public static boolean isEmptyCheck(final String string) {
        if(TextUtils.isEmpty(string))
        {
            return true;
        }
        else
        {
            return false;
        }

    }
    public static boolean isEmailValid(final String mailAddress) {

        Pattern pattern;
        Matcher matcher;
        final String EMAIL_PATTERN = "^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@" + "[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$";

        pattern = Pattern.compile(EMAIL_PATTERN);
        matcher = pattern.matcher(mailAddress);
        return matcher.matches();

    }

    public static boolean isCompare(final String str,final String compare) {
        if(str.toLowerCase().equalsIgnoreCase(compare.toLowerCase()))
        {
            return true;
        }
        else
        {
            return false;
        }
    }
    public static void hideKeyboard(Activity activity) {
        InputMethodManager inputManager = (InputMethodManager) activity
                .getSystemService(Context.INPUT_METHOD_SERVICE);
        // check if no view has focus:
        View view = activity.getCurrentFocus();
        if (view != null) {
            inputManager.hideSoftInputFromWindow(view.getWindowToken(),
                    InputMethodManager.HIDE_NOT_ALWAYS);
        }
    }




    static AlertDialog alertDialog,alertDialogclick;
    public static void Simple1ButtonDialog(String strMessage, Context context){

        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);
        alertDialogBuilder.setTitle(context.getResources().getString(R.string.app_name));
        alertDialogBuilder.setMessage(strMessage);


        alertDialogBuilder.setNegativeButton("OK",new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                alertDialog.dismiss();
            }
        });

        alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }

    public static void Simple1ButtonDialogClick(String strMessage, final Context context){

        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);
        alertDialogBuilder.setTitle(context.getResources().getString(R.string.app_name));
        alertDialogBuilder.setMessage(strMessage);
        alertDialogBuilder.setCancelable(false);


        alertDialogBuilder.setNegativeButton("OK",new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                alertDialogclick.dismiss();
                ((Activity)context).finish();
            }
        });

        alertDialogclick = alertDialogBuilder.create();
        alertDialogclick.show();
    }

    //Dispaly post bitmap

    public static void ShowPostImage(Intent data,Context mContext){

        Uri selectedImageUri, fileUri = null;
        boolean isCamera=false;
        Bitmap finalBitmap=null;

        selectedImageUri = (data == null ? true : MediaStore.ACTION_IMAGE_CAPTURE.equals(data.getAction())) ? fileUri : (data == null ? null : data.getData());

        Log.d("navigationResult  ", data+" ");


        if (selectedImageUri != null) {
            String file_path;
            file_path = selectedImageUri.getPath();

            Bitmap bitmap1 = Generalfunction.decodeFile(file_path);
            // Bitmap bitmap1 = MediaStore.Images.Media.getBitmap(this.getContentResolver(), filePathUri);
            Bitmap bmRotated = Generalfunction.rotateBitmap(bitmap1, file_path);
            finalBitmap = bmRotated;
        }
        else{

        }

        Log.d("rup", "final bitmap is "+finalBitmap);

        Intent intent  = new Intent(mContext, PostActivity.class);
        intent.putExtra("Image", finalBitmap);
        mContext.startActivity(intent);


    }

    public Bitmap StringToBitMap(String encodedString){
        try{
            byte [] encodeByte= Base64.decode(encodedString,Base64.DEFAULT);
            Bitmap bitmap=BitmapFactory.decodeByteArray(encodeByte, 0, encodeByte.length);
            return bitmap;
        }catch(Exception e){
            e.getMessage();
            return null;
        }
    }

    /*Camera bitmap */

    public static Bitmap decodeFile(String filePath) {
        // Decode image size
        BitmapFactory.Options o = new BitmapFactory.Options();
        o.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(filePath, o);
        // The new size we want to scale to
        final int REQUIRED_SIZE = 1024;
        // Find the correct scale value. It should be the power of 2.
        int width_tmp = o.outWidth, height_tmp = o.outHeight;
        int scale = 1;
        while (true) {
            if (width_tmp < REQUIRED_SIZE && height_tmp < REQUIRED_SIZE)
                break;
            width_tmp /= 2;
            height_tmp /= 2;
            scale *= 2;
        }
        // Decode with inSampleSize
        BitmapFactory.Options o2 = new BitmapFactory.Options();
        o2.inSampleSize = scale;
        Bitmap bitmap1 = BitmapFactory.decodeFile(filePath, o2);
        return bitmap1;
    }

    public static Bitmap rotateBitmap(Bitmap bitmap, String file_path) {

        ExifInterface exif = null;
        try {
            exif = new ExifInterface(file_path);
        } catch (IOException e) {
            e.printStackTrace();
        }
        int orientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_UNDEFINED);

        Matrix matrix = new Matrix();
        switch (orientation) {
            case ExifInterface.ORIENTATION_NORMAL:
                return bitmap;
            case ExifInterface.ORIENTATION_FLIP_HORIZONTAL:
                matrix.setScale(-1, 1);
                break;
            case ExifInterface.ORIENTATION_ROTATE_180:
                matrix.setRotate(180);
                break;
            case ExifInterface.ORIENTATION_FLIP_VERTICAL:
                matrix.setRotate(180);
                matrix.postScale(-1, 1);
                break;
            case ExifInterface.ORIENTATION_TRANSPOSE:
                matrix.setRotate(90);
                matrix.postScale(-1, 1);
                break;
            case ExifInterface.ORIENTATION_ROTATE_90:
                matrix.setRotate(90);
                break;
            case ExifInterface.ORIENTATION_TRANSVERSE:
                matrix.setRotate(-90);
                matrix.postScale(-1, 1);
                break;
            case ExifInterface.ORIENTATION_ROTATE_270:
                matrix.setRotate(-90);
                break;
            default:
                return bitmap;
        }
        try {
            Bitmap bmRotated = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
            bitmap.recycle();
            return bmRotated;
        } catch (OutOfMemoryError e) {
            e.printStackTrace();
            return null;
        }
    }

    public static void OpenMap(String latitude,String longitude,Context context){

        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION)
                != PackageManager.PERMISSION_GRANTED)  {
            ActivityCompat.requestPermissions((Activity) context,
                    new String[]{Manifest.permission.ACCESS_COARSE_LOCATION},
                    Constant.PERMISSION_REQUEST_CODE_Location);
        } else {
            GPSTracker gpsTracker = new GPSTracker(context);

            String stringLatitude = "";
            String stringLongitude = "";
            String dLAT = latitude;
            String dLONG = longitude;

            if (gpsTracker.getIsGPSTrackingEnabled()) {
                stringLatitude = String.valueOf(gpsTracker.getLatitude());
                stringLongitude = String.valueOf(gpsTracker.getLongitude());
            }

            Log.d("Map url", "OpenMap: "+"http://maps.google.com/maps?" + "saddr=" + stringLatitude + "," + stringLongitude + "&daddr=" + dLAT + "," + dLONG);

            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("http://maps.google.com/maps?" + "saddr=" + stringLatitude + "," + stringLongitude + "&daddr=" + dLAT + "," + dLONG));
            intent.setClassName("com.google.android.apps.maps", "com.google.android.maps.MapsActivity");
            context.startActivity(intent);
        }

    }

   /* ******************************Check permission of android M  ******************************************/

    public static boolean checkPermission(Context mContext,int code){

        int result = 10;

        switch (code){
            case Constant.PERMISSION_REQUEST_CODE_Call:
                result= ContextCompat.checkSelfPermission(mContext, Manifest.permission.CALL_PHONE);
                break;
            case Constant.PERMISSION_REQUEST_CODE_Camera:
                result= ContextCompat.checkSelfPermission(mContext, Manifest.permission.CAMERA);
                break;
        }

        if (result == PackageManager.PERMISSION_GRANTED){
            return true;
        } else {
            return false;
        }
    }


    public static void requestPermission(Context mContext,int code){
        if (ActivityCompat.shouldShowRequestPermissionRationale((Activity) mContext,Manifest.permission.CALL_PHONE)){
            String strMSG="Permission allows us to access . Please allow for additional functionality.";
            PermissionDialog(strMSG,mContext);
        } else {
            ActivityCompat.requestPermissions((Activity) mContext,new String[]{Manifest.permission.CALL_PHONE},Constant.PERMISSION_REQUEST_CODE_Call);
        }
    }

    static AlertDialog alertDialogpermission;
    private static void PermissionDialog(String strMessage, final Context mContext){

        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(mContext);
        alertDialogBuilder.setTitle("Allows Permission");
        alertDialogBuilder.setMessage(strMessage);


        alertDialogBuilder.setPositiveButton("yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface arg0, int arg1) {
                // Toast.makeText(MainActivity.this,"You clicked yes button",Toast.LENGTH_LONG).show();
                startInstalledAppDetailsActivity((Activity) mContext);
            }
        });

        alertDialogBuilder.setNegativeButton("No",new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                alertDialogpermission.dismiss();
            }
        });

        alertDialogpermission = alertDialogBuilder.create();
        alertDialogpermission.show();
    }

    public static void startInstalledAppDetailsActivity(final Activity context) {
        if (context == null) {
            return;
        }
        final Intent i = new Intent();
        i.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
        i.addCategory(Intent.CATEGORY_DEFAULT);
        i.setData(Uri.parse("package:" + context.getPackageName()));
        i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        i.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
        i.addFlags(Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS);
        context.startActivity(i);
    }

    //Save values of user which show on many screen

    public static void SaveUserDetail(Context context,String firstname,String lastname,String username,String id,String email){
        GlobalVar.setMyStringPref(context, Constant.loginUserfirstName,firstname);
        GlobalVar.setMyStringPref(context, Constant.loginUserlastName,lastname);
        GlobalVar.setMyStringPref(context, Constant.loginUserName,username);
        GlobalVar.setMyStringPref(context, Constant.loginUserID,id);
        GlobalVar.setMyStringPref(context, Constant.loginUserEmail,email);
    }


}
