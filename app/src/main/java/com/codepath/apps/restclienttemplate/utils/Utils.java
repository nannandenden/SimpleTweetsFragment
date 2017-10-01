package com.codepath.apps.restclienttemplate.utils;


import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
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
        if (rawJsonData.contains("ago")) return rawJsonData;
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
        Log.d(LOG_TAG, relativeDate);
        return relativeDate;
    }

    public static boolean isNetworkAvailable(Context context) {
        ConnectivityManager cm =
                (ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        return activeNetwork != null &&
                activeNetwork.isConnectedOrConnecting();
    }
}
