package com.codepath.apps.simpletweetsfragment.activities;

import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.codepath.apps.simpletweetsfragment.R;
import com.codepath.apps.simpletweetsfragment.adapter.TweetsPagerAdapter;
import com.codepath.apps.simpletweetsfragment.databinding.ActivityTimelineBinding;
import com.codepath.apps.simpletweetsfragment.fragments.ComposeFragment;
import com.codepath.apps.simpletweetsfragment.fragments.HomeTimelineFragment;
import com.codepath.apps.simpletweetsfragment.fragments.TweetsListFragment;
import com.codepath.apps.simpletweetsfragment.models.Tweet;
import com.codepath.apps.simpletweetsfragment.models.User;
import com.codepath.apps.simpletweetsfragment.network.MyDatabase;
import com.codepath.apps.simpletweetsfragment.network.TwitterApp;
import com.codepath.apps.simpletweetsfragment.network.TwitterClient;
import com.codepath.apps.simpletweetsfragment.utils.Utils;
import com.google.gson.Gson;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.raizlabs.android.dbflow.config.DatabaseDefinition;
import com.raizlabs.android.dbflow.config.FlowManager;
import com.raizlabs.android.dbflow.structure.database.DatabaseWrapper;
import com.raizlabs.android.dbflow.structure.database.transaction.ProcessModelTransaction;
import com.raizlabs.android.dbflow.structure.database.transaction.Transaction;

import org.json.JSONObject;
import org.parceler.Parcels;

import java.util.List;

import cz.msebera.android.httpclient.Header;

public class TimelineActivity extends AppCompatActivity implements
        ComposeFragment.EditTweetDialogListener,
        TweetsListFragment.OnImageSelectedListener,
        TweetsListFragment.OnTweetSelectedListener,
        TweetsListFragment.OnSpanNameClickedListener,
        TweetsListFragment.OnSpanTagClickedListener,
        TweetsListFragment.OnReplyClickedListener {

    private static final String LOG_TAG = TimelineActivity.class.getSimpleName();
    private ActivityTimelineBinding binding;
    private TweetsPagerAdapter pagerAdapter;
    private TwitterClient client;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        client = TwitterApp.getRestClient();
        binding = DataBindingUtil.setContentView(this, R.layout.activity_timeline);
        setView();
    }

    /**
     * Override methods
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_timeline, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public void onFinishEditTweet(Tweet tweet) {
        Log.d(LOG_TAG, "input: " + tweet);
        // get the instance of the HomeTimelineFragment
        HomeTimelineFragment fragment = (HomeTimelineFragment) pagerAdapter.getRegisteredFragment(0);
        // directory calling the fragment method to add a new tweet
        fragment.addOneTweet(tweet);
    }

    @Override
    public void onImageClick(User user) {
        // profile image was licked, display profile timeline
        Log.d(LOG_TAG, "user: " + user.toString());
        displayProfileInfo(user);
    }

    @Override
    public void onTweetClick(Tweet tweet) {
        // row of the tweet was clicked, display the tweet detail
        displayTweetDetail(tweet);
    }

    @Override
    public void onSpanNameClick(String screenName) {
        // using the screenName to get the user object
        String searchName = screenName.replace("@", "");
        Log.d(LOG_TAG, searchName);
        JsonHttpResponseHandler handler = new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                super.onSuccess(statusCode, headers, response);
                Gson gson = new Gson();
                User user = gson.fromJson(response.toString(), User.class);
                displayProfileInfo(user);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                Utils.showToast(TimelineActivity.this, "Request Failed: " + throwable.getMessage());
            }
        };
        if (!Utils.isNetworkAvailable(this)) {
            Utils.showToast(this, "No internet connection");
        } else {
            client.userShow(handler, searchName);
        }
    }

    @Override
    public void onSpanTagClick(String hashTag) {
        Log.d(LOG_TAG, "hashTag: " + hashTag);
        Intent intent = new Intent(this, SearchResultActivity.class);
        intent.putExtra("hash_tag", hashTag);
        startActivity(intent);
    }

    @Override
    public void onReplyClicked(Tweet tweet) {
        String id = String.valueOf(tweet.getId());
        String screenName = tweet.getUser().getScreenName();
        Log.d(LOG_TAG, "id: " + id + "\tname: " + screenName);
        openComposeMessageDialog(id, screenName);
    }

    /**
     * private methods
     */
    private void setView() {
        // get the view pager
        setSupportActionBar(binding.toolbar);
        getSupportActionBar().setIcon(R.drawable.ic_toys_black_24dp);
        getSupportActionBar().setTitle(" Twitter Fragment");
        // attaching the viewpager to the tweetspageradapter
        pagerAdapter = new TweetsPagerAdapter(getSupportFragmentManager(), this);
        binding.viewPager.setAdapter(pagerAdapter);
        // set the viewpager on the tablayout to connect the pager with the tabs
        binding.tabLayout.setupWithViewPager(binding.viewPager);
        // set the adapter for the pager
        // setup the tablayout to use the view pager
        // tablayout is a bar that goes under the action bar. You can define style or add custom
        // view to the tablayout
        binding.fabTimeline.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openComposeMessageDialog(null, null);
            }
        });
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

    /**
     * public methods
     */
    // profile icon on the action bar was clicked
    public void onProfileView(MenuItem item) {
        displayProfileInfo(null);
    }

    private void displayProfileInfo(User user) {
        Intent intent = new Intent(this, ProfileActivity.class);
        if (user != null) {
            intent.putExtra("user", Parcels.wrap(user));
        }
        startActivity(intent);
    }

    private void displayTweetDetail(Tweet tweet) {
        if (tweet == null) {
            Utils.showToast(this, "tweet is null!");
        } else {
            Intent intent = new Intent(this, TweetDetailActivity.class);
            intent.putExtra("tweet", Parcels.wrap(tweet));
            startActivity(intent);
        }
    }

    private void openComposeMessageDialog(String id, String screenName) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        ComposeFragment composeFragment = ComposeFragment.newInstance(id, screenName);
        composeFragment.show(fragmentManager, "fragment_compose");

    }

}
