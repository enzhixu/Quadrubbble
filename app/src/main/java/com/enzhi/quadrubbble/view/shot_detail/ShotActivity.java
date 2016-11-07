package com.enzhi.quadrubbble.view.shot_detail;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.os.AsyncTaskCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.enzhi.quadrubbble.Dribbble;
import com.enzhi.quadrubbble.R;
import com.enzhi.quadrubbble.models.Shot;
import com.enzhi.quadrubbble.models.User;
import com.enzhi.quadrubbble.utils.ModelUtils;
import com.enzhi.quadrubbble.view.bucket_list.BucketListActivity;
import com.enzhi.quadrubbble.view.user.UserActivity;
import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.drawee.interfaces.DraweeController;
import com.facebook.drawee.view.SimpleDraweeView;
import com.google.gson.reflect.TypeToken;

/**
 * Created by enzhi on 9/20/2016.
 */
public class ShotActivity extends AppCompatActivity {        //老師 extends SingleFragmentActivity， 不知何故
    boolean like = false;
    boolean likeChanging = false;
    int likeCount;
    Shot shot;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.shot_item);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        String shotData = getIntent().getStringExtra("shotData");
        shot = ModelUtils.toObject(shotData, new TypeToken<Shot>(){});

        if (shot.user == null) {
            String userData = getIntent().getStringExtra("userData");
            final User user = ModelUtils.toObject(userData, new TypeToken<User>(){});
            shot.user = user;
        }

        setTitle(shot.title);
        likeCount = shot.likes_count;
        AsyncTaskCompat.executeParallel(new CheckLikeTask());

        SimpleDraweeView shotImage = (SimpleDraweeView) findViewById(R.id.shot_image);
        TextView shotViewCount = (TextView) findViewById(R.id.shot_view_count);
        TextView shotLikeCount = (TextView) findViewById(R.id.shot_like_count);
        TextView shotBucketCount = (TextView) findViewById(R.id.shot_bucket_count);

        DraweeController controller = Fresco.newDraweeControllerBuilder()
                .setUri(Uri.parse (shot.getImageUrl()))
                .setAutoPlayAnimations(true)
                .build();
        shotImage.setController(controller);
        //shotImage.setImageURI(Uri.parse (shot.getImageUrl()));        //This way not animated

        shotViewCount.setText(String.valueOf(shot.views_count));
        shotLikeCount.setText(String.valueOf(shot.likes_count));
        shotBucketCount.setText(String.valueOf(shot.buckets_count));

        SimpleDraweeView portrait = (SimpleDraweeView) findViewById(R.id.portrait);

        LinearLayout shotUserButton = (LinearLayout) findViewById(R.id.shot_user_button);
        shotUserButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ShotActivity.this, UserActivity.class);
                intent.putExtra("userData", ModelUtils.toString(shot.user));
                //intent.putExtra("userId", shot.user.id);
                //intent.putExtra("userName", shot.user.name);
                startActivity(intent);
            }
        });

        TextView title = (TextView) findViewById(R.id.title);
        TextView name = (TextView) findViewById(R.id.name);
        TextView desription = (TextView) findViewById(R.id.despription);

        portrait.setImageURI(Uri.parse(shot.user.avatar_url));
        title.setText(shot.title);
        name.setText(shot.user.name);
        desription.setText(shot.description == null ? "" : Html.fromHtml(shot.description));

        LinearLayout likeButton = (LinearLayout) findViewById(R.id.like_button);
        likeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AsyncTaskCompat.executeParallel(new LikeTask(shot));
            }
        });

        LinearLayout bucketButton = (LinearLayout) findViewById(R.id.bucket_button);
        bucketButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ShotActivity.this, BucketListActivity.class);
                intent.putExtra("choosingMode", true);
                intent.putExtra("shotId", shot.id);
                startActivity(intent);
                //AsyncTaskCompat.executeParallel(new BucketTask(shot));
            }
        });

        final LinearLayout shareButton = (LinearLayout) findViewById(R.id.share_button);
        shareButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent shareIntent = new Intent();
                shareIntent.setAction(Intent.ACTION_SEND);
                shareIntent.putExtra(Intent.EXTRA_TEXT, shot.title + " " + shot.html_url);
                shareIntent.setType("text/plain");
                startActivity(Intent.createChooser(shareIntent, getApplicationContext().getString(R.string.share_to)));
            }
        });

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()){
            case android.R.id.home:
                saveAndExit();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private class CheckLikeTask extends AsyncTask<Void, Void, Boolean>{

        @Override
        protected Boolean doInBackground(Void... voids) {
            boolean res = Dribbble.checkLike(shot.id);
            if (like != res){
                likeChanging = true;
                like = res;
            }
            return like;
        }

        @Override
        protected void onPostExecute(Boolean aBoolean) {
            if(likeChanging){
                ImageView likeIcon = (ImageView) findViewById(R.id.like_icon);
                if(like){
                    likeIcon.setImageResource(R.drawable.ic_favorite_black_24px);
                } else {
                    likeIcon.setImageResource(R.drawable.ic_favorite_border_black_24px);
                }
                likeChanging = false;
            }
        }
    }

    private class LikeTask extends AsyncTask<Void, Void, Boolean>{
        Shot shot;
        LikeTask(Shot shot){
            this.shot = shot;
        }
        @Override
        protected Boolean doInBackground(Void... voids) {
            if(like == false) {
                Dribbble.like(shot.id);
                like = true;
            } else {
                Dribbble.unLike(shot.id);
                like = false;
            }
            return like;
        }

        @Override
        protected void onPostExecute(Boolean aBoolean) {
            ImageView likeIcon = (ImageView) findViewById(R.id.like_icon);
            TextView shotLikeCount = (TextView) findViewById(R.id.shot_like_count);
            if(like){
                likeIcon.setImageResource(R.drawable.ic_favorite_black_24px);
                likeCount++;
            } else {
                likeIcon.setImageResource(R.drawable.ic_favorite_border_black_24px);
                likeCount--;
            }
            shotLikeCount.setText(String.valueOf(likeCount));
        }
    }

    protected void saveAndExit(){
        Intent resultIntent = new Intent();
        resultIntent.putExtra("shotId", shot.id);
        resultIntent.putExtra("likeCount", likeCount);
        //resultIntent.putExtra("bucketCount", bucketCount);
        setResult(Activity.RESULT_OK, resultIntent);
        finish();
    }
}
