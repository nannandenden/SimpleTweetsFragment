package com.codepath.apps.simpletweetsfragment.adapter;

import android.content.Context;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.codepath.apps.simpletweetsfragment.R;
import com.codepath.apps.simpletweetsfragment.databinding.ItemTweetBinding;
import com.codepath.apps.simpletweetsfragment.models.Tweet;
import com.codepath.apps.simpletweetsfragment.utils.PatternEditableBuilder;

import java.util.List;
import java.util.regex.Pattern;

/**
 * Created by nanden on 9/26/17.
 */

public class TweetAdapter extends RecyclerView.Adapter<TweetAdapter.ViewHolder>{

    // for setup the listenerImage to pass back to the fragment
    private OnProfileImageClickListener imageClickListener;
    public interface OnProfileImageClickListener {

        void onImageClick(View view, int position);
    }

    public void setOnProfileImageClickListener(OnProfileImageClickListener imageClickListener) {
        this.imageClickListener = imageClickListener;
    }
    // for clicking something like @codepath
    private OnSpanNameClickListener nameClickListener;
    public interface OnSpanNameClickListener {
        void onSpanNameClick(String name);
    }
    public void setOnSpanNameClickListener(OnSpanNameClickListener nameClickListener) {
        this.nameClickListener = nameClickListener;
    }
    // interface for reply
    private OnReplyClickListener replyClickListener;
    public interface OnReplyClickListener {
        void onReplyClick(View view, int position);
    }
    public void setOnReplyClickListener(OnReplyClickListener replyClickListener) {
        this.replyClickListener = replyClickListener;
    }

    // for clicking something like #codepath
    private OnSpanTagClickListener tagClickListener;
    public interface OnSpanTagClickListener {
        void onSpanTagClick(String hashTag);
    }
    public void setOnSpanTagClickListener(OnSpanTagClickListener tagClickListener) {
        this.tagClickListener = tagClickListener;
    }
    // set up row click to show the tweet detail
    private OnTweetRowClickListener tweetRowClickListener;
    public interface OnTweetRowClickListener {
        void onTweetRowClick(View view, int position);

    }
    public void setOnTweetRowClickListener(OnTweetRowClickListener tweetRowClickListener) {
        this.tweetRowClickListener = tweetRowClickListener;
    }


    private static final String LOG_TAG = TweetAdapter.class.getSimpleName();
    private Context context;
    private List<Tweet> tweets;

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
        holder.bind(tweet);
    }

    @Override
    public int getItemCount() {
        return tweets.size();
    }

    // bind the tweet object value with the references
    // normally we use static for viewholder class to avoid memory leak
    // define as non-static object since we need to access listenerImage event
    public class ViewHolder extends RecyclerView.ViewHolder {
        final ItemTweetBinding binding;

        public ViewHolder(View itemView) {
            super(itemView);
            binding = ItemTweetBinding.bind(itemView);

        }

        public void bind(Tweet tweet) {
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    tweetRowClickListener.onTweetRowClick(v, getAdapterPosition());
                }
            });
            binding.ivProfileImage.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    imageClickListener.onImageClick(v, getAdapterPosition());
                }
            });
            binding.ivReply.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    replyClickListener.onReplyClick(v, getAdapterPosition());
                }
            });
            // populate the view according to the data
            binding.setTweet(tweet);
            binding.executePendingBindings();
            new PatternEditableBuilder().
                    addPattern(Pattern.compile("\\@(\\w+)"), Color.BLUE, new PatternEditableBuilder
                            .SpannableClickedListener() {
                        @Override
                        public void onSpanClicked(String text) {
                            nameClickListener.onSpanNameClick(text);
                        }
                    }).
                    addPattern(Pattern.compile("\\#(\\w+)"), Color.BLUE, new PatternEditableBuilder
                            .SpannableClickedListener() {
                        @Override
                        public void onSpanClicked(String text) {
                            tagClickListener.onSpanTagClick(text);
                        }
                    }).
                    into(binding.tvBody);
        }
    }

}
