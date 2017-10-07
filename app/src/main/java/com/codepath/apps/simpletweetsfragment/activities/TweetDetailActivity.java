package com.codepath.apps.simpletweetsfragment.activities;

import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import com.codepath.apps.simpletweetsfragment.R;
import com.codepath.apps.simpletweetsfragment.databinding.ActivityTweetDetailBinding;
import com.codepath.apps.simpletweetsfragment.models.Tweet;

import org.parceler.Parcels;

public class TweetDetailActivity extends AppCompatActivity {
    private static final String LOG_TAG = TweetDetailActivity.class.getSimpleName();
    private ActivityTweetDetailBinding binding;
    private Toolbar toolbar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_tweet_detail);
        Tweet tweet = Parcels.unwrap(getIntent().getParcelableExtra("tweet"));
        binding.setTweet(tweet);
        binding.executePendingBindings();
        toolbar = binding.toolbar;
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Tweet");
    }

}
