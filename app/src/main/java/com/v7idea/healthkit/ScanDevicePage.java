package com.v7idea.healthkit;

import android.app.AlertDialog;
import android.app.Dialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothProfile;
import android.content.Intent;
import android.graphics.Paint;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.style.LeadingMarginSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.v7idea.healthkit.Domain.DeviceInfo;
import com.v7idea.healthkit.View.ImageTextView;
import com.v7idea.template.Activity.BaseActivity;
import com.v7idea.template.AirApplication;
import com.v7idea.template.DataBase.SimpleDatabase;
import com.v7idea.template.Tool.DebugLog;
import com.v7idea.template.Tool.NotifyDialog;
import com.v7idea.template.Tool.ViewScaling;
import com.v7idea.template.View.AutoReleaseImageView;
import com.v7idea.v7rcliteandroidsdk.V7RCLiteController;

import java.util.LinkedHashMap;

/**
 * 2019/1/24 ＊確認使用的頁面 進行量測-藍牙掃描
 */
public class ScanDevicePage extends BaseActivity implements V7RCLiteController.BluetoothCallBack, View.OnClickListener {

    private final static String TAG = "ScanDevicePage";

    private static final int REQUEST_ENABLE_BT = 1;
    private String lastDeviceMacAddress = "";
    private V7RCLiteController bleController = null;

    private NotifyDialog progressDialog = null;
    private LinkToDeviceByBluetoothAdapter linkToDeviceByBluetoothAdapter = null;

    private SimpleDatabase simpleDatabase = null;
    private Handler handler = null;
    private ScanRunnable scanRunnable = null;
    private ReConnectDeviceRunnable reConnectDeviceRunnable = null;

    private Dialog bleAlertDialog = null;

    ListView ShowDeviceList;
    TextView Title;
    LinearLayout LinearLayoutMode1, LinearLayoutMode2, LinearLayoutMode3;
    ImageTextView mImageTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scan_device_page);

        AutoReleaseImageView Banner = (AutoReleaseImageView) ViewScaling.findViewByIdAndScale(currentActivity, R.id.ScanDevicePage_AutoReleaseImageView_Banner);
        ViewScaling.setImageView(Banner);

        AutoReleaseImageView BackIcon = (AutoReleaseImageView) ViewScaling.findViewByIdAndScale(currentActivity, R.id.ScanDevicePage_AutoReleaseImageView_BackImage);
        BackIcon.setOnClickListener(this);

        Title = (TextView) ViewScaling.findViewByIdAndScale(currentActivity, R.id.ScanDevicePage_TextView_Title);

        ShowDeviceList = (ListView) ViewScaling.findViewByIdAndScale(currentActivity, R.id.ScanDevicePage_ListView_ShowDeviceList);
        ShowDeviceList.setDividerHeight((int) (ShowDeviceList.getDividerHeight() * ViewScaling.getScaleValue()));

        RelativeLayout RelativeLayout_ModeContainer = (RelativeLayout) ViewScaling.findViewByIdAndScale(currentActivity, R.id.ScanDevicePage_RelativeLayout_ModeContainer);
        LinearLayoutMode1 = (LinearLayout) ViewScaling.findViewByIdAndScale(currentActivity, R.id.ScanDevicePage_LinearLayout_Mode1);
        TextView Mode1MiddleTitle = (TextView) ViewScaling.findViewByIdAndScale(currentActivity, R.id.ScanDevicePage_Mode1_MiddleTitle);
        ImageView Mode1Icon1 = (ImageView) ViewScaling.findViewByIdAndScale(currentActivity, R.id.ScanDevicePage_Mode1_Icon1);
        TextView Mode1Item1 = (TextView) ViewScaling.findViewByIdAndScale(currentActivity, R.id.ScanDevicePage_Mode1_Item1);
        ImageView Mode1Icon2 = (ImageView) ViewScaling.findViewByIdAndScale(currentActivity, R.id.ScanDevicePage_Mode1_Icon2);
        TextView Mode1Item2 = (TextView) ViewScaling.findViewByIdAndScale(currentActivity, R.id.ScanDevicePage_Mode1_Item2);
        ImageView Mode1Icon3 = (ImageView) ViewScaling.findViewByIdAndScale(currentActivity, R.id.ScanDevicePage_Mode1_Icon3);
        TextView Mode1Item3 = (TextView) ViewScaling.findViewByIdAndScale(currentActivity, R.id.ScanDevicePage_Mode1_Item3);
        ImageView Mode1Icon4 = (ImageView) ViewScaling.findViewByIdAndScale(currentActivity, R.id.ScanDevicePage_Mode1_Icon4);
        TextView Mode1Item4 = (TextView) ViewScaling.findViewByIdAndScale(currentActivity, R.id.ScanDevicePage_Mode1_Item4);
        ImageView Mode1Icon5 = (ImageView) ViewScaling.findViewByIdAndScale(currentActivity, R.id.ScanDevicePage_Mode1_Icon5);
        TextView Mode1Item5 = (TextView) ViewScaling.findViewByIdAndScale(currentActivity, R.id.ScanDevicePage_Mode1_Item5);

        LinearLayoutMode2 = (LinearLayout) ViewScaling.findViewByIdAndScale(currentActivity, R.id.ScanDevicePage_LinearLayout_Mode2);
        TextView Mode2MiddleTitle = (TextView) ViewScaling.findViewByIdAndScale(currentActivity, R.id.ScanDevicePage_Mode2_MiddleTitle);
        ImageView Mode2Icon1 = (ImageView) ViewScaling.findViewByIdAndScale(currentActivity, R.id.ScanDevicePage_Mode2_Icon1);
        TextView Mode2Item1 = (TextView) ViewScaling.findViewByIdAndScale(currentActivity, R.id.ScanDevicePage_Mode2_Item1);
        ImageView Mode2Icon2 = (ImageView) ViewScaling.findViewByIdAndScale(currentActivity, R.id.ScanDevicePage_Mode2_Icon2);
        TextView Mode2Item2 = (TextView) ViewScaling.findViewByIdAndScale(currentActivity, R.id.ScanDevicePage_Mode2_Item2);

        LinearLayoutMode3 = (LinearLayout) ViewScaling.findViewByIdAndScale(currentActivity, R.id.ScanDevicePage_LinearLayout_Mode3);
        TextView Modde3MiddleTitle = (TextView) ViewScaling.findViewByIdAndScale(currentActivity, R.id.ScanDevicePage_Mode3_MiddleTitle);
        ImageView Mode3Icon1 = (ImageView) ViewScaling.findViewByIdAndScale(currentActivity, R.id.ScanDevicePage_Mode3_Icon1);
        TextView Mode3Item1 = (TextView) ViewScaling.findViewByIdAndScale(currentActivity, R.id.ScanDevicePage_Mode3_Item1);
        ImageView Mode3Icon2 = (ImageView) ViewScaling.findViewByIdAndScale(currentActivity, R.id.ScanDevicePage_Mode3_Icon2);
        TextView Mode3Item2 = (TextView) ViewScaling.findViewByIdAndScale(currentActivity, R.id.ScanDevicePage_Mode3_Item2);
        ImageView Mode3Icon3 = (ImageView) ViewScaling.findViewByIdAndScale(currentActivity, R.id.ScanDevicePage_Mode3_Icon3);
        TextView Mode3Item3 = (TextView) ViewScaling.findViewByIdAndScale(currentActivity, R.id.ScanDevicePage_Mode3_Item3);
        ImageView Mode3Icon4 = (ImageView) ViewScaling.findViewByIdAndScale(currentActivity, R.id.ScanDevicePage_Mode3_Icon4);
        TextView Mode3Item4 = (TextView) ViewScaling.findViewByIdAndScale(currentActivity, R.id.ScanDevicePage_Mode3_Item4);
        TextView Mode3BottomItem = (TextView) ViewScaling.findViewByIdAndScale(currentActivity, R.id.ScanDevicePage_Mode3_BottomItem);
        TextView TextView_Empty = (TextView) ViewScaling.findViewByIdAndScale(currentActivity, R.id.ScanDevicePage_TextView_Empty);

        SpannableStringBuilder demoSpannableString = getDemoSpannable("1. ", "1. 若依照上述程序，仍無法連結裝置及進行量測，請退出【健康快篩】APP，並將手機重新關、開機。", Mode3BottomItem.getPaint());
        demoSpannableString.append("\n");
        demoSpannableString.append(getDemoSpannable("2. ", "2. 重新啟動【健康快篩】APP", Mode3BottomItem.getPaint()));
        demoSpannableString.append("\n");
        demoSpannableString.append(getDemoSpannable("3. ", "3. 再依照步驟1-步驟5之程序操作。", Mode3BottomItem.getPaint()));

        Mode3BottomItem.setText(demoSpannableString, TextView.BufferType.SPANNABLE);

        mImageTextView = (ImageTextView) ViewScaling.findViewByIdAndScale(currentActivity, R.id.ScanDevicePage_ImageTextView);

        mImageTextView.setOnClickListener(onClickListener);


        ShowDeviceList.setOnItemClickListener(onItemClickListener);

        if (handler == null) {
            handler = new Handler();
        }
        Mode(2);
        progressDialog = new NotifyDialog(currentActivity);
    }

    private SpannableStringBuilder getDemoSpannable(final String headIndentString, String shwoString, final Paint paint) {
        final int columnIndentation = (int) paint.measureText(headIndentString);


        SpannableStringBuilder demoSpannableString = new SpannableStringBuilder(shwoString);

        demoSpannableString.setSpan(new LeadingMarginSpan.Standard(0, columnIndentation), 0, demoSpannableString.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        return demoSpannableString;
    }

    public void Mode(int Mode) {
        switch (Mode) {
//            case 1: {
//                Title.setText(R.string.ScanDevicePage_Text_Mode1_Title);
//                LinearLayoutMode1.setVisibility(View.VISIBLE);
//                LinearLayoutMode2.setVisibility(View.GONE);
//                LinearLayoutMode3.setVisibility(View.GONE);
//                ShowDeviceList.setVisibility(View.GONE);
//                mImageTextView.setIconVisibility(ImageTextView.GONE);
//                mImageTextView.setText(getResources().getString(R.string.ScanDevicePage_Text_Mode1_BottomBotton));
//                mImageTextView.setTag(1);
//            }
//            break;
            case 2: {
                Title.setText(R.string.ScanDevicePage_Text_Mode2_Title);
                LinearLayoutMode1.setVisibility(View.GONE);
                LinearLayoutMode2.setVisibility(View.VISIBLE);
                LinearLayoutMode3.setVisibility(View.GONE);
                ShowDeviceList.setVisibility(View.VISIBLE);
                mImageTextView.setIconVisibility(ImageTextView.VISIBLE);
                mImageTextView.setText(getResources().getString(R.string.ScanDevicePage_Text_Mode2_BottomBotton));
                mImageTextView.setTag(2);
            }
            break;
            case 3: {
                Title.setText(R.string.ScanDevicePage_Text_Mode3_Title);
                LinearLayoutMode1.setVisibility(View.GONE);
                LinearLayoutMode2.setVisibility(View.GONE);
                LinearLayoutMode3.setVisibility(View.VISIBLE);
                ShowDeviceList.setVisibility(View.GONE);
                mImageTextView.setIconVisibility(ImageTextView.GONE);
                mImageTextView.setText(getResources().getString(R.string.ScanDevicePage_Text_Mode3_BottomBotton));
                mImageTextView.setTag(3);
            }
            break;
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        // TODO: 2018/12/13 關閉飛航模式檢查
//        IntentFilter intentFilter = new IntentFilter("android.intent.action.AIRPLANE_MODE");
//        registerReceiver(receiver, intentFilter);


        if (simpleDatabase == null) {
            simpleDatabase = new SimpleDatabase();
        }

        lastDeviceMacAddress = simpleDatabase.getStringValueByKey(Constant.LAST_CONNECTED_DEVICE_MAC, "");

        if (scanRunnable == null) {
            scanRunnable = new ScanRunnable();
        }
        if (reConnectDeviceRunnable == null) {
            reConnectDeviceRunnable = new ReConnectDeviceRunnable();
        }
        {
            // TODO: 2018/12/13 關閉飛航模式檢查
//        checkAirplaneModeAndBle();
            checkBle();
        }
    }

    private boolean checkAirplaneModeAndBle() {
        if (isAirplaneModeOn()) {
            return checkBle();
        } else {
            AlertDialog.Builder alertBuild = new AlertDialog.Builder(currentActivity);
            alertBuild.setCancelable(false);
            alertBuild.setMessage(R.string.PLEASE_OPEN_AIR_PLANE_MODE);
            alertBuild.setPositiveButton(R.string.YES, null);
            alertBuild.show();

            return false;
        }
    }
    // TODO: 2018/12/13 關閉飛航模式檢查
//    private BroadcastReceiver receiver = new BroadcastReceiver() {
//        @Override
//        public void onReceive(Context context, Intent intent) {
//            String action = intent.getAction();
//
//            if (action.contentEquals("android.intent.action.AIRPLANE_MODE")) {
//                checkAirplaneModeAndBle();
//            }
//
//            DebugLog.d(TAG, "action is: " + action);
//        }
//    };

    private boolean checkBle() {
        checkBLEPermission();

        BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

        if (bluetoothAdapter.isEnabled() == false && Build.VERSION.SDK_INT < Build.VERSION_CODES.S) {
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);

            return false;
        } else {
            AirApplication thisApp = (AirApplication) getApplication();

            if (thisApp.getBleController() == null) {
                bleController = new V7RCLiteController(this);
            } else {
                bleController = thisApp.getBleController();
            }

            bleController.setCurrentWriteCharacteristicType(BluetoothGattCharacteristic.WRITE_TYPE_DEFAULT);
            bleController.setCallBack(this);

            if (checkBLEPermission()) {

                if (isLocationEnable()) {
                    scanRunnable.isStartScan = true;
                    handler.post(scanRunnable);
                    linkToDeviceByBluetoothAdapter = new LinkToDeviceByBluetoothAdapter();
                    ShowDeviceList.setAdapter(linkToDeviceByBluetoothAdapter);

                    return true;
                } else {
                    return false;
                }
            } else {
                return false;
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        DebugLog.e(TAG, "onDestroy");

        if (isFinishing()) {
            // TODO: 2018/12/13 關閉飛航模式檢查
//            unregisterReceiver(receiver);

            DebugLog.e(TAG, "onDestroy finishing");
            BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
            if (bluetoothAdapter.isEnabled()) {
                if (bleController != null)
                    bleController.stopScan();
            }
            scanRunnable.isStartScan = false;

        }
    }

    private AdapterView.OnItemClickListener onItemClickListener = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            AirApplication.setOnclikcFeedback(view);
            if (linkToDeviceByBluetoothAdapter != null) {
                DeviceInfo device = linkToDeviceByBluetoothAdapter.getDevice(position);

                if (device != null) {
                    lastDeviceMacAddress = device.getMacAddress();

                    DebugLog.d(TAG, "lastDeviceMacAddress: " + lastDeviceMacAddress);

                    linkToDeviceByBluetoothAdapter.notifyDataSetChanged();
                }
            }
        }
    };

    private View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            AirApplication.setOnclikcFeedback(v);
            int tag = (int) mImageTextView.getTag();
            Log.e(TAG, "tag = " + tag);
            // TODO: 2018/12/13 關閉飛航模式檢查
//            if (checkAirplaneModeAndBle()) {
            BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

            if (bluetoothAdapter.isEnabled() == false) {
                Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
            } else {
                if (tag == 1) {
                    progressDialog.settingProgressDialog();
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            progressDialog.dismiss();
                            if (linkToDeviceByBluetoothAdapter != null)
                                linkToDeviceByBluetoothAdapter.notifyDataSetChanged();
                        }
                    }, 3000);
                    mImageTextView.setTag(0);
                } else if (tag == 2) {
                    connectDevice(lastDeviceMacAddress);
                } else if (tag == 3) {
//                        progressDialog.settingProgressDialog();
//                        handler.postDelayed(new Runnable() {
//                            @Override
//                            public void run() {
//                                progressDialog.dismiss();
//                                if (linkToDeviceByBluetoothAdapter != null)
//                                    linkToDeviceByBluetoothAdapter.notifyDataSetChanged();
//                            }
//                        }, 3000);
//                        mImageTextView.setTag(0);
                    Intent intent = new Intent(currentActivity, LightDescriptionPage.class);
                    startActivity(intent);
                    finish();
                    setBackInPrePageAnimation(currentActivity);
                } else {
                    progressDialog.settingProgressDialog();
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            progressDialog.dismiss();
                            if (linkToDeviceByBluetoothAdapter != null)
                                linkToDeviceByBluetoothAdapter.notifyDataSetChanged();
                        }
                    }, 3000);
                    mImageTextView.setTag(0);
                }
            }
        }
//        }
        // TODO: 2018/12/13 關閉飛航模式檢查

    };

    private boolean connectDevice(String strDeviceMacAddress) {
        int tag = (int) mImageTextView.getTag();

        if (strDeviceMacAddress.isEmpty() == false) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (progressDialog != null) {
                        progressDialog.dismiss();
                    }

                    if (isFinishing() == false) {
                        progressDialog = new NotifyDialog(currentActivity);
                        progressDialog.settingProgressDialog();
                    }
                }
            });

            boolean connectSuccess = bleController.connect(strDeviceMacAddress);

            DebugLog.d(TAG, "connectSuccess is: " + connectSuccess);

            if (connectSuccess) {
                simpleDatabase.setValueByKey(Constant.LAST_CONNECTED_DEVICE_MAC, strDeviceMacAddress);
            }

            return connectSuccess;
        } else if (strDeviceMacAddress.isEmpty() == true && tag == 2) {
            showErrorAlert(getString(R.string.BLE_DEVICE_ADDRESS_IS_EMPTY));
        }

        return false;
    }

    @Override
    public void onConnected() {

    }

    @Override
    public void onDisconnected() {
        if (progressDialog != null) {
            progressDialog.dismiss();
        }
        BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (bluetoothAdapter.isEnabled()) {
            bleController.stopScan();
        }
    }

    @Override
    public void onDiscoverCharacteristics() {
        //因為有設定Notification，就是有設定BluetoothGattDescriptor的關系，一定要有Delay，要不然怎麼寫入都會回傳false

        if (bleController.getDeviceState() == BluetoothProfile.STATE_CONNECTED) {
            String deviceMacAddress = bleController.getConnectDevice().getAddress();

            simpleDatabase.setValueByKey(Constant.LAST_CONNECTED_DEVICE_MAC, deviceMacAddress);
            bleController.setCallBack(null);

            runOnUiThread(new Runnable() {
                @Override
                public void run() {

                    if (progressDialog != null) {
                        progressDialog.dismiss();
                    }

                    bleController.stopScan();
                    scanRunnable.isStartScan = false;


                    AirApplication thisApp = (AirApplication) getApplication();
                    thisApp.setBleController(bleController);

                    Intent intent = new Intent(currentActivity, CheckConnectedPage.class);
                    startActivity(intent);
                    finish();
                    setTurnInNextPageAnimation(currentActivity);
                }
            });
        }

//        handler.postDelayed(new Runnable() {
//            @Override
//            public void run() {
//                if(bleController.getDeviceState() == BluetoothProfile.STATE_CONNECTED)
//                {
//                    String deviceMacAddress = bleController.getConnectDevice().getAddress();
//
//                    simpleDatabase.setValueByKey(Constant.LAST_CONNECTED_DEVICE_MAC, deviceMacAddress);
//
//                    //發送 GetSerialNo 的命令
//                    //取得UnixTime, 用Int64 的原因是在 iPhone5會掛
//                    long currentTime = System.currentTimeMillis();
//
//                    String currentTimeToHexString = Long.toHexString(currentTime);
//
//                    DebugLog.d(TAG, "currentTimeToHexString: "+currentTimeToHexString);
//
//                    AirApplication.myID = "" + currentTime;
//
//                    DebugLog.d(TAG, "unix time: "+AirApplication.myID);
//
//                    //將UnixTime轉成16進制，不足16位時前面補0
//                    int addZero = 16 - currentTimeToHexString.length();
//
//                    StringBuilder padded = new StringBuilder(currentTimeToHexString);
//
//                    for(int i = 0 ; i < addZero ; i++){
//                        padded.insert(0, '0');
//                    }
//
//                    String command = padded.toString() + "0003000400030004";
//
//                    DebugLog.d(TAG, "command: "+command);
//
//                    byte[] commandByteData = Utils.hexToByteArray(command);
//
//                    boolean isSendSuccess = bleController.sendCommand(commandByteData);
//
//                    DebugLog.d(TAG, "isSendSuccess: "+isSendSuccess);
//                }
//
//                runOnUiThread(new Runnable() {
//                    @Override
//                    public void run() {
//                        if(progressDialog != null){
//                            progressDialog.dismiss();
//                        }
//                    }
//                });
//            }
//        }, 300);
    }

    @Override
    public void onCharacteristicChanged(BluetoothGattCharacteristic bluetoothGattCharacteristic) {
//        byte[] getValue = bluetoothGattCharacteristic.getValue();
//
//        DebugLog.e(TAG, "get Data");
//
//        if(getValue != null && getValue.length > 0)
//        {
//            DebugLog.d(TAG, "getValue length is : " +getValue.length);
//
//            String serialNo = Utils.bytesToHex(getValue);
//
//            AirApplication.serialNo = serialNo;
//
//            DebugLog.d(TAG, "serialNo: " + serialNo + " legth: " + serialNo.length());
//
//            bleController.setCallBack(null);
//
//            AirApplication thisApp = (AirApplication)getApplication();
//            thisApp.setBleController(bleController);
//
//            Intent intent = new Intent(currentActivity, ConnectedPage.class);
//            startActivity(intent);
//            finish();
//            setTurnInNextPageAnimation(currentActivity);
//        }
    }

    @Override
    public void onCharacteristicWrite(BluetoothGattCharacteristic bluetoothGattCharacteristic, int i) {

    }

    @Override
    public void onScanResult(final BluetoothDevice bluetoothDevice, int i, byte[] bytes) {
        DebugLog.d(TAG, "bluetoothDevice: " + bluetoothDevice.getAddress());

        if (linkToDeviceByBluetoothAdapter != null) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    linkToDeviceByBluetoothAdapter.addDevice(new DeviceInfo(bluetoothDevice));
                    if (progressDialog != null) {
                        progressDialog.dismiss();
                    }

                }
            });
        }
    }

    @Override
    public void enable() {
//        runOnUiThread(new Runnable() {
//            @Override
//            public void run() {
//
//                if (bleAlertDialog != null) {
//                    TextView messageView = (TextView) bleAlertDialog.findViewById(android.R.id.message);
//                    messageView.setText(R.string.BLE_IS_ENABLE);
//                } else {
//                    AlertDialog.Builder alertBuilder = new AlertDialog.Builder(currentActivity);
//                    alertBuilder.setMessage(getString(R.string.BLE_IS_ENABLE));
//                    alertBuilder.setPositiveButton(R.string.YES, null);
//                    alertBuilder.setCancelable(false);
//                    bleAlertDialog = alertBuilder.show();
//                }
//
//                if (checkBLEPermission()) {
//                    handler.post(scanRunnable);
//                }
//            }
//        });
    }

    @Override
    public void disable() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {

                if (bleAlertDialog != null) {
                    TextView messageView = (TextView) bleAlertDialog.findViewById(android.R.id.message);
                    messageView.setText(R.string.BLE_NOT_ENABLE);
                } else {
                    AlertDialog.Builder alertBuilder = new AlertDialog.Builder(currentActivity);
                    alertBuilder.setMessage(getString(R.string.BLE_NOT_ENABLE));
                    alertBuilder.setPositiveButton(R.string.YES, null);
                    alertBuilder.setCancelable(false);
                    bleAlertDialog = alertBuilder.show();
                }
            }
        });
    }

    @Override
    public void notSupport() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                showErrorAlert(getString(R.string.BLE_NOT_SUPPORT));
            }
        });
    }

    @Override
    public void onGattError(int i) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                progressDialog.dismiss();
                bleController.stopScan();
                scanRunnable.isStartScan = false;
                handler.removeCallbacks(scanRunnable);

                if (reConnectDeviceRunnable.ConnectCount >= 2) {
                    handler.removeCallbacks(reConnectDeviceRunnable);
                    reConnectDeviceRunnable.ConnectCount = 0;

                    scanRunnable.isStartScan = true;
                    handler.post(scanRunnable);
                    if (linkToDeviceByBluetoothAdapter != null) {
                        linkToDeviceByBluetoothAdapter.clear();
                        linkToDeviceByBluetoothAdapter.notifyDataSetChanged();
                    }
                    showErrorAlert("連線失敗，請重新嘗試");
                } else {
                    handler.post(reConnectDeviceRunnable);
                }
            }
        });
    }

    @Override
    public void onReadRssi(int i, int i1) {

    }

    @Override
    public void onMtuChanged(int i) {

    }

    @Override
    public void onClick(View view) {
        AirApplication.setOnclikcFeedback(view);
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

        view.setAnimation(scaleAnimation);
        view.startAnimation(scaleAnimation);
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(currentActivity, MainActivity.class);
        startActivity(intent);
        finish();
        setBackInPrePageAnimation(currentActivity);
    }

    private class LinkToDeviceByBluetoothAdapter extends BaseAdapter {
        private LinkedHashMap<String, DeviceInfo> mLeDevices;
        private LayoutInflater mInflator;

        public LinkToDeviceByBluetoothAdapter() {
            super();
            mLeDevices = new LinkedHashMap<String, DeviceInfo>();
            mInflator = getLayoutInflater();
        }

        public void addDevice(DeviceInfo device) {
            mLeDevices.put(device.getMacAddress(), device);
            notifyDataSetChanged();
        }

        public DeviceInfo getDevice(int position) {
            String[] keys = mLeDevices.keySet().toArray(new String[mLeDevices.size()]);
            return mLeDevices.get(keys[position]);
        }

        public void clear() {
            mLeDevices.clear();
        }

        @Override
        public int getCount() {
            String[] keys = mLeDevices.keySet().toArray(new String[mLeDevices.size()]);
            int tag = (int) mImageTextView.getTag();
            if (tag != 1) {
                if (keys.length == 0) {
                    Mode(3);
                } else if (tag == 2 || tag == 0) {
                    Mode(2);
                } else if (tag == 3) {
                    Mode(2);
                }

                return keys.length;
            }
            return 0;
        }

        @Override
        public Object getItem(int i) {
            String[] keys = (String[]) mLeDevices.keySet().toArray();
            return mLeDevices.get(keys[i]);
        }

        @Override
        public long getItemId(int i) {
            return i;
        }

        @Override
        public View getView(int i, View view, ViewGroup viewGroup) {
            ViewHolder viewHolder;
            // General ListView optimization code.
            if (view == null) {
                view = mInflator.inflate(R.layout.select_device_item_layout, null);
                view.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT
                        , (int) (100 * ViewScaling.getScaleValue())));

                viewHolder = new ViewHolder();
                viewHolder.backgroundImage = (ImageView) ViewScaling.findViewByIdAndScale(view, R.id.DeviceItem_ImageView_Background);
                viewHolder.deviceName = (TextView) ViewScaling.findViewByIdAndScale(view, R.id.DeviceItem_V7TitleView_Name);
                viewHolder.beforeConnectedIcon = (ImageView) ViewScaling.findViewByIdAndScale(view, R.id.DeviceItem_ImageView_Icon);
                viewHolder.deviceIcon = (ImageView) ViewScaling.findViewByIdAndScale(view, R.id.DeviceItem_ImageView_DeviceIcon);
                view.setTag(viewHolder);
            } else {
                viewHolder = (ViewHolder) view.getTag();
            }

            DeviceInfo device = getDevice(i);

            final String deviceName = device.getPeripheral().getName();

            if (deviceName != null && deviceName.length() > 0) {
                viewHolder.deviceName.setText(deviceName);
            } else {
                viewHolder.deviceName.setText(R.string.unknown_device);
            }

            if (device.getMacAddress().contentEquals(lastDeviceMacAddress)) {
                viewHolder.beforeConnectedIcon.setVisibility(View.VISIBLE);
                viewHolder.backgroundImage.setSelected(true);
            } else {
                viewHolder.beforeConnectedIcon.setVisibility(View.INVISIBLE);
                viewHolder.backgroundImage.setSelected(false);
            }


            return view;
        }

        private class ViewHolder {
            ImageView backgroundImage = null;
            TextView deviceName;
            ImageView beforeConnectedIcon = null;
            ImageView deviceIcon = null;
        }
    }

    private class ScanRunnable implements Runnable {
        public boolean isStartScan = false;
        BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

        @Override
        public void run() {

            if (isStartScan) {

                if (bluetoothAdapter.isEnabled()) {
                    bleController.stopScan();
                    if (progressDialog != null) {
                        progressDialog.dismiss();
                    }
                    if (bluetoothAdapter.isEnabled()) {
                        bleController.scanDevice();
                    }
                }
                handler.postDelayed(this, 5000);
            } else {
                handler.removeCallbacks(this);
            }


            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (linkToDeviceByBluetoothAdapter != null) {
                        for (int i = 0; i < linkToDeviceByBluetoothAdapter.getCount(); i++) {
                            DeviceInfo device = null;
                            if (linkToDeviceByBluetoothAdapter.getCount() == 1) {
                                device = linkToDeviceByBluetoothAdapter.getDevice(0);
                            } else {
                                device = linkToDeviceByBluetoothAdapter.getDevice(i);
                            }

                            if (device.isUpdate() == false) {
                                linkToDeviceByBluetoothAdapter.mLeDevices.remove(device.getMacAddress());
                                linkToDeviceByBluetoothAdapter.notifyDataSetChanged();
                            }
                            device.resetIsUpdate();
                        }
                    }
                }
            });

        }
    }

    private class ReConnectDeviceRunnable implements Runnable {
        public int ConnectCount = 0;

        @Override
        public void run() {
            ConnectCount = ConnectCount + 1;
            connectDevice(lastDeviceMacAddress);
        }
    }
}
