package com.enzhi.quadrubbble.view.user;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.widget.Toast;

import com.enzhi.quadrubbble.R;
import com.enzhi.quadrubbble.models.User;
import com.enzhi.quadrubbble.utils.ModelUtils;
import com.enzhi.quadrubbble.view.shot_list.ShotListFragment;
import com.google.gson.reflect.TypeToken;

/**
 * Created by enzhi on 9/27/2016.
 */
public class UserShotListActivity extends AppCompatActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_single_fragment);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);


        String dataString = getIntent().getStringExtra("user");
        final User user = ModelUtils.toObject(dataString, new TypeToken<User>(){});

        Toast.makeText(this, "User ID: " + user.id + ", \n and User Name: " + user.name, Toast.LENGTH_LONG).show();
        setTitle("User: " + user.name);






        Fragment fragment = ShotListFragment.newInstance(ShotListFragment.LIST_TYPE_USER, null, user); //這裏可能不能傳user
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
