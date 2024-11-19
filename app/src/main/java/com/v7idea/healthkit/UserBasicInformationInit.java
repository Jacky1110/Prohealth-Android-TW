package com.v7idea.healthkit;

import android.Manifest;
import android.app.AlertDialog;
import android.content.ContentResolver;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.StrictMode;
import android.provider.MediaStore;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import android.text.InputType;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.v7idea.healthkit.Model.Member;
import com.v7idea.healthkit.Model.Token;
import com.v7idea.healthkit.View.TitleEditTextView;
import com.v7idea.template.Activity.BaseActivity;
import com.v7idea.template.AirApplication;
import com.v7idea.template.Tool.ApiResult;
import com.v7idea.template.Tool.NotifyDialog;
import com.v7idea.template.Tool.V7ideaAsyncTask;
import com.v7idea.template.Tool.ViewScaling;
import com.v7idea.template.View.Banner;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 2019/1/24 ＊確認使用的頁面 進行量測-第一次量測
 */
public class UserBasicInformationInit extends BaseActivity
{
    private static final String TAG = "UserBasicInformationIni";
    EditText Birthday_Year, Birthday_Ｍonth;
    Button Gender_Female, Gender_Male;
    Button Blood_A, Blood_B, Blood_O, Blood_AB;
    Button Button_Back, Button_Next;
    TitleEditTextView LastName, FirstName, Height, Weight;
    RelativeLayout BirthdayReLayout, GenderReLayout, BloodTypeReLayout;
    LinearLayout ButtonContainer;
    int Gender = -1;
    int BloodType = -1;

    ImageView Profile;
    TextView editPhoto;

    // 啟動功能用的請求代碼
    private static final int START_CAMERA = 0;
    private static final int START_CAMERA_RequestCode = 100;
    private static final int START_ALBUM = 1;
    private static final int START_ALBUM_RequestCode = 101;

    private String profilePath = "";

    Member member = new Member();

    String member_lastName, member_firstName, member_birthday, member_sex,
            member_bloodGroup, member_height, member_weight, member_userPhoto;


    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_basic_information_init);

        initPhotoError();

        Banner banner = (Banner) ViewScaling.findViewByIdAndScale(currentActivity, R.id.UserBasicInformation_Init_Banner_Header);
        banner.initShortBanner();
        banner.backIcon.setOnClickListener(OnBackIconClick);
        banner.title.setText(R.string.UserBasicInformation_Init_Text_Title);
        banner.title.setTextColor(Color.parseColor("#FFFFFF"));


        LinearLayout linearLayout = (LinearLayout) ViewScaling.findViewByIdAndScale(currentActivity, R.id.UserBasicInformation_Init_LinearLayout_ScrollContainer);
        Profile = (ImageView) ViewScaling.findViewByIdAndScale(currentActivity, R.id.UserBasicInformation_Init_AutoReleaseImageView_BannerImage);

        editPhoto = (TextView) ViewScaling.findViewByIdAndScale(currentActivity, R.id.UserBasicInformation_Init_V7TitleView_EditPhoto);
        editPhoto.setOnClickListener(OnEditPhotoClick);


        RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams((int) (165 * ViewScaling.getScaleValue()), RelativeLayout.LayoutParams.MATCH_PARENT);
        layoutParams.leftMargin = (int) (55 * ViewScaling.getScaleValue());

        LastName = (TitleEditTextView) ViewScaling.findViewByIdAndScale(currentActivity, R.id.UserBasicInformation_Init_TitleEditTextView_LastName);
        LastName.setData(getString(R.string.UserBasicInformation_Text_LastName), getString(R.string.UserBasicInformation_Hint_LastName), TitleEditTextView.Type2_100px);
        LastName.Title.setTextColor(getResources().getColor(R.color.color_626262));
        LastName.Title.setGravity(Gravity.LEFT | Gravity.CENTER_VERTICAL);
        LastName.Title.setLayoutParams(layoutParams);

        FirstName = (TitleEditTextView) ViewScaling.findViewByIdAndScale(currentActivity, R.id.UserBasicInformation_Init_TitleEditTextView_FirstName);
        FirstName.setData(getString(R.string.UserBasicInformation_Text_FirstName), getString(R.string.UserBasicInformation_Hint_FirstName), TitleEditTextView.Type2_100px);
        FirstName.Title.setTextColor(getResources().getColor(R.color.color_626262));
        FirstName.Title.setGravity(Gravity.LEFT | Gravity.CENTER_VERTICAL);
        FirstName.Title.setLayoutParams(layoutParams);
        FirstName.EditText.setNextFocusDownId(R.id.UserBasicInformation_Init_Editext_Birthday_Year);


        BirthdayReLayout = (RelativeLayout) ViewScaling.findViewByIdAndScale(currentActivity, R.id.UserBasicInformation_Init_RelativeLayout_Birthday);
        TextView BirthdayTextView = (TextView) ViewScaling.findViewByIdAndScale(currentActivity, R.id.UserBasicInformation_Init_TextView_Birthday);

        LinearLayout BirthdayContainer = (LinearLayout) ViewScaling.findViewByIdAndScale(currentActivity, R.id.UserBasicInformation_Init_LinearLayout_BirthdayContainer);

        TextView Dividing_Line = (TextView) ViewScaling.findViewByIdAndScale(currentActivity, R.id.UserBasicInformation_Init_TextView_Dividing_Line);

        Birthday_Year = (EditText) ViewScaling.findViewByIdAndScale(currentActivity, R.id.UserBasicInformation_Init_Editext_Birthday_Year);
        Birthday_Ｍonth = (EditText) ViewScaling.findViewByIdAndScale(currentActivity, R.id.UserBasicInformation_Init_Editext_Birthday_Ｍonth);
        Birthday_Year.setInputType(InputType.TYPE_CLASS_NUMBER);
        Birthday_Ｍonth.setInputType(InputType.TYPE_CLASS_NUMBER);

        GenderReLayout = (RelativeLayout) ViewScaling.findViewByIdAndScale(currentActivity, R.id.UserBasicInformation_Init_RelativeLayout_Gender);
        TextView GenderTextView = (TextView) ViewScaling.findViewByIdAndScale(currentActivity, R.id.UserBasicInformation_Init_TextView_Gender);
        RelativeLayout GenderContainer = (RelativeLayout) ViewScaling.findViewByIdAndScale(currentActivity, R.id.UserBasicInformation_Init_RelativeLayout_GenderContainer);
        LinearLayout LinearLayout_GenderContainer_Init = (LinearLayout) ViewScaling.findViewByIdAndScale(currentActivity, R.id.LinearLayout_GenderContainer_Init);
        Gender_Female = (Button) ViewScaling.findViewByIdAndScale(currentActivity, R.id.UserBasicInformation_Init_Button_Gender_Female);
        Gender_Male = (Button) ViewScaling.findViewByIdAndScale(currentActivity, R.id.UserBasicInformation_Init_Button_Gender_Male);
        setButtonPaddingSize(LinearLayout_GenderContainer_Init);

        Gender_Female.setOnClickListener(OnGenderClick);
        Gender_Male.setOnClickListener(OnGenderClick);

        BloodTypeReLayout = (RelativeLayout) ViewScaling.findViewByIdAndScale(currentActivity, R.id.UserBasicInformation_Init_RelativeLayout_BloodType);
        TextView BloodTypeTextView = (TextView) ViewScaling.findViewByIdAndScale(currentActivity, R.id.UserBasicInformation_Init_TextView_BloodType);
        RelativeLayout BloodTypeContainer = (RelativeLayout) ViewScaling.findViewByIdAndScale(currentActivity, R.id.UserBasicInformation_Init_RelativeLayout_BloodTypeContainer);
        LinearLayout LinearLayout_BloodTypeContainer_Init = (LinearLayout) ViewScaling.findViewByIdAndScale(currentActivity, R.id.LinearLayout_BloodTypeContainer_Init);
        Blood_A = (Button) ViewScaling.findViewByIdAndScale(currentActivity, R.id.UserBasicInformation_Init_Button_BloodType_A);
        Blood_B = (Button) ViewScaling.findViewByIdAndScale(currentActivity, R.id.UserBasicInformation_Init_Button_BloodType_B);
        Blood_O = (Button) ViewScaling.findViewByIdAndScale(currentActivity, R.id.UserBasicInformation_Init_Button_BloodType_O);
        Blood_AB = (Button) ViewScaling.findViewByIdAndScale(currentActivity, R.id.UserBasicInformation_Init_Button_BloodType_AB);
        setButtonPaddingSize(LinearLayout_BloodTypeContainer_Init);

        Blood_A.setOnClickListener(OnBloodTypeClick);
        Blood_B.setOnClickListener(OnBloodTypeClick);
        Blood_O.setOnClickListener(OnBloodTypeClick);
        Blood_AB.setOnClickListener(OnBloodTypeClick);


        layoutParams = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.MATCH_PARENT);
        layoutParams.leftMargin = (int) (55 * ViewScaling.getScaleValue());
        layoutParams.rightMargin = (int) (40 * ViewScaling.getScaleValue());

        Height = (TitleEditTextView) ViewScaling.findViewByIdAndScale(currentActivity, R.id.UserBasicInformation_Init_TitleEditTextView_Hight);
        Height.setData(getString(R.string.UserBasicInformation_Text_Height), getString(R.string.UserBasicInformation_Hint_Height), TitleEditTextView.Type2_100px);
        Height.Title.setTextColor(getResources().getColor(R.color.color_626262));
        Height.Title.setGravity(Gravity.LEFT | Gravity.CENTER_VERTICAL);
        Height.Title.setLayoutParams(layoutParams);
        Height.EditText.setInputType(InputType.TYPE_CLASS_NUMBER);

        Weight = (TitleEditTextView) ViewScaling.findViewByIdAndScale(currentActivity, R.id.UserBasicInformation_Init_TitleEditTextView_Weight);
        Weight.setData(getString(R.string.UserBasicInformation_Text_Weight), getString(R.string.UserBasicInformation_Hint_Weight), TitleEditTextView.Type2_100px);
        Weight.Title.setTextColor(getResources().getColor(R.color.color_626262));
        Weight.Title.setGravity(Gravity.LEFT | Gravity.CENTER_VERTICAL);
        Weight.Title.setLayoutParams(layoutParams);
        Weight.EditText.setInputType(InputType.TYPE_CLASS_NUMBER);

        ButtonContainer = (LinearLayout) ViewScaling.findViewByIdAndScale(currentActivity, R.id.UserBasicInformation_Init_LinearLayout_ButtonContainer);
        Button_Back = (Button) ViewScaling.findViewByIdAndScale(currentActivity, R.id.UserBasicInformation_Init_Button_Back);
        Button_Next = (Button) ViewScaling.findViewByIdAndScale(currentActivity, R.id.UserBasicInformation_Init_Button_Next);
        Button_Back.setOnClickListener(OnBackIconClick);
        Button_Next.setOnClickListener(OnNextClick);
        Button_Next.setTag(1);

        LastName.EditText.setText(member.lastName);
        FirstName.EditText.setText(member.firstName);


        if (member.userPhoto != null && !"".equals("" + member.userPhoto))
        {
            profilePath = member.userPhoto;
            Log.d("profile", profilePath);
            DownloadImageTask downloadImageTask = new DownloadImageTask(Profile);
            downloadImageTask.execute(profilePath);
        }
        Save_InitMember();
    }

    private void Button_NextTag1()
    {
        Profile.setVisibility(View.VISIBLE);
        LinearLayout.LayoutParams TitleLayoutParams = new LinearLayout.LayoutParams(
                RelativeLayout.LayoutParams.MATCH_PARENT, (int) (56 * ViewScaling.getScaleValue()));
        TitleLayoutParams.topMargin = (int) (18 * ViewScaling.getScaleValue());
        editPhoto.setLayoutParams(TitleLayoutParams);
        editPhoto.setTextSize(TypedValue.COMPLEX_UNIT_PX, (int) (40 * ViewScaling.getScaleValue()));
        editPhoto.setText("編輯照片");
        editPhoto.setGravity(Gravity.CENTER);
        editPhoto.setEnabled(true);

        LastName.EditText.setEnabled(true);
        FirstName.EditText.setEnabled(true);
        Birthday_Year.setEnabled(true);
        Birthday_Ｍonth.setEnabled(true);
        Gender_Female.setEnabled(true);
        Gender_Male.setEnabled(true);
        Blood_A.setEnabled(true);
        Blood_B.setEnabled(true);
        Blood_O.setEnabled(true);
        Blood_AB.setEnabled(true);
        Height.EditText.setEnabled(true);
        Weight.EditText.setEnabled(true);

        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                RelativeLayout.LayoutParams.MATCH_PARENT, (int) (100 * ViewScaling.getScaleValue()));
        layoutParams.topMargin = (int) (17 * ViewScaling.getScaleValue());
        LastName.setLayoutParams(layoutParams);
        LastName.setHight(TitleEditTextView.Type3_120px);
        FirstName.setLayoutParams(layoutParams);
        FirstName.setHight(TitleEditTextView.Type3_120px);
        BirthdayReLayout.setLayoutParams(layoutParams);
        GenderReLayout.setLayoutParams(layoutParams);
        BloodTypeReLayout.setLayoutParams(layoutParams);
        Height.setLayoutParams(layoutParams);
        Height.setHight(TitleEditTextView.Type3_120px);
        Weight.setLayoutParams(layoutParams);
        Weight.setHight(TitleEditTextView.Type3_120px);
        Button_Next.setTag(1);
        Button_Next.setText(R.string.UserBasicInformation_Text_Next);
    }

    private void Button_NextTag2()
    {
        Profile.setVisibility(View.GONE);
        LinearLayout.LayoutParams TitleLayoutParams = new LinearLayout.LayoutParams(
                RelativeLayout.LayoutParams.MATCH_PARENT, (int) (100 * ViewScaling.getScaleValue()));
        TitleLayoutParams.topMargin = (int) (0 * ViewScaling.getScaleValue());
        editPhoto.setLayoutParams(TitleLayoutParams);
        editPhoto.setTextSize(TypedValue.COMPLEX_UNIT_PX, (int) (40 * ViewScaling.getScaleValue()));
        editPhoto.setText("請確認下列您輸入的資料是否正確無誤");
        editPhoto.setGravity(Gravity.CENTER);
        editPhoto.setEnabled(false);

        LastName.EditText.setEnabled(false);
        FirstName.EditText.setEnabled(false);
        Birthday_Year.setEnabled(false);
        Birthday_Ｍonth.setEnabled(false);
        Gender_Female.setEnabled(false);
        Gender_Male.setEnabled(false);
        Blood_A.setEnabled(false);
        Blood_B.setEnabled(false);
        Blood_O.setEnabled(false);
        Blood_AB.setEnabled(false);
        Height.EditText.setEnabled(false);
        Weight.EditText.setEnabled(false);

        LinearLayout.LayoutParams layoutParams1 = new LinearLayout.LayoutParams(
                RelativeLayout.LayoutParams.MATCH_PARENT, (int) (120 * ViewScaling.getScaleValue()));

        layoutParams1.topMargin = (int) (0 * ViewScaling.getScaleValue());
        LastName.setHight(TitleEditTextView.Type3_120px);
        LastName.setLayoutParams(layoutParams1);

        LinearLayout.LayoutParams layoutParams2 = new LinearLayout.LayoutParams(
                RelativeLayout.LayoutParams.MATCH_PARENT, (int) (120 * ViewScaling.getScaleValue()));

        layoutParams2.topMargin = (int) (24 * ViewScaling.getScaleValue());
        FirstName.setLayoutParams(layoutParams2);
        FirstName.setHight(TitleEditTextView.Type3_120px);
        BirthdayReLayout.setLayoutParams(layoutParams2);
        GenderReLayout.setLayoutParams(layoutParams2);
        BloodTypeReLayout.setLayoutParams(layoutParams2);
        Height.setLayoutParams(layoutParams2);
        Height.setHight(TitleEditTextView.Type3_120px);
        Weight.setLayoutParams(layoutParams2);
        Weight.setHight(TitleEditTextView.Type3_120px);
        Button_Next.setTag(2);
        Button_Next.setText(R.string.UserBasicInformation_Init_Text_Save);
    }

    //保存一開始的資料
    private void Save_InitMember()
    {
        member_lastName = member.lastName;
        member_firstName = member.firstName;
        member_birthday = member.birthday;
        member_sex = member.sex;
        member_bloodGroup = member.bloodGroup;
        member_height = member.height;
        member_weight = member.weight;
        member_userPhoto = member.userPhoto;
    }

    //上傳失敗讀取一開始的資料
    private void Load_InitMember()
    {
        member.lastName = member_lastName;
        member.firstName = member_firstName;
        member.birthday = member_birthday;
        member.sex = member_sex;
        member.bloodGroup = member_bloodGroup;
        member.height = member_height;
        member.weight = member_weight;
        member.userPhoto = member_userPhoto;
    }

    boolean isVerification = false;//驗證是否通過
    private View.OnClickListener OnNextClick = new View.OnClickListener()
    {
        @Override
        public void onClick(View v)
        {
            AirApplication.setOnclikcFeedback(v);
            String mLastName = LastName.EditText.getText().toString();
            String mFirstName = FirstName.EditText.getText().toString();
            String mBirthday_Year = Birthday_Year.getText().toString();
            String mBirthday_Month = Birthday_Ｍonth.getText().toString();
            String mHeight = Height.EditText.getText().toString();
            String mWeight = Weight.EditText.getText().toString();

            isVerification = false;
            boolean isInputFirstNameIsEmpty = false;
            boolean isInputLastNameIsEmpty = false;
            boolean isInputBirthdayYearIsEmpty = false;
            boolean isInputBirthdayＭonthIsEmpty = false;
            boolean isInputHeightIsEmpty = false;
            boolean isInputWeightIsEmpty = false;

            isInputFirstNameIsEmpty = mLastName.isEmpty();
            isInputLastNameIsEmpty = mFirstName.isEmpty();
            isInputBirthdayYearIsEmpty = mBirthday_Year.isEmpty();
            isInputBirthdayＭonthIsEmpty = mBirthday_Month.isEmpty();
            isInputHeightIsEmpty = mHeight.isEmpty();
            isInputWeightIsEmpty = mWeight.isEmpty();

            if (isInputFirstNameIsEmpty)
            {
                alertDialogShow(LastName.EditText, getResources().getString(R.string.UserBasicInformation_ERROR_FIRST_NAME_EMPTY));
            }
            else if (isInputLastNameIsEmpty)
            {
                alertDialogShow(FirstName.EditText, getResources().getString(R.string.UserBasicInformation_ERROR_LAST_NAME_EMPTY));
            }
            else if (isInputBirthdayYearIsEmpty)
            {
                alertDialogShow(Birthday_Year, getResources().getString(R.string.UserBasicInformation_ERROR_BIRTHDAY_YEAR_EMPTY));
            }
            else if (checkNum(mBirthday_Year) == false)
            {
                alertDialogShow(Birthday_Year, getResources().getString(R.string.UserBasicInformation_ERROR_BIRTHDAY_IS_NOT_CORRECT));
            }
            else if (isInputBirthdayＭonthIsEmpty)
            {
                alertDialogShow(Birthday_Ｍonth, getResources().getString(R.string.UserBasicInformation_ERROR_BIRTHDAY_ＭONTH_EMPTY));
            }
            else if (checkNum(mBirthday_Month) == false)
            {
                alertDialogShow(Birthday_Ｍonth, getResources().getString(R.string.UserBasicInformation_ERROR_BIRTHDAY_IS_NOT_CORRECT));
            }
            else if (Gender == -1)
            {
                alertDialogShow(null, getResources().getString(R.string.UserBasicInformation_ERROR_GENDER_EMPTY));
            }
            else if (BloodType == -1)
            {
                alertDialogShow(null, getResources().getString(R.string.UserBasicInformation_ERROR_BLOODTYPE_EMPTY));
            }
            else if (isInputHeightIsEmpty)
            {
                alertDialogShow(Height.EditText, getResources().getString(R.string.UserBasicInformation_ERROR_HEIGHT_EMPTY));
            }
            else if (checkNum(mHeight) == false)
            {
                alertDialogShow(Height.EditText, getResources().getString(R.string.UserBasicInformation_ERROR_HEIGHT_NUM));
            }
            else if (Integer.parseInt(mHeight) == 0)
            {
                alertDialogShow(Height.EditText, getResources().getString(R.string.UserBasicInformation_ERROR_HEIGHT_NUM_0));
            }
            else if (isInputWeightIsEmpty)
            {
                alertDialogShow(Weight.EditText, getResources().getString(R.string.UserBasicInformation_ERROR_WEIGHT_EMPTY));
            }
            else if (checkNum(mWeight) == false)
            {
                alertDialogShow(Height.EditText, getResources().getString(R.string.UserBasicInformation_ERROR_WEIGHT_NUM));
            }
            else if (Integer.parseInt(mWeight) == 0)
            {
                alertDialogShow(Weight.EditText, getResources().getString(R.string.UserBasicInformation_ERROR_WEIGHT_NUM_0));
            }
            else if (!(Integer.valueOf(mBirthday_Year) > 1900))
            {
                alertDialogShow(Birthday_Year, getResources().getString(R.string.UserBasicInformation_ERROR_BIRTHDAY_IS_NOT_CORRECT));
            }
            else if (Integer.valueOf(mBirthday_Month) <= 0 || Integer.valueOf(mBirthday_Month) > 12)
            {
                alertDialogShow(Birthday_Ｍonth, getResources().getString(R.string.UserBasicInformation_ERROR_BIRTHDAY_IS_NOT_CORRECT));
            }
            else
            {
                isVerification = true;
            }

            if (isVerification == true)
            {
                int tag = (int) Button_Next.getTag();

                if (tag == 1)
                {
                    Button_NextTag2();
                    setTurnInNextPageAnimation(currentActivity);
                }
                else if (tag == 2 && isConnectedNetWork())
                {
                    member.lastName = mLastName;
                    member.firstName = mFirstName;
                    if (mBirthday_Month.length() == 1)
                    {
                        member.birthday = mBirthday_Year + "-" + "0" + mBirthday_Month + "-" + "01";
                    }
                    else
                    {
                        member.birthday = mBirthday_Year + "-" + mBirthday_Month + "-" + "01";
                    }
                    member.sex = String.valueOf(Gender);
                    member.bloodGroup = String.valueOf(BloodType);
                    member.height = mHeight;
                    member.weight = mWeight;
                    member.userPhoto = profilePath;
                    Log.d("profile", profilePath);

                    UpDateMemberData upDateMemberData = new UpDateMemberData();
                    upDateMemberData.execute();
                }

            }
        }
    };

    //驗證數字
    public boolean checkNum(String Str)
    {
        Pattern p = Pattern.compile("^[0-9]*$");
        Matcher m = p.matcher(Str);
        if (m.matches())
        {
            return true;
        }
        else
        {
            return false;
        }
    }

    private View.OnClickListener OnGenderClick = new View.OnClickListener()
    {
        @Override
        public void onClick(View v)
        {
            AirApplication.setOnclikcFeedback(v);
            switch (v.getId())
            {
                case R.id.UserBasicInformation_Init_Button_Gender_Female:
                {
                    Gender_Female.setSelected(true);
                    Gender_Male.setSelected(false);
                    Gender = 0;
                }
                break;
                case R.id.UserBasicInformation_Init_Button_Gender_Male:
                {
                    Gender_Male.setSelected(true);
                    Gender_Female.setSelected(false);
                    Gender = 1;
                }
                break;
            }
        }
    };

    private View.OnClickListener OnBloodTypeClick = new View.OnClickListener()
    {
        @Override
        public void onClick(View v)
        {
            AirApplication.setOnclikcFeedback(v);
            switch (v.getId())
            {
                case R.id.UserBasicInformation_Init_Button_BloodType_A:
                {
                    Blood_A.setSelected(true);
                    Blood_B.setSelected(false);
                    Blood_O.setSelected(false);
                    Blood_AB.setSelected(false);
                    BloodType = 1;
                }
                break;
                case R.id.UserBasicInformation_Init_Button_BloodType_B:
                {
                    Blood_A.setSelected(false);
                    Blood_B.setSelected(true);
                    Blood_O.setSelected(false);
                    Blood_AB.setSelected(false);
                    BloodType = 2;
                }
                break;
                case R.id.UserBasicInformation_Init_Button_BloodType_O:
                {
                    Blood_A.setSelected(false);
                    Blood_B.setSelected(false);
                    Blood_O.setSelected(true);
                    Blood_AB.setSelected(false);
                    BloodType = 0;
                }
                break;
                case R.id.UserBasicInformation_Init_Button_BloodType_AB:
                {
                    Blood_A.setSelected(false);
                    Blood_B.setSelected(false);
                    Blood_O.setSelected(false);
                    Blood_AB.setSelected(true);
                    BloodType = 3;
                }
                break;
            }
        }
    };
    private View.OnClickListener OnBackIconClick = new View.OnClickListener()
    {

        @Override
        public void onClick(View v)
        {
            AirApplication.setOnclikcFeedback(v);
            Animation scaleAnimation = AnimationUtils.loadAnimation(currentActivity, R.anim.normal_button_alaph_animation);
            scaleAnimation.setAnimationListener(new Animation.AnimationListener()
            {
                @Override
                public void onAnimationStart(Animation animation)
                {

                }

                @Override
                public void onAnimationEnd(Animation animation)
                {


                    int tag = (int) Button_Next.getTag();

                    if (tag == 2)
                    {
                        Button_NextTag1();
                        setBackInPrePageAnimation(currentActivity);
                    }
                    else if (tag == 1)
                    {
                        Intent intent = new Intent(currentActivity, MainActivity.class);
                        startActivity(intent);
                        finish();
                        setBackInPrePageAnimation(currentActivity);
                    }

                }

                @Override
                public void onAnimationRepeat(Animation animation)
                {

                }
            });

            v.setAnimation(scaleAnimation);
            v.startAnimation(scaleAnimation);
        }
    };


    private View.OnClickListener OnEditPhotoClick = new View.OnClickListener()
    {
        @Override
        public void onClick(View v)
        {
            AirApplication.setOnclikcFeedback(v);
            AlertDialog.Builder MyAlertDialog = new AlertDialog.Builder(v.getContext());
            MyAlertDialog.setTitle("設置大頭貼");
            MyAlertDialog.setMessage("請選擇方式");
            DialogInterface.OnClickListener AlbumClick = new DialogInterface.OnClickListener()
            {
                @Override
                public void onClick(DialogInterface dialog, int which)
                {
                    int permissionCheckForStorage = ContextCompat.checkSelfPermission(UserBasicInformationInit.this, Manifest.permission.READ_EXTERNAL_STORAGE);
                    if (permissionCheckForStorage != PackageManager.PERMISSION_GRANTED)
                    {
                        ActivityCompat.requestPermissions(UserBasicInformationInit.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, START_ALBUM_RequestCode);
                    }
                    else
                    {
                        Intent intent = new Intent(Intent.ACTION_PICK, null);
                        intent.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*");
                        startActivityForResult(intent, START_ALBUM);
                    }

                }
            };
            DialogInterface.OnClickListener CameraClick = new DialogInterface.OnClickListener()
            {
                @Override
                public void onClick(DialogInterface dialog, int which)
                {
                    int permissionCheck = ContextCompat.checkSelfPermission(UserBasicInformationInit.this, Manifest.permission.CAMERA);
                    if (permissionCheck != PackageManager.PERMISSION_GRANTED)
                    {
                        ActivityCompat.requestPermissions(UserBasicInformationInit.this, new String[]{Manifest.permission.CAMERA}, START_CAMERA_RequestCode);
                    }
                    else
                    {
                        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                        startActivityForResult(intent, START_CAMERA);
                    }
                }
            };
            MyAlertDialog.setNeutralButton("相簿", AlbumClick);
            MyAlertDialog.setPositiveButton("相機", CameraClick);
            MyAlertDialog.show();
        }
    };

    private class UpDateMemberData extends V7ideaAsyncTask<String, ApiResult>
    {

        private NotifyDialog progress = null;

        @Override
        protected void onPreExecute()
        {
            super.onPreExecute();

            progress = new NotifyDialog(currentActivity);
            progress.settingProgressDialog();
        }

        @Override
        public void ifNeedCloseSomeThing()
        {
            if (progress != null)
            {
                progress.dismiss();
                progress = null;
            }
        }

        @Override
        public void downLoadSuccess(ApiResult result)
        {
            DownLoadMemberData downLoadMemberData = new DownLoadMemberData();
            downLoadMemberData.execute();
        }

        @Override
        public void downLoadFail(ApiResult apiResult)
        {
            Load_InitMember();
            if (apiResult.getErrorNo().contentEquals(Constant.ErrorCode.CODE_500))
            {
                showErrorAlert(getResources().getString(R.string.no_internet_error500));
            }
            else if (apiResult.getErrorNo().equals("1002") || apiResult.getErrorNo().equals("1003"))
            {
                showErrorAlert("err: " + apiResult.getErrorNo() + " \nmessage: " + apiResult.getMessage() + " 請重新登入", new DialogInterface.OnClickListener()
                {
                    @Override
                    public void onClick(DialogInterface dialog, int which)
                    {
                        Intent intent = new Intent(currentActivity, LoginPage.class);
                        startActivity(intent);
                        finish();
                        setBackInPrePageAnimation(currentActivity);
                    }
                });
            }
            else
            {
                showErrorAlert("err: " + apiResult.getErrorNo() + " \nmessage: " + apiResult.getMessage());
            }
        }

        @Override
        protected ApiResult doInBackground(String... params)
        {
            //上傳會員資料
            Token token = new Token();
            ApiResult apiResult = member.putMemberDataToServer(token.getTokenId());

            if (apiResult.getErrorNo().contentEquals(Constant.ErrorCode.SUCCESS))
            {
                return member.getMemberData(token.getTokenId());
            }
            else
            {
                return apiResult;
            }
        }
    }

    private class DownLoadMemberData extends V7ideaAsyncTask<String, ApiResult>
    {

        private NotifyDialog progress = null;

        @Override
        protected void onPreExecute()
        {
            super.onPreExecute();

            progress = new NotifyDialog(currentActivity);
            progress.settingProgressDialog();
        }

        @Override
        public void ifNeedCloseSomeThing()
        {
//            if (progress != null) {
//                progress.dismiss();
//                progress = null;
//            }
        }

        @Override
        public void downLoadSuccess(ApiResult result)
        {
            if (progress != null)
            {
                progress.dismiss();
                progress = null;
            }
            Intent intent = new Intent(currentActivity, LightDescriptionPage.class);
            startActivity(intent);
            finish();
            setTurnInNextPageAnimation(currentActivity);
        }

        @Override
        public void downLoadFail(ApiResult apiResult)
        {
            if (progress != null)
            {
                progress.dismiss();
                progress = null;
            }
            if (apiResult.getErrorNo().contentEquals(Constant.ErrorCode.CODE_500))
            {
                showErrorAlert(getResources().getString(R.string.no_internet_error500));
            }
            else if (apiResult.getErrorNo().contentEquals(Constant.ErrorCode.CODE_1002))
            {
                showErrorAlert(apiResult.getMessage(), new DialogInterface.OnClickListener()
                {
                    @Override
                    public void onClick(DialogInterface dialog, int which)
                    {
                        new Token().destroy();
                        Intent intent = new Intent(currentActivity, LoginPage.class);
                        startActivity(intent);
                        finish();
                        setBackInPrePageAnimation(currentActivity);

                    }
                });
            }
            else if (apiResult.getErrorNo().contentEquals(Constant.ErrorCode.CODE_1003))
            {
                showErrorAlert(apiResult.getMessage(), new DialogInterface.OnClickListener()
                {
                    @Override
                    public void onClick(DialogInterface dialog, int which)
                    {
                        new Token().destroy();
                        Intent intent = new Intent(currentActivity, LoginPage.class);
                        startActivity(intent);
                        finish();
                        setBackInPrePageAnimation(currentActivity);

                    }
                });
            }
            else
            {
                showErrorAlert(apiResult.getMessage());
            }
        }

        @Override
        protected ApiResult doInBackground(String... params)
        {
            Token token = new Token();
            return member.getMemberData(token.getTokenId());
        }
    }

    private class DownloadImageTask extends AsyncTask<String, Void, Bitmap>
    {
        ImageView bmImage;

        public DownloadImageTask(ImageView bmImage)
        {
            this.bmImage = bmImage;
        }

        protected Bitmap doInBackground(String... urls)
        {
            String urldisplay = urls[0];
            Bitmap mIcon11 = null;
            try
            {
                BitmapFactory.Options options = new BitmapFactory.Options();
                options.inSampleSize = 2;

                InputStream in = new java.net.URL(urldisplay).openStream();
                mIcon11 = BitmapFactory.decodeStream(in, null, options);
                //
                int orientation = 0;
                ExifInterface exifInterface = null;
                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N)
                {
                    exifInterface = new ExifInterface(in);
                    // 獲取圖片的旋轉資訊

                    orientation = exifInterface.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);
                    // 根據旋轉角度，生成旋轉矩陣
                    Matrix mtx = new Matrix();
                    switch (orientation)
                    {
                        // 將原始圖片按照旋轉矩陣進行旋轉，並得到新的圖片
                        case ExifInterface.ORIENTATION_ROTATE_90:
                            mtx.postRotate(90);
                            int w = mIcon11.getWidth();
                            int h = mIcon11.getHeight();
                            mIcon11 = Bitmap.createBitmap(mIcon11, 0, 0, w, h, mtx, true);
                            break;
                        case ExifInterface.ORIENTATION_ROTATE_180:
                            mtx.postRotate(180);
                            int w2 = mIcon11.getWidth();
                            int h2 = mIcon11.getHeight();
                            mIcon11 = Bitmap.createBitmap(mIcon11, 0, 0, w2, h2, mtx, true);
                            break;
                        case ExifInterface.ORIENTATION_ROTATE_270:
                            mtx.postRotate(270);
                            int w3 = mIcon11.getWidth();
                            int h3 = mIcon11.getHeight();
                            mIcon11 = Bitmap.createBitmap(mIcon11, 0, 0, w3, h3, mtx, true);
                            break;
                    }
                }

                in.close();
                //

            } catch (Exception e)
            {
                Log.e("Error", e.getMessage());
                e.printStackTrace();
            }
            return mIcon11;
        }

        protected void onPostExecute(Bitmap result)
        {
            bmImage.setImageBitmap(result);
        }
    }


    //錯誤視窗
    public void alertDialogShow(final EditText v, String str)
    {

        new AlertDialog.Builder(UserBasicInformationInit.this)
                .setMessage(str)//設定顯示的文字
                .setPositiveButton(R.string.determine, new DialogInterface.OnClickListener()
                {
                    @Override
                    public void onClick(DialogInterface dialog, int which)
                    {
                        if (v != null)
                        {
                            v.requestFocus();
                            InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
                            imm.toggleSoftInput(0, InputMethodManager.HIDE_NOT_ALWAYS);
                        }
                    }
                })
                .setCancelable(false)
                .show();//呈現對話視窗
    }

    //權限處理
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults)
    {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode)
        {

            case START_ALBUM_RequestCode:
            {

                // 如果在授權請求選擇「允許」
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED)
                {
                    // 相簿
                    Intent intent = new Intent(Intent.ACTION_PICK, null);
                    intent.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*");
                    startActivityForResult(intent, START_ALBUM);
                }
                // 如果在授權請求選擇「拒絕」
                else
                {
                    // 顯示沒有授權的訊息
                    Toast.makeText(this, "沒有寫入外部儲存設備授權", Toast.LENGTH_SHORT).show();
                }

            }
            break;

            case START_CAMERA_RequestCode:
            {
                // 如果在授權請求選擇「允許」
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED)
                {
                    // 拍攝照片
                    Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    startActivityForResult(intent, START_CAMERA);
                }
                // 如果在授權請求選擇「拒絕」
                else
                {
                    // 顯示沒有授權的訊息
                    Toast.makeText(this, "沒有相機授權", Toast.LENGTH_SHORT).show();
                }
            }
            break;
        }
    }

    //圖片處理
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == START_ALBUM && data != null)
        {
            Uri uri = data.getData();
            profilePath = getPath(uri);
            ContentResolver cr = this.getContentResolver();
            int orientation = 0;
            try
            {
                // 從指定路徑下讀取圖片，並獲取其EXIF資訊
                ExifInterface exifInterface = new ExifInterface(profilePath);
                // 獲取圖片的旋轉資訊
                orientation = exifInterface.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);

                BitmapFactory.Options options = new BitmapFactory.Options();
                options.inSampleSize = 2;
                Bitmap bitmap = BitmapFactory.decodeStream(cr.openInputStream(uri), null, options);
                // 根據旋轉角度，生成旋轉矩陣
                Matrix mtx = new Matrix();
                switch (orientation)
                {
                    // 將原始圖片按照旋轉矩陣進行旋轉，並得到新的圖片
                    case ExifInterface.ORIENTATION_ROTATE_90:
                        mtx.postRotate(90);
                        int w = bitmap.getWidth();
                        int h = bitmap.getHeight();
                        bitmap = Bitmap.createBitmap(bitmap, 0, 0, w, h, mtx, true);
                        break;
                    case ExifInterface.ORIENTATION_ROTATE_180:
                        mtx.postRotate(180);
                        int w2 = bitmap.getWidth();
                        int h2 = bitmap.getHeight();
                        bitmap = Bitmap.createBitmap(bitmap, 0, 0, w2, h2, mtx, true);
                        break;
                    case ExifInterface.ORIENTATION_ROTATE_270:
                        mtx.postRotate(270);
                        int w3 = bitmap.getWidth();
                        int h3 = bitmap.getHeight();
                        bitmap = Bitmap.createBitmap(bitmap, 0, 0, w3, h3, mtx, true);
                        break;
                }
//                if(orientation != 0){
//                    bitmap = rotateBitmapByDegree(bitmap,90);

//                }
                Profile.setImageBitmap(bitmap);
            } catch (FileNotFoundException e)
            {
                e.printStackTrace();
            } catch (IOException e)
            {
                e.printStackTrace();
            }

        }
        if (requestCode == START_CAMERA)
        {
            // 如果有檔案名稱
//            if (fileName != null && fileName.length() > 0) {
//                // 照片檔案物件
//                File file = configFileName("P", ".jpg");
//
//                // 如果照片檔案存在
//                if (file.exists()) {
//                    // 顯示照片元件
//                    Profile.setVisibility(View.VISIBLE);
//                    // 設定照片
//                    FileUtil.fileToImageView(file.getAbsolutePath(), Profile);
//                    profilePath = file.getAbsolutePath();
//                }
//            }
            //上面是拿取原圖 下面是縮圖方式
            if (data != null)
            {

                Bitmap bitmap = (Bitmap) data.getExtras().get("data");
//                if(bitmap.getHeight()>bitmap.getWidth()){
//                    https://blog.csdn.net/gh8609123/article/details/60369777  假如是直的要轉90度
//                }
                Profile.setImageBitmap(bitmap);
                Uri uri = data.getData();
                profilePath = getPath(uri);
            }
        }
    }

    //圖片路徑
    private String getPath(Uri uri)
    {
        String[] projection = {MediaStore.Video.Media.DATA};
        Cursor cursor = managedQuery(uri, projection, null, null, null);
        int column_index = cursor
                .getColumnIndexOrThrow(MediaStore.Audio.Media.DATA);
        cursor.moveToFirst();
        return cursor.getString(column_index);
    }

    // android 7.0系統解決拍照的問題
    private void initPhotoError()
    {
        StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
        StrictMode.setVmPolicy(builder.build());
        builder.detectFileUriExposure();
    }

    //返回鍵
    @Override
    public void onBackPressed()
    {
        Intent intent = new Intent(currentActivity, MainActivity.class);
        startActivity(intent);
        finish();
        setBackInPrePageAnimation(currentActivity);
    }

    /**
     * 創造日期 :2019/6/19
     * 方法名稱：setButtonPaddingSize
     * 方法用途：固定間隔距離
     * ＠return void
     *
     * @param linearLayoutContainer
     **/
    private void setButtonPaddingSize(final LinearLayout linearLayoutContainer)
    {
        final String TAG = "setButtonPaddingSize";
        linearLayoutContainer.post(new Runnable()
        {
            @Override
            public void run()
            {
                Log.e(TAG, "run : " + " Name = " + getResources().getResourceEntryName(linearLayoutContainer.getId()));
                int width = linearLayoutContainer.getWidth();
                Log.e(TAG, "run : " + " width = " + width);
                int childCount = linearLayoutContainer.getChildCount();
                Log.e(TAG, "run : " + " childCount = " + childCount);
                int paddCount = linearLayoutContainer.getChildCount() - 1;
                Log.e(TAG, "run : " + " paddCount = " + paddCount);
                int childCountSumSize = 0;
                for (int i = 0; i < childCount; i++)
                {
                    int childwidth = linearLayoutContainer.getChildAt(i).getWidth();
                    Log.e(TAG, "run : " + " childwidth = " + childwidth + " , i = " + i);
                    childCountSumSize = childCountSumSize + childwidth;
                }
                Log.e(TAG, "run : " + " childCountSumSize = " + childCountSumSize);


                if (paddCount < 2)
                {   //固定間隔距離
                    int paddSize = (int) (20 * ViewScaling.getScaleValue());
                    Log.e(TAG, "run : " + " paddSize = " + paddSize);
                    int newWidth = width - paddSize;
                    Log.e(TAG, "run : " + " newWidth = " + newWidth);
                    for (int i = 0; i < childCount; i++)
                    {//調整大小,調整間隔距離
                        LinearLayout.LayoutParams layoutParams = (LinearLayout.LayoutParams) linearLayoutContainer.getChildAt(i).getLayoutParams();
                        layoutParams.weight = newWidth / 2;
                        if (i != 0) layoutParams.leftMargin = paddSize;

                        linearLayoutContainer.getChildAt(i).setLayoutParams(layoutParams);
                    }
                }
                else
                {
                    int paddSize = (width - childCountSumSize) / paddCount;
                    Log.e(TAG, "run : " + " paddSize = " + paddSize);
                    for (int i = 1; i < childCount; i++)
                    {//調整間隔距離
                        LinearLayout.LayoutParams layoutParams = (LinearLayout.LayoutParams) linearLayoutContainer.getChildAt(i).getLayoutParams();
                        layoutParams.leftMargin = paddSize;
                        linearLayoutContainer.getChildAt(i).setLayoutParams(layoutParams);
                    }
                }
            }
        });
    }
}
