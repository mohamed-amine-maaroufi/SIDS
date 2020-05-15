package com.andromob.sids.settings;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Toast;

import com.andromob.sids.R;

import static android.content.Context.MODE_PRIVATE;
/**
 * Created by andromob on 26/04/14.
 */
public class ListenerSpinnerDetectionBehavior implements AdapterView.OnItemSelectedListener {

    SharedPreferences sharedPreferences;
    Context context;
    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
      // Storing data into SharedPreferences
        sharedPreferences = context.getSharedPreferences(String.valueOf(R.string.shared_preferences_settings), MODE_PRIVATE);
        SharedPreferences.Editor myEdit = sharedPreferences.edit();
        Log.d("ListenerDetectionbeh", parent.getSelectedItem().toString());
        myEdit.putString(String.valueOf(R.string.key_detection_behavior), parent.getSelectedItem().toString());
        myEdit.commit();

    }

    ListenerSpinnerDetectionBehavior(Context ctx)
    {
        context = ctx;
    }

    @Override
    public void onNothingSelected(AdapterView<?> arg0) {
        // TODO Auto-generated method stub
    }

}