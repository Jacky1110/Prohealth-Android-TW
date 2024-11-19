package com.v7idea.healthkit;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Handler;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;

import com.v7idea.healthkit.Model.Detection;
import com.v7idea.healthkit.Model.HealthCheck;
import com.v7idea.healthkit.Model.HealthReport;
import com.v7idea.healthkit.Model.Member;
import com.v7idea.healthkit.Model.Token;
import com.v7idea.template.Activity.BaseActivity;
import com.v7idea.template.AirApplication;
import com.v7idea.template.DataBase.SimpleDatabase;
import com.v7idea.template.Tool.ApiResult;
import com.v7idea.template.Tool.DebugLog;
import com.v7idea.template.Tool.NotifyDialog;
import com.v7idea.template.Tool.Utils;
import com.v7idea.template.Tool.V7BaseNameValuePair;
import com.v7idea.template.Tool.V7NameValuePair;
import com.v7idea.template.Tool.V7ideaAsyncTask;
import com.v7idea.template.Tool.ViewScaling;
import com.v7idea.template.View.AutoReleaseImageView;
import com.v7idea.template.View.V7TitleView;

import org.joda.time.DateTime;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import me.leolin.shortcutbadger.ShortcutBadger;

/**
 * 2019/1/24 ＊確認使用的頁面 進行量測-上傳頁
 */
public class EndScanPage extends BaseActivity implements View.OnClickListener
{
    private static final String TAG = "EndScanPage";
    private SimpleDatabase simpleDataBase = null;

    private AirApplication thisApp = null;

    private AutoReleaseImageView SuccessIcon = null;
    private V7TitleView MiddleTitle = null;
    private V7TitleView Complete = null;
    private V7TitleView RemindTitle = null;
    private V7TitleView RemindText = null;
    private String MacAddress = "";

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_end_scan_page);

        simpleDataBase = new SimpleDatabase();

        thisApp = (AirApplication) getApplication();
        MacAddress = thisApp.this_mac_sn;

        AutoReleaseImageView BannerImage = (AutoReleaseImageView) ViewScaling.findViewByIdAndScale(currentActivity
                , R.id.EndScanPage_AutoReleaseImageView_BannerImage);
        ViewScaling.setImageView(BannerImage);

        SuccessIcon = (AutoReleaseImageView) ViewScaling.findViewByIdAndScale(currentActivity
                , R.id.EndScanPage_AutoReleaseImageView_SuccessIcon);

        MiddleTitle = (V7TitleView) ViewScaling.findViewByIdAndScale(currentActivity
                , R.id.EndScanPage_V7TitleView_MiddleTitle);

        Complete = (V7TitleView) ViewScaling.findViewByIdAndScale(currentActivity
                , R.id.EndScanPage_V7TitleView_Complete);
        Complete.setOnClickListener(this);

        RemindTitle = (V7TitleView) ViewScaling.findViewByIdAndScale(currentActivity
                , R.id.EndScanPage_V7TitleView_RemindTitle);

        RemindText = (V7TitleView) ViewScaling.findViewByIdAndScale(currentActivity
                , R.id.EndScanPage_V7TitleView_RemindText);

        AutoReleaseImageView BackImage = (AutoReleaseImageView) ViewScaling.findViewByIdAndScale(currentActivity, R.id.EndScanPage_AutoReleaseImageView_BackImage);
        BackImage.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                AirApplication.setOnclikcFeedback(v);
                Intent intent = new Intent(currentActivity, MainActivity.class);
                startActivity(intent);
                finish();
                setBackInPrePageAnimation(currentActivity);
            }
        });

        prepareUpLoad();
    }

    private void prepareUpLoad()
    {
        //設定準備上傳資料的狀態
        SuccessIcon.setVisibility(View.INVISIBLE);
        MiddleTitle.setText(R.string.EndScan_Text_MiddleText1);
        Complete.setTag(0);
        Complete.setText("上傳");
        RemindTitle.setVisibility(View.VISIBLE);
        RemindText.setVisibility(View.VISIBLE);
    }

    private void uploadSuccessStatus()
    {
        SuccessIcon.setVisibility(View.VISIBLE);
        MiddleTitle.setText(R.string.EndScan_Text_MiddleText2);
        Complete.setTag(1);
        Complete.setText("確認");
        RemindTitle.setVisibility(View.GONE);
        RemindText.setVisibility(View.GONE);
    }

    private class UpLoadDetectionData extends V7ideaAsyncTask<String, String>
    {
        private ApiResult apiResult = null;
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
            if (progress != null)
            {
                progress.dismiss();
                progress = null;
            }
        }

        @Override
        public void downLoadSuccess(ApiResult result)
        {
            Complete.setEnabled(true);
            uploadSuccessStatus();

            DateTime currentDateTime = new DateTime();

            simpleDataBase.setValueByKey(Constant.LAST_DETECTION_DATE, ("" + currentDateTime.getMillis()));
//            GetReportLastDetection getReportLastDetection =new GetReportLastDetection();
//            getReportLastDetection.execute();
//            DateTime nextDetectionDate = currentDateTime.plusDays(Constant.DETECTION_PERIOD);
//
//            String message = apiResult.getMessage() + " 下次建議量測時間為"+nextDetectionDate.getMonthOfYear()
//                    +"月"+nextDetectionDate.getDayOfMonth()+"日(週期"+Constant.DETECTION_PERIOD+"天)。請持續量測，關心健康";
//
//            showErrorAlert(message, new DialogInterface.OnClickListener() {
//                @Override
//                public void onClick(DialogInterface dialog, int which) {
//                    AirApplication thisApp = (AirApplication)getApplication();
//                    thisApp.setBleController(null);
//
//                    Intent intent = new Intent(currentActivity, EndScanPage.class);
//                    startActivity(intent);
//                    finish();
//                    setBackInPrePageAnimation(currentActivity);
//                }
//            });
        }

        @Override
        public void downLoadFail(ApiResult apiResult)
        {
            Complete.setEnabled(true);
            if (apiResult.getErrorNo().contentEquals(Constant.ErrorCode.CODE_500))
            {
                showErrorAlert(getResources().getString(R.string.no_internet_error500));
            }
            else if (apiResult.getErrorNo().contentEquals(Constant.ErrorCode.CODE_1002)
                    || apiResult.getErrorNo().contentEquals(Constant.ErrorCode.CODE_1003))
            {
                showErrorAlertIfNoLogin(apiResult.getMessage() + "\n系統將自動幫您登出");
            }
            else
            {
                showErrorAlert(apiResult.getMessage());
            }
        }

        @Override
        protected ApiResult doInBackground(String... params)
        {
            String bluetoothSerial = simpleDataBase.getStringValueByKey(Constant.LAST_CONNECTED_DEVICE_MAC, "");
            Token token = new Token();
            Detection detection = new Detection();

            //            string        bluetoothSerial= FA4AWY808950
            //            string        deviceSerial= FA4AWY808950
            //            string        oberonSerial= 17d9f5439182aaaba9229f60dafdb146ca6f50c6b38dba88dc2ea7eab37e4defed7534bcf9deb1052f09cbeb0366f8e0df602413b9e2382cffafda440b2f2a23
            //            string        deviceType=Android
            //            striing       mydata=藍牙接收資料
            //            string    longitude=24.778289
            //            string        latitude=120.988108

            thisApp.reseller = "tw-00017";
            long currentTime = System.currentTimeMillis();
            AirApplication.myID = "" + currentTime;

            ArrayList<V7NameValuePair> bodyData = new ArrayList<V7NameValuePair>();
            bodyData.add(new V7BaseNameValuePair("reseller", thisApp.reseller));
            bodyData.add(new V7BaseNameValuePair("myid", AirApplication.myID));
            bodyData.add(new V7BaseNameValuePair("bluetoothSerial", thisApp.this_mac_name));
            bodyData.add(new V7BaseNameValuePair("bluetoothID", thisApp.this_mac_name));
            bodyData.add(new V7BaseNameValuePair("MacAddress", MacAddress));
            bodyData.add(new V7BaseNameValuePair("deviceSerial", thisApp.this_mac_name));
            bodyData.add(new V7BaseNameValuePair("oberonSerial", AirApplication.serialNo));
            String deviceType_Data = "Android" + ";" + "NIRPLUS" + ";" + "APP:" + getAppVersionName(EndScanPage.this) + ";" + "BLE:" + thisApp.getFirmwareData().NowFirmwareVersion + ";";
            bodyData.add(new V7BaseNameValuePair("deviceType", deviceType_Data));
            bodyData.add(new V7BaseNameValuePair("mydata", thisApp.send_data.toString()));
            bodyData.add(new V7BaseNameValuePair("longitude", "" + AirApplication.longitude));
            bodyData.add(new V7BaseNameValuePair("latitude", "" + AirApplication.latitude));
            Member member = new Member();
            if (AirApplication.Detection_Height.isEmpty())
            {
                AirApplication.Detection_Height = member.height;
            }
            bodyData.add(new V7BaseNameValuePair("height", "" + AirApplication.Detection_Height));
            if (AirApplication.Detection_Weight.isEmpty())
            {
                AirApplication.Detection_Weight = member.weight;
            }
            bodyData.add(new V7BaseNameValuePair("weight", "" + AirApplication.Detection_Weight));
            if (AirApplication.Detection_Question1.isEmpty() == false)
            {
                bodyData.add(new V7BaseNameValuePair("question1", "" + AirApplication.Detection_Question1));
            }
            if (AirApplication.Detection_Question2.isEmpty() == false)
            {
                bodyData.add(new V7BaseNameValuePair("question2", "" + AirApplication.Detection_Question2));
            }

            bodyData.add(new V7BaseNameValuePair("tel4", member.firstName));
            bodyData.add(new V7BaseNameValuePair("tel5", member.lastName));
            DebugLog.d("UpLoadDetectionData", "bodyString: " + bodyData.toString());
            DebugLog.d("UpLoadDeviceSerial", "deviceSerial : " + Utils.getAndroidId(EndScanPage.this));

            apiResult = detection.postDetection(token.getTokenId(), bodyData);

            return apiResult;
        }
    }

    @Override
    public void onClick(View view)
    {
        AirApplication.setOnclikcFeedback(view);
        if (view.getId() == R.id.EndScanPage_V7TitleView_Complete)
        {
            int tag = (int) view.getTag();

            if (tag == 0)
            {
                if (isConnectedNetWork() == false)
                {
                    return;
                }
                Complete.setEnabled(false);
                GetHealthCheck getHealthCheck = new GetHealthCheck();
                getHealthCheck.execute();

            }
            else if (tag == 1)
            {
                onBackPressed();
            }
        }
        else
        {
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
                    onBackPressed();
                }

                @Override
                public void onAnimationRepeat(Animation animation)
                {

                }
            });

            view.setAnimation(scaleAnimation);
            view.startAnimation(scaleAnimation);
        }
    }

    @Override
    public void onBackPressed()
    {
        Intent intent = new Intent(currentActivity, MainActivity.class);
        startActivity(intent);
        finish();
        setBackInPrePageAnimation(currentActivity);
    }


//    private class GetReportLastDetection extends V7ideaAsyncTask<String, ApiResult>
//    {
//        private HealthReport healthReport = null;
//        private static final String TAG = "GetReportIsRead";
//
//        @Override
//        protected void onPreExecute()
//        {
//            super.onPreExecute();
//            healthReport = new HealthReport();
//        }
//
//        @Override
//        public void ifNeedCloseSomeThing()
//        {
//
//        }
//
//        @Override
//        public void downLoadSuccess(ApiResult result)
//        {
//            JSONObject jsonObject = result.getDataJSONObject();
//            SimpleDatabase simpleDatabase = new SimpleDatabase();
//            String lastDetection = jsonObject.optString("lastDetection");
//
//            if (lastDetection.isEmpty() == false)
//            {
//                    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
//                    Date date = null;
//                    try
//                    {
//                        date = dateFormat.parse(lastDetection);
//                    } catch (ParseException e)
//                    {
//                        e.printStackTrace();
//                    }
//
//                    DateTime dateTime = new DateTime(date);
//                    dateTime = dateTime.plusDays(7);
//                    dateTime = dateTime.plusHours(12);
//                    long intervalTime = dateTime.getMillis();
//                    AlarmManagerHelper.setAlarmTime(intervalTime, true);
//                    simpleDatabase.setValueByKey(Constant.LAST_DETECTION, false);
//            }
//        }
//
//        @Override
//        public void downLoadFail(ApiResult apiResult)
//        {
//
//        }
//
//        @Override
//        protected ApiResult doInBackground(String... params)
//        {
//            Token token = new Token();
//            return healthReport.getHealthReport(token.getTokenId());
//        }
//    }

    //7天內沒有量測
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
        }

        @Override
        public void ifNeedCloseSomeThing()
        {

        }

        @Override
        public void downLoadSuccess(ApiResult result)
        {

            UpLoadDetectionData upLoadDetectionData = new UpLoadDetectionData();
            upLoadDetectionData.execute();
        }

        @Override
        public void downLoadFail(ApiResult apiResult)
        {
            Complete.setEnabled(true);
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

    public String getAppVersionName(Context context)
    {
        String versionName = "";

        try
        {
            PackageManager packageManager = context.getPackageManager();
            PackageInfo packageInfo = packageManager.getPackageInfo(context.getPackageName(), 0);
            versionName = packageInfo.versionName;

        } catch (Exception e)
        {
            e.printStackTrace();
        }

        if (versionName == null || versionName.length() <= 0)
        {
            versionName = "";
        }

        return versionName;
    }
}
