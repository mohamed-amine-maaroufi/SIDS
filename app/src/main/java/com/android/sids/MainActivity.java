package com.android.sids;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import android.os.Bundle;

import com.android.sids.logs.LogsFragment;
import com.android.sids.logs.TabAdapter;
import com.android.sids.settings.SettingFragment;
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