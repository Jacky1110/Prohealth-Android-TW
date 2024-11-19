package com.v7idea.healthkit.Model;

import com.v7idea.template.Tool.ApiResult;
import com.v7idea.template.Tool.DebugLog;
import com.v7idea.template.Tool.DownLoad;
import com.v7idea.template.Tool.V7BaseNameValuePair;
import com.v7idea.template.Tool.V7HttpResult;
import com.v7idea.template.Tool.V7NameValuePair;

import java.util.ArrayList;
import java.util.List;

public class LifeNotice {
    private static final String TAG = "LifeNotice";

    public ApiResult getLifeNotice(String tokenID) {

        String strApi = APIFetch.getLifeNoticeApi();

        DownLoad downLoad = new DownLoad();

        V7HttpResult httpResult = downLoad.getStringFromURLByPostAddHeader(strApi, DownLoad.HeaderKeyName, tokenID, downLoad.defaultContentType, null);

        DebugLog.d(TAG, "httpResult code: " + httpResult.getResponseCode());
        DebugLog.d(TAG, "httpResult result: " + httpResult.getResultString());

        if (httpResult.getResponseCode() == 200) {
            String resultString = httpResult.getResultString();

            ApiResult apiResult = ApiResult.getInstance(resultString);

            return apiResult;
        }

        return new ApiResult("" + httpResult.getResponseCode(), httpResult.getResultString(), null);
    }




    public ApiResult getLifeNoticeData(String tokenID,String arry) {
        String strApi = APIFetch.getLifeNoticeReadApi();

        DownLoad downLoad = new DownLoad();
        List<V7NameValuePair> content = new ArrayList<V7NameValuePair>();
        content.add(new V7BaseNameValuePair("noticeID", arry));
        V7HttpResult httpResult = downLoad.getStringFromURLByPostAddHeader(strApi, DownLoad.HeaderKeyName, tokenID, downLoad.defaultContentType, content);

//        V7HttpResult httpResult = downLoad.getStringFromURLByPostAddHeaderAndBodyIsJsonArry(strApi, DownLoad.HeaderKeyName, tokenID, downLoad.defaultContentType,"NoticeID = "+ arry);

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

