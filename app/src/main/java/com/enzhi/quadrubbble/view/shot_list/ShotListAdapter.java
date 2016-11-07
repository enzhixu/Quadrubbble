package com.enzhi.quadrubbble.view.shot_list;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.enzhi.quadrubbble.R;
import com.enzhi.quadrubbble.models.Shot;
import com.enzhi.quadrubbble.models.User;
import com.enzhi.quadrubbble.utils.ModelUtils;
import com.enzhi.quadrubbble.view.shot_detail.ShotActivity;

import java.util.List;

/**
 * Created by enzhi on 9/18/2016.
 */
public class ShotListAdapter extends RecyclerView.Adapter {
    public ShotListFragment shotListFragment;
    private final Context context;
    public List<Shot> data;
    public int listType;
    public User user;
    public int VIEW_TYPE_LOADING = 1;
    public int VIEW_TYPE_SHOT = 0;
    public boolean showLoading = true;
    public LoadMoreListener loadMoreListener;
    public ShotListAdapter(ShotListFragment shotListFragment, List<Shot> data, int listType, User user, LoadMoreListener loadMoreListener){
        this.shotListFragment = shotListFragment;
        context = shotListFragment.getContext();
        this.data = data;
        this.listType = listType;
        this.user = user;
        this.loadMoreListener = loadMoreListener;
    }
    public Context getContext(){
        return context;
    }
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if(viewType == VIEW_TYPE_SHOT) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_shot, parent, false);
            return new ShotViewHolder(view);
        }else{
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.loading, parent, false);
            return new RecyclerView.ViewHolder(view){};
        }
    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, final int position) {
        if(getItemViewType(position) == VIEW_TYPE_LOADING){
            loadMoreListener.onLoadMore();
            return;
        }else{
            final Shot shot = data.get(position);

            Uri uri = Uri.parse (shot.getImageUrl());
            ((ShotViewHolder) holder).shotImage.setImageURI(uri);
            ((ShotViewHolder) holder).shotViewCount.setText(String.valueOf(shot.views_count));
            ((ShotViewHolder) holder).shotLikeCount.setText(String.valueOf(shot.likes_count));
            ((ShotViewHolder) holder).shotBucketCount.setText(String.valueOf(shot.buckets_count));

            ((ShotViewHolder) holder).cover.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    ShotListFragment.shotClicked = position;
                    Intent intent = new Intent(getContext(), ShotActivity.class);
                    intent.putExtra("shotData", ModelUtils.toString(shot));
                    if (listType == ShotListFragment.LIST_TYPE_USER) {
                        intent.putExtra("userData", ModelUtils.toString(user));
                    }
                    shotListFragment.startActivityForResult(intent, ShotListFragment.REQ_CODE_SHOT);
                }
            });
        }
    }

    @Override
    public int getItemViewType(int position) {
        if(position == data.size()) return VIEW_TYPE_LOADING;
        else return VIEW_TYPE_SHOT;
    }

    @Override
    public int getItemCount() {
        return showLoading ? data.size() + 1 : data.size();
    }

    public interface LoadMoreListener{
        void onLoadMore();
    }

    public void setData(List<Shot> data){
        this.data.clear();
        this.data.addAll(data);
        notifyDataSetChanged();
    }

    public void append(List<Shot> moreData){
        data.addAll(moreData);
        notifyDataSetChanged();
    }

    public void setShowLoading(boolean showLoading){
        this.showLoading = showLoading;
        notifyDataSetChanged();
    }

    public int getDataSize(){
        return data.size();
    }

    public List<Shot> getData() {
        return data;
    }
}
