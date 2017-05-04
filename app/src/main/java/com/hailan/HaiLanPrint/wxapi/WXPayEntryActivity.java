package com.hailan.HaiLanPrint.wxapi;


import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.hailan.HaiLanPrint.activity.payment.wechat.WechatPayCallback;
import com.hailan.HaiLanPrint.activity.payment.wechat.WechatPayUtil;
import com.tencent.mm.sdk.constants.ConstantsAPI;
import com.tencent.mm.sdk.modelbase.BaseReq;
import com.tencent.mm.sdk.modelbase.BaseResp;
import com.tencent.mm.sdk.openapi.IWXAPI;
import com.tencent.mm.sdk.openapi.IWXAPIEventHandler;
import com.tencent.mm.sdk.openapi.WXAPIFactory;


/**
 * 微信支付回调
 */
public class WXPayEntryActivity extends Activity implements IWXAPIEventHandler {

    private static final String TAG = "WXPayEntryActivity";
    private IWXAPI api;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        api = WXAPIFactory.createWXAPI(this, WechatPayUtil.APP_ID);
        api.handleIntent(getIntent(), this);
        Log.e(TAG, "onPay Activity");
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);
        api.handleIntent(intent, this);
    }

    @Override
    public void onReq(BaseReq req) {
        Log.e(TAG, "onPayBegin");
    }

    @Override
    public void onResp(BaseResp resp) {
        Log.e(TAG, "onPayFinish, errCode = " + resp.errCode);

        if (resp.getType() == ConstantsAPI.COMMAND_PAY_BY_WX) {
            if (resp.errCode == BaseResp.ErrCode.ERR_OK) {
                showToast("支付成功");
                callbackSuccess(resp.errCode);
            } else {
                showToast("支付失败");
                callbackError(resp.errCode);
            }
        } else {
            showToast("支付失败");
            callbackError(resp.errCode);
        }
        finish();
    }

    private void showToast(String content) {
        Toast.makeText(this, content, Toast.LENGTH_SHORT).show();
    }

    /**
     * 成功回调
     */
    private void callbackSuccess(int errCode) {
        WechatPayCallback.sendSuccessBroadcast(this, errCode);
    }

    /**
     * 失败回调
     */
    public void callbackError(int errCode) {
        WechatPayCallback.sendErrorBroadcast(this, errCode);
    }

}