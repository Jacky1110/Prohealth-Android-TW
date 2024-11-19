package com.v7idea.healthkit;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.v7idea.healthkit.View.BottomButton;
import com.v7idea.template.Activity.BaseActivity;
import com.v7idea.template.AirApplication;
import com.v7idea.template.Tool.ViewScaling;
import com.v7idea.template.View.AutoReleaseImageView;
import com.v7idea.template.View.Banner;
import com.v7idea.template.View.V7TitleView;

/**
 * 2019/1/24 ＊確認使用的頁面 系統設定-立即改善說明1
 */

public class HealthDescriptionPage1 extends BaseActivity
{
    private static final String TAG = "HealthDescriptionPage1";
    RelativeLayout HealthDescriptionPage1 = null;
    AutoReleaseImageView AutoReleaseImageView = null;
    int page = 0;
    private String SystemSettingsPageIntentString = "";
    private boolean SystemSettingsPageIntentData = false;
    private String IntentTitle = "";
    private boolean IntentTitleData = false;
    private RelativeLayout HealthDescriptionPage1_RelativeLayout_Page0;
    private RelativeLayout HealthDescriptionPage1_RelativeLayout_PageContainer;

    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_health_description_page1);

        if (getIntent() != null)
        {
            if (getIntent().getExtras() != null)
            {
                SystemSettingsPageIntentString = getIntent().getExtras().getString("SystemSettingsPage");
                Log.e(TAG, "onCreate SystemSettingsPageIntentString = " + SystemSettingsPageIntentString);
                if (SystemSettingsPageIntentString != null)
                {
                    if (SystemSettingsPageIntentString.isEmpty() == false & SystemSettingsPageIntentString.contentEquals("HealthDescriptionPage1"))
                    {
                        SystemSettingsPageIntentData = true;
                        IntentTitle = getIntent().getExtras().getString("Title","");
                        if (IntentTitle.isEmpty() == false && IntentTitle.contentEquals("HealthRecommendationPage_Text_Title"))
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
        HealthDescriptionPage1 = (RelativeLayout) ViewScaling.findViewByIdAndScale(currentActivity, R.id.HealthDescriptionPage1);

        Banner banner = (Banner) ViewScaling.findViewByIdAndScale(currentActivity, R.id.HealthDescriptionPage1_Banner_Header);
        banner.initShortBanner();
        banner.backIcon.setOnClickListener(OnBackIcon);
        banner.title.setText("立即改善-說明");
        banner.title.setTextColor(Color.parseColor("#FFFFFF"));

        AutoReleaseImageView = (AutoReleaseImageView) ViewScaling.findViewByIdAndScale(currentActivity, R.id.HealthDescriptionPage1_AutoReleaseImageView);

        BottomButton BottomButton = (BottomButton) ViewScaling.findViewByIdAndScale(currentActivity, R.id.HealthDescriptionPage1_BottomButton);
//        BottomButton.setData("略過","下一頁", 100);
//        V7TitleView SkipPage = BottomButton.getLeftButton();
//        SkipPage.setOnClickListener(OnSkipPage);
//        V7TitleView NextPage = BottomButton.getRightButton();
//        NextPage.setOnClickListener(OnNextPage);
        BottomButton.setData("下一頁", 100);
        V7TitleView NextPage = BottomButton.getOneButton();
        NextPage.setOnClickListener(OnNextPage);


        AutoReleaseImageView AutoReleaseImageView_Page0 = (AutoReleaseImageView) ViewScaling.findViewByIdAndScale(currentActivity, R.id.HealthDescriptionPage1_AutoReleaseImageView_Page0);
        ViewScaling.setImageView(AutoReleaseImageView_Page0, (int) (606 * ViewScaling.getScaleValue()), (int) (760 * ViewScaling.getScaleValue()));
        TextView TextView_Page0_Title = (TextView) ViewScaling.findViewByIdAndScale(currentActivity, R.id.HealthDescriptionPage1_TextView_Page0_Title);
        BottomButton BottomButton_Page0 = (BottomButton) ViewScaling.findViewByIdAndScale(currentActivity, R.id.HealthDescriptionPage1_BottomButton_Page0);
        BottomButton_Page0.setData("開始導讀說明", 100);
        HealthDescriptionPage1_RelativeLayout_Page0 = (RelativeLayout) ViewScaling.findViewByIdAndScale(currentActivity, R.id.HealthDescriptionPage1_RelativeLayout_Page0);
        HealthDescriptionPage1_RelativeLayout_PageContainer = (RelativeLayout) ViewScaling.findViewByIdAndScale(currentActivity, R.id.HealthDescriptionPage1_RelativeLayout_PageContainer);


        Intent intent = this.getIntent();
        page = intent.getIntExtra("Healthpage", 0);
        Log.e(TAG, "onCreate = " + " page = " + page);
        if (page == 0)
        {
            page = 1;
            BottomButton_Page0.getOneButton().setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View v)
                {
                    Intent intent = new Intent(currentActivity, HealthDescriptionPage1.class);
                    intent.putExtras(getIntent());
                    intent.putExtra("Healthpage", page);
                    startActivity(intent);
                    finish();
                    setTurnInNextPageAnimation(currentActivity);
                }
            });
            HealthDescriptionPage1_RelativeLayout_Page0.setVisibility(View.VISIBLE);
            HealthDescriptionPage1_RelativeLayout_PageContainer.setVisibility(View.INVISIBLE);
        }
        else if (page == 1)
        {
            page = 2;
            AutoReleaseImageView.setImageResource(R.mipmap.health_description_page1);
        }
        else if (page == 2)
        {
            page = 3;
            AutoReleaseImageView.setImageResource(R.mipmap.health_description_page2);
        }
        else if (page == 3)
        {
            page = 4;
            AutoReleaseImageView.setImageResource(R.mipmap.health_description_page3);
        }

    }

    View.OnClickListener OnNextPage = new View.OnClickListener()
    {
        @Override
        public void onClick(View v)
        {
            AirApplication.setOnclikcFeedback(v);
            if (page < 4)
            {
                Intent intent = new Intent(currentActivity, HealthDescriptionPage1.class);
                intent.putExtras(getIntent());
                intent.putExtra("Healthpage", page);
                startActivity(intent);
                finish();
                setTurnInNextPageAnimation(currentActivity);
            }
            else
            {
                Intent intent = new Intent(currentActivity, HealthDescriptionPage2.class);
                intent.putExtras(getIntent());
                startActivity(intent);
                finish();
                setTurnInNextPageAnimation(currentActivity);
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

            if (SystemSettingsPageIntentData == true)
            {
                Intent intent = new Intent(currentActivity, SystemSettingsPage.class);
                startActivity(intent);
                finish();
                setTurnInNextPageAnimation(currentActivity);
            }
            else
            {
                Intent intent = new Intent(currentActivity, HealthRecommendationPage2.class);
                intent.putExtras(getIntent());
                startActivity(intent);
                finish();
                setTurnInNextPageAnimation(currentActivity);
            }
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
}
