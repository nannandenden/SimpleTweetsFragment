package com.codepath.apps.restclienttemplate.activities;

import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;

import com.codepath.apps.restclienttemplate.R;
import com.codepath.apps.restclienttemplate.adapter.TweetAdapter;
import com.codepath.apps.restclienttemplate.databinding.ActivityTimelineBinding;
import com.codepath.apps.restclienttemplate.models.Tweet;
import com.codepath.apps.restclienttemplate.network.TwitterApp;
import com.codepath.apps.restclienttemplate.network.TwitterClient;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.json.JSONArray;
import org.json.JSONObject;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import cz.msebera.android.httpclient.Header;

public class TimelineActivity extends AppCompatActivity {

    private static final String LOG_TAG = TimelineActivity.class.getSimpleName();
    private ActivityTimelineBinding binding;
    private TwitterClient client;
    private TweetAdapter tweetAdapter;
    private List<Tweet> tweets;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_timeline);

        setView();
        populateTimeline();
    }

    private void setView() {
        client = TwitterApp.getRestClient();
        tweets = new ArrayList<>(); // init array list and this is data source
        tweetAdapter = new TweetAdapter(this, tweets); // construct the adapter using the data
        // source
        RecyclerView rvTweets = binding.rvTweets;
        rvTweets.setLayoutManager(new LinearLayoutManager(this));
        rvTweets.setAdapter(tweetAdapter);
    }

    private void populateTimeline() {
        client.getHomeTimeline(new JsonHttpResponseHandler() {

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONArray response) {

                Type tweetType = new TypeToken<ArrayList<Tweet>>(){}.getType();
                List<Tweet> tweetList = new Gson().fromJson(response.toString(), tweetType);
                tweets.addAll(tweetList);
                tweetAdapter.notifyDataSetChanged();

            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                // twitter will response error with JSONObject
                Log.d(LOG_TAG, "onFailure: JSONObject: " + errorResponse.toString());
                // find out what is the error
                throwable.printStackTrace();
            }

        });
    }
}
