package com.hailan.HaiLanPrint.enumobject;

/**
 * Created by yoghourt on 5/16/16.
 */
public enum ThirdPartyLoginType {

    Wechat(1),
    QQ(2),
    Weibo(3);

    private int value;

    private ThirdPartyLoginType(int product) {
        this.value = product;
    }

    public int value() {
        return value;
    }

    public static ThirdPartyLoginType fromValue(int value) {
        for (ThirdPartyLoginType type : ThirdPartyLoginType.values()) {
            if (type.value() == value) {
                return type;
            }
        }
        return null;
    }
}
