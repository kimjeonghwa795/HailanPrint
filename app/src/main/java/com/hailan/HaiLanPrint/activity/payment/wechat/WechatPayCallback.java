package com.hailan.HaiLanPrint.activity.payment.wechat;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.v4.content.LocalBroadcastManager;


/**
 * Author : zhouyx
 * Date   : 2016/10/16
 * Description : 微信支付回调
 */
public class WechatPayCallback extends BroadcastReceiver {

    public static final String Action = "com.payutils.Intent.WechatPayAction";
    private static final int ERROR = 0;
    private static final int SUCCESS = 1;
    private static final String extra_status = "extra_status", extra_errCode = "extra_errCode";
    private PayCallback payCallback;

    /**
     * 成功广播
     *
     * @param context
     * @param errCode
     */
    public static void sendSuccessBroadcast(Context context, int errCode) {
        Intent i = new Intent(Action);
        i.putExtra(extra_status, SUCCESS);
        i.putExtra(extra_errCode, errCode);
        LocalBroadcastManager.getInstance(context).sendBroadcast(i);
    }

    /**
     * 失败广播
     *
     * @param context
     * @param errCode
     */
    public static void sendErrorBroadcast(Context context, int errCode) {
        Intent i = new Intent(Action);
        i.putExtra(extra_status, ERROR);
        i.putExtra(extra_errCode, errCode);
        LocalBroadcastManager.getInstance(context).sendBroadcast(i);
    }

    /**
     * 绑定监听
     *
     * @param context
     * @param wechatPayCallback
     */
    public static void register(Context context, WechatPayCallback wechatPayCallback) {
        IntentFilter filter = new IntentFilter();
        filter.addAction(Action);
        LocalBroadcastManager.getInstance(context).registerReceiver(wechatPayCallback, filter);
    }

    /**
     * 解除绑定监听
     *
     * @param context
     * @param wechatPayCallback
     */
    public static void unregister(Context context, WechatPayCallback wechatPayCallback) {
        LocalBroadcastManager.getInstance(context).unregisterReceiver(wechatPayCallback);
    }

    public void setPayCallback(PayCallback payCallback) {
        this.payCallback = payCallback;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        int status = intent.getIntExtra(extra_status, SUCCESS);
        int errCode = intent.getIntExtra(extra_errCode, 0);
        switch (status) {
            case SUCCESS:
                if (payCallback != null) {
                    payCallback.onPayCallback(PayCallback.PayType.Wechat, PayCallback.SUCCESS, errCode);
                }
                break;
            case ERROR:
                if (payCallback != null) {
                    payCallback.onPayCallback(PayCallback.PayType.Wechat, PayCallback.ERROR, errCode);
                }
                break;
        }
    }

}
