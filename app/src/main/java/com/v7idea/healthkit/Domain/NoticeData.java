package com.v7idea.healthkit.Domain;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class NoticeData {
    ArrayList<NoticeData> noticeDataArrayList = new ArrayList<NoticeData>();

    String NoticeKey = "";
    String NoticeName = "";
    int NoticeSetting = 0;

    public NoticeData(String noticeKey, String noticeName, int noticeSetting) {
        NoticeKey = noticeKey;
        NoticeName = noticeName;
        NoticeSetting = noticeSetting;
    }

    public NoticeData() {
    }

    public String getNoticeKey() {
        return NoticeKey;
    }

    public void setNoticeKey(String noticeKey) {
        NoticeKey = noticeKey;
    }

    public String getNoticeName() {
        return NoticeName;
    }

    public void setNoticeName(String noticeName) {
        NoticeName = noticeName;
    }

    public int getNoticeSetting() {
        return NoticeSetting;
    }

    public void setNoticeSetting(int noticeSetting) {
        NoticeSetting = noticeSetting;
    }

    public ArrayList<NoticeData> parseData(String string) {
        try {

            if (string != null && !string.isEmpty()) {
                JSONArray jsonArray = new JSONArray(string);
                if (jsonArray.length() > 0) {
                    for (int i = 0; i < jsonArray.length(); i++) {
                        String jasonString = jsonArray.optString(i);
                        try {
                            JSONObject jsonObject = new JSONObject(jasonString);
                            String noticeKey = jsonObject.optString("noticeKey");
                            String noticeName = jsonObject.optString("noticeName");
                            int noticeSetting = jsonObject.optInt("noticeSetting");

                            noticeDataArrayList.add(new NoticeData(noticeKey, noticeName, noticeSetting));

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }
                return noticeDataArrayList;
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }

}
