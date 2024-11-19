package com.v7idea.template.ThirdPartyLogin;

import android.accounts.Account;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import androidx.fragment.app.FragmentActivity;
import android.util.Log;

import com.google.android.gms.auth.GoogleAuthException;
import com.google.android.gms.auth.GoogleAuthUtil;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.common.Scopes;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.OptionalPendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.v7idea.healthkit.R;
import com.v7idea.template.DataBase.SimpleDatabase;

import java.io.IOException;

/**
 * Created by mortal on 16/1/27.
 */
public class GooglePlusSignInManager
{
    public static final int REQUEST_GOOGLE_PLAY_SERVICES = 1972;
    private final String TAG = "GooglePlusSignInManager";

    private static final int RC_SIGN_IN = 9001;
    private GoogleApiClient mGoogleApiClient;

    private FragmentActivity CurrentActivity = null;
    private SignSuccess AfterSignSuccess = null;

    public void setAfterSignSuccess(SignSuccess interfaceSignSuccess)
    {
        AfterSignSuccess = interfaceSignSuccess;
    }

    public void init(FragmentActivity CurrentActivity)
    {
        this.CurrentActivity = CurrentActivity;

        // [START configure_signin]
        // Configure sign-in to request the user's ID, email address, and basic
        // profile. ID and basic profile are included in DEFAULT_SIGN_IN.
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .requestId()
                .requestProfile()
                .requestIdToken(CurrentActivity.getResources().getString(R.string.google_plus_client_id))
                .build();

        // [START build_client]
        // Build a GoogleApiClient with access to the Google Sign-In API and the
        // options specified by gso.
        mGoogleApiClient = new GoogleApiClient.Builder(CurrentActivity)
                .enableAutoManage(CurrentActivity, OnConnectionFailedListener)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();
    }

    public boolean checkPlayServices() {
        GoogleApiAvailability googleAPI = GoogleApiAvailability.getInstance();
        int result = googleAPI.isGooglePlayServicesAvailable(CurrentActivity);
        if(result != ConnectionResult.SUCCESS) {
            if(googleAPI.isUserResolvableError(result)) {
                googleAPI.getErrorDialog(CurrentActivity, result, GooglePlusSignInManager.REQUEST_GOOGLE_PLAY_SERVICES).show();
            }

            return false;
        }

        return true;
    }

    public void checkSignInCached()
    {
        OptionalPendingResult<GoogleSignInResult> opr = Auth.GoogleSignInApi.silentSignIn(mGoogleApiClient);
        if (opr.isDone()) {
            // If the user's cached credentials are valid, the OptionalPendingResult will be "done"
            // and the GoogleSignInResult will be available instantly.
            Log.d(TAG, "Got cached sign-in");
            GoogleSignInResult result = opr.get();
            handleSignInResult(result);
        } else {
            // If the user has not previously signed in on this device or the sign-in has expired,
            // this asynchronous branch will attempt to sign in the user silently.  Cross-device
            // single sign-on will occur in this branch.
//            showProgressDialog();
            opr.setResultCallback(new ResultCallback<GoogleSignInResult>() {
                @Override
                public void onResult(GoogleSignInResult googleSignInResult) {
//                    hideProgressDialog();
                    handleSignInResult(googleSignInResult);
                }
            });
        }
    }

    public void inActivityResult(int requestCode, Intent data)
    {
        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            handleSignInResult(result);
        }
    }

    public void signIn() {
        // [START signIn]
        Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
        CurrentActivity.startActivityForResult(signInIntent, RC_SIGN_IN);
        // [END signIn]
    }

    public void signOut() {
        // [START signOut]
       if(mGoogleApiClient.isConnected())
       {
           Auth.GoogleSignInApi.signOut(mGoogleApiClient).setResultCallback(
                   new ResultCallback<Status>() {
                       @Override
                       public void onResult(Status status) {
                           // [START_EXCLUDE]
//                        updateUI(false);
                           // [END_EXCLUDE]
                           if(status != null)
                           {
                               Log.d(TAG, "signOut: " + status.getStatusMessage());
                           }
                       }
                   });
       }
    }

    private void handleSignInResult(GoogleSignInResult result)
    {
        Log.d(TAG, "handleSignInResult:" + result.isSuccess());

        if (result.isSuccess()) {
            // Signed in successfully, show authenticated UI.
            final GoogleSignInAccount acct = result.getSignInAccount();

            String userID = acct.getId();
            String userIDToken = acct.getIdToken();

            String email = acct.getEmail();

            Uri userPhotoUri = acct.getPhotoUrl();
            String userPhotoAddress = "";

            new AsyncTask<Void, Void, Void>()
            {
                @Override
                protected Void doInBackground(Void... params)
                {
                    String scope =  "oauth2:" + Scopes.PROFILE;
                    Account account = new Account(acct.getEmail(), "com.google");

                    try {
                        String strAccessToken = GoogleAuthUtil.getToken(CurrentActivity, account, scope);

                        Log.e(TAG, "strAccessToken: "+strAccessToken);
                    } catch (IOException e) {
                        e.printStackTrace();
                    } catch (GoogleAuthException e) {
                        e.printStackTrace();
                    }

                    return null;
                }
            }.execute();

            if(userPhotoUri != null)
            {
                userPhotoAddress = userPhotoUri.toString() + "?sz=200";

                Log.d(TAG, "userPhotoAddress:" + userPhotoAddress);
            }

//            Uri UserPhotoUri = acct.getPhotoUrl();
            String name = acct.getDisplayName();

            Log.d(TAG, "DisplayName:" + acct.getDisplayName());
            Log.d(TAG, "userID:" + userID);
            Log.d(TAG, "userIDToken:" + userIDToken);
            Log.d(TAG, "email:" + email);

            SimpleDatabase simpleDatabase = new SimpleDatabase();
            simpleDatabase.setValueByKey("GooglePlusIDToken", userIDToken);
            simpleDatabase.setValueByKey("CommunitySoftwareID", "GooglePlusID:" + userID);
            simpleDatabase.setValueByKey("CommunitySoftwareName", name);
            simpleDatabase.setValueByKey("CommunitySoftwareGender", "");
            simpleDatabase.setValueByKey("CommunitySoftwareEmail", email);
            simpleDatabase.setValueByKey("CommunitySoftwareUserPhoto", userPhotoAddress);

            //http://graph.facebook.com/103407310026838/picture?type=large&width=1000&height=1000

//            Toast.makeText(CurrentActivity, "handleSignInResult:" + acct.getDisplayName(), Toast.LENGTH_SHORT).show();

            GetAccessTokenTask getAccessTokenTask = new GetAccessTokenTask();
            getAccessTokenTask.execute(email, userID);

//            LoginByGooglePlusID LoginByGooglePlusID = new LoginByGooglePlusID(userID);
//            LoginByGooglePlusID.execute("");

        } else {
            // Signed out, show unauthenticated UI.
            Log.d(TAG, "handleSignInResult: fail");
        }
    }

    private GoogleApiClient.OnConnectionFailedListener OnConnectionFailedListener = new GoogleApiClient.OnConnectionFailedListener() {
        @Override
        public void onConnectionFailed(ConnectionResult connectionResult) {
            Log.d(TAG, "onConnectionFailed:" + connectionResult);
        }
    };

    public interface SignSuccess
    {
        void afterSignSuccess(String strCommunitySoftware, String strCommunitySoftwareID);
    }

    private class GetAccessTokenTask extends AsyncTask<String, Void, String>
    {
        private String userMail = null;
        private String userID = null;

        @Override
        protected String doInBackground(String... params)
        {
            userMail = params[0];
            userID = params[1];

            String scope =  "oauth2:" + Scopes.PROFILE;
            Account account = new Account(userMail, "com.google");

            try
            {
                String strAccessToken = GoogleAuthUtil.getToken(CurrentActivity, account, scope);

//                Log.e(TAG, "strAccessToken: "+strAccessToken);

                SimpleDatabase simpleDatabase = new SimpleDatabase();
                simpleDatabase.setValueByKey("GooglePlusAccessTokenToken", strAccessToken);

                return strAccessToken;
            }
            catch (IOException e) {
                e.printStackTrace();
                return "";
            }
            catch (GoogleAuthException e) {
                e.printStackTrace();
                return "";
            }
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);

            if(AfterSignSuccess != null)
            {
                AfterSignSuccess.afterSignSuccess("google", userID);
            }
        }
    };
}
