package com.hailan.HaiLanPrint.models;

import java.io.Serializable;

/**
 * Created by PC on 2016-06-13.
 */

public class UserKid implements Serializable {
    
    private int AutoID;
    public String Class;
    private String DOB;
    private String Name;
    private String School;
    private String UpdatedTime;
    private int UserID;

    public int getAutoID() {
        return AutoID;
    }

    public void setAutoID(int autoID) {
        AutoID = autoID;
    }

    public void setClass(String aClass) {
        Class = aClass;
    }

    public String getDOB() {
        return DOB;
    }

    public void setDOB(String DOB) {
        this.DOB = DOB;
    }

    public String getName() {
        return Name;
    }

    public void setName(String name) {
        Name = name;
    }

    public String getSchool() {
        return School;
    }

    public void setSchool(String school) {
        School = school;
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
}
