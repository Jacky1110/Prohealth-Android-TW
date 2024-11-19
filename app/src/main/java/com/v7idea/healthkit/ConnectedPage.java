package com.v7idea.healthkit;

import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothProfile;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Paint;
import android.os.Bundle;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.style.LeadingMarginSpan;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.v7idea.template.Activity.BaseActivity;
import com.v7idea.template.AirApplication;
import com.v7idea.template.Tool.DebugLog;
import com.v7idea.template.Tool.NotifyDialog;
import com.v7idea.template.Tool.ViewScaling;
import com.v7idea.template.View.AutoReleaseImageView;
import com.v7idea.v7rcliteandroidsdk.V7RCLiteController;


/**
 * 2018/12/25 癈棄使用
 */
/**
 * 2019/1/24 沒有使用的頁面
 */
public class ConnectedPage extends BaseActivity implements V7RCLiteController.BluetoothCallBack, View.OnClickListener {
    private static final String TAG = "ConnectedPage";

    private AirApplication thisApp = null;
    private V7RCLiteController remoteController = null;

    private TextView NextStep = null;
    private NotifyDialog progressDialog = null;

    private String currentCommand = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_connected_page);

        thisApp = (AirApplication) getApplication();
        remoteController = thisApp.getBleController();
        remoteController.setCallBack(this);

        AutoReleaseImageView Banner = (AutoReleaseImageView) ViewScaling.findViewByIdAndScale(currentActivity, R.id.ConnectedPage_AutoReleaseImageView_Banner);
        ViewScaling.setImageView(Banner);
        AutoReleaseImageView BackImage = (AutoReleaseImageView) ViewScaling.findViewByIdAndScale(currentActivity, R.id.ConnectedPage_AutoReleaseImageView_BackImage);
        BackImage.setOnClickListener(this);

        TextView MiddleTitle = (TextView) ViewScaling.findViewByIdAndScale(currentActivity, R.id.ConnectedPage_TextView_MiddleTitle);
        LinearLayout AlertTitleContainer = (LinearLayout) ViewScaling.findViewByIdAndScale(currentActivity, R.id.ConnectedPage_LinearLayout_AlertTitleContainer);
        TextView AlertTitle1 = (TextView) ViewScaling.findViewByIdAndScale(currentActivity, R.id.ConnectedPage_TextView_AlertTitle1);
        TextView AlertTitle2 = (TextView) ViewScaling.findViewByIdAndScale(currentActivity, R.id.ConnectedPage_TextView_AlertTitle2);

        SpannableStringBuilder demoSpannableString1 = getDemoSpannable("1. ", "1. 若有配戴寶石類項鍊、電子手錶，請卸除。（耳環、 眼鏡除外）。", AlertTitle1.getPaint());
        demoSpannableString1.append("\n");
        demoSpannableString1.append(getDemoSpannable("2. ", "2. 若有配戴手套，請脫掉手套。", AlertTitle1.getPaint()));
        demoSpannableString1.append("\n");
        demoSpannableString1.append(getDemoSpannable("3. ", "3. 雙手雙腳請平放、勿交叉。", AlertTitle1.getPaint()));
        demoSpannableString1.append("\n");
        demoSpannableString1.append(getDemoSpannable("4. ", "4. 受量測者請移除身上其他具有通訊功能之電子設備（如手機）。", AlertTitle1.getPaint()));
        demoSpannableString1.append("\n");
        demoSpannableString1.append(getDemoSpannable("5. ", "5. 受量測者與其他人請保持至少50cm的距離。", AlertTitle1.getPaint()));

        SpannableStringBuilder demoSpannableString2 = getDemoSpannable("6. ", "6. 請按下【開始量測】按鍵，並將四指完全平貼於NIR+內側，食指及無名指按壓於NIR+內側上下鈕，當按壓正確，NIR+的燈號顯示為綠燈恆亮，請保持手勢不動，開始進行量測。", AlertTitle2.getPaint());
        demoSpannableString2.append("\n");
        demoSpannableString2.append(getDemoSpannable("7. ", "7. 若按壓不正確或環境光源太亮、NIR+顯示操作失誤，紅色LED則會顯示恆亮，請避開光源，並重新按『步驟6』重新操作。", AlertTitle2.getPaint()));

        AlertTitle1.setText(demoSpannableString1, TextView.BufferType.SPANNABLE);
        AlertTitle2.setText(demoSpannableString2, TextView.BufferType.SPANNABLE);

        NextStep = (TextView) ViewScaling.findViewByIdAndScale(currentActivity, R.id.ConnectedPage_TextView_NextStep);
        NextStep.setOnClickListener(this);
    }

    private SpannableStringBuilder getDemoSpannable (final String headIndentString, String shwoString, final Paint paint) {
        final int columnIndentation = (int)paint.measureText(headIndentString);


        SpannableStringBuilder demoSpannableString = new SpannableStringBuilder(shwoString);

        demoSpannableString.setSpan(new LeadingMarginSpan.Standard(0, columnIndentation), 0, demoSpannableString.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        return demoSpannableString;
    }


    @Override
    public void onClick(View v) {
        AirApplication.setOnclikcFeedback(v);
        switch (v.getId()) {
            case R.id.ConnectedPage_AutoReleaseImageView_BackImage: {
                Animation scaleAnimation = AnimationUtils.loadAnimation(currentActivity, R.anim.normal_button_alaph_animation);
                scaleAnimation.setAnimationListener(new Animation.AnimationListener() {
                    @Override
                    public void onAnimationStart(Animation animation) {

                    }

                    @Override
                    public void onAnimationEnd(Animation animation) {
                        onBackPressed();
                    }

                    @Override
                    public void onAnimationRepeat(Animation animation) {

                    }
                });
                v.setAnimation(scaleAnimation);
                v.startAnimation(scaleAnimation);
            }
            break;

            case R.id.ConnectedPage_TextView_NextStep: {

                Intent intent = new Intent(currentActivity, CheckConnectedPage.class);
                startActivity(intent);
                finish();
                setTurnInNextPageAnimation(currentActivity);
            }
            break;
        }
    }


    @Override
    public void onBackPressed() {
        if (remoteController != null) {
            remoteController.closeConnection();
        }
        Intent intent = new Intent(currentActivity, ScanDevicePage.class);
        startActivity(intent);
        finish();
        setBackInPrePageAnimation(currentActivity);
    }

    @Override
    public void onConnected() {

    }

    @Override
    public void onDisconnected() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                showErrorAlert("藍牙被干擾造成斷線必須重新量測", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (remoteController != null) {
                            if (remoteController.getDeviceState() == BluetoothProfile.STATE_CONNECTED) {
                                remoteController.closeConnection();
                            }
                        }
                        Intent intent = new Intent(currentActivity, ScanDevicePage.class);
                        startActivity(intent);
                        currentActivity.finish();
                    }
                });
            }
        });
    }

    @Override
    public void onDiscoverCharacteristics() {

    }

    @Override
    public void onCharacteristicChanged(BluetoothGattCharacteristic bluetoothGattCharacteristic) {
        byte[] getValue = bluetoothGattCharacteristic.getValue();


        if (getValue != null && getValue.length > 0) {
            final String receiveData = thisApp.byteArrayToUTF8String(getValue);
            DebugLog.d(TAG, "receiveData: " + receiveData);
        }
    }

    @Override
    public void onCharacteristicWrite(BluetoothGattCharacteristic bluetoothGattCharacteristic, int i) {

    }


    @Override
    public void onScanResult(BluetoothDevice bluetoothDevice, int i, byte[] bytes) {

    }

    @Override
    public void enable() {

    }

    @Override
    public void disable() {

    }

    @Override
    public void notSupport() {

    }

    @Override
    public void onGattError(int i) {

    }

    @Override
    public void onReadRssi(int i, int i1) {

    }

    @Override
    public void onMtuChanged(int i) {

    }
}
