package com.codepath.apps.simpletweetsfragment.fragments;
// support v4 supports backword compatibility

import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.codepath.apps.simpletweetsfragment.R;
import com.codepath.apps.simpletweetsfragment.adapter.TweetAdapter;
import com.codepath.apps.simpletweetsfragment.databinding.FragmentsTweetsListBinding;
import com.codepath.apps.simpletweetsfragment.models.Tweet;
import com.codepath.apps.simpletweetsfragment.models.User;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by nanden on 10/3/17.
 */

public class TweetsListFragment extends Fragment implements TweetAdapter.OnProfileImageClickListener {

    // define the interface to communicate it's activity
    public interface OnTweetSelectedListener {
        void onTweetClick(User user);
    }

    private static final String LOG_TAG = TweetsListFragment.class.getSimpleName();
    private FragmentsTweetsListBinding binding;
    private TweetAdapter tweetAdapter;
    private List<Tweet> tweets;
    private RecyclerView rvTweets;

    // inflation happens inside onCreateview
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragments_tweets_list, container,
                false);
        setView();
        return binding.getRoot();
    }

    private void setView() {

        tweets = new ArrayList<>(); // init array list and this is data source
        tweetAdapter = new TweetAdapter(getContext(), tweets, this); // construct the adapter using
        // the data
        rvTweets = binding.rvTweets;
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        rvTweets.setLayoutManager(linearLayoutManager);
        rvTweets.setAdapter(tweetAdapter);
//        rvTweets.addOnScrollListener(new EndlessRecyclerViewScrollListener(linearLayoutManager) {
//            @Override
//            public void onLoadMore(int currentPage, int totalItemCount, RecyclerView recyclerView) {
//
//                int lastPosition = tweets.size()-1;
//                long maxId = tweets.get(lastPosition).getId();
//
//                Log.d(LOG_TAG, "maxId: " + maxId);
//                Log.d(LOG_TAG, "onLoadMore called. currentPage: " + currentPage);
//                JsonHttpResponseHandler handler = new JsonHttpResponseHandler() {
//
//                    @Override
//                    public void onSuccess(int statusCode, Header[] headers, JSONArray response) {
//                        Log.d(LOG_TAG, "Success! loading more page");
//                        Type tweetType = new TypeToken<ArrayList<Tweet>>(){}.getType();
//                        List<Tweet> tweetList = new Gson().fromJson(response.toString(), tweetType);
//                        tweets.addAll(tweetList);
//                        tweetAdapter.notifyDataSetChanged();
//
//                    }
//
//                    @Override
//                    public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONArray errorResponse) {
//                        Log.d(LOG_TAG, "onFailure" + errorResponse.toString());
//                        throwable.printStackTrace();
//
//                    }
//
//                };
//                client.getHomeTimeline(handler, (long)tweets.get(lastPosition).getId());
//            }
//        });
    }

    public void addList(JSONArray response) {
        Log.d(LOG_TAG, "JSONObject success: " + response.toString());
        if (response.length() == 1) {
            // handle the case where there is only one object inside the array
            JSONObject object = null;
            try {
                 object = response.getJSONObject(0);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            Tweet tweet = new Gson().fromJson(object.toString(), Tweet.class);
            tweets.add(0, tweet);
            tweetAdapter.notifyItemInserted(0);
        } else {
            Type tweetType = new TypeToken<ArrayList<Tweet>>(){}.getType();
            List<Tweet> tweetList = new Gson().fromJson(response.toString(), tweetType);
            addList(tweetList);
        }


    }

    public void addList(List<Tweet> list) {
        tweets.addAll(list);
        tweetAdapter.notifyItemRangeInserted(0, tweets.size()-1);
    }

    public void addNewTweet(Tweet tweet) {
        tweets.add(0, tweet);
        tweetAdapter.notifyItemInserted(0);
        rvTweets.scrollToPosition(0);
    }

    // fire the event to pass the screenName to the activity when the user selects the profile
    // picture
    @Override
    public void onImageClick(View view, int position) {
        Log.d(LOG_TAG, position + " clicked");
        // pass the tweet object back to it's activity using the listener in this class
        User user = tweets.get(position).getUser();
        ((OnTweetSelectedListener) getActivity()).onTweetClick(user);
    }
}
