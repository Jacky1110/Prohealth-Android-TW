package com.v7idea.template.View;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ListView;

/**
 * Created by mortal on 15/9/7.
 */
public class SpecialListView extends ListView {
    private OnOverScrolledListener OnOverScrolledListener = null;
    private OnScrollChangedListener OnScrollChangedListener = null;

    public SpecialListView(Context context) {
        super(context);
    }

    public SpecialListView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public SpecialListView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int expandSpec = MeasureSpec.makeMeasureSpec(Integer.MAX_VALUE >> 2,
                MeasureSpec.AT_MOST);
        super.onMeasure(widthMeasureSpec, expandSpec);
    }

    @Override
    protected int computeVerticalScrollOffset() {
        return super.computeVerticalScrollOffset();
    }


    @Override
    protected void onScrollChanged(int l, int t, int oldl, int oldt) {

        if (OnScrollChangedListener != null) {
            OnScrollChangedListener.OnScrollChangedListener(l, t, oldl, oldt);
        }

        super.onScrollChanged(l, t, oldl, oldt);
    }

    public interface OnScrollChangedListener {
        void OnScrollChangedListener(int l, int t, int oldl, int oldt);
    }

    @Override
    protected boolean overScrollBy(int deltaX, int deltaY, int scrollX, int scrollY
            , int scrollRangeX, int scrollRangeY, int maxOverScrollX, int maxOverScrollY, boolean isTouchEvent) {

//        Log.e("SpecialListView", "deltaY: " + deltaY + "  scrollRangeY: " + scrollRangeY
//                + "   maxOverScrollY: " + maxOverScrollY + "  isTouchEvent: " + isTouchEvent);

        if (isTouchEvent == true && scrollRangeY == 0 && maxOverScrollY == 0 && deltaY < 0) {
            if (OnOverScrolledListener != null) {
                OnOverScrolledListener.isOverScrolled(true);
            }
        }


        return super.overScrollBy(deltaX, deltaY, scrollX, scrollY
                , scrollRangeX, scrollRangeY, maxOverScrollX, maxOverScrollY, isTouchEvent);
    }

    public void setOnOverScrolledListener(OnOverScrolledListener OnOverScrolledListener) {
        this.OnOverScrolledListener = OnOverScrolledListener;
    }

    public void setOnScrollChangedListener(OnScrollChangedListener OnScrollChangedListener) {
        this.OnScrollChangedListener = OnScrollChangedListener;
    }

    public interface OnOverScrolledListener {
        void isOverScrolled(boolean isTopOverScrolled);
    }
}
