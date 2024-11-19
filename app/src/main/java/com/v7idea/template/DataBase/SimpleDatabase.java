package com.v7idea.template.DataBase;

import android.content.Context;
import android.content.SharedPreferences;

import com.v7idea.healthkit.Constant;
import com.v7idea.template.AirApplication;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by mortal on 15/3/12.
 */
public class SimpleDatabase {
    private String tag = "SimpleDatabase_user";

    private Context context;
    private SharedPreferences mDataBase = null;

    public SimpleDatabase() {
        this.context = AirApplication.getAppContext();
        init();
    }

    public SimpleDatabase(String tableName) {
        this.context = AirApplication.getAppContext();
        init(tableName);
    }

    private void init(String tableName) {
        if (mDataBase == null) {
            tag = tableName;
            mDataBase = context.getSharedPreferences(tag, Context.MODE_PRIVATE);
        }
    }

    private void init() {
        if (mDataBase == null) {
            mDataBase = context.getSharedPreferences(tag, Context.MODE_PRIVATE);
        }
    }

    public JSONArray getJSONArrayByKeyIfNullReturnNull(String strKeyName) {
        init();

        String strKeyData = mDataBase.getString(strKeyName, "");

        JSONArray DataJSON = null;

        try {
            DataJSON = new JSONArray(strKeyData);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return DataJSON;
    }

    public JSONObject getJSONObjectByKeyIfNullReturnEmpty(String keyName) {
        init();

        String strKeyData = mDataBase.getString(keyName, "");

        JSONObject DataJSON = null;

        try {
            DataJSON = new JSONObject(strKeyData);
        } catch (JSONException e) {
            e.printStackTrace();
            DataJSON = new JSONObject();
        }

        return DataJSON;
    }

    public void setValueByKey(String keyName, long value) {
        init();

        SharedPreferences.Editor editor = mDataBase.edit();
        editor.putLong(keyName, value);
        editor.commit();
    }

    public void setValueByKey(String keyName, String value) {
        init();

        SharedPreferences.Editor editor = mDataBase.edit();
        editor.putString(keyName, value);
        editor.commit();
    }

    public void setValueByKey(String keyName, boolean value) {
        init();

        SharedPreferences.Editor editor = mDataBase.edit();
        editor.putBoolean(keyName, value);
        editor.commit();
    }

    public void setValueByKey(String keyName, int value) {
        init();

        SharedPreferences.Editor editor = mDataBase.edit();
        editor.putInt(keyName, value);
        editor.commit();
    }

    public String getStringValueByKey(String keyName, String defaultValue) {
        init();
        return mDataBase.getString(keyName, defaultValue);
    }

    public boolean getBooleanValueByKey(String keyName, boolean defaultValue) {
        init();
        return mDataBase.getBoolean(keyName, defaultValue);
    }

    public int getIntValueByKey(String keyName, int defaultValue) {
        init();
        return mDataBase.getInt(keyName, defaultValue);
    }

    public long getLongValueByKey(String keyName, long defaultValue) {
        init();
        return mDataBase.getLong(keyName, defaultValue);
    }

    public void clearDataBase() {
        init();

        boolean isSaveRegisterID =  mDataBase.contains(Constant.GCMRegisterID);
        boolean isSaveShowWellComePageParam =  mDataBase.contains(Constant.InitString);

        String registerID = "";
        boolean isShowWellComePage = true;

        if(isSaveRegisterID){
            registerID = mDataBase.getString(Constant.GCMRegisterID, "");
        }

        if(isSaveShowWellComePageParam){
            isShowWellComePage = mDataBase.getBoolean(Constant.InitString, true);
        }


        SharedPreferences.Editor editor = mDataBase.edit();
        editor.clear();
        editor.commit();

        if(isSaveRegisterID){
            editor.putString(Constant.GCMRegisterID, registerID);
            editor.commit();
        }

        if(isSaveShowWellComePageParam){
            editor.putBoolean(Constant.InitString, isShowWellComePage);
            editor.commit();
        }

    }
}
