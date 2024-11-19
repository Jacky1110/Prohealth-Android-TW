package com.v7idea.healthkit;

import android.graphics.Paint;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.v7idea.healthkit.View.BottomButton;
import com.v7idea.template.Activity.BaseActivity;
import com.v7idea.template.Tool.ViewScaling;
import com.v7idea.template.View.AutoReleaseImageView;
import com.v7idea.template.View.V7TitleView;
/**
 * 2019/1/24 沒有使用的頁面
 */
public class BluetoothErrorMessagePage extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bluetooth_error_message_page);

        AutoReleaseImageView BannerImage = (AutoReleaseImageView) ViewScaling.findViewByIdAndScale(currentActivity,R.id.BluetoothErrorMessagePage_AutoReleaseImageView_BannerImage);
        AutoReleaseImageView BackImage = (AutoReleaseImageView) ViewScaling.findViewByIdAndScale(currentActivity,R.id.BluetoothErrorMessagePage_AutoReleaseImageView_BackImage);
        AutoReleaseImageView ErrorIcon = (AutoReleaseImageView) ViewScaling.findViewByIdAndScale(currentActivity,R.id.BluetoothErrorMessagePage_AutoReleaseImageView_errorIcon);
        RelativeLayout Container = (RelativeLayout) ViewScaling.findViewByIdAndScale(currentActivity,R.id.BluetoothErrorMessagePage_RelativeLayout_Container);
        V7TitleView Mode2MiddleTitle = (V7TitleView) ViewScaling.findViewByIdAndScale(currentActivity,R.id.BluetoothErrorMessagePage_V7TitleView_Mode2_MiddleTitle);
        LinearLayout Mode2TextViewContainer = (LinearLayout) ViewScaling.findViewByIdAndScale(currentActivity,R.id.BluetoothErrorMessagePage_LinearLayout_Mode2_TextViewContainer);
        TextView Item = (TextView) ViewScaling.findViewByIdAndScale(currentActivity,R.id.BluetoothErrorMessagePage_TextView_Item);
        TextView Error = (TextView) ViewScaling.findViewByIdAndScale(currentActivity,R.id.BluetoothErrorMessagePage_TextView_Error);

        BottomButton BottomButton = (BottomButton) ViewScaling.findViewByIdAndScale(currentActivity, R.id.CheckCodePage_BottomButton);
        BottomButton.setData(getResources().getString(R.string.CheckCodePage_Text_Cancel), getResources().getString(R.string.CheckCodePage_Text_Send), 100);

        V7TitleView V7TitleView_Cancel = BottomButton.getLeftButton();
        V7TitleView V7TitleView_Send = BottomButton.getRightButton();


    }

}
