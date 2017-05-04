package com.hailan.HaiLanPrint.glide.resource;

import com.hailan.HaiLanPrint.models.OriginalSizeBitmap;
import com.bumptech.glide.load.engine.Resource;
import com.bumptech.glide.load.engine.bitmap_recycle.BitmapPool;
import com.bumptech.glide.util.Util;

/**
 * Created by yoghourt on 8/8/16.
 */
public class OriginalSizeBitmapResource implements Resource<OriginalSizeBitmap> {

    private final OriginalSizeBitmap originalSizeBitmap;
    private final BitmapPool bitmapPool;

    public OriginalSizeBitmapResource(OriginalSizeBitmap originalSizeBitmap, BitmapPool bitmapPool) {
        this.originalSizeBitmap = originalSizeBitmap;
        this.bitmapPool = bitmapPool;
    }

    @Override
    public OriginalSizeBitmap get() {
        return originalSizeBitmap;
    }

    @Override
    public int getSize() {
        return Util.getBitmapByteSize(originalSizeBitmap.bitmap);
    }

    @Override
    public void recycle() {
        if (!bitmapPool.put(originalSizeBitmap.bitmap)) {
            originalSizeBitmap.bitmap.recycle();
        }
    }
}
