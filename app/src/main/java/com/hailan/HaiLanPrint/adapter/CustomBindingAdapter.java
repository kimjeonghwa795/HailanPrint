package com.hailan.HaiLanPrint.adapter;

import android.widget.ImageView;

import com.hailan.HaiLanPrint.models.MDImage;
import com.hailan.HaiLanPrint.utils.GlideUtil;

/**
 * Created by yoghourt on 5/12/16.
 */
public class CustomBindingAdapter {

    public static void loadImageByMDImage(ImageView imageView, MDImage mdImage) {
        GlideUtil.loadImageByMDImage(imageView, mdImage, true);
    }

    public static void loadImageByPhotoSID(ImageView imageView, int photoSID) {
        GlideUtil.loadImageByPhotoSID(imageView, photoSID, true);
    }
}

