package com.enzhi.quadrubbble.view.shot_list;

import android.graphics.Rect;
import android.support.v7.widget.RecyclerView;
import android.view.View;

/**
 * Created by enzhi on 9/19/2016.
 */
public class SpaceItemDecoration extends RecyclerView.ItemDecoration {
    int space;
    public SpaceItemDecoration(int space){
        this.space = space;
    }

    @Override
    public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
        super.getItemOffsets(outRect, view, parent, state);
        outRect.bottom = space;
        outRect.left = space;
        outRect.right = space;
        if(parent.getChildAdapterPosition(view) == 0){
            outRect.top = space;
        }
    }
}
