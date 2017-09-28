package com.codepath.apps.restclienttemplate.activities;

import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;

import com.codepath.apps.restclienttemplate.R;
import com.codepath.apps.restclienttemplate.adapter.TweetAdapter;
import com.codepath.apps.restclienttemplate.databinding.ActivityTimelineBinding;
import com.codepath.apps.restclienttemplate.fragments.ComposeFragment;
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

public class TimelineActivity extends AppCompatActivity implements ComposeFragment
        .EditTweetDialogListener {

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
        FloatingActionButton fabTimeline = binding.fabTimeline;
        fabTimeline.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentManager fragmentManager = getSupportFragmentManager();
                ComposeFragment composeFragment = ComposeFragment.newInstance();
                composeFragment.show(fragmentManager, "fragment_compose");
            }
        });
        // source
        RecyclerView rvTweets = binding.rvTweets;
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        rvTweets.setLayoutManager(linearLayoutManager);
        rvTweets.setAdapter(tweetAdapter);
        rvTweets.addOnScrollListener(new EndlessRecyclerViewScrollListener(linearLayoutManager) {
            @Override
            public void onLoadMore(int currentPage, int totalItemCount, RecyclerView recyclerView) {

                int lastPosition = tweets.size()-1;
                long maxId = tweets.get(lastPosition).getId();

                Log.d(LOG_TAG, "maxId: " + maxId);
                Log.d(LOG_TAG, "onLoadMore called. currentPage: " + currentPage);
                JsonHttpResponseHandler handler = new JsonHttpResponseHandler() {

                    @Override
                    public void onSuccess(int statusCode, Header[] headers, JSONArray response) {
                        Log.d(LOG_TAG, "Success! loading more page");
                        Type tweetType = new TypeToken<ArrayList<Tweet>>(){}.getType();
                        List<Tweet> tweetList = new Gson().fromJson(response.toString(), tweetType);
                        tweets.addAll(tweetList);
                        tweetAdapter.notifyDataSetChanged();

                    }

                    @Override
                    public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONArray errorResponse) {
                        Log.d(LOG_TAG, "onFailure" + errorResponse.toString());
                        throwable.printStackTrace();

                    }

                };
                client.getHomeTimeline(handler, (long)tweets.get(lastPosition).getId());
            }
        });
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

        }, 0);
    }

    private void postNewTweet(String message) {
        JsonHttpResponseHandler handler = new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                Log.d(LOG_TAG, "JSONObject success: " + response.toString());
                Tweet tweet = new Gson().fromJson(response.toString(), Tweet.class);
                tweets.add(tweet);
                tweetAdapter.notifyItemChanged(tweets.size()-1);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                Log.d(LOG_TAG, "JSONObject fail: " + errorResponse.toString());

            }
        };
        client.postTweet(handler, message);
    }

    @Override
    public void onFinishEditTweet(String input) {
        Log.d(LOG_TAG, "input: " + input);
        postNewTweet(input);
    }
}
