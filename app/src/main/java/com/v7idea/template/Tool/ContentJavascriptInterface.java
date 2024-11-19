package com.v7idea.template.Tool;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.os.Message;
import android.util.Log;
import android.view.ViewGroup;
import android.webkit.JavascriptInterface;

import com.v7idea.template.AirApplication;
import com.v7idea.template.View.ContentWebView;


/**
 * 這個APP主要用在設計每個功能明細的內容HTML頁面中處理有關於Javascript回傳的資料。
 *
 * @author louischuang
 */

public class ContentJavascriptInterface {

    public Context thisContext;                                                    // 主要的容器
    private AirApplication thisApp;                                                // 已被開啟的APP Class
    private ContentWebView thisWebView;                                                // 主要使用這個JavaScriptInterface的WebVIew;
    private int ActivityType = 0;                                                // 被呼叫的Activity類型

    public ContentJavascriptInterface(Context context, ContentWebView view) {

        thisContext = context;
        // 呼叫app全域環境
        thisApp = (AirApplication) thisContext.getApplicationContext();
        thisWebView = view;

    }

    /**
     * 從系統回傳上下移動的數值;
     * Injects the supplied Java object into this WebView. The object is injected into the JavaScript context
     * of the main frame, using the supplied name. This allows the Java object's methods to be accessed
     * from JavaScript. For applications targeted to API level JELLY_BEAN_MR1 and above, only public
     * methods that are annotated with JavascriptInterface can be accessed from JavaScript.
     * For applications targeted to API level JELLY_BEAN or below, all public methods (including the
     * inherited ones) can be accessed, see the important security note below for implications.
     * Note that injected objects will not appear in JavaScript until the page is next (re)loaded. For example:
     * <p/>
     * ex. class JsObject {
     *
     * @param scrollValue
     * @JavascriptInterface public String toString() { return "injectedObject"; } }
     */
    @JavascriptInterface
    public void scrollProcess(String scrollValue) {

        Bundle openProgressBundle = simpleBundle("SCROLL", scrollValue);
        sendContentData(openProgressBundle);

        Log.d("HTML", "scroll value:" + scrollValue);

    }

    private int marginTop = -1;
    private int marginBottom = -1;

    public void setMarginTop(int marginTop) {
        this.marginTop = marginTop;
    }

    public void setMarginBottom(int marginBottom) {
        this.marginBottom = marginBottom;
    }

    @JavascriptInterface
    public int getMarginTop() {
        return marginTop;
    }

    @JavascriptInterface
    public int getMarginBottom() {
        return marginBottom;
    }

    /**
     * 發送來自html中的資料，並且傳給ContentWebView的Handler處理
     *
     * @param dataBundle 資料Bundle
     * @return true: 成功,false：失敗
     */
    public boolean sendContentData(Bundle dataBundle) {

        boolean result = true;

        if (dataBundle != null) {

            int thisHandlerId = thisWebView.getFeedBackHandlerId();
            Message thisMessage = new Message();

            thisMessage.what = thisHandlerId;
            thisMessage.setData(dataBundle);
            thisWebView.upDateStatushandler.sendMessage(thisMessage);


        }

        return result;

    }

    // 快速設定一個簡單的Bundle;
    public Bundle simpleBundle(String status, String value) {

        Bundle thisBundle = new Bundle();
        thisBundle.putString("status", status);
        thisBundle.putString("value", value);

        return thisBundle;

    }

    /**
     * 取得要顯示的內容;
     *
     * @return 字串
     */
    @JavascriptInterface
    public String getContentString() {

        return thisWebView.getContentString();

    }

    /**
     * 取得要顯示的內容;
     *
     * @return 字串
     */
    @JavascriptInterface
    public String getContentString2() {

        return thisWebView.getContentString2();

    }

    /**
     * 取得要顯示的內容;
     *
     * @return 字串
     */
    @JavascriptInterface
    public String getContentString3() {

        return thisWebView.getContentString3();

    }

    /**
     * 處理有關於按下日期後的動作
     */
    @JavascriptInterface
    public void fieldDateProcess() {

        Bundle openProgressBundle = simpleBundle("CLICK_DATE", null);
        sendContentData(openProgressBundle);

    }

    /**
     * 處理有關於按下地點後的動作
     */
    @JavascriptInterface
    public void fieldPlaceProcess() {

        Bundle openProgressBundle = simpleBundle("CLICK_PLACE", null);
        sendContentData(openProgressBundle);

    }

    /**
     * 處理有關於按下人數後的動作
     */
    @JavascriptInterface
    public void fieldPeopleProcess() {

        Bundle openProgressBundle = simpleBundle("CLICK_PEOPLE", null);
        sendContentData(openProgressBundle);

    }

    /**
     * 處理有關於按下主辦單位後的動作
     */
    @JavascriptInterface
    public void fieldHostProcess() {

        Bundle openProgressBundle = simpleBundle("CLICK_Host", null);
        sendContentData(openProgressBundle);

    }

    /**
     * 處理有關於按下費用後的動作
     */
    @JavascriptInterface
    public void fieldCostProcess() {

        Bundle openProgressBundle = simpleBundle("CLICK_COST", null);
        sendContentData(openProgressBundle);

    }

    /**
     * 處理有關於按下電話後的動作
     */
    @JavascriptInterface
    public void fieldPhoneProcess() {

        Bundle openProgressBundle = simpleBundle("CLICK_PHONE", null);
        sendContentData(openProgressBundle);

    }

    /**
     * 下載檔案的相關處理
     */
    @JavascriptInterface
    public void downloadFile(String fileURL) {

        Bundle openProgressBundle = simpleBundle("DOWNLOAD_FILE", fileURL);
        sendContentData(openProgressBundle);

    }

    /**
     * 取得活動日期的字串
     *
     * @return String 活動日期
     */
    @JavascriptInterface
    public String getEventDateValue() {

        return thisWebView.getEventDateValue();

    }

    /**
     * 取得活動費用說明
     *
     * @return String 費用說明
     */
    @JavascriptInterface
    public String getEventCostValue() {

        return thisWebView.getEventCostValue();

    }

    /**
     * 取得主辦單位
     *
     * @return String 主辦單位
     */
    @JavascriptInterface
    public String getEventHostValue() {

        return thisWebView.getEventHostValue();

    }

    /**
     * 取得活動人數
     *
     * @return String 主活動人數單位
     */
    @JavascriptInterface
    public String getEventPeopleValue() {

        return thisWebView.getEventPeopleValue();

    }

    /**
     * 取得聯絡電話
     *
     * @return String 聯絡電話
     */
    @JavascriptInterface
    public String getEventPhoneValue() {

        return thisWebView.getEventPhoneValue();

    }

    /**
     * 取得舉辦活動地點
     *
     * @return String 舉辦活動地點
     */
    @JavascriptInterface
    public String getEventPlaceValue() {

        return thisWebView.getEventPlaceValue();

    }

    /**
     * 取得舉辦時間
     *
     * @return String 舉辦活動時間
     */
    @JavascriptInterface
    public String getEventTimeValue() {

        return thisWebView.getEventTimeValue();

    }

    /**
     * 設定所有下載的資料
     *
     * @return
     */
    @JavascriptInterface
    public boolean listAllDownloadFiles() {

        Bundle openProgressBundle = simpleBundle("LIST_ALL_FILE", null);
        sendContentData(openProgressBundle);

        // thisWebView.listAllDownloadFiles();
        return true;

    }

    @JavascriptInterface
    public void resize(final float height) {
        int contentHeight = (int) (height * thisContext.getResources().getDisplayMetrics().density);

//        Log.e("CONTENTWEBVIEW", "content height: "+contentHeight);

        final int miniHeight = (int )(1280 * ViewScaling.getScaleValue());

        if(contentHeight < miniHeight)
        {
            if(thisContext != null && ((Activity)thisContext).isFinishing() == false)
            {
                ((Activity)thisContext).runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        ViewScaling.setScaleForLinearLayout(thisWebView, ViewGroup.LayoutParams.MATCH_PARENT
                                , miniHeight, (int) (20 * ViewScaling.getScaleValue()), 0);
                    }
                });
            }
        }
    }

}
