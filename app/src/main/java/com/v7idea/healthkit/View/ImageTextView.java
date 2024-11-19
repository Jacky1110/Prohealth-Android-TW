package com.v7idea.healthkit.View;

import android.content.Context;
import androidx.annotation.Nullable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.v7idea.healthkit.R;
import com.v7idea.template.Tool.ViewScaling;

public class ImageTextView extends LinearLayout {
    private LinearLayout mLinearLayout = null;
    private ImageView mImgView = null;
    private TextView mTextView = null;
    private Context mContext;

    private void init(Context context) {
        mContext = context;
        LayoutInflater inflater = LayoutInflater.from(mContext);
        View contextView = inflater.inflate(R.layout.customview_image_text, null);

        mImgView = (ImageView) ViewScaling.findViewByIdAndScale(contextView, R.id.ImageTextView_ImageView_Icon);
        mTextView = (TextView) ViewScaling.findViewByIdAndScale(contextView, R.id.ImageTextView_TextView_Text);
        mLinearLayout = (LinearLayout) ViewScaling.findViewByIdAndScale(contextView, (R.id.ImageTextView_LinearLayout_background));

        addView(contextView, new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, (int) (100 * ViewScaling.getScaleValue())));
    }

    public ImageTextView(Context context) {
        super(context);
        init(context);
    }

    public ImageTextView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public ImageTextView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    public ImageTextView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init(context);
    }

    public void setText(String string) {
        mTextView.setText(string);
    }

    public void setText(String string, int Resources) {
        mTextView.setText(string);
        mTextView.setTextColor(Resources);
    }

    public void setIconVisibility(int i) {
        mImgView.setVisibility(i);
    }

    public void setTextVisibility(int i) {
        mTextView.setVisibility(i);
    }

    public void setIcon(int Resources) {
        mImgView.setImageResource(Resources);
    }

    public void setBackground(int Resources) {
        mLinearLayout.setBackgroundResource(Resources);
    }

    public void setBackgroundColor(int color) {
        mLinearLayout.setBackgroundColor(color);
    }


}
