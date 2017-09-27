package com.codepath.apps.restclienttemplate.utils;


import android.text.format.DateUtils;
import android.util.Log;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Locale;

/**
 * Created by nanden on 9/26/17.
 */

public class Utils {

    private static final String LOG_TAG = Utils.class.getSimpleName();
    // get relative time ago
    public static String getRelativeTimeAgo(String rawJsonData) {
        String twitterFormat = "EEE MMM dd HH:mm:ss ZZZZZ yyyy";
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(twitterFormat, Locale.ENGLISH);
        simpleDateFormat.setLenient(true);

        String relativeDate = "";
        try {
            long dateMills = simpleDateFormat.parse(rawJsonData).getTime();
            relativeDate = DateUtils.getRelativeTimeSpanString(dateMills, System
                    .currentTimeMillis(), DateUtils.SECOND_IN_MILLIS).toString();
        } catch (ParseException e) {
            Log.d(LOG_TAG, e.getMessage());
            e.printStackTrace();
        }
        return relativeDate;
    }
}
