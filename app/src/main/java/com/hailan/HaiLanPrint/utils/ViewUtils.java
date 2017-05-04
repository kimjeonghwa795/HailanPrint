package com.hailan.HaiLanPrint.utils;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.text.InputType;
import android.util.DisplayMetrics;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.Toast;

import com.hailan.HaiLanPrint.R;
import com.hailan.HaiLanPrint.application.MDGroundApplication;
import com.socks.library.KLog;

/**
 * Created by shadow on 15/11/7.
 */
public class ViewUtils {

    private static ProgressDialog sProgressDialog;

    public static int screenWidth() {
        Context context = MDGroundApplication.sInstance;
        if (context == null) {
            return 0;
        }
        DisplayMetrics dm = context.getResources().getDisplayMetrics();
        Configuration configuration = context.getResources().getConfiguration();
        return configuration.orientation == Configuration.ORIENTATION_PORTRAIT ? dm.widthPixels : dm.heightPixels;
    }

    public static int screenHeight() {
        Context context = MDGroundApplication.sInstance;
        if (context == null) {
            return 0;
        }
        DisplayMetrics dm = context.getResources().getDisplayMetrics();
        Configuration configuration = context.getResources().getConfiguration();
        return configuration.orientation == Configuration.ORIENTATION_PORTRAIT ? dm.heightPixels : dm.widthPixels;
    }

    public static int screenDensity() {
        Context context = MDGroundApplication.sInstance;
        if (context == null) {
            return 0;
        }
        DisplayMetrics dm = context.getResources().getDisplayMetrics();
        return (int) dm.density;
    }

    /**
     * 根据手机的分辨率从 dp 的单位 转成为 px(像素)
     */
    public static int dp2px(float dpValue) {
        final float scale = MDGroundApplication.sInstance.getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }

    /**
     * 根据手机的分辨率从 dp 的单位 转成为 px(像素)
     */
    public static float dp2pxf(float dpValue) {
        final float scale = MDGroundApplication.sInstance.getResources().getDisplayMetrics().density;
        return (dpValue * scale + 0.5f);
    }

    /**
     * 根据手机的分辨率从 px(像素) 的单位 转成为 dp
     */
    public static int px2dp(float pxValue) {
        final float scale = MDGroundApplication.sInstance.getResources().getDisplayMetrics().density;
        return (int) (pxValue / scale + 0.5f);
    }


    public static void toast(String str) {
        Toast.makeText(MDGroundApplication.sInstance, str, Toast.LENGTH_SHORT).show();
    }

    public static void toast(int resId) {
        Toast.makeText(MDGroundApplication.sInstance, resId, Toast.LENGTH_SHORT).show();
    }

    public static void loading(Context context) {
        try {
            dismiss();
            String message = context.getString(R.string.loading);
            sProgressDialog = ProgressDialog.show(context, null, message);
        } catch (Throwable e) {
            e.printStackTrace();
        }
    }

    public static void dismiss() {
        try {
            if (sProgressDialog != null && sProgressDialog.isShowing()) {
                sProgressDialog.dismiss();
            }
        } catch (Throwable e) {
            e.printStackTrace();
        }
    }

    public static void setLoadingMessage(String text) {
        try {
            if (sProgressDialog != null) {
                sProgressDialog.setMessage(text);
            }
        } catch (Throwable e) {
            e.printStackTrace();
        }
    }

    public static void closeKeyboard(Activity context) {
        InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(context.getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);

    }

    public static void openKeyboard(Activity context, EditText editText) {
        InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
        editText.requestFocus();
        imm.showSoftInput(editText, 0);
    }

    public static String getString(int resId) {
        return MDGroundApplication.sInstance.getString(resId);
    }

    // 根据路径获得图片, 如果超过1024 * 1024就压缩
    public static Bitmap getSmallBitmap(String filePath) {
        final BitmapFactory.Options options = new BitmapFactory.Options();

        options.inJustDecodeBounds = true;

        BitmapFactory.decodeFile(filePath, options);

        KLog.e("解析图片的宽 : " + options.outWidth);
        KLog.e("解析图片的高 : " + options.outHeight);

        // Calculate inSampleSize
        options.inSampleSize = calculateInSampleSize(options, 2048, 2048);

        // Decode bitmap with inSampleSize set
        options.inJustDecodeBounds = false;

        return BitmapFactory.decodeFile(filePath, options);
    }

    //计算图片的缩放值
    public static int calculateInSampleSize(BitmapFactory.Options options, int reqWidth, int reqHeight) {
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;

        if (height > reqHeight || width > reqWidth) {
            float heightRatio = (float) height / (float) reqHeight;
            float widthRatio = (float) width / (float) reqWidth;
            float ratio = heightRatio > widthRatio ? heightRatio : widthRatio;
            inSampleSize = Math.round(ratio);
        }
        return inSampleSize;
    }

    public static void copy(String str) {
        ClipboardManager clipboardManager = (ClipboardManager) MDGroundApplication.sInstance.getSystemService(Context.CLIPBOARD_SERVICE);
        clipboardManager.setText(str);
        ViewUtils.toast(str);
    }

    public static void isShowPassword(boolean isShow, EditText editText) {
        if (isShow) {
            editText.setInputType(InputType.TYPE_CLASS_TEXT
                    | InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
        } else {
            editText.setInputType(InputType.TYPE_CLASS_TEXT
                    | InputType.TYPE_TEXT_VARIATION_PASSWORD);
        }
        editText.setSelection(editText.length());
    }

    public static Bitmap drawableToBitmap(Drawable drawable) {
        if (drawable instanceof BitmapDrawable) {
            return ((BitmapDrawable) drawable).getBitmap();
        }

        int width = drawable.getIntrinsicWidth();
        width = width > 0 ? width : 1;
        int height = drawable.getIntrinsicHeight();
        height = height > 0 ? height : 1;

        Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
        drawable.draw(canvas);

        return bitmap;
    }

    public static AlertDialog createAlertDialog(Context context, String message,
                                                DialogInterface.OnClickListener cancelListener,
                                                DialogInterface.OnClickListener confirmListener) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle(R.string.tips);
        builder.setMessage(message);

        if (cancelListener != null) {
            builder.setNegativeButton(R.string.cancel, cancelListener);
        }

        if (confirmListener != null) {
            builder.setPositiveButton(R.string.confirm, confirmListener);
        }

        return builder.create();
    }

    public static AlertDialog createAlertDialog2(Context context, String message,
                                                 DialogInterface.OnClickListener cancelListener,
                                                 DialogInterface.OnClickListener confirmListener) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle(R.string.tips);
        builder.setMessage(message);

        if (cancelListener != null) {
            builder.setNegativeButton(R.string.no, cancelListener);
        }

        if (confirmListener != null) {
            builder.setPositiveButton(R.string.yes, confirmListener);
        }

        return builder.create();
    }
}
