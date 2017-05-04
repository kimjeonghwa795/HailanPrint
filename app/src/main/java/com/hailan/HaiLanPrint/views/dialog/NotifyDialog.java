package com.hailan.HaiLanPrint.views.dialog;

import android.app.Dialog;
import android.content.Context;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout.LayoutParams;

import com.hailan.HaiLanPrint.R;
import com.hailan.HaiLanPrint.databinding.DialogNotifyBinding;


/**
 * 提示消息
 *
 * @author yoghourt
 */
public class NotifyDialog extends Dialog {

    private DialogNotifyBinding mDataBinding;

    private String mtips;

    private OnSureClickListener mListener;

    public static interface OnSureClickListener {
        public void onSureClick();
    }

    public NotifyDialog(Context context, String tips) {
        this(context, R.style.customDialogStyle);
        this.mtips = tips;
    }

    public NotifyDialog(Context context) {
        this(context, R.style.customDialogStyle);
    }

    public NotifyDialog(Context context, int theme) {
        super(context, theme);
    }

    protected NotifyDialog(Context context, boolean cancelable, OnCancelListener cancelListener) {
        super(context, cancelable, cancelListener);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mDataBinding = DataBindingUtil.inflate(LayoutInflater.from(getContext()), R.layout.dialog_notify, null, false);

        setContentView(mDataBinding.getRoot());

        getWindow().setLayout(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
        setCanceledOnTouchOutside(false);

        if (mtips != null) {
            mDataBinding.tvTips.setText(mtips);
        }

        mDataBinding.tvCancle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });

        mDataBinding.tvSure.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mListener != null) {
                    mListener.onSureClick();
                }
                dismiss();
            }
        });
    }

    public void setOnSureClickListener(OnSureClickListener listener) {
        this.mListener = listener;
    }

    public void setTitle(CharSequence title) {
        mDataBinding.tvTitle.setText(title);
    }

    public void setTvContent(CharSequence message) {
        mDataBinding.tvTips.setText(message);
    }

    public void setTvSure(CharSequence string) {
        mDataBinding.tvSure.setText(string);
    }
}
