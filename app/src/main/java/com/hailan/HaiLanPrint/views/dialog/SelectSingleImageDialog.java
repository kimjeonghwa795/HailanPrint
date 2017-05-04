package com.hailan.HaiLanPrint.views.dialog;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;

import com.hailan.HaiLanPrint.R;
import com.hailan.HaiLanPrint.constants.Constants;
import com.hailan.HaiLanPrint.databinding.DialogSelectSingleImageBinding;

import java.io.File;

import me.nereo.multi_image_selector.MultiImageSelector;

/**
 * Created by yoghourt on 5/25/16.
 */

public class SelectSingleImageDialog extends Dialog {
    public static final int PHOTO_REQUEST_GALLERY = 1;// 相册
    public static final int PHOTO_REQUEST_CAREMA = 2;// 拍照
    public static final int PHOTO_REQUEST_CUT = 3;// 剪切
    private DialogSelectSingleImageBinding mDataBinding;
    private Activity mActivity;
    public static String sCaptureImageURL;

    public SelectSingleImageDialog(Activity activity) {
        super(activity, R.style.customDialogStyle);
        mActivity = activity;
    }

    public SelectSingleImageDialog(Activity activity, int themeResId) {
        super(activity, themeResId);
        mActivity = activity;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mDataBinding = DataBindingUtil.inflate(LayoutInflater.from(getContext()), R.layout.dialog_select_single_image, null, false);

        setContentView(mDataBinding.getRoot());

        Window window = getWindow();
        window.getDecorView().setPadding(0, 0, 0, 0);
        window.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT); // 填充满屏幕的宽度
        window.setWindowAnimations(R.style.action_sheet_animation); // 添加动画
        WindowManager.LayoutParams wlp = window.getAttributes();
        wlp.gravity = Gravity.BOTTOM; // 使dialog在底部显示
        window.setAttributes(wlp);

        mDataBinding.btnTakePicture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                camera();
            }
        });

        mDataBinding.btnLocalImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                gallery();
            }
        });

        mDataBinding.btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });
    }

    /*
     * 从相册获取
	 */
    private void gallery() {
        dismiss();
//        Intent intent = new Intent();
//        intent.setType("image/*");
//        intent.setAction(Intent.ACTION_GET_CONTENT);
//        mActivity.startActivityForResult(intent, PHOTO_REQUEST_GALLERY);

        MultiImageSelector.create()
                .count(1)
                .showCamera(false)
                .single()
                .start(mActivity, PHOTO_REQUEST_GALLERY);
    }

    /*
     * 从相机获取
	 */
    private void camera() {
        dismiss();
        if (isSdCardMounted()) {
            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            String folderString = Environment.getExternalStorageDirectory().toString() + Constants.PHOTO_FILE;
            File folderFile = new File(folderString);
            if (!folderFile.exists()) {
                folderFile.mkdir();
            }
            String imageName = System.currentTimeMillis() + ".jpg";
            File file = new File(folderFile, imageName);
            sCaptureImageURL = file.getAbsolutePath();
            Uri outputFileUri = Uri.fromFile(file);
            intent.putExtra(MediaStore.EXTRA_OUTPUT, outputFileUri);
            mActivity.startActivityForResult(intent, PHOTO_REQUEST_CAREMA);
        } else {
            Toast.makeText(mActivity, "内存卡不存在", Toast.LENGTH_LONG).show();
        }
    }


    private boolean isSdCardMounted() {
        String status = Environment.getExternalStorageState();

        if (status.equals(Environment.MEDIA_MOUNTED))
            return true;
        return false;
    }
}
