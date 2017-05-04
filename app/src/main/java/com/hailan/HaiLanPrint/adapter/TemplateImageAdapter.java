package com.hailan.HaiLanPrint.adapter;

import android.content.Context;
import android.databinding.DataBindingUtil;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.graphics.Point;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;
import com.hailan.HaiLanPrint.R;
import com.hailan.HaiLanPrint.application.MDGroundApplication;
import com.hailan.HaiLanPrint.databinding.ItemTemplateImageBinding;
import com.hailan.HaiLanPrint.enumobject.ProductType;
import com.hailan.HaiLanPrint.models.MDImage;
import com.hailan.HaiLanPrint.models.OriginalSizeBitmap;
import com.hailan.HaiLanPrint.models.PhotoTemplateAttachFrame;
import com.hailan.HaiLanPrint.utils.GlideUtil;
import com.hailan.HaiLanPrint.utils.SelectImageUtils;
import com.hailan.HaiLanPrint.utils.TemplateUtils;
import com.hailan.HaiLanPrint.utils.ViewUtils;
import com.hailan.HaiLanPrint.views.DrawingBoardView;
import com.socks.library.KLog;

import java.util.List;
import java.util.Locale;

import static com.hailan.HaiLanPrint.utils.SelectImageUtils.sTemplateImage;

/**
 * Created by yoghourt on 5/17/16.
 */
public class TemplateImageAdapter extends RecyclerView.Adapter<TemplateImageAdapter.ViewHolder> {

    private int mCurrentSelectedIndex = 0;
    private Context context;

    public interface onSelectImageLisenter {
        void selectImage(int position, MDImage mdImage);
    }

    public void setContext(Context context) {
        this.context = context;
    }

    private onSelectImageLisenter onSelectImageLisenter;

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_template_image, parent, false);
        ViewHolder holder = new ViewHolder(itemView);
        return holder;
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        holder.viewDataBinding.setImage(sTemplateImage.get(position));
        holder.viewDataBinding.setViewHolder(holder);
        holder.viewDataBinding.setIsSelected(position == mCurrentSelectedIndex);
//        GlideUtil.loadImageByMDImage(holder.viewDataBinding.ivImage, sTemplateImage.get(position), true);

        setImage(holder, sTemplateImage.get(position), position);

        String indexString = null;
        if (MDGroundApplication.sInstance.getChoosedProductType() == ProductType.Postcard
                || MDGroundApplication.sInstance.getChoosedProductType() == ProductType.LOMOCard
                || MDGroundApplication.sInstance.getChoosedProductType() == ProductType.Poker) {
            indexString = String.valueOf(position + 1);
        } else {
            if (position == 0) {
                indexString = holder.viewDataBinding.tvIndex.getContext().getString(R.string.cover);
            } else {
                indexString = String.valueOf(position);
            }
        }
        holder.viewDataBinding.tvIndex.setText(indexString);
    }

    @Override
    public int getItemCount() {
        return sTemplateImage.size();
    }

    public void setOnSelectImageLisenter(TemplateImageAdapter.onSelectImageLisenter onSelectImageLisenter) {
        this.onSelectImageLisenter = onSelectImageLisenter;
    }

    private float mRateOfEditArea = 1.0f;

    private float rate = ViewUtils.dp2pxf(100f) / ViewUtils.screenWidth();//根据屏幕对比

    private void setImage(final ViewHolder holder, MDImage mdImage, final int position) {
        holder.viewDataBinding.productionView.clear();
        // 模板背景图片加载
        GlideUtil.getOriginalSizeBitmap2(context, mdImage, new SimpleTarget<OriginalSizeBitmap>() {
            @Override
            public void onResourceReady(OriginalSizeBitmap resource, GlideAnimation<? super OriginalSizeBitmap> glideAnimation) {
                holder.viewDataBinding.productionView.backgroundLayer.setImageBitmap(resource.bitmap);

                // 根据返回Bitmap的大小设置在android上对应的宽高
                Point sizePoint = TemplateUtils.getEditAreaSizeOnAndroid(resource.size);
                final float width = sizePoint.x * rate;
                final float height = sizePoint.y * rate;

                holder.viewDataBinding.productionView.setWidthAndHeight((int) width, (int) height);

                mRateOfEditArea = TemplateUtils.getRateOfEditAreaOnAndroid(resource.size);

                // 杂志册,艺术册,个性月历 这三个功能块有定位块
                if (TemplateUtils.isTemplateHasModules()) {

                    // 各个编辑定位块加载
                    MDImage templateImage = sTemplateImage.get(position);
                    final List<PhotoTemplateAttachFrame> photoTemplateAttachFrameList = templateImage.getPhotoTemplateAttachFrameList();

                    if (photoTemplateAttachFrameList != null && photoTemplateAttachFrameList.size() > 0) {
                        for (int moduleIndex = 0; moduleIndex < photoTemplateAttachFrameList.size(); moduleIndex++) {
                            final PhotoTemplateAttachFrame photoTemplateAttachFrame = photoTemplateAttachFrameList.get(moduleIndex);

                            final int finalI = moduleIndex;

                            MDImage moduleShowImage = SelectImageUtils
                                    .getMdImageByPageIndexAndModuleIndex(position, moduleIndex);

                            // 加载用户选择的图片
                            GlideUtil.loadImageAsLimitBitmap(context, moduleShowImage, new SimpleTarget<Bitmap>() {
                                @Override
                                public void onResourceReady(Bitmap resource, GlideAnimation<? super Bitmap> glideAnimation) {
                                    KLog.e("定位块位置 : " + finalI + "图片的original size是: ", String.format(Locale.ROOT, "%dx%d", resource.getWidth(), resource.getHeight()));
                                    KLog.e("定位块位置 : " + finalI + "图片的压缩的 size是: ", String.format(Locale.ROOT, "%dx%d", resource.getWidth(), resource.getHeight()));

                                    Bitmap copyBitmap = resource.copy(resource.getConfig(), true); // safe copy

                                    int dx = photoTemplateAttachFrame.getPositionX();
                                    int dy = photoTemplateAttachFrame.getPositionY();
                                    int width = photoTemplateAttachFrame.getWidth();
                                    int height = photoTemplateAttachFrame.getHeight();

                                    float androidDx = dx * mRateOfEditArea * rate;
                                    float androidDy = dy * mRateOfEditArea * rate;
                                    float androidWidth = width * mRateOfEditArea * rate;
                                    float androidHeight = height * mRateOfEditArea * rate;

                                    Matrix matrix = TemplateUtils.getMatrixByString(photoTemplateAttachFrame.getMatrix(), rate);

                                    // 添加module编辑区域
                                    addDrawBoard(androidDx, androidDy, androidWidth, androidHeight, resource.getWidth(), resource.getHeight(),
                                            copyBitmap, copyBitmap, matrix, mRateOfEditArea, finalI, holder, position);

                                    if (holder.viewDataBinding.productionView.mDrawingBoardViewSparseArray.size() == photoTemplateAttachFrameList.size()) {
                                        ViewUtils.dismiss();
                                    }
                                }
                            });

                        }
                    } else {
                        ViewUtils.dismiss();
                    }
                } else {
                    MDImage image = SelectImageUtils.sAlreadySelectImage.get(position);

                    GlideUtil.loadImageAsLimitBitmap(context, image, new SimpleTarget<Bitmap>() {
                        @Override
                        public void onResourceReady(Bitmap resource, GlideAnimation<? super Bitmap> glideAnimation) {
                            Bitmap copyBitmap = resource.copy(resource.getConfig(), true); // safe copy

                            Matrix matrix = TemplateUtils.getMatrixByString(sTemplateImage.get(position)
                                    .getWorkPhoto().getMatrix(), rate);

                            addDrawBoard(0, 0, width, height, resource.getWidth(), resource.getHeight(),
                                    copyBitmap, copyBitmap, matrix, mRateOfEditArea, 0, holder, position);
                            ViewUtils.dismiss();
                        }
                    });
                }
            }
        });
    }

    private void addDrawBoard(float androidDx, float androidDy, float androidWidth, float androidHeight,
                              int userSelectBitmapOriginalWidth, int UserSelectBitmaporiginalHeight,
                              Bitmap mouldBmp, Bitmap userSelectBitmap, Matrix matrix, float rate, int modulePosition, final ViewHolder holder, final int position) {
        DrawingBoardView drawingBoardView = new DrawingBoardView(context, null,
                androidWidth, androidHeight, mouldBmp, userSelectBitmap, matrix, rate,
                userSelectBitmapOriginalWidth, UserSelectBitmaporiginalHeight, false);
        drawingBoardView.setTag(Integer.valueOf(modulePosition));

        int brightLevel = SelectImageUtils.sTemplateImage.get(position).getWorkPhoto().getBrightLevel();
        drawingBoardView.setBrightness(brightLevel);

        FrameLayout.LayoutParams layoutParams = new FrameLayout.LayoutParams((int) androidWidth, (int) androidHeight);
        layoutParams.setMargins((int) androidDx, (int) androidDy, 0, 0);
        drawingBoardView.setLayoutParams(layoutParams);

        holder.viewDataBinding.productionView.drawBoardLayer.addView(drawingBoardView);
        holder.viewDataBinding.productionView.mDrawingBoardViewSparseArray.put(modulePosition, drawingBoardView);
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        public ItemTemplateImageBinding viewDataBinding;

        public ViewHolder(View itemView) {
            super(itemView);
            viewDataBinding = DataBindingUtil.bind(itemView);
        }

        public void selectImageAction(View view) {
            int position = getAdapterPosition();
            int lastSelectIndex = mCurrentSelectedIndex;
            mCurrentSelectedIndex = position;
            notifyItemChanged(lastSelectIndex);
            notifyItemChanged(mCurrentSelectedIndex);

            if (position < sTemplateImage.size()) {
                MDImage mdImage = sTemplateImage.get(position);

                if (onSelectImageLisenter != null) {
                    onSelectImageLisenter.selectImage(/**/position, mdImage);
                }
            }
        }
    }
}