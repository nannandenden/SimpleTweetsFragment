package com.codepath.apps.simpletweetsfragment.models;

import com.codepath.apps.simpletweetsfragment.network.MyDatabase;
import com.codepath.apps.simpletweetsfragment.utils.Utils;
import com.google.gson.annotations.SerializedName;
import com.raizlabs.android.dbflow.annotation.Column;
import com.raizlabs.android.dbflow.annotation.ForeignKey;
import com.raizlabs.android.dbflow.annotation.PrimaryKey;
import com.raizlabs.android.dbflow.annotation.Table;
import com.raizlabs.android.dbflow.structure.BaseModel;

import org.parceler.Parcel;

/**
 * Created by nanden on 9/26/17.
 */
@Parcel
@Table(database = MyDatabase.class)
public class Tweet extends BaseModel {
    private static final String LOG_TAG = Tweet.class.getSimpleName();

    @Column
    @PrimaryKey
    @SerializedName("id")
    public long uid;

    @Column
    @SerializedName("text")
    public String body;

    @Column
    @SerializedName("created_at")
    public String createdAt;

    @Column
    @SerializedName("retweet_count")
    public int reTweets;

    @Column
    @SerializedName("favorite_count")
    public int likes;

    @Column
    public String mediaUrl;

    // one-to=one relation Tweet has a child element User saved inside
    // User table is saved indie an another table
    // unable to concatenate the parent element User via @Column but use @Foreignkey instead
    @ForeignKey(saveForeignKeyModel = true)
    @SerializedName("user")
    public User user;

    public Tweet() {
        // empty constructor needed for parcel
    }
    public String getBody() {
        return body;
    }

    public String getCreatedAt() {
        if (createdAt.contains("ago")) return createdAt;
        return Utils.getRelativeTimeAgo(createdAt);
    }

    public long getId() {
        return uid;
    }

    public User getUser() {
        return user;
    }

    public int getReTweets() {
        return reTweets;
    }

    public int getLikes() {
        return likes;
    }

    public void setUid(long uid) {
        this.uid = uid;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public void setReTweets(int reTweets) {
        this.reTweets = reTweets;
    }

    public void setLikes(int likes) {
        this.likes = likes;
    }

    public void setMediaUrl(String mediaUrl) {
        this.mediaUrl = mediaUrl;
    }

    public void setUser(User user) {
        this.user = user;
    }


}
