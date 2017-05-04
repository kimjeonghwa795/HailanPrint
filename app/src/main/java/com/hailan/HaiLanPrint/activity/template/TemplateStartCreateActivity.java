package com.hailan.HaiLanPrint.activity.template;

import android.databinding.DataBindingUtil;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.hailan.HaiLanPrint.R;
import com.hailan.HaiLanPrint.activity.base.ToolbarActivity;
import com.hailan.HaiLanPrint.application.MDGroundApplication;
import com.hailan.HaiLanPrint.constants.Constants;
import com.hailan.HaiLanPrint.databinding.ActivityTemplateStartCreateBinding;
import com.hailan.HaiLanPrint.databinding.ItemTemplateStartCreateBinding;
import com.hailan.HaiLanPrint.enumobject.ProductType;
import com.hailan.HaiLanPrint.models.MDImage;
import com.hailan.HaiLanPrint.utils.GlideUtil;
import com.hailan.HaiLanPrint.utils.NavUtils;
import com.hailan.HaiLanPrint.utils.SelectImageUtils;

import java.util.ArrayList;


/**
 * Created by yoghourt on 5/11/16.
 */
public class TemplateStartCreateActivity extends ToolbarActivity<ActivityTemplateStartCreateBinding> {

    private ArrayList<MDImage> mTemplateCoverImageList;

    @Override
    protected int getContentLayout() {
        return R.layout.activity_template_start_create;
    }

    @Override
    protected void initData() {
        mTemplateCoverImageList = getIntent().getParcelableArrayListExtra(Constants.KEY_COVER_IMAGE_LIST);

        mDataBinding.setTemplate(MDGroundApplication.sInstance.getChoosedTemplate());
        mDataBinding.webView.loadData(MDGroundApplication.sInstance.getChoosedTemplate().getMaterialDesc(),
                "text/html; charset=UTF-8", null);
        mDataBinding.tvPageNum.setText(MDGroundApplication.sInstance.getString(R.string.page_num, MDGroundApplication.sInstance.getChoosedTemplate().getPageCount()));

        if (SelectImageUtils.sTemplateImage.size() > 0) {
            setCurrentPageTips(1);
        } else {
            setCurrentPageTips(0);
        }

        mDataBinding.viewPager.setAdapter(new TemplateStartCreateAdapter());

        if (MDGroundApplication.sInstance.getChoosedProductType() == ProductType.LOMOCard
                || MDGroundApplication.sInstance.getChoosedProductType() == ProductType.MagicCup) {
            mDataBinding.tvPageNum.setVisibility(View.INVISIBLE);
        }
    }

    @Override
    protected void setListener() {
        mDataBinding.viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                setCurrentPageTips(position + 1);
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }

    //region ACTION

    private void setCurrentPageTips(int currentIndex) {
        tvTitle.setText(getString(R.string.current_page_index, currentIndex, mTemplateCoverImageList.size()));
    }

    public void nextStepAction(View view) {
        NavUtils.toSelectAlbumActivity(this);
    }
    //endregion

    //region ADAPTER
    private class TemplateStartCreateAdapter extends PagerAdapter {

        @Override
        public int getCount() {
            return mTemplateCoverImageList.size();
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view.equals(object);
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            ItemTemplateStartCreateBinding dataBinding = DataBindingUtil.inflate(LayoutInflater.from(container.getContext())
                    , R.layout.item_template_start_create, container, false);
            MDImage mdImage = mTemplateCoverImageList.get(position);
            dataBinding.setMdImage(mdImage);
            GlideUtil.loadImageByMDImage(dataBinding.iv, mdImage, true);
            View root = dataBinding.getRoot();
            container.addView(root);
            return root;
        }

        @Override
        public void destroyItem(View container, int position, Object object) {
            ((ViewPager) container).removeView((View) object);
        }
    }
    //endregion

}
