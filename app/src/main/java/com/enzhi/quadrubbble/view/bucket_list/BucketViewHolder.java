package com.enzhi.quadrubbble.view.bucket_list;

import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.CheckBox;
import android.widget.TextView;

import com.enzhi.quadrubbble.R;

/**
 * Created by enzhi on 9/24/2016.
 */
public class BucketViewHolder extends RecyclerView.ViewHolder {

    public BucketViewHolder(View itemView) {
        super(itemView);
    }
    CardView bucketCover = (CardView) itemView.findViewById(R.id.bucket_cover);
    TextView bucketName = (TextView) itemView.findViewById(R.id.bucket_name);
    TextView bucketShotCount = (TextView) itemView.findViewById(R.id.bucket_shot_count);
    CheckBox bucketShotChosen = (CheckBox) itemView.findViewById(R.id.bucket_shot_chosen);
}
