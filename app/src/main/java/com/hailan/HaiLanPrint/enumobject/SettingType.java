package com.hailan.HaiLanPrint.enumobject;

/**
 * Created by yoghourt on 5/16/16.
 */
public enum SettingType {

    GetIntegralAmount(1),     // 消费1元获得积分数
    PayIntegralAmount(2),     // 1积分可抵现现金数
    DeliveryFee(3),           // 运费
    IOSVersion(4),            // ios最新版本
    ANDROIDVersion(5);        // android最新版本

    private int value;

    SettingType(int product) {
        this.value = product;
    }

    public int value() {
        return value;
    }

    public static SettingType fromValue(int value) {
        for (SettingType type : SettingType.values()) {
            if (type.value() == value) {
                return type;
            }
        }
        return null;
    }
}
