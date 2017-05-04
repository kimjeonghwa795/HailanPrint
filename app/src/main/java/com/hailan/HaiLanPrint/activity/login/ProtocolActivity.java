package com.hailan.HaiLanPrint.activity.login;

import com.hailan.HaiLanPrint.R;
import com.hailan.HaiLanPrint.activity.base.ToolbarActivity;
import com.hailan.HaiLanPrint.databinding.ActivityProtocolBinding;

public class ProtocolActivity extends ToolbarActivity<ActivityProtocolBinding> {

    @Override
    public int getContentLayout() {
        return R.layout.activity_protocol;
    }

    @Override
    protected void initData() {
        mDataBinding.webView.loadUrl("file:///android_asset/service_protocol.html");
    }

    @Override
    protected void setListener() {

    }
}

