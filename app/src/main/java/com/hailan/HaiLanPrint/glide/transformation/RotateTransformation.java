package com.hailan.HaiLanPrint.glide.transformation;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Matrix;

import com.bumptech.glide.load.engine.bitmap_recycle.BitmapPool;
import com.bumptech.glide.load.resource.bitmap.BitmapTransformation;

/**
 * Created by yoghourt on 7/13/16.
 */

public class RotateTransformation extends BitmapTransformation {

    private float mRotateAngle = 0f;

    public RotateTransformation(Context context, float rotateAngle) {
        super(context);

        this.mRotateAngle = rotateAngle;
    }

    @Override
    protected Bitmap transform(BitmapPool pool, Bitmap toTransform, int outWidth, int outHeight) {
        Matrix matrix = new Matrix();

        matrix.postRotate(mRotateAngle);

        return Bitmap.createBitmap(toTransform, 0, 0, toTransform.getWidth(), toTransform.getHeight(), matrix, true);
    }

    @Override
    public String getId() {
        return "rotate" + mRotateAngle;
    }
}
