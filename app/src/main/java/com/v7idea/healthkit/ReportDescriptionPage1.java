package com.v7idea.healthkit;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.v7idea.healthkit.Model.HealthReport;
import com.v7idea.healthkit.Model.Token;
import com.v7idea.healthkit.View.BottomButton;
import com.v7idea.template.Activity.BaseActivity;
import com.v7idea.template.AirApplication;
import com.v7idea.template.DataBase.SimpleDatabase;
import com.v7idea.template.Tool.ApiResult;
import com.v7idea.template.Tool.NotifyDialog;
import com.v7idea.template.Tool.V7ideaAsyncTask;
import com.v7idea.template.Tool.ViewScaling;
import com.v7idea.template.View.AutoReleaseImageView;
import com.v7idea.template.View.Banner;
import com.v7idea.template.View.V7TitleView;

import org.json.JSONObject;

/**
 * 2019/1/24 ＊確認使用的頁面 系統設定-健康報告說明1
 */
public class ReportDescriptionPage1 extends BaseActivity
{
    private static final String TAG = "ReportDescriptionPage1";
    RelativeLayout ReportDescriptionPage1_RelativeLayout = null;
    AutoReleaseImageView AutoReleaseImageView = null;
    int page = 0;
    private String SystemSettingsPageIntentString = "";
    private boolean SystemSettingsPageIntentData = false;
    private String IntentTitle = "";
    private boolean IntentTitleData = false;
    boolean isFUNCTION_TYPE_DOWNLOAD_DATA = false;
    private RelativeLayout ReportDescriptionPage1_RelativeLayout_Page0;
    private RelativeLayout ReportDescriptionPage1_RelativeLayout_PageContainer;

    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_report_description_page1);


        ReportDescriptionPage1_RelativeLayout = (RelativeLayout) ViewScaling.findViewByIdAndScale(currentActivity, R.id.ReportDescriptionPage1_RelativeLayout);
        AirApplication airApplication = (AirApplication) getApplication();
        if (getIntent() != null)
        {
            if (getIntent().getExtras() != null)
            {
                Log.e(TAG, "getIntent = " + getIntent().getExtras());
                isFUNCTION_TYPE_DOWNLOAD_DATA = getIntent().getExtras().getBoolean(Constant.NOTICE_FUNCTION_TYPE_DOWNLOAD_DATA, false);

                Token token = new Token();
                String strTokenId = token.getTokenId();
                if (strTokenId != null && strTokenId.isEmpty() == false)
                {

                    if (isFUNCTION_TYPE_DOWNLOAD_DATA == true)
                    {
                        ReportDescriptionPage1_RelativeLayout.setVisibility(View.INVISIBLE);
                        if (isConnectedToNetworkNotAlert())
                        {
                            DownLoadHealthReport downLoadHealthReport = new DownLoadHealthReport();
                            downLoadHealthReport.execute();

                        }
                        else
                        {
                            Intent intent = new Intent(currentActivity, InspectionReportPage.class);
                            intent.putExtra(InspectionReportPage.TYPE, "健康報告");
                            intent.putExtra(InspectionReportPage.TAG, false);
                            intent.putExtra(InspectionReportPage.ERROR_MESSAGE, "您尚未開啟網路，故無法觀看報告!!");
                            startActivity(intent);
                            finish();
                        }
                    }
                }
                else
                {
                    ReportDescriptionPage1_RelativeLayout.setVisibility(View.INVISIBLE);
                    if (isConnectedToNetworkNotAlert())
                    {
                        showErrorAlert("您尚未登入，請您先進行登入", new DialogInterface.OnClickListener()
                        {
                            @Override
                            public void onClick(DialogInterface dialog, int which)
                            {
                                new Token().destroy();
                                Intent intent = new Intent(currentActivity, LoginPage.class);
                                startActivity(intent);
                                currentActivity.finish();
                                setBackInPrePageAnimation(currentActivity);

                            }
                        });
                    }
                    else
                    {
                        showErrorAlert("您尚未登入，請您先進行登入", new DialogInterface.OnClickListener()
                        {
                            @Override
                            public void onClick(DialogInterface dialog, int which)
                            {
                                new Token().destroy();
                                Intent intent = new Intent(currentActivity, LoginPage.class);
                                intent.putExtra(getResources().getString(R.string.no_internet_can_not_use), true);
                                startActivity(intent);
                                currentActivity.finish();
                                setBackInPrePageAnimation(currentActivity);

                            }
                        });
                    }
                }
            }
        }


        if (airApplication.isIsEntryPage() == false)
        {
            Intent intent = new Intent();
            intent.setClass(ReportDescriptionPage1.this, EntryPage.class);
            intent.putExtras(getIntent());
            startActivity(intent);
            finish();
        }
        if (getIntent() != null)
        {
            if (getIntent().getExtras() != null)
            {
                SystemSettingsPageIntentString = getIntent().getExtras().getString("SystemSettingsPage");
                Log.e(TAG, "onCreate SystemSettingsPageIntentString = " + SystemSettingsPageIntentString);
                if (SystemSettingsPageIntentString != null)
                {
                    if (SystemSettingsPageIntentString.isEmpty() == false & SystemSettingsPageIntentString.contentEquals("ReportDescriptionPage1"))
                    {
                        SystemSettingsPageIntentData = true;
                        IntentTitle = getIntent().getExtras().getString("Title", "");
                        if (IntentTitle.isEmpty() == false && IntentTitle.contentEquals("InspectionReportPage_Text_Title"))
                        {
                            IntentTitleData = true;
                        }
                        else
                        {
                            IntentTitleData = false;
                        }
                    }
                }
            }
        }
        ViewScaling.setScaleValue(this);


        Banner banner = (Banner) ViewScaling.findViewByIdAndScale(currentActivity, R.id.ReportDescriptionPage1_Banner_Header);
        banner.initShortBanner();
        banner.backIcon.setOnClickListener(OnBackIcon);
        banner.title.setText("健康報告-綜合指標說明");
        banner.title.setTextColor(Color.parseColor("#FFFFFF"));

        AutoReleaseImageView = (AutoReleaseImageView) ViewScaling.findViewByIdAndScale(currentActivity, R.id.ReportDescriptionPage1_AutoReleaseImageView);

        BottomButton BottomButton = (BottomButton) ViewScaling.findViewByIdAndScale(currentActivity, R.id.ReportDescriptionPage1_BottomButton);
//        BottomButton.setData("略過", "下一頁", 100);
//        V7TitleView SkipPage = BottomButton.getLeftButton();
//        SkipPage.setOnClickListener(OnSkipPage);
//        V7TitleView NextPage = BottomButton.getRightButton();
//        NextPage.setOnClickListener(OnNextPage);
        BottomButton.setData("下一頁", 100);
        V7TitleView NextPage = BottomButton.getOneButton();
        NextPage.setOnClickListener(OnNextPage);

        AutoReleaseImageView AutoReleaseImageView_Page0 = (AutoReleaseImageView) ViewScaling.findViewByIdAndScale(currentActivity, R.id.ReportDescriptionPage1_AutoReleaseImageView_Page0);
        ViewScaling.setImageView(AutoReleaseImageView_Page0, (int) (606 * ViewScaling.getScaleValue()), (int) (760 * ViewScaling.getScaleValue()));
        TextView TextView_Page0_Title = (TextView) ViewScaling.findViewByIdAndScale(currentActivity, R.id.ReportDescriptionPage1_TextView_Page0_Title);
        BottomButton BottomButton_Page0 = (BottomButton) ViewScaling.findViewByIdAndScale(currentActivity, R.id.ReportDescriptionPage1_BottomButton_Page0);
        BottomButton_Page0.setData("開始導讀說明", 100);
        ReportDescriptionPage1_RelativeLayout_Page0 = (RelativeLayout) ViewScaling.findViewByIdAndScale(currentActivity, R.id.ReportDescriptionPage1_RelativeLayout_Page0);
        ReportDescriptionPage1_RelativeLayout_PageContainer = (RelativeLayout) ViewScaling.findViewByIdAndScale(currentActivity, R.id.ReportDescriptionPage1_RelativeLayout_PageContainer);


        Intent intent = this.getIntent();
        page = intent.getIntExtra("page", 0);
        Log.e(TAG, "onCreate  " + " page = " + page);

        if (SystemSettingsPageIntentData == true)
        {


            if (page == 0)
            {
                page = 1;
                BottomButton_Page0.getOneButton().setOnClickListener(new View.OnClickListener()
                {
                    @Override
                    public void onClick(View v)
                    {
                        Intent intent = new Intent(currentActivity, ReportDescriptionPage1.class);
                        intent.putExtras(getIntent());
                        intent.putExtra("page", page);
                        startActivity(intent);
                        finish();
                        setTurnInNextPageAnimation(currentActivity);
                    }
                });
                ReportDescriptionPage1_RelativeLayout_Page0.setVisibility(View.VISIBLE);
                ReportDescriptionPage1_RelativeLayout_PageContainer.setVisibility(View.INVISIBLE);
            }
            else if (page == 1)
            {
                page = 2;
                AutoReleaseImageView.setImageResource(R.mipmap.report_description_page1);
                ViewScaling.setImageView(AutoReleaseImageView, (int) (1090 * ViewScaling.getScaleValue()));
                banner.title.setText("健康報告-保健指標項目說明");
            }
            else if (page == 2)
            {
                page = 3;
                AutoReleaseImageView.setImageResource(R.mipmap.report_description_page2);
            }
            else if (page == 3)
            {
                page = 4;
                AutoReleaseImageView.setImageResource(R.mipmap.report_description_page3);
            }
            else if (page == 4)
            {
                page = 5;
                AutoReleaseImageView.setImageResource(R.mipmap.report_description_page4);
            }
            else if (page == 5)
            {
                page = 6;
                AutoReleaseImageView.setImageResource(R.mipmap.report_description_page5);
                banner.title.setText("健康報告-保健器官指標說明");
            }
            else if (page == 6)
            {
                page = 7;
                AutoReleaseImageView.setImageResource(R.mipmap.report_description_page6);
                banner.title.setText("健康報告-保健器官指標說明");
            }
            else if (page == 7)
            {
                page = 8;
                AutoReleaseImageView.setImageResource(R.mipmap.report_description_page7);
                banner.title.setText("健康報告-保健器官指標說明");
            }
            else if (page == 8)
            {
                page = 9;
                AutoReleaseImageView.setImageResource(R.mipmap.report_description_page8);
                banner.title.setText("健康報告-保健指標項目說明");
            }
        }
        else
        {
            if (page == 0)
            {
                page = 1;
                BottomButton_Page0.getOneButton().setOnClickListener(new View.OnClickListener()
                {
                    @Override
                    public void onClick(View v)
                    {
                        Intent intent = new Intent(currentActivity, ReportDescriptionPage1.class);
                        intent.putExtras(getIntent());
                        intent.putExtra("page", page);
                        startActivity(intent);
                        finish();
                        setTurnInNextPageAnimation(currentActivity);
                    }
                });
                ReportDescriptionPage1_RelativeLayout_Page0.setVisibility(View.VISIBLE);
                ReportDescriptionPage1_RelativeLayout_PageContainer.setVisibility(View.INVISIBLE);
            }
            else if (page == 1)
            {
                page = 2;
                AutoReleaseImageView.setImageResource(R.mipmap.report_description_page2);
            }
            else if (page == 2)
            {
                page = 3;
                AutoReleaseImageView.setImageResource(R.mipmap.report_description_page3);
            }
            else if (page == 3)
            {
                page = 4;
                AutoReleaseImageView.setImageResource(R.mipmap.report_description_page5);
                banner.title.setText("健康報告-保健器官指標說明");
            }
            else if (page == 4)
            {
                page = 5;
                AutoReleaseImageView.setImageResource(R.mipmap.report_description_page6);
                banner.title.setText("健康報告-保健器官指標說明");
            }
            else if (page == 5)
            {
                page = 6;
                AutoReleaseImageView.setImageResource(R.mipmap.report_description_page7);
                banner.title.setText("健康報告-保健器官指標說明");
            }
        }
    }

    View.OnClickListener OnNextPage = new View.OnClickListener()
    {
        @Override
        public void onClick(View v)
        {
            AirApplication.setOnclikcFeedback(v);
            if (SystemSettingsPageIntentData == true)
            {

                if (page < 9)
                {
                    Intent intent = new Intent(currentActivity, ReportDescriptionPage1.class);
                    intent.putExtras(getIntent());
                    intent.putExtra("page", page);
                    startActivity(intent);
                    finish();
                    setTurnInNextPageAnimation(currentActivity);
                }
                else
                {
                    Intent intent = new Intent(currentActivity, ReportDescriptionPage2.class);
                    intent.putExtras(getIntent());
                    startActivity(intent);
                    finish();
                    setTurnInNextPageAnimation(currentActivity);
                }
            }
            else
            {
                if (page < 6)
                {
                    Intent intent = new Intent(currentActivity, ReportDescriptionPage1.class);
                    intent.putExtras(getIntent());
                    intent.putExtra("page", page);
                    startActivity(intent);
                    finish();
                    setTurnInNextPageAnimation(currentActivity);
                }
                else
                {
                    Intent intent = new Intent(currentActivity, ReportDescriptionPage2.class);
                    intent.putExtras(getIntent());
                    startActivity(intent);
                    finish();
                    setTurnInNextPageAnimation(currentActivity);
                }
            }
        }
    };

    View.OnClickListener OnBackIcon = new View.OnClickListener()
    {
        @Override
        public void onClick(View v)
        {
            AirApplication.setOnclikcFeedback(v);
            if (SystemSettingsPageIntentData == true)
            {
                Intent intent = new Intent(currentActivity, SystemSettingsPage.class);
                startActivity(intent);
                finish();
                setTurnInNextPageAnimation(currentActivity);
            }
            else
            {
                Intent intent = new Intent(currentActivity, MainActivity.class);
                startActivity(intent);
                finish();
                setTurnInNextPageAnimation(currentActivity);
            }
        }
    };

    View.OnClickListener OnSkipPage = new View.OnClickListener()
    {
        @Override
        public void onClick(View v)
        {
            AirApplication.setOnclikcFeedback(v);
            Intent intent = new Intent(currentActivity, InspectionReportPage.class);
            intent.putExtras(getIntent());

            startActivity(intent);
            finish();
            setTurnInNextPageAnimation(currentActivity);
        }
    };

    @Override
    public void onBackPressed()
    {
        if (SystemSettingsPageIntentData == true)
        {
            if (IntentTitleData == true)
            {
                Intent intent = new Intent(currentActivity, InspectionReportPage.class);
                intent.putExtras(getIntent());
                startActivity(intent);
                finish();
                setTurnInNextPageAnimation(currentActivity);
            }
            else
            {
                Intent intent = new Intent(currentActivity, SystemSettingsPage.class);
                startActivity(intent);
                finish();
                setTurnInNextPageAnimation(currentActivity);
            }
        }
        else
        {
            Intent intent = new Intent(currentActivity, MainActivity.class);
            startActivity(intent);
            finish();
            setTurnInNextPageAnimation(currentActivity);
        }
    }

    private class DownLoadHealthReport extends V7ideaAsyncTask<String, ApiResult>
    {
        private HealthReport healthReport = null;

        private NotifyDialog progress = null;

        public DownLoadHealthReport()
        {
            healthReport = new HealthReport();
        }

        @Override
        protected void onPreExecute()
        {
            super.onPreExecute();

            progress = new NotifyDialog(currentActivity);
            progress.settingProgressDialog();
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
            String pageUrl = jsonObject.optString("pageurl");
            SimpleDatabase simpleDatabase = new SimpleDatabase();

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
            currentActivity.finish();
            setTurnInNextPageAnimation(currentActivity);
        }

        @Override
        public void downLoadFail(ApiResult apiResult)
        {


            if (apiResult.getErrorNo().contentEquals(Constant.ErrorCode.CODE_500))
            {
                showErrorAlert(getResources().getString(R.string.no_internet_error500));
                Intent intent = new Intent(currentActivity, InspectionReportPage.class);
                intent.putExtra(InspectionReportPage.TYPE, "健康報告");
                intent.putExtra(InspectionReportPage.TAG, false);
                intent.putExtra(InspectionReportPage.ERROR_MESSAGE, apiResult.getMessage());
                startActivity(intent);
                currentActivity.finish();
                setTurnInNextPageAnimation(currentActivity);
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
                        currentActivity.finish();
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
                        currentActivity.finish();
                        setBackInPrePageAnimation(currentActivity);

                    }
                });
            }
            else if (apiResult.getErrorNo().contentEquals(Constant.ErrorCode.CODE_1100))
            {

                Intent intent = new Intent(currentActivity, InspectionReportPage.class);
                intent.putExtra(InspectionReportPage.TAG, false);
                startActivity(intent);
                currentActivity.finish();
                setTurnInNextPageAnimation(currentActivity);
            }
            else if (apiResult.getErrorNo().contentEquals(Constant.ErrorCode.CODE_404))
            {

                Intent intent = new Intent(currentActivity, InspectionReportPage.class);
                intent.putExtra(InspectionReportPage.TYPE, "健康報告");
                intent.putExtra(InspectionReportPage.TAG, false);
                startActivity(intent);
                currentActivity.finish();
                setTurnInNextPageAnimation(currentActivity);
            }
            else if (apiResult.getErrorNo().contentEquals(Constant.ErrorCode.CODE_9000))
            {

                Intent intent = new Intent(currentActivity, InspectionReportPage.class);
                intent.putExtra(InspectionReportPage.TYPE, "健康報告");
                intent.putExtra(InspectionReportPage.TAG, false);
                intent.putExtra(InspectionReportPage.ERROR_MESSAGE, apiResult.getMessage());
                startActivity(intent);
                currentActivity.finish();
                setTurnInNextPageAnimation(currentActivity);
            }
            else if (apiResult.getErrorNo().contentEquals(Constant.ErrorCode.CODE_9001))
            {

                Intent intent = new Intent(currentActivity, InspectionReportPage.class);
                intent.putExtra(InspectionReportPage.TYPE, "健康報告");
                intent.putExtra(InspectionReportPage.TAG, false);
                intent.putExtra(InspectionReportPage.ERROR_MESSAGE, apiResult.getMessage());
                startActivity(intent);
                currentActivity.finish();
                setTurnInNextPageAnimation(currentActivity);
            }
            else if (apiResult.getErrorNo().contentEquals(Constant.ErrorCode.CODE_9999))
            {

                Intent intent = new Intent(currentActivity, InspectionReportPage.class);
                intent.putExtra(InspectionReportPage.TYPE, "健康報告");
                intent.putExtra(InspectionReportPage.TAG, false);
                intent.putExtra(InspectionReportPage.ERROR_MESSAGE, apiResult.getMessage());
                startActivity(intent);
                currentActivity.finish();
                setTurnInNextPageAnimation(currentActivity);
            }
            else
            {
//                showErrorAlert(apiResult.getMessage());
                Intent intent = new Intent(currentActivity, InspectionReportPage.class);
                intent.putExtra(InspectionReportPage.TYPE, "健康報告");
                intent.putExtra(InspectionReportPage.TAG, false);
                intent.putExtra(InspectionReportPage.ERROR_MESSAGE, apiResult.getMessage());
                startActivity(intent);
                currentActivity.finish();
                setTurnInNextPageAnimation(currentActivity);
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
