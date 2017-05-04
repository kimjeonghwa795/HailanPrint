package com.hailan.HaiLanPrint.views.dialog;

import android.app.Dialog;
import android.content.Context;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import com.hailan.HaiLanPrint.R;
import com.hailan.HaiLanPrint.databinding.DialogShareBinding;
import com.hailan.HaiLanPrint.greendao.Location;
import com.hailan.HaiLanPrint.utils.ShareUtils;

/**
 * Created by yoghourt on 5/25/16.
 */

public class ShareDialog extends Dialog {

    private Context mContext;

    private DialogShareBinding mDataBinding;

    private OnRegionSelectListener onRegionSelectListener;

    private String imagePath = "";//图片分享内容

    private String url = "";//url分享内容

    public ShareDialog(Context context) {
        super(context, R.style.customDialogStyle);
        mContext = context;
    }

    public ShareDialog(Context context, int themeResId) {
        super(context, themeResId);
        mContext = context;
    }

    //region INTERFACE
    public interface OnRegionSelectListener {
        void onRegionSelect(Location province, Location city, Location county);
    }
    //endregion

    protected ShareDialog(Context context, boolean cancelable, OnCancelListener cancelListener) {
        super(context, cancelable, cancelListener);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mDataBinding = DataBindingUtil.inflate(LayoutInflater.from(getContext()), R.layout.dialog_share, null, false);

        setContentView(mDataBinding.getRoot());

        Window window = getWindow();
        window.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT); // 填充满屏幕的宽度
        window.setWindowAnimations(R.style.action_sheet_animation); // 添加动画
        WindowManager.LayoutParams wlp = window.getAttributes();
        wlp.gravity = Gravity.BOTTOM; // 使dialog在底部显示
        window.setAttributes(wlp);

        setListener();
    }

    public void initImageShareParams(String imagePath) {
        this.imagePath = imagePath;
    }

    public void initURLShareParams(String url) {
        this.url = url;
    }

    private void setListener() {
        mDataBinding.btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });

        mDataBinding.rltShareToWechat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                shareToWechat();
            }
        });

        mDataBinding.rltShareToWechatMoment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                shareToWechatMoment();
            }
        });

        mDataBinding.rltShareToQQ.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                shareToQQ();
            }
        });

        mDataBinding.rltShareToWeibo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                shareToWeibo();
            }
        });
    }

    private void shareToWechat() {
        dismiss();
        ShareUtils.shareToWechat(mContext, ShareUtils.getWechatShareParams(mContext, url, imagePath));
    }

    private void shareToWechatMoment() {
        dismiss();
        ShareUtils.shareToWechatMoment(mContext, ShareUtils.getWechatShareParams(mContext, url, imagePath));
    }

    private void shareToQQ() {
        dismiss();
        ShareUtils.shareToQQ(mContext, ShareUtils.getQQShareParams(mContext, url, imagePath));
    }

    private void shareToWeibo() {
        dismiss();
        ShareUtils.shareToWeibo(mContext, ShareUtils.getWeiBoShareParams(mContext, url, imagePath));
    }

}
