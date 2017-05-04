package com.hailan.HaiLanPrint.views;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.widget.ImageView;

import com.hailan.HaiLanPrint.R;


/**
 * A flexible ImageView that  widith = parent'width * ratio,
 */
public class FlexibleImageView extends ImageView {

    private float mRatio = 1; // width与父view's width比,默认相等

    public FlexibleImageView(Context context) {
        super(context);
    }

    public FlexibleImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initAttributeSet(context, attrs);
    }

    public FlexibleImageView(Context context, AttributeSet attrs, int defStyleAttr) {
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
        int heightSize = MeasureSpec.getSize(heightMeasureSpec);

        int width = (int) (widthSize * mRatio);
        int height = (int) (heightSize * mRatio);

        widthMeasureSpec = MeasureSpec.makeMeasureSpec(width, MeasureSpec.EXACTLY);
        heightMeasureSpec = MeasureSpec.makeMeasureSpec(height, MeasureSpec.EXACTLY);

        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

}