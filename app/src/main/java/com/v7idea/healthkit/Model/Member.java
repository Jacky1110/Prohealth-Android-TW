package com.v7idea.healthkit.Model;

import android.util.Log;

import com.v7idea.template.DataBase.SimpleDatabase;
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

/**
 * Created by mortal on 2017/9/26.
 */

public class Member {
    private final static String TAG = "Member";

    public final static String REGIST_VALID_ID = "validID";

//    private final static String USER_ID = "userID";
//    private final static String ORG_ID = "orgID";
//    private final static String DEPARTMENT_ID = "departmentID";
//    private final static String MOBILE = "mobile";
//    private final static String EMAIL = "email";
//    private final static String FIRST_NAME = "firstName";
//    private final static String LAST_NAME = "lastName";
//    private final static String SEX = "sex";
//    private final static String BIRTHDAY = "birthday";
//    private final static String USER_PHOTO = "userPhoto";
//    private final static String BLOOD_GROUP = "bloodGroup";
//    private final static String HEIGHT = "height";
//    private final static String WEIGHT = "weight";
//    private final static String BIRTH_PLACE = "birthplace";
//    private final static String ADDRESS = "address";
//    private final static String COUNTRY_Code = "countryCode";

    public String userID = "";
    public String orgID = "";
    public String departmentID = "";
    public String mobile = "";
    public String email = "";
    public String firstName = "";
    public String lastName = "";
    public String sex = "";
    public String birthday = "";
    public String userPhoto = "";
    public String bloodGroup = "";
    public String height = "";
    public String weight = "";
    public String isTestUser = "";
    public String birthplace = "";
    public String address = "";
    public String countryCode = "";

    private SimpleDatabase mDataBase = null;

    public Member() {
        mDataBase = new SimpleDatabase(TAG);
        readData();
    }

    public void clear() {
        mDataBase.clearDataBase();
    }

    public String getUserName() {
        return firstName + lastName;
    }

    public String getUserNickName() {
        return firstName;
    }

    public String getValidID() {
        return mDataBase.getStringValueByKey(REGIST_VALID_ID, "");
    }

    public void setValidID(String strValidID) {
        mDataBase.setValueByKey(REGIST_VALID_ID, strValidID);
    }

//    public ApiResult tokenCheck(String strTokenID, String channelID){
//
//    }

    public ApiResult putMemberDataToServer(String tokenID) {
        String strApi = APIFetch.getMemberEditApi();
        DownLoad httpUtils = new DownLoad();

        ArrayList<V7NameValuePair> content = memberDataToBody();

        V7HttpResult httpResult = httpUtils.postUrlByFormData(httpUtils.HeaderKeyName, tokenID, strApi, content);

        DebugLog.d(TAG, "httpResult code: " + httpResult.getResponseCode());
        DebugLog.d(TAG, "httpResult result: " + httpResult.getResultString());

        if (httpResult.getResponseCode() == 200) {
            String resultString = httpResult.getResultString();

            return ApiResult.getInstance(resultString);
        } else {
            return new ApiResult(String.valueOf(httpResult.getResponseCode())
                    , httpResult.getResultString(), "");
        }
    }

    //原本的備份
    public ApiResult putMemberDataToServer2(String tokenID) {
        String strApi = APIFetch.getMemberEditApi();
        DownLoad httpUtils = new DownLoad();

        ArrayList<V7NameValuePair> content = memberDataToBody();

        V7HttpResult httpResult = httpUtils.getStringFromURLByPostAddHeader(strApi, DownLoad.HeaderKeyName, tokenID
                , DownLoad.formDataContentType, content);

        DebugLog.d(TAG, "httpResult code: " + httpResult.getResponseCode());
        DebugLog.d(TAG, "httpResult result: " + httpResult.getResultString());

        if (httpResult.getResponseCode() == 200) {
            String resultString = httpResult.getResultString();

            return ApiResult.getInstance(resultString);
        } else {
            return new ApiResult(String.valueOf(httpResult.getResponseCode())
                    , httpResult.getResultString(), "");
        }
    }

    public ApiResult getMemberData(String strTokenID) {

        String strApi = APIFetch.getMemberApi();

        DownLoad downLoad = new DownLoad();

        V7HttpResult httpResult = downLoad.getStringFromURLAddHeader(strApi, strTokenID);

        DebugLog.d(TAG, "httpResult code: " + httpResult.getResponseCode());
        DebugLog.d(TAG, "httpResult result: " + httpResult.getResultString());

        if (httpResult.getResponseCode() == 200) {
            String resultString = httpResult.getResultString();

            ApiResult apiResult = ApiResult.getInstance(resultString);

            JSONObject dataObject = apiResult.getDataJSONObject();

            parseData(dataObject);

            mDataBase.setValueByKey(TAG, dataObject.toString());

            return apiResult;
        }

        return new ApiResult("" + httpResult.getResponseCode(), httpResult.getResultString(), null);
    }

    public ApiResult createNewMember(String memberNo, String password, String firstName, String lastName, String email) {
        String strApi = APIFetch.getMemberApi();

        DownLoad downLoad = new DownLoad();

        List<V7NameValuePair> content = new ArrayList<V7NameValuePair>();
        content.add(new V7BaseNameValuePair("memberNo", memberNo));
        content.add(new V7BaseNameValuePair("password", password));
        content.add(new V7BaseNameValuePair("firstName", firstName));
        content.add(new V7BaseNameValuePair("lastName", lastName));
        content.add(new V7BaseNameValuePair("email", email));
//        content.add(new V7BaseNameValuePair("birthday", birthday));
//        content.add(new V7BaseNameValuePair("height", height));
//        content.add(new V7BaseNameValuePair("weight", weight));
//        content.add(new V7BaseNameValuePair("bloodtype", bloodType));


        V7HttpResult httpResult = downLoad.postUrlByFormData(strApi, content);

        DebugLog.d(TAG, "createNewMember httpResult code: " + httpResult.getResponseCode());
        DebugLog.d(TAG, "createNewMember httpResult result: " + httpResult.getResultString());

        if (httpResult.getResponseCode() == 200) {
            String resultString = httpResult.getResultString();

            ApiResult apiResult = ApiResult.getInstance(resultString);

            JSONObject dataObject = apiResult.getDataJSONObject();
            Token token = new Token();
//            String processValidID = dataObject.optString(REGIST_VALID_ID);
            String tokenID = dataObject.optString("v7idea_Token");
            DebugLog.e(TAG, "TokenID : " + tokenID);

            if (tokenID.isEmpty() == false) {
//                setValidID(processValidID);
                token.setTokenId(tokenID);
            }

            return apiResult;
        }

        return new ApiResult("" + httpResult.getResponseCode(), httpResult.getResultString(), null);
    }

    public ApiResult sendCheckCode(String tokenID, String strCheckCode) {

        String strApi = APIFetch.getCheckCodeApi() + strCheckCode;


        DownLoad downLoad = new DownLoad();

//        List<V7NameValuePair> content = new ArrayList<V7NameValuePair>();
//        content.add(new V7BaseNameValuePair("checkCode", strCheckCode));


        V7HttpResult httpResult = downLoad.getStringFromURLAddHeader(strApi, tokenID);


        DebugLog.d(TAG, "sendCheckCode httpResult code: " + httpResult.getResponseCode());
        DebugLog.d(TAG, "sendCheckCode httpResult result: " + httpResult.getResultString());

        if (httpResult.getResponseCode() == 200) {
            String resultString = httpResult.getResultString();

            ApiResult apiResult = ApiResult.getInstance(resultString);
            return apiResult;
        }

        return new ApiResult("" + httpResult.getResponseCode(), httpResult.getResultString(), null);
    }

    private ArrayList<V7NameValuePair> memberDataToBody() {
        ArrayList<V7NameValuePair> content = new ArrayList<V7NameValuePair>();
        content.add(new V7BaseNameValuePair("firstName", firstName));
        content.add(new V7BaseNameValuePair("lastName", lastName));
        content.add(new V7BaseNameValuePair("mobile", mobile));
        content.add(new V7BaseNameValuePair("email", email));
        content.add(new V7BaseNameValuePair("birthday", birthday));

        content.add(new V7BaseNameValuePair("sex", sex));
        content.add(new V7BaseNameValuePair("bloodGroup", bloodGroup));
        content.add(new V7BaseNameValuePair("height", height));
        content.add(new V7BaseNameValuePair("weight", weight));
        content.add(new V7BaseNameValuePair("birthplace", birthplace));

        content.add(new V7BaseNameValuePair("address", address));
        content.add(new V7BaseNameValuePair("userPhoto", userPhoto));
        content.add(new V7BaseNameValuePair("countryCode", countryCode));

        return content;
    }

    private void readData() {
        String memberDataString = mDataBase.getStringValueByKey(TAG, "");

        if (memberDataString.isEmpty() == false) {
            try {
                JSONObject jsonObject = new JSONObject(memberDataString);
                parseData(jsonObject);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    private void parseData(JSONObject apiData) {
        userID = apiData.optString("userID");
        orgID = apiData.optString("orgID");
        departmentID = apiData.optString("departmentID");
        mobile = apiData.optString("mobile");
        email = apiData.optString("email");
        firstName = apiData.optString("firstName");
        lastName = apiData.optString("lastName");
        sex = apiData.optString("sex");
        birthday = apiData.optString("birthday");
        userPhoto = apiData.optString("userPhoto");
        bloodGroup = apiData.optString("bloodGroup");
        height = apiData.optString("height");
        weight = apiData.optString("weight");
        birthplace = apiData.optString("birthplace");
        isTestUser = apiData.optString("isTestUser");
        address = apiData.optString("address");
        countryCode = apiData.optString("countryCode");
    }
}
