package com.hailan.HaiLanPrint.views;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.flyco.banner.widget.Banner.BaseIndicatorBanner;
import com.hailan.HaiLanPrint.R;

public class SimpleGuideBanner extends BaseIndicatorBanner<Integer, SimpleGuideBanner> {

    public SimpleGuideBanner(Context context) {
        this(context, null, 0);
    }

    public SimpleGuideBanner(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public SimpleGuideBanner(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        setBarShowWhenLast(false);
    }

    @Override
    public View onCreateItemView(int position) {
        View view = View.inflate(mContext, R.layout.item_simple_guide, null);
        ImageView ivGuide = (ImageView) view.findViewById(R.id.ivGuide);
        TextView tvJump = (TextView) view.findViewById(R.id.tvJump);

        tvJump.setVisibility(position == mDatas.size() - 1 ? VISIBLE : GONE);

        final Integer resId = mDatas.get(position);
        Glide.with(mContext)
                .load(resId)
                .into(ivGuide);

        tvJump.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (onJumpClickListener != null)
                    onJumpClickListener.onJumpClick();
            }
        });

        return view;
    }

    private OnJumpClickListener onJumpClickListener;

    public interface OnJumpClickListener {
        void onJumpClick();
    }

    public void setOnJumpClickListener(OnJumpClickListener onJumpClickListener) {
        this.onJumpClickListener = onJumpClickListener;
    }
}
