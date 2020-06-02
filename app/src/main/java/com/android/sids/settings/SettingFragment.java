package com.android.sids.settings;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.admin.DevicePolicyManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;

import com.android.sids.DeviceAdminSampleReceiver;
import com.android.sids.R;
import com.android.sids.Tools;

import androidx.fragment.app.Fragment;

import static android.content.Context.MODE_PRIVATE;

/**
 * Created by andromob on 26/04/14.
 */
public class SettingFragment extends Fragment {
    private Spinner spinnerNumberpic,spinnerdetectionBehavior, spinnerNumberAttempts;
    private EditText EditTextSmsNumber;

    private SharedPreferences sharedPreferences ;
    //private String keyNumberPic = "";
    private String keyConfiguredNumbeAttempts = "";
    private String keyDetectionBehavior = "";
    private String keySmsNumber = "";
    private String TAG = "SettingFragment";
    private ComponentName componentName;
    private DevicePolicyManager devicePolicyManager;

    @SuppressLint("WrongConstant")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_setting, container, false);

        //Configuration for device Admin
        componentName = new ComponentName(getContext(), DeviceAdminSampleReceiver.class);
        devicePolicyManager = (DevicePolicyManager)getContext().getSystemService(Context.DEVICE_POLICY_SERVICE);
        final Switch switchDeviceAdmin = view.findViewById(R.id.switchAdminDevice);
        final Switch switchsms = view.findViewById(R.id.switchsSms);
        boolean isDeviceAdmin = Tools.isAdminDevice(componentName,devicePolicyManager);


        sharedPreferences = getContext().getSharedPreferences(String.valueOf(R.string.shared_preferences_settings), MODE_PRIVATE);
        boolean isSmsEnabled = sharedPreferences.getBoolean(String.valueOf(R.string.key_is_sms_enabled), true);

        Log.d(TAG,"isSmsEnabled = " + isSmsEnabled);

        if(isDeviceAdmin)
        {
            switchDeviceAdmin.setChecked(true);
        }
        else{
            switchDeviceAdmin.setChecked(false);
        }

        if(isSmsEnabled)
        {
            switchsms.setChecked(true);
        }
        else{
            switchsms.setChecked(false);
        }

        //Enable /disable Device Admin
        switchDeviceAdmin.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    Tools.requestPermissionAdminDevice(getActivity(),componentName);
                    Log.d(TAG,"Device Admin Enabled");
                } else {
                    // The toggle is disabled


                    AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getContext());
                    // set title
                    alertDialogBuilder.setTitle("Device Admin Permission");
                    // set dialog message
                    alertDialogBuilder
                            .setMessage(R.string.msg_alert_dialog_admin_device)
                            .setCancelable(false)
                            .setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {

                                    devicePolicyManager.removeActiveAdmin(componentName);
                                    Log.d(TAG,"Device Admin Disabled");

                                }
                            })
                            .setNegativeButton(R.string.no,new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {

                                    dialog.cancel();
                                    switchDeviceAdmin.setChecked(true);
                                }
                            });

                    // create alert dialog
                    AlertDialog alertDialog = alertDialogBuilder.create();

                    // show it
                    alertDialog.show();

                }
            }
        });

        //Enable /disable send sms
        switchsms.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {

                    // Storing data into SharedPreferences
                    SharedPreferences.Editor myEdit = sharedPreferences.edit();
                    myEdit.putBoolean(String.valueOf(R.string.key_is_sms_enabled),true);
                    myEdit.commit();

                    Log.d(TAG,"sms Enabled");
                } else {

                    // Storing data into SharedPreferences
                    SharedPreferences.Editor myEdit = sharedPreferences.edit();
                    myEdit.putBoolean(String.valueOf(R.string.key_is_sms_enabled),false);
                    myEdit.commit();


                    Log.d(TAG,"sms Disabled");
                }
            }
        });

        //keyNumberPic = sharedPreferences.getString(String.valueOf(R.string.key_number_pictures), "");
        keyConfiguredNumbeAttempts = sharedPreferences.getString(String.valueOf(R.string.key_configured_number_attempts), "");
        keyDetectionBehavior = sharedPreferences.getString(String.valueOf(R.string.key_detection_behavior), "");
        keySmsNumber = sharedPreferences.getString(String.valueOf(R.string.key_phone_number), "");

        //addListenerOnspinnerNumberpicItemSelection(view);
        addListenerOnspinnerNumberAttemptsItemSelection(view);
        addListenerOnspinnerDetectionBehaviorItemSelection(view);

        // Edit text for sms number, the number will be saved in data base when the user finish typing and press done
        EditTextSmsNumber = view.findViewById(R.id.smsNumber);
        EditTextSmsNumber.setText(keySmsNumber);

        EditTextSmsNumber.setOnEditorActionListener(
                new EditText.OnEditorActionListener() {
                    @Override
                    public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                        if (actionId == EditorInfo.IME_ACTION_SEARCH ||
                                actionId == EditorInfo.IME_ACTION_DONE ||
                                event != null &&
                                        event.getAction() == KeyEvent.ACTION_DOWN &&
                                        event.getKeyCode() == KeyEvent.KEYCODE_ENTER) {
                            if (event == null || !event.isShiftPressed()) {
                                // the user is done typing.
                                // Storing data into SharedPreferences
                                try
                                {

                                    SharedPreferences.Editor myEdit = sharedPreferences.edit();
                                    myEdit.putString(String.valueOf(R.string.key_phone_number), EditTextSmsNumber.getText().toString());
                                    myEdit.commit();

                                    //close keybord
                                    if(getActivity().getCurrentFocus() != null)
                                    {
                                        InputMethodManager inputmanager = (InputMethodManager)getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                                        if (inputmanager != null) {
                                            inputmanager.hideSoftInputFromWindow(getActivity().getCurrentFocus().getWindowToken(), 0);
                                        }
                                    }

                                    //revome focus on sms number editbox
                                    EditTextSmsNumber.clearFocus();
                                }
                                catch (Exception exc)
                                {
                                    Log.d(TAG, "excpetion : " + exc.getMessage());
                                }

                                return true; // consume.
                            }
                        }
                        return false; // pass on to other listeners.
                    }
                }
        );

        return view;
    }

   /* public void addListenerOnspinnerNumberpicItemSelection(View view) {

        spinnerNumberpic = (Spinner) view.findViewById(R.id.spinnerNumberpic);

        //set selected value from sharedPreferences
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(getContext(), R.array.numbers, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerNumberpic.setAdapter(adapter);
        if (keyNumberPic != null) {
            int spinnerPosition = adapter.getPosition(keyNumberPic);
            spinnerNumberpic.setSelection(spinnerPosition);
        }

        //Listner on spinner to save selected item in the data base SahredPreferences "SAHRED_PREFERENCES_SETTINGS"
        spinnerNumberpic.setOnItemSelectedListener(new ListenerSpinnerNumberpic(getContext()));
    }*/

    public void addListenerOnspinnerDetectionBehaviorItemSelection(View view) {

        spinnerdetectionBehavior = (Spinner) view.findViewById(R.id.spinnerdetectionBehavior);

        //set selected value from sharedPreferences
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(getContext(), R.array.detectionBehavior, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerdetectionBehavior.setAdapter(adapter);
        if (keyDetectionBehavior != null) {
            int spinnerPosition = adapter.getPosition(keyDetectionBehavior);
            spinnerdetectionBehavior.setSelection(spinnerPosition);
        }

        //Listner on spinner to save selected item in the data base SahredPreferences "SAHRED_PREFERENCES_SETTINGS"
        spinnerdetectionBehavior.setOnItemSelectedListener(new ListenerSpinnerDetectionBehavior(getContext()));
    }


    public void addListenerOnspinnerNumberAttemptsItemSelection(View view) {

        spinnerNumberAttempts = (Spinner) view.findViewById(R.id.spinnernumberattempts);

        //set selected value from sharedPreferences
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(getContext(), R.array.numbers, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerNumberAttempts.setAdapter(adapter);
        if (keyConfiguredNumbeAttempts != null) {
            int spinnerPosition = adapter.getPosition(keyConfiguredNumbeAttempts);
            spinnerNumberAttempts.setSelection(spinnerPosition);
        }

        //Listner on spinner to save selected item in the data base SahredPreferences "SAHRED_PREFERENCES_SETTINGS"
        spinnerNumberAttempts.setOnItemSelectedListener(new ListenerSpinnerNumberAttempts(getContext()));
    }

}
