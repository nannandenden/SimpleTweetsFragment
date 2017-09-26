package com.codepath.apps.restclienttemplate.activities;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;

import com.codepath.apps.restclienttemplate.R;
import com.codepath.apps.restclienttemplate.TwitterApp;
import com.codepath.apps.restclienttemplate.TwitterClient;
import com.codepath.apps.restclienttemplate.adapter.TweetAdapter;
import com.codepath.apps.restclienttemplate.models.Tweet;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import cz.msebera.android.httpclient.Header;

public class TimelineActivity extends AppCompatActivity {

    private static final String LOG_TAG = TimelineActivity.class.getSimpleName();
    private TwitterClient client;
    private TweetAdapter tweetAdapter;
    private List<Tweet> tweets;

    @BindView(R.id.rvTweet)
    RecyclerView rvTweets;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_timeline);
        ButterKnife.bind(this);

        setView();
        populateTimeline();
    }

    private void setView() {
        client = TwitterApp.getRestClient();
        tweets = new ArrayList<>(); // init array list and this is data source
        tweetAdapter = new TweetAdapter(this, tweets); // construct the adapter using the data
        // source
        rvTweets.setLayoutManager(new LinearLayoutManager(this));
        rvTweets.setAdapter(tweetAdapter);
    }

    private void populateTimeline() {
        client.getHomeTimeline(new JsonHttpResponseHandler() {

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONArray response) {
                // iterate through the JSON array
                for (int i = 0; i < response.length(); i++) {
                    try {
                        // for each entry, deserialize the JSONObject
                        // convert each object to a Tweet model
                        // add that Tweet model to the data source
                        tweets.add(Tweet.formJSON(response.getJSONObject(i)));
                        // notify the adapter about the data source changes
                        tweetAdapter.notifyItemInserted(tweets.size()-1);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
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
