package com.v7idea.healthkit.Model;

import com.v7idea.template.Tool.ApiResult;
import com.v7idea.template.Tool.DebugLog;
import com.v7idea.template.Tool.DownLoad;
import com.v7idea.template.Tool.V7BaseNameValuePair;
import com.v7idea.template.Tool.V7HttpResult;
import com.v7idea.template.Tool.V7NameValuePair;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class HealthSummary
{
    private final static String TAG = "HealthSummary";

    public ApiResult getHealthSummary(String tokenID)
    {
        String api = APIFetch.getHealthSummaryApi();
        DownLoad httpUtils = new DownLoad();

        V7HttpResult httpResult = httpUtils.getStringFromURLAddHeader(api, tokenID);

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

    public ApiResult getReadHealthSummarySoonApi(String tokenID, String reportID)
    {
        String api = APIFetch.getReadHealthSummarySoonApi();

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

    public ApiResult getLastHealthSummary(String tokenID)
    {
        String api = APIFetch.getLastHealthSummaryApi();
        DownLoad httpUtils = new DownLoad();

        V7HttpResult httpResult = httpUtils.getStringFromURLByPostAddHeader(api, DownLoad.HeaderKeyName, tokenID, DownLoad.defaultContentType, null);

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
