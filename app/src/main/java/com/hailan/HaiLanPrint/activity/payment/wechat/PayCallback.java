package com.hailan.HaiLanPrint.activity.payment.wechat;

/**
 * Author : zhouyx
 * Date   : 2016/10/16
 * Description : 支付回调
 */
public abstract class PayCallback {

    public enum PayType {
        Alipay,//支付宝
        Wechat,//微信
        Uppay//银联
    }

    public static final int ERROR = 0;      // 失败
    public static final int SUCCESS = 1;    // 成功

    public abstract void onPayCallback(PayType payType, int code, Object info);

}
