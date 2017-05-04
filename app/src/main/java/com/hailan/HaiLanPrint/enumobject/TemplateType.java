package com.hailan.HaiLanPrint.enumobject;

/**
 * Created by yoghourt on 5/16/16.
 */
public enum TemplateType {

    Parent_Child(1),    // 亲子
    Campus(2),          // 校园
    Travel(3),          // 旅游
    Photo(4),           // 写真
    Classic(5),         // 经典
    Love(6);            // 爱情

    private int value;

    private TemplateType(int product) {
        this.value = product;
    }

    public int value() {
        return value;
    }

    public static TemplateType fromValue(int value) {
        for (TemplateType type : TemplateType.values()) {
            if (type.value() == value) {
                return type;
            }
        }
        return null;
    }
}
