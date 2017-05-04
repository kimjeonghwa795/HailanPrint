package com.hailan.HaiLanPrint.activity.imagepreview;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;
import com.hailan.HaiLanPrint.R;
import com.hailan.HaiLanPrint.constants.Constants;
import com.hailan.HaiLanPrint.models.MDImage;
import com.hailan.HaiLanPrint.utils.GlideUtil;

import uk.co.senab.photoview.PhotoViewAttacher;

/**
 * Created by yoghourt on 2016-08-4.
 */
public class ImagePreviewFragment extends Fragment {

    public static ImagePreviewFragment getInstance(MDImage mdImage) {
        ImagePreviewFragment fragment = new ImagePreviewFragment();
        Bundle bundle = new Bundle();
        bundle.putParcelable(Constants.KEY_PREVIEW_IMAGE, mdImage);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View contentView = inflater.inflate(R.layout.fragment_image_preview, container, false);
        final ImageView ivPreview = (ImageView) contentView.findViewById(R.id.ivPreview);
        final PhotoViewAttacher mAttacher = new PhotoViewAttacher(ivPreview);

        MDImage mdImage = getArguments().getParcelable(Constants.KEY_PREVIEW_IMAGE);

        GlideUtil.loadImageAsBitmapWithoutCache(mdImage, new SimpleTarget<Bitmap>() {
            @Override
            public void onResourceReady(Bitmap bitmap, GlideAnimation<? super
                    Bitmap> glideAnimation) {
                ivPreview.setImageBitmap(bitmap);
                mAttacher.update();
            }
        });

        mAttacher.setOnViewTapListener(new PhotoViewAttacher.OnViewTapListener() {
            @Override
            public void onViewTap(View view, float x, float y) {
            }
        });
        return contentView;
    }
}
