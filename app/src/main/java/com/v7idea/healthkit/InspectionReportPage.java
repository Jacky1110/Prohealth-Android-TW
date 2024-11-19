package com.v7idea.healthkit;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.webkit.JavascriptInterface;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.v7idea.healthkit.Model.Detection;
import com.v7idea.healthkit.Model.HealthReport;
import com.v7idea.healthkit.Model.HealthSummary;
import com.v7idea.healthkit.Model.Token;
import com.v7idea.healthkit.View.BottomButton;
import com.v7idea.template.Activity.BaseActivity;
import com.v7idea.template.AirApplication;
import com.v7idea.template.DataBase.SimpleDatabase;
import com.v7idea.template.Tool.ApiResult;
import com.v7idea.template.Tool.NotifyDialog;
import com.v7idea.template.Tool.V7ideaAsyncTask;
import com.v7idea.template.Tool.ViewScaling;
import com.v7idea.template.View.Banner;
import com.v7idea.template.View.V7TitleView;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

/**
 * 2019/1/24 ＊確認使用的頁面 主頁面-健康報告
 */
public class InspectionReportPage extends BaseActivity implements View.OnClickListener
{
    public final static String TAG = "InspectionReportPage";
    public final static String TYPE = "TYPE";
    public final static String ERROR_MESSAGE = "ErrorMessage";
    public final static String URL = "url";

    private Detection detection = null;

    private WebView webView = null;

    private boolean isGetLastNewInspectionReport = false;
    private String CheckType = "";
    private String HealthReportURL = null;
    private String errorMessage = null;
    Banner banner = null;
    boolean isFUNCTION_TYPE_DOWNLOAD_DATA = false;
    RelativeLayout InspectionReport_RelativeLayout = null;
    V7TitleView bottomButtonLeftButton = null;
    V7TitleView bottomButtonRightButton = null;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inspection_report_page);
        InspectionReport_RelativeLayout = (RelativeLayout) ViewScaling.findViewByIdAndScale(currentActivity, R.id.InspectionReport);

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
                        InspectionReport_RelativeLayout.setVisibility(View.INVISIBLE);
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
                    InspectionReport_RelativeLayout.setVisibility(View.INVISIBLE);
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
            intent.setClass(InspectionReportPage.this, EntryPage.class);
            intent.putExtras(getIntent());
            startActivity(intent);
            finish();
        }

        isGetLastNewInspectionReport = getIntent().getBooleanExtra(InspectionReportPage.TAG, false);
        CheckType = getIntent().getStringExtra(InspectionReportPage.TYPE);
        HealthReportURL = getIntent().getStringExtra(InspectionReportPage.URL);
        errorMessage = getIntent().getStringExtra(ERROR_MESSAGE);


        banner = (Banner) ViewScaling.findViewByIdAndScale(currentActivity, R.id.InspectionReport_Banner_Header);
        banner.initShortBanner();
        banner.backIcon.setOnClickListener(this);
        banner.title.setTextColor(Color.parseColor("#FFFFFF"));
        banner.title.setText(R.string.InspectionReportPage_Text_Title);
        banner.rightIcon.setBackgroundResource(R.mipmap.back_page_icon);
        banner.rightIcon.setOnClickListener(this);

        RelativeLayout ReportPanel = (RelativeLayout) ViewScaling.findViewByIdAndScale(currentActivity, R.id.InspectionReport_RelativeLayout_ReportPanel);
        webView = (WebView) ViewScaling.findViewByIdAndScale(currentActivity, R.id.InspectionReport_WebView_ShowWebView);

        WebSettings webSetting = webView.getSettings();

        webSetting.setUseWideViewPort(true);
        webSetting.setAppCacheEnabled(false);
        webSetting.setCacheMode(WebSettings.LOAD_NO_CACHE);
        webSetting.setLoadsImagesAutomatically(true);
        webSetting.setSupportZoom(true);
        webSetting.setLoadWithOverviewMode(true);
        webSetting.setUseWideViewPort(true);
        webSetting.setJavaScriptEnabled(true);
        webSetting.setAllowFileAccess(true);
        webSetting.setDomStorageEnabled(true);

        webView.setWebViewClient(new WebViewClient()
        {

            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon)
            {
                super.onPageStarted(view, url, favicon);

                SimpleDatabase simpleDatabase = new SimpleDatabase();

                if (CheckType != null && !CheckType.isEmpty())
                {
                    if (CheckType.contentEquals("立即改善"))
                    {
                        simpleDatabase.setValueByKey(Constant.IMMEDIATE_IMPROVEMENT, 0);
                    }
                    else if (CheckType.contentEquals("健康報告"))
                    {
                        simpleDatabase.setValueByKey(Constant.HEALTH_REPORT, 0);
                    }
                }
            }
        });

        webView.addJavascriptInterface(this, "android");
        webView.setWebChromeClient(new WebChromeClient());

        LinearLayout ButtonContainer = (LinearLayout) ViewScaling.findViewByIdAndScale(currentActivity, R.id.InspectionReport_LinearLayout_ButtonContainer);
        V7TitleView BackPageButton = (V7TitleView) ViewScaling.findViewByIdAndScale(currentActivity, R.id.InspectionReport_V7TitleView_BackPageButton);
        BackPageButton.setOnClickListener(this);
        V7TitleView DetermineButton = (V7TitleView) ViewScaling.findViewByIdAndScale(currentActivity, R.id.InspectionReport_V7TitleView_DetermineButton);
        DetermineButton.setOnClickListener(this);
        DetermineButton.setVisibility(View.GONE);

        RelativeLayout AlertPanel = (RelativeLayout) ViewScaling.findViewByIdAndScale(currentActivity, R.id.InspectionReport_RelativeLayout_AlertPanel);
        ImageView BannerImage = (ImageView) ViewScaling.findViewByIdAndScale(currentActivity, R.id.InspectionReport_V7TitleView_BannerImage);
        ViewScaling.setImageView(BannerImage);
        V7TitleView MiddelText = (V7TitleView) ViewScaling.findViewByIdAndScale(currentActivity, R.id.InspectionReport_V7TitleView_MiddelText);
        V7TitleView V7TitleView_BottomText = (V7TitleView) ViewScaling.findViewByIdAndScale(currentActivity, R.id.InspectionReport_V7TitleView_BottomText);
        V7TitleView AlertPanelButton = (V7TitleView) ViewScaling.findViewByIdAndScale(currentActivity, R.id.InspectionReport_V7TitleView_AlertPanelButton);
        AlertPanelButton.setOnClickListener(this);

        BottomButton BottomButton = (BottomButton) ViewScaling.findViewByIdAndScale(currentActivity, R.id.InspectionReport_BottomButton);

        if (isGetLastNewInspectionReport)
        {
            ReportPanel.setVisibility(View.VISIBLE);
            AlertPanel.setVisibility(View.GONE);

            detection = new Detection();

//        String reportUrl = "http://scanreport.lohasgen.com/UserReport/EightIndicatorChartToAPP.html?mobile=0911950862&scoreType=1";
//        String reportUrl = "http://scanreport.lohasgen.com/APPReport/EightIndicatorChartToAPP.html?token=c3f76d99-33c0-4ef0-a535-07bdd83cd272&mobile=0975161361&scoreType=1";

//            String reportUrl = detection.getDetectionUrl();
            Token token = new Token();

//            String reportUrl = "http://scanreport.lohasgen.com/HealthReport";
            Log.e(TAG, "reportUrl:" + HealthReportURL);
            Map<String, String> extraHeaders = new HashMap<String, String>();
            extraHeaders.put("v7idea_Token", token.getTokenId());
            webView.loadUrl(HealthReportURL, extraHeaders);
            String[] separated = HealthReportURL.split("/");
            String reportID = separated[5];
            if (reportID.isEmpty() == false)
            {
                UpHealthReportRead upHealthReportRead = new UpHealthReportRead();
                upHealthReportRead.execute(reportID);
            }
        }
        else
        {
            ReportPanel.setVisibility(View.GONE);
            AlertPanel.setVisibility(View.VISIBLE);
            if (CheckType != null && !CheckType.isEmpty())
            {
                if (CheckType.contentEquals("立即改善"))
                {
                    BottomButton.setData("導讀說明", "上次立即改善", 100);
                    bottomButtonLeftButton = BottomButton.getLeftButton();
                    bottomButtonRightButton = BottomButton.getRightButton();
                    bottomButtonRightButton.setVisibility(View.GONE);


                    if (isConnectedToNetworkNotAlert() == true)
                    {
                        DownLastLoadHealthSummary downLastLoadHealthSummary = new DownLastLoadHealthSummary();
                        downLastLoadHealthSummary.execute();
                    }
                    else
                    {
                        bottomButtonRightButton.setVisibility(View.GONE);
                    }


                    banner.title.setText(R.string.HealthRecommendationPage_Text_Title);
                    MiddelText.setText("在您等待最新健康報告的同時，\n您可以前往閱讀【導讀說明】，\n也可以前往閱讀【上次立即改善】。");
                    V7TitleView_BottomText.setText(getResources().getString(R.string.InspectionReportPage_Text_AlertPanel_MiddleText));

                    bottomButtonLeftButton.setOnClickListener(new View.OnClickListener()
                    {
                        @Override
                        public void onClick(View v)
                        {
                            //立即改善說明
                            Intent intent = new Intent(currentActivity, HealthDescriptionPage1.class);
                            intent.putExtras(getIntent());
                            intent.putExtra("SystemSettingsPage", "HealthDescriptionPage1");
                            intent.putExtra("Title", "HealthRecommendationPage_Text_Title");
                            intent.putExtra("Healthpage", 0);
                            startActivity(intent);
                            finish();
                            setTurnInNextPageAnimation(currentActivity);

                        }
                    });

                    bottomButtonRightButton.setOnClickListener(new View.OnClickListener()
                    {
                        @Override
                        public void onClick(View v)
                        {
                            if (isConnectedNetWork())
                            {
                                if (pageUrl.isEmpty() == false)
                                {
                                    Intent intent = new Intent();
                                    intent.setClass(InspectionReportPage.this, HealthRecommendationPage2.class);
                                    intent.putExtra("url", pageUrl);
                                    intent.putExtra("back", true);
                                    startActivity(intent);
                                    finish();
                                    setTurnInNextPageAnimation(currentActivity);
                                }
                                else
                                {
                                    showErrorAlert("無法取得上一次量測的立即改善報告！！");
                                }
                            }
                        }
                    });

                }
                else if (CheckType.contentEquals("健康報告"))
                {
                    BottomButton.setData("導讀說明", "上次健康報告", 100);
                    bottomButtonLeftButton = BottomButton.getLeftButton();
                    bottomButtonRightButton = BottomButton.getRightButton();
                    bottomButtonRightButton.setVisibility(View.GONE);


                    if (isConnectedToNetworkNotAlert() == true)
                    {
                        DownLastLoadHealthReport downLastLoadHealthReport = new DownLastLoadHealthReport();
                        downLastLoadHealthReport.execute();
                    }
                    else
                    {
                        bottomButtonRightButton.setVisibility(View.GONE);
                    }

                    banner.title.setText(R.string.InspectionReportPage_Text_Title);
                    MiddelText.setText("在您等待最新健康報告的同時，\n您可以前往閱讀【導讀說明】，\n也可以前往閱讀【上次健康報告】。");
                    V7TitleView_BottomText.setText(getResources().getString(R.string.InspectionReportPage_Text_AlertPanel_MiddleText));
                    bottomButtonLeftButton.setOnClickListener(new View.OnClickListener()
                    {
                        @Override
                        public void onClick(View v)
                        {
                            //健康快篩報告說明
                            Intent intent = new Intent(currentActivity, ReportDescriptionPage1.class);
                            intent.putExtras(getIntent());
                            intent.putExtra("SystemSettingsPage", "ReportDescriptionPage1");
                            intent.putExtra("Title", "InspectionReportPage_Text_Title");
                            intent.putExtra("page", 0);
                            startActivity(intent);
                            finish();
                            setTurnInNextPageAnimation(currentActivity);

                        }
                    });

                    bottomButtonRightButton.setOnClickListener(new View.OnClickListener()
                    {
                        @Override
                        public void onClick(View v)
                        {
                            if (isConnectedNetWork())
                            {
                                if (pageUrl.isEmpty() == false)
                                {
                                    Intent intent = new Intent();
                                    intent.setClass(InspectionReportPage.this, InspectionReportPage.class);
                                    intent.putExtra(InspectionReportPage.TAG, true);
                                    intent.putExtra(InspectionReportPage.TYPE, "健康報告");
                                    intent.putExtra("url", pageUrl);
                                    intent.putExtra("back", true);
                                    startActivity(intent);
                                    currentActivity.finish();
                                    setTurnInNextPageAnimation(currentActivity);
                                }
                                else
                                {
                                    showErrorAlert("無法取得上一次量測的健康報告！！");
                                }
                            }
                        }
                    });

                }

                if (errorMessage != null)
                {
                    MiddelText.setText(errorMessage);
                }
            }
        }
    }

    @JavascriptInterface
    public void setTitle(String s)
    {
        banner.title.setText(s);
    }

    @Override
    public void onBackPressed()
    {
        if (webView.canGoBack())
        {
            webView.goBack();
        }
        else
        {
            boolean isback = false;
            if (getIntent() != null)
            {
                if (getIntent().getExtras() != null)
                {
                    isback = getIntent().getExtras().getBoolean("back", false);
                }
            }

            if (isback == true)
            {
                Intent intent = new Intent(currentActivity, InspectionReportPage.class);
                intent.putExtra(InspectionReportPage.TAG, false);
                intent.putExtra(InspectionReportPage.TYPE, "健康報告");
                startActivity(intent);
                finish();
                setBackInPrePageAnimation(currentActivity);
            }
            else
            {

                Intent intent = new Intent(currentActivity, MainActivity.class);
                startActivity(intent);
                finish();
                setBackInPrePageAnimation(currentActivity);
            }
        }
    }

    @Override
    public void onClick(final View view)
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

                if (view.getId() == R.id.shortBanner_AutoReleaseImageView_backIcon)
                {
                    Intent intent = new Intent(currentActivity, MainActivity.class);
                    startActivity(intent);
                    finish();
                    setBackInPrePageAnimation(currentActivity);
                }
                else if (view.getId() == R.id.InspectionReport_V7TitleView_BackPageButton)
                {
                    if (webView.canGoBack())
                    {
                        webView.goBack();
                    }
                }
                else if (view.getId() == R.id.shortBanner_AutoReleaseImageView_rightIcon)
                {
                    onBackPressed();
                }
                else if (view.getId() == R.id.InspectionReport_V7TitleView_AlertPanelButton)
                {
                    Intent intent = new Intent(currentActivity, MainActivity.class);
                    startActivity(intent);
                    finish();
                    setBackInPrePageAnimation(currentActivity);
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


    private class UpHealthReportRead extends V7ideaAsyncTask<String, ApiResult>
    {
        private HealthReport healthReport = null;

        private NotifyDialog progress = null;

        public UpHealthReportRead()
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
            return healthReport.getReadHealthReportApi(token.getTokenId(), params[0]);
        }
    }

    public String pageUrl = "";

    private class DownLastLoadHealthReport extends V7ideaAsyncTask<String, ApiResult>
    {
        private HealthReport healthReport = null;

        private NotifyDialog progress = null;

        public DownLastLoadHealthReport()
        {
            healthReport = new HealthReport();
        }

        @Override
        protected void onPreExecute()
        {
            super.onPreExecute();


        }

        @Override
        public boolean isActivityNoFinish()
        {
            return !currentActivity.isFinishing();
        }

        @Override
        public void ifNeedCloseSomeThing()
        {

        }

        @Override
        public void downLoadSuccess(ApiResult result)
        {

            JSONObject jsonObject = result.getDataJSONObject();
            pageUrl = jsonObject.optString("pageurl");

            bottomButtonRightButton.setVisibility(View.VISIBLE);
        }

        @Override
        public void downLoadFail(ApiResult apiResult)
        {


            if (apiResult.getErrorNo().contentEquals(Constant.ErrorCode.CODE_500))
            {
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
                intent.putExtra(InspectionReportPage.ERROR_MESSAGE, apiResult.getMessage());
                startActivity(intent);
                currentActivity.finish();
                setTurnInNextPageAnimation(currentActivity);
            }
            else if (apiResult.getErrorNo().contentEquals(Constant.ErrorCode.CODE_9000))
            {
                bottomButtonRightButton.setVisibility(View.GONE);
            }
            else if (apiResult.getErrorNo().contentEquals(Constant.ErrorCode.CODE_9001))
            {
                bottomButtonRightButton.setAlpha(0.6f);
                bottomButtonRightButton.setEnabled(false);
            }
            else if (apiResult.getErrorNo().contentEquals(Constant.ErrorCode.CODE_9999))
            {


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
            return healthReport.getLastHealthReport(token.getTokenId());
        }
    }

    private class DownLastLoadHealthSummary extends V7ideaAsyncTask<String, ApiResult>
    {
        private HealthSummary healthSummary = null;

        private NotifyDialog progress = null;


        public DownLastLoadHealthSummary()
        {
            healthSummary = new HealthSummary();
        }


        @Override
        protected void onPreExecute()
        {
            super.onPreExecute();
        }

        @Override
        public boolean isActivityNoFinish()
        {
            return !currentActivity.isFinishing();
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
            pageUrl = jsonObject.optString("pageurl");
            bottomButtonRightButton.setVisibility(View.VISIBLE);

        }

        @Override
        public void downLoadFail(ApiResult apiResult)
        {
            if (apiResult.getErrorNo().contentEquals(Constant.ErrorCode.CODE_500))
            {
                Intent intent = new Intent(currentActivity, InspectionReportPage.class);
                intent.putExtra(InspectionReportPage.TYPE, "立即改善");
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

            else if (apiResult.getErrorNo().contentEquals(Constant.ErrorCode.CODE_404))
            {
                Intent intent = new Intent(currentActivity, InspectionReportPage.class);
                intent.putExtra(InspectionReportPage.TAG, false);
                intent.putExtra(InspectionReportPage.TYPE, "立即改善");
                intent.putExtra(InspectionReportPage.ERROR_MESSAGE, apiResult.getMessage());
                startActivity(intent);
                finish();
                setTurnInNextPageAnimation(currentActivity);
            }
            else if (apiResult.getErrorNo().contentEquals(Constant.ErrorCode.CODE_9000))
            {
                bottomButtonRightButton.setVisibility(View.GONE);
            }
            else if (apiResult.getErrorNo().contentEquals(Constant.ErrorCode.CODE_9001))
            {
                bottomButtonRightButton.setAlpha(0.6f);
                bottomButtonRightButton.setEnabled(false);
            }
            else if (apiResult.getErrorNo().contentEquals(Constant.ErrorCode.CODE_9999))
            {


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
            return healthSummary.getLastHealthSummary(token.getTokenId());
        }
    }
}
