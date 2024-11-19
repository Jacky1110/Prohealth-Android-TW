package com.v7idea.template.Tool;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by mortal on 2017/9/26.
 */

public class ApiResult {
    private final static String TAG = "ApiResult";

    private String errorNo = null;
    private String message = null;
    private String data = null;

    public static ApiResult getInstance(String resultString){
        try {
            JSONObject resultJson = new JSONObject(resultString);

            JSONObject resultData = resultJson.optJSONObject("result");

            String errorNo = resultJson.optString("errorNo");
            String message = resultJson.optString("message");
            String data = resultJson.optString("data");

            return new ApiResult(errorNo, message, data);

        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }
    }

    public ApiResult(String errorNo, String message, String data){
        this.errorNo = errorNo;
        this.message = message;
        this.data = data;
    }

    public String getErrorNo() {
        return errorNo;
    }

    public String getMessage() {

        switch (errorNo){
            case "0":
               DebugLog.d(TAG, "errorCode 0 message is: "+message);
                break;

            case "200":
                DebugLog.d(TAG, "errorCode 200 message is: "+message);
                break;

            case "401":
                DebugLog.e(TAG, "errorCode 401 message is: "+message);
            break;

            case "404":
                DebugLog.e(TAG, "errorCode 404 message is: "+message);
                break;

            case "406":
                DebugLog.e(TAG, "errorCode 406 message is: "+message);
                break;

            case "409":
                DebugLog.e(TAG, "errorCode 409 message is: "+message);
                break;

            case "422":
                DebugLog.e(TAG, "errorCode 422 message is: "+message);
                break;

            case "500":
                DebugLog.e(TAG, "errorCode 500 message is: "+message);
                break;
        }

        return message;
    }

    public String getData() {
        return data;
    }

    public JSONObject getDataJSONObject(){
        try {
            if (getData()!=null){
                return new JSONObject(getData());
            }else {
                return new JSONObject();
            }
        } catch (JSONException e) {
            e.printStackTrace();
            return new JSONObject();
        }
    }

    public JSONArray getDataJSONArray(){
        try {
            return new JSONArray(getData());
        } catch (JSONException e) {
            e.printStackTrace();
            return new JSONArray();
        }
    }

    @Override
    public String toString() {
        return "errorNo: "+errorNo+" message: "+message+" data: "+data;
    }
}
