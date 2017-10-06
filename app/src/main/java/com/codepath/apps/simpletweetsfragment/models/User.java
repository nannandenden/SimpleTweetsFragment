package com.codepath.apps.simpletweetsfragment.models;

import com.codepath.apps.simpletweetsfragment.network.MyDatabase;
import com.google.gson.annotations.SerializedName;
import com.raizlabs.android.dbflow.annotation.Column;
import com.raizlabs.android.dbflow.annotation.PrimaryKey;
import com.raizlabs.android.dbflow.annotation.Table;
import com.raizlabs.android.dbflow.structure.BaseModel;

import org.parceler.Parcel;

/**
 * Created by nanden on 9/26/17.
 */
@Parcel
@Table(database = MyDatabase.class)
public class User extends BaseModel {

    @Column
    @PrimaryKey
    @SerializedName("id")
    public long uid;

    @Column
    @SerializedName("name")
    public String name;

    @Column
    @SerializedName("screen_name")
    public String screenName;

    @Column
    @SerializedName("profile_image_url")
    public String profileImageUrl;

    @Column
    @SerializedName("description")
    public String tagLine;

    @Column
    @SerializedName("followers_count")
    public int followers;

    @Column
    @SerializedName("friends_count")
    public int following;

    public User() {
        // empty constructor needed for parcel
    }

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

    public String getTagLine() {
        return tagLine;
    }

    public int getFollowers() {
        return followers;
    }

    public int getFollowing() {
        return following;
    }

    public void setUid(long uid) {
        this.uid = uid;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setScreenName(String screenName) {
        this.screenName = screenName;
    }

    public void setProfileImageUrl(String profileImageUrl) {
        this.profileImageUrl = profileImageUrl;
    }

    public void setTagLine(String tagLine) {
        this.tagLine = tagLine;
    }

    public void setFollowers(int followers) {
        this.followers = followers;
    }

    public void setFollowing(int following) {
        this.following = following;
    }
}
