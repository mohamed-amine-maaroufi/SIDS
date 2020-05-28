package com.andromob.sids.settings;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.andromob.sids.MainActivity;
import com.andromob.sids.R;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.Scope;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.api.services.drive.DriveScopes;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

public class SignInActivity extends AppCompatActivity {
    Button mSignOut,mRevokeAccess;
    SignInButton mSignIn;
    GoogleSignInClient mGoogleSignInClient;
    GoogleSignInAccount account;
    SharedPreferences sharedPreferences;
    SharedPreferences.Editor myEdit;

    String token;
    public static final int RC_SIGN_IN = 1;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);

        mSignIn = (SignInButton) findViewById(R.id.sign_in_btn);
        mSignIn.setSize(SignInButton.SIZE_STANDARD);
        mSignOut =(Button) findViewById(R.id.sign_out_btn);

        //the local storage to save data is opened
        sharedPreferences = getSharedPreferences(String.valueOf(R.string.shared_preferences_settings), MODE_PRIVATE);
        myEdit = sharedPreferences.edit();

        //request permissions for get Account google
        ActivityCompat.requestPermissions(this, new String[] {Manifest.permission.GET_ACCOUNTS},103);

        mSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                signIn();

            }});


        mSignOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                signOut();

            }});

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestScopes(new Scope(DriveScopes.DRIVE)) // request permission for Drive API
                .requestEmail()
                .build();

        /*In order to access files in drive the scope of the permission has to be specified.
        More info on scope is available in Google Drive Api Documentation*/

        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);

        account = GoogleSignIn.getLastSignedInAccount(this);
        // If already signed in with the app it can be obtained here

        if(account==null)
        {
            Toast.makeText(getApplicationContext(),
                    "You Need To Sign In First", Toast.LENGTH_SHORT).show();
        }
        if (account != null) {


            myEdit.putString(String.valueOf(R.string.key_account_name), account.getEmail());
            myEdit.commit();

            Intent intent = new Intent(this, MainActivity.class);
            //intent.putExtra("ACCOUNT",account);
            startActivity(intent);
        }
    }

    private void signIn() {
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Result returned from launching the Intent from GoogleSignInClient.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            // The Task returned from this call is always completed, no need to attach
            // a listener.
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            handleSignInResult(task);
            Intent intent = new Intent(this,MainActivity.class);



            if(account != null)
            {
                myEdit.putString(String.valueOf(R.string.key_account_name), account.getEmail());
                myEdit.commit();
            }

            //intent.putExtra("ACCOUNT",account);

            startActivity(intent);
        }
    }

    private void handleSignInResult(Task<GoogleSignInAccount> completedTask) {
        try {
            account = completedTask.getResult(ApiException.class);

            // Signed in successfully, show authenticated UI.

            Log.e("Check",account.getEmail()+account.getGivenName()+account.getFamilyName());
            // If successful you can obtain the account info using the getter methods
        } catch (ApiException e) {
            // The ApiException status code indicates the detailed failure reason.
            // Please refer to the GoogleSignInStatusCodes class reference for more information.
            Log.e("Sign-In", "signInResult:failed code=" + e.getStatusCode() + e);
            Toast.makeText(getApplicationContext(),
                    "Sign In Failed.Try again Later", Toast.LENGTH_LONG).show();
        }
    }

    @Override
    protected void onStop() {
        super.onStop();

    }

    @Override
    protected void onStart() {
        super.onStart();
    }
    public void signOut() {
        mGoogleSignInClient.signOut()
                .addOnCompleteListener(this, new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        Toast.makeText(getApplicationContext(),
                                "Signed Out", Toast.LENGTH_SHORT).show();
                    }
                });
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {

        if (grantResults.length > 0)
        {
            switch (requestCode) {
                case 103:

                    if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                        Log.i("SignInActivity", "Permissions granted ");
                    }
                    else {

                        Log.i("SignInActivity", "Permissions not granted ");
                    }
                    break;
                default:
                    super.onRequestPermissionsResult(requestCode, permissions, grantResults);
            }
        }
        else
        {
            Log.i("SignInActivity", "Permissions not granted ");
        }




    }
}
