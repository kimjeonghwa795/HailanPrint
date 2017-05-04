package com.hailan.HaiLanPrint.models;

import java.io.Serializable;

/**
 * Created by yoghourt on 5/16/16.
 */
public class Measurement implements Serializable {

    private int AutoID;

    private int Price;

    private String PriceDesc;

    private String Title;

    private String TitleSub;

    private int TypeDescID;

    private int TypeID;

    private String UpdatedTime;

    private int TypePhoto;

    public int getAutoID() {
        return AutoID;
    }

    public void setAutoID(int autoID) {
        AutoID = autoID;
    }

    public int getPrice() {
        return Price;
    }

    public void setPrice(int price) {
        Price = price;
    }

    public String getPriceDesc() {
        return PriceDesc;
    }

    public void setPriceDesc(String priceDesc) {
        PriceDesc = priceDesc;
    }

    public String getTitle() {
        return Title;
    }

    public void setTitle(String title) {
        Title = title;
    }

    public String getTitleSub() {
        return TitleSub;
    }

    public void setTitleSub(String titleSub) {
        TitleSub = titleSub;
    }

    public int getTypeDescID() {
        return TypeDescID;
    }

    public void setTypeDescID(int typeDescID) {
        TypeDescID = typeDescID;
    }

    public int getTypeID() {
        return TypeID;
    }

    public void setTypeID(int typeID) {
        TypeID = typeID;
    }

    public String getUpdatedTime() {
        return UpdatedTime;
    }

    public void setUpdatedTime(String updatedTime) {
        UpdatedTime = updatedTime;
    }

    public int getTypePhoto() {
        return TypePhoto;
    }

    public void setTypePhoto(int typePhoto) {
        TypePhoto = typePhoto;
    }
}
