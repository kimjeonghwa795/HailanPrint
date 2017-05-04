package com.hailan.HaiLanPrint.models;

import java.io.Serializable;

/**
 * Created by PC on 2016-06-20.
 */

public class InformationInfo implements Serializable {
    private int AutoID;
    private String CreatedTime;
    private String Message;
    private String Title;

    public int getAutoID() {
        return AutoID;
    }

    public void setAutoID(int autoID) {
        AutoID = autoID;
    }

    public String getCreatedTime() {
        return CreatedTime;
    }

    public void setCreatedTime(String createdTime) {
        CreatedTime = createdTime;
    }

    public String getMessage() {
        return Message;
    }

    public void setMessage(String message) {
        Message = message;
    }

    public String getTitle() {
        return Title;
    }

    public void setTitle(String title) {
        Title = title;
    }
}
