package com.hailan.HaiLanPrint.activity.payment.wechat;

public class WechatPayUtil {

    /**
     * ############################################################################################
     * 微信支付，微信支付分两种方式，一种为服务的获取prepareId，一种为客户端获取id，客户端获取prepareId相关信息可以不填写
     * ############################################################################################
     */
    //请同时修改 AndroidManifest.xml里面，PayActivity里的属性<data android:scheme="wxb4ba3c02aa476ea1"/>为新设置的appid
    public static final String APP_ID = "wxe64d610e925062a2";
    // 商户号 （尽量服务端获取）
    public static String MCH_ID = "";
    // API密钥，在商户平台设置  （尽量服务端获取）
    public static String API_KEY = "";

}
