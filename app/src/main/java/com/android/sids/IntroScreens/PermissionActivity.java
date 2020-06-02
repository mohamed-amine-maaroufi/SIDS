package com.android.sids.IntroScreens;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import com.android.sids.R;
import com.android.sids.Tools;
/**
 * Created by andromob on 26/04/14.
 */
public class PermissionActivity extends AppCompatActivity {

    private static final String TAG = "PermissionActivity";
    ConstraintLayout layoutParent;
    private Button button, buttonPermission;
    private ImageView ic_done;
    private boolean swipeNext = false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // making activity full screen
        if (Build.VERSION.SDK_INT >= 21) {
            getWindow().getDecorView()
                    .setSystemUiVisibility(
                            View.SYSTEM_UI_FLAG_LAYOUT_STABLE | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
        }

        setContentView(R.layout.activity_permission);

        // hide action bar you can use NoAction theme as well
        getSupportActionBar().hide();

        // make status bar transparent
        Tools.changeStatusBarColor(this);


        // hide action bar you can use NoAction theme as well
        getSupportActionBar().hide();

        button = findViewById(R.id.button);
        buttonPermission = findViewById(R.id.button_permissions);
        ic_done = findViewById(R.id.ic_done);

        //check permissions (camera / stockage)
        Log.d(TAG,"isPermissionGranted() = " + Tools.isPermissionGranted(PermissionActivity.this));
        if(Tools.isPermissionGranted(PermissionActivity.this))
        {
            buttonPermission.setVisibility(View.GONE);
            button.setEnabled(true);
            ic_done.setVisibility(View.VISIBLE);
            button.setBackground(ContextCompat.getDrawable(this, R.drawable.ic_button_bg));
            swipeNext = true;
        }
        else{

            buttonPermission.setVisibility(View.VISIBLE);
            button.setEnabled(false);
            button.setBackgroundColor(Color.parseColor("#9D9FA2"));
            ic_done.setVisibility(View.GONE);
            swipeNext = false;
        }

        //swipe beetween intro screens
        layoutParent = findViewById(R.id.mainContent);
        layoutParent.setOnTouchListener(new OnSwipeTouchListener(PermissionActivity.this) {

            public void onSwipeLeft() {
                if(swipeNext)
                {
                    Intent intent = new Intent(PermissionActivity.this, AdminPermissionActivity.class);
                    startActivity(intent);
                    //animation transition to right
                    overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                }
            }

            public void onSwipeRight() {
                Intent intent = new Intent(PermissionActivity.this, WelcomeActivity.class);
                startActivity(intent);
                //animation transition to left
                overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
            }
        });

        button.setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View view) {
                Intent intent = new Intent(PermissionActivity.this, AdminPermissionActivity.class);
                startActivity(intent);
                //animation transition to right
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
            }
        });

        buttonPermission.setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View view) {
                Tools.requestPermission(PermissionActivity.this);
            }
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case 101:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED
                        && grantResults[1] == PackageManager.PERMISSION_GRANTED
                        && grantResults[2] == PackageManager.PERMISSION_GRANTED
                        && grantResults[3] == PackageManager.PERMISSION_GRANTED
                        && grantResults[4] == PackageManager.PERMISSION_GRANTED
                        && grantResults[5] == PackageManager.PERMISSION_GRANTED) {

                    Log.i(TAG, "Permissions granted ");
                    buttonPermission.setVisibility(View.GONE);
                    button.setEnabled(true);
                    ic_done.setVisibility(View.VISIBLE);
                    button.setBackground(ContextCompat.getDrawable(this, R.drawable.ic_button_bg));
                    swipeNext = true;
                }
                else {

                    Log.i(TAG, "Permissions not granted ");
                }
                break;
            default:
                super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }

    @Override
    public void onBackPressed() {

        Intent intent = new Intent(PermissionActivity.this, WelcomeActivity.class);
        startActivity(intent);
        //animation transition to left
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (Tools.isPermissionGranted(this)) {
            Log.i(TAG, "Permissions granted ");
            buttonPermission.setVisibility(View.GONE);
            button.setEnabled(true);
            ic_done.setVisibility(View.VISIBLE);
            button.setBackground(ContextCompat.getDrawable(this, R.drawable.ic_button_bg));
            swipeNext = true;
        }
    }
}
