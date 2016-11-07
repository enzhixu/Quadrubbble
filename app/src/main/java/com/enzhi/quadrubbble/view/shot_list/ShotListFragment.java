package com.enzhi.quadrubbble.view.shot_list;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.os.AsyncTaskCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.enzhi.quadrubbble.Dribbble;
import com.enzhi.quadrubbble.R;
import com.enzhi.quadrubbble.models.Shot;
import com.enzhi.quadrubbble.models.User;
import com.enzhi.quadrubbble.utils.ModelUtils;
import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by enzhi on 9/19/2016.
 */
public class ShotListFragment extends Fragment {
    public static final int REQ_CODE_SHOT = 100;
    public static int shotClicked = -1;
    public static final int LIST_TYPE_POPULAR = 1;
    public static final int LIST_TYPE_LIKED = 2;
    public static final int LIST_TYPE_BUCKET = 3;
    public static final int LIST_TYPE_USER = 4;
    public static final String KEY_LIST_TYPE = "listType";
    public static final String KEY_BUCKET_ID = "bucketId";
    public static final String KEY_USER_DATA = "userData";
    public String bucketId = null;
    public User user = null;

    private int listType;
    ShotListAdapter adapter;
    RecyclerView shotList;
    SwipeRefreshLayout swipeRefreshLayout;
    Parcelable layoutManagerSavedState;


    //這個方法幹什麼用的？？？
    //和老師的不同，有點虛
    public static ShotListFragment newInstance(int listType, String bucketId, User user){
        Bundle args = new Bundle();             //看起來newInstance的唯一目的是為了傳入數據應付屏幕旋轉
        args.putInt(KEY_LIST_TYPE, listType);
        if (listType == LIST_TYPE_BUCKET) {
            args.putString(KEY_BUCKET_ID, bucketId);
        } else if(listType == LIST_TYPE_USER) {
            args.putString(KEY_USER_DATA, ModelUtils.toString(user));
        }
        ShotListFragment fragment = new ShotListFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_recyclerview, container, false);     //false 什麼意思？？？
        if(listType == LIST_TYPE_USER) {
            user = ModelUtils.toObject(getArguments().getString("user"), new TypeToken<User>(){});
        }
        return view;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {

        //以下讀取數據
        Bundle args = getArguments();
        listType = args.getInt(KEY_LIST_TYPE);
        if (listType == LIST_TYPE_BUCKET) {
            bucketId = args.getString(KEY_BUCKET_ID);
        } else if(listType == LIST_TYPE_USER) {
            user = ModelUtils.toObject(args.getString(KEY_USER_DATA), new TypeToken<User>(){});
        }

        ArrayList<Shot> data;
        if (savedInstanceState == null) {
            data = new ArrayList<>();
        } else {
            //以下為獲取轉屏前的數據
            String dataString = savedInstanceState.getString("dataString");
            data = ModelUtils.toObject(dataString, new TypeToken<ArrayList<Shot>>(){});
        }


        shotList = (RecyclerView) view.findViewById(R.id.recycler_view);

        shotList.addItemDecoration(new SpaceItemDecoration(
                getResources().getDimensionPixelSize(R.dimen.spacing_small)));
        shotList.setLayoutManager(new LinearLayoutManager(getContext()));

        adapter = new ShotListAdapter(this, data, listType, user, new ShotListAdapter.LoadMoreListener() {
            @Override
            public void onLoadMore() {
                AsyncTaskCompat.executeParallel(new LoadShotTask(adapter.getDataSize() / Dribbble.COUNT_PER_LOAD + 1, false));
            }
        });
        shotList.setAdapter(adapter);

        //
        if (savedInstanceState != null) {
            shotList.getLayoutManager().onRestoreInstanceState(layoutManagerSavedState);
        }

        //下拉刷新
        swipeRefreshLayout = (SwipeRefreshLayout)view.findViewById(R.id.swipe_refresh_container);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                Toast.makeText(getContext(),"Refreshing", Toast.LENGTH_LONG);
                AsyncTaskCompat.executeParallel(new LoadShotTask(1, true));
            }
        });
    }

    @Override
    public void onViewStateRestored(@Nullable Bundle savedInstanceState) {
        if (savedInstanceState != null) {
            layoutManagerSavedState = ((Bundle) savedInstanceState).getParcelable("saved_layout_manager");
        }
        super.onViewStateRestored(savedInstanceState);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        //保存fragment數據以便轉屏後可以獲取
        outState.putString("dataString", ModelUtils.toString(adapter.getData()));
        outState.putParcelable("saved_layout_manager", shotList.getLayoutManager().onSaveInstanceState());
    }

    private class LoadShotTask extends AsyncTask<Void, Void, List<Shot>>{
        int page;
        boolean refresh;

        public LoadShotTask(int page, boolean refresh) {
            this.page = page;
            this.refresh = refresh;
        }

        @Override
        protected List<Shot> doInBackground(Void... voids){
            try {
                switch(listType) {
                    case LIST_TYPE_POPULAR:
                        return Dribbble.getShots(page);
                    case LIST_TYPE_LIKED:
                        return Dribbble.getLikedShots(page);
                    case LIST_TYPE_BUCKET:
                        return Dribbble.getBucketShots(bucketId, page);
                    case LIST_TYPE_USER:
                        return Dribbble.getUserShots(user.id, page);
                }
            } catch(IOException | JsonSyntaxException e) {           //這裏的 | 是什麼？？？
                e.printStackTrace();
                return null;
            }
            return null;
        }

        @Override
        protected void onPostExecute(List<Shot> shotList) {
            if(shotList != null) {
                if(shotList.size() < Dribbble.COUNT_PER_LOAD){
                    adapter.setShowLoading(false);
                }
                if(refresh){
                    adapter.setData(shotList);
                    swipeRefreshLayout.setRefreshing(false);
                }else {
                    adapter.append(shotList);
                }
            }else{
                Toast.makeText(getContext(), "Loading error.", Toast.LENGTH_LONG);
            }
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode == REQ_CODE_SHOT && resultCode == Activity.RESULT_OK){
            String shotId = data.getStringExtra("shotId");
            int likeCount = data.getIntExtra("likeCount", 0);
            int bucketCount = data.getIntExtra("bucketCount", 0);

            for (Shot shot : adapter.getData()) {
                if (TextUtils.equals(shot.id, shotId)) {
                    shot.likes_count = likeCount;
                    //shot.buckets_count = bucketCount;
                    adapter.notifyDataSetChanged();
                    return;
                }
            }

        }
        adapter.notifyDataSetChanged();
    }
}
