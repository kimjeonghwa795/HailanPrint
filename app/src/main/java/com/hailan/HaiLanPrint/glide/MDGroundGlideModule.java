package com.hailan.HaiLanPrint.glide;

import android.content.Context;

import com.bumptech.glide.Glide;
import com.bumptech.glide.GlideBuilder;
import com.bumptech.glide.load.DecodeFormat;
import com.bumptech.glide.load.engine.cache.InternalCacheDiskCacheFactory;
import com.bumptech.glide.module.GlideModule;
import com.bumptech.glide.request.target.ViewTarget;
import com.hailan.HaiLanPrint.R;
import com.hailan.HaiLanPrint.constants.Constants;
import com.hailan.HaiLanPrint.models.MDImage;

import java.io.InputStream;

/**
 * Created by yoghourt on 3/21/16.
 */
public class MDGroundGlideModule implements GlideModule {
    @Override
    public void applyOptions(Context context, GlideBuilder builder) {
        ViewTarget.setTagId(R.id.glide_tag_id); // 设置别的get/set tag id，以免占用View默认的
        builder.setDecodeFormat(DecodeFormat.PREFER_ARGB_8888); // 设置图片质量为高质量

        int cacheSize200MegaBytes = 209715200;

        builder.setDiskCache(
                new InternalCacheDiskCacheFactory(context, Constants.GLIDE_DISK_CACHE_FILE_NAME, cacheSize200MegaBytes)
        );
    }

    @Override
    public void registerComponents(Context context, Glide glide) {
        glide.register(MDImage.class, InputStream.class, new MDGroundLoader.Factory());
    }
}
