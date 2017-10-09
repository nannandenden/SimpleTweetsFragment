package com.codepath.apps.simpletweetsfragment.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;

import com.codepath.apps.simpletweetsfragment.network.TwitterApp;
import com.codepath.apps.simpletweetsfragment.network.TwitterClient;
import com.codepath.apps.simpletweetsfragment.utils.Utils;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import cz.msebera.android.httpclient.Header;

/**
 * Created by nanden on 10/7/17.
 */

public class SearchResultTimeLineFragment extends TweetsListFragment {

    private static final String LOG_TAG = SearchResultTimeLineFragment.class.getSimpleName();
    private TwitterClient client;
    private String hashTag;

    public static SearchResultTimeLineFragment newInstance(String hashTag) {
        Bundle args = new Bundle();
        SearchResultTimeLineFragment fragment = new SearchResultTimeLineFragment();
        args.putString("hash_tag", hashTag);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        client = TwitterApp.getRestClient();
        hashTag = getArguments().getString("hash_tag");
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        populateTimeLine(0);
    }

    private void populateTimeLine(long maxId) {
        showProgressBar();
        if (!Utils.isNetworkAvailable(getContext())) {
            hideProgressBar();
            Utils.showToast(getContext(), "No internet connection");
        } else {
            JsonHttpResponseHandler handler = new JsonHttpResponseHandler() {
                @Override
                public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                    super.onSuccess(statusCode, headers, response);
                    JSONArray jsonArray = null;
                    try {
                         jsonArray = response.getJSONArray("statuses");
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    addList(jsonArray);
                    hideProgressBar();
                }
                @Override
                public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                    super.onFailure(statusCode, headers, throwable, errorResponse);
                    hideProgressBar();
                    Utils.showToast(getContext(), "Request failed: " + throwable.getMessage());
                }
            };
            client.searchTweets(handler, hashTag, maxId);
        }
    }

    @Override
    public void loadMorePage(long maxId) {
       // for pagination
        populateTimeLine(maxId);
    }
}
