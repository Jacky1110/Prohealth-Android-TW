package com.v7idea.healthkit.Model;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;
import androidx.core.content.FileProvider;
import android.util.Log;


import com.v7idea.healthkit.BuildConfig;

import java.io.File;
import java.io.IOException;
import java.util.List;

/**
 * Created by mortal on 16/9/2.
 */
public class PhotoManager
{
    private final static int select_photo = 1000;
    private final static int take_photo = 0;

    // your authority, must be the same as in your manifest file
    private static String CAPTURE_IMAGE_FILE_PROVIDER = "";

    private static final String TAG = PhotoManager.class.getSimpleName();

    private MediaScannerConnection mConn = null;
    private ScannerClient mClient = null;
    private File mFile = null;
    private String mMimeType = null;

    public PhotoManager(Activity activity)
    {
        CAPTURE_IMAGE_FILE_PROVIDER = BuildConfig.APPLICATION_ID  + ".fileprovider";
    }

    public void takePhoto(Activity activity)
    {
//        String strFilePath = Environment.getExternalStorageDirectory() + "/" + Environment.DIRECTORY_DCIM;

        File path = new File(Environment.getExternalStorageDirectory(), "DCIM/NirPlus");
        if (!path.exists()) path.mkdirs();

        File image = new File(path, "image.jpg");

        Uri imageUri = FileProvider.getUriForFile(activity, CAPTURE_IMAGE_FILE_PROVIDER, image);

        String imageURIString = imageUri.toString();

        Log.e(TAG, "imageURIString: "+imageURIString);

        Intent intent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            intent.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
        } else {
            List<ResolveInfo> resInfoList =
                    activity.getPackageManager()
                            .queryIntentActivities(intent, PackageManager.MATCH_DEFAULT_ONLY);

            for (ResolveInfo resolveInfo : resInfoList) {
                String packageName = resolveInfo.activityInfo.packageName;

                Log.e(TAG, "packageName: "+packageName);

                activity.grantUriPermission(packageName, imageUri,
                        Intent.FLAG_GRANT_WRITE_URI_PERMISSION |
                                Intent.FLAG_GRANT_READ_URI_PERMISSION);
            }
        }

        activity.startActivityForResult(intent, take_photo);
    }


    public File getTmpImageFile(){
        File path = new File(Environment.getExternalStorageDirectory(), "DCIM/NirPlus");
        if (!path.exists()) path.mkdirs();
        return new File(path, "image.jpg");
    }

    public Bitmap onActivityResultHandlerImage()
    {
        File path = new File(Environment.getExternalStorageDirectory(), "DCIM/NirPlus");
        if (!path.exists()) path.mkdirs();
        File imageFile = new File(path, "image.jpg");

        Bitmap sourceBitmap = BitmapFactory.decodeFile(imageFile.getAbsolutePath(), null);

        try
        {
            ExifInterface exif = new ExifInterface(imageFile.getAbsolutePath());

            int rotation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);

            int rotationInDegrees = exifToDegrees(rotation);

            Matrix matrix = new Matrix();
            if (rotation != 0f) {matrix.preRotate(rotationInDegrees);}

            return Bitmap.createBitmap(sourceBitmap, 0, 0, sourceBitmap.getWidth(), sourceBitmap.getHeight(), matrix, true);

        } catch (IOException e) {
            e.printStackTrace();

            return sourceBitmap;
        }
    }

    private static int exifToDegrees(int exifOrientation) {
        if (exifOrientation == ExifInterface.ORIENTATION_ROTATE_90) { return 90; }
        else if (exifOrientation == ExifInterface.ORIENTATION_ROTATE_180) {  return 180; }
        else if (exifOrientation == ExifInterface.ORIENTATION_ROTATE_270) {  return 270; }
        return 0;
    }

    public PhotoManager(Context context) {
        if (mClient == null) {
            mClient = new ScannerClient();
        }
        if (mConn == null) {
            mConn = new MediaScannerConnection(context, mClient);
        }
    }

    private class ScannerClient implements
            MediaScannerConnection.MediaScannerConnectionClient {

        public void onMediaScannerConnected() {

            if (mFile == null) {
                return;
            }
            scan(mFile, mMimeType);
        }

        public void onScanCompleted(String path, Uri uri) {
            mConn.disconnect();
        }

        private void scan(File file, String type) {
            if (file.isFile()) {
                mConn.scanFile(file.getAbsolutePath(), null);
                return;
            }
            File[] files = file.listFiles();
            if (files == null) {
                return;
            }
            for (File f : file.listFiles()) {
                scan(f, type);
            }
        }
    }

    public void scanFile(File file, String mimeType) {
        mFile = file;
        mMimeType = mimeType;
        mConn.connect();
    }
}
