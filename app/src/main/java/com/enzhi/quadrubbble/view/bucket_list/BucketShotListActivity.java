package com.enzhi.quadrubbble.view.bucket_list;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;

import com.enzhi.quadrubbble.R;
import com.enzhi.quadrubbble.view.shot_list.ShotListFragment;

/**
 * Created by enzhi on 9/25/2016.
 */
public class BucketShotListActivity extends AppCompatActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_single_fragment);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);

        String bucketId = getIntent().getStringExtra("bucketId");
        String bucketName = getIntent().getStringExtra("bucketName");
        setTitle("Bucket: " + bucketName);

        Fragment fragment = ShotListFragment.newInstance(ShotListFragment.LIST_TYPE_BUCKET, bucketId, null);
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fragment_container, fragment) //replace是關鍵，如果是add, 會疊在上面
                .commit();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home){
            finish();
        }
        return true;
    }
}
