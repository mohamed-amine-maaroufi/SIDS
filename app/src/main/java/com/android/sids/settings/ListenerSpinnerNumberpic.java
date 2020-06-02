package com.android.sids.settings;

import android.content.Context;
import android.content.SharedPreferences;
import android.view.View;
import android.widget.AdapterView;

import com.android.sids.R;

import static android.content.Context.MODE_PRIVATE;
/**
 * Created by andromob on 26/04/14.
 */
public class ListenerSpinnerNumberpic implements AdapterView.OnItemSelectedListener {

    SharedPreferences sharedPreferences;
    Context context;
    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {

       // Storing data into SharedPreferences
        sharedPreferences = context.getSharedPreferences(String.valueOf(R.string.shared_preferences_settings), MODE_PRIVATE);
        SharedPreferences.Editor myEdit = sharedPreferences.edit();
        myEdit.putString(String.valueOf(R.string.key_number_pictures), parent.getItemAtPosition(pos).toString());
        myEdit.commit();

    }

    ListenerSpinnerNumberpic(Context ctx)
    {
        context = ctx;
    }

    @Override
    public void onNothingSelected(AdapterView<?> arg0) {
        // TODO Auto-generated method stub
    }

}