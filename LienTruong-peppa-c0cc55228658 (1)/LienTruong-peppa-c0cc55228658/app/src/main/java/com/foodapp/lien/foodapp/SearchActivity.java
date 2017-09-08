/*
package com.foodapp.lien.foodapp;

import android.content.ComponentName;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Parcelable;
import android.provider.MediaStore;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.style.ScaleXSpan;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class SearchActivity extends AppCompatActivity {

    private static final int CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE = 100;
    Uri selectedImageUri, fileUri;
    String fileName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        TextView greeting = (TextView) findViewById(R.id.greetingTextSearchTv);
        TextView question = (TextView) findViewById(R.id.questionTextSearchTv);
       TextView location = (TextView) findViewById(R.id.locationTextSearchTv);
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);

       Typeface playfair = Typeface.createFromAsset(getAssets(), "fonts/playfair.otf");
       Typeface lato = Typeface.createFromAsset(getAssets(), "fonts/lato.ttf");


       greeting.setTypeface(playfair);
       //question.setText(applyKerning("WHAT DO YOU FEEL LIKE?", 0));
       //question.setText(getResources().getString(R.string.question));
       question.setTypeface(lato);
        location.setTypeface(lato);

        location.setOnClickListener(new View.OnClickListener() {
          @Override
           public void onClick(View view) {

              Intent intent = new Intent(SearchActivity.this, ResultActivity.class);
               startActivity(intent);
          }
      });

      fab.setOnClickListener(new View.OnClickListener() {
            @Override
           public void onClick(View view) {

               openImageIntent();
          }
        });
    }

    public static Spannable applyKerning(CharSequence src, float kerning)
    {
        if (src == null) return null;
        final int srcLength = src.length();
        if (srcLength < 2) return src instanceof Spannable
                ? (Spannable)src
                : new SpannableString(src);

        final String nonBreakingSpace = "\u00A0";
        final SpannableStringBuilder builder = src instanceof SpannableStringBuilder
                ? (SpannableStringBuilder)src
                : new SpannableStringBuilder(src);
        for (int i = src.length() - 1; i >= 1; i--)
        {
            builder.insert(i, nonBreakingSpace);
            builder.setSpan(new ScaleXSpan(kerning), i, i + 1,
                    Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        }

        return builder;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            if (requestCode == CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE) {
                final boolean isCamera;
                if (data == null) {
                    isCamera = true;
                } else {
                    final String action = data.getAction();
                    if (action == null) {
                        isCamera = false;
                    } else {
                        isCamera = action.equals(MediaStore.ACTION_IMAGE_CAPTURE);
                    }
                }

                if (isCamera) {
                    selectedImageUri = fileUri;
                    fileName = getExtension(selectedImageUri.getPath());
                } else {
                    selectedImageUri = data == null? null : data.getData();
                    fileName = "img_"+ System.currentTimeMillis() + ".jpg";
                }

                if (selectedImageUri != null) {
                    Intent intent  = new Intent(SearchActivity.this, PostActivity.class);
                    startActivity(intent);
                }
            }
        }
    }

    private void openImageIntent() {

        // Determine URI of camera image to save.
        final File root = new File(Environment.getExternalStorageDirectory() + File.separator + "FoodApp" + File.separator);
        root.mkdirs();
        String fname = "img_"+ System.currentTimeMillis() + ".jpg";
        final File sdImageMainDirectory = new File(root, fname);
        fileUri = Uri.fromFile(sdImageMainDirectory);

        // Camera.
        final List<Intent> cameraIntents = new ArrayList<Intent>();
        final Intent captureIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
        final PackageManager packageManager = getPackageManager();
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
        galleryIntent.setType("image*/
/*");
        galleryIntent.setAction(Intent.ACTION_GET_CONTENT);

        // Chooser of file System options.
        final Intent chooserIntent = Intent.createChooser(galleryIntent, "Select Source");

        // Add the camera options.
        chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, cameraIntents.toArray(new Parcelable[cameraIntents.size()]));

        startActivityForResult(chooserIntent, CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE);
    }

    public String getExtension(String path) {
        return  path.substring(path.lastIndexOf("/")+1);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_search, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
         int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.actionProfileSearch) {
            Intent intent = new Intent(SearchActivity.this, ProfileActivity.class);
            startActivity(intent);
        }

        return super.onOptionsItemSelected(item);
    }



}
*/
