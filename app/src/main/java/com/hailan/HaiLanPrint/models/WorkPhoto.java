package com.hailan.HaiLanPrint.models;

import java.util.List;

/**
 * Created by yoghourt on 6/11/16.
 */

public class WorkPhoto {

    private int AutoID;

    private int BrightLevel;

    private String Description;

    private int Photo1ID;

    private int Photo1SID;

    private int Photo2ID;

    private int Photo2SID;

    private int PhotoIndex;

    private int PositionX;

    private int PositionY;

    private int Rotate;

    private int TemplatePID;

    private int TemplatePSID;

    private int WorkID;

    private int ZoomSize;

    private List<WorkPhotoEdit> WorkPhotoEditList;

    private String matrix;

    public int getAutoID() {
        return AutoID;
    }

    public void setAutoID(int autoID) {
        AutoID = autoID;
    }

    public int getBrightLevel() {
        return BrightLevel;
    }

    public void setBrightLevel(int brightLevel) {
        BrightLevel = brightLevel;
    }

    public String getDescription() {
        return Description;
    }

    public void setDescription(String description) {
        Description = description;
    }

    public int getPhoto1ID() {
        return Photo1ID;
    }

    public void setPhoto1ID(int photo1ID) {
        Photo1ID = photo1ID;
    }

    public int getPhoto1SID() {
        return Photo1SID;
    }

    public void setPhoto1SID(int photo1SID) {
        Photo1SID = photo1SID;
    }

    public int getPhoto2ID() {
        return Photo2ID;
    }

    public void setPhoto2ID(int photo2ID) {
        Photo2ID = photo2ID;
    }

    public int getPhoto2SID() {
        return Photo2SID;
    }

    public void setPhoto2SID(int photo2SID) {
        Photo2SID = photo2SID;
    }

    public int getPhotoIndex() {
        return PhotoIndex;
    }

    public void setPhotoIndex(int photoIndex) {
        PhotoIndex = photoIndex;
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

    public void setRotate(int rotate) {
        Rotate = rotate;
    }

    public int getTemplatePID() {
        return TemplatePID;
    }

    public void setTemplatePID(int templatePID) {
        TemplatePID = templatePID;
    }

    public int getTemplatePSID() {
        return TemplatePSID;
    }

    public void setTemplatePSID(int templatePSID) {
        TemplatePSID = templatePSID;
    }

    public int getWorkID() {
        return WorkID;
    }

    public void setWorkID(int workID) {
        WorkID = workID;
    }

    public float getZoomSize() {
        return ZoomSize;
    }

    public void setZoomSize(int zoomSize) {
        ZoomSize = zoomSize;
    }

    public List<WorkPhotoEdit> getWorkPhotoEditList() {
        return WorkPhotoEditList;
    }

    public void setWorkPhotoEditList(List<WorkPhotoEdit> workPhotoEditList) {
        WorkPhotoEditList = workPhotoEditList;
    }

    public String getMatrix() {
        return matrix;
    }

    public void setMatrix(String matrix) {
        this.matrix = matrix;
    }
}
