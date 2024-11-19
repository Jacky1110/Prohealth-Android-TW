package com.v7idea.healthkit;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Paint;
import android.graphics.Rect;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.appevents.AppEventsLogger;
import com.v7idea.healthkit.Model.CheckCode;
import com.v7idea.healthkit.Model.Member;
import com.v7idea.healthkit.Model.Token;
import com.v7idea.healthkit.View.BottomButton;
import com.v7idea.template.Activity.BaseActivity;
import com.v7idea.template.AirApplication;
import com.v7idea.template.DataBase.SimpleDatabase;
import com.v7idea.template.ThirdPartyLogin.FaceBookLoginManager;
import com.v7idea.template.ThirdPartyLogin.GooglePlusSignInManager;
import com.v7idea.template.Tool.ApiResult;
import com.v7idea.template.Tool.DownLoad;
import com.v7idea.template.Tool.NotifyDialog;
import com.v7idea.template.Tool.V7ideaAsyncTask;
import com.v7idea.template.Tool.ViewScaling;
import com.v7idea.template.View.AutoReleaseImageView;
import com.v7idea.template.View.LockScrollView;
import com.v7idea.template.View.V7TitleView;

/**
 * 2019/1/24 ＊確認使用的頁面 登入頁
 */
/**
 * FIXME 1.沒有第三方登入用的API
 */
public class LoginPage extends BaseActivity {
    private final static String TAG = "LoginPage";

    private GooglePlusSignInManager PlusSignInManager = null;
    private FaceBookLoginManager ThisFaceBookLoginManager = null;
    private SimpleDatabase simpleDatabase = null;

    private EditText inputAccount = null;
    private EditText inputPassword = null;

    private boolean ifRequestToServer = false;

    private View view = null;
    private ScrollView ScrollArea = null;

    private TextView forgetPassword = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        view = getLayoutInflater().inflate(R.layout.activity_login_page, null);

        setContentView(view);

        AndroidBug5497Workaround.assistActivity(this);

        simpleDatabase = new SimpleDatabase();

        RelativeLayout baseLayout = (RelativeLayout) ViewScaling.findViewByIdAndScale(currentActivity, R.id.loginPage_RelativeLayout_BaseLayout);
        ScrollArea = (ScrollView) ViewScaling.findViewByIdAndScale(currentActivity, R.id.loginPage_LockScrollView_ScrollArea);
//        ScrollArea.setScrollingEnabled(true);
//        ScrollArea.setOnTouchListener(onTouchBlank);

        RelativeLayout ScrollContainer = (RelativeLayout) ViewScaling.findViewByIdAndScale(currentActivity, R.id.loginPage_RelativeLayout_ScrollContainer);

        AutoReleaseImageView SpecialImageView_Background = (AutoReleaseImageView) ViewScaling.findViewByIdAndScale(currentActivity, R.id.loginPage_SpecialImageView_Background);

        AutoReleaseImageView TopBannerBackground = (AutoReleaseImageView) ViewScaling.findViewByIdAndScale(currentActivity, R.id.loginPage_SpecialImageView_TopBannerBackground);
        ViewScaling.setImageView(TopBannerBackground);

        V7TitleView TopBannerTitle = (V7TitleView) ViewScaling.findViewByIdAndScale(currentActivity, R.id.loginPage_V7TitleView_TopBannerTitle);

        inputAccount = (EditText) ViewScaling.findViewByIdAndScale(currentActivity, R.id.loginPage_EditText_inputAccount);
        AutoReleaseImageView AccountFieldIcon = (AutoReleaseImageView) ViewScaling.findViewByIdAndScale(currentActivity, R.id.loginPage_SpecialImageView_AccountFieldIcon);

        inputPassword = (EditText) ViewScaling.findViewByIdAndScale(currentActivity, R.id.loginPage_EditText_inputPassword);
        AutoReleaseImageView PasswordFieldIcon = (AutoReleaseImageView) ViewScaling.findViewByIdAndScale(currentActivity, R.id.loginPage_SpecialImageView_PasswordFieldIcon);

        inputPassword.setOnEditorActionListener(onEditorActionListener);


        BottomButton BottomButton = (BottomButton) ViewScaling.findViewByIdAndScale(currentActivity, R.id.loginPage_BottomButton);
        BottomButton.setData(getResources().getString(R.string.LoginPage_Text_Login), getResources().getString(R.string.LoginPage_Text_Register), 100);

        V7TitleView login = BottomButton.getLeftButton();
        login.setOnClickListener(OnPressLogin);

        V7TitleView Register = BottomButton.getRightButton();
        Register.setOnClickListener(onPressRegister);

//FB登入
//        ThisFaceBookLoginManager = FaceBookLoginManager.getInstance(this);


        forgetPassword = (TextView) ViewScaling.findViewByIdAndScale(currentActivity, R.id.loginPage_forget_password_textView);
        forgetPassword.getPaint().setFlags(Paint.UNDERLINE_TEXT_FLAG);//下划線
        forgetPassword.getPaint().setAntiAlias(true);
        forgetPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AirApplication.setOnclikcFeedback(v);
                Intent intent = new Intent(currentActivity, ForgotPasswordPage.class);
                startActivity(intent);
                finish();
                setTurnInNextPageAnimation(currentActivity);
            }
        });

        //Google Plus 登入
//        PlusSignInManager = new GooglePlusSignInManager();
//        PlusSignInManager.init(this);
//        PlusSignInManager.signOut();

//        view.getViewTreeObserver().addOnGlobalLayoutListener(softWareKeyBoardWatcher);


        AirApplication.setOnclikcFeedbackData(true);


        if (getIntent()!=null)
        {
            if (getIntent().getExtras() != null) {
                boolean internet_can_not_use = getIntent().getExtras().getBoolean(getResources().getString(R.string.no_internet_can_not_use), false);
                if (internet_can_not_use) {
                    isConnectedNetWork();
                }
            }
        }


    }

    @Override
    protected void onResume() {
        super.onResume();
        AppEventsLogger.activateApp(this);
    }

    private View.OnClickListener onPressRegister = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            AirApplication.setOnclikcFeedback(v);
            Intent intent = new Intent(currentActivity, TermsOfServicePage.class);
            startActivity(intent);
            currentActivity.finish();
            setTurnInNextPageAnimation(currentActivity);
        }
    };


    @Override
    protected void onActivityResult(final int requestCode, final int resultCode, final Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        ThisFaceBookLoginManager.onActivityResult(requestCode, resultCode, data);
        PlusSignInManager.inActivityResult(requestCode, data);
    }

    private GooglePlusSignInManager.SignSuccess AfterSignSuccess = new GooglePlusSignInManager.SignSuccess() {
        @Override
        public void afterSignSuccess(String strCommunitySoftware, String strCommunitySoftwareID) {
            String strAccessToken = simpleDatabase.getStringValueByKey("GooglePlusAccessTokenToken", "");
            LoginByCommunitySoftwareID LoginByCommunitySoftwareID = new LoginByCommunitySoftwareID(strCommunitySoftware, strCommunitySoftwareID);
            LoginByCommunitySoftwareID.execute(strAccessToken);
        }
    };

    private FaceBookLoginManager.SignSuccess AfterFaceBookSignSuccess = new FaceBookLoginManager.SignSuccess() {
        @Override
        public void afterSignSuccess(String strCommunitySoftware, String strCommunitySoftwareID) {
            String strAccessToken = simpleDatabase.getStringValueByKey("CommunitySoftwareToken", "");
            LoginByCommunitySoftwareID LoginByCommunitySoftwareID = new LoginByCommunitySoftwareID(strCommunitySoftware, strCommunitySoftwareID);
            LoginByCommunitySoftwareID.execute(strAccessToken);
        }
    };

    private EditText.OnEditorActionListener onEditorActionListener = new EditText.OnEditorActionListener() {
        @Override
        public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {

            if (actionId == EditorInfo.IME_ACTION_SEARCH
                    || actionId == EditorInfo.IME_ACTION_DONE
                    || event.getAction() == KeyEvent.ACTION_DOWN
                    && event.getKeyCode() == KeyEvent.KEYCODE_ENTER) {

                login();

                return true;
            }

            return false;
        }
    };

    private View.OnClickListener OnPressLogin = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            AirApplication.setOnclikcFeedback(v);
            login();
        }
    };

    private void login() {
        if (!ifRequestToServer) {
            ifRequestToServer = true;

            if (DownLoad.isConnectInternet()) {
                String userAccount = inputAccount.getText().toString();
                String userPassword = inputPassword.getText().toString();

                boolean isPhoneNumberEmpty = true;
                boolean isPasswordEmpty = true;

                if (AirApplication.isStringNotNullAndEmpty(userAccount)) {
                    isPhoneNumberEmpty = false;
                }

                if (AirApplication.isStringNotNullAndEmpty(userPassword)) {
                    isPasswordEmpty = false;
                }

                if (isPhoneNumberEmpty) {
                    Toast.makeText(LoginPage.this, getResources().getString(R.string.phone_number_empty), Toast.LENGTH_SHORT).show();
                } else if (isPasswordEmpty) {
                    Toast.makeText(LoginPage.this, getResources().getString(R.string.password_empty), Toast.LENGTH_SHORT).show();
                }

                if (isPasswordEmpty == false && isPhoneNumberEmpty == false) {
                    simpleDatabase.setValueByKey(Constant.TEMP_ACCOUNT, userAccount);
                    simpleDatabase.setValueByKey(Constant.TEMP_PASSWORD, userPassword);

                    LoginSystem login = new LoginSystem();
                    login.execute(userAccount, userPassword);
                } else {
                    ifRequestToServer = false;
                }
            } else {
                ifRequestToServer = false;

                showErrorAlert(getResources().getString(R.string.no_internet_can_not_login));
            }
        }
    }

    private class LoginSystem extends V7ideaAsyncTask<String, ApiResult> {

        private NotifyDialog progress = null;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            progress = new NotifyDialog(currentActivity);
            progress.settingProgressDialog();
        }

        @Override
        public void ifNeedCloseSomeThing() {
            if (progress != null) {
                progress.dismiss();
                progress = null;
            }
            ifRequestToServer = false;
        }

        @Override
        public void downLoadSuccess(ApiResult result) {
            Intent intent = new Intent(currentActivity, MainActivity.class);
            startActivity(intent);
            finish();

        }

        @Override
        public void downLoadFail(ApiResult apiResult) {
            if (apiResult.getErrorNo().contentEquals(Constant.ErrorCode.CODE_500)) {
                showErrorAlert(getResources().getString(R.string.no_internet_error500));
            } else {
                if (apiResult.getErrorNo().contentEquals("VERIFY")) {
                    Intent intent = new Intent(currentActivity, CheckCodePage.class);
                    startActivity(intent);
                    finish();

                } else if (apiResult.getErrorNo().contentEquals("1100")) {
                    AlertDialog.Builder alertBuilder = new AlertDialog.Builder(currentActivity);
                    alertBuilder.setMessage(apiResult.getMessage());
                    alertBuilder.setPositiveButton("前往註冊", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            Intent intent = new Intent(currentActivity, TermsOfServicePage.class);
                            startActivity(intent);
                            finish();
                        }
                    });

                    alertBuilder.setNegativeButton("重新登入", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                        }
                    });

                    alertBuilder.setCancelable(false);
                    alertBuilder.show();
                } else {
                    showErrorAlert(apiResult.getMessage());
                }
            }
        }

        @Override
        protected ApiResult doInBackground(String... params) {
            if (params != null && params.length > 0) {

                Token token = new Token();
                ApiResult tokenResult = token.getTokenID(params[0], params[1]);

                if (tokenResult.getErrorNo().contentEquals(Constant.ErrorCode.SUCCESS)) {
                    Member member = new Member();
                    return member.getMemberData(token.getTokenId());
                } else {
                    return new ApiResult(tokenResult.getErrorNo(), tokenResult.getMessage(), tokenResult.getData());
                }
            }
            return null;
        }
    }

    public class LoginByCommunitySoftwareID extends AsyncTask<String, String, String> {
        private String CommunitySoftwareTag = null;
        private String CommunitySoftwareID = null;
        private Member currentMember = null;
        private String ErrorMessage = null;

        public LoginByCommunitySoftwareID(String strCommunitySoftwareTag, String strCommunitySoftwareID) {
            this.CommunitySoftwareTag = strCommunitySoftwareTag;
            this.CommunitySoftwareID = strCommunitySoftwareID;
        }

        @Override
        protected String doInBackground(String... params) {
//            String strGCMID = simpleDatabase.getStringValueByKey(SimpleDatabase.GCMRegisterID, "");
//
//            APIFetch apiFetch = new APIFetch();
//
//            String api = apiFetch.getLoginByCommunitySoftware(CommunitySoftwareID, CommunitySoftwareTag, strGCMID);
//
//            DownLoad downLoad = new DownLoad();
//
////            Log.e(tag, "access token: " + params[0]);
//
//            String resultString = downLoad.getStringFromURLAddHeader(api, "communityAccessToken", params[0]);
//
////            Log.e(tag, "resultString: " + resultString);
//
//            //{"result":{"errorNo":"0","message":"","data":""}}
//
//            try {
//                JSONObject resultJSON = new JSONObject(resultString);
//
//                JSONObject resultData = resultJSON.optJSONObject("result");
//
//                String errorNo = resultData.optString("errorNo");
//
//                ErrorMessage = resultData.optString("message");
//
//                if(errorNo.contentEquals(DownLoad.DOWNLOAD_SUCCESS))
//                {
//                    //"data":{"processTicketID":"a24b1ee3-60e9-4011-aa4c-05c3e306c3b2"}
//                    JSONObject TokenData = resultData.optJSONObject("data");
//
//                    String strTokenID = TokenData.optString("processTicketID");
//
//                    if(!strTokenID.isEmpty())
//                    {
//                        String getUserDetailApi = apiFetch.getUserDetailDataApi();
//
//                        String resultUserDetailString = downLoad.getStringFromURLAddHeader(getUserDetailApi, strTokenID);
//
////                        Log.d("ActivityLoginPage_LoginSystem", "resultString: " + resultString);
//
//                        String status = null;
//
//                        try {
//                            JSONObject resultUserDetailJSON = new JSONObject(resultUserDetailString);
//
//                            JSONObject resultUserDetail = resultUserDetailJSON.optJSONObject("result");
//
//                            status = resultUserDetail.optString("errorNo");
//
////                            Log.d("ActivityLoginPage_LoginSystem", "status: " + status);
//
//                            if (status.contentEquals(DownLoad.DOWNLOAD_SUCCESS)) {
//                                JSONObject UserPermissionData = resultUserDetail.optJSONObject("data");
//
//                                //解析成	UserPermission物件
//                                CurrentUserPermission = new UserPermission(UserPermissionData);
//                                CurrentUserPermission.setProcessTicketID(strTokenID);
//                            }
//
//                            return status;
//                        } catch (JSONException e) {
//                            e.printStackTrace();
//
//                            return resultString;
//                        }
//                    }
//                }
//
//                return errorNo;
//            } catch (JSONException e) {
//                e.printStackTrace();
//
//                return resultString;
//            }

            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);

//            if(LoginPage.this != null && !LoginPage.this.isFinishing())
//            {
//                if(s.contentEquals(DownLoad.DOWNLOAD_SUCCESS))
//                {
//                    loginSuccess(CurrentUserPermission);
//                }
//                else if(s.contentEquals("1100"))
//                {
//                    Intent intent = new Intent(LoginPage.this, TermsOfServicePage.class);
//                    LoginPage.this.startActivity(intent);
//                    LoginPage.this.finish();
//                    Air.setTurnInNextPageAnimation(LoginPage.this);
//                }
//                else
//                {
//                    if(MessageDialog == null)
//                    {
//                        MessageDialog = new NotifyDialog(LoginPage.this);
//                        MessageDialog.initAlertStyleTwo((ErrorMessage));
//                        MessageDialog.setAgreeListener(new View.OnClickListener() {
//                            @Override
//                            public void onClick(View v) {
//                                AirApplication.setOnclikcFeedback(v);
//                                if(MessageDialog != null)
//                                {
//                                    MessageDialog.dismiss();
//                                    MessageDialog = null;
//                                }
//                            }
//                        });
//
//                        MessageDialog.show();
//                    }
//                }
//            }
        }
    }


}
