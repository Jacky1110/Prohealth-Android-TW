package com.v7idea.template.View;

import android.content.Context;
import androidx.annotation.Nullable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.v7idea.healthkit.R;
import com.v7idea.template.Tool.ViewScaling;

/**
 * Created by mortal on 2017/10/25.
 */

public class Banner extends LinearLayout
{
    public Banner(Context context) {
        super(context);
        setOrientation(LinearLayout.VERTICAL);
    }

    public Banner(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        setOrientation(LinearLayout.VERTICAL);
    }

    public Banner(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        setOrientation(LinearLayout.VERTICAL);
    }

    public V7TitleView title = null;
    public AutoReleaseImageView backIcon = null;
    public AutoReleaseImageView rightIcon = null;

    public void initShortBanner() {
        LayoutInflater inflater = LayoutInflater.from(getContext());

        RelativeLayout contentLayout = (RelativeLayout) inflater.inflate(R.layout.short_banner_layout, null);

        title = (V7TitleView) ViewScaling.findViewByIdAndScale(contentLayout, R.id.shortBanner_TextView_title);
        backIcon = (AutoReleaseImageView) ViewScaling.findViewByIdAndScale(contentLayout, R.id.shortBanner_AutoReleaseImageView_backIcon);
        rightIcon = (AutoReleaseImageView) ViewScaling.findViewByIdAndScale(contentLayout,R.id.shortBanner_AutoReleaseImageView_rightIcon);
        addView(contentLayout);
    }
}
