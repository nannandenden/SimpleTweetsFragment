package com.codepath.apps.simpletweetsfragment.network;

import com.codepath.apps.simpletweetsfragment.models.Tweet;
import com.codepath.apps.simpletweetsfragment.models.User;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;

import java.lang.reflect.Type;

/**
 * Created by nanden on 10/7/17.
 */

public class TweetDeserializer implements JsonDeserializer<Tweet> {

    @Override
    public Tweet deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        JsonObject jsonObject = json.getAsJsonObject();

        long uid = jsonObject.get("id").getAsLong();
        String body = jsonObject.get("text").getAsString();
        String createdAt = jsonObject.get("created_at").getAsString();
        int reTweets = jsonObject.get("retweet_count").getAsInt();
        int likes = jsonObject.get("favorite_count").getAsInt();
        JsonObject entities = jsonObject.getAsJsonObject("entities");
        String mediaUrl = "";
        if (entities.has("media")){
            JsonObject object = entities.getAsJsonArray("media").get(0).getAsJsonObject();
            mediaUrl = object.get("media_url").getAsString();
        }
        User user = context.deserialize(jsonObject.get("user"), User.class);

        Tweet tweet = new Tweet();
        tweet.setUid(uid);
        tweet.setBody(body);
        tweet.setCreatedAt(createdAt);
        tweet.setReTweets(reTweets);
        tweet.setLikes(likes);
        tweet.setMediaUrl(mediaUrl);
        tweet.setUser(user);
        return tweet;
    }
}
