package com.v7idea.template.Tool;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.text.method.LinkMovementMethod;
import android.text.method.ScrollingMovementMethod;
import android.text.util.Linkify;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.v7idea.healthkit.R;
import com.v7idea.template.AirApplication;

/**
 * Created by mortal on 15/2/24.
 */
public class NotifyDialog extends Dialog {
    private final String tag = "NotifyDialog";

    private Context context = null;
    private AirApplication thisApp = null;

    private TextView announcementTitle = null;
    private TextView announcementContent = null;
    private TextView agree = null;
    private TextView disagree = null;

    public EditText inputOldPassword = null;
    public EditText inputNewPassword = null;
    public EditText inputConfirmPassword = null;
//    public SpecialImageView CancelIcon = null;

    private ListView itemList = null;

    public NotifyDialog(Context context) {
        super(context, R.style.dialog);
        this.context = context;

        setCancelable(false);
        thisApp = (AirApplication) this.context.getApplicationContext();
    }

//    public void initScaleImageStyle(String strPhotoUrl, View.OnClickListener onPressCancelListener)
//    {
//        RelativeLayout contentLayout = (RelativeLayout) getLayoutInflater().inflate(R.layout.alert_scale_image_layout_style, null);
//
//        setContentView(contentLayout, new ViewGroup.LayoutParams(thisApp.getScreenWidth(), thisApp.getScreenHeight()));
//
//        ScaleImageView scaleImageView = (ScaleImageView) contentLayout.findViewById(R.id.ScaleImage_ScaleImageView_ShowUserImage);
//        thisApp.setScaleByRelativeLayout(scaleImageView);
//
//        scaleImageView.setViewWidth(scaleImageView.getLayoutParams().width);
//        scaleImageView.setViewHeight(scaleImageView.getLayoutParams().height);
//
//        scaleImageView.resetMatrix();
//        scaleImageView.setOnTouchScaleListener();
//        scaleImageView.setDoubleTapGestureDetector();
//
//        SpecialImageView CancelImageView = (SpecialImageView) contentLayout.findViewById(R.id.ScaleImage_SpecialImageView_Cancel);
//        thisApp.setScaleByRelativeLayout(CancelImageView);
//        CancelImageView.setOnClickListener(onPressCancelListener);
//
//        ImageCatch imageCatch = new ImageCatch();
//
//        DisplayImageOptions options = imageCatch.getDisplayImageOptions();
//        ImageLoader.getInstance().displayImage(APIFetch.getDownLoadImageApi(strPhotoUrl)
//                , scaleImageView, options, afterLoadImage);
//
//        scaleImageView.minZoom();
//        scaleImageView.setCurrentDegree(-1);
//        scaleImageView.rotationImage(0);
//    }
//
//    private ImageLoadingListener afterLoadImage = new ImageLoadingListener() {
//        @Override
//        public void onLoadingStarted(String s, View view) {
//
//        }
//
//        @Override
//        public void onLoadingFailed(String s, View view, FailReason failReason) {
//
//        }
//
//        @Override
//        public void onLoadingComplete(String s, View view, Bitmap bitmap) {
//            ((ScaleImageView)view).minZoom();
//            ((ScaleImageView)view).setCurrentDegree(-1);
//            ((ScaleImageView)view).rotationImage(0);
//        }
//
//        @Override
//        public void onLoadingCancelled(String s, View view) {
//
//        }
//    };
//
//    public void setChangePasswordStyle() {
//        LinearLayout contentLayout = (LinearLayout) getLayoutInflater().inflate(R.layout.alert_dialog_change_password_style, null);
//        setContentView(contentLayout, new ViewGroup.LayoutParams((int) (720 * thisApp.getScaleValue()), ViewGroup.LayoutParams.WRAP_CONTENT));
//
//        V7TitleView Title = (V7TitleView) contentLayout.findViewById(R.id.ChangePassword_V7TitleView_Title);
//        thisApp.setScaleByLinearLayout(Title);
//        thisApp.setScaleTextSize(Title, thisApp.getScaleMin());
//
//        inputOldPassword = (EditText) contentLayout.findViewById(R.id.ChangePassword_EditText_inputOldPassword);
//        thisApp.setScaleByLinearLayout(inputOldPassword);
//        thisApp.setScaleTextSize(inputOldPassword, thisApp.getScaleMin());
//
//        inputNewPassword = (EditText) contentLayout.findViewById(R.id.ChangePassword_EditText_inputNewPassword);
//        thisApp.setScaleByLinearLayout(inputNewPassword);
//        thisApp.setScaleTextSize(inputNewPassword, thisApp.getScaleMin());
//
//        inputConfirmPassword = (EditText) contentLayout.findViewById(R.id.ChangePassword_EditText_inputConfirmPassword);
//        thisApp.setScaleByLinearLayout(inputConfirmPassword);
//        thisApp.setScaleTextSize(inputConfirmPassword, thisApp.getScaleMin());
//
//        agree = (V7TitleView) contentLayout.findViewById(R.id.ChangePassword_V7TitleView_Yes);
//        thisApp.setScaleByLinearLayout(agree);
//        thisApp.setScaleTextSize(agree, thisApp.getScaleMin());
//    }

    public void settingProgressDialog() {
        RelativeLayout background = (RelativeLayout) getLayoutInflater().inflate(R.layout.layout_empty, null);
        ViewScaling.setScale(background, (int) (394 * ViewScaling.getScaleValue()), (int) (296 * ViewScaling.getScaleValue()));

        ImageView toolImage = (ImageView) getLayoutInflater().inflate(R.layout.imageview, null);
        background.addView(toolImage);

        ViewScaling.setScale(toolImage, (int) (94 * ViewScaling.getScaleValue()), (int) (94 * ViewScaling.getScaleValue()), (int) (70 * ViewScaling.getScaleValue()), (int) (150 * ViewScaling.getScaleValue()));

        TextView showText = new TextView(context);
        background.addView(showText);
        background.setAlpha(0.8f);
        ViewScaling.setScale(showText, ViewGroup.LayoutParams.MATCH_PARENT, (int) (90 *  ViewScaling.getScaleValue()), (int) (184 *  ViewScaling.getScaleValue()), 0);
        showText.setText("請稍候");
        showText.setTextColor(Color.WHITE);
        showText.setTextSize(TypedValue.COMPLEX_UNIT_PX, (int) (30 * ViewScaling.getScaleValue()));
        showText.setGravity(Gravity.CENTER);

        Animation hyperspaceJumpAnimation = AnimationUtils.loadAnimation(context, R.anim.progressdialogcustom);
        toolImage.setImageResource(R.mipmap.progresspicturetool);
//			toolImage.setScaleType(ScaleType.FIT_XY);
        toolImage.setAnimation(hyperspaceJumpAnimation);
        toolImage.startAnimation(hyperspaceJumpAnimation);

        setContentView(background, new RelativeLayout.LayoutParams((int) (394 * ViewScaling.getScaleValue()), (int) (296 * ViewScaling.getScaleValue())));
        setCancelable(false);
        show();
        getWindow().setLayout((int) (394 * ViewScaling.getScaleValue()), (int) (296 * ViewScaling.getScaleValue()));
    }

//    public void initAlertStyleFive(String strFirstText, String strSecondText)
//    {
//        RelativeLayout chooseItemLayout = (RelativeLayout) ((Activity) context).getLayoutInflater().inflate(R.layout.alert_notify_style_five_layout, null);
//        setContentView(chooseItemLayout, new ViewGroup.LayoutParams((int) (720 * thisApp.getScaleValue()), (int) (720 * thisApp.getScaleValue())));
//
//        LinearLayout textContainer = (LinearLayout) findViewById(R.id.notifyStyleFive_LinearLayout_textContainer);
//        thisApp.setScaleByRelativeLayout(textContainer);
////        textContainer.setGravity(gravity);
//
//        CancelIcon = (SpecialImageView) chooseItemLayout.findViewById(R.id.notifyStyleFive_SpecialImageView_Cancel);
//        thisApp.setScaleByRelativeLayout(CancelIcon);
//
//        announcementTitle = (TextView) textContainer.findViewById(R.id.notifyStyleFive_TextView_showTitle);
//        thisApp.setScaleTextSize(announcementTitle, thisApp.getScaleMin());
//        announcementTitle.setAutoLinkMask(Linkify.ALL);
//        announcementTitle.setMovementMethod(LinkMovementMethod.getInstance());
//
//        if (Air.isStringNotNullAndEmpty(strFirstText)) {
//            announcementTitle.setText(strFirstText);
//        }
//        else
//        {
//            announcementTitle.setVisibility(View.GONE);
//        }
//
//        announcementContent = (TextView) textContainer.findViewById(R.id.notifyStyleFive_TextView_showContent);
//        thisApp.setScaleTextSize(announcementContent, thisApp.getScaleMin());
//        announcementContent.setMovementMethod(new ScrollingMovementMethod());
//
//        if (strSecondText != null) {
//            announcementContent.setText(strSecondText);
//        }
//
//        LinearLayout bottomContainer = (LinearLayout) findViewById(R.id.notifyStyleFive_LinearLayout_bottomContainer);
//        thisApp.setScaleByRelativeLayout(bottomContainer);
//
//        agree = (TextView) findViewById(R.id.notifyStyleFive_TextView_agree);
//        thisApp.setScaleByLinearLayout(agree);
//        thisApp.setScaleTextSize(agree, thisApp.getScaleMin());
//    }
//
//    public void initAlertStyleFour(String strShowTitle, String showContent, int gravity) {
//        RelativeLayout chooseItemLayout = (RelativeLayout) ((Activity) context).getLayoutInflater().inflate(R.layout.alert_notify_style_four_layout, null);
//        chooseItemLayout.setMinimumHeight((int) (461 * thisApp.getScaleValue()));
//        setContentView(chooseItemLayout, new ViewGroup.LayoutParams((int) (720 * thisApp.getScaleValue()), ViewGroup.LayoutParams.WRAP_CONTENT));
//
//        LinearLayout textContainer = (LinearLayout) findViewById(R.id.notifyStyleFour_LinearLayout_textContainer);
//        thisApp.setScaleByRelativeLayout(textContainer);
//        textContainer.setGravity(gravity);
//
//        announcementTitle = (TextView) textContainer.findViewById(R.id.notifyStyleFour_TextView_showTitle);
//        thisApp.setScaleTextSize(announcementTitle, thisApp.getScaleMin());
//
//        if (Air.isStringNotNullAndEmpty(strShowTitle)) {
//            announcementTitle.setText(strShowTitle);
//        }
//        else
//        {
//            announcementTitle.setVisibility(View.GONE);
//        }
//
//        announcementContent = (TextView) textContainer.findViewById(R.id.notifyStyleFour_TextView_showContent);
//        thisApp.setScaleTextSize(announcementContent, thisApp.getScaleMin());
//
//        if (showContent != null) {
//            announcementContent.setText(showContent);
//        }
//
//        LinearLayout bottomContainer = (LinearLayout) findViewById(R.id.notifyStyleFour_LinearLayout_bottomContainer);
//        thisApp.setScaleByRelativeLayout(bottomContainer);
//
//        disagree = (TextView) findViewById(R.id.notifyStyleFour_TextView_disAgree);
//        thisApp.setScaleByLinearLayout(disagree);
//        thisApp.setScaleTextSize(disagree, thisApp.getScaleMin());
//
//        agree = (TextView) findViewById(R.id.notifyStyleFour_TextView_agree);
//        thisApp.setScaleByLinearLayout(agree);
//        thisApp.setScaleTextSize(agree, thisApp.getScaleMin());
//    }
//
//    public static NotifyDialog getCurrentNoInternetDialog(Context context, String strShowString) {
//        NotifyDialog CurrentNoInternetDialog = new NotifyDialog(context);
//        CurrentNoInternetDialog.setCancelable(false);
//        CurrentNoInternetDialog.initAlertStyleTwo(strShowString);
//        return CurrentNoInternetDialog;
//    }
//
//    //
//    public void initAlertStyleTwo(String showContent) {
//        RelativeLayout chooseItemLayout = (RelativeLayout) ((Activity) context).getLayoutInflater().inflate(R.layout.alert_notify_style_two_layout, null);
//        setContentView(chooseItemLayout, new ViewGroup.LayoutParams((int) (720 * thisApp.getScaleValue()), (int) (461 * thisApp.getScaleValue())));
//
//        LinearLayout textContainer = (LinearLayout) findViewById(R.id.notifyStyle2_LinearLayout_textContainer);
//        thisApp.setScaleByRelativeLayout(textContainer);
//
//        announcementTitle = (TextView) textContainer.findViewById(R.id.notifyStyle2_TextView_showContent);
//        thisApp.setScaleTextSize(announcementTitle, thisApp.getScaleMin());
//
//        if (showContent != null) {
//            announcementTitle.setText(showContent);
//        }
//
//        LinearLayout bottomContainer = (LinearLayout) findViewById(R.id.notifyStyle2_LinearLayout_bottomContainer);
//        thisApp.setScaleByRelativeLayout(bottomContainer);
//
//        agree = (TextView) findViewById(R.id.notifyStyle2_TextView_agree);
//        thisApp.setScaleByLinearLayout(agree);
//        thisApp.setScaleTextSize(agree, thisApp.getScaleMin());
//    }
//
//    public void initListAlert(int iconID, BaseAdapter itemAdapter, AdapterView.OnItemClickListener OnItemClickListener) {
//        RelativeLayout chooseItemLayout = (RelativeLayout) ((Activity) context).getLayoutInflater().inflate(R.layout.select_notification_alert_mode_layout, null);
//        setContentView(chooseItemLayout, new ViewGroup.LayoutParams(thisApp.getScreenWidth(), thisApp.getScreenHeight()));
//
//        LinearLayout container = (LinearLayout) chooseItemLayout.findViewById(R.id.SelectNotificationMode_LinearLayout_container);
//        thisApp.setScaleByRelativeLayout(container);
//
//        itemList = (ListView) chooseItemLayout.findViewById(R.id.SelectNotificationMode_SpecialListView_ShowOption);
//        thisApp.setScaleByLinearLayout(itemList);
//        itemList.setAdapter(itemAdapter);
//        itemList.setOnItemClickListener(OnItemClickListener);
//
//        LinearLayout buttonContainer = (LinearLayout) chooseItemLayout.findViewById(R.id.SelectNotificationMode_LinearLayout_bottomContainer);
//        thisApp.setScaleByLinearLayout(buttonContainer);
//
//        agree = (TextView) buttonContainer.findViewById(R.id.SelectNotificationMode_TextView_agree);
//        thisApp.setScaleByLinearLayout(agree);
//        thisApp.setScaleTextSize(agree, thisApp.getScaleMin());
//
//        SpecialImageView HearderIcon = (SpecialImageView) chooseItemLayout.findViewById(R.id.SelectNotificationMode_SpecialImageView_HeaderIcon);
//        thisApp.setScaleByRelativeLayout(HearderIcon);
//        HearderIcon.setImageResource(iconID);
//    }
//
//    public void initDeleteCheckPassword()
//    {
//        LinearLayout chooseItemLayout = (LinearLayout) ((Activity) context).getLayoutInflater().inflate(R.layout.check_password_alert_layout, null);
//        chooseItemLayout.setMinimumHeight((int) (461 * thisApp.getScaleValue()));
//        setContentView(chooseItemLayout, new ViewGroup.LayoutParams((int) (720 * thisApp.getScaleValue()), ViewGroup.LayoutParams.WRAP_CONTENT));
//
//        announcementTitle = (TextView) chooseItemLayout.findViewById(R.id.DeleteAccount_V7TitleView_Title);
//        thisApp.setScaleByLinearLayout(announcementTitle);
//        thisApp.setScaleTextSize(announcementTitle, thisApp.getScaleMin());
//
//        inputOldPassword = (EditText) chooseItemLayout.findViewById(R.id.DeleteAccount_EditText_inputPassword);
//        thisApp.setScaleByLinearLayout(inputOldPassword);
//        thisApp.setScaleTextSize(inputOldPassword, thisApp.getScaleMin());
//
//        inputConfirmPassword = (EditText) chooseItemLayout.findViewById(R.id.DeleteAccount_EditText_checkPassword);
//        thisApp.setScaleByLinearLayout(inputConfirmPassword);
//        thisApp.setScaleTextSize(inputConfirmPassword, thisApp.getScaleMin());
//
//        LinearLayout ButtonContainer = (LinearLayout) chooseItemLayout.findViewById(R.id.DeleteAccount_LinearLayout_ButtonContainer);
//        thisApp.setPadding(ButtonContainer);
//
//        disagree = (V7TitleView) findViewById(R.id.DeleteAccount_V7TitleView_No);
//        thisApp.setScaleByLinearLayout(disagree);
//        thisApp.setScaleTextSize(disagree, thisApp.getScaleMin());
//
//        agree = (V7TitleView) findViewById(R.id.DeleteAccount_V7TitleView_Yes);
//        thisApp.setScaleByLinearLayout(agree);
//        thisApp.setScaleTextSize(agree, thisApp.getScaleMin());
//    }
//
//    public void setAgreeListener(String showString, View.OnClickListener onAgreePress) {
//        if (agree != null) {
//            if (Air.isStringNotNullAndEmpty(showString)) {
//                agree.setText(showString);
//            }
//
//            agree.setOnClickListener(onAgreePress);
//        }
//    }
//
//    public void setAgreeListener(View.OnClickListener onAgreePress) {
//        setAgreeListener("", onAgreePress);
//    }
//
//    public void setDisagreeListener(String showString, View.OnClickListener onDisagreePress) {
//        if (disagree != null) {
//            if (Air.isStringNotNullAndEmpty(showString)) {
//                disagree.setText(showString);
//            }
//
//            disagree.setOnClickListener(onDisagreePress);
//        }
//    }
//
//    public void setDisagreeListener(View.OnClickListener onDisagreePress) {
//        setDisagreeListener("", onDisagreePress);
//    }
}
