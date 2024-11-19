package com.v7idea.healthkit;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Paint;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.v7idea.healthkit.Model.ForgetPassword;
import com.v7idea.healthkit.Model.Member;
import com.v7idea.healthkit.View.BottomButton;
import com.v7idea.template.Activity.BaseActivity;
import com.v7idea.template.AirApplication;
import com.v7idea.template.Tool.ApiResult;
import com.v7idea.template.Tool.DownLoad;
import com.v7idea.template.Tool.NotifyDialog;
import com.v7idea.template.Tool.V7ideaAsyncTask;
import com.v7idea.template.Tool.ViewScaling;
import com.v7idea.template.View.AutoReleaseImageView;
import com.v7idea.template.View.V7TitleView;
/**
 * 2019/1/24 ＊確認使用的頁面 忘記密碼-輸入驗證碼
 */
public class CheckCodePageForPassword extends BaseActivity {
    private static String TAG = "CheckCodePageForPassword";
    EditText EditText_CheckCode_password;
    boolean ifRequestToServer = false;
    TextView TextView_ReSendCode;
    String userAccount = "";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_check_code_page_for_password);

        LinearLayout Container = (LinearLayout) ViewScaling.findViewByIdAndScale(currentActivity, R.id.CheckCodePageForPassword_LinearLayout_Container);

        AutoReleaseImageView SpecialImageView_Background = (AutoReleaseImageView) ViewScaling.findViewByIdAndScale(currentActivity, R.id.CheckCodePageForPassword_SpecialImageView_Background);
        AutoReleaseImageView TopBannerBackground = (AutoReleaseImageView) ViewScaling.findViewByIdAndScale(currentActivity, R.id.CheckCodePageForPassword_SpecialImageView_TopBannerBackground);
        ViewScaling.setImageView(TopBannerBackground);

        V7TitleView TopBannerTitle = (V7TitleView) ViewScaling.findViewByIdAndScale(currentActivity, R.id.CheckCodePageForPassword_V7TitleView_TopBannerTitle);
        V7TitleView Title = (V7TitleView) ViewScaling.findViewByIdAndScale(currentActivity, R.id.CheckCodePageForPassword_V7TitleView_Title);

        RelativeLayout CheckCodeContainer = (RelativeLayout) ViewScaling.findViewByIdAndScale(currentActivity, R.id.CheckCodePageForPassword_RelativeLayout_CheckCodeContainer);
        TextView TextView_CheckCode = (TextView) ViewScaling.findViewByIdAndScale(currentActivity, R.id.CheckCodePageForPassword_TextView_CheckCode);
        EditText_CheckCode_password = (EditText) ViewScaling.findViewByIdAndScale(currentActivity, R.id.CheckCodePageForPassword_EditText_CheckCode);

        BottomButton BottomButton = (BottomButton) ViewScaling.findViewByIdAndScale(currentActivity, R.id.CheckCodePageForPassword_BottomButton);
        BottomButton.setData(getResources().getString(R.string.CheckCodePageForPassword_Text_Cancel), getResources().getString(R.string.CheckCodePageForPassword_Text_Send), 100);
        V7TitleView V7TitleView_Send = BottomButton.getRightButton();
        V7TitleView V7TitleView_Cancel = BottomButton.getLeftButton();

        TextView_ReSendCode = (TextView) ViewScaling.findViewByIdAndScale(currentActivity, R.id.CheckCodePageForPassword_TextView_ReSendCode);
        TextView_ReSendCode.getPaint().setFlags(Paint.UNDERLINE_TEXT_FLAG);

        TextView_ReSendCode.setOnClickListener(onClickExplanation);


        V7TitleView_Send.setOnClickListener(onClickSend);
        V7TitleView_Cancel.setOnClickListener(onClickCancel);

        if (getIntent() != null)
        {
            if (getIntent().getExtras() != null) {
                Log.e(TAG, "getIntent().getExtras()  = " +getIntent().getExtras()  );


                userAccount = getIntent().getExtras().getString("userAccount");
                Log.e(TAG, "userAccount = " +userAccount );

                SendCheckCode sendCheckCode = new SendCheckCode();
                sendCheckCode.execute(userAccount);
            }
        }
    }

    private View.OnClickListener onClickSend = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            AirApplication.setOnclikcFeedback(v);
            if (DownLoad.isConnectInternet()) {
                if (EditText_CheckCode_password.getText().toString().isEmpty()) {
                    String strMessage = getResources().getString(R.string.CheckCodePage_ERROR_CHECK_CDOE_IS_EMPTY);
                    AlertDialog.Builder builder = new AlertDialog.Builder(currentActivity);
                    builder.setMessage(strMessage);
                    builder.setPositiveButton(R.string.determine, null);
                    builder.show();
                } else {
                    GetNewPassword getNewPassword = new GetNewPassword(EditText_CheckCode_password.getText().toString());
                    getNewPassword.execute();
                }
            } else {
                showErrorAlert(getResources().getString(R.string.no_internet_can_not_checkcode));
            }

        }
    };
    private View.OnClickListener onClickCancel = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            AirApplication.setOnclikcFeedback(v);
            Intent intent = new Intent(currentActivity, LoginPage.class);
            startActivity(intent);
            finish();
            setBackInPrePageAnimation(currentActivity);

        }
    };
    private View.OnClickListener onClickExplanation = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            AirApplication.setOnclikcFeedback(view);
            Animation scaleAnimation = AnimationUtils.loadAnimation(currentActivity, R.anim.normal_button_alaph_animation);
            scaleAnimation.setAnimationListener(new Animation.AnimationListener() {
                @Override
                public void onAnimationStart(Animation animation) {

                }

                @Override
                public void onAnimationEnd(Animation animation) {
                    if (!ifRequestToServer) {
                        ifRequestToServer = true;
                        if (DownLoad.isConnectInternet()) {

                            String strMessage = getResources().getString(R.string.CheckCodePageForPassword_Text_ReSendChaeckCode);
                            AlertDialog.Builder builder = new AlertDialog.Builder(currentActivity);
                            builder.setMessage(strMessage);
                            builder.setPositiveButton(R.string.CheckCodePageForPassword_Text_ReSendChaeckCode_NO, null);
                            builder.setNegativeButton(R.string.CheckCodePageForPassword_Text_ReSendChaeckCode_YES, new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    ReSendCheckCode reSendChaeckCodeing = new ReSendCheckCode();
                                    reSendChaeckCodeing.execute(userAccount);

                                }
                            });
                            builder.setCancelable(false);


                            builder.show();


                            ifRequestToServer = false;
                        } else {
                            ifRequestToServer = false;
                            showErrorAlert(getResources().getString(R.string.no_internet_can_not_checkcode));
                        }

                    }
                }

                @Override
                public void onAnimationRepeat(Animation animation) {

                }
            });

            view.setAnimation(scaleAnimation);
            view.startAnimation(scaleAnimation);
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
            showErrorAlert("成功發送驗証碼");
            MyCountDownTimer mCountDownTimer = new MyCountDownTimer(60000, 1000);
            mCountDownTimer.start();
            if (TextView_ReSendCode.isEnabled())
                TextView_ReSendCode.setEnabled(false);
        }

        @Override
        public void downLoadFail(ApiResult apiResult) {
            if (apiResult.getErrorNo().contentEquals(Constant.ErrorCode.CODE_500)) {
                showErrorAlert(getResources().getString(R.string.no_internet_error500));
            } else {
                showErrorAlert("驗証碼發送失敗+\n" + apiResult.getErrorNo() + apiResult.getMessage());
            }
        }

        @Override
        protected ApiResult doInBackground(String... strings) {
            //上傳會員資料
            return forgetPassword.getCheckCodeForPassword(strings[0]);
        }
    }


    private class ReSendCheckCode extends V7ideaAsyncTask<String, ApiResult> {
        private ForgetPassword forgetPassword = null;
        private NotifyDialog progress = null;

        public ReSendCheckCode() {
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
            showErrorAlert("已重新發送驗證碼");
            MyCountDownTimer mCountDownTimer = new MyCountDownTimer(60000, 1000);
            mCountDownTimer.start();
            if (TextView_ReSendCode.isEnabled())
                TextView_ReSendCode.setEnabled(false);
        }

        @Override
        public void downLoadFail(ApiResult apiResult) {
            if (apiResult.getErrorNo().contentEquals(Constant.ErrorCode.CODE_500)) {
                showErrorAlert(getResources().getString(R.string.no_internet_error500));
            } else {
                showErrorAlert("傳送失敗");
            }
        }

        @Override
        protected ApiResult doInBackground(String... strings) {
            //上傳會員資料
            return forgetPassword.getCheckCodeForPassword(strings[0]);
        }
    }

    public class MyCountDownTimer extends CountDownTimer {
        //0.001秒為單位，1000=1秒,millisInFuture=總時間,countDownInterval=倒數的間隔時間
        public MyCountDownTimer(long millisInFuture, long countDownInterval) {
            super(millisInFuture, countDownInterval);
        }

        @Override
        public void onTick(long millisUntilFinished) {
            // 每倒數一次,會執行這裡
            String s1 = getString(R.string.CheckCodePageForPassword_Text_ReSendCode1);
            String s2 = getString(R.string.CheckCodePageForPassword_Text_ReSendCode2);
            TextView_ReSendCode.setText(s1 + millisUntilFinished / 1000 + s2);
        }

        @Override
        public void onFinish() {
            // 倒數結束時,會執行這裡
            TextView_ReSendCode.setText(R.string.CheckCodePageForPassword_Text_ReSendCode);
            if (!TextView_ReSendCode.isEnabled()) TextView_ReSendCode.setEnabled(true);
        }
    }

    @Override
    public void onBackPressed() {
    }

    private class GetNewPassword extends V7ideaAsyncTask<String, ApiResult> {
        private ForgetPassword forgetPassword = null;
        private NotifyDialog progress = null;
        private String checkCode = null;

        public GetNewPassword(String checkCodeNum) {
            forgetPassword = new ForgetPassword();
            checkCode = checkCodeNum;
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
            Intent intent = new Intent(currentActivity, EditPasswordPageForForgotPassword.class);
            startActivity(intent);
            finish();
            setBackInPrePageAnimation(currentActivity);
        }

        @Override
        public void downLoadFail(ApiResult apiResult) {
            if (apiResult.getErrorNo().contentEquals(Constant.ErrorCode.CODE_500)) {
                showErrorAlert(getResources().getString(R.string.no_internet_error500));
            } else {
                showErrorAlert(apiResult.getMessage());
            }
        }

        @Override
        protected ApiResult doInBackground(String... strings) {

            return forgetPassword.getCheckCodeStatus(checkCode);
        }
    }
}
