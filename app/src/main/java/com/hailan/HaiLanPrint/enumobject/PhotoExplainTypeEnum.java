package com.hailan.HaiLanPrint.enumobject;

/**
 * Created by yoghourt on 5/16/16.
 */
public enum PhotoExplainTypeEnum {

    Banner(1),
    IntroductionPage(2),
    MeasurementDescription(3);

    private int value;

    private PhotoExplainTypeEnum(int product) {
        this.value = product;
    }

    public int value() {
        return value;
    }

    public static PhotoExplainTypeEnum fromValue(int value) {
        for (PhotoExplainTypeEnum type : PhotoExplainTypeEnum.values()) {
            if (type.value() == value) {
                return type;
            }
        }
        return null;
    }
}
