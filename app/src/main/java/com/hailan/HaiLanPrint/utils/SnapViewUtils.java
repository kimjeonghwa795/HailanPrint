package com.hailan.HaiLanPrint.utils;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.os.Environment;
import android.view.View;
import android.widget.ScrollView;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

public class SnapViewUtils {

    public static String snapViewReturnLocalPath(View snapView) {
        if (snapView instanceof ScrollView) {
            snapView = ((ScrollView) snapView).getChildAt(0);
        }
        ByteArrayOutputStream baos = null;
        Bitmap bitmap = null;

        try {
            bitmap = Bitmap.createBitmap(snapView.getWidth(),
                    snapView.getHeight(), Bitmap.Config.RGB_565);
            snapView.draw(new Canvas(bitmap));

            String filename = "smartKitchenShare.jpg";

            File sd = Environment.getExternalStorageDirectory();
            File dest = new File(sd, filename);
            try {
                FileOutputStream out = new FileOutputStream(dest);
                bitmap.compress(Bitmap.CompressFormat.PNG, 45, out);
                out.flush();
                out.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
            return dest.getPath();
//			baos = new ByteArrayOutputStream();
//			bitmap.compress(Bitmap.CompressFormat.JPEG, 70, baos);
        } finally {
            try {
                /** no need to close, actually do nothing */
                if (null != baos)
                    baos.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            if (null != bitmap && !bitmap.isRecycled()) {
                bitmap.recycle();
                bitmap = null;
            }
        }
    }

    public static Bitmap snapViewReturnBitmap(View snapView) {
        if (snapView instanceof ScrollView) {
            snapView = ((ScrollView) snapView).getChildAt(0);
        }
        ByteArrayOutputStream baos = null;
        Bitmap bitmap = null;

        bitmap = Bitmap.createBitmap(snapView.getWidth(),
                snapView.getHeight(), Bitmap.Config.RGB_565);
        snapView.draw(new Canvas(bitmap));
        return bitmap;
    }

    public static Bitmap getResizedBitmap(Bitmap bm, int newWidth, int newHeight) {
        int width = bm.getWidth();
        int height = bm.getHeight();

        float ratio = ((float) width) / height;

        float scale = 0;

        if (newWidth > newHeight) {
            scale = ((float) newHeight) / height;
        } else {
            scale = ((float) newWidth) / width;
        }

        int wantedWidth = (int) (newWidth * ratio);
        int wantedHeight = (int) (newHeight * scale);

        Bitmap resizedBitmap = Bitmap.createScaledBitmap(bm, wantedWidth, newHeight, true);
        return resizedBitmap;
    }

    public static Bitmap resizeImageForImageView(Bitmap bitmap, int desiredHeight) {
        Bitmap resizedBitmap = null;
        int originalWidth = bitmap.getWidth();
        int originalHeight = bitmap.getHeight();
        int newWidth = -1;
        int newHeight = -1;
        float multFactor = -1.0F;
        if(originalHeight > originalWidth) {
            newHeight = desiredHeight ;
            multFactor = (float) originalWidth/(float) originalHeight;
            newWidth = (int) (newHeight*multFactor);
        } else if(originalWidth > originalHeight) {
            newWidth = desiredHeight ;
            multFactor = (float) originalHeight/ (float)originalWidth;
            newHeight = (int) (newWidth*multFactor);
        } else if(originalHeight == originalWidth) {
            newHeight = desiredHeight ;
            newWidth = desiredHeight ;
        }
        resizedBitmap = Bitmap.createScaledBitmap(bitmap, newWidth, newHeight, false);
        return resizedBitmap;
    }
}
