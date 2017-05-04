package com.hailan.HaiLanPrint.views;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PointF;
import android.graphics.Rect;
import android.os.Environment;
import android.support.v4.content.ContextCompat;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ScrollView;

import com.hailan.HaiLanPrint.R;
import com.hailan.HaiLanPrint.utils.ViewUtils;
import com.socks.library.KLog;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;

/**
 * Created by yoghourt on 7/12/16.
 */

public class DrawingBoardView extends View {

    private static final int MODE_UP = 0;
    private static final int MODE_DRAG = 1;
    private static final int MODE_ZOOM = 2;

    private Context mContext;

    private ScrollView mScrollView;

    private Bitmap mPhotoBmp, mMouldBmp, mOutputBmp;

    private Rect mSrcRect, mDestRect;

    private PointF mStartPoint = new PointF();
    private Matrix mMatrixOfEditPhoto = new Matrix();
    private Matrix mCurrentMatrix = new Matrix();
    private PointF mMidPoint;

    private float mViewHeight, mViewWidth;
    private float mStartDistance;
    private float mOldRotation = 0.0f;
    private float mDx, mDy;
    // 定位块在android上的缩放比例(比如定位块实际像素是200 * 200的, 如果mRate是0.5, 则定位块宽高是100 * 100)
    private float mRate;
    // 如何用户选择的图片太大,则根据定位块的宽高和用户选择图片的宽高比例来设置图片
    private float mUserSelectPhotoSetScale;
    private float mUserSelectPhotoOriginalScale;
    private float mBrightness;
    private int mMode = MODE_UP;
    private boolean mIsTouch, mIsSelected, touchable;

    private OnDrawingBoardClickListener mOnDrawingBoardClickListener;

    public interface OnDrawingBoardClickListener {
        void onDrawingBoardTouch(DrawingBoardView drawingBoardView);

        void onDrawingBoardClick(DrawingBoardView drawingBoardView);
    }

    public DrawingBoardView(Context context) {
        super(context);
        mContext = context;
    }

    public DrawingBoardView(Context context, OnDrawingBoardClickListener onDrawingBoardClickListener,
                            float width, float height, Bitmap mouldBmp, Bitmap userSelectBitmap,
                            Matrix matrix, float rate, int userSelectBitmapOriginalWidth,
                            int userSelectBitmapOriginalHeight) {
        this(context, onDrawingBoardClickListener, width, height, mouldBmp, userSelectBitmap, matrix, rate,
                userSelectBitmapOriginalWidth, userSelectBitmapOriginalHeight, true);
    }

    public DrawingBoardView(Context context, OnDrawingBoardClickListener onDrawingBoardClickListener,
                            float width, float height, Bitmap mouldBmp, Bitmap userSelectBitmap,
                            Matrix matrix, float rate, int userSelectBitmapOriginalWidth,
                            int userSelectBitmapOriginalHeight, boolean touchable) {
        super(context);
        mContext = context;
        mOnDrawingBoardClickListener = onDrawingBoardClickListener;
        this.touchable = touchable;
        setFocusable(true);
        setFocusableInTouchMode(true);

        if (width < 1) {
            width = 1;
        }
        if (height < 1) {
            height = 1;
        }

        setMouldBitmap(width, height, mouldBmp);

//        KLog.e("设置的Matrix : " + matrix);

        setUserSelectBitmap(userSelectBitmap, matrix, rate,
                userSelectBitmapOriginalWidth, userSelectBitmapOriginalHeight);

//        mTestBitmap = BitmapFactory.decodeResource(mContext.getResources(),
//                R.drawable.icon_mz_home);
//        mTestBitmap = convertToMutable(mTestBitmap);

    }

    public void setMouldBitmap(float width, float height, Bitmap mouldBmp) {
        if (mouldBmp != null) {
            mViewWidth = width;
            mViewHeight = height;
            mMouldBmp = mouldBmp;
            this.mSrcRect = new Rect(0, 0, mMouldBmp.getWidth(), mMouldBmp.getHeight());
            this.mDestRect = new Rect(0, 0, (int) width, (int) height);
            try {
                mOutputBmp = Bitmap.createBitmap((int) width, (int) height, Bitmap.Config.ARGB_4444);
            } catch (OutOfMemoryError e) {
//                BitMapUtil.oom();
                try {
                    mOutputBmp = Bitmap.createBitmap((int) width, (int) height, Bitmap.Config.ARGB_4444);
                } catch (OutOfMemoryError e2) {
//                    BitMapUtil.oom();
                    mOutputBmp = Bitmap.createBitmap((int) width, (int) height, Bitmap.Config.ARGB_4444);
                }
            }
        }
    }

    public void setUserSelectBitmap(Bitmap userSelectBitmap, Matrix matrix, float rate,
                                    int userSelectBitmapOriginalWidth, int userSelectBitmapOriginalHeight) {
        if (userSelectBitmap != null) {
            mRate = rate;
            if (matrix == null) {
                matrix = new Matrix();
            }
            mMatrixOfEditPhoto = matrix;

            int photoBmpWidth = userSelectBitmap.getWidth();
            int photoBmpHeight = userSelectBitmap.getHeight();

            KLog.e("userSelectBitmap的原宽度 : " + userSelectBitmapOriginalWidth);
            KLog.e("userSelectBitmap的原高度 : " + userSelectBitmapOriginalHeight);

            KLog.e("压缩后的userSelectBitmap的宽度 : " + photoBmpWidth);
            KLog.e("压缩后的userSelectBitmap的高度 : " + photoBmpHeight);

            mUserSelectPhotoSetScale = mViewWidth / ((float) photoBmpWidth) > mViewHeight / ((float) photoBmpHeight)
                    ? mViewWidth / ((float) photoBmpWidth)
                    : mViewHeight / ((float) photoBmpHeight);

            mUserSelectPhotoOriginalScale = photoBmpWidth / ((float) userSelectBitmapOriginalWidth) > photoBmpHeight / ((float) userSelectBitmapOriginalHeight)
                    ? photoBmpWidth / ((float) userSelectBitmapOriginalWidth)
                    : photoBmpHeight / ((float) userSelectBitmapOriginalHeight);

            if (mUserSelectPhotoSetScale != 0.0f) {
                Bitmap scalePhotoBmp;
                Matrix photoMatrix = new Matrix();
                photoMatrix.setScale(mUserSelectPhotoSetScale, mUserSelectPhotoSetScale);
                try {
                    scalePhotoBmp = Bitmap.createBitmap(userSelectBitmap, 0, 0, photoBmpWidth, photoBmpHeight, photoMatrix, true);
                } catch (OutOfMemoryError e) {
//                    BitMapUtil.oom();
                    try {
                        scalePhotoBmp = Bitmap.createBitmap(userSelectBitmap, 0, 0, photoBmpWidth, photoBmpHeight, photoMatrix, true);
                    } catch (OutOfMemoryError e2) {
//                        BitMapUtil.oom();
                        scalePhotoBmp = Bitmap.createBitmap(userSelectBitmap, 0, 0, photoBmpWidth, photoBmpHeight, photoMatrix, true);
                    }
                }
                if (scalePhotoBmp != userSelectBitmap) {
                    userSelectBitmap.recycle();
                }

//                KLog.e("photoBmp缩放后宽度 : " + scalePhotoBmp.getWidth());
//                KLog.e("photoBmp缩放后高度 : " + scalePhotoBmp.getHeight());

                mPhotoBmp = scalePhotoBmp;
//                float photoBitmapWidth = (float) scalePhotoBmp.getWidth();
//                float photoBitmapHeight = (float) scalePhotoBmp.getHeight();
//                mDx = (this.mViewWidth - photoBitmapWidth) / 2.0f;
//                mDy = (this.mViewHeight - photoBitmapHeight) / 2.0f;
//                mMatrixOfEditPhoto.preTranslate(mDx, mDy);
            }
            invalidate();
        }
    }

    private void compositeBitmap() {
//        mOutputBmp.eraseColor(Color.parseColor("#00000000"));
        mOutputBmp.eraseColor(ContextCompat.getColor(mContext, R.color.color_f8f8f8));

        Canvas canvas = new Canvas(mOutputBmp);
        Paint paint = new Paint();
        paint.setAntiAlias(true);
        paint.setFilterBitmap(true);
        paint.setColor(Color.parseColor("#BAB399"));

        // 亮度调整
        ColorMatrix cMatrix = new ColorMatrix();
        cMatrix.set(new float[]{
                1, 0, 0, 0, mBrightness,
                0, 1, 0, 0, mBrightness,
                0, 0, 1, 0, mBrightness,
                0, 0, 0, 1, 0
        });
        paint.setColorFilter(new ColorMatrixColorFilter(cMatrix));

//        mMouldBmp = mTestBitmap;
        if (!(mMouldBmp == null || mMouldBmp.isRecycled())) {
            /**
             * Rect src: 是对图片进行裁截，若是空null则显示整个图片
             * RectF dst：是图片在Canvas画布中显示的区域，大于src则把src的裁截区放大，小于src则把src的裁截区缩小。
             */
//            KLog.e("mSrcRect : " + mSrcRect);
//            KLog.e("mDestRect : " + mDestRect);
            canvas.drawBitmap(mMouldBmp, this.mSrcRect, this.mDestRect, paint);
        }

        // 第一次用canvas.drawBitmap的bitmap是dest,第二次draw的bitmap是src
//        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN)); // 取两层绘制交集。显示上层(src,即这里的变量mPhotoBmp)。
        paint.setDither(true);

        if (mPhotoBmp != null && !mPhotoBmp.isRecycled()) {
            canvas.drawBitmap(mPhotoBmp, mMatrixOfEditPhoto, paint);
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
//        boolean isCanDraw = mViewWidth != 0.0f && mViewHeight != 0.0f && mMouldBmp != null && mPhotoBmp != null;
        boolean isCanDraw = mViewWidth != 0.0f
                && mViewHeight != 0.0f
                && mPhotoBmp != null;
        if (isCanDraw) {
            float[] values = new float[9];
            mMatrixOfEditPhoto.getValues(values);
            compositeBitmap();
            canvas.drawBitmap(mOutputBmp, 0.0f, 0.0f, null);
//            float resizeScale = (float) Math.sqrt((double) ((values[Matrix.MSCALE_X] * values[Matrix.MSCALE_X])
//                    + (values[Matrix.MSKEW_Y] * values[Matrix.MSKEW_Y])));
//            if (mRate < resizeScale / mPhotoSetScale) {
//                drawPixelDeficiency(canvas);
//            }
            // photoBitmap的缩放比例
            float scaleX = values[Matrix.MSCALE_X];
            float scaleY = values[Matrix.MSCALE_Y];
            float resizeScale = (float) Math.sqrt((double) ((scaleX * scaleX) + (scaleY * scaleY)));
//            if (resizeScale > (mRate / mPhotoSetScale)) {
//                drawPixelDeficiency(canvas);
//            }

            KLog.e((resizeScale * mUserSelectPhotoSetScale) * mRate * mUserSelectPhotoOriginalScale);

            if ((resizeScale * mUserSelectPhotoSetScale) * mRate * mUserSelectPhotoOriginalScale > 1.2) { // 根据需求, 缩放大于1.2倍提示像素不足
                if (touchable) {// 如果是触摸模式，即大图模式才显示像素不足
                    drawPixelDeficiency(canvas);
                }
            }
//            if ((scaleX * mUserSelectPhotoOriginalScale) > 1.2) { // 根据需求, 缩放大于1.2倍提示像素不足
//                drawPixelDeficiency(canvas);
//            }
            if (mIsSelected) {
                drawWarningLine(canvas);
            }
        }
    }

    public PointF matrixCalculator() {
        Matrix matrix = getMatrixOfEditPhoto();

        float[] values = new float[9];
        matrix.getValues(values);
        float sinO = values[3];
        float cosO = values[0];
        float scaleM = (float) Math.sqrt((double) ((sinO * sinO) + (cosO * cosO)));
        sinO /= scaleM;
        cosO /= scaleM;
        float l = (float) Math.sqrt((double) ((mViewWidth * mViewWidth) + (mViewHeight * mViewHeight)));
        float sinA = mViewWidth / l;
        float cosA = mViewHeight / l;
        float x1 = (l * ((sinA * cosO) - (cosA * sinO))) / 2.0f;
        float y1 = (l * ((cosA * cosO) + (sinA * sinO))) / 2.0f;
        return new PointF((mViewWidth / 2.0f) - (scaleM * x1), (mViewHeight / 2.0f) - (scaleM * y1));
    }

    // 像素不足
    private void drawPixelDeficiency(Canvas canvas) {
        Paint textPaint = new Paint();
        textPaint.setColor(ContextCompat.getColor(mContext, R.color.colorRed));
        textPaint.setTextSize(ViewUtils.dp2px(18));

        String text = mContext.getString(R.string.pixel_deficiency);
        float textWidth = textPaint.measureText(text);

        if (textWidth > mViewWidth) {
            textPaint.setTextSize(mViewWidth / 5);
            textWidth = textPaint.measureText(text);
        }

        float textHeight = textPaint.getFontMetricsInt(null);

        canvas.drawText(text, (mViewWidth - textWidth) / 2.0f,
                ((mViewHeight - textHeight) / 2.0f) - textPaint.getFontMetrics().top,
                textPaint);
    }

    private void drawWarningLine(Canvas canvas) {
        Paint paint = new Paint();
        paint.setColor(ContextCompat.getColor(mContext, R.color.color_0076fe));

        float lineWidth = ViewUtils.dp2px(1.0f);
        paint.setStrokeWidth(lineWidth);

        float startPosition = lineWidth / 2.0f;

        // 左线
        canvas.drawLine(startPosition, startPosition, startPosition, mViewHeight - startPosition, paint);

        // 上线
        canvas.drawLine(0, startPosition, mViewWidth, startPosition, paint);

        // 右线
        canvas.drawLine(mViewWidth - startPosition, startPosition, mViewWidth - startPosition, mViewHeight - startPosition, paint);

        // 下线
        canvas.drawLine(0, mViewHeight - startPosition, mViewWidth, mViewHeight - startPosition, paint);
    }

    public void setMatrix(float pos1, float pos2, float pos3, float pos4, float pos5, float pos6) {
        float[] values = new float[9];
        mMatrixOfEditPhoto.getValues(values);
        values[0] = pos1;
        values[1] = pos2;
        values[2] = pos3;
        values[3] = pos4;
        values[4] = pos5;
        values[5] = pos6;
        mMatrixOfEditPhoto.setValues(values);
    }

    public Matrix getMatrixOfEditPhoto() {
        Matrix matrix = new Matrix(mMatrixOfEditPhoto);
//        matrix.preTranslate(-mDx, -mDy);
//        KLog.e("拿到的Matrix : " + matrix);
        return matrix;
    }

    public void translate(String dx, String dy) {
        mMatrixOfEditPhoto.postTranslate(Float.parseFloat(dx), Float.parseFloat(dy));
    }

    public void bigger() {
        mMatrixOfEditPhoto.postScale(1.1f, 1.1f, getWidth() / 2, getHeight() / 2);
        invalidate();
    }

    public void littler() {
        mMatrixOfEditPhoto.postScale(0.9f, 0.9f, getWidth() / 2, getHeight() / 2);
        invalidate();
    }

    public void rotate() {
        mMatrixOfEditPhoto.postRotate(90.0f, getWidth() / 2, getHeight() / 2);
        invalidate();
    }

    // 翻转图像
    public void reverse() {
        mMatrixOfEditPhoto.postScale(-1.0f, 1.0f);
        mMatrixOfEditPhoto.postTranslate((float) getWidth(), 0.0f);
        invalidate();
    }

    public void setOnTouchListener(ScrollView scrollView) {
        mScrollView = scrollView;
        setOnTouchListener(new OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                onTouchEvent(event);
                return true;
            }
        });
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (!touchable) {
            return false;
        }
        this.mIsSelected = false;
        float x = event.getX();
        float y = event.getY();

        if (event.getAction() == MotionEvent.ACTION_UP) {
            if (mScrollView != null) {
                mScrollView.requestDisallowInterceptTouchEvent(false); // 允许ScrollView拦截事件
            }
            this.mIsTouch = false;
        }

        if (x > 0.0f && x < mViewWidth && y > 0.0f && y < mViewHeight) {
            touch(event, x, y);

            if (event.getAction() != MotionEvent.ACTION_UP) {
                if (mScrollView != null) {
                    mScrollView.requestDisallowInterceptTouchEvent(true);
                }
                mIsTouch = true;
            } else {
                if (mScrollView != null) {
                    mScrollView.requestDisallowInterceptTouchEvent(false);
                }
                mIsTouch = false;
            }
        }

        invalidate();

        return true;
    }

    private void touch(MotionEvent event, float x, float y) {
        mIsSelected = true;

        switch (event.getAction() & MotionEvent.ACTION_MASK) {
            case MotionEvent.ACTION_DOWN:
                mMode = MODE_DRAG;
                mCurrentMatrix.set(mMatrixOfEditPhoto);
                mStartPoint.set(x, y);

                if (mOnDrawingBoardClickListener != null) {
                    mOnDrawingBoardClickListener.onDrawingBoardTouch(this);
                }
                break;

            case MotionEvent.ACTION_POINTER_DOWN:
                mMode = MODE_ZOOM;
                mStartDistance = distance(event);
                if (mStartDistance > 10.0f) {
                    mMidPoint = mid(event);
                }
                mOldRotation = rotation(event);
                break;

            case MotionEvent.ACTION_UP:
                mMode = MODE_UP;
                mIsSelected = false;
                if (moveDistance(x, y) < ViewUtils.dp2px(5)) {
                    // 点击事件触发
                    if (mOnDrawingBoardClickListener != null) {
                        mOnDrawingBoardClickListener.onDrawingBoardClick(this);
                    }
                }
                break;
            case MotionEvent.ACTION_POINTER_UP:
                mMode = MODE_UP;
                break;

            case MotionEvent.ACTION_MOVE:
                if (mMode == MODE_DRAG) {
                    float dx = event.getX() - mStartPoint.x;
                    float dy = event.getY() - mStartPoint.y;
                    mMatrixOfEditPhoto.set(mCurrentMatrix);
                    mMatrixOfEditPhoto.postTranslate(dx, dy);
                } else if (mMode == MODE_ZOOM) {
                    float endDistance = distance(event);

                    if (endDistance > 10.0f) {
                        float scale = endDistance / mStartDistance;

                        mMatrixOfEditPhoto.set(mCurrentMatrix);
                        mMatrixOfEditPhoto.postScale(scale, scale, mMidPoint.x, mMidPoint.y);
                        mMatrixOfEditPhoto.postRotate(rotation(event) - mOldRotation, mMidPoint.x, mMidPoint.y);
                    }
                }
                break;
        }
    }

    private float moveDistance(float x, float y) {
        double sum = Math.pow(x - mStartPoint.x, 2) + Math.pow(y - mStartPoint.y, 2);
        double distance = Math.sqrt(sum);
        return (float) distance;
    }

    private float distance(MotionEvent event) {
        try {
            float dx = event.getX(1) - event.getX(0);
            float dy = event.getY(1) - event.getY(0);
            return (float) Math.sqrt((dx * dx) + (dy * dy));
        } catch (IllegalArgumentException exception) {
            return 0;
        }
    }

    private PointF mid(MotionEvent event) {
        return new PointF((event.getX(1) + event.getX(0)) / 2.0f,
                (event.getY(1) + event.getY(0)) / 2.0f);
    }

    private float rotation(MotionEvent event) {
        float angle = (float) Math.toDegrees(Math.atan2((double) (event.getY(0) - event.getY(1)),
                (double) (event.getX(0) - event.getX(1))));
        return angle;
    }

    public void clear() {
        if (!(mOutputBmp == null || mOutputBmp.isRecycled())) {
            mOutputBmp.recycle();
            mOutputBmp = null;
        }
        if (mPhotoBmp != null && !mPhotoBmp.isRecycled()) {
            mPhotoBmp.recycle();
            mPhotoBmp = null;
        }
    }

    public void setBrightness(float brightness) {
        mBrightness = brightness;
        invalidate();
    }

    /**
     * Converts a immutable bitmap to a mutable bitmap. This operation doesn't allocates
     * more memory that there is already allocated.
     *
     * @param imgIn - Source image. It will be released, and should not be used more
     * @return a copy of imgIn, but muttable.
     */
    public static Bitmap convertToMutable(Bitmap imgIn) {
        try {
            //this is the file going to use temporally to save the bytes.
            // This file will not be a image, it will store the raw image data.
            File file = new File(Environment.getExternalStorageDirectory() + File.separator + "temp.tmp");

            //Open an RandomAccessFile
            //Make sure you have added uses-permission android:name="android.permission.WRITE_EXTERNAL_STORAGE"
            //into AndroidManifest.xml file
            RandomAccessFile randomAccessFile = new RandomAccessFile(file, "rw");

            // get the width and height of the source bitmap.
            int width = imgIn.getWidth();
            int height = imgIn.getHeight();
            Bitmap.Config type = imgIn.getConfig();

            //Copy the byte to the file
            //Assume source bitmap loaded using options.inPreferredConfig = Config.ARGB_8888;
            FileChannel channel = randomAccessFile.getChannel();
            MappedByteBuffer map = channel.map(FileChannel.MapMode.READ_WRITE, 0, imgIn.getRowBytes() * height);
            imgIn.copyPixelsToBuffer(map);
            //recycle the source bitmap, this will be no longer used.
            imgIn.recycle();
            System.gc();// try to force the bytes from the imgIn to be released

            //Create a new bitmap to load the bitmap again. Probably the memory will be available.
            imgIn = Bitmap.createBitmap(width, height, type);
            map.position(0);
            //load it back from temporary
            imgIn.copyPixelsFromBuffer(map);
            //close the temporary file and channel , then delete that also
            channel.close();
            randomAccessFile.close();

            // delete the temp file
            file.delete();

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return imgIn;
    }
}

