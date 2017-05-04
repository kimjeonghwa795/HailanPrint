package com.hailan.HaiLanPrint.models;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by yoghourt on 5/24/16.
 */

public class DeliveryAddress implements Parcelable {

    private int AutoID;

    private long CityID;

    private long CountryID;

    private long DistrictID;

    private String Phone;

    private long ProvinceID;

    private String Receiver;

    private String Street;

    private String UpdatedTime;

    private int UserID;

    public DeliveryAddress() {

    }

    public int getAutoID() {
        return AutoID;
    }

    public void setAutoID(int autoID) {
        AutoID = autoID;
    }

    public long getCityID() {
        return CityID;
    }

    public void setCityID(long cityID) {
        CityID = cityID;
    }

    public long getCountryID() {
        return CountryID;
    }

    public void setCountryID(long countryID) {
        CountryID = countryID;
    }

    public long getDistrictID() {
        return DistrictID;
    }

    public void setDistrictID(long districtID) {
        DistrictID = districtID;
    }

    public String getPhone() {
        return Phone;
    }

    public void setPhone(String phone) {
        Phone = phone;
    }

    public long getProvinceID() {
        return ProvinceID;
    }

    public void setProvinceID(long provinceID) {
        ProvinceID = provinceID;
    }

    public String getReceiver() {
        return Receiver;
    }

    public void setReceiver(String receiver) {
        Receiver = receiver;
    }

    public String getStreet() {
        return Street;
    }

    public void setStreet(String street) {
        Street = street;
    }

    public String getUpdatedTime() {
        return UpdatedTime;
    }

    public void setUpdatedTime(String updatedTime) {
        UpdatedTime = updatedTime;
    }

    public int getUserID() {
        return UserID;
    }

    public void setUserID(int userID) {
        UserID = userID;
    }

    protected DeliveryAddress(Parcel in) {
        AutoID = in.readInt();
        CityID = in.readLong();
        CountryID = in.readLong();
        DistrictID = in.readLong();
        Phone = in.readString();
        ProvinceID = in.readLong();
        Receiver = in.readString();
        Street = in.readString();
        UpdatedTime = in.readString();
        UserID = in.readInt();
    }

    public static final Creator<DeliveryAddress> CREATOR = new Creator<DeliveryAddress>() {
        @Override
        public DeliveryAddress createFromParcel(Parcel in) {
            return new DeliveryAddress(in);
        }

        @Override
        public DeliveryAddress[] newArray(int size) {
            return new DeliveryAddress[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(AutoID);
        dest.writeLong(CityID);
        dest.writeLong(CountryID);
        dest.writeLong(DistrictID);
        dest.writeString(Phone);
        dest.writeLong(ProvinceID);
        dest.writeString(Receiver);
        dest.writeString(Street);
        dest.writeString(UpdatedTime);
        dest.writeInt(UserID);
    }
}
