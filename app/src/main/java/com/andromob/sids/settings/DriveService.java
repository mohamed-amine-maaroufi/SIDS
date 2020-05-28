package com.andromob.sids.settings;

import android.app.Activity;
import android.app.Dialog;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.IBinder;
import android.util.Log;

import com.andromob.sids.R;
import com.andromob.sids.Tools;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential;
import com.google.api.client.googleapis.extensions.android.gms.auth.UserRecoverableAuthIOException;
import com.google.api.client.http.FileContent;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.client.util.ExponentialBackOff;
import com.google.api.services.drive.Drive;
import com.google.api.services.drive.DriveScopes;
import com.google.api.services.drive.model.File;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import androidx.annotation.Nullable;

public class DriveService extends Service {

    GoogleAccountCredential mCredential = null;
    static final int REQUEST_ACCOUNT_PICKER = 1000;
    static final int REQUEST_AUTHORIZATION = 1001;
    static final int REQUEST_GOOGLE_PLAY_SERVICES = 1002;
    static final int REQUEST_PERMISSION_GET_ACCOUNTS = 1003;
    private static final String[] SCOPES = { DriveScopes.DRIVE };
    private static final String PREF_ACCOUNT_NAME = "accountName";

    java.io.File file2;
    static String path = null;

    //GoogleSignInAccount account;
    private int calledFrom= 0;

    @Override
    public void onCreate() {
       // super.onCreate(savedInstanceState);

        mCredential = GoogleAccountCredential.usingOAuth2(
                this, Arrays.asList(SCOPES))
                .setBackOff(new ExponentialBackOff());

        //moveTaskToBack(true);
        //path = "/storage/emulated/0/Pictures/SIDS/SIDS20200523011523.jpg";
        uploadFileToDrive();

    }


    public void uploadFileToDrive()
    {
        path = Tools.filepath;

        calledFrom = 2;
        getResultsFromApi();
        //new DriveService.MakeDriveRequestTask2(mCredential, DriveService.this).execute();//upload q and responses xlsx files
        //getResultsFromApi();

    }

    public void getResultsFromApi() {
        if (! isGooglePlayServicesAvailable()) {
            acquireGooglePlayServices();
        } else if (mCredential.getSelectedAccountName() == null) {
            chooseAccount();
        } else if (! isDeviceOnline()) {

            Log.d(this.toString(),"No network connection available.");
        }
        else
        {
            //if everything is Ok
            if (calledFrom == 2 )
            {
                new DriveService.MakeDriveRequestTask2(mCredential, DriveService.this).execute();//upload q and responses xlsx files
            }
            if (calledFrom == 1 )
            {
                new DriveService.MakeDriveRequestTask(mCredential, DriveService.this).execute();//create app folder in drive
            }
        }
    }


    private void chooseAccount() {

           /* String accountName = getPreferences(Context.MODE_PRIVATE)
                    .getString(PREF_ACCOUNT_NAME, null);*/
       SharedPreferences sharedPreferences = getSharedPreferences(String.valueOf(R.string.shared_preferences_settings), MODE_PRIVATE);
       String accountName = sharedPreferences.getString(String.valueOf(R.string.key_account_name), null);

        if (accountName != null)
        {
                mCredential.setSelectedAccountName(accountName);
                getResultsFromApi();

        }
        else
        {
            Log.d("DriveService", "account name is null !!");
        }

    }

/*
    @Override
    protected void onActivityResult(
            int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch(requestCode) {
            case REQUEST_GOOGLE_PLAY_SERVICES:
                if (resultCode != RESULT_OK) {
                    Log.d(this.toString(),"This app requires Google Play Services. Please install " +
                            "Google Play Services on your device and relaunch this app.");

                } else {
                    getResultsFromApi();
                }
                break;
            case REQUEST_ACCOUNT_PICKER:
                if (resultCode == RESULT_OK && data != null &&
                        data.getExtras() != null) {
                    String accountName =
                            data.getStringExtra(AccountManager.KEY_ACCOUNT_NAME);
                    if (accountName != null) {
                        SharedPreferences settings =
                                getPreferences(Context.MODE_PRIVATE);
                        SharedPreferences.Editor editor = settings.edit();
                        editor.putString(PREF_ACCOUNT_NAME, accountName);
                        editor.apply();
                        mCredential.setSelectedAccountName(accountName);
                        getResultsFromApi();
                    }
                }
                break;
            case REQUEST_AUTHORIZATION:
                if (resultCode == RESULT_OK) {
                    getResultsFromApi();
                }
                break;
        }
    }
*/



    private boolean isDeviceOnline() {
        ConnectivityManager connMgr =
                (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        Log.d(this.toString(),"Checking if device");
        return (networkInfo != null && networkInfo.isConnected());

    }

    private boolean isGooglePlayServicesAvailable() {
        GoogleApiAvailability apiAvailability =
                GoogleApiAvailability.getInstance();
        final int connectionStatusCode =
                apiAvailability.isGooglePlayServicesAvailable(this);
        return connectionStatusCode == ConnectionResult.SUCCESS;
    }

    private void acquireGooglePlayServices() {
        GoogleApiAvailability apiAvailability =
                GoogleApiAvailability.getInstance();
        final int connectionStatusCode =
                apiAvailability.isGooglePlayServicesAvailable(this);
        if (apiAvailability.isUserResolvableError(connectionStatusCode)) {
            showGooglePlayServicesAvailabilityErrorDialog(connectionStatusCode);
        }
    }

    void showGooglePlayServicesAvailabilityErrorDialog(
            final int connectionStatusCode) {
        GoogleApiAvailability apiAvailability = GoogleApiAvailability.getInstance();
        Dialog dialog = apiAvailability.getErrorDialog(
                (Activity) getApplicationContext(),
                connectionStatusCode,
                REQUEST_GOOGLE_PLAY_SERVICES);
        dialog.show();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }


    private class MakeDriveRequestTask2 extends AsyncTask<Void, Void, List<String>> {
        private Drive mService = null;
        private Exception mLastError = null;
        private Context mContext;


        MakeDriveRequestTask2(GoogleAccountCredential credential,Context context) {

            mContext = context;
            HttpTransport transport = AndroidHttp.newCompatibleTransport();
            JsonFactory jsonFactory = JacksonFactory.getDefaultInstance();
            mService = new Drive.Builder(
                    transport, jsonFactory, credential)
                    .setApplicationName("SIDS")
                    .build();
            // TODO change the application name to the name of your applicaiton
        }

        @Override
        protected List<String> doInBackground(Void... params) {

            try {
                uploadFile();
            }
            catch (Exception e)
            {
                e.printStackTrace();
                mLastError = e;

               /* if (mLastError instanceof GooglePlayServicesAvailabilityIOException) {
                    showGooglePlayServicesAvailabilityErrorDialog(
                            ((GooglePlayServicesAvailabilityIOException) mLastError)
                                    .getConnectionStatusCode());
                } else if (mLastError instanceof UserRecoverableAuthIOException) {

                    startActivityForResult(
                            ((UserRecoverableAuthIOException) mLastError).getIntent(),
                            DriveService.REQUEST_AUTHORIZATION);
                } else {
                    Log.d(this.toString(),"The following error occurred:\n"+ mLastError.getMessage());
                }
                Log.d(this.toString(),e+"");*/
            }


            return null;
        }

        @Override
        protected void onPreExecute() {


        }

        @Override
        protected void onPostExecute(List<String> output) {

        }

        @Override
        protected void onCancelled() {


            /*if (mLastError != null) {
                if (mLastError instanceof GooglePlayServicesAvailabilityIOException) {
                    showGooglePlayServicesAvailabilityErrorDialog(
                            ((GooglePlayServicesAvailabilityIOException) mLastError)
                                    .getConnectionStatusCode());
                } else if (mLastError instanceof UserRecoverableAuthIOException) {

                    startActivityForResult(
                            ((UserRecoverableAuthIOException) mLastError).getIntent(),
                            DriveService.REQUEST_AUTHORIZATION);
                } else {
                    //mTextView.setText("The following error occurred:\n"+ mLastError.getMessage());
                }
            } else {
               // mTextView.setText("Request cancelled.");
            }*/
        }

        private void uploadFile()  {


            File fileMetadata = new File();
            fileMetadata.setName(Tools.fileName);
            //String compressedImagePath = Tools.compressImage(path,mContext);
            //java.io.File filePath = new java.io.File("/storage/emulated/0/Pictures/SIDS/SIDS20200525045015.jpg");

            File file = null;
            try {
                java.io.File filePath = new java.io.File(path);
                FileContent mediaContent = new FileContent("image/jpeg", filePath);
                file = mService.files().create(fileMetadata, mediaContent)
                        .setFields("id")
                        .execute();
                System.out.println("File ID: " + file.getId());
                Log.d("drive","File ID: " + file.getId());
            } catch (IOException e) {
                e.printStackTrace();
                Log.d("drive","IOException : " + e.getMessage());
            }

        }


    }



    private class MakeDriveRequestTask extends AsyncTask<Void, Void, List<String>> {
        private Drive mService = null;
        private Exception mLastError = null;
        private Context mContext;


        MakeDriveRequestTask(GoogleAccountCredential credential,Context context) {

            mContext = context;
            HttpTransport transport = AndroidHttp.newCompatibleTransport();
            JsonFactory jsonFactory = JacksonFactory.getDefaultInstance();
            mService = new Drive.Builder(
                    transport, jsonFactory, credential)
                    .setApplicationName("SIDS")
                    .build();
            // TODO change the application name to the name of your applicaiton
        }

        @Override
        protected List<String> doInBackground(Void... params) {


            try {
                createFolderInDrive();

            } catch (Exception e) {
                e.printStackTrace();
                mLastError = e;

                /*if (mLastError instanceof GooglePlayServicesAvailabilityIOException) {
                    showGooglePlayServicesAvailabilityErrorDialog(
                            ((GooglePlayServicesAvailabilityIOException) mLastError)
                                    .getConnectionStatusCode());
                } else if (mLastError instanceof UserRecoverableAuthIOException) {

                    startActivityForResult(
                            ((UserRecoverableAuthIOException) mLastError).getIntent(),
                            DriveService.REQUEST_AUTHORIZATION);
                } else {
                    Log.d(this.toString(),"The following error occurred:\n"+ mLastError.getMessage());
                }
                Log.d(this.toString(),e+"");*/
            }
            return null;
        }

        @Override
        protected void onPreExecute() {


        }

        @Override
        protected void onPostExecute(List<String> output) {


        }

        @Override
        protected void onCancelled() {


            /*if (mLastError != null) {
                if (mLastError instanceof GooglePlayServicesAvailabilityIOException) {
                    showGooglePlayServicesAvailabilityErrorDialog(
                            ((GooglePlayServicesAvailabilityIOException) mLastError)
                                    .getConnectionStatusCode());
                } else if (mLastError instanceof UserRecoverableAuthIOException) {

                    startActivityForResult(
                            ((UserRecoverableAuthIOException) mLastError).getIntent(),
                            DriveService.REQUEST_AUTHORIZATION);
                } else {
                    //mTextView.setText("The following error occurred:\n"+ mLastError.getMessage());
                }
            } else {
                //mTextView.setText("Request cancelled.");
            }*/
        }

        private void createFolderInDrive() throws IOException {
            File fileMetadata = new File();
            fileMetadata.setName("Sample Folder");
            fileMetadata.setMimeType("application/vnd.google-apps.folder");

            File file = mService.files().create(fileMetadata)
                    .setFields("id")
                    .execute();
            System.out.println("Folder ID: " + file.getId());

            Log.d(this.toString(),"Folder Created with ID:"+ file.getId());

        }


    }

}
