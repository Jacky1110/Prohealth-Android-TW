package com.v7idea.healthkit.Model;

/**
 * Created by mortal on 15/3/12.
 */
public class APIFetch {
    private static final String TAG = "APIFetch";
    //正式站
    private final static String serverDomainName = "https://api.lohasgen.com";
    //測式站
 //   private final static String serverDomainName = "http://api.prohealthbio.com";
    // 中國站
//    private final static String serverDomainName = "https://api.pro-healthbio.cn";

    public static String getTokenApi() {
        return serverDomainName + "/api/Token";
    }

    public static String getUpdateTokenApi(){
        return APIFetch.serverDomainName + "/api/TokenUpdate";
    }

    public static String getMemberApi() {
        return serverDomainName + "/api/Member";
    }

    public static String getMemberEditApi() {
        return serverDomainName + "/api/MemberEdit";
    }

    public static String getReportList() {
        return serverDomainName + "/api/HealthReport";
    }

    public static String getFunctionListApi() {
        return serverDomainName + "/api/FunctionList";
    }

    public static String getCheckCodeApi() {
        return serverDomainName + "/api/SMS/";
    }

    public static String getCheckCodeForPasswordApi() {
        return APIFetch.serverDomainName + "/api/Password";
    }

    public static String getEditPasswordApi() {
        return APIFetch.serverDomainName + "/api/EditPassword";
    }

    public static String getRegisterResendCheckCodeApi() {
        return APIFetch.serverDomainName + "/api/SMS";
    }

    //健康報告 - 取的最近紀錄
    public static String getHealthReportApi() {
        return APIFetch.serverDomainName + "/api/HealthReportSoon";
    }

    //健康報告 - 取得最後一次紀錄
    public static String getLastHealthReportApi() {
        return APIFetch.serverDomainName + "/api/LastHealthReport";
    }

    //健康報告 - 已讀
    public static String getReadHealthReportApi() {
        return APIFetch.serverDomainName + "/api/ReadHealthReport";
    }

    //立即改善 - 取的最近紀錄
    public static String getHealthSummaryApi() {
        return APIFetch.serverDomainName + "/api/HealthSummarySoon";
    }

    //立即改善 - 取得最後一次紀錄
    public static String getLastHealthSummaryApi() {
        return APIFetch.serverDomainName + "/api/LastHealthSummary";
    }

    //立即改善 - 已讀
    public static String getReadHealthSummarySoonApi() {
        return APIFetch.serverDomainName + "/api/ReadHealthSummarySoon";
    }

    //我要回饋 - 回饋類別
    public static String getFeedbackTypeApi() {
        return APIFetch.serverDomainName + "/api/FeedbackType";
    }

    //我要回饋 - 送出我要回饋
    public static String getFeedbackApi() {
        return APIFetch.serverDomainName + "/api/Feedback";
    }

    //是否進行量測
    public static String getHealthCheckApi() {
        return APIFetch.serverDomainName + "/api/HealthCheck";
    }

    //取得量測報告API
    public static String getDetectionApi() {
        return "http://prohealthapiv2.v7idea.com.tw/api/Detection";
    }

    //建立量測API
    public static String getNewDetectionApi() {
        return APIFetch.serverDomainName + "/api/Detection";
    }

    //提醒設定 - 取得提醒設定
    public static String getNoticeSettingApi() {
        return APIFetch.serverDomainName + "/api/NoticeSetting";
    }

    //提醒設定 - 修改提醒設定
    public static String getEditNoticeSettingApi() {
        return APIFetch.serverDomainName + "/api/EditNoticeSetting";
    }

    //生活建議
    public static String getLifeNoticeApi() {
        return APIFetch.serverDomainName + "/api/notice";
    }

    //生活建議 - 已讀
    public static String getLifeNoticeReadApi() {
        return APIFetch.serverDomainName + "/api/noticeread";
    }
}
