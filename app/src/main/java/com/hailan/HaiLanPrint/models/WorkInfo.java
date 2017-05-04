package com.hailan.HaiLanPrint.models;

import java.io.Serializable;

/**
 * Created by yoghourt on 6/11/16.
 */

public class WorkInfo implements Serializable,Comparable<WorkInfo> {

    private int UserID;

    private int Price;

    private String CreatedTime;

    private int TypeID;

    private String TypeName;

    private int TemplateID;

    private String TypeTitle;

    private int WorkID;

    private int PhotoCover;

    private String WorkDesc;

    private String WorkFormat;

    private String WorkMaterial;

    private String WorkStyle;

    private int PhotoCount;

    private boolean Enabled;

    public int getUserID() {
        return UserID;
    }

    public void setUserID(int userID) {
        UserID = userID;
    }

    public int getPrice() {
        return Price;
    }

    public void setPrice(int price) {
        Price = price;
    }

    public String getCreatedTime() {
        return CreatedTime;
    }

    public void setCreatedTime(String createdTime) {
        CreatedTime = createdTime;
    }

    public int getTypeID() {
        return TypeID;
    }

    public void setTypeID(int typeID) {
        TypeID = typeID;
    }

    public String getTypeName() {
        return TypeName;
    }

    public void setTypeName(String typeName) {
        TypeName = typeName;
    }

    public int getTemplateID() {
        return TemplateID;
    }

    public void setTemplateID(int templateID) {
        TemplateID = templateID;
    }

    public String getTypeTitle() {
        return TypeTitle;
    }

    public void setTypeTitle(String typeTitle) {
        TypeTitle = typeTitle;
    }

    public int getWorkID() {
        return WorkID;
    }

    public void setWorkID(int workID) {
        WorkID = workID;
    }

    public int getPhotoCover() {
        return PhotoCover;
    }

    public void setPhotoCover(int photoCover) {
        PhotoCover = photoCover;
    }

    public String getWorkDesc() {
        return WorkDesc;
    }

    public void setWorkDesc(String workDesc) {
        WorkDesc = workDesc;
    }

    public int getPhotoCount() {
        return PhotoCount;
    }

    public void setPhotoCount(int photoCount) {
        PhotoCount = photoCount;
    }

    public String getWorkFormat() {
        return WorkFormat;
    }

    public void setWorkFormat(String workFormat) {
        WorkFormat = workFormat;
    }

    public String getWorkMaterial() {
        return WorkMaterial;
    }

    public void setWorkMaterial(String workMaterial) {
        WorkMaterial = workMaterial;
    }

    public String getWorkStyle() {
        return WorkStyle;
    }

    public void setWorkStyle(String workStyle) {
        WorkStyle = workStyle;
    }

    public boolean isEnabled() {
        return Enabled;
    }

    public void setEnabled(boolean enabled) {
        Enabled = enabled;
    }

    @Override
    public int compareTo(WorkInfo another) {
        return (this.TypeID - another.TypeID);
    }
}
