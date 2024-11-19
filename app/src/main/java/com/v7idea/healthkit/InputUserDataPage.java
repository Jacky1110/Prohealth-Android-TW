package com.v7idea.healthkit;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.Spinner;

import com.v7idea.healthkit.Model.Member;
import com.v7idea.healthkit.Model.Token;
import com.v7idea.template.Activity.BaseActivity;
import com.v7idea.template.AirApplication;
import com.v7idea.template.DataBase.SimpleDatabase;
import com.v7idea.template.Tool.ApiResult;
import com.v7idea.template.Tool.ListItem;
import com.v7idea.template.Tool.NotifyDialog;
import com.v7idea.template.Tool.Utils;
import com.v7idea.template.Tool.V7ideaAsyncTask;
import com.v7idea.template.Tool.ViewScaling;
import com.v7idea.template.View.AutoReleaseImageView;
import com.v7idea.template.View.V7TitleView;
import com.v7idea.template.View.V7ideaBaseAdapter;
import com.v7idea.v7rcliteandroidsdk.V7RCLiteController;

import org.joda.time.DateTime;

import java.lang.reflect.Array;
import java.util.ArrayList;
/**
 * 2019/1/24 沒有使用的頁面
 */
public class InputUserDataPage extends BaseActivity {
    private static final String TAG = "InputUserDataPage";

    private EditText BirthYear = null;
    private EditText BirthMonth = null;
    private EditText BirthDay = null;

    private Spinner SelectGender = null;
    private Spinner SelectBlood = null;
    private Spinner SelectMode = null;

    private V7TitleView Modify = null;

    private int lastGenderIndex = 0;
    private int lastBloodIndex = 0;
    private int lastDetectionTypeIndex = 0;

    private String[] saveGenderArray = {"F", "M"};
    private String[] saveBloodType = {"0", "1", "2", "3"};
    private String[] saveMode = {"tw-00017", "tw-00018"};//tw-0017:智能，tw-0018:豪華
//    private String[] saveRhType = ["0", "1", "2"]//0:-，1:＋，2:不知道

    private Member member = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_input_user_data_page);

        ScrollView ScrollArea = (ScrollView) ViewScaling.findViewByIdAndScale(currentActivity, R.id.InputUserDataPage_ScrollView_ScrollView);
        LinearLayout ScrollContainer = (LinearLayout) ViewScaling.findViewByIdAndScale(currentActivity, R.id.InputUserDataPage_LinearLayout_ScrollContainer);

        AutoReleaseImageView TopBannerBackground = (AutoReleaseImageView) ViewScaling.findViewByIdAndScale(currentActivity, R.id.InputUserDataPage_AutoReleaseImageView_BannerImage);
        V7TitleView TopBannerTitle = (V7TitleView) ViewScaling.findViewByIdAndScale(currentActivity, R.id.InputUserDataPage_V7TitleView_Title);

        LinearLayout BirthDayContainer = (LinearLayout) ViewScaling.findViewByIdAndScale(currentActivity, R.id.InputUserDataPage_LinearLayout_BirthDayContainer);
        V7TitleView BirthDayTitle = (V7TitleView) ViewScaling.findViewByIdAndScale(currentActivity, R.id.InputUserDataPage_V7TitleView_BirthDayTitle);

        BirthYear = (EditText) ViewScaling.findViewByIdAndScale(currentActivity, R.id.InputUserDataPage_V7TitleView_BirthYear);
        V7TitleView DividerText1 = (V7TitleView) ViewScaling.findViewByIdAndScale(currentActivity, R.id.InputUserDataPage_V7TitleView_DividerText1);

        BirthMonth = (EditText) ViewScaling.findViewByIdAndScale(currentActivity, R.id.InputUserDataPage_V7TitleView_BirthMonth);
        V7TitleView DividerText2 = (V7TitleView) ViewScaling.findViewByIdAndScale(currentActivity, R.id.InputUserDataPage_V7TitleView_DividerText2);

        BirthDay = (EditText) ViewScaling.findViewByIdAndScale(currentActivity, R.id.InputUserDataPage_V7TitleView_BirthDay);


        RelativeLayout GenderArea = (RelativeLayout) ViewScaling.findViewByIdAndScale(currentActivity, R.id.InputUserDataPage_RelativeLayout_GenderArea);
        V7TitleView GenderText = (V7TitleView) ViewScaling.findViewByIdAndScale(currentActivity, R.id.InputUserDataPage_V7TitleView_GenderText);
        SelectGender = (Spinner) ViewScaling.findViewByIdAndScale(currentActivity, R.id.InputUserDataPage_Spinner_SelectGender);

        RelativeLayout BloodArea = (RelativeLayout) ViewScaling.findViewByIdAndScale(currentActivity, R.id.InputUserDataPage_RelativeLayout_BloodArea);
        V7TitleView BloodText = (V7TitleView) ViewScaling.findViewByIdAndScale(currentActivity, R.id.InputUserDataPage_V7TitleView_BloodText);
        SelectBlood = (Spinner) ViewScaling.findViewByIdAndScale(currentActivity, R.id.InputUserDataPage_Spinner_SelectBlood);

        RelativeLayout ModeArea = (RelativeLayout) ViewScaling.findViewByIdAndScale(currentActivity, R.id.InputUserDataPage_RelativeLayout_ModeArea);
        V7TitleView ModeText = (V7TitleView) ViewScaling.findViewByIdAndScale(currentActivity, R.id.InputUserDataPage_V7TitleView_ModeText);
        SelectMode = (Spinner) ViewScaling.findViewByIdAndScale(currentActivity, R.id.InputUserDataPage_Spinner_SelectMode);

        V7TitleView Send = (V7TitleView) ViewScaling.findViewByIdAndScale(currentActivity, R.id.InputUserDataPage_V7TitleView_Send);
        Send.setOnClickListener(onPressSend);

        Modify = (V7TitleView) ViewScaling.findViewByIdAndScale(currentActivity, R.id.InputUserDataPage_V7TitleView_Modify);
        Modify.setOnClickListener(onPressModify);

        AutoReleaseImageView BackIcon = (AutoReleaseImageView) ViewScaling.findViewByIdAndScale(currentActivity, R.id.InputUserDataPage_AutoReleaseImageView_BackIcon);
        BackIcon.setOnClickListener(onPressBackIcon);

        SelectAdapter selectBloodAdapter = new SelectAdapter();
        SelectBlood.setAdapter(selectBloodAdapter);
        selectBloodAdapter.setData(getStringArrayToListItemArray(R.array.Blood_Array));

        SelectAdapter selectGenderAdapter = new SelectAdapter();
        SelectGender.setAdapter(selectGenderAdapter);
        selectGenderAdapter.setData(getStringArrayToListItemArray(R.array.Gender_Array));

        SelectAdapter selectModeAdapter = new SelectAdapter();
        SelectMode.setAdapter(selectModeAdapter);
        selectModeAdapter.setData(getStringArrayToListItemArray(R.array.Mode_Array));

        SelectBlood.setOnItemSelectedListener(onItemSelectedListener);
        SelectGender.setOnItemSelectedListener(onItemSelectedListener);
        SelectMode.setOnItemSelectedListener(onItemSelectedListener);

        loadSaveUserData();
    }

    private ArrayList<ListItem> getStringArrayToListItemArray(int arrayID) {
        String[] stringArray = getResources().getStringArray(arrayID);

        ArrayList<ListItem> listItems = new ArrayList<ListItem>();

        for (int i = 0; i < stringArray.length; i++) {
            String value = stringArray[i];

            SelectItem selectItem = new SelectItem(value);

            listItems.add(selectItem);
        }

        return listItems;
    }

    private boolean checkUserInputData() {
        String birthDayYear = BirthYear.getText().toString();
        String birthDayMonth = BirthMonth.getText().toString();
        String birthDayDay = BirthDay.getText().toString();

        boolean birthDayYearSuccess = false;
        boolean birthDayMonthSuccess = false;
        boolean birthDayDaySuccess = false;

        //取得今天日期
        DateTime currentDate = new DateTime();

        if (birthDayYear.isEmpty() == false && birthDayYear.length() == 4 && Integer.valueOf(birthDayYear) <= currentDate.getYear()) {
            birthDayYearSuccess = true;
        }

        if (birthDayMonth.isEmpty() == false && Integer.valueOf(birthDayMonth) > 0 && Integer.valueOf(birthDayMonth) <= 12) {
            birthDayMonthSuccess = true;
        }

        if (birthDayDay.isEmpty() == false) ;
        {
            String checkBirthDay = birthDayYear + "-" + birthDayMonth + "-" + birthDayDay;
            birthDayDaySuccess = Utils.checkDate(checkBirthDay);
        }

        if (birthDayYearSuccess && birthDayMonthSuccess && birthDayDaySuccess) {
            String checkBirthDate = birthDayYear + "-" + birthDayMonth + "-" + birthDayDay;
            return Utils.checkDate(checkBirthDate);
        } else {
            if (birthDayYearSuccess == false) {
                BirthYear.requestFocus();
                showSoftKeyBoardWhenTouchEditText(BirthYear);
            } else if (birthDayMonthSuccess == false) {
                BirthMonth.requestFocus();
                showSoftKeyBoardWhenTouchEditText(BirthMonth);
            } else if (birthDayDaySuccess == false) {
                BirthDay.requestFocus();
                showSoftKeyBoardWhenTouchEditText(BirthDay);
            }

            return false;
        }
    }

    private void loadSaveUserData() {
        member = new Member();

        SimpleDatabase simpleDatabase = new SimpleDatabase();

        String userBirthDate = simpleDatabase.getStringValueByKey(Constant.USER_BIRTHDAY, "");
        String userGENDER = simpleDatabase.getStringValueByKey(Constant.USER_GENDER, "");
        String userBLOOD = simpleDatabase.getStringValueByKey(Constant.USER_BLOOD, "");
        String userRESELLER = simpleDatabase.getStringValueByKey(Constant.USER_RESELLER, "");

        lastGenderIndex = Utils.checkValueInArrayIndex(userGENDER, saveGenderArray);
        lastBloodIndex = Utils.checkValueInArrayIndex(userBLOOD, saveBloodType);
        lastDetectionTypeIndex = Utils.checkValueInArrayIndex(userRESELLER, saveMode);

        SelectGender.setSelection(lastGenderIndex);
        SelectBlood.setSelection(lastBloodIndex);
        SelectMode.setSelection(lastDetectionTypeIndex);

        if (userBirthDate.isEmpty() == false) {

            if (Utils.checkDate(userBirthDate)) {
                lockedPanel();

                DateTime dateTime = new DateTime(userBirthDate);

                BirthYear.setText(String.valueOf(dateTime.getYear()));
                BirthMonth.setText(String.valueOf(dateTime.getMonthOfYear()));
                BirthDay.setText(String.valueOf(dateTime.getDayOfMonth()));
            }
        } else {
            unlockedPanel();
        }
    }

    //做JOIN collect Data 的動作
    private void saveUserData() {
        SimpleDatabase simpleDatabase = new SimpleDatabase();

        String txtYear = BirthYear.getText().toString();
        String txtMonth = BirthMonth.getText().toString();

        if (txtMonth.length() < 2) {
            txtMonth = "0" + txtMonth;
        }

        String txtDay = BirthDay.getText().toString();

        if (txtDay.length() < 2) {
            txtDay = "0" + txtDay;
        }

        String dateString = txtYear + "-" + txtMonth + "-" + txtDay;
        String newDate = new DateTime(dateString).toString(Utils.DEFAULT_DATE_PATTERN);
        String saveGender = saveGenderArray[lastGenderIndex];
        String saveBlood = saveBloodType[lastBloodIndex];
        String saveDetectionType = saveMode[lastDetectionTypeIndex];

        simpleDatabase.setValueByKey(Constant.USER_BIRTHDAY, newDate);
        simpleDatabase.setValueByKey(Constant.USER_GENDER, saveGender);
        simpleDatabase.setValueByKey(Constant.USER_BLOOD, saveBlood);
        simpleDatabase.setValueByKey(Constant.USER_RESELLER, saveDetectionType);
    }

    private void unlockedPanel() {
        SelectGender.setEnabled(true);
        SelectBlood.setEnabled(true);
        SelectMode.setEnabled(true);
        Modify.setVisibility(View.GONE);

        BirthYear.setEnabled(true);
        BirthMonth.setEnabled(true);
        BirthDay.setEnabled(true);

        SelectGender.setAlpha(1f);
        SelectBlood.setAlpha(1f);
        SelectMode.setAlpha(1f);

        BirthYear.setAlpha(1f);
        BirthMonth.setAlpha(1f);
        BirthDay.setAlpha(1f);
    }

    private void lockedPanel() {
        SelectGender.setEnabled(false);
        SelectBlood.setEnabled(false);
        SelectMode.setEnabled(false);
        Modify.setVisibility(View.VISIBLE);

        BirthYear.setEnabled(false);
        BirthMonth.setEnabled(false);
        BirthDay.setEnabled(false);

        SelectGender.setAlpha(0.5f);
        SelectBlood.setAlpha(0.5f);
        SelectMode.setAlpha(0.5f);

        BirthYear.setAlpha(0.5f);
        BirthMonth.setAlpha(0.5f);
        BirthDay.setAlpha(0.5f);
    }

    private AdapterView.OnItemSelectedListener onItemSelectedListener = new AdapterView.OnItemSelectedListener() {
        @Override
        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
            switch (parent.getId()) {
                case R.id.InputUserDataPage_Spinner_SelectGender: {
                    lastGenderIndex = position;
                }
                break;
                case R.id.InputUserDataPage_Spinner_SelectBlood: {
                    lastBloodIndex = position;
                }
                break;
                case R.id.InputUserDataPage_Spinner_SelectMode: {
                    lastDetectionTypeIndex = position;
                }
                break;
            }
        }

        @Override
        public void onNothingSelected(AdapterView<?> parent) {
        }
    };

    private View.OnClickListener onPressModify = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            AirApplication.setOnclikcFeedback(v);
            unlockedPanel();
        }
    };

    private View.OnClickListener onPressSend = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            AirApplication.setOnclikcFeedback(view);
            if (checkUserInputData()) {
                saveUserData();

                if (isConnectedNetWork()) {

                    SimpleDatabase simpleDatabase = new SimpleDatabase();

                    member.birthday = simpleDatabase.getStringValueByKey(Constant.USER_BIRTHDAY, "");
                    member.sex = "" + lastGenderIndex;
                    member.bloodGroup = "" + lastBloodIndex;

                    UpDateMemberData upDateMemberData = new UpDateMemberData();
                    upDateMemberData.execute();
                }
            }
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
            Intent intent = new Intent(currentActivity, ScanDevicePage.class);
            startActivity(intent);
            finish();
            setTurnInNextPageAnimation(currentActivity);
        }

        @Override
        public void downLoadFail(ApiResult apiResult) {
            if (apiResult.getErrorNo().contentEquals(Constant.ErrorCode.CODE_500)) {
                showErrorAlert(getResources().getString(R.string.no_internet_error500));
            } else if (apiResult.getErrorNo().contentEquals(Constant.ErrorCode.NO_LOGIN)) {
                Token token = new Token();
                token.destroy();

                showErrorAlert(apiResult.getMessage());

                Intent intent = new Intent(currentActivity, LoginPage.class);
                startActivity(intent);
                finish();
                setBackInPrePageAnimation(currentActivity);
            } else if (apiResult.getErrorNo().contentEquals(Constant.ErrorCode.CODE_1002)) {
                showErrorAlert(apiResult.getMessage(), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Intent intent = new Intent(currentActivity, LoginPage.class);
                        startActivity(intent);
                        finish();
                        setBackInPrePageAnimation(currentActivity);

                    }
                });
            } else {
                showErrorAlert(apiResult.getMessage());
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

    private class SelectItem implements ListItem {

        private String selectValue = null;

        public SelectItem(String selectValue) {
            this.selectValue = selectValue;
        }

        @Override
        public String getItemID() {
            return null;
        }

        @Override
        public int getIconResourceID() {
            return 0;
        }

        @Override
        public String getImagePath() {
            return null;
        }

        @Override
        public String getTitle() {
            return selectValue;
        }

        @Override
        public String getSubTitle() {
            return null;
        }

        @Override
        public String getCode() {
            return null;
        }

        @Override
        public int getSerialNo() {
            return 0;
        }

        @Override
        public int getNoticeNumber() {
            return 0;
        }
    }

    private class SelectAdapter extends V7ideaBaseAdapter {

        @Override
        public View getDropDownView(int position, View convertView, ViewGroup parent) {

            ViewHolder viewHolder = null;

            if (convertView == null) {
                convertView = getLayoutInflater().inflate(R.layout.select_list_item, null);

                viewHolder = new ViewHolder();
                viewHolder.init(convertView);

                convertView.setTag(viewHolder);
            } else {
                viewHolder = (ViewHolder) convertView.getTag();
            }

            ListItem item = getItem(position);

            if (item != null) {
//                viewHolder.title.setVisibility(View.GONE);
//                viewHolder.title.setText(R.string.InputUserDataPage_Text_Blood);
                viewHolder.value.setText(item.getTitle());
            }

            return convertView;
        }


        @Override
        public View getItemView(int position, View convertView, ViewGroup parent) {

//            ViewHolder viewHolder = getConvertView(convertView);
//            viewHolder.title.setVisibility(View.VISIBLE);

            ViewHolder viewHolder = null;

            if (convertView == null) {
                convertView = getLayoutInflater().inflate(R.layout.select_list_item, null);

                viewHolder = new ViewHolder();
                viewHolder.init(convertView);

                convertView.setTag(viewHolder);
            } else {
                viewHolder = (ViewHolder) convertView.getTag();
            }

            ListItem item = getItem(position);

            if (item != null) {
//                viewHolder.title.setVisibility(View.VISIBLE);
//                viewHolder.title.setText(R.string.InputUserDataPage_Text_Blood);
                viewHolder.value.setText(item.getTitle());
            }

            return convertView;
        }

        private class ViewHolder {
            //            public V7TitleView title = null;
            public V7TitleView value = null;

            public void init(View convertView) {
//                title = (V7TitleView) ViewScaling.findViewByIdAndScale(convertView,R.id.selectListItem_V7TitleView_title);
                value = (V7TitleView) ViewScaling.findViewByIdAndScale(convertView, R.id.selectListItem_V7TitleView_value);
            }
        }

        private ViewHolder getConvertView(View convertView) {

            ViewHolder viewHolder = null;

            if (convertView == null) {
                convertView = getLayoutInflater().inflate(R.layout.select_list_item, null);

                viewHolder = new ViewHolder();
                viewHolder.init(convertView);

                convertView.setTag(viewHolder);
            } else {
                viewHolder = (ViewHolder) convertView.getTag();
            }

            return viewHolder;
        }
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(currentActivity, MainActivity.class);
        startActivity(intent);
        finish();
        setBackInPrePageAnimation(currentActivity);
    }
}
