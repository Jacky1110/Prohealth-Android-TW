package com.v7idea.healthkit;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.v7idea.healthkit.View.BottomButton;
import com.v7idea.template.Activity.BaseActivity;
import com.v7idea.template.AirApplication;
import com.v7idea.template.Tool.ViewScaling;
import com.v7idea.template.View.AutoReleaseImageView;
import com.v7idea.template.View.V7TitleView;
/**
 * 2019/1/24 ＊確認使用的頁面 註冊-隱私權確認頁
 */
public class TermsOfServicePage extends BaseActivity {

    private ImageView checkBoxImage = null;
    private TextView middleTitle = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_terms_of_service_page);

        AutoReleaseImageView SpecialImageView_Background = (AutoReleaseImageView) ViewScaling.findViewByIdAndScale(currentActivity, R.id.TermsOfServicePage_SpecialImageView_Background);

        LinearLayout CheckBoxContainer = (LinearLayout)ViewScaling.findViewByIdAndScale(currentActivity, R.id.TermsOfServicePage_LinearLayout_CheckBoxContainer);

        checkBoxImage = (ImageView) ViewScaling.findViewByIdAndScale(currentActivity, R.id.TermsOfServicePage_ImageView_CheckIcon);
        middleTitle = (TextView) ViewScaling.findViewByIdAndScale(currentActivity, R.id.TermsOfServicePage_TextView_MiddleTitle);

        checkBoxImage.setOnClickListener(onPressCheckBoxImageOrMiddleTitle);
        middleTitle.setOnClickListener(onPressCheckBoxImageOrMiddleTitle);

        BottomButton BottomButton = (BottomButton) ViewScaling.findViewByIdAndScale(currentActivity, R.id.TermsOfServicePage_BottomButton);
        BottomButton.setData(getResources().getString(R.string.TermsOfServicePage_Text_Disagree), getResources().getString(R.string.TermsOfServicePage_Text_Agree), 100);
        V7TitleView buttonAgree = BottomButton.getRightButton();
        V7TitleView buttonDisagree = BottomButton.getLeftButton();

        buttonAgree.setOnClickListener(onAgree);
        buttonDisagree.setOnClickListener(onDisagree);

        WebView mWebView = (WebView) ViewScaling.findViewByIdAndScale(currentActivity, R.id.TermsOfServicePage_WebView);
        mWebView.setBackgroundColor(0);
        mWebView.getSettings().setJavaScriptEnabled(false);
        mWebView.getSettings().setSupportZoom(false);
        mWebView.getSettings().setBuiltInZoomControls(false);
        mWebView.getSettings().setLayoutAlgorithm(WebSettings.LayoutAlgorithm.NARROW_COLUMNS);
        mWebView.getSettings().setDefaultFontSize(16);
//        String testString = "<!DOCTYPE html>\r\n<html>\r\n\r\n<head>\r\n    <title>{Title}</title>\r\n    <meta name=\"apple-mobile-web-app-capable\" content=\"yes\" />\r\n    <meta name=\"viewport\" content=\"width=device-width, maximum-scale=1.0,user-scalable=0\" />\r\n    <meta name=\"HandheldFriendly\" content=\"true\" />\r\n    <meta name=\"MobileOptimized\" content=\"320\" />\r\n    <meta http-equiv=\"Content-Type\" content=\"text/html; charset=utf-8\">\r\n</head>\r\n<style>\r\n.aboutUs{\r\n color:#000000; \r\n}\r\n</style>\r\n<body>\r\n\r\n    <!--Document Start-->\r\n    <div class=\"aboutUs\">\r\n        <h3>\r\n            <span style =\"color:#000000\">\r\n                ProHealth BioIntellect隱私權保護政策</span>\r\n                <!--<span style=\"color:#a2a3ff\"></span>-->\r\n        </h3>\r\n    </div>\r\n     <!--<hr>-->\r\n    <div class=\"aboutUs\">\r\n        <p>ProHealth BioIntellect Limited（以下簡稱本公司）所提供的Prohealth 服務（以下簡稱「Prohealth」），為確保您能夠安心使用本公司的各項會員服務與資訊，謹依個人資料保護法（以下簡稱個資法）與本「隱私權政策」蒐集、處理及利用您的個人資料，及承諾尊重以及保護您個人於本公司服務平台的隱私權。並提醒在您完成Prohealth 服務的註冊程序後，您亦同時成為本公司的會員，當您使用Prohealth 服務時，將視為您已閱讀、了解並接受本隱私權政策。<br><br>1. 本隱私權政策適用之範圍<br>本「隱私權政策」適用本公司所有服務平台與網域。<br>本隱私權政策僅適用於本公司之Prohealth服務對您個人資料所為蒐集、處理與利用，不及於其他非本公司所有或控制之其他公司或個人。您可能經由本網站連結至第三人所經營之網站，您同意這些網站關於個人資訊的處理依其各自的隱私權政策。本公司不因此負任何責任。<br><br>2. 資料蒐集及使用<br>當您在 Prohealth註冊時，我們會依該服務功能性質，請您提供必要的個人資料（包含但不限於姓名、電子郵件、出生日期、性別、行動電話、身高、體重、喜好的飲食習慣與運動類型、居住城市等資料），而您同意我們蒐集、保存、處理並使用這些資料，非經您書面同意，本公司不會將個人資料用於其他用途。當您使用Prohealth服務或參加Prohealth活動時，我們也可能會蒐集其他關於您個人或您活動之特定資訊。<br><br>3. 蒐集、處理及利用個人資料之特定目的，包括但不限於：<br><li>改善Prohealth服務內容與品質，提升您對產品及服務的滿意度。</li><li>調查、研究、分析及統計各項使用數據及記錄，經營並改進Prohealth的服務內容與品質。</li><li>客製化健康促進建議。</li><li>規劃Prohealth產品新功能及新服務。</li><li>客戶服務及問題處理。</li><li>提供內部及外部客戶匿名報告。</li><li>本公司各項會員服務、活動之通知。</li><li>Prohealth 將在註冊登錄後取得您的同意後，傳送商業性資料或電子郵件給您。</li><li>Prohealth在您使用Prohealth服務時，自動接收並記錄您在行動裝置及瀏覽器上的資料，包括 IP位址、Cookie存取、及App的使用資料。這些資料僅用於分析使用者總流量或網路行為調查。</li><br><br>4. 資料分享與揭露<br>本公司絕不會提供、交換、出租或出售任何您的個人資料給其他個人、團體、私人企業或公務機關，但有法律依據或合約義務者，不在此限。<br>前項但書之情形包括不限於：<br><li>經由您書面同意。</li><li>法律明文規定。</li><li>為維護國家安全或增進公共利益。</li><li>為免除您生命、身體、自由或財產上之危險。</li><li>公務機關或學術研究機構基於公共利益為統計或學術研究而有必要，且資料經過提供者處理或蒐集著依其揭露方式無從識別特定之當事人。</li><li>有利於您的權益。</li><li>本網站委託廠商協助蒐集、處理或利用您的個人資料時，將對委外廠商或個人善盡監督管理之責。</li><br><br>5. 查資料分享與揭露<br>您可隨時查詢、閱覽、複製及修改您的Prohealth會員帳號及個人資訊。我們保有權利向您傳送Prohealth服務相關的特定訊息 (如服務公告、管理訊息)，這些訊息被視為您Prohealth帳號的一部分，您無法選擇退出接收。但您可以採取終止使用Prohealth服務以及書面通知本公司的方式，來刪除本公司所有已蒐集之個人資訊及停止寄送與服務相關之訊息。<br><br>6. 隱私保權政策之修訂<br>本公司可不時修訂本政策。當我們在個人資料的處理上有重大變更時，會將通知傳送到您在本公司登記之會員帳號中，指定的主要電子郵件地址，或在Prohealth服務平台網站上張貼告示。<br><br>7. 問題和建議<br>如果您有任何問題或建議，請至Prohealth官網填寫意見反應表，或透過客服信箱service@prohealthbio.com與我們聯繫。</p>\r\n    </div>\r\n\r\n\r\n    <!--Document End-->\r\n\r\n\r\n</body>\r\n\r\n\r\n</html>";
//        mWebView.loadDataWithBaseURL(null, testString, "text/html", "utf-8", "");
        mWebView.loadUrl("file:///android_asset/prohealth_policy.html");
    }

    private View.OnClickListener onPressCheckBoxImageOrMiddleTitle = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            AirApplication.setOnclikcFeedback(v);
            boolean isSelected = !checkBoxImage.isSelected();
            checkBoxImage.setSelected(isSelected);
            middleTitle.setSelected(isSelected);
        }
    };


    private View.OnClickListener onAgree = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            AirApplication.setOnclikcFeedback(v);
            if (checkBoxImage.isSelected()) {
                Intent intent = new Intent(currentActivity, RegisterPage.class);
                startActivity(intent);
                currentActivity.finish();
                setTurnInNextPageAnimation(currentActivity);
            } else {
                showErrorAlert(getResources().getString(R.string.TermsOfServicePage_Text_Error));
            }
        }
    };
    private View.OnClickListener onDisagree = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            AirApplication.setOnclikcFeedback(v);
            Intent intent = new Intent(currentActivity, LoginPage.class);
            startActivity(intent);
            currentActivity.finish();
            setTurnInNextPageAnimation(currentActivity);
        }
    };

    public void onBackPressed() {
        Intent intent = new Intent(currentActivity, LoginPage.class);
        startActivity(intent);
        finish();
        setBackInPrePageAnimation(currentActivity);
    }
}
