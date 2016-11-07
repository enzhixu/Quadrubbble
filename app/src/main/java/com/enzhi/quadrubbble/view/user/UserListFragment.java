package com.enzhi.quadrubbble.view.user;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.os.AsyncTaskCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.enzhi.quadrubbble.Dribbble;
import com.enzhi.quadrubbble.R;
import com.enzhi.quadrubbble.models.User;
import com.enzhi.quadrubbble.view.shot_list.SpaceItemDecoration;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by enzhi on 9/29/2016.
 */
public class UserListFragment extends Fragment {

    RecyclerView userListView;

    UserListAdapter adapter;
    SwipeRefreshLayout swipeRefreshLayout;

    public static final int REQ_CODE_USER = 101;

    public static UserListFragment newInstance() {
        UserListFragment fragment = new UserListFragment();
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_recyclerview, container, false);
        return view;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {

        userListView = (RecyclerView) view.findViewById(R.id.recycler_view);

        userListView.addItemDecoration(new SpaceItemDecoration(
                getResources().getDimensionPixelSize(R.dimen.spacing_small)));
        userListView.setLayoutManager(new LinearLayoutManager(getContext()));

        adapter = new UserListAdapter(this, new ArrayList<User>(), new UserListAdapter.LoadMoreListener() {
            @Override
            public void onLoadMore() {
                AsyncTaskCompat.executeParallel(new LoadFolloweeTask(adapter.getDataSize() / Dribbble.COUNT_PER_LOAD + 1, false));
            }
        });

        userListView.setAdapter(adapter);

        //下拉刷新
        swipeRefreshLayout = (SwipeRefreshLayout)view.findViewById(R.id.swipe_refresh_container);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                Toast.makeText(getContext(),"Refreshing", Toast.LENGTH_LONG);
                AsyncTaskCompat.executeParallel(new LoadFolloweeTask(1, true));
            }
        });

    }

    public class LoadFolloweeTask extends AsyncTask<Void, Void, List<User>>{
        int page;
        boolean refresh;

        LoadFolloweeTask(int page, boolean refresh){
            this.page = page;
            this.refresh = refresh;
        }

        @Override
        protected List<User> doInBackground(Void... voids) {
            try {
                return Dribbble.getFollowees(page);
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(List<User> users) {
            if(users != null) {
                if(users.size() < Dribbble.COUNT_PER_LOAD){
                    adapter.setShowLoading(false);
                }
                if(refresh){
                    adapter.setData(users);
                    swipeRefreshLayout.setRefreshing(false);
                }else {
                    adapter.append(users);
                }

            }else{
                Toast.makeText(getContext(), "Loading error.", Toast.LENGTH_LONG);
            }
        }
    }
}
