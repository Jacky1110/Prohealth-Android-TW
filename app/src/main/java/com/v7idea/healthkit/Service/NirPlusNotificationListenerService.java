package com.v7idea.healthkit.Service;


import android.content.Context;
import android.service.notification.NotificationListenerService;
import android.service.notification.StatusBarNotification;
import android.util.Log;

import com.v7idea.healthkit.Constant;
import com.v7idea.healthkit.Domain.LifeNoticeData;
import com.v7idea.healthkit.Model.HealthReport;
import com.v7idea.healthkit.Model.LifeNotice;
import com.v7idea.healthkit.Model.Token;
import com.v7idea.template.DataBase.SimpleDatabase;
import com.v7idea.template.Tool.ApiResult;
import com.v7idea.template.Tool.DebugLog;
import com.v7idea.template.Tool.V7ideaAsyncTask;

import org.json.JSONObject;

import java.util.ArrayList;

import me.leolin.shortcutbadger.ShortcutBadger;

/**
 * Created by mortal on 2018/11/22.
 */

public class NirPlusNotificationListenerService extends NotificationListenerService
{
    Context context = null;
    private static final String TAG = "NirPlusNotificationListenerService";

    @Override
    public void onNotificationPosted(StatusBarNotification sbn)
    {

        String sbnPackageName = sbn.getPackageName();

        DebugLog.e(TAG, "sbnPackageName1 is: " + sbnPackageName);

        if (sbnPackageName != null && sbnPackageName.contentEquals("com.prohealth.nirplus"))
        {
            context = this;

            GetLifeNoticIsRead getLifeNoticIsRead = new GetLifeNoticIsRead();
            getLifeNoticIsRead.execute();

            GetReportIsRead getReportIsRead = new GetReportIsRead();
            getReportIsRead.execute();

        }

        super.onNotificationPosted(sbn);
    }


    @Override
    public void onNotificationRemoved(StatusBarNotification sbn)
    {
//        DebugLog.e(TAG, "onNotificationRemoved");
        super.onNotificationRemoved(sbn);
    }

    @Override
    public void onListenerConnected()
    {
//        DebugLog.e(TAG, "onListenerConnected");
        super.onListenerConnected();
    }

    private class GetLifeNoticIsRead extends V7ideaAsyncTask<String, ApiResult>
    {
        private static final String TAG = "GetLifeNoticIsRead";
        //        private NotifyDialog progress = null;
        private LifeNotice lifeNotice = null;


        @Override
        protected void onPreExecute()
        {
            super.onPreExecute();
            lifeNotice = new LifeNotice();
        }

        @Override
        public void ifNeedCloseSomeThing()
        {
        }

        @Override
        public void downLoadSuccess(ApiResult result)
        {
            Log.e(TAG, "downLoadSuccess = " + result.toString());

            int badgeCount = getLifeNoticeCount(result.getData());
            SimpleDatabase simpleDatabase = new SimpleDatabase();

            if (badgeCount > 0)
            {
                simpleDatabase.setValueByKey(Constant.NOT_READ_Count, badgeCount);
                ShortcutBadger.applyCount(context, badgeCount); //for 1.1.4+
            }
            else
            {
                simpleDatabase.setValueByKey(Constant.NOT_READ_Count, 0);
            }
        }

        @Override
        public void downLoadFail(ApiResult apiResult)
        {
            Log.e(TAG, "downLoadFail = " + apiResult.toString());
        }

        @Override
        protected ApiResult doInBackground(String... params)
        {

            Token token = new Token();
            return lifeNotice.getLifeNotice(token.getTokenId());
        }

    }


    private int getLifeNoticeCount(String dataString)
    {
        LifeNoticeData lifeNoticeData = new LifeNoticeData();
        ArrayList<LifeNoticeData> data = lifeNoticeData.parseData(dataString);
        int count = 0;
        if (data != null)
        {
            for (int i = 0; i < data.size(); i++)
            {
                if (data.get(i).getIfRead().contentEquals("0"))
                {
                    count++;
                }
            }
        }
        return count;
    }


    private class GetReportIsRead extends V7ideaAsyncTask<String, ApiResult>
    {
        private HealthReport healthReport = null;
        private static final String TAG = "GetReportIsRead";

        @Override
        protected void onPreExecute()
        {
            super.onPreExecute();
            healthReport = new HealthReport();
        }

        @Override
        public void ifNeedCloseSomeThing()
        {

        }

        @Override
        public void downLoadSuccess(ApiResult result)
        {
            JSONObject jsonObject = result.getDataJSONObject();
            String IfRead = jsonObject.optString("IfRead");
            SimpleDatabase simpleDatabase = new SimpleDatabase();
            if (IfRead.contentEquals("0"))
            {
                int badgeCount = simpleDatabase.getIntValueByKey(Constant.NOT_READ_Count, 0);
                simpleDatabase.setValueByKey(Constant.NOT_READ_Count, (badgeCount + 1));
                ShortcutBadger.applyCount(context, (badgeCount + 1)); //for 1.1.4+
            }
        }

        @Override
        public void downLoadFail(ApiResult apiResult)
        {
            Log.e(TAG, "downLoadFail = " + apiResult.toString());
        }

        @Override
        protected ApiResult doInBackground(String... params)
        {
            Token token = new Token();
            return healthReport.getHealthReport(token.getTokenId());
        }
    }
}
