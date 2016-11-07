package com.enzhi.quadrubbble;

import android.content.Intent;
import android.content.res.Configuration;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.webkit.CookieManager;
import android.widget.TextView;
import android.widget.Toast;

import com.enzhi.quadrubbble.view.bucket_list.BucketListFragment;
import com.enzhi.quadrubbble.view.shot_list.ShotListFragment;
import com.enzhi.quadrubbble.view.user.UserListFragment;
import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.drawee.view.SimpleDraweeView;

public class MainActivity extends AppCompatActivity {
    private ActionBarDrawerToggle drawerToggle;
    private DrawerLayout drawerLayout;
    private NavigationView navigationView;

    public static final int REQ_CODE_NEW_BUCKET = 100;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Fresco.initialize(this);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);

        navigationView = (NavigationView) findViewById(R.id.drawer);
        drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);

      View headView = navigationView.inflateHeaderView(R.layout.header_logged_in);

        //不能用ImageView, 因為貌似要另開一個線程
        SimpleDraweeView userImage = (SimpleDraweeView) headView.findViewById(R.id.user_image);
        Uri userImageUri = Uri.parse(Dribbble.getCurrentUser().avatar_url);

        TextView userName = (TextView) headView.findViewById(R.id.user_name);
        userName.setText(Dribbble.getCurrentUser().name);

        TextView logoutButton = (TextView) headView.findViewById(R.id.log_out_button);
        logoutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Dribbble.logout(MainActivity.this);

                //清空瀏覽器，否則用戶無需輸入賬號密碼即可獲得authcode
                CookieManager cookieManager = CookieManager.getInstance();
                cookieManager.removeAllCookie();
                Intent intent = new Intent(MainActivity.this, LoginActivity.class);
                startActivity(intent);
                finish();
            }
        });

        setUpDrawer();

        if(savedInstanceState == null){
            getSupportFragmentManager()
                    .beginTransaction()
                    .add(R.id.fragment_container, ShotListFragment.newInstance(ShotListFragment.LIST_TYPE_POPULAR, null, null))
                    .commit();
        }
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        drawerToggle.syncState();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        drawerToggle.onConfigurationChanged(newConfig);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (drawerToggle.onOptionsItemSelected(item)) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void setUpDrawer(){
        drawerToggle = new ActionBarDrawerToggle(this, drawerLayout, R.string.open_drawer, R.string.close_drawer);
        drawerLayout.addDrawerListener(drawerToggle);

        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                if(item.isChecked()){
                    drawerLayout.closeDrawers();
                    return true;
                }
                Fragment fragment = null;
                switch(item.getItemId()){
                    case R.id.drawer_menu_home:
                        setTitle(R.string.title_home);
                        Toast.makeText(MainActivity.this, "Home clicked.", Toast.LENGTH_LONG).show();
                        fragment = ShotListFragment.newInstance(ShotListFragment.LIST_TYPE_POPULAR, null, null);
                        break;
                    case R.id.drawer_menu_likes:
                        setTitle(R.string.title_likes);
                        fragment = ShotListFragment.newInstance(ShotListFragment.LIST_TYPE_LIKED, null, null);
                        Toast.makeText(MainActivity.this, "Likes clicked.", Toast.LENGTH_LONG).show();
                        break;
                    case R.id.drawer_menu_buckets:
                        setTitle(R.string.title_buckets);
                        fragment = BucketListFragment.newInstance(false, null);
                        Toast.makeText(MainActivity.this, "Buckets clicked.", Toast.LENGTH_LONG).show();
                        break;
                    case R.id.drawer_menu_following:
                        setTitle(R.string.title_following);
                        fragment = UserListFragment.newInstance();
                        Toast.makeText(MainActivity.this, "Following clicked.", Toast.LENGTH_LONG).show();
                        break;
                }
                getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.fragment_container, fragment) //replace是關鍵，如果是add, 會疊在上面
                        .commit();
                drawerLayout.closeDrawers();
                return true;
            }
        });

    }
}
