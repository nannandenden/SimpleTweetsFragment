package com.codepath.apps.simpletweetsfragment.fragments;

import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;

import com.codepath.apps.simpletweetsfragment.R;
import com.codepath.apps.simpletweetsfragment.databinding.FragmentComposeBinding;
import com.codepath.apps.simpletweetsfragment.models.Tweet;
import com.codepath.apps.simpletweetsfragment.network.TwitterApp;
import com.codepath.apps.simpletweetsfragment.network.TwitterClient;
import com.google.gson.Gson;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.json.JSONObject;

import cz.msebera.android.httpclient.Header;

/**
 * Created by nanden on 9/27/17.
 */

public class ComposeFragment extends DialogFragment implements View.OnClickListener {
    // define the interface for passing data back to activity
    public interface EditTweetDialogListener {

        void onFinishEditTweet(Tweet tweet);

    }
    private static final String LOG_TAG = ComposeFragment.class.getSimpleName();
    FragmentComposeBinding binding;
    private TwitterClient client;
    public ComposeFragment() {

    }

    public static ComposeFragment newInstance() {

        Bundle args = new Bundle();

        ComposeFragment fragment = new ComposeFragment();
        fragment.setArguments(args);
        return fragment;
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
        binding.btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });
        binding.btnTweet.setOnClickListener(this);
        binding.etTweet.requestFocus();
        binding.etTweet.setImeOptions(EditorInfo.IME_ACTION_DONE);
        binding.etTweet.setRawInputType(InputType.TYPE_CLASS_TEXT);
        int position = binding.etTweet.length();
        binding.etTweet.setSelection(position);
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
    public void onClick(View v) {
        Log.d(LOG_TAG, "clicked!");
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
            client.postTweet(handler, binding.etTweet.getText().toString());
            dismiss();
        } else {
            Log.d(LOG_TAG, "exceeding the mex characters");
            // potentially I can add snackbar here
        }
    }

}
