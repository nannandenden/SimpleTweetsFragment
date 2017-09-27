package com.codepath.apps.restclienttemplate.models;

import com.google.gson.annotations.SerializedName;

/**
 * Created by nanden on 9/26/17.
 */

public class Tweet {

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
        return createdAt;
    }

    public long getId() {
        return uid;
    }

    public User getUser() {
        return user;
    }


}
