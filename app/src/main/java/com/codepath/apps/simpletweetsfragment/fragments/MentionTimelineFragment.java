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

public class MentionTimelineFragment extends TweetsListFragment {

    private static final String LOG_TAG = MentionTimelineFragment.class.getSimpleName();
    private TwitterClient client;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
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
            Utils.showToast(getContext(), "No internet....:(");
            hideProgressBar();

        } else {
            client.getMentionTimeline(new JsonHttpResponseHandler() {

                @Override
                public void onSuccess(int statusCode, Header[] headers, JSONArray response) {
                    Log.d(LOG_TAG, "json array response. response: " + response.toString());
                    addList(response);
                    hideProgressBar();
                }

                @Override
                public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                    throwable.printStackTrace();
                    hideProgressBar();
                }
            }, maxId);
        }
    }

    @Override
    public void loadMorePage(long maxId) {
        populateTimeline(maxId);
    }
}
