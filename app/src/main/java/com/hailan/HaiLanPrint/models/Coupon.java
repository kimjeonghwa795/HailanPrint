package com.hailan.HaiLanPrint.models;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by yoghourt on 6/1/16.
 */

public class Coupon implements Parcelable{

    private String ActiveTime;

    private int AutoID;

    private int CouponID;

    private String CouponName;

    private String CouponNo;

    private int CouponStatus;//0:可使用 1已过期 2已使用

    private String CreatedTime;

    private String ExpireTime;

    private int Price;

    private int PriceLimit;

    private int UserID;

    protected Coupon(Parcel in) {
        ActiveTime = in.readString();
        AutoID = in.readInt();
        CouponID = in.readInt();
        CouponName = in.readString();
        CouponNo = in.readString();
        CouponStatus = in.readInt();
        CreatedTime = in.readString();
        ExpireTime = in.readString();
        Price = in.readInt();
        PriceLimit = in.readInt();
        UserID = in.readInt();
    }

    public static final Creator<Coupon> CREATOR = new Creator<Coupon>() {
        @Override
        public Coupon createFromParcel(Parcel in) {
            return new Coupon(in);
        }

        @Override
        public Coupon[] newArray(int size) {
            return new Coupon[size];
        }
    };

    public String getActiveTime() {
        return ActiveTime;
    }

    public void setActiveTime(String activeTime) {
        ActiveTime = activeTime;
    }

    public int getAutoID() {
        return AutoID;
    }

    public void setAutoID(int autoID) {
        AutoID = autoID;
    }

    public int getCouponID() {
        return CouponID;
    }

    public void setCouponID(int couponID) {
        CouponID = couponID;
    }

    public String getCouponName() {
        return CouponName;
    }

    public void setCouponName(String couponName) {
        CouponName = couponName;
    }

    public String getCouponNo() {
        return CouponNo;
    }

    public void setCouponNo(String couponNo) {
        CouponNo = couponNo;
    }

    public int getCouponStatus() {
        return CouponStatus;
    }

    public void setCouponStatus(int couponStatus) {
        CouponStatus = couponStatus;
    }

    public String getCreatedTime() {
        return CreatedTime;
    }

    public void setCreatedTime(String createdTime) {
        CreatedTime = createdTime;
    }

    public String getExpireTime() {
        return ExpireTime;
    }

    public void setExpireTime(String expireTime) {
        ExpireTime = expireTime;
    }

    public int getPrice() {
        return Price;
    }

    public void setPrice(int price) {
        Price = price;
    }

    public int getPriceLimit() {
        return PriceLimit;
    }

    public void setPriceLimit(int priceLimit) {
        PriceLimit = priceLimit;
    }

    public int getUserID() {
        return UserID;
    }

    public void setUserID(int userID) {
        UserID = userID;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(ActiveTime);
        dest.writeInt(AutoID);
        dest.writeInt(CouponID);
        dest.writeString(CouponName);
        dest.writeString(CouponNo);
        dest.writeInt(CouponStatus);
        dest.writeString(CreatedTime);
        dest.writeString(ExpireTime);
        dest.writeInt(Price);
        dest.writeInt(PriceLimit);
        dest.writeInt(UserID);
    }
}
