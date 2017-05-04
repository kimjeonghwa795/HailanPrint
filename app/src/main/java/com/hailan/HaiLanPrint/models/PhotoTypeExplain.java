package com.hailan.HaiLanPrint.models;

import java.io.Serializable;

/**
 * Created by yoghourt on 6/10/16.
 */

public class PhotoTypeExplain implements Serializable {

    private int AutoID;

    private int ExplainType;

    private int PhotoID;

    private int PhotoSID;

    private boolean Status;

    private String Title;

    private int TypeID;

    private String UpdatedTime;

    public int getAutoID() {
        return AutoID;
    }

    public void setAutoID(int autoID) {
        AutoID = autoID;
    }

    public int getExplainType() {
        return ExplainType;
    }

    public void setExplainType(int explainType) {
        ExplainType = explainType;
    }

    public int getPhotoID() {
        return PhotoID;
    }

    public void setPhotoID(int photoID) {
        PhotoID = photoID;
    }

    public int getPhotoSID() {
        return PhotoSID;
    }

    public void setPhotoSID(int photoSID) {
        PhotoSID = photoSID;
    }

    public boolean isStatus() {
        return Status;
    }

    public void setStatus(boolean status) {
        Status = status;
    }

    public String getTitle() {
        return Title;
    }

    public void setTitle(String title) {
        Title = title;
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
}
