package com.codepath.apps.restclienttemplate.models;

import com.google.gson.annotations.SerializedName;

/**
 * Created by nanden on 9/26/17.
 */

public class User {

    // list of attributes
    @SerializedName("name")
    public String name;
    @SerializedName("screen_name")
    public String screenName;
    @SerializedName("profile_image_url")
    public String profileImageUrl;
    @SerializedName("id")
    public long uid;

    public String getName() {
        return name;
    }

    public String getScreenName() {
        return screenName;
    }

    public String getProfileImageUrl() {
        return profileImageUrl;
    }

    public long getUid() {
        return uid;
    }
}
