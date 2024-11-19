package com.v7idea.healthkit.Model;

import com.v7idea.healthkit.Constant;
import com.v7idea.healthkit.R;
import com.v7idea.template.DataBase.SimpleDatabase;
import com.v7idea.template.Tool.ApiResult;
import com.v7idea.template.Tool.ListItem;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

/**
 * Created by mortal on 2017/9/27.
 */

public class FunctionList {
    private final static String TAG = "FunctionList";
    private final static String FUNCTION_LIST = "FunctionList";

    private SimpleDatabase mDataBase = null;

    public FunctionList() {
        mDataBase = new SimpleDatabase(TAG);
    }

    public void clear() {
        mDataBase.clearDataBase();
    }

    private final String DummyData = "[{\"id\":\"假FunctionID 1\",\"functionCode\":\"detection\",\"name\":\"進行量測\",\"smallPhoto\":\"\",\"serialNo\":\"1\",\"displayItemCount\":\"0\"}" +
            ",{\"id\":\"假FunctionID 2\",\"functionCode\":\"improvement\",\"name\":\"立即改善\",\"smallPhoto\":\"\",\"serialNo\":\"2\",\"displayItemCount\":\"0\"}" +
            ",{\"id\":\"假FunctionID 3\",\"functionCode\":\"report\",\"name\":\"健康報告\",\"smallPhoto\":\"\",\"serialNo\":\"3\",\"displayItemCount\":\"0\"}" +
            ",{\"id\":\"假FunctionID 6\",\"functionCode\":\"suggest\",\"name\":\"生活建議\",\"smallPhoto\":\"\",\"serialNo\":\"4\",\"displayItemCount\":\"0\"}" +
            ",{\"id\":\"假FunctionID 4\",\"functionCode\":\"feedBack\",\"name\":\"我要反饋\",\"smallPhoto\":\"\",\"serialNo\":\"5\",\"displayItemCount\":\"0\"}" +
            ",{\"id\":\"假FunctionID 5\",\"functionCode\":\"setting\",\"name\":\"系統設定\",\"smallPhoto\":\"\",\"serialNo\":\"6\",\"displayItemCount\":\"0\"}]";

    public ArrayList<ListItem> getFunctionArray() {
        try {
            JSONArray dataArray = new JSONArray(DummyData);

            ArrayList<ListItem> itemArrayList = new ArrayList<ListItem>();

            for (int i = 0; i < dataArray.length(); i++) {
                JSONObject data = dataArray.optJSONObject(i);

                Function function = new Function(data);
                itemArrayList.add(function);
            }

            sortFunctionList(itemArrayList);

            return itemArrayList;

        } catch (JSONException e) {
            e.printStackTrace();

            return null;
        }
    }

    public ApiResult getFunctionList(String strTokenID) {

        return new ApiResult(Constant.ErrorCode.SUCCESS, "", DummyData);

//        String strApi = APIFetch.getFunctionListApi();
//
//        DownLoad downLoad = new DownLoad();
//
//        V7HttpResult httpResult = downLoad.getStringFromURLAddHeader(strApi, strTokenID);
//
//        DebugLog.d(TAG, "httpResult code: " + httpResult.getResponseCode());
//        DebugLog.d(TAG, "httpResult result: " + httpResult.getResultString());
//
//        if(httpResult.getResponseCode() == 200){
//            String resultString = httpResult.getResultString();
//
//            ApiResult apiResult = ApiResult.getInstance(resultString);
//
//            JSONObject dataObject = apiResult.getDataJSONObject();
//
//            parseData(dataObject);
//
//            return apiResult;
//        }
//
//        return new ApiResult("" + httpResult.getResponseCode(), httpResult.getResultString(), null);
    }

    private void sortFunctionList(ArrayList<ListItem> itemArrayList) {
        Collections.sort(itemArrayList, new Comparator<ListItem>() {
            @Override
            public int compare(ListItem lhs, ListItem rhs) {

                int lhsSerialNo = lhs.getSerialNo();
                int rhsSerialNo = rhs.getSerialNo();

                if (lhsSerialNo == rhsSerialNo) {
                    return 0;
                } else {
                    if (lhsSerialNo < rhsSerialNo) {
                        return -1;
                    } else {
                        return 1;
                    }
                }
            }
        });
    }

    public class Function implements ListItem {

        private String id = null;
        private String functionCode = null;
        private String name = null;
        private String serialNo = null;
        private String smallPhoto = null;
        private String displayItemCount = null;

        private int iconID = -1;

        public Function(JSONObject functionData) {
            this.id = functionData.optString("id");
            this.functionCode = functionData.optString("functionCode");
            this.name = functionData.optString("name");
            this.serialNo = functionData.optString("serialNo", "-1");
            this.smallPhoto = functionData.optString("smallPhoto");
            this.displayItemCount = functionData.optString("displayItemCount", "0");
        }

        public Function(JSONObject functionData, int iconID) {
            this(functionData);
            this.iconID = iconID;
        }

        @Override
        public String getItemID() {
            return id;
        }

        @Override
        public int getIconResourceID() {

            int serialNo = getSerialNo();

            switch (serialNo) {
                case 1:
                    return R.mipmap.page_main_icon_1;

                case 2:
                    return R.mipmap.page_main_icon_2;

                case 3:
                    return R.mipmap.page_main_icon_3;

                case 4:
                    return R.mipmap.page_main_icon_4;

                case 5:
                    return R.mipmap.page_main_icon_5;

                case 6:
                    return R.mipmap.page_main_icon_6;

                default:
                    return -1;
            }
        }

        @Override
        public String getImagePath() {
            return smallPhoto;
        }

        @Override
        public String getTitle() {
            return name;
        }

        @Override
        public String getSubTitle() {
            return "";
        }

        @Override
        public String getCode() {
            return functionCode;
        }

        @Override
        public int getSerialNo() {
            try {
                return Integer.valueOf(serialNo);
            } catch (Exception e) {
                return -1;
            }
        }

        @Override
        public int getNoticeNumber() {
            return Integer.valueOf(displayItemCount);
        }
    }
}
