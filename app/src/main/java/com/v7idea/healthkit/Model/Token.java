package com.v7idea.healthkit.Model;

import com.v7idea.healthkit.Constant;
import com.v7idea.template.AirApplication;
import com.v7idea.template.DataBase.SimpleDatabase;
import com.v7idea.template.Tool.ApiResult;
import com.v7idea.template.Tool.DebugLog;
import com.v7idea.template.Tool.DownLoad;
import com.v7idea.template.Tool.Utils;
import com.v7idea.template.Tool.V7BaseNameValuePair;
import com.v7idea.template.Tool.V7HttpResult;
import com.v7idea.template.Tool.V7NameValuePair;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by mortal on 15/3/12.
 */
public class Token
{
    private final static String TAG = "Token";
    private final static String PROCESS_TICKET_ID = "v7idea_Token";

    private SimpleDatabase mDataBase = null;

    public Token()
    {
        mDataBase = new SimpleDatabase(TAG);
    }

    public void setTokenId(String tokenId)
    {
        mDataBase.setValueByKey(Constant.TOKEN_ID, tokenId);
    }

    public String getTokenId()
    {
        return mDataBase.getStringValueByKey(Constant.TOKEN_ID, "");
    }

    public void destroy(){
        setTokenId("");
//        AlarmManagerHelper.cancelAlarmTime();
        SimpleDatabase simpleDatabase = new SimpleDatabase();
        simpleDatabase.setValueByKey(Constant.HEALTH_REPORT, 0);
        simpleDatabase.setValueByKey(Constant.IMMEDIATE_IMPROVEMENT, 0);
        simpleDatabase.clearDataBase();
    }

    public ApiResult getTokenID(String strAccount, String strPassword){

        String strApi = APIFetch.getTokenApi();

        DownLoad downLoad = new DownLoad();
        SimpleDatabase simpleDatabase = new SimpleDatabase();

        String strDevcieInfo = Utils.getDeviceInfor();
        String strGCMRegisterID = simpleDatabase.getStringValueByKey(Constant.GCMRegisterID, "");

        List<V7NameValuePair> content = new ArrayList<V7NameValuePair>();
        content.add(new V7BaseNameValuePair("account", strAccount));
        content.add(new V7BaseNameValuePair("password", Utils.getStringBase64Data(strPassword)));
        content.add(new V7BaseNameValuePair("channelID", strGCMRegisterID ));
        content.add(new V7BaseNameValuePair("deviceInfo", strDevcieInfo));
        content.add(new V7BaseNameValuePair("CountryCode", "TW"));

        V7HttpResult httpResult = downLoad.getStringFromURLByPostAddHeader(strApi, content);

        DebugLog.d(TAG, "httpResult code: " + httpResult.getResponseCode());
        DebugLog.d(TAG, "httpResult result: " + httpResult.getResultString());

        if(httpResult.getResponseCode() == 200){
            String resultString = httpResult.getResultString();

            ApiResult apiResult = ApiResult.getInstance(resultString);

            JSONObject dataObject = apiResult.getDataJSONObject();

            String processTicketID = dataObject.optString(PROCESS_TICKET_ID);

            if(processTicketID.contentEquals("null")) processTicketID = "";
            if(processTicketID.isEmpty() == false){
                setTokenId(processTicketID);
            }

            return apiResult;
        }

        return new ApiResult("" + httpResult.getResponseCode(), httpResult.getResultString(), null);
    }

    public ApiResult deleteTokenID()
    {
        String strApi = APIFetch.getTokenApi();
        String currentTokenID = getTokenId();

        DownLoad httpUtils = new DownLoad();

        V7HttpResult v7HttpResult = httpUtils.getStringFromUrlByDelete(strApi, currentTokenID);

        DebugLog.d(Token.TAG, "httpResult code: "+v7HttpResult.getResponseCode());

        DebugLog.d(Token.TAG, "httpResult result: "+v7HttpResult.getResultString());

        if(v7HttpResult.getResponseCode() == 200){
            String resultString = v7HttpResult.getResultString();

            return ApiResult.getInstance(resultString);
        }
        else
        {
            ApiResult apiResult = new ApiResult(""+v7HttpResult.getResponseCode(), v7HttpResult.getResultString(), "");

            return apiResult;
        }
    }

    public ApiResult updateDeviceToken(String strToken, String fcmTokenID){
        String strApi = APIFetch.getUpdateTokenApi();
        String strDevcieInfo = Utils.getDeviceInfor();

        DownLoad httpUtils = new DownLoad();

        ArrayList<V7NameValuePair> content = new ArrayList<V7NameValuePair>();
        content.add(new V7BaseNameValuePair("ChannelID", fcmTokenID));
        content.add(new V7BaseNameValuePair("DeviceInfo", strDevcieInfo));

        DebugLog.e(TAG, "post content: "+content.toString());

        V7HttpResult v7HttpResult = httpUtils.getStringFromURLByPostAddHeader(strApi, DownLoad.HeaderKeyName, strToken, DownLoad.defaultContentType, content);

        DebugLog.e(TAG, "v7HttpResult: "+v7HttpResult.getResultString());

        if(v7HttpResult.getResponseCode() == 200){
            String resultString = v7HttpResult.getResultString();

            return ApiResult.getInstance(resultString);
        }
        else
        {
            ApiResult apiResult = new ApiResult(""+v7HttpResult.getResponseCode(), v7HttpResult.getResultString(), "");

            return apiResult;
        }
    }
}
