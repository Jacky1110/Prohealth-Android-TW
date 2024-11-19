package com.v7idea.template.Activity;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;

import androidx.fragment.app.FragmentActivity;

import com.v7idea.healthkit.Constant;
import com.v7idea.healthkit.LoginPage;
import com.v7idea.healthkit.Model.CheckCode;
import com.v7idea.healthkit.Model.Detection;
import com.v7idea.healthkit.Model.FunctionList;
import com.v7idea.healthkit.Model.Member;
import com.v7idea.healthkit.Model.Token;
import com.v7idea.healthkit.R;
import com.v7idea.template.AirApplication;
import com.v7idea.template.Tool.DownLoad;
import com.v7idea.template.Tool.Utils;
import com.v7idea.template.Tool.ViewScaling;

import java.util.ArrayList;

/**
 * Created by mortal on 2017/9/25.
 */

public class BaseActivity extends FragmentActivity {
    protected BaseActivity currentActivity = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        AirApplication airApplication = (AirApplication) getApplication();

        if (airApplication.lastActivity != null) {
            airApplication.lastActivity.finish();
            airApplication.lastActivity = null;
        }

        airApplication.lastActivity = this;

        super.onCreate(savedInstanceState);

        currentActivity = this;

        ViewScaling.setScaleValue(currentActivity);

        //取消螢幕上的那一槓
        final Window win = this.getWindow();
        win.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
    }

    public void clearAllData() {
        CheckCode checkCode = new CheckCode();
        checkCode.clear();

        Detection detection = new Detection();
        detection.clear();

        FunctionList functionList = new FunctionList();
        functionList.clear();

        Member member = new Member();
        member.clear();

        Token token = new Token();
        token.destroy();
    }

    public boolean isAirplaneModeOn() {
        return Utils.isAirplaneModeOn(this);
    }

    public void setTurnInNextPageAnimation(Activity FinishActivity) {
        if (FinishActivity != null) {
            FinishActivity.overridePendingTransition(R.anim.activity_in_from_right, R.anim.activity_out_to_left);
        }
    }

    public void setBackInPrePageAnimation(Activity FinishActivity) {
        if (FinishActivity != null) {
            FinishActivity.overridePendingTransition(R.anim.activity_in_from_left, R.anim.activity_out_to_right);
        }
    }

    public void showErrorAlertIfNoLogin(String strMessage) {
        clearAllData();

        showErrorAlert(strMessage, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Intent intent = new Intent(currentActivity, LoginPage.class);
                startActivity(intent);
                finish();
            }
        });
    }

    public void showErrorAlert(String strMessage) {
        AlertDialog.Builder alertBuilder = new AlertDialog.Builder(currentActivity);
        alertBuilder.setMessage(strMessage);
        alertBuilder.setPositiveButton(R.string.YES, null);
        alertBuilder.setCancelable(false);
        alertBuilder.show();
    }

    public void showErrorAlert(String strMessage, DialogInterface.OnClickListener onClickListener) {
        AlertDialog.Builder alertBuilder = new AlertDialog.Builder(currentActivity);
        alertBuilder.setMessage(strMessage);
        alertBuilder.setPositiveButton(R.string.YES, onClickListener);
        alertBuilder.setCancelable(false);
        alertBuilder.show();
    }

    public void showErrorAlert(String strMessage, DialogInterface.OnClickListener onClickListener
            , DialogInterface.OnClickListener onPressNegativeListener, boolean isCancelAble) {
        AlertDialog.Builder alertBuilder = new AlertDialog.Builder(currentActivity);
        alertBuilder.setMessage(strMessage);
        alertBuilder.setPositiveButton(R.string.YES, onClickListener);
        alertBuilder.setNegativeButton(R.string.NO, onPressNegativeListener);
        alertBuilder.setCancelable(isCancelAble);
        alertBuilder.show();
    }

    public void showSoftKeyBoardWhenTouchEditText(EditText whichEditText) {
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.showSoftInput(whichEditText, InputMethodManager.SHOW_IMPLICIT);
    }

    public boolean isConnectedNetWork() {
        boolean isConnected = DownLoad.isConnectInternet();

        if (isConnected == false) {
            showErrorAlert(getResources().getString(R.string.no_internet_can_not_use));
        }

        return isConnected;
    }

    public boolean isConnectedToNetworkNotAlert() {
        return DownLoad.isConnectInternet();
    }

    public String checkNetworkType() {
        return Utils.checkNetworkType(this);
    }

    public boolean isLocationEnable() {
        LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        boolean isEnable = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);

        if (isEnable == false) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setMessage(R.string.gps_network_not_enabled); // Want to enable?
            builder.setCancelable(false);
            builder.setPositiveButton(R.string.YES, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialogInterface, int i) {
                    startActivity(new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS));
                }
            });
            builder.setNegativeButton(R.string.NO, null);
            builder.create().show();
        }

        return isEnable;
    }

    public boolean checkBLEPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            int permissionCOARSE_LOCATION = checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION);
            int permissionFINE_LOCATION = checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION);

            ArrayList<String> permissionList = new ArrayList<String>();

            if (permissionFINE_LOCATION != PackageManager.PERMISSION_GRANTED) {
                permissionList.add(Manifest.permission.ACCESS_FINE_LOCATION);
            }

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                int permissionBLEScan = checkSelfPermission(Manifest.permission.BLUETOOTH_SCAN);
                int permissionBLEConnect = checkSelfPermission(Manifest.permission.BLUETOOTH_CONNECT);

                if (permissionBLEScan != PackageManager.PERMISSION_GRANTED) {
                    permissionList.add(Manifest.permission.BLUETOOTH_SCAN);
                }

                if (permissionBLEConnect != PackageManager.PERMISSION_GRANTED) {
                    permissionList.add(Manifest.permission.BLUETOOTH_CONNECT);
                }
            }

            if (permissionList.size() > 0) {
                String[] permissionArray = new String[permissionList.size()];

                for (int i = 0; i < permissionList.size(); i++) {
                    permissionArray[i] = permissionList.get(i);
                }

                requestPermissions(permissionArray, Constant.REQUEST_CODE);

                return false;
            } else {
                return true;
            }
        } else {
            return true;
        }
    }
}
