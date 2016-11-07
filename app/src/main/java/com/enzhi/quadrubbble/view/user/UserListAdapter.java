package com.enzhi.quadrubbble.view.user;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.enzhi.quadrubbble.R;
import com.enzhi.quadrubbble.models.User;
import com.enzhi.quadrubbble.utils.ModelUtils;

import java.util.List;

/**
 * Created by enzhi on 9/29/2016.
 */
public class UserListAdapter extends RecyclerView.Adapter {
    UserListFragment userListFragment;
    private List<User> data;
    public boolean showLoading = true;
    private LoadMoreListener loadMoreListener;
    private final Context context;

    public int VIEW_TYPE_LOADING = 1;
    public int VIEW_TYPE_USER = 0;

    public UserListAdapter(UserListFragment userListFragment, List<User> data, LoadMoreListener loadMoreListener) {
        this.userListFragment = userListFragment;
        context = userListFragment.getContext();
        this.data = data;
        this.loadMoreListener = loadMoreListener;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if(viewType == VIEW_TYPE_USER) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_user, parent, false);
            return new UserViewHolder(view);
        }else{
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.loading, parent, false);
            return new RecyclerView.ViewHolder(view){};
        }
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if(getItemViewType(position) == VIEW_TYPE_LOADING){
            loadMoreListener.onLoadMore();
            return;
        }else {
            final User user = data.get(position);
            ((UserViewHolder) holder).listUserPortrait.setImageURI(user.avatar_url);
            ((UserViewHolder) holder).userName.setText(user.name);
            ((UserViewHolder) holder).userBio.setText(user.bio == null ? "" : Html.fromHtml(user.bio));
            ((UserViewHolder) holder).userButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    //ShotListFragment.shotClicked = position;
                    Intent intent = new Intent(getContext(), UserActivity.class);
                    intent.putExtra("userData", ModelUtils.toString(user));
                    userListFragment.startActivityForResult(intent, UserListFragment.REQ_CODE_USER);
                }
            });
        }
    }

    @Override
    public int getItemViewType(int position) {
        if(position == data.size()) return VIEW_TYPE_LOADING;
        else return VIEW_TYPE_USER;
    }

    public void setShowLoading(boolean showLoading){
        this.showLoading = showLoading;
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return showLoading ? data.size() + 1 : data.size();
    }

    public int getDataSize() {
        return data.size();
    }

    public void setData(List<User> data) {
        this.data = data;
    }

    public void append(List<User> moreData) {
        data.addAll(moreData);
    }

    public List<User> getData() {
        return data;
    }

    public interface LoadMoreListener{
        void onLoadMore();
    }

    public Context getContext() {
        return context;
    }
}
