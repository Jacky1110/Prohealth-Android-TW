package com.v7idea.healthkit.Model;
//  CheckCode.swift
//  HealthKitDemo
//
//  Created by 連祐鑫 on 2017/10/16.
//  Copyright © 2017年 連祐鑫. All rights reserved.
//


import com.v7idea.template.DataBase.SimpleDatabase;
import com.v7idea.template.Tool.ApiResult;
import com.v7idea.template.Tool.DebugLog;
import com.v7idea.template.Tool.DownLoad;
import com.v7idea.template.Tool.V7BaseNameValuePair;
import com.v7idea.template.Tool.V7HttpResult;
import com.v7idea.template.Tool.V7NameValuePair;

import java.util.ArrayList;
import java.util.List;

public class CheckCode {
    private static String TAG = "CheckCode";
    private SimpleDatabase mDataBase = null;

    public CheckCode() {
        mDataBase = new SimpleDatabase(TAG);
    }

    public void clear(){
        mDataBase.clearDataBase();
    }

    public ApiResult reSendChaeckCode(String mobile) {

        String strApi = APIFetch.getRegisterResendCheckCodeApi();
        DownLoad downLoad = new DownLoad();

        List<V7NameValuePair> content = new ArrayList<V7NameValuePair>();
        content.add(new V7BaseNameValuePair("Mobile", mobile));

        V7HttpResult httpResult = downLoad.getStringFromURLByPostAddHeader(strApi, content);

        DebugLog.d(TAG, "reSendChaeckCode httpResult code: " + httpResult.getResponseCode());
        DebugLog.d(TAG, "reSendChaeckCode httpResult result: " + httpResult.getResultString());
        if (httpResult.getResponseCode() == 200) {
            String resultString = httpResult.getResultString();

            ApiResult apiResult = ApiResult.getInstance(resultString);
            return apiResult;
        }

        return new ApiResult("" + httpResult.getResponseCode(), httpResult.getResultString(), null);
    }


}