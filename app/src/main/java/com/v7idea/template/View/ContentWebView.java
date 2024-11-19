package com.v7idea.template.View;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.DownloadManager;
import android.app.DownloadManager.Query;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.util.Log;
import android.view.KeyEvent;
import android.view.ViewParent;
import android.webkit.JsResult;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.v7idea.template.Tool.ContentJavascriptInterface;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;


/**
 * 這個ContentWebView是用來繼承一般的WebView, 並且進一步提供換頁相關功能
 *
 * @author louischuang
 */
@SuppressLint("SetJavaScriptEnabled")
public class ContentWebView extends WebView {

    ContentWebView thisView = null;        // 物件本身;

    private int maxChangeParentScroll = 0;                                // 最大移動的高度
    // 用來儲存要往上移動的高度
    private int currentParentScroll = 0;                                        // 目前母ScrollView已經移動的高度

    private boolean ifChangeParentScroll = false;                        // 是否啟動變更母ScrollView;

    private int currentTop = 0;                                                    // 目前的高度位置
    private int currentLeft = 0;                                                    // 目前的左邊偏移位置

    private boolean isSwingUp = false;                                        // 是否往回翻;
    private boolean isChangeParentState = false;                        //	是否已經改變母Scroll的狀態

    private ContentJavascriptInterface thisInterface = null;            // 作為與網頁串接的Javascript Interface;

    private String contentString = "";                                        // 內容字串
    private String contentString2 = null;                                        // 內容字串2
    private String contentString3 = null;                                        // 內容字串3

    private String eventDateValue = null;                                    // 日期數值
    private String eventTimeValue = null;                                    // 時間數值
    private String eventPlaceValue = null;                                    // 地點數值
    private String eventPeopleValue = null;                                // 人數數值
    private String eventHostValue = null;                                    // 主辦單位數值
    private String eventCostValue = null;                                    // 費用數值
    private String eventPhoneValue = null;                                    // 電話數值

    private Intent dateIntent = null;                            // 存放日期的Internet
    private Intent placeIntent = null;                            // 存放地方的Intent
    private Intent peopleIntent = null;                        // 存放日期的Intent
    private Intent hostIntent = null;                            // 存放主辦單位的Intent
    private Intent costIntent = null;                            // 存放成本
    private Intent phoneIntent = null;                            // 存放電話

    private int selfOriginigalWidth = 0;                        // 本身原始的寬度;
    private int selfOriginigalHeight = 0;                        // 本身原始的高度;
    private int selfChangedWidth = 0;                        // 本身原始的寬度;
    private int selfChangedHeight = 0;                        // 本身原始的高度;

    private ViewParent thisParent = null;
    private int parentType = 0;                                    // 表示沒有找到Parent View的形態
    // 1: RelativeLayout
    // 2. LinearLayout;

    private ArrayList<RelativeFile> downloadList = null;

    private HashMap<String, Intent> fileList = null;    // 用來存放webView放進來的Download檔案

//    private PDFView thisPDFView = null;

    public int marginTop = -1;

    @SuppressWarnings("static-access")
    public ContentWebView(Context context) {
        super(context);
    }

    public ContentWebView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public ContentWebView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public String TargetFestival = null;

    public void scrollTopSelected(String strFestival) {
        if (strFestival != null && !strFestival.isEmpty()) {
            thisView.loadUrl("javascript:scrollToSelectedClass('" + strFestival + "');");
        }
    }

    public void init() {
        thisView = this;
        thisView.clearCache(true);
        thisView.clearHistory();

        // 設定Interface;
        thisInterface = new ContentJavascriptInterface(getContext(), thisView);
        thisView.addJavascriptInterface(thisInterface, "android");

        Log.d("CONTENTWEBVIEW", "Completed added a java interface!");

        // 設定一個新的Handler;
        // 表示有抓到Parent物件; 那就需要去找到這個物件的Type.
        parentType = getParentType(thisView.getParent());

        // Initialize web view
        WebSettings webViewSettings = thisView.getSettings();
        webViewSettings.setJavaScriptEnabled(true);
        webViewSettings.setDomStorageEnabled(true);
        webViewSettings.setAllowContentAccess(true);
        webViewSettings.setSupportZoom(false);
        webViewSettings.setLoadWithOverviewMode(true);
        webViewSettings.setUseWideViewPort(true);
        webViewSettings.setLayoutAlgorithm(WebSettings.LayoutAlgorithm.SINGLE_COLUMN);//如果網頁用要Wrap_content這邊要設

//        if(android.os.Build.VERSION.SDK_INT > 18) {
//
//        		thisView.setWebContentsDebuggingEnabled(true);
//
//        }

        thisView.setClickable(true);
        thisView.setAnimationCacheEnabled(true);


        downloadList = new ArrayList<RelativeFile>();

        // Override functions so that javascript alert() and confirm() can work
        thisView.setWebChromeClient(new WebChromeClient() {

            @Override
            public boolean onJsAlert(WebView view, String url, String message,
                                     final JsResult result) {
                new AlertDialog.Builder(getContext())
                        .setMessage(message)
                        .setPositiveButton("確定", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                result.confirm();
                            }
                        })
                        .show();

                return true;
            }

            @Override
            public boolean onJsConfirm(WebView view, String url,
                                       String message, final JsResult result) {
                new AlertDialog.Builder(getContext())
                        .setMessage(message)
                        .setPositiveButton("確定", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                result.confirm();
                            }
                        })
                        .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                result.cancel();
                            }
                        })
                        .show();

                return true;
            }
        });


        // Initialize a webview client
        thisView.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {

                /**
                 * 原本只有 view.loadUrl(url); 這行，為什麼加上這行忘掉了。
                 * 現在改是為了把顯示內容的網與外部連結區分開來，如果只有一行 view.loadUrl(url);
                 * 在會不斷的重載網頁（會一直看到 onPageFinished 這個LOG ！！）
                 */
                if(url != null && url.contentEquals("file:///android_asset/content.html"))
                {
                    view.loadUrl(url);
                }
                else
                {
                    Intent i = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                    getContext().startActivity(i);
                }

                return true;

            }

            @Override
            public void onPageFinished(WebView view, String url) {

                super.onPageFinished(view, url);

                Log.e("CONTENTWEBVIEW", "onPageFinished");
                thisView.loadUrl("javascript:android.resize(document.body.getBoundingClientRect().height)");
//                Log.e("CONTENTWEBVIEW", "url："+url);

//				isOnPageFinished = true;

                // thisView.loadUrl("javascript:alert('this webView is on Page Finished!');");

                thisView.loadUrl("javascript:onloadProcess();");
                scrollTopSelected(TargetFestival);

//				 thisView.loadUrl("javascript:onloadProcess();");

//                Air thisApp = (Air) getContext().getApplicationContext();
//
//                int miniHeight = (int )(1280 * thisApp.getScaleValue());
////
//                int currentHeight = getScrollRange();
//
//                Log.e("CONTENTWEBVIEW", "currentHeight: " + currentHeight);
//
//                if(currentHeight < miniHeight)
//                {
//                    thisApp.scaleLayout(thisView, ViewGroup.LayoutParams.MATCH_PARENT
//                            , miniHeight, (int) (20 * thisApp.getScaleValue()), 0);
//                }

                if (whenPageLoadedFinish != null) {
                    whenPageLoadedFinish.WhenPageLoadedFinish();
                }
            }


            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {

                super.onPageStarted(view, url, favicon);

            }

        });
    }

//    @Override
//    protected void onLayout(boolean changed, int l, int t, int r, int b) {
//        super.onLayout(changed, l, t, r, b);
//
//        Log.e("CONTENTWEBVIEW", "onLayout Height: " + (b-t));
//
//        int currentHeight = getScrollRange();
//
//        Log.e("CONTENTWEBVIEW", "onLayout currentHeight: " + currentHeight);
//    }

    public int getScrollRange()
    {
        return super.computeVerticalScrollRange(); //working after load of page
    }

    private WhenPageLoadedFinish whenPageLoadedFinish = null;

    public void setWhenPageLoadedFinish(WhenPageLoadedFinish whenPageLoadedFinish) {
        this.whenPageLoadedFinish = whenPageLoadedFinish;
    }

    public interface WhenPageLoadedFinish {
        void WhenPageLoadedFinish();
    }

    public void setWebMarginTop(int intMarginTop) {
        thisInterface.setMarginTop(intMarginTop);
    }

    public void setWebMarginBottom(int intMarginBottom) {
        thisInterface.setMarginBottom(intMarginBottom);
    }

    public static final int MEG_GETFEEDBACK = 5675;                // 設定html中回傳的值

    /*
     * 外部自動取得要返回JAVASCRIPT呼叫的ID值
     */
    public int getFeedBackHandlerId() {

        return MEG_GETFEEDBACK;

    }

    /**
     * 設定是否要滑動
     *
     * @param changeStatus true: Yes; false:No
     */
    public void setChangeParentScroll(boolean changeStatus) {

        ifChangeParentScroll = changeStatus;

    }

    /**
     * 取得在這個ContentView所取得的Handler;
     */
    public Handler getHandler() {

        return upDateStatushandler;

    }

	/*
	 *  這個Handler主要是用來處理其他Thread對於本Activity的呼叫
	 * 
	 * 
	 */

    public Handler upDateStatushandler = new Handler() {

        // 在執行這個Handle，必須要用handleMessage來觸發;
        // 傳遞的資料為Bundle;
        // 基本上傳遞的參數(key)有：
        // 1. type: 回傳的類型;
        // 2. value: 回傳的值;
        // 3. 其他的key值就依照實際的需要來取得

        public void handleMessage(Message msg) {
            switch (msg.what) {

                // 處理來自於Javascript的回覆值

                case MEG_GETFEEDBACK:

                    String status = "";
                    String value = "";

                    Bundle thisData = msg.getData();

                    if (thisData.containsKey("status")) {
                        status = thisData.getString("status");
                    }

                    if (thisData.containsKey("value")) {
                        value = thisData.getString("value");
                    }

                    // Toast.makeText(MeetingRoom.this, "收到Handler的訊息:" + "status:" + status + "value:" + value , Toast.LENGTH_LONG).show();
                    processJavascriptFeedBack(status, value);

                    break;

            }
            super.handleMessage(msg);
        }

    };

    private int OldScrollY = 0;
//    private BottomMenuLayout ThisBottomMenuLayout = null;
//
//    public void setBottomMenuLayout(BottomMenuLayout ThisBottomMenuLayout) {
//        this.ThisBottomMenuLayout = ThisBottomMenuLayout;
//    }

    /**
     * 用來偵測與移動母View
     */
    protected void onScrollChanged(int left, int top, int oldLeft, int oldTop) {
        super.onScrollChanged(left, top, oldLeft, oldTop);

        Log.e("Scroll is Changed!", "top:" + top + ";left:" + left + "  oldTop: " + oldTop);

        if (oldTop > top) {

            isSwingUp = true;

        } else {

            isSwingUp = false;

        }

        currentTop = top;
        currentLeft = left;
        currentScrollValue = currentTop;

        if (top > oldTop) {
            OldScrollY = +(top - oldTop);

            if (OldScrollY >= 10) {
//                if (ThisBottomMenuLayout != null) ThisBottomMenuLayout.scrollBottomMenuDown();

                OldScrollY = 0;
            }
        } else if (top < oldTop) {
            OldScrollY = +(oldTop - top);

            if (OldScrollY >= 10) {
//                if (ThisBottomMenuLayout != null) ThisBottomMenuLayout.scrollBottomMenuUp();

                OldScrollY = 0;
            }
        }

        if (isSetTarget && targetLayout != null) {            // 如果有設定連動物件，那就必須要進行聯動

            // 設定連動的動作

            RelativeLayout.LayoutParams thisParam = (RelativeLayout.LayoutParams) targetLayout.getLayoutParams();


            if (currentTop <= targetMaxScrollHeight) {

                Log.d("TARGET_CHANGED", "newValue:" + (originalTargetTop - currentTop));


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

    }

    public void processJavascriptFeedBack(String status, String value) {

        Log.d("WebView", "get JavascriptFeedBack, status:" + status + "; value:" + value);

        if (status.contentEquals("processJavascriptFeedBack")) {    // 舊命令更新的相關機制

            // 表示正確的取得來自于HTML中的ScrollY資料;

//            Log.d("processJavascriptFeedBack", "processJavascriptFeedBack:" + value);
            onScrollChanged(0, Integer.parseInt(value), 0, this.currentTop);

        } else if (status.contentEquals("CLICK_DATE")) {

            if (dateIntent != null) {

                getContext().startActivity(dateIntent);

            }


        } else if (status.contentEquals("CLICK_PLACE")) {

            if (placeIntent != null) {

                getContext().startActivity(placeIntent);

            }


        } else if (status.contentEquals("CLICK_PHONE")) {

            if (phoneIntent != null) {

                getContext().startActivity(phoneIntent);

            }

        } else if (status.contentEquals("CLICK_PEOPLE")) {

            if (peopleIntent != null) {

                getContext().startActivity(peopleIntent);

            }

        } else if (status.contentEquals("CLICK_HOST")) {

            if (hostIntent != null) {

                getContext().startActivity(hostIntent);

            }

        } else if (status.contentEquals("LIST_ALL_FILE")) {        // 列出所有可供下載的檔案

            listAllDownloadFiles();

        } else if (status.contentEquals("DOWNLOAD_FILE")) {

            // 來自Javascript 下載檔案的呼叫
//            Air thisApp = null;
//
//            if (getContext() != null && value != null) {
//
//                thisApp = (Air) getContext().getApplicationContext();
////		    	   		 	thisApp.downLoadOrOpenFile(thisContext, value);
//
//                Log.d("DOWNLOAD_FILE", "下載檔案的流程開始");
//
//            } else {
//
//                Log.d("DOWNLOAD_FILE", "因為Context is Null 或是 沒有下載的檔案URL, 所以無法開始下載");
//
//            }

        }

    }


    /**
     * 載入content的內容;
     */
    public void setContent(String thisString) {

        Log.d("CONTENTWEBVIEW", "thisContent:" + thisString);

//    	   		if(android.os.Build.VERSION.SDK_INT > 18) {
//					
//					// thisView.evaluateJavascript("onloadProcess();", null);
//					thisView.evaluateJavascript("addTextToContent('" + thisString + "');", null);
//					
//			} else {
//					
//					thisView.loadUrl("javascript:addTextToContent('" + thisString + "');");
//					
//			}


    }

//       /**
//        * 載入content的內容;
//        * @param thisContent 要設定網頁的資料
//        */
//       public void setContent(String thisString, String thisString2, String thisString3) {
//    	   
//    	   		Log.d("CONTENTWEBVIEW","thisContent:" + thisString);
//    	   
//    	   		thisView.loadUrl("javascript:addTextToContent('" + thisString + "');");
//    	   		thisView.loadUrl("javascript:addTextToContent2('" + thisString2 + "');");
//    	   		thisView.loadUrl("javascript:addTextToContent3('" + thisString3+ "');");
//    	   		
//       }  

    /**
     * 更換網頁內容;
     *
     * @param thisString
     */
    public void replaceContent(String thisString) {

        // Log.d("CONTENTWEBVIEW","thisContent:" + thisString);
        setContentString(thisString);
        thisView.loadUrl("javascript:addTextToContent('"+thisString+"')");
        // thisView.loadUrl("javascript:alert('check!!');");
        // thisView.loadUrl("javascript:onloadProcess();");

//    	   		if(android.os.Build.VERSION.SDK_INT > 18) {
//					
//    	   			thisView.evaluateJavascript("onloadProcess();", null);
//					
//			} else {
//					
//				thisView.loadUrl("javascript:onloadProcess();");
//					
//			}


    }

    /**
     * 更換網頁內容;
     *
     * @param thisString
     */
    public void replaceContent(String thisString, String thisString2, String thisString3) {

        // Log.d("CONTENTWEBVIEW","thisContent:" + thisString);
        setContentString(thisString, thisString2, thisString3);
        // thisView.loadUrl("javascript:alert('check!!');");
        // thisView.loadUrl("javascript:onloadProcess();");
//    	   		if(android.os.Build.VERSION.SDK_INT > 18) {
//					
//    	   			thisView.evaluateJavascript("onloadProcess();", null);
//					
//			} else {
//					
//				thisView.loadUrl("javascript:onloadProcess();");
//					
//			}

    }


    /**
     * 設定主要的Content;
     *
     * @param thisString
     */
    public void setContentString(String thisString) {

        contentString = thisString;

    }

    /**
     * 設定主要的Content;
     *
     * @param thisString
     */
    public void setContentString(String thisString, String thisString2, String thisString3) {

        contentString = thisString;
        contentString2 = thisString2;
        contentString3 = thisString3;

    }

    /**
     * 取得Content String
     *
     * @return
     */
    public String getContentString() {

        // Toast.makeText(thisContext, "啟動getContentString();", Toast.LENGTH_LONG ).show();
        return contentString;

    }

    /**
     * 取得Content String2
     *
     * @return
     */
    public String getContentString2() {

        return contentString2;

    }

    /**
     * 取得Content String3
     *
     * @return
     */
    public String getContentString3() {

        return contentString3;

    }

    /**
     * @return the eventDateValue
     */
    public String getEventDateValue() {
        return eventDateValue;
    }

    /**
     * @param eventDateValue the eventDateValue to set
     */
    public void setEventDateValue(String eventDateValue, Intent thisIntent) {

        this.eventDateValue = eventDateValue;
        dateIntent = thisIntent;

//		if(android.os.Build.VERSION.SDK_INT > 18) {
//			
//   			thisView.evaluateJavascript("setDateValue('" + eventDateValue +  "');", null);
//			
//		} else {
//				
//			thisView.loadUrl("javascript:setDateValue('" + eventDateValue +  "');");
//				
//		}


    }


    /**
     * @return the eventTimeValue
     */
    public String getEventTimeValue() {
        return eventTimeValue;

    }

    /**
     * @param eventTimeValue the eventTimeValue to set
     */
    public void setEventTimeValue(String eventTimeValue, Intent thisIntent) {
        this.eventTimeValue = eventTimeValue;

//		if(android.os.Build.VERSION.SDK_INT > 18) {
//			
//   			thisView.evaluateJavascript("setTimeValue('" + eventTimeValue +  "');", null);
//			
//		} else {
//				
//			thisView.loadUrl("javascript:setTimeValue('" + eventTimeValue +  "');");
//				
//		}


    }

    /**
     * @return the eventPlaceValue
     */
    public String getEventPlaceValue() {
        return eventPlaceValue;
    }

    /**
     * @param eventPlaceValue the eventPlaceValue to set
     */
    public void setEventPlaceValue(String eventPlaceValue, Intent thisIntent) {
        this.eventPlaceValue = eventPlaceValue;
        placeIntent = thisIntent;


//		if(android.os.Build.VERSION.SDK_INT > 18) {
//			
//   			thisView.evaluateJavascript("setPlaceValue('" + eventPlaceValue +  "');", null);
//			
//		} else {
//				
//			thisView.loadUrl("javascript:setPlaceValue('" + eventPlaceValue +  "');");
//				
//		}

    }

    /**
     * @return the eventPeopleValue
     */
    public String getEventPeopleValue() {
        return eventPeopleValue;
    }

    /**
     * @param eventPeopleValue the eventPeopleValue to set
     */
    public void setEventPeopleValue(String eventPeopleValue, Intent thisIntent) {
        this.eventPeopleValue = eventPeopleValue;
        peopleIntent = thisIntent;


//		if(android.os.Build.VERSION.SDK_INT > 18) {
//			
//   			thisView.evaluateJavascript("setPeopleValue('" + eventPeopleValue +  "');", null);
//			
//		} else {
//				
//			thisView.loadUrl("javascript:setPeopleValue('" + eventPeopleValue +  "');");
//				
//		}

    }

    /**
     * @return the eventHostValue
     */
    public String getEventHostValue() {
        return eventHostValue;
    }

    /**
     * @param eventHostValue the eventHostValue to set
     */
    public void setEventHostValue(String eventHostValue, Intent thisIntent) {
        this.eventHostValue = eventHostValue;
        hostIntent = thisIntent;


//		if(android.os.Build.VERSION.SDK_INT > 18) {
//			
//   			thisView.evaluateJavascript("setHostValue('" + eventHostValue +  "');", null);
//			
//		} else {
//				
//			thisView.loadUrl("javascript:setHostValue('" + eventHostValue +  "');");
//				
//		}

    }

    /**
     * @return the eventCostValue
     */
    public String getEventCostValue() {
        return eventCostValue;
    }

    /**
     * @param eventCostValue the eventCostValue to set
     */
    public void setEventCostValue(String eventCostValue, Intent thisIntent) {
        this.eventCostValue = eventCostValue;
        costIntent = thisIntent;


//		if(android.os.Build.VERSION.SDK_INT > 18) {
//			
//   			thisView.evaluateJavascript("setCostValue('" + eventCostValue +  "');", null);
//			
//		} else {
//				
//			thisView.loadUrl("javascript:setCostValue('" + eventCostValue +  "');");
//				
//		}

    }

    /**
     * @return the eventPhoneValue
     */
    public String getEventPhoneValue() {
        return eventPhoneValue;
    }

    /**
     * @param eventPhoneValue the eventPhoneValue to set
     */
    public void setEventPhoneValue(String eventPhoneValue, Intent thisIntent) {
        this.eventPhoneValue = eventPhoneValue;
        phoneIntent = thisIntent;


//		if(android.os.Build.VERSION.SDK_INT > 18) {
//			
//   			thisView.evaluateJavascript("setPhoneValue('" + eventPhoneValue +  "');", null);
//			
//		} else {
//				
//			thisView.loadUrl("javascript:setPhoneValue('" + eventPhoneValue +  "');");
//				
//		}


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

    /**
     * 增加下載檔案的品項
     *
     * @param fileName  檔案名稱
     * @param ImagePath 圖項路徑
     * @param fileDesc  檔案說明
     * @param fileURL   下載路徑
     */
    public void addFileItem(String fileName, String ImagePath, String fileDesc, String fileURL) {

        // Toast.makeText(thisContext, "fileName:" + fileName, Toast.LENGTH_LONG).show();

        Log.e("addFileItem", "fileURL: " + fileURL);

        RelativeFile thisFile = new RelativeFile(thisView, ImagePath, fileURL, fileName);
        downloadList.add(thisFile);

        // 目前沒有 檔案下載的相關說明，未來可以加上去
        //thisView.loadUrl("javascript:addNewFile('" + fileName +  "','" + ImagePath +  "', '" + fileURL +  "' );");

//		if(android.os.Build.VERSION.SDK_INT > 18) {
//			
//   			thisView.evaluateJavascript("addNewFile('" + fileName +  "','" + ImagePath +  "', '" + fileURL +  "' );", null);
//			
//		} else {
//				
//			thisView.loadUrl("javascript:addNewFile('" + fileName +  "','" + ImagePath +  "', '" + fileURL +  "' );");
//				
//		}

        Log.e("addFileItem", "Add File Item Complete!");

    }

    public void listAllDownloadFiles() {

        if (downloadList != null && downloadList.size() > 0) {

            int i = 0;

            for (i = 0; i < downloadList.size(); i++) {

                RelativeFile thisFile = downloadList.get(i);
                String thisFileIcon = "";
                String thisFileTitle = "";
                String thisFileDownloadLink = "";

                thisFileIcon = thisFile.getIconPath();
                thisFileTitle = thisFile.getDownloadTitle();
                thisFileDownloadLink = thisFile.getDownloadUrl();

                if (android.os.Build.VERSION.SDK_INT > 18) {

                    thisView.evaluateJavascript("addNewFile('" + thisFileTitle + "','" + thisFileIcon + "', '" + thisFileDownloadLink + "' );", null);

                } else {

                    thisView.loadUrl("javascript:addNewFile('" + thisFileTitle + "','" + thisFileIcon + "', '" + thisFileDownloadLink + "' );");

                }

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

    //以下祐鑫增加

    /**
     * 當下卷動的數值
     */
    private int currentScrollValue = 0;

    public int getCurrentScrollValue() {

        if (currentScrollValue > targetMaxScrollHeight) {
            currentScrollValue = targetMaxScrollHeight;
        } else if (currentScrollValue < 0) {
            currentScrollValue = 0;
        }

        return currentScrollValue;
    }

    /**
     * 在系統將元件"畫"出來之前的階段註冊BroadcastReceiver
     */
    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();

        Log.e("ContentWebview", "onAttachedToWindow()");
        IntentFilter filter = new IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE);
        getContext().registerReceiver(listenDownLoad, filter);
    }

    /**
     * 本文,或Activity結束的階段取消BroadcastReceiver
     */
    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();

        Log.e("ContentWebview", "onDetachedFromWindow()");

        getContext().unregisterReceiver(listenDownLoad);
    }

    /**
     * 下載完的通知監聽器
     */
    private BroadcastReceiver listenDownLoad = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            DownloadManager dm = (DownloadManager) getContext().getSystemService(getContext().DOWNLOAD_SERVICE);

//			long completeDownloadId = intent.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1);
            String action = intent.getAction();
            if (DownloadManager.ACTION_DOWNLOAD_COMPLETE.equals(action)) {

                long downloadId = intent.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, 0);
                // 查询
                Query query = new Query();
                query.setFilterById(downloadId);
                Cursor c = dm.query(query);
                if (c.moveToFirst()) {
                    int columnIndex = c
                            .getColumnIndex(DownloadManager.COLUMN_STATUS);
                    if (DownloadManager.STATUS_SUCCESSFUL == c.getInt(columnIndex)) {
                        String uriString = c.getString(c.getColumnIndex(DownloadManager.COLUMN_LOCAL_FILENAME));

                        String[] getFileName = uriString.split("/");

                        //removeDownload(downloadId);
//	                    Toast.makeText(thisContext,getFileName[getFileName.length-1]+"下載完成!!", 0).show();
                        // Uri.parse(uriString);

                        Log.e("BroadcastReceiver listenDownLoad", "uriString: " + uriString);

                        //下載完成後的是否開啟檔案
                        File downLoadFile = new File(uriString);

                        if (downLoadFile.exists()) {

                            // Louis: 2015/2/25 Add New Feature;


//	                    		if(thisPDFView != null) {
//	                    			
//	                    			thisPDFView.fromFile(downLoadFile)
//	            		             .defaultPage(1)
//	            		             .load();
//	                    			
//	                    			 Log.e("PDFView Ready","PDFView Ready");
//	                    			 
//	                    			 thisPDFView.setVisibility(View.VISIBLE);
//		                    			
//	                    			
//	                    		} else  {
//	                    			
//	                    			 Log.e("No thisPDFView","No thisPDFView");
//	                    			
//	                    		}

//	                    	 	Air thisApp = (Air) context.getApplicationContext();
//	                    	 	thisApp.ifOnpenDownLoadFile(context, downLoadFile);

                        }
                    }
                }

                c.close();
            }
        }
    };

    /**
     * 有關於下載檔案的URL;
     *
     * @author louis
     */
    private class RelativeFile {

        private ContentWebView thisView;
        private String iconPath = "";
        private String downloadUrl = "";
        private String downloadTitle = "";

        /**
         * 建構子
         *
         * @param view     這個物件所在的View
         * @param thisPath 這個下載物件的圖黨路徑
         * @param thisLink 這個下載物件的聯結
         * @param title    這個下載物件的標題
         */
        public RelativeFile(ContentWebView view, String thisPath, String thisLink, String title) {

            thisView = view;
            iconPath = thisPath;
            downloadUrl = thisLink;
            downloadTitle = title;

            // Toast.makeText(thisContext, "downloadTitle:" + downloadTitle, Toast.LENGTH_LONG).show();

        }

        /**
         * 取得下載圖檔的位置
         *
         * @return
         */
        public String getIconPath() {

            return iconPath;

        }

        /**
         * 取得下載標題
         *
         * @return String 下載標題
         */
        public String getDownloadTitle() {

            return downloadTitle;

        }

        /**
         * 取得下載檔案路徑
         *
         * @return String 下載檔案路徑
         */
        public String getDownloadUrl() {

            return downloadUrl;

        }

    }

    /**
     * 清除下載清單;
     */
    public void clearAllDownloadFile() {

        if (downloadList != null && downloadList.size() > 0) {

            downloadList.clear();

        }

    }

    /* (non-Javadoc)
     * @see android.webkit.WebView#onKeyDown(int, android.view.KeyEvent)
     */
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {


        return super.onKeyDown(keyCode, event);

    }

    /**
     * 取得目前的PDFView
     *
     * @return the thisPDFView
     */
//    public PDFView getThisPDFView() {
//        return thisPDFView;
//    }

    /**
     * 設定目前的PDFView
     *
     * @param thisPDFView the thisPDFView to set
     */
//    public void setThisPDFView(PDFView thisPDFView) {
//        this.thisPDFView = thisPDFView;
//    }


}
