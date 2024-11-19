package com.v7idea.template.GCM;

import android.app.AlertDialog;
import android.app.PendingIntent;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import android.util.Log;


import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.v7idea.healthkit.Constant;
import com.v7idea.healthkit.HealthDescriptionPage1;
import com.v7idea.healthkit.HealthRecommendationPage2;
import com.v7idea.healthkit.InspectionReportPage;
import com.v7idea.healthkit.LiftSuggestListPage;
import com.v7idea.healthkit.LoginPage;
import com.v7idea.healthkit.Model.HealthSummary;
import com.v7idea.healthkit.Model.LifeNotice;
import com.v7idea.healthkit.Model.Token;
import com.v7idea.healthkit.R;
import com.v7idea.healthkit.ReportDescriptionPage1;
import com.v7idea.template.AirApplication;
import com.v7idea.template.DataBase.SimpleDatabase;
import com.v7idea.template.Tool.ApiResult;
import com.v7idea.template.Tool.DebugLog;
import com.v7idea.template.Tool.DownLoad;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Map;

import me.leolin.shortcutbadger.ShortcutBadger;

/**
 * Created by mortal on 15/9/25.
 */
public class V7ideaGcmListenerService extends FirebaseMessagingService {

    private static final String TAG = "MyGcmListenerService";

    public V7ideaGcmListenerService() {
        super();
    }

    @Override
    public void onCreate() {
        super.onCreate();

        DebugLog.e(TAG, "onCreate");

//        if (isNotificationListenerEnabled(this) == false) {
//            Intent intent = new Intent(Settings.ACTION_NOTIFICATION_LISTENER_SETTINGS);
//            intent.setFlags(FLAG_ACTIVITY_NEW_TASK);
//            startActivity(intent);
//
//        }
    }


//    public boolean isNotificationListenerEnabled(Context context) {
//        Set<String> packageNames = NotificationManagerCompat.getEnabledListenerPackages(this);
//        if (packageNames.contains(context.getPackageName())) {
//            return true;
//        }
//        return false;
//    }


    @Override
    public void onNewToken(String s) {
        super.onNewToken(s);

        SimpleDatabase simpleDatabase = new SimpleDatabase();

        String strOldToken = simpleDatabase.getStringValueByKey(Constant.GCMRegisterID, "");

        if (strOldToken.isEmpty()) {
            simpleDatabase.setValueByKey(Constant.GCMRegisterID, strOldToken);
        } else if (strOldToken.contentEquals(s) == false) {
            simpleDatabase.setValueByKey(Constant.GCMRegisterID, s);
        }

        //tokenCheck update registerID
        Token token = new Token();

        String tokenID = token.getTokenId();

        DebugLog.e(TAG, "tokenID: " + tokenID);

        if (DownLoad.isConnectInternet() && (tokenID.isEmpty() == false)) {

            ApiResult apiResult = token.updateDeviceToken(token.getTokenId(), s);

            if (apiResult.getErrorNo().contentEquals(Constant.ErrorCode.CODE_1002) || apiResult.getErrorNo().contentEquals(Constant.ErrorCode.CODE_1003)) {
                AlertDialog.Builder alertBuilder = new AlertDialog.Builder(this);
                alertBuilder.setMessage(apiResult.getMessage());
                alertBuilder.setPositiveButton(R.string.YES, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Intent intent = new Intent(getApplicationContext(), LoginPage.class);
                        startActivity(intent);
                    }
                });

                alertBuilder.setCancelable(false);
                alertBuilder.show();
            }
        }
    }

    /**
     * FCM 收到的推播在這處理
     *
     * @param remoteMessage
     */
    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);
        Log.e(TAG, "onMessageReceived");

//        if (isNotificationListenerEnabled(this) == false) {
//            Intent intent = new Intent(Settings.ACTION_NOTIFICATION_LISTENER_SETTINGS);
//            startActivity(intent);
//        }

        if (remoteMessage.getNotification() != null) {



            SimpleDatabase simpleDatabase = new SimpleDatabase();
            simpleDatabase.setValueByKey(Constant.HEALTH_REPORT, 1);
            simpleDatabase.setValueByKey(Constant.IMMEDIATE_IMPROVEMENT, 1);


            Intent intent = new Intent(Constant.GET_NOTIFICATION);
            sendBroadcast(intent);

            String title = remoteMessage.getNotification().getTitle();
            String body = remoteMessage.getNotification().getBody();
            String messageID = remoteMessage.getMessageId();

            Token token = new Token();
            Log.e(TAG, "onMessageReceived = " + " remoteMessage = " + remoteMessage.getData());

            String Tag = remoteMessage.getData().get("Tag");
            Intent intentData = null;

            if (Tag.contentEquals("PacientNotice")) {
//                HealthSummary healthSummary =new HealthSummary();
//                ApiResult result =  healthSummary.getHealthSummary(token.getTokenId());
//                boolean isIntroReport = simpleDatabase.getBooleanValueByKey(Constant.IS_NOT_HEALTH_REPORT, false);
//
//                if (isIntroReport) {
//                    intentData = new Intent(AirApplication.getAppContext(), HealthRecommendationPage2.class);
//                } else {
//                    intentData = new Intent(AirApplication.getAppContext(),HealthDescriptionPage1.class);
//                }
//                JSONObject jsonObject = result.getDataJSONObject();
//                intentData.putExtra("url", jsonObject.optString("pageurl"));
//                intentData.putExtra(Constant.NOTICE_FUNCTION_TYPE_PACIENT, true);


//                HealthReport healthReport = new HealthReport();
//
//                ApiResult result = healthReport.getHealthReport(token.getTokenId());
//                JSONObject jsonObject = result.getDataJSONObject();
//                String pageUrl = jsonObject.optString("pageurl");

                boolean isIntroReport = simpleDatabase.getBooleanValueByKey(Constant.IS_NOT_INTRO_REPORT, Constant.IS_NOT_INTRO_REPORT_DEFAUL);
                if (isIntroReport) {
                    intentData = new Intent(AirApplication.getAppContext(), InspectionReportPage.class);
                } else {
                    intentData = new Intent(AirApplication.getAppContext(), ReportDescriptionPage1.class);
                }

//                intentData.putExtra(InspectionReportPage.TAG, true);
//                intentData.putExtra(InspectionReportPage.TYPE, "健康報告");
//                intentData.putExtra("url", pageUrl);
                intentData.putExtra(Constant.NOTICE_FUNCTION_TYPE_DOWNLOAD_DATA, true);
                intentData.putExtra(Constant.NOTICE_FUNCTION_TYPE_PACIENT, true);
            } else if (Tag.contentEquals("LifeNotice")) {

//                LifeNotice lifeNotice = new LifeNotice();
//                ApiResult result = lifeNotice.getLifeNotice(token.getTokenId());F
//                Log.e(TAG, "lifeNotice result = " + result.getData());
//                simpleDatabase.setValueByKey(Constant.LIFE_NOTICE_DATE, result.getData());

                intentData = new Intent(AirApplication.getAppContext(), LiftSuggestListPage.class);
                intentData.putExtra(Constant.NOTICE_FUNCTION_TYPE_LIFE, true);
            }

            NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);
            long time = System.currentTimeMillis();
            int modTime = (int) (time % 10000000);
            PendingIntent contentIntent = PendingIntent.getActivities(this, modTime, new Intent[]{intentData}, PendingIntent.FLAG_UPDATE_CURRENT);

            Log.e(TAG, "messageID: " + messageID);
            Log.e(TAG, "messageID hash: " + messageID.hashCode());

            Uri uri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

            NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(this, getString(R.string.default_notification_channel_id))
                    .setSmallIcon(R.mipmap.app_notification_icon)
                    .setContentTitle(title)
                    .setContentText(body)
                    .setVibrate(new long[]{1000, 1000, 1000, 1000})
                    .setLargeIcon(BitmapFactory.decodeResource(getResources(), R.mipmap.ic_launcher))
                    .setSound(uri)
                    .setContentIntent(contentIntent)
                    .setAutoCancel(true)
                    .setPriority(NotificationCompat.PRIORITY_MAX);

            notificationManager.notify(modTime, mBuilder.build());

        } else {
            Log.e(TAG, "notification is null");



            SimpleDatabase simpleDatabase = new SimpleDatabase();
            simpleDatabase.setValueByKey(Constant.HEALTH_REPORT, 1);
            simpleDatabase.setValueByKey(Constant.IMMEDIATE_IMPROVEMENT, 1);

            Intent intent = new Intent(Constant.GET_NOTIFICATION);
            sendBroadcast(intent);

            Map<String, String> data = remoteMessage.getData();

            String title = data.get("title");
            String body = data.get("body");
            String messageID = remoteMessage.getMessageId();

            Log.e(TAG, "messageID: " + messageID);
            Log.e(TAG, "messageID hash: " + messageID.hashCode());

            Uri uri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

            Token token = new Token();
            Log.e(TAG, "onMessageReceived = " + " remoteMessage = " + remoteMessage.getData());

            String Tag = remoteMessage.getData().get("Tag");
            Intent intentData = null;


            if (Tag.contentEquals("PacientNotice")) {
//                HealthSummary healthSummary = new HealthSummary();
//                ApiResult result = healthSummary.getHealthSummary(token.getTokenId());
                boolean isIntroReport = simpleDatabase.getBooleanValueByKey(Constant.IS_NOT_HEALTH_REPORT, Constant.IS_NOT_HEALTH_REPORT_DEFAUL);

                if (isIntroReport) {
                    intentData = new Intent(AirApplication.getAppContext(), HealthRecommendationPage2.class);
                } else {
                    intentData = new Intent(AirApplication.getAppContext(), HealthDescriptionPage1.class);
                }
//                JSONObject jsonObject = result.getDataJSONObject();
//                intentData.putExtra("url", jsonObject.optString("pageurl"));
                intentData.putExtra(Constant.NOTICE_FUNCTION_TYPE_DOWNLOAD_DATA, true);
                intentData.putExtra(Constant.NOTICE_FUNCTION_TYPE_PACIENT, true);
            } else if (Tag.contentEquals("LifeNotice")) {
//                LifeNotice lifeNotice = new LifeNotice();
//                ApiResult result = lifeNotice.getLifeNotice(token.getTokenId());
//                Log.e(TAG, "lifeNotice result = " + result.getData());
//                simpleDatabase.setValueByKey(Constant.LIFE_NOTICE_DATE, result.getData());

                intentData = new Intent(AirApplication.getAppContext(), LiftSuggestListPage.class);
                intentData.putExtra(Constant.NOTICE_FUNCTION_TYPE_LIFE, true);
            }

            NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);
            long time = System.currentTimeMillis();
            int modTime = (int) (time % 10000000);
            PendingIntent contentIntent = PendingIntent.getActivities(this, modTime, new Intent[]{intentData}, PendingIntent.FLAG_UPDATE_CURRENT);


            NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(this, getString(R.string.default_notification_channel_id))
                    .setSmallIcon(R.mipmap.app_notification_icon)
                    .setContentTitle(title)
                    .setContentText(body)
                    .setVibrate(new long[]{1000, 1000, 1000, 1000})
                    .setLargeIcon(BitmapFactory.decodeResource(getResources(), R.mipmap.ic_launcher))
                    .setSound(uri)
                    .setContentIntent(contentIntent)
                    .setAutoCancel(true)
                    .setPriority(NotificationCompat.PRIORITY_MAX);

            notificationManager.notify(modTime, mBuilder.build());
        }
    }

    /**
     * Called when message is received.
     *
     * @param from SenderID of the sender.
     * @param data Data bundle containing message data as key/value pairs.
     *             For Set of keys use data.keySet().
     */
    // [START receive_message]
    public void onMessageReceived(String from, Bundle data) {
        Log.e(TAG, "onMessageReceived");
//        String message = data.getString("message");
        String GCMTag = data.getString("Tag");

//        Log.d(TAG, "From: " + from);
//        Log.d(TAG, "GCMTag: " + GCMTag);
//        Log.d(TAG, "data: " + data.toString());
//        Log.d(TAG, "Message: " + message);
//        Log.d(TAG, "Tag: " + message);

        String title = data.getString("title");
        String body = data.getString("body");

//        NotificationMessage notificationMessage = new NotificationMessage(this);
//        notificationMessage.showTestNotification("GCM", "我收到了，我收到GCM的訊號了");

        String detail = data.getString("Detail");

        JSONObject DetailData = null;

        try {
            DetailData = new JSONObject(detail);
            DetailData.put("Code", GCMTag);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        if (DetailData != null) {
//            Intent intent = new Intent(DownLoadAlertedNoticeService.StartDownLoad);
//            sendBroadcast(intent);
//
//            int intNotificationID = Integer.valueOf(DetailData.optString("NotificationNum", "0"));
//            notificationMessage.newShowNotificationByApi(DetailData.optString("Title"), DetailData.optString("Body"), DetailData.toString(), intNotificationID);
        }

        //TestCode
        if (GCMTag != null && GCMTag.contentEquals("Silent")) {
//            ChurchDataBase thisDataBase = new ChurchDataBase(this);
//            thisDataBase.open();
//
//            if(thisDataBase != null)
//            {
//                UserPermission user = thisDataBase.getUserData();
//
//                if(user != null)
//                {
//                    Log.d(TAG, "start count unread notification !!");
//
//                    Intent intent = new Intent(this, DownLoadAlertedNoticeService.class);
//                    intent.putExtra(DownLoadAlertedNoticeService.UserTokeIDKeyName, user.getProcessTicketID());
//                    intent.putExtra(DownLoadAlertedNoticeService.UserOrgIDKeyName, user.getUserOrganizationId());
//                    startService(intent);
//                }
//            }
        }


        if (from.startsWith("/topics/")) {
            // message received from some topic.
        } else {
            // normal downstream message.
        }

//        Intent notifyIntent = new Intent("com.HotelityIn.CSS_SplendorHotel_user.HaveNotice");
//        sendBroadcast(notifyIntent);
//
//        boolean isInBackground = Air.isApplicationSentToBackground(this);
//        Log.e(TAG, "isInBackground: "+isInBackground);

//        if(GCMTag != null)
//        {
//            if(GCMTag.contentEquals("LoadMessage"))
//            {
//                Air.appendLog("收到 下載話紀錄 的GCM");
//
//                Intent intent = new Intent(this, NewInstantMessage.class);
//                startService(intent);
//            }
//            else if(GCMTag.contentEquals("StatusService"))
//            {
//                Air.appendLog("收到 員工端按確認收到 的GCM");
//
//                // 叫,員工有接受服務
//                boolean isInBackground = Air.isApplicationSentToBackground(this);
//
//                JSONObject detailJSON = null;
//
//                try {
//                    String strDetail = data.getString("Detail");
//                    detailJSON = new JSONObject(strDetail);
//                } catch (Exception e) {
//                    e.printStackTrace();
//                }
//
//                if(detailJSON != null)
//                {
//                    String status = detailJSON.optString("Status");
//
//                    if(status.contentEquals("Check"))
//                    {
//                        if(isInBackground == false)
//                        {
//                            Intent intent = new Intent("com.HotelityIn.CSS_SplendorHotel_user.ServiceStatus");
//                            sendBroadcast(intent);
//                        }
//                        else
//                        {
//                            NotificationMessage NotificationMessage = new NotificationMessage(this);
//                            Intent intent = new Intent(this, MainActivity.class);
//                            intent.setType("NewAlert");
//
//                            NotificationMessage.showNormalNotification(0, intent);
//                        }
//                    }
//                }
//            }
//            else if(GCMTag.contentEquals("Notice"))
//            {
//                Air.appendLog("收到 推播 收到的GCM");
//
//                Intent intent = new Intent(this, PushNotifyCationService.class);
//                startService(intent);
//            }
//            else if(GCMTag.contentEquals("Order"))
//            {
//                Air.appendLog("收到 訂單 收到的GCM");
//
//                Intent intent1 = new Intent(this, PushNotifyCationService.class);
//                intent1.putExtra("Order", true);
//                startService(intent1);
//
//                //更新客戶的訂單
//                Intent intent = new Intent();
//                intent.setAction("com.HotelityIn.CSS_SplendorHotel_user.GCMTag.Order");
//                sendBroadcast(intent);
//            }
//            else if(GCMTag.contentEquals("WaitCheck"))
//            {
//
//            }
//            else if(GCMTag.contentEquals("CheckIn"))
//            {
//
//            }
//        }

        // [START_EXCLUDE]
        /**
         * Production applications would usually process the message here.
         * Eg: - Syncing with server.
         *     - Store message in local database.
         *     - Update UI.
         */

        /**
         * In some cases it may be useful to show a notification indicating to the user
         * that a message was received.
         */
//        sendNotification(message);
        // [END_EXCLUDE]
    }

    private JSONObject parseStringToJSON(String jsonString) {
        if (jsonString != null) {
            try {
                JSONObject JSONData = new JSONObject(jsonString);

                return JSONData;
            } catch (JSONException e) {
                e.printStackTrace();

                return null;
            }
        }

        return null;
    }

    // [END receive_message]

//    /**
//     * Create and show a simple notification containing the received GCM message.
//     *
//     * @param message GCM message received.
//     */
//    private void sendNotification(String message) {
//        Intent intent = new Intent(this, MainActivity.class);
//        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0 /* Request code */, intent,
//                PendingIntent.FLAG_ONE_SHOT);
//
//        Uri defaultSoundUri= RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
//        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this)
//                .setSmallIcon(R.drawable.ic_stat_ic_notification)
//                .setContentTitle("GCM Message")
//                .setContentText(message)
//                .setAutoCancel(true)
//                .setSound(defaultSoundUri)
//                .setContentIntent(pendingIntent);
//
//        NotificationManager notificationManager =
//                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
//
//        notificationManager.notify(0 /* ID of notification */, notificationBuilder.build());
//    }
}
