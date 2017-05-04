package com.hailan.HaiLanPrint.views;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.widget.RelativeLayout;

import com.hailan.HaiLanPrint.R;


/**
 * A flexible RelativeLayout that  height = mRatio * width,
 * where the height is based off the width.
 */
public class FlexibleRelativeLayout extends RelativeLayout {

    private float mRatio = 1; // 宽高比,默认相等

    public FlexibleRelativeLayout(Context context) {
        super(context);
    }

    public FlexibleRelativeLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        initAttributeSet(context, attrs);
    }

    public FlexibleRelativeLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initAttributeSet(context, attrs);
    }

    private void initAttributeSet(Context context, AttributeSet attrs) {
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.FlexibleLayout);
        mRatio = typedArray.getFloat(R.styleable.FlexibleLayout_ratio, 1f);
    }


    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int widthSize = MeasureSpec.getSize(widthMeasureSpec);
        int heightMode = MeasureSpec.getMode(widthMeasureSpec);

        heightMeasureSpec = MeasureSpec.makeMeasureSpec((int)(widthSize * mRatio), MeasureSpec.EXACTLY);

        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

}