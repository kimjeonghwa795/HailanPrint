package com.hailan.HaiLanPrint.models;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by PC on 2016-06-20.
 */

public class Message implements Parcelable {

    private int AutoID;
    private String CreatedTime;
    private String Message;
    private String Title;
    private String type;

    public Message() {
    }

    protected Message(Parcel in) {
        AutoID = in.readInt();
        CreatedTime = in.readString();
        Message = in.readString();
        Title = in.readString();
    }

    public static final Creator<Message> CREATOR = new Creator<Message>() {
        @Override
        public Message createFromParcel(Parcel in) {
            return new Message(in);
        }

        @Override
        public Message[] newArray(int size) {
            return new Message[size];
        }
    };

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

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(AutoID);
        dest.writeString(CreatedTime);
        dest.writeString(Message);
        dest.writeString(Title);
    }

    public boolean isMessageType(){
        return "消息中心".equals(getType());
    }

    public boolean isCreditType(){
        return "积分赠送".equals(getType());
    }

    public boolean isCouponType(){
        return "优惠券".equals(getType());
    }

    @Override
    public String toString() {
        return "Message{" +
                "AutoID=" + AutoID +
                ", CreatedTime='" + CreatedTime + '\'' +
                ", Message='" + Message + '\'' +
                ", Title='" + Title + '\'' +
                ", type='" + type + '\'' +
                '}';
    }
}
