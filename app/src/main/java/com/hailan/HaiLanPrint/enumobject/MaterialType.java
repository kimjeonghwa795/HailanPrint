package com.hailan.HaiLanPrint.enumobject;

/**
 * Created by yoghourt on 5/16/16.
 */
public enum MaterialType {

    Landscape(1),     // 横版
    Portrait(2),        // 竖版
    Silicone(4),        // 硅胶
    Plastic(8);         // 塑料

    private int value;

    private MaterialType(int product) {
        this.value = product;
    }

    public int value() {
        return value;
    }

    public static MaterialType fromValue(int value) {
        for (MaterialType type : MaterialType.values()) {
            if (type.value() == value) {
                return type;
            }
        }
        return null;
    }
}
