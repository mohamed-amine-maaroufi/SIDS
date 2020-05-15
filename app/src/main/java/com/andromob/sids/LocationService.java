package com.andromob.sids;

import android.Manifest;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Criteria;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;

import java.io.IOException;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class LocationService extends Service implements LocationListener {

    LocationManager m_locationManager;

    @Override
    public void onCreate() {
        Log.d("LocationService","--> onCreate() / Location Service starts");

        this.m_locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
       /* Toast.makeText(this, "onCreate()", Toast.LENGTH_LONG)
                .show();*/


    }


    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        //TODO do something useful

        Log.d("LocationService","--> onStartCommand() / Service starts");
        //Toast.makeText(this, "onStartCommand()", Toast.LENGTH_SHORT).show();

        //  Here I offer two options: either you are using satellites or the Wi-Fi services to get user's location
        //  this.m_locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 3000, 0, this); //  User's location is retrieve every 3 seconds
        //this.m_locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        if (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED && checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {

            this.m_locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
            this.m_locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 3000, 0, this);
            //this.m_locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000, 1, this);


        }
        else
        {
            Log.d("LocationService","permission for location not granted");
        }
        return START_STICKY;
    }


    @Override
    public IBinder onBind(Intent intent) {
        //TODO for communication return IBinder implementation
        return null;
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
       // Toast.makeText(getApplicationContext(), "Service Task destroyed", Toast.LENGTH_LONG).show();
        Intent myIntent = new Intent(getApplicationContext(), LocationService.class);

        PendingIntent pendingIntent = PendingIntent.getService(getApplicationContext(), 0, myIntent, 0);

        AlarmManager alarmManager1 = (AlarmManager) getSystemService(ALARM_SERVICE);

        Calendar calendar = Calendar.getInstance();

        calendar.setTimeInMillis(System.currentTimeMillis());

        calendar.add(Calendar.SECOND, 10);

        alarmManager1.set(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pendingIntent);

       // Toast.makeText(getApplicationContext(), "Start Alarm", Toast.LENGTH_SHORT).show();
    }



    @Override
    public void onTaskRemoved(Intent rootIntent) {
        super.onTaskRemoved(rootIntent);
        Intent myIntent = new Intent(getApplicationContext(), LocationService.class);

        PendingIntent pendingIntent = PendingIntent.getService(getApplicationContext(), 0, myIntent, 0);

        AlarmManager alarmManager1 = (AlarmManager) getSystemService(ALARM_SERVICE);

        Calendar calendar = Calendar.getInstance();

        calendar.setTimeInMillis(System.currentTimeMillis());

        calendar.add(Calendar.SECOND, 10);

        alarmManager1.set(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pendingIntent);

       // Toast.makeText(getApplicationContext(), "Start Alarm", Toast.LENGTH_SHORT).show();



    }


    @Override
    public void onLocationChanged(final Location loc) {
        Log.d("LocationService","--> onLocationChanged() / detetct new location");
        //Toast.makeText(this, "onLocationChanged() / detetct new location", Toast.LENGTH_LONG).show();

        int retry = 3;

        if (loc == null) {
            if (retry > 0) {
                retry--;
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        Log.d("LocationService", "--> onLocationChanged() / postDelayed ");
                        onLocationChanged(loc);
                    }
                }, 2000);
            }
        }
        else
        {

            // Storing data into SharedPreferences
            final SharedPreferences sharedPreferences = getSharedPreferences(String.valueOf(R.string.shared_preferences_settings), MODE_PRIVATE);
            SharedPreferences.Editor myEdit = sharedPreferences.edit();

            myEdit.putString(String.valueOf(R.string.key_last_latitude), String.valueOf(loc.getLatitude()));
            myEdit.putString(String.valueOf(R.string.key_last_longitude), String.valueOf(loc.getLongitude()));
            myEdit.commit();

            Tools.waitDetectLocation = true;
            Log.d("LocationService", "--> onLocationChanged() / Latitude = " + loc.getLatitude() + "\nLongitude = " + loc.getLongitude());
        }

    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {

    }


    public Address getAddressForLocation(Context context, Location location) throws IOException {

        if (location == null) {
            return null;
        }
        double latitude = location.getLatitude();
        double longitude = location.getLongitude();
        int maxResults = 1;

        Geocoder gc = new Geocoder(context, Locale.getDefault());
        List<Address> addresses = gc.getFromLocation(latitude, longitude, maxResults);

        if (addresses.size() == 1) {
            return addresses.get(0);
        } else {
            return null;
        }
    }




}