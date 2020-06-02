package com.android.sids.IntroScreens;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;

import android.app.admin.DevicePolicyManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import com.android.sids.DeviceAdminSampleReceiver;
import com.android.sids.R;
import com.android.sids.Tools;
import com.android.sids.settings.SignInActivity;

import java.util.List;

/**
 * Created by andromob on 26/04/14.
 */
public class AdminPermissionActivity extends AppCompatActivity {
    private static final String TAG = "AdminPermissionActivity" ;
    ConstraintLayout layoutParent;
    private Button button, buttonPermission;
    private ImageView ic_done;

    //Declaration for DeviceAdmin
    DevicePolicyManager devicePolicyManager;
    ComponentName componentNameDeviceAdmin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // making activity full screen
        if (Build.VERSION.SDK_INT >= 21) {
            getWindow().getDecorView()
                    .setSystemUiVisibility(
                            View.SYSTEM_UI_FLAG_LAYOUT_STABLE | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
        }

        setContentView(R.layout.activity_admin_permission);

        // hide action bar you can use NoAction theme as well
        getSupportActionBar().hide();

        // make status bar transparent
        Tools.changeStatusBarColor(this);

        // hide action bar you can use NoAction theme as well
        getSupportActionBar().hide();

        button = findViewById(R.id.button);

        buttonPermission = findViewById(R.id.button_permissions);
        ic_done = findViewById(R.id.ic_done);


        // Initialize Device Policy Manager service and our receiver class
        devicePolicyManager = (DevicePolicyManager) getSystemService(Context.DEVICE_POLICY_SERVICE);
        componentNameDeviceAdmin = new ComponentName(this, DeviceAdminSampleReceiver.class);
        List<ComponentName> activeAdmins = devicePolicyManager.getActiveAdmins();
        if(activeAdmins != null && !activeAdmins.isEmpty()){
            for(int index = 0; index < activeAdmins.size(); index++ ){
                Log.i(TAG, "flattenToShortString: "+ activeAdmins.get(index).flattenToShortString());
                Log.i(TAG, "flattenToString: "+ activeAdmins.get(index).flattenToString());
                Log.i(TAG, "getClassName: "+ activeAdmins.get(index).getClassName());
                Log.i(TAG, "getPackageName: "+ activeAdmins.get(index).getPackageName());
                Log.i(TAG, "getShortClassName: "+ activeAdmins.get(index).getShortClassName());
                Log.i(TAG, "toShortString: "+ activeAdmins.get(index).toShortString());
            }
        }
        else {
            Log.i(TAG, "No Active Device Policy Manager");
        }

        //check permissions (camera / stockage)
        boolean isadmin = Tools.isAdminDevice(componentNameDeviceAdmin, devicePolicyManager);
        Log.d(TAG,"isAdminDevice() = " + isadmin );
        if(isadmin)
        {
            buttonPermission.setVisibility(View.GONE);
            button.setEnabled(true);
            ic_done.setVisibility(View.VISIBLE);
            button.setBackground(ContextCompat.getDrawable(this, R.drawable.ic_button_bg));
        }
        else{

            buttonPermission.setVisibility(View.VISIBLE);
            button.setEnabled(false);
            button.setBackgroundColor(Color.parseColor("#9D9FA2"));
            ic_done.setVisibility(View.GONE);
        }


        //swipe beetween intro screens
        layoutParent = findViewById(R.id.mainContent);
        layoutParent.setOnTouchListener(new OnSwipeTouchListener(AdminPermissionActivity.this) {

            public void onSwipeRight() {
                Intent intent = new Intent(AdminPermissionActivity.this, PermissionActivity.class);
                startActivity(intent);
                //animation transition to left
                overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
            }
        });

        button.setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View view) {
                Intent intent = new Intent(AdminPermissionActivity.this, SignInActivity.class);
                startActivity(intent);
            }
        });

        buttonPermission.setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View view) {
                Tools.requestPermissionAdminDevice(AdminPermissionActivity.this, componentNameDeviceAdmin);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        Log.d(TAG,"resultCode = " + resultCode);

        if (resultCode == -1) {
            Log.d(TAG,"Admin Device granted");
            buttonPermission.setVisibility(View.GONE);
            button.setEnabled(true);
            ic_done.setVisibility(View.VISIBLE);
            button.setBackground(ContextCompat.getDrawable(this, R.drawable.ic_button_bg));
        }
        else{
            Log.d(TAG,"Failed to grant Admin Device ");
        }
    }

    @Override
    public void onBackPressed() {

        Intent intent = new Intent(AdminPermissionActivity.this, PermissionActivity.class);
        startActivity(intent);
        //animation transition to left
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
    }
}
