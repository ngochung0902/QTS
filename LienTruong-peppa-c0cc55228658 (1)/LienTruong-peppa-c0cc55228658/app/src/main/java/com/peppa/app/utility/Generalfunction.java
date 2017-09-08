package com.peppa.app.utility;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.ComponentName;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.location.LocationManager;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.ParcelFileDescriptor;
import android.os.Parcelable;
import android.provider.MediaStore;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Base64;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.webkit.MimeTypeMap;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.peppa.app.Activity.NavigationalSearchActivity;
import com.peppa.app.Activity.PostActivity_;
import com.peppa.app.Activity.UserProfileActivity;
import com.peppa.app.R;
import com.peppa.app.Activity.SettingActivity;
import com.squareup.picasso.Picasso;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileDescriptor;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.peppa.app.model.DishModel;

/**
 * Created by Rup barad on 13-09-2016.
 * General Function which is use in activity
 */
public class Generalfunction {

    //Display log
    public static  void DisplayLog(String Message){
        if(!Constant.isProductionBuild) {
            Log.d(Constant.TAG, "DisplayLog: " + Message);
        }
    }


    //Check - Check string is empty?
    public static boolean isEmptyCheck(final String string) {

        return (TextUtils.isEmpty(string)) ? true : false ;

    }


    //Check - Check email id is valid?
    public static boolean isEmailValid(final String mailAddress) {

        Pattern pattern;
        Matcher matcher;
        final String EMAIL_PATTERN = "^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@" + "[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$";

        pattern = Pattern.compile(EMAIL_PATTERN);
        matcher = pattern.matcher(mailAddress);
        return matcher.matches();

    }


    //Check - Check 2 string are equal or not?
    public static boolean isCompare(String str, String compare) {

        str=Generalfunction.Isnull(str);
        compare=Generalfunction.Isnull(compare);

        return (str.toLowerCase().trim().equalsIgnoreCase(compare.toLowerCase().trim())) ? true:false ;

    }



    //Hide keyboard
    public static void hideKeyboard(Activity activity) {
        InputMethodManager inputManager = (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
        // check if no view has focus:
        View view = activity.getCurrentFocus();
        if (view != null) {
            inputManager.hideSoftInputFromWindow(view.getWindowToken(),InputMethodManager.HIDE_NOT_ALWAYS);
        }
    }

    public static void ShowKeyboard(Activity activity) {
        InputMethodManager inputManager = (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
        // check if no view has focus:
        View view = activity.getCurrentFocus();
        if (view != null) {
            inputManager.showSoftInput(view, InputMethodManager.SHOW_IMPLICIT);
        }
    }


    //Display - One button dialog
    static AlertDialog alertDialog,alertDialogclick;
    public static void Simple1ButtonDialog(final String strMessage, final Context context){

        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);
        alertDialogBuilder.setTitle(context.getResources().getString(R.string.app_name));
        alertDialogBuilder.setMessage(strMessage);

        alertDialogBuilder.setNegativeButton("OK",new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                alertDialog.dismiss();
                if(Generalfunction.isCompare(context.getResources().getString(R.string.Internet_Message),strMessage)){
                    ((Activity)context).finish();
                }
            }
        });

        alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }



    //Display - One button dialog which is not cancelable
    public static void Simple1ButtonDialogClick(String strMessage, final Context context){

        final Activity mActivity=((Activity)context);

        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);
        alertDialogBuilder.setTitle(context.getResources().getString(R.string.app_name));
        alertDialogBuilder.setMessage(strMessage);
        alertDialogBuilder.setCancelable(false);

        alertDialogBuilder.setNegativeButton("OK",new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                alertDialogclick.dismiss();
//                ((Activity) context).finish();

                if(mActivity instanceof PostActivity_){
                    if(GlobalVar.getMyBooleanPref(context,Constant.IsPost_Nextdish)){
                        Intent intent = new Intent(mActivity, PostActivity_.class);
                        context.startActivity(intent);
                    }
                }
            }
        });

        alertDialogclick = alertDialogBuilder.create();
        alertDialogclick.show();
    }

    public static void Simple1ButtonDialogClick2(String strMessage, final Context context){

        final Activity mActivity=((Activity)context);

        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);
        alertDialogBuilder.setTitle(context.getResources().getString(R.string.app_name));
        alertDialogBuilder.setMessage(strMessage);
        alertDialogBuilder.setCancelable(false);

        alertDialogBuilder.setNegativeButton("OK",new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                alertDialogclick.dismiss();
                //((Activity) context).finish();


            }
        });

        alertDialogclick = alertDialogBuilder.create();
        alertDialogclick.show();
    }

    //Get bitmap - Decode file and get bitmap
    public static Bitmap decodeFile(Uri imageUri,Context context) {

        Bitmap bitmap1=null;

        /* Decode using file */
        try {
            String filePath = imageUri.getPath();

            // Decode image size
            BitmapFactory.Options o = new BitmapFactory.Options();
            o.inJustDecodeBounds = true;
            BitmapFactory.decodeFile(filePath, o);
            final int REQUIRED_SIZE = Constant.ImageSize;                // The new size we want to scale to
            int width_tmp = o.outWidth, height_tmp = o.outHeight;        // Find the correct scale value. It should be the power of 2.
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
            bitmap1 = BitmapFactory.decodeFile(filePath, o2);
        }
        catch(Exception e){  }


        /*Decode stream file using Uri
        * DecodestremBitmap
        * */

        if(bitmap1==null){
            bitmap1=DecodestremBitmap(context,imageUri);
        }

        return bitmap1;
    }


    //Get bitmap - Decode Uri and get bitmap
    public static Bitmap DecodestremBitmap(Context mContext,Uri selectedImageUri) {

        Bitmap bitmap1=null;
        InputStream is = null;
        try {
            is = mContext.getContentResolver().openInputStream(selectedImageUri);
            if (is != null) {
                //int orientation = getOrientation(mContext, selectedImageUri);
                try {

                    // decode image size (decode metadata only, not the whole image)
                    BitmapFactory.Options options = new BitmapFactory.Options();
                    options.inJustDecodeBounds = true;
                    BitmapFactory.decodeStream(is, null, options);
                    is.close();
                    is = null;

                    // The new size we want to scale to
                    final int REQUIRED_SIZE = Constant.ImageSize ;

                    // Find the correct scale value. It should be the power of 2.
                    int width_tmp = options.outWidth, height_tmp = options.outHeight;
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

                    bitmap1=BitmapFactory.decodeStream(is, null, options);

                } catch (IOException ex) {
                    //Log.e("Image", e.getMessage(), ex);
                    Log.d(Constant.TAG, "Exception " + ex);

                }
            }
        }
        catch(Exception e){
        }

        if(bitmap1 ==null){
            bitmap1=decodeUri(mContext,selectedImageUri);
        }

        return bitmap1;
    }


    //Get bitmap - Decode Uri and get bitmap using ParcelFileDescriptor
    public static Bitmap decodeUri(Context context, Uri uri) {

        Bitmap bitmap=null;
        ParcelFileDescriptor parcelFD = null;
        try {
            parcelFD = context.getContentResolver().openFileDescriptor(uri, "r");
            FileDescriptor imageSource = parcelFD.getFileDescriptor();

            // Decode image size
            BitmapFactory.Options o = new BitmapFactory.Options();
            o.inJustDecodeBounds = true;
            BitmapFactory.decodeFileDescriptor(imageSource, null, o);

            // the new size we want to scale to
            final int REQUIRED_SIZE = Constant.ImageSize;

            // Find the correct scale value. It should be the power of 2.
            int width_tmp = o.outWidth, height_tmp = o.outHeight;
            int scale = 1;
            while (true) {
                if (width_tmp < REQUIRED_SIZE && height_tmp < REQUIRED_SIZE) {
                    break;
                }
                width_tmp /= 2;
                height_tmp /= 2;
                scale *= 2;
            }

            // decode with inSampleSize
            BitmapFactory.Options o2 = new BitmapFactory.Options();
            o2.inSampleSize = scale;
            bitmap = BitmapFactory.decodeFileDescriptor(imageSource, null, o2);

            return bitmap;

        } catch (FileNotFoundException e) {
            // handle errors
        } catch (IOException e) {
            // handle errors
        } finally {
            if (parcelFD != null)
                try {
                    parcelFD.close();
                } catch (IOException e) {
                    // ignored
                }
        }
        return bitmap;
    }


    //Get bitmap - Rotate bitmap using file path
    public static Bitmap rotateBitmap(Bitmap bitmap, String file_path) {

        ExifInterface exif = null;
        try {
            exif = new ExifInterface(file_path);
        } catch (IOException e) {
            e.printStackTrace();
        }

        int orientation = 0;
        if (exif != null) {
            orientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_UNDEFINED);
        }

        Log.d(Constant.TAG, "rotateBitmap: ");

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


    //Draw - Google map route
    public static void OpenMap(String latitude,Context context,String DestinatonAddress){

        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION)!= PackageManager.PERMISSION_GRANTED)  {
            ActivityCompat.requestPermissions((Activity) context,
                    new String[]{Manifest.permission.ACCESS_COARSE_LOCATION}, Constant.PERMISSION_REQUEST_CODE_Location);
        } else {
            try {
                GPSTracker gpsTracker = new GPSTracker(context);

                String strCurrentLatitude = "";
                String strCurrentLongitude = "";
                String dLAT = latitude;
                //String dLONG = longitude;

                // Check Gps(Location) Avialble
                boolean IsGpsEnable = Generalfunction.CheckGpsAvailable(context);
                if (!IsGpsEnable) {
                    Generalfunction.GpsAlertMessage(context, ((Activity) context));
                } else {
                    if (gpsTracker.getIsGPSTrackingEnabled()) {
                        strCurrentLatitude = String.valueOf(gpsTracker.getLatitude());
                        strCurrentLongitude = String.valueOf(gpsTracker.getLongitude());
                    }

                    if (strCurrentLatitude.equalsIgnoreCase("0.0")) {
                        strCurrentLatitude = GlobalVar.getMyStringPref(context, Constant.CurrentLatitude);
                        strCurrentLongitude = GlobalVar.getMyStringPref(context, Constant.CurrentLongitude);
                    }
                    Log.d("Map url", "OpenMap: " + "http://maps.google.com/maps?" + "saddr=" + strCurrentLatitude + "," + strCurrentLongitude + "&daddr=" + dLAT);

                    String strMapRoute = "http://maps.google.com/maps?" + "saddr=" + strCurrentLatitude + "," + strCurrentLongitude + "&daddr=" + dLAT;
                    if(Generalfunction.isEmptyCheck(Generalfunction.Isnull(strCurrentLatitude)) || strCurrentLatitude.equalsIgnoreCase("0.0")
                            || Generalfunction.isEmptyCheck(Generalfunction.Isnull(dLAT)) || dLAT.equalsIgnoreCase("0.0")){
                        strMapRoute="geo:0,0?q="+DestinatonAddress;
                    }

                    Intent mapIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(strMapRoute));
                    mapIntent.setClassName("com.google.android.apps.maps", "com.google.android.maps.MapsActivity");

                    if (mapIntent.resolveActivity(context.getPackageManager()) != null) {
                        context.startActivity(mapIntent);
                    }

                }
            }catch (Exception e){}

        }
    }


    //Check - GPS is available?
    public static boolean CheckGpsAvailable(final Context mContext) {
        LocationManager manager = (LocationManager)mContext.getSystemService(Context.LOCATION_SERVICE);
        boolean statusOfGPS = manager.isProviderEnabled(LocationManager.GPS_PROVIDER);
        return statusOfGPS;
    }

    //Display - GPS Alert
    public static void GpsAlertMessage(final Context mContext,final Activity activity){
        try {
            AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
            builder.setTitle("Gps setting")
                    .setMessage("GPS is currently disabled. In order to use this application please enable it")
                    .setCancelable(false)
                    .setPositiveButton("Ok",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog,
                                                    int id) {
                                    //Enable GPS

                                    //Open - Setting - Location Service
                                    Intent intentOpenLocationServiceSettings = new Intent();
                                    intentOpenLocationServiceSettings.setAction(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                                    mContext.startActivity(intentOpenLocationServiceSettings);
                                    ((Activity)mContext).finish();

                                }
                            })
                    .setNegativeButton("Cancel",
                            new DialogInterface.OnClickListener(){
                                public void onClick(DialogInterface dialog, int id){
                                    dialog.cancel();
                                }
                            });
            AlertDialog alert = builder.create();
            alert.show();
        } catch (Exception e) {
            Generalfunction.messageBox(mContext, "Logout",Log.getStackTraceString(e));
        }
    }


    //GPS Error alert
    public static void messageBox(Context context, String method, String message) {
        if (message != null) {

            AlertDialog.Builder messageBox = new AlertDialog.Builder(context);
            messageBox.setTitle("Error:-" + method);
            messageBox.setMessage(message);
            messageBox.setCancelable(false);
            messageBox.setNeutralButton("OK", null);
            messageBox.show();
        }
    }



    //Display Marker - Marker using Convert a view to bitmap
    public static Bitmap createDrawableFromView(Context context, View view) {
        DisplayMetrics displayMetrics = new DisplayMetrics();
        ((Activity) context).getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        view.setLayoutParams(new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT));
        view.measure(displayMetrics.widthPixels, displayMetrics.heightPixels);
        view.layout(0, 0, displayMetrics.widthPixels, displayMetrics.heightPixels);
        view.buildDrawingCache();
        Bitmap bitmap = Bitmap.createBitmap(view.getMeasuredWidth(), view.getMeasuredHeight(), Bitmap.Config.ARGB_8888);

        Canvas canvas = new Canvas(bitmap);
        view.draw(canvas);

        return bitmap;
    }



    //Save as a shared preferences - Save values of user which show on many screen
    public static void SaveUserDetail(Context context,String firstname,String lastname,String username,String id,String email,String userProfilephoto,String userCoverphoto){
        GlobalVar.setMyStringPref(context, Constant.loginUserfirstName,firstname);
        GlobalVar.setMyStringPref(context, Constant.loginUserlastName,lastname);
        GlobalVar.setMyStringPref(context, Constant.loginUserName,username);
        GlobalVar.setMyStringPref(context, Constant.loginUserID,id);
        GlobalVar.setMyStringPref(context, Constant.loginUserEmail,email);
        GlobalVar.setMyStringPref(context, Constant.loginUserProfilePhoto,Isnull(userProfilephoto));
        GlobalVar.setMyStringPref(context, Constant.loginUserCoverPhoto,Isnull(userCoverphoto));

    }



    //Display image with picasso
    public static void DisplayImage_picasso(String strImagepath,Context context,int Case,ImageView imageView,String placeholder){

        strImagepath=Generalfunction.Isnull(strImagepath);

        if(Generalfunction.isEmptyCheck(strImagepath))
        {
            if (Generalfunction.isCompare(placeholder, Constant.Ph_dish)) {
                Picasso.with(context).load(R.drawable.no_image).into(imageView);

            }
            else if (Generalfunction.isCompare(placeholder, Constant.Ph_restaurant_coverimage)) {
                Picasso.with(context).load(R.drawable.no_image).into(imageView);
            }
            else if (Generalfunction.isCompare(placeholder, Constant.Ph_user_coverimage)) {
                Picasso.with(context).load(R.drawable.no_image).into(imageView);
            }
            else if (Generalfunction.isCompare(placeholder, Constant.Ph_userprofilepic)) {
                Picasso.with(context).load(R.drawable.no_image).into(imageView);
            }
            else {
                Picasso.with(context).load(R.drawable.no_image).into(imageView);
            }
        }
        else
        {
            switch (Case){

                case 1:
                    Picasso.with(context)
                            .load(strImagepath)
                            .placeholder(R.drawable.image_loading) //this is optional the image to display while the url image is downloading
                            .error(R.drawable.no_image)  //this is also optional if some error has occurred in downloading the image this image would be displayed
                            .into(imageView);
                    break;

                case 2:
                    Picasso.with(context)
                            .load(strImagepath)
                            .centerCrop()
                            .fit()
                            .placeholder(R.drawable.image_loading).error(R.drawable.no_image)
                            .into(imageView);
                    break;

                case 3:
                   // Log.d(Constant.TAG, "image onPostExecute: "+strImagepath);

                    Picasso.with(context)
                            .load(strImagepath)
                            .fit()
                            .placeholder(R.drawable.image_loading)
                            .error(R.drawable.no_image)
                            .into(imageView);

                    break;
            }

        }

    }


    //Display - two button dialog
    public static void DisplaySimple2buttondialog(final String Message, final Context context){

        final Activity mActivity=((Activity)context);

        new AlertDialog.Builder(context)
                .setTitle("Are You Sure")
                .setMessage(Message)
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        if(Message.equalsIgnoreCase(context.getResources().getString(R.string.image_update_cover))){
                            if(mActivity instanceof NavigationalSearchActivity){
                                //Cover photo of user profile
                                NavigationalSearchActivity mUserprofileActivity = (NavigationalSearchActivity) mActivity;
                                mUserprofileActivity.UpdateCoverphoto();
                            }
                            else if(mActivity instanceof SettingActivity){
                                //Cover photo of user profile
                                SettingActivity mSettingActivity = (SettingActivity) mActivity;
                                mSettingActivity.UpdateCoverphoto();
                            }
                        }
                        else if(Message.equalsIgnoreCase(context.getResources().getString(R.string.image_update_photo))){

                            if(mActivity instanceof NavigationalSearchActivity){
                                //Profile photo of user profile
                                NavigationalSearchActivity mUserprofileActivity = (NavigationalSearchActivity) mActivity;
                                mUserprofileActivity.UpdateProfilephoto();
                            }

                            if(mActivity instanceof SettingActivity){
                                //Profile photo of user profile
                                SettingActivity mSettingActivity = (SettingActivity) mActivity;
                                mSettingActivity.UpdateProfilephoto();
                            }

                        }
                    }
                })
                .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        // do nothing
                        dialog.dismiss();
                    }
                })
                //.setIcon(android.R.drawable.ic_dialog_alert)
                .show();
    }


    //Get - Camera URI
    public static Uri getCameraUri(){
        final File root = new File(Constant.Camerapath_Image);
        root.mkdirs();
        String fname = "img_" + System.currentTimeMillis() + ".jpg";
        final File sdImageMainDirectory = new File(root, fname);
        return Uri.fromFile(sdImageMainDirectory);
    }


    //Display - Open image intent
    public static void OpenImageIntent(Context context,Uri fileUri){
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED) {
            // Request missing Camera permission.
            ActivityCompat.requestPermissions(((Activity)context),
                    new String[]{Manifest.permission.CAMERA}, Constant.PERMISSION_REQUEST_CODE_Camera);
        } else {
            // Determine URI of camera image to save.

            // Camera.
            final List<Intent> cameraIntents = new ArrayList<Intent>();
            final Intent captureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            final PackageManager packageManager = context.getPackageManager();
            final List<ResolveInfo> listCam = packageManager.queryIntentActivities(captureIntent, 0);
            for (ResolveInfo res : listCam) {
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

            ((Activity)context).startActivityForResult(chooserIntent, Constant.CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE);
        }
    }


    /** image methodology when image select from
     * gallery
     * or
     * camera
     */
    public static void GetSelectedPhotobitmap_uplaod(String Filename,boolean flag_Cam_Gallery,boolean flagIscamera,boolean isCamerabutgallery,
                                                     ImageView imageview,Context context,String id,int type){

        //if(type = 1) then it is myuser id

        try {
            Uri imageUri = null;

            if (flag_Cam_Gallery) {
                if (flagIscamera) {
                    File imgFile = new File(Filename);
                    imageUri = Uri.fromFile(imgFile);
                } else {
                    imageUri = Uri.parse(Filename);
                }
            } else {
            }

            Generalfunction.DisplayLog("onCreate: Uri  " + imageUri);

            //Get Image mime type
            ContentResolver cR = context.getContentResolver();
            MimeTypeMap mime = MimeTypeMap.getSingleton();

            //Get bitmap with Decode and center Rotation
            Bitmap bitmapDecode = Generalfunction.decodeFile(imageUri, context);
            Generalfunction.DisplayLog("onCreate: bitmapdecode " + bitmapDecode);


            // rotate bitmap when it is a not right
            Bitmap bmRoted = Generalfunction.rotateBitmap(bitmapDecode, imageUri.getPath());
            if (bmRoted == null) {
                bmRoted = bitmapDecode;
                Generalfunction.DisplayLog("onCreate: bitmapRotate" + bmRoted);
            }

            //When Decode and rotate time we don't have bitmap then get bitmap from ImageView
            if (bmRoted == null) {
                bmRoted = ((BitmapDrawable) imageview.getDrawable()).getBitmap();
                Generalfunction.DisplayLog("onCreate: bitmap from imageview" + bmRoted);

            }

            //Set bitmap in Final Bitmap
            if (bmRoted != null) {
                imageview.setImageBitmap(bmRoted);
                String WebPhoto = Generalfunction.bitmapToBase64(bmRoted);

                UploadImage imgUpload = new UploadImage(context, id, WebPhoto, type, imageview, bmRoted);
                imgUpload.Upload_Image();
            }

            //Temporary stored Image delete
            try {
                if (isCamerabutgallery) {
                    File imgFile = new File(imageUri.getPath());
                    boolean flag = imgFile.delete();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        catch(Exception e){}

    }


    //Convert bitmap to base64
    public static String bitmapToBase64(Bitmap bitmap) {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream);
        byte[] byteArray = byteArrayOutputStream .toByteArray();
        return Base64.encodeToString(byteArray, Base64.DEFAULT);
    }


    //Set - Refresh Shared preference value
    public static void RefreshvalueTrue(Context context){
        GlobalVar.setMyBooleanPref(context,Constant.IsScreenRefresh,true);
    }


    //Set - Refresh Shared preference value
    public static void Refreshvaluefalse(Context context){
        GlobalVar.setMyBooleanPref(context,Constant.IsScreenRefresh,false);
    }


    //Set - Refresh Shared preference value
    public static void Refreshvalue_Filter(Context context){

        String strEmpty="";
        boolean flag=false;

        GlobalVar.setMyStringPref(context,Constant.Filter_sortby,strEmpty);
        GlobalVar.setMyStringPref(context,Constant.Filter_priceMax,strEmpty);
        GlobalVar.setMyStringPref(context,Constant.Filter_priceMin,strEmpty);
        GlobalVar.setMyStringPref(context,Constant.Filter_Rating,strEmpty);

        GlobalVar.setMyBooleanPref(context,Constant.Filter_isshowfavorite,flag);
        GlobalVar.setMyBooleanPref(context,Constant.Filter_isTag_entry,flag);
        GlobalVar.setMyBooleanPref(context,Constant.Filter_isTag_main,flag);
        GlobalVar.setMyBooleanPref(context,Constant.Filter_isTag_dessert,flag);
        GlobalVar.setMyBooleanPref(context,Constant.Filter_isTag_drink,flag);
        GlobalVar.setMyBooleanPref(context,Constant.Filter_isTag_vegeterian,flag);
        GlobalVar.setMyBooleanPref(context,Constant.Filter_isTag_vegen,flag);
        GlobalVar.setMyBooleanPref(context,Constant.Filter_isTag_gluten,flag);

        GlobalVar.setMyStringPref(context,Constant.Filter_selecteddone,strEmpty);

        //Min and Max price
        GlobalVar.setMyIntPref(context,Constant.Filter_MinPrice,0);
        GlobalVar.setMyIntPref(context,Constant.Filter_MaxPrice,100);

        GlobalVar.setMyBooleanPref(context,Constant.Setting_Refresh,flag);


    }


    //Set - Refresh Shared preference value
    public static void Savedataoffilter(Context context,String strSort,String strPricemax,String strPricemin,String strRating,
                                        boolean isShow_fav,
                                        boolean isTag_entry,boolean isTag_main,boolean isTag_desert,boolean isTag_drink,
                                        boolean isTag_vegeterian,boolean isTag_veg,boolean isTag_gluten,String strFulldata,String selecteddistance,String strtag){

        GlobalVar.setMyStringPref(context,Constant.Filter_sortby,strSort);
        GlobalVar.setMyStringPref(context,Constant.Filter_priceMax,strPricemax);
        GlobalVar.setMyStringPref(context,Constant.Filter_priceMin,strPricemin);
        GlobalVar.setMyStringPref(context,Constant.Filter_Rating,strRating);
        GlobalVar.setMyStringPref(context,Constant.Filter_selectedDistance,selecteddistance);

        GlobalVar.setMyBooleanPref(context,Constant.Filter_isshowfavorite,isShow_fav);
        GlobalVar.setMyBooleanPref(context,Constant.Filter_isTag_entry,isTag_entry);
        GlobalVar.setMyBooleanPref(context,Constant.Filter_isTag_main,isTag_main);
        GlobalVar.setMyBooleanPref(context,Constant.Filter_isTag_dessert,isTag_desert);
        GlobalVar.setMyBooleanPref(context,Constant.Filter_isTag_drink,isTag_drink);
        GlobalVar.setMyBooleanPref(context,Constant.Filter_isTag_vegeterian,isTag_vegeterian);
        GlobalVar.setMyBooleanPref(context,Constant.Filter_isTag_vegen,isTag_veg);
        GlobalVar.setMyBooleanPref(context,Constant.Filter_isTag_gluten,isTag_gluten);

        GlobalVar.setMyStringPref(context,Constant.Filter_selecteddone,strFulldata);
        GlobalVar.setMyStringPref(context,Constant.Filter_selecteddone1,strtag);

    }


    //Check - Is null?
    public static String Isnull(String str){

        if(str == null || str == "null" || str.toLowerCase().equalsIgnoreCase("null")){
            str="";
        }

        return str;
    }


    //Set - current location latitude and longitude in Shared preference value
    public static void SetsearchLocationParameter(Context context,String Latitude,String Longitude){

        GlobalVar.setMyStringPref(context,Constant.Search_Latitude,Latitude);
        GlobalVar.setMyStringPref(context,Constant.Search_Longitude,Longitude);

    }


    //Count - Calculate distance
    public static String CalculateDistance(String strDestLatitude,String strDestLongitude,Context context){

        String strDistance="";

        try {
            Location mylocation = new Location("");
            Location dest_location = new Location("");

            Double my_loc_lat = 0.00;
            Double my_loc_long = 0.00;


            String strMylat = GlobalVar.getMyStringPref(context, Constant.Search_Latitude);
            String strMylog = GlobalVar.getMyStringPref(context, Constant.Search_Longitude);

            if (!Generalfunction.isCompare(strMylat, "0.0") || !Generalfunction.isEmptyCheck(strMylat)) {
                my_loc_lat = Double.parseDouble(strMylat);
                my_loc_long = Double.parseDouble(strMylog);
            }

            if (Generalfunction.isCompare(strDestLatitude, "0.0") || Generalfunction.isEmptyCheck(strDestLatitude)) {
                strDestLatitude = "-37.801900";
                strDestLongitude = "144.988150";
            }

            Double dest_loc_lat = Double.parseDouble(strDestLatitude);
            Double dest_loc_long = Double.parseDouble(strDestLongitude);

            mylocation.setLatitude(my_loc_lat);
            mylocation.setLongitude(my_loc_long);

            dest_location.setLatitude(dest_loc_lat);
            dest_location.setLongitude(dest_loc_long);
            //float distance = mylocation.distanceTo(dest_location);//in meters
            float distance = mylocation.distanceTo(dest_location) / 1000;  //in km

            strDistance = Integer.toString((int) distance);

            Log.d(Constant.TAG, "CalculateDistance : " + strDistance + " myLat: " + my_loc_lat + " my Log: " + my_loc_long + " Dest Lat: " + dest_loc_lat + " Dest Log: " + dest_loc_long);
        }
        catch(Exception e){
            e.printStackTrace();
        }

        return strDistance;

    }


  /*  //Filter which use for all screen*/

    //Filter - Filter of Sort by rating

    //Sort average rating
    public static Comparator<DishModel> SortComparatorRating = new Comparator<DishModel>() {
        @Override
        public int compare(DishModel d1, DishModel d2) {
            String Sort1 = d1.rating;
            String Sort2 = d2.rating;

            //decending order
            return Sort2.compareTo(Sort1);
        }
    };

    //Filter - Filter of Sort by price
    public static Comparator<DishModel> SortComparatorPrice = new Comparator<DishModel>() {
        @Override
        public int compare(DishModel d1, DishModel d2) {
            Float Sort1 = Float.valueOf(d1.price);
            Float Sort2 = Float.valueOf(d2.price);
            int result = Float.compare(Sort1, Sort2);

            return result;
        }
    };


    //Filter - Filter of Sort by distance
    public static Comparator<DishModel> SortComparatorDistance = new Comparator<DishModel>() {
        @Override
        public int compare(DishModel d1, DishModel d2) {
            String Sort1 = d1.distance;
            String Sort2 = d2.distance;

            //ascending order
            return Sort1.compareTo(Sort2); //small to big
            /*//decending order
            return Sort2.compareTo(Sort1);*/    //big to small value
        }
    };



    //Filter - show favorite first
    public static ArrayList<DishModel> ShowFavoritefirstFilter(ArrayList<DishModel> Dishlist) {

        ArrayList<DishModel> arrayfavorite = new ArrayList<>();
        ArrayList<DishModel> arrayNotfavorite = new ArrayList<>();

        for (DishModel d : Dishlist) {
            if (d.favourited_by) {
                arrayfavorite.add(d);
            } else {
                arrayNotfavorite.add(d);
            }
        }

        Dishlist = new ArrayList<>();
        Dishlist = arrayfavorite;

        if(arrayNotfavorite.size()>0) {
            Dishlist.addAll(arrayNotfavorite);
        }

        return Dishlist;

    }


    //Filter - Max and min price filter
    public static ArrayList<DishModel> PriceFilter(ArrayList<DishModel> Dishlist,Context mContext) {

        ArrayList<DishModel> arrayfilter = new ArrayList<>();

        Double maxprice = Double.parseDouble(GlobalVar.getMyStringPref(mContext, Constant.Filter_priceMax));
        Double minprice = Double.parseDouble(GlobalVar.getMyStringPref(mContext, Constant.Filter_priceMin));

        for (DishModel d : Dishlist) {

            if (!Generalfunction.isEmptyCheck(d.price)) {

                Double dishPrice = Double.parseDouble(d.price);

                if (dishPrice >= minprice && dishPrice <= maxprice) {
                    arrayfilter.add(d);
                }
            }
        }
        if(arrayfilter.size()>0) {
            Collections.sort(arrayfilter, SortComparatorPrice);
        }
        //Collections.reverse(arrayfilter);
        return arrayfilter;
    }


    //Filter - rating
    public static ArrayList<DishModel> RatingFilter(ArrayList<DishModel> Dishlist,Context mContext) {

        ArrayList<DishModel> arrayfilter = new ArrayList<>();
        Double dRating = Double.parseDouble(GlobalVar.getMyStringPref(mContext, Constant.Filter_Rating));

        for (DishModel d : Dishlist) {

            if (!Generalfunction.isEmptyCheck(d.rating)) {
                if (Double.parseDouble(d.rating) >= dRating) {
                    arrayfilter.add(d);
                }
            }
        }

        return arrayfilter;
    }


    //Get File - Save bitmap as a file
    public static File SaveBitmap(Bitmap bitmap){
        File file;
        String file_path = Constant.Filepath_Image;
        File dir = new File(file_path);
        if(!dir.exists())
            dir.mkdirs();
        file = new File(dir, "images");
        Generalfunction.DisplayLog("SaveBitmap: File: "+file);
        FileOutputStream fOut;
        try {
            fOut = new FileOutputStream(file);
            bitmap.compress(Bitmap.CompressFormat.PNG, 85, fOut);
            fOut.flush();
            fOut.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return file;
    }


    //Delete - delete file
    public static void DeletefIle(File fileOrDirectory ){

        try {
            if (fileOrDirectory.isDirectory()) {
                for (File child : fileOrDirectory.listFiles()) {
                    DeletefIle(child);
                }
            }
            fileOrDirectory.delete();
            Generalfunction.DisplayLog("delete file:" + fileOrDirectory);
        }
        catch(Exception e){}
    }


    //Set - Toolbar tital style for all screen
    public static void SetToolbartitalstyle(Context context, TextView textView,boolean flagWhite){

        if(flagWhite){
            textView.setTextColor(context.getResources().getColor(R.color.white));
        }
        else{
            textView.setTextColor(context.getResources().getColor(R.color.colorTitleText));
        }

        textView.setLetterSpacing((float) 0.1);
//        textView.setPadding(0,15,0,0);

        if(textView.getText().toString().length() > 15){
            textView.setTextSize(TypedValue.COMPLEX_UNIT_PX,context.getResources().getDimension(R.dimen.headercentertextsize));
        }
        else{
            textView.setTextSize(TypedValue.COMPLEX_UNIT_PX,context.getResources().getDimension(R.dimen.ToolbarTiltle_textsize));
        }

        Typeface lato = Typeface.createFromAsset(context.getAssets(), "fonts/lato.ttf");
        textView.setTypeface(lato);
    }


    //Make call - call with Phone number
    public static void RestaurantCall(String strPhoneNumber,Context context){

        if(!Generalfunction.isEmptyCheck(strPhoneNumber)) {
            if (ActivityCompat.checkSelfPermission(context, Manifest.permission.CALL_PHONE)!= PackageManager.PERMISSION_GRANTED) {
                // Request missing location permission.
                ActivityCompat.requestPermissions(((Activity)context),new String[]{Manifest.permission.CALL_PHONE},Constant.CallPERMISSION_REQUEST_CODE);
            } else {
                Generalfunction.DisplayLog("RestaurantCall action: "+strPhoneNumber);
                Intent intent = new Intent(Intent.ACTION_DIAL);
                intent.setData(Uri.parse("tel:" + strPhoneNumber));
                context.startActivity(intent);
            }
        }
        else{
            String message="Looks like this place doesn't have a phone number";
            DisplayMessage(message,context);
        }
    }
    public static void DisplayMessage(String strMessage,final Context context) {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);
        alertDialogBuilder.setTitle(context.getResources().getString(R.string.app_name));
        alertDialogBuilder.setMessage(strMessage);
        alertDialogBuilder.setCancelable(false);

        alertDialogBuilder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

               dialog.dismiss();
            }
        });
        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }

    //Display - Display grey Phone icon according to value
    public static void DisplayCall_image(ImageView imageviewCall,String strPhone,Context context){

        if(Generalfunction.isEmptyCheck(strPhone)){
            imageviewCall.setImageDrawable(context.getResources().getDrawable(R.mipmap.nocall, context.getTheme()));
        }
        else{
            imageviewCall.setImageDrawable(context.getResources().getDrawable(R.mipmap.phone, context.getTheme()));
        }

    }

    //Display - Display white Phone icon according to value
    public static void DisplayCall_image_white(ImageView imageviewCall,String strPhone,Context context){

        if(Generalfunction.isEmptyCheck(strPhone)){
            imageviewCall.setImageDrawable(context.getResources().getDrawable(R.mipmap.nocall_white, context.getTheme()));
        }
        else{
            imageviewCall.setImageDrawable(context.getResources().getDrawable(R.mipmap.phone_white_action, context.getTheme()));
        }

    }


    //Set - Dish min and max price
    public static void SaveFilterMin_MaxPrice(Context context,int minprice,int maxprice){
        GlobalVar.setMyIntPref(context,Constant.Filter_MinPrice,minprice);
        GlobalVar.setMyIntPref(context,Constant.Filter_MaxPrice,maxprice);
    }


    //Set - Refresh value
    public static void RefreshScreenValue(Context thisContext){
        GlobalVar.setMyBooleanPref(thisContext, Constant.Isquickpost, false);
        GlobalVar.setMyBooleanPref(thisContext,Constant.Filter_FollowRefresh,false);
        GlobalVar.setMyBooleanPref(thisContext,Constant.Filter_FavouriteRefresh,false);
        GlobalVar.setMyStringPref(thisContext, Constant.Quick_Dishimage, "");
    }


    /**
     * Use this method to colorize toolbar icons to the desired target color
     * @param toolbarView toolbar view being colored
     * @param toolbarIconsColor the target color of toolbar icons
     * @param activity reference to activity needed to register observers
     */
    public static void colorizeToolbar(Toolbar toolbarView, int toolbarIconsColor, Activity activity) {

        final PorterDuffColorFilter colorFilter= new PorterDuffColorFilter(toolbarIconsColor, PorterDuff.Mode.MULTIPLY);

        for(int i = 0; i < toolbarView.getChildCount(); i++) {
            final View v = toolbarView.getChildAt(i);

            //Step 1 : Changing the color of back button (or open drawer button).
            if(v instanceof ImageButton) {
                //Action Bar back button
                Drawable drawable=((ImageButton)v).getDrawable();
                drawable.setColorFilter(toolbarIconsColor, PorterDuff.Mode.SRC_ATOP);
                toolbarView.setNavigationIcon(drawable);
            }
        }
    }


    //Set - Font(Text) style
    public static void loadFont(Context  context, TextView view, String fontName) {
        Typeface tf = Typeface.createFromAsset(context.getAssets(), "fonts/" + fontName);
        view.setTypeface(tf);
    }


    //Check - Is null?
    public static String IsnullRating(String str){
        if(str==null || str=="null" || str.toLowerCase().equalsIgnoreCase("null")){
            str="";
        }

        if(Generalfunction.isEmptyCheck(str)){
            str = "0";
        }

        return str;
    }


    //Control Permission

    public static void HandleApppermission(int requestCode,String[] permissions, int[] grantResults, final Context context) {

        Generalfunction.DisplayLog("onRequest permssiom "+requestCode);

        if (requestCode == Constant.PERMISSION_REQUEST_CODE_Location) {
            for (int i = 0, len = permissions.length; i < len; i++) {
                if (grantResults[i] == PackageManager.PERMISSION_DENIED) {
                    Generalfunction.Simple1ButtonDialogApppermission(Constant.LOCATION, context);
                }
            }
        } else if (requestCode == Constant.CallPERMISSION_REQUEST_CODE) {
            for (int i = 0, len = permissions.length; i < len; i++) {
                if (grantResults[i] == PackageManager.PERMISSION_DENIED) {
                    Generalfunction.Simple1ButtonDialogApppermission(Constant.PHONE, context);
                }
            }
        }
        else if (requestCode == Constant.PERMISSION_REQUEST_CODE_Camera) {
            for (int i = 0, len = permissions.length; i < len; i++) {
                if (grantResults[i] == PackageManager.PERMISSION_DENIED) {
                    Generalfunction.Simple1ButtonDialogApppermission(Constant.CAMERA, context);
                }
            }
        }
        else if (requestCode == Constant.PERMISSION_REQUEST_CODE_STORAGE) {
            for (int i = 0, len = permissions.length; i < len; i++) {
                if (grantResults[i] == PackageManager.PERMISSION_DENIED) {
                    Generalfunction.Simple1ButtonDialogApppermission(Constant.STORAGE, context);
                }
            }
        }


    }


    public static void Simple1ButtonDialogApppermission(final String strMessage, final Context context){

        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);
        alertDialogBuilder.setTitle(context.getResources().getString(R.string.app_name));
        alertDialogBuilder.setMessage("Please allow permission for  "+strMessage.toUpperCase()+". without this permission the app is unable to use "+ strMessage.toLowerCase()+" functionality");

        alertDialogBuilder.setPositiveButton("Ok",new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                alertDialog.dismiss();
                Intent intent = new Intent();
                intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                Uri uri = Uri.fromParts("package", context.getPackageName(), null);
                intent.setData(uri);
                context.startActivity(intent);
            }
        });


        alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }

}
