package com.v7idea.template.Tool;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.CalendarContract;
import android.provider.MediaStore;
import android.text.TextUtils;

/**
 * Created by mortal on 16/1/13.
 */
public class IntentGenerator {

    /**
     * 產生一個增加活動到行事曆的Intent
     *
     * @param longStartTime
     * @param longEndTime
     * @param strEventTitle
     * @param strEventDescription
     * @param strEventLocation
     * @param strMails
     * @return
     */
    public Intent getNewEventToCalenderIntent(long longStartTime, long longEndTime, String strEventTitle, String strEventDescription
            , String strEventLocation, String strMails) {

        //官方範例 2016/01/13
//        Calendar beginTime = Calendar.getInstance();
//        beginTime.set(2012, 0, 19, 7, 30);
//        Calendar endTime = Calendar.getInstance();
//        endTime.set(2012, 0, 19, 8, 30);
//        Intent intent = new Intent(Intent.ACTION_INSERT)
//                .setData(CalendarContract.Events.CONTENT_URI)
//                .putExtra(CalendarContract.EXTRA_EVENT_BEGIN_TIME, beginTime.getTimeInMillis())
//                .putExtra(CalendarContract.EXTRA_EVENT_END_TIME, endTime.getTimeInMillis())
//                .putExtra(CalendarContract.Events.TITLE, "Yoga")
//                .putExtra(CalendarContract.Events.DESCRIPTION, "Group class")
//                .putExtra(CalendarContract.Events.EVENT_LOCATION, "The gym")
//                .putExtra(CalendarContract.Events.AVAILABILITY, CalendarContract.Events.AVAILABILITY_BUSY)
//                .putExtra(Intent.EXTRA_EMAIL, "rowan@example.com,trevor@example.com");

        Intent addEventToCalenderIntent = new Intent(Intent.ACTION_INSERT);
        addEventToCalenderIntent.setData(CalendarContract.Events.CONTENT_URI);

        if (longStartTime > 0) {
            addEventToCalenderIntent.putExtra(CalendarContract.EXTRA_EVENT_BEGIN_TIME, longStartTime);
        }

        if (longEndTime > 0) {
            addEventToCalenderIntent.putExtra(CalendarContract.EXTRA_EVENT_END_TIME, longEndTime);
        }

        if (strEventTitle != null) {
            addEventToCalenderIntent.putExtra(CalendarContract.Events.TITLE, strEventTitle);
        }

        if (strEventDescription != null) {
            addEventToCalenderIntent.putExtra(CalendarContract.Events.DESCRIPTION, strEventDescription);
        }

        if (strEventLocation != null) {
            addEventToCalenderIntent.putExtra(CalendarContract.Events.EVENT_LOCATION, strEventLocation);
        }

        if (strMails != null) {
            addEventToCalenderIntent.putExtra(Intent.EXTRA_EMAIL, strMails);
        }

        return addEventToCalenderIntent;
    }

    /**
     * 產生一個拍照用的Intent
     *
     * @param capturedImageUri
     * @return
     */
    public static Intent getTakePictureIntent(Uri capturedImageUri) {
        Intent i = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
        i.putExtra(MediaStore.EXTRA_OUTPUT, capturedImageUri);

        return i;
    }

    /**
     * 產生一個拍照用的Intent
     *
     * @return
     */
    public static Intent getTakePictureIntent() {
        return new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
    }

    /**
     * 產生一個可選擇相簿中的圖片，且可裁切
     *
     * @param tmpUri
     * @return
     */
    public Intent getSelectCutPictureIntent(Uri tmpUri) {
        Intent photoPickerIntent = new Intent(Intent.ACTION_PICK,
                android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        photoPickerIntent.setType("image/*");
        photoPickerIntent.putExtra("crop", "true");
        photoPickerIntent.putExtra("aspectX", 1);
        photoPickerIntent.putExtra("aspectY", 1);
        photoPickerIntent.putExtra("outputX", 200);
        photoPickerIntent.putExtra("outputY", 200);
        photoPickerIntent.putExtra(MediaStore.EXTRA_OUTPUT, tmpUri);
        photoPickerIntent.putExtra("outputFormat", Bitmap.CompressFormat.JPEG.toString());

        return photoPickerIntent;
    }

    public static Intent getUrlIntent(String strUrl)
    {
        if(TextUtils.isEmpty(strUrl) == false)
        {
            return new Intent(Intent.ACTION_VIEW, Uri.parse(strUrl));
        }
        else
        {
            return null;
        }
    }

    public static Intent getDialPhoneIntent(String strPhoneNumber)
    {
        return new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + strPhoneNumber));
    }
}
