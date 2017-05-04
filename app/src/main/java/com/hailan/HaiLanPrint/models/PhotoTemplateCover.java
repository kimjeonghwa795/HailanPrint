package com.hailan.HaiLanPrint.models;

/**
 * Created by yoghourt on 9/14/16.
 */
public class PhotoTemplateCover {

    private int AutoID;

    private int TemplateID;

    private int PhotoID;

    private int PhotoSID;

    private int PhotoIndex;

    private int Width;

    private int Height;

    private String UpdatedTime;

    public int getAutoID() {
        return AutoID;
    }

    public void setAutoID(int autoID) {
        AutoID = autoID;
    }

    public int getTemplateID() {
        return TemplateID;
    }

    public void setTemplateID(int templateID) {
        TemplateID = templateID;
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

    public int getPhotoIndex() {
        return PhotoIndex;
    }

    public void setPhotoIndex(int photoIndex) {
        PhotoIndex = photoIndex;
    }

    public int getWidth() {
        return Width;
    }

    public void setWidth(int width) {
        Width = width;
    }

    public int getHeight() {
        return Height;
    }

    public void setHeight(int height) {
        Height = height;
    }

    public String getUpdatedTime() {
        return UpdatedTime;
    }

    public void setUpdatedTime(String updatedTime) {
        UpdatedTime = updatedTime;
    }
}
