package com.v7idea.healthkit;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.v7idea.healthkit.View.ImageTextView;
import com.v7idea.template.Activity.BaseActivity;
import com.v7idea.template.AirApplication;
import com.v7idea.template.DataBase.SimpleDatabase;
import com.v7idea.template.Tool.ViewScaling;
import com.v7idea.template.View.AutoReleaseImageView;
/**
 * 2019/1/24 ＊確認使用的頁面 進行量測-NIR+量測設備燈號狀態說明
 */
public class LightDescriptionPage extends BaseActivity {
    private static final String TAG = "LightDescriptionPage";
    ImageTextView mImageTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_light_description_page);

        AutoReleaseImageView SpecialImageView_Background = (AutoReleaseImageView) ViewScaling.findViewByIdAndScale(currentActivity, R.id.LightDescriptionPage_AutoReleaseImageView_Banner);
        ViewScaling.setImageView(SpecialImageView_Background);
        AutoReleaseImageView BackImage = (AutoReleaseImageView) ViewScaling.findViewByIdAndScale(currentActivity, R.id.LightDescriptionPage_AutoReleaseImageView_BackImage);
        BackImage.setOnClickListener(onBackClick);

        mImageTextView = (ImageTextView) ViewScaling.findViewByIdAndScale(currentActivity, R.id.LightDescriptionPage_ImageTextView);
        mImageTextView.setIconVisibility(View.GONE);
        mImageTextView.setText(getResources().getString(R.string.LightDescriptionPage_Text_Button));
        mImageTextView.setOnClickListener(onNextClick);

        TextView Title = (TextView) ViewScaling.findViewByIdAndScale(currentActivity, R.id.LightDescriptionPage_TextView_Title);
        LinearLayout ItemContainer = (LinearLayout) ViewScaling.findViewByIdAndScale(currentActivity, R.id.LightDescriptionPage_LinearLayout_ItemContainer);
        ImageView Icon1 = (ImageView) ViewScaling.findViewByIdAndScale(currentActivity, R.id.LightDescriptionPage_ImageView_Icon1);
        TextView Item1 = (TextView) ViewScaling.findViewByIdAndScale(currentActivity, R.id.LightDescriptionPage_TextView_Item1);
        ImageView Icon2 = (ImageView) ViewScaling.findViewByIdAndScale(currentActivity, R.id.LightDescriptionPage_ImageView_Icon2);
        TextView Item2 = (TextView) ViewScaling.findViewByIdAndScale(currentActivity, R.id.LightDescriptionPage_TextView_Item2);
        ImageView Icon3 = (ImageView) ViewScaling.findViewByIdAndScale(currentActivity, R.id.LightDescriptionPage_ImageView_Icon3);
        TextView Item3 = (TextView) ViewScaling.findViewByIdAndScale(currentActivity, R.id.LightDescriptionPage_TextView_Item3);
        ImageView Icon4 = (ImageView) ViewScaling.findViewByIdAndScale(currentActivity, R.id.LightDescriptionPage_ImageView_Icon4);
        TextView Item4 = (TextView) ViewScaling.findViewByIdAndScale(currentActivity, R.id.LightDescriptionPage_TextView_Item4);
        ImageView Icon5 = (ImageView) ViewScaling.findViewByIdAndScale(currentActivity, R.id.LightDescriptionPage_ImageView_Icon5);
        TextView Item5 = (TextView) ViewScaling.findViewByIdAndScale(currentActivity, R.id.LightDescriptionPage_TextView_Item5);
        ImageView Icon6 = (ImageView) ViewScaling.findViewByIdAndScale(currentActivity, R.id.LightDescriptionPage_ImageView_Icon6);
        TextView Item6 = (TextView) ViewScaling.findViewByIdAndScale(currentActivity, R.id.LightDescriptionPage_TextView_Item6);


    }


    private View.OnClickListener onNextClick = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            AirApplication.setOnclikcFeedback(v);
            SimpleDatabase simpleDatabase = new SimpleDatabase();
            boolean isOperationInstructions = simpleDatabase.getBooleanValueByKey(Constant.IS_OPERATION_INSTRUCTIONS, false);
            Log.e(TAG, "onClick = " + " isOperationInstructions = " + isOperationInstructions);

            Intent intent = null;

            if (isOperationInstructions) {
                intent = new Intent(currentActivity, ScanDevicePage.class);
            } else {
                intent = new Intent(currentActivity, PrepareConnectPage1.class);
            }
            startActivity(intent);
            currentActivity.finish();
            setTurnInNextPageAnimation(currentActivity);
        }
    };
    private View.OnClickListener onBackClick = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            AirApplication.setOnclikcFeedback(v);
            Intent intent = new Intent(currentActivity, MainActivity.class);
            startActivity(intent);
            finish();
            setTurnInNextPageAnimation(currentActivity);
        }
    };

    public void onBackPressed() {
        Intent intent = new Intent(currentActivity, MainActivity.class);
        startActivity(intent);
        finish();
        setBackInPrePageAnimation(currentActivity);
    }
}
