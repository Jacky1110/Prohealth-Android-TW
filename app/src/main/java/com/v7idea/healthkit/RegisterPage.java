package com.v7idea.healthkit;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.text.InputType;
import android.text.method.DigitsKeyListener;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.v7idea.healthkit.Model.Member;
import com.v7idea.healthkit.Model.Token;
import com.v7idea.healthkit.View.BottomButton;
import com.v7idea.healthkit.View.TitleEditTextView;
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

import java.lang.reflect.Type;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
/**
 * 2019/1/24 ＊確認使用的頁面 註冊-填寫個人資料
 */

public class RegisterPage extends BaseActivity {
    private static String TAG = "RegisterPage";
    TitleEditTextView LastName, FirstName, PhoneNumber, Email, Password, ConfirmPassword;

    V7TitleView RegisterPage_Send;
    boolean ifRequestToServer = false;
    AirApplication Value;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_page);

        Value = (AirApplication) getApplication();

        ScrollView ScrollArea = (ScrollView) ViewScaling.findViewByIdAndScale(currentActivity, R.id.RegisterPage_ScrollView_ScrollView);
        LinearLayout ScrollContainer = (LinearLayout) ViewScaling.findViewByIdAndScale(currentActivity, R.id.RegisterPage_LinearLayout_ScrollContainer);

        AutoReleaseImageView TopBannerBackground = (AutoReleaseImageView) ViewScaling.findViewByIdAndScale(currentActivity, R.id.RegisterPage_AutoReleaseImageView_BannerImage);
        ViewScaling.setImageView(TopBannerBackground);
        V7TitleView TopBannerTitle = (V7TitleView) ViewScaling.findViewByIdAndScale(currentActivity, R.id.RegisterPage_V7TitleView_Title);

        BottomButton BottomButton = (BottomButton) ViewScaling.findViewByIdAndScale(currentActivity, R.id.RegisterPage_BottomButton);
        BottomButton.setData(getResources().getString(R.string.RegisterPage_Text_Cancel), getResources().getString(R.string.RegisterPage_Text_Send), 100);

        V7TitleView RegisterPage_Cancle = BottomButton.getLeftButton();
        RegisterPage_Send = BottomButton.getRightButton();

        LastName = (TitleEditTextView) ViewScaling.findViewByIdAndScale(currentActivity, R.id.RegisterPage_TitleEditTextView_LastName);
        LastName.setData(getString(R.string.RegisterPage_Text_LastName), getString(R.string.RegisterPage_Hint_LastName), TitleEditTextView.Type1_90px);
        LastName.setTitleLeftPadding(30);

        FirstName = (TitleEditTextView) ViewScaling.findViewByIdAndScale(currentActivity, R.id.RegisterPage_TitleEditTextView_FirstName);
        FirstName.setData(getString(R.string.RegisterPage_Text_FirstName), getString(R.string.RegisterPage_Hint_FirstName), TitleEditTextView.Type1_90px);
        FirstName.setTitleLeftPadding(30);

        PhoneNumber = (TitleEditTextView) ViewScaling.findViewByIdAndScale(currentActivity, R.id.RegisterPage_TitleEditTextView_PhoneNumber);
        PhoneNumber.setData(getString(R.string.RegisterPage_Text_PhoneNumber), getString(R.string.RegisterPage_Hint_PhoneNumber), TitleEditTextView.Type1_90px);
        PhoneNumber.setTitleLeftPadding(30);

        Email = (TitleEditTextView) ViewScaling.findViewByIdAndScale(currentActivity, R.id.RegisterPage_TitleEditTextView_Email);
        Email.setData(getString(R.string.RegisterPage_Text_Email), getString(R.string.RegisterPage_Hint_Email), TitleEditTextView.Type1_90px);
        Email.setTitleLeftPadding(30);

        Password = (TitleEditTextView) ViewScaling.findViewByIdAndScale(currentActivity, R.id.RegisterPage_TitleEditTextView_Password);
        Password.setData(getString(R.string.RegisterPage_Text_Password), getString(R.string.RegisterPage_Hint_Password), TitleEditTextView.Type1_90px);
        Password.EditText.setTransformationMethod(PasswordTransformationMethod.getInstance());
        Password.setTitleLeftPadding(30);

        ConfirmPassword = (TitleEditTextView) ViewScaling.findViewByIdAndScale(currentActivity, R.id.RegisterPage_TitleEditTextView_ConfirmPassword);
        ConfirmPassword.setData(getString(R.string.RegisterPage_Text_ConfirmPassword), getString(R.string.RegisterPage_Hint_ConfirmPassword), TitleEditTextView.Type1_90px);
        ConfirmPassword.EditText.setTransformationMethod(PasswordTransformationMethod.getInstance());
        ConfirmPassword.setTitleLeftPadding(30);

        RegisterPage_Cancle.setOnClickListener(onClickBack);
        ScrollArea.setOnTouchListener(onTouchBlank);
        RegisterPage_Send.setOnClickListener(onClickSend);
    }

    private View.OnTouchListener onTouchBlank = new View.OnTouchListener() {

        @Override
        public boolean onTouch(View v, MotionEvent event) {
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(RegisterPage.this.getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
                    break;
            }

            return false;
        }
    };

    private View.OnClickListener onClickSend = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            AirApplication.setOnclikcFeedback(view);
            if (DownLoad.isConnectInternet()) {
                String LastNameString = LastName.EditText.getText().toString();
                String FirstNameString = FirstName.EditText.getText().toString();
                String PhoneNumberString = PhoneNumber.EditText.getText().toString();
                String EmailString = Email.EditText.getText().toString();
                String PasswordString = Password.EditText.getText().toString();
                String ConfirmPasswordString = ConfirmPassword.EditText.getText().toString();

                boolean isInputAccountIsEmpty = false;
                boolean isInputAccountLengthMoreThan15 = false;
                boolean isLikeMobilePhoneNumber = false;

                boolean isInputPasswordIsEmpty = false;
                boolean isInputPasswordLengthIs8OrMore = false;
                boolean isInputPasswordHaveNumber = false;
                boolean isInputConfirmPasswordIsEmpty = false;
                boolean isConfirmPasswordNotTheSamePassword = false;

                boolean isInputFirstNameIsEmpty = false;
                boolean isInputLastNameIsEmpty = false;
                boolean isInputEmailIsEmpty = false;
                boolean isInputEmailFormatError = false;

                isInputAccountIsEmpty = PhoneNumberString.isEmpty();
                isInputPasswordIsEmpty = PasswordString.isEmpty();
                isInputConfirmPasswordIsEmpty = ConfirmPasswordString.isEmpty();
                isInputFirstNameIsEmpty = FirstNameString.isEmpty();
                isInputLastNameIsEmpty = LastNameString.isEmpty();
               // isInputEmailIsEmpty = EmailString.isEmpty();

                if (isInputAccountIsEmpty == false) {
                    isLikeMobilePhoneNumber = checkMobileNumber(PhoneNumberString);
                }

                if (isInputPasswordIsEmpty == false) {

                    isInputPasswordLengthIs8OrMore = PasswordString.length() >= 8;

                    isInputPasswordHaveNumber = checkEnglishNum(PasswordString);

                    if (isInputPasswordHaveNumber && isInputConfirmPasswordIsEmpty == false) {
                        isConfirmPasswordNotTheSamePassword = !(PasswordString.contentEquals(ConfirmPasswordString));
                    }
                }

              //  if (isInputEmailIsEmpty == false) {
              //      isInputEmailFormatError = !(checkEmail(EmailString));
              //  }

                if (isInputLastNameIsEmpty) {
                    alertDialogShow(LastName.EditText, getResources().getString(R.string.REGISTER_ERROR_LAST_NAME_EMPTY));
                } else if (isInputFirstNameIsEmpty) {
                    alertDialogShow(FirstName.EditText, getResources().getString(R.string.REGISTER_ERROR_FIRST_NAME_EMPTY));
                } else if (isInputAccountIsEmpty) {
                    alertDialogShow(PhoneNumber.EditText, getResources().getString(R.string.REGISTER_ERROR_ACCOUNT_EMPTY));
                } else if (isInputAccountLengthMoreThan15) {
                    alertDialogShow(PhoneNumber.EditText, getResources().getString(R.string.REGISTER_ERROR_ACCOUNT_LENGTH_MORE_THAN_15));
                } else if (isLikeMobilePhoneNumber == false) {
                    alertDialogShow(PhoneNumber.EditText, getResources().getString(R.string.REGISTER_ERROR_ACCOUNT_IS_NOT_PHONE));
                } else if (isInputEmailIsEmpty) {
                    alertDialogShow(Email.EditText, getResources().getString(R.string.REGISTER_ERROR_EMAIL_EMPTY));
                } else if (isInputEmailFormatError) {
                    alertDialogShow(Email.EditText, getResources().getString(R.string.REGISTER_ERROR_EMAIL_FORMAT));
                } else if (isInputPasswordIsEmpty) {
                    alertDialogShow(Password.EditText, getResources().getString(R.string.REGISTER_ERROR_PASSWORD_EMPTY));
                } else if (isInputPasswordLengthIs8OrMore == false) {
                    alertDialogShow(Password.EditText, getResources().getString(R.string.REGISTER_ERROR_PASSWORD_LENGTH_MORE_THAN_8));
                } else if (isInputPasswordHaveNumber == false) {
                    alertDialogShow(Password.EditText, getResources().getString(R.string.REGISTER_ERROR_PASSWORD_HAVE_NUM));
                } else if (isInputConfirmPasswordIsEmpty) {
                    alertDialogShow(ConfirmPassword.EditText, getResources().getString(R.string.REGISTER_ERROR_CONFIRM_PASSWORD_EMPTY));
                } else if (isConfirmPasswordNotTheSamePassword) {
                    alertDialogShow(ConfirmPassword.EditText, getResources().getString(R.string.REGISTER_ERROR_PASSWORD_NOT_THESAME));
                }


                if (isInputAccountIsEmpty == false && isInputAccountLengthMoreThan15 == false
                        && isInputEmailFormatError == false && isInputPasswordIsEmpty == false
                        && isInputPasswordLengthIs8OrMore && isInputPasswordHaveNumber
                        && isInputConfirmPasswordIsEmpty == false && isConfirmPasswordNotTheSamePassword == false
                        && isInputLastNameIsEmpty == false && isInputFirstNameIsEmpty == false
                        && isInputEmailIsEmpty == false && isInputEmailFormatError == false
                        && isLikeMobilePhoneNumber == true) {

                    //目前沒用到第二頁
//                    Value.PhoneNumber=PhoneNumberString;
//                    Value.Password=PasswordString;
//                    Value.LastName=LastNameString;
//                    Value.FirstName=FirstNameString;
//                    Value.Email=EmailString;
//                    Intent intent = new Intent(currentActivity, RegisterPage2.class);
//                    startActivity(intent);
//                    finish();
//                    setBackInPrePageAnimation(currentActivity);
                    //改到第二頁在做資料傳送

                    if (ifRequestToServer == false) {
                        ifRequestToServer = true;


                        Rregistering Rregistering = new Rregistering();

                        //String memberNo, String password, String firstName, String lastName, String email
                        Rregistering.execute(PhoneNumberString, PasswordString, FirstNameString, LastNameString, EmailString);
                    }
                }
            } else {
                showErrorAlert(getResources().getString(R.string.no_internet_can_not_register));
            }

        }
    };

    private class Rregistering extends V7ideaAsyncTask<String, ApiResult> {

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
            Intent intent = new Intent(currentActivity, CheckCodePage.class);
            startActivity(intent);
            finish();
            setTurnInNextPageAnimation(currentActivity);

            Log.d(TAG, "Rregistering: " + "SUCCESS");
            Log.d(TAG, "Rregistering: " + result.toString());

        }

        @Override
        public void downLoadFail(ApiResult apiResult) {
            if (apiResult.getErrorNo().contentEquals(Constant.ErrorCode.CODE_500)) {
                showErrorAlert(getResources().getString(R.string.no_internet_error500));
            } else {
                showErrorAlert(apiResult.getMessage());
            }
            ifRequestToServer = false;
        }

        @Override
        protected ApiResult doInBackground(String... params) {
            if (params != null && params.length > 0) {

                SimpleDatabase simpleDatabase = new SimpleDatabase();
                simpleDatabase.setValueByKey(Constant.TEMP_ACCOUNT, params[0]);
                simpleDatabase.setValueByKey(Constant.TEMP_PASSWORD, params[1]);
                //String memberNo, String password, String firstName, String lastName, String email
                Member register = new Member();
                ApiResult registerResult = register.createNewMember(params[0], params[1], params[2], params[3], params[4]);
                return registerResult;
            }
            return null;
        }
    }

    public void alertDialogShow(final EditText v, String str) {

        new AlertDialog.Builder(RegisterPage.this)
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

    //checkWord字數限制//0=檢查到未輸入//1=通過檢查//2=回傳MinNum//3=回傳MaxNum
    public int checkWord(String string, int MinNum, int MaxNum) {
        //MinNum,MaxNum=0,不做驗證,只檢查是否未輸入
        boolean setMinNum = false;
        boolean setMaxNum = false;
        if (string.isEmpty()) {
            return 0;
        }
        if (MinNum > 0) {
            setMinNum = true;
        }
        if (MaxNum > 0) {
            setMaxNum = true;
        }
        //最大最小都有限制字數
        if (setMinNum && setMaxNum) {
            if (string.length() >= MinNum && string.length() <= MaxNum) {
                return 1;
            }
            if (!(string.length() <= MinNum)) {
                return 2 + MinNum;
            }
            if (!(string.length() >= MaxNum)) {
                return 3 + MaxNum;
            }

        }
        //最小限制字數
        if (setMinNum) {
            if (string.length() >= MinNum) {
                return 1;
            } else {
                return 2;
            }
        }
        //最大限制字數
        if (setMaxNum) {
            if (string.length() <= MaxNum) {
                return 1;
            } else {
                return 3;
            }
        }

        return 1;

    }

    private boolean checkMobileNumber(String number){
        //只有第一碼可以輸入"+"號
        if(number.length() >= 2){
            String first = "" + number.charAt(0);

            if(first.contentEquals("+")){
                String otherString = number.substring(1, number.length() - 1);
                return checkNum(otherString);
            }else{
                return checkNum(number);
            }
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

    //驗證Email
    public boolean checkEmail(String Str) {
        Pattern p = Pattern.compile("^\\w+([-+.]\\w+)*@\\w+([-.]\\w+)*\\.\\w+([-.]\\w+)*$");
        Matcher m = p.matcher(Str);
        if (Str.length() > 8 && m.matches()) {
            return true;
        } else {
            return false;
        }
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

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(currentActivity, TermsOfServicePage.class);
        startActivity(intent);
        finish();
        setBackInPrePageAnimation(currentActivity);
    }
}
