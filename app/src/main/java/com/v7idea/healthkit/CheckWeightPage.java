package com.v7idea.healthkit;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.v7idea.healthkit.Model.Member;
import com.v7idea.healthkit.Model.Token;
import com.v7idea.healthkit.View.BottomButton;
import com.v7idea.template.Activity.BaseActivity;
import com.v7idea.template.AirApplication;
import com.v7idea.template.Tool.ApiResult;
import com.v7idea.template.Tool.NotifyDialog;
import com.v7idea.template.Tool.V7ideaAsyncTask;
import com.v7idea.template.Tool.ViewScaling;
import com.v7idea.template.View.AutoReleaseImageView;
import com.v7idea.template.View.V7TitleView;
import com.v7idea.template.DataBase.SimpleDatabase;
/**
 * 2019/1/24 ＊確認使用的頁面 進行量測-第二次量測
 */
public class CheckWeightPage extends BaseActivity implements View.OnClickListener {
    private final static String TAG = "CheckWeightPage";

    private Member member = null;

    private V7TitleView CheckWeightPage_Status;
    private EditText EditTextWeight;
    private V7TitleView TopBannerTitle;

    private Button Question2Answer1;
    private Button Question2Answer2;
    private Button Question2Answer3;

    private Button Question3Answer1;
    private Button Question3Answer2;
    private Button Question3Answer3;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_check_weight_page);

        member = new Member();

        AutoReleaseImageView Header = (AutoReleaseImageView) ViewScaling.findViewByIdAndScale(currentActivity, R.id.CheckWeightPage_SpecialImageView_Header);

//        ScrollView ScrollArea = (ScrollView) ViewScaling.findViewByIdAndScale(currentActivity, R.id.CheckWeightPage_ScrollArea);
        LinearLayout ScrollContainer = (LinearLayout) ViewScaling.findViewByIdAndScale(currentActivity, R.id.CheckWeightPage_LinearLayout_contentLayout);

        RelativeLayout TopBannerContainer = (RelativeLayout) ViewScaling.findViewByIdAndScale(currentActivity, R.id.CheckWeightPage_RelativeLayout_TopBannerContainer);
        AutoReleaseImageView TopBannerBackground = (AutoReleaseImageView) ViewScaling.findViewByIdAndScale(currentActivity, R.id.CheckWeightPage_SpecialImageView_TopBannerBackground);
        ViewScaling.setImageView(TopBannerBackground);
        TopBannerTitle = (V7TitleView) ViewScaling.findViewByIdAndScale(currentActivity, R.id.CheckWeightPage_V7TitleView_TopBannerTitle);
        CheckWeightPage_Status = (V7TitleView) ViewScaling.findViewByIdAndScale(currentActivity, R.id.CheckWeightPage_V7TitleView_Status);

        TextView Question1_Label = (TextView) ViewScaling.findViewByIdAndScale(currentActivity, R.id.CheckWeightPage_TextView_Question1_Label);
        TextView Question1_Title = (TextView) ViewScaling.findViewByIdAndScale(currentActivity, R.id.CheckWeightPage_TextView_Question1_Title);
        RelativeLayout WeightContainer = (RelativeLayout) ViewScaling.findViewByIdAndScale(currentActivity, R.id.CheckWeightPage_RelativeLayout_WeightContainer);
        TextView TextViewWeight = (TextView) ViewScaling.findViewByIdAndScale(currentActivity, R.id.CheckWeightPage_TextView_Weight);

        EditTextWeight = (EditText) ViewScaling.findViewByIdAndScale(currentActivity, R.id.CheckWeightPage_EditText_Weight);

        TextView Question2_Label = (TextView) ViewScaling.findViewByIdAndScale(currentActivity, R.id.CheckWeightPage_TextView_Question2_Label);
        TextView Question2_Title = (TextView) ViewScaling.findViewByIdAndScale(currentActivity, R.id.CheckWeightPage_TextView_Question2_Title);
        LinearLayout Question2Container = (LinearLayout) ViewScaling.findViewByIdAndScale(currentActivity, R.id.CheckWeightPage_LinearLayout_Question2Container);
        Question2Answer1 = (Button) ViewScaling.findViewByIdAndScale(currentActivity, R.id.CheckWeightPage_Button_Question2_Answer1);
        Question2Answer2 = (Button) ViewScaling.findViewByIdAndScale(currentActivity, R.id.CheckWeightPage_Button_Question2_Answer2);
        Question2Answer3 = (Button) ViewScaling.findViewByIdAndScale(currentActivity, R.id.CheckWeightPage_Button_Question2_Answer3);
        Question2Answer1.setOnClickListener(this);
        Question2Answer2.setOnClickListener(this);
        Question2Answer3.setOnClickListener(this);

        TextView Question3_Label = (TextView) ViewScaling.findViewByIdAndScale(currentActivity, R.id.CheckWeightPage_TextView_Question3_Label);
        TextView Question3_Title = (TextView) ViewScaling.findViewByIdAndScale(currentActivity, R.id.CheckWeightPage_TextView_Question3_Title);
        LinearLayout Question3Container = (LinearLayout) ViewScaling.findViewByIdAndScale(currentActivity, R.id.CheckWeightPage_LinearLayout_Question3Container);
        Question3Answer1 = (Button) ViewScaling.findViewByIdAndScale(currentActivity, R.id.CheckWeightPage_Button_Question3_Answer1);
        Question3Answer2 = (Button) ViewScaling.findViewByIdAndScale(currentActivity, R.id.CheckWeightPage_Button_Question3_Answer2);
        Question3Answer3 = (Button) ViewScaling.findViewByIdAndScale(currentActivity, R.id.CheckWeightPage_Button_Question3_Answer3);
        Question3Answer1.setOnClickListener(this);
        Question3Answer2.setOnClickListener(this);
        Question3Answer3.setOnClickListener(this);

        BottomButton CheckCodePage_BottomButton = (BottomButton) ViewScaling.findViewByIdAndScale(currentActivity, R.id.CheckCodePage_BottomButton);
        CheckCodePage_BottomButton.setData(getString(R.string.leave), getResources().getString(R.string.CheckWeightPage_Text_EndAndSend), 100);
//        CheckCodePage_BottomButton.setData(getString(R.string.leave), 100);
        TextView EndAndSend = CheckCodePage_BottomButton.getRightButton();
        TextView LeaveButton = CheckCodePage_BottomButton.getLeftButton();
        EndAndSend.setOnClickListener(OnSendData);

        LeaveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AirApplication.setOnclikcFeedback(v);
                AirApplication.setOnclikcFeedback(v);
                Intent intent = new Intent(currentActivity, MainActivity.class);
                startActivity(intent);
                finish();
                setBackInPrePageAnimation(currentActivity);
            }
        });

        init();

        EditTextWeight.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (s.length() >= 2) {
                    setCheckWeightPage_Status(s.toString());
                } else {
                    CheckWeightPage_Status.setText("");
                }
            }
        });
    }

    private void init() {
        TopBannerTitle.setText(member.getUserNickName() + "，" + "您好");

        EditTextWeight.setText(member.weight);

        AirApplication.Detection_Weight = "";
        AirApplication.Detection_Height = "";
        AirApplication.Detection_Question1 = "";
        AirApplication.Detection_Question2 = "";
        setCheckWeightPage_Status(member.weight);
    }

    private class UpDateMemberData extends V7ideaAsyncTask<String, ApiResult> {

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
        }

        @Override
        public void downLoadSuccess(ApiResult result) {
            SimpleDatabase simpleDatabase = new SimpleDatabase();
            boolean isOperationInstructions = simpleDatabase.getBooleanValueByKey(Constant.IS_OPERATION_INSTRUCTIONS, false);
            Intent intent = null;

            if (isOperationInstructions) {
                intent = new Intent(currentActivity, ScanDevicePage.class);
            } else {
                intent = new Intent(currentActivity, PrepareConnectPage1.class);
            }
            startActivity(intent);
            finish();
            setTurnInNextPageAnimation(currentActivity);
        }

        @Override
        public void downLoadFail(ApiResult apiResult) {
            if (apiResult.getErrorNo().contentEquals(Constant.ErrorCode.CODE_500)) {
                showErrorAlert(getResources().getString(R.string.no_internet_error500));
            } else if (apiResult.getErrorNo().equals("1002") || apiResult.getErrorNo().equals("1003")) {
                showErrorAlert("err: " + apiResult.getErrorNo() + " \nmessage: " + apiResult.getMessage() + " 請重新登入", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Intent intent = new Intent(currentActivity, LoginPage.class);
                        startActivity(intent);
                        finish();
                        setBackInPrePageAnimation(currentActivity);
                    }
                });
            } else {
                showErrorAlert("err: " + apiResult.getErrorNo() + " \nmessage: " + apiResult.getMessage());
            }
        }

        @Override
        protected ApiResult doInBackground(String... params) {
            //上傳會員資料
            Token token = new Token();
            ApiResult apiResult = member.putMemberDataToServer(token.getTokenId());

            if (apiResult.getErrorNo().contentEquals(Constant.ErrorCode.SUCCESS)) {
                return member.getMemberData(token.getTokenId());
            } else {
                return apiResult;
            }
        }
    }

    @Override
    public void onClick(View v) {
        AirApplication.setOnclikcFeedback(v);
        switch (v.getId()) {

            case R.id.CheckWeightPage_Button_Question2_Answer1: {
                Question2Answer1.setSelected(true);
                Question2Answer2.setSelected(false);
                Question2Answer3.setSelected(false);
                AirApplication.Detection_Question1 = Question2Answer1.getText().toString();
            }
            break;

            case R.id.CheckWeightPage_Button_Question2_Answer2: {
                Question2Answer1.setSelected(false);
                Question2Answer2.setSelected(true);
                Question2Answer3.setSelected(false);
                AirApplication.Detection_Question1 = Question2Answer2.getText().toString();
            }
            break;

            case R.id.CheckWeightPage_Button_Question2_Answer3: {
                Question2Answer1.setSelected(false);
                Question2Answer2.setSelected(false);
                Question2Answer3.setSelected(true);
                AirApplication.Detection_Question1 = Question2Answer3.getText().toString();
            }
            break;

            case R.id.CheckWeightPage_Button_Question3_Answer1: {
                Question3Answer1.setSelected(true);
                Question3Answer2.setSelected(false);
                Question3Answer3.setSelected(false);
                AirApplication.Detection_Question2 = Question3Answer1.getText().toString();
            }
            break;

            case R.id.CheckWeightPage_Button_Question3_Answer2: {
                Question3Answer1.setSelected(false);
                Question3Answer2.setSelected(true);
                Question3Answer3.setSelected(false);
                AirApplication.Detection_Question2 = Question3Answer2.getText().toString();
            }
            break;

            case R.id.CheckWeightPage_Button_Question3_Answer3: {
                Question3Answer1.setSelected(false);
                Question3Answer2.setSelected(false);
                Question3Answer3.setSelected(true);
                AirApplication.Detection_Question2 = Question3Answer3.getText().toString();
            }
            break;

        }
    }

    public View.OnClickListener OnSendData = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            AirApplication.setOnclikcFeedback(v);
            AirApplication.Detection_Weight = EditTextWeight.getText().toString();
            AirApplication.Detection_Height = member.height;

            if (AirApplication.Detection_Weight.isEmpty()
                    || !(AirApplication.Detection_Weight.length() >= 2)) {
                showErrorAlert("錯誤", "請正確填寫問題一");
            } else if (AirApplication.Detection_Question1.isEmpty()) {
                showErrorAlert("錯誤", "請正確填寫問題二");
            } else if (AirApplication.Detection_Question2.isEmpty()) {
                showErrorAlert("錯誤", "請正確填寫問題三");
            } else {
                if (isConnectedNetWork()) {
                    member.weight = EditTextWeight.getText().toString();
                    UpDateMemberData upDateMemberData = new UpDateMemberData();
                    upDateMemberData.execute();
                }
            }
        }
    };

    public void setCheckWeightPage_Status(String weight) {
        float standardＷeight = getStandardＷeight(member.sex, member.height);
        float currentWeight = Float.valueOf(weight);

//        if (standardＷeight == 0 || standardＷeight <= 0) {
//            showErrorAlert("標準體重異常", "錯誤資料 : " + standardＷeight + " 公斤");
//        }

        if ((float) (standardＷeight * 1.1) > currentWeight && currentWeight > (float) (standardＷeight * 0.9)) {
            CheckWeightPage_Status.setText("您的體重符合標準範圍喔！");
            CheckWeightPage_Status.setTextColor(Color.BLACK);
        } else if ((float) (standardＷeight * 1.2) > currentWeight && currentWeight > (float) (standardＷeight * 1.1)) {
            CheckWeightPage_Status.setText("您的體重有點超標喔！");
            CheckWeightPage_Status.setTextColor(getResources().getColor(R.color.color_66C4EF));
        } else if (currentWeight > (float) (standardＷeight * 1.2)) {
            CheckWeightPage_Status.setText("您的體重超標太多囉！");
            CheckWeightPage_Status.setTextColor(getResources().getColor(R.color.color_D0021B));
        } else if ((float) (standardＷeight * 0.9) > currentWeight && currentWeight > (float) (standardＷeight * 0.8)) {
            CheckWeightPage_Status.setText("您的體重低於標準喔！");
            CheckWeightPage_Status.setTextColor(getResources().getColor(R.color.color_66C4EF));
        } else if (currentWeight < (float) (standardＷeight * 0.8)) {
            CheckWeightPage_Status.setText("您的體重過輕囉！");
            CheckWeightPage_Status.setTextColor(getResources().getColor(R.color.color_D0021B));
        } else {
            CheckWeightPage_Status.setText("");
        }
    }

    public float getStandardＷeight(String sex, String Height) {
        float FH = 0;
        float standardＷeight = 0;
        try {
            FH = Float.valueOf(Height);
            switch (sex) {
                case "0": {
//                    Log.e(TAG, "setCheckWeightPage_Status = " + "  sex = " + sex + "  女");
                    standardＷeight = (float) ((FH - 70.0) * 0.6);
                }
                break;
                case "1": {
//                    Log.e(TAG, "setCheckWeightPage_Status = " + "  sex = " + sex + "  男");
                    standardＷeight = (float) ((FH - 80.0) * 0.7);
                }
                break;
            }
        } catch (NumberFormatException e) {
//            showErrorAlert("身高資料異常", "錯誤資料 : " + Height);
        }

        return standardＷeight;
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
        Intent intent = new Intent(currentActivity, MainActivity.class);
        startActivity(intent);
        finish();
        setBackInPrePageAnimation(currentActivity);
    }
}
