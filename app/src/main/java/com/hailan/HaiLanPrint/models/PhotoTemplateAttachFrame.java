package com.hailan.HaiLanPrint.models;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * 模板图片定位块实体
 * Created by yoghourt on 7/29/16.
 */
public class PhotoTemplateAttachFrame implements Parcelable {

    private int FrameID;
    private int Height;
    private int ParentID;
    private int PositionX;
    private int PositionY;
    private int Width;
    private String matrix;

    protected PhotoTemplateAttachFrame(Parcel in) {
        FrameID = in.readInt();
        Height = in.readInt();
        ParentID = in.readInt();
        PositionX = in.readInt();
        PositionY = in.readInt();
        Width = in.readInt();
        matrix = in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(FrameID);
        dest.writeInt(Height);
        dest.writeInt(ParentID);
        dest.writeInt(PositionX);
        dest.writeInt(PositionY);
        dest.writeInt(Width);
        dest.writeString(matrix);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<PhotoTemplateAttachFrame> CREATOR = new Creator<PhotoTemplateAttachFrame>() {
        @Override
        public PhotoTemplateAttachFrame createFromParcel(Parcel in) {
            return new PhotoTemplateAttachFrame(in);
        }

        @Override
        public PhotoTemplateAttachFrame[] newArray(int size) {
            return new PhotoTemplateAttachFrame[size];
        }
    };

    public int getFrameID() {
        return FrameID;
    }

    public void setFrameID(int frameID) {
        FrameID = frameID;
    }

    public int getHeight() {
        return Height;
    }

    public void setHeight(int height) {
        Height = height;
    }

    public int getParentID() {
        return ParentID;
    }

    public void setParentID(int parentID) {
        ParentID = parentID;
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

    public int getWidth() {
        return Width;
    }

    public void setWidth(int width) {
        Width = width;
    }

    public String getMatrix() {
        return matrix;
    }

    public void setMatrix(String matrix) {
        this.matrix = matrix;
    }
}
