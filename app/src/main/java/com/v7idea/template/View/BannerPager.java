package com.v7idea.template.View;

import android.content.Context;
import android.graphics.Color;
import android.os.Handler;
import android.os.Message;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

//import com.google.android.youtube.player.YouTubePlayer;
//import com.google.android.youtube.player.YouTubePlayerView;
//import com.google.android.youtube.player.YouTubeThumbnailView;

import com.v7idea.template.Activity.BaseActivity;
import com.v7idea.template.Tool.ListItem;

import java.util.ArrayList;
import java.util.List;

/**
 * @author louischuang
 */
public class BannerPager extends ViewPager {

    private Context mainContext = null;     // 主要的容器
    private BannerPager thisObj = null;

    private int objectWidth = 800;                                // 物件的寬度
    private int objectHeight = 450;                            // 物件的寬度
    private int pageNum = 0;                                        // 這個物件的頁數
    private int currentPageNo = 0;                                // 目前頁面的Index;

    private int singleBannerImageWidth = 0;                // 預設一個Banner的寬度
    private int singleBannerImageHeight = 0;                // 預設一個Banner的高度
    private int singleBannerImageTop = 0;                    // 預設一個Banner在整體位置的y軸位置
    private int singleBannerImageLeft = 0;                    // 預設一個Banner在整體位置的x軸位置

    private int secondBannerImageWidth = 0;                // 預設一個子Banner的寬度
    private int secondBannerImageHeight = 0;            // 預設一個子Banner的高度
    private int secondBannerImageTop = 0;                // 預設一個子Banner在整體位置的y軸位置
    private int secondBannerImageLeft = 0;                // 預設一個子Banner在整體位置的x軸位置

    private boolean hasTitle = true;                            // 預設是否有標題;
    private boolean hasSubTitle = true;                        // 預設是否有副標題
    private boolean hasCustomTitleArea = false;        // 是否是客製化標題

    private int singleTitleAreaWidth = 0;                    // 預設一個標題的寬度
    private int singleTitleAreaHeight = 0;                    // 預設一個標題列的高度
    private int singleTitleAreaLeft = 0;                        // 預設一個標題列位置的x軸位置
    private int singleTitleAreaTop = 0;                        // 預設一個標題列位置的x軸位置
    private int singleTitleAreaBackgroundColor = Color.TRANSPARENT;        // 預設一個標題列的背景顏色;

    private int subMainTitleWidth = 0;                        // 預設一個主標題的寬度
    private int subMainTitleHeight = 0;                        // 預設一個主標題列的高度
    private int subMainTitleLeft = 0;                            // 預設一個主標題列位置的x軸位置
    private int subMainTitleTop = 0;                            // 預設一個主標題列位置的x軸位置
    private int subMainTitleFontColor = Color.BLACK;        // 主標題的字體顏色
    private int subMainTitleFontSize = 0;                        // 主標題的字體大小

    private int subSecondTitleWidth = 0;                    // 預設一個主標題的寬度
    private int subSecondTitleHeight = 0;                    // 預設一個主標題列的高度
    private int subSecondTitleLeft = 0;                        // 預設一個主標題列位置的x軸位置
    private int subSecondTitleTop = 0;                        // 預設一個主標題列位置的x軸位置
    private int subSecondTitleFontColor = Color.BLACK;            // 主標題的字體顏色
    private int subSecondTitleFontSize = 0;                        // 主標題的字體大小

    private LinearLayout contentView = null;            // ScrollView所包含的容器;

    private BannerPagerAdapter thisBannerAdapter = null;
    private ArrayList<View> viewList = null;                // Banner內容列表

    /**
     * 文字區的背景,祐鑫增加
     */
    private int titleAreaBackground = -1;

    /**
     * 記錄資料在ArrayList位置
     * <p/>
     * 祐鑫增加
     */
    private ArrayList<String> recordDataPosition = null;

    public BannerPager(Context context, int width, int height, int orientationType) {
        super(context);

        mainContext = context;                // 主要容器
        thisObj = this;

        objectWidth = width;                // 定義此物件的大小 (寬）
        objectHeight = height;            // 定義此物見的大小 (高）

        setBannerImageSize(width, height, 0, 0);            // 設定預設的位置;

        thisObj.setBackgroundColor(Color.BLACK);
        thisObj.setAlwaysDrawnWithCacheEnabled(true);

        recordDataPosition = new ArrayList<String>();

        thisObj.setOnPageChangeListener(new OnPageChangeListener() {

            @Override
            public void onPageScrollStateChanged(int arg0) {
                // TODO Auto-generated method stub

            }

            @Override
            public void onPageScrolled(int pageNo, float arg1, int arg2) {
                // TODO Auto-generated method stub

                currentPageNo = pageNo;
//				Log.d("VIEWPAGER SCROLL END","PageNo:" + pageNo);


            }

            @Override
            public void onPageSelected(int arg0) {
                // TODO Auto-generated method stub

            }
        });


    }

    /***
     * 設定要顯示的Banner Image要出現的寬度與高度
     *
     * @param width  寬度
     * @param height 高度
     * @param top    位置(X)
     * @param left   位置(Y)
     */
    public void setBannerImageSize(int width, int height, int top, int left) {

        singleBannerImageWidth = width;
        singleBannerImageHeight = height;
        singleBannerImageLeft = left;
        singleBannerImageTop = top;

        // 重新變更位置與大小

        if (viewList != null && viewList.size() > 0) {            // 表示BannerList  裡面有存在Banner

            for (int i = 0; i < viewList.size(); i++) {

                Banner thisBanner = (Banner) viewList.get(i);
                ImageView thisImage = thisBanner.getImageView();

                if (thisImage != null) {

                    RelativeLayout.LayoutParams thisParams = new RelativeLayout.LayoutParams(singleBannerImageWidth, singleBannerImageHeight);
                    thisParams.width = singleBannerImageWidth;
                    thisParams.height = singleBannerImageHeight;

                    thisParams.topMargin = singleBannerImageTop;
                    thisParams.leftMargin = singleBannerImageLeft;
                    thisImage.setLayoutParams(thisParams);

                }

            }

        }

    }

    /***
     * 設定要顯示的Banner Image要出現的寬度與高度
     *
     * @param width  寬度
     * @param height 高度
     * @param top    位置(X)
     * @param left   位置(Y)
     */
    public void setSecondImageSize(int width, int height, int top, int left) {

        secondBannerImageWidth = width;
        secondBannerImageHeight = height;
        secondBannerImageLeft = left;
        secondBannerImageTop = top;

        // 重新變更位置與大小

        if (viewList != null && viewList.size() > 0) {            // 表示BannerList  裡面有存在Banner

            for (int i = 0; i < viewList.size(); i++) {

                Banner thisBanner = (Banner) viewList.get(i);
                ImageView thisImage = thisBanner.getSecondImageView();

                if (thisImage != null) {

                    RelativeLayout.LayoutParams thisParams = new RelativeLayout.LayoutParams(secondBannerImageWidth, secondBannerImageHeight);
                    thisParams.width = secondBannerImageWidth;
                    thisParams.height = secondBannerImageHeight;

                    thisParams.topMargin = secondBannerImageTop;
                    thisParams.leftMargin = secondBannerImageLeft;
                    thisImage.setLayoutParams(thisParams);

                }

            }

        }


    }

    /**
     * 祐鑫增加
     * <p/>
     * 設定這個Banner有一個Title區域
     *
     * @param width      寬度
     * @param height     高度
     * @param top        位置(Y)
     * @param left       位置(X)
     * @param resourceId 背景的圖片
     */
    public void setTitleArea(int width, int height, int top, int left, int resourceId) {

        // 設定整個標題區域的大小

        hasTitle = true;
        singleTitleAreaWidth = width;
        singleTitleAreaHeight = height;
        singleTitleAreaTop = top;
        singleTitleAreaLeft = left;

        // 設定標題的大小

        subMainTitleWidth = width;
        subMainTitleHeight = height;
        subMainTitleTop = top;
        subMainTitleLeft = left;

        titleAreaBackground = resourceId;
    }

    /**
     * 設定這個Banner有一個Title區域
     *
     * @param width  寬度
     * @param height 高度
     * @param top    位置(Y)
     * @param left   位置(X)
     */
    public void setTitleArea(int width, int height, int top, int left) {

        // 設定整個標題區域的大小

        hasTitle = true;
        singleTitleAreaWidth = width;
        singleTitleAreaHeight = height;
        singleTitleAreaTop = top;
        singleTitleAreaLeft = left;

        // 設定標題的大小

        subMainTitleWidth = width;
        subMainTitleHeight = height;
        subMainTitleTop = top;
        subMainTitleLeft = left;

    }

    /**
     * 設定在標題區內的主標題文字長、寬、位置
     *
     * @param width     寬度
     * @param height    高度
     * @param top       位置(Y)
     * @param left      位置(X)
     * @param fontColor 字體顏色
     * @param fontSize  字體大小
     */
    public void setSubMainTitle(int width, int height, int top, int left, int fontColor, int fontSize) {

        if (!hasTitle) {            // 如果還沒有設定整個標題區域時，必須要先設定整個標題區域

            hasTitle = true;
            singleTitleAreaWidth = width;
            singleTitleAreaHeight = height;
            singleTitleAreaTop = top;
            singleTitleAreaLeft = left;

        }

        subMainTitleWidth = width;
        subMainTitleHeight = height;
        subMainTitleTop = top;
        subMainTitleLeft = left;
        subMainTitleFontSize = fontSize;
        subMainTitleFontColor = fontColor;

    }

    /**
     * 設定主標題區內副標題的文字大小與位置
     *
     * @param width     寬度
     * @param height    高度
     * @param top       位置(Y)
     * @param left      位置(X)
     * @param fontColor 字體顏色
     * @param fontSize  字體大小
     */
    public void setSubSecondTitle(int width, int height, int top, int left, int fontColor, int fontSize) {

        subSecondTitleWidth = width;
        subSecondTitleHeight = height;
        subSecondTitleTop = top;
        subSecondTitleLeft = left;
        subSecondTitleFontSize = fontSize;
        subSecondTitleFontColor = fontColor;

    }

    /**
     * 新增一個BannerImage
     *
     * @param thisBannerImage
     */
    public void addBanner(ImageView thisBannerImage, OnClickListener thisListener) {

        Banner thisBanner = new Banner(mainContext, thisBannerImage);

        if (viewList == null) {            // 如果BannerList爲Null, 請創建新的物件

            viewList = new ArrayList<View>();

        }

        if (thisListener != null) {                // 設定這個Banner的Listener

            thisBanner.setOnClickListener(thisListener);

        }

        viewList.add(thisBanner);                // 用來檢查是否可以用ViewPager;
        //contentView.addView(thisBanner);
        Log.d("BANNER", "ADD NEW BANNER");

        update();

    }

    /**
     * 新增一個BannerImage
     *
     * @param thisBannerImage
     * @param mainTitle
     */
    public void addBanner(ImageView thisBannerImage, String mainTitle, OnClickListener thisListener) {

        Banner thisBanner = new Banner(mainContext, thisBannerImage, mainTitle);

        if (viewList == null) {            // 如果BannerList爲Null, 請創建新的物件

            viewList = new ArrayList<View>();

        }

        if (thisListener != null) {                // 設定這個Banner的Listener

            thisBanner.setOnClickListener(thisListener);

        }

        viewList.add(thisBanner);
        //contentView.addView(thisBanner);

        update();
    }

    /**
     * 新增一個BannerImage
     *
     * @param thisBannerImage
     * @param mainTitle
     */
    public void addBanner(ImageView thisBannerImage, ImageView secondImage, String mainTitle, OnClickListener thisListener) {

        Banner thisBanner = new Banner(mainContext, thisBannerImage, secondImage, mainTitle);

        if (viewList == null) {            // 如果BannerList爲Null, 請創建新的物件

            viewList = new ArrayList<View>();

        }

        if (thisListener != null) {                // 設定這個Banner的Listener

            thisBanner.setOnClickListener(thisListener);

        }

        viewList.add(thisBanner);
        //contentView.addView(thisBanner);

        update();
    }

    /**
     * 新增一個BannerImage
     *
     * @param thisBannerImage 一個Banner的ImageView;
     * @param mainTitle       主標題;
     * @param subTitle        副標題;
     */
    public void addBanner(ImageView thisBannerImage, String mainTitle, String subTitle, OnClickListener thisListener) {

        Banner thisBanner = new Banner(mainContext, thisBannerImage, mainTitle, subTitle);

        if (viewList == null) {            // 如果BannerList爲Null, 請創建新的物件

            viewList = new ArrayList<View>();

        }

        if (thisListener != null) {                // 設定這個Banner的Listener

            thisBanner.setOnClickListener(thisListener);

        }

        viewList.add(thisBanner);
        // contentView.addView(thisBanner);

        update();

    }

    /**
     * 新增一個BannerImage
     *
     * @param thisBannerImage 一個Banner的ImageView;
     * @param mainTitle       主標題;
     * @param subTitle        副標題;
     */
    public void addBanner(ImageView thisBannerImage, ImageView anotherImage, String mainTitle, String subTitle, OnClickListener thisListener) {

        Banner thisBanner = new Banner(mainContext, thisBannerImage, anotherImage, mainTitle, subTitle);

        if (viewList == null) {            // 如果BannerList爲Null, 請創建新的物件

            viewList = new ArrayList<View>();

        }

        if (thisListener != null) {                // 設定這個Banner的Listener

            thisBanner.setOnClickListener(thisListener);

        }

        viewList.add(thisBanner);
        // contentView.addView(thisBanner);

        update();

    }

    /**
     * 祐鑫增加
     *
     * @param thisBannerImage 一個Banner的ImageView;
     * @param mainTitle       主標題;
     * @param subTitle        副標題;
     * @param dataId          資料的Id
     */
    public void addBanner(ImageView thisBannerImage, ImageView anotherImage, String mainTitle, String subTitle, String dataId, OnClickListener thisListener) {

        Banner thisBanner = new Banner(mainContext, thisBannerImage, anotherImage, mainTitle, subTitle);

        if (viewList == null) {            // 如果BannerList爲Null, 請創建新的物件

            viewList = new ArrayList<View>();

        }

        if (thisListener != null) {                // 設定這個Banner的Listener

            thisBanner.setOnClickListener(thisListener);

        }

        viewList.add(thisBanner);
        // contentView.addView(thisBanner);

        recordDataPosition.add(dataId);

        update();

    }

    /**
     * 祐鑫增加
     * 新增一個BannerImage
     *
     * @param thisBannerImage 一個Banner的ImageView;
     * @param mainTitle       主標題;
     * @param subTitle        副標題;
     */
    public void addBanner(ImageView thisBannerImage, String mainTitle, String subTitle, String dataId, OnClickListener thisListener) {

        Banner thisBanner = new Banner(mainContext, thisBannerImage, mainTitle, subTitle);

        if (viewList == null) {            // 如果BannerList爲Null, 請創建新的物件

            viewList = new ArrayList<View>();

        }

        if (thisListener != null) {                // 設定這個Banner的Listener

            thisBanner.setOnClickListener(thisListener);

        }

        viewList.add(thisBanner);
        // contentView.addView(thisBanner);

        recordDataPosition.add(dataId);

        update();

    }

    public static void insertBannerImage(final BaseActivity useActivity, ArrayList<ListItem> dataArray, BannerPager targetBannerPager)
    {
        if(targetBannerPager != null && dataArray != null && dataArray.size() > 0)
        {
            for(int i = 0 ; i < dataArray.size() ; i++)
            {
                ListItem item = dataArray.get(i);

                AutoReleaseImageView imageView = new AutoReleaseImageView(useActivity);
                imageView.setScaleType(ScaleType.FIT_XY);
                imageView.setAutoReleaseImage(false);

                imageView.setImageResource(item.getIconResourceID());

                targetBannerPager.addBanner(imageView, "", "", new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                    }
                });
            }
        }
    }


    /**
     * 移除與dataId相同的Banner
     *
     * @param dataId
     */
    public boolean removeBanner(String dataId) {
        boolean isRemove = false;

        if (recordDataPosition != null && dataId != null) {
            int dataPosition = recordDataPosition.indexOf(dataId);

            if (viewList != null && dataPosition > -1) {
//                Log.d("BannerPager_removeBanner", "dataPosition: " + dataPosition);

                viewList.remove(dataPosition);
                update();
                recordDataPosition.remove(dataId);

//                Log.d("BannerPager_removeBanner", "remove success !!");

                isRemove = true;
            } else {
//                Log.e("BannerPager_removeBanner", "dataPosition is null !!");

                isRemove = false;
            }
        }

        return isRemove;
    }

    /**
     * 檢查該Id是否己存在於banner中
     *
     * @param dataId
     * @return
     */
    public int getBannerPositionByDataId(String dataId) {
        return recordDataPosition.indexOf(dataId);
    }

//    public void addBanner(YouTubeThumbnailView youTubeThumbnailView
//            , YouTubePlayerView youTubePlayerView, ImageView videoIcon, OnClickListener thisListener) {
//
//        Banner thisBanner = new Banner(mainContext, youTubeThumbnailView);
//        thisBanner.setYouTubePlayerView(youTubePlayerView);
////		youTubePlayerView.setVisibility(View.GONE);
//
//        thisBanner.setSecondImage(videoIcon);
//
//        if (viewList == null) {            // 如果BannerList爲Null, 請創建新的物件
//
//            viewList = new ArrayList<View>();
//
//        }
//
//        if (thisListener != null) {                // 設定這個Banner的Listener
//
//            thisBanner.setOnClickListener(thisListener);
//            videoIcon.setOnClickListener(thisListener);
//        }
//
//        viewList.add(thisBanner);
//        // contentView.addView(thisBanner);
//
//        update();
//    }

    /**
     * @param thisBannerImage
     * @param secondImage
     */
    public void addBanner(ImageView thisBannerImage, ImageView secondImage, OnClickListener thisListener) {

        Banner thisBanner = new Banner(mainContext, thisBannerImage);
        thisBanner.setSecondImage(secondImage);

        if (viewList == null) {            // 如果BannerList爲Null, 請創建新的物件

            viewList = new ArrayList<View>();

        }

        if (thisListener != null) {                // 設定這個Banner的Listener

            thisBanner.setOnClickListener(thisListener);

        }

        viewList.add(thisBanner);
        // contentView.addView(thisBanner);

        update();

    }

    /***
     * 取回ViewPager
     *
     * @return bannerViewPager;
     */
    public ViewPager getViewPager() {

        return thisObj;

    }

    /**
     * 更新內容;
     */
    public void update() {

        if (thisBannerAdapter == null) {
            thisBannerAdapter = new BannerPagerAdapter(viewList);
            thisObj.setAdapter(thisBannerAdapter);
        } else {
            thisBannerAdapter.setList(viewList);
        }

        pageNum = viewList.size();                // 更新目前頁數;

    }

    /**
     * 釋放資源
     * <p/>
     * 祐鑫增加
     */
    public void release() {
        if (thisObj != null) thisObj.stopAutoScroll();

        if (thisBannerAdapter != null) {
            thisBannerAdapter.release();
        }

        thisBannerAdapter = null;

        if (viewList != null) {
            viewList.clear();
            viewList = null;
        }

        mainContext = null;     // 主要的容器
        thisObj = null;
    }

    public int getBannerSize() {
        if (thisBannerAdapter != null) {
            return thisBannerAdapter.getCount();
        }

        return 0;
    }

    public void reset() {
        if (thisBannerAdapter != null) {
            thisBannerAdapter.reset();
        }

//        if (viewList != null) {
//            viewList.clear();
//            viewList = null;
//        }
    }


    /**
     * 這是一個單一Banner的物件,有關於Banner的產生都交給這個物件
     * 這個Banner物件可以有圖片，連結Intent，以及上方可以有一個標題列與副標題列
     *
     * @author louischuang
     */
    public class Banner extends RelativeLayout {

        Banner thisBanner = null;                                                // 指向自己的Banner
        private String mainTitleText = "";                                    // 主標題文字
        private String subTitleText = "";                                    // 副標題文字

        private boolean hasTitle = true;                                    // 預設是否有標題;
        private boolean hasSubTitle = true;                                // 預設是否有副標題
        private boolean hasCustomTitleArea = false;                // 是否是客製化標題

        private int titleAreaWidth = 0;                                        // 標題區寬度
        private int titleAreaHeight = 0;                                        // 標題區高度

        private RelativeLayout customTitleLayout = null;            // 客製化的標題區域

        private ImageView bannerImage = null;                        // 顯示Banner的圖片
        private ImageView secondImage = null;
        private RelativeLayout titleArea = null;
        private TextView mainTitle = null;
        private TextView subTitle = null;

//        private YouTubePlayerView youTubePlayerView = null;
//        private YouTubePlayer youTubePlayer = null;
//
//        public YouTubePlayerView getYouTubePlayerView() {
//            return youTubePlayerView;
//        }
//
//        public void setYouTubePlayer(YouTubePlayer youTubePlayer) {
//            this.youTubePlayer = youTubePlayer;
//        }
//
//        public void setYouTubePlayerView(YouTubePlayerView youTubePlayerView) {
//            this.youTubePlayerView = youTubePlayerView;
//            thisBanner.addView(this.youTubePlayerView, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT
//                    , ViewGroup.LayoutParams.MATCH_PARENT));
//        }
//
//        public YouTubePlayer getYouTubePlayer() {
//            return youTubePlayer;
//        }
//
        public void release() {
//            if (youTubePlayer != null) {
//                youTubePlayer.release();
//            }
        }

        /**
         * 建構這個Banner的建構子
         *
         * @param context 傳入Activity.Context;
         */
        public Banner(Context context) {
            super(context);

            thisBanner = this;

            LinearLayout.LayoutParams thisParams = new LinearLayout.LayoutParams(objectWidth, objectHeight);
            thisBanner.setLayoutParams(thisParams);
            // contentView.addView(thisBanner);

        }

//        public Banner(Context context, YouTubePlayerView youTubePlayerView) {
//            super(context);
//
//            thisBanner = this;
//            LinearLayout.LayoutParams thisParams = new LinearLayout.LayoutParams(objectWidth, objectHeight);
//            thisBanner.setLayoutParams(thisParams);
//
//            LayoutParams viewParams = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
//            youTubePlayerView.setLayoutParams(viewParams);
//            thisBanner.addView(youTubePlayerView);
//        }

        /***
         * 建構子包含了直接增加新的ImageView
         *
         * @param context  容器
         * @param thisVIew 建構子包含了直接增加新的ImageView
         */
        public Banner(Context context, ImageView thisVIew) {
            super(context);

            thisBanner = this;
            LinearLayout.LayoutParams thisParams = new LinearLayout.LayoutParams(objectWidth, objectHeight);
            thisBanner.setLayoutParams(thisParams);
            setBannerImage(thisVIew);

        }

        /***
         * 建構子包含了直接增加新的ImageView
         *
         * @param context     容器
         * @param thisVIew    建構子包含了直接增加新的ImageView
         * @param anotherView 建構子包含了直接增加新次要的ImageView
         */
        public Banner(Context context, ImageView thisVIew, ImageView anotherView) {
            super(context);

            thisBanner = this;
            LinearLayout.LayoutParams thisParams = new LinearLayout.LayoutParams(objectWidth, objectHeight);
            thisBanner.setLayoutParams(thisParams);
            setBannerImage(thisVIew);
            setSecondImage(anotherView);

        }

        /***
         * 建構子包含了直接增加新的ImageView
         *
         * @param context       容器
         * @param thisVIew      建構子包含了直接增加新的ImageView
         * @param mainTitleText 主標題文字
         */
        public Banner(Context context, ImageView thisVIew, String mainTitleText) {
            super(context);

            thisBanner = this;

            LinearLayout.LayoutParams thisParams = new LinearLayout.LayoutParams(objectWidth, objectHeight);
            thisBanner.setLayoutParams(thisParams);
            setBannerImage(thisVIew, mainTitleText);

        }

        /***
         * 建構子包含了直接增加新的ImageView
         *
         * @param context       容器
         * @param thisVIew      建構子包含了直接增加新的ImageView
         * @param anotherView   建構子包含了直接增加第二個ImageView
         * @param mainTitleText 主標題文字
         */
        public Banner(Context context, ImageView thisVIew, ImageView anotherView, String mainTitleText) {
            super(context);

            thisBanner = this;

            LinearLayout.LayoutParams thisParams = new LinearLayout.LayoutParams(objectWidth, objectHeight);
            thisBanner.setLayoutParams(thisParams);
            setBannerImage(thisVIew);
            setSecondImage(anotherView);
            setMainTitle(mainTitleText);

        }

        /***
         * 建構子包含了直接增加新的ImageView
         *
         * @param context       容器
         * @param thisVIew      建構子包含了直接增加新的ImageView
         * @param mainTitleText 主標題文字
         * @param subTitleText  副標題文字
         */
        public Banner(Context context, ImageView thisVIew, String mainTitleText, String subTitleText) {
            super(context);

            thisBanner = this;

            LinearLayout.LayoutParams thisParams = new LinearLayout.LayoutParams(objectWidth, objectHeight);
            thisBanner.setLayoutParams(thisParams);
            setBannerImage(thisVIew, mainTitleText, subTitleText);

        }

        /***
         * 建構子包含了直接增加新的ImageView
         *
         * @param context       容器
         * @param thisVIew      建構子包含了直接增加新的ImageView
         * @param mainTitleText 主標題文字
         * @param subTitleText  副標題文字
         */
        public Banner(Context context, ImageView thisVIew, ImageView anotherView, String mainTitleText, String subTitleText) {
            super(context);

            thisBanner = this;

            LinearLayout.LayoutParams thisParams = new LinearLayout.LayoutParams(objectWidth, objectHeight);
            thisBanner.setLayoutParams(thisParams);
            setBannerImage(thisVIew);
            setSecondImage(anotherView);
            setMainTitle(mainTitleText);
            setSubTitle(subTitleText);

        }

        /**
         * 設定主要的影像，使用的方式是傳入ImageView;
         *
         * @param thisImage 一個已經做好的ImageView;
         */
        public void setBannerImage(ImageView thisImage) {

            bannerImage = thisImage;
            LayoutParams thisParams = new LayoutParams(singleBannerImageWidth, singleBannerImageHeight);
            thisParams.width = singleBannerImageWidth;
            thisParams.height = singleBannerImageHeight;
            thisParams.topMargin = singleBannerImageTop;
            thisParams.leftMargin = singleBannerImageLeft;
            bannerImage.setLayoutParams(thisParams);

            Log.d("BANNER IMAGE", "width:" + singleBannerImageWidth + ";height:" + singleBannerImageHeight);

            thisBanner.addView(bannerImage);
            bannerImage.setScaleType(ScaleType.CENTER_CROP);
//			bannerImage.setScaleType(ScaleType.FIT_XY);

        }

        /**
         * 設定主要的影像，使用的方式是傳入ImageView;
         *
         * @param andotherImage 一個已經做好的ImageView;
         */
        public void setBannerImage(ImageView mainImage, ImageView andotherImage) {

            bannerImage = mainImage;
            secondImage = andotherImage;

            LayoutParams thisParams = new LayoutParams(singleBannerImageWidth, singleBannerImageHeight);
            thisParams.width = singleBannerImageWidth;
            thisParams.height = singleBannerImageHeight;
            thisParams.topMargin = singleBannerImageTop;
            thisParams.leftMargin = singleBannerImageLeft;
            bannerImage.setLayoutParams(thisParams);

            Log.d("BANNER IMAGE", "width:" + singleBannerImageWidth + ";height:" + singleBannerImageHeight);

            thisBanner.addView(bannerImage);
            bannerImage.setScaleType(ScaleType.FIT_XY);
            setSecondImage(andotherImage);

        }

        /**
         * 設定第二個的影像，使用的方式是傳入ImageView;
         *
         * @param thisImage 一個已經做好的ImageView;
         */
        public void setSecondImage(ImageView thisImage) {

            secondImage = thisImage;
            LayoutParams thisParams = new LayoutParams(secondBannerImageWidth, secondBannerImageHeight);
            thisParams.width = secondBannerImageWidth;
            thisParams.height = secondBannerImageHeight;
            thisParams.topMargin = secondBannerImageTop;
            thisParams.leftMargin = secondBannerImageLeft;
            secondImage.setLayoutParams(thisParams);

            Log.d("BANNER IMAGE", " Second Image width:" + secondBannerImageWidth + ";height:" + secondBannerImageHeight);

            if (secondImage.getParent() == null) ;
            thisBanner.addView(secondImage);

            secondImage.setScaleType(ScaleType.FIT_XY);

        }

        /**
         * 另外一個形式的SetBannerImage;
         *
         * @param thisImage
         * @param mainTitleText
         */
        public void setBannerImage(ImageView thisImage, String mainTitleText) {

            setBannerImage(thisImage);            // 使用基本的AddView
            setTitleArea();                                // 設定標題區域
            setMainTitle(mainTitleText);    // 設定文字
        }

        /**
         * 設定包含主標題副標題的Image
         *
         * @param thisImage
         * @param mainTitleText
         * @param subTitleText
         */
        public void setBannerImage(ImageView thisImage, String mainTitleText, String subTitleText) {

            setBannerImage(thisImage);            // 使用基本的AddView
            setTitleArea();                                // 設定標題區域
            setMainTitle(mainTitleText);    // 設定主標題
            setSubTitle(subTitleText);        // 設定副標題

        }

        /**
         * 設定主標題
         *
         * @param mainTitleText
         */
        public void setMainTitle(String mainTitleText) {

            if (mainTitle == null || mainTitle.getParent() == null) {

                setMainTitle();                                // 設定主標題

            }

            mainTitle.setText(mainTitleText);    // 設定文字

        }

        /**
         * 設定副標題
         *
         * @param setSubTitle
         */
        public void setSubTitle(String setSubTitle) {

            if (subTitle == null || subTitle.getParent() == null) {

                setSubTitle();                                // 設定主標題

            }

            subTitle.setText(setSubTitle);    // 設定文字
        }

        /**
         * 設定標題區域
         */
        private void setTitleArea() {

            if (hasTitle) {            // 如果已經設定好標題區域

                titleArea = new RelativeLayout(mainContext);
                LayoutParams thisParams = new LayoutParams(singleTitleAreaWidth, singleTitleAreaHeight);
                thisParams.leftMargin = singleTitleAreaLeft;
                thisParams.topMargin = singleTitleAreaTop;
                titleArea.setLayoutParams(thisParams);

                if (titleAreaBackground > -1) {
                    titleArea.setBackgroundResource(titleAreaBackground);
                } else {
                    titleArea.setBackgroundColor(singleTitleAreaBackgroundColor);
                }

                Log.d("BANNER", "完成標題區域的設定");

                thisBanner.addView(titleArea);
            }

        }

        /**
         * 設定主標題
         */
        private void setMainTitle() {

            if (titleArea == null) {

                setTitleArea();

            }

            mainTitle = new TextView(mainContext);            // 設定主要的標題
            LayoutParams thisParams = new LayoutParams(subMainTitleWidth, subMainTitleHeight);
            thisParams.leftMargin = subMainTitleLeft;
            thisParams.topMargin = subMainTitleTop;
            mainTitle.setLayoutParams(thisParams);
            mainTitle.setTextColor(subMainTitleFontColor);
            mainTitle.setTextSize(TypedValue.COMPLEX_UNIT_PX, subMainTitleFontSize);
            titleArea.addView(mainTitle);
        }

        /**
         * 設定主標題
         */
        private void setSubTitle() {

            subTitle = new TextView(mainContext);            // 設定主要的標題
            LayoutParams thisParams = new LayoutParams(subSecondTitleWidth, subSecondTitleHeight);
            thisParams.leftMargin = subSecondTitleLeft;
            thisParams.topMargin = subSecondTitleTop;
            subTitle.setLayoutParams(thisParams);
            subTitle.setTextColor(subSecondTitleFontColor);
            subTitle.setTextSize(TypedValue.COMPLEX_UNIT_PX, subSecondTitleFontSize);
            titleArea.addView(subTitle);

            //祐鑫增加
            subTitle.setMaxLines(1);
        }

        /**
         * 設定這個Banner的OnClickListener;
         *
         * @param thisListener 指定這個Banner要觸發的On Click Listener;
         */
        public void setImageOnClickListener(OnClickListener thisListener) {

            thisBanner.setOnClickListener(thisListener);

        }

        /***
         * 取得Banner中包含的ImageView
         *
         * @return ImageView  這個Banner主要的ImageView;
         */
        public ImageView getImageView() {

            return bannerImage;

        }

        /***
         * 取得Banner中包含的第二個ImageView
         *
         * @return ImageView  這個Banner次要的ImageView;
         */
        public ImageView getSecondImageView() {

            return secondImage;

        }

    }

    // 設定給ViewPager使用的Adapter;
    protected class BannerPagerAdapter extends PagerAdapter {

        private List<View> bannerListViews;

        BannerPagerAdapter(List thisList) {

            bannerListViews = thisList;

        }

        /**
         * 釋放資源
         * <p/>
         * 祐鑫增加
         */
        public void release() {
            bannerListViews.clear();
            notifyDataSetChanged();
            bannerListViews = null;
        }

        public void setList(List thisList) {
            bannerListViews = thisList;
            notifyDataSetChanged();
        }

        public void reset() {
            if (bannerListViews != null) {
                if (bannerListViews.size() > 0) {
                    for (int i = 0; i < bannerListViews.size(); i++) {
                        View getView = bannerListViews.get(i);

                        if (getView != null && getView instanceof BannerPager.Banner) {
                            ((BannerPager.Banner) getView).release();
                        }
                    }
                }

                bannerListViews.clear();
            }

            notifyDataSetChanged();
        }

        @Override
        public int getCount() {
            if (bannerListViews == null)
                return 0;
            return bannerListViews.size();
        }

        @Override
        public boolean isViewFromObject(View arg0, Object arg1) {
            //return false;
            return arg0 == (arg1);
        }

        @Override
        public void destroyItem(View container, int position, Object object) {

            if (position < bannerListViews.size()) {
                ((ViewPager) container).removeView((View) bannerListViews.get(position));
            }
        }

        @Override
        public Object instantiateItem(View container, int position) {
            //((ViewPager) container).addView((View) bannerListViews.get(position), 0);
            return bannerListViews.get(position);
        }

        @Override
        public void finishUpdate(ViewGroup container) {
            // TODO Auto-generated method stub
            super.finishUpdate(container);
        }

        @Override
        public void notifyDataSetChanged() {
            // TODO Auto-generated method stub
            super.notifyDataSetChanged();

        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            // TODO Auto-generated method stub
            ((ViewPager) container).addView((View) bannerListViews.get(position), 0);
            return super.instantiateItem(container, position);
        }

//		@Override
//		public int getItemPosition(Object object) {
//			// TODO Auto-generated method stub
//			 return POSITION_NONE;
//		}

        /**
         * 祐鑫修改版
         */
        @Override
        public int getItemPosition(Object object) {
            int index = bannerListViews.indexOf(object);
            if (index == -1)
                return POSITION_NONE;
            else
                return index;
        }

    }

    private boolean isAutoScroll = false;
    private long scrollInterval = 3000;
    private scrollHandler thisScrollHandler = null;
    private static final int SCROLL_WHAT = 1000;

    public void startAutoScroll() {

        isAutoScroll = true;
        thisScrollHandler = new scrollHandler();
        sendScrollMessage(scrollInterval);

    }

    /**
     * 停止翻頁
     * <p/>
     * 祐鑫增加
     */
    public void stopAutoScroll() {
        isAutoScroll = false;
        thisScrollHandler = null;
    }

    private void sendScrollMessage(long delayTimeInMills) {

        /** remove messages before, keeps one message is running at most **/

        if (thisScrollHandler != null) {
            thisScrollHandler.removeMessages(SCROLL_WHAT);

            thisScrollHandler.sendEmptyMessageDelayed(SCROLL_WHAT, delayTimeInMills);
        }
    }

    /***
     * 執行自動換頁的功能;
     *
     * @author louischuang
     */
    private class scrollHandler extends Handler {

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);

            switch (msg.what) {
                case SCROLL_WHAT:

                    if (thisObj != null) {
                        thisObj.scrollOnce();

                        if (isAutoScroll) {
                            sendScrollMessage(scrollInterval);
                        }
                    }

                    break;
            }
        }
    }

    /**
     * 快速地翻動頁面
     */
    public void scrollOnce() {

        if (currentPageNo < pageNum - 1) {

            thisObj.setCurrentItem(currentPageNo + 1, true);

        } else {

            thisObj.setCurrentItem(0, true);

        }

        //thisObj.scrollBy(objectWidth, 0);

    }


}
