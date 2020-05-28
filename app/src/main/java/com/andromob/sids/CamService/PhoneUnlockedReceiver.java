package com.andromob.sids.CamService;
/*
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;



public class PhoneUnlockedReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.d("PhoneUnlockedReceiver", "onReceive action");

        if (intent.getAction().equals(Intent.ACTION_USER_PRESENT)){

            Log.d("asdaxxx", "User action present");

        }else if (intent.getAction().equals(Intent.ACTION_SCREEN_OFF)){
            Log.d("asdaxxx", "Phone locked");
        }
        else if (intent.getAction().equals(Intent.ACTION_SCREEN_ON)){
            Log.d("asdaxxx", "Power On");
            //Toast.makeText(context,"Power On",Toast.LENGTH_SHORT).show();
           /* CameraManager mgr = new CameraManager(context);
            mgr.takePhoto();
            Toast.makeText(context,"Photo saved to Pictures\\iSelfie", Toast.LENGTH_SHORT).show();*/
/*
        }
    }
}
*/