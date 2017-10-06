package com.codepath.apps.simpletweetsfragment.fragments;

import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.codepath.apps.simpletweetsfragment.R;
import com.codepath.apps.simpletweetsfragment.adapter.UserAdapter;
import com.codepath.apps.simpletweetsfragment.databinding.FragmentUsersListBinding;
import com.codepath.apps.simpletweetsfragment.models.User;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONArray;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by nanden on 10/6/17.
 */

public class UsersListFragment extends Fragment {

    private static final String LOG_TAG = UsersListFragment.class.getSimpleName();
    private FragmentUsersListBinding binding;
    private UserAdapter userAdapter;
    private List<User> users;
    private RecyclerView rvUsers;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_users_list, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        users = new ArrayList<>();
        userAdapter = new UserAdapter(getContext(), users);
        rvUsers = binding.rvUsers;
        rvUsers.setLayoutManager(new LinearLayoutManager(getContext()));
        rvUsers.setAdapter(userAdapter);
    }
    public void addList(JSONArray responseArray) {
        Type userType = new TypeToken<ArrayList<User>>(){}.getType();
        List<User> userList = new Gson().fromJson(responseArray.toString(), userType);
        addList(userList);
    }
    public void addList(List<User> userList) {
        int startIndex = 0;
        if (!this.users.isEmpty()) {
            startIndex = userList.size();
        }
        users.addAll(userList);
        userAdapter.notifyItemRangeInserted(startIndex, this.users.size()-1);
    }

}
