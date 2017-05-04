package com.hailan.HaiLanPrint.views;

import android.content.Context;
import android.graphics.Bitmap;
import android.opengl.Matrix;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;

import com.hailan.HaiLanPrint.views.gesture.RotateGestureDetector;

import jp.co.cyberagent.android.gpuimage.GPUImage;
import jp.co.cyberagent.android.gpuimage.GPUImageBrightnessFilter;
import jp.co.cyberagent.android.gpuimage.GPUImageFilterGroup;
import jp.co.cyberagent.android.gpuimage.GPUImageNormalBlendFilter;
import jp.co.cyberagent.android.gpuimage.GPUImageTransformFilter;
import jp.co.cyberagent.android.gpuimage.GPUImageView;

/**
 * Created by yoghourt on 5/19/16.
 */

public class BaoGPUImage extends GPUImageView {

    public interface OnSingleTouchListener {
        public void onSingleTouch();
    }

    private OnSingleTouchListener onSingleTouchListener;

    private GPUImageTransformFilter mTransformFilter;

    public GPUImageBrightnessFilter mBrightnessFilter;

    private GPUImageFilterGroup mFilterGroup;

    private Context mContext;

    private Bitmap mBitmap;

    private float mScaleFactor = 1.0f;  // 放大缩小倍数

    private float mRotationDegrees = 0.f; // 旋转倍数

    private ScaleGestureDetector mScaleDetector;

    private RotateGestureDetector mRotateDetector;

    private boolean mIsMultiTouch = false;

    private class ScaleListener extends ScaleGestureDetector.SimpleOnScaleGestureListener {
        @Override
        public boolean onScale(ScaleGestureDetector detector) {
            mScaleFactor *= detector.getScaleFactor(); // scale change since previous event

            setTransformFactor(mScaleFactor, mRotationDegrees);

            return true;
        }
    }

    private class RotateListener extends RotateGestureDetector.SimpleOnRotateGestureListener {
        @Override
        public boolean onRotate(RotateGestureDetector detector) {
            mRotationDegrees += detector.getRotationDegreesDelta();

            return true;
        }
    }

    public BaoGPUImage(Context context) {
        super(context);
        init(context);
    }

    public BaoGPUImage(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    private void init(Context context) {
        mContext = context;

//        setScaleType(GPUImage.ScaleType.CENTER_INSIDE);

        mScaleDetector = new ScaleGestureDetector(context, new ScaleListener());
        mRotateDetector = new RotateGestureDetector(context, new RotateListener());
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {

        if (getParent() != null) {
            getParent().requestDisallowInterceptTouchEvent(true);
        }

        int action = event.getAction() & MotionEvent.ACTION_MASK;

        if (action == MotionEvent.ACTION_POINTER_DOWN) {
            mIsMultiTouch = true;
        }

        if ((action == MotionEvent.ACTION_UP) && (action != MotionEvent.ACTION_POINTER_UP)) {
            if (!mIsMultiTouch) {
                // Single touch
                if (onSingleTouchListener != null) {
                    onSingleTouchListener.onSingleTouch();
                }
            }
            mIsMultiTouch = false;
        }

        mScaleDetector.onTouchEvent(event);
        mRotateDetector.onTouchEvent(event);

        return true;
    }

    public void loadNewImage(Bitmap bitmap) {
        loadNewImage(bitmap, 1, 0, 0);
    }

    public void loadNewImage(Bitmap bitmap, float scaleFactor, float rotationDegrees, float brightness) {
        mBitmap = bitmap;

        mScaleFactor = scaleFactor;
        mRotationDegrees = rotationDegrees;

        mTransformFilter = new GPUImageTransformFilter();
        mBrightnessFilter = new GPUImageBrightnessFilter();
        mBrightnessFilter.setBrightness(brightness);

        mFilterGroup = new GPUImageFilterGroup();
        mFilterGroup.addFilter(mBrightnessFilter);
        mFilterGroup.addFilter(mTransformFilter);

        setFilter(mFilterGroup);

        getGPUImage().deleteImage();
        setImage(bitmap);

        setTransformFactor(mScaleFactor, mRotationDegrees);
//        requestRender();
    }

    public void setTransformFactor(float scaleFactor, float rotationDegree) {
        mScaleFactor = scaleFactor;
        mRotationDegrees = rotationDegree;

        float[] transform = new float[16];
        Matrix.setIdentityM(transform, 0);
        Matrix.setRotateM(transform, 0, mRotationDegrees, 0, 0, 1.0f);
        if (mScaleFactor < 0) {
            mScaleFactor = 1;
        }
        Matrix.scaleM(transform, 0, mScaleFactor, mScaleFactor, 1.0f);

        mTransformFilter.setTransform3D(transform);
        requestRender();
    }

    public GPUImageTransformFilter getTransformFilter(float scaleFactor, float rotationDegree) {
        float[] transform = new float[16];
        Matrix.setIdentityM(transform, 0);
        Matrix.setRotateM(transform, 0, rotationDegree, 0, 0, 1.0f);
        if (scaleFactor < 0) {
            scaleFactor = 1;
        }
        Matrix.scaleM(transform, 0, scaleFactor, scaleFactor, 1.0f);

        GPUImageTransformFilter transformFilter = new GPUImageTransformFilter();
        transformFilter.setTransform3D(transform);
        return transformFilter;
    }

    public void setmBrightness(float brightness) {
        GPUImageTransformFilter transformFilter = getTransformFilter(mScaleFactor, mRotationDegrees);

        GPUImageBrightnessFilter brightnessFilter = new GPUImageBrightnessFilter();
        mBrightnessFilter.setBrightness(brightness);

        GPUImageFilterGroup gpuImageFilterGroup = new GPUImageFilterGroup();
        gpuImageFilterGroup.addFilter(brightnessFilter);
        gpuImageFilterGroup.addFilter(transformFilter);

        GPUImage gpuImage = new GPUImage(mContext);
        gpuImage.setFilter(gpuImageFilterGroup);
        gpuImage.setImage(mBitmap);

        Bitmap bitmap = gpuImage.getBitmapWithFilterApplied();

        getGPUImage().deleteImage();
        setImage(bitmap);
    }

    public Bitmap addTemplate(Context context, Bitmap templateBitmap) {
        GPUImageNormalBlendFilter blendFilter = new GPUImageNormalBlendFilter();

        blendFilter.setBitmap(templateBitmap);

        GPUImage blendImage = new GPUImage(context);
        blendImage.setImage(getGPUImage().getBitmapWithFilterApplied());
        blendImage.setFilter(blendFilter);

        Bitmap blendBitmap = blendImage.getBitmapWithFilterApplied();

//        GLSurfaceView mGLSurfaceView = new GLSurfaceView(context);

//        mGLSurfaceView.setRenderMode(GLSurfaceView.RENDERMODE_CONTINUOUSLY);


        return blendBitmap;
    }

    public float getmScaleFactor() {
        return mScaleFactor;
    }

    public void setmScaleFactor(float mScaleFactor) {
        this.mScaleFactor = mScaleFactor;
    }

    public float getmRotationDegrees() {
        return mRotationDegrees;
    }

    public void setmRotationDegrees(float mRotationDegrees) {
        this.mRotationDegrees = mRotationDegrees;
    }

    public OnSingleTouchListener getOnSingleTouchListener() {
        return onSingleTouchListener;
    }

    public void setOnSingleTouchListener(OnSingleTouchListener onSingleTouchListener) {
        this.onSingleTouchListener = onSingleTouchListener;
    }
}
