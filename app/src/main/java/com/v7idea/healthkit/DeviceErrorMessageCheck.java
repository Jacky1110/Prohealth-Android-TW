package com.v7idea.healthkit;

import android.app.AlertDialog;
import android.content.DialogInterface;

import com.v7idea.template.Tool.DebugLog;

public class DeviceErrorMessageCheck {
    public String checkErrorNumber(String number) {
        if (number.substring(4, 6).equals("00")) {
            //食指沒按 無名指沒按
            return "e0809";
        } else if (number.substring(4, 5).equals("0")) {
            //無名指沒按
            return "e09";
        } else if (number.substring(5, 6).equals("0")) {
            //食指沒按
            return "e08";
        } else if (number.equals("ER00110200000000")) {
            //成功 開始量測
            return "success";
        } else if (number.substring(7, 8).equals("0")) {
            //LOW　BATTERY
            return "18";
        } else if (number.substring(6, 7).equals("1")) {
            //藍牙中斷
            return "e13";
        } else if (number.substring(10, 11).equals("1")) {
            //連線愈時
            return "連線愈時";
        }
        return "else";
    }

    private void Dialog(String title, String message) {

    }
}
