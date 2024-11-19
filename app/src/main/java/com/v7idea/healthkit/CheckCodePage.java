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

import com.v7idea.healthkit.Domain.NoticeData;
import com.v7idea.healthkit.Model.CheckCode;
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
 * 2019/1/24 ＊確認使用的頁面 註冊-帳號驗證頁
 */
public class CheckCodePage extends BaseActivity {
    private static String TAG = "CheckCodePage";
    EditText EditText_CheckCode;
    boolean ifRequestToServer = false;
    TextView TextView_ReSendCode;


    String data = "[{\"noticeKey\":\"dining\",\"noticeName\":\"用餐提示\",\"noticeSetting\":1},{\"noticeKey\":\"drinkingWater\",\"noticeName\":\"飲水/排尿提示\",\"noticeSetting\":1},{\"noticeKey\":\"sittingPosition\",\"noticeName\":\"坐姿提示\",\"noticeSetting\":1},{\"noticeKey\":\"eyeCare\",\"noticeName\":\"眼部保健提示\",\"noticeSetting\":1},{\"noticeKey\":\"defecation\",\"noticeName\":\"排便提示\",\"noticeSetting\":1},{\"noticeKey\":\"sleeping\",\"noticeName\":\"睡眠提示\",\"noticeSetting\":1},{\"noticeKey\":\"pressure\",\"noticeName\":\"壓力提示\",\"noticeSetting\":1}]";
    NoticeData noticeData = new NoticeData();
    ArrayList<NoticeData> NoticeData_List = noticeData.parseData(data);
    int listIndex = 0;
    private NotifyDialog progress = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_check_code_page);

        LinearLayout Container = (LinearLayout) ViewScaling.findViewByIdAndScale(currentActivity, R.id.CheckCodePage_LinearLayout_Container);

        AutoReleaseImageView SpecialImageView_Background = (AutoReleaseImageView) ViewScaling.findViewByIdAndScale(currentActivity, R.id.CheckCodePage_SpecialImageView_Background);
        AutoReleaseImageView TopBannerBackground = (AutoReleaseImageView) ViewScaling.findViewByIdAndScale(currentActivity, R.id.CheckCodePage_SpecialImageView_TopBannerBackground);
        ViewScaling.setImageView(TopBannerBackground);

        V7TitleView TopBannerTitle = (V7TitleView) ViewScaling.findViewByIdAndScale(currentActivity, R.id.CheckCodePage_V7TitleView_TopBannerTitle);
        V7TitleView Title = (V7TitleView) ViewScaling.findViewByIdAndScale(currentActivity, R.id.CheckCodePage_V7TitleView_Title);

        RelativeLayout CheckCodeContainer = (RelativeLayout) ViewScaling.findViewByIdAndScale(currentActivity, R.id.CheckCodePage_RelativeLayout_CheckCodeContainer);
        TextView TextView_CheckCode = (TextView) ViewScaling.findViewByIdAndScale(currentActivity, R.id.CheckCodePage_TextView_CheckCode);
        EditText_CheckCode = (EditText) ViewScaling.findViewByIdAndScale(currentActivity, R.id.CheckCodePage_EditText_CheckCode);


        BottomButton BottomButton = (BottomButton) ViewScaling.findViewByIdAndScale(currentActivity, R.id.CheckCodePage_BottomButton);
        BottomButton.setData(getResources().getString(R.string.CheckCodePage_Text_Cancel), getResources().getString(R.string.CheckCodePage_Text_Send), 100);

        V7TitleView V7TitleView_Cancel = BottomButton.getLeftButton();
        V7TitleView V7TitleView_Send = BottomButton.getRightButton();

        TextView_ReSendCode = (TextView) ViewScaling.findViewByIdAndScale(currentActivity, R.id.CheckCodePage_TextView_ReSendCode);
        TextView_ReSendCode.getPaint().setFlags(Paint.UNDERLINE_TEXT_FLAG);

        TextView_ReSendCode.setOnClickListener(onClickExplanation);
        V7TitleView_Send.setOnClickListener(onClickSend);
        V7TitleView_Cancel.setOnClickListener(onClickBack);

        MyCountDownTimer mCountDownTimer = new MyCountDownTimer(60000, 1000);
        mCountDownTimer.start();
        TextView_ReSendCode.setEnabled(false);
    }

    private View.OnClickListener onClickBack = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            AirApplication.setOnclikcFeedback(view);
            Intent intent = new Intent(currentActivity, LoginPage.class);
            startActivity(intent);
            finish();
            setBackInPrePageAnimation(currentActivity);
        }
    };

    private View.OnClickListener onClickSend = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            AirApplication.setOnclikcFeedback(view);
            if (!ifRequestToServer) {
                ifRequestToServer = true;
                if (DownLoad.isConnectInternet()) {
                    if (EditText_CheckCode.getText().toString().isEmpty()) {
                        String strMessage = getResources().getString(R.string.CheckCodePage_ERROR_CHECK_CDOE_IS_EMPTY);
                        AlertDialog.Builder builder = new AlertDialog.Builder(currentActivity);
                        builder.setMessage(strMessage);
                        builder.setPositiveButton(R.string.determine, null);
                        builder.show();
                        ifRequestToServer = false;
                    } else {
                        Token tokenID = new Token();
                        sendCheckCode sendCheckCodeing = new sendCheckCode();
                        sendCheckCodeing.execute(tokenID.getTokenId(), EditText_CheckCode.getText().toString());
                    }
                } else {
                    ifRequestToServer = false;
                    showErrorAlert(getResources().getString(R.string.no_internet_can_not_checkcode));
                }
            }
        }

    };

    private class sendCheckCode extends V7ideaAsyncTask<String, ApiResult> {



        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            progress = new NotifyDialog(currentActivity);
            progress.settingProgressDialog();
        }

        @Override
        public void ifNeedCloseSomeThing() {
//            if (progress != null) {
//                progress.dismiss();
//                progress = null;
//            }
            ifRequestToServer = false;
        }

        @Override
        public void downLoadSuccess(ApiResult result) {


            String noticeKey = NoticeData_List.get(listIndex).getNoticeKey();
            String noticeSetting = String.valueOf(NoticeData_List.get(listIndex).getNoticeSetting());
            UpDataNoticeSetting upDataNoticeSetting = new UpDataNoticeSetting();
            upDataNoticeSetting.execute(noticeKey, noticeSetting);

        }

        @Override
        public void downLoadFail(ApiResult apiResult) {
            if (progress != null) {
                progress.dismiss();
                progress = null;
            }

            if (apiResult.getErrorNo().contentEquals(Constant.ErrorCode.CODE_500)) {
                showErrorAlert(getResources().getString(R.string.no_internet_error500));
            } else {
                showErrorAlert(apiResult.getMessage());
            }
        }

        @Override
        protected ApiResult doInBackground(String... params) {
            if (params != null && params.length > 0) {
                Member member = new Member();
                ApiResult sendCheckCcodeResult = member.sendCheckCode(params[0], params[1]);

                Token token = new Token();

                if (sendCheckCcodeResult.getErrorNo().contentEquals(Constant.ErrorCode.SUCCESS)) {
                    SimpleDatabase simpleDatabase = new SimpleDatabase();

                    String tmpAccount = simpleDatabase.getStringValueByKey(Constant.TEMP_ACCOUNT, null);
                    String strPassword = simpleDatabase.getStringValueByKey(Constant.TEMP_PASSWORD, null);

                    sendCheckCcodeResult = token.getTokenID(tmpAccount, strPassword);
                    if (sendCheckCcodeResult.getErrorNo().contentEquals(Constant.ErrorCode.SUCCESS) && token.getTokenId() != null) {
                        return member.getMemberData(token.getTokenId());
                    }
                    return sendCheckCcodeResult;
                }

                return sendCheckCcodeResult;
            }
            return null;
        }
    }

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

                            String strMessage = getResources().getString(R.string.CheckCodePage_Text_ReSendChaeckCode);
                            AlertDialog.Builder builder = new AlertDialog.Builder(currentActivity);
                            builder.setMessage(strMessage);
                            builder.setPositiveButton(R.string.CheckCodePage_Text_ReSendChaeckCode_NO, null);
                            builder.setNegativeButton(R.string.CheckCodePage_Text_ReSendChaeckCode_YES, new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    SimpleDatabase simpleDatabase = new SimpleDatabase();
                                    reSendChaeckCode reSendChaeckCodeing = new reSendChaeckCode();
                                    if (simpleDatabase.getStringValueByKey(Constant.TEMP_ACCOUNT, null) != null) {
                                        reSendChaeckCodeing.execute(simpleDatabase.getStringValueByKey(Constant.TEMP_ACCOUNT, null));
                                    } else {
                                        showErrorAlert("註冊電話已經遺失,請重新註冊", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                Intent intent = new Intent(currentActivity, RegisterPage.class);
                                                startActivity(intent);
                                                currentActivity.finish();
                                                setBackInPrePageAnimation(currentActivity);
                                            }
                                        });

                                    }


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

    private class reSendChaeckCode extends V7ideaAsyncTask<String, ApiResult> {

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
        public void downLoadSuccess(ApiResult apiResult) {
            Token token = new Token();
            token.setTokenId(apiResult.getDataJSONObject().optString("v7idea_Token"));
            String strMessage = getResources().getString(R.string.CheckCodePage_ERROR_CHECK_CDOE_IS_EMPTY);
            AlertDialog.Builder builder = new AlertDialog.Builder(currentActivity);
            builder.setMessage(strMessage);
            builder.setPositiveButton(R.string.determine, null);
            builder.show();
            if (TextView_ReSendCode.isEnabled())
                TextView_ReSendCode.setEnabled(false);
            MyCountDownTimer mCountDownTimer = new MyCountDownTimer(60000, 1000);
            mCountDownTimer.start();
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
        protected ApiResult doInBackground(String... params) {
            if (params != null && params.length > 0) {
                CheckCode reSendChaeckCode = new CheckCode();
                ApiResult reSendChaeckCodeResult = reSendChaeckCode.reSendChaeckCode(params[0]);
                return reSendChaeckCodeResult;
            }
            return null;
        }
    }


    private View.OnClickListener onPressBackIcon = new View.OnClickListener() {
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
                    onBackPressed();
                }

                @Override
                public void onAnimationRepeat(Animation animation) {

                }
            });

            view.setAnimation(scaleAnimation);
            view.startAnimation(scaleAnimation);
        }
    };

    public class MyCountDownTimer extends CountDownTimer {
        //0.001秒為單位，1000=1秒,millisInFuture=總時間,countDownInterval=倒數的間隔時間
        public MyCountDownTimer(long millisInFuture, long countDownInterval) {
            super(millisInFuture, countDownInterval);
        }

        @Override
        public void onTick(long millisUntilFinished) {
            // 每倒數一次,會執行這裡
            String s1 = getString(R.string.CheckCodePage_Text_ReSendCode1);
            String s2 = getString(R.string.CheckCodePage_Text_ReSendCode2);
            TextView_ReSendCode.setText(s1 + millisUntilFinished / 1000 + s2);
        }

        @Override
        public void onFinish() {
            // 倒數結束時,會執行這裡
            TextView_ReSendCode.setText(R.string.CheckCodePage_Text_ReSendCode);
            if (!TextView_ReSendCode.isEnabled()) {
                TextView_ReSendCode.setEnabled(true);
            }
        }
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(currentActivity, RegisterPage.class);
        startActivity(intent);
        finish();
        setBackInPrePageAnimation(currentActivity);
    }

    private class UpDataNoticeSetting extends V7ideaAsyncTask<String, ApiResult> {
        private NoticeSetting noticeSetting = null;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            noticeSetting = new NoticeSetting();
            if (progress == null) {
                progress = new NotifyDialog(currentActivity);
                progress.settingProgressDialog();
            } else {
                progress.settingProgressDialog();
            }
        }

        @Override
        public void ifNeedCloseSomeThing() {

        }

        @Override
        public void downLoadSuccess(ApiResult result) {
            listIndex = listIndex + 1;

            if (!(listIndex > NoticeData_List.size() - 1)) {
                Log.e(TAG, "A listIndex = " + listIndex);
                String noticeKey = NoticeData_List.get(listIndex).getNoticeKey();
                String noticeSetting = String.valueOf(NoticeData_List.get(listIndex).getNoticeSetting());
                Log.d(TAG, "noticeName : " + NoticeData_List.get(listIndex).getNoticeName());
                Log.d(TAG, "noticeKey : " + noticeKey);
                Log.d(TAG, "noticeSetting : " + noticeSetting);

                UpDataNoticeSetting upDataNoticeSetting = new UpDataNoticeSetting();
                upDataNoticeSetting.execute(noticeKey, noticeSetting);
            } else {

                if (progress != null) {
                    progress.dismiss();
                    progress = null;
                }
                listIndex = 0;
                Log.e(TAG, "downLoadSuccess end");

                Intent intent = new Intent(currentActivity, RegisterComplete.class);
                startActivity(intent);
                finish();
                setTurnInNextPageAnimation(currentActivity);


            }

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
//                showErrorAlert(apiResult.getMessage());


                String noticeKey = NoticeData_List.get(listIndex).getNoticeKey();
                String noticeSetting = String.valueOf(NoticeData_List.get(listIndex).getNoticeSetting());
                UpDataNoticeSetting upDataNoticeSetting = new UpDataNoticeSetting();
                upDataNoticeSetting.execute(noticeKey, noticeSetting);
                Log.e(TAG, "downLoadFail: " + apiResult.getMessage());
            }
        }

        @Override
        protected ApiResult doInBackground(String... params) {

            Token token = new Token();
            return noticeSetting.getEditNoticeSetting(token.getTokenId(), params[0], params[1]);
        }

    }

}
