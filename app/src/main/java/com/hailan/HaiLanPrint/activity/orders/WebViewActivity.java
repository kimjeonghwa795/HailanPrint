package com.hailan.HaiLanPrint.activity.orders;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.hailan.HaiLanPrint.R;
import com.hailan.HaiLanPrint.activity.base.ToolbarActivity;
import com.hailan.HaiLanPrint.databinding.ActivityWebViewBinding;
import com.socks.library.KLog;

/**
 * Author : zhouyx
 * Date   : 2016/12/2
 * Description :
 */
public class WebViewActivity extends ToolbarActivity<ActivityWebViewBinding> {

    /**
     * 查询快递信息
     *
     * @param context
     * @param expressCompany
     * @param expressNo
     */
    public static void queryExpress(Context context, String expressCompany, String expressNo) {
        Intent intent = new Intent(context, WebViewActivity.class);
        String url = "https://m.kuaidi100.com/index_all.html?type=" + expressCompany + "&postid=" + expressNo;
        intent.putExtra("url", url);
        context.startActivity(intent);
    }

    @Override
    protected int getContentLayout() {
        return R.layout.activity_web_view;
    }

    @Override
    protected void initData() {
        String url = getIntent().getStringExtra("url");
        KLog.e(url);
        mDataBinding.webView.getSettings().setJavaScriptEnabled(true);
        mDataBinding.webView.getSettings().setCacheMode(WebSettings.LOAD_CACHE_ELSE_NETWORK);
        mDataBinding.webView.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                view.loadUrl(url);
                return true;
            }

            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                mDataBinding.webView.getSettings().setBlockNetworkImage(true);
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                mDataBinding.webView.getSettings().setBlockNetworkImage(false);
            }
        });
        //加载需要显示的网页
        mDataBinding.webView.loadUrl(url);
    }

    @Override
    protected void setListener() {
    }

}
