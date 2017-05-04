package com.hailan.HaiLanPrint.models;

import java.io.Serializable;

public class User implements Serializable {

    //public List<UserKid> UserKidList;

    public int UserID;

    public int UserRole;

    public String WXOpenID;

    public String WBUID;

    public String QQOpenID;

    public String Phone;

    public String Password;

    public int Status;

    public String UserName;

    public String UserNickName;

    public String DOB;

    public int Gender;

    public int PhotoID;

    public int PhotoSID;

    public int CountryID;

    public int ProvinceID;

    public int CityID;

    public int DistrictID;

    public int SystemSetting;

    public String InvitationCode;

    public String UpdatedTime;

    public String CreatedTime;

    public int DeviceID;

    public String ServiceToken;

    public String KidName1;

    public String KidDOB1;

    public String KidSchool1;

    public String KidClass1;

    public String KidName2;

    public String KidDOB2;

    public String KidSchool2;

    public String KidClass2;

    public String getKidName1() {
        return KidName1;
    }

    public void setKidName1(String kidName1) {
        KidName1 = kidName1;
    }

    public String getKidDOB1() {
        return KidDOB1;
    }

    public void setKidDOB1(String kidDOB1) {
        KidDOB1 = kidDOB1;
    }

    public String getKidSchool1() {
        return KidSchool1;
    }

    public void setKidSchool1(String kidSchool1) {
        KidSchool1 = kidSchool1;
    }

    public String getKidClass1() {
        return KidClass1;
    }

    public void setKidClass1(String kidClass1) {
        KidClass1 = kidClass1;
    }

    public String getKidName2() {
        return KidName2;
    }

    public void setKidName2(String kidName2) {
        KidName2 = kidName2;
    }

    public String getKidDOB2() {
        return KidDOB2;
    }

    public void setKidDOB2(String kidDOB2) {
        KidDOB2 = kidDOB2;
    }

    public String getKidSchool2() {
        return KidSchool2;
    }

    public void setKidSchool2(String kidSchool2) {
        KidSchool2 = kidSchool2;
    }

    public String getKidClass2() {
        return KidClass2;
    }

    public void setKidClass2(String kidClass2) {
        KidClass2 = kidClass2;
    }

    public User() {
        InvitationCode = "";
    }

//    public List<UserKid> getUserKidList() {
//        return UserKidList;
//    }
//
//    public void setUserKidList(List<UserKid> userKid) {
//        UserKidList = userKid;
//    }

    public int getUserID() {
        return UserID;
    }

    public void setUserID(int userID) {
        UserID = userID;
    }

    public int getUserRole() {
        return UserRole;
    }

    public void setUserRole(int userRole) {
        UserRole = userRole;
    }

    public String getWXOpenID() {
        return WXOpenID;
    }

    public void setWXOpenID(String WXOpenID) {
        this.WXOpenID = WXOpenID;
    }

    public String getWBUID() {
        return WBUID;
    }

    public void setWBUID(String WBUID) {
        this.WBUID = WBUID;
    }

    public String getQQOpenID() {
        return QQOpenID;
    }

    public void setQQOpenID(String QQOpenID) {
        this.QQOpenID = QQOpenID;
    }

    public String getPhone() {
        return Phone;
    }

    public void setPhone(String phone) {
        Phone = phone;
    }

    public String getPassword() {
        return Password;
    }

    public void setPassword(String password) {
        Password = password;
    }

    public int getStatus() {
        return Status;
    }

    public void setStatus(int status) {
        Status = status;
    }

    public String getUserName() {
        return UserName;
    }

    public void setUserName(String userName) {
        UserName = userName;
    }

    public String getUserNickName() {
        return UserNickName;
    }

    public void setUserNickName(String userNickName) {
        UserNickName = userNickName;
    }

    public String getDOB() {
        return DOB;
    }

    public void setDOB(String DOB) {
        this.DOB = DOB;
    }

    public int getGender() {
        return Gender;
    }

    public void setGender(int gender) {
        Gender = gender;
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

    public int getCountryID() {
        return CountryID;
    }

    public void setCountryID(int countryID) {
        CountryID = countryID;
    }

    public int getProvinceID() {
        return ProvinceID;
    }

    public void setProvinceID(int provinceID) {
        ProvinceID = provinceID;
    }

    public int getCityID() {
        return CityID;
    }

    public void setCityID(int cityID) {
        CityID = cityID;
    }

    public int getDistrictID() {
        return DistrictID;
    }

    public void setDistrictID(int districtID) {
        DistrictID = districtID;
    }

    public int getSystemSetting() {
        return SystemSetting;
    }

    public void setSystemSetting(int systemSetting) {
        SystemSetting = systemSetting;
    }

    public String getInvitationCode() {
        return InvitationCode;
    }

    public void setInvitationCode(String invitationCode) {
        InvitationCode = invitationCode;
    }

    public String getUpdatedTime() {
        return UpdatedTime;
    }

    public void setUpdatedTime(String updatedTime) {
        UpdatedTime = updatedTime;
    }

    public String getCreatedTime() {
        return CreatedTime;
    }

    public void setCreatedTime(String createdTime) {
        CreatedTime = createdTime;
    }

    public int getDeviceID() {
        return DeviceID;
    }

    public void setDeviceID(int deviceID) {
        DeviceID = deviceID;
    }

    public String getServiceToken() {
        return ServiceToken;
    }

    public void setServiceToken(String serviceToken) {
        ServiceToken = serviceToken;
    }
}
