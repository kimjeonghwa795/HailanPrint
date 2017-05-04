package com.hailan.HaiLanPrint.activity.imagepreview;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;

import com.hailan.HaiLanPrint.R;
import com.hailan.HaiLanPrint.activity.base.ToolbarActivity;
import com.hailan.HaiLanPrint.constants.Constants;
import com.hailan.HaiLanPrint.databinding.ActivityImagePreviewBinding;
import com.hailan.HaiLanPrint.models.MDImage;

import java.util.ArrayList;

/**
 * Created by yoghourt on 2016-08-4.
 */

public class ImagePreviewActivity extends ToolbarActivity<ActivityImagePreviewBinding> {

    private ArrayList<MDImage> mAllPreviewImages = new ArrayList<>();

    @Override
    protected int getContentLayout() {
        return R.layout.activity_image_preview;
    }

    @Override
    protected void initData() {
//        mToolbar.setBackgroundResource(R.color.image_overlay2);
        mAllPreviewImages = getIntent().getParcelableArrayListExtra(Constants.KEY_PREVIEW_IMAGE_LIST);
        int imagePosition = getIntent().getIntExtra(Constants.KEY_PREVIEW_IMAGE_POSITION, 0);

        tvTitle.setText((imagePosition + 1) + "/" + mAllPreviewImages.size());

        mDataBinding.previewViewPager.setAdapter(new SimpleFragmentAdapter(getSupportFragmentManager()));
        mDataBinding.previewViewPager.setCurrentItem(imagePosition);
    }

    @Override
    protected void setListener() {
        mDataBinding.previewViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                tvTitle.setText((position + 1) + "/" + mAllPreviewImages.size());
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }

    private class SimpleFragmentAdapter extends FragmentPagerAdapter {
        public SimpleFragmentAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            return ImagePreviewFragment.getInstance(mAllPreviewImages.get(position));
        }

        @Override
        public int getCount() {
            return mAllPreviewImages.size();
        }
    }
}
