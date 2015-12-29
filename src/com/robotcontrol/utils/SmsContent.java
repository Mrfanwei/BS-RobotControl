package com.robotcontrol.utils;

import android.app.Activity;
import android.content.ContentValues;
import android.database.ContentObserver;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Handler;
import android.widget.EditText;

import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class SmsContent extends ContentObserver {
    /**
     * Creates a content observer.
     *
     * @param handler The handler to run {@link #onChange} on, or null if none.
     */
    private Activity activity;
    private String smsContent = "";
    private EditText verifyText;
    public static final String SMS_URI_INBOX = "content://sms/inbox";

    public SmsContent(Activity activity, Handler handler, EditText verifyText) {
        super(handler);
        this.activity = activity;
        this.verifyText = verifyText;
    }

    @Override
    public void onChange(boolean selfChange) {
        super.onChange(selfChange);
        Cursor cursor = null;
        cursor = activity.managedQuery(Uri.parse(SMS_URI_INBOX), new String[]{"_id", "address", "body", "read"}, "address=? and read=?", new String[]{"10690046825", "0"}, "date desc");
        if (cursor != null && cursor.getCount() > 0) {

            ContentValues values = new ContentValues();

            values.put("read", "1"); // 修改短信为已读模�?

            cursor.moveToNext();

            int smsbodyColumn = cursor.getColumnIndex("body");

            String smsBody = cursor.getString(smsbodyColumn);

           verifyText.setText(getDynamicPassword(smsBody));
        }
// 在用managedQuery的时候，不能主动调用close()方法�? 否则�?<a href="http://www.it165.net/pro/ydad/" target="_blank" class="keylink">Android</a> 4.0+的系统上�? 会发生崩溃\
        if (Build.VERSION.SDK_INT < 14) {
            cursor.close();
        }
    }

    public static String getDynamicPassword(String str) {
// 6是验证码的位数一般为六位
        Pattern continuousNumberPattern = Pattern.compile("(?<![0-9])([0-9]{"
                + 4 + "})(?![0-9])");
        Matcher m = continuousNumberPattern.matcher(str);
        String dynamicPassword = "";
        while (m.find()) {
            System.out.print(m.group());
            dynamicPassword = m.group();
        }
        return dynamicPassword;
    }
}
