package com.enzhi.quadrubbble.view.user;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.enzhi.quadrubbble.R;
import com.facebook.drawee.view.SimpleDraweeView;

/**
 * Created by enzhi on 9/29/2016.
 */
public class UserViewHolder extends RecyclerView.ViewHolder {
    SimpleDraweeView listUserPortrait = (SimpleDraweeView) itemView.findViewById(R.id.list_user_portrait);
    TextView userName = (TextView) itemView.findViewById(R.id.user_name);
    TextView userBio = (TextView) itemView.findViewById(R.id.list_user_bio);
    RelativeLayout userButton = (RelativeLayout) itemView.findViewById(R.id.user_button);
    public UserViewHolder(View itemView) {
        super(itemView);
    }
}
