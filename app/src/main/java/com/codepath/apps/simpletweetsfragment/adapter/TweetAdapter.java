package com.codepath.apps.simpletweetsfragment.adapter;

import android.content.Context;
import android.databinding.BindingAdapter;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.codepath.apps.simpletweetsfragment.R;
import com.codepath.apps.simpletweetsfragment.databinding.ItemTweetBinding;
import com.codepath.apps.simpletweetsfragment.models.Tweet;

import java.util.List;

/**
 * Created by nanden on 9/26/17.
 */

public class TweetAdapter extends RecyclerView.Adapter<TweetAdapter.ViewHolder>{

    private static final String LOG_TAG = TweetAdapter.class.getSimpleName();

    Context context;
    List<Tweet> tweets;

    // pass in the Tweets array in the constructor to use it
    public TweetAdapter(Context context, List<Tweet> tweets) {
        this.context = context;
        this.tweets = tweets;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // for each row, inflate the layout and pass to tweetView
        View tweetView = LayoutInflater.from(this.context).inflate(R.layout.item_tweet, parent, false);
        // pass the tweetView(cached reference) to the viewholder
        return new ViewHolder(tweetView);
    }

    // this method will be called after onCreateViewHOlder is called
    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        // get data according to the position
        Tweet tweet = tweets.get(position);
        Log.d(LOG_TAG, String.valueOf(tweet.getId()));
        // populate the view according to the data
        holder.binding.setTweet(tweet);
        holder.binding.executePendingBindings();
    }

    @Override
    public int getItemCount() {
        return tweets.size();

    }

    // bind the tweet object value with the references

    public static class ViewHolder extends RecyclerView.ViewHolder {
        final ItemTweetBinding binding;

        public ViewHolder(View itemView) {
            super(itemView);
            binding = ItemTweetBinding.bind(itemView);

        }

    }

    public static class BindingAdapterUtils {
        @BindingAdapter({"bind:imageUrl"})
        public static void loadImage(ImageView imageView, String url) {
            Glide.with(imageView.getContext()).load(url).into(imageView);
        }
    }

}
