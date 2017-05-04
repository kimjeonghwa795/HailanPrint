package com.hailan.HaiLanPrint.models;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by dee on 2015/8/5.
 */
public class Album implements Parcelable {
    private String name;
    private String path;
    private String firstImagePath;
    private int imageNum;
    private List<MDImage> images = new ArrayList<MDImage>();

    public Album() {

    }

    protected Album(Parcel in) {
        name = in.readString();
        path = in.readString();
        firstImagePath = in.readString();
        imageNum = in.readInt();
        images = in.createTypedArrayList(MDImage.CREATOR);
    }

    public static final Creator<Album> CREATOR = new Creator<Album>() {
        @Override
        public Album createFromParcel(Parcel in) {
            return new Album(in);
        }

        @Override
        public Album[] newArray(int size) {
            return new Album[size];
        }
    };

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getFirstImagePath() {
        return firstImagePath;
    }

    public void setFirstImagePath(String firstImagePath) {
        this.firstImagePath = firstImagePath;
    }

    public int getImageNum() {
        return imageNum;
    }

    public void setImageNum(int imageNum) {
        this.imageNum = imageNum;
    }

    public List<MDImage> getImages() {
        return images;
    }

    public void setImages(List<MDImage> images) {
        this.images = images;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(name);
        dest.writeString(path);
        dest.writeString(firstImagePath);
        dest.writeInt(imageNum);
        dest.writeTypedList(images);
    }
}
