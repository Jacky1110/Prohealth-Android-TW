package com.v7idea.healthkit.Model;

import android.util.Log;

import com.v7idea.template.AirApplication;
import com.v7idea.template.Tool.ApiResult;
import com.v7idea.template.Tool.DebugLog;
import com.v7idea.template.Tool.DownLoad;
import com.v7idea.template.Tool.V7BaseNameValuePair;
import com.v7idea.template.Tool.V7HttpResult;
import com.v7idea.template.Tool.V7NameValuePair;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class ForgetPassword {
    private final static String TAG = "ForgetPassword";
    Member member = new Member();

    //取得驗證碼
    public ApiResult getCheckCodeForPassword(String phoneNum) {

        String strApi = APIFetch.getCheckCodeForPasswordApi();

        DownLoad downLoad = new DownLoad();

        List<V7NameValuePair> content = new ArrayList<V7NameValuePair>();
        content.add(new V7BaseNameValuePair("memberNo", phoneNum));

//        V7HttpResult httpResult = downLoad.getStringFromURLByPostAddHeader(strApi, content);
        V7HttpResult httpResult = downLoad.getStringFromURLByPostAddHeader(strApi, "memberNo", phoneNum, downLoad.defaultContentType, null);
        DebugLog.d(TAG, "httpResult code: " + httpResult.getResponseCode());
        DebugLog.d(TAG, "httpResult result: " + httpResult.getResultString());

        if (httpResult.getResponseCode() == 200) {
            String resultString = httpResult.getResultString();

            ApiResult apiResult = ApiResult.getInstance(resultString);

            if (apiResult.getErrorNo().contentEquals("0000")) {
                try {
                    JSONObject jsonObject = new JSONObject(resultString);
                    JSONObject resultData = jsonObject.optJSONObject("data");
                    member.setValidID(resultData.optString("validID"));
                    member.mobile = phoneNum;
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            return apiResult;
        }

        return new ApiResult("" + httpResult.getResponseCode(), httpResult.getResultString(), null);
    }

    //檢查驗證碼
    public ApiResult getCheckCodeStatus(String checkCode) {

        String strApi = APIFetch.getCheckCodeForPasswordApi();

        DownLoad downLoad = new DownLoad();

        ArrayList<V7NameValuePair> bodyData = new ArrayList<V7NameValuePair>();
        bodyData.add(new V7BaseNameValuePair("validID", member.getValidID()));
        bodyData.add(new V7BaseNameValuePair("checkCode", checkCode));

        V7HttpResult httpResult = downLoad.getStringFromURLByPostAddHeader(strApi, null, null, downLoad.defaultContentType, bodyData);

        DebugLog.d(TAG, "httpResult code: " + httpResult.getResponseCode());
        DebugLog.d(TAG, "httpResult result: " + httpResult.getResultString());

        if (httpResult.getResponseCode() == 200) {
            String resultString = httpResult.getResultString();

            ApiResult apiResult = ApiResult.getInstance(resultString);

            if (apiResult.getErrorNo().contentEquals("0000")) {
                try {
                    JSONObject jsonObject = new JSONObject(resultString);
                    JSONObject resultData = jsonObject.optJSONObject("data");
                    member.setValidID(resultData.optString("validID", ""));
                    AirApplication.checkCode = checkCode;
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            return apiResult;
        }

        return new ApiResult("" + httpResult.getResponseCode(), httpResult.getResultString(), null);
    }

    //設定新密碼
    public ApiResult setNewPassword(String newPassword) {

        String strApi = APIFetch.getCheckCodeForPasswordApi();

        DownLoad downLoad = new DownLoad();

        ArrayList<V7NameValuePair> bodyData = new ArrayList<V7NameValuePair>();
        bodyData.add(new V7BaseNameValuePair("validID", member.getValidID()));
        bodyData.add(new V7BaseNameValuePair("checkCode", AirApplication.checkCode));
        bodyData.add(new V7BaseNameValuePair("newPassword", newPassword));

        V7HttpResult httpResult = downLoad.getStringFromURLByPostAddHeader(strApi, null, null, downLoad.defaultContentType, bodyData);

        DebugLog.d(TAG, "httpResult code: " + httpResult.getResponseCode());
        DebugLog.d(TAG, "httpResult result: " + httpResult.getResultString());

        if (httpResult.getResponseCode() == 200) {
            String resultString = httpResult.getResultString();

            ApiResult apiResult = ApiResult.getInstance(resultString);

            return apiResult;
        }

        return new ApiResult("" + httpResult.getResponseCode(), httpResult.getResultString(), null);
    }

}
