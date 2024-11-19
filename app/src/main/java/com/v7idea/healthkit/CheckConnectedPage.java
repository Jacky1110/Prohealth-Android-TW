package com.v7idea.healthkit;

import android.app.AlertDialog;
import android.app.Dialog;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothProfile;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.style.AbsoluteSizeSpan;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import com.v7idea.healthkit.Domain.FirmwareData;
import com.v7idea.healthkit.Model.Member;
import com.v7idea.template.Activity.BaseActivity;
import com.v7idea.template.AirApplication;
import com.v7idea.template.Tool.DebugLog;
import com.v7idea.template.Tool.NotifyDialog;
import com.v7idea.template.Tool.Utils;
import com.v7idea.template.Tool.ViewScaling;
import com.v7idea.template.View.AutoReleaseImageView;
import com.v7idea.v7rcliteandroidsdk.V7RCLiteController;

/**
 * 2019/1/24 ＊確認使用的頁面 進行量測-手指按壓
 */
public class CheckConnectedPage extends BaseActivity implements V7RCLiteController.BluetoothCallBack
{
    private static final String TAG = "CheckConnectedPage";

    private AirApplication thisApp = null;
    private V7RCLiteController remoteController = null;

    private NotifyDialog progressDialog = null;
    private TimeOutCountDownTimer timeOutCountDownTimer = null;
    private BEHAVIOR_CHECKCountDownTimer behavior_checkCountDownTimer = null;
    private CheckData_CountDownTimer checkDataCountDownTimer = null;
    private String currentCommand = "";
    private Handler handler = null;
    private TextView TextView_Error = null;
    private Member member = null;
    private TextView AlertTitle = null;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_check_connected_page);

        thisApp = (AirApplication) getApplication();
        remoteController = thisApp.getBleController();
        remoteController.setCallBack(this);

        handler = new Handler();
        AutoReleaseImageView SpecialImageView_Background = (AutoReleaseImageView) ViewScaling.findViewByIdAndScale(currentActivity, R.id.CheckConnectedPage_AutoReleaseImageView_Banner);
//        ViewScaling.setImageView(SpecialImageView_Background);

        AutoReleaseImageView BackImage = (AutoReleaseImageView) ViewScaling.findViewByIdAndScale(currentActivity, R.id.CheckConnectedPage_AutoReleaseImageView_BackImage);
        BackImage.setOnClickListener(OnBackIcon);

        AlertTitle = (TextView) ViewScaling.findViewByIdAndScale(currentActivity, R.id.CheckConnectedPage_TextView_AlertTitle);

        behavior_checkCountDownTimer = new BEHAVIOR_CHECKCountDownTimer(3000, 1000);
        behavior_checkCountDownTimer.start();
        timeOutCountDownTimer = new TimeOutCountDownTimer(12000, 1000);
        timeOutCountDownTimer.start();
        checkDataCountDownTimer = new CheckData_CountDownTimer(11000, 1000);

        TextView_Error = (TextView) ViewScaling.findViewByIdAndScale(currentActivity, R.id.CheckConnectedPage_TextView_Error);

        member = new Member();
        Log.e(TAG, "isTestUser = " + member.isTestUser);

        if (member.isTestUser.contentEquals("1"))
        {
            TextView_Error.setVisibility(View.VISIBLE);
        }
        else if (member.isTestUser.contentEquals("0"))
        {
            TextView_Error.setVisibility(View.GONE);
        }
    }

    @Override
    protected void onDestroy()
    {
        super.onDestroy();
        if (alertdialog != null)
        {
            alertdialog.dismiss();
        }
    }

    public class BEHAVIOR_CHECKCountDownTimer extends CountDownTimer
    {
        //0.001秒為單位，1000=1秒,millisInFuture=總時間,countDownInterval=倒數的間隔時間
        public BEHAVIOR_CHECKCountDownTimer(long millisInFuture, long countDownInterval)
        {
            super(millisInFuture, countDownInterval);
        }

        @Override
        public void onTick(long millisUntilFinished)
        {
            // 每倒數一次,會執行這裡
            Log.e(TAG, "onTick = " + millisUntilFinished / 1000);
        }

        @Override
        public void onFinish()
        {
            // 倒數結束時,會執行這裡
            if (remoteController.getDeviceState() == BluetoothProfile.STATE_CONNECTED)
            {
                //發送BEHAVIOR CHECK!!
                currentCommand = Constant.Command.BEHAVIOR_CHECK;
                byte[] bytesCommand = thisApp.toUTF8ByteArray(currentCommand);

                if (bytesCommand != null)
                {

                    boolean isSendSuccess = remoteController.sendCommand(bytesCommand);

                    if (isSendSuccess)
                    {
                        progressDialog = new NotifyDialog(currentActivity);
                        progressDialog.settingProgressDialog();
                    }
                }

            }
            setCheckDataCountDownTimer(true);
        }
    }

    boolean is10SecEnd = false;
    boolean isFirmwareData = false;
    boolean isCheckSuccess = false;
    boolean isIntentPage = false;

    public class TimeOutCountDownTimer extends CountDownTimer
    {
        //0.001秒為單位，1000=1秒,millisInFuture=總時間,countDownInterval=倒數的間隔時間
        public TimeOutCountDownTimer(long millisInFuture, long countDownInterval)
        {
            super(millisInFuture, countDownInterval);
        }

        @Override
        public void onTick(long millisUntilFinished)
        {
            // 每倒數一次,會執行這裡
            Log.e(TAG, "TimeOutCountDownTimer onTick = " + millisUntilFinished / 1000);
            int sec = (int) (millisUntilFinished / 1000);
            final SpannableStringBuilder time = setTimeOutString(sec);
            runOnUiThread(new Runnable()
            {
                @Override
                public void run()
                {
                    AlertTitle.setText(time);
                }
            });

            if (sec <= 10 && sec != 0
                    && isFirmwareData == true
                    && is10SecEnd == false
                    && isCheckSuccess == false)
            {
                currentCommand = Constant.Command.QUERY_ERROR_STA;
                byte[] command = thisApp.toUTF8ByteArray(currentCommand);
                if (command != null)
                {
                    boolean isSendSuccess = remoteController.sendCommand(thisApp.toUTF8ByteArray(currentCommand));
                    Log.e(TAG, "QUERY_ERROR_STA isSend status: " + isSendSuccess);
                    setCheckDataCountDownTimer(true);
                }
            }

        }

        @Override
        public void onFinish()
        {
            // 倒數結束時,會執行這裡

            final SpannableStringBuilder time = setTimeOutString(0);
            runOnUiThread(new Runnable()
            {
                @Override
                public void run()
                {
                    AlertTitle.setText(time);
                }
            });

            if (is10SecEnd == false && isCheckSuccess == false)
            {
                timeOutCountDownTimer = new TimeOutCountDownTimer(11000, 1000);
                is10SecEnd = true;
                currentCommand = Constant.Command.QUERY_ERROR_STA;
                byte[] command = thisApp.toUTF8ByteArray(currentCommand);
                if (command != null)
                {
                    boolean isSendSuccess = remoteController.sendCommand(thisApp.toUTF8ByteArray(currentCommand));
                    Log.e(TAG, "QUERY_ERROR_STA isSend status: " + isSendSuccess);
                }
            }

            if (isCheckSuccess == true)
            {
                is10SecEnd = true;

                if (isFinishing() == false)
                {
                    runOnUiThread(new Runnable()
                    {
                        @Override
                        public void run()
                        {
                            timeOutCountDownTimer.cancel();
                            setCheckDataCountDownTimer(false);
                            showErrorAlert("目前無法正確收到NIR+的機器序號，請重新開始", new DialogInterface.OnClickListener()
                            {
                                @Override
                                public void onClick(DialogInterface dialog, int which)
                                {
                                    if (remoteController != null)
                                    {
                                        if (remoteController.getDeviceState() == BluetoothProfile.STATE_CONNECTED)
                                        {
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

            }
        }
    }

    public class CheckData_CountDownTimer extends CountDownTimer
    {
        public int acount = 0;

        //0.001秒為單位，1000=1秒,millisInFuture=總時間,countDownInterval=倒數的間隔時間
        public CheckData_CountDownTimer(long millisInFuture, long countDownInterval)
        {
            super(millisInFuture, countDownInterval);
        }

        @Override
        public void onTick(long millisUntilFinished)
        {
            // 每倒數一次,會執行這裡
            acount++;
            Log.e(TAG, "acount = " + acount);


            if (acount % 3 == 0)
            {

                if (currentCommand.contentEquals(Constant.Command.BEHAVIOR_CHECK))
                {
                    currentCommand = Constant.Command.MEASURE_MODE_END;
                    byte[] bytesCommand = thisApp.toUTF8ByteArray(currentCommand);

                    if (bytesCommand != null)
                    {

                        boolean isSendSuccess = remoteController.sendCommand(bytesCommand);
                        DebugLog.e(TAG, "currentCommand :" + currentCommand + " ,isSendSuccess = " + isSendSuccess);
                    }

                }
                else if (is10SecEnd == true)
                {
                    if (currentCommand.contentEquals(Constant.Command.QUERY_ERROR_STA))
                    {
                        //不送
                    }
                    else
                    {
                        byte[] bytesCommand = thisApp.toUTF8ByteArray(currentCommand);
                        if (bytesCommand != null)
                        {
                            boolean isSendSuccess = remoteController.sendCommand(bytesCommand);
                            DebugLog.e(TAG, "currentCommand :" + currentCommand + " ,isSendSuccess = " + isSendSuccess);
                        }
                    }
                }
                else if (currentCommand.contentEquals(Constant.Command.SERIAL_NUMBER))
                {
                    sendDeviceSNCommand();

                }
                else
                {
                    byte[] bytesCommand = thisApp.toUTF8ByteArray(currentCommand);
                    if (bytesCommand != null)
                    {
                        boolean isSendSuccess = remoteController.sendCommand(bytesCommand);
                        DebugLog.e(TAG, "currentCommand :" + currentCommand + " ,isSendSuccess = " + isSendSuccess);
                    }
                }
            }
            else if (acount > 9)

            {
                timeOutCountDownTimer.cancel();
                setCheckDataCountDownTimer(false);
                if (isFinishing() == false)
                {
                    runOnUiThread(new Runnable()
                    {
                        @Override
                        public void run()
                        {
                            showErrorAlert("目前裝置沒有回應，請重新連結NIR+裝置!!", new DialogInterface.OnClickListener()
                            {
                                @Override
                                public void onClick(DialogInterface dialog, int which)
                                {
                                    if (remoteController != null)
                                    {
                                        if (remoteController.getDeviceState() == BluetoothProfile.STATE_CONNECTED)
                                        {
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
            }
        }

        @Override
        public void onFinish()
        {
            // 倒數結束時,會執行這裡
            checkDataCountDownTimer.start();
        }

    }

    public void setCheckDataCountDownTimer(boolean isOpen)
    {
        if (checkDataCountDownTimer == null)
        {
            checkDataCountDownTimer = new CheckData_CountDownTimer(15000, 1000);
            checkDataCountDownTimer.acount = 0;
            if (isOpen == true)
            {
                checkDataCountDownTimer.start();
            }
            else if (isOpen == false)
            {
                checkDataCountDownTimer.cancel();
            }
        }
        else if (checkDataCountDownTimer != null)
        {
            if (isOpen == true)
            {
                checkDataCountDownTimer.acount = 0;
                checkDataCountDownTimer.start();
            }
            else if (isOpen == false)
            {
                checkDataCountDownTimer.cancel();
            }
        }

    }


    private byte[] createOldRequestSerialNoCommad()
    {
        //serialNo 的命令
        //取得UnixTime, 用Int64 的原因是在 iPhone5會掛
        long currentTime = System.currentTimeMillis();

        String currentTimeToHexString = Long.toHexString(currentTime);

        DebugLog.d(TAG, "currentTimeToHexString: " + currentTimeToHexString);

        AirApplication.myID = "" + currentTime;

        DebugLog.d(TAG, "unix time: " + AirApplication.myID);

        //將UnixTime轉成16進制，不足16位時前面補0
        int addZero = 16 - currentTimeToHexString.length();

        StringBuilder padded = new StringBuilder(currentTimeToHexString);

        for (int i = 0; i < addZero; i++)
        {
            padded.insert(0, '0');
        }

        currentCommand = Constant.Command.SERIAL_NUMBER;

        String command = padded.toString() + currentCommand;

        DebugLog.d(TAG, "command: " + command + " length: " + command.length());

        byte[] commandByteData = Utils.hexToByteArray(command);

        return commandByteData;
    }

    private void sendDeviceSNCommand()
    {
        byte[] requestSNData = createOldRequestSerialNoCommad();

        boolean isSendSuccess = remoteController.sendCommand(requestSNData);

        DebugLog.d(TAG, "sendDeviceSNCommand isSendSuccess: " + isSendSuccess);
    }

    View.OnClickListener OnBackIcon = new View.OnClickListener()
    {
        @Override
        public void onClick(View v)
        {
            AirApplication.setOnclikcFeedback(v);
            if (remoteController != null)
            {
                if (remoteController.getDeviceState() == BluetoothProfile.STATE_CONNECTED)
                {
                    remoteController.closeConnection();
                }
            }
            timeOutCountDownTimer.cancel();
            setCheckDataCountDownTimer(false);
            Intent intent = new Intent(currentActivity, MainActivity.class);
            startActivity(intent);
            currentActivity.finish();
            setBackInPrePageAnimation(currentActivity);
        }
    };


    @Override
    public void onBackPressed()
    {
        if (remoteController != null)
        {
            if (remoteController.getDeviceState() == BluetoothProfile.STATE_CONNECTED)
            {
                remoteController.closeConnection();
            }
        }
        timeOutCountDownTimer.cancel();
        setCheckDataCountDownTimer(false);
        Intent intent = new Intent(currentActivity, ScanDevicePage.class);
        startActivity(intent);
        currentActivity.finish();
        setBackInPrePageAnimation(currentActivity);
    }

    @Override
    public void onConnected()
    {

    }

    @Override
    public void onDisconnected()
    {
        timeOutCountDownTimer.cancel();
        setCheckDataCountDownTimer(false);
        runOnUiThread(new Runnable()
        {
            @Override
            public void run()
            {
                showErrorAlert("藍牙被干擾造成斷線必須重新量測", new DialogInterface.OnClickListener()
                {
                    @Override
                    public void onClick(DialogInterface dialog, int which)
                    {
                        if (remoteController != null)
                        {
                            if (remoteController.getDeviceState() == BluetoothProfile.STATE_CONNECTED)
                            {
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
    public void onDiscoverCharacteristics()
    {

    }

    @Override
    public void onCharacteristicChanged(BluetoothGattCharacteristic bluetoothGattCharacteristic)
    {
        byte[] getValue = bluetoothGattCharacteristic.getValue();


        if (getValue != null && getValue.length > 0)
        {
            final String receiveData = thisApp.byteArrayToUTF8String(getValue);

            DebugLog.d(TAG, "receiveData: " + receiveData);

            if (currentCommand.contentEquals(Constant.Command.BEHAVIOR_CHECK))
            {

                if (receiveData.contentEquals(Constant.Command.BEHAVIOR_CHECK))
                {
                    setCheckDataCountDownTimer(false);
                    runOnUiThread(new Runnable()
                    {
                        @Override
                        public void run()
                        {
                            if (progressDialog != null)
                            {
                                progressDialog.dismiss();
                            }
                        }
                    });
                    handler.postDelayed(new Runnable()
                    {
                        @Override
                        public void run()
                        {
                            currentCommand = Constant.Command.MP10;
                            byte[] command = thisApp.toUTF8ByteArray(currentCommand);
                            if (command != null && command.length > 0)
                            {
                                boolean isSendSuccess = remoteController.sendCommand(thisApp.toUTF8ByteArray(currentCommand));

                                Log.e(TAG, "MP10 command send status: " + isSendSuccess);
                                setCheckDataCountDownTimer(true);
                            }
                        }
                    }, 1000);


                }
                else
                {
                    DialogForBehavior("系統偵測到您未確實按壓NIR+內側上下量測按鈕，請重新確認您的食指及無名指完全按壓於NIR+內側上下鈕後，並按下【確定】鍵以進行量測。\n");

                }
            }
            else if (currentCommand.contentEquals(Constant.Command.MP10))
            {
                setCheckDataCountDownTimer(false);
                //版本號取得
                int checkData = receiveData.lastIndexOf("MP10dev");
                if (checkData != -1)
                {
                    FirmwareData firmwareData = new FirmwareData();
                    String Version = receiveData.replaceFirst("MP10dev", "");
                    firmwareData.CheckFirmwareData(Version);
                    thisApp.setFirmwareData(firmwareData);
                    Log.e(TAG, "firmwareData = " + firmwareData.toString());
                    isFirmwareData = true;
                }


            }
            else if (currentCommand.contentEquals(Constant.Command.QUERY_ERROR_STA))
            {
                setCheckDataCountDownTimer(false);
                DeviceErrorMessageCheck check = new DeviceErrorMessageCheck();

                final String ErrorMessageString = getErrorMessageString(getValue);
                runOnUiThread(new Runnable()
                {
                    @Override
                    public void run()
                    {
                        TextView_Error.setText(ErrorMessageString);
                    }
                });
//                先進行心跳判斷
                // 0-45 顯示 手指務必平貼內壁，勿用手指以外得部位量測
                // 46-120 or 241 pass
                // over 120 顯示 請先休息一下再量
//                if (0 <= getValue[9] && getValue[9] <= 45) {
//                    Dialog("手指務必平貼內壁，或勿用手指以外的部位量測");
//                } else if ((120 < getValue[9] && getValue[9] < 241) || getValue[9] > 241) {
//                    showErrorAlert("請先休息一下再量", new DialogInterface.OnClickListener() {
//                        @Override
//                        public void onClick(DialogInterface dialog, int which) {
//                            Intent intent = new Intent(currentActivity, MainActivity.class);
//                            startActivity(intent);
//                            finish();
//                            setBackInPrePageAnimation(currentActivity);
//                        }
//                    });
//                } else {
                getValue[9] = 48;//代表心跳 位於46-120 or 241 所以pass
                final String receiveDataSta = thisApp.byteArrayToUTF8String(getValue);
                String status = check.checkErrorNumber(receiveDataSta);
                if (status.equals("success"))
                {
                    if (isCheckSuccess == false)
                    {
                        currentCommand = Constant.Command.RESET_AND_START;
                        byte[] bytesCommand = thisApp.toUTF8ByteArray(currentCommand);

                        if (bytesCommand != null)
                        {
                            boolean isSendSuccess = remoteController.sendCommand(bytesCommand);
                            DebugLog.e(TAG, "currentCommand :" + currentCommand + " ,isSendSuccess = " + isSendSuccess);
                            isCheckSuccess = true;
                        }
                        setCheckDataCountDownTimer(true);
                    }
                }
                else if (status.equals("e0809"))
                {
                    Dialog("系統偵測到您未確實按壓NIR+內側上下量測按鈕，請重新確認您的食指及無名指完全按壓於NIR+內側上下鈕後，並按下【確定】鍵以進行量測。\n");
                }
                else if (status.equals("e08"))
                {
                    Dialog("系統偵測到您未確實按壓NIR+內側上下量測按鈕，請重新確認您的食指及無名指完全按壓於NIR+內側上下鈕後，並按下【確定】鍵以進行量測。\n");
                }
                else if (status.equals("e09"))
                {
                    Dialog("系統偵測到您未確實按壓NIR+內側上下量測按鈕，請重新確認您的食指及無名指完全按壓於NIR+內側上下鈕後，並按下【確定】鍵以進行量測。\n");
                }
                else if (status.equals("18"))
                {
                    Dialog("NIR+電量不足，請立即充電");
                }
                else if (status.equals("e13"))
                {
                    Dialog("藍牙被干擾造成斷線必須重新量測");
//                    runOnUiThread(new Runnable() {
//                        @Override
//                        public void run() {
//                            showErrorAlert("藍牙中斷，請確認", new DialogInterface.OnClickListener() {
//                                @Override
//                                public void onClick(DialogInterface dialog, int which) {
//                                    if (remoteController != null) {
//                                        if (remoteController.getDeviceState() == BluetoothProfile.STATE_CONNECTED) {
//                                            remoteController.closeConnection();
//                                        }
//                                    }
//                                    Intent intent = new Intent(currentActivity, ScanDevicePage.class);
//                                    startActivity(intent);
//                                    currentActivity.finish();
//                                }
//                            });
//                        }
//                    });
                }
                else if (status.equals("連線愈時"))
                {
                    Dialog("連線愈時，請重新連接");
                }
                else if (status.equals("else"))
                {
                    Dialog(receiveData + "End");
                }
//                }
            }
            else if (currentCommand.contentEquals(Constant.Command.RESET_AND_START))
            {

                if (receiveData.contentEquals(Constant.Command.RESET_AND_START))
                {
                    if (isCheckSuccess == true)
                    {
                        setCheckDataCountDownTimer(false);

                        handler.postDelayed(new Runnable()
                        {
                            @Override
                            public void run()
                            {
                                sendDeviceSNCommand();
                            }
                        }, 300);
                    }
                }
            }
            else if (currentCommand.contentEquals(Constant.Command.SERIAL_NUMBER))
            {
                timeOutCountDownTimer.cancel();
                setCheckDataCountDownTimer(false);

                if (isIntentPage == false)
                {
                    isIntentPage = true;
                    String serialNo = Utils.bytesToHex(getValue);

                    AirApplication.serialNo = serialNo;

                    DebugLog.d(TAG, "serialNo: " + serialNo + " legth: " + serialNo.length());

                    runOnUiThread(new Runnable()
                    {
                        @Override
                        public void run()
                        {
                            if (progressDialog != null)
                            {
                                progressDialog.dismiss();
                            }

                            Intent intent = new Intent(currentActivity, StartScanPage.class);
                            startActivity(intent);
                            currentActivity.finish();
                            setTurnInNextPageAnimation(currentActivity);
                        }
                    });
                }
            }
            else if (currentCommand.contentEquals(Constant.Command.MEASURE_MODE_END))
            {
                //重送BEHAVIOR_CHECK
                currentCommand = Constant.Command.BEHAVIOR_CHECK;
                byte[] command = thisApp.toUTF8ByteArray(currentCommand);
                if (command != null)
                {
                    boolean isSendSuccess = remoteController.sendCommand(thisApp.toUTF8ByteArray(currentCommand));

                    Log.e(TAG, "BEHAVIOR_CHECK isReSend status: " + isSendSuccess);
                }
            }
        }
    }

    @Override
    public void onCharacteristicWrite(BluetoothGattCharacteristic bluetoothGattCharacteristic, int i)
    {

    }

    private String getErrorMessageString(byte[] getValue)
    {
        String afterString = "";
        if (getValue.length > 15)
        {
            byte[] beforeData = new byte[]{getValue[0], getValue[1], getValue[2], getValue[3],
                    getValue[4], getValue[5], getValue[6], getValue[7], getValue[8]};
            String beforeString = thisApp.byteArrayToUTF8String(beforeData);

            byte[] beforeData2 = new byte[]{getValue[10], getValue[11]};
            String beforeString2 = thisApp.byteArrayToUTF8String(beforeData2);

            byte[] beforeData3 = new byte[]{getValue[13], getValue[14], getValue[15]};
            String beforeString3 = thisApp.byteArrayToUTF8String(beforeData3);

            afterString = beforeString + getValue[9] + beforeString2 + getValue[12] + beforeString3;

        }
        return afterString;
    }

    AlertDialog.Builder alertBuilder = null;
    Dialog alertdialog = null;

    public void Dialog(final String Message)
    {
        if (is10SecEnd == true)
        {
            runOnUiThread(new Runnable()
            {
                @Override
                public void run()
                {
                    if (alertBuilder == null)
                    {
                        final String message = Message;
                        alertBuilder = new AlertDialog.Builder(currentActivity);
                        alertBuilder.setTitle("錯誤訊息");
                        alertBuilder.setMessage(message);
                        alertBuilder.setCancelable(false);
                        if (timeOutCountDownTimer != null) timeOutCountDownTimer.cancel();
                        if (!message.equals("連線愈時，請重新連接")
                                && !message.equals("藍牙被干擾造成斷線必須重新量測"))
                        {
                            //返回掃描頁
                            alertBuilder.setNegativeButton("取消", new DialogInterface.OnClickListener()
                            {
                                @Override
                                public void onClick(DialogInterface dialog, int which)
                                {
                                    if (remoteController != null)
                                    {
                                        if (remoteController.getDeviceState() == BluetoothProfile.STATE_CONNECTED)
                                        {
                                            remoteController.closeConnection();
                                        }
                                    }
                                    timeOutCountDownTimer.cancel();
                                    setCheckDataCountDownTimer(false);
                                    Intent intent = new Intent(currentActivity, ScanDevicePage.class);
                                    startActivity(intent);
                                    finish();
                                    setTurnInNextPageAnimation(currentActivity);
                                }
                            });

                        }
                        alertBuilder.setPositiveButton("確定", new DialogInterface.OnClickListener()
                        {
                            @Override
                            public void onClick(DialogInterface dialog, int which)
                            {
                                if (message.equals("連線愈時，請重新連接"))
                                {
                                    //返回掃描頁

                                    if (progressDialog != null)
                                    {
                                        progressDialog.dismiss();
                                    }

                                    if (remoteController != null)
                                    {
                                        if (remoteController.getDeviceState() == BluetoothProfile.STATE_CONNECTED)
                                        {
                                            remoteController.closeConnection();
                                        }
                                    }
                                    timeOutCountDownTimer.cancel();
                                    setCheckDataCountDownTimer(false);
                                    Intent intent = new Intent(currentActivity, ScanDevicePage.class);
                                    startActivity(intent);
                                    finish();
                                    setTurnInNextPageAnimation(currentActivity);
                                }
                                else if (message.equals("藍牙被干擾造成斷線必須重新量測"))
                                {
                                    //返回掃描頁

                                    if (progressDialog != null)
                                    {
                                        progressDialog.dismiss();
                                    }

                                    if (remoteController != null)
                                    {
                                        if (remoteController.getDeviceState() == BluetoothProfile.STATE_CONNECTED)
                                        {
                                            remoteController.closeConnection();
                                        }
                                    }
                                    timeOutCountDownTimer.cancel();
                                    setCheckDataCountDownTimer(false);
                                    Intent intent = new Intent(currentActivity, ScanDevicePage.class);
                                    startActivity(intent);
                                    currentActivity.finish();
                                }
                                else
                                {
                                    //顯示錯誤訊息,重新計時

                                    timeOutCountDownTimer.cancel();
                                    timeOutCountDownTimer.start();

                                    handler.postDelayed(new Runnable()
                                    {
                                        @Override
                                        public void run()
                                        {
                                            currentCommand = Constant.Command.QUERY_ERROR_STA;
                                            byte[] command = thisApp.toUTF8ByteArray(currentCommand);
                                            if (command != null && command.length > 0)
                                            {
                                                boolean isSendSuccess = remoteController.sendCommand(thisApp.toUTF8ByteArray(currentCommand));
                                                Log.e(TAG, "QUERY_ERROR_STA command send status: " + isSendSuccess);
                                            }
                                            setCheckDataCountDownTimer(true);
                                        }
                                    }, 300);

                                }

                                alertBuilder = null;
                            }
                        });
                        if (isFinishing() == false)
                        {
                            alertdialog = alertBuilder.show();
                        }
                    }
                }
            });
        }
    }

    public void DialogForBehavior(final String Message)
    {
        runOnUiThread(new Runnable()
        {
            @Override
            public void run()
            {
                String message = Message;
                AlertDialog.Builder alertBuilder = new AlertDialog.Builder(currentActivity);
                alertBuilder.setTitle("錯誤訊息");
                alertBuilder.setMessage(message);
                alertBuilder.setPositiveButton("確定", new DialogInterface.OnClickListener()
                {
                    @Override
                    public void onClick(DialogInterface dialog, int which)
                    {
                        if (remoteController.getDeviceState() == BluetoothProfile.STATE_CONNECTED)
                        {
                            //發送BEHAVIOR CHECK!!
                            currentCommand = Constant.Command.BEHAVIOR_CHECK;
                            byte[] bytesCommand = thisApp.toUTF8ByteArray(currentCommand);

                            if (bytesCommand != null)
                            {

                                boolean isSendSuccess = remoteController.sendCommand(bytesCommand);

                                if (isSendSuccess)
                                {
                                    progressDialog = new NotifyDialog(currentActivity);
                                    progressDialog.settingProgressDialog();
                                }
                            }

                        }
                    }
                });
                alertBuilder.show();
            }
        });
    }

    @Override
    public void onScanResult(BluetoothDevice bluetoothDevice, int i, byte[] bytes)
    {

    }

    @Override
    public void enable()
    {

    }

    @Override
    public void disable()
    {

    }

    @Override
    public void notSupport()
    {

    }

    @Override
    public void onGattError(int i)
    {

    }

    @Override
    public void onReadRssi(int i, int i1)
    {

    }

    @Override
    public void onMtuChanged(int i)
    {

    }


    private SpannableStringBuilder setTimeOutString(int sec)
    {
        if (sec <= -1)
        {
            sec = 0;
        }
        else if (sec >= 10)
        {
            sec = 10;
        }
        SpannableStringBuilder spannableStringBuilder = new SpannableStringBuilder();

        spannableStringBuilder.append("請於");
        String secString = String.valueOf(sec) + "秒";
        spannableStringBuilder.append(secString);
        spannableStringBuilder.append("內，\n\n請將手掌虎口緊貼儀器邊緣，\n\n四指伸入NIR+內，\n\n讓指腹完全平貼NIR+內側，\n\n同時將食指及無名指指腹，按壓NIR內側上下鈕。");

        ForegroundColorSpan gray1 = new ForegroundColorSpan(getResources().getColor(R.color.color_9B9B9B));
        ForegroundColorSpan blue = new ForegroundColorSpan(getResources().getColor(R.color.color_0078FD));
        ForegroundColorSpan gray2 = new ForegroundColorSpan(getResources().getColor(R.color.color_9B9B9B));
        spannableStringBuilder.setSpan(gray1, 1, 2, Spannable.SPAN_INCLUSIVE_EXCLUSIVE);
        spannableStringBuilder.setSpan(blue, 2, 2 + secString.length(), Spannable.SPAN_INCLUSIVE_EXCLUSIVE);
        spannableStringBuilder.setSpan(gray2, 2 + secString.length(), spannableStringBuilder.length(), Spannable.SPAN_EXCLUSIVE_INCLUSIVE);
        AbsoluteSizeSpan absoluteSizeSpan = new AbsoluteSizeSpan((int) (48 * ViewScaling.getScaleValue()));
        spannableStringBuilder.setSpan(absoluteSizeSpan, 2, 3 + secString.length(), Spannable.SPAN_INCLUSIVE_EXCLUSIVE);


        return spannableStringBuilder;
    }
}
