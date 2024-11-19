package com.v7idea.healthkit.Model;

import android.app.Application;

import com.v7idea.template.AirApplication;
import com.v7idea.template.DataBase.SimpleDatabase;
import com.v7idea.template.Tool.ApiResult;
import com.v7idea.template.Tool.DebugLog;
import com.v7idea.template.Tool.DownLoad;
import com.v7idea.template.Tool.V7BaseNameValuePair;
import com.v7idea.template.Tool.V7HttpResult;
import com.v7idea.template.Tool.V7NameValuePair;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class HealthReport
{
    private final static String TAG = "HealthReport";

    private SimpleDatabase mDataBase = null;

    public HealthReport()
    {
        mDataBase = new SimpleDatabase(TAG);
    }

    public void clear()
    {
        mDataBase.clearDataBase();
    }

    public ApiResult getHealthReport(String tokenID)
    {
        String api = APIFetch.getHealthReportApi();
        DownLoad httpUtils = new DownLoad();

        V7HttpResult httpResult = httpUtils.getStringFromURLAddHeader(api, tokenID);

        DebugLog.d(TAG, "httpResult code: " + httpResult.getResponseCode());
        DebugLog.d(TAG, "httpResult result: " + httpResult.getResultString());

        if (httpResult.getResponseCode() == 200)
        {
            String resultString = httpResult.getResultString();

            ApiResult apiResult = ApiResult.getInstance(resultString);

            JSONObject apiData = apiResult.getDataJSONObject();

            if (apiData != null)
            {
                String healthReportUrl = apiData.optString("url", "");

                DebugLog.d(TAG, "url is: " + healthReportUrl);

                mDataBase.setValueByKey(TAG, healthReportUrl);
            }

            return apiResult;
        }
        else
        {
            return new ApiResult(String.valueOf(httpResult.getResponseCode())
                    , httpResult.getResultString(), "");
        }
    }


    public ApiResult getReadHealthReportApi(String tokenID, String reportID)
    {
        String api = APIFetch.getReadHealthReportApi();

        String reseller = "tw-00017";
        DownLoad downLoad = new DownLoad();

        List<V7NameValuePair> content = new ArrayList<V7NameValuePair>();
        content.add(new V7BaseNameValuePair("reportID", reportID));
        content.add(new V7BaseNameValuePair("reseller", reseller));
        V7HttpResult httpResult = downLoad.getStringFromURLByPostAddHeader(api, DownLoad.HeaderKeyName, tokenID, downLoad.defaultContentType, content);

        DebugLog.d(TAG, "httpResult code: " + httpResult.getResponseCode());
        DebugLog.d(TAG, "httpResult result: " + httpResult.getResultString());

        if (httpResult.getResponseCode() == 200)
        {
            String resultString = httpResult.getResultString();

            ApiResult apiResult = ApiResult.getInstance(resultString);

            return apiResult;
        }
        else
        {
            return new ApiResult(String.valueOf(httpResult.getResponseCode())
                    , httpResult.getResultString(), "");
        }
    }


    public ApiResult getLastHealthReport(String tokenID)
    {
        String api = APIFetch.getLastHealthReportApi();
        DownLoad httpUtils = new DownLoad();

        V7HttpResult httpResult = httpUtils.getStringFromURLByPostAddHeader(api, DownLoad.HeaderKeyName, tokenID, DownLoad.defaultContentType,null);

        DebugLog.d(TAG, "httpResult code: " + httpResult.getResponseCode());
        DebugLog.d(TAG, "httpResult result: " + httpResult.getResultString());

        if (httpResult.getResponseCode() == 200)
        {
            String resultString = httpResult.getResultString();

            ApiResult apiResult = ApiResult.getInstance(resultString);

            return apiResult;
        }
        else
        {
            return new ApiResult(String.valueOf(httpResult.getResponseCode())
                    , httpResult.getResultString(), "");
        }
    }

}
