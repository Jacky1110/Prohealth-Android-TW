package com.v7idea.healthkit;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.v7idea.healthkit.Model.Member;
import com.v7idea.template.Activity.BaseActivity;
import com.v7idea.template.AirApplication;
import com.v7idea.template.Tool.LoadQRimage;
import com.v7idea.template.Tool.ViewScaling;
import com.v7idea.template.View.Banner;
/**
 * 2019/1/24 沒有使用的頁面
 */
public class CreateAccountQRcodePage extends BaseActivity implements View.OnClickListener{
    private ImageLoader imageLoader=ImageLoader.getInstance();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_account_qr_code);

        Banner banner = (Banner) ViewScaling.findViewByIdAndScale(currentActivity, R.id.Create_account_QRcodePage_Banner_Header);
        banner.initShortBanner();
        banner.backIcon.setOnClickListener(this);
        banner.title.setText(R.string.CreateAccountQRcode_Text_Title);
        banner.title.setTextColor(Color.parseColor("#FFFFFF"));

        TextView createAccountQRcode_TextVIew = (TextView) ViewScaling.findViewByIdAndScale(currentActivity,R.id.Create_account_QRcodePage_TextView);
        ImageView QR_code_ImageView = (ImageView) ViewScaling.findViewByIdAndScale(currentActivity,R.id.Create_account_QRcodePage_QRcode_ImageView);

        Member member = new Member();
//        ImageLoader
        LoadQRimage.imageLoading(this,"http://stonetestweb.azurewebsites.net/img.aspx?custid=1&username=public&codetype=QR&EClevel=0&data="+member.mobile,QR_code_ImageView);
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
