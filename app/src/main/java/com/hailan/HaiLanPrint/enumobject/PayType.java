package com.hailan.HaiLanPrint.enumobject;

/**
 * Created by yoghourt on 5/16/16.
 */
public enum PayType {

    WeChat(1),          // 微信支付
    Alipay(2);          // 支付宝支付

    private int value;

    private PayType(int product) {
        this.value = product;
    }

    public int value() {
        return value;
    }

    public static PayType fromValue(int value) {
        for (PayType type : PayType.values()) {
            if (type.value() == value) {
                return type;
            }
        }
        return null;
    }
}
