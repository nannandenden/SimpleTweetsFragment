package com.codepath.apps.simpletweetsfragment.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.View;

import com.codepath.apps.simpletweetsfragment.network.TwitterApp;
import com.codepath.apps.simpletweetsfragment.network.TwitterClient;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import cz.msebera.android.httpclient.Header;

/**
 * Created by nanden on 10/6/17.
 */

public class FollowingFragment extends UsersListFragment {

    private static final String LOG_TAG = FollowingFragment.class.getSimpleName();
    private TwitterClient client;
    private String screenName;

    public static FollowingFragment newInstance(String screenName) {
        FollowingFragment fragment = new FollowingFragment();
        Bundle args = new Bundle();
        args.putString("screen_name", screenName);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // obtain network request singleton instance
        client = TwitterApp.getRestClient();
        screenName = getArguments().getString("screen_name");
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        populateList(screenName);
    }

    private void populateList(String screenName) {
        JsonHttpResponseHandler handler = new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                super.onSuccess(statusCode, headers, response);
                Log.d(LOG_TAG, String.valueOf(statusCode));
                JSONArray responseArray = null;
                try {
                    responseArray = response.getJSONArray("users");
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                addList(responseArray);
            }
        };
        client.getFollowingsList(handler, screenName, 0);
    }
}
