package com.enzhi.quadrubbble.view.user;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.os.AsyncTaskCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.enzhi.quadrubbble.Dribbble;
import com.enzhi.quadrubbble.R;
import com.enzhi.quadrubbble.models.User;
import com.enzhi.quadrubbble.utils.ModelUtils;
import com.facebook.drawee.view.SimpleDraweeView;
import com.google.gson.reflect.TypeToken;

import java.io.IOException;

/**
 * Created by enzhi on 9/27/2016.
 */
public class UserActivity extends AppCompatActivity {
    User user;
    boolean isFollowing;
    Menu menu;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.user);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        String userData = getIntent().getStringExtra("userData");
        user = ModelUtils.toObject(userData, new TypeToken<User>(){});
        //user.id = getIntent().getStringExtra("userId");
        //user.name = getIntent().getStringExtra("userName");
        setTitle("User: " + user.name);

        TextView userLocation = (TextView) findViewById(R.id.user_location);
        userLocation.setText(user.location);
        AsyncTaskCompat.executeParallel(new CheckFollowingTask());

        SimpleDraweeView userPortrait = (SimpleDraweeView) findViewById(R.id.user_portrait);
        userPortrait.setImageURI(user.avatar_url);

        TextView userName = (TextView) findViewById(R.id.user_name);
        userName.setText(user.name);

        TextView shotsButton = (TextView) findViewById(R.id.user_shots_button);
        shotsButton.setText("Shots");
        shotsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(UserActivity.this, UserShotListActivity.class);
                intent.putExtra("user", ModelUtils.toString(user));
                startActivity(intent);
            }
        });

        TextView userWeb = (TextView) findViewById(R.id.user_web);
        TextView userTwitter = (TextView) findViewById(R.id.user_twitter);

        userWeb.setText(user.links.get("web"));
        userTwitter.setText(user.links.get("twitter"));

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_user, menu);
        this.menu = menu;
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()){
            case android.R.id.home:
                finish();
                return true;
            case R.id.follow:
                AsyncTaskCompat.executeParallel(new FollowTask());
                return true;

        }
        return super.onOptionsItemSelected(item);
    }

    private class CheckFollowingTask extends AsyncTask<Void, Void, Boolean>{

        @Override
        protected Boolean doInBackground(Void... voids) {
            try {
                user = Dribbble.getOtherUser(user.id);
                isFollowing = Dribbble.checkFollowing(user.id);
                return isFollowing;
            } catch (IOException e) {
                e.printStackTrace();
                return false;
            }
        }

        @Override
        protected void onPostExecute(Boolean isFollowing) {
            if (isFollowing) {
                menu.findItem(R.id.follow).setTitle(getResources().getString(R.string.unfollow));
            }
        }
    }

    private class FollowTask extends AsyncTask<Void, Void, Boolean> {
        @Override
        protected Boolean doInBackground(Void... voids) {
            try {
                if (isFollowing) {
                    Dribbble.unfollow(user.id);
                } else {
                    Dribbble.follow(user.id);
                }
                return true;
            } catch (IOException e) {
                e.printStackTrace();
                return false;
            }
        }

        @Override
        protected void onPostExecute(Boolean aBoolean) {
            isFollowing = !isFollowing;
            if (isFollowing) {
                menu.findItem(R.id.follow).setTitle(getResources().getString(R.string.unfollow));
            } else {
                menu.findItem(R.id.follow).setTitle(getResources().getString(R.string.follow));
            }
        }
    }

}
