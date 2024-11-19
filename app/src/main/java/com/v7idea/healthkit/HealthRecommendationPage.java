package com.v7idea.healthkit;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;

import com.v7idea.template.Activity.BaseActivity;
import com.v7idea.template.AirApplication;
import com.v7idea.template.Tool.ListItem;
import com.v7idea.template.Tool.ViewScaling;
import com.v7idea.template.View.AutoReleaseImageView;
import com.v7idea.template.View.Banner;
import com.v7idea.template.View.V7TitleView;
import com.v7idea.template.View.V7ideaBaseAdapter;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
/**
 * 2019/1/24 沒有使用的頁面
 */

public class HealthRecommendationPage extends BaseActivity implements View.OnClickListener{
    private static final String TAG = "HealthRecommendationPage";

    private Banner Header = null;
    private AutoReleaseImageView BannerImage = null;
    private ListView ShowContent = null;
    private HealthRecommendationListViewAdapter healthRecommendationListViewAdapter = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_health_recommendation_page);

        Header = (Banner) ViewScaling.findViewByIdAndScale(currentActivity, R.id.HealthRecommendationPage_Banner_Header);
        Header.initShortBanner();
        Header.backIcon.setOnClickListener(this);
        Header.title.setText("健康概要");

        BannerImage = (AutoReleaseImageView) ViewScaling.findViewByIdAndScale(currentActivity, R.id.HealthRecommendationPage_AutoReleaseImageView_BannerImage);
        ShowContent = (ListView) ViewScaling.findViewByIdAndScale(currentActivity, R.id.HealthRecommendationPage_ListView_ShowContent);

        healthRecommendationListViewAdapter = new HealthRecommendationListViewAdapter();
        ShowContent.setAdapter(healthRecommendationListViewAdapter);
        ShowContent.setOnItemClickListener(onItemClickListener);

        ArrayList<ListItem> dummyDataArray = getDummyData();

        healthRecommendationListViewAdapter.setData(dummyDataArray);
    }

    private ArrayList<ListItem> getDummyData(){
        String DummyData = "[{\"id\":\"假ID 1\",\"name\":\"營養師\",\"smallPhoto\":\"\",\"serialNo\":\"1\",\"ifGetNewData\":\"1\"},{\"id\":\"假ID 2\",\"name\":\"生活作息\",\"smallPhoto\":\"\",\"serialNo\":\"2\",\"ifGetNewData\":\"2\"},{\"id\":\"假ID 3\",\"name\":\"生鮮飲食\",\"smallPhoto\":\"\",\"serialNo\":\"3\",\"ifGetNewData\":\"0\"}]";

        ArrayList<ListItem> dummyDataArray = new ArrayList<ListItem>();

        try {
            JSONArray dummyJSONArray = new JSONArray(DummyData);

            for(int i = 0 ; i < dummyJSONArray.length() ; i++){
                JSONObject dummyItem = dummyJSONArray.optJSONObject(i);

                dummyDataArray.add(new DummyItem(dummyItem));
            }

            return dummyDataArray;

        } catch (JSONException e) {
            e.printStackTrace();

            return dummyDataArray;
        }
    }

    @Override
    public void onClick(View v) {
        AirApplication.setOnclikcFeedback(v);
        onBackPressed();
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(currentActivity, MainActivity.class);
        startActivity(intent);
        finish();
        setBackInPrePageAnimation(currentActivity);
    }

    private class DummyItem implements ListItem{

        private String title = "";
        private int ifGetNewData = 0;

        public DummyItem(JSONObject item){
            title = item.optString("name");
            ifGetNewData = Integer.valueOf(item.optString("ifGetNewData", "0"));
        }

        @Override
        public String getItemID() {
            return "";
        }

        @Override
        public int getIconResourceID() {
            return 0;
        }

        @Override
        public String getImagePath() {
            return "";
        }

        @Override
        public String getTitle() {
            return title;
        }

        @Override
        public String getSubTitle() {
            return "";
        }

        @Override
        public String getCode() {
            return "";
        }

        @Override
        public int getSerialNo() {
            return 0;
        }

        @Override
        public int getNoticeNumber() {
            return ifGetNewData;
        }
    }

    private AdapterView.OnItemClickListener onItemClickListener = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            AirApplication.setOnclikcFeedback(view);
            Intent intent = new Intent(currentActivity, HealthRecommendationSubMenuPage.class);
            startActivity(intent);
            finish();
            setTurnInNextPageAnimation(currentActivity);
        }
    };

    public class HealthRecommendationListViewAdapter extends V7ideaBaseAdapter {

        @Override
        public View getItemView(int position, View convertView, ViewGroup parent) {

            ViewHolder viewHolder;

            if (convertView == null) {
                convertView = getLayoutInflater().inflate(R.layout.health_recommendation_list_item, null);
                convertView.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT
                        , (int)(148 * ViewScaling.getScaleValue())));
                ViewScaling.setPadding(convertView);

                viewHolder = new ViewHolder(convertView);
                convertView.setTag(viewHolder);
            } else {
                viewHolder = (ViewHolder) convertView.getTag();
            }

            ListItem item = getItem(position);

            if(item != null){
                String title = item.getTitle();
                viewHolder.Title.setText(title);

                if(item.getNoticeNumber() > 0){
                    viewHolder.Icon.setVisibility(View.VISIBLE);
                }
                else {
                    viewHolder.Icon.setVisibility(View.INVISIBLE);
                }
            }

            return convertView;
        }

        private class ViewHolder{

            public ImageView Background = null;
            public V7TitleView Title = null;
            public ImageView Icon = null;

            public ViewHolder(View view){
                Background = (ImageView) ViewScaling.findViewByIdAndScale(view, R.id.RecommendationItem_ImageView_Background);
                Title = (V7TitleView) ViewScaling.findViewByIdAndScale(view, R.id.RecommendationItem_V7TitleView_Name);
                Icon = (ImageView) ViewScaling.findViewByIdAndScale(view, R.id.RecommendationItem_ImageView_Icon);
            }
        }
    }
}
