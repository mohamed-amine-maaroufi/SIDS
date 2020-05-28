package com.andromob.sids;


import android.annotation.SuppressLint;
import android.app.admin.DeviceAdminReceiver;
import android.app.admin.DevicePolicyManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.util.Log;
import android.widget.Toast;

import static android.content.Context.MODE_PRIVATE;


/**
 * Created by andromob on 26/04/14.
 */
public class DeviceAdminSampleReceiver extends DeviceAdminReceiver {
    private final String TAG = "DeviceAdminSampleReceiver";

    protected Location locationManager;
    protected LocationListener locationListener;

    @SuppressLint("LongLogTag")
    /** Called when this application is approved to be a device administrator. */
    @Override
    public void onEnabled(Context context, Intent intent) {
        super.onEnabled(context, intent);
        Toast.makeText(context, R.string.device_admin_enabled,
                Toast.LENGTH_LONG).show();

        Log.d(TAG, "onEnabled");
    }
    @SuppressLint("LongLogTag")
    /** Called when this application is no longer the device administrator. */
    @Override
    public void onDisabled(Context context, Intent intent) {
        super.onDisabled(context, intent);
        Toast.makeText(context, R.string.device_admin_disabled,
                Toast.LENGTH_LONG).show();
        Log.d(TAG, "onDisabled");
    }
    @SuppressLint("LongLogTag")
    @Override
    public void onPasswordChanged(Context context, Intent intent) {
        super.onPasswordChanged(context, intent);
        Log.d(TAG, "onPasswordChanged");
    }

    @SuppressLint("LongLogTag")
    @Override
    public void onPasswordFailed(final Context context, Intent intent) {
        super.onPasswordFailed(context, intent);

        Log.d(TAG, "onPasswordFailed");

        int attempts =0;
        DevicePolicyManager DevicePolicyManager=
                (DevicePolicyManager)context.getSystemService(Context.DEVICE_POLICY_SERVICE);
        if(DevicePolicyManager != null)
            attempts = DevicePolicyManager.getCurrentFailedPasswordAttempts();

        // Storing data into SharedPreferences
        final SharedPreferences sharedPreferences = context.getSharedPreferences(String.valueOf(R.string.shared_preferences_settings), MODE_PRIVATE);
        SharedPreferences.Editor myEdit = sharedPreferences.edit();

       //get the configured number of attempts to take selfie
        int configuredNumberAttempts = Integer.parseInt(sharedPreferences.getString(String.valueOf(R.string.key_configured_number_attempts), "1"));

        //get the totol number of failed attempts
        int oldTotalNumberAttempts = Integer.parseInt(sharedPreferences.getString(String.valueOf(R.string.key_total_failed_attempts), "0"));

        //increment the total number of failed attempts + 1
        myEdit.putString(String.valueOf(R.string.key_total_failed_attempts), String.valueOf(oldTotalNumberAttempts + 1));
        myEdit.commit();

        //int NumberPicToTake = Integer.parseInt(sharedPreferences.getString(String.valueOf(R.string.key_number_pictures), "1"));
        String BehaviorCam = sharedPreferences.getString(String.valueOf(R.string.key_detection_behavior), String.valueOf(R.string.fail_unlock));
        final String PhoneNumber = sharedPreferences.getString(String.valueOf(R.string.key_phone_number), "");
        boolean isSmsEnabled = sharedPreferences.getBoolean(String.valueOf(R.string.key_is_sms_enabled), true);


        Log.d(TAG, "BehaviorCam = " + BehaviorCam);
        //Log.d(TAG, "NumberPicToTake = " + NumberPicToTake);
        Log.d(TAG, "configuredNumberAttempts = " + configuredNumberAttempts);





        if(BehaviorCam.equals("Fail Unlock") || BehaviorCam.equals("Always"))
        {
            if(attempts >= configuredNumberAttempts)
            {
                if(Tools.isPermissionGranted(context))
                {
                    Tools.TakeSelfie(context,1);
                    if(isSmsEnabled)
                    {

                        AppLocationService appLocationService = new AppLocationService(context);
                        Location gpsLocation = appLocationService
                                .getLocation(LocationManager.GPS_PROVIDER);
                        if (gpsLocation != null) {

                            String result = "Latitude: " + gpsLocation.getLatitude() +
                                    " Longitude: " + gpsLocation.getLongitude();
                            Log.d(TAG, "resulat location = " + result);



                            final String PhoneModelName = android.os.Build.MODEL;
                            final String message = Tools.smsMessage + " " + PhoneModelName + " !!!"
                                    + "\n the last detected location : "
                                    + "\n   - " + result;
                                   // + "\n http://maps.google.com/maps?q="  + latitude + "," + longitude + "&iwloc=A";


                            Tools.SendMessage(context,PhoneNumber,message);
                            Log.d(TAG, message);


                        }


                    }
                }else
                {
                    Log.d(TAG, "Permission not granted !!!");
                }

            }
            else{
                Log.d(TAG, "NumberAttempts not correct for onPasswordFailed ");
            }
        }
        else{
            Log.d(TAG, "behavior not correct for onPasswordFailed ");
        }
    }

    @SuppressLint("LongLogTag")
    @Override
    public void onPasswordSucceeded(Context context, Intent intent) {
        super.onPasswordSucceeded(context, intent);
        Log.d(TAG, "onPasswordSucceeded");


        //Capture intruder with selfie camera
        SharedPreferences sharedPreferences = context.getSharedPreferences(String.valueOf(R.string.shared_preferences_settings), MODE_PRIVATE);
        //int NumberPicToTake = Integer.parseInt(sharedPreferences.getString(String.valueOf(R.string.key_number_pictures), "1"));
        String BehaviorCam = sharedPreferences.getString(String.valueOf(R.string.key_detection_behavior), String.valueOf(R.string.fail_unlock));
        String PhoneNumber = sharedPreferences.getString(String.valueOf(R.string.key_phone_number), "");
        boolean isSmsEnabled = sharedPreferences.getBoolean(String.valueOf(R.string.key_is_sms_enabled), true);


        Log.d(TAG, "BehaviorCam = " + BehaviorCam);
        //Log.d(TAG, "NumberPicToTake = " + NumberPicToTake);


        if (BehaviorCam.equals("Success Unlock") || BehaviorCam.equals("Always")) {
            if (Tools.isPermissionGranted(context)) {
                Tools.TakeSelfie(context, 1);
                if (isSmsEnabled) {
                    AppLocationService appLocationService = new AppLocationService(context);
                    Location gpsLocation = appLocationService
                            .getLocation(LocationManager.GPS_PROVIDER);
                    if (gpsLocation != null) {
                        double latitude = gpsLocation.getLatitude();
                        double longitude = gpsLocation.getLongitude();
                        String result = "Latitude: " + gpsLocation.getLatitude() +
                                " Longitude: " + gpsLocation.getLongitude();
                        Log.d(TAG, "resulat location = " + result);


                        final String PhoneModelName = android.os.Build.MODEL;
                        final String message = Tools.smsMessage + " " + PhoneModelName + " !!!"
                                + "\n the last detected location : "
                                + "\n   - " + result;
                        // + "\n http://maps.google.com/maps?q="  + latitude + "," + longitude + "&iwloc=A";


                        Tools.SendMessage(context, PhoneNumber, message);
                        Log.d(TAG, message);


                    }

                } else {
                    Log.d(TAG, "Permission not granted !!!");
                }

            } else {
                Log.d(TAG, "behavior not correct for onPasswordSucceeded ");
            }
        }
    }



}

