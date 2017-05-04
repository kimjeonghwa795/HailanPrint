package com.hailan.HaiLanPrint.activity.phototype;

import android.content.Intent;
import android.view.View;

import com.hailan.HaiLanPrint.R;
import com.hailan.HaiLanPrint.activity.base.ToolbarActivity;
import com.hailan.HaiLanPrint.activity.template.SelectTemplateActivity;
import com.hailan.HaiLanPrint.application.MDGroundApplication;
import com.hailan.HaiLanPrint.databinding.ActivityPhotoTypeBinding;
import com.hailan.HaiLanPrint.enumobject.ProductType;
import com.hailan.HaiLanPrint.utils.GlideUtil;
import com.hailan.HaiLanPrint.utils.NavUtils;

/**
 * Author : zhouyx
 * Date   : 2016/12/9
 * Description : 图文介绍
 */
public class PhotoTypeActivity extends ToolbarActivity<ActivityPhotoTypeBinding> {

    @Override
    protected int getContentLayout() {
        return R.layout.activity_photo_type;
    }

    @Override
    protected void initData() {
        GlideUtil.loadImageByPhotoSIDWithDialogLimit(mDataBinding.ivIntroductionPage,
                MDGroundApplication.sInstance.getChoosedMeasurement().getTypePhoto());
    }

    @Override
    protected void setListener() {

    }

    public void nextStepAction(View view) {
        ProductType productType = MDGroundApplication.sInstance.getChoosedProductType();
        if (productType == ProductType.PhoneShell) {
            NavUtils.toSelectAlbumActivity(PhotoTypeActivity.this);
            return;
        }
        Intent intent = new Intent(PhotoTypeActivity.this, SelectTemplateActivity.class);
        startActivity(intent);
    }

}
