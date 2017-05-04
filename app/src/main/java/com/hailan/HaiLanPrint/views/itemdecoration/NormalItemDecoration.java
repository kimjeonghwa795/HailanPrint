package com.hailan.HaiLanPrint.views.itemdecoration;

import android.graphics.Rect;
import android.support.v7.widget.RecyclerView;
import android.view.View;

/**
 * Created by dee on 2015/8/18.
 */
public class NormalItemDecoration extends RecyclerView.ItemDecoration {

    private int mSpacing;

    public NormalItemDecoration(int spacing) {
        this.mSpacing = spacing;
    }

    @Override
    public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
        outRect.top = mSpacing;
        outRect.bottom = mSpacing;
    }
}