package com.enzhi.quadrubbble.view.bucket_list;

import android.content.Intent;
import android.support.v4.app.FragmentActivity;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.enzhi.quadrubbble.R;
import com.enzhi.quadrubbble.models.Bucket;

import java.util.List;

/**
 * Created by enzhi on 9/24/2016.
 */
public class BucketListAdapter extends RecyclerView.Adapter {
    public int VIEW_TYPE_LOADING = 1;
    public int VIEW_TYPE_BUCKET = 0;
    boolean choosingMode;
    private FragmentActivity activity;
    private boolean showLoading = true;
    private List<Bucket> data;
    private LoadMoreListener loadMoreListener;

    public BucketListAdapter(boolean choosingMode, FragmentActivity activity, List<Bucket> data, LoadMoreListener loadMoreListener){
        this.choosingMode = choosingMode;
        this.activity = activity;
        this.data = data;
        this.loadMoreListener = loadMoreListener;
    }

    public FragmentActivity getActivity(){
        return activity;
    }


    //ViewGroup 是什麼？？？
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == VIEW_TYPE_BUCKET) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_bucket, parent, false);
            return new BucketViewHolder(view);
        } else {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.loading, parent, false);
            return new RecyclerView.ViewHolder(view){};
        }
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (getItemViewType(position) == VIEW_TYPE_LOADING) {
            loadMoreListener.onLoadMore();
        } else {
            final Bucket bucket = data.get(position);
            //System.out.println(bucket.id + ", " + bucket.name);
            ((BucketViewHolder) holder).bucketName.setText(bucket.name);
            ((BucketViewHolder) holder).bucketShotCount.setText(bucket.shots_count + "shots");

            if (choosingMode) {
                ((BucketViewHolder) holder).bucketShotChosen.setChecked(bucket.isChoosing);
                ((BucketViewHolder) holder).bucketShotChosen.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        bucket.isChoosing = !bucket.isChoosing;
                        notifyDataSetChanged();
                    }
                });
            } else {
                ((BucketViewHolder) holder).bucketShotChosen.setVisibility(View.GONE);
                ((BucketViewHolder) holder).bucketCover.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent = new Intent(getActivity(), BucketShotListActivity.class);
                        intent.putExtra("bucketId", bucket.id);
                        intent.putExtra("bucketName", bucket.name);
                        getActivity().startActivity(intent);

                   /* Fragment fragment = ShotListFragment.newInstance(ShotListFragment.LIST_TYPE_BUCKET, bucket.id);
                    getActivity().getSupportFragmentManager()
                            .beginTransaction()
                            .replace(R.id.fragment_container, fragment) //replace是關鍵，如果是add, 會疊在上面
                            .commit();*/
                    }
                });
            }
        }
    }

    @Override
    public int getItemCount() {
        return showLoading ? data.size() + 1 : data.size();
    }

    public int getDataSize() {
        return data.size();
    }

    public List<Bucket> getData() {
        return data;
    }

    public void setData(List<Bucket> newData){
        data.clear();
        data.addAll(newData);
        notifyDataSetChanged();
    }

    public void append(List<Bucket> moreData){
        data.addAll(moreData);
        notifyDataSetChanged();
    }

    @Override
    public int getItemViewType(int position) {
        if (position == data.size()){
            return VIEW_TYPE_LOADING;
        }else {
            return VIEW_TYPE_BUCKET;
        }
    }

    public void setShowLoading(boolean showLoading){
        this.showLoading = showLoading;
        notifyDataSetChanged();
    }

    public interface LoadMoreListener{
        void onLoadMore();
    }
}
