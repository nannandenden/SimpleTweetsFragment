package com.codepath.apps.restclienttemplate.models;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by nanden on 9/26/17.
 */

public class Tweet {
    // attributes
    public String body;
    public String createdAt;
     public User user;
    public long uid;

    //take JSONObject and instantiate Tweet object
    // deserialize the data
    public static Tweet formJSON(JSONObject jsonObject) throws JSONException {
        Tweet tweet = new Tweet();
        // extract the information from the JSONObject
        tweet.body = jsonObject.getString("text");
        tweet.uid = jsonObject.getLong("id");
        tweet.createdAt = jsonObject.getString("created_at");
        tweet.user = User.fromJSON(jsonObject.getJSONObject("user"));
        return tweet;
    }

}
