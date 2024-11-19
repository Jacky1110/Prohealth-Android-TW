package com.v7idea.healthkit;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;

import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.v7idea.template.Activity.BaseActivity;
import com.v7idea.template.AirApplication;
import com.v7idea.template.Tool.ListItem;
import com.v7idea.template.Tool.ViewScaling;
import com.v7idea.template.View.Banner;
import com.v7idea.template.View.BannerPager;
import com.v7idea.template.View.ContentWebView;
import com.v7idea.template.View.ListScrollView;
import com.v7idea.template.View.V7TitleView;

import java.util.ArrayList;
/**
 * 2019/1/24 沒有使用的頁面
 */
public class HealthRecommendationDetailPage extends BaseActivity implements View.OnClickListener{

    private Banner Header = null;
    private ListScrollView ScrollView = null;
    private RelativeLayout BannerContainer = null;
    private V7TitleView MiddleTitle = null;
    private BannerPager ThisPager = null;

    private ContentWebView ThisContentWebView = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_health_recommendation_detail_page);

        Header = (Banner) ViewScaling.findViewByIdAndScale(currentActivity, R.id.HealthRecommendationDetailPage_Banner_Header);
        Header.initShortBanner();
        Header.backIcon.setOnClickListener(this);
        Header.title.setText("營養師");

        ScrollView = (ListScrollView) ViewScaling.findViewByIdAndScale(currentActivity, R.id.HealthRecommendationDetailPage_ListScrollView_ScrollView);
        ScrollView.setTopEmptyArea((int) (620 * ViewScaling.getScaleValue()));
        ScrollView.setTargetMaxScrollHeight((int) ((float) 390 * ViewScaling.getScaleValue()));

        BannerContainer = (RelativeLayout) ViewScaling.findViewByIdAndScale(currentActivity, R.id.HealthRecommendationDetailPage_RelativeLayout_BannerContainer);
        ThisPager = getBannerPage();
        MiddleTitle = (V7TitleView) ViewScaling.findViewByIdAndScale(currentActivity, R.id.HealthRecommendationDetailPage_V7TitleView_MiddleTitle);

        MiddleTitle.setText("勿忽視肌肉痠痛！　小問題恐造成大麻煩");

        BannerPager.insertBannerImage(currentActivity, getDummyBannerImageData(), ThisPager);
        BannerContainer.addView(ThisPager);
        BannerContainer.bringToFront();
        Header.bringToFront();

        ScrollView.setTargetLayout(BannerContainer, (int)(390 * ViewScaling.getScaleValue()));

        ThisContentWebView = new ContentWebView(this);
        ViewScaling.setScaleForLinearLayout(ThisContentWebView, ViewGroup.LayoutParams.MATCH_PARENT
                , ViewGroup.LayoutParams.WRAP_CONTENT, 0, 0);
        ThisContentWebView.init();
        ThisContentWebView.loadUrl("file:///android_asset/content.html");
        ThisContentWebView.setWebMarginTop(0);
        ThisContentWebView.setWebMarginBottom(0);
        ThisContentWebView.setTargetLayout(null, 0);

        ScrollView.addView(ThisContentWebView);

        String loadString = "如果你有肌肉痠痛的問題，可不要太輕忽，以免小問題造成大麻煩！很多人都難免會這裡痠、那裡痛，尤其是天氣逐漸變冷，或是運動之後，都會覺得肌肉又痠又痛。傳統中醫認為，不通則痛，到底是哪裡不通呢？深諳養生之道的中醫師李深浦指出，關鍵點源自於脾胃！\n" +
                "\n" +
                "肌肉痠痛是體內散熱機制衰退　原因在腸胃機能不好\n" +
                "\n" +
                "李深浦醫師表示，人身體裡面一直在散熱，如果有人體內熱量無法散熱，外面的溫度溫差，以及風邪寒氣濕氣就會侵入體內，並會導致風濕，因而會感到肌肉痠痛；所以，風濕就是體內散熱機制已經衰退，而且體內散熱機制衰退的主要原因，還在於先有腸胃機能不好，營養發酵不好，體內就沒有足夠熱量來散熱。\n" +
                "\n" +
                "五行心火不下降　脾胃之土就不好\n" +
                "\n" +
                "李深浦醫師解釋，中醫講究五行養生，胃腸脾胃屬中土，心臟血液循環中樞屬火，火生土、土生金、金生水、水生木、木生火，五行運轉不息。但是現在人都勞心，沒有勞力，白天忙，晚上還在煩惱，無法休息，使得心火無法下降歸脾胃，就無法火生土，以致現代人的腸胃會不好，有胃酸逆流和胃病的人很多；因此，所產生的熱量就不足抵抗天氣變化，不但容易感冒，也很容易有頭痛、肌肉痠痛，恢復疲勞速度慢，因為體內氣體散發速度慢且不夠，體液循環沒有足夠溫度，所以肌肉僵硬緊繃的情況就無法舒緩，恢復體力的速度會延緩，而且得病有年輕化的現象。\n" +
                "\n" +
                "心神守舍　才能使火生土\n" +
                "\n" +
                "李深浦醫師分析，若是火不能生土，胃腸的酵素就無法發揮作用；所以，人每天都要有時間讓心回到胃腸，使火生土，胃腸才會繁殖強大的體氣，使肌肉間有溫暖液體循環，也就是體氣，才可使肌肉關節鬆，骨骼神經往外擴張不會被壓迫到。至於氣如何來？李醫師強調，就是要懂得心神守舍，一天要三、四小時全神貫注，心神守舍；否則，心不能守舍，氣就會弱，以致會消化不完全，營養無法完全分解變成熱量，身體就會慢慢變質，而人一旦體質變壞，要復原就很難！\n" +
                "\n" +
                "每天要讓心神歸根　才不會肌肉痠痛\n" +
                "\n" +
                "所以，肌肉痠痛就是體氣問題，而且飲食也很重要；李深浦醫師說，現在人愛喝冷飲，溫度突然下降，心臟無法負荷，心臟抽筋，很容易猝死。所以，人體的氣不能離開精神，胃腸吸收分解產生熱量，要有心才有用不完的能量，但要讓腦筋休息，而且不要忘記每天要讓心神歸根，才能得到永遠用不完的能量，也才不會肌肉痠痛。尤其是神耕時代來臨；所以，懂得維持體內的氣很重要，以免因為休息不足導致肌肉受傷機率增高。";
        ThisContentWebView.replaceContent(loadString);
    }

    private BannerPager getBannerPage() {
        int bannerWidth = (int) ((float) 750 * ViewScaling.getScaleValue());
        int bannerHeight = (int) ((float) 390 * ViewScaling.getScaleValue());
        int bannerLeft = (int) ((float) 0 * ViewScaling.getScaleValue());
        int bannerTop = (int) ((float) 0 * ViewScaling.getScaleValue());

        int imageWidth = (int) ((float) 750 * ViewScaling.getScaleValue());
        int imageHeight = (int) ((float) 390 * ViewScaling.getScaleValue());

        BannerPager topBanner = new BannerPager(this, bannerWidth, imageHeight, LinearLayout.HORIZONTAL);
        topBanner.setBannerImageSize(imageWidth, imageHeight, bannerLeft, bannerTop);

        RelativeLayout.LayoutParams thisParams = new RelativeLayout.LayoutParams(bannerWidth, imageHeight);
        thisParams.leftMargin = bannerLeft;
        thisParams.topMargin = bannerTop;
        topBanner.setLayoutParams(thisParams);

        topBanner.setBackgroundColor(0);

        return topBanner;
    }

    private ArrayList<ListItem> getDummyBannerImageData(){
        ArrayList<ListItem> dummyBannerImageDataArray = new ArrayList<ListItem>();
        dummyBannerImageDataArray.add(new DummyBannerImageItem(R.mipmap.health_news_top_1));
        dummyBannerImageDataArray.add(new DummyBannerImageItem(R.mipmap.health_news_top_2));

        return dummyBannerImageDataArray;
    }

    private class DummyBannerImageItem implements ListItem{

        private int imageResourceID = 0;

        public DummyBannerImageItem(int imageResourceID){
            this.imageResourceID = imageResourceID;
        }

        @Override
        public String getItemID() {
            return null;
        }

        @Override
        public int getIconResourceID() {
            return imageResourceID;
        }

        @Override
        public String getImagePath() {
            return null;
        }

        @Override
        public String getTitle() {
            return null;
        }

        @Override
        public String getSubTitle() {
            return null;
        }

        @Override
        public String getCode() {
            return null;
        }

        @Override
        public int getSerialNo() {
            return 0;
        }

        @Override
        public int getNoticeNumber() {
            return 0;
        }
    }


    @Override
    public void onClick(View v) {
        AirApplication.setOnclikcFeedback(v);
        onBackPressed();
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(currentActivity, HealthRecommendationSubMenuPage.class);
        startActivity(intent);
        finish();
        setBackInPrePageAnimation(currentActivity);
    }
}
