package com.codepath.apps.restclienttemplate.fragments;

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

import com.codepath.apps.restclienttemplate.R;
import com.codepath.apps.restclienttemplate.databinding.FragmentComposeBinding;

/**
 * Created by nanden on 9/27/17.
 */

public class ComposeFragment extends DialogFragment implements View.OnClickListener {
    // define the interface for passing data back to activity
    public interface EditTweetDialogListener {

        void onFinishEditTweet(String input);

    }
    private static final String LOG_TAG = ComposeFragment.class.getSimpleName();
    FragmentComposeBinding binding;
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
        EditTweetDialogListener listener = (EditTweetDialogListener) getActivity();
        Log.d(LOG_TAG, binding.etTweet.getText().toString());
        if (binding.etTweet.getText().length() <= 140) {
            listener.onFinishEditTweet(binding.etTweet.getText().toString());
            dismiss();
        } else {
            //TODO exceeding the max character length, maybe add snackbar to prompt the error
            // message?
        }
    }

}
