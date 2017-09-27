package com.codepath.apps.restclienttemplate.models;

import android.util.Log;

import com.codepath.apps.restclienttemplate.utils.Utils;
import com.google.gson.annotations.SerializedName;

/**
 * Created by nanden on 9/26/17.
 */

public class Tweet {
    private static final String LOG_TAG = Tweet.class.getSimpleName();
    @SerializedName("text")
    public String body;
    @SerializedName("created_at")
    public String createdAt;
    @SerializedName("id")
    public long uid;
    @SerializedName("user")
    public User user;

    public String getBody() {
        return body;
    }

    public String getCreatedAt() {
        return Utils.getRelativeTimeAgo(createdAt);
    }

    public long getId() {
        return uid;
    }

    public User getUser() {
        return user;
    }


}
