package com.v7idea.healthkit.View;

import android.content.Context;
import androidx.annotation.Nullable;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.v7idea.healthkit.R;
import com.v7idea.template.Tool.ViewScaling;

public class TitleEditTextView extends RelativeLayout {
    public TextView Title = null;
    public EditText EditText = null;
    public RelativeLayout Container = null;
    private Context mContext;

    private void init(Context context) {
        mContext = context;
        LayoutInflater inflater = LayoutInflater.from(mContext);
        View contextView = inflater.inflate(R.layout.customview_title_edit, null);
        Title = (TextView) ViewScaling.findViewByIdAndScale(contextView, R.id.TitleEditTextView_TextView_Title);
        EditText = (EditText) ViewScaling.findViewByIdAndScale(contextView, R.id.TitleEditTextView_EditText_Value);
        Container = (RelativeLayout) ViewScaling.findViewByIdAndScale(contextView, R.id.TitleEditTextView_RelativeLayout_Container);

        addView(contextView);
    }

    public TitleEditTextView(Context context) {
        super(context);
        init(context);
    }

    public TitleEditTextView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public TitleEditTextView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    public TitleEditTextView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init(context);
    }


    public static final int Type1_90px = 90;
    public static final int Type2_100px = 100;
    public static final int Type3_120px = 120;
    public static final int Type4_128px = 128;

    public void setData(String title, String editTextHint, int hight) {
        setTitleText(title);
        setEditTextHint(editTextHint);
        setHight(hight);
    }

    public void setTitleText(String title) {
        Title.setText(title);
    }

    public void setEditTextHint(String editTextHint) {
        EditText.setHint(editTextHint);
    }

    public void setHight(int hight) {
        Container.setLayoutParams(new RelativeLayout.LayoutParams(LayoutParams.MATCH_PARENT, (int) (hight * ViewScaling.getScaleValue())));
    }

    public void setTitleLeftPadding(int value) {
        RelativeLayout.LayoutParams layoutParams = (LayoutParams) Title.getLayoutParams();
        layoutParams.leftMargin = (int) (value * ViewScaling.getScaleValue());
        Title.setLayoutParams(layoutParams);
    }

    public void setUserBasicInformation_ON() {
        Title.setGravity(Gravity.LEFT | Gravity.CENTER_VERTICAL);
        setTitleLeftPadding(54);
    }

    public void setUserBasicInformation_OFF() {
        Title.setGravity(Gravity.LEFT | Gravity.CENTER_VERTICAL);
        EditText.setEnabled(false);
        setTitleLeftPadding(54);
        Container.setBackground(getResources().getDrawable(R.drawable.gray_frame_dark_gray_without_outline_content));
    }
}