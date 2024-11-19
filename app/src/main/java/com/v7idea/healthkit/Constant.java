package com.v7idea.healthkit;

/**
 * Created by mortal on 2017/9/25.
 */

public class Constant
{
    public class Command
    {
        public static final String BEHAVIOR_CHECK = "BEHAVIOR CHECK!!";
        public static final String RESET_AND_START = "RESET AND START!";
        public static final String MEASURE_MODE_END = "MEASURE MODE END";
        public static final String QUERY_ERROR_STA = "QUERY ERROR STA?";

        //MP10 是取得firmware版號
        public static final String MP10 = "MP10";
        //MP13 NXP停止傳送(82,84錯誤)
        public static final String MP13 = "MP13";

        //取得版子的SerialNumber
        public static final String SERIAL_NUMBER = "0003000400030004";
        public static final String RECEIVE_SCAN_DATA = "001D000F001D000F";
    }

    public class ErrorCode
    {
        public final static String SUCCESS = "0000";
        public final static String FAIL = "1";
        public final static String NO_LOGIN = "2";
        public final static String ACCOUNT_NOT_EXIST = "2002";

        public final static String CODE_404 = "404";
        public final static String CODE_500 = "500";

        public final static String CODE_1002 = "1002";
        public final static String CODE_1003 = "1003";
        public final static String CODE_1100 = "1100";
        public final static String CODE_1200 = "1200";//會員不存在

        public final static String CODE_9000 = "9000";
        public final static String CODE_9001 = "9001";
        public final static String CODE_9999 = "9999";
    }

    public final static String IS_NOT_INTRO_REPORT = "isIntro_Report";
    public final static boolean IS_NOT_INTRO_REPORT_DEFAUL = true;
    public final static String IS_NOT_HEALTH_REPORT = "isHealth_Report";
    public final static boolean IS_NOT_HEALTH_REPORT_DEFAUL = true;
    public final static String IS_OPERATION_INSTRUCTIONS = "isOperation_Instructions";
    //立即改善
    public static final String IMMEDIATE_IMPROVEMENT = "Immediate_improvement";
    //健康報告
    public static final String HEALTH_REPORT = "Health_report";

    public static final String NOT_READ_Count = "Not_Read_Count";

    public static final String LAST_REPORT_ID = "lastReportID";

    public static final String GET_NOTIFICATION = "GetNotification";

    public final static String TOKEN_ID = "NirPlusProcessTicketID";
    public static final String GCMRegisterID = "GCMID";
    public final static int EntryPageToMainPageDelayTime = 3000;

    public static final boolean InitBoolean = true;
    public static final String InitString = "Init";
    public static final String LAST_DETECTION = "lastDetection";
    public static final boolean LAST_DETECTION_BOOLEAN = false;

    public static final String USER_INPUT_DATA = "userInputData";
    public static final String USER_NAME = "myname";
    public static final String USER_TEL = "mytel";
    public static final String USER_BIRTHDAY = "mybirthday";
    public static final String USER_GENDER = "mysex";
    public static final String USER_BLOOD = "myblood";
    public static final String USER_RESELLER = "reseller";

    public static final String TEMP_ACCOUNT = "tempAccount";
    public static final String TEMP_PASSWORD = "tempPassword";
    public static final String LAST_CONNECTED_DEVICE_MAC = "laseConnectedMac";

    public static final String LAST_DETECTION_DATE = "LastDetectionDate";

    public static final String DEVICE_RESET_AND_START_COMMAND = "RESET AND START!";

    public static final int DETECTION_PERIOD = 7;

    public static final String LIFE_NOTICE_DATE = "LifeNoticeDate";

    /**
     * 在掃瞄藍牙BLE頁面用，確認權限是否開起的回傳辨示碼
     */
    public static final int REQUEST_CODE = 9992;
    public static final String ONCLICK_FEEDBACK = "onClickFeedback";

    public static final String NOTICE_FUNCTION_TYPE_LIFE = "LifeNoticeDate";
    public static final String NOTICE_FUNCTION_TYPE_PACIENT = "PacientNotice";
    public static final String NOTICE_FUNCTION_TYPE_ENTRYPAGE = "EntryPage";
    public static final String NOTICE_FUNCTION_TYPE_DOWNLOAD_DATA = "DownloadData";
//    public static final String REGISTER_SUCCESS_SOON_GET_ONLY_KEY = "OnlyKey";
//    public static final String LANGUAGE_TYPE = "LanguageType";
//    public static final String AREA_AND_CHURCH = "AreaAndChurch";
//
//    public static final String UserTempName = "UserTempName";
//    public static final String UserTempPassword = "UserTempPassword";
//
//    public static final String SETTING_NOT_SUSPEND = "NoticeNotSuspend";
//    public static final String SETTING_RING = "NoticeRing";
//    public static final String SETTING_SHOCK = "NoticeShock";
//    public static final String SETTING_LED = "NoticeLed";
//    public static final String IsSuspendMode = "IsSuspendMode";
//    public static final String SuspendTime = "SuspendTime";
//
//    public static final String NoticeTable = "NoticeTable";
//    public static final String NoticeData = "NoticeData";
//
//    public static final String UnUpDateBible = "UnUpDateBible";
//
//    public static final String LAST_APP_VERSION_CODE = "lastAppVersionCode";
}
