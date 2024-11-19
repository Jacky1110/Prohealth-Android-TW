package com.v7idea.template.ThirdPartyLogin;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.v7idea.template.DataBase.SimpleDatabase;

import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

/**
 * Created by mortal on 16/1/28.
 */
public class FaceBookLoginManager
{
    private final String TAG = "FaceBookLoginManager";

    private Activity CurrentActivity = null;

    private CallbackManager callbackManager = null;
    private LoginManager ThisLoginManager = null;
    private GraphRequest.GraphJSONObjectCallback GraphJSONObjectCallback = null;

    private Bundle RequestField = null;

    private SignSuccess AfterSignSuccess = null;

    public void setAfterSignSuccess(SignSuccess interfaceSignSuccess)
    {
        AfterSignSuccess = interfaceSignSuccess;
    }

    public void setRequestField(Bundle RequestField)
    {
        this.RequestField = RequestField;
    }

    public static FaceBookLoginManager getInstance(Activity CurrentActivity)
    {
        FaceBookLoginManager NewInstance = new FaceBookLoginManager(CurrentActivity);
        NewInstance.setDefaultFaceBookLoginResultCallback();

        return NewInstance;
    }

    public FaceBookLoginManager(Activity CurrentActivity)
    {
        this.CurrentActivity = CurrentActivity;

        FacebookSdk.sdkInitialize(this.CurrentActivity.getApplicationContext());
        callbackManager = CallbackManager.Factory.create();
        ThisLoginManager = LoginManager.getInstance();
    }

    public void setGraphJSONObjectCallback(GraphRequest.GraphJSONObjectCallback GraphJSONObjectCallback)
    {
        this.GraphJSONObjectCallback = GraphJSONObjectCallback;
    }

    public void setDefaultFaceBookLoginResultCallback()
    {
        ThisLoginManager.registerCallback(callbackManager, FaceBookLoginResultCallback);
    }

    public void setFaceBookLoginResultCallback(FacebookCallback<LoginResult> FaceBookLoginResultCallback)
    {
        ThisLoginManager.registerCallback(callbackManager, FaceBookLoginResultCallback);
    }

    /**
     * 有關要求權限的設定，請參閱 https://developers.facebook.com/docs/facebook-login/android/permissions
     * @param FaceBookRequestPermission
     */
    public void loginFaceBook(String... FaceBookRequestPermission)
    {
        List<String> listFaceBookRequestPermission = Arrays.asList(FaceBookRequestPermission);

        ThisLoginManager.logInWithReadPermissions(CurrentActivity, listFaceBookRequestPermission);
    }

    public void loginFaceBook()
    {
        loginFaceBook("public_profile", "email");
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        if(callbackManager != null)
        {
            callbackManager.onActivityResult(requestCode, resultCode, data);
        }
    }

    private FacebookCallback<LoginResult> FaceBookLoginResultCallback = new FacebookCallback<LoginResult>(){
        @Override
        public void onSuccess(LoginResult loginResult) {

            AccessToken getFaceBookToken = loginResult.getAccessToken();

            if(getFaceBookToken != null)
            {
                String strUserID = getFaceBookToken.getUserId();
                String strUserFaceBookToken = getFaceBookToken.getToken();
                boolean booleanIsTokenExpire = getFaceBookToken.isExpired();
                String strAppID = getFaceBookToken.getApplicationId();
                Date DateExpiresDate = getFaceBookToken.getExpires();
                Date DateLastRefreshDate = getFaceBookToken.getLastRefresh();

                SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

                SimpleDatabase simpleDatabase = new SimpleDatabase();
                simpleDatabase.setValueByKey("CommunitySoftwareToken", strUserFaceBookToken);

//                Log.d(TAG, "strUserID: " + strUserID);
//                Log.d(TAG, "strUserFaceBookToken: " + strUserFaceBookToken);
//                Log.d(TAG, "booleanIsTokenExpire: " + booleanIsTokenExpire);
//                Log.d(TAG, "strAppID: " +strAppID);
//                Log.d(TAG, "DateExpiresDate: " + dateFormat.format(DateExpiresDate));
//                Log.d(TAG, "DateLastRefreshDate" + dateFormat.format(DateLastRefreshDate));

                //包入你想要得到的資料 送出request
                if(RequestField == null)
                {
                    RequestField = new Bundle();
                    RequestField.putString("fields", "id,name,link,gender,email,age_range,picture");
                }

                GraphRequest.GraphJSONObjectCallback WitchGraphJSONObjectCallback = null;

                if(GraphJSONObjectCallback == null)
                {
                    WitchGraphJSONObjectCallback = DefaultGraphJSONObjectCallBack;
                }
                else
                {
                    WitchGraphJSONObjectCallback = GraphJSONObjectCallback;
                }

                GraphRequest request = GraphRequest.newMeRequest(getFaceBookToken, WitchGraphJSONObjectCallback);
                request.setParameters(RequestField);
                request.executeAsync();
            }
        }

        @Override
        public void onCancel() {
            Log.d(TAG, "Login cancel");
        }

        @Override
        public void onError(FacebookException error) {
            Log.d(TAG, "error: "+error.toString());
        }
    };

    private GraphRequest.GraphJSONObjectCallback DefaultGraphJSONObjectCallBack = new GraphRequest.GraphJSONObjectCallback(){
        @Override
        public void onCompleted(JSONObject object, GraphResponse response) {
            //讀出姓名 ID FB個人頁面連結

            if(object != null)
            {
                String strUserAccountID = object.optString("id");

                String name = object.optString("name");
                String gender = object.optString("gender");
                String email = object.optString("email");

                Log.d(TAG, "complete");
                Log.d(TAG, "name: " + name);
                Log.d(TAG, "link: " + object.optString("link"));
                Log.d(TAG, "id: " + object.optString("id"));
                Log.d(TAG, "gender: " + gender);
                Log.d(TAG, "email: " + email);

                String userPhoto = "http://graph.facebook.com/"+strUserAccountID+"/picture?type=large&width=200&height=200";

                SimpleDatabase simpleDatabase = new SimpleDatabase();
                simpleDatabase.setValueByKey("CommunitySoftwareID", "FBID:" + strUserAccountID);
                simpleDatabase.setValueByKey("CommunitySoftwareName", name);
                simpleDatabase.setValueByKey("CommunitySoftwareGender", gender);
                simpleDatabase.setValueByKey("CommunitySoftwareEmail", email);
                simpleDatabase.setValueByKey("CommunitySoftwareUserPhoto", userPhoto);

                if(AfterSignSuccess != null)
                {
                    AfterSignSuccess.afterSignSuccess("fb", strUserAccountID);
                }
            }
            else
            {
                Toast.makeText(CurrentActivity, "目前FaceBook無法提供資料", Toast.LENGTH_SHORT).show();
            }

//            LoginByFaceBookID LoginByFaceBookID = new LoginByFaceBookID(strUserAccountID);
//            LoginByFaceBookID.execute("");
        }
    };

    public interface SignSuccess
    {
        void afterSignSuccess(String strCommunitySoftware, String strCommunitySoftwareID);
    }
}
