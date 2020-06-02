package com.android.sids;

import android.Manifest;
import android.app.Activity;
import android.app.ActivityManager;
import android.app.admin.DevicePolicyManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.os.Handler;
import android.provider.MediaStore;
import android.telephony.SmsManager;
import android.text.TextUtils;
import android.util.Log;
import android.view.Window;
import android.view.WindowManager;

import com.android.sids.CamService.CameraManager;
import com.android.sids.CamService.CameraService;
import com.android.sids.settings.DriveService;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

/**
 * Created by andromob on 26/04/14.
 */
public class Tools {

    public static String app_name ="SIDS";
    public static String smsMessage ="Someone try to unlock your phone";
    public static String packageName ="com.android.sids";
    public static Double lastlatitude, lastlongitude;
    public static boolean waitDetectLocation = false;
    public static String filepath = null;
    public static String fileName = null;
    //make ActionBar of activity transparent
    public static void changeStatusBarColor(Activity activity) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = activity.getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(Color.TRANSPARENT);
        }
    }

    //request permissions for (Camera / stockage)
    public static void requestPermission(Activity activity)
    {
        ActivityCompat.requestPermissions(activity, new String[] {Manifest.permission.CAMERA,
                Manifest.permission.SEND_SMS,
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.ACCESS_FINE_LOCATION },101);

    }


    //Check if permessions for camera and stockage granted /  return true if permession granted ; else return false
    public static  boolean isPermissionGranted(Context context)
    {
        boolean l_bRet = true;
        int cameraPermission = ContextCompat.checkSelfPermission(context, Manifest.permission.CAMERA);
        int writestoragePermission = ContextCompat.checkSelfPermission(context, Manifest.permission.WRITE_EXTERNAL_STORAGE);
        int readstoragePermission = ContextCompat.checkSelfPermission(context, Manifest.permission.READ_EXTERNAL_STORAGE);
        int SmsPermission = ContextCompat.checkSelfPermission(context, Manifest.permission.SEND_SMS);
        int CorseLocationPermission = ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION);
        int FineLocationPermission = ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION);
        if(cameraPermission == PackageManager.PERMISSION_DENIED
                || readstoragePermission == PackageManager.PERMISSION_DENIED
                ||writestoragePermission == PackageManager.PERMISSION_DENIED
                ||FineLocationPermission == PackageManager.PERMISSION_DENIED
                ||CorseLocationPermission == PackageManager.PERMISSION_DENIED
                || SmsPermission == PackageManager.PERMISSION_DENIED)
        {
            l_bRet =  false;
        }
        return l_bRet;
    }


    //Check if permessions for camera and stockage granted /  return true if permession granted ; else return false
    public static  boolean isPermissionGrantedSms(Context context)
    {
        boolean l_bRet = true;

        int SmsPermission = ContextCompat.checkSelfPermission(context, Manifest.permission.SEND_SMS);
        if(SmsPermission == PackageManager.PERMISSION_DENIED)
        {
            l_bRet =  false;
        }
        return l_bRet;
    }

    // Activate device administration
    public static  void requestPermissionAdminDevice(Activity activity, ComponentName componentName)
    {

        Intent intent = new Intent(DevicePolicyManager.ACTION_ADD_DEVICE_ADMIN);
        intent.putExtra(DevicePolicyManager.EXTRA_DEVICE_ADMIN,componentName);
        intent.putExtra(DevicePolicyManager.EXTRA_ADD_EXPLANATION,"To detect failed attempts we need this permissions");
        activity.startActivityForResult(intent, 102);
    }

    //check if the is device Admin / return true if device admin ; else return false
    public static  boolean isAdminDevice(ComponentName componentName, DevicePolicyManager devicePolicyManager)
    {
        boolean l_bRet = false;
        boolean AdminPermission = devicePolicyManager.isAdminActive(componentName);
        if(AdminPermission)
        {
            l_bRet =  true;
        }
        return l_bRet;
    }


   //capture intruder with camera2
    public static void TakeSelfie(Context context,int NumberPicToTake )
    {
        Intent i = new Intent(context, CameraService.class);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            context.startForegroundService(i);
        } else {
            context.startService(i);
        }


        CameraManager mgr = new CameraManager(context);
        mgr.takePhoto();
    }



    //send message
    public static void SendMessage(final Context context, String phoneNumber, String message) {
        String l_sNumber = phoneNumber.trim();
        String l_sMsg = message.trim();
        if (l_sNumber.equals("") || l_sMsg.equals("")){

            Log.d("Tools", "sms Fields (l_sNumber + l_sMsg) cannot be empty");
        } else {
            if (TextUtils.isDigitsOnly(l_sNumber)){
                SmsManager smsManager = SmsManager.getDefault();
                smsManager.sendTextMessage(l_sNumber, null, l_sMsg, null, null);
                Log.d("Tools", "message is sended with success");
                Log.d("Tools", "number of phone = " +phoneNumber);

            } else {
                Log.d("Tools", "Incorrect phone number, failed to send message !!!");
            }
        }
    }

    //to ckeck if app is opened
    public static boolean isAppRunning(final Context context, final String packageName) {
        final ActivityManager activityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        final List<ActivityManager.RunningAppProcessInfo> procInfos = activityManager.getRunningAppProcesses();
        if (procInfos != null)
        {
            for (final ActivityManager.RunningAppProcessInfo processInfo : procInfos) {
                if (processInfo.processName.equals(packageName)) {
                    return true;
                }
            }
        }
        return false;
    }


    //wait to detect location then send sms
    public static void waitThenUploadToDive(final Context context) {

        if (filepath == null) {

            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    Log.d("Tools", "waitThenUploadToDive() / postDelayed");
                    waitThenUploadToDive(context);
                }
            }, 3000);

        }
        else
        {


            long delay = 3000;
            Timer timer =  new Timer();
            TimerTask timerTask = new TimerTask() {
                @Override
                public void run() {

                    Intent i = new Intent(context, DriveService.class);

                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                        context.startForegroundService(i);
                    } else {
                        context.startService(i);
                    }
                    Log.d("Tools", "waitThenUploadToDive() / send msg");
                }
            };
            timer.schedule(timerTask,delay);

        }
    }



    //to compress image
    public static  String compressImage(String imageUri,Context context) {

        String filePath = getRealPathFromURI(imageUri,context);
        Bitmap scaledBitmap = null;

        BitmapFactory.Options options = new BitmapFactory.Options();

//      by setting this field as true, the actual bitmap pixels are not loaded in the memory. Just the bounds are loaded. If
//      you try the use the bitmap here, you will get null.
        options.inJustDecodeBounds = true;
        Bitmap bmp = BitmapFactory.decodeFile(filePath, options);

        int actualHeight = options.outHeight;
        int actualWidth = options.outWidth;

//      max Height and width values of the compressed image is taken as 816x612
        float maxHeight = 816.0f;
        float maxWidth = 612.0f;
        float imgRatio = actualWidth / actualHeight;
        float maxRatio = maxWidth / maxHeight;

//      width and height values are set maintaining the aspect ratio of the image
        if (actualHeight > maxHeight || actualWidth > maxWidth) {
            if (imgRatio < maxRatio) {
                imgRatio = maxHeight / actualHeight;
                actualWidth = (int) (imgRatio * actualWidth);
                actualHeight = (int) maxHeight;
            } else if (imgRatio > maxRatio) {
                imgRatio = maxWidth / actualWidth;
                actualHeight = (int) (imgRatio * actualHeight);
                actualWidth = (int) maxWidth;
            } else {
                actualHeight = (int) maxHeight;
                actualWidth = (int) maxWidth;
            }
        }

//      setting inSampleSize value allows to load a scaled down version of the original image
        options.inSampleSize = calculateInSampleSize(options, actualWidth, actualHeight);

//      inJustDecodeBounds set to false to load the actual bitmap
        options.inJustDecodeBounds = false;

//      this options allow android to claim the bitmap memory if it runs low on memory
        options.inPurgeable = true;
        options.inInputShareable = true;
        options.inTempStorage = new byte[16 * 1024];

        try {
//          load the bitmap from its path
            bmp = BitmapFactory.decodeFile(filePath, options);
        } catch (OutOfMemoryError exception) {
            exception.printStackTrace();

        }
        try {
            scaledBitmap = Bitmap.createBitmap(actualWidth, actualHeight, Bitmap.Config.ARGB_8888);
        } catch (OutOfMemoryError exception) {
            exception.printStackTrace();
        }

        float ratioX = actualWidth / (float) options.outWidth;
        float ratioY = actualHeight / (float) options.outHeight;
        float middleX = actualWidth / 2.0f;
        float middleY = actualHeight / 2.0f;

        Matrix scaleMatrix = new Matrix();
        scaleMatrix.setScale(ratioX, ratioY, middleX, middleY);

        Canvas canvas = new Canvas(scaledBitmap);
        canvas.setMatrix(scaleMatrix);
        canvas.drawBitmap(bmp, middleX - bmp.getWidth() / 2, middleY - bmp.getHeight() / 2, new Paint(Paint.FILTER_BITMAP_FLAG));

//      check the rotation of the image and display it properly
        ExifInterface exif;
        try {
            exif = new ExifInterface(filePath);

            int orientation = exif.getAttributeInt(
                    ExifInterface.TAG_ORIENTATION, 0);
            Log.d("EXIF", "Exif: " + orientation);
            Matrix matrix = new Matrix();
            if (orientation == 6) {
                matrix.postRotate(90);
                Log.d("EXIF", "Exif: " + orientation);
            } else if (orientation == 3) {
                matrix.postRotate(180);
                Log.d("EXIF", "Exif: " + orientation);
            } else if (orientation == 8) {
                matrix.postRotate(270);
                Log.d("EXIF", "Exif: " + orientation);
            }
            scaledBitmap = Bitmap.createBitmap(scaledBitmap, 0, 0,
                    scaledBitmap.getWidth(), scaledBitmap.getHeight(), matrix,
                    true);
        } catch (IOException e) {
            e.printStackTrace();
        }

        FileOutputStream out = null;
        String filename = getFilename();
        try {
            out = new FileOutputStream(filename);

//          write the compressed bitmap at the destination specified by filename.
            scaledBitmap.compress(Bitmap.CompressFormat.JPEG, 80, out);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        Log.d("Tools","Compressed image path = " + filename);
        return filename;
    }

    public static  String getFilename() {
        File sdDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
        //File file = new File(Environment.getExternalStorageDirectory().getPath(), "CompressedSIDS/Images");
        File file = new File(sdDir, Tools.app_name);
        if (!file.exists()) {
            file.mkdirs();
        }
        String uriSting = (file.getAbsolutePath() + "/" + /*System.currentTimeMillis()*/ Tools.fileName );
        return uriSting;
    }


    public static String getRealPathFromURI(String contentURI,Context context) {
        Uri contentUri = Uri.parse(contentURI);
        Cursor cursor = context.getContentResolver().query(contentUri, null, null, null, null);
        if (cursor == null) {
            return contentUri.getPath();
        } else {
            cursor.moveToFirst();
            int index = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA);
            return cursor.getString(index);
        }
    }

    public static int calculateInSampleSize(BitmapFactory.Options options, int reqWidth, int reqHeight) {
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;

        if (height > reqHeight || width > reqWidth) {
            final int heightRatio = Math.round((float) height / (float) reqHeight);
            final int widthRatio = Math.round((float) width / (float) reqWidth);
            inSampleSize = heightRatio < widthRatio ? heightRatio : widthRatio;
        }
        final float totalPixels = width * height;
        final float totalReqPixelsCap = reqWidth * reqHeight * 2;
        while (totalPixels / (inSampleSize * inSampleSize) > totalReqPixelsCap) {
            inSampleSize++;
        }

        return inSampleSize;
    }
}

