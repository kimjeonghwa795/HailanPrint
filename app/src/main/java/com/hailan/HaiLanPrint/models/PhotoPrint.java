package com.hailan.HaiLanPrint.models;

import android.os.Parcel;
import android.os.Parcelable;
import android.view.View;

/**
 * Created by yoghourt on 5/12/16.
 */
public class PhotoPrint implements Parcelable {

    private int AutoID;

    private int PhotoID;

    private int PhotoSID;

    private int PhotoCount;

    private boolean Shared;

    private transient BindingHandler handler = new BindingHandler();

    public BindingHandler getHandler() {
        if (handler == null) {
            handler = new BindingHandler();
        }
        return handler;
    }

    protected PhotoPrint(Parcel in) {
        AutoID = in.readInt();
        PhotoID = in.readInt();
        PhotoSID = in.readInt();
        PhotoCount = in.readInt();
        Shared = in.readByte() != 0;
    }

    public static final Creator<PhotoPrint> CREATOR = new Creator<PhotoPrint>() {
        @Override
        public PhotoPrint createFromParcel(Parcel in) {
            return new PhotoPrint(in);
        }

        @Override
        public PhotoPrint[] newArray(int size) {
            return new PhotoPrint[size];
        }
    };

    public int getAutoID() {
        return AutoID;
    }

    public void setAutoID(int autoID) {
        AutoID = autoID;
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

    public int getPhotoCount() {
        return PhotoCount;
    }

    public void setPhotoCount(int photoCount) {
        PhotoCount = photoCount;
    }

    public boolean isShared() {
        return Shared;
    }

    public void setShared(boolean shared) {
        Shared = shared;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(AutoID);
        dest.writeInt(PhotoID);
        dest.writeInt(PhotoSID);
        dest.writeInt(PhotoCount);
        dest.writeByte((byte) (Shared ? 1 : 0));
    }

    public class BindingHandler {
        public void toImageSelectorActivityAction(View view) {
        }
    }
}
