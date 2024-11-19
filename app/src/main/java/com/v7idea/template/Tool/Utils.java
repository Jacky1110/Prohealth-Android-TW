package com.v7idea.template.Tool;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.graphics.Paint;
import android.graphics.Rect;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.provider.Settings;
import android.telephony.TelephonyManager;
import android.util.Base64;
import android.widget.TextView;

import com.v7idea.template.AirApplication;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.IntBuffer;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * Created by mortal on 2017/9/26.
 */

public class Utils {
    private static final boolean isUsePasswordBase64 = true;
    public static final String DEFAULT_DATE_PATTERN = "yyyy-MM-dd";

    public static int getStringWidth(TextView textView, String text){
        Rect bounds = getStringRect(textView, text);
        return bounds.width();
    }

    public static Rect getStringRect(TextView textView, String text){
        Rect bounds = new Rect();
        Paint textPaint = textView.getPaint();
        textPaint.getTextBounds(text, 0, text.length(), bounds);
//        int height = bounds.height();
//        int width = bounds.width();
        return bounds;
    }


    public static boolean isAirplaneModeOn(Context context) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN_MR1) {
            return Settings.System.getInt(context.getContentResolver(),
                    Settings.System.AIRPLANE_MODE_ON, 0) != 0;
        } else {
            return Settings.Global.getInt(context.getContentResolver(),
                    Settings.Global.AIRPLANE_MODE_ON, 0) != 0;
        }
    }

    /**
     * Get the network info
     *
     * @param context
     * @return
     */
    public static NetworkInfo getNetworkInfo(Context context) {
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        return cm.getActiveNetworkInfo();
    }

    /**
     * Check if there is any connectivity
     *
     * @param context
     * @return
     */
    public static boolean isConnected(Context context) {
        NetworkInfo info = getNetworkInfo(context);
        return (info != null && info.isConnected());
    }

    /**
     * Check if there is any connectivity to a Wifi network
     *
     * @param context
     * @return
     */
    public static boolean isConnectedWifi(Context context) {
        NetworkInfo info = getNetworkInfo(context);
        return (info != null && info.isConnected() && info.getType() == ConnectivityManager.TYPE_WIFI);
    }

    /**
     * Check if there is any connectivity to a mobile network
     *
     * @param context
     * @return
     */
    public static boolean isConnectedMobile(Context context) {
        NetworkInfo info = getNetworkInfo(context);
        return (info != null && info.isConnected() && info.getType() == ConnectivityManager.TYPE_MOBILE);
    }

    /**
     * Check if there is fast connectivity
     *
     * @param context
     * @return
     */
    public static boolean isConnectedFast(Context context) {
        NetworkInfo info = getNetworkInfo(context);
        return (info != null && info.isConnected() && isConnectionFast(info.getType(), info.getSubtype()));
    }

    public static String checkNetworkType(Context context) {
        NetworkInfo currentNetworkInfo = getNetworkInfo(context);

        int networkType = currentNetworkInfo.getType();

        if (networkType == ConnectivityManager.TYPE_WIFI) {
            return "Wifi";
        } else if (networkType == ConnectivityManager.TYPE_MOBILE) {
            int networkSubType = currentNetworkInfo.getSubtype();

            switch (networkSubType) {
                case TelephonyManager.NETWORK_TYPE_GPRS:
                case TelephonyManager.NETWORK_TYPE_EDGE:
                case TelephonyManager.NETWORK_TYPE_CDMA:
                case TelephonyManager.NETWORK_TYPE_1xRTT:
                case TelephonyManager.NETWORK_TYPE_IDEN:
                    return "2g";
                case TelephonyManager.NETWORK_TYPE_UMTS:
                case TelephonyManager.NETWORK_TYPE_EVDO_0:
                case TelephonyManager.NETWORK_TYPE_EVDO_A:
                    /**
                     From this link https://goo.gl/R2HOjR ..NETWORK_TYPE_EVDO_0 & NETWORK_TYPE_EVDO_A
                     EV-DO is an evolution of the CDMA2000 (IS-2000) standard that supports high data rates.

                     Where CDMA2000 https://goo.gl/1y10WI .CDMA2000 is a family of 3G[1] mobile technology standards for sending voice,
                     data, and signaling data between mobile phones and cell sites.
                     */
                case TelephonyManager.NETWORK_TYPE_HSDPA:
                case TelephonyManager.NETWORK_TYPE_HSUPA:
                case TelephonyManager.NETWORK_TYPE_HSPA:
                case TelephonyManager.NETWORK_TYPE_EVDO_B:
                case TelephonyManager.NETWORK_TYPE_EHRPD:
                case TelephonyManager.NETWORK_TYPE_HSPAP:
                    //Log.d("Type", "3g");
                    //For 3g HSDPA , HSPAP(HSPA+) are main  networktype which are under 3g Network
                    //But from other constants also it will 3g like HSPA,HSDPA etc which are in 3g case.
                    //Some cases are added after  testing(real) in device with 3g enable data
                    //and speed also matters to decide 3g network type
                    //http://goo.gl/bhtVT
                    return "3g";
                case TelephonyManager.NETWORK_TYPE_LTE:
                    //No specification for the 4g but from wiki
                    //I found(LTE (Long-Term Evolution, commonly marketed as 4G LTE))
                    //https://goo.gl/9t7yrR
                    return "4g";
                default:
                    return "Notfound";
            }
        }

        return "";
    }

    /**
     * Check if the connection is fast
     *
     * @param type
     * @param subType
     * @return
     */
    public static boolean isConnectionFast(int type, int subType) {
        if (type == ConnectivityManager.TYPE_WIFI) {
            return true;
        } else if (type == ConnectivityManager.TYPE_MOBILE) {
            switch (subType) {
                case TelephonyManager.NETWORK_TYPE_1xRTT:
                    return false; // ~ 50-100 kbps
                case TelephonyManager.NETWORK_TYPE_CDMA:
                    return false; // ~ 14-64 kbps
                case TelephonyManager.NETWORK_TYPE_EDGE:
                    return false; // ~ 50-100 kbps
                case TelephonyManager.NETWORK_TYPE_EVDO_0:
                    return true; // ~ 400-1000 kbps
                case TelephonyManager.NETWORK_TYPE_EVDO_A:
                    return true; // ~ 600-1400 kbps
                case TelephonyManager.NETWORK_TYPE_GPRS:
                    return false; // ~ 100 kbps
                case TelephonyManager.NETWORK_TYPE_HSDPA:
                    return true; // ~ 2-14 Mbps
                case TelephonyManager.NETWORK_TYPE_HSPA:
                    return true; // ~ 700-1700 kbps
                case TelephonyManager.NETWORK_TYPE_HSUPA:
                    return true; // ~ 1-23 Mbps
                case TelephonyManager.NETWORK_TYPE_UMTS:
                    return true; // ~ 400-7000 kbps
                /*
                 * Above API level 7, make sure to set android:targetSdkVersion
                 * to appropriate level to use these
                 */
                case TelephonyManager.NETWORK_TYPE_EHRPD: // API level 11
                    return true; // ~ 1-2 Mbps
                case TelephonyManager.NETWORK_TYPE_EVDO_B: // API level 9
                    return true; // ~ 5 Mbps
                case TelephonyManager.NETWORK_TYPE_HSPAP: // API level 13
                    return true; // ~ 10-20 Mbps
                case TelephonyManager.NETWORK_TYPE_IDEN: // API level 8
                    return false; // ~25 kbps
                case TelephonyManager.NETWORK_TYPE_LTE: // API level 11
                    return true; // ~ 10+ Mbps
                // Unknown
                case TelephonyManager.NETWORK_TYPE_UNKNOWN:
                default:
                    return false;
            }
        } else {
            return false;
        }
    }

    public static int[] byteArrayToIntArray(byte[] byteArray) {
        IntBuffer intBuf =
                ByteBuffer.wrap(byteArray)
                        .order(ByteOrder.BIG_ENDIAN)
                        .asIntBuffer();
        int[] array = new int[intBuf.remaining()];
        intBuf.get(array);

        return array;
    }

    /**
     * method to convert a byte to a hex string.
     *
     * @param data the byte to convert
     * @return String the converted byte
     */
    public static String byteToHex(byte data) {
        StringBuffer buf = new StringBuffer();
        buf.append(toHexChar((data >>> 4) & 0x0F));
        buf.append(toHexChar(data & 0x0F));
        return buf.toString();
    }

    public static String bytesToHex(byte[] data) {
        StringBuffer buf = new StringBuffer();
        for (int i = 0; i < data.length; i++) {
            buf.append(byteToHex(data[i]).toUpperCase());
        }
        return (buf.toString());
    }

    public static byte[] hexToByteArray(String hex) {
        hex = hex.length() % 2 != 0 ? "0" + hex : hex;

        byte[] b = new byte[hex.length() / 2];

        for (int i = 0; i < b.length; i++) {
            int index = i * 2;
            String subString = hex.substring(index, index + 2);
            int v = Integer.parseInt(subString, 16);
            b[i] = (byte) v;
        }
        return b;
    }

    /***
     * If you want a range of 0-255
     * @param hex
     * @return
     */
    public static byte[] hexToBytes2(char[] hex) {
        int length = hex.length / 2;
        byte[] raw = new byte[length];
        for (int i = 0; i < length; i++) {
            int high = Character.digit(hex[i * 2], 16);
            int low = Character.digit(hex[i * 2 + 1], 16);
            int value = (high << 4) | low;

            raw[i] = (byte) value;
        }
        return raw;
    }

    /***
     * If you want a range of -128 to 127
     * @param hex
     * @return
     */
    public static byte[] hexToBytes(char[] hex) {
        int length = hex.length / 2;
        byte[] raw = new byte[length];
        for (int i = 0; i < length; i++) {
            int high = Character.digit(hex[i * 2], 16);
            int low = Character.digit(hex[i * 2 + 1], 16);
            int value = (high << 4) | low;
            if (value > 127)
                value -= 256;
            raw[i] = (byte) value;
        }
        return raw;
    }


    /**
     * Convenience method to convert an int to a hex char.
     *
     * @param i the int to convert
     * @return char the converted char
     */
    public static char toHexChar(int i) {
        if ((0 <= i) && (i <= 9)) {
            return (char) ('0' + i);
        } else {
            return (char) ('a' + (i - 10));
        }
    }

    public static int checkValueInArrayIndex(String value, String[] array) {
        if (value.isEmpty()) {
            return 0;
        } else {

            for (int i = 0; i < array.length; i++) {
                String arrayValue = array[i];

                if (arrayValue.contentEquals(value)) {
                    return i;
                }
            }

            return 0;
        }
    }

    public static boolean checkDate(String dateString) {
        try {
            DateTimeFormatter fmt = DateTimeFormat.forPattern(DEFAULT_DATE_PATTERN);
            DateTime dateTime = fmt.parseDateTime(dateString);
            dateTime = null;
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public static boolean checkDateYear(String dateString) {
        try {
            DateTimeFormatter fmt = DateTimeFormat.forPattern(DEFAULT_DATE_PATTERN);
            DateTime dateTime = fmt.parseDateTime(dateString);
            int Year = dateTime.getYear();
            if (Year > 1900) {
                dateTime = null;
                return true;
            } else {
                dateTime = null;
                return false;
            }
        } catch (Exception e) {
            return false;
        }
    }

    public static String getStringBase64Data(String data) {
        if (isUsePasswordBase64) {
            try {
                byte[] dataArray = data.getBytes("UTF-8");
                return Base64.encodeToString(dataArray, Base64.DEFAULT);
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
                return "";
            }
        } else {
            return data;
        }
    }

    public static String getDeviceInfor() {
        TelephonyManager tel = (TelephonyManager) AirApplication.getAppContext().getSystemService(Context.TELEPHONY_SERVICE);

        String manufcturer = Build.MANUFACTURER;
        String prosuct = Build.PRODUCT;
        String telString = "none";

        if (tel == null) {
            telString = getAndroidId(AirApplication.getAppContext());
        }

        try {
            if (telString == null) telString = "none";
            if (manufcturer == null) manufcturer = "none";
            if (prosuct == null) prosuct = "none";

            return URLEncoder.encode(telString, "utf-8") + ";" + URLEncoder.encode(manufcturer, "utf-8") + ";" + URLEncoder.encode(prosuct, "utf-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        return "";
    }

    /**
     * 取得android id
     *
     * @param context
     * @return
     */
    public static String getAndroidId(Context context) {
        return Settings.Secure.getString(context.getContentResolver(), Settings.Secure.ANDROID_ID);
    }

    public static String getHashKey() {
        // Add code to print out the key hash
        try {
            PackageInfo info = AirApplication.getAppContext().getPackageManager().getPackageInfo(
                    AirApplication.getAppContext().getPackageName(),
                    PackageManager.GET_SIGNATURES);
            String KeyHash = "";

            for (Signature signature : info.signatures) {
                MessageDigest md = MessageDigest.getInstance("SHA");
                md.update(signature.toByteArray());

                KeyHash = Base64.encodeToString(md.digest(), Base64.DEFAULT);
//                DebugLog.e("KeyHash:", "" + KeyHash);
            }

            return KeyHash;
        } catch (PackageManager.NameNotFoundException e) {

        } catch (NoSuchAlgorithmException e) {

        }

        return "";
    }
}
