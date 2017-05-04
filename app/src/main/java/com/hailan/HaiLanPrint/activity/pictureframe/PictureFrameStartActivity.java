package com.hailan.HaiLanPrint.activity.pictureframe;

import android.content.Intent;
import android.view.View;

import com.hailan.HaiLanPrint.R;
import com.hailan.HaiLanPrint.activity.base.ToolbarActivity;
import com.hailan.HaiLanPrint.activity.template.SelectTemplateActivity;
import com.hailan.HaiLanPrint.application.MDGroundApplication;
import com.hailan.HaiLanPrint.databinding.ActivityPictureFrameStartBinding;
import com.hailan.HaiLanPrint.enumobject.PhotoExplainTypeEnum;
import com.hailan.HaiLanPrint.enumobject.ProductType;
import com.hailan.HaiLanPrint.models.PhotoTypeExplain;
import com.hailan.HaiLanPrint.utils.GlideUtil;

/**
 * Created by yoghourt on 5/18/16.
 */
public class PictureFrameStartActivity extends ToolbarActivity<ActivityPictureFrameStartBinding> {
    @Override
    protected int getContentLayout() {
        return R.layout.activity_picture_frame_start;
    }

    @Override
    protected void initData() {
        for (PhotoTypeExplain photoTypeExplain : MDGroundApplication.sInstance.getPhotoTypeExplainArrayList()) {
            if (photoTypeExplain.getExplainType() == PhotoExplainTypeEnum.IntroductionPage.value()
                    && photoTypeExplain.getTypeID() == ProductType.PictureFrame.value()) {
                GlideUtil.loadImageByPhotoSIDWithDialog(mDataBinding.ivIntroductionPage, photoTypeExplain.getPhotoSID());
                break;
            }
        }
    }

    @Override
    protected void setListener() {

    }

    public void nextStepAction(View view) {
        Intent intent = new Intent(this, SelectTemplateActivity.class);
        startActivity(intent);
    }
}
