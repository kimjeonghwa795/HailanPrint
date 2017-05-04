package com.hailan.HaiLanPrint.models;

import android.databinding.BaseObservable;
import android.databinding.Bindable;
import android.os.Parcel;
import android.os.Parcelable;

import com.hailan.HaiLanPrint.BR;

import java.util.List;

/**
 * Created by yoghourt on 5/24/16.
 */

public class OrderWork extends BaseObservable implements Parcelable {

    private String CreateTime;

    private int OrderCount;

    private int OrderID;

    private List<OrderWorkPhoto> OrderWorkPhotos;

    private int PhotoCount;

    private int PhotoCover;

    private double Price;

    private int TemplateID;

    private String TemplateName;

    private int TypeID;

    private String TypeName;

    private String TypeTitle;

    private String WorkFormat;

    private String WorkMaterial;

    private int WorkOID;

    private String WorkStyle;

    public OrderWork() {
        WorkMaterial = "";
        WorkFormat = "";
        WorkStyle = "";
    }

    protected OrderWork(Parcel in) {
        CreateTime = in.readString();
        OrderCount = in.readInt();
        OrderID = in.readInt();
        OrderWorkPhotos = in.createTypedArrayList(OrderWorkPhoto.CREATOR);
        PhotoCount = in.readInt();
        PhotoCover = in.readInt();
        Price = in.readDouble();
        TemplateID = in.readInt();
        TemplateName = in.readString();
        TypeID = in.readInt();
        TypeName = in.readString();
        TypeTitle = in.readString();
        WorkFormat = in.readString();
        WorkMaterial = in.readString();
        WorkOID = in.readInt();
        WorkStyle = in.readString();
    }

    public static final Creator<OrderWork> CREATOR = new Creator<OrderWork>() {
        @Override
        public OrderWork createFromParcel(Parcel in) {
            return new OrderWork(in);
        }

        @Override
        public OrderWork[] newArray(int size) {
            return new OrderWork[size];
        }
    };

    public String getCreateTime() {
        return CreateTime;
    }

    public void setCreateTime(String createTime) {
        CreateTime = createTime;
    }

    @Bindable
    public int getOrderCount() {
        return OrderCount;
    }

    public void setOrderCount(int orderCount) {
        OrderCount = orderCount;
        notifyPropertyChanged(BR.orderCount);
    }

    public int getOrderID() {
        return OrderID;
    }

    public void setOrderID(int orderID) {
        OrderID = orderID;
    }

    public List<OrderWorkPhoto> getOrderWorkPhotos() {
        return OrderWorkPhotos;
    }

    public void setOrderWorkPhotos(List<OrderWorkPhoto> orderWorkPhotos) {
        OrderWorkPhotos = orderWorkPhotos;
    }

    public int getPhotoCount() {
        return PhotoCount;
    }

    public void setPhotoCount(int photoCount) {
        PhotoCount = photoCount;
    }

    public int getPhotoCover() {
        return PhotoCover;
    }

    public void setPhotoCover(int photoCover) {
        PhotoCover = photoCover;
    }

    public double getPrice() {
        return Price;
    }

    public void setPrice(double price) {
        Price = price;
    }

    public int getTemplateID() {
        return TemplateID;
    }

    public void setTemplateID(int templateID) {
        TemplateID = templateID;
    }

    public String getTemplateName() {
        return TemplateName;
    }

    public void setTemplateName(String templateName) {
        TemplateName = templateName;
    }

    public int getTypeID() {
        return TypeID;
    }

    public void setTypeID(int typeID) {
        TypeID = typeID;
    }

    public String getTypeName() {
        return TypeName;
    }

    public void setTypeName(String typeName) {
        TypeName = typeName;
    }

    public String getTypeTitle() {
        return TypeTitle;
    }

    public void setTypeTitle(String typeTitle) {
        TypeTitle = typeTitle;
    }

    public String getWorkFormat() {
        return WorkFormat;
    }

    public void setWorkFormat(String workFormat) {
        WorkFormat = workFormat;
    }

    public String getWorkMaterial() {
        return WorkMaterial;
    }

    public void setWorkMaterial(String workMaterial) {
        WorkMaterial = workMaterial;
    }

    public int getWorkOID() {
        return WorkOID;
    }

    public void setWorkOID(int workOID) {
        WorkOID = workOID;
    }

    public String getWorkStyle() {
        return WorkStyle;
    }

    public void setWorkStyle(String workStyle) {
        WorkStyle = workStyle;
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(CreateTime);
        dest.writeInt(OrderCount);
        dest.writeInt(OrderID);
        dest.writeTypedList(OrderWorkPhotos);
        dest.writeInt(PhotoCount);
        dest.writeInt(PhotoCover);
        dest.writeDouble(Price);
        dest.writeInt(TemplateID);
        dest.writeString(TemplateName);
        dest.writeInt(TypeID);
        dest.writeString(TypeName);
        dest.writeString(TypeTitle);
        dest.writeString(WorkFormat);
        dest.writeString(WorkMaterial);
        dest.writeInt(WorkOID);
        dest.writeString(WorkStyle);
    }

    @Override
    public String toString() {
        return "OrderWork{" +
                "CreateTime='" + CreateTime + '\'' +
                ", OrderCount=" + OrderCount +
                ", OrderID=" + OrderID +
                ", OrderWorkPhotos=" + OrderWorkPhotos +
                ", PhotoCount=" + PhotoCount +
                ", PhotoCover=" + PhotoCover +
                ", Price=" + Price +
                ", TemplateID=" + TemplateID +
                ", TemplateName='" + TemplateName + '\'' +
                ", TypeID=" + TypeID +
                ", TypeName='" + TypeName + '\'' +
                ", TypeTitle='" + TypeTitle + '\'' +
                ", WorkFormat='" + WorkFormat + '\'' +
                ", WorkMaterial='" + WorkMaterial + '\'' +
                ", WorkOID=" + WorkOID +
                ", WorkStyle='" + WorkStyle + '\'' +
                '}';
    }
}
