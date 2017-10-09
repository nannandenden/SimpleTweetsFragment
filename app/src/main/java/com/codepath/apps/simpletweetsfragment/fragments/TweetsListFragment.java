package com.codepath.apps.simpletweetsfragment.fragments;
// support v4 supports backword compatibility

import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import com.codepath.apps.simpletweetsfragment.R;
import com.codepath.apps.simpletweetsfragment.adapter.TweetAdapter;
import com.codepath.apps.simpletweetsfragment.databinding.FragmentsTweetsListBinding;
import com.codepath.apps.simpletweetsfragment.models.Tweet;
import com.codepath.apps.simpletweetsfragment.models.User;
import com.codepath.apps.simpletweetsfragment.network.SimpleTweetsDatabase;
import com.codepath.apps.simpletweetsfragment.network.TweetDeserializer;
import com.codepath.apps.simpletweetsfragment.utils.EndlessRecyclerViewScrollListener;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.raizlabs.android.dbflow.config.DatabaseDefinition;
import com.raizlabs.android.dbflow.config.FlowManager;
import com.raizlabs.android.dbflow.sql.language.Delete;
import com.raizlabs.android.dbflow.structure.database.DatabaseWrapper;
import com.raizlabs.android.dbflow.structure.database.transaction.ProcessModelTransaction;
import com.raizlabs.android.dbflow.structure.database.transaction.Transaction;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by nanden on 10/3/17.
 */

public abstract class TweetsListFragment extends Fragment implements
        TweetAdapter.OnProfileImageClickListener,
        TweetAdapter.OnTweetRowClickListener,
        TweetAdapter.OnSpanNameClickListener,
        TweetAdapter.OnSpanTagClickListener,
        TweetAdapter.OnReplyClickListener {

    // define the interface to communicate it's activity
    public interface OnImageSelectedListener {
        void onImageClick(User user);
    }
    public interface OnTweetSelectedListener {
        void onTweetClick(Tweet tweet);
    }
    public interface OnSpanNameClickedListener {
        void onSpanNameClick(String screenName);
    }
    public interface OnSpanTagClickedListener {
        void onSpanTagClick(String hashTag);
    }
    public interface OnReplyClickedListener {
        void onReplyClicked(Tweet tweet);
    }
    private static final String LOG_TAG = TweetsListFragment.class.getSimpleName();
    private FragmentsTweetsListBinding binding;
    private TweetAdapter tweetAdapter;
    private List<Tweet> tweets;
    private RecyclerView rvTweets;
    private ProgressBar progressBar;

    // inflation happens inside onCreateview
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragments_tweets_list, container,
                false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setView();
    }

    private void setView() {
        tweets = new ArrayList<>(); // init array list and this is data source
        tweetAdapter = new TweetAdapter(getContext(), tweets); // construct the adapter using
        tweetAdapter.setOnProfileImageClickListener(this);
        tweetAdapter.setOnTweetRowClickListener(this);
        tweetAdapter.setOnSpanNameClickListener(this);
        tweetAdapter.setOnSpanTagClickListener(this);
        tweetAdapter.setOnReplyClickListener(this);
        // data binding
        rvTweets = binding.rvTweets;
        progressBar = binding.progressBar;
        // set up recyclerview behavior
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        rvTweets.setLayoutManager(linearLayoutManager);
        rvTweets.setAdapter(tweetAdapter);
        rvTweets.addOnScrollListener(new EndlessRecyclerViewScrollListener(linearLayoutManager) {
            // need to determine which api I need to call tho...
            @Override
            public void onLoadMore(int currentPage, int totalItemCount, RecyclerView recyclerView) {
                int lastPosition = tweets.size()-1;
                long maxId = tweets.get(lastPosition).getId()-1;
                loadMorePage(maxId);
            }
        });
    }

    public void addList(JSONArray response) {
        List<Tweet> tweetList = new ArrayList<>();
        GsonBuilder gsonBuilder = new GsonBuilder();
        gsonBuilder.registerTypeAdapter(Tweet.class, new TweetDeserializer());
        Gson gson = gsonBuilder.create();
        for (int i = 0; i < response.length(); i++) {
            try {
                Tweet tweet = gson.fromJson(response.getJSONObject(i).toString(), Tweet.class);
                Log.d(LOG_TAG, tweet.toString());
                tweetList.add(tweet);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        // bulk saving to database
        saveToDataBase(tweetList);
        addList(tweetList);
    }

    public void addList(List<Tweet> list) {
        int startIndex = 0;
        if (tweets.size() > 0) {
            startIndex = tweets.size();
        }
        tweets.addAll(list);
        tweetAdapter.notifyItemRangeInserted(startIndex, tweets.size()-1);
    }

    public void addOneTweet(Tweet tweet) {
        tweets.add(0, tweet);
        tweetAdapter.notifyItemInserted(0);
        rvTweets.scrollToPosition(0);
        // save to database. if it's a single value, it's okay to save it synchronously
        tweet.save();
    }

    public void showProgressBar() {
        progressBar.setVisibility(View.VISIBLE);
    }

    public void hideProgressBar() {
        progressBar.setVisibility(View.GONE);
    }

    public void deleteDatabaseTable() {
        // Delete multiple instantly
        Delete.tables(Tweet.class, User.class);
    }

    // fire the event to pass the screenName to the activity when the user selects the profile
    // picture
    @Override
    public void onImageClick(View view, int position) {
        Log.d(LOG_TAG, position + " clicked");
        // pass the tweet object back to it's activity using the listener in this class
        User user = tweets.get(position).getUser();
        ((OnImageSelectedListener) getActivity()).onImageClick(user);
    }

    @Override
    public void onTweetRowClick(View view, int position) {
        Tweet tweet = tweets.get(position);
        ((OnTweetSelectedListener) getActivity()).onTweetClick(tweet);
    }

    @Override
    public void onSpanNameClick(String name) {
        ((OnSpanNameClickedListener) getActivity()).onSpanNameClick(name);
    }

    @Override
    public void onSpanTagClick(String hashTag) {
        ((OnSpanTagClickedListener) getActivity()).onSpanTagClick(hashTag);
    }

    @Override
    public void onReplyClick(View view, int position) {
        Tweet tweet = tweets.get(position);
        ((OnReplyClickedListener) getActivity()).onReplyClicked(tweet);
    }

    public abstract void loadMorePage(long maxId);

    // you keep saving the data, mentioned, clicked.... to different database...?
    // then you load from the database once it's loaded...?
    // async transaction: saving the data in bulk for offline support
    private void saveToDataBase(List<Tweet> tweets) {

        // instantiate ProcessModelTransaction
        ProcessModelTransaction<Tweet> processModelTransaction = new ProcessModelTransaction
                .Builder<>(new ProcessModelTransaction.ProcessModel<Tweet>() {
            @Override
            public void processModel(Tweet tweet, DatabaseWrapper wrapper) {
                // saving to database
                tweet.save();
            }
        }).processListener(new ProcessModelTransaction.OnModelProcessListener<Tweet>() {
            @Override
            public void onModelProcessed(long current, long total, Tweet modifiedModel) {

            }
        }).addAll(tweets).build();

        // DatabaseDefinition: The main interface that all Database implementations extend from.
        DatabaseDefinition database = FlowManager.getDatabase(SimpleTweetsDatabase.class);
        // Transaction represents a transaction that occurs in the database. This is a handy
        // class that allows you to wrap up a set of database modification (or queries) int a
        // code block that gets accessed all on the same thread, in the same queue.(prevent
        // locking and synchronization issue for writing/reading at the same time)
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
                        Log.d(LOG_TAG, "transaction error: " + error.getMessage());

                    }
                })
                .build();

        transaction.execute();
    }


}
