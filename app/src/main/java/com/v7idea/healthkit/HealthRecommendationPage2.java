package com.v7idea.healthkit;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.webkit.JavascriptInterface;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.RelativeLayout;

import com.v7idea.healthkit.Model.HealthSummary;
import com.v7idea.healthkit.Model.Token;
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
 * 2019/1/24 ＊確認使用的頁面 主頁面-立即改善
 */
public class HealthRecommendationPage2 extends BaseActivity implements View.OnClickListener
{
    public final static String URL = "url";

    WebView webView = null;
    String url = null;
    Banner banner = null;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inspection_report_page2);

        banner = (Banner) ViewScaling.findViewByIdAndScale(currentActivity, R.id.InspectionReport_Banner_Header2);
        banner.initShortBanner();
        banner.backIcon.setOnClickListener(this);
        banner.title.setTextColor(Color.parseColor("#FFFFFF"));
        banner.title.setText(R.string.HealthRecommendationPage_Text_Title);
        banner.rightIcon.setBackgroundResource(R.mipmap.back_page_icon);
        banner.rightIcon.setOnClickListener(this);

        RelativeLayout ReportPanel = (RelativeLayout) ViewScaling.findViewByIdAndScale(currentActivity, R.id.InspectionReport_RelativeLayout_ReportPanel2);
        webView = (WebView) ViewScaling.findViewByIdAndScale(currentActivity, R.id.InspectionReport_WebView_ShowWebView2);
        V7TitleView PrePageButton = (V7TitleView) ViewScaling.findViewByIdAndScale(currentActivity, R.id.InspectionReport_V7TitleView_PrePageButton2);
        PrePageButton.setOnClickListener(this);

        Token token = new Token();

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


//        setContentView(webview);
        webView.setWebViewClient(new WebViewClient()
        {
            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon)
            {
                super.onPageStarted(view, url, favicon);
                SimpleDatabase simpleDatabase = new SimpleDatabase();
                simpleDatabase.setValueByKey(Constant.IMMEDIATE_IMPROVEMENT, 0);
            }
        });
        webView.addJavascriptInterface(this, "android");
        webView.setWebChromeClient(new WebChromeClient());

        Map<String, String> extraHeaders = new HashMap<String, String>();
        extraHeaders.put("v7idea_Token", token.getTokenId());
        url = getIntent().getStringExtra(HealthRecommendationPage2.URL);
        webView.loadUrl(url, extraHeaders);

//        webview.loadUrl("http://scanreport.lohasgen.com/HealthSummary");

        String[] separated = url.split("/");
        String reportID = separated[5];
        if (reportID.isEmpty() == false)
        {
            UpHealthSummaryRead upHealthSummaryRead = new UpHealthSummaryRead();
            upHealthSummaryRead.execute(reportID);
        }
    }

    @JavascriptInterface
    public void setTitle(String s)
    {
        banner.title.setText(s);
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
                else if (view.getId() == R.id.shortBanner_AutoReleaseImageView_rightIcon)
                {
                    onBackPressed();
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
                intent.putExtra(InspectionReportPage.TYPE, "立即改善");
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


    private class UpHealthSummaryRead extends V7ideaAsyncTask<String, ApiResult>
    {
        private HealthSummary healthSummary = null;

        private NotifyDialog progress = null;


        public UpHealthSummaryRead()
        {
            healthSummary = new HealthSummary();
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
                Intent intent = new Intent(currentActivity, InspectionReportPage.class);
                intent.putExtra(InspectionReportPage.TAG, false);
                intent.putExtra(InspectionReportPage.TYPE, "立即改善");
                startActivity(intent);
                finish();
                setTurnInNextPageAnimation(currentActivity);
            }
            else if (apiResult.getErrorNo().contentEquals(Constant.ErrorCode.CODE_404))
            {
                Intent intent = new Intent(currentActivity, InspectionReportPage.class);
                intent.putExtra(InspectionReportPage.TAG, false);
                intent.putExtra(InspectionReportPage.TYPE, "立即改善");
                startActivity(intent);
                finish();
                setTurnInNextPageAnimation(currentActivity);
            }
            else if (apiResult.getErrorNo().contentEquals(Constant.ErrorCode.CODE_9000))
            {
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
            return healthSummary.getReadHealthSummarySoonApi(token.getTokenId(), params[0]);
        }
    }
}
