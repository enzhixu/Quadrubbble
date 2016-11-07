package com.enzhi.quadrubbble.view.shot_list;

import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.enzhi.quadrubbble.R;
import com.facebook.drawee.view.SimpleDraweeView;

/**
 * Created by enzhi on 9/18/2016.
 */
public class ShotViewHolder extends RecyclerView.ViewHolder{
    CardView cover = (CardView) itemView.findViewById(R.id.cover);
    SimpleDraweeView shotImage = (SimpleDraweeView) itemView.findViewById(R.id.shot_list_image);
    TextView shotViewCount = (TextView) itemView.findViewById(R.id.shot_list_view_count);
    TextView shotLikeCount = (TextView) itemView.findViewById(R.id.shot_list_like_count);
    TextView shotBucketCount = (TextView) itemView.findViewById(R.id.shot_list_bucket_count);
    public ShotViewHolder(View itemView) {
        super(itemView);
    }
}
