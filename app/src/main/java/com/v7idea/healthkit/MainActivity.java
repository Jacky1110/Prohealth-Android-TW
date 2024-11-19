package com.v7idea.healthkit;

import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import androidx.core.app.NotificationManagerCompat;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.v7idea.healthkit.Domain.LifeNoticeData;
import com.v7idea.healthkit.Model.FunctionList;
import com.v7idea.healthkit.Model.HealthCheck;
import com.v7idea.healthkit.Model.HealthReport;
import com.v7idea.healthkit.Model.HealthSummary;
import com.v7idea.healthkit.Model.LifeNotice;
import com.v7idea.healthkit.Model.Member;
import com.v7idea.healthkit.Model.Token;

import com.v7idea.template.Activity.BaseActivity;
import com.v7idea.template.AirApplication;
import com.v7idea.template.DataBase.SimpleDatabase;
import com.v7idea.template.Tool.ApiResult;
import com.v7idea.template.Tool.DebugLog;
import com.v7idea.template.Tool.ListItem;
import com.v7idea.template.Tool.NotifyDialog;
import com.v7idea.template.Tool.Utils;
import com.v7idea.template.Tool.V7ideaAsyncTask;
import com.v7idea.template.Tool.ViewScaling;
import com.v7idea.template.View.AutoReleaseImageView;
import com.v7idea.template.View.SpecialListView;
import com.v7idea.template.View.V7TitleView;
import com.v7idea.template.View.V7ideaBaseAdapter;

import org.joda.time.DateTime;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Set;

/**
 * 2019/1/24 ＊確認使用的頁面 主頁面
 */
public class MainActivity extends BaseActivity
{
    private final static String TAG = "MainActivity";

    private Member member = null;
    private FunctionListAdapter functionListAdapter = null;

    private SimpleDatabase simpleDatabase = null;

    private boolean isRunning = false;

    private Handler handler = new Handler();
    private Handler lifeNoticeHandler = new Handler();

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        simpleDatabase = new SimpleDatabase();
        simpleDatabase.setValueByKey(Constant.TEMP_ACCOUNT, "");
        simpleDatabase.setValueByKey(Constant.TEMP_PASSWORD, "");

        AutoReleaseImageView Header = (AutoReleaseImageView) ViewScaling.findViewByIdAndScale(currentActivity, R.id.MainActivity_SpecialImageView_Header);

        LinearLayout ScrollContainer = (LinearLayout) ViewScaling.findViewByIdAndScale(currentActivity, R.id.MainActivity_LinearLayout_contentLayout);

        AutoReleaseImageView TopBannerBackground = (AutoReleaseImageView) ViewScaling.findViewByIdAndScale(currentActivity, R.id.MainActivity_SpecialImageView_TopBannerBackground);
        ViewScaling.setImageView(TopBannerBackground);

        SpecialListView showFunctionList = (SpecialListView) ViewScaling.findViewByIdAndScale(currentActivity, R.id.MainActivity_SpecialListView_FunctionList);
        showFunctionList.setOnItemClickListener(onItemClickListener);

        functionListAdapter = new FunctionListAdapter();
        showFunctionList.setAdapter(functionListAdapter);

        FunctionList functionList = new FunctionList();
        functionListAdapter.setData(functionList.getFunctionArray());

        member = new Member();

        handler.post(requestHealthReport);
        lifeNoticeHandler.post(requestLifeNotice);

        if (isNotificationListenerEnabled(this) == false)
        {
            AirApplication application = (AirApplication) getApplication();

            if (application.isThisTimeAsk == false)
            {
                int isAsk = simpleDatabase.getIntValueByKey("ManagerIcon", 0);

                if (isAsk == 0)
                {

                    application.isThisTimeAsk = true;

                    AlertDialog.Builder alertBuilder = new AlertDialog.Builder(currentActivity);
                    alertBuilder.setMessage("請問您是否同意健康快篩透過通知來更新資料？");
                    alertBuilder.setPositiveButton("同意", new DialogInterface.OnClickListener()
                    {
                        @Override
                        public void onClick(DialogInterface dialog, int which)
                        {
                            Intent intent = new Intent(Settings.ACTION_NOTIFICATION_LISTENER_SETTINGS);
//            intent.setFlags(FLAG_ACTIVITY_NEW_TASK);
                            startActivity(intent);
                        }
                    });
                    alertBuilder.setNegativeButton("不同意", new DialogInterface.OnClickListener()
                    {
                        @Override
                        public void onClick(DialogInterface dialog, int which)
                        {
                            simpleDatabase.setValueByKey("ManagerIcon", 1);
                        }
                    });
                    alertBuilder.setCancelable(true);

                    AlertDialog dialog = alertBuilder.show();
                    dialog.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(Color.parseColor("#666666"));
                    dialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(Color.parseColor("#666666"));
                }
            }
        }
    }

    public boolean isNotificationListenerEnabled(Context context)
    {
        Set<String> packageNames = NotificationManagerCompat.getEnabledListenerPackages(this);
        if (packageNames.contains(context.getPackageName()))
        {
            return true;
        }
        return false;
    }


    @Override
    protected void onResume()
    {
        super.onResume();

        registerReceiver(receiver, new IntentFilter(Constant.GET_NOTIFICATION));
    }

    @Override
    protected void onPause()
    {
        super.onPause();
        unregisterReceiver(receiver);

        if (isFinishing())
        {

            isRunning = true;
            handler.removeCallbacks(requestHealthReport);
            lifeNoticeHandler.removeCallbacks(requestLifeNotice);
        }
    }

    private Runnable requestHealthReport = new Runnable()
    {
        @Override
        public void run()
        {
            downLoadHealthReport();
            handler.postDelayed(this, 120000);
        }
    };

    private Runnable requestLifeNotice = new Runnable()
    {
        @Override
        public void run()
        {
            downLoadLifeNotice();
            lifeNoticeHandler.postDelayed(this, 120000);
        }
    };
    private BroadcastReceiver receiver = new BroadcastReceiver()
    {
        @Override
        public void onReceive(Context context, Intent intent)
        {
            downLoadHealthReport();
        }
    };

    private void downLoadHealthReport()
    {
        if (isRunning == false)
        {
            if (isConnectedToNetworkNotAlert() && isFinishing() == false)
            {
                DownLoadHealthReport downLoadHealthReport = new DownLoadHealthReport(false);
                downLoadHealthReport.execute();

                DebugLog.e(TAG, "downLoadHealthReport");

                DownLoadHealthSummary downLoadHealthSummary = new DownLoadHealthSummary(false);
                downLoadHealthSummary.execute();
                DebugLog.e(TAG, "downLoadHealthSummary");
            }
        }
    }

    private void downLoadLifeNotice()
    {
        if (isConnectedToNetworkNotAlert() && isFinishing() == false)
        {
            DownLoadLifeNotice downLoadLifeNotice = new DownLoadLifeNotice();
            downLoadLifeNotice.execute();

            DebugLog.e(TAG, "downLoadLifeNotice");
        }
    }

    private AdapterView.OnItemClickListener onItemClickListener = new AdapterView.OnItemClickListener()
    {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, final int position, long id)
        {
            AirApplication.setOnclikcFeedback(view);
            Animation scaleAnimation = AnimationUtils.loadAnimation(currentActivity, R.anim.normal_button_alaph_animation);
            scaleAnimation.setAnimationListener(new Animation.AnimationListener()
            {
                @Override
                public void onAnimationStart(Animation animation)
                {

                }

                @Override
                public void onAnimationEnd(Animation animation)
                {
                    ListItem function = functionListAdapter.getItem(position);
                    String strFunctionName = function.getTitle();
                    //進行量測
                    if (function.getCode().contentEquals("detection"))
                    {
//                        取測試時間的API
                        if (isConnectedNetWork())
                        {
                            if (member.isTestUser.contentEquals("1"))
                            {
                                Log.e(TAG, "isTestUser = " + member.isTestUser + " 測試帳號");
                                DownLoadMemberData downLoadMemberData = new DownLoadMemberData();
                                downLoadMemberData.execute();
                            }
                            else if (member.isTestUser.contentEquals("0"))
                            {
                                Log.e(TAG, "isTestUser = " + member.isTestUser + " 一般帳號");
                                GetHealthCheck getHealthCheck = new GetHealthCheck();
                                getHealthCheck.execute();
                            }

                        }
                    }
                    //立即改善
                    else if (function.getCode().contentEquals("improvement"))
                    {
                        if (isConnectedNetWork())
                        {
                            DownLoadHealthSummary downLoadHealthSummary = new DownLoadHealthSummary(true);
                            downLoadHealthSummary.execute();
                        }
                    }
                    //健康報告
                    else if (function.getCode().contentEquals("report"))
                    {

                        if (isConnectedNetWork())
                        {
                            DownLoadHealthReport downLoadHealthReport = new DownLoadHealthReport(true);
                            downLoadHealthReport.execute();
                        }
                    }
                    //我要反饋
                    else if (function.getCode().contentEquals("feedBack"))
                    {
                        if (isConnectedNetWork())
                        {
                            Intent intent = new Intent(currentActivity, FeedbackPage.class);
                            startActivity(intent);
                            finish();
                            setTurnInNextPageAnimation(currentActivity);
                        }
                    }
                    //系統設定
                    else if (function.getCode().contentEquals("setting"))
                    {
                        Intent intent = new Intent(currentActivity, SystemSettingsPage.class);
                        startActivity(intent);
                        finish();
                        setTurnInNextPageAnimation(currentActivity);
                    }
                    //生活建議
                    else if (function.getCode().contentEquals("suggest"))
                    {
                        Intent intent = new Intent(currentActivity, LiftSuggestListPage.class);
                        startActivity(intent);
                        finish();
                        setTurnInNextPageAnimation(currentActivity);
                    }
                    else
                    {
                        Toast.makeText(currentActivity, strFunctionName + "施工中", Toast.LENGTH_SHORT).show();
                    }
                }


                @Override
                public void onAnimationRepeat(Animation animation)
                {

                }
            });

            view.setAnimation(scaleAnimation);
            view.startAnimation(scaleAnimation);
        }
    };

    private class FunctionListAdapter extends V7ideaBaseAdapter
    {

        private class ViewHolder
        {
            public RelativeLayout Container = null;

            public RelativeLayout IconContainer = null;
            public AutoReleaseImageView Icon = null;

            public V7TitleView ShowTitle = null;

            public RelativeLayout AlertIconContainer = null;
            public V7TitleView AlertIcon = null;

            public AutoReleaseImageView divider = null;

            public void init(View convertView)
            {
                Container = (RelativeLayout) ViewScaling.findViewByIdAndScale(convertView, R.id.FunctionListItem_RelativeLayout_Container);

                IconContainer = (RelativeLayout) ViewScaling.findViewByIdAndScale(convertView, R.id.FunctionListItem_RelativeLayout_IconContainer);
                Icon = (AutoReleaseImageView) ViewScaling.findViewByIdAndScale(convertView, R.id.FunctionListItem_AutoReleaseImageView_Icon);

                ShowTitle = (V7TitleView) ViewScaling.findViewByIdAndScale(convertView, R.id.FunctionListItem_AutoReleaseImageView_ShowTitle);

                AlertIconContainer = (RelativeLayout) ViewScaling.findViewByIdAndScale(convertView, R.id.FunctionListItem_RelativeLayout_AlertIconContainer);
                AlertIcon = (V7TitleView) ViewScaling.findViewByIdAndScale(convertView, R.id.FunctionListItem_AutoReleaseImageView_AlertIcon);

                divider = (AutoReleaseImageView) ViewScaling.findViewByIdAndScale(convertView, R.id.FunctionListItem_AutoReleaseImageView_divider);
            }
        }

        @Override
        public View getItemView(int position, View convertView, ViewGroup parent)
        {

            ViewHolder viewHolder = null;

            if (convertView == null)
            {
                convertView = getLayoutInflater().inflate(R.layout.function_list_item, null);

                viewHolder = new ViewHolder();
                viewHolder.init(convertView);

                convertView.setTag(viewHolder);
            }
            else
            {
                viewHolder = (ViewHolder) convertView.getTag();
            }

            ListItem item = getItem(position);

            if (item != null)
            {
                int iconID = item.getIconResourceID();
                String title = item.getTitle();
                int noticeNumber = item.getNoticeNumber();

                viewHolder.Icon.setImageResource(iconID);
                viewHolder.ShowTitle.setText(title);

                //立即改善
                if (item.getSerialNo() == 2)
                {
                    int count = simpleDatabase.getIntValueByKey(Constant.IMMEDIATE_IMPROVEMENT, 0);


                    if (count >= 1)
                    {
                        viewHolder.AlertIcon.setVisibility(View.VISIBLE);
                        viewHolder.AlertIcon.setText("1");
                    }
                    else
                    {
                        viewHolder.AlertIcon.setVisibility(View.INVISIBLE);
                    }
                }
                //健康報告
                else if (item.getSerialNo() == 3)
                {
                    int count = simpleDatabase.getIntValueByKey(Constant.HEALTH_REPORT, 0);

                    if (count >= 1)
                    {
                        viewHolder.AlertIcon.setVisibility(View.VISIBLE);
                        viewHolder.AlertIcon.setText("1");
                    }
                    else
                    {
                        viewHolder.AlertIcon.setVisibility(View.INVISIBLE);
                    }
                    //生活建議
                }
                else if (item.getSerialNo() == 4)
                {
                    String jsonData = simpleDatabase.getStringValueByKey(Constant.LIFE_NOTICE_DATE, "");
                    LifeNoticeData lifeNoticeData = new LifeNoticeData();
                    if (jsonData != null && jsonData.isEmpty() == false)
                    {
                        ArrayList<LifeNoticeData> data = lifeNoticeData.parseData(jsonData);
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
                        if (count >= 1)
                        {
                            if (count > 9)
                            {
                                viewHolder.AlertIcon.setText("9⁺");
                            }
                            else
                            {
                                viewHolder.AlertIcon.setText("" + count);
                            }
                            viewHolder.AlertIcon.setVisibility(View.VISIBLE);
                        }
                        else
                        {
                            viewHolder.AlertIcon.setVisibility(View.INVISIBLE);
                        }
                    }
                    else
                    {
                        viewHolder.AlertIcon.setVisibility(View.INVISIBLE);
                    }
                }
                else
                {
                    viewHolder.AlertIcon.setVisibility(View.INVISIBLE);
                }
            }

            return convertView;
        }
    }

//    private class LogoutTask extends V7ideaAsyncTask<String, ApiResult> {
//        private NotifyDialog progress = null;
//
//        @Override
//        protected void onPreExecute() {
//            super.onPreExecute();
//
////            progress = new NotifyDialog(currentActivity);
////            progress.settingProgressDialog();
//        }
//
//        @Override
//        public void downLoadSuccess(ApiResult result) {
//            Token token = new Token();
//            token.destroy();
//
//            Intent intent = new Intent(currentActivity, LoginPage.class);
//            startActivity(intent);
//            finish();
//            setBackInPrePageAnimation(currentActivity);
//        }
//
//        @Override
//        public void downLoadFail(ApiResult apiResult) {
//            if (apiResult.getErrorNo().contentEquals(Constant.ErrorCode.CODE_500)) {
//                showErrorAlert(getResources().getString(R.string.no_internet_error500));
//            } else if (apiResult.getErrorNo().contentEquals(Constant.ErrorCode.CODE_1002)) {
//                showErrorAlert(apiResult.getMessage(), new DialogInterface.OnClickListener() {
//                    @Override
//                    public void onClick(DialogInterface dialog, int which) {
//                        new Token().destroy();
//                        Intent intent = new Intent(currentActivity, LoginPage.class);
//                        startActivity(intent);
//                        finish();
//                        setBackInPrePageAnimation(currentActivity);
//
//                    }
//                });
//            } else if (apiResult.getErrorNo().contentEquals(Constant.ErrorCode.CODE_1003)) {
//                showErrorAlert(apiResult.getMessage(), new DialogInterface.OnClickListener() {
//                    @Override
//                    public void onClick(DialogInterface dialog, int which) {
//                        new Token().destroy();
//                        Intent intent = new Intent(currentActivity, LoginPage.class);
//                        startActivity(intent);
//                        finish();
//                        setBackInPrePageAnimation(currentActivity);
//
//                    }
//                });
//            } else {
//                showErrorAlert(apiResult.getMessage());
//            }
//        }
//
//        @Override
//        public void ifNeedCloseSomeThing() {
//            if (progress != null) {
//                progress.dismiss();
//                progress = null;
//            }
//        }
//
//        @Override
//        protected ApiResult doInBackground(String... params) {
//            Token token = new Token();
//            return token.deleteTokenID();
//        }
//
//    }

    private class GetHealthCheck extends V7ideaAsyncTask<String, ApiResult>
    {
        private HealthCheck healthCheck = null;

        private NotifyDialog progress = null;

        public GetHealthCheck()
        {
            healthCheck = new HealthCheck();
        }

        @Override
        protected void onPreExecute()
        {
            super.onPreExecute();

            progress = new NotifyDialog(currentActivity);
            progress.settingProgressDialog();
        }

        @Override
        public void ifNeedCloseSomeThing()
        {
//            if (progress != null) {
//                progress.dismiss();
//                progress = null;
//            }
        }

        @Override
        public void downLoadSuccess(ApiResult result)
        {
            if (progress != null)
            {
                progress.dismiss();
                progress = null;
            }
            DownLoadMemberData downLoadMemberData = new DownLoadMemberData();
            downLoadMemberData.execute();
        }

        @Override
        public void downLoadFail(ApiResult apiResult)
        {
            if (progress != null)
            {
                progress.dismiss();
                progress = null;
            }
            if (apiResult.getErrorNo().contentEquals(Constant.ErrorCode.CODE_500))
            {
                showErrorAlert(getResources().getString(R.string.no_internet_error500));
            }
            else if (apiResult.getErrorNo().contentEquals(Constant.ErrorCode.CODE_9999))
            {
                showErrorAlert(apiResult.getMessage());
            }
            else if (apiResult.getErrorNo().contentEquals(Constant.ErrorCode.CODE_1002))
            {
                showErrorAlert(apiResult.getMessage(), new DialogInterface.OnClickListener()
                {
                    @Override
                    public void onClick(DialogInterface dialog, int which)
                    {
                        new Token().destroy();
                        Intent intent = new Intent(currentActivity, LoginPage.class);
                        startActivity(intent);
                        finish();
                        setBackInPrePageAnimation(currentActivity);

                    }
                });
            }
            else if (apiResult.getErrorNo().contentEquals(Constant.ErrorCode.CODE_1003))
            {
                showErrorAlert(apiResult.getMessage(), new DialogInterface.OnClickListener()
                {
                    @Override
                    public void onClick(DialogInterface dialog, int which)
                    {
                        new Token().destroy();
                        Intent intent = new Intent(currentActivity, LoginPage.class);
                        startActivity(intent);
                        finish();
                        setBackInPrePageAnimation(currentActivity);

                    }
                });
            }
            else
            {
                showErrorAlert(apiResult.getMessage());
            }
        }

        @Override
        protected ApiResult doInBackground(String... params)
        {
            Token token = new Token();
            return healthCheck.getHealthCheck(token.getTokenId());
        }
    }

    private class DownLoadHealthSummary extends V7ideaAsyncTask<String, ApiResult>
    {
        private HealthSummary healthSummary = null;

        private NotifyDialog progress = null;
        private boolean isToReportPage = false;

        public DownLoadHealthSummary(boolean isToReportPage)
        {
            healthSummary = new HealthSummary();

            this.isToReportPage = isToReportPage;
        }


        @Override
        protected void onPreExecute()
        {
            super.onPreExecute();

            if (this.isToReportPage)
            {
                progress = new NotifyDialog(currentActivity);
                progress.settingProgressDialog();
            }
        }

        @Override
        public boolean isActivityNoFinish()
        {
            return !currentActivity.isFinishing();
        }

        @Override
        public void ifNeedCloseSomeThing()
        {
            if (progress != null)
            {
                progress.dismiss();
                progress = null;
            }
        }

        @Override
        public void downLoadSuccess(ApiResult result)
        {

            JSONObject jsonObject = result.getDataJSONObject();

            String IfRead = jsonObject.optString("IfRead");
            String pageUrl = jsonObject.optString("pageurl");

            if (this.isToReportPage == false)
            {

                String[] separated = pageUrl.split("/");

                if (separated.length >= 6)
                {
                    String reportID = separated[5];

                    if (IfRead.isEmpty() == false)
                    {
                        if (IfRead.contentEquals("0"))
                        {
                            simpleDatabase.setValueByKey(Constant.IMMEDIATE_IMPROVEMENT, 1);
                        }
                        else if (IfRead.contentEquals("1"))
                        {
                            simpleDatabase.setValueByKey(Constant.IMMEDIATE_IMPROVEMENT, 0);
                        }
                        functionListAdapter.notifyDataSetChanged();
                    }
                }
                return;
            }


            boolean isIntroReport = simpleDatabase.getBooleanValueByKey(Constant.IS_NOT_HEALTH_REPORT, Constant.IS_NOT_HEALTH_REPORT_DEFAUL);

            Intent intent = null;

            if (isIntroReport)
            {
                intent = new Intent(currentActivity, HealthRecommendationPage2.class);
            }
            else
            {
                intent = new Intent(currentActivity, HealthDescriptionPage1.class);
            }

            intent.putExtra("url", pageUrl);
            startActivity(intent);
            finish();
            setTurnInNextPageAnimation(currentActivity);
        }

        @Override
        public void downLoadFail(ApiResult apiResult)
        {
            if (apiResult.getErrorNo().contentEquals(Constant.ErrorCode.CODE_500))
            {
                showErrorAlert(getResources().getString(R.string.no_internet_error500));
            }
            else if (apiResult.getErrorNo().contentEquals(Constant.ErrorCode.CODE_1002))
            {
                showErrorAlert(apiResult.getMessage(), new DialogInterface.OnClickListener()
                {
                    @Override
                    public void onClick(DialogInterface dialog, int which)
                    {
                        new Token().destroy();
                        Intent intent = new Intent(currentActivity, LoginPage.class);
                        startActivity(intent);
                        finish();
                        setBackInPrePageAnimation(currentActivity);

                    }
                });
            }
            else if (apiResult.getErrorNo().contentEquals(Constant.ErrorCode.CODE_1003))
            {
                showErrorAlert(apiResult.getMessage(), new DialogInterface.OnClickListener()
                {
                    @Override
                    public void onClick(DialogInterface dialog, int which)
                    {
                        new Token().destroy();
                        Intent intent = new Intent(currentActivity, LoginPage.class);
                        startActivity(intent);
                        finish();
                        setBackInPrePageAnimation(currentActivity);

                    }
                });
            }
            else if (apiResult.getErrorNo().contentEquals(Constant.ErrorCode.CODE_1100))
            {
                if (this.isToReportPage == false)
                {
                    return;
                }
                Intent intent = new Intent(currentActivity, InspectionReportPage.class);
                intent.putExtra(InspectionReportPage.TAG, false);
                intent.putExtra(InspectionReportPage.TYPE, "立即改善");
                startActivity(intent);
                finish();
                setTurnInNextPageAnimation(currentActivity);
            }
            else if (apiResult.getErrorNo().contentEquals(Constant.ErrorCode.CODE_404))
            {
                if (this.isToReportPage == false)
                {
                    return;
                }
                Intent intent = new Intent(currentActivity, InspectionReportPage.class);
                intent.putExtra(InspectionReportPage.TAG, false);
                intent.putExtra(InspectionReportPage.TYPE, "立即改善");
                startActivity(intent);
                finish();
                setTurnInNextPageAnimation(currentActivity);
            }
            else if (apiResult.getErrorNo().contentEquals(Constant.ErrorCode.CODE_9000))
            {
                if (this.isToReportPage == false)
                {
                    return;
                }
                Intent intent = new Intent(currentActivity, InspectionReportPage.class);
                intent.putExtra(InspectionReportPage.TAG, false);
                intent.putExtra(InspectionReportPage.TYPE, "立即改善");
                intent.putExtra(InspectionReportPage.ERROR_MESSAGE, apiResult.getMessage());
                startActivity(intent);
                finish();
                setTurnInNextPageAnimation(currentActivity);
            }
            else if (apiResult.getErrorNo().contentEquals(Constant.ErrorCode.CODE_9001))
            {
                if (this.isToReportPage == false)
                {
                    return;
                }
                Intent intent = new Intent(currentActivity, InspectionReportPage.class);
                intent.putExtra(InspectionReportPage.TAG, false);
                intent.putExtra(InspectionReportPage.TYPE, "立即改善");
                intent.putExtra(InspectionReportPage.ERROR_MESSAGE, apiResult.getMessage());
                startActivity(intent);
                finish();
                setTurnInNextPageAnimation(currentActivity);
            }
            else if (apiResult.getErrorNo().contentEquals(Constant.ErrorCode.CODE_9999))
            {
                if (this.isToReportPage == false)
                {
                    return;
                }

                Intent intent = new Intent(currentActivity, InspectionReportPage.class);
                intent.putExtra(InspectionReportPage.TAG, false);
                intent.putExtra(InspectionReportPage.TYPE, "立即改善");
                intent.putExtra(InspectionReportPage.ERROR_MESSAGE, apiResult.getMessage());
                startActivity(intent);
                finish();
                setTurnInNextPageAnimation(currentActivity);
            }
            else
            {
                showErrorAlert(apiResult.getMessage());
            }
        }

        @Override
        protected ApiResult doInBackground(String... params)
        {
            Token token = new Token();
            return healthSummary.getHealthSummary(token.getTokenId());
        }
    }

    private class DownLoadHealthReport extends V7ideaAsyncTask<String, ApiResult>
    {
        private HealthReport healthReport = null;

        private NotifyDialog progress = null;

        private boolean isToReportPage = false;

        public DownLoadHealthReport(boolean isToReportPage)
        {
            healthReport = new HealthReport();

            this.isToReportPage = isToReportPage;
        }

        @Override
        protected void onPreExecute()
        {
            super.onPreExecute();

            if (this.isToReportPage)
            {
                progress = new NotifyDialog(currentActivity);
                progress.settingProgressDialog();
            }
        }

        @Override
        public boolean isActivityNoFinish()
        {
            return !currentActivity.isFinishing();
        }

        @Override
        public void ifNeedCloseSomeThing()
        {
            if (progress != null)
            {
                progress.dismiss();
                progress = null;
            }
        }

        @Override
        public void downLoadSuccess(ApiResult result)
        {

            isRunning = false;

            JSONObject jsonObject = result.getDataJSONObject();

            String IfRead = jsonObject.optString("IfRead");
            String pageUrl = jsonObject.optString("pageurl");
//            String lastDetection = jsonObject.optString("lastDetection");

            if (this.isToReportPage == false)
            {

                String[] separated = pageUrl.split("/");

                if (separated.length >= 6)
                {
                    String reportID = separated[5];

                    if (IfRead.isEmpty() == false)
                    {
                        if (IfRead.contentEquals("0"))
                        {
                            simpleDatabase.setValueByKey(Constant.HEALTH_REPORT, 1);
                        }
                        else if (IfRead.contentEquals("1"))
                        {
                            simpleDatabase.setValueByKey(Constant.HEALTH_REPORT, 0);
                        }
                        functionListAdapter.notifyDataSetChanged();
                    }
                }

//                if (lastDetection.isEmpty() == false)
//                {
//
//                    Boolean lastDetectionBoolean = simpleDatabase.getBooleanValueByKey(Constant.LAST_DETECTION, Constant.LAST_DETECTION_BOOLEAN);
//                    if (lastDetectionBoolean == false)
//                    {
//                        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
//                        Date date = null;
//                        try
//                        {
//                            date = dateFormat.parse(lastDetection);
//                        } catch (ParseException e)
//                        {
//                            e.printStackTrace();
//                        }
//
//                        DateTime dateTime = new DateTime(date);
//                        dateTime = dateTime.plusDays(7);
//                        dateTime = dateTime.plusHours(12);
//                        long intervalTime = dateTime.getMillis();
//                        AlarmManagerHelper.setAlarmTime(intervalTime, true);
//                        simpleDatabase.setValueByKey(Constant.LAST_DETECTION, true);
//                    }
//                }
                return;
            }

            boolean isIntroReport = simpleDatabase.getBooleanValueByKey(Constant.IS_NOT_INTRO_REPORT, Constant.IS_NOT_INTRO_REPORT_DEFAUL);

            Intent intent = null;

            if (isIntroReport)
            {
                intent = new Intent(currentActivity, InspectionReportPage.class);
            }
            else
            {
                intent = new Intent(currentActivity, ReportDescriptionPage1.class);
            }

            intent.putExtra(InspectionReportPage.TAG, true);
            intent.putExtra(InspectionReportPage.TYPE, "健康報告");
            intent.putExtra("url", pageUrl);
            startActivity(intent);
            finish();
            setTurnInNextPageAnimation(currentActivity);
        }

        @Override
        public void downLoadFail(ApiResult apiResult)
        {
            isRunning = false;

            if (apiResult.getErrorNo().contentEquals(Constant.ErrorCode.CODE_500))
            {
                showErrorAlert(getResources().getString(R.string.no_internet_error500));
            }
            else if (apiResult.getErrorNo().contentEquals(Constant.ErrorCode.CODE_1002))
            {
                showErrorAlert(apiResult.getMessage(), new DialogInterface.OnClickListener()
                {
                    @Override
                    public void onClick(DialogInterface dialog, int which)
                    {
                        new Token().destroy();
                        Intent intent = new Intent(currentActivity, LoginPage.class);
                        startActivity(intent);
                        finish();
                        setBackInPrePageAnimation(currentActivity);

                    }
                });
            }
            else if (apiResult.getErrorNo().contentEquals(Constant.ErrorCode.CODE_1003))
            {
                showErrorAlert(apiResult.getMessage(), new DialogInterface.OnClickListener()
                {
                    @Override
                    public void onClick(DialogInterface dialog, int which)
                    {
                        new Token().destroy();
                        Intent intent = new Intent(currentActivity, LoginPage.class);
                        startActivity(intent);
                        finish();
                        setBackInPrePageAnimation(currentActivity);

                    }
                });
            }
            else if (apiResult.getErrorNo().contentEquals(Constant.ErrorCode.CODE_1100))
            {
                if (this.isToReportPage == false)
                {
                    return;
                }

                Intent intent = new Intent(currentActivity, InspectionReportPage.class);
                intent.putExtra(InspectionReportPage.TAG, false);
                startActivity(intent);
                finish();
                setTurnInNextPageAnimation(currentActivity);
            }
            else if (apiResult.getErrorNo().contentEquals(Constant.ErrorCode.CODE_404))
            {
                if (this.isToReportPage == false)
                {
                    return;
                }

                Intent intent = new Intent(currentActivity, InspectionReportPage.class);
                intent.putExtra(InspectionReportPage.TYPE, "健康報告");
                intent.putExtra(InspectionReportPage.TAG, false);
                startActivity(intent);
                finish();
                setTurnInNextPageAnimation(currentActivity);
            }
            else if (apiResult.getErrorNo().contentEquals(Constant.ErrorCode.CODE_9000))
            {
                if (this.isToReportPage == false)
                {
                    return;
                }

                Intent intent = new Intent(currentActivity, InspectionReportPage.class);
                intent.putExtra(InspectionReportPage.TYPE, "健康報告");
                intent.putExtra(InspectionReportPage.TAG, false);
                intent.putExtra(InspectionReportPage.ERROR_MESSAGE, apiResult.getMessage());
                startActivity(intent);
                finish();
                setTurnInNextPageAnimation(currentActivity);
            }
            else if (apiResult.getErrorNo().contentEquals(Constant.ErrorCode.CODE_9001))
            {
                if (this.isToReportPage == false)
                {
                    return;
                }

                Intent intent = new Intent(currentActivity, InspectionReportPage.class);
                intent.putExtra(InspectionReportPage.TYPE, "健康報告");
                intent.putExtra(InspectionReportPage.TAG, false);
                intent.putExtra(InspectionReportPage.ERROR_MESSAGE, apiResult.getMessage());
                startActivity(intent);
                finish();
                setTurnInNextPageAnimation(currentActivity);
            }
            else if (apiResult.getErrorNo().contentEquals(Constant.ErrorCode.CODE_9999))
            {
                if (this.isToReportPage == false)
                {
                    return;
                }

                Intent intent = new Intent(currentActivity, InspectionReportPage.class);
                intent.putExtra(InspectionReportPage.TYPE, "健康報告");
                intent.putExtra(InspectionReportPage.TAG, false);
                intent.putExtra(InspectionReportPage.ERROR_MESSAGE, apiResult.getMessage());
                startActivity(intent);
                finish();
                setTurnInNextPageAnimation(currentActivity);
            }
            else
            {
                showErrorAlert(apiResult.getMessage());
            }
        }

        @Override
        protected ApiResult doInBackground(String... params)
        {
            isRunning = true;

            Token token = new Token();
            return healthReport.getHealthReport(token.getTokenId());
        }
    }


    private class DownLoadMemberData extends V7ideaAsyncTask<String, ApiResult>
    {

        private NotifyDialog progress = null;

        @Override
        protected void onPreExecute()
        {
            super.onPreExecute();

            progress = new NotifyDialog(currentActivity);
            progress.settingProgressDialog();
        }

        @Override
        public void ifNeedCloseSomeThing()
        {
//            if (progress != null) {
//                progress.dismiss();
//                progress = null;
//            }
        }

        @Override
        public boolean isActivityNoFinish()
        {
            return !currentActivity.isFinishing();
        }

        @Override
        public void downLoadSuccess(ApiResult result)
        {
            if (progress != null)
            {
                progress.dismiss();
                progress = null;
            }


            GetCheckReportIs9000 getCheckReportIs9000 = new GetCheckReportIs9000();
            getCheckReportIs9000.execute();


        }

        @Override
        public void downLoadFail(ApiResult apiResult)
        {
            if (progress != null)
            {
                progress.dismiss();
                progress = null;
            }
            if (apiResult.getErrorNo().contentEquals(Constant.ErrorCode.CODE_500))
            {
                showErrorAlert(getResources().getString(R.string.no_internet_error500));
            }
            else if (apiResult.getErrorNo().contentEquals(Constant.ErrorCode.CODE_1002))
            {
                showErrorAlert(apiResult.getMessage(), new DialogInterface.OnClickListener()
                {
                    @Override
                    public void onClick(DialogInterface dialog, int which)
                    {
                        new Token().destroy();
                        Intent intent = new Intent(currentActivity, LoginPage.class);
                        startActivity(intent);
                        finish();
                        setBackInPrePageAnimation(currentActivity);

                    }
                });
            }
            else if (apiResult.getErrorNo().contentEquals(Constant.ErrorCode.CODE_1003))
            {
                showErrorAlert(apiResult.getMessage(), new DialogInterface.OnClickListener()
                {
                    @Override
                    public void onClick(DialogInterface dialog, int which)
                    {
                        new Token().destroy();
                        Intent intent = new Intent(currentActivity, LoginPage.class);
                        startActivity(intent);
                        finish();
                        setBackInPrePageAnimation(currentActivity);

                    }
                });
            }
            else
            {
                showErrorAlert(apiResult.getMessage());
            }
        }

        @Override
        protected ApiResult doInBackground(String... params)
        {
            //上傳會員資料
            Token token = new Token();
            return member.getMemberData(token.getTokenId());
        }
    }

    private class DownLoadLifeNotice extends V7ideaAsyncTask<String, ApiResult>
    {

        //        private NotifyDialog progress = null;
        private LifeNotice lifeNotice = null;
        private LifeNoticeData lifeNoticeData = null;

        @Override
        protected void onPreExecute()
        {
            super.onPreExecute();
            lifeNotice = new LifeNotice();
//            progress = new NotifyDialog(currentActivity);
//            progress.settingProgressDialog();

            lifeNoticeData = new LifeNoticeData();
        }

        @Override
        public void ifNeedCloseSomeThing()
        {
//            if (progress != null) {
//                progress.dismiss();
//                progress = null;
//            }
        }

        @Override
        public void downLoadSuccess(ApiResult result)
        {

            simpleDatabase.setValueByKey(Constant.LIFE_NOTICE_DATE, result.getData());
            functionListAdapter.notifyDataSetChanged();

        }

        @Override
        public void downLoadFail(ApiResult apiResult)
        {

            if (apiResult.getErrorNo().contentEquals(Constant.ErrorCode.CODE_500))
            {
                showErrorAlert(getResources().getString(R.string.no_internet_error500));
                Intent intent = new Intent(currentActivity, MainActivity.class);
                startActivity(intent);
                finish();
                setBackInPrePageAnimation(currentActivity);
            }
            else if (apiResult.getErrorNo().contentEquals(Constant.ErrorCode.CODE_1002))
            {
                showErrorAlert(apiResult.getMessage(), new DialogInterface.OnClickListener()
                {
                    @Override
                    public void onClick(DialogInterface dialog, int which)
                    {
                        new Token().destroy();
                        Intent intent = new Intent(currentActivity, LoginPage.class);
                        startActivity(intent);
                        finish();
                        setBackInPrePageAnimation(currentActivity);

                    }
                });
            }
            else if (apiResult.getErrorNo().contentEquals(Constant.ErrorCode.CODE_1003))
            {
                showErrorAlert(apiResult.getMessage(), new DialogInterface.OnClickListener()
                {
                    @Override
                    public void onClick(DialogInterface dialog, int which)
                    {
                        new Token().destroy();
                        Intent intent = new Intent(currentActivity, LoginPage.class);
                        startActivity(intent);
                        finish();
                        setBackInPrePageAnimation(currentActivity);

                    }
                });
            }
            else
            {
                showErrorAlert(apiResult.getMessage());
            }
        }

        @Override
        protected ApiResult doInBackground(String... params)
        {

            Token token = new Token();
            return lifeNotice.getLifeNotice(token.getTokenId());
        }

    }

    private class GetCheckReportIs9000 extends V7ideaAsyncTask<String, ApiResult>
    {
        private HealthReport healthReport = null;

        private NotifyDialog progress = null;
        boolean isCODE_9000 = false;

        @Override
        protected void onPreExecute()
        {
            super.onPreExecute();

            if (progress == null)
            {
                progress = new NotifyDialog(currentActivity);
                progress.settingProgressDialog();
                healthReport = new HealthReport();
            }
        }

        @Override
        public boolean isActivityNoFinish()
        {
            return !currentActivity.isFinishing();
        }

        @Override
        public void ifNeedCloseSomeThing()
        {
            if (progress != null)
            {
                progress.dismiss();
                progress = null;
            }
        }

        @Override
        public void downLoadSuccess(ApiResult result)
        {
            isCODE_9000 = false;

            if (isCODE_9000 == false)
            {
                if (member.birthday.isEmpty() == false && Utils.checkDate(member.birthday)
                        && Utils.checkDateYear(member.birthday) && member.height.isEmpty() == false
                        && member.weight.isEmpty() == false)
                {
                    if (Integer.parseInt(member.height) > 0 && Integer.parseInt(member.weight) > 0)
                    {
                        Intent intent = new Intent(currentActivity, CheckWeightPage.class);
                        startActivity(intent);
                        finish();
                        setTurnInNextPageAnimation(currentActivity);
                    }
                    else
                    {
                        Intent intent = new Intent(currentActivity, UserBasicInformationInit.class);
                        startActivity(intent);
                        finish();
                        setTurnInNextPageAnimation(currentActivity);
                    }
                }
                else
                {
                    Intent intent = new Intent(currentActivity, UserBasicInformationInit.class);
                    startActivity(intent);
                    finish();
                    setTurnInNextPageAnimation(currentActivity);
                }
            }
        }

        @Override
        public void downLoadFail(ApiResult apiResult)
        {
            boolean isError = false;
            isCODE_9000 = false;
            if (apiResult.getErrorNo().contentEquals(Constant.ErrorCode.CODE_500))
            {
                showErrorAlert(getResources().getString(R.string.no_internet_error500));
                isError = true;
            }
            else if (apiResult.getErrorNo().contentEquals(Constant.ErrorCode.CODE_1002))
            {
                showErrorAlert(apiResult.getMessage(), new DialogInterface.OnClickListener()
                {
                    @Override
                    public void onClick(DialogInterface dialog, int which)
                    {
                        new Token().destroy();
                        Intent intent = new Intent(currentActivity, LoginPage.class);
                        startActivity(intent);
                        finish();
                        setBackInPrePageAnimation(currentActivity);

                    }
                });
                isError = true;
            }
            else if (apiResult.getErrorNo().contentEquals(Constant.ErrorCode.CODE_1003))
            {
                showErrorAlert(apiResult.getMessage(), new DialogInterface.OnClickListener()
                {
                    @Override
                    public void onClick(DialogInterface dialog, int which)
                    {
                        new Token().destroy();
                        Intent intent = new Intent(currentActivity, LoginPage.class);
                        startActivity(intent);
                        finish();
                        setBackInPrePageAnimation(currentActivity);

                    }
                });
                isError = true;
            }

            else if (apiResult.getErrorNo().contentEquals(Constant.ErrorCode.CODE_9000))
            {
                isCODE_9000 = true;
            }
            else
            {

                isError = true;
            }

            if (isCODE_9000 == true && isError == false)
            {
                //未量測過
                if (member.birthday.isEmpty() == false && Utils.checkDate(member.birthday)
                        && Utils.checkDateYear(member.birthday) && member.height.isEmpty() == false
                        && member.weight.isEmpty() == false)
                {
                    if (Integer.parseInt(member.height) > 0 && Integer.parseInt(member.weight) > 0)
                    {
                        Intent intent = new Intent(currentActivity, LightDescriptionPage.class);
                        startActivity(intent);
                        finish();
                        setTurnInNextPageAnimation(currentActivity);
                    }
                    else
                    {
                        Intent intent = new Intent(currentActivity, UserBasicInformationInit.class);
                        startActivity(intent);
                        finish();
                        setTurnInNextPageAnimation(currentActivity);
                    }
                }
                else
                {
                    Intent intent = new Intent(currentActivity, UserBasicInformationInit.class);
                    startActivity(intent);
                    finish();
                    setTurnInNextPageAnimation(currentActivity);
                }

            }
            else if (isCODE_9000 == false && isError == false)
            {
                if (member.birthday.isEmpty() == false && Utils.checkDate(member.birthday)
                        && Utils.checkDateYear(member.birthday) && member.height.isEmpty() == false
                        && member.weight.isEmpty() == false)
                {
                    if (Integer.parseInt(member.height) > 0 && Integer.parseInt(member.weight) > 0)
                    {
                        Intent intent = new Intent(currentActivity, CheckWeightPage.class);
                        startActivity(intent);
                        finish();
                        setTurnInNextPageAnimation(currentActivity);
                    }
                    else
                    {
                        Intent intent = new Intent(currentActivity, UserBasicInformationInit.class);
                        startActivity(intent);
                        finish();
                        setTurnInNextPageAnimation(currentActivity);
                    }
                }
                else
                {
                    Intent intent = new Intent(currentActivity, UserBasicInformationInit.class);
                    startActivity(intent);
                    finish();
                    setTurnInNextPageAnimation(currentActivity);
                }
            }
            else if (isCODE_9000 == false && isError == true)
            {
                if (member.isTestUser.contentEquals("1"))
                {
                    Log.e(TAG, "isTestUser = " + member.isTestUser + " 測試帳號");
                    if (member.birthday.isEmpty() == false && Utils.checkDate(member.birthday)
                            && Utils.checkDateYear(member.birthday) && member.height.isEmpty() == false
                            && member.weight.isEmpty() == false)
                    {
                        if (Integer.parseInt(member.height) > 0 && Integer.parseInt(member.weight) > 0)
                        {
                            Intent intent = new Intent(currentActivity, CheckWeightPage.class);
                            startActivity(intent);
                            finish();
                            setTurnInNextPageAnimation(currentActivity);
                        }
                        else
                        {
                            Intent intent = new Intent(currentActivity, UserBasicInformationInit.class);
                            startActivity(intent);
                            finish();
                            setTurnInNextPageAnimation(currentActivity);
                        }


                    }
                    else
                    {
                        Intent intent = new Intent(currentActivity, UserBasicInformationInit.class);
                        startActivity(intent);
                        finish();
                        setTurnInNextPageAnimation(currentActivity);
                    }
                }
                else
                {
                    showErrorAlert(apiResult.getMessage());
                }
            }
            else
            {
                showErrorAlert(apiResult.getMessage());
            }
        }

        @Override
        protected ApiResult doInBackground(String... params)
        {
            Token token = new Token();
            return healthReport.getHealthReport(token.getTokenId());
        }
    }
}
