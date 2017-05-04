package com.hailan.HaiLanPrint.activity.orders;

import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebViewClient;

import com.hailan.HaiLanPrint.R;
import com.hailan.HaiLanPrint.activity.base.ToolbarActivity;
import com.hailan.HaiLanPrint.constants.Constants;
import com.hailan.HaiLanPrint.databinding.ActivityProductionInfoBinding;

/**
 * Created by yoghourt on 2016-08-04.
 */

public class ProductionInfoActivity extends ToolbarActivity<ActivityProductionInfoBinding> {

    @Override
    protected int getContentLayout() {
        return R.layout.activity_production_info;
    }

    @Override
    protected void initData() {
        WebSettings setting = mDataBinding.webView.getSettings();
        setSettings(setting);
        mDataBinding.webView.setWebChromeClient(new WebChromeClient());
        mDataBinding.webView.setWebViewClient(new WebViewClient());

        String orderUrl = getIntent().getStringExtra(Constants.KEY_ORDER_URL);
        mDataBinding.webView.loadUrl(orderUrl);
    }

    @Override
    protected void setListener() {
    }

    private void setSettings(WebSettings setting) {
        setting.setJavaScriptEnabled(true);
        setting.setBuiltInZoomControls(true);
        setting.setDisplayZoomControls(false);
        setting.setSupportZoom(true);

        setting.setDomStorageEnabled(true);
        setting.setDatabaseEnabled(true);
        // 全屏显示
        setting.setLoadWithOverviewMode(true);
        setting.setUseWideViewPort(true);
    }
}
