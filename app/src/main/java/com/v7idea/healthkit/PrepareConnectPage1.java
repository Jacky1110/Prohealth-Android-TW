package com.v7idea.healthkit;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.v7idea.healthkit.View.BottomButton;
import com.v7idea.template.Activity.BaseActivity;
import com.v7idea.template.AirApplication;
import com.v7idea.template.Tool.ViewScaling;
import com.v7idea.template.View.AutoReleaseImageView;
/**
 * 2019/1/24 ＊確認使用的頁面 系統設定-配對操作說明1
 */
public class PrepareConnectPage1 extends BaseActivity {
    private static final String TAG = "PrepareConnectPage1";
    int page = 1;
    private String SystemSettingsPageIntentString = "";
    private boolean SystemSettingsPageIntentData = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_prepare_connect_page1);
        if (getIntent()!=null)
        {
            if (getIntent().getExtras()!=null)
            {
                SystemSettingsPageIntentString = getIntent().getExtras().getString("SystemSettingsPage");
                Log.e(TAG, "onCreate SystemSettingsPageIntentString = " + SystemSettingsPageIntentString);
                if (SystemSettingsPageIntentString != null) {
                    if (SystemSettingsPageIntentString.isEmpty() == false & SystemSettingsPageIntentString.contentEquals("PrepareConnectPage1")) {
                        SystemSettingsPageIntentData = true;
                    }
                }
            }
        }
        AutoReleaseImageView headerImage = (AutoReleaseImageView) ViewScaling.findViewByIdAndScale(currentActivity, R.id.PrepareConnectPage_AutoReleaseImageView_Header);
        AutoReleaseImageView contentImage = (AutoReleaseImageView) ViewScaling.findViewByIdAndScale(currentActivity, R.id.PrepareConnectPage_AutoReleaseImageView_ContentImage);
        Intent intent = this.getIntent();
        page = intent.getIntExtra("PrepareConnectPage1", 1);
        Log.e(TAG, "onCreate " + " page = " + page);
        if (page == 1) {
            page = 2;
            contentImage.getLayoutParams().width = (int) (750 * ViewScaling.getScaleValue());
            contentImage.getLayoutParams().height = (int) (1170 * ViewScaling.getScaleValue());
            contentImage.setImageResource(R.mipmap.prepare_connect_page1_bg);
        } else if (page == 2) {
            page = 3;
            contentImage.getLayoutParams().width = (int) (674 * ViewScaling.getScaleValue());
            contentImage.getLayoutParams().height = (int) (1124 * ViewScaling.getScaleValue());
            contentImage.setImageResource(R.mipmap.prepare_connect_page2_bg);
        } else if (page == 3) {
            page = 4;
            contentImage.getLayoutParams().width = (int) (648 * ViewScaling.getScaleValue());
            contentImage.getLayoutParams().height = (int) (1052 * ViewScaling.getScaleValue());
            contentImage.setImageResource(R.mipmap.prepare_connect_page3_bg);
        } else if (page == 4) {
            page = 5;
            contentImage.getLayoutParams().width = (int) (750 * ViewScaling.getScaleValue());
            contentImage.getLayoutParams().height = (int) (1170 * ViewScaling.getScaleValue());
            contentImage.setImageResource(R.mipmap.prepare_connect_page4_bg);
        } else if (page == 5) {
            page = 6;
            contentImage.getLayoutParams().width = (int) (750 * ViewScaling.getScaleValue());
            contentImage.getLayoutParams().height = (int) (1170 * ViewScaling.getScaleValue());
            contentImage.setImageResource(R.mipmap.prepare_connect_page5_bg);
        } else {
            page = 6;
        }

        BottomButton bottomButton = (BottomButton) ViewScaling.findViewByIdAndScale(currentActivity, R.id.PrepareConnectPage_BottomButton);
        bottomButton.setData(getString(R.string.ScanDevicePage_Text_Mode1_BottomBotton), 100);
        bottomButton.getOneButton().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AirApplication.setOnclikcFeedback(v);

                if (page < 6) {
                    Intent intent = new Intent(currentActivity, PrepareConnectPage1.class);
                    if (SystemSettingsPageIntentData == true) intent.putExtras(getIntent());
                    intent.putExtra("PrepareConnectPage1", page);
                    startActivity(intent);
                    finish();
                    setTurnInNextPageAnimation(currentActivity);
                } else {
                    Intent intent = new Intent(currentActivity, PrepareConnectPage2.class);
                    if (SystemSettingsPageIntentData == true) intent.putExtras(getIntent());
                    startActivity(intent);
                    finish();
                    setTurnInNextPageAnimation(currentActivity);
                }
            }
        });
    }


    @Override
    public void onBackPressed() {
        if (SystemSettingsPageIntentData == true) {
            Intent intent = new Intent(currentActivity, SystemSettingsPage.class);
            startActivity(intent);
            finish();
            setTurnInNextPageAnimation(currentActivity);
        } else {
            Intent intent = new Intent(currentActivity, MainActivity.class);
            startActivity(intent);
            finish();
            setTurnInNextPageAnimation(currentActivity);
        }
    }
}
