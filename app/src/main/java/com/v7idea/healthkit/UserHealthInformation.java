package com.v7idea.healthkit;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.v7idea.healthkit.Model.Token;
import com.v7idea.template.Activity.BaseActivity;
import com.v7idea.template.AirApplication;
import com.v7idea.template.Tool.ViewScaling;
import com.v7idea.template.View.Banner;
/**
 * 2019/1/24 沒有使用的頁面
 */
public class UserHealthInformation extends BaseActivity implements View.OnClickListener{

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_health_information);

        Banner banner = (Banner) ViewScaling.findViewByIdAndScale(currentActivity, R.id.UserHealthInformation_Banner_Header);
        banner.initShortBanner();
        banner.backIcon.setOnClickListener(this);
        banner.title.setText(R.string.UserHealthInformation_Text_Title);
        banner.title.setTextColor(Color.parseColor("#FFFFFF"));

        ScrollView scrollArea = (ScrollView) ViewScaling.findViewByIdAndScale(currentActivity,R.id.UserHealthInformation_LockScrollView_ScrollArea);
        LinearLayout linearLayout = (LinearLayout) ViewScaling.findViewByIdAndScale(currentActivity,R.id.UserHealthInformation_LinearLayout_ScrollContainer);

        RelativeLayout FamilyReLayout = (RelativeLayout) ViewScaling.findViewByIdAndScale(currentActivity,R.id.UserHealthInformation_RelativeLayout_Family);
        TextView FamilyTextView = (TextView) ViewScaling.findViewByIdAndScale(currentActivity,R.id.UserHealthInformation_TextView_Family);
        EditText familyEditText = (EditText)ViewScaling.findViewByIdAndScale(currentActivity,R.id.UserHealthInformation_EditText_Family );

//        Token token = new Token();
//        familyEditText.setText(""+token.getTokenId());
        RelativeLayout SmokeReLayout = (RelativeLayout) ViewScaling.findViewByIdAndScale(currentActivity,R.id.UserHealthInformation_RelativeLayout_Smoke);
        TextView SmokeTextView = (TextView) ViewScaling.findViewByIdAndScale(currentActivity,R.id.UserHealthInformation_TextView_Smoke);

        RelativeLayout DrinkReLayout = (RelativeLayout) ViewScaling.findViewByIdAndScale(currentActivity,R.id.UserHealthInformation_RelativeLayout_Drink);
        TextView DrinkTextView = (TextView) ViewScaling.findViewByIdAndScale(currentActivity,R.id.UserHealthInformation_TextView_Drink);



    }

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
                Intent intent = new Intent(currentActivity, SystemSettingsPage.class);
                startActivity(intent);
                finish();
                setBackInPrePageAnimation(currentActivity);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });

        view.setAnimation(scaleAnimation);
        view.startAnimation(scaleAnimation);
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
