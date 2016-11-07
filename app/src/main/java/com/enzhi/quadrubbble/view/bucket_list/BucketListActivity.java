package com.enzhi.quadrubbble.view.bucket_list;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import com.enzhi.quadrubbble.R;

/**
 * Created by enzhi on 9/25/2016.
 */
public class BucketListActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_single_fragment);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);


        String shotId = null;

        boolean choosingMode = getIntent().getBooleanExtra("choosingMode", false);
        if(choosingMode){
            setTitle("Choose bucket");
            shotId = getIntent().getStringExtra("shotId");
        }else{
            setTitle("Buckets");
        }

        if (savedInstanceState == null) {
            Fragment fragment = BucketListFragment.newInstance(choosingMode, shotId);
            getSupportFragmentManager()
                    .beginTransaction()
                    .add(R.id.fragment_container, fragment) //replace是關鍵，如果是add, 會疊在上面
                    .commit();
        }
    }
}
