package com.hailan.HaiLanPrint.enumobject;

/**
 * Created by yoghourt on 5/16/16.
 */
public enum UploadType {

    Order(1),
    Work(2);

    private int value;

    private UploadType(int product) {
        this.value = product;
    }

    public int value() {
        return value;
    }

    public static UploadType fromValue(int value) {
        for (UploadType type : UploadType.values()) {
            if (type.value() == value) {
                return type;
            }
        }
        return null;
    }
}
