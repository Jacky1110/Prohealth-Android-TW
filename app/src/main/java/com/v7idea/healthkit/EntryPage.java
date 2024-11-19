package com.v7idea.healthkit;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import android.util.Log;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;
import com.v7idea.healthkit.Model.Token;
import com.v7idea.template.Activity.BaseActivity;
import com.v7idea.template.AirApplication;
import com.v7idea.template.DataBase.SimpleDatabase;
import com.v7idea.template.GCM.RegistrationIntentService;
import com.v7idea.template.Tool.ApiResult;
import com.v7idea.template.Tool.DebugLog;
import com.v7idea.template.Tool.V7ideaAsyncTask;
import com.v7idea.template.View.AutoReleaseImageView;
import com.v7idea.template.Tool.ViewScaling;

import java.util.Set;

import me.leolin.shortcutbadger.ShortcutBadger;

import static android.content.Intent.FLAG_ACTIVITY_NEW_TASK;
/**
 * 2019/1/24 ＊確認使用的頁面 進入頁
 */
public class EntryPage extends BaseActivity {
    private final static String TAG = "EntryPage";
    boolean isFUNCTION_TYPE_LIFE = false;
    boolean isFUNCTION_TYPE_PACIENT = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_entry_page);
        AirApplication.setOnclikcFeedbackData(true);
        AirApplication airApplication = (AirApplication) getApplication();
        airApplication.setIsEntryPage(true);
        RelativeLayout thisLayout = (RelativeLayout) ViewScaling.findViewByIdAndScale(currentActivity, R.id.entryPage_RelativeLayout_baseLayout);

        AutoReleaseImageView ImageText = (AutoReleaseImageView) ViewScaling.findViewByIdAndScale(currentActivity, R.id.entryPage_AutoReleaseImageView_ImageText);
        AutoReleaseImageView middleLine = (AutoReleaseImageView) ViewScaling.findViewByIdAndScale(currentActivity, R.id.entryPage_AutoReleaseImageView_middleLine);
        AutoReleaseImageView logoImage = (AutoReleaseImageView) ViewScaling.findViewByIdAndScale(currentActivity, R.id.entryPage_AutoReleaseImageView_Logo);
        TextView textView_VersionName = (TextView) ViewScaling.findViewByIdAndScale(currentActivity, R.id.entryPage_TextView_VersionName);
        textView_VersionName.setText(getAppVersionName(this));

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = getString(R.string.default_notification_channel_name);
            String description = "預設";
            int importance = NotificationManager.IMPORTANCE_HIGH;
            NotificationChannel channel = new NotificationChannel(getString(R.string.default_notification_channel_id), name, importance);
            channel.setDescription(description);
            // Register the channel with the system; you can't change the importance
            // or other notification behaviors after this
            NotificationManager mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

            NotificationChannel existChannel = mNotificationManager.getNotificationChannel(getString(R.string.default_notification_channel_name));

            if (existChannel != null) {
                mNotificationManager.deleteNotificationChannel(getString(R.string.default_notification_channel_name));
                existChannel = null;
            }

            mNotificationManager.createNotificationChannel(channel);
        }

        //移除 ShortcutBadge
        ShortcutBadger.removeCount(this);

        if (getIntent()!=null)
        {
            if (getIntent().getExtras() != null) {
                Log.e(TAG, "getIntent = " + getIntent().getExtras());
                Bundle bundle = getIntent().getExtras();
                String Tag = bundle.getString("Tag");
                if (Tag != null) {
                    Log.e(TAG, "Tag = " + Tag);
                    if (Tag.contentEquals("PacientNotice")) {
                        isFUNCTION_TYPE_PACIENT = true;
                    } else if (Tag.contentEquals("LifeNotice")) {
                        isFUNCTION_TYPE_LIFE = true;
                    }
                } else {
                    isFUNCTION_TYPE_LIFE = getIntent().getExtras().getBoolean(Constant.NOTICE_FUNCTION_TYPE_LIFE, false);
                    isFUNCTION_TYPE_PACIENT = getIntent().getExtras().getBoolean(Constant.NOTICE_FUNCTION_TYPE_PACIENT, false);
                    Log.e(TAG, "isFUNCTION_TYPE_LIFE = " + isFUNCTION_TYPE_LIFE);
                    Log.e(TAG, "isFUNCTION_TYPE_PACIENT = " + isFUNCTION_TYPE_PACIENT);
                }
            }
        }


    }

    @Override
    protected void onResume() {
        super.onResume();

        FirebaseInstanceId.getInstance().getInstanceId().addOnSuccessListener(new OnSuccessListener<InstanceIdResult>() {
            @Override
            public void onSuccess(InstanceIdResult instanceIdResult) {
                String newToken = instanceIdResult.getToken();
                Log.e(TAG, newToken);

                SimpleDatabase simpleDatabase = new SimpleDatabase();
                simpleDatabase.setValueByKey(Constant.GCMRegisterID, newToken);

                Token token = new Token();

                if (isConnectedNetWork() && token.getTokenId().isEmpty() == false) {
//                    Handler handler = new Handler();
//                    handler.postDelayed(toMainPageRunnable, Constant.EntryPageToMainPageDelayTime);

                    UpdateFcmTokenID updateFcmTokenID = new UpdateFcmTokenID();
                    updateFcmTokenID.execute(newToken);
                } else {
                    Handler handler = new Handler();
                    handler.postDelayed(toMainPageRunnable, Constant.EntryPageToMainPageDelayTime);
                }
            }
        });

    }

    private class UpdateFcmTokenID extends V7ideaAsyncTask<String, String> {

        @Override
        public void downLoadSuccess(ApiResult result) {
            Handler handler = new Handler();
            handler.postDelayed(toMainPageRunnable, Constant.EntryPageToMainPageDelayTime);
        }

        @Override
        public void downLoadFail(ApiResult apiResult) {
            if (apiResult.getErrorNo().contentEquals(Constant.ErrorCode.CODE_500)) {
                showErrorAlert(getResources().getString(R.string.no_internet_error500));
            } else if (apiResult.getErrorNo().contentEquals(Constant.ErrorCode.CODE_1002)
                    || apiResult.getErrorNo().contentEquals(Constant.ErrorCode.CODE_1003)) {
                showErrorAlert(apiResult.getMessage(), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            new Token().destroy();
                            Intent intent = new Intent(currentActivity, LoginPage.class);
                            startActivity(intent);
                            finish();
                            setBackInPrePageAnimation(currentActivity);
                        }
                });
            } else if (apiResult.getErrorNo().contentEquals(Constant.ErrorCode.CODE_1200) )
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
            } else {
                showErrorAlert(apiResult.getMessage());
            }
        }

        @Override
        protected ApiResult doInBackground(String... strings) {
            Token token = new Token();
            return token.updateDeviceToken(token.getTokenId(), strings[0]);
        }
    }

    private Runnable toMainPageRunnable = new Runnable() {
        @Override
        public void run() {

            Token token = new Token();
            String strTokenId = token.getTokenId();
//            strTokenId = "";

            DebugLog.d(TAG, "strTokenId: " + strTokenId);

            boolean isInit = getString(Constant.InitString).isEmpty();

            if (isInit == false) {
                if (strTokenId != null && strTokenId.isEmpty() == false) {
                    //TODO TokenCheck API , update registerID

                    if (isFUNCTION_TYPE_LIFE || isFUNCTION_TYPE_PACIENT) {
                        Intent intent = null;
                        if (isFUNCTION_TYPE_LIFE) {
                            intent = new Intent(currentActivity, LiftSuggestListPage.class);
                            intent.putExtra(Constant.NOTICE_FUNCTION_TYPE_LIFE, true);
                        } else if (isFUNCTION_TYPE_PACIENT) {
                            SimpleDatabase simpleDatabase = new SimpleDatabase();
//                            boolean isIntroReport = simpleDatabase.getBooleanValueByKey(Constant.IS_NOT_HEALTH_REPORT, false);
//                            if (isIntroReport) {
//                                intent = new Intent(EntryPage.this, HealthRecommendationPage2.class);
//                            } else {
//                                intent = new Intent(EntryPage.this, HealthDescriptionPage1.class);
//                            }
                            boolean isIntroReport = simpleDatabase.getBooleanValueByKey(Constant.IS_NOT_INTRO_REPORT, Constant.IS_NOT_INTRO_REPORT_DEFAUL);
                            if (isIntroReport) {
                                intent = new Intent(AirApplication.getAppContext(), InspectionReportPage.class);
                            } else {
                                intent = new Intent(AirApplication.getAppContext(), ReportDescriptionPage1.class);
                            }
                            intent.putExtras(getIntent());
                            intent.putExtra(Constant.NOTICE_FUNCTION_TYPE_DOWNLOAD_DATA, true);
                            intent.putExtra(Constant.NOTICE_FUNCTION_TYPE_PACIENT, true);
                        }
                        startActivity(intent);
                        currentActivity.finish();
                    } else {
                        Intent intent = new Intent(currentActivity, MainActivity.class);
                        startActivity(intent);
                        currentActivity.finish();
                    }
                } else {
                    Intent intent = new Intent(currentActivity, LoginPage.class);
                    if (isFUNCTION_TYPE_LIFE || isFUNCTION_TYPE_PACIENT) {
                        if (isFUNCTION_TYPE_LIFE) {
                            intent = new Intent(currentActivity, LiftSuggestListPage.class);
                            intent.putExtra(Constant.NOTICE_FUNCTION_TYPE_LIFE, true);
                        } else if (isFUNCTION_TYPE_PACIENT) {
                            SimpleDatabase simpleDatabase = new SimpleDatabase();
                            boolean isIntroReport = simpleDatabase.getBooleanValueByKey(Constant.IS_NOT_INTRO_REPORT, Constant.IS_NOT_INTRO_REPORT_DEFAUL);
                            if (isIntroReport) {
                                intent = new Intent(AirApplication.getAppContext(), InspectionReportPage.class);
                            } else {
                                intent = new Intent(AirApplication.getAppContext(), ReportDescriptionPage1.class);
                            }
                            intent.putExtras(getIntent());
                            intent.putExtra(Constant.NOTICE_FUNCTION_TYPE_DOWNLOAD_DATA, true);
                            intent.putExtra(Constant.NOTICE_FUNCTION_TYPE_PACIENT, true);
                        }
                    }
                    startActivity(intent);
                    currentActivity.finish();
                }
            } else {
//                setString(Constant.InitString, String.valueOf(Constant.InitBoolean));
                Intent intent = new Intent(currentActivity, EntryPage2.class);
                if (isFUNCTION_TYPE_LIFE) {
                    intent.putExtra(Constant.NOTICE_FUNCTION_TYPE_LIFE, true);
                } else if (isFUNCTION_TYPE_PACIENT) {
                    intent.putExtras(getIntent());
                    intent.putExtra(Constant.NOTICE_FUNCTION_TYPE_PACIENT, true);
                }
                startActivity(intent);
                currentActivity.finish();
            }
        }
    };

    public String getAppVersionName(Context context) {
        String versionName = "";

        try {
            PackageManager packageManager = context.getPackageManager();
            PackageInfo packageInfo = packageManager.getPackageInfo(context.getPackageName(), 0);
            versionName = packageInfo.versionName;

        } catch (Exception e) {
            e.printStackTrace();
        }

        if (versionName == null || versionName.length() <= 0) {
            versionName = "";
        }

        return versionName;
    }

    public void setString(String Key, String Value) {
        SharedPreferences sharedPreferences = this.getSharedPreferences(Key, MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(Key, Value);
        editor.commit();
    }

    public String getString(String Key) {
        SharedPreferences sharedPreferences = this.getSharedPreferences(Key, MODE_PRIVATE);
        String saveString = sharedPreferences.getString(Key, "");
        return saveString;
    }
}
