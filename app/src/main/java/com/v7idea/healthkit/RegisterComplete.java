package com.v7idea.healthkit;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.v7idea.healthkit.Domain.NoticeData;
import com.v7idea.healthkit.Model.Member;
import com.v7idea.healthkit.Model.NoticeSetting;
import com.v7idea.healthkit.Model.Token;
import com.v7idea.healthkit.View.BottomButton;
import com.v7idea.template.Activity.BaseActivity;
import com.v7idea.template.AirApplication;
import com.v7idea.template.DataBase.SimpleDatabase;
import com.v7idea.template.Tool.ApiResult;
import com.v7idea.template.Tool.DownLoad;
import com.v7idea.template.Tool.NotifyDialog;
import com.v7idea.template.Tool.V7ideaAsyncTask;
import com.v7idea.template.Tool.ViewScaling;
import com.v7idea.template.View.AutoReleaseImageView;
import com.v7idea.template.View.V7TitleView;

import java.util.ArrayList;
/**
 * 2019/1/24 ＊確認使用的頁面 註冊-註冊完成確認
 */
public class RegisterComplete extends BaseActivity {
    private static final String TAG = "RegisterComplete";
    private boolean ifRequestToServer = false;
    private NotifyDialog progress = null;

    String data = "[{\"noticeKey\":\"dining\",\"noticeName\":\"用餐提示\",\"noticeSetting\":1},{\"noticeKey\":\"drinkingWater\",\"noticeName\":\"飲水/排尿提示\",\"noticeSetting\":0},{\"noticeKey\":\"sittingPosition\",\"noticeName\":\"坐姿提示\",\"noticeSetting\":1},{\"noticeKey\":\"eyeCare\",\"noticeName\":\"眼部保健提示\",\"noticeSetting\":1},{\"noticeKey\":\"defecation\",\"noticeName\":\"排便提示\",\"noticeSetting\":1},{\"noticeKey\":\"sleeping\",\"noticeName\":\"睡眠提示\",\"noticeSetting\":0},{\"noticeKey\":\"pressure\",\"noticeName\":\"壓力提示\",\"noticeSetting\":1}]";
    NoticeData noticeData = new NoticeData();
    ArrayList<NoticeData> NoticeData_List = noticeData.parseData(data);
    int listIndex = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_complete);

        AutoReleaseImageView SpecialImageView_Background = (AutoReleaseImageView) ViewScaling.findViewByIdAndScale(currentActivity, R.id.RegisterComplete_SpecialImageView_Background);
        AutoReleaseImageView TopBannerBackground = (AutoReleaseImageView) ViewScaling.findViewByIdAndScale(currentActivity, R.id.RegisterComplete_SpecialImageView_TopBannerBackground);
        ViewScaling.setImageView(TopBannerBackground);

        LinearLayout LinearLayout_Container = (LinearLayout) ViewScaling.findViewByIdAndScale(currentActivity, R.id.RegisterComplete_LinearLayout_Container);

        V7TitleView TopBannerTitle = (V7TitleView) ViewScaling.findViewByIdAndScale(currentActivity, R.id.RegisterComplete_V7TitleView_TopBannerTitle);

        AutoReleaseImageView SpecialImageView_Logo = (AutoReleaseImageView) ViewScaling.findViewByIdAndScale(currentActivity, R.id.RegisterComplete_SpecialImageView_Logo);

        BottomButton BottomButton = (BottomButton) ViewScaling.findViewByIdAndScale(currentActivity, R.id.RegisterComplete_BottomButton);
        BottomButton.setData(getResources().getString(R.string.RegisterCompletePage_Text_BackMain), 100);
        V7TitleView RegisterComplete_BackMain = BottomButton.getOneButton();
        RegisterComplete_BackMain.setOnClickListener(onBackMainClick);

    }

    private View.OnClickListener onBackMainClick = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            AirApplication.setOnclikcFeedback(v);
            login();
        }
    };

    public void onBackPressed() {

    }

    private void login() {
        if (!ifRequestToServer) {
            ifRequestToServer = true;

            if (DownLoad.isConnectInternet()) {
                SimpleDatabase simpleDatabase = new SimpleDatabase();

                String userAccount = simpleDatabase.getStringValueByKey(Constant.TEMP_ACCOUNT, null);
                String userPassword = simpleDatabase.getStringValueByKey(Constant.TEMP_PASSWORD, null);

                progress = new NotifyDialog(RegisterComplete.this);
                progress.settingProgressDialog();

                LoginSystem login = new LoginSystem();
                login.execute(userAccount, userPassword);


            } else {
                ifRequestToServer = false;

                showErrorAlert(getResources().getString(R.string.no_internet_can_not_login));
            }
        }
    }

    private class LoginSystem extends AsyncTask<String, String, ApiResult> {

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

        @Override
        protected void onPostExecute(ApiResult result) {
            super.onPostExecute(result);

            if (progress != null) {
                progress.dismiss();
                progress = null;
            }

            ifRequestToServer = false;

            if (result != null) {
                String status = result.getErrorNo();

                Log.d(TAG, "status: " + status);

                if (status.contentEquals(Constant.ErrorCode.SUCCESS)) {
                    //解析成	UserPermission物件
                    Intent intent = new Intent(currentActivity, MainActivity.class);
                    startActivity(intent);
                    finish();

                } else {
                    String strMessage = result.getMessage();
                    AlertDialog.Builder builder = new AlertDialog.Builder(currentActivity);
                    builder.setMessage(strMessage);
                    builder.setPositiveButton(R.string.determine, null);
                    builder.show();
                }
            }
        }
    }

}
