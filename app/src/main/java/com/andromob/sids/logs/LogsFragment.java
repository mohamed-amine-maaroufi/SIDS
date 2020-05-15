package com.andromob.sids.logs;

import android.annotation.SuppressLint;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.TextView;
import android.widget.Toast;

import com.andromob.sids.R;

import androidx.fragment.app.Fragment;

import static android.content.Context.MODE_APPEND;
/**
 * Created by andromob on 26/04/14.
 */
public class LogsFragment extends Fragment {

    ImageAdapter imageAdapter;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_logs, container, false);

        //initiliaze SahredPreferences
        @SuppressLint("WrongConstant")
        SharedPreferences sharedPreferences = getContext().getSharedPreferences(String.valueOf(R.string.shared_preferences_settings),MODE_APPEND);
        String keyTotalfailedAttempts = sharedPreferences.getString(String.valueOf(R.string.key_total_failed_attempts), "0");

        //set number of failed attempts
        TextView textViewfailedAttempts = view.findViewById(R.id.NumberfailedAttempts);
        textViewfailedAttempts.setText(keyTotalfailedAttempts);

        //load pictures from gallery
        GridView gallery = (GridView) view.findViewById(R.id.galleryGridView);
        gallery.setAdapter(new ImageAdapter(getActivity()));

        imageAdapter = new ImageAdapter(getActivity());
        gallery.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @SuppressLint("WrongConstant")
            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1,
                                    int position, long arg3) {
                if (null != imageAdapter.getImages() && !imageAdapter.getImages().isEmpty())
                    Toast.makeText(
                            getContext(),
                            "position " + position + " " + imageAdapter.getImages().get(position),
                            300).show();
                ;

            }
        });

        return view;
    }
}
