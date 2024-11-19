package com.v7idea.template.GCM;

import android.app.IntentService;
import android.content.Intent;
import android.util.Log;

import com.v7idea.healthkit.Constant;
import com.v7idea.healthkit.R;
import com.v7idea.template.DataBase.SimpleDatabase;

import java.io.IOException;

/**
 * Created by mortal on 15/9/25.
 */
public class RegistrationIntentService extends IntentService {
    private static final String TAG = "RegIntentService";
    private static final String[] TOPICS = {"global"};

    public RegistrationIntentService() {
        super(TAG);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
//
//        String strOrgID = "";
//        String strUserToken = "";
//
//        if (intent != null) {
//            strOrgID = intent.getStringExtra("OrgID");
//            strUserToken = intent.getStringExtra("processTicketID");
//        }
//
//        Log.d(TAG, "strOrgID: " + strOrgID);
//
////        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
//
//        try {
//            // [START register_for_gcm]
//            // Initially this call goes out to the network to retrieve the token, subsequent calls
//            // are local.
//
//            // [START get_token]
//            InstanceID instanceID = InstanceID.getInstance(getApplicationContext());// 266132841019
//            String token = instanceID.getToken(getResources().getString(R.string.gcm_sender_id), GoogleCloudMessaging.INSTANCE_ID_SCOPE, null);
//            // [END get_token]
//            Log.e(TAG, "GCM Registration Token: " + token);
//
//            SimpleDatabase DataBase = new SimpleDatabase();
//
//            String strCurrentToken = DataBase.getStringValueByKey(Constant.GCMRegisterID, "");
//
//            Log.e(TAG, "GCM strCurrentToken: " + strCurrentToken);
//
//            DataBase.setValueByKey(Constant.GCMRegisterID, token);
////            sendRegistrationToServer(token, strOrgID);
//
//            if (strCurrentToken.isEmpty()) {
//                DataBase.setValueByKey(Constant.GCMRegisterID, token);
//
//                // Implement this method to send any registration to your app's servers.
//                sendRegistrationToServer(token, strOrgID);
//            } else {
//                DataBase.setValueByKey(Constant.GCMRegisterID, token);
//
//                //替換GCMID 給自家Server
//
//                changeRegistrationToServer(strUserToken, strOrgID, token, strCurrentToken);
//            }
//
//            // Subscribe to topic channels
//            subscribeTopics(token);
//
//            // You should store a boolean that indicates whether the generated token has been
//            // sent to your server. If the boolean is false, send the token to your server,
//            // otherwise your server should have already received the token.
////            sharedPreferences.edit().putBoolean(QuickstartPreferences.SENT_TOKEN_TO_SERVER, true).apply();
//            // [END register_for_gcm]
//        } catch (Exception e) {
//            Log.d(TAG, "Failed to complete token refresh", e);
//            // If an exception happens while fetching the new token or updating our registration data
//            // on a third-party server, this ensures that we'll attempt the update at a later time.
////            sharedPreferences.edit().putBoolean(QuickstartPreferences.SENT_TOKEN_TO_SERVER, false).apply();
//        }
//        // Notify UI that registration has completed, so the progress indicator can be hidden.
////        Intent registrationComplete = new Intent(QuickstartPreferences.REGISTRATION_COMPLETE);
////        LocalBroadcastManager.getInstance(this).sendBroadcast(registrationComplete);
    }

    private void changeRegistrationToServer(String strUserToken, String strOrdID, String strNewRegistrationID, String strOldRegistrationID) {
//        APIFetch apiFetch = new APIFetch();
//
//        String api = apiFetch.getChangeRegisterGCMIDApi(strOrdID);
//
//        Log.e(TAG, "GCM Registration Token: " + api);
//
//        DownLoad downLoad = new DownLoad();
//
//        String strIMEI = Utils.getAndroidId(this);
//
//        Log.e(TAG, "strIMEI: " + strIMEI);
//
//        JSONObject sendData = new JSONObject();
//
//        try {
//            sendData.put("tokenID", strUserToken);
//            sendData.put("registrationID", strNewRegistrationID);
//            sendData.put("deviceKey", strIMEI);
//            sendData.put("oldToken", strOldRegistrationID);//舊的 GCMRegistrationID
//        } catch (JSONException e) {
//            e.printStackTrace();
//            sendData = null;
//        }
//
//        if (sendData != null) {
//            String resultString = downLoad.getStringFromUrlByPut(api, sendData);
//            Log.e(TAG, "Register GCMToken sendData: " + sendData.toString());
//            Log.e(TAG, "Register GCMToken result: " + resultString);
//        }
    }

    private void sendRegistrationToServer(String strToken, String strOrgID) {
//        APIFetch apiFetch = new APIFetch();
//
//        String api = apiFetch.getFirstRegisterGCMApi(Air.getAndroidId(this), strOrgID, strToken);
//
//        Log.e(TAG, "GCM Registration Token: " + api);
//
//        DownLoad downLoad = new DownLoad();
//
//        String resultString = downLoad.getStringFromUrlByPost(api, (String) null);
//
//        Log.e(TAG, "Register GCMToken result: " + resultString);
    }

    private void subscribeTopics(String token) throws IOException {
//        GcmPubSub pubSub = GcmPubSub.getInstance(this);
//        for (String topic : TOPICS) {
//            pubSub.subscribe(token, "/topics/" + topic, null);
//        }
    }
}
