package com.hailan.HaiLanPrint.activity.payment.wechat;

import android.app.Activity;


public class PayUtils {

    /**
     * 微信支付 APP只需配置APP_ID
     * WXPayEntryActivity回调
     *
     * @param activity
     * @param prepayId  服务端加密后返回的预支付单号
     * @param partner   服务端返回的商户号
     * @param nonceStr  服务端加密后返回的随机数字符串
     * @param timeStamp 服务端返回的交易时间戳
     * @param appSign   服务端加密后返回的订单信息
     */
    public static void wechatPay(Activity activity, String prepayId, String partner, String nonceStr,
                                 String timeStamp, String appSign) {
        WechatPayUtil.MCH_ID = partner;

        WechatModel wechatModel = new WechatModel();
        wechatModel.setPrepayId(prepayId);
        wechatModel.setNonceStr(nonceStr);
        wechatModel.setTimeStamp(timeStamp);
        wechatModel.setAppSign(appSign);
        wechatModel.setPayType(WechatModel.PAY_TYPE_2);

        WechatHelper helper = new WechatHelper(activity, wechatModel);
        helper.pay();
    }

}
