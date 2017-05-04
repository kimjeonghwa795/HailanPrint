package com.hailan.HaiLanPrint.glide.decoder;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import com.hailan.HaiLanPrint.glide.resource.OriginalSizeBitmapResource;
import com.hailan.HaiLanPrint.models.OriginalSizeBitmap;
import com.hailan.HaiLanPrint.models.Size;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.ResourceDecoder;
import com.bumptech.glide.load.engine.Resource;

import java.io.File;
import java.io.IOException;

/**
 * Created by yoghourt on 8/8/16.
 */
public class OriginalSizeBitmapDecoder2 implements ResourceDecoder<File, OriginalSizeBitmap> {

    private final Context mContext;

    public OriginalSizeBitmapDecoder2(Context context) {
        mContext = context;
    }

    @Override
    public Resource<OriginalSizeBitmap> decode(File source, int width, int height) throws IOException {
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(source.getAbsolutePath(), options);

        Size size = new Size();
        size.width = options.outWidth;
        size.height = options.outHeight;

        options.inPreferredConfig = Bitmap.Config.RGB_565;
        options.inSampleSize = calculateInSampleSize(options, width, height);
        options.inJustDecodeBounds = false;
        Bitmap bitmap = BitmapFactory.decodeFile(source.getAbsolutePath(), options);

        OriginalSizeBitmap originalSizeBitmap = new OriginalSizeBitmap();
        originalSizeBitmap.size = size;
        originalSizeBitmap.bitmap = bitmap;
        return new OriginalSizeBitmapResource(originalSizeBitmap, Glide.get(mContext).getBitmapPool());
    }

    public int calculateInSampleSize(BitmapFactory.Options op, int reqWidth, int reqheight) {
        int originalWidth = op.outWidth;
        int originalHeight = op.outHeight;
        int inSampleSize = 1;
        if (originalWidth > reqWidth || originalHeight > reqheight) {
            int halfWidth = originalWidth / 2;
            int halfHeight = originalHeight / 2;
            while ((halfWidth / inSampleSize > reqWidth)
                    && (halfHeight / inSampleSize > reqheight)) {
                inSampleSize *= 2;
            }
        }
        return inSampleSize;
    }

    @Override
    public String getId() {
        return getClass().getName();
    }
}
