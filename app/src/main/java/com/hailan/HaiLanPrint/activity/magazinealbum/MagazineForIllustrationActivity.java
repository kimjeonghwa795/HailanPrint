package com.hailan.HaiLanPrint.activity.magazinealbum;

import com.hailan.HaiLanPrint.R;
import com.hailan.HaiLanPrint.activity.base.ToolbarActivity;
import com.hailan.HaiLanPrint.application.MDGroundApplication;
import com.hailan.HaiLanPrint.databinding.ActivityMagazineIllustrationBinding;
import com.hailan.HaiLanPrint.enumobject.PhotoExplainTypeEnum;
import com.hailan.HaiLanPrint.enumobject.ProductType;
import com.hailan.HaiLanPrint.models.PhotoTypeExplain;
import com.hailan.HaiLanPrint.utils.GlideUtil;

/**
 * Created by yoghourt on 5/23/16.
 */

public class MagazineForIllustrationActivity extends ToolbarActivity<ActivityMagazineIllustrationBinding> {
    @Override
    protected int getContentLayout() {
        return R.layout.activity_magazine_illustration;
    }

    @Override
    protected void initData() {
        for (PhotoTypeExplain photoTypeExplain : MDGroundApplication.sInstance.getPhotoTypeExplainArrayList()) {
            if (photoTypeExplain.getExplainType() == PhotoExplainTypeEnum.IntroductionPage.value()
                    && photoTypeExplain.getTypeID() == ProductType.MagazineAlbum.value()) {

                GlideUtil.loadImageByPhotoSIDWithDialog(mDataBinding.ivMeasurementDescription, photoTypeExplain.getPhotoSID());
                break;
            }
        }
    }

    @Override
    protected void setListener() {

    }
}
