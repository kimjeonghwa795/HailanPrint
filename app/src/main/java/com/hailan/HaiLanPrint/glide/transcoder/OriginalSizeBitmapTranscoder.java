package com.hailan.HaiLanPrint.glide.transcoder;

import android.content.Context;
import android.graphics.Bitmap;
import android.support.annotation.NonNull;

import com.hailan.HaiLanPrint.models.OriginalSizeBitmap;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.Resource;
import com.bumptech.glide.load.engine.bitmap_recycle.BitmapPool;
import com.bumptech.glide.load.resource.transcode.ResourceTranscoder;

/**
 * Created by yoghourt on 8/8/16.
 */
public class OriginalSizeBitmapTranscoder implements ResourceTranscoder<Bitmap, OriginalSizeBitmap> {

    private final BitmapPool bitmapPool;

    public OriginalSizeBitmapTranscoder(@NonNull Context context) {
        this.bitmapPool = Glide.get(context).getBitmapPool();
    }

    @Override public Resource<OriginalSizeBitmap> transcode(Resource<Bitmap> toTranscode) {
//        BitmapFactory.Options options = toTranscode.get();
//        Size size = new Size();
//        size.width = options.outWidth;
//        size.height = options.outHeight;
//        return new SimpleResource<>(size);
//
//        Bitmap bitmap = toTranscode.get();
//        Palette palette = new Palette.Builder(bitmap).generate();
//        PaletteBitmap result = new PaletteBitmap(bitmap, palette);
//        return new PaletteBitmapResource(result, bitmapPool);
        return null;
    }

    @Override
    public String getId() {
        return OriginalSizeBitmapTranscoder.class.getName();
    }
}
