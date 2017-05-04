package com.hailan.HaiLanPrint.models;

/**
 * Created by yoghourt on 6/17/16.
 */

public class SystemSetting {

    private int AutoID;

    private int SettingType;

    private String UpdatedTime;

    private int Value;

    private String ValueDesc;

    public int getAutoID() {
        return AutoID;
    }

    public void setAutoID(int autoID) {
        AutoID = autoID;
    }

    public int getSettingType() {
        return SettingType;
    }

    public void setSettingType(int settingType) {
        SettingType = settingType;
    }

    public String getUpdatedTime() {
        return UpdatedTime;
    }

    public void setUpdatedTime(String updatedTime) {
        UpdatedTime = updatedTime;
    }

    public int getValue() {
        return Value;
    }

    public void setValue(int value) {
        Value = value;
    }

    public String getValueDesc() {
        return ValueDesc;
    }

    public void setValueDesc(String valueDesc) {
        ValueDesc = valueDesc;
    }
}
