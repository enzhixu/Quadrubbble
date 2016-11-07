package com.enzhi.quadrubbble.view.shot_list;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.enzhi.quadrubbble.R;

/**
 * Created by enzhi on 9/22/2016.
 */
public class RetryFragment extends Fragment {

    public static RetryFragment newInstance(){
        return new RetryFragment();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_retry, container, false);
        return view;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        TextView test = (TextView) view.findViewById(R.id.test);
        test.setText("FFFFFFFFFFFFFFFFFFFFFF");
    }
}
