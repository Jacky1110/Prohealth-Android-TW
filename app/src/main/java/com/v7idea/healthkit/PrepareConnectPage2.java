package com.v7idea.healthkit;

import android.app.AlertDialog;
import android.bluetooth.BluetoothAdapter;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
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
import com.v7idea.template.View.V7TitleView;
/**
 * 2019/1/24 ＊確認使用的頁面 系統設定-配對操作說明2
 */
public class PrepareConnectPage2 extends BaseActivity {
    private ImageView CheckIcon = null;
    private TextView textView = null;
    private String SystemSettingsPageIntentString = "";
    private boolean SystemSettingsPageIntentData = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_prepare_connect_page2);
        if (getIntent() != null)
        {
            if (getIntent().getExtras() != null)
            {
                SystemSettingsPageIntentString = getIntent().getExtras().getString("SystemSettingsPage");
                if (SystemSettingsPageIntentString != null) {
                    if (SystemSettingsPageIntentString.isEmpty() == false & SystemSettingsPageIntentString.contentEquals("PrepareConnectPage1")) {
                        SystemSettingsPageIntentData = true;
                    }
                }
            }
        }
        AutoReleaseImageView headerImage = (AutoReleaseImageView) ViewScaling.findViewByIdAndScale(currentActivity, R.id.PrepareConnectPage2_AutoReleaseImageView_Header);
        AutoReleaseImageView contentImage = (AutoReleaseImageView) ViewScaling.findViewByIdAndScale(currentActivity, R.id.PrepareConnectPage2_AutoReleaseImageView_ContentImage);

        LinearLayout CheckBoxContainer = (LinearLayout) ViewScaling.findViewByIdAndScale(currentActivity, R.id.PrepareConnectPage2_LinearLayout_CheckBoxContainer);

        CheckIcon = (ImageView) ViewScaling.findViewByIdAndScale(currentActivity, R.id.PrepareConnectPage2_ImageView_CheckIcon);
        textView = (TextView) ViewScaling.findViewByIdAndScale(currentActivity, R.id.PrepareConnectPage2_TextView_MiddleTitle);
        TextView CheckBoxHint = (TextView) ViewScaling.findViewByIdAndScale(currentActivity, R.id.PrepareConnectPage2_TextView_CheckBoxHint);

        CheckIcon.setOnClickListener(onPressCheckBoxImageOrMiddleTitle);
        textView.setOnClickListener(onPressCheckBoxImageOrMiddleTitle);

        BottomButton bottomButton = (BottomButton) ViewScaling.findViewByIdAndScale(currentActivity, R.id.PrepareConnectPage2_BottomButton);
        if (SystemSettingsPageIntentData == true) {
            bottomButton.setData("返回系統設定", 100);
            V7TitleView NextPage = bottomButton.getOneButton();
            NextPage.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(currentActivity, SystemSettingsPage.class);
                    startActivity(intent);
                    finish();
                    setTurnInNextPageAnimation(currentActivity);
                }
            });
            CheckBoxContainer.setVisibility(View.INVISIBLE);
            CheckBoxHint.setVisibility(View.INVISIBLE);
        } else {
            bottomButton.setData(getString(R.string.ScanDevicePage_Text_Mode1_BottomBotton), 100);
            bottomButton.getOneButton().setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    AirApplication.setOnclikcFeedback(v);
                    SimpleDatabase simpleDatabase = new SimpleDatabase();
                    simpleDatabase.setValueByKey(Constant.IS_OPERATION_INSTRUCTIONS, CheckIcon.isSelected());

                    Intent intent = new Intent(currentActivity, ScanDevicePage.class);
                    startActivity(intent);
                    currentActivity.finish();
                    setTurnInNextPageAnimation(currentActivity);
                }
            });
            CheckBoxContainer.setVisibility(View.VISIBLE);
            CheckBoxHint.setVisibility(View.VISIBLE);
        }
    }

    private View.OnClickListener onPressCheckBoxImageOrMiddleTitle = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            AirApplication.setOnclikcFeedback(v);
            boolean isSelected = !CheckIcon.isSelected();
            CheckIcon.setSelected(isSelected);
            textView.setSelected(isSelected);
        }
    };

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
