package com.hailan.HaiLanPrint.models;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by yoghourt on 8/5/16.
 */
public class OrderWorkPhotoEdit implements Parcelable{

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

    public OrderWorkPhotoEdit() {
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.AutoID);
        dest.writeInt(this.ParentID);
        dest.writeInt(this.PhotoID);
        dest.writeInt(this.PhotoSID);
        dest.writeInt(this.PositionX);
        dest.writeInt(this.PositionY);
        dest.writeFloat(this.Rotate);
        dest.writeFloat(this.ZoomSize);
        dest.writeString(this.Matrix);
        dest.writeInt(this.FrameID);
    }

    protected OrderWorkPhotoEdit(Parcel in) {
        this.AutoID = in.readInt();
        this.ParentID = in.readInt();
        this.PhotoID = in.readInt();
        this.PhotoSID = in.readInt();
        this.PositionX = in.readInt();
        this.PositionY = in.readInt();
        this.Rotate = in.readFloat();
        this.ZoomSize = in.readFloat();
        this.Matrix = in.readString();
        this.FrameID = in.readInt();
    }

    public static final Creator<OrderWorkPhotoEdit> CREATOR = new Creator<OrderWorkPhotoEdit>() {
        @Override
        public OrderWorkPhotoEdit createFromParcel(Parcel source) {
            return new OrderWorkPhotoEdit(source);
        }

        @Override
        public OrderWorkPhotoEdit[] newArray(int size) {
            return new OrderWorkPhotoEdit[size];
        }
    };
}
