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

import com.v7idea.healthkit.Model.APIFetch;
import com.v7idea.healthkit.Model.Member;
import com.v7idea.healthkit.Model.Token;
import com.v7idea.healthkit.View.BottomButton;
import com.v7idea.template.Activity.BaseActivity;
import com.v7idea.template.AirApplication;
import com.v7idea.template.Tool.ApiResult;
import com.v7idea.template.Tool.DebugLog;
import com.v7idea.template.Tool.DownLoad;
import com.v7idea.template.Tool.NotifyDialog;
import com.v7idea.template.Tool.V7BaseNameValuePair;
import com.v7idea.template.Tool.V7HttpResult;
import com.v7idea.template.Tool.V7NameValuePair;
import com.v7idea.template.Tool.V7ideaAsyncTask;
import com.v7idea.template.Tool.ViewScaling;
import com.v7idea.template.View.Banner;
import com.v7idea.template.View.V7TitleView;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
/**
 * 2019/1/24 ＊確認使用的頁面 系統設定-修改密碼
 */
public class EditPasswordPage extends BaseActivity implements View.OnClickListener {
    private final static String TAG = "EditPasswordPage";
    EditText currentPassword_editText, newPassword_editText, confirmPassword_editText;
    Member member = new Member();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_password);

        Banner banner = (Banner) ViewScaling.findViewByIdAndScale(currentActivity, R.id.EditPasswordPage_Banner_Header);
        banner.initShortBanner();
        banner.backIcon.setOnClickListener(this);
        banner.title.setText(R.string.EditPasswordPage_Text_Title);
        banner.title.setTextColor(Color.parseColor("#FFFFFF"));

        RelativeLayout ScrollContainer = (RelativeLayout) findViewById(R.id.EditPasswordPage_RelativeLayout_Container);
        LinearLayout PasswordContainer = (LinearLayout) findViewById(R.id.EditPasswordPage_LinearLayout_PasswordContainer);

        RelativeLayout currentPasswordReLayout = (RelativeLayout) ViewScaling.findViewByIdAndScale(currentActivity, R.id.EditPasswordPage_RelativeLayout_Current_Password);
        TextView currentPasswordTextView = (TextView) ViewScaling.findViewByIdAndScale(currentActivity, R.id.EditPasswordPage_TextView_Current_Password);
        currentPassword_editText = (EditText) ViewScaling.findViewByIdAndScale(currentActivity, R.id.EditPasswordPage_EditText_Current_Password);

        RelativeLayout newPasswordReLayout = (RelativeLayout) ViewScaling.findViewByIdAndScale(currentActivity, R.id.EditPasswordPage_RelativeLayout_New_Password);
        TextView newPasswordTextView = (TextView) ViewScaling.findViewByIdAndScale(currentActivity, R.id.EditPasswordPage_TextView_New_Password);
        newPassword_editText = (EditText) ViewScaling.findViewByIdAndScale(currentActivity, R.id.EditPasswordPage_EditText_New_Password);

        RelativeLayout confirmPasswordReLayout = (RelativeLayout) ViewScaling.findViewByIdAndScale(currentActivity, R.id.EditPasswordPage_RelativeLayout_Confirm_Password);
        TextView confirmPasswordTextView = (TextView) ViewScaling.findViewByIdAndScale(currentActivity, R.id.EditPasswordPage_TextView_Confirm_Password);
        confirmPassword_editText = (EditText) ViewScaling.findViewByIdAndScale(currentActivity, R.id.EditPasswordPage_EditText_Confirm_Password);

        BottomButton BottomButton = (BottomButton) ViewScaling.findViewByIdAndScale(currentActivity, R.id.EditPasswordPage_BottomButton);
        BottomButton.setData(getResources().getString(R.string.EditPasswordPage_Text_Back), getResources().getString(R.string.EditPasswordPage_Text_SendSave), 100);
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
                    Intent intent = new Intent(currentActivity, MainActivity.class);
                    startActivity(intent);
                    finish();
                    setBackInPrePageAnimation(currentActivity);
                } else if (view.getId() == R.id.BottomButton_V7TitleView_Left) {
                    Intent intent = new Intent(currentActivity, SystemSettingsPage.class);
                    startActivity(intent);
                    finish();
                    setBackInPrePageAnimation(currentActivity);
                } else if (view.getId() == R.id.BottomButton_V7TitleView_Right) {
//                    String oldPassword = null;
//                    String newPassword = null;
//                    if (currentPassword_editText.getText().toString() != null && !currentPassword_editText.getText().toString().equals("")) {
//                        oldPassword = currentPassword_editText.getText().toString();
//                        if (!newPassword_editText.getText().toString().equals("") && !confirmPassword_editText.getText().toString().equals("") && confirmPassword_editText.getText().toString().equals(newPassword_editText.getText().toString())) {
//                            newPassword = newPassword_editText.getText().toString();
//                            SendCheckCode sendCheckCode = new SendCheckCode(oldPassword, newPassword);
//                            sendCheckCode.execute();
//                        } else {
//                            Toast.makeText(EditPasswordPage.this, "新密碼有誤", Toast.LENGTH_SHORT).show();
//                        }
//
//                    } else {
//                        Toast.makeText(EditPasswordPage.this, "目前密碼不可為空", Toast.LENGTH_SHORT).show();
//                    }

                    String currentPassword = currentPassword_editText.getText().toString();
                    String newPassword = newPassword_editText.getText().toString();
                    String confirmPassword = confirmPassword_editText.getText().toString();

                    boolean isCurrentPasswordEmpty = currentPassword.isEmpty();
                    boolean isNewPasswordEmpty = newPassword.isEmpty();
                    boolean isConfirmPasswordEmpty = confirmPassword.isEmpty();

                    if (isCurrentPasswordEmpty) {
                        alertDialogShow(currentPassword_editText, "目前密碼不能空白");
                    } else if (isNewPasswordEmpty) {
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
                        String oldPassword = null;
                        oldPassword = currentPassword;
                        SendCheckCode sendCheckCode = new SendCheckCode(oldPassword, newPassword);
                        sendCheckCode.execute();
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
        private String oldPassword, newPassword;

        public SendCheckCode(String OldPassword, String NewPassword) {
            oldPassword = OldPassword;
            newPassword = NewPassword;
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
                    Intent intent = new Intent(currentActivity, SystemSettingsPage.class);
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
            } else if (apiResult.getErrorNo().contentEquals(Constant.ErrorCode.CODE_1002)) {
                showErrorAlert(apiResult.getMessage(), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        new Token().destroy();
                        Intent intent = new Intent(currentActivity, LoginPage.class);
                        startActivity(intent);
                        finish();
                        setBackInPrePageAnimation(currentActivity);

                    }
                });
            } else if (apiResult.getErrorNo().contentEquals(Constant.ErrorCode.CODE_1003)) {
                showErrorAlert(apiResult.getMessage(), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        new Token().destroy();
                        Intent intent = new Intent(currentActivity, LoginPage.class);
                        startActivity(intent);
                        finish();
                        setBackInPrePageAnimation(currentActivity);

                    }
                });
            } else {
//                Toast.makeText(EditPasswordPage.this, "" + apiResult.getMessage(), Toast.LENGTH_SHORT).show();
                showErrorAlert(apiResult.getMessage());
            }
        }

        @Override
        protected ApiResult doInBackground(String... strings) {
            //上傳會員資料
            return EditPassword(oldPassword, newPassword);
        }
    }

    public ApiResult EditPassword(String oldPassword, String newPassword) {

        String strApi = APIFetch.getEditPasswordApi();

        DownLoad downLoad = new DownLoad();

        List<V7NameValuePair> content = new ArrayList<V7NameValuePair>();
        content.add(new V7BaseNameValuePair("oldPassword", oldPassword));
        content.add(new V7BaseNameValuePair("newPassword", newPassword));

//        V7HttpResult httpResult = downLoad.getStringFromURLByPostAddHeader(strApi, content);
        V7HttpResult httpResult = downLoad.getStringFromURLByPostAddHeader(strApi, "v7idea_token", member.mobile, downLoad.defaultContentType, content);
        DebugLog.d(TAG, "httpResult code: " + httpResult.getResponseCode());
        DebugLog.d(TAG, "httpResult result: " + httpResult.getResultString());

        if (httpResult.getResponseCode() == 200) {
            String resultString = httpResult.getResultString();

            ApiResult apiResult = ApiResult.getInstance(resultString);

            return apiResult;
        }

        return new ApiResult("" + httpResult.getResponseCode(), httpResult.getResultString(), null);
    }

    public void alertDialogShow(final EditText v, String str) {

        new AlertDialog.Builder(EditPasswordPage.this)
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
//        super.onBackPressed();
        Intent intent = new Intent(currentActivity, SystemSettingsPage.class);
        startActivity(intent);
        finish();
        setBackInPrePageAnimation(currentActivity);
    }
}
