package com.v7idea.template.View;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.widget.ScrollView;

/**
 * Created by mortal on 15/2/11.
 */
public class LockScrollView extends ScrollView
{
    private final String tag = "LockScrollView";

    private boolean mScrollable = true;
    private int softWareKeyBoardHeight = -1;
    private int scrollOffset = 0;

    private OnSoftKeyboardListener onSoftKeyboardListener = null;

    public LockScrollView(Context context) {
        super(context);
    }

    public LockScrollView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public LockScrollView(Context context, AttributeSet attrs, int defStyle, boolean mScrollable)
    {
        super(context, attrs, defStyle);
    }

    public void setScrollingEnabled(boolean enabled) {
        mScrollable = enabled;
    }

    public boolean isScrollable() {
        return mScrollable;
    }

    public void setSoftWareKeyBoardHeight(int softWareKeyBoardHeight) {
        this.softWareKeyBoardHeight = softWareKeyBoardHeight;
    }

    public void setScrollOffset(int scrollOffset) {
        this.scrollOffset = scrollOffset;
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        switch (ev.getAction()) {
            case MotionEvent.ACTION_DOWN:
                // if we can scroll pass the event to the superclass
                if (mScrollable) return super.onTouchEvent(ev);
                // only continue to handle the touch event if scrolling enabled
                return mScrollable; // mScrollable is always false at this point
            default:
                return super.onTouchEvent(ev);
        }
    }

    @Override
    protected void onMeasure(final int widthMeasureSpec, final int heightMeasureSpec) {
        if (onSoftKeyboardListener != null)
        {
            final int newSpec = MeasureSpec.getSize(heightMeasureSpec);
            final int oldSpec = getMeasuredHeight();
            if (oldSpec > newSpec){
                onSoftKeyboardListener.onShown();
            } else {
                onSoftKeyboardListener.onHidden();
            }
        }
          final int newSpec = MeasureSpec.getSize(heightMeasureSpec);
          final int oldSpec = getMeasuredHeight();

        Log.e(tag, "newSpec: " + newSpec + "  oldSpec: " + oldSpec);

        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b)
    {
        super.onLayout(changed, l, t, r, b);

        Log.e(tag, "l: " + l + "  t: " + t + " r: " + r + "  b: " + b);
    }

    public final void setOnSoftKeyboardListener(final OnSoftKeyboardListener listener) {
        this.onSoftKeyboardListener = listener;
    }

    public interface OnSoftKeyboardListener {
        public void onShown();
        public void onHidden();
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        // Don't do anything with intercepted touch events if
        // we are not scrollable
        if (!mScrollable) return false;
        else return super.onInterceptTouchEvent(ev);
    }

    @Override
    protected void onScrollChanged(int l, int t, int oldl, int oldt)
    {
//        Log.e(tag, "l: " + l + "  t: " + t + "  oldl: " + oldl + "  oldt: "+oldt);

        int scrollLimit = softWareKeyBoardHeight - scrollOffset;

        if(t >= scrollLimit)
        {
            t = scrollLimit;
            oldt = scrollLimit;
        }

        super.onScrollChanged(l, t, oldl, oldt);
    }

    @Override
    protected void onOverScrolled(int scrollX, int scrollY, boolean clampedX, boolean clampedY)
    {
        int scrollLimit = softWareKeyBoardHeight - scrollOffset;

        if(scrollY >= scrollLimit)scrollY = scrollLimit;

        super.onOverScrolled(scrollX, scrollY, clampedX, clampedY);
    }
}
