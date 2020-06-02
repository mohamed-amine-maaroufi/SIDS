package com.android.sids.IntroScreens;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.android.sids.R;
import com.android.sids.Tools;
/**
 * Created by andromob on 26/04/14.
 */
public class WelcomeActivity extends AppCompatActivity {

    Button button;
    ConstraintLayout layoutParent;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // making activity full screen
        if (Build.VERSION.SDK_INT >= 21) {
            getWindow().getDecorView()
                    .setSystemUiVisibility(
                            View.SYSTEM_UI_FLAG_LAYOUT_STABLE | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
        }

        setContentView(R.layout.activity_welcome);

        // hide action bar you can use NoAction theme as well
        getSupportActionBar().hide();

        // make status bar transparent
        Tools.changeStatusBarColor(this);

        button = findViewById(R.id.button);

        //swipe beetween intro screens
        layoutParent = findViewById(R.id.mainContent);
        layoutParent.setOnTouchListener(new OnSwipeTouchListener(WelcomeActivity.this) {

            public void onSwipeLeft() {
                Intent intent = new Intent(WelcomeActivity.this, PermissionActivity.class);
                startActivity(intent);
                //animation transition to right
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
            }
        });

        button.setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View view) {
                Intent intent = new Intent(WelcomeActivity.this, PermissionActivity.class);
                startActivity(intent);
                //animation transition to right
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
            }
        });
    }


}
