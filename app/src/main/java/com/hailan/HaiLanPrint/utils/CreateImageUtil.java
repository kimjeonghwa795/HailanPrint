package com.hailan.HaiLanPrint.utils;

import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.RectF;

import com.hailan.HaiLanPrint.models.MDImage;
import com.hailan.HaiLanPrint.models.OriginalSizeBitmap;
import com.hailan.HaiLanPrint.models.PhotoTemplateAttachFrame;
import com.hailan.HaiLanPrint.models.WorkPhotoEdit;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by yoghourt on 8/2/16.
 */
public class CreateImageUtil {

    public static final String SAVE_PATH = Tools.getAppPath() + File.separator + "work";

    public interface onCreateAllCompositeImageCompleteListener {
        void onComplete(List<String> allCompositeImageLocalPathList);
    }

    // 有定位块的模版图片生成
    public static void createAllPageHasModules(final onCreateAllCompositeImageCompleteListener listener) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                List<String> allCompositeImageLocalPathList = new ArrayList<>();
                for (int pageIndex = 0; pageIndex < SelectImageUtils.sTemplateImage.size(); pageIndex++) {
                    Bitmap bitmap = createPageBitmapHasModules(pageIndex);

                    String filePath = saveBitmapToSDCard(bitmap, pageIndex);
                    allCompositeImageLocalPathList.add(filePath);
                }

                if (listener != null) {
                    listener.onComplete(allCompositeImageLocalPathList);
                }
            }
        }).start();
    }

    // 没有定位块的模版图片生成
    public static void createAllPageWithoutModules(final onCreateAllCompositeImageCompleteListener listener) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                List<String> allCompositeImageLocalPathList = new ArrayList<>();
                for (int pageIndex = 0; pageIndex < SelectImageUtils.sTemplateImage.size(); pageIndex++) {
                    Bitmap bitmap = createPageBitmapWithoutModules(pageIndex);

                    String filePath = saveBitmapToSDCard(bitmap, pageIndex);
                    allCompositeImageLocalPathList.add(filePath);
                }

                if (listener != null) {
                    listener.onComplete(allCompositeImageLocalPathList);
                }
            }
        }).start();
    }

    // 有定位块的模版图片生成（不生成图片，只处理参数）
    public static void createAllPageHasModulesOnlyParam(final onCreateAllCompositeImageCompleteListener listener) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                List<String> allCompositeImageLocalPathList = new ArrayList<>();
                for (int pageIndex = 0; pageIndex < SelectImageUtils.sTemplateImage.size(); pageIndex++) {
                    createPageBitmapHasModulesOnlyParam(pageIndex);
                }

                if (listener != null) {
                    listener.onComplete(allCompositeImageLocalPathList);
                }
            }
        }).start();
    }

    // 没有定位块的模版图片生成（不生成图片，只处理参数）
    public static void createAllPageWithoutModulesOnlyParam(final onCreateAllCompositeImageCompleteListener listener) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                List<String> allCompositeImageLocalPathList = new ArrayList<>();
                for (int pageIndex = 0; pageIndex < SelectImageUtils.sTemplateImage.size(); pageIndex++) {
                    createPageBitmapWithoutModulesOnlyParam(pageIndex);
                }
                if (listener != null) {
                    listener.onComplete(allCompositeImageLocalPathList);
                }
            }
        }).start();
    }

    // 没有定位块的模版图片生成（不生成图片，只处理参数）
    public static void createPageBitmapWithoutModulesOnlyParam(int pageIndex) {
        MDImage templateImage = SelectImageUtils.sTemplateImage.get(pageIndex);
        OriginalSizeBitmap originalSizeBitmap = GlideUtil.loadImageAsOriginalSizeBitmap(templateImage);

        float rateOfEditWidth = TemplateUtils.getRateOfEditAreaOnAndroid(originalSizeBitmap.size);

        // 用户编辑模块绘制
        MDImage userSelectImage = SelectImageUtils.sAlreadySelectImage.get(pageIndex);
        OriginalSizeBitmap userSelectOriginalSizeBitmap = GlideUtil.loadImageAsOriginalSizeBitmap(userSelectImage);

        Matrix matrix = TemplateUtils.getMatrixByString(templateImage.getWorkPhoto().getMatrix());

        compositePictureOnlyParam(pageIndex, 0, originalSizeBitmap.size.width, originalSizeBitmap.size.height,
                userSelectOriginalSizeBitmap, matrix, rateOfEditWidth);
    }

    public static Bitmap createPageBitmapWithoutModules(int pageIndex) {
        MDImage templateImage = SelectImageUtils.sTemplateImage.get(pageIndex);

        List<PhotoTemplateAttachFrame> photoTemplateAttachFrameList = templateImage.getPhotoTemplateAttachFrameList();

//        Bitmap backgroundBitmap = GlideUtil.loadImageAsBitmap(templateImage);

        OriginalSizeBitmap originalSizeBitmap = GlideUtil.loadImageAsOriginalSizeBitmap(templateImage);
        Bitmap backgroundBitmap = originalSizeBitmap.bitmap;

        Paint paint = new Paint();
        paint.setAntiAlias(true);
        paint.setDither(true);

        Bitmap bitmap = Bitmap.createBitmap(originalSizeBitmap.size.width, originalSizeBitmap.size.height, Bitmap.Config.ARGB_4444);

        Canvas canvas = new Canvas(bitmap);

        float rateOfEditWidth = TemplateUtils.getRateOfEditAreaOnAndroid(originalSizeBitmap.size);

        // 用户编辑模块绘制
        MDImage userSelectImage = SelectImageUtils.sAlreadySelectImage.get(pageIndex);

        OriginalSizeBitmap userSelectOriginalSizeBitmap = GlideUtil.loadImageAsOriginalSizeBitmap(userSelectImage);

        Matrix matrix = TemplateUtils.getMatrixByString(templateImage.getWorkPhoto().getMatrix());

        Bitmap compositeBitmap = compositePicture(pageIndex, 0, originalSizeBitmap.size.width, originalSizeBitmap.size.height,
                null, userSelectOriginalSizeBitmap, matrix, rateOfEditWidth);

        // 如果用户没有设置图片, 则不画
        if (!(templateImage.getPhotoID() == 0 && templateImage.getPhotoSID() == 0 && StringUtil.isEmpty(templateImage.getImageLocalPath()))) {
            canvas.drawBitmap(compositeBitmap, 0, 0, paint);
        }

        // 背景图绘制
//        canvas.drawBitmap(backgroundBitmap, 0, 0, paint);
        RectF rectF = new RectF(0, 0, originalSizeBitmap.size.width, originalSizeBitmap.size.height);
        canvas.drawBitmap(backgroundBitmap, null, rectF, paint);

        return bitmap;
    }

    public static void createPageBitmapHasModulesOnlyParam(int pageIndex) {
        MDImage mdImage = SelectImageUtils.sTemplateImage.get(pageIndex);

        OriginalSizeBitmap originalSizeBitmap = GlideUtil.loadImageAsOriginalSizeBitmap(mdImage);

        List<PhotoTemplateAttachFrame> photoTemplateAttachFrameList = mdImage.getPhotoTemplateAttachFrameList();
        float rateOfEditArea = TemplateUtils.getRateOfEditAreaOnAndroid(originalSizeBitmap.size);

        // 各个模块绘制
        createModuleOnlyParam(pageIndex, photoTemplateAttachFrameList, 1.0f, 1.0f, rateOfEditArea);
    }

    public static Bitmap createPageBitmapHasModules(int pageIndex) {
        MDImage mdImage = SelectImageUtils.sTemplateImage.get(pageIndex);

        List<PhotoTemplateAttachFrame> photoTemplateAttachFrameList = mdImage.getPhotoTemplateAttachFrameList();

//        Bitmap backgroundBitmap = GlideUtil.loadImageAsBitmap(mdImage);
        OriginalSizeBitmap originalSizeBitmap = GlideUtil.loadImageAsOriginalSizeBitmap(mdImage);
        Bitmap backgroundBitmap = originalSizeBitmap.bitmap;

        Paint paint = new Paint();
        paint.setAntiAlias(true);
        paint.setDither(true);

        Bitmap bitmap = Bitmap.createBitmap(originalSizeBitmap.size.width, originalSizeBitmap.size.height, Bitmap.Config.ARGB_4444);

        Canvas canvas = new Canvas(bitmap);

        float rateOfEditArea = TemplateUtils.getRateOfEditAreaOnAndroid(originalSizeBitmap.size);

        // 各个模块绘制
        createMould(pageIndex, canvas, paint, photoTemplateAttachFrameList, 1.0f, 1.0f, rateOfEditArea);

        // 背景图绘制
//        canvas.drawBitmap(backgroundBitmap, 0, 0, paint);
        RectF rectF = new RectF(0, 0, originalSizeBitmap.size.width, originalSizeBitmap.size.height);
        canvas.drawBitmap(backgroundBitmap, null, rectF, paint);

        return bitmap;
    }

    private static void createMould(int pageIndex, final Canvas canvas, final Paint paint, List<PhotoTemplateAttachFrame> moulds,
                                    float rate, float r, float rateOfEditWidth) {
        for (int moduleIndex = 0; moduleIndex < moulds.size(); moduleIndex++) {
            final PhotoTemplateAttachFrame photoTemplateAttachFrame = moulds.get(moduleIndex);
            MDImage mdImage = SelectImageUtils.getMdImageByPageIndexAndModuleIndex(pageIndex, moduleIndex);

            OriginalSizeBitmap originalSizeBitmap = GlideUtil.loadImageAsOriginalSizeBitmap(mdImage);
            float dx = photoTemplateAttachFrame.getPositionX();
            float dy = photoTemplateAttachFrame.getPositionY();
            float width = photoTemplateAttachFrame.getWidth();
            float height = photoTemplateAttachFrame.getHeight();

            Matrix matrix = TemplateUtils.getMatrixByString(photoTemplateAttachFrame.getMatrix());

            Bitmap compositeBitmap = compositePicture(pageIndex, moduleIndex, width, height, null, originalSizeBitmap,
                    matrix, rateOfEditWidth);

            // 如果用户没有设置图片, 则不画
            if (!(mdImage.getPhotoID() == 0 && mdImage.getPhotoSID() == 0 && StringUtil.isEmpty(mdImage.getImageLocalPath()))) {
                canvas.drawBitmap(compositeBitmap, dx, dy, paint);
            }
        }
    }

    private static void createModuleOnlyParam(int pageIndex, List<PhotoTemplateAttachFrame> moulds, float rate,
                                              float r, float rateOfEditWidth) {
        for (int moduleIndex = 0; moduleIndex < moulds.size(); moduleIndex++) {
            final PhotoTemplateAttachFrame photoTemplateAttachFrame = moulds.get(moduleIndex);
            MDImage mdImage = SelectImageUtils.getMdImageByPageIndexAndModuleIndex(pageIndex, moduleIndex);

            OriginalSizeBitmap originalSizeBitmap = GlideUtil.loadImageAsOriginalSizeBitmap(mdImage);
            float dx = photoTemplateAttachFrame.getPositionX();
            float dy = photoTemplateAttachFrame.getPositionY();
            float width = photoTemplateAttachFrame.getWidth();
            float height = photoTemplateAttachFrame.getHeight();

            Matrix matrix = TemplateUtils.getMatrixByString(photoTemplateAttachFrame.getMatrix());

            compositePictureOnlyParam(pageIndex, moduleIndex, width, height, originalSizeBitmap, matrix, rateOfEditWidth);
        }
    }

    private static Bitmap compositePicture(int pageIndex, int moduleIndex, float moduleWidth, float moduleHeight,
                                           Bitmap mouldBmp, OriginalSizeBitmap originalSizeBitmap, Matrix matrix,
                                           float rateOfEditWidth) {
        Bitmap outputBitmap;
        outputBitmap = Bitmap.createBitmap((int) moduleWidth, (int) moduleHeight, Config.ARGB_4444);
        outputBitmap.eraseColor(Color.parseColor("#00000000"));

        int userSelectBitmapOriginalWidth = originalSizeBitmap.size.width;
        int userSelectBitmapOriginalHeight = originalSizeBitmap.size.height;

        int photoBmpWidth = originalSizeBitmap.bitmap.getWidth();
        int photoBmpHeight = originalSizeBitmap.bitmap.getHeight();

        float userSelectPhotoSetScale = moduleWidth / ((float) photoBmpWidth) > moduleHeight / ((float) photoBmpHeight)
                ? moduleWidth / ((float) photoBmpWidth) : moduleHeight / ((float) photoBmpHeight);

        if (userSelectPhotoSetScale != 0.0f) {
//            Bitmap scalePhotoBmp;
            matrix.preScale(userSelectPhotoSetScale, userSelectPhotoSetScale);
//            Matrix photoMatrix = new Matrix();
//            photoMatrix.setScale(scale, scale);
//            try {
//                scalePhotoBmp = Bitmap.createBitmap(photoBmp, 0, 0, photoBmpWidth, photoBmpHeight, photoMatrix, true);
//            } catch (OutOfMemoryError e) {
//                try {
//                    scalePhotoBmp = Bitmap.createBitmap(photoBmp, 0, 0, photoBmpWidth, photoBmpHeight, photoMatrix, true);
//                } catch (OutOfMemoryError e2) {
//                    scalePhotoBmp = Bitmap.createBitmap(photoBmp, 0, 0, photoBmpWidth, photoBmpHeight, photoMatrix, true);
//                }
//            }
//            photoBmp = scalePhotoBmp;

//            float photoBitmapWidth = (float) photoBmp.getWidth();
//            float photoBitmapHeight = (float) photoBmp.getHeight();
//            float dx = (width - photoBitmapWidth) / 2.0f;
//            float dy = (height - photoBitmapHeight) / 2.0f;
//            matrix.preTranslate(dx, dy);
        }

        float[] values = new float[9];
        matrix.getValues(values);
        values[Matrix.MTRANS_X] = values[Matrix.MTRANS_X] / (rateOfEditWidth);
        values[Matrix.MTRANS_Y] = values[Matrix.MTRANS_Y] / (rateOfEditWidth);
        matrix.setValues(values);

        Canvas canvas = new Canvas(outputBitmap);
        Paint paint = new Paint();
        paint.setAntiAlias(true);
        paint.setDither(true);

        MDImage templateImage = SelectImageUtils.sTemplateImage.get(pageIndex);

        int brightness = templateImage.getWorkPhoto().getBrightLevel();

        // 亮度调整
        ColorMatrix cMatrix = new ColorMatrix();
        cMatrix.set(new float[]{
                1, 0, 0, 0, brightness,
                0, 1, 0, 0, brightness,
                0, 0, 1, 0, brightness,
                0, 0, 0, 1, 0
        });
        paint.setColorFilter(new ColorMatrixColorFilter(cMatrix));

//        paint.setColor(Color.parseColor("#BAB399"));
//        canvas.drawBitmap(mouldBmp, new Rect(0, 0, mouldBmp.getWidth(), mouldBmp.getHeight()), new RectF(0.0f, 0.0f, w, h), paint);
//        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
//        canvas.drawColor(-1, PorterDuff.Mode.SRC_IN);
        canvas.drawBitmap(originalSizeBitmap.bitmap, matrix, paint);
//        canvas.drawBitmap(photoBmp, 0, 0, paint);

        // 因为服务器是用原图来合成的,所以这里要将压缩后的图片宽高和原图的宽高对比,相乘到matrix上要返回给服务器
        float userSelectPhotoOriginalScale = photoBmpWidth / ((float) userSelectBitmapOriginalWidth) > photoBmpHeight / ((float) userSelectBitmapOriginalHeight)
                ? photoBmpWidth / ((float) userSelectBitmapOriginalWidth) : photoBmpHeight / ((float) userSelectBitmapOriginalHeight);

        if (userSelectPhotoOriginalScale != 0.0f) {
            matrix.preScale(userSelectPhotoOriginalScale, userSelectPhotoOriginalScale);
        }

        // 保存编辑信息,用于传到服务器
        float tx = values[Matrix.MTRANS_X];
        float ty = values[Matrix.MTRANS_Y];
        float scalex = values[Matrix.MSCALE_X];

        // calculate the degree of rotation
        float rAngle = -Math.round(Math.atan2(values[Matrix.MSKEW_X], values[Matrix.MSCALE_X]) * (180 / Math.PI));

//        KLog.e("tx : " + tx);
//        KLog.e("ty : " + ty);
//        KLog.e("rScale : " + scalex);
//        KLog.e("rAngle : " + rAngle);

        WorkPhotoEdit workPhotoEdit = SelectImageUtils.getMdImageByPageIndexAndModuleIndex(pageIndex, moduleIndex).getWorkPhotoEdit();

        List<PhotoTemplateAttachFrame> photoTemplateAttachFrameList = templateImage.getPhotoTemplateAttachFrameList();
        if (photoTemplateAttachFrameList != null && photoTemplateAttachFrameList.size() > moduleIndex) {
            PhotoTemplateAttachFrame photoTemplateAttachFrame = photoTemplateAttachFrameList.get(moduleIndex);

            workPhotoEdit.setFrameID(photoTemplateAttachFrame.getFrameID());
            workPhotoEdit.setPositionX(photoTemplateAttachFrame.getPositionX());
            workPhotoEdit.setPositionY(photoTemplateAttachFrame.getPositionY());
        }

//        MDImage templateImage = SelectImageUtils.sTemplateImage.get(pageIndex);
//        List<PhotoTemplateAttachFrame> photoTemplateAttachFrameList = templateImage.getPhotoTemplateAttachFrameList();
//        PhotoTemplateAttachFrame photoTemplateAttachFrame = photoTemplateAttachFrameList.get(moduleIndex);

//        workPhotoEdit.setPositionX((int) tx + photoTemplateAttachFrame.getPositionX());
//        workPhotoEdit.setPositionY((int) ty + photoTemplateAttachFrame.getPositionY());
//        workPhotoEdit.setPositionX((int) tx);
//        workPhotoEdit.setPositionY((int) ty);
        workPhotoEdit.setRotate(rAngle);
        workPhotoEdit.setZoomSize(scalex);

        workPhotoEdit.setMatrix(TemplateUtils.getServerMatrixString(matrix));

        return outputBitmap;
    }

    //（不生成图片，只处理参数）
    private static void compositePictureOnlyParam(int pageIndex, int moduleIndex, float moduleWidth, float moduleHeight,
                                                  OriginalSizeBitmap originalSizeBitmap, Matrix matrix, float rateOfEditWidth) {

        int userSelectBitmapOriginalWidth = originalSizeBitmap.size.width;
        int userSelectBitmapOriginalHeight = originalSizeBitmap.size.height;

        int photoBmpWidth = originalSizeBitmap.bitmap.getWidth();
        int photoBmpHeight = originalSizeBitmap.bitmap.getHeight();

        float userSelectPhotoSetScale = moduleWidth / ((float) photoBmpWidth) > moduleHeight / ((float) photoBmpHeight)
                ? moduleWidth / ((float) photoBmpWidth) : moduleHeight / ((float) photoBmpHeight);

        if (userSelectPhotoSetScale != 0.0f) {
            matrix.preScale(userSelectPhotoSetScale, userSelectPhotoSetScale);
        }

        float[] values = new float[9];
        matrix.getValues(values);
        values[Matrix.MTRANS_X] = values[Matrix.MTRANS_X] / (rateOfEditWidth);
        values[Matrix.MTRANS_Y] = values[Matrix.MTRANS_Y] / (rateOfEditWidth);
        matrix.setValues(values);

        MDImage templateImage = SelectImageUtils.sTemplateImage.get(pageIndex);

        int brightness = templateImage.getWorkPhoto().getBrightLevel();

        // 亮度调整
        ColorMatrix cMatrix = new ColorMatrix();
        cMatrix.set(new float[]{
                1, 0, 0, 0, brightness,
                0, 1, 0, 0, brightness,
                0, 0, 1, 0, brightness,
                0, 0, 0, 1, 0
        });

        // 因为服务器是用原图来合成的,所以这里要将压缩后的图片宽高和原图的宽高对比,相乘到matrix上要返回给服务器
        float userSelectPhotoOriginalScale = photoBmpWidth / ((float) userSelectBitmapOriginalWidth) > photoBmpHeight / ((float) userSelectBitmapOriginalHeight)
                ? photoBmpWidth / ((float) userSelectBitmapOriginalWidth) : photoBmpHeight / ((float) userSelectBitmapOriginalHeight);

        if (userSelectPhotoOriginalScale != 0.0f) {
            matrix.preScale(userSelectPhotoOriginalScale, userSelectPhotoOriginalScale);
        }

        // 保存编辑信息,用于传到服务器
        float scaleX = values[Matrix.MSCALE_X];

        float rAngle = -Math.round(Math.atan2(values[Matrix.MSKEW_X], values[Matrix.MSCALE_X]) * (180 / Math.PI));

        WorkPhotoEdit workPhotoEdit = SelectImageUtils.getMdImageByPageIndexAndModuleIndex(pageIndex, moduleIndex).getWorkPhotoEdit();

        List<PhotoTemplateAttachFrame> photoTemplateAttachFrameList = templateImage.getPhotoTemplateAttachFrameList();
        if (photoTemplateAttachFrameList != null && photoTemplateAttachFrameList.size() > moduleIndex) {
            PhotoTemplateAttachFrame photoTemplateAttachFrame = photoTemplateAttachFrameList.get(moduleIndex);

            workPhotoEdit.setFrameID(photoTemplateAttachFrame.getFrameID());
            workPhotoEdit.setPositionX(photoTemplateAttachFrame.getPositionX());
            workPhotoEdit.setPositionY(photoTemplateAttachFrame.getPositionY());
        }

        workPhotoEdit.setRotate(rAngle);
        workPhotoEdit.setZoomSize(scaleX);

        workPhotoEdit.setMatrix(TemplateUtils.getServerMatrixString(matrix));
    }

    private static Bitmap compositePicture(int pageIndex, int moduleIndex, float width, float height,
                                           Bitmap userSelectPhotoBmp, Matrix matrix, float rateOfEditWidth) {
        Bitmap outputBitmap;
        outputBitmap = Bitmap.createBitmap((int) width, (int) height, Config.ARGB_4444);
        outputBitmap.eraseColor(Color.parseColor("#00000000"));

        int photoBmpWidth = userSelectPhotoBmp.getWidth();
        int photoBmpHeight = userSelectPhotoBmp.getHeight();

        float scale = width / ((float) photoBmpWidth) > height / ((float) photoBmpHeight)
                ? width / ((float) photoBmpWidth) : height / ((float) photoBmpHeight);

        if (scale != 0.0f) {
            matrix.preScale(scale, scale);
        }

        float[] values = new float[9];
        matrix.getValues(values);
        values[Matrix.MTRANS_X] = values[Matrix.MTRANS_X] / (rateOfEditWidth);
        values[Matrix.MTRANS_Y] = values[Matrix.MTRANS_Y] / (rateOfEditWidth);
        matrix.setValues(values);

        Canvas canvas = new Canvas(outputBitmap);
        Paint paint = new Paint();
        paint.setAntiAlias(true);
        paint.setDither(true);
        canvas.drawBitmap(userSelectPhotoBmp, matrix, paint);

        return outputBitmap;
    }

    private static String saveBitmapToSDCard(Bitmap saveBitmap, int index) {
        File saveFolder = new File(SAVE_PATH);
        if (!saveFolder.exists()) {
            saveFolder.mkdirs();
        } else {
            // 删除之前所有的文件
//            for (File file : saveFoler.listFiles()) {
//                if (!file.isDirectory()) {
//                    file.delete();
//                }
//            }
        }
        String fileName = "page_" + index + ".jpg";
        String filePath = SAVE_PATH + File.separator + fileName;

        File file = new File(filePath);

        try {
            FileOutputStream out = new FileOutputStream(file);
            saveBitmap.compress(Bitmap.CompressFormat.JPEG, 100, out);
            out.flush();
            out.close();

        } catch (IOException e) {
            e.printStackTrace();
        }
        return filePath;
    }

}
