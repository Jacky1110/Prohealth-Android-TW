package com.v7idea.healthkit;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.v7idea.healthkit.View.BottomButton;
import com.v7idea.template.Activity.BaseActivity;
import com.v7idea.template.AirApplication;
import com.v7idea.template.DataBase.SimpleDatabase;
import com.v7idea.template.Tool.ViewScaling;
import com.v7idea.template.View.AutoReleaseImageView;
import com.v7idea.template.View.Banner;
import com.v7idea.template.View.V7TitleView;

/**
 * 2019/1/24 ＊確認使用的頁面 系統設定-健康報告說明2
 */
public class ReportDescriptionPage2 extends BaseActivity
{
    private static final String TAG = "ReportDescriptionPage2";
    private ImageView CheckIcon = null;
    private TextView textView = null;
    private String SystemSettingsPageIntentString = "";
    private boolean SystemSettingsPageIntentData = false;
    private String IntentTitle = "";
    private boolean IntentTitleData = false;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_report_description_page2);
        if (getIntent() != null)
        {
            if (getIntent().getExtras() != null)
            {
                SystemSettingsPageIntentString = getIntent().getExtras().getString("SystemSettingsPage");
                Log.e(TAG, "onCreate SystemSettingsPageIntentString = " + SystemSettingsPageIntentString);
                if (SystemSettingsPageIntentString != null)
                {
                    if (SystemSettingsPageIntentString.isEmpty() == false && SystemSettingsPageIntentString.contentEquals("ReportDescriptionPage1"))
                    {
                        SystemSettingsPageIntentData = true;
                        IntentTitle = getIntent().getExtras().getString("Title","");
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

        Banner banner = (Banner) ViewScaling.findViewByIdAndScale(currentActivity, R.id.ReportDescriptionPage2_Banner_Header);
        banner.initShortBanner();
        banner.backIcon.setOnClickListener(OnBackIcon);
        banner.title.setText("健康報告-說明");
        banner.title.setTextColor(Color.parseColor("#FFFFFF"));

        AutoReleaseImageView AutoReleaseImageView = (AutoReleaseImageView) ViewScaling.findViewByIdAndScale(currentActivity, R.id.ReportDescriptionPage2_AutoReleaseImageView);
        LinearLayout CheckBoxContainer = (LinearLayout) ViewScaling.findViewByIdAndScale(currentActivity, R.id.ReportDescriptionPage2_LinearLayout_CheckBoxContainer);
        CheckIcon = (ImageView) ViewScaling.findViewByIdAndScale(currentActivity, R.id.ReportDescriptionPage2_ImageView_CheckIcon);
        textView = (TextView) ViewScaling.findViewByIdAndScale(currentActivity, R.id.ReportDescriptionPage2_TextView_MiddleTitle);
        TextView CheckBoxHint = (TextView) ViewScaling.findViewByIdAndScale(currentActivity, R.id.ReportDescriptionPage2_TextView_CheckBoxHint);

        CheckIcon.setOnClickListener(onPressCheckBoxImageOrMiddleTitle);
        textView.setOnClickListener(onPressCheckBoxImageOrMiddleTitle);

        BottomButton BottomButton = (BottomButton) ViewScaling.findViewByIdAndScale(currentActivity, R.id.ReportDescriptionPage2_BottomButton);

        if (SystemSettingsPageIntentData == true)
        {
            if (IntentTitleData == true)
            {
                BottomButton.setData("返回上一頁", 100);
                V7TitleView NextPage = BottomButton.getOneButton();
                NextPage.setOnClickListener(new View.OnClickListener()
                {
                    @Override
                    public void onClick(View v)
                    {
                        Intent intent = new Intent(currentActivity, InspectionReportPage.class);
                        intent.putExtras(getIntent());
                        startActivity(intent);
                        finish();
                        setTurnInNextPageAnimation(currentActivity);
                    }
                });
                CheckBoxContainer.setVisibility(View.INVISIBLE);
                CheckBoxHint.setVisibility(View.INVISIBLE);
            }
            else
            {
                BottomButton.setData("返回系統設定", 100);
                V7TitleView NextPage = BottomButton.getOneButton();
                NextPage.setOnClickListener(new View.OnClickListener()
                {
                    @Override
                    public void onClick(View v)
                    {
                        Intent intent = new Intent(currentActivity, SystemSettingsPage.class);
                        startActivity(intent);
                        finish();
                        setTurnInNextPageAnimation(currentActivity);
                    }
                });
                CheckBoxContainer.setVisibility(View.INVISIBLE);
                CheckBoxHint.setVisibility(View.INVISIBLE);
            }
        }
        else
        {
            BottomButton.setData("閱讀健康報告", 100);
            V7TitleView NextPage = BottomButton.getOneButton();
            NextPage.setOnClickListener(OnNextPage);
            CheckBoxContainer.setVisibility(View.VISIBLE);
            CheckBoxHint.setVisibility(View.VISIBLE);
        }

    }

    private View.OnClickListener onPressCheckBoxImageOrMiddleTitle = new View.OnClickListener()
    {
        @Override
        public void onClick(View v)
        {
            AirApplication.setOnclikcFeedback(v);
            boolean isSelected = !CheckIcon.isSelected();
            CheckIcon.setSelected(isSelected);
            textView.setSelected(isSelected);
        }
    };

    View.OnClickListener OnNextPage = new View.OnClickListener()
    {
        @Override
        public void onClick(View v)
        {
            AirApplication.setOnclikcFeedback(v);
            SimpleDatabase simpleDatabase = new SimpleDatabase();
            simpleDatabase.setValueByKey(Constant.IS_NOT_INTRO_REPORT, CheckIcon.isSelected());

            Intent intent = new Intent(currentActivity, InspectionReportPage.class);
            intent.putExtras(getIntent());
            startActivity(intent);
            finish();
            setTurnInNextPageAnimation(currentActivity);
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
