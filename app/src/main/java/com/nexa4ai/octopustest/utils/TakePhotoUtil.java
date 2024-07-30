package com.nexa4ai.octopustest.utils;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.ContentValues;
import android.content.Intent;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.widget.Toast;

import androidx.core.content.FileProvider;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class TakePhotoUtil {

    public static final int REQUEST_TAKE_PHOTO = 2091;
    private Activity activity;
    private String currentPhotoPath;

    public TakePhotoUtil(Activity activity) {
        this.activity = activity;
    }

    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = activity.getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",   /* suffix */
                storageDir      /* directory */
        );

        // Save a file: path for use with ACTION_VIEW intents
        currentPhotoPath = image.getAbsolutePath();
        return image;
    }

    public void takePhoto(List<String> functionArguments) {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        // Ensure there's a camera activity to handle the intent
        // Create the File where the photo should go
        File photoFile = null;
        try {
            photoFile = createImageFile();
        } catch (IOException ex) {
            Log.e("TakePhotoUtil", "Failed to create image file", ex);
        }
        // Continue only if the File was successfully created
        if (photoFile != null) {
            Uri photoURI = FileProvider.getUriForFile(activity,
                    activity.getPackageName() + ".fileprovider",
                    photoFile);
            takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
            activity.startActivityForResult(takePictureIntent, REQUEST_TAKE_PHOTO);
        }

    }

    // Getter for the path of the photo that was taken
    public String getCurrentPhotoPath() {
        return currentPhotoPath;
    }

    public void addToGallery() {
        ContentValues values = new ContentValues();
        values.put(MediaStore.Images.Media.DISPLAY_NAME, new File(currentPhotoPath).getName());
        values.put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg");
        values.put(MediaStore.Images.Media.DATE_ADDED, System.currentTimeMillis() / 1000);
        values.put(MediaStore.Images.Media.DATE_TAKEN, System.currentTimeMillis());

        // For Android 10 and above, use the MediaStore.Images.Media.getContentUri
        Uri uri = activity.getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);

        try {
            if (uri != null) {
                OutputStream outputStream = activity.getContentResolver().openOutputStream(uri);
                try {
                    InputStream inputStream = new FileInputStream(currentPhotoPath);
                    if (outputStream != null) {
                        byte[] buf = new byte[1024];
                        int len;
                        while ((len = inputStream.read(buf)) > 0) {
                            outputStream.write(buf, 0, len);
                        }
                    }
                } finally {
                    if (outputStream != null) {
                        outputStream.close();
                    }
                }
            }
        } catch (Exception e) {
            Log.e("TakePhotoUtil", "Failed to copy file to MediaStore", e);
        }

        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_VIEW);
        intent.setType("image/*");
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        try {
            activity.startActivity(intent);
        } catch (ActivityNotFoundException e) {
            Toast.makeText(activity, "No app found to view photos.", Toast.LENGTH_SHORT).show();
        }

    }

}
