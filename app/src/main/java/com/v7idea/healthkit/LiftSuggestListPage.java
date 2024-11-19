package com.v7idea.healthkit;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.api.client.json.Json;
import com.v7idea.healthkit.Domain.LifeNoticeData;
import com.v7idea.healthkit.Domain.ListItem;
import com.v7idea.healthkit.Model.LifeNotice;
import com.v7idea.healthkit.Model.Token;
import com.v7idea.healthkit.View.BottomButton;
import com.v7idea.template.Activity.BaseActivity;
import com.v7idea.template.AirApplication;
import com.v7idea.template.DataBase.SimpleDatabase;
import com.v7idea.template.Tool.ApiResult;
import com.v7idea.template.Tool.DownLoad;
import com.v7idea.template.Tool.NotifyDialog;
import com.v7idea.template.Tool.V7ideaAsyncTask;
import com.v7idea.template.Tool.ViewScaling;
import com.v7idea.template.View.Banner;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
/**
 * 2019/1/24 ＊確認使用的頁面 主頁面-生活建議
 */
public class LiftSuggestListPage extends BaseActivity {
    private static final String TAG = "LiftSuggestListPage";
    private ListViewAdapter listViewAdapter = null;
    private SimpleDatabase simpleDatabase = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lift_suggest_list_page);
        AirApplication airApplication = (AirApplication) getApplication();
        if (airApplication.isIsEntryPage() == false) {
            Intent intent = new Intent();
            intent.setClass(LiftSuggestListPage.this, EntryPage.class);
            intent.putExtras(getIntent());
            startActivity(intent);
            finish();
        }

        Banner banner = (Banner) ViewScaling.findViewByIdAndScale(currentActivity, R.id.LiftSuggestListPage_Banner_Header);
        banner.initShortBanner();
        banner.backIcon.setOnClickListener(OnBackIcon);
        banner.title.setText("生活建議");
        banner.title.setTextColor(Color.parseColor("#FFFFFF"));

        simpleDatabase = new SimpleDatabase();
        String jsonData = simpleDatabase.getStringValueByKey(Constant.LIFE_NOTICE_DATE, "");

        ListView listView = (ListView) ViewScaling.findViewByIdAndScale(currentActivity, R.id.LiftSuggestListPage_ListView_ShowList);
        listViewAdapter = new ListViewAdapter();
        listView.setAdapter(listViewAdapter);


        BottomButton bottomButton = (BottomButton) ViewScaling.findViewByIdAndScale(currentActivity, R.id.LiftSuggestListPage_BottomButton_BottomButton);
        bottomButton.setData(getString(R.string.NO), getString(R.string.YES), 100);
        bottomButton.getLeftButton().setOnClickListener(OnBackIcon);
        bottomButton.setVisibility(View.GONE);

        Token token = new Token();
        String strTokenId = token.getTokenId();
        if (strTokenId != null && strTokenId.isEmpty() == false) {
            if (isConnectedToNetworkNotAlert()) {
                DownLoadLifeNotice downLoadLifeNotice = new DownLoadLifeNotice();
                downLoadLifeNotice.execute();
            } else {
                showErrorAlert("您尚未開啟網路，故無法觀看最新資料!!") ;
                LifeNoticeData lifeNoticeData = new LifeNoticeData();
                if (jsonData != null && jsonData.isEmpty() == false) {
                    ArrayList<LifeNoticeData> data = lifeNoticeData.parseData(jsonData);
                    listViewAdapter.setData(data);
                }
            }


        } else {
            if (isConnectedToNetworkNotAlert()) {

                showErrorAlert("您尚未登入，請您先進行登入", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        new Token().destroy();
                        Intent intent = new Intent(currentActivity, LoginPage.class);
                        startActivity(intent);
                        currentActivity.finish();
                        setBackInPrePageAnimation(currentActivity);

                    }
                });
            } else {
                showErrorAlert("您尚未登入，請您先進行登入", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        new Token().destroy();
                        Intent intent = new Intent(currentActivity, LoginPage.class);
                        intent.putExtra(getResources().getString(R.string.no_internet_can_not_use),true);
                        startActivity(intent);
                        currentActivity.finish();
                        setBackInPrePageAnimation(currentActivity);

                    }
                });

            }
        }
    }

    private class ListViewAdapter extends BaseAdapter {

        private ArrayList<LifeNoticeData> itemArrayList = null;

        public void setData(ArrayList<LifeNoticeData> itemArrayList) {
            this.itemArrayList = itemArrayList;
            notifyDataSetChanged();
        }

        @Override
        public int getCount() {
            if (itemArrayList != null) {
                return itemArrayList.size();
            } else {
                return 0;
            }
        }

        @Override
        public LifeNoticeData getItem(int position) {
            if (itemArrayList != null && position > -1 && position < itemArrayList.size()) {
                return itemArrayList.get(position);
            } else {
                return null;
            }
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder viewHolder = null;

            if (convertView == null) {
                viewHolder = new ViewHolder();

                convertView = getLayoutInflater().inflate(R.layout.life_suggest_list_item_layout, null);

                viewHolder.showDate = (TextView) ViewScaling.findViewByIdAndScale(convertView, R.id.lifeSuggest_TextView_dateTime);

                viewHolder.showTitle = (TextView) ViewScaling.findViewByIdAndScale(convertView, R.id.lifeSuggest_TextView_title);

                convertView.setTag(viewHolder);
            } else {
                viewHolder = (ViewHolder) convertView.getTag();
            }

            LifeNoticeData item = getItem(position);

            if (item != null) {
                viewHolder.showTitle.setText(item.getMessage());
                viewHolder.showDate.setText(item.getDate());
            }

            return convertView;
        }

        public class ViewHolder {
            public TextView showDate = null;
            public TextView showTitle = null;
        }
    }

    View.OnClickListener OnBackIcon = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            AirApplication.setOnclikcFeedback(v);
            onBackPressed();
        }
    };

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(currentActivity, MainActivity.class);
        startActivity(intent);
        currentActivity.finish();
        setTurnInNextPageAnimation(currentActivity);
    }

    private class DownLoadLifeNotice extends V7ideaAsyncTask<String, ApiResult> {

        private NotifyDialog progress = null;
        private LifeNotice lifeNotice = null;
        private LifeNoticeData lifeNoticeData = null;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            lifeNotice = new LifeNotice();
            progress = new NotifyDialog(currentActivity);
            progress.settingProgressDialog();

            lifeNoticeData = new LifeNoticeData();
        }

        @Override
        public void ifNeedCloseSomeThing() {
            if (progress != null) {
                progress.dismiss();
                progress = null;
            }
        }

        @Override
        public void downLoadSuccess(ApiResult result) {
            Log.e(TAG, "downLoadSuccess = " + " result = " + result);

            ArrayList<LifeNoticeData> listdata = lifeNoticeData.parseData(result.getData());
            listViewAdapter.setData(listdata);
            String arrydata[] = new String[listdata.size()];
            JSONObject jsonObject = new JSONObject();
            JSONArray jsonArray = new JSONArray();
            try {


                for (int i = 0; i < listdata.size(); i++) {
                    if (listdata.get(i).getIfRead().contentEquals("0")) {
                        jsonArray.put(listdata.get(i).getNoticeID());
                        arrydata[i] = listdata.get(i).getNoticeID();
                    }
                }
                jsonObject.put("NoticeID", jsonArray);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            UpLifeNoticeRead upLifeNoticeRead = new UpLifeNoticeRead();
            upLifeNoticeRead.execute(jsonArray.toString());
//            Log.e(TAG, "arrydata.toString() = " + Arrays.toString(aaa));


            simpleDatabase.setValueByKey(Constant.LIFE_NOTICE_DATE, result.getData());


        }

        @Override
        public void downLoadFail(ApiResult apiResult) {

            if (apiResult.getErrorNo().contentEquals(Constant.ErrorCode.CODE_500)) {
                showErrorAlert(getResources().getString(R.string.no_internet_error500));
                Intent intent = new Intent(currentActivity, MainActivity.class);
                startActivity(intent);
                finish();
                setBackInPrePageAnimation(currentActivity);
            } else if (apiResult.getErrorNo().contentEquals(Constant.ErrorCode.CODE_1002)) {
                showErrorAlert(apiResult.getMessage(), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        new Token().destroy();
                        Intent intent = new Intent(currentActivity, LoginPage.class);
                        startActivity(intent);
                        finish();
                        setBackInPrePageAnimation(currentActivity);

                    }
                });
            } else if (apiResult.getErrorNo().contentEquals(Constant.ErrorCode.CODE_1003)) {
                showErrorAlert(apiResult.getMessage(), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        new Token().destroy();
                        Intent intent = new Intent(currentActivity, LoginPage.class);
                        startActivity(intent);
                        finish();
                        setBackInPrePageAnimation(currentActivity);

                    }
                });
            } else {
                showErrorAlert(apiResult.getMessage());
            }
        }

        @Override
        protected ApiResult doInBackground(String... params) {

            Token token = new Token();
            return lifeNotice.getLifeNotice(token.getTokenId());
        }

    }

    private class UpLifeNoticeRead extends V7ideaAsyncTask<String, ApiResult> {

        private NotifyDialog progress = null;
        private LifeNotice lifeNotice = null;
        private LifeNoticeData lifeNoticeData = null;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            lifeNotice = new LifeNotice();
            progress = new NotifyDialog(currentActivity);
            progress.settingProgressDialog();

            lifeNoticeData = new LifeNoticeData();
        }

        @Override
        public void ifNeedCloseSomeThing() {
            if (progress != null) {
                progress.dismiss();
                progress = null;
            }
        }

        @Override
        public void downLoadSuccess(ApiResult result) {
            Log.e(TAG, "downLoadSuccess = " + " result = " + result);


        }

        @Override
        public void downLoadFail(ApiResult apiResult) {
            Log.e(TAG, "downLoadFail = " + " apiResult = " + apiResult);
        }

        @Override
        protected ApiResult doInBackground(String... params) {

            Token token = new Token();
            return lifeNotice.getLifeNoticeData(token.getTokenId(), params[0]);
        }

    }

//    private ArrayList<LifeNoticeData> invertOrderList(ArrayList<LifeNoticeData> lifeNoticeData) {
//        SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd  HH:mm:ss");
//        Date d1 = null;
//        Date d2 = null;
//        LifeNoticeData temp_r = new LifeNoticeData();
//        //做一个冒泡排序，大的在数组的前列
//        for (int i = 0; i < lifeNoticeData.size() - 1; i++) {
//            for (int j = i + 1; j < lifeNoticeData.size(); j++) {
//                ParsePosition pos1 = new ParsePosition(0);
//                ParsePosition pos2 = new ParsePosition(0);
//
//
//                if (lifeNoticeData.get(i).getDate().indexOf("上午") != -1) {
//                    String string = lifeNoticeData.get(i).getDate().replace("上午", "");
//                    d1 = sdf.parse(string, pos1);
//                } else if (lifeNoticeData.get(i).getDate().indexOf("下午") != -1) {
//                    String string = lifeNoticeData.get(i).getDate().replace("下午", "");
//                    d1 = sdf.parse(string, pos1);
//                    Date newDate1 = new Date(d1.getTime() + (long) 12 * 60 * 60 * 1000);
//                    d1 = newDate1;
//                }
//
//                if (lifeNoticeData.get(j).getDate().indexOf("上午") != -1) {
//                    String string2 = lifeNoticeData.get(j).getDate().replace("上午", "");
//                    d2 = sdf.parse(string2, pos2);
//                } else if (lifeNoticeData.get(j).getDate().indexOf("下午") != -1) {
//                    String string2 = lifeNoticeData.get(j).getDate().replace("下午", "");
//                    d2 = sdf.parse(string2, pos2);
//                    Date newDate2 = new Date(d2.getTime() + (long) 12 * 60 * 60 * 1000);
//                    d2 = newDate2;
//                }
////                Log.e(TAG, "string = " + string);
////                Log.e(TAG, "string2 = " + string2);
////                Log.e(TAG, "d1 = " + d1);
////                Log.e(TAG, "d2 = " + d2);
//                if (d1.before(d2)) {//如果队前日期靠前，调换顺序
//                    lifeNoticeData.set(i, lifeNoticeData.get(j));
//                    lifeNoticeData.set(j, lifeNoticeData.get(i));
//                }
//            }
//        }
//        return lifeNoticeData;
//    }
}
