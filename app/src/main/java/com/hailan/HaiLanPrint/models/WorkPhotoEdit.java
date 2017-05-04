package com.hailan.HaiLanPrint.models;

/**
 * Created by yoghourt on 7/29/16.
 */
public class WorkPhotoEdit {

    private int AutoID;

    private int ParentID;

    private int PhotoID;

    private int PhotoSID;

    private int PositionX;

    private int PositionY;

    private float Rotate;

    private float ZoomSize;

    private String Matrix;

    private int FrameID;

    public WorkPhotoEdit() {
        ZoomSize = 1.0f;
    }

    public int getAutoID() {
        return AutoID;
    }

    public void setAutoID(int autoID) {
        AutoID = autoID;
    }

    public int getParentID() {
        return ParentID;
    }

    public void setParentID(int parentID) {
        ParentID = parentID;
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

    public int getPositionX() {
        return PositionX;
    }

    public void setPositionX(int positionX) {
        PositionX = positionX;
    }

    public int getPositionY() {
        return PositionY;
    }

    public void setPositionY(int positionY) {
        PositionY = positionY;
    }

    public float getRotate() {
        return Rotate;
    }

    public void setRotate(float rotate) {
        Rotate = rotate;
    }

    public float getZoomSize() {
        return ZoomSize;
    }

    public void setZoomSize(float zoomSize) {
        ZoomSize = zoomSize;
    }

    public String getMatrix() {
        return Matrix;
    }

    public void setMatrix(String matrix) {
        Matrix = matrix;
    }

    public int getFrameID() {
        return FrameID;
    }

    public void setFrameID(int frameID) {
        FrameID = frameID;
    }
}
