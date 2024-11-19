package com.v7idea.template.View;

import android.content.Context;
import android.os.Handler;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.ViewParent;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;

/**
 * 這個Scroll是用來取代目前的ScrollView, 目的是讓整個畫面有滑動的效果;
 *
 * @author louischuang
 */
public class ListScrollView extends ScrollView {
    private final static String TAG = "ListScrollView";

    private ListScrollView thisView = null;

    private int maxChangeParentScroll = 0;                // 最大移動的高度
    private int currentParentScroll = 0;                        // 目前母ScrollView已經移動的高度

    private boolean ifChangeParentScroll = false;        // 是否啟動變更母ScrollView;

    private int currentTop = 0;                                    // 目前的高度位置
    private int currentLeft = 0;                                    // 目前的左邊偏移位置

    private boolean isSwingUp = false;                        // 是否往回翻;
    private boolean isChangeParentState = false;        //	是否已經改變母Scroll的狀態

    private int selfOriginigalWidth = 0;                        // 本身原始的寬度;
    private int selfOriginigalHeight = 0;                        // 本身原始的高度;
    private int selfChangedWidth = 0;                        // 本身原始的寬度;
    private int selfChangedHeight = 0;                        // 本身原始的高度;

    private ViewParent thisParent = null;
    private int parentType = 0;                                    // 表示沒有找到Parent View的形態
    // 1: RelativeLayout
    // 2. LinearLayout;

    private LinearLayout mainLinearLayout = null;    // ScrollView內的主要容器
    private LinearLayout emptyArea = null;
    private LinearLayout contentArea = null;            // 真正放置內容的地方

    /**
     * 創建子
     *
     * @param context
     */
    public ListScrollView(Context context) {
        super(context);
        init();
    }

    public ListScrollView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public ListScrollView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    public void init() {
        thisView = this;

        thisView.setSmoothScrollingEnabled(true);
//        thisView.setOverScrollMode(OVER_SCROLL_IF_CONTENT_SCROLLS);

        // 表示有抓到Parent物件; 那就需要去找到這個物件的Type.
        parentType = getParentType(thisView.getParent());

        mainLinearLayout = new LinearLayout(getContext());
        //mainLinearLayout.setMeasureWithLargestChildEnabled(true);
        mainLinearLayout.setOrientation(LinearLayout.VERTICAL);

        LinearLayout.LayoutParams mainLinearLayoutParams = new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
        mainLinearLayoutParams.topMargin = 0;
        mainLinearLayoutParams.leftMargin = 0;
        mainLinearLayout.setLayoutParams(mainLinearLayoutParams);

        emptyArea = new LinearLayout(getContext());
        contentArea = new LinearLayout(getContext());
        contentArea.setOrientation(LinearLayout.VERTICAL);


        if (emptyArea != null) {

            LinearLayout.LayoutParams emptyAreaParams = new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, 1);//原本為20,祐鑫修改為1
            emptyAreaParams.topMargin = 0;
            emptyAreaParams.leftMargin = 0;

            emptyArea.setLayoutParams(emptyAreaParams);
            mainLinearLayout.addView(emptyArea);
//            emptyArea.setBackgroundColor(Color.WHITE);
//			emptyArea.setBackgroundColor(Color.BLUE);

            thisView.invalidate();
            Log.d(TAG, "top magrin of LinearLayout is changed. height:" + 20);

        }

        if (contentArea != null) {

            LinearLayout.LayoutParams contentAreaParams = new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
            contentAreaParams.topMargin = 0;
            contentAreaParams.leftMargin = 0;

            //contentArea.setLayoutParams(contentAreaParams);
            mainLinearLayout.addView(contentArea);


            thisView.invalidate();
            Log.d(TAG, "SET UP CONTENT VIEW top magrin of LinearLayout is changed. height:" + 20);

        }

        thisView.addView(mainLinearLayout);
    }

    public void setContentAreaGravity(int intGravity) {
        if (contentArea != null) contentArea.setGravity(intGravity);
    }

    /**
     * 重新改寫OnScrollChanged方法
     */
    @Override
    protected void onScrollChanged(int left, int top, int oldLeft, int oldTop) {

        super.onScrollChanged(left, top, oldLeft, oldTop);

        int totalHeight = getChildAt(0).getHeight();
        int limit = totalHeight / 200;

        int currentOffset = computeVerticalScrollOffset();

//        Log.i("Scroll is Changed!", "totalHeight: " + totalHeight);
//        Log.i("Scroll is Changed!", "limit: " + limit);
//        Log.i("Scroll is Changed!", "currentOffset: " + currentOffset);
//        Log.i("Scroll is Changed!", "top: " + top + "  oldTop: " + oldTop);


//        if (top > oldTop) {
//            OldScrollY += (top - oldTop);
//
//            if (OldScrollY >= limit)
//            {
//                if(ThisBottomMenuLayout != null && isClampedY == false)
//                {
//                    ThisBottomMenuLayout.scrollBottomMenuDown();
//                }
//
//                OldScrollY = 0;
//            }
//        } else if (top < oldTop) {
//            OldScrollY += (oldTop - top);
//
//            if (OldScrollY >= limit) {
//                if(ThisBottomMenuLayout != null && isClampedY == false)
//                {
//                    ThisBottomMenuLayout.scrollBottomMenuUp();
//                }
//
//                OldScrollY = 0;
//            }
//        }

//		Log.i("Scroll is Changed!", "top:" + top + " oldTop:" + oldTop);

//		
//		int rage = thisView.computeVerticalScrollOffset();
//		
//		Log.i("Scroll is Changed!","rage: " + rage);

        if (oldTop > top) {

            isSwingUp = true;//往下卷

        } else {

            isSwingUp = false;//往上卷

        }

        currentTop = top;
        currentLeft = left;

        currentScrollValue = currentTop;

//		Log.d("Scroll is Changed!","isSwingUp:" + isSwingUp);

        if (isSetTarget && targetLayout != null) {            // 如果有設定連動物件，那就必須要進行連動

            // 設定連動的動作

            RelativeLayout.LayoutParams thisParam = (RelativeLayout.LayoutParams) targetLayout.getLayoutParams();


            if (currentTop <= targetMaxScrollHeight) {

//   				Log.d("TARGET_CHANGED","newValue:" + (originalTargetTop - currentTop));

                if (thisParam != null) {

                    thisParam.topMargin = originalTargetTop - currentTop;
                    targetLayout.setLayoutParams(thisParam);

                }

            } else {

                if (thisParam != null) {

                    thisParam.topMargin = originalTargetTop - targetMaxScrollHeight;
                    targetLayout.setLayoutParams(thisParam);

                }
            }
        }

//		isSwingUp = true;//手指往下滑
//		isSwingUp = false;//手指往上滑

        if (canShowIcon) {
            if (isSwingUp) {
                if (isShowIcon) {
                    moveTopIcon.setVisibility(ImageView.GONE);
                    isShowIcon = false;
                }
            } else {
                if (top > 1280) {
                    if (!isShowIcon) {
                        moveTopIcon.setVisibility(ImageView.VISIBLE);
                        isShowIcon = true;
                    }
                }
            }
        }
    }

    /**
     * 設定是否開啟捲動設定
     *
     * @param changeStatus
     */
    public void setChangeParentScroll(boolean changeStatus) {

        ifChangeParentScroll = true;

    }

    /**
     * 設定自己變動的Size
     *
     * @param originalWidth  原始寬度
     * @param originalHeight 原始高度
     * @param changedWidth   改變過的寬度
     * @param changedHeight  改變過的高度
     */
    public void setSelfObjSize(int originalWidth, int originalHeight, int changedWidth, int changedHeight) {

        defineViewParent();

        selfOriginigalWidth = originalWidth;
        selfOriginigalHeight = originalHeight;
        selfChangedWidth = changedWidth;
        selfChangedHeight = changedHeight;

    }

    /**
     * 定義自己的ViewParent;
     */
    public void defineViewParent() {

        parentType = getParentType(thisView.getParent());

    }

    /**
     * 取得Parent View的類型
     *
     * @param viewParent
     */
    public int getParentType(ViewParent viewParent) {

        int thisType = 0;
        String className = null;

        if (viewParent != null) {

            thisParent = viewParent;

            className = viewParent.getClass().getName();

            Log.d("GET PARENT TYPE", "TYPE:" + className);

            if (className != null) {

                if (className.contentEquals("android.widget.RelativeLayout")) {

                    thisType = 1;

                } else if (className.contentEquals("android.widget.LinearLayout")) {

                    thisType = 2;

                } else {

                    thisType = 99;

                }

            }

        } else {


            Log.d("GET PARENT TYPE", "NULL");


        }

        Log.d("GET PARENT TYPE", "typeID:" + thisType);

        return thisType;

    }

    /**
     * 改變自己的寬度與高度
     *
     * @param width
     * @param height
     */
    public void setSelfSize(int width, int height) {

        Log.d("RESET WIDTH AND HEIGHT", "width:" + width + ";height:" + height + ";parentType:" + parentType);

        if (width != 0 && height != 0) {

            switch (parentType) {

                case 1:        // 表示為RelativeLayout

                    RelativeLayout.LayoutParams thisParam = (RelativeLayout.LayoutParams) thisView.getLayoutParams();
                    thisParam.width = width;
                    thisParam.height = height;
                    thisParam.bottomMargin = 0;
                    thisParam.topMargin = 0;

                    thisView.setLayoutParams(thisParam);

                    Log.d("set RelativeLayout", "設定好RelativeLayout");

                    break;

                case 2:

                    LinearLayout.LayoutParams thisParam2 = (LinearLayout.LayoutParams) thisView.getLayoutParams();
                    thisParam2.width = width;
                    thisParam2.height = height;
                    thisView.setLayoutParams(thisParam2);

                    Log.d("set RelativeLayout", "設定好LinearLayout");

                    break;

            }

        }

    }

    private RelativeLayout targetLayout = null;            // 設定影響滑動的Layout
    private int targetMaxScrollHeight = 0;                    // 最大滑動影響的範圍;
    private boolean isSetTarget = false;                        // 是否設定影響的Layout
    private int originalTargetTop = 0;                        // 一開始物件原始的高度

    /**
     * 設定要影響滑動的layout;
     *
     * @param layout       layout物件(使用RelativeLayout)
     * @param scrollHeight 最大變動的高度;
     */
    public void setTargetLayout(RelativeLayout layout, int scrollHeight) {

        targetLayout = layout;
        targetMaxScrollHeight = scrollHeight;
        isSetTarget = true;

        if (targetLayout != null) {

            RelativeLayout.LayoutParams thisParam = (RelativeLayout.LayoutParams) targetLayout.getLayoutParams();

            if (thisParam != null) {

                originalTargetTop = thisParam.topMargin;
                thisParam = null;

            }

        }

//        thisView.setChildrenDrawingCacheEnabled(true);

//			emptyBottom = scrollHeight;
    }

    /**
     * 設定是否要改變目標的Topmargin (連動滑動）
     * 如果有使用setTargetLayout, 則預設直會變成true, 可以利用此Method, 去關掉連動的功能
     *
     * @param ifYes
     */
    public void setIsChangeTarget(boolean ifYes) {

        isSetTarget = ifYes;

    }

    public void setTargetMaxScrollHeight(int scrollHeight)
    {
        targetMaxScrollHeight = scrollHeight;
    }

    /**
     * 設定ScrollView上方不放東西的區域
     *
     * @param height 高度
     */
    public void setTopEmptyArea(int height) {

        if (emptyArea != null) {

//            emptyArea.setBackgroundColor(Color.GREEN);

            LinearLayout.LayoutParams emptyAreaParams = new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, height);
            emptyAreaParams.topMargin = 0;
            emptyAreaParams.leftMargin = 0;

            emptyArea.setLayoutParams(emptyAreaParams);

            Log.d(TAG, "LINEARLAYOUT IN LISTSCROLLVIEW top magrin of LinearLayout is changed. height:" + height);

        }

        emptyBottom = height;
    }

    /* (non-Javadoc)
     * @see android.widget.ScrollView#addView(android.view.View)
     */
    @Override
    public void addView(View child) {

        Log.e("ListScrollView", "thisView.getChildCount():" + thisView.getChildCount());

        if (thisView.getChildCount() < 1) {

            super.addView(child);

        } else {

            contentArea.addView(child);
        }
    }

    /* (non-Javadoc)
     * @see android.widget.ScrollView#addView(android.view.View, int)
     */
    @Override
    public void addView(View child, int index) {
        // super.addView(child, index);

        if (thisView.getChildCount() < 1) {
            super.addView(child, index);
        } else {
            contentArea.addView(child, index);
        }


    }

    /* (non-Javadoc)
     * @see android.widget.ScrollView#addView(android.view.View, android.view.ViewGroup.LayoutParams)
     */
    @Override
    public void addView(View child, android.view.ViewGroup.LayoutParams params) {
        // super.addView(child, params);

        if (thisView.getChildCount() < 1) {
            super.addView(child, params);
        } else {
            contentArea.addView(child, params);
        }

    }

    //以下 祐鑫增加

    /**
     * 當下卷動的數值
     */
    public int currentScrollValue = 0;

    /**
     * 是否可以把 "移至最頂的Icon" show出來,這個參數只有在執行過 setMoveTopIcon(RelativeLayout moveTopParent, float scaleValue)這個方法才會改變成true
     */
    private boolean canShowIcon = false;

    /**
     * "移至最頂的Icon",只有在執行過 setMoveTopIcon(RelativeLayout moveTopParent, float scaleValue)這個方法才會被 new 出來
     */
    private ImageView moveTopIcon = null;

    /**
     * 是否己顯示Icon
     */
    private boolean isShowIcon = false;

    /**
     * 設定 "移至最頂的Icon"
     *
     * @param moveTopParent 加載這個Icon的容器
     * @param scaleValue    縮放值
     * @param marginTop     "移至最頂的Icon" 要出現的高度
     */
    public void setMoveTopIcon(RelativeLayout moveTopParent, float scaleValue, int marginTop) {
        if (moveTopParent != null) {
            moveTopIcon = new ImageView(getContext());
//			moveTopIcon.setImageResource(R.drawable.go_top);

            RelativeLayout.LayoutParams moveTopIconUse = new RelativeLayout.LayoutParams((int) (113 * scaleValue), (int) (74 * scaleValue));

            moveTopIconUse.addRule(RelativeLayout.ALIGN_PARENT_TOP);
            moveTopIconUse.addRule(RelativeLayout.CENTER_HORIZONTAL);
            moveTopIconUse.topMargin = marginTop;

            moveTopIcon.setLayoutParams(moveTopIconUse);

            moveTopParent.addView(moveTopIcon);

            moveTopIcon.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    thisView.smoothScrollBy(0, -currentTop);
                }
            });

            moveTopIcon.setVisibility(ImageView.GONE);

            canShowIcon = true;
        }
    }

    public int getCurrentScrollValue() {

        if (currentScrollValue > targetMaxScrollHeight) {
            currentScrollValue = targetMaxScrollHeight;
        } else if (currentScrollValue < 0) {
            currentScrollValue = 0;
        }

        return currentScrollValue;
    }

    /**
     * 最大變動的高度的值
     */
    private int emptyBottom = 0;

    /**
     * 資料型態,是 EventItem 或是 document
     */
    private String dataType = null;

    /**
     * 身分驗証碼
     */
    private String processTicketID = null;

    /**
     * 大類的名稱,大部分是document在用
     */
    private String fromCategoryName = null;

    /**
     * 連結更新的網址,大部分是document在用
     */
    private String mainAddress = null;

    /**
     * 容器
     */
    private LinearLayout contentLayout = null;

    private Handler handler = null;

    public void scrollToBottom()
    {
        if(mainLinearLayout != null)
        {
            scrollToBottom(mainLinearLayout);
        }
    }

    /**
     * 移置最底
     *
     * @param view 列表的容器
     */
    public void scrollToBottom(final View view) {
        Handler mHandler = new Handler();

        mHandler.post(new Runnable() {
            public void run() {
                int offset = view.getMeasuredHeight() - thisView.getHeight() + emptyBottom;
                if (offset < 0) {
                    offset = 0;
                }

                thisView.scrollTo(0, offset);
            }
        });
    }


    private void release() {
        if (contentLayout != null) contentLayout.removeAllViews();
        contentLayout = null;
    }

    @Override
    protected void onDetachedFromWindow() {

        super.onDetachedFromWindow();

        release();
    }
}
