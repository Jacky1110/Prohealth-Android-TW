package com.v7idea.healthkit;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;

import com.v7idea.healthkit.Domain.NoticeData;
import com.v7idea.healthkit.Model.NoticeSetting;
import com.v7idea.healthkit.Model.Token;
import com.v7idea.healthkit.View.BottomButton;
import com.v7idea.template.Activity.BaseActivity;
import com.v7idea.template.AirApplication;
import com.v7idea.template.Tool.ApiResult;
import com.v7idea.template.Tool.NotifyDialog;
import com.v7idea.template.Tool.V7ideaAsyncTask;
import com.v7idea.template.Tool.ViewScaling;
import com.v7idea.template.View.AutoReleaseImageView;
import com.v7idea.template.View.Banner;
import com.v7idea.template.View.SpecialListView;
import com.v7idea.template.View.V7TitleView;

import java.util.ArrayList;
/**
 * 2019/1/24 ＊確認使用的頁面 系統設定-提醒設定
 */
public class ReminderSettings extends BaseActivity {
    private static final String TAG = "ReminderSettings";
    private ReminderListAdapter reminderListAdapter = null;
    private SpecialListView showFunctionList = null;
    int listIndex = 0;
    private NotifyDialog progress = null;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reminder_settings_page);

        Banner banner = (Banner) ViewScaling.findViewByIdAndScale(currentActivity, R.id.ReminderSettings_Banner_Header);
        banner.initShortBanner();
        banner.backIcon.setOnClickListener(OnBackIcon);
        banner.title.setText(R.string.ReminderSettingsPage_Text_Title);
        banner.title.setTextColor(Color.parseColor("#FFFFFF"));

        showFunctionList = (SpecialListView) ViewScaling.findViewByIdAndScale(currentActivity, R.id.ReminderSettings_SpecialListView_FunctionList);

        BottomButton BottomButton = (BottomButton) ViewScaling.findViewByIdAndScale(currentActivity, R.id.ReminderSettings_BottomButton);
        BottomButton.setData(getString(R.string.ReminderSettings_Text_Cancel), getString(R.string.ReminderSettings_Text_SendSave), 100);
        V7TitleView BackPage = BottomButton.getLeftButton();
        BackPage.setOnClickListener(OnBackPage);
        V7TitleView SendData = BottomButton.getRightButton();
        BottomButton.setVisibility(View.GONE);
//        SendData.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                AirApplication.setOnclikcFeedback(v);
//                if (isConnectedNetWork()) {
//                    String noticeKey = reminderListAdapter.NoticeData_List.get(listIndex).getNoticeKey();
//                    String noticeSetting = String.valueOf(reminderListAdapter.NoticeData_List.get(listIndex).getNoticeSetting());
//                    UpDataNoticeSetting upDataNoticeSetting = new UpDataNoticeSetting(null);
//                    upDataNoticeSetting.execute(noticeKey, noticeSetting);
//                }
//
//            }
//        });
        DownLoadNoticeSetting downLoadNoticeSetting = new DownLoadNoticeSetting();
        downLoadNoticeSetting.execute();
    }

    View.OnClickListener OnBackIcon = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            AirApplication.setOnclikcFeedback(v);
//            backSaveDialog(MainActivity.class);
            Intent intent = new Intent(currentActivity, SystemSettingsPage.class);
            startActivity(intent);
            finish();
            setBackInPrePageAnimation(currentActivity);
        }
    };

    View.OnClickListener OnBackPage = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            AirApplication.setOnclikcFeedback(v);
//            backSaveDialog(SystemSettingsPage.class);
        }
    };


    private class DownLoadNoticeSetting extends V7ideaAsyncTask<String, ApiResult> {

        private NoticeSetting noticeSetting = null;
        private NoticeData noticeData = null;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            noticeData = new NoticeData();
            noticeSetting = new NoticeSetting();
            if (progress == null) {
                progress = new NotifyDialog(currentActivity);
                progress.settingProgressDialog();
            }
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
            if (result.getData() != null && result.getDataJSONArray().length() > 0) {
                reminderListAdapter = new ReminderListAdapter(ReminderSettings.this,
                        noticeData.parseData(result.getData()));
            } else {
                String data = "[{\"noticeKey\":\"dining\",\"noticeName\":\"用餐提示\",\"noticeSetting\":1},{\"noticeKey\":\"drinkingWater\",\"noticeName\":\"飲水/排尿提示\",\"noticeSetting\":0},{\"noticeKey\":\"sittingPosition\",\"noticeName\":\"坐姿提示\",\"noticeSetting\":1},{\"noticeKey\":\"eyeCare\",\"noticeName\":\"眼部保健提示\",\"noticeSetting\":1},{\"noticeKey\":\"defecation\",\"noticeName\":\"排便提示\",\"noticeSetting\":1},{\"noticeKey\":\"sleeping\",\"noticeName\":\"睡眠提示\",\"noticeSetting\":0},{\"noticeKey\":\"pressure\",\"noticeName\":\"壓力提示\",\"noticeSetting\":0}]";
                reminderListAdapter = new ReminderListAdapter(ReminderSettings.this,
                        noticeData.parseData(data));
            }

            showFunctionList.setAdapter(reminderListAdapter);
        }

        @Override
        public void downLoadFail(ApiResult apiResult) {

            if (apiResult.getErrorNo().contentEquals(Constant.ErrorCode.CODE_500)) {
                showErrorAlert(getResources().getString(R.string.no_internet_error500));
                Intent intent = new Intent(currentActivity, SystemSettingsPage.class);
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
                Intent intent = new Intent(currentActivity, SystemSettingsPage.class);
                startActivity(intent);
                finish();
                setBackInPrePageAnimation(currentActivity);
            }
        }

        @Override
        protected ApiResult doInBackground(String... params) {

            Token token = new Token();
            return noticeSetting.getNoticeSetting(token.getTokenId());
        }

    }

//    private class UpDataNoticeSetting extends V7ideaAsyncTask<String, ApiResult> {
//
//        private NoticeSetting noticeSetting = null;
//        private NoticeData noticeData = null;
//        private Intent intent = null;
//        Class<?> gotoPage = null;
//
//        public UpDataNoticeSetting(Class<?> gotoPage) {
//            this.gotoPage = gotoPage;
//            if (gotoPage == null) {
//                intent = new Intent(currentActivity, MainActivity.class);
//            } else {
//                intent = new Intent(currentActivity, gotoPage);
//            }
//        }
//
//        @Override
//        protected void onPreExecute() {
//            super.onPreExecute();
//            noticeData = new NoticeData();
//            noticeSetting = new NoticeSetting();
//            if (progress == null) {
//                progress = new NotifyDialog(currentActivity);
//                progress.settingProgressDialog();
//            } else {
//                progress.settingProgressDialog();
//            }
//        }
//
//        @Override
//        public void ifNeedCloseSomeThing() {
//
//        }
//
//        @Override
//        public void downLoadSuccess(ApiResult result) {
//            listIndex = listIndex + 1;
//            if (!(listIndex > reminderListAdapter.getCount() - 1)) {
//                Log.e(TAG, "A listIndex = " + listIndex);
//                String noticeKey = reminderListAdapter.NoticeData_List.get(listIndex).getNoticeKey();
//                String noticeSetting = String.valueOf(reminderListAdapter.NoticeData_List.get(listIndex).getNoticeSetting());
//                Log.d(TAG, "noticeKey() : " + noticeKey);
//                Log.d(TAG, "noticeSetting() : " + noticeSetting);
//
//                UpDataNoticeSetting upDataNoticeSetting = new UpDataNoticeSetting(gotoPage);
//                upDataNoticeSetting.execute(noticeKey, noticeSetting);
//            } else {
//
//                if (progress != null) {
//                    progress.dismiss();
//                    progress = null;
//                }
//                listIndex = 0;
//
//                showErrorAlert("修改成功", new DialogInterface.OnClickListener() {
//                    @Override
//                    public void onClick(DialogInterface dialog, int which) {
//                        startActivity(intent);
//                        finish();
//                        setBackInPrePageAnimation(currentActivity);
//                    }
//                });
//            }
//
//        }
//
//        @Override
//        public void downLoadFail(ApiResult apiResult) {
//
//            if (apiResult.getErrorNo().contentEquals(Constant.ErrorCode.CODE_500)) {
//                showErrorAlert(getResources().getString(R.string.no_internet_error500));
//                Log.e(TAG, "downLoadFail = " + apiResult.toString());
//
//
//            } else if (apiResult.getErrorNo().contentEquals(Constant.ErrorCode.CODE_1002)) {
//                showErrorAlert(apiResult.getMessage(), new DialogInterface.OnClickListener() {
//                    @Override
//                    public void onClick(DialogInterface dialog, int which) {
//                        new Token().destroy();
//                        Intent intent = new Intent(currentActivity, LoginPage.class);
//                        startActivity(intent);
//                        finish();
//                        setBackInPrePageAnimation(currentActivity);
//
//                    }
//                });
//            } else if (apiResult.getErrorNo().contentEquals(Constant.ErrorCode.CODE_1003)) {
//                showErrorAlert(apiResult.getMessage(), new DialogInterface.OnClickListener() {
//                    @Override
//                    public void onClick(DialogInterface dialog, int which) {
//                        new Token().destroy();
//                        Intent intent = new Intent(currentActivity, LoginPage.class);
//                        startActivity(intent);
//                        finish();
//                        setBackInPrePageAnimation(currentActivity);
//                    }
//                });
//            } else {
//                showErrorAlert(apiResult.getMessage());
//            }
//        }
//
//        @Override
//        protected ApiResult doInBackground(String... params) {
//
//            Token token = new Token();
//            return noticeSetting.getEditNoticeSetting(token.getTokenId(), params[0], params[1]);
//        }
//
//    }

    private class UpDataNoticeSetting extends V7ideaAsyncTask<String, ApiResult> {

        private NoticeSetting noticeSetting = null;
        private NoticeData noticeData = null;


        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            noticeData = new NoticeData();
            noticeSetting = new NoticeSetting();
            if (progress == null) {
                progress = new NotifyDialog(currentActivity);
                progress.settingProgressDialog();
            } else {
                progress.settingProgressDialog();
            }
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

//            AlertDialog.Builder alertBuilder = new AlertDialog.Builder(currentActivity);
//            alertBuilder.setMessage("修改成功");
//            alertBuilder.setPositiveButton(R.string.YES, null);
//            alertBuilder.setCancelable(false);
//            alertBuilder.show();
        }

        @Override
        public void downLoadFail(ApiResult apiResult) {

            if (apiResult.getErrorNo().contentEquals(Constant.ErrorCode.CODE_500)) {
                showErrorAlert(getResources().getString(R.string.no_internet_error500));
                Log.e(TAG, "downLoadFail = " + apiResult.toString());


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
            return noticeSetting.getEditNoticeSetting(token.getTokenId(), params[0], params[1]);
        }

    }

    public class ReminderListAdapter extends BaseAdapter {
        private final Context context;
        private LayoutInflater layoutInflater;
        private ArrayList<NoticeData> NoticeData_List;
        private ListView listView = null;

        public ReminderListAdapter(Context context, ArrayList<NoticeData> mNoticeDataList) {
            this.context = context;
            layoutInflater = LayoutInflater.from(context);
            this.NoticeData_List = mNoticeDataList;
        }

        public void addData(NoticeData mNoticeData) {
            if (!NoticeData_List.contains(mNoticeData)) {
                NoticeData_List.add(mNoticeData);
                notifyDataSetChanged();
            }
        }


        @Override
        public int getCount() {
            if (NoticeData_List == null) {
                return 0;
            }
            return NoticeData_List.size();
        }

        @Override
        public Object getItem(int position) {
            return NoticeData_List.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            ViewHolder holder = null;

            if (convertView == null) {
                convertView = layoutInflater.inflate(R.layout.function_list_item_reminder_settings, null);
                holder = new ViewHolder();
                holder.Container2 = (RelativeLayout) ViewScaling.findViewByIdAndScale(convertView, R.id.ReminderSettings_RelativeLayout_Container);
                holder.IconContainer2 = (RelativeLayout) ViewScaling.findViewByIdAndScale(convertView, R.id.ReminderSettings_RelativeLayout_IconContainer);
                holder.AlertIconContainer2 = (RelativeLayout) ViewScaling.findViewByIdAndScale(convertView, R.id.ReminderSettings_RelativeLayout_AlertIconContainer);
                holder.titleText2 = (V7TitleView) ViewScaling.findViewByIdAndScale(convertView, R.id.ReminderSettings_AutoReleaseImageView_TitleText);
                holder.aSwitch2 = (ImageView) ViewScaling.findViewByIdAndScale(convertView, R.id.ReminderSettings_ImageView_Switch);
                holder.Divider2 = (AutoReleaseImageView) ViewScaling.findViewByIdAndScale(convertView, R.id.ReminderSettings_AutoReleaseImageView_divider);

                convertView.setTag(holder);
            } else {

                holder = (ViewHolder) convertView.getTag();
            }

            holder.titleText2.setText(NoticeData_List.get(position).getNoticeName());

            holder.aSwitch2.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    AirApplication.setOnclikcFeedback(v);
                    if (v.isSelected()) {
                        v.setSelected(false);
                        NoticeData_List.get(position).setNoticeSetting(0);
                    } else {
                        v.setSelected(true);
                        NoticeData_List.get(position).setNoticeSetting(1);
                    }

                    if (isConnectedNetWork()) {
                        String noticeKey = reminderListAdapter.NoticeData_List.get(position).getNoticeKey();
                        String noticeSetting = String.valueOf(reminderListAdapter.NoticeData_List.get(position).getNoticeSetting());
                        UpDataNoticeSetting upDataNoticeSetting = new UpDataNoticeSetting();
                        upDataNoticeSetting.execute(noticeKey, noticeSetting);
                    }
                }
            });

            if (NoticeData_List.get(position).getNoticeSetting() == 1) {
                holder.aSwitch2.setSelected(true);
            } else {
                holder.aSwitch2.setSelected(false);
            }
            return convertView;
        }

        public class ViewHolder {
            public RelativeLayout Container2;
            public RelativeLayout IconContainer2;
            public RelativeLayout AlertIconContainer2;
            public V7TitleView titleText2;
            public ImageView aSwitch2;
            public AutoReleaseImageView Divider2;
        }
    }

    @Override
    public void onBackPressed() {
//        backSaveDialog(SystemSettingsPage.class);
        Intent intent = new Intent(currentActivity, SystemSettingsPage.class);
        startActivity(intent);
        finish();
        setBackInPrePageAnimation(currentActivity);
    }
//
//    public void backSaveDialog(final Class<?> GotoPage) {
//        final AlertDialog.Builder alertBuilder = new AlertDialog.Builder(currentActivity);
//        alertBuilder.setMessage("返回前是否儲存？");
//        alertBuilder.setPositiveButton(R.string.YES, new DialogInterface.OnClickListener() {
//            @Override
//            public void onClick(DialogInterface dialog, int which) {
//                if (isConnectedNetWork()) {
//                    String noticeKey = reminderListAdapter.NoticeData_List.get(listIndex).getNoticeKey();
//                    String noticeSetting = String.valueOf(reminderListAdapter.NoticeData_List.get(listIndex).getNoticeSetting());
//                    UpDataNoticeSetting upDataNoticeSetting = new UpDataNoticeSetting(GotoPage);
//                    upDataNoticeSetting.execute(noticeKey, noticeSetting);
//                } else {
//                    Intent intent = new Intent(currentActivity, GotoPage);
//                    startActivity(intent);
//                    finish();
//                    setBackInPrePageAnimation(currentActivity);
//                }
//            }
//        });
//        alertBuilder.setNegativeButton(R.string.NO, new DialogInterface.OnClickListener() {
//            @Override
//            public void onClick(DialogInterface dialog, int which) {
//                Intent intent = new Intent(currentActivity, GotoPage);
//                startActivity(intent);
//                finish();
//                setBackInPrePageAnimation(currentActivity);
//            }
//        });
//        alertBuilder.setCancelable(false);
//        alertBuilder.show();
//    }
}
