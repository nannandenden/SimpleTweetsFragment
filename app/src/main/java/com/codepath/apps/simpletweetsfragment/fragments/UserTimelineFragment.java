package com.codepath.apps.simpletweetsfragment.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.View;

import com.codepath.apps.simpletweetsfragment.network.TwitterApp;
import com.codepath.apps.simpletweetsfragment.network.TwitterClient;
import com.codepath.apps.simpletweetsfragment.utils.Utils;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.json.JSONArray;
import org.json.JSONObject;

import cz.msebera.android.httpclient.Header;

/**
 * Created by nanden on 10/4/17.
 */

public class UserTimelineFragment extends TweetsListFragment {

    private static final String LOG_TAG = UserTimelineFragment.class.getSimpleName();
    private TwitterClient client;
    private String screenName;

    public static UserTimelineFragment newInstance(String screenName) {
        UserTimelineFragment fragment = new UserTimelineFragment();
        Bundle args = new Bundle();
        args.putString("screen_name", screenName);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // instantiate network call client
        client = TwitterApp.getRestClient();
        screenName = getArguments().getString("screen_name");
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        populateTimeline(screenName, 0);
    }

    private void populateTimeline(String screenName, long maxId) {
        showProgressBar();
        if (!Utils.isNetworkAvailable(getContext())) {
            hideProgressBar();
            Utils.showToast(getContext(), "No internet connection");
        } else {
            client.getUserTimeline(new JsonHttpResponseHandler() {
                @Override
                public void onSuccess(int statusCode, Header[] headers, JSONArray response) {
                    Log.d(LOG_TAG, "success!: " + response.toString());
                    addList(response);
                    hideProgressBar();
                }
                @Override
                public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                    hideProgressBar();
                    Utils.showToast(getContext(), "Request failed: " + throwable.getMessage());
                }
            }, screenName, maxId);
        }
    }

    @Override
    public void loadMorePage(long maxId) {
        populateTimeline(screenName, maxId);
    }

    @Override
    public void onImageClick(View view, int position) {
        // call this method to disable the click
    }

    @Override
    public void onTweetRowClick(View view, int position) {
        // need to make some adjustment to fix this crash
        // since it's my own tweet, it is crashing
    }
}
