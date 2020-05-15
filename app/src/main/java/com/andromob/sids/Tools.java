package com.andromob.sids;

import android.Manifest;
import android.app.Activity;
import android.app.ActivityManager;
import android.app.admin.DevicePolicyManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Handler;
import android.provider.MediaStore;
import android.telephony.SmsManager;
import android.text.TextUtils;
import android.util.Log;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;

import com.andromob.sids.CamService.CameraManager;
import com.andromob.sids.CamService.CameraService;
import com.klinker.android.send_message.Message;
import com.klinker.android.send_message.Settings;
import com.klinker.android.send_message.Transaction;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import static android.content.Context.MODE_PRIVATE;
import static androidx.constraintlayout.widget.Constraints.TAG;

/**
 * Created by andromob on 26/04/14.
 */
public class Tools {

    public static String app_name ="SIDS";
    public static String smsMessage ="Someone try to unlock your phone";
    public static String packageName ="com.andromob.sids";
    public static Double lastlatitude, lastlongitude;
    public static boolean waitDetectLocation = false;
    public static String filepath = null;
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

    //get last location of user location
    public static void getlastLocation(Context context)
    {
        Intent i = new Intent(context, LocationService.class);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            context.startForegroundService(i);
        } else {
            context.startService(i);
        }
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



                long delay = 5000;
                Timer timer =  new Timer();
                TimerTask timerTask = new TimerTask() {
                    @Override
                    public void run() {

                        Log.d("Tools", "-- TimerTask start --");
                        Tools.filepath = "/storage/emulated/0/Pictures/SIDS/SIDS20200511071117.jpg";
                        if(Tools.filepath != null)
                        {
                            Log.d("Tools", "Tools.filepath = " + Tools.filepath);
                            Settings settings = new Settings();
                            settings.setUseSystemSending(true);
                            Transaction transaction  = new Transaction(context, settings);
                            Message messagemms = new Message("hello MMS", "23028502");

                            Uri imageUri = Uri.fromFile(new File(Tools.filepath));
                            Bitmap bitmap= null;

                            try {
                                bitmap = MediaStore.Images.Media.getBitmap(context.getContentResolver(), imageUri);
                            } catch (IOException e) {
                                e.printStackTrace();
                                Toast.makeText(context, "-- catch bitmap --", Toast.LENGTH_LONG).show();
                                Log.d("Tools", e.getMessage());
                            }

                            if(bitmap != null)
                            {
                                messagemms.setImage(bitmap);
                                transaction.sendNewMessage(messagemms, Transaction.NO_THREAD_ID);
                            }
                            else
                            {
                                Log.d("Tools", "Bitmap is null !!");
                                Toast.makeText(context, "Bitmap is null ", Toast.LENGTH_LONG).show();
                            }

                        }
                        else
                        {
                            Toast.makeText(context, "-- failed to send mms filePath of image is null--", Toast.LENGTH_LONG).show();
                            Log.d("Tools", "failed to send mms filePath of image is null !!!");
                        }

                        Log.d("Tools", "-- TimerTask end --");
                        Toast.makeText(context, "-- TimerTask end --", Toast.LENGTH_LONG).show();
                    }
                };
                timer.schedule(timerTask,delay);







                /*Uri contentUri = null;
                    contentUri = Uri.fromFile(new File(Tools.filepath));

                if (contentUri != null) {
                    smsManager.sendMultimediaMessage(context,
                            contentUri, null, null,
                            null);
                } else {

                        Log.d(TAG, "Uri is null to send multimedia !!!");

                }*/
                Log.d("Tools", "message is sended with success");
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
    public static void waitLocationThenSendSms(final Context context, final String PhoneNumber) {

        if (!waitDetectLocation) {

            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    Log.d("Tools", "waitLocationThenSendSms() / postDelayed");
                    waitLocationThenSendSms(context,PhoneNumber);
                }
            }, 4000);

        }
        else
        {


            long delay = 5000;
            Timer timer =  new Timer();
            TimerTask timerTask = new TimerTask() {
                @Override
                public void run() {

                    final SharedPreferences sharedPreferences = context.getSharedPreferences(String.valueOf(R.string.shared_preferences_settings), MODE_PRIVATE);
                    String lat = sharedPreferences.getString(String.valueOf(R.string.key_last_latitude), "null");
                    String lon = sharedPreferences.getString(String.valueOf(R.string.key_last_longitude), "null");

                    String Latitude = "Latitude = " + lat;
                    String Longitude = "Longitude = " + lon;

                    final String PhoneModelName = android.os.Build.MODEL;
                    final String message = Tools.smsMessage + " " + PhoneModelName + " !!!"
                            + "\n the last detected location : "
                            + "\n   - " + Latitude
                            + "\n   - " + Longitude
                            + "\n http://maps.google.com/maps?q="  + lat + "," + lon + "&iwloc=A";


                    Tools.SendMessage(context,PhoneNumber,message);
                    Log.d(TAG, message);
                    Log.d("Tools", "waitLocationThenSendSms() / send msg");
                }
            };
            timer.schedule(timerTask,delay);

        }
    }
}
