package com.hailan.HaiLanPrint.views;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.flyco.banner.widget.Banner.BaseIndicatorBanner;
import com.hailan.HaiLanPrint.R;
import com.hailan.HaiLanPrint.models.MDImage;


public class SimpleImageBanner extends BaseIndicatorBanner<MDImage, SimpleImageBanner> {

    private ColorDrawable colorDrawable;

    public SimpleImageBanner(Context context) {
        this(context, null, 0);
    }

    public SimpleImageBanner(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public SimpleImageBanner(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        colorDrawable = new ColorDrawable(Color.parseColor("#555555"));
    }

    @Override
    public void onTitleSlect(TextView tv, int position) {
        final MDImage item = mDatas.get(position);
//        tv.setText(item.title);
    }

    @Override
    public View onCreateItemView(int position) {
        View inflate = View.inflate(mContext, R.layout.item_banner, null);
        ImageView iv = (ImageView) inflate.findViewById(R.id.iv);

        final MDImage item = mDatas.get(position);
        int itemWidth = mDisplayMetrics.widthPixels;
        int itemHeight = (int) (itemWidth * 360 * 1.0f / 640);
//        iv.setScaleType(ImageView.ScaleType.CENTER_CROP);
//        iv.setLayoutParams(new LinearLayout.LayoutParams(itemWidth, itemHeight));


        if (item != null) {
            Glide.with(mContext)
                    .load(item)
                    .override(itemWidth, itemHeight)
                    .placeholder(colorDrawable)
                    .into(iv);
        } else {
            iv.setImageDrawable(colorDrawable);
        }

        return inflate;
    }
}
