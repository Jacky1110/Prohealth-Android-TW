package com.v7idea.healthkit;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Rect;
import android.os.Bundle;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.v7idea.healthkit.Domain.TypeData;
import com.v7idea.healthkit.Model.Feedback;
import com.v7idea.healthkit.Model.Token;
import com.v7idea.healthkit.View.BottomButton;
import com.v7idea.template.Activity.BaseActivity;
import com.v7idea.template.AirApplication;
import com.v7idea.template.Tool.ApiResult;
import com.v7idea.template.Tool.NotifyDialog;
import com.v7idea.template.Tool.V7ideaAsyncTask;
import com.v7idea.template.Tool.ViewScaling;
import com.v7idea.template.View.Banner;
import com.v7idea.template.View.V7TitleView;

import java.util.ArrayList;
/**
 * 2019/1/24 ＊確認使用的頁面 主頁面-我要反饋
 */
public class FeedbackPage extends BaseActivity {
    private static final String TAG = "FeedbackPage";

    private RecyclerView RecyclerViewType;
    private EditText EditText_Data;
    private V7TitleView V7TitleView_Cancel;
    private V7TitleView V7TitleView_Determine;
    TypeAdapter mTypeAdapter = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feedback_page);

        Banner banner = (Banner) ViewScaling.findViewByIdAndScale(currentActivity, R.id.FeedbackPage_Banner_Header);
        banner.initShortBanner();
        banner.backIcon.setOnClickListener(OnBackIconClick);
        banner.title.setText(getResources().getString(R.string.FeedbackPage_Text_Title));

        banner.title.setTextColor(Color.parseColor("#FFFFFF"));

        ScrollView scrollArea = (ScrollView) ViewScaling.findViewByIdAndScale(currentActivity, R.id.FeedbackPage_ScrollView);

        TextView Title = (TextView) ViewScaling.findViewByIdAndScale(currentActivity, R.id.FeedbackPage_TextView_Title);
        TextView TypeTitle = (TextView) ViewScaling.findViewByIdAndScale(currentActivity, R.id.FeedbackPage_TextView_TypeTitle);
        RecyclerViewType = (RecyclerView) ViewScaling.findViewByIdAndScale(currentActivity, R.id.FeedbackPage_RecyclerView_Type);

        TextView DataTitle = (TextView) ViewScaling.findViewByIdAndScale(currentActivity, R.id.FeedbackPage_TextView_DataTitle);
        EditText_Data = (EditText) ViewScaling.findViewByIdAndScale(currentActivity, R.id.FeedbackPage_EditText_Data);


        BottomButton ButtonContainer = (BottomButton) ViewScaling.findViewByIdAndScale(currentActivity, R.id.FeedbackPage_BottomButton);
        ButtonContainer.setData(getResources().getString(R.string.FeedbackPage_Text_Cancel), getResources().getString(R.string.FeedbackPage_Text_Send), 100);
        V7TitleView_Cancel = ButtonContainer.getLeftButton();
        V7TitleView_Cancel.setOnClickListener(OnBackIconClick);
        V7TitleView_Determine = ButtonContainer.getRightButton();
        V7TitleView_Determine.setOnClickListener(OnSendDataClick);
        //https://www.jianshu.com/p/4b80381e6e80
        GridLayoutManager gridLayoutManager = new GridLayoutManager(this, 3, GridLayoutManager.VERTICAL, false);
        RecyclerViewType.setLayoutManager(gridLayoutManager);

        DownLoadFeedbackType downLoadFeedbackType = new DownLoadFeedbackType();
        downLoadFeedbackType.execute();


        //RecyclerView網格的間隔距離
        int screenWidth = ViewScaling.getScreenWidth();
        int itemWidth = (int) (210 * ViewScaling.getScaleValue());//item 寬度
        int padding = (int) (27 * ViewScaling.getScaleValue()) * 2;//左右內縮
        RecyclerViewType.addItemDecoration(new SpaceItemDecoration(((screenWidth - itemWidth * 3) - padding) / 6));
    }

    View.OnClickListener OnSendDataClick = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            AirApplication.setOnclikcFeedback(v);
            String string = EditText_Data.getText().toString();
            if (isConnectedNetWork()) {
                if (mTypeAdapter.typeID.isEmpty()) {
                    showErrorAlert(getResources().getString(R.string.FeedbackPage_ERROR_DATA_TYPE_EMPTY));
                } else if (string.isEmpty() || string.length() == 0) {
                    showErrorAlert(getResources().getString(R.string.FeedbackPage_ERROR_DATA_EMPTY));
                } else if (string.length() > 500) {
                    showErrorAlert(getResources().getString(R.string.FeedbackPage_ERROR_DATA_FORMAT));
                } else {
                    UpDataFeedback upDataFeedback = new UpDataFeedback();
                    upDataFeedback.execute(mTypeAdapter.typeID, string);
                }
            }
        }
    };

    View.OnClickListener OnBackIconClick = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            AirApplication.setOnclikcFeedback(v);
            Intent intent = new Intent(currentActivity, MainActivity.class);
            startActivity(intent);
            finish();
            setBackInPrePageAnimation(currentActivity);
        }
    };

    public static class TypeAdapter extends RecyclerView.Adapter<TypeAdapter.ViewHolder> {
        private final Context context;
        private LayoutInflater layoutInflater;
        private ArrayList<TypeData> TypeData_List;
        private int selectedPosition = -1;
        public String typeID = "";
        public String typeName = "";

        public TypeAdapter(Context context, ArrayList<TypeData> mTypeData_List) {
            this.context = context;
            this.TypeData_List = mTypeData_List;
            layoutInflater = LayoutInflater.from(context);
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View v = layoutInflater.inflate(R.layout.recycleview_item_feedback_type, parent, false);

            TypeAdapter.ViewHolder viewHolder = new TypeAdapter.ViewHolder(v);
            ViewScaling.setScaleByRecyclerView(v);

            return viewHolder;
        }

        @Override
        public void onBindViewHolder(TypeAdapter.ViewHolder holder, final int position) {


            holder.tv_typeData.setText(TypeData_List.get(position).getTypeName());
            holder.tv_typeData.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    AirApplication.setOnclikcFeedback(v);
                    selectedPosition = position;
                    typeID = TypeData_List.get(position).getTypeID();
                    typeName = TypeData_List.get(position).getTypeName();
                    notifyDataSetChanged();
                }
            });

            if (selectedPosition == position) {
                holder.tv_typeData.setSelected(true);
            } else {
                holder.tv_typeData.setSelected(false);
            }
        }


        public void addData(TypeData data) {
            if (TypeData_List != null) {
                TypeData_List.add(data);
                notifyDataSetChanged();
            }
        }

        public void addData(int position, TypeData data) {
            if (TypeData_List != null) {
                TypeData_List.add(data);
                notifyItemInserted(position);
            }
        }

        @Override
        public int getItemCount() {
            if (TypeData_List != null
                    && TypeData_List.size() > 0) {
                return TypeData_List.size();
            }
            return 0;
        }

        public class ViewHolder extends RecyclerView.ViewHolder {
            private View view;
            TextView tv_typeData;


            public ViewHolder(View itemView) {
                super(itemView);
                view = itemView;
                tv_typeData = (TextView) ViewScaling.findViewByIdAndScale(view, R.id.FeedbackPage_RecyclerView_TextView_TypeData);
            }
        }


    }

    public class SpaceItemDecoration extends RecyclerView.ItemDecoration {
        private int space;  //位移間隔距離

        public SpaceItemDecoration(int space) {
            this.space = space;
        }

        @Override
        public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
            if (parent.getChildAdapterPosition(view) % 3 == 0) {
                outRect.left = 0; //第一列左邊貼邊
            } else {
                if (parent.getChildAdapterPosition(view) % 3 == 1) {
                    outRect.left = space;//第二列移動一個位移間距
                } else {
                    outRect.left = space * 2;//由於第二列已經移動了一個間距，所以第三列要移動兩個位移間距就能右邊貼邊，且項目間距相等
                }
            }

            if (parent.getChildAdapterPosition(view) >= 3) {
                outRect.top = 10;
            } else {
                outRect.top = 0;
            }
        }

    }

    private class DownLoadFeedbackType extends V7ideaAsyncTask<String, ApiResult> {

        private NotifyDialog progress = null;
        private Feedback feedback = null;
        private TypeData typeData = null;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            feedback = new Feedback();
            typeData = new TypeData();
            progress = new NotifyDialog(currentActivity);
            progress.settingProgressDialog();
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
            mTypeAdapter = new TypeAdapter(FeedbackPage.this, typeData.parseData(result.getData()));
            RecyclerViewType.setAdapter(mTypeAdapter);

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
            return feedback.getFeedbackType(token.getTokenId());
        }

    }

    private class UpDataFeedback extends V7ideaAsyncTask<String, ApiResult> {

        private NotifyDialog progress = null;
        private Feedback feedback = null;


        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            feedback = new Feedback();
            progress = new NotifyDialog(currentActivity);
            progress.settingProgressDialog();
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

            showErrorAlert("上傳成功", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    Intent intent = new Intent(currentActivity, MainActivity.class);
                    startActivity(intent);
                    finish();
                    setBackInPrePageAnimation(currentActivity);
                }
            });

        }

        @Override
        public void downLoadFail(ApiResult apiResult) {

            if (apiResult.getErrorNo().contentEquals(Constant.ErrorCode.CODE_500)) {
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
            return feedback.getFeedbackData(token.getTokenId(), params[0], params[1]);
        }

    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(currentActivity, MainActivity.class);
        startActivity(intent);
        finish();
        setBackInPrePageAnimation(currentActivity);
    }

}
