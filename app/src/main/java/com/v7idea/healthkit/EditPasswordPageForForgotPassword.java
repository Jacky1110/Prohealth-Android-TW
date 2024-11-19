package com.v7idea.healthkit;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.InputMethodManager;
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
import com.v7idea.template.Tool.NotifyDialog;
import com.v7idea.template.Tool.V7ideaAsyncTask;
import com.v7idea.template.Tool.ViewScaling;
import com.v7idea.template.View.Banner;
import com.v7idea.template.View.V7TitleView;

import java.util.regex.Matcher;
import java.util.regex.Pattern;
/**
 * 2019/1/24 ＊確認使用的頁面 忘記密碼-重設密碼
 */
public class EditPasswordPageForForgotPassword extends BaseActivity implements View.OnClickListener {
    private final static String TAG = "EditPassword_ForgotPassword";
    EditText currentPassword_editText, newPassword_editText, confirmPassword_editText;
    Member member = new Member();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_password);

        Banner banner = (Banner) ViewScaling.findViewByIdAndScale(currentActivity, R.id.EditPasswordPage_Banner_Header);
        banner.initShortBanner();
        banner.backIcon.setOnClickListener(this);
        banner.title.setText(R.string.EditPasswordPageForForgotPasswodr_Text_Title);
        banner.title.setTextColor(Color.parseColor("#FFFFFF"));
        banner.backIcon.setVisibility(View.GONE);

        RelativeLayout ScrollContainer = (RelativeLayout) findViewById(R.id.EditPasswordPage_RelativeLayout_Container);
        LinearLayout PasswordContainer = (LinearLayout) findViewById(R.id.EditPasswordPage_LinearLayout_PasswordContainer);

        RelativeLayout currentPasswordReLayout = (RelativeLayout) ViewScaling.findViewByIdAndScale(currentActivity, R.id.EditPasswordPage_RelativeLayout_Current_Password);
        TextView currentPasswordTextView = (TextView) ViewScaling.findViewByIdAndScale(currentActivity, R.id.EditPasswordPage_TextView_Current_Password);
        currentPassword_editText = (EditText) ViewScaling.findViewByIdAndScale(currentActivity, R.id.EditPasswordPage_EditText_Current_Password);
        currentPasswordReLayout.setVisibility(View.GONE);

        RelativeLayout newPasswordReLayout = (RelativeLayout) ViewScaling.findViewByIdAndScale(currentActivity, R.id.EditPasswordPage_RelativeLayout_New_Password);
        TextView newPasswordTextView = (TextView) ViewScaling.findViewByIdAndScale(currentActivity, R.id.EditPasswordPage_TextView_New_Password);
        newPassword_editText = (EditText) ViewScaling.findViewByIdAndScale(currentActivity, R.id.EditPasswordPage_EditText_New_Password);

        RelativeLayout confirmPasswordReLayout = (RelativeLayout) ViewScaling.findViewByIdAndScale(currentActivity, R.id.EditPasswordPage_RelativeLayout_Confirm_Password);
        TextView confirmPasswordTextView = (TextView) ViewScaling.findViewByIdAndScale(currentActivity, R.id.EditPasswordPage_TextView_Confirm_Password);
        confirmPassword_editText = (EditText) ViewScaling.findViewByIdAndScale(currentActivity, R.id.EditPasswordPage_EditText_Confirm_Password);

        BottomButton BottomButton = (BottomButton) ViewScaling.findViewByIdAndScale(currentActivity, R.id.EditPasswordPage_BottomButton);
        BottomButton.setData(getResources().getString(R.string.EditPasswordPage_Text_Cancel), getResources().getString(R.string.EditPasswordPage_Text_SendSave), 100);
        V7TitleView V7TitleView_Back = BottomButton.getLeftButton();
        V7TitleView V7TitleView_SendSave = BottomButton.getRightButton();

        V7TitleView_Back.setOnClickListener(this);
        V7TitleView_SendSave.setOnClickListener(this);
    }

    @Override
    public void onClick(final View view) {
        AirApplication.setOnclikcFeedback(view);
        Animation scaleAnimation = AnimationUtils.loadAnimation(currentActivity, R.anim.normal_button_alaph_animation);
        scaleAnimation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                if (view.getId() == R.id.shortBanner_AutoReleaseImageView_backIcon) {
                    Intent intent = new Intent(currentActivity, LoginPage.class);
                    startActivity(intent);
                    finish();
                    setBackInPrePageAnimation(currentActivity);
                } else if (view.getId() == R.id.BottomButton_V7TitleView_Left) {
                    Intent intent = new Intent(currentActivity, LoginPage.class);
                    startActivity(intent);
                    finish();
                    setBackInPrePageAnimation(currentActivity);
                } else if (view.getId() == R.id.BottomButton_V7TitleView_Right) {
                    String newPassword = newPassword_editText.getText().toString();
                    String confirmPassword = confirmPassword_editText.getText().toString();

                    boolean isNewPasswordEmpty = newPassword.isEmpty();
                    boolean isConfirmPasswordEmpty = confirmPassword.isEmpty();


                    if (isNewPasswordEmpty) {
                        alertDialogShow(newPassword_editText, "新密碼不能空白");
                    } else if (isConfirmPasswordEmpty) {
                        alertDialogShow(confirmPassword_editText, "確認密碼不能空白");
                    } else if (newPassword.length() < 8) {
                        alertDialogShow(newPassword_editText, getResources().getString(R.string.REGISTER_ERROR_PASSWORD_LENGTH_MORE_THAN_8));
                    } else if (checkEnglishNum(newPassword) == false) {
                        alertDialogShow(newPassword_editText, getResources().getString(R.string.REGISTER_ERROR_PASSWORD_HAVE_NUM));
                    } else if (!newPassword.contentEquals(confirmPassword)) {
                        alertDialogShow(newPassword_editText, "新密碼與再次確認密碼不符");
                    } else {

                        SendCheckCode sendCheckCode = new SendCheckCode();
                        sendCheckCode.execute(newPassword);
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

    private class SendCheckCode extends V7ideaAsyncTask<String, ApiResult> {

        private NotifyDialog progress = null;
        private ForgetPassword forgetPassword = null;

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

            showErrorAlert(result.getMessage().toString() + "\n密碼已修改", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    Intent intent = new Intent(currentActivity, LoginPage.class);
                    startActivity(intent);
                    finish();
                    setBackInPrePageAnimation(currentActivity);
                }
            });

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
            //上傳會員資料
            return forgetPassword.setNewPassword(strings[0]);
        }
    }


    public void alertDialogShow(final EditText v, String str) {

        new AlertDialog.Builder(EditPasswordPageForForgotPassword.this)
                .setMessage(str)//設定顯示的文字
                .setPositiveButton(R.string.determine, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (v != null) {
                            v.requestFocus();
                            InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
                            imm.toggleSoftInput(0, InputMethodManager.HIDE_NOT_ALWAYS);
                        }
                    }
                })
                .setCancelable(false)
                .show();//呈現對話視窗
    }

    //驗證英文數字,最少各一(密碼用)
    public boolean checkEnglishNum(String Str) {
        Pattern p = Pattern.compile("^(?=.*[A-Za-z])(?=.*\\d)[A-Za-z\\d]{0,}$");
        Matcher m = p.matcher(Str);
        if (m.matches()) {
            return true;
        } else {
            return false;
        }
    }

    @Override
    public void onBackPressed() {


    }
}
