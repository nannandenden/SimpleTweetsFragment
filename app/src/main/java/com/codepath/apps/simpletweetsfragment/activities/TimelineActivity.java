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
import com.codepath.apps.simpletweetsfragment.databinding.ActivityTimelineBinding;
import com.codepath.apps.simpletweetsfragment.fragments.ComposeFragment;
import com.codepath.apps.simpletweetsfragment.fragments.TweetsListFragment;
import com.codepath.apps.simpletweetsfragment.fragments.TweetsPagerAdapter;
import com.codepath.apps.simpletweetsfragment.models.Tweet;
import com.codepath.apps.simpletweetsfragment.models.User;
import com.codepath.apps.simpletweetsfragment.network.MyDatabase;
import com.raizlabs.android.dbflow.config.DatabaseDefinition;
import com.raizlabs.android.dbflow.config.FlowManager;
import com.raizlabs.android.dbflow.structure.database.DatabaseWrapper;
import com.raizlabs.android.dbflow.structure.database.transaction.ProcessModelTransaction;
import com.raizlabs.android.dbflow.structure.database.transaction.Transaction;

import org.parceler.Parcels;

import java.util.List;

public class TimelineActivity extends AppCompatActivity implements ComposeFragment
        .EditTweetDialogListener, TweetsListFragment.OnTweetSelectedListener {

    private static final String LOG_TAG = TimelineActivity.class.getSimpleName();
    private ActivityTimelineBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_timeline);
        setView();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_timeline, menu);
        return super.onCreateOptionsMenu(menu);
    }


    private void setView() {
        // get the view pager
        setSupportActionBar(binding.toolbar);
        binding.viewPager.setAdapter(new TweetsPagerAdapter(getSupportFragmentManager(),this));
        binding.slidingTab.setupWithViewPager(binding.viewPager);
        // set the adapter for the pager
        // setup the tablayout to use the view pager
        binding.fabTimeline.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentManager fragmentManager = getSupportFragmentManager();
                ComposeFragment composeFragment = ComposeFragment.newInstance();
                composeFragment.show(fragmentManager, "fragment_compose");
            }
        });
    }



    @Override
    public void onFinishEditTweet(Tweet tweet) {
        Log.d(LOG_TAG, "input: " + tweet);
        // let's worry about this later
//        tweets.add(0, tweet);
//        tweetAdapter.notifyItemInserted(0);
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
    // profile icon on the action bar was clicked
    public void onProfileView(MenuItem item) {
        displayProfileInfo(null);
    }

    // profile image was clicked
    @Override
    public void onTweetClick(User user) {
        Log.d(LOG_TAG, "user: " + user.toString());
        displayProfileInfo(user);
    }

    private void displayProfileInfo(User user) {
        Intent intent = new Intent(this, ProfileActivity.class);
        if (user != null) {
            intent.putExtra("user", Parcels.wrap(user));
        }
        startActivity(intent);
    }
}
