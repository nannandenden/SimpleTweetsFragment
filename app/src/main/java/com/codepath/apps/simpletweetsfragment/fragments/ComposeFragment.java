package com.codepath.apps.simpletweetsfragment.fragments;

import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.TextView;

import com.codepath.apps.simpletweetsfragment.R;
import com.codepath.apps.simpletweetsfragment.databinding.FragmentComposeBinding;
import com.codepath.apps.simpletweetsfragment.models.Tweet;
import com.codepath.apps.simpletweetsfragment.network.TwitterApp;
import com.codepath.apps.simpletweetsfragment.network.TwitterClient;
import com.codepath.apps.simpletweetsfragment.utils.Utils;
import com.google.gson.Gson;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.json.JSONObject;

import cz.msebera.android.httpclient.Header;

/**
 * Created by nanden on 9/27/17.
 */

public class ComposeFragment extends DialogFragment implements TextView.OnEditorActionListener {
    // define the interface for passing data back to activity
    public interface EditTweetDialogListener {

        void onFinishEditTweet(Tweet tweet);

    }

    private static final String LOG_TAG = ComposeFragment.class.getSimpleName();
    private FragmentComposeBinding binding;
    private TwitterClient client;
    private String id;
    private String screenName;
    public ComposeFragment() {

    }
    public static ComposeFragment newInstance(String id, String screenName) {
        Bundle args = new Bundle();
        ComposeFragment fragment = new ComposeFragment();
        args.putString("id", id);
        args.putString("screen_name", screenName);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        id = getArguments().getString("id");
        screenName = getArguments().getString("screen_name");
        Log.d(LOG_TAG, "id: " + id + "\tscreen_name: " + screenName);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_compose, container, false);
        client = TwitterApp.getRestClient();
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        binding.ivCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });
        binding.etTweet.requestFocus();
        binding.etTweet.setImeOptions(EditorInfo.IME_ACTION_SEND);
        binding.etTweet.setRawInputType(InputType.TYPE_CLASS_TEXT);
        if (screenName != null) {
            binding.etTweet.setText("@" + screenName + "\n");
        }
        int position = binding.etTweet.length();
        binding.etTweet.setSelection(position);
        binding.etTweet.setOnEditorActionListener(this);
        binding.tilCompose.getEditText().addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence text, int start, int before, int count) {
                if (text.length() > 140) {
                    binding.tilCompose.setErrorEnabled(true);
                    binding.tilCompose.setError(getString(R.string.exceeding_max_text));
                } else {
                    binding.tilCompose.setErrorEnabled(false);
                }
            }
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override
            public void afterTextChanged(Editable s) {}
        });

    }

    @Override
    public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
        if (EditorInfo.IME_ACTION_SEND == actionId) {
            final EditTweetDialogListener listener = (EditTweetDialogListener) getActivity();
            Log.d(LOG_TAG, binding.etTweet.getText().toString());
            if (binding.etTweet.getText().length() <= 140) {

                JsonHttpResponseHandler handler = new JsonHttpResponseHandler() {
                    @Override
                    public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                        Log.d(LOG_TAG, "JSONObject success: " + response.toString());
                        Tweet tweet = new Gson().fromJson(response.toString(), Tweet.class);
                        listener.onFinishEditTweet(tweet);
                    }

                    @Override
                    public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                        Log.d(LOG_TAG, "JSONObject fail: " + errorResponse.toString());

                    }
                };
                client.postTweet(handler, binding.etTweet.getText().toString(), id);
                // Dismiss the fragment and its dialog.
                dismiss();
            } else {
                Log.d(LOG_TAG, "exceeding the max characters");
                // potentially I can add snackbar here
                Utils.showToast(getContext(), "exceeding the max characters");
            }
        }
        return false;
    }
}
