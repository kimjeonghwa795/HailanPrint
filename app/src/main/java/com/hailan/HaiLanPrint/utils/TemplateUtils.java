package com.hailan.HaiLanPrint.utils;

import android.graphics.Matrix;
import android.graphics.Point;

import com.hailan.HaiLanPrint.application.MDGroundApplication;
import com.hailan.HaiLanPrint.enumobject.ProductType;
import com.hailan.HaiLanPrint.models.Size;
import com.socks.library.KLog;

/**
 * Created by yoghourt on 8/1/16.
 */
public class TemplateUtils {

    /**
     * 是否有定位块
     *
     * @return
     */
    public static boolean isTemplateHasModules() {
        boolean hasModules = true;
        ProductType chooseProductType = MDGroundApplication.sInstance.getChoosedProductType();

        if (chooseProductType == ProductType.PrintPhoto
                || chooseProductType == ProductType.PhoneShell) {
            hasModules = false;
        }
        return hasModules;
    }

    /**
     * 返回bitmap在android上的实际显示宽高
     *
     * @param size 模版的原始宽高
     * @return
     */
    public static Point getEditAreaSizeOnAndroid(Size size) {
        float bitmapWidth = size.width;
        float bitmapHeight = size.height;

        float windowWidth = ((float) ViewUtils.screenWidth());
        float halfWindowHeight = ((float) ViewUtils.screenHeight() / 2.0f);  // 默认最大高度是屏幕的一半

        float widthScale = windowWidth / bitmapWidth;
        float heightScale = halfWindowHeight / bitmapHeight;

        Point point = new Point();
        if (widthScale < heightScale) {
            point.x = (int) windowWidth;
            point.y = (int) (widthScale * bitmapHeight);
        } else {
            point.x = (int) (heightScale * bitmapWidth);
            point.y = (int) halfWindowHeight;
        }

        return point;
    }

    /**
     * 返回各个定位块在android上的实际缩放比例
     *
     * @param size 模版的原始宽高
     * @return
     */
    public static float getRateOfEditAreaOnAndroid(Size size) {
        float bitmapWidth = size.width;
        float bitmapHeight = size.height;

        float windowWidth = ((float) ViewUtils.screenWidth());
        float halfWindowHeight = ((float) ViewUtils.screenHeight() / 2.0f);  // 默认最大高度是屏幕的一半

        float widthScale = windowWidth / bitmapWidth;
        float heightScale = halfWindowHeight / bitmapHeight;

        if (widthScale < heightScale) {
            return widthScale;
        } else {
            return heightScale;
        }
    }

    // 将String cast成 Matrix
    public static Matrix getMatrixByString(String matrixString) {
        Matrix matrix = new Matrix();
        if (!StringUtil.isEmpty(matrixString)) {
            float[] values = new float[9];
            matrix.getValues(values);
            StringBuffer buff = new StringBuffer();
            int i = 0;
            for (char c : matrixString.toCharArray()) {
                if (c != '[') {
                    if (c != ',') {
                        if (c == ']') {
                            break;
                        }
                        buff.append(c);
                    } else {
                        values[i] = Float.parseFloat(buff.toString());
                        buff = new StringBuffer();
                        i++;
                    }
                }
            }
//            values[2] = values[2] * this.scaleWin;
//            values[5] = values[5] * this.scaleWin;
            matrix.setValues(values);
        }

        return matrix;
    }

    // 将String cast成 Matrix
    public static Matrix getMatrixByString(String matrixString, float rate) {
        Matrix matrix = new Matrix();
        if (!StringUtil.isEmpty(matrixString)) {
            float[] values = new float[9];
            matrix.getValues(values);
            StringBuffer buff = new StringBuffer();
            int i = 0;
            for (char c : matrixString.toCharArray()) {
                if (c != '[') {
                    if (c != ',') {
                        if (c == ']') {
                            break;
                        }
                        buff.append(c);
                    } else {
                        if (i == 2 || i == 5) {//平移变换乘以缩放比
                            values[i] = Float.parseFloat(buff.toString()) * rate;
                        } else {
                            values[i] = Float.parseFloat(buff.toString());
                        }
                        buff = new StringBuffer();
                        i++;
                    }
                }
            }
//            values[2] = values[2] * this.scaleWin;
//            values[5] = values[5] * this.scaleWin;
            matrix.setValues(values);
        }

        return matrix;
    }

    public static String getStringByMatrix(Matrix matrix) {
        float[] values = new float[9];
        matrix.getValues(values);

        String matrixString = "[" + values[0] + "," + values[1] + "," + values[2] + "," + values[3] + ","
                + values[4] + "," + values[5] + "," + values[6] + "," + values[7] + "," + values[8] + "]";

        KLog.e("getStringByMatrix", matrixString);
        return matrixString;
    }

    public static String getServerMatrixString(Matrix matrix) {
        float[] values = new float[9];
        matrix.getValues(values);

        String matrixString = values[Matrix.MSCALE_X] + "," + values[Matrix.MSKEW_Y] + "," + values[Matrix.MPERSP_0] + ","
                + values[Matrix.MSKEW_X] + "," + values[Matrix.MSCALE_Y] + "," + values[Matrix.MPERSP_1] + ","
                + values[Matrix.MTRANS_X] + "," + values[Matrix.MTRANS_Y] + "," + values[Matrix.MPERSP_2];

        KLog.e("getServerMatrixString", matrixString);
        return matrixString;
    }

    public static String convertServerMatrixToAndroidMatrixString(String serverMatrixString) {
        float[] values = new float[9];

        String[] strings = serverMatrixString.split(",");

        values[Matrix.MSCALE_X] = Float.valueOf(strings[0]);
        values[Matrix.MSKEW_Y] = Float.valueOf(strings[1]);
        values[Matrix.MPERSP_0] = Float.valueOf(strings[2]);

        values[Matrix.MSKEW_X] = Float.valueOf(strings[3]);
        values[Matrix.MSCALE_Y] = Float.valueOf(strings[4]);
        values[Matrix.MPERSP_1] = Float.valueOf(strings[5]);

        values[Matrix.MTRANS_X] = Float.valueOf(strings[6]);
        values[Matrix.MTRANS_Y] = Float.valueOf(strings[7]);
        values[Matrix.MPERSP_2] = Float.valueOf(strings[8]);

        String androidMatrixString = "[" + values[0] + "," + values[1] + "," + values[2] + "," + values[3] + ","
                + values[4] + "," + values[5] + "," + values[6] + "," + values[7] + "," + values[8] + "]";

        return androidMatrixString;
    }
}
