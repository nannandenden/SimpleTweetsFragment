package com.codepath.apps.simpletweetsfragment.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.Toast;

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
            List<Tweet> tweetList = SQLite.select().from(Tweet.class).queryList();
            if (tweetList.size()==0) {
                Toast.makeText(getContext(), "No network available!", Toast.LENGTH_LONG).show();
            } else {
                //TODO if not network load from database and pass the data to fragment
//                tweetsListFragment.addList(tweetList);
            }

        } else {
            client.getHomeTimeline(new JsonHttpResponseHandler() {
                @Override
                public void onSuccess(int statusCode, Header[] headers, JSONArray response) {
                    addList(response);
                    hideProgressBar();
//                    saveToDataBase(tweets);
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
