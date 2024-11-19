package com.v7idea.healthkit.Model;

import com.v7idea.template.DataBase.SimpleDatabase;
import com.v7idea.template.Tool.ApiResult;
import com.v7idea.template.Tool.DebugLog;
import com.v7idea.template.Tool.DownLoad;
import com.v7idea.template.Tool.V7HttpResult;
import com.v7idea.template.Tool.V7NameValuePair;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by mortal on 2017/11/1.
 */

public class Detection {
    private final static String TAG = "Detection";
    private final static String URL_KEY_NAME = "url";

    private SimpleDatabase mDataBase = null;

    public Detection() {
        mDataBase = new SimpleDatabase(TAG);
    }

    public void clear() {
        mDataBase.clearDataBase();
    }

    public String getDetectionUrl() {
        return mDataBase.getStringValueByKey(TAG, "");
    }

    public ApiResult postDetection(String strTokenID, ArrayList<V7NameValuePair> bodyData) {
//        String urlString = APIFetch.getDetectionApi();
        String urlString = APIFetch.getNewDetectionApi();

        DownLoad httpUtils = new DownLoad();

//        V7HttpResult httpResult = httpUtils.getStringFromURLByPostAddHeader(urlString, DownLoad.HeaderKeyName, strTokenID
//                , DownLoad.defaultContentType, bodyData);
        V7HttpResult httpResult = httpUtils.postUrlByFormData( DownLoad.HeaderKeyName, strTokenID
                , urlString, bodyData);
        DebugLog.d(TAG, "httpResult code: " + httpResult.getResponseCode());
        DebugLog.d(TAG, "httpResult result: " + httpResult.getResultString());


        if (httpResult.getResponseCode() == 200) {
            String resultString = httpResult.getResultString();

            ApiResult apiResult = ApiResult.getInstance(resultString);

            return apiResult;
        } else {
            return new ApiResult(String.valueOf(httpResult.getResponseCode())
                    , httpResult.getResultString(), "");
        }
    }

    public ApiResult getDetection(String tokenID) {
        String api = APIFetch.getDetectionApi();
        DownLoad httpUtils = new DownLoad();

        V7HttpResult httpResult = httpUtils.getStringFromURLAddHeader(api, tokenID);

        DebugLog.d(TAG, "httpResult code: " + httpResult.getResponseCode());
        DebugLog.d(TAG, "httpResult result: " + httpResult.getResultString());

        if (httpResult.getResponseCode() == 200) {
            String resultString = httpResult.getResultString();

            ApiResult apiResult = ApiResult.getInstance(resultString);

            JSONObject apiData = apiResult.getDataJSONObject();

            if (apiData != null) {
                String detectionReportUrl = apiData.optString(Detection.URL_KEY_NAME, "");

                DebugLog.d(TAG, "url is: " + detectionReportUrl);

                mDataBase.setValueByKey(TAG, detectionReportUrl);
            }

            return apiResult;
        } else {
            return new ApiResult(String.valueOf(httpResult.getResponseCode())
                    , httpResult.getResultString(), "");
        }
    }
}
