package com.hailan.HaiLanPrint.activity.templateedit;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.graphics.Point;
import android.support.v7.widget.LinearLayoutManager;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.SeekBar;

import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;
import com.google.gson.reflect.TypeToken;
import com.hailan.HaiLanPrint.R;
import com.hailan.HaiLanPrint.activity.base.ToolbarActivity;
import com.hailan.HaiLanPrint.activity.imagepreview.ImagePreviewActivity;
import com.hailan.HaiLanPrint.activity.selectimage.SelectAlbumWhenEditActivity;
import com.hailan.HaiLanPrint.adapter.TemplateImageAdapter;
import com.hailan.HaiLanPrint.application.MDGroundApplication;
import com.hailan.HaiLanPrint.constants.Constants;
import com.hailan.HaiLanPrint.databinding.ActivityGlobalTemplateEditBinding;
import com.hailan.HaiLanPrint.enumobject.ProductType;
import com.hailan.HaiLanPrint.enumobject.restfuls.ResponseCode;
import com.hailan.HaiLanPrint.models.MDImage;
import com.hailan.HaiLanPrint.models.OriginalSizeBitmap;
import com.hailan.HaiLanPrint.models.PhotoTemplateAttachFrame;
import com.hailan.HaiLanPrint.models.WorkInfo;
import com.hailan.HaiLanPrint.models.WorkPhoto;
import com.hailan.HaiLanPrint.restfuls.GlobalRestful;
import com.hailan.HaiLanPrint.restfuls.bean.ResponseData;
import com.hailan.HaiLanPrint.utils.CreateImageUtil;
import com.hailan.HaiLanPrint.utils.GlideUtil;
import com.hailan.HaiLanPrint.utils.NavUtils;
import com.hailan.HaiLanPrint.utils.OrderUtils;
import com.hailan.HaiLanPrint.utils.SelectImageUtils;
import com.hailan.HaiLanPrint.utils.TemplateUtils;
import com.hailan.HaiLanPrint.utils.ViewUtils;
import com.hailan.HaiLanPrint.views.DrawingBoardView;
import com.hailan.HaiLanPrint.views.ProductionView;
import com.socks.library.KLog;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.hailan.HaiLanPrint.utils.SelectImageUtils.sTemplateImage;

/**
 * Created by yoghourt on 5/18/16.
 */
public class GlobalTemplateEditActivity extends ToolbarActivity<ActivityGlobalTemplateEditBinding>
        implements DrawingBoardView.OnDrawingBoardClickListener {

    private ProductionView mProductionView;

    private TemplateImageAdapter mTeplateImageAdapter;

    private int mCurrentSelectPageIndex = 0;

    private DrawingBoardView mCurrentSelectDrawingBoardView;

    private float mRateOfEditArea = 1.0f;

    private ProductType mProductType;

    private boolean isSetAdapter = false;

    private int myWorkSize;//我的作品目前总数

    @Override
    protected int getContentLayout() {
        return R.layout.activity_global_template_edit;
    }

    @Override
    protected void initData() {
        tvRight.setText(R.string.preview);

        initOrder();

        mProductionView = new ProductionView(this);
//        mDataBinding.lltEdit.addView(mProductionView, 0);
        mDataBinding.fltEdit.addView(mProductionView, 0);

        mProductType = MDGroundApplication.sInstance.getChoosedProductType();

        // 明信片和Lomo卡可以输入文字
        if (mProductType == ProductType.Postcard
                || mProductType == ProductType.LOMOCard) {
            mDataBinding.cetInput.setVisibility(View.GONE);
        }

        // 手机壳,魔术杯没有模版选择
        if (mProductType == ProductType.PhoneShell) {
            mDataBinding.templateRecyclerView.setVisibility(View.GONE);
        }

        // 只有杂志册,艺术册,个性月历 这三个功能块有定位块, 否则背景图片显示在最前面
        if (!TemplateUtils.isTemplateHasModules()) {
            mProductionView.bringBackgroundToFront();
        }

        selectPageByIndexToEdit(0, sTemplateImage.get(0));

        mDataBinding.templateRecyclerView.setHasFixedSize(true);
        LinearLayoutManager imageLayoutManager = new LinearLayoutManager(this);
        imageLayoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        mDataBinding.templateRecyclerView.setLayoutManager(imageLayoutManager);
        mTeplateImageAdapter = new TemplateImageAdapter();
        mTeplateImageAdapter.setContext(this);
//        mDataBinding.templateRecyclerView.setAdapter(mTeplateImageAdapter);

        getUserWorkListRequest();
    }

    @Override
    protected void setListener() {
        mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showSaveDialog();
            }
        });

        tvRight.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveCurrentPageEditStatus();
                ViewUtils.loading(GlobalTemplateEditActivity.this);
                if (TemplateUtils.isTemplateHasModules()) {
                    CreateImageUtil.createAllPageHasModules(new CreateImageUtil.onCreateAllCompositeImageCompleteListener() {
                        @Override
                        public void onComplete(final List<String> allCompositeImageLocalPathList) {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    toPreviewActivity(allCompositeImageLocalPathList);
                                }
                            });
                        }
                    });
                } else {
                    CreateImageUtil.createAllPageWithoutModules(new CreateImageUtil.onCreateAllCompositeImageCompleteListener() {
                        @Override
                        public void onComplete(final List<String> allCompositeImageLocalPathList) {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    toPreviewActivity(allCompositeImageLocalPathList);
                                }
                            });
                        }
                    });
                }
            }
        });

        mDataBinding.cetInput.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                WorkPhoto workPhoto = sTemplateImage.get(mCurrentSelectPageIndex).getWorkPhoto();

                workPhoto.setDescription(s.toString());
            }
        });

        mDataBinding.seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
//                if (mCurrentSelectDrawingBoardView == null) {
//                    mProductionView.mDrawingBoardViewSparseArray.get(0).setBrightness(progress);
//                } else {
//                    mCurrentSelectDrawingBoardView.setBrightness(progress);
//                }

                if (fromUser) {
                    int brightLevel = -255 + 510 * progress / 100;
                    sTemplateImage.get(mCurrentSelectPageIndex).getWorkPhoto().setBrightLevel(brightLevel);

                    mDataBinding.tvPercent.setText(getString(R.string.percent, brightLevel));

                    for (int i = 0; i < mProductionView.mDrawingBoardViewSparseArray.size(); i++) {
                        DrawingBoardView drawingBoardView = mProductionView.mDrawingBoardViewSparseArray.valueAt(i);
                        drawingBoardView.setBrightness(brightLevel);
                    }
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
            }
        });

        mTeplateImageAdapter.setOnSelectImageLisenter(new TemplateImageAdapter.onSelectImageLisenter() {
            @Override
            public void selectImage(int pageIndex, MDImage mdImage) {

                if (mCurrentSelectPageIndex != pageIndex) {
                    int beforeIndex = mCurrentSelectPageIndex;

                    ViewUtils.loading(GlobalTemplateEditActivity.this);
                    saveCurrentPageEditStatus();

                    selectPageByIndexToEdit(pageIndex, mdImage);

                    // 如果是日历模块,则显示日历
                    if (mProductType == ProductType.Calendar) {
                        if (pageIndex == 0) {
//                            mDataBinding.calendarCard.setVisibility(View.GONE);
                        } else {
//                            mDataBinding.calendarCard.setVisibility(View.VISIBLE);

//                            DateTime newDateTime = mDateTime.plusMonths(pageIndex - 1);

//                            mDataBinding.calendarCard.setTime(newDateTime.getYear(), newDateTime.getMonthOfYear());
                        }
                    }
                    mTeplateImageAdapter.notifyItemChanged(beforeIndex);
                }
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            MDImage newMDImage = data.getParcelableExtra(Constants.KEY_SELECT_IMAGE);

            int moduleIndex = (int) mCurrentSelectDrawingBoardView.getTag();

            MDImage oldMDImage = SelectImageUtils.getMdImageByPageIndexAndModuleIndex(mCurrentSelectPageIndex, moduleIndex);

            oldMDImage.setPhotoID(newMDImage.getPhotoID());
            oldMDImage.setPhotoSID(newMDImage.getPhotoSID());
            oldMDImage.setImageLocalPath(newMDImage.getImageLocalPath());

//            SelectImageUtils.sTemplateImage.get(mCurrentSelectPageIndex)
//                    .getWorkPhoto().setDescription("");
            // 切换模板图片后，则输入栏清空显示
            mDataBinding.cetInput.setText("");

//            GlideUtil.loadImageAsBitmap(oldMDImage, new SimpleTarget<Bitmap>() {
//                @Override
//                public void onResourceReady(final Bitmap newBitmap, GlideAnimation<? super
//                        Bitmap> glideAnimation) {
//                    Bitmap copyBitmap = newBitmap.copy(newBitmap.getConfig(), true); // safe copy
//
//                    mCurrentSelectDrawingBoardView.setUserSelectBitmap(copyBitmap, new Matrix(), 1.0f);
//                }
//            });

            GlideUtil.getOriginalSizeBitmap(GlobalTemplateEditActivity.this, oldMDImage, new SimpleTarget<OriginalSizeBitmap>() {
                @Override
                public void onResourceReady(OriginalSizeBitmap resource, GlideAnimation<? super OriginalSizeBitmap> glideAnimation) {
                    Bitmap copyBitmap = resource.bitmap.copy(resource.bitmap.getConfig(), true); // safe copy
                    mCurrentSelectDrawingBoardView.setUserSelectBitmap(copyBitmap, new Matrix(), mRateOfEditArea,
                            resource.size.width, resource.size.height);
                }
            });

            if (mProductType != ProductType.PhoneShell) {
                SelectImageUtils.originalSelectImageSize++;
            }
        }
    }

    @Override
    public void onBackPressed() {
        showSaveDialog();
    }

    @Override
    public void onDrawingBoardTouch(DrawingBoardView drawingBoardView) {
        mCurrentSelectDrawingBoardView = drawingBoardView;
    }

    @Override
    public void onDrawingBoardClick(DrawingBoardView drawingBoardView) {
        mCurrentSelectDrawingBoardView = drawingBoardView;

        Intent intent = new Intent(GlobalTemplateEditActivity.this, SelectAlbumWhenEditActivity.class);
        startActivityForResult(intent, 0);
    }

    private void initOrder() {
        ProductType productType = MDGroundApplication.sInstance.getChoosedProductType();

        if (productType == ProductType.PhoneShell) {
            MDGroundApplication.sOrderutUtils = new OrderUtils(this, false, 1,
                    MDGroundApplication.sInstance.getChoosedTemplate().getPrice(), null,
                    MDGroundApplication.sInstance.getChoosedTemplate().getSelectMaterial(), null);
        } else {
            int price;
            if (productType == ProductType.MagicCup) {
                price = MDGroundApplication.sInstance.getChoosedMeasurement().getPrice();
            } else {
                price = MDGroundApplication.sInstance.getChoosedTemplate().getPrice();
            }

            MDGroundApplication.sOrderutUtils = new OrderUtils(this, false, 1, price);
        }
    }

    // 保存当前页的编辑状态
    private void saveCurrentPageEditStatus() {
        mCurrentSelectDrawingBoardView = null;

        if (TemplateUtils.isTemplateHasModules()) {
            MDImage templateImage = sTemplateImage.get(mCurrentSelectPageIndex);
            List<PhotoTemplateAttachFrame> photoTemplateAttachFrameList = templateImage.getPhotoTemplateAttachFrameList();

            if (photoTemplateAttachFrameList != null) {
                for (int i = 0; i < photoTemplateAttachFrameList.size(); i++) {
                    DrawingBoardView drawingBoardView = mProductionView.mDrawingBoardViewSparseArray.get(i);

                    if (drawingBoardView != null) {
                        Matrix matrix = drawingBoardView.getMatrixOfEditPhoto();
                        String matrixString = TemplateUtils.getStringByMatrix(matrix);
                        photoTemplateAttachFrameList.get(i).setMatrix(matrixString);
                    }
                }
            }

        } else {
            try {
                MDImage templateImage = sTemplateImage.get(mCurrentSelectPageIndex);
                DrawingBoardView drawingBoardView = mProductionView.mDrawingBoardViewSparseArray.get(0);

                Matrix matrix = drawingBoardView.getMatrixOfEditPhoto();
                String matrixString = TemplateUtils.getStringByMatrix(matrix);
                templateImage.getWorkPhoto().setMatrix(matrixString);

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void selectPageByIndexToEdit(final int pageIndex, final MDImage mdImage) {
        int brightLevel = SelectImageUtils.sTemplateImage.get(pageIndex).getWorkPhoto().getBrightLevel();

        int progress = (brightLevel + 255) * 100 / 510;

        mDataBinding.seekBar.setProgress(progress);
        mDataBinding.tvPercent.setText(getString(R.string.percent, brightLevel));

        mCurrentSelectPageIndex = pageIndex;
        mProductionView.clear();

        WorkPhoto workPhoto = mdImage.getWorkPhoto();
        String description = workPhoto.getDescription();

        mDataBinding.cetInput.setText("");
        if (description != null) {
            mDataBinding.cetInput.append(description);
        }
        // 模板背景图片加载
        GlideUtil.getOriginalSizeBitmap(GlobalTemplateEditActivity.this, mdImage, new SimpleTarget<OriginalSizeBitmap>() {
            @Override
            public void onResourceReady(OriginalSizeBitmap resource, GlideAnimation<? super OriginalSizeBitmap> glideAnimation) {
                mProductionView.backgroundLayer.setImageBitmap(resource.bitmap);

                // 根据返回Bitmap的大小设置在android上对应的宽高
                Point sizePoint = TemplateUtils.getEditAreaSizeOnAndroid(resource.size);
                final int width = sizePoint.x;
                final float height = sizePoint.y;

                mProductionView.setWidthAndHeight(width, (int) height);

                mRateOfEditArea = TemplateUtils.getRateOfEditAreaOnAndroid(resource.size);

                if (TemplateUtils.isTemplateHasModules()) {
                    // 各个编辑定位块加载
                    MDImage templateImage = sTemplateImage.get(pageIndex);
                    final List<PhotoTemplateAttachFrame> photoTemplateAttachFrameList = templateImage.getPhotoTemplateAttachFrameList();

                    if (photoTemplateAttachFrameList != null && photoTemplateAttachFrameList.size() > 0) {
                        for (int moduleIndex = 0; moduleIndex < photoTemplateAttachFrameList.size(); moduleIndex++) {
                            final PhotoTemplateAttachFrame photoTemplateAttachFrame = photoTemplateAttachFrameList.get(moduleIndex);

                            final int finalI = moduleIndex;

                            MDImage moduleShowImage = SelectImageUtils
                                    .getMdImageByPageIndexAndModuleIndex(mCurrentSelectPageIndex, moduleIndex);

                            // 加载用户选择的图片
                            GlideUtil.getOriginalSizeBitmap(GlobalTemplateEditActivity.this, moduleShowImage, new SimpleTarget<OriginalSizeBitmap>() {
                                @Override
                                public void onResourceReady(OriginalSizeBitmap resource, GlideAnimation<? super OriginalSizeBitmap> glideAnimation) {
                                    KLog.e("定位块位置 : " + finalI + "图片的original size是: ", String.format(Locale.ROOT, "%dx%d", resource.size.width, resource.size.height));
                                    KLog.e("定位块位置 : " + finalI + "图片的压缩的 size是: ", String.format(Locale.ROOT, "%dx%d", resource.bitmap.getWidth(), resource.bitmap.getHeight()));

                                    Bitmap copyBitmap = resource.bitmap.copy(resource.bitmap.getConfig(), true); // safe copy

                                    int dx = photoTemplateAttachFrame.getPositionX();
                                    int dy = photoTemplateAttachFrame.getPositionY();
                                    int width = photoTemplateAttachFrame.getWidth();
                                    int height = photoTemplateAttachFrame.getHeight();

                                    float androidDx = dx * mRateOfEditArea;
                                    float androidDy = dy * mRateOfEditArea;
                                    float androidWidth = width * mRateOfEditArea;
                                    float androidHeight = height * mRateOfEditArea;

                                    Matrix matrix = TemplateUtils.getMatrixByString(photoTemplateAttachFrame.getMatrix());
                                    // 添加module编辑区域
                                    addDrawBoard(androidDx, androidDy, androidWidth, androidHeight, resource.size.width, resource.size.height,
                                            copyBitmap, copyBitmap, matrix, mRateOfEditArea, finalI);

                                    if (mProductionView.mDrawingBoardViewSparseArray.size() == photoTemplateAttachFrameList.size()) {
                                        ViewUtils.dismiss();
                                    }
                                }
                            });

                            // 加载用户选择的图片
//                            GlideUtil.loadImageAsBitmap(moduleShowImage, new SimpleTarget<Bitmap>() {
//                                @Override
//                                public void onResourceReady(Bitmap moduleBitmap, GlideAnimation<? super
//                                        Bitmap> glideAnimation) {
//
//                                    Bitmap copyBitmap = moduleBitmap.copy(moduleBitmap.getConfig(), true); // safe copy
//
//                                    int dx = photoTemplateAttachFrame.getPositionX();
//                                    int dy = photoTemplateAttachFrame.getPositionY();
//                                    int width = photoTemplateAttachFrame.getWidth();
//                                    int height = photoTemplateAttachFrame.getHeight();
//
//                                    float rate = TemplateUtils.getRateOfEditAreaOnAndroid(backgroundBitmap);
//                                    float androidDx = dx * rate;
//                                    float androidDy = dy * rate;
//                                    float androidWidth = width * rate;
//                                    float androidHeight = height * rate;
//
//                                    Matrix matrix = TemplateUtils.getMatrixByString(photoTemplateAttachFrame.getMatrix());
//                                    // 添加module编辑区域
//                                    addDrawBoard(androidDx, androidDy, androidWidth, androidHeight,
//                                            copyBitmap, copyBitmap, matrix, rate, finalI);
//
//                                    if (mProductionView.mDrawingBoardViewSparseArray.size() == photoTemplateAttachFrameList.size()) {
//                                        ViewUtils.dismiss();
//                                    }
//                                }
//                            });
                        }
                    } else {
                        ViewUtils.dismiss();
                    }

                } else {
                    MDImage image = SelectImageUtils.sAlreadySelectImage.get(mCurrentSelectPageIndex);

                    GlideUtil.getOriginalSizeBitmap(GlobalTemplateEditActivity.this, image, new SimpleTarget<OriginalSizeBitmap>() {
                        @Override
                        public void onResourceReady(OriginalSizeBitmap resource, GlideAnimation<? super OriginalSizeBitmap> glideAnimation) {
                            Bitmap copyBitmap = resource.bitmap.copy(resource.bitmap.getConfig(), true); // safe copy

                            Matrix matrix = TemplateUtils.getMatrixByString(sTemplateImage.get(mCurrentSelectPageIndex)
                                    .getWorkPhoto().getMatrix());
                            addDrawBoard(0, 0, width, height, resource.size.width, resource.size.height,
                                    copyBitmap, copyBitmap, matrix, mRateOfEditArea, 0);
                            ViewUtils.dismiss();
                        }
                    });

//                    GlideUtil.loadImageAsBitmap(image, new SimpleTarget<Bitmap>() {
//                        @Override
//                        public void onResourceReady(Bitmap moduleBitmap, GlideAnimation<? super
//                                Bitmap> glideAnimation) {
//
//                            moduleBitmap = moduleBitmap.copy(moduleBitmap.getConfig(), true); // safe copy
//
//                            Matrix matrix = TemplateUtils.getMatrixByString(SelectImageUtils.sTemplateImage.get(mCurrentSelectPageIndex)
//                                    .getWorkPhoto().getMatrix());
//                            addDrawBoard(0, 0, width, height,
//                                    moduleBitmap, moduleBitmap, matrix, 1.0f, 0);
//                            ViewUtils.dismiss();
//                        }
//                    });
                }

                if (!isSetAdapter) {
                    isSetAdapter = true;
                    mDataBinding.templateRecyclerView.setAdapter(mTeplateImageAdapter);
                }
            }
        });
    }

    private void addDrawBoard(float androidDx, float androidDy, float androidWidth, float androidHeight,
                              int userSelectBitmapOriginalWidth, int UserSelectBitmaporiginalHeight,
                              Bitmap mouldBmp, Bitmap userSelectBitmap, Matrix matrix, float rate, int modulePosition) {
        DrawingBoardView drawingBoardView = new DrawingBoardView(this, this,
                androidWidth, androidHeight, mouldBmp, userSelectBitmap, matrix, rate,
                userSelectBitmapOriginalWidth, UserSelectBitmaporiginalHeight);
        drawingBoardView.setTag(Integer.valueOf(modulePosition));

        int brightLevel = SelectImageUtils.sTemplateImage.get(mCurrentSelectPageIndex).getWorkPhoto().getBrightLevel();
        drawingBoardView.setBrightness(brightLevel);

        FrameLayout.LayoutParams layoutParams = new FrameLayout.LayoutParams((int) androidWidth, (int) androidHeight);
        layoutParams.setMargins((int) androidDx, (int) androidDy, 0, 0);
        drawingBoardView.setLayoutParams(layoutParams);

        drawingBoardView.setOnTouchListener(mDataBinding.scrollView);

        mProductionView.drawBoardLayer.addView(drawingBoardView);
        mProductionView.mDrawingBoardViewSparseArray.put(modulePosition, drawingBoardView);
    }

    private void saveToMyWork() {
        saveCurrentPageEditStatus();
        ViewUtils.loading(this);
        CreateImageUtil.createAllPageHasModulesOnlyParam(new CreateImageUtil.onCreateAllCompositeImageCompleteListener() {
            @Override
            public void onComplete(final List<String> allCompositeImageLocalPathList) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        // 保存到我的作品中
                        MDGroundApplication.sOrderutUtils.mIsJustSaveUserWork = true;
                        MDGroundApplication.sOrderutUtils.uploadAllCompositeImageReuqestNotEnable(GlobalTemplateEditActivity.this,
                                allCompositeImageLocalPathList, 0);
                    }
                });
            }
        });
    }

    private void createCompositeImage() {
        ViewUtils.loading(this);
        if (TemplateUtils.isTemplateHasModules()) {
            CreateImageUtil.createAllPageHasModulesOnlyParam(new CreateImageUtil.onCreateAllCompositeImageCompleteListener() {
                @Override
                public void onComplete(final List<String> allCompositeImageLocalPathList) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            MDGroundApplication.sOrderutUtils.uploadAllCompositeImageReuqestNotEnable(GlobalTemplateEditActivity.this,
                                    allCompositeImageLocalPathList, 0);
                        }
                    });
                }
            });

        } else {
            CreateImageUtil.createAllPageWithoutModulesOnlyParam(new CreateImageUtil.onCreateAllCompositeImageCompleteListener() {
                @Override
                public void onComplete(final List<String> allCompositeImageLocalPathList) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            MDGroundApplication.sOrderutUtils.uploadAllCompositeImageReuqestNotEnable(GlobalTemplateEditActivity.this,
                                    allCompositeImageLocalPathList, 0);
                        }
                    });
                }
            });
        }
    }

    private void toPreviewActivity(List<String> allCompositeImageLocalPathList) {
        ViewUtils.dismiss();

        ArrayList<MDImage> allPreviewImages = new ArrayList<>();
        for (String localPathString : allCompositeImageLocalPathList) {
            MDImage mdImage = new MDImage();
            mdImage.setImageLocalPath(localPathString);

            allPreviewImages.add(mdImage);
        }

        Intent intent = new Intent(this, ImagePreviewActivity.class);
        intent.putParcelableArrayListExtra(Constants.KEY_PREVIEW_IMAGE_LIST, allPreviewImages);
        startActivity(intent);
    }

    public void nextStepAction(View view) {
        saveCurrentPageEditStatus();

        for (int i = 0; i < SelectImageUtils.sAlreadySelectImage.size(); i++) {
            MDImage selectImage = SelectImageUtils.sAlreadySelectImage.get(i);

            if (selectImage.getPhotoSID() == 0 && selectImage.getImageLocalPath() == null) {
                int pageIndex = SelectImageUtils.getPageIndexBySelectPhotoIndex(i);
                Dialog alertDialog = ViewUtils.createAlertDialog(this, getString(R.string.not_add_image, pageIndex + 1),
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
//                                NavUtils.toMainActivity(GlobalTemplateEditActivity.this);
                            }
                        }, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                createCompositeImage();
                            }
                        });
                alertDialog.show();
                return;
            }
        }
        createCompositeImage();
    }

    /**
     * 展示保存对话框
     */
    private void showSaveDialog() {
        String content;
        if (myWorkSize > 9) {
            content = "您的作品已超出限额10个，将自动替换1个作品";
        } else {
            content = getString(R.string.if_add_to_my_work);
        }
        AlertDialog mAlertDialog = ViewUtils.createAlertDialog2(this, content, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                ViewUtils.dismiss();
                NavUtils.toMainActivity(GlobalTemplateEditActivity.this);
            }
        }, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                saveToMyWork();
            }
        });
        mAlertDialog.show();
    }

    //获取作品列表（用户判断提示）
    public void getUserWorkListRequest() {
        GlobalRestful.getInstance().GetUserWorkList(new Callback<ResponseData>() {
            @Override
            public void onResponse(Call<ResponseData> call, Response<ResponseData> response) {
                if (ResponseCode.isSuccess(response.body())) {
                    List<WorkInfo> mAllWorkInfoList = response.body().getContent(new TypeToken<List<WorkInfo>>() {
                    });
                    if (mAllWorkInfoList != null) {
                        myWorkSize = mAllWorkInfoList.size();
                    }
                }
            }

            @Override
            public void onFailure(Call<ResponseData> call, Throwable t) {
            }
        });
    }

}
