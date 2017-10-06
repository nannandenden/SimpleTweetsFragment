package com.codepath.apps.simpletweetsfragment.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.codepath.apps.simpletweetsfragment.R;
import com.codepath.apps.simpletweetsfragment.databinding.ItemUserBinding;
import com.codepath.apps.simpletweetsfragment.models.User;

import java.util.List;

/**
 * Created by nanden on 10/6/17.
 */

public class UserAdapter extends RecyclerView.Adapter<UserAdapter.ViewHolder> {

    private Context context;
    private List<User> users;

    public UserAdapter(Context context, List<User> users) {
        this.context = context;
        this.users = users;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View userView = LayoutInflater.from(this.context).inflate(R.layout.item_user, parent,
                false);
        return new ViewHolder(userView);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        User user = users.get(position);
        holder.bind(user);
    }

    @Override
    public int getItemCount() {
        return users.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        ItemUserBinding binding;

        public ViewHolder(View itemView) {
            super(itemView);
            binding = ItemUserBinding.bind(itemView);
        }

        public void bind(User user) {
            binding.setUser(user);
            binding.executePendingBindings();
        }
    }
}
