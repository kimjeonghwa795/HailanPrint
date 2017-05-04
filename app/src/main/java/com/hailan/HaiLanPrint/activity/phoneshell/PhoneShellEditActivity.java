package com.hailan.HaiLanPrint.activity.phoneshell;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.view.View;
import android.widget.SeekBar;

import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;
import com.hailan.HaiLanPrint.R;
import com.hailan.HaiLanPrint.activity.base.ToolbarActivity;
import com.hailan.HaiLanPrint.activity.selectimage.SelectAlbumWhenEditActivity;
import com.hailan.HaiLanPrint.application.MDGroundApplication;
import com.hailan.HaiLanPrint.constants.Constants;
import com.hailan.HaiLanPrint.databinding.ActivityPhoneShellEditBinding;
import com.hailan.HaiLanPrint.models.MDImage;
import com.hailan.HaiLanPrint.models.WorkPhoto;
import com.hailan.HaiLanPrint.utils.GlideUtil;
import com.hailan.HaiLanPrint.utils.NavUtils;
import com.hailan.HaiLanPrint.utils.OrderUtils;
import com.hailan.HaiLanPrint.utils.SelectImageUtils;
import com.hailan.HaiLanPrint.utils.ViewUtils;
import com.hailan.HaiLanPrint.views.BaoGPUImage;

/**
 * Created by yoghourt on 5/18/16.
 */
public class PhoneShellEditActivity extends ToolbarActivity<ActivityPhoneShellEditBinding> {

    private AlertDialog mAlertDialog;

    @Override
    protected int getContentLayout() {
        return R.layout.activity_phone_shell_edit;
    }

    @Override
    protected void initData() {
        mAlertDialog = ViewUtils.createAlertDialog(this, getString(R.string.if_add_to_my_work),
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        NavUtils.toMainActivity(PhoneShellEditActivity.this);
                    }
                }, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        saveToMyWork();
                    }
                });

        showImageToGPUImageView();
    }

    @Override
    protected void setListener() {
        mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mAlertDialog.show();
            }
        });

        mDataBinding.bgiImage.setOnSingleTouchListener(new BaoGPUImage.OnSingleTouchListener() {
            @Override
            public void onSingleTouch() {
                Intent intent = new Intent(PhoneShellEditActivity.this, SelectAlbumWhenEditActivity.class);
                startActivityForResult(intent, 0);
            }
        });

        mDataBinding.seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

                WorkPhoto workPhoto = SelectImageUtils.sAlreadySelectImage.get(0).getWorkPhoto();
                workPhoto.setBrightLevel(progress);

                mDataBinding.tvPercent.setText(getString(R.string.percent, progress) + "%");

                mDataBinding.bgiImage.mBrightnessFilter.setBrightness(progress / 100f);
                mDataBinding.bgiImage.requestRender();
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
    }

    @Override
    public void onBackPressed() {
        mAlertDialog.show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            MDImage newMdImage = data.getParcelableExtra(Constants.KEY_SELECT_IMAGE);

            MDImage oldMdImage = SelectImageUtils.sAlreadySelectImage.get(0);

            WorkPhoto workPhoto = oldMdImage.getWorkPhoto();
            workPhoto.setZoomSize(100);
            workPhoto.setBrightLevel(0);
            workPhoto.setRotate(0);
            newMdImage.setWorkPhoto(workPhoto);

            SelectImageUtils.sAlreadySelectImage.set(0, newMdImage);

            showImageToGPUImageView();
        }
    }

    private void showImageToGPUImageView() {
        if (SelectImageUtils.sTemplateImage.size() > 0) {
            // 模板图片加载
            GlideUtil.loadImageByMDImage(mDataBinding.ivTemplate, SelectImageUtils.sTemplateImage.get(0), false);
        }

        final MDImage mdImage = SelectImageUtils.sAlreadySelectImage.get(0);

        // 用户选择的图片加载
        GlideUtil.loadImageAsBitmap(mdImage,
                new SimpleTarget<Bitmap>(200, 200) {
                    @Override
                    public void onResourceReady(Bitmap bitmap, GlideAnimation glideAnimation) {
//                        mDataBinding.bgiImage.loadNewImage(bitmap);
                        WorkPhoto workPhoto = mdImage.getWorkPhoto();

                        mDataBinding.bgiImage.loadNewImage(bitmap,
                                workPhoto.getZoomSize() / 100f,
                                workPhoto.getRotate(),
                                workPhoto.getBrightLevel() / 100f);
                    }
                });
    }

    private void saveToMyWork() {
        ViewUtils.loading(this);
        // 保存到我的作品中
        MDGroundApplication.sOrderutUtils = new OrderUtils(this, true,
                1, MDGroundApplication.sInstance.getChoosedTemplate().getPrice(),
                null,
                MDGroundApplication.sInstance.getChoosedTemplate().getSelectMaterial(),
                null);
        MDGroundApplication.sOrderutUtils.uploadPrintPhotoOrEngravingImageRequest(this, 0);
    }

    //region ACTION
    public void nextStepAction(View view) {
        float scaleFactor = mDataBinding.bgiImage.getmScaleFactor();
        float rotateDegree = mDataBinding.bgiImage.getmRotationDegrees();

        WorkPhoto workPhoto = SelectImageUtils.sAlreadySelectImage.get(0).getWorkPhoto();
        workPhoto.setZoomSize((int) (scaleFactor * 100));
        workPhoto.setRotate((int) rotateDegree);

        ViewUtils.loading(this);

        MDGroundApplication.sOrderutUtils = new OrderUtils(this, false,
                1, MDGroundApplication.sInstance.getChoosedTemplate().getPrice(),
                null,
                MDGroundApplication.sInstance.getChoosedTemplate().getSelectMaterial(),
                null);
        MDGroundApplication.sOrderutUtils.uploadPrintPhotoOrEngravingImageRequest(this, 0);
    }
    //endregion

}
