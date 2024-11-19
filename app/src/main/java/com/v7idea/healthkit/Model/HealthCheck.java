package com.v7idea.healthkit.Model;

import com.v7idea.template.Tool.ApiResult;
import com.v7idea.template.Tool.DebugLog;
import com.v7idea.template.Tool.DownLoad;
import com.v7idea.template.Tool.V7HttpResult;

public class HealthCheck {
    private final static String TAG = "HealthCheck";

    public ApiResult getHealthCheck(String tokenID){
        String api = APIFetch.getHealthCheckApi();
        DownLoad httpUtils = new DownLoad();

        V7HttpResult httpResult = httpUtils.getStringFromURLAddHeader(api, tokenID);

        DebugLog.d(TAG, "httpResult code: " + httpResult.getResponseCode());
        DebugLog.d(TAG, "httpResult result: " + httpResult.getResultString());
        int a = httpResult.getResponseCode();
        if(httpResult.getResponseCode() == 200){
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
