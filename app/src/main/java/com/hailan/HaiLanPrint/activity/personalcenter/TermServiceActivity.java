package com.hailan.HaiLanPrint.activity.personalcenter;

import com.hailan.HaiLanPrint.R;
import com.hailan.HaiLanPrint.activity.base.ToolbarActivity;
import com.hailan.HaiLanPrint.databinding.ActivityTermServiceBinding;

/**
 * Created by PC on 2016-06-15.
 */

public class TermServiceActivity extends ToolbarActivity<ActivityTermServiceBinding> {

    @Override
    protected int getContentLayout() {
        return R.layout.activity_term_service;
    }

    @Override
    protected void initData() {
        mDataBinding.webView.loadUrl("file:///android_asset/service_protocol.html");
    }

    @Override
    protected void setListener() {
    }

}
