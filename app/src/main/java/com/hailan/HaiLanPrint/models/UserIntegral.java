package com.hailan.HaiLanPrint.models;

/**
 * Created by PC on 2016-06-08.
 */

public class UserIntegral {
    private String Amount;
    private long AutoID;
    private String CreatedTime;
    private String Description;
    private long UserID;

    public String getAmount() {
        return Amount;
    }

    public void setAmount(String amount) {
        Amount = amount;
    }

    public long getAutoID() {
        return AutoID;
    }

    public void setAutoID(long autoID) {
        AutoID = autoID;
    }

    public String getCreatedTime() {
        return CreatedTime;
    }

    public void setCreatedTime(String createdTime) {
        CreatedTime = createdTime;
    }

    public String getDescription() {
        return Description;
    }

    public void setDescription(String description) {
        Description = description;
    }

    public long getUserID() {
        return UserID;
    }

    public void setUserID(long userID) {
        UserID = userID;
    }
}
