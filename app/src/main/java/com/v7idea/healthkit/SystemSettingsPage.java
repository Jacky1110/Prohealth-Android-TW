package com.v7idea.healthkit;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

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
/**
 * 2019/1/24 ＊確認使用的頁面 主頁面-系統設定
 */
public class SystemSettingsPage extends BaseActivity implements View.OnClickListener {

    private SettingListAdapter functionListAdapter = null;
    private SettingListAdapter.Holder holder;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_system_settings_page);

        Banner banner = (Banner) ViewScaling.findViewByIdAndScale(currentActivity, R.id.SystemSettingPage_Banner_Header);
        banner.initShortBanner();
        banner.backIcon.setOnClickListener(this);
        banner.title.setText(R.string.SystemSettingsPage_Text_Title);
        banner.title.setTextColor(Color.parseColor("#FFFFFF"));

        BottomButton BottomButton = (BottomButton) ViewScaling.findViewByIdAndScale(currentActivity, R.id.SystemSettingPage_BottomButton);
        BottomButton.setData(getResources().getString(R.string.SystemSettingsPage_Text_BackPage), 100);
        V7TitleView BackPage = BottomButton.getOneButton();
        BackPage.setOnClickListener(this);
        BottomButton.setVisibility(View.GONE);

        SpecialListView showFunctionList = (SpecialListView) ViewScaling.findViewByIdAndScale(currentActivity, R.id.SystemSettingPage_SpecialListView_FunctionList);
        showFunctionList.setAdapter(new SettingListAdapter());
        showFunctionList.setOnItemClickListener(onItemClickListener);


        TextView VersionName = (TextView) ViewScaling.findViewByIdAndScale(currentActivity, R.id.SystemSettingPage_TextView_VersionName);
        VersionName.setText("版本:" + getAppVersionName(this));
    }

    public String getAppVersionName(Context context) {
        String versionName = "";

        try {
            PackageManager packageManager = context.getPackageManager();
            PackageInfo packageInfo = packageManager.getPackageInfo(context.getPackageName(), 0);
            versionName = packageInfo.versionName;

        } catch (Exception e) {
            e.printStackTrace();
        }

        if (versionName == null || versionName.length() <= 0) {
            versionName = "";
        }

        return versionName;
    }

    @Override
    public void onClick(View view) {
        AirApplication.setOnclikcFeedback(view);
        Animation scaleAnimation = AnimationUtils.loadAnimation(currentActivity, R.anim.normal_button_alaph_animation);
        scaleAnimation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                Intent intent = new Intent(currentActivity, MainActivity.class);
                startActivity(intent);
                finish();
                setBackInPrePageAnimation(currentActivity);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });

        view.setAnimation(scaleAnimation);
        view.startAnimation(scaleAnimation);
    }

    private class SettingListAdapter extends BaseAdapter {

        private class Holder {
            public RelativeLayout Container;
            public RelativeLayout IconContainer;
            public RelativeLayout AlertIconContainer;
            public V7TitleView titleText;
            public ImageView next;
            public AutoReleaseImageView Divider;

            public void init(View convertView) {
                Container = (RelativeLayout) ViewScaling.findViewByIdAndScale(convertView, R.id.SystemSettingPage_RelativeLayout_Container);
                IconContainer = (RelativeLayout) ViewScaling.findViewByIdAndScale(convertView, R.id.SystemSettingPage_RelativeLayout_IconContainer);
                AlertIconContainer = (RelativeLayout) ViewScaling.findViewByIdAndScale(convertView, R.id.SystemSettingPage_RelativeLayout_AlertIconContainer);
                titleText = (V7TitleView) ViewScaling.findViewByIdAndScale(convertView, R.id.SystemSettingPage_AutoReleaseImageView_TitleText);
                next = (ImageView) ViewScaling.findViewByIdAndScale(convertView, R.id.SystemSettingPage_AutoReleaseImageView_AlertIcon);
                Divider = (AutoReleaseImageView) ViewScaling.findViewByIdAndScale(convertView, R.id.SystemSettingPage_AutoReleaseImageView_divider);
            }
        }

        //取得這listView有多少列
        @Override
        public int getCount() {
            return 9;
        }

        //取得某一列的內容
        @Override
        public Object getItem(int position) {
            return null;
        }

        //取得某一列的id
        @Override
        public long getItemId(int position) {
            return 0;
        }

        //每一列都可以看成一個View
        //修改某一列的View得內容
        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View v = convertView;

            if (v == null) {
                v = LayoutInflater.from(getApplicationContext()).inflate(R.layout.function_list_item_system_settings, null);
                holder = new Holder();
                holder.init(v);

                v.setTag(holder);
            } else {
                holder = (Holder) v.getTag();
            }
            switch (position) {
                case 0:
                    holder.titleText.setText("用戶基本資料");
                    break;
                case 1:
                    holder.titleText.setText("修改密碼");
                    break;
                case 2:
                    holder.titleText.setText("提醒設定");
                    break;
//                case 3:
//                    holder.titleText.setText("綁定社群");
//                    break;
                case 3:
                    holder.titleText.setText("健康快篩服務說明");
                    break;
                case 4:
                    holder.titleText.setText("NIR+配對操作說明");
                    break;
                case 5:
                    holder.titleText.setText("立即改善説明");
                    break;
                case 6:
                    holder.titleText.setText("健康報告說明");
                    break;
                case 7:
                    holder.titleText.setText("常見問題");
                    break;
                case 8:
                    holder.titleText.setText("登出");
                    break;
            }

            return v;
        }
    }

    private AdapterView.OnItemClickListener onItemClickListener = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            AirApplication.setOnclikcFeedback(view);
            if (position == 0) {
                //用戶基本資料
                Intent intent = new Intent(currentActivity, UserBasicInformation.class);
                startActivity(intent);
                finish();
                setTurnInNextPageAnimation(currentActivity);

            } else if (position == 1) {
                //修改密碼
                Intent intent = new Intent(currentActivity, EditPasswordPage.class);
                startActivity(intent);
                finish();
                setTurnInNextPageAnimation(currentActivity);
            } else if (position == 2) {
                //提醒設定
                if (isConnectedNetWork()) {
                    Intent intent = new Intent(currentActivity, ReminderSettings.class);
                    startActivity(intent);
                    finish();
                    setTurnInNextPageAnimation(currentActivity);
                }
            }
//            else if (position == 3) {
//                //綁定社群
//                Intent intent = new Intent(currentActivity, CommunityBlindingPage.class);
//                startActivity(intent);
//                finish();
//                setTurnInNextPageAnimation(currentActivity);
//            }
            else if (position == 3) {
                //健康快篩服務說明
                Intent intent = new Intent(currentActivity, EntryPage2.class);
                intent.putExtra("SystemSettingsPage", "EntryPage2");
                startActivity(intent);
                finish();
                setTurnInNextPageAnimation(currentActivity);
            } else if (position == 4) {
                //NIR+配對操作說明
                Intent intent = new Intent(currentActivity, PrepareConnectPage1.class);
                intent.putExtra("SystemSettingsPage", "PrepareConnectPage1");
                startActivity(intent);
                finish();
                setTurnInNextPageAnimation(currentActivity);
            } else if (position == 5) {
                //立即改善說明
                Intent intent = new Intent(currentActivity, HealthDescriptionPage1.class);
                intent.putExtra("SystemSettingsPage", "HealthDescriptionPage1");
                startActivity(intent);
                finish();
                setTurnInNextPageAnimation(currentActivity);
            } else if (position == 6) {
                //健康快篩報告說明
                Intent intent = new Intent(currentActivity, ReportDescriptionPage1.class);
                intent.putExtra("SystemSettingsPage", "ReportDescriptionPage1");
                startActivity(intent);
                finish();
                setTurnInNextPageAnimation(currentActivity);
            } else if (position == 7) {
                //常見問題
                Intent intent = new Intent();
                intent.setAction(Intent.ACTION_VIEW);
                intent.setData(Uri.parse("http://www.prohealthbio.com/post/qa"));
                startActivity(intent);

            } else if (position == 8) {
                //登出
                if (isConnectedNetWork()) {
                    Token token = new Token();
                    if (!"".equals(token.getTokenId())) {
                        showErrorAlert(getString(R.string.if_logout), new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                SystemSettingsPage.LogoutTask logoutTask = new LogoutTask();
                                logoutTask.execute();
                            }
                        }, null, true);
                    }
                }
            }
        }
    };

    private class LogoutTask extends V7ideaAsyncTask<String, ApiResult> {
        private NotifyDialog progress = null;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            progress = new NotifyDialog(currentActivity);
            progress.settingProgressDialog();
        }

        @Override
        public void downLoadSuccess(ApiResult result) {
            Token token = new Token();
            token.destroy();

            Intent intent = new Intent(currentActivity, LoginPage.class);
            startActivity(intent);
            finish();
            setBackInPrePageAnimation(currentActivity);
        }

        @Override
        public void downLoadFail(ApiResult apiResult) {
            if (apiResult.getErrorNo().contentEquals(Constant.ErrorCode.CODE_500)) {
                showErrorAlert(getResources().getString(R.string.no_internet_error500));
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
        public void ifNeedCloseSomeThing() {
            if (progress != null) {
                progress.dismiss();
                progress = null;
            }
        }

        @Override
        protected ApiResult doInBackground(String... params) {
            Token token = new Token();
            return token.deleteTokenID();
        }

    }

    @Override
    public void onBackPressed() {
//        super.onBackPressed();
        Intent intent = new Intent(currentActivity, MainActivity.class);
        startActivity(intent);
        finish();
        setBackInPrePageAnimation(currentActivity);
    }
}
