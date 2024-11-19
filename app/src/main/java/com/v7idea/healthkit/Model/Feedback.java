package com.v7idea.healthkit.Model;

import com.v7idea.template.Tool.ApiResult;
import com.v7idea.template.Tool.DebugLog;
import com.v7idea.template.Tool.DownLoad;
import com.v7idea.template.Tool.V7BaseNameValuePair;
import com.v7idea.template.Tool.V7HttpResult;
import com.v7idea.template.Tool.V7NameValuePair;

import java.util.ArrayList;
import java.util.List;

public class Feedback {
    private final static String TAG = "Feedback";

    public ApiResult getFeedbackType(String tokenID) {
        String api = APIFetch.getFeedbackTypeApi();
        DownLoad httpUtils = new DownLoad();

        V7HttpResult httpResult = httpUtils.getStringFromURLByPostAddHeader(api, DownLoad.HeaderKeyName, tokenID
                , DownLoad.formDataContentType, null);

        DebugLog.d(TAG, "httpResult code: " + httpResult.getResponseCode());
        DebugLog.d(TAG, "httpResult result: " + httpResult.getResultString());


        if (httpResult.getResponseCode() == 200) {
            String resultString = httpResult.getResultString();

            ApiResult apiResult = ApiResult.getInstance(resultString);

            return apiResult;
        } else

        {
            return new ApiResult(String.valueOf(httpResult.getResponseCode())
                    , httpResult.getResultString(), "");
        }
    }

    public ApiResult getFeedbackData(String tokenID, String typeID, String feedback) {
        String api = APIFetch.getFeedbackApi();
        DownLoad httpUtils = new DownLoad();

        List<V7NameValuePair> content = new ArrayList<V7NameValuePair>();
        content.add(new V7BaseNameValuePair("typeID", typeID));
        content.add(new V7BaseNameValuePair("feedback", feedback));

        V7HttpResult httpResult = httpUtils.postUrlByFormData(DownLoad.HeaderKeyName, tokenID, api, content);

        DebugLog.d(TAG, "httpResult code: " + httpResult.getResponseCode());
        DebugLog.d(TAG, "httpResult result: " + httpResult.getResultString());
        int a = httpResult.getResponseCode();
        if (httpResult.getResponseCode() == 200) {
            String resultString = httpResult.getResultString();

            ApiResult apiResult = ApiResult.getInstance(resultString);

            return apiResult;
        } else {
            return new ApiResult(String.valueOf(httpResult.getResponseCode())
                    , httpResult.getResultString(), "");
        }
    }


}
