package com.codepath.apps.simpletweetsfragment.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
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
            List<Tweet> tweetList = SQLite.select().from(Tweet.class).queryList();
            if (tweetList.size()==0) {
                Toast.makeText(getContext(), "No network available!", Toast.LENGTH_LONG).show();
            } else {
                //TODO if not network load from database and pass the data to fragment
//                tweetsListFragment.addList(tweetList);
            }

        } else {
            client.getUserTimeline(new JsonHttpResponseHandler() {
                @Override
                public void onSuccess(int statusCode, Header[] headers, JSONArray response) {
                    Log.d(LOG_TAG, "success!: " + response.toString());
                    addList(response);
                    hideProgressBar();
//                    saveToDataBase(tweets);
                }
                @Override
                public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                    throwable.printStackTrace();
                    hideProgressBar();
                }

            }, screenName, maxId);
        }
    }

    @Override
    public void loadMorePage(long maxId) {
        populateTimeline(screenName, maxId);
    }
}
