package com.v7idea.healthkit.Domain;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class LifeNoticeData {
    ArrayList<LifeNoticeData> lifeNoticeDataArrayList = new ArrayList<LifeNoticeData>();

    private String noticeID;
    private String title;
    private String message;
    private String date;
    private String ifRead;
    private String functionCode;
    private String target;

    public LifeNoticeData(String noticeID, String title, String message, String date, String ifRead, String functionCode, String target) {
        this.noticeID = noticeID;
        this.title = title;
        this.message = message;
        this.date = date;
        this.ifRead = ifRead;
        this.functionCode = functionCode;
        this.target = target;
    }

    public LifeNoticeData() {
    }

    public String getNoticeID() {
        return noticeID;
    }

    public void setNoticeID(String noticeID) {
        this.noticeID = noticeID;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getIfRead() {
        return ifRead;
    }

    public void setIfRead(String ifRead) {
        this.ifRead = ifRead;
    }

    public String getFunctionCode() {
        return functionCode;
    }

    public void setFunctionCode(String functionCode) {
        this.functionCode = functionCode;
    }

    public String getTarget() {
        return target;
    }

    public void setTarget(String target) {
        this.target = target;
    }

    @Override
    public String toString() {
        return "LifeNoticeData{" +
                "lifeNoticeDataArrayList=" + lifeNoticeDataArrayList +
                ", noticeID='" + noticeID + '\'' +
                ", title='" + title + '\'' +
                ", message='" + message + '\'' +
                ", date='" + date + '\'' +
                ", ifRead=" + ifRead +
                ", functionCode='" + functionCode + '\'' +
                ", target='" + target + '\'' +
                '}';
    }

    public ArrayList<LifeNoticeData> parseData(String string) {
        try {

            if (string != null && !string.isEmpty()) {
                JSONArray jsonArray = new JSONArray(string);
                if (jsonArray.length() > 0) {
                    for (int i = 0; i < jsonArray.length(); i++) {
                        String jasonString = jsonArray.optString(i);
                        try {
                            JSONObject jsonObject = new JSONObject(jasonString);

                            String noticeID = jsonObject.optString("NoticeID");
                            String title = jsonObject.optString("Title");
                            String message = jsonObject.optString("Message");
                            String date = jsonObject.optString("Date");
                            String ifRead = jsonObject.optString("IfRead");
                            String functionCode = jsonObject.optString("FunctionCode");
                            String target = jsonObject.optString("Target");

                            lifeNoticeDataArrayList.add(new LifeNoticeData(noticeID, title, message, date, ifRead, functionCode, target));
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }
                return lifeNoticeDataArrayList;
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }
}
