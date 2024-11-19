package com.v7idea.template.Tool;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;


import com.v7idea.healthkit.R;
import com.v7idea.template.DataBase.SimpleDatabase;

import org.joda.time.DateTime;

import java.util.Random;

/**
 * Created by mortal on 15/10/21.
 */
public class NotificationMessage {
    private final String tag = "NotificationMessage";
    private Context context = null;
    private NotificationManager mManager = null;
    private SimpleDatabase mDataBase = null;

    private long[] VibratePattern = new long[]{1000, 1000, 1000, 1000};

    public NotificationMessage(Context context) {
        this.context = context;
        mManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        mDataBase = new SimpleDatabase();
    }

    public NotificationMessage(Context context, NotificationManager NotificationManager, SimpleDatabase HotelDatabase) {
        this.context = context;
        this.mManager = NotificationManager;
        this.mDataBase = HotelDatabase;
    }

    public void showTestNotification(String title, String showString) {
        Notification.Builder builder = getDefaultNotificationBuilder();
        builder.setTicker(showString);

        Intent intent = new Intent();

        //Navigator to the new activity when click the notification title
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        builder.setContentIntent(pendingIntent).setContentTitle(title).setContentText(showString);
        builder.setContentInfo(showString);

        mManager.cancel(0);
        mManager.notify(0, builder.getNotification());
    }

    public int getNotificationIcon() {
        boolean useWhiteIcon = (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP);
        return useWhiteIcon ? R.mipmap.app_notification_icon : R.mipmap.ic_launcher;
    }

    public void showNotification(Notification notification){
        mManager.cancel(0);
        mManager.notify(0, notification);
    }

    public Notification.Builder getDefaultNotificationBuilder() {
        Notification.Builder builder = new Notification.Builder(context);
//        builder.setSmallIcon(R.mipmap.appicon_notification_icon);

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
        {
//            int color = 0x008000;
            builder.setColor(context.getResources().getColor(android.R.color.transparent));
            builder.setSmallIcon(R.mipmap.app_notification_icon);
            builder.setLargeIcon(BitmapFactory.decodeResource(context.getResources(), R.mipmap.ic_launcher));
        }
        else
        {
            builder.setSmallIcon(R.mipmap.app_notification_icon);
        }

        builder.setAutoCancel(true);
        builder.setVibrate(VibratePattern);
        builder.setLights(-2, 1000, 1000);
        Uri uri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        builder.setSound(uri);

//        boolean isRingNotice = mDataBase.getBooleanValueByKey(SimpleDatabase.SETTING_RING, true);
//        boolean isShockNotice = mDataBase.getBooleanValueByKey(SimpleDatabase.SETTING_SHOCK, true);
//        boolean isLedNotice = mDataBase.getBooleanValueByKey(SimpleDatabase.SETTING_LED, true);
//
//        if (isShockNotice) {
//            builder.setVibrate(VibratePattern);
//        }
//
//        if (isLedNotice) {
//            builder.setLights(-2, 1000, 1000);
//        }
//
//        if (isRingNotice) {
//            Uri uri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
//            builder.setSound(uri);
//        }

        return builder;
    }

    //    public void showNormalNotification(int requestCode, Intent intent)
//    {
//        boolean isOpenNotification = mDataBase.getBooleanValueByKey(HotelDatabase.IsOpenNotification, true);
//
//        long SuspendTime = mDataBase.getLongValueByKey(HotelDatabase.SuspendTime, 0l);
//
//        DateTime CurrentNoticeTime = new DateTime();
//        long longCurrentNoticeTime = CurrentNoticeTime.getMillis();
//
//        if(SuspendTime > 0 && SuspendTime <= longCurrentNoticeTime)
//        {
//            mDataBase.setValueByKey(HotelDatabase.SuspendTime, 0l);
//            mDataBase.setValueByKey(HotelDatabase.SETTING_SUSPEND, false);
//        }
//
//        if(isOpenNotification)
//        {
//            boolean isSuspendNotice = mDataBase.getBooleanValueByKey(HotelDatabase.SETTING_SUSPEND, false);
//
//            Notification.Builder builder = getDefaultNotificationBuilder();
//
//            if(!isSuspendNotice)
//            {
//                String notifyString = context.getResources().getString(R.string.you_have_new_message);
//                DebugLog.print(tag, DebugLog.FlowParams, Color.RED, "notifyString: " + notifyString);
//
//                if(notifyString != null)
//                {
//                    builder.setTicker(notifyString);
//
//                    //Navigator to the new activity when click the notification title
//
//                    PendingIntent pendingIntent = PendingIntent.getActivity(context, requestCode, intent, PendingIntent.FLAG_UPDATE_CURRENT);
//                    builder.setContentIntent(pendingIntent).setContentTitle(context.getResources().getString(R.string.app_name)).setContentText(notifyString);
//                    builder.setContentInfo(notifyString);
//
//                    mManager.cancel(requestCode);
//                    mManager.notify(requestCode, builder.getNotification());
//                }
//            }
//        }
//    }
//
//    public void newShowNotificationByApi(String strTitle, String strContent, String dataString
//            , int notifyCationID) {
////        DebugLog.print(tag, DebugLog.FlowDetailParams, Color.RED, "notifyCationID: "+notifyCationID);
//
//        long SuspendTime = mDataBase.getLongValueByKey(SimpleDatabase.SuspendTime, 0l);
//
//        DateTime CurrentNoticeTime = new DateTime();
//        long longCurrentNoticeTime = CurrentNoticeTime.getMillis();
//
//        if (SuspendTime > 0 && SuspendTime <= longCurrentNoticeTime) {
//            mDataBase.setValueByKey(SimpleDatabase.SuspendTime, 0l);
//            mDataBase.setValueByKey(SimpleDatabase.SETTING_NOT_SUSPEND, true);
//        }
//
//        boolean isSuspendNotNotice = mDataBase.getBooleanValueByKey(SimpleDatabase.SETTING_NOT_SUSPEND, true);
//
//        if (isSuspendNotNotice) {
//            Notification.Builder builder = getDefaultNotificationBuilder();
//
//            Intent intent = new Intent(context, HandlePushNotifyCationActivity.class);
//            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//            intent.putExtra("TargetDataString", dataString);
//            intent.putExtra("isFull", 1);
//
//            //PendingIntent 的識別就靠requestCode, 千萬千萬千萬千萬千萬千萬千萬別設一樣，在相同的requestCode出來的PendingIntent是一樣
//            //可能連資料都一樣，所以再重覆一次，requestCode千萬千萬千萬千萬千萬千萬千萬別設一樣
//            PendingIntent pendingIntent = PendingIntent.getActivity(context, notifyCationID, intent, PendingIntent.FLAG_UPDATE_CURRENT);
//
//            Intent intent2 = new Intent(context, HandlePushNotifyCationActivity.class);
//            intent2.putExtra("TargetDataString", dataString);
//            intent2.putExtra("isFull", 2);
//
//            Random RandomRequestCode = new Random();
//            int intFullScreenRequestCode = Math.abs(RandomRequestCode.nextInt());
//            PendingIntent pendingIntentFull = PendingIntent.getActivity(context, intFullScreenRequestCode, intent2, PendingIntent.FLAG_UPDATE_CURRENT);
//
//            builder.setContentTitle(strTitle)
//                    .setContentText(strContent)
//                    .setContentInfo(context.getResources().getString(R.string.app_name))
//                    .setTicker(strTitle)
//                    .setContentIntent(pendingIntent)
//                    .setFullScreenIntent(pendingIntentFull, true)//在5.0的手機才會有在Header彈出視窗的動作，上下兩個都要設，要參考Google文件
//                    .setPriority(Notification.PRIORITY_HIGH);//在5.0的手機才會有在Header彈出視窗的動作
//
//            Notification notification = builder.build();
//            mManager.notify(notifyCationID, notification);
//        }
//    }
}
