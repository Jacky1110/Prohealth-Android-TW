package com.v7idea.healthkit;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.v7idea.MyApp;
import com.v7idea.healthkit.Model.Member;
import com.v7idea.template.Activity.BaseActivity;
import com.v7idea.template.AirApplication;
import com.v7idea.template.DataBase.SimpleDatabase;
import com.v7idea.template.Tool.ApiResult;
import com.v7idea.template.Tool.DownLoad;
import com.v7idea.template.Tool.ViewScaling;
import com.v7idea.template.View.AutoReleaseImageView;
import com.v7idea.template.View.V7TitleView;

import org.joda.time.DateTime;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
/**
 * 2019/1/24 沒有使用的頁面
 */
public class RegisterPage2  extends BaseActivity {

    private static String TAG = "RegisterPage2";

    EditText EditText_Birthday_Year, EditText_Birthday_Month, EditText_Birthday_Day, EditText_Height, EditText_Weight;
    V7TitleView RegisterPage2_Send;
    RadioGroup RadioGroup_BloodType,RadioGroup_Gender;
    RadioButton Gender_man,Gender_woman;
    RadioButton BloodType_A,BloodType_B,BloodType_O,BloodType_AB;
    AirApplication Value = null;
    boolean ifRequestToServer = false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_page2);

        ScrollView ScrollArea = (ScrollView) ViewScaling.findViewByIdAndScale(currentActivity, R.id.RegisterPage2_ScrollView_ScrollView);
        LinearLayout ScrollContainer = (LinearLayout) ViewScaling.findViewByIdAndScale(currentActivity, R.id.RegisterPage2_LinearLayout_ScrollContainer);

        AutoReleaseImageView TopBannerBackground = (AutoReleaseImageView) ViewScaling.findViewByIdAndScale(currentActivity, R.id.RegisterPage2_AutoReleaseImageView_BannerImage);
        V7TitleView TopBannerTitle = (V7TitleView) ViewScaling.findViewByIdAndScale(currentActivity, R.id.RegisterPage2_V7TitleView_Title);

        Value = (AirApplication) getApplicationContext();
        Log.d("第一頁儲存的密碼",""+Value.Password);
        Log.d("第二頁儲存的電話",""+Value.PhoneNumber);

        LinearLayout BirthdayContainer = (LinearLayout) ViewScaling.findViewByIdAndScale(currentActivity, R.id.RegisterPage2_LinearLayout_BirthDayContainer);
        TextView TextView_Birthday = (TextView) ViewScaling.findViewByIdAndScale(currentActivity, R.id.RegisterPage2_TextView_Birthday);
        TextView DividerText1 = (TextView) ViewScaling.findViewByIdAndScale(currentActivity,R.id.RegisterPage2_V7TitleView_DividerText1);
        TextView DividerText2 = (TextView) ViewScaling.findViewByIdAndScale(currentActivity,R.id.RegisterPage2_V7TitleView_DividerText2);
        EditText_Birthday_Year = (EditText) ViewScaling.findViewByIdAndScale(currentActivity, R.id.RegisterPage2_V7TitleView_BirthYear);
        EditText_Birthday_Month = (EditText) ViewScaling.findViewByIdAndScale(currentActivity, R.id.RegisterPage2_V7TitleView_BirthMonth);
        EditText_Birthday_Day = (EditText) ViewScaling.findViewByIdAndScale(currentActivity, R.id.RegisterPage2_V7TitleView_BirthDay);

        RelativeLayout HeightContainer = (RelativeLayout) ViewScaling.findViewByIdAndScale(currentActivity, R.id.RegisterPage2_RelativeLayout_Height);
        TextView TextView_Height = (TextView) ViewScaling.findViewByIdAndScale(currentActivity, R.id.RegisterPage2_TextView_Height);
        EditText_Height = (EditText) ViewScaling.findViewByIdAndScale(currentActivity, R.id.RegisterPage2_EditText_Height);

        RelativeLayout WeightContainer = (RelativeLayout) ViewScaling.findViewByIdAndScale(currentActivity, R.id.RegisterPage2_RelativeLayout_Weight);
        TextView TextView_Weight = (TextView) ViewScaling.findViewByIdAndScale(currentActivity, R.id.RegisterPage2_TextView_Weight);
        EditText_Weight = (EditText) ViewScaling.findViewByIdAndScale(currentActivity, R.id.RegisterPage2_EditText_Weight);

        RelativeLayout GenderContainer = (RelativeLayout) ViewScaling.findViewByIdAndScale(currentActivity, R.id.RegisterPage2_RelativeLayout_Gender);
        TextView TextView_Gender = (TextView) ViewScaling.findViewByIdAndScale(currentActivity, R.id.RegisterPage2_TextView_Gender);
//        RadioGroup_Gender = (RadioGroup) ViewScaling.findViewByIdAndScale(currentActivity, R.id.RegisterPage2_RadioGroup_Gender);

        RelativeLayout BloodTypeContainer = (RelativeLayout) ViewScaling.findViewByIdAndScale(currentActivity, R.id.RegisterPage2_RelativeLayout_BloodType);
        TextView TextView_BloodType = (TextView) ViewScaling.findViewByIdAndScale(currentActivity, R.id.RegisterPage2_TextView_BloodType);

        RadioGroup_Gender = (RadioGroup) ViewScaling.findViewByIdAndScale(currentActivity, R.id.RegisterPage2_RadioGroup_Gender);

        RadioGroup_BloodType = (RadioGroup) ViewScaling.findViewByIdAndScale(currentActivity, R.id.RegisterPage2_RadioGroup_BloodType);

        Gender_man = (RadioButton) ViewScaling.findViewByIdAndScale(currentActivity,R.id.Gender_RadioButton_man);
        Gender_woman = (RadioButton) ViewScaling.findViewByIdAndScale(currentActivity,R.id.Gender_RadioButton_woman);

        BloodType_A = (RadioButton) ViewScaling.findViewByIdAndScale(currentActivity,R.id.BloodType_RadioButton_BloodType_A);
        BloodType_B = (RadioButton) ViewScaling.findViewByIdAndScale(currentActivity,R.id.BloodType_RadioButton_BloodType_B);
        BloodType_O = (RadioButton) ViewScaling.findViewByIdAndScale(currentActivity,R.id.BloodType_RadioButton_BloodType_O);
        BloodType_AB = (RadioButton) ViewScaling.findViewByIdAndScale(currentActivity,R.id.BloodType_RadioButton_BloodType_AB);

        RegisterPage2_Send = (V7TitleView) ViewScaling.findViewByIdAndScale(currentActivity, R.id.RegisterPage2_V7TitleView_Send2);
        AutoReleaseImageView BackIcon = (AutoReleaseImageView) ViewScaling.findViewByIdAndScale(currentActivity, R.id.RegisterPage2_AutoReleaseImageView_BackIcon);
        BackIcon.setOnClickListener(onPressBackIcon);
        ScrollArea.setOnTouchListener(onTouchBlank);
        RegisterPage2_Send.setOnClickListener(onClickSend);

    }

    private View.OnClickListener onClickSend = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            AirApplication.setOnclikcFeedback(view);
            if(DownLoad.isConnectInternet()){

                int selectedRadioButtonID_Gender = RadioGroup_Gender.getCheckedRadioButtonId();
                //假如Gender沒有被選
                if(selectedRadioButtonID_Gender == -1){
                    ShowMessageDialog(getResources().getString(R.string.REGISTER2_ERROR_GENDER_EMPTY));
                }else{
                    RadioButton selectedRadioButton_Gender = (RadioButton) findViewById(selectedRadioButtonID_Gender);

                    int selectedRadioButtonID_BloodType = RadioGroup_BloodType.getCheckedRadioButtonId();
                    //BloodType沒有被選
                    if(selectedRadioButtonID_BloodType == -1){
                        ShowMessageDialog(getResources().getString(R.string.REGISTER2_ERROR_BLOODTYPE_EMPTY));
                    }else{
                        RadioButton selectedRadioButton_BloodType = (RadioButton) findViewById(selectedRadioButtonID_BloodType);
//生日資料形式 yyyy-mm-dd
                        String BirthdayString = EditText_Birthday_Year.getText().toString()+"-"+EditText_Birthday_Month.getText().toString()+"-"+EditText_Birthday_Day.getText().toString();
                        String HeightString = EditText_Height.getText().toString();
                        String WeightString = EditText_Weight.getText().toString();
                        String GenderString = selectedRadioButton_Gender.getText().toString();
                        String BloodTypeString = selectedRadioButton_BloodType.getText().toString();

                        boolean isInputBirthdayIsEmpty = false;

                        boolean isInputWeightIsEmpty = false;
                        boolean isInputHeightIsEmpty = false;

                        boolean isInputGenderIsEmpty = false;
                        boolean isInputBloodTypeIsEmpty = false;
                        //生日格式有誤
                        boolean isInputBirthdayIsInCorrect = false;
                        //日期超出合理範圍
                        boolean isInputBirthdayMonthIsInCorrect = false;
                        boolean isInputBirthdayDayIsInCorrect = false;
                        //判斷年月日是否為空
                        if(EditText_Birthday_Day.getText().toString().isEmpty()||EditText_Birthday_Month.getText().toString().isEmpty()||EditText_Birthday_Year.getText().toString().isEmpty()){
                            //其中有空值
                            isInputBirthdayIsEmpty = true;
                        }
                        isInputWeightIsEmpty = WeightString.isEmpty();
                        isInputHeightIsEmpty = HeightString.isEmpty();
                        isInputGenderIsEmpty = GenderString.isEmpty();
                        isInputBloodTypeIsEmpty = BloodTypeString.isEmpty();

                        if(isInputBirthdayIsEmpty==false){
                            //生日日期沒有空值

                                //格式無誤檢查月日有無超出合理範圍
                                int month = Integer.parseInt(EditText_Birthday_Month.getText().toString());
                                int day = Integer.parseInt(EditText_Birthday_Day.getText().toString());
                                if(month>12||month<1){
                                    //月份超出合理範圍
                                    isInputBirthdayMonthIsInCorrect = true;
                                }
                                else if(day>31||day<1){
                                    //日期超出合理範圍
                                    isInputBirthdayDayIsInCorrect = true;
                                }else{
                                    if(checkBirthdayFormat(BirthdayString)){
                                        //生日日期格式有誤
                                        isInputBirthdayIsInCorrect = true;
                                    }
                                }

                        }


                        if(isInputBirthdayIsEmpty){
                            //生日選項不能空白
                            alertDialogShow(EditText_Birthday_Year, getResources().getString(R.string.REGISTER2_ERROR_BIRTHDAY_EMPTY));
                        }
                        else if(isInputBirthdayIsInCorrect){
                            alertDialogShow(EditText_Birthday_Year,getResources().getString(R.string.REGISTER2_ERROR_BIRTHDAY_IS_NOT_CORRECT));
                        }else if(isInputBirthdayMonthIsInCorrect){
                            alertDialogShow(EditText_Birthday_Month,getResources().getString(R.string.REGISTER2_ERROR_BIRTHDAY_MONTH_IS_NOT_CORRECT));
                        }else if(isInputBirthdayDayIsInCorrect){
                            alertDialogShow(EditText_Birthday_Day,getResources().getString(R.string.REGISTER2_ERROR_BIRTHDAY_DAY_IS_NOT_CORRECT));
                        }
                        else if(isInputHeightIsEmpty) {
                            //身高選項不能空白
                            alertDialogShow(EditText_Height, getResources().getString(R.string.REGISTER2_ERROR_HEIGHT_EMPTY));
                        }else if(checkNum(HeightString)==false){
                            //不是數字
                            alertDialogShow(EditText_Height, getResources().getString(R.string.REGISTER2_ERROR_HEIGHT_IS_NOT_CORRECT));
                        }
                        else if(isInputWeightIsEmpty){
                            //體重選項不能空白
                            alertDialogShow(EditText_Weight, getResources().getString(R.string.REGISTER2_ERROR_WEIGHT_EMPTY));
                        }else if(checkNum(WeightString)==false){
                            //不是數字
                            alertDialogShow(EditText_Weight, getResources().getString(R.string.REGISTER2_ERROR_WEIGHT_IS_NOT_CORRECT));
                        }

                        if(isInputBirthdayIsEmpty == false && isInputHeightIsEmpty == false
                                && isInputWeightIsEmpty == false && isInputGenderIsEmpty == false
                                && isInputBloodTypeIsEmpty == false && isInputBirthdayDayIsInCorrect==false
                                && isInputBirthdayDayIsInCorrect==false && isInputBirthdayMonthIsInCorrect==false
                                && checkNum(HeightString)==true && checkNum(WeightString) == true){

                            if(ifRequestToServer == false){
                                ifRequestToServer = true;

                                RegisterPage2.Rregistering Rregistering = new RegisterPage2.Rregistering();
                                Rregistering.execute(Value.PhoneNumber, Value.Password, Value.LastName, Value.FirstName, Value.Email, BirthdayString,HeightString,WeightString,BloodTypeString);
                            }
                        }
                    }

                }

            }
        }
    };

    private class Rregistering extends AsyncTask<String, Void, ApiResult> {
        @Override
        protected ApiResult doInBackground(String... params) {
            if (params != null && params.length > 0) {

                SimpleDatabase simpleDatabase = new SimpleDatabase();
                simpleDatabase.setValueByKey(Constant.TEMP_ACCOUNT, params[0]);
                simpleDatabase.setValueByKey(Constant.TEMP_PASSWORD, params[1]);

                Member register = new Member();
                ApiResult registerResult = register.createNewMember(params[0], params[1], params[2], params[3], params[4]);
                return registerResult;
            }
            return null;
        }

        @Override
        protected void onPostExecute(ApiResult apiResult) {
            super.onPostExecute(apiResult);

            ifRequestToServer = false;

            if (apiResult != null) {
                String status = apiResult.getErrorNo();

                Log.d(TAG, "status: " + status);

                if (status.contentEquals(Constant.ErrorCode.SUCCESS)) {
                    Intent intent = new Intent(currentActivity, CheckCodePage.class);
                    startActivity(intent);
                    finish();
                    setBackInPrePageAnimation(currentActivity);
                }else {
                    String strMessage = apiResult.getMessage();
                    AlertDialog.Builder builder = new AlertDialog.Builder(currentActivity);
                    builder.setMessage(strMessage);
                    builder.setPositiveButton(R.string.determine, null);
                    builder.show();
                }
            }
        }
    }

    private void ShowMessageDialog(String Msg){
        AlertDialog.Builder MyAlertDialog = new AlertDialog.Builder(this);
        MyAlertDialog.setTitle("提醒");
        MyAlertDialog.setMessage(Msg);
        DialogInterface.OnClickListener okClick = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        };
        MyAlertDialog.setNeutralButton("確定",okClick);
        MyAlertDialog.show();
    }

    public void alertDialogShow(final EditText v, String str) {

        new AlertDialog.Builder(RegisterPage2.this)
                .setMessage(str)//設定顯示的文字
                .setPositiveButton(R.string.determine, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        v.requestFocus();
                        InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
                        imm.toggleSoftInput(0, InputMethodManager.HIDE_NOT_ALWAYS);
                    }
                })
                .setCancelable(false)
                .show();//呈現對話視窗
    }

    private View.OnTouchListener onTouchBlank = new View.OnTouchListener() {

        @Override
        public boolean onTouch(View v, MotionEvent event) {
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(RegisterPage2.this.getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
                    break;
            }

            return false;
        }
    };

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

    public boolean checkBirthdayFormat(String Str) {
//        String pattern = "^[0-9]{4}-[0-9]{2}-[0-9]{2}";
////        Matcher m = p.matcher(Str);
//        if (Str.matches(pattern)) {
//            return true;
//        } else {
//            return false;
//        }
        SimpleDateFormat df = new SimpleDateFormat("yyyy-mm-dd");
        try {
            df.parse(Str);
            return false;
        } catch (ParseException e) {
            return true;
        }
    }

    public boolean checkBirthdayTime(String Str){
        //確認時間是否超過現在時間
        DateTime dt = new DateTime(Str);
        if(dt.isAfterNow()){
            return true;
        }else{
            return false;
        }
    }


    //驗證數字
    public boolean checkNum(String Str) {
        Pattern p = Pattern.compile("^[0-9]*$");
        Matcher m = p.matcher(Str);
        if (m.matches()) {
            return true;
        } else {
            return false;
        }
    }
}
