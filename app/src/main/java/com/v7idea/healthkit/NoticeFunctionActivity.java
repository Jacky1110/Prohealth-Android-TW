//package com.v7idea.healthkit;
//
//import android.app.ActivityManager;
//import android.content.Context;
//import android.content.DialogInterface;
//import android.content.Intent;
//import android.os.Bundle;
//import android.util.Log;
//
//import com.v7idea.healthkit.Model.HealthSummary;
//import com.v7idea.healthkit.Model.Token;
//import com.v7idea.template.Activity.BaseActivity;
//import com.v7idea.template.AirApplication;
//import com.v7idea.template.DataBase.SimpleDatabase;
//import com.v7idea.template.Tool.ApiResult;
//import com.v7idea.template.Tool.NotifyDialog;
//import com.v7idea.template.Tool.V7ideaAsyncTask;
//
//import org.json.JSONObject;
//
//import java.util.List;
//
//public class NoticeFunctionActivity extends BaseActivity {
//    private static final String TAG = "NoticeFunctionActivity";
//    SimpleDatabase simpleDatabase = null;
//    boolean isFUNCTION_TYPE_LIFE = false;
//    boolean isFUNCTION_TYPE_PACIENT = false;
//    boolean isFUNCTION_TYPE_ENTRYPAGE = false;
//
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_notice_function);
//        simpleDatabase = new SimpleDatabase();
//        int i = isAppAlive(AirApplication.getAppContext(), getPackageName());
//        Log.e(TAG, "onCreate i = " + i+"  "+getPackageName());
//        if (getIntent() != null) {
//            isFUNCTION_TYPE_ENTRYPAGE = getIntent().getExtras().getBoolean(Constant.NOTICE_FUNCTION_TYPE_ENTRYPAGE, false);
//            isFUNCTION_TYPE_LIFE = getIntent().getExtras().getBoolean(Constant.NOTICE_FUNCTION_TYPE_LIFE, false);
//            isFUNCTION_TYPE_PACIENT = getIntent().getExtras().getBoolean(Constant.NOTICE_FUNCTION_TYPE_PACIENT, false);
//
//            Intent launchIntent = getIntent();
//            if ("OPEN_TRANSFORM_ACTIVITY".equals(launchIntent.getAction())) {
//                isFUNCTION_TYPE_ENTRYPAGE = true;
//                if (isFUNCTION_TYPE_ENTRYPAGE) {
//                    Intent intent = new Intent(currentActivity, EntryPage.class);
//                    intent.putExtra(Constant.NOTICE_FUNCTION_TYPE_ENTRYPAGE, false);
//                    intent.putExtra(Constant.NOTICE_FUNCTION_TYPE_LIFE, isFUNCTION_TYPE_LIFE);
//                    intent.putExtra(Constant.NOTICE_FUNCTION_TYPE_PACIENT, isFUNCTION_TYPE_PACIENT);
//                    startActivity(intent);
//                    finish();
//                    setTurnInNextPageAnimation(currentActivity);
//                } else if (isFUNCTION_TYPE_LIFE) {
//                    Intent intent = new Intent(currentActivity, LiftSuggestListPage.class);
//                    startActivity(intent);
//                    finish();
//                    setTurnInNextPageAnimation(currentActivity);
//                } else if (isFUNCTION_TYPE_PACIENT) {
//                    if (isConnectedNetWork()) {
//                        DownLoadHealthSummary downLoadHealthSummary = new DownLoadHealthSummary();
//                        downLoadHealthSummary.execute();
//                    }
//                }
//            } else {
//                if (isFUNCTION_TYPE_ENTRYPAGE) {
//                    Intent intent = new Intent(currentActivity, EntryPage.class);
//                    intent.putExtra(Constant.NOTICE_FUNCTION_TYPE_ENTRYPAGE, false);
//                    intent.putExtra(Constant.NOTICE_FUNCTION_TYPE_LIFE, isFUNCTION_TYPE_LIFE);
//                    intent.putExtra(Constant.NOTICE_FUNCTION_TYPE_PACIENT, isFUNCTION_TYPE_PACIENT);
//                    startActivity(intent);
//                    finish();
//                    setTurnInNextPageAnimation(currentActivity);
//                } else if (isFUNCTION_TYPE_LIFE) {
//                    Intent intent = new Intent(currentActivity, LiftSuggestListPage.class);
//                    startActivity(intent);
//                    finish();
//                    setTurnInNextPageAnimation(currentActivity);
//                } else if (isFUNCTION_TYPE_PACIENT) {
//                    if (isConnectedNetWork()) {
//                        DownLoadHealthSummary downLoadHealthSummary = new DownLoadHealthSummary();
//                        downLoadHealthSummary.execute();
//                    }
//                }
//            }
//        }
//
//
//    }
//
//
//    private class DownLoadHealthSummary extends V7ideaAsyncTask<String, ApiResult> {
//        private HealthSummary healthSummary = null;
//
////        private NotifyDialog progress = null;
//
//        public DownLoadHealthSummary() {
//            healthSummary = new HealthSummary();
//        }
//
//        @Override
//        protected void onPreExecute() {
//            super.onPreExecute();
////
////            progress = new NotifyDialog(currentActivity);
////            progress.settingProgressDialog();
//        }
//
//        @Override
//        public boolean isActivityNoFinish() {
//            return !currentActivity.isFinishing();
//        }
//
//        @Override
//        public void ifNeedCloseSomeThing() {
////            if (progress != null) {
////                progress.dismiss();
////                progress = null;
////            }
//        }
//
//        @Override
//        public void downLoadSuccess(ApiResult result) {
//
//
//            boolean isIntroReport = simpleDatabase.getBooleanValueByKey(Constant.IS_NOT_HEALTH_REPORT, false);
//
//            Intent intent = null;
//
//            if (isIntroReport) {
//                intent = new Intent(currentActivity, HealthRecommendationPage2.class);
//            } else {
//                intent = new Intent(currentActivity, HealthDescriptionPage1.class);
//            }
//
//            JSONObject jsonObject = result.getDataJSONObject();
//            intent.putExtra("url", jsonObject.optString("pageurl"));
//            startActivity(intent);
//            finish();
//            setTurnInNextPageAnimation(currentActivity);
//        }
//
//        @Override
//        public void downLoadFail(ApiResult apiResult) {
////            if (apiResult.getErrorNo().contentEquals(Constant.ErrorCode.CODE_500)) {
////                showErrorAlert(getResources().getString(R.string.no_internet_error500));
////            } else if (apiResult.getErrorNo().contentEquals(Constant.ErrorCode.CODE_1002)) {
////                showErrorAlert(apiResult.getMessage(), new DialogInterface.OnClickListener() {
////                    @Override
////                    public void onClick(DialogInterface dialog, int which) {
////                        new Token().destroy();
////                        Intent intent = new Intent(currentActivity, LoginPage.class);
////                        startActivity(intent);
////                        finish();
////                        setBackInPrePageAnimation(currentActivity);
////
////                    }
////                });
////            } else if (apiResult.getErrorNo().contentEquals(Constant.ErrorCode.CODE_1003)) {
////                showErrorAlert(apiResult.getMessage(), new DialogInterface.OnClickListener() {
////                    @Override
////                    public void onClick(DialogInterface dialog, int which) {
////                        new Token().destroy();
////                        Intent intent = new Intent(currentActivity, LoginPage.class);
////                        startActivity(intent);
////                        finish();
////                        setBackInPrePageAnimation(currentActivity);
////
////                    }
////                });
////            } else if (apiResult.getErrorNo().contentEquals(Constant.ErrorCode.CODE_1100)) {
////                Intent intent = new Intent(currentActivity, InspectionReportPage.class);
////                intent.putExtra(InspectionReportPage.TAG, false);
////                intent.putExtra(InspectionReportPage.TYPE, "立即改善");
////                startActivity(intent);
////                finish();
////                setTurnInNextPageAnimation(currentActivity);
////            } else if (apiResult.getErrorNo().contentEquals(Constant.ErrorCode.CODE_404)) {
////                Intent intent = new Intent(currentActivity, InspectionReportPage.class);
////                intent.putExtra(InspectionReportPage.TAG, false);
////                intent.putExtra(InspectionReportPage.TYPE, "立即改善");
////                startActivity(intent);
////                finish();
////                setTurnInNextPageAnimation(currentActivity);
////            } else if (apiResult.getErrorNo().contentEquals(Constant.ErrorCode.CODE_9000)) {
////                Intent intent = new Intent(currentActivity, InspectionReportPage.class);
////                intent.putExtra(InspectionReportPage.TAG, false);
////                intent.putExtra(InspectionReportPage.TYPE, "立即改善");
////                intent.putExtra(InspectionReportPage.ERROR_MESSAGE, apiResult.getMessage());
////                startActivity(intent);
////                finish();
////                setTurnInNextPageAnimation(currentActivity);
////            } else if (apiResult.getErrorNo().contentEquals(Constant.ErrorCode.CODE_9001)) {
////                Intent intent = new Intent(currentActivity, InspectionReportPage.class);
////                intent.putExtra(InspectionReportPage.TAG, false);
////                intent.putExtra(InspectionReportPage.TYPE, "立即改善");
////                intent.putExtra(InspectionReportPage.ERROR_MESSAGE, apiResult.getMessage());
////                startActivity(intent);
////                finish();
////                setTurnInNextPageAnimation(currentActivity);
////            } else if (apiResult.getErrorNo().contentEquals(Constant.ErrorCode.CODE_9999)) {
////                Intent intent = new Intent(currentActivity, InspectionReportPage.class);
////                intent.putExtra(InspectionReportPage.TAG, false);
////                intent.putExtra(InspectionReportPage.TYPE, "立即改善");
////                intent.putExtra(InspectionReportPage.ERROR_MESSAGE, apiResult.getMessage());
////                startActivity(intent);
////                finish();
////                setTurnInNextPageAnimation(currentActivity);
////            } else {
////                Intent intent = new Intent(currentActivity, MainActivity.class);
////                startActivity(intent);
////                finish();
////                setTurnInNextPageAnimation(currentActivity);
////                showErrorAlert(apiResult.getMessage());
////            }
//        }
//
//        @Override
//        protected ApiResult doInBackground(String... params) {
//            Token token = new Token();
//            return healthSummary.getHealthSummary(token.getTokenId());
//        }
//    }
//
//    /**
//     * 返回app运行状态
//     *
//     * @param context     一个context
//     * @param packageName 要判断应用的包名
//     * @return int 1:前台 2:后台 0:不存在
//     */
//    public static int isAppAlive(Context context, String packageName) {
//        ActivityManager activityManager = (ActivityManager) context
//                .getSystemService(Context.ACTIVITY_SERVICE);
//        List<ActivityManager.RunningTaskInfo> listInfos = activityManager
//                .getRunningTasks(20);
//        // 判断程序是否在栈顶
//        if (listInfos.get(0).topActivity.getPackageName().equals(packageName)) {
//            return 1;
//        } else {
//            // 判断程序是否在栈里
//            for (ActivityManager.RunningTaskInfo info : listInfos) {
//                if (info.topActivity.getPackageName().equals(packageName)) {
//                    return 2;
//                }
//            }
//            return 0;// 栈里找不到，返回3
//        }
//    }
//
//}
