package com.hailan.HaiLanPrint.activity.pictureframe;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.graphics.Point;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;
import com.hailan.HaiLanPrint.R;
import com.hailan.HaiLanPrint.activity.base.ToolbarActivity;
import com.hailan.HaiLanPrint.activity.selectimage.SelectAlbumWhenEditActivity;
import com.hailan.HaiLanPrint.application.MDGroundApplication;
import com.hailan.HaiLanPrint.constants.Constants;
import com.hailan.HaiLanPrint.databinding.ActivityPictureFrameEditBinding;
import com.hailan.HaiLanPrint.enumobject.MaterialType;
import com.hailan.HaiLanPrint.models.MDImage;
import com.hailan.HaiLanPrint.models.OriginalSizeBitmap;
import com.hailan.HaiLanPrint.models.Size;
import com.hailan.HaiLanPrint.models.Template;
import com.hailan.HaiLanPrint.restfuls.GlobalRestful;
import com.hailan.HaiLanPrint.restfuls.bean.ResponseData;
import com.hailan.HaiLanPrint.utils.CreateImageUtil;
import com.hailan.HaiLanPrint.utils.GlideUtil;
import com.hailan.HaiLanPrint.utils.OrderUtils;
import com.hailan.HaiLanPrint.utils.SelectImageUtils;
import com.hailan.HaiLanPrint.utils.StringUtil;
import com.hailan.HaiLanPrint.utils.TemplateUtils;
import com.hailan.HaiLanPrint.utils.ViewUtils;
import com.hailan.HaiLanPrint.views.DrawingBoardView;
import com.hailan.HaiLanPrint.views.ProductionView;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by yoghourt on 5/18/16.
 */
public class PictureFrameEditActivity extends ToolbarActivity<ActivityPictureFrameEditBinding>
        implements DrawingBoardView.OnDrawingBoardClickListener {

    private ProductionView mProductionView;

    private Template mChooseTemplate;

    private int mPrice;

    private String mWorkFormat, mWorkStyle, mMaterialName;

    private Bitmap mTemplateBitmap;

    private Size mSize;

    private float mRateOfEditArea = 1.0f;

    private Template template;

    private RadioButton checkButton = null, sizeButton = null;

    private View.OnClickListener onRadioButtonClick = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if (checkButton == v) {
                return;
            }
            if (checkButton != null) {
                checkButton.setChecked(false);
            }
            ((RadioButton) v).setChecked(true);
            checkButton = (RadioButton) v;
            Template.PropertyListBean propertyListBean = (Template.PropertyListBean) v.getTag();
            mMaterialName = propertyListBean.getMaterialName();
            refreshSizeButton(propertyListBean);
        }
    };
    private View.OnClickListener onSizeRadioButtonClick = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if (sizeButton == v) {
                return;
            }
            if (sizeButton != null) {
                sizeButton.setChecked(false);
            }
            ((RadioButton) v).setChecked(true);
            sizeButton = (RadioButton) v;
            Template.PropertyListBean propertyListBean = (Template.PropertyListBean) v.getTag();
            mWorkFormat = propertyListBean.getTypeDesc();
            mMaterialName = propertyListBean.getMaterialName();
            mPrice = propertyListBean.getPrice();
            mDataBinding.tvPrice.setText(getString(R.string.yuan_amount, StringUtil.toYuanWithoutUnit(mPrice)));
        }
    };

    @Override
    protected int getContentLayout() {
        return R.layout.activity_picture_frame_edit;
    }

    @Override
    protected void initData() {
        initBackgroundBitmap();

        mChooseTemplate = MDGroundApplication.sInstance.getChoosedTemplate();
        mChooseTemplate.setPageCount(1);
        MDGroundApplication.sInstance.setChoosedTemplate(mChooseTemplate);

        mDataBinding.setTemplate(mChooseTemplate);

        mDataBinding.tvPrice.setText(getString(R.string.yuan_amount, StringUtil.toYuanWithoutUnit(mChooseTemplate.getPrice())));
        changeMaterialAvailable();
        GetPhotoTemplate(mChooseTemplate.getTemplateID());
    }

    @Override
    protected void setListener() {
        mDataBinding.rgStyle.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch (checkedId) {
                    case R.id.rbLandscape:
                        refreshProductionView(-90);
                        mWorkStyle = getString(R.string.landscape);
                        break;
                    case R.id.rbPortrait:
                        refreshProductionView(90);
                        mWorkStyle = getString(R.string.portrait);
                        break;
                }
            }
        });
    }

    @Override
    public void onDrawingBoardTouch(DrawingBoardView drawingBoardView) {

    }

    @Override
    public void onDrawingBoardClick(DrawingBoardView drawingBoardView) {
        Intent intent = new Intent(PictureFrameEditActivity.this, SelectAlbumWhenEditActivity.class);
        startActivityForResult(intent, 0);
    }

    private void initBackgroundBitmap() {
        mProductionView = new ProductionView(this);
        mDataBinding.lltEdit.addView(mProductionView, 0);
        mProductionView.bringBackgroundToFront();

        MDImage templateImage = SelectImageUtils.sTemplateImage.get(0);

        GlideUtil.getOriginalSizeBitmap(this, templateImage, new SimpleTarget<OriginalSizeBitmap>() {
            @Override
            public void onResourceReady(OriginalSizeBitmap resource, GlideAnimation<? super OriginalSizeBitmap> glideAnimation) {
                mTemplateBitmap = resource.bitmap;
                mSize = resource.size;

                refreshProductionView(0f);
            }
        });

    }

    private void refreshProductionView(float rotateAngle) {
        if (rotateAngle != 0) {
            Matrix matrix = new Matrix();
            matrix.postRotate(rotateAngle);
            mTemplateBitmap = Bitmap.createBitmap(mTemplateBitmap, 0, 0,
                    mTemplateBitmap.getWidth(), mTemplateBitmap.getHeight(), matrix, true);
        }

        mProductionView.clear();
        mProductionView.backgroundLayer.setImageBitmap(mTemplateBitmap);

        mRateOfEditArea = TemplateUtils.getRateOfEditAreaOnAndroid(mSize);

        // 根据返回Bitmap的大小设置在android上对应的宽高
        Point sizePoint = TemplateUtils.getEditAreaSizeOnAndroid(mSize);

        final int width = sizePoint.x;
        final float height = sizePoint.y;

        mProductionView.setWidthAndHeight(width, (int) height);

        MDImage image = SelectImageUtils.sAlreadySelectImage.get(0);

        GlideUtil.getOriginalSizeBitmap(PictureFrameEditActivity.this, image, new SimpleTarget<OriginalSizeBitmap>() {
            @Override
            public void onResourceReady(OriginalSizeBitmap resource, GlideAnimation<? super OriginalSizeBitmap> glideAnimation) {
                Bitmap copyBitmap = resource.bitmap.copy(resource.bitmap.getConfig(), true); // safe copy

                Matrix matrix = new Matrix();
                addDrawBoard(0, 0, width, height, resource.size.width, resource.size.height,
                        copyBitmap, copyBitmap, matrix, mRateOfEditArea, 0);
                ViewUtils.dismiss();
            }
        });

//        GlideUtil.loadImageAsBitmap(image, new SimpleTarget<Bitmap>() {
//            @Override
//            public void onResourceReady(Bitmap moduleBitmap, GlideAnimation<? super
//                    Bitmap> glideAnimation) {
//
//                moduleBitmap = moduleBitmap.copy(moduleBitmap.getConfig(), true); // safe copy
//
//                Matrix matrix = new Matrix();
//                addDrawBoard(0, 0, width, height,
//                        moduleBitmap, moduleBitmap, matrix, 1.0f, 0);
//            }
//        });
    }

    private void addDrawBoard(float androidDx, float androidDy, float androidWidth, float androidHeight,
                              int userSelectBitmapOriginalWidth, int UserSelectBitmaporiginalHeight,
                              Bitmap mouldBmp, Bitmap userSelectBitmap, Matrix matrix, float rate, int position) {
        DrawingBoardView drawingBoardView = new DrawingBoardView(this, this,
                androidWidth, androidHeight, mouldBmp, userSelectBitmap, matrix, rate,
                userSelectBitmapOriginalWidth, UserSelectBitmaporiginalHeight);
        drawingBoardView.setTag(Integer.valueOf(position));

        FrameLayout.LayoutParams layoutParams = new FrameLayout.LayoutParams((int) androidWidth, (int) androidHeight);
        layoutParams.setMargins((int) androidDx, (int) androidDy, 0, 0);
        drawingBoardView.setLayoutParams(layoutParams);

        mProductionView.drawBoardLayer.addView(drawingBoardView);
        mProductionView.mDrawingBoardViewSparseArray.append(position, drawingBoardView);
    }

    private void changeMaterialAvailable() {
        mDataBinding.rgStyle.clearCheck();

        if ((MDGroundApplication.sInstance.getChoosedTemplate().getMaterialType() & MaterialType.Landscape.value()) != 0) {
            mDataBinding.rbLandscape.setEnabled(true);
            if (mDataBinding.rgStyle.getCheckedRadioButtonId() == -1) {
                mDataBinding.rbLandscape.setChecked(true);

                mWorkStyle = getString(R.string.landscape);
            }
        } else {
            mDataBinding.rbLandscape.setEnabled(false);
        }

        if ((MDGroundApplication.sInstance.getChoosedTemplate().getMaterialType() & MaterialType.Portrait.value()) != 0) {
            mDataBinding.rbPortrait.setEnabled(true);
            if (mDataBinding.rgStyle.getCheckedRadioButtonId() == -1) {
                mDataBinding.rbPortrait.setChecked(true);

                mWorkStyle = getString(R.string.portrait);
            }
        } else {
            mDataBinding.rbPortrait.setEnabled(false);
        }
    }

    private void saveToMyWork() {
        ViewUtils.loading(this);
        // 保存到我的作品中
        CreateImageUtil.createAllPageHasModules(new CreateImageUtil.onCreateAllCompositeImageCompleteListener() {
            @Override
            public void onComplete(final List<String> allCompositeImageLocalPathList) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        // 保存到我的作品中
                        MDGroundApplication.sOrderutUtils = new OrderUtils(PictureFrameEditActivity.this, true,
                                mChooseTemplate.getPageCount(), mPrice, mWorkFormat, null, mWorkStyle);
                        MDGroundApplication.sOrderutUtils.uploadAllCompositeImageReuqestNotEnable(PictureFrameEditActivity.this,
                                allCompositeImageLocalPathList, 0);
                    }
                });
            }
        });
    }

    private void saveCurrentPageEditStatus() {
        MDImage templateImage = SelectImageUtils.sTemplateImage.get(0);
        DrawingBoardView drawingBoardView = mProductionView.mDrawingBoardViewSparseArray.get(0);

        Matrix matrix = drawingBoardView.getMatrixOfEditPhoto();
        String matrixString = TemplateUtils.getStringByMatrix(matrix);
        templateImage.getWorkPhoto().setMatrix(matrixString);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            MDImage newMDImage = data.getParcelableExtra(Constants.KEY_SELECT_IMAGE);

            MDImage oldMDImage = SelectImageUtils.sAlreadySelectImage.get(0);

            oldMDImage.setPhotoID(newMDImage.getPhotoID());
            oldMDImage.setPhotoSID(newMDImage.getPhotoSID());
            oldMDImage.setImageLocalPath(newMDImage.getImageLocalPath());

            GlideUtil.getOriginalSizeBitmap(PictureFrameEditActivity.this, oldMDImage, new SimpleTarget<OriginalSizeBitmap>() {
                @Override
                public void onResourceReady(OriginalSizeBitmap resource, GlideAnimation<? super OriginalSizeBitmap> glideAnimation) {
                    Bitmap copyBitmap = resource.bitmap.copy(resource.bitmap.getConfig(), true); // safe copy

                    mProductionView.mDrawingBoardViewSparseArray.valueAt(0).setUserSelectBitmap(copyBitmap, new Matrix(),
                            mRateOfEditArea, resource.size.width, resource.size.height);
                }
            });

//            GlideUtil.loadImageAsBitmap(oldMDImage, new SimpleTarget<Bitmap>() {
//                @Override
//                public void onResourceReady(final Bitmap newBitmap, GlideAnimation<? super
//                        Bitmap> glideAnimation) {
//                    mProductionView.mDrawingBoardViewSparseArray.valueAt(0).setUserSelectBitmap(newBitmap, new Matrix(), 1.0f);
//                }
//            });
        }
    }

    public void minusNumAction(View view) {
        int photoCount = mChooseTemplate.getPageCount();

        if (photoCount == 1) {
            return;
        }

        mChooseTemplate.setPageCount(--photoCount);
        MDGroundApplication.sInstance.setChoosedTemplate(mChooseTemplate);
    }

    public void addNumAction(View view) {
        int photoCount = mChooseTemplate.getPageCount();

        mChooseTemplate.setPageCount(++photoCount);
        MDGroundApplication.sInstance.setChoosedTemplate(mChooseTemplate);
    }

    public void purchaseAction(View view) {
        saveCurrentPageEditStatus();
        ViewUtils.loading(this);

        CreateImageUtil.createAllPageWithoutModules(new CreateImageUtil.onCreateAllCompositeImageCompleteListener() {
            @Override
            public void onComplete(final List<String> allCompositeImageLocalPathList) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        MDGroundApplication.sOrderutUtils = new OrderUtils(PictureFrameEditActivity.this, false,
                                mChooseTemplate.getPageCount(), mPrice, mWorkFormat, mMaterialName, mWorkStyle);

                        MDGroundApplication.sOrderutUtils.uploadAllCompositeImageReuqestNotEnable(PictureFrameEditActivity.this,
                                allCompositeImageLocalPathList, 0);
                    }
                });
            }
        });
    }

    /**
     * 根据模板id获取尺寸信息
     *
     * @param TemplateID
     */
    private void GetPhotoTemplate(final int TemplateID) {
        ViewUtils.loading(this);
        GlobalRestful.getInstance().GetPhotoTemplate(TemplateID, new Callback<ResponseData>() {
            @Override
            public void onResponse(Call<ResponseData> call, Response<ResponseData> response) {
                ViewUtils.dismiss();
                template = response.body().getContent(Template.class);

                checkButton = null;
                mDataBinding.rgMaterial.removeAllViews();

                if (template != null && template.getPropertyList().size() > 0) {
                    List<Template.PropertyListBean> newPropertyList = new ArrayList<>();
                    for (Template.PropertyListBean propertyListBean : template.getPropertyList()) {
                        boolean isAdd = false;
                        for (Template.PropertyListBean listBean : newPropertyList) {
                            if (StringUtil.checkEquels(listBean.getMaterialName(), propertyListBean.getMaterialName())) {
                                isAdd = true;
                                break;
                            }
                        }
                        if (!isAdd) {
                            newPropertyList.add(propertyListBean);
                        }
                    }
                    for (Template.PropertyListBean propertyListBean : newPropertyList) {
                        addButtonView(propertyListBean);
                    }
                }
            }

            @Override
            public void onFailure(Call<ResponseData> call, Throwable t) {
                ViewUtils.dismiss();
            }
        });
    }

    /**
     * 添加材质控件
     */
    private void addButtonView(Template.PropertyListBean propertyListBean) {
        View view = LayoutInflater.from(this).inflate(R.layout.widget_prepay_radio_button, null);
        RadioButton radioButton = (RadioButton) view.findViewById(R.id.rb_button);
        radioButton.setText(propertyListBean.getMaterialName());
        radioButton.setTag(propertyListBean);
        radioButton.setOnClickListener(onRadioButtonClick);
        if (mDataBinding.rgMaterial.getChildCount() == 0) {
            radioButton.setChecked(true);
            checkButton = radioButton;
            mMaterialName = propertyListBean.getMaterialName();
            refreshSizeButton(propertyListBean);
        }
        mDataBinding.rgMaterial.addView(view);
    }

    /**
     * 添加大小控件
     */
    private void addSizeButtonView(Template.PropertyListBean propertyListBean) {
        View view = LayoutInflater.from(this).inflate(R.layout.widget_prepay_radio_button, null);
        RadioButton radioButton = (RadioButton) view.findViewById(R.id.rb_button);
        radioButton.setText(propertyListBean.getTypeDesc());
        radioButton.setTag(propertyListBean);
        radioButton.setOnClickListener(onSizeRadioButtonClick);
        if (mDataBinding.rgSize.getChildCount() == 0) {
            radioButton.setChecked(true);
            sizeButton = radioButton;
            mWorkFormat = propertyListBean.getTypeDesc();
            mPrice = propertyListBean.getPrice();
            mDataBinding.tvPrice.setText(getString(R.string.yuan_amount, StringUtil.toYuanWithoutUnit(mPrice)));
        }
        mDataBinding.rgSize.addView(view);
    }

    /**
     * 创建大小按钮
     *
     * @param propertyListBean
     */
    private void refreshSizeButton(Template.PropertyListBean propertyListBean) {
        sizeButton = null;
        mDataBinding.rgSize.removeAllViews();

        for (Template.PropertyListBean listBean : template.getPropertyList()) {
            if (listBean.getMaterialID() == propertyListBean.getMaterialID()) {
                addSizeButtonView(listBean);
            }
        }
    }
    //endregion

}
