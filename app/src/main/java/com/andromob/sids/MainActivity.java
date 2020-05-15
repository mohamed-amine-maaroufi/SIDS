package com.andromob.sids;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;

import com.andromob.sids.logs.LogsFragment;
import com.andromob.sids.logs.TabAdapter;
import com.andromob.sids.settings.SettingFragment;
import com.google.android.material.tabs.TabLayout;
/**
 * Created by andromob on 26/04/14.
 */
public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";
    private TabAdapter adapter;
    private TabLayout tabLayout;
    private ViewPager viewPager;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        viewPager = (ViewPager) findViewById(R.id.viewPager);
        tabLayout = (TabLayout) findViewById(R.id.tabLayout);
        adapter = new TabAdapter(getSupportFragmentManager());
        adapter.addFragment(new LogsFragment(), "LOGS");
        adapter.addFragment(new SettingFragment(), "SETTINGS");
        viewPager.setAdapter(adapter);
        tabLayout.setupWithViewPager(viewPager);



    }



}