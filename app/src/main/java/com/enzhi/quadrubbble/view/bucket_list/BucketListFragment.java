package com.enzhi.quadrubbble.view.bucket_list;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.os.AsyncTaskCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.enzhi.quadrubbble.Dribbble;
import com.enzhi.quadrubbble.R;
import com.enzhi.quadrubbble.models.Bucket;
import com.enzhi.quadrubbble.view.shot_list.SpaceItemDecoration;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * Created by enzhi on 9/24/2016.
 */
public class BucketListFragment extends Fragment {

    BucketListAdapter adapter;
    SwipeRefreshLayout swipeRefreshLayout;
    boolean choosingMode;
    String shotId;
    List<Bucket> buckets;
    Set<String> shotBucketsId = null;
    public static final int REQ_CODE_NEW_BUCKET = 100;
    public static final String KEY_CHOOSING_MODE = "choosingMode";
    public static final String KEY_SHOT_ID = "shotId";

    public static BucketListFragment newInstance(boolean choosingMode, String shotId){
        BucketListFragment fragment = new BucketListFragment();
        Bundle args = new Bundle();
        args.putBoolean(KEY_CHOOSING_MODE, choosingMode);
        if (choosingMode) {
            args.putString(KEY_SHOT_ID, shotId);
        }
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_recyclerview_fab, container, false);
        setHasOptionsMenu(true);        //重要
        return view;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        Bundle args = getArguments();
        choosingMode = args.getBoolean(KEY_CHOOSING_MODE);
        if (choosingMode) {
            shotId = args.getString(KEY_SHOT_ID);
        }

        RecyclerView bucketList = (RecyclerView) view.findViewById(R.id.recycler_view);
        bucketList.addItemDecoration(new SpaceItemDecoration(
                getResources().getDimensionPixelSize(R.dimen.spacing_small)));
        bucketList.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new BucketListAdapter(choosingMode, getActivity(), new ArrayList<Bucket>(), new BucketListAdapter.LoadMoreListener(){
            @Override
            public void onLoadMore() {
                AsyncTaskCompat.executeParallel(
                        new LoadBucketTask(adapter.getDataSize() / Dribbble.COUNT_PER_LOAD + 1, false));
            }
        });

        bucketList.setAdapter(adapter);
        //下拉刷新
        swipeRefreshLayout = (SwipeRefreshLayout)view.findViewById(R.id.swipe_refresh_container);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                Toast.makeText(getContext(),"Refreshing", Toast.LENGTH_LONG);
                AsyncTaskCompat.executeParallel(new LoadBucketTask(1, true));
            }
        });

        FloatingActionButton addBucketButton = (FloatingActionButton) view.findViewById(R.id.fab);
        addBucketButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                //關鍵是繼承自 DialogFragment
                NewBucketDialogFragment newBucketDialogFragment = NewBucketDialogFragment.newInstance();
                newBucketDialogFragment.setTargetFragment(BucketListFragment.this, REQ_CODE_NEW_BUCKET);
                newBucketDialogFragment.show(getFragmentManager(), NewBucketDialogFragment.TAG);
            }
        });
    }

    private class LoadBucketTask extends AsyncTask<Void, Void, List<Bucket>>{
        int page;
        boolean refresh;

        public LoadBucketTask(int page, boolean refresh){
            this.page = page;
            this.refresh = refresh;
        }

        @Override
        protected List<Bucket> doInBackground(Void... voids) {
            try {
                if (choosingMode){
                    shotBucketsId = Dribbble.getShotBucketsId(shotId);
                }
                return Dribbble.getBuckets(page);
            } catch (IOException e){
                e.printStackTrace();
                return null;
            }
        }

        @Override
        protected void onPostExecute(List<Bucket> buckets) {
            if (choosingMode) {
                for (Bucket bucket : buckets){
                    if (shotBucketsId.contains(bucket.id)){
                        bucket.isChoosing = true;
                    }
                }
            }
            if (buckets.size() < Dribbble.COUNT_PER_LOAD) {
                adapter.setShowLoading(false);
            }
            if (refresh) {
                adapter.setData(buckets);
                swipeRefreshLayout.setRefreshing(false);
            }else{
                adapter.append(buckets);
            }
        }
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        if (choosingMode) {
            inflater.inflate(R.menu.bucket_list_choose_mode_menu, menu);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home){
            getActivity().finish();
        }else if (item.getItemId() == R.id.save) {
            saveAndExit();
        }
        return super.onOptionsItemSelected(item);
    }

    public void saveAndExit(){
        buckets = adapter.getData();
        AsyncTaskCompat.executeParallel(new UpdateBucketTask());
        getActivity().finish();
    }

    class UpdateBucketTask extends AsyncTask<Void, Void, Boolean>{

        @Override
        protected Boolean doInBackground(Void... voids) {
            try{
                for (Bucket bucket : buckets){
                    if (bucket.isChoosing) {
                        Dribbble.addBucketShot(bucket.id, shotId);
                    } else {
                        Dribbble.removeBucketShot(bucket.id, shotId);
                    }
                }
            } catch (IOException e){
                e.printStackTrace();
            }
            return true;
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQ_CODE_NEW_BUCKET && resultCode == Activity.RESULT_OK){
            String newBucketName = data.getStringExtra(NewBucketDialogFragment.KEY_BUCKET_NAME);
            String newBucketDescription = data.getStringExtra(NewBucketDialogFragment.KEY_BUCKET_DESCRIPTION);
            if (newBucketName != null) {
                AsyncTaskCompat.executeParallel(new newBucketTask(newBucketName, newBucketDescription));
            }
        }
    }

    class newBucketTask extends  AsyncTask<Void, Void, String>{
        String newBucketName;
        String newBucketDescription;

        public newBucketTask(String newBucketName, String newBucketDescription){
            this.newBucketName = newBucketName;
            this.newBucketDescription = newBucketDescription;
        }

        @Override
        protected String doInBackground(Void... voids) {
            try {
                Dribbble.newBucket(newBucketName, newBucketDescription);
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }
    }
}
