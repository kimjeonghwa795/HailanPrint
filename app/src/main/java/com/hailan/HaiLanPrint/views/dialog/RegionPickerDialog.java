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
import com.hailan.HaiLanPrint.databinding.DialogRegionPickerBinding;
import com.hailan.HaiLanPrint.greendao.Location;

/**
 * Created by yoghourt on 5/25/16.
 */

public class RegionPickerDialog extends Dialog {

    private DialogRegionPickerBinding mDataBinding;

    private OnRegionSelectListener onRegionSelectListener;

    public RegionPickerDialog(Context context) {
        super(context, R.style.customDialogStyle);
    }

//    public RegionPickerDialog(Context context, DeliveryAddress address) {
//        super(context, R.style.customDialogStyle);

//    }

    public RegionPickerDialog(Context context, int themeResId) {
        super(context, themeResId);
    }

    //region INTERFACE
    public interface OnRegionSelectListener {
        public void onRegionSelect(Location province, Location city, Location county);
    }
    //endregion

    protected RegionPickerDialog(Context context, boolean cancelable, OnCancelListener cancelListener) {
        super(context, cancelable, cancelListener);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mDataBinding = DataBindingUtil.inflate(LayoutInflater.from(getContext()), R.layout.dialog_region_picker, null, false);

        setContentView(mDataBinding.getRoot());

        Window window = getWindow();
        window.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT); // 填充满屏幕的宽度
        window.setWindowAnimations(R.style.action_sheet_animation); // 添加动画
        WindowManager.LayoutParams wlp = window.getAttributes();
        wlp.gravity = Gravity.BOTTOM; // 使dialog在底部显示
        window.setAttributes(wlp);

        mDataBinding.tvCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });

        mDataBinding.tvFinish.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (onRegionSelectListener != null) {
                    onRegionSelectListener.onRegionSelect(mDataBinding.regionWheelview.mSelectProvince,
                            mDataBinding.regionWheelview.mSelectCity,
                            mDataBinding.regionWheelview.mSelectCounty);
                }
                dismiss();
            }
        });
    }

    public void setOnRegionSelectListener(OnRegionSelectListener onRegionSelectListener) {
        this.onRegionSelectListener = onRegionSelectListener;
    }
}
