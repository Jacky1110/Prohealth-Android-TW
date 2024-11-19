package com.v7idea.healthkit;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.v7idea.template.Activity.BaseActivity;
import com.v7idea.template.AirApplication;
import com.v7idea.template.Tool.ViewScaling;
import com.v7idea.template.View.Banner;
/**
 * 2019/1/24 不確認的頁面
 */
public class CommunityBlindingPage extends BaseActivity implements View.OnClickListener{

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_binding_community);

        Banner banner = (Banner) ViewScaling.findViewByIdAndScale(currentActivity, R.id.BindingCommunityPage_Banner_Header);
        banner.initShortBanner();
        banner.backIcon.setOnClickListener(this);
        banner.title.setText(R.string.BindingCommunityPage_Text_title);
        banner.title.setTextColor(Color.parseColor("#FFFFFF"));

        ScrollView scrollArea = (ScrollView) ViewScaling.findViewByIdAndScale(currentActivity,R.id.BindingCommunityPage_LockScrollView_ScrollArea);
//        LinearLayout linearLayout = (LinearLayout) ViewScaling.findViewByIdAndScale(currentActivity,R.id.BindingCommunityPage_LinearLayout_ScrollContainer);

        RelativeLayout WechatIcon_relativeLayout = (RelativeLayout) ViewScaling.findViewByIdAndScale(currentActivity,R.id.BindingCommunityPage_WeChat_RelativeLayout);
        ImageView WechatIcon_ImageView = (ImageView) ViewScaling.findViewByIdAndScale(currentActivity,R.id.BindingCommunityPage_WeChat_ImageView);
        TextView WechatIcon_TextView = (TextView) ViewScaling.findViewByIdAndScale(currentActivity,R.id.BindingCommunityPage_WeChat_TextView);
        TextView Wechat_binding_TextView = (TextView) ViewScaling.findViewByIdAndScale(currentActivity,R.id.BindingCommunityPage_WeChat_TextView_binding);
        ImageView Wechat_arrow_next_ImageView = (ImageView) ViewScaling.findViewByIdAndScale(currentActivity,R.id.BindingCommunityPage_WeChat_ImageView_arrow_next);


        RelativeLayout LineIcon_relativeLayout = (RelativeLayout) ViewScaling.findViewByIdAndScale(currentActivity,R.id.BindingCommunityPage_Line_RelativeLayout);
        ImageView LineIcon_ImageView = (ImageView) ViewScaling.findViewByIdAndScale(currentActivity,R.id.BindingCommunityPage_Line_ImageView);
        TextView LineIcon_TextView = (TextView) ViewScaling.findViewByIdAndScale(currentActivity,R.id.BindingCommunityPage_Line_TextView);
        TextView Line_binding_TextView = (TextView) ViewScaling.findViewByIdAndScale(currentActivity,R.id.BindingCommunityPage_Line_TextView_binding);
        ImageView Line_arrow_next_ImageView = (ImageView) ViewScaling.findViewByIdAndScale(currentActivity,R.id.BindingCommunityPage_Line_ImageView_arrow_next);



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
