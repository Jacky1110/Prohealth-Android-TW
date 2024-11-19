package com.v7idea.template;

import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.PowerManager;
import android.provider.Settings;
import android.util.Log;
import android.view.HapticFeedbackConstants;
import android.view.View;

import com.v7idea.healthkit.Constant;
import com.v7idea.healthkit.Domain.FirmwareData;
import com.v7idea.template.Activity.BaseActivity;
import com.v7idea.template.DataBase.SimpleDatabase;
import com.v7idea.template.Tool.DebugLog;
import com.v7idea.template.Tool.Utils;
import com.v7idea.v7rcliteandroidsdk.V7RCLiteController;

import java.io.UnsupportedEncodingException;

/**
 * Created by mortal on 2017/9/25.
 */

public class AirApplication extends Application {
    private static final String TAG = "AirApplication";

    public String LastName;
    public String FirstName;
    public String PhoneNumber;
    public String Password;
    public String Email;
    public String Birthday;
    public String Height;
    public String Weight;
    public String Gender;
    public String BloodType;
    public static String this_mac_sn;
    public static String this_mac_name;

    public static String checkCode;
    /////////////////
    public static String Detection_Height = "";
    public static String Detection_Weight = "";
    public static String Detection_Question1 = "";
    public static String Detection_Question2 = "";
    /////////////////
    private static Context context = null;

    public static String myID = "";
    public static String serialNo = "";

    public StringBuilder send_data = null;

    public String reseller = "";

    //经度
    public static double longitude = 0;
    //纬度
    public static double latitude = 0;

    private V7RCLiteController bleController = null;

    public static Context getAppContext() {
        return context;
    }

    private FirmwareData firmwareData = null;

    public BaseActivity lastActivity = null;

    public boolean isThisTimeAsk = false;

    @Override
    public void onCreate() {
        super.onCreate();

        DebugLog.e("Air", "onCreate");

        context = this;

        Utils.getHashKey();
        simpleDatabase = new SimpleDatabase();
        setOnclikcFeedbackData(true);
        setIsEntryPage(false);
    }

    public V7RCLiteController getBleController() {
        return bleController;
    }

    public void setBleController(V7RCLiteController bleController) {
        this.bleController = bleController;
    }

    public FirmwareData getFirmwareData() {
        return firmwareData;
    }

    public void setFirmwareData(FirmwareData firmwareData) {
        this.firmwareData = firmwareData;
    }

    /**
     * 量測字串是否為null或""
     *
     * @param checkString
     * @return 不是null及""回傳true
     */
    public static boolean isStringNotNullAndEmpty(String checkString) {
        if (checkString != null && !checkString.isEmpty()) {
            return true;
        }

        return false;
    }

    public byte[] toUTF8ByteArray(String string) {
        try {
            return string.getBytes("UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            return null;
        }
    }

    public String byteArrayToUTF8String(byte[] bytes) {
        try {
            String utf8String = new String(bytes, "UTF-8");
            return utf8String;
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            return "";
        }
    }

    private static SimpleDatabase simpleDatabase = null;

    public static void setOnclikcFeedbackData(boolean feedback) {
        simpleDatabase.setValueByKey(Constant.ONCLICK_FEEDBACK, feedback);
    }

    public static boolean getOnclikcFeedback() {
        return simpleDatabase.getBooleanValueByKey(Constant.ONCLICK_FEEDBACK, false);
    }

    public static void setOnclikcFeedback(View view) {
        if (getOnclikcFeedback()) {
            view.performHapticFeedback(HapticFeedbackConstants.LONG_PRESS,
                    HapticFeedbackConstants.FLAG_IGNORE_GLOBAL_SETTING);
        }
    }

    private  boolean isEntryPage = true;

    public  boolean isIsEntryPage() {
        return isEntryPage;
    }

    public  void setIsEntryPage(boolean isEntryPage) {
//       this.isEntryPage = isEntryPage;
        Log.e(TAG, "setIsEntryPage = " + isEntryPage);
    }

    @Override
    public void onTerminate() {
        super.onTerminate();
        setIsEntryPage(false);
    }
}
