package com.hailan.HaiLanPrint.models;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.List;

/**
 * Created by yoghourt on 5/24/16.
 */

public class OrderWorkPhoto implements Parcelable{

    private int AutoID;

    private int BrightLevel;

    private int Photo1ID;

    private int Photo1SID;

    private int Photo2ID;

    private int Photo2SID;

    private int PhotoCount;

    private int PhotoIndex;

    private String tDescription;

    private int WorkOID;

    private int TemplatePID;

    private int TemplatePSID;

    private List<OrderWorkPhotoEdit> OrderWorkPhotoEditList;

    public OrderWorkPhoto() {

    }

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

    public int getPhotoCount() {
        return PhotoCount;
    }

    public void setPhotoCount(int photoCount) {
        this.PhotoCount = photoCount;
    }

    public int getPhotoIndex() {
        return PhotoIndex;
    }

    public void setPhotoIndex(int photoIndex) {
        PhotoIndex = photoIndex;
    }

    public String gettDescription() {
        return tDescription;
    }

    public void settDescription(String tDescription) {
        this.tDescription = tDescription;
    }

    public int getWorkOID() {
        return WorkOID;
    }

    public void setWorkOID(int workOID) {
        WorkOID = workOID;
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

    public List<OrderWorkPhotoEdit> getOrderWorkPhotoEditList() {
        return OrderWorkPhotoEditList;
    }

    public void setOrderWorkPhotoEditList(List<OrderWorkPhotoEdit> orderWorkPhotoEditList) {
        OrderWorkPhotoEditList = orderWorkPhotoEditList;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.AutoID);
        dest.writeInt(this.BrightLevel);
        dest.writeInt(this.Photo1ID);
        dest.writeInt(this.Photo1SID);
        dest.writeInt(this.Photo2ID);
        dest.writeInt(this.Photo2SID);
        dest.writeInt(this.PhotoCount);
        dest.writeInt(this.PhotoIndex);
        dest.writeString(this.tDescription);
        dest.writeInt(this.WorkOID);
        dest.writeInt(this.TemplatePID);
        dest.writeInt(this.TemplatePSID);
        dest.writeTypedList(this.OrderWorkPhotoEditList);
    }

    protected OrderWorkPhoto(Parcel in) {
        this.AutoID = in.readInt();
        this.BrightLevel = in.readInt();
        this.Photo1ID = in.readInt();
        this.Photo1SID = in.readInt();
        this.Photo2ID = in.readInt();
        this.Photo2SID = in.readInt();
        this.PhotoCount = in.readInt();
        this.PhotoIndex = in.readInt();
        this.tDescription = in.readString();
        this.WorkOID = in.readInt();
        this.TemplatePID = in.readInt();
        this.TemplatePSID = in.readInt();
        this.OrderWorkPhotoEditList = in.createTypedArrayList(OrderWorkPhotoEdit.CREATOR);
    }

    public static final Creator<OrderWorkPhoto> CREATOR = new Creator<OrderWorkPhoto>() {
        @Override
        public OrderWorkPhoto createFromParcel(Parcel source) {
            return new OrderWorkPhoto(source);
        }

        @Override
        public OrderWorkPhoto[] newArray(int size) {
            return new OrderWorkPhoto[size];
        }
    };
}
