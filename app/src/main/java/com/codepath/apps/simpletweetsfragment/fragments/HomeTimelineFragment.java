package com.codepath.apps.simpletweetsfragment.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.View;

import com.codepath.apps.simpletweetsfragment.models.Tweet;
import com.codepath.apps.simpletweetsfragment.network.TwitterApp;
import com.codepath.apps.simpletweetsfragment.network.TwitterClient;
import com.codepath.apps.simpletweetsfragment.utils.Utils;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.raizlabs.android.dbflow.sql.language.SQLite;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.List;

import cz.msebera.android.httpclient.Header;

/**
 * Created by nanden on 10/4/17.
 */

public class HomeTimelineFragment extends TweetsListFragment {

    private static final String LOG_TAG = HomeTimelineFragment.class.getSimpleName();
    private TwitterClient client;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // instantiate network call client
        client = TwitterApp.getRestClient();
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        populateTimeline(0);
    }

    private void populateTimeline(long maxId) {
        showProgressBar();
        if (!Utils.isNetworkAvailable(getContext())) {
            Log.d(LOG_TAG, "No internet connection. Attempt to load from database");
            populateFromDatabase("No internet...:(");
            hideProgressBar();
        } else {
            client.getHomeTimeline(new JsonHttpResponseHandler() {
                @Override
                public void onSuccess(int statusCode, Header[] headers, JSONArray response) {
                    // successfully getting the new data, delete the database before inserting
                    // the new items
                    deleteDatabaseTable();
                    // add new items
                    addList(response);
                    hideProgressBar();
                }
                @Override
                public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                    Log.d(LOG_TAG, throwable.getMessage());
                    populateFromDatabase(throwable.getMessage());
                    hideProgressBar();
                }
            }, maxId);
        }
    }

    // item will be load from database when there is network request error/user offline.
    private void populateFromDatabase(String reasonMessage) {
        List<Tweet> tweetList = SQLite.select().from(Tweet.class).queryList();
        if (tweetList.size()==0) {
            Utils.showToast(getContext(), reasonMessage);
        } else {
            addList(tweetList);
        }
    }

    @Override
    public void loadMorePage(long maxId) {
        if (Utils.isNetworkAvailable(getContext())) {
            populateTimeline(maxId);
        }
    }
}
