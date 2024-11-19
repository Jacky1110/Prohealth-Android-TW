package com.v7idea.healthkit;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.v7idea.healthkit.Model.Token;
import com.v7idea.template.Activity.BaseActivity;
import com.v7idea.template.AirApplication;
import com.v7idea.template.DataBase.SimpleDatabase;
import com.v7idea.template.Tool.ViewScaling;
import com.v7idea.template.View.AutoReleaseImageView;

import java.util.ArrayList;

/**
 * 2019/1/24 ＊確認使用的頁面 系統設定-健康快篩服務說明（前導頁）
 */
public class EntryPage2 extends BaseActivity
{
    private final static String TAG = "EntryPage2";
    private String IntentString = "";
    private boolean IntentData = false;
    boolean isFUNCTION_TYPE_LIFE = false;
    boolean isFUNCTION_TYPE_PACIENT = false;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_entry_page2);
        if (getIntent() != null)
        {
            if (getIntent().getExtras() != null)
            {
                IntentString = getIntent().getExtras().getString("SystemSettingsPage");
                if (IntentString != null)
                {
                    if (IntentString.isEmpty() == false & IntentString.contentEquals("EntryPage2"))
                    {
                        IntentData = true;
                    }
                }
                isFUNCTION_TYPE_LIFE = getIntent().getExtras().getBoolean(Constant.NOTICE_FUNCTION_TYPE_LIFE, false);
                isFUNCTION_TYPE_PACIENT = getIntent().getExtras().getBoolean(Constant.NOTICE_FUNCTION_TYPE_PACIENT, false);
            }
        }

        Drawable drawable = null;
        ArrayList<EntryData> entryDataArrayList = new ArrayList<EntryData>();

        drawable = getResources().getDrawable(R.mipmap.entry_info_0);
        entryDataArrayList.add(new EntryData(drawable, "輕鬆量測", "請開啟手機藍牙功能與NIR+連結，將手機與量測儀器NIR+配對，並將食指及無名指全程按壓NIR+內側上下鈕; 僅需約50秒即可完成量測。"));
        drawable = getResources().getDrawable(R.mipmap.entry_info_1);
        entryDataArrayList.add(new EntryData(drawable, "數據分析", "量測結束後，請按下【上傳】鍵將資料傳回數據中心分析，約數分鐘後即可完成全身健康快篩報告，並回傳至手機。"));
        drawable = getResources().getDrawable(R.mipmap.entry_info_2);
        entryDataArrayList.add(new EntryData(drawable, "立即改善", "點選主選單的「立即改善」項目，您可快速瀏覽依據您健康快篩的結果、綜合歸納的生活建議、及適合您的健康促進方案。"));
        drawable = getResources().getDrawable(R.mipmap.entry_info_3);
        entryDataArrayList.add(new EntryData(drawable, "健康報告", "點選主選單的「健康報告」項目，您可閱讀歷次報告。並可進一步取得各項保健指標的分析及健康促進建議。"));
        drawable = getResources().getDrawable(R.mipmap.entry_info_4);
        entryDataArrayList.add(new EntryData(drawable, "促進管理", "每隔一週，我們將透過手機的應用程式提醒您再次量測、追蹤健康狀況，以有效達成健康促進。"));
        final ViewPager ViewPager = (ViewPager) ViewScaling.findViewByIdAndScale(currentActivity, R.id.entryPage2_ViewPager);
        final EntryAdapter entryAdapter = new EntryAdapter(this, ViewPager, entryDataArrayList);
        ViewPager.setAdapter(entryAdapter);

        RelativeLayout RelativeLayout_Container = (RelativeLayout) ViewScaling.findViewByIdAndScale(currentActivity, R.id.entryPage2_RelativeLayout_Container);
        final TextView textView_NextPage = (TextView) ViewScaling.findViewByIdAndScale(currentActivity, R.id.entryPage2_TextView_NextPage);
        TextView textView_Skip = (TextView) ViewScaling.findViewByIdAndScale(currentActivity, R.id.entryPage2_TextView_Skip);
        final ImageView ImageView_Ball = (ImageView) ViewScaling.findViewByIdAndScale(currentActivity, R.id.entryPage2_ImageView_Ball);

        ViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener()
        {
            @Override
            public void onPageScrolled(final int position, float positionOffset, int positionOffsetPixels)
            {
                if (position == entryAdapter.getCount() - 1)
                {
                    if (IntentData == true)
                    {
                        textView_NextPage.setText("回系統設定");
                        textView_NextPage.setOnClickListener(new View.OnClickListener()
                        {
                            @Override
                            public void onClick(View v)
                            {
                                AirApplication.setOnclikcFeedback(v);
                                Intent intent = new Intent(currentActivity, SystemSettingsPage.class);
                                startActivity(intent);
                                currentActivity.finish();
                            }
                        });

                    }
                    else if (IntentData == false)
                    {
                        //TODO Token check and update registerID
                        textView_NextPage.setText("開始使用");
                        textView_NextPage.setOnClickListener(new View.OnClickListener()
                        {
                            @Override
                            public void onClick(View v)
                            {
                                AirApplication.setOnclikcFeedback(v);
                                setString(Constant.InitString, String.valueOf(Constant.InitBoolean));

                                Token token = new Token();
                                String tokenID = token.getTokenId();

                                if (tokenID != null && tokenID.isEmpty() == false)
                                {
                                    if (isFUNCTION_TYPE_LIFE || isFUNCTION_TYPE_PACIENT)
                                    {
                                        Intent intent = null;
                                        if (isFUNCTION_TYPE_LIFE)
                                        {
                                            intent = new Intent(currentActivity, LiftSuggestListPage.class);
                                            intent.putExtra(Constant.NOTICE_FUNCTION_TYPE_LIFE, true);
                                        }
                                        else if (isFUNCTION_TYPE_PACIENT)
                                        {
                                            SimpleDatabase simpleDatabase = new SimpleDatabase();
                                            boolean isIntroReport = simpleDatabase.getBooleanValueByKey(Constant.IS_NOT_INTRO_REPORT, Constant.IS_NOT_INTRO_REPORT_DEFAUL);
                                            if (isIntroReport)
                                            {
                                                intent = new Intent(AirApplication.getAppContext(), InspectionReportPage.class);
                                            }
                                            else
                                            {
                                                intent = new Intent(AirApplication.getAppContext(), ReportDescriptionPage1.class);
                                            }
                                            intent.putExtras(getIntent());
                                            intent.putExtra(Constant.NOTICE_FUNCTION_TYPE_DOWNLOAD_DATA, true);
                                            intent.putExtra(Constant.NOTICE_FUNCTION_TYPE_PACIENT, true);
                                        }
                                        startActivity(intent);
                                        currentActivity.finish();
                                    }
                                    else
                                    {
                                        Intent intent = new Intent(currentActivity, MainActivity.class);
                                        startActivity(intent);
                                        currentActivity.finish();
                                    }
                                }
                                else
                                {
                                    Intent intent = new Intent(currentActivity, LoginPage.class);
                                    if (isFUNCTION_TYPE_LIFE || isFUNCTION_TYPE_PACIENT)
                                    {
                                        if (isFUNCTION_TYPE_LIFE)
                                        {
                                            intent = new Intent(currentActivity, LiftSuggestListPage.class);
                                            intent.putExtra(Constant.NOTICE_FUNCTION_TYPE_LIFE, true);
                                        }
                                        else if (isFUNCTION_TYPE_PACIENT)
                                        {
                                            SimpleDatabase simpleDatabase = new SimpleDatabase();
                                            boolean isIntroReport = simpleDatabase.getBooleanValueByKey(Constant.IS_NOT_INTRO_REPORT, Constant.IS_NOT_INTRO_REPORT_DEFAUL);
                                            if (isIntroReport)
                                            {
                                                intent = new Intent(AirApplication.getAppContext(), InspectionReportPage.class);
                                            }
                                            else
                                            {
                                                intent = new Intent(AirApplication.getAppContext(), ReportDescriptionPage1.class);
                                            }
                                            intent.putExtras(getIntent());
                                            intent.putExtra(Constant.NOTICE_FUNCTION_TYPE_DOWNLOAD_DATA, true);
                                            intent.putExtra(Constant.NOTICE_FUNCTION_TYPE_PACIENT, true);
                                        }
                                    }
                                    startActivity(intent);
                                    currentActivity.finish();
                                }
                            }
                        });
                    }
                }
                else
                {
                    textView_NextPage.setText("下一頁");
                    textView_NextPage.setOnClickListener(new View.OnClickListener()
                    {
                        @Override
                        public void onClick(View v)
                        {
                            AirApplication.setOnclikcFeedback(v);
                            ViewPager.setCurrentItem(position + 1);
                        }
                    });
                }

                if (position == 0)
                {
                    ImageView_Ball.setBackgroundResource(R.mipmap.dot_page_1);
                }
                else if (position == 1)
                {
                    ImageView_Ball.setBackgroundResource(R.mipmap.dot_page_2);
                }
                else if (position == 2)
                {
                    ImageView_Ball.setBackgroundResource(R.mipmap.dot_page_3);
                }
                else if (position == 3)
                {
                    ImageView_Ball.setBackgroundResource(R.mipmap.dot_page_4);
                }
                else if (position == 4)
                {
                    ImageView_Ball.setBackgroundResource(R.mipmap.dot_page_5);
                }
            }

            @Override
            public void onPageSelected(int position)
            {

            }

            @Override
            public void onPageScrollStateChanged(int state)
            {

            }
        });

        textView_Skip.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                AirApplication.setOnclikcFeedback(v);
                if (IntentData == true)
                {
                    Intent intent = new Intent(currentActivity, SystemSettingsPage.class);
                    startActivity(intent);
                    currentActivity.finish();
                }
                else if (IntentData == false)
                {
                    setString(Constant.InitString, "");

                    Token token = new Token();
                    String tokenID = token.getTokenId();

                    if (tokenID != null && tokenID.isEmpty() == false)
                    {
                        Intent intent = new Intent(currentActivity, MainActivity.class);
                        if (isFUNCTION_TYPE_LIFE || isFUNCTION_TYPE_PACIENT)
                        {
                            if (isFUNCTION_TYPE_LIFE)
                            {
                                intent = new Intent(currentActivity, LiftSuggestListPage.class);
                                intent.putExtra(Constant.NOTICE_FUNCTION_TYPE_LIFE, true);
                            }
                            else if (isFUNCTION_TYPE_PACIENT)
                            {
                                SimpleDatabase simpleDatabase = new SimpleDatabase();
                                boolean isIntroReport = simpleDatabase.getBooleanValueByKey(Constant.IS_NOT_INTRO_REPORT, Constant.IS_NOT_INTRO_REPORT_DEFAUL);
                                if (isIntroReport)
                                {
                                    intent = new Intent(AirApplication.getAppContext(), InspectionReportPage.class);
                                }
                                else
                                {
                                    intent = new Intent(AirApplication.getAppContext(), ReportDescriptionPage1.class);
                                }
                                intent.putExtras(getIntent());
                                intent.putExtra(Constant.NOTICE_FUNCTION_TYPE_DOWNLOAD_DATA, true);
                                intent.putExtra(Constant.NOTICE_FUNCTION_TYPE_PACIENT, true);
                            }
                        }
                        startActivity(intent);
                        currentActivity.finish();
                    }
                    else
                    {
                        Intent intent = new Intent(currentActivity, LoginPage.class);
                        if (isFUNCTION_TYPE_LIFE || isFUNCTION_TYPE_PACIENT)
                        {
                            if (isFUNCTION_TYPE_LIFE)
                            {
                                intent = new Intent(currentActivity, LiftSuggestListPage.class);
                                intent.putExtra(Constant.NOTICE_FUNCTION_TYPE_LIFE, true);
                            }
                            else if (isFUNCTION_TYPE_PACIENT)
                            {
                                SimpleDatabase simpleDatabase = new SimpleDatabase();
                                boolean isIntroReport = simpleDatabase.getBooleanValueByKey(Constant.IS_NOT_INTRO_REPORT, Constant.IS_NOT_INTRO_REPORT_DEFAUL);
                                if (isIntroReport)
                                {
                                    intent = new Intent(AirApplication.getAppContext(), InspectionReportPage.class);
                                }
                                else
                                {
                                    intent = new Intent(AirApplication.getAppContext(), ReportDescriptionPage1.class);
                                }
                                intent.putExtras(getIntent());
                                intent.putExtra(Constant.NOTICE_FUNCTION_TYPE_DOWNLOAD_DATA, true);
                                intent.putExtra(Constant.NOTICE_FUNCTION_TYPE_PACIENT, true);
                            }
                        }
                        startActivity(intent);
                        currentActivity.finish();
                    }
                }
            }
        });

    }

    public class EntryAdapter extends PagerAdapter
    {
        private final Context context;
        private ArrayList<EntryData> EntryData_List;
        private LayoutInflater layoutInflater;
        ViewPager ViewPager = null;

        public EntryAdapter(Context context, ViewPager viewPager, ArrayList<EntryData> String_List)
        {
            this.context = context;
            this.EntryData_List = String_List;
            layoutInflater = LayoutInflater.from(context);
            ViewPager = viewPager;
        }

        @Override
        public int getCount()
        {
            if (EntryData_List == null)
            {
                return 0;
            }
            return EntryData_List.size();
        }

        @Override
        public boolean isViewFromObject(View view, Object object)
        {
            return view == object;
        }

        @Override
        public Object instantiateItem(ViewGroup container, final int position)
        {
            View view = layoutInflater.inflate(R.layout.activity_entrypage2_view_page, container, false);
            container.addView(view);

            AutoReleaseImageView imageView_Info = (AutoReleaseImageView) ViewScaling.findViewByIdAndScale(view, R.id.entryPage2_AutoReleaseImageView_Info);
            TextView textView_Title = (TextView) ViewScaling.findViewByIdAndScale(view, R.id.entryPage2_TextView_Title);
            TextView textView_Data = (TextView) ViewScaling.findViewByIdAndScale(view, R.id.entryPage2_TextView_Data);

            imageView_Info.setBackground(EntryData_List.get(position).getImageView_Info());
            textView_Title.setText(EntryData_List.get(position).getTitle());
            textView_Data.setText(EntryData_List.get(position).getData());


            return view;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object)
        {
            container.removeView((View) object);

        }
    }

    private class EntryData
    {
        private Drawable ImageView_Info = null;
        private String Title = "";
        private String Data = "";

        public EntryData(Drawable imageView_Info, String title, String data)
        {
            ImageView_Info = imageView_Info;
            Title = title;
            Data = data;
        }

        public Drawable getImageView_Info()
        {
            return ImageView_Info;
        }

        public String getTitle()
        {
            return Title;
        }

        public String getData()
        {
            return Data;
        }
    }

    public void onBackPressed()
    {

    }


    public void setString(String Key, String Value)
    {
        SharedPreferences sharedPreferences = this.getSharedPreferences(Key, MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(Key, Value);
        editor.commit();
    }

    public String getString(String Key)
    {
        SharedPreferences sharedPreferences = this.getSharedPreferences(Key, MODE_PRIVATE);
        String saveString = sharedPreferences.getString(Key, "");
        return saveString;
    }
}
