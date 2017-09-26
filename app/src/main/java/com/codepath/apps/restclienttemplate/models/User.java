package com.codepath.apps.restclienttemplate.models;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by nanden on 9/26/17.
 */

public class User {

    // list of attributes
    public String name;
    public String screenName;
    public String profileImageUrl;
    public long uid;

    public static User fromJSON(JSONObject jsonObject) throws JSONException {
        User user = new User();

        user.name = jsonObject.getString("name");
        user.screenName = jsonObject.getString("screen_name");
        user.profileImageUrl = jsonObject.getString("profile_image_url");
        user.uid = jsonObject.getLong("id");

        return user;
    }

}
