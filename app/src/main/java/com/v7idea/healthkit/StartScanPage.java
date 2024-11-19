package com.v7idea.healthkit;

import android.app.AlertDialog;
import android.app.Dialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothProfile;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.v7idea.healthkit.Model.Member;
import com.v7idea.template.Activity.BaseActivity;
import com.v7idea.template.AirApplication;
import com.v7idea.template.DataBase.SimpleDatabase;
import com.v7idea.template.Tool.DebugLog;
import com.v7idea.template.Tool.Utils;
import com.v7idea.template.Tool.ViewScaling;
import com.v7idea.template.View.AutoReleaseImageView;
import com.v7idea.template.View.V7TitleView;
import com.v7idea.v7rcliteandroidsdk.V7RCLiteController;

import java.io.UnsupportedEncodingException;

/**
 * 2019/1/24 ＊確認使用的頁面 進行量測-量測頁
 */
public class StartScanPage extends BaseActivity implements V7RCLiteController.BluetoothCallBack, View.OnClickListener
{
    private static String TAG = "StartScanPage";

    private String reseller = "tw-00017";

    ///目前接收資料的長度
    private int reclen = 0;

    ///依據 reseller 的內容修改接收資料的總長度
    private int MaxLen = 12800;

    private float oldStstus = 0;
    private float newStstus = 0;

    private StringBuilder send_data = null;

    private String this_mac_sn = "";
    private String this_mac_name = "";

    private V7RCLiteController remoteController = null;

    private AutoReleaseImageView errorIcon = null;
    private ProgressBar progressBar = null;

    private RelativeLayout RelativeLayout_Mode1;
    private RelativeLayout RelativeLayout_Mode2;

    private V7TitleView ShowProgressInfo = null;
    private V7TitleView Mode1_cancelButton = null;

    private V7TitleView Mode2_cancelButton = null;
    private V7TitleView Mode2_reStartButton = null;

    private V7TitleView Mode1_MiddleTitle = null;
    private V7TitleView Mode2_MiddleTitle = null;

    private TextView Mode2_Item = null;
    private TextView Mode2_Error = null;
    private AirApplication thisApp = null;

    private SimpleDatabase simpleDataBase = null;

    private String currentCommand = "";

    private Intent toWitchPage = null;
    private Dialog dialog = null;
    private Member member = null;
    private Handler handler = null;
    private Handler checkDataHandler = null;
    private CheckDataRunnable checkDataRunnable = null;

    private int check82 = 0;
    private int check84 = 0;

    LinearLayout ProgressBarContainer = null;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start_scan_page);

        thisApp = (AirApplication) getApplication();

        AutoReleaseImageView BackImage = (AutoReleaseImageView) ViewScaling.findViewByIdAndScale(currentActivity, R.id.StartScanPage_AutoReleaseImageView_BackImage);
        BackImage.setOnClickListener(this);

        AutoReleaseImageView BannerImage = (AutoReleaseImageView) ViewScaling.findViewByIdAndScale(currentActivity, R.id.StartScanPage_AutoReleaseImageView_BannerImage);
        ViewScaling.setImageView(BannerImage);

        errorIcon = (AutoReleaseImageView) ViewScaling.findViewByIdAndScale(currentActivity, R.id.StartScanPage_AutoReleaseImageView_errorIcon);


        RelativeLayout RelativeLayout_ModeContainer = (RelativeLayout) ViewScaling.findViewByIdAndScale(currentActivity, R.id.StartScanPage_RelativeLayout_ModeContainer);

        RelativeLayout_Mode1 = (RelativeLayout) ViewScaling.findViewByIdAndScale(currentActivity, R.id.StartScanPage_RelativeLayout_Mode1);
        Mode1_MiddleTitle = (V7TitleView) ViewScaling.findViewByIdAndScale(currentActivity, R.id.StartScanPage_V7TitleView_Mode1_MiddleTitle);

        ProgressBarContainer = (LinearLayout) ViewScaling.findViewByIdAndScale(currentActivity, R.id.StartScanPage_LinearLayout_Mode1_ProgressBarContainer);
        progressBar = (ProgressBar) ViewScaling.findViewByIdAndScale(currentActivity, R.id.StartScanPage_ProgressBar_ProgressBar);
        ShowProgressInfo = (V7TitleView) ViewScaling.findViewByIdAndScale(currentActivity, R.id.StartScanPage_V7TitleView_ShowProgressInfo);
        V7TitleView ShowHint = (V7TitleView) ViewScaling.findViewByIdAndScale(currentActivity, R.id.StartScanPage_V7TitleView_ShowHint);

        Mode1_cancelButton = (V7TitleView) ViewScaling.findViewByIdAndScale(currentActivity, R.id.StartScanPage_V7TitleView_Mode1_cancelButton);
        Mode1_cancelButton.setOnClickListener(this);

        V7TitleView FirmwareVersion = (V7TitleView) ViewScaling.findViewByIdAndScale(currentActivity, R.id.StartScanPage_V7TitleView_FirmwareVersion);
        FirmwareVersion.setText("NIR+韌體版本：" + thisApp.getFirmwareData().NowFirmwareVersion);
        FirmwareVersion.setVisibility(View.INVISIBLE);

        RelativeLayout_Mode2 = (RelativeLayout) ViewScaling.findViewByIdAndScale(currentActivity, R.id.StartScanPage_RelativeLayout_Mode2);
        Mode2_MiddleTitle = (V7TitleView) ViewScaling.findViewByIdAndScale(currentActivity, R.id.StartScanPage_V7TitleView_Mode2_MiddleTitle);

        LinearLayout Mode2_TextViewContainer = (LinearLayout) ViewScaling.findViewByIdAndScale(currentActivity, R.id.StartScanPage_LinearLayout_Mode2_TextViewContainer);
        Mode2_Item = (TextView) ViewScaling.findViewByIdAndScale(currentActivity, R.id.StartScanPage_TextView_Mode2_Item);
        Mode2_Error = (TextView) ViewScaling.findViewByIdAndScale(currentActivity, R.id.StartScanPage_TextView_Mode2_Error);

        Mode2_cancelButton = (V7TitleView) ViewScaling.findViewByIdAndScale(currentActivity, R.id.StartScanPage_V7TitleView_Mode2_cancelButton);
        Mode2_cancelButton.setOnClickListener(this);

        Mode2_reStartButton = (V7TitleView) ViewScaling.findViewByIdAndScale(currentActivity, R.id.StartScanPage_V7TitleView_Mode2_reStartButton);
        Mode2_reStartButton.setOnClickListener(this);


        Mode_Status(1, "", "");

        simpleDataBase = new SimpleDatabase();

        String reseller = simpleDataBase.getStringValueByKey(Constant.USER_RESELLER, "");

        if (reseller == null || reseller.isEmpty())
        {
            reseller = "tw-00017";
        }

        thisApp.reseller = reseller;

        if (reseller == "tw-00018")
        {
            MaxLen = 102400;
        }
        else
        {
            MaxLen = 12800;
        }

        send_data = new StringBuilder();

        remoteController = thisApp.getBleController();
        remoteController.setCallBack(this);
        if (handler == null)
        {
            handler = new Handler();
        }
        if (remoteController.getDeviceState() == BluetoothProfile.STATE_CONNECTED)
        {
            this_mac_sn = remoteController.getConnectDevice().getAddress();
            this_mac_name = remoteController.getConnectDevice().getName();
            if (!this_mac_sn.isEmpty()) thisApp.this_mac_sn = this_mac_sn;
            if (!this_mac_name.isEmpty()) thisApp.this_mac_name = this_mac_name;

            currentCommand = Constant.Command.RECEIVE_SCAN_DATA;

//                        String sendCommand = "0000000000000000" + currentCommand;

            String sendCommand = "0000000000000000001d000f001d000f";

            byte[] command = Utils.hexToByteArray(sendCommand);

            if (command != null && command.length > 0)
            {
                boolean sendReceiveDataCommandStatus = remoteController.sendCommand(command);
                DebugLog.d(TAG, "sendReceiveDataCommandStatus: " + sendReceiveDataCommandStatus);
            }

            Mode_Status(1, "", "");
        }
        member = new Member();
        Log.e(TAG, "isTestUser = " + member.isTestUser);

        if (member.isTestUser.contentEquals("1"))
        {
            Mode2_Error.setVisibility(View.VISIBLE);
//            FirmwareVersion.setVisibility(View.VISIBLE);
            FirmwareVersion.setVisibility(View.INVISIBLE);
        }
        else if (member.isTestUser.contentEquals("0"))
        {
            Mode2_Error.setVisibility(View.GONE);
            FirmwareVersion.setVisibility(View.GONE);
        }
        checkDataHandler = new Handler();
        checkDataRunnable = new CheckDataRunnable();
        checkDataHandler.post(checkDataRunnable);
    }

    @Override
    protected void onDestroy()
    {
        super.onDestroy();
        checkDataRunnable.runnableStatus = Status.End;
    }


    private void Mode_Status(final int i, final String MiddleTitle, final String Item)
    {
        if (i != 1) checkDataRunnable.runnableStatus = Status.End;
        runOnUiThread(new Runnable()
        {
            @Override
            public void run()
            {
                switch (i)
                {
                    case 1:
                    {
                        errorIcon.setVisibility(View.INVISIBLE);
                        RelativeLayout_Mode1.setVisibility(View.VISIBLE);
                        RelativeLayout_Mode2.setVisibility(View.GONE);
                        Mode1_MiddleTitle.setText(R.string.StartScan_Text_Mode1_MiddleText);
                        Mode2_reStartButton.setTag(2);//2 = 沒有要重新量測
                    }
                    break;

                    case 21:
                    {
                        errorIcon.setVisibility(View.VISIBLE);
                        RelativeLayout_Mode1.setVisibility(View.GONE);
                        RelativeLayout_Mode2.setVisibility(View.VISIBLE);
                        Mode2_MiddleTitle.setText(R.string.StartScan_Text_Mode2_MiddleText_Error1);
                        Mode2_Item.setText(R.string.StartScan_Text_Mode2_Item_Error1);
                        Mode2_reStartButton.setTag(1);//1 = 有要重新量測
                    }
                    break;
                    case 22:
                    {
                        errorIcon.setVisibility(View.VISIBLE);
                        RelativeLayout_Mode1.setVisibility(View.GONE);
                        RelativeLayout_Mode2.setVisibility(View.VISIBLE);
                        Mode2_MiddleTitle.setText(R.string.StartScan_Text_Mode2_MiddleText_Error1);
                        Mode2_Item.setText(R.string.StartScan_Text_Mode2_Item_Error2);
                        Mode2_reStartButton.setTag(1);//1 = 有要重新量測
                    }
                    break;
                    case 23:
                    {
                        errorIcon.setVisibility(View.VISIBLE);
                        RelativeLayout_Mode1.setVisibility(View.GONE);
                        RelativeLayout_Mode2.setVisibility(View.VISIBLE);
                        Mode2_MiddleTitle.setText(R.string.StartScan_Text_Mode2_MiddleText_Error1);
                        Mode2_Item.setText(R.string.StartScan_Text_Mode2_Item_Error3);
                        Mode2_reStartButton.setTag(1);//1 = 有要重新量測
                    }
                    break;
                    case 24:
                    {
                        errorIcon.setVisibility(View.VISIBLE);
                        RelativeLayout_Mode1.setVisibility(View.GONE);
                        RelativeLayout_Mode2.setVisibility(View.VISIBLE);
                        Mode2_MiddleTitle.setText(R.string.StartScan_Text_Mode2_MiddleText_Error2);
                        Mode2_Item.setText(R.string.StartScan_Text_Mode2_Item_Error4);
                        Mode2_reStartButton.setTag(1);//1 = 有要重新量測
                        Mode2_Error.setVisibility(View.GONE);
                    }
                    break;
                    case 3:
                    {
                        errorIcon.setVisibility(View.VISIBLE);
                        RelativeLayout_Mode1.setVisibility(View.VISIBLE);
                        RelativeLayout_Mode2.setVisibility(View.GONE);
                        RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                        layoutParams.topMargin = (int) (100 * ViewScaling.getScaleValue());
                        Mode1_MiddleTitle.setLayoutParams(layoutParams);
                        Mode1_MiddleTitle.setText("很抱歉!經系統偵測，您的手機與本設備不相容，無法進行量測，建議您使用它款手機或到App首頁”我要反饋”註明“手機不相容”，我們會儘速回覆，謝謝。");
                        Mode2_reStartButton.setTag(2);//2 = 沒有要重新量測
                        ProgressBarContainer.setVisibility(View.GONE);
                    }
                    break;
                    default:
                    {
                        errorIcon.setVisibility(View.VISIBLE);
                        RelativeLayout_Mode1.setVisibility(View.GONE);
                        RelativeLayout_Mode2.setVisibility(View.VISIBLE);
                        Mode2_MiddleTitle.setText(MiddleTitle);
                        Mode2_Item.setText(Item);
                        Mode2_reStartButton.setTag(1);//1 = 有要重新量測
                        Mode2_Error.setVisibility(View.GONE);
                    }
                    break;
                }
            }
        });
    }

    @Override
    public void onConnected()
    {

    }

    @Override
    public void onDisconnected()
    {
        runOnUiThread(new Runnable()
        {
            @Override
            public void run()
            {
                Mode_Status(0, "藍牙被干擾造成斷線必須重新量測！！", "藍牙被干擾造成斷線必須重新量測！！");
                currentCommand = Constant.Command.MEASURE_MODE_END;
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
        final byte[] getValue = bluetoothGattCharacteristic.getValue();
        final String receiveData = thisApp.byteArrayToUTF8String(getValue);
        DebugLog.e(TAG, "getValue = " + getValue);
        DebugLog.e(TAG, "receiveData = " + receiveData);

        if (getValue != null && getValue.length > 0)
        {
            if (currentCommand.contentEquals(Constant.Command.RESET_AND_START))
            {
//                checkDataRunnable.runnableStatus = Status.Stop;
//
//
//                handler.postDelayed(new Runnable()
//                {
//                    @Override
//                    public void run()
//                    {
//                        sendDeviceSNCommand();
//                        checkDataRunnable.runnableStatus = Status.Run;
//                    }
//                }, 200);
//
//                runOnUiThread(new Runnable()
//                {
//                    @Override
//                    public void run()
//                    {
//                        Mode_Status(1, "", "");
//                    }
//                });
            }
            else if (currentCommand.contentEquals(Constant.Command.SERIAL_NUMBER))
            {
//                checkDataRunnable.runnableStatus = Status.Stop;
//                DebugLog.d(TAG, "getValue length is : " + getValue.length);
//
//                boolean isCheck8284Return = Check8284(getValue);
//                DebugLog.d(TAG, "isCheck8284Return : " + isCheck8284Return);
//                checkDataRunnable.runnableStatus = Status.Run;
//                if (isCheck8284Return == true)
//                {
//                    checkDataRunnable.runnableStatus = Status.Stop;
//                    String serialNo = Utils.bytesToHex(getValue);
//
//                    AirApplication.serialNo = serialNo;
//
//                    DebugLog.d(TAG, "serialNo: " + serialNo + " legth: " + serialNo.length());
//                    //下面是原本RESET_AND_START 要接的事情 現在把get serial number 放在兩者之間執行
//                    currentCommand = Constant.Command.RECEIVE_SCAN_DATA;
//
////                        String sendCommand = "0000000000000000" + currentCommand;
//
//                    String sendCommand = "0000000000000000001d000f001d000f";
//
//                    byte[] command = Utils.hexToByteArray(sendCommand);
//
//                    if (command != null && command.length > 0)
//                    {
//                        boolean sendReceiveDataCommandStatus = remoteController.sendCommand(command);
//                        DebugLog.d(TAG, "sendReceiveDataCommandStatus: " + sendReceiveDataCommandStatus);
//                    }
//                    checkDataRunnable.runnableStatus = Status.Run;
//                }
            }
            else if (currentCommand.contentEquals(Constant.Command.QUERY_ERROR_STA))
            {
                checkDataRunnable.runnableStatus = Status.Stop;

                DeviceErrorMessageCheck check = new DeviceErrorMessageCheck();

                final String ErrorMessageString = getErrorMessageString(getValue);
                runOnUiThread(new Runnable()
                {
                    @Override
                    public void run()
                    {
                        Mode2_Error.setText(ErrorMessageString);
                    }
                });

                getValue[9] = 48;//代表心跳 位於46-120 or 241 所以pass

                final String finalReceiveData = thisApp.byteArrayToUTF8String(getValue);
                String status = check.checkErrorNumber(finalReceiveData);

                if (status.equals("success"))
                {
                    currentCommand = Constant.Command.RESET_AND_START;
                    final byte[] command = thisApp.toUTF8ByteArray(currentCommand);
                    if (command != null)
                    {
                        handler.postDelayed(new Runnable()
                        {
                            @Override
                            public void run()
                            {
                                boolean isSendSuccess = remoteController.sendCommand(command);
                                DebugLog.d(TAG, currentCommand + "isSendSuccess is: " + isSendSuccess);
                            }
                        }, 300);

                        checkDataRunnable.runnableStatus = Status.Run;
                    }
                }
                else if (status.equals("e0809"))
                {
//                    Dialog("量測食指與無名指鬆動，請重量");
                    Mode_Status(23, "", "");
                    currentCommand = Constant.Command.MEASURE_MODE_END;
                }
                else if (status.equals("e08"))
                {
//                    Dialog("量測食指鬆動，請重量");
                    Mode_Status(21, "", "");
                    currentCommand = Constant.Command.MEASURE_MODE_END;
                }
                else if (status.equals("e09"))
                {
//                    Dialog("量測無名指鬆動，請重量");
                    Mode_Status(22, "", "");
                    currentCommand = Constant.Command.MEASURE_MODE_END;
                }
                else if (status.equals("18"))
                {
//                    Dialog("NIR+電量不足，請立即充電");
                    Mode_Status(0, "NIR+電量不足，請立即充電", "NIR+電量不足，請立即充電");
                    currentCommand = Constant.Command.MEASURE_MODE_END;
                }
                else if (status.equals("e13"))
                {
//                    Dialog("藍牙中斷，請確認");
                    Mode_Status(0, "藍牙被干擾造成斷線必須重新量測！！", "藍牙被干擾造成斷線必須重新量測！！");
                    currentCommand = Constant.Command.MEASURE_MODE_END;
                }
                else if (status.equals("else"))
                {
                    Dialog("其他 狀況" + "  " + receiveData);
                    currentCommand = Constant.Command.MEASURE_MODE_END;

                    runOnUiThread(new Runnable()
                    {
                        @Override
                        public void run()
                        {
                            showErrorAlert("其他 狀況" + "  " + receiveData, new DialogInterface.OnClickListener()
                            {
                                @Override
                                public void onClick(DialogInterface dialog, int which)
                                {
                                    if (remoteController.getDeviceState() == BluetoothProfile.STATE_CONNECTED)
                                    {
                                        remoteController.setCallBack(null);
                                        if (remoteController != null)
                                        {
                                            if (remoteController.getDeviceState() == BluetoothProfile.STATE_CONNECTED)
                                            {
                                                remoteController.closeConnection();
                                            }
                                        }
                                    }
                                    toWitchPage = new Intent(currentActivity, ScanDevicePage.class);
                                    startActivity(toWitchPage);
                                    currentActivity.finish();
                                }
                            });
                        }
                    });
                }
            }
            else if (currentCommand.contentEquals(Constant.Command.RECEIVE_SCAN_DATA))
            {
//                final String receiveData = thisApp.byteArrayToUTF8String(getValue);
                checkDataRunnable.count = 0;
                if (reclen <= MaxLen)
                {
                    check84 = 0;
                    check82 = 0;
                    for (int i = 0; i < getValue.length; i++)
                    {

                        final String currentValue = Utils.byteToHex(getValue[i]);

                        DebugLog.d(TAG, "currentValue: " + currentValue);
                        DeviceErrorMessageCheck check = new DeviceErrorMessageCheck();
//                        String status = check.checkErrorNumber(receiveData);
//                        if(status.equals("e08")){
//                            Dialog("量測食指鬆動，請重量");
//                            currentCommand = Constant.Command.MEASURE_MODE_END;

//原本的
                        if (currentValue.contentEquals("82") || currentValue.contentEquals("84"))
                        {

                            if (currentValue.contentEquals("82"))
                            {
                                check82 = check82 + 1;
                            }
                            else if (currentValue.contentEquals("84"))
                            {
                                check84 = check84 + 1;
                            }

                            runOnUiThread(new Runnable()
                            {
                                @Override
                                public void run()
                                {
                                    toWitchPage = null;


                                    if (check82 >= 3)
                                    {
                                        if (thisApp.getFirmwareData().isVersionBig == true)
                                        {
                                            currentCommand = Constant.Command.MP13;
                                            byte[] Command = thisApp.toUTF8ByteArray(currentCommand);

                                            if (Command != null && Command.length > 0)
                                            {
                                                boolean isSendSuccess = remoteController.sendCommand(thisApp.toUTF8ByteArray(currentCommand));

                                                DebugLog.d(TAG, "MP13 command send Mode_Status: " + isSendSuccess);
                                            }

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

                                                        DebugLog.d(TAG, "QUERY_ERROR_STA command send status: " + isSendSuccess);
                                                    }
                                                }
                                            }, 500);
                                        }
                                        else
                                        {
                                            currentCommand = Constant.Command.QUERY_ERROR_STA;
                                            byte[] Command = thisApp.toUTF8ByteArray(currentCommand);

                                            if (Command != null && Command.length > 0)
                                            {
                                                boolean isSendSuccess = remoteController.sendCommand(thisApp.toUTF8ByteArray(currentCommand));

                                                DebugLog.d(TAG, "QUERY_ERROR_STA command send Mode_Status: " + isSendSuccess);
                                            }
                                        }
//                                        currentCommand = Constant.Command.QUERY_ERROR_STA;
//                                        byte[] Command = thisApp.toUTF8ByteArray(currentCommand);
//
//                                        if (Command != null && Command.length > 0) {
//                                            boolean isSendSuccess = remoteController.sendCommand(thisApp.toUTF8ByteArray(currentCommand));
//
//                                            DebugLog.d(TAG, "QUERY_ERROR_STA command send Mode_Status: " + isSendSuccess);
//                                        }
                                        check82 = 0;
                                        check84 = 0;
                                    }
                                    else if (check84 >= 3)
                                    {
                                        if (thisApp.getFirmwareData().isVersionBig == true)
                                        {
                                            currentCommand = Constant.Command.MP13;
                                            byte[] Command = thisApp.toUTF8ByteArray(currentCommand);

                                            if (Command != null && Command.length > 0)
                                            {
                                                boolean isSendSuccess = remoteController.sendCommand(thisApp.toUTF8ByteArray(currentCommand));

                                                DebugLog.d(TAG, "MP13 command send Mode_Status: " + isSendSuccess);
                                            }

                                            handler.postDelayed(new Runnable()
                                            {
                                                @Override
                                                public void run()
                                                {
                                                    currentCommand = Constant.Command.MEASURE_MODE_END;
                                                    byte[] command = thisApp.toUTF8ByteArray(currentCommand);
                                                    if (command != null && command.length > 0)
                                                    {
                                                        boolean isSendSuccess = remoteController.sendCommand(thisApp.toUTF8ByteArray(currentCommand));

                                                        DebugLog.d(TAG, "MEASURE_MODE_END command send status: " + isSendSuccess);
                                                    }
                                                }
                                            }, 500);
                                        }
                                        else
                                        {
                                            currentCommand = Constant.Command.MEASURE_MODE_END;
                                            byte[] Command = thisApp.toUTF8ByteArray(currentCommand);

                                            if (Command != null && Command.length > 0)
                                            {
                                                boolean isSendSuccess = remoteController.sendCommand(thisApp.toUTF8ByteArray(currentCommand));

                                                DebugLog.d(TAG, "MEASURE_MODE_END command send Mode_Status: " + isSendSuccess);
                                            }
                                        }
//                                        currentCommand = Constant.Command.MEASURE_MODE_END;
//                                        byte[] Command = thisApp.toUTF8ByteArray(currentCommand);
//
//                                        if (Command != null && Command.length > 0) {
//                                            boolean isSendSuccess = remoteController.sendCommand(thisApp.toUTF8ByteArray(currentCommand));
//
//                                            DebugLog.d(TAG, "MEASURE_MODE_END command send Mode_Status: " + isSendSuccess);
//                                        }
//                                        Dialog("量測時環境光線太亮，請調整重量");
                                        check84 = 0;
                                        check82 = 0;
                                        Mode_Status(24, "", "");
                                        final String ErrorMessageString = getErrorMessageString(getValue);
                                        runOnUiThread(new Runnable()
                                        {
                                            @Override
                                            public void run()
                                            {
                                                Mode2_Error.setText(ErrorMessageString);
                                            }
                                        });
                                    }


                                }
                            });


                        }
                        else
                        {
                            reclen += 1;

                            if (reclen > MaxLen)
                            {
                                break;
                            }

                            DebugLog.d(TAG, "MaxLen: " + MaxLen);
                            DebugLog.d(TAG, "reclen: " + reclen);

                            send_data.append(currentValue);
                            oldStstus = (Float.valueOf(reclen) / Float.valueOf(MaxLen));

                            DebugLog.d(TAG, "oldStstus: " + oldStstus);

                            if (oldStstus > newStstus)
                            {
                                newStstus = oldStstus;

                                runOnUiThread(new Runnable()
                                {
                                    @Override
                                    public void run()
                                    {
                                        progressBar.setProgress((int) (newStstus * 100f));
                                        ShowProgressInfo.setText((int) (newStstus * 100f) + "%");
                                    }
                                });

//                            progressBar.progress = Float(newStstus);
//                            app.showError("量測中..." + newStstus + "%");
                            }
//                        }else{
//                            currentCommand = Constant.Command.MEASURE_MODE_END;
//                            byte[] command = thisApp.toUTF8ByteArray(currentCommand);
//
//                            if(command != null){
//                                boolean isSendMeasureModeEndCommandSuccess = remoteController.sendCommand(command);
//                            }
//                            DebugLog.d(TAG, "else 其他狀況發生");
//                            Dialog2(currentValue+" *** "+receiveData);
////                            currentCommand = Constant.Command.MEASURE_MODE_END;
//                            break;
//                        }
                        }

                        DebugLog.d(TAG, "newStstus: " + newStstus);
                        DebugLog.d(TAG, "reclen: " + reclen);

                        if (newStstus > 1)
                        {
                            newStstus = 1;
                        }

                        runOnUiThread(new Runnable()
                        {
                            @Override
                            public void run()
                            {
                                progressBar.setProgress((int) (newStstus * 100));
                                ShowProgressInfo.setText(((int) (newStstus * 100)) + "%");
                            }
                        });
                    }
                }
                else
                {
//                serial.close(app.onDisconnect, app.onError);
                    DebugLog.d(TAG, "send_data legth: " + send_data.length());

                    toWitchPage = new Intent(currentActivity, EndScanPage.class);
                    currentCommand = Constant.Command.MEASURE_MODE_END;

                    byte[] command = thisApp.toUTF8ByteArray(currentCommand);

                    if (command != null)
                    {
                        boolean isSendMeasureModeEndCommandSuccess = remoteController.sendCommand(command);
                    }
                }
            }
            else if (currentCommand.contentEquals(Constant.Command.MEASURE_MODE_END))
            {
//                String receiveData = thisApp.byteArrayToUTF8String(getValue);

                if (receiveData.contentEquals(Constant.Command.MEASURE_MODE_END))
                {
                    if (checkDataRunnable.isMEASURE_MODE_END == false)
                    {
                        runOnUiThread(new Runnable()
                        {
                            @Override
                            public void run()
                            {
                                thisApp.send_data = send_data;

                                if ((int) (Mode2_reStartButton.getTag()) == 1 && toWitchPage != null)
                                {
                                    if (toWitchPage.getComponent().getClassName().contentEquals(MainActivity.class.getName()))
                                    {
                                        if (remoteController != null)
                                        {
                                            remoteController.setCallBack(null);
                                            if (remoteController.getDeviceState() == BluetoothProfile.STATE_CONNECTED)
                                            {
                                                remoteController.closeConnection();
                                            }
                                        }
                                    }

                                    startActivity(toWitchPage);
                                    finish();
                                    setBackInPrePageAnimation(currentActivity);
                                }
                                else
                                {
                                    if (toWitchPage != null)
                                    {
                                        if (toWitchPage.getComponent().getClassName().contentEquals(MainActivity.class.getName()))
                                        {
                                            if (remoteController != null)
                                            {
                                                remoteController.setCallBack(null);
                                                if (remoteController.getDeviceState() == BluetoothProfile.STATE_CONNECTED)
                                                {
                                                    remoteController.closeConnection();
                                                }
                                            }
                                        }
                                        else if (toWitchPage.getComponent().getClassName().contentEquals(EndScanPage.class.getName()))
                                        {
                                            if (remoteController != null)
                                            {
                                                remoteController.setCallBack(null);
                                                if (remoteController.getDeviceState() == BluetoothProfile.STATE_CONNECTED)
                                                {
                                                    remoteController.closeConnection();
                                                }
                                            }
                                        }

                                        startActivity(toWitchPage);
                                        finish();
                                        setBackInPrePageAnimation(currentActivity);
                                    }
                                }
                            }
                        });
                    }
                    else
                    {
                        checkDataRunnable.runnableStatus = Status.Stop;
                        Log.e(TAG, "onCharacteristicChanged isMEASURE_MODE_END = false ,RESET_AND_START = Stop");
                        handler.postDelayed(new Runnable()
                        {
                            @Override
                            public void run()
                            {
                                currentCommand = Constant.Command.RESET_AND_START;
                                byte[] Command = thisApp.toUTF8ByteArray(currentCommand);
                                if (Command != null && Command.length > 0)
                                {
                                    boolean isSendSuccess = remoteController.sendCommand(thisApp.toUTF8ByteArray(currentCommand));
                                    DebugLog.d(TAG, "CheckDataRunnable RESET_AND_START isSendSuccess : " + isSendSuccess);
                                }
                                checkDataRunnable.isMEASURE_MODE_END = false;
                                checkDataRunnable.runnableStatus = Status.Run;
                            }
                        }, 1500);
                    }
                }
            }
        }
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

    @Override
    public void onCharacteristicWrite(BluetoothGattCharacteristic bluetoothGattCharacteristic,
                                      int i)
    {

    }

    public void Dialog(final String Message)
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
                alertBuilder.setPositiveButton("重新量測", new DialogInterface.OnClickListener()
                {
                    @Override
                    public void onClick(DialogInterface dialog, int which)
                    {
                        toWitchPage = new Intent(currentActivity, ScanDevicePage.class);

                        currentCommand = Constant.Command.MEASURE_MODE_END;

                        byte[] command = thisApp.toUTF8ByteArray(currentCommand);

                        if (command != null)
                        {
                            boolean isSendSuccess = remoteController.sendCommand(command);

                            DebugLog.d(TAG, "measure mode end command isSendSuccess: " + isSendSuccess);
                        }
                    }
                });
                dialog = alertBuilder.show();

            }
        });
    }


    @Override
    public void onBackPressed()
    {
        this.onClick(Mode1_cancelButton);
    }

    @Override
    public void onClick(View v)
    {
        AirApplication.setOnclikcFeedback(v);
        switch (v.getId())
        {
            case R.id.StartScanPage_V7TitleView_Mode1_cancelButton:
            {
                if (remoteController.getDeviceState() == BluetoothProfile.STATE_CONNECTED)
                {
                    toWitchPage = new Intent(currentActivity, MainActivity.class);

                    currentCommand = Constant.Command.MEASURE_MODE_END;

                    byte[] command = thisApp.toUTF8ByteArray(currentCommand);

                    if (command != null)
                    {
                        boolean isSendSuccess = remoteController.sendCommand(command);

                        DebugLog.d(TAG, "measure mode end command isSendSuccess: " + isSendSuccess);
                    }
                }
                else if (remoteController.getDeviceState() == BluetoothProfile.STATE_DISCONNECTED)
                {
                    toWitchPage = new Intent(currentActivity, MainActivity.class);

                    startActivity(toWitchPage);
                    finish();
                    setBackInPrePageAnimation(currentActivity);
                }
            }
            break;

            case R.id.StartScanPage_V7TitleView_Mode2_reStartButton:
            {
                if (thisApp.send_data != null)
                    thisApp.send_data.setLength(0);
                send_data.setLength(0);
                if (remoteController.getDeviceState() == BluetoothProfile.STATE_CONNECTED)
                {

                    toWitchPage = new Intent(currentActivity, CheckConnectedPage.class);

                    currentCommand = Constant.Command.MEASURE_MODE_END;

                    byte[] command = thisApp.toUTF8ByteArray(currentCommand);

                    if (command != null)
                    {
                        boolean isSendSuccess = remoteController.sendCommand(command);

                        DebugLog.d(TAG, "measure mode end command isSendSuccess: " + isSendSuccess);
                    }
                }
                else
                {
                    toWitchPage = new Intent(currentActivity, ScanDevicePage.class);

                    startActivity(toWitchPage);
                    finish();
                    setTurnInNextPageAnimation(currentActivity);
                }
            }
            break;

            case R.id.StartScanPage_V7TitleView_Mode2_cancelButton:
            {
                if (remoteController.getDeviceState() == BluetoothProfile.STATE_CONNECTED)
                {
                    toWitchPage = new Intent(currentActivity, MainActivity.class);

                    currentCommand = Constant.Command.MEASURE_MODE_END;

                    byte[] command = thisApp.toUTF8ByteArray(currentCommand);

                    if (command != null)
                    {
                        boolean isSendSuccess = remoteController.sendCommand(command);

                        DebugLog.d(TAG, "measure mode end command isSendSuccess: " + isSendSuccess);
                    }
                }
                else if (remoteController.getDeviceState() == BluetoothProfile.STATE_DISCONNECTED)
                {
                    toWitchPage = new Intent(currentActivity, MainActivity.class);

                    startActivity(toWitchPage);
                    finish();
                    setBackInPrePageAnimation(currentActivity);
                }
            }
            break;

            case R.id.StartScanPage_AutoReleaseImageView_BackImage:
            {
                if (remoteController.getDeviceState() == BluetoothProfile.STATE_CONNECTED)
                {
                    toWitchPage = new Intent(currentActivity, MainActivity.class);

                    currentCommand = Constant.Command.MEASURE_MODE_END;

                    byte[] command = thisApp.toUTF8ByteArray(currentCommand);

                    if (command != null)
                    {
                        boolean isSendSuccess = remoteController.sendCommand(command);

                        DebugLog.d(TAG, "measure mode end command isSendSuccess: " + isSendSuccess);
                    }
                }
                else if (remoteController.getDeviceState() == BluetoothProfile.STATE_DISCONNECTED)
                {
                    toWitchPage = new Intent(currentActivity, MainActivity.class);

                    startActivity(toWitchPage);
                    finish();
                    setBackInPrePageAnimation(currentActivity);
                }
            }
            break;
        }
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
    public void onGattError(final int i)
    {
        runOnUiThread(new Runnable()
        {
            @Override
            public void run()
            {
                Mode_Status(0, "藍牙被干擾造成斷線必須重新量測！！", "藍牙被干擾造成斷線必須重新量測！！");
                currentCommand = Constant.Command.MEASURE_MODE_END;
            }
        });
    }

    @Override
    public void onReadRssi(int i, int i1)
    {

    }

    @Override
    public void onMtuChanged(int i)
    {

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

    /**
     * 創造日期 :2019/1/22
     * 方法名稱：Check8284
     * 方法用途：檢查8284,做相對應的動作
     * ＠return null
     *
     * @param getValue
     **/
    boolean isReturn = false;

    private boolean Check8284(final byte[] getValue)
    {
        isReturn = true;
        check82 = 0;
        check84 = 0;
        for (int i = 0; i < getValue.length; i++)
        {
            final String currentValue = Utils.byteToHex(getValue[i]);
//            DebugLog.d(TAG, "Check8284 currentValue: " + currentValue);

            if (currentValue.contentEquals("82") || currentValue.contentEquals("84"))
            {
                if (currentValue.contentEquals("82"))
                {
                    check82 = check82 + 1;
                }
                else if (currentValue.contentEquals("84"))
                {
                    check84 = check84 + 1;
                }

                runOnUiThread(new Runnable()
                {
                    @Override
                    public void run()
                    {
                        toWitchPage = null;


                        if (check82 >= 3)
                        {
                            if (thisApp.getFirmwareData().isVersionBig == true)
                            {
                                currentCommand = Constant.Command.MP13;
                                byte[] Command = thisApp.toUTF8ByteArray(currentCommand);

                                if (Command != null && Command.length > 0)
                                {
                                    boolean isSendSuccess = remoteController.sendCommand(thisApp.toUTF8ByteArray(currentCommand));

                                    DebugLog.d(TAG, "MP13 command send Mode_Status: " + isSendSuccess);
                                }

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

                                            DebugLog.d(TAG, "QUERY_ERROR_STA command send status: " + isSendSuccess);
                                        }
                                    }
                                }, 500);
                            }
                            else
                            {
                                currentCommand = Constant.Command.QUERY_ERROR_STA;
                                byte[] Command = thisApp.toUTF8ByteArray(currentCommand);

                                if (Command != null && Command.length > 0)
                                {
                                    boolean isSendSuccess = remoteController.sendCommand(thisApp.toUTF8ByteArray(currentCommand));

                                    DebugLog.d(TAG, "QUERY_ERROR_STA command send Mode_Status: " + isSendSuccess);
                                }
                            }
                            check82 = 0;
                            check84 = 0;
                            isReturn = false;
                        }
                        else if (check84 >= 3)
                        {
                            if (thisApp.getFirmwareData().isVersionBig == true)
                            {
                                currentCommand = Constant.Command.MP13;
                                byte[] Command = thisApp.toUTF8ByteArray(currentCommand);

                                if (Command != null && Command.length > 0)
                                {
                                    boolean isSendSuccess = remoteController.sendCommand(thisApp.toUTF8ByteArray(currentCommand));

                                    DebugLog.d(TAG, "MP13 command send Mode_Status: " + isSendSuccess);
                                }

                                handler.postDelayed(new Runnable()
                                {
                                    @Override
                                    public void run()
                                    {
                                        currentCommand = Constant.Command.MEASURE_MODE_END;
                                        byte[] command = thisApp.toUTF8ByteArray(currentCommand);
                                        if (command != null && command.length > 0)
                                        {
                                            boolean isSendSuccess = remoteController.sendCommand(thisApp.toUTF8ByteArray(currentCommand));

                                            DebugLog.d(TAG, "MEASURE_MODE_END command send status: " + isSendSuccess);
                                        }
                                    }
                                }, 500);
                            }
                            else
                            {
                                currentCommand = Constant.Command.MEASURE_MODE_END;
                                byte[] Command = thisApp.toUTF8ByteArray(currentCommand);

                                if (Command != null && Command.length > 0)
                                {
                                    boolean isSendSuccess = remoteController.sendCommand(thisApp.toUTF8ByteArray(currentCommand));

                                    DebugLog.d(TAG, "MEASURE_MODE_END command send Mode_Status: " + isSendSuccess);
                                }
                            }
//                                        Dialog("量測時環境光線太亮，請調整重量");
                            check84 = 0;
                            check82 = 0;
                            Mode_Status(24, "", "");
                            final String ErrorMessageString = getErrorMessageString(getValue);
                            runOnUiThread(new Runnable()
                            {
                                @Override
                                public void run()
                                {
                                    Mode2_Error.setText(ErrorMessageString);
                                }
                            });
                            isReturn = false;
                        }


                    }
                });


            }
        }
        return isReturn;

    }


    public enum Status
    {
        Run, Stop, End
    }

    private class CheckDataRunnable implements Runnable
    {
        public Status runnableStatus = Status.Run;

        int count = 0;
        public boolean isMEASURE_MODE_END = false;

        @Override
        public void run()
        {
            if (runnableStatus == Status.Run)
            {
                count++;
                Log.e(TAG, "CheckDataRunnable count = " + count);

                if (count % 3 == 0)
                {
//                    if (currentCommand.contentEquals(Constant.Command.SERIAL_NUMBER))
//                    {
//                        checkDataRunnable.runnableStatus = Status.Stop;
//                        Log.e(TAG, "CheckDataRunnable MEASURE_MODE_END = Stop");
//                        currentCommand = Constant.Command.MEASURE_MODE_END;
//                        byte[] Command = thisApp.toUTF8ByteArray(currentCommand);
//                        if (Command != null && Command.length > 0)
//                        {
//                            boolean isSendSuccess = remoteController.sendCommand(thisApp.toUTF8ByteArray(currentCommand));
//                            if (isSendSuccess == true) isMEASURE_MODE_END = true;
//                            DebugLog.d(TAG, "CheckDataRunnable MEASURE_MODE_END isSendSuccess : " + isSendSuccess);
//                        }
//                        checkDataRunnable.runnableStatus = Status.Run;
//                        Log.e(TAG, "CheckDataRunnable MEASURE_MODE_END = Run");
//
//
//                    }
//                    else
                    if (currentCommand.contentEquals(Constant.Command.RECEIVE_SCAN_DATA))
                    {

                        String sendCommand = "0000000000000000001d000f001d000f";

                        byte[] command = Utils.hexToByteArray(sendCommand);

                        if (command != null && command.length > 0)
                        {
                            boolean sendReceiveDataCommandStatus = remoteController.sendCommand(command);
                            DebugLog.d(TAG, "sendReceiveDataCommandStatus: " + sendReceiveDataCommandStatus);
                        }
                    }
                    else
                    {
                        byte[] bytesCommand = thisApp.toUTF8ByteArray(currentCommand);
                        if (bytesCommand != null)
                        {
                            boolean isSendSuccess = remoteController.sendCommand(bytesCommand);
                            Log.e(TAG, "CheckDataRunnable currentCommand= " + currentCommand);
                            Log.e(TAG, "CheckDataRunnable isSendSuccess= " + isSendSuccess);
                        }
                    }
                }
                else if (count >= 11)
                {
                    if (isFinishing() == false)
                    {
                        if ((int) (newStstus * 100f) > 1)
                        {
                            Mode_Status(0, "藍牙被干擾造成斷線必須重新量測！！", "藍牙被干擾造成斷線必須重新量測！！");
                        }
                        else
                        {
                            Mode_Status(3, "", "");
                        }
                        currentCommand = Constant.Command.MEASURE_MODE_END;
                        checkDataHandler.removeCallbacks(this);
                        return;
                    }
                }

                checkDataHandler.postDelayed(this, 1000);
            }
            else if (runnableStatus == Status.Stop)
            {
                //停止不做事情
                checkDataHandler.postDelayed(this, 1000);
            }
            else if (runnableStatus == Status.End)
            {
                checkDataHandler.removeCallbacks(this);
            }
        }
    }
}
