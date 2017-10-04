package com.codepath.apps.simpletweetsfragment.activities;

import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.codepath.apps.simpletweetsfragment.R;
import com.codepath.apps.simpletweetsfragment.adapter.TweetAdapter;
import com.codepath.apps.simpletweetsfragment.databinding.ActivityTimelineBinding;
import com.codepath.apps.simpletweetsfragment.fragments.ComposeFragment;
import com.codepath.apps.simpletweetsfragment.models.Tweet;
import com.codepath.apps.simpletweetsfragment.network.MyDatabase;
import com.codepath.apps.simpletweetsfragment.network.TwitterApp;
import com.codepath.apps.simpletweetsfragment.network.TwitterClient;
import com.codepath.apps.simpletweetsfragment.utils.Utils;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.raizlabs.android.dbflow.config.DatabaseDefinition;
import com.raizlabs.android.dbflow.config.FlowManager;
import com.raizlabs.android.dbflow.sql.language.SQLite;
import com.raizlabs.android.dbflow.structure.database.DatabaseWrapper;
import com.raizlabs.android.dbflow.structure.database.transaction.ProcessModelTransaction;
import com.raizlabs.android.dbflow.structure.database.transaction.Transaction;

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
        if (!Utils.isNetworkAvailable(this)) {
            List<Tweet> tweetList = SQLite.select().from(Tweet.class).queryList();
            if (tweetList.size()==0) {
                Toast.makeText(this, "No network available!", Toast.LENGTH_LONG).show();
            } else {
                tweets.addAll(tweetList);
                tweetAdapter.notifyItemRangeInserted(0, tweetList.size()-1);
            }

        } else {
            client.getHomeTimeline(new JsonHttpResponseHandler() {

                @Override
                public void onSuccess(int statusCode, Header[] headers, JSONArray response) {
                    Log.d(LOG_TAG, "JSONObject success: " + response.toString());
                    Type tweetType = new TypeToken<ArrayList<Tweet>>(){}.getType();
                    List<Tweet> tweetList = new Gson().fromJson(response.toString(), tweetType);
                    tweets.addAll(tweetList);
                    tweetAdapter.notifyItemRangeInserted(0, tweets.size()-1);
                    saveToDataBase(tweets);

                }

                @Override
                public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                    throwable.printStackTrace();
                }

            }, 0);
        }
    }


    @Override
    public void onFinishEditTweet(Tweet tweet) {
        Log.d(LOG_TAG, "input: " + tweet);
        tweets.add(0, tweet);
        tweetAdapter.notifyItemInserted(0);
    }

    private void saveToDataBase(List<Tweet> tweets) {

        DatabaseDefinition database = FlowManager.getDatabase(MyDatabase.class);

        ProcessModelTransaction<Tweet> processModelTransaction = new ProcessModelTransaction
                .Builder<>(new ProcessModelTransaction.ProcessModel<Tweet>() {
            @Override
            public void processModel(Tweet tweet, DatabaseWrapper wrapper) {
                tweet.save();

            }
        }).processListener(new ProcessModelTransaction.OnModelProcessListener<Tweet>() {
            @Override
            public void onModelProcessed(long current, long total, Tweet modifiedModel) {

            }
        }).addAll(tweets).build();

        Transaction transaction = database.beginTransactionAsync(processModelTransaction)
                .success(new Transaction.Success() {
                    @Override
                    public void onSuccess(@NonNull Transaction transaction) {
                        Log.d(LOG_TAG, "transaction success!");
                    }
                })
                .error(new Transaction.Error() {
                    @Override
                    public void onError(@NonNull Transaction transaction, @NonNull Throwable error) {
                        Log.d(LOG_TAG, "transaction error");

                    }
                })
                .build();
        transaction.execute();

    }
}
