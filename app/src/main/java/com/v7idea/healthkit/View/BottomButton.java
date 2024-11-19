package com.v7idea.healthkit.View;

import android.content.Context;
import androidx.annotation.Nullable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.v7idea.healthkit.R;
import com.v7idea.template.Tool.ViewScaling;
import com.v7idea.template.View.V7TitleView;

public class BottomButton extends LinearLayout {
    private LinearLayout Container = null;
    private V7TitleView OneButton = null;
    private V7TitleView LeftButton = null;
    private V7TitleView RightButton = null;
    private Context mContext;

    private void init(Context context) {
        mContext = context;
        LayoutInflater inflater = LayoutInflater.from(mContext);
        View contextView = inflater.inflate(R.layout.customview_bottom_two_button, null);

        Container = (LinearLayout) ViewScaling.findViewByIdAndScale(contextView, R.id.BottomButton_LinearLayout_Container);
        LeftButton = (V7TitleView) ViewScaling.findViewByIdAndScale(contextView, R.id.BottomButton_V7TitleView_Left);
        RightButton = (V7TitleView) ViewScaling.findViewByIdAndScale(contextView, R.id.BottomButton_V7TitleView_Right);
        OneButton = (V7TitleView) ViewScaling.findViewByIdAndScale(contextView, R.id.BottomButton_V7TitleView_OneButton);

        addView(contextView, new LayoutParams(LayoutParams.MATCH_PARENT, (int) (100 * ViewScaling.getScaleValue())));
    }

    public BottomButton(Context context) {
        super(context);
        init(context);
    }

    public BottomButton(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public BottomButton(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    public BottomButton(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init(context);
    }

    public void setOneButtonText(String text) {
        OneButton.setText(text);
    }

    public void setLeftButtonText(String text) {
        LeftButton.setText(text);
    }

    public void setRightButtonText(String text) {
        RightButton.setText(text);
    }

    public void setHight(int hight) {
        Container.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, (int) (hight * ViewScaling.getScaleValue())));
    }

    public void setData(String leftText, String rightText, int hight) {
        setLeftButtonText(leftText);
        setRightButtonText(rightText);
        setHight(hight);
    }

    public void setData(String OneText, int hight) {
        OneButton.setVisibility(VISIBLE);
        LeftButton.setVisibility(GONE);
        RightButton.setVisibility(GONE);
        setOneButtonText(OneText);
        setHight(hight);
    }

    public LinearLayout getContainer() {
        return Container;
    }

    public V7TitleView getOneButton() {
        return OneButton;
    }

    public V7TitleView getLeftButton() {
        return LeftButton;
    }

    public V7TitleView getRightButton() {
        return RightButton;
    }
}
