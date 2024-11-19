package com.v7idea.healthkit;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.v7idea.healthkit.Model.ForgetPassword;
import com.v7idea.healthkit.View.BottomButton;
import com.v7idea.template.Activity.BaseActivity;
import com.v7idea.template.AirApplication;
import com.v7idea.template.Tool.ApiResult;
import com.v7idea.template.Tool.NotifyDialog;
import com.v7idea.template.Tool.V7ideaAsyncTask;
import com.v7idea.template.Tool.ViewScaling;
import com.v7idea.template.View.AutoReleaseImageView;
import com.v7idea.template.View.V7TitleView;
/**
 * 2019/1/24 ＊確認使用的頁面 忘記密碼-輸入手機號碼
 */
public class ForgotPasswordPage extends BaseActivity {
    private final static String TAG = "ForgotPasswordPage";

    private EditText inputAccount;
    private V7TitleView V7TitleView_Cancel;
    private V7TitleView V7TitleView_Send;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forget_password_page);

        RelativeLayout BaseLayout = (RelativeLayout) ViewScaling.findViewByIdAndScale(currentActivity, R.id.ForgotPasswordPage_RelativeLayout_BaseLayout);
        AutoReleaseImageView SpecialImageView_Background = (AutoReleaseImageView) ViewScaling.findViewByIdAndScale(currentActivity, R.id.ForgotPasswordPage_SpecialImageView_Background);
        RelativeLayout Container = (RelativeLayout) ViewScaling.findViewByIdAndScale(currentActivity, R.id.ForgotPasswordPage_RelativeLayout_Container);

        RelativeLayout RelativeLayoutTopBanner = (RelativeLayout) ViewScaling.findViewByIdAndScale(currentActivity, R.id.ForgotPasswordPage_RelativeLayout_TopBanner);
        AutoReleaseImageView TopBannerBackground = (AutoReleaseImageView) ViewScaling.findViewByIdAndScale(currentActivity, R.id.ForgotPasswordPage_SpecialImageView_TopBannerBackground);
        ViewScaling.setImageView(TopBannerBackground);

        V7TitleView TopBannerTitle = (V7TitleView) ViewScaling.findViewByIdAndScale(currentActivity, R.id.ForgotPasswordPage_V7TitleView_TopBannerTitle);

        inputAccount = (EditText) ViewScaling.findViewByIdAndScale(currentActivity, R.id.ForgotPasswordPage_EditText_inputAccount);
        AutoReleaseImageView AccountFieldIcon = (AutoReleaseImageView) ViewScaling.findViewByIdAndScale(currentActivity, R.id.ForgotPasswordPage_SpecialImageView_AccountFieldIcon);

        BottomButton BottomButton = (BottomButton) ViewScaling.findViewByIdAndScale(currentActivity, R.id.ForgotPasswordPage_BottomButton);
        BottomButton.setData(getResources().getString(R.string.ForgotPasswordPage_Text_Cancel), getResources().getString(R.string.ForgotPasswordPage_Text_Send), 100);

        V7TitleView_Cancel = BottomButton.getLeftButton();
        V7TitleView_Send = BottomButton.getRightButton();

        V7TitleView_Cancel.setOnClickListener(OnCancelClick);
        V7TitleView_Send.setOnClickListener(OnSendClick);
    }

    public View.OnClickListener OnCancelClick = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            AirApplication.setOnclikcFeedback(v);
            Intent intent = new Intent(currentActivity, LoginPage.class);
            startActivity(intent);
            finish();
            setTurnInNextPageAnimation(currentActivity);
        }
    };
    public View.OnClickListener OnSendClick = new View.OnClickListener() {
        @Override
         public void onClick(View v) {
            AirApplication.setOnclikcFeedback(v);
            String userAccount = inputAccount.getText().toString();
            if (isConnectedNetWork()) {
                boolean isPhoneNumberEmpty = true;

                if (AirApplication.isStringNotNullAndEmpty(userAccount)) {
                    isPhoneNumberEmpty = false;
                }

                if (isPhoneNumberEmpty) {
                    Toast.makeText(ForgotPasswordPage.this, getResources().getString(R.string.phone_number_empty), Toast.LENGTH_SHORT).show();
                } else {
//                    SendCheckCode sendCheckCode = new SendCheckCode();
//                    sendCheckCode.execute(userAccount);

                    Intent intent = new Intent(currentActivity, CheckCodePageForPassword.class);
                    intent.putExtra("userAccount",userAccount);
                    startActivity(intent);
                    finish();
                    setTurnInNextPageAnimation(currentActivity);


                }
            }
        }
    };


    private class SendCheckCode extends V7ideaAsyncTask<String, ApiResult> {
        private ForgetPassword forgetPassword = null;
        private NotifyDialog progress = null;

        public SendCheckCode() {
            forgetPassword = new ForgetPassword();
        }

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
        }

        @Override
        public void downLoadSuccess(ApiResult result) {
            Intent intent = new Intent(currentActivity, CheckCodePageForPassword.class);
            startActivity(intent);
            finish();
            setTurnInNextPageAnimation(currentActivity);
        }

        @Override
        public void downLoadFail(ApiResult apiResult) {
            if (apiResult.getErrorNo().contentEquals(Constant.ErrorCode.CODE_500)) {
                showErrorAlert(getResources().getString(R.string.no_internet_error500));
            } else {
                showErrorAlert("驗證碼發送失敗", apiResult.getErrorNo() + apiResult.getMessage());
            }
        }

        @Override
        protected ApiResult doInBackground(String... strings) {
            //上傳會員資料
            return forgetPassword.getCheckCodeForPassword(strings[0]);
        }
    }

    private void showErrorAlert(String title, String message) {
        AlertDialog.Builder alertBuilder = new AlertDialog.Builder(currentActivity);
        alertBuilder.setTitle(title);
        alertBuilder.setMessage(message);
        alertBuilder.setPositiveButton("確定", null);
        alertBuilder.show();
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(currentActivity, LoginPage.class);
        startActivity(intent);
        finish();
        setBackInPrePageAnimation(currentActivity);
    }

}
