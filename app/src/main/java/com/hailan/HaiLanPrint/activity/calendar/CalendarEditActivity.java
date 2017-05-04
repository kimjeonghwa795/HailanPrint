package com.hailan.HaiLanPrint.activity.calendar;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.support.v7.widget.LinearLayoutManager;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;
import android.widget.SeekBar;

import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;
import com.hailan.HaiLanPrint.R;
import com.hailan.HaiLanPrint.activity.base.ToolbarActivity;
import com.hailan.HaiLanPrint.activity.selectimage.SelectAlbumWhenEditActivity;
import com.hailan.HaiLanPrint.adapter.TemplateImageAdapter;
import com.hailan.HaiLanPrint.application.MDGroundApplication;
import com.hailan.HaiLanPrint.constants.Constants;
import com.hailan.HaiLanPrint.databinding.ActivityCalendarEditBinding;
import com.hailan.HaiLanPrint.models.MDImage;
import com.hailan.HaiLanPrint.models.WorkPhoto;
import com.hailan.HaiLanPrint.utils.GlideUtil;
import com.hailan.HaiLanPrint.utils.NavUtils;
import com.hailan.HaiLanPrint.utils.OrderUtils;
import com.hailan.HaiLanPrint.utils.SelectImageUtils;
import com.hailan.HaiLanPrint.utils.ViewUtils;
import com.hailan.HaiLanPrint.views.BaoGPUImage;
import com.hailan.HaiLanPrint.views.dialog.NotifyDialog;

import java.util.Calendar;

/**
 * Created by yoghourt on 5/18/16.
 */
public class CalendarEditActivity extends ToolbarActivity<ActivityCalendarEditBinding> implements DatePickerDialog.OnDateSetListener {

    private TemplateImageAdapter mTeplateImageAdapter;

    private int mCurrentSelectIndex = 0;

    private NotifyDialog mNotifyDialog;

    private AlertDialog mAlertDialog;

    private DatePickerDialog mBirthdayDatePickerDialog;

    @Override
    protected int getContentLayout() {
        return R.layout.activity_calendar_edit;
    }

    @Override
    protected void initData() {
        mAlertDialog = ViewUtils.createAlertDialog(this, getString(R.string.if_add_to_my_work),
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        NavUtils.toMainActivity(CalendarEditActivity.this);
                    }
                }, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        saveToMyWork();
                    }
                });

        showImageToGPUImageView(0, SelectImageUtils.sTemplateImage.get(0));

        mDataBinding.templateRecyclerView.setHasFixedSize(true);
        LinearLayoutManager imageLayoutManager = new LinearLayoutManager(this);
        imageLayoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        mDataBinding.templateRecyclerView.setLayoutManager(imageLayoutManager);
        mTeplateImageAdapter = new TemplateImageAdapter();
        mDataBinding.templateRecyclerView.setAdapter(mTeplateImageAdapter);
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
                Intent intent = new Intent(CalendarEditActivity.this, SelectAlbumWhenEditActivity.class);
                startActivityForResult(intent, 0);
            }
        });

        mTeplateImageAdapter.setOnSelectImageLisenter(new TemplateImageAdapter.onSelectImageLisenter() {
            @Override
            public void selectImage(int position, MDImage mdImage) {

                if (position > mCurrentSelectIndex) {
                    mDataBinding.calendarCard.rightSlide();
                } else {
                    mDataBinding.calendarCard.leftSlide();
                }

                if (mCurrentSelectIndex != position) {
                    float scaleFactor = mDataBinding.bgiImage.getmScaleFactor();
                    float rotateDegree = mDataBinding.bgiImage.getmRotationDegrees();

                    WorkPhoto workPhoto = SelectImageUtils.sAlreadySelectImage.get(mCurrentSelectIndex).getWorkPhoto();
                    workPhoto.setZoomSize((int) (scaleFactor * 100));
                    workPhoto.setRotate((int) rotateDegree);

                    showImageToGPUImageView(position, mdImage);
                }
            }
        });

        mDataBinding.seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

                WorkPhoto workPhoto = SelectImageUtils.sAlreadySelectImage.get(mCurrentSelectIndex).getWorkPhoto();
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

    private void showImageToGPUImageView(final int position, MDImage mdImage) {
        mCurrentSelectIndex = position;

        // 模板图片加载
        GlideUtil.loadImageByMDImage(mDataBinding.ivTemplate, mdImage, false);

        // 用户选择的图片加载
        MDImage selectImage = SelectImageUtils.sAlreadySelectImage.get(position);
        if (selectImage.getPhotoSID() != 0 || selectImage.getImageLocalPath() != null) {
            GlideUtil.loadImageAsBitmap(selectImage, new SimpleTarget<Bitmap>(200, 200) {
                @Override
                public void onResourceReady(Bitmap bitmap, GlideAnimation glideAnimation) {
                    WorkPhoto workPhoto = SelectImageUtils.sAlreadySelectImage.get(mCurrentSelectIndex).getWorkPhoto();

                    mDataBinding.bgiImage.loadNewImage(bitmap, workPhoto.getZoomSize() / 100f,
                            workPhoto.getRotate(),
                            workPhoto.getBrightLevel() / 100f);

                }
            });
        } else {
            mDataBinding.bgiImage.loadNewImage(null);
        }
    }

    private void saveToMyWork() {
        ViewUtils.loading(this);
        // 保存到我的作品中
        MDGroundApplication.sOrderutUtils = new OrderUtils(this, true,
                1, MDGroundApplication.sInstance.getChoosedTemplate().getPrice());
        MDGroundApplication.sOrderutUtils.uploadPrintPhotoOrEngravingImageRequest(this, 0);
    }

    private void generateOrder() {
        ViewUtils.loading(this);
        // 生成订单
        MDGroundApplication.sOrderutUtils = new OrderUtils(this, false,
                1, MDGroundApplication.sInstance.getChoosedTemplate().getPrice());
        MDGroundApplication.sOrderutUtils.uploadPrintPhotoOrEngravingImageRequest(this, 0);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            MDImage newMDImage = data.getParcelableExtra(Constants.KEY_SELECT_IMAGE);
            MDImage oldMDImage = SelectImageUtils.sAlreadySelectImage.get(mCurrentSelectIndex);

            newMDImage.setWorkPhoto(oldMDImage.getWorkPhoto());
            newMDImage.setWorkPhotoEdit(oldMDImage.getWorkPhotoEdit());

            SelectImageUtils.sAlreadySelectImage.set(mCurrentSelectIndex, newMDImage);

            showImageToGPUImageView(mCurrentSelectIndex, SelectImageUtils.sTemplateImage.get(mCurrentSelectIndex));
        }
    }

    @Override
    public void onBackPressed() {
        mAlertDialog.show();
    }

    @Override
    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
        mDataBinding.tvStartDate.setText(year + "-" + (monthOfYear + 1) + "-" + dayOfMonth);
    }

    //region ACTION
    public void toSelectCalendarAction(View view) {
        if (mBirthdayDatePickerDialog == null) {
            Calendar calendar = Calendar.getInstance();
            mBirthdayDatePickerDialog = new DatePickerDialog(this,
//                    android.R.style.Theme_Holo_Dialog_MinWidth,
                    android.R.style.Theme_Holo_Dialog,
                    this,
                    calendar.get(Calendar.YEAR),
                    calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));

            ((ViewGroup) mBirthdayDatePickerDialog.getDatePicker()).findViewById(Resources.getSystem().getIdentifier("day", "id", "android")).setVisibility(View.GONE);
        }
        mBirthdayDatePickerDialog.show();
    }

    public void nextStepAction(View view) {
        for (int i = 0; i < SelectImageUtils.sAlreadySelectImage.size(); i++) {
            MDImage selectImage = SelectImageUtils.sAlreadySelectImage.get(i);

            if (selectImage.getPhotoSID() == 0 && selectImage.getImageLocalPath() == null) {
                if (mNotifyDialog == null) {
                    mNotifyDialog = new NotifyDialog(this);
                    mNotifyDialog.setOnSureClickListener(new NotifyDialog.OnSureClickListener() {
                        @Override
                        public void onSureClick() {
                            mNotifyDialog.dismiss();
                            generateOrder();
                        }
                    });
                }
                mNotifyDialog.show();

                mNotifyDialog.setTvContent(getString(R.string.not_add_image, i + 1));
                return;
            }
        }

        generateOrder();
    }
    //endregion

}
