package com.hailan.HaiLanPrint.models;

import android.databinding.BaseObservable;
import android.databinding.Bindable;

import com.hailan.HaiLanPrint.BR;

import java.io.Serializable;
import java.util.List;

import kotlin.jvm.Transient;


/**
 * Created by yoghourt on 5/25/16.
 */
public class Template extends BaseObservable implements Serializable {

    private String MaterialDesc;

    private int MaterialType;

    private String MaterialTypeString;

    private int PageCount;

    private int ParentID;

    private int Price;

    private int Price2;

    private int Price3;

    private int Price4;

    private String PriceString;

    private int Status;

    private String StatusString;

    private int TemplateID;

    private String TemplateName;

    private String TemplateType;

    private String TypeDesc;

    private int TypeDescID;

    private int TypeID;

    private String UpdatedTime;

    @Transient
    private String selectMaterial;

    private String TemplatePhoto;

    private List<?> CoverList;

    private List<PropertyListBean> PropertyList;

    private int PhotoCover;

    private List<MaterialListBean> MaterialList;

    private int PhotoCount;

    public String getMaterialDesc() {
        return MaterialDesc;
    }

    public void setMaterialDesc(String materialDesc) {
        MaterialDesc = materialDesc;
    }

    public int getMaterialType() {
        return MaterialType;
    }

    public void setMaterialType(int materialType) {
        MaterialType = materialType;
    }

    public String getMaterialTypeString() {
        return MaterialTypeString;
    }

    public void setMaterialTypeString(String materialTypeString) {
        MaterialTypeString = materialTypeString;
    }

    @Bindable
    public int getPageCount() {
        return PageCount;
    }

    public void setPageCount(int pageCount) {
        PageCount = pageCount;
        notifyPropertyChanged(BR.pageCount);
    }

    public int getParentID() {
        return ParentID;
    }

    public void setParentID(int parentID) {
        ParentID = parentID;
    }

    public int getPrice() {
        return Price;
    }

    public void setPrice(int price) {
        Price = price;
    }

    public int getPrice2() {
        return Price2;
    }

    public void setPrice2(int price2) {
        Price2 = price2;
    }

    public int getPrice3() {
        return Price3;
    }

    public void setPrice3(int price3) {
        Price3 = price3;
    }

    public int getPrice4() {
        return Price4;
    }

    public void setPrice4(int price4) {
        Price4 = price4;
    }

    public String getPriceString() {
        return PriceString;
    }

    public void setPriceString(String priceString) {
        PriceString = priceString;
    }

    public int getStatus() {
        return Status;
    }

    public void setStatus(int status) {
        Status = status;
    }

    public String getStatusString() {
        return StatusString;
    }

    public void setStatusString(String statusString) {
        StatusString = statusString;
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

    public String getTemplateType() {
        return TemplateType;
    }

    public void setTemplateType(String templateType) {
        TemplateType = templateType;
    }

    public String getTypeDesc() {
        return TypeDesc;
    }

    public void setTypeDesc(String typeDesc) {
        TypeDesc = typeDesc;
    }

    public int getTypeDescID() {
        return TypeDescID;
    }

    public void setTypeDescID(int typeDescID) {
        TypeDescID = typeDescID;
    }

    public int getTypeID() {
        return TypeID;
    }

    public void setTypeID(int typeID) {
        TypeID = typeID;
    }

    public String getUpdatedTime() {
        return UpdatedTime;
    }

    public void setUpdatedTime(String updatedTime) {
        UpdatedTime = updatedTime;
    }

    public String getSelectMaterial() {
        return selectMaterial;
    }

    public void setSelectMaterial(String selectMaterial) {
        this.selectMaterial = selectMaterial;
    }

    public String getTemplatePhoto() {
        return TemplatePhoto;
    }

    public void setTemplatePhoto(String TemplatePhoto) {
        this.TemplatePhoto = TemplatePhoto;
    }

    public List<?> getCoverList() {
        return CoverList;
    }

    public void setCoverList(List<?> CoverList) {
        this.CoverList = CoverList;
    }

    public List<PropertyListBean> getPropertyList() {
        return PropertyList;
    }

    public void setPropertyList(List<PropertyListBean> PropertyList) {
        this.PropertyList = PropertyList;
    }

    public int getPhotoCover() {
        return PhotoCover;
    }

    public void setPhotoCover(int photoCover) {
        PhotoCover = photoCover;
    }

    public List<MaterialListBean> getMaterialList() {
        return MaterialList;
    }

    public void setMaterialList(List<MaterialListBean> materialList) {
        MaterialList = materialList;
    }

    public int getPhotoCount() {
        return PhotoCount;
    }

    public void setPhotoCount(int photoCount) {
        PhotoCount = photoCount;
    }

    public static class PropertyListBean extends BaseObservable implements Serializable {

        /**
         * ItemExp1 : null
         * ItemExp2 : null
         * ItemExp3 : null
         * MaterialID : 10
         * MaterialName : 水晶
         * Price : 230
         * TemplateID : 126
         * TemplatePropertyID : 66
         * TypeDesc : 6寸
         */

        private Object ItemExp1;
        private Object ItemExp2;
        private Object ItemExp3;
        private int MaterialID;
        private String MaterialName;
        private int Price;
        private int TemplateID;
        private int TemplatePropertyID;
        private String TypeDesc;

        public Object getItemExp1() {
            return ItemExp1;
        }

        public void setItemExp1(Object ItemExp1) {
            this.ItemExp1 = ItemExp1;
        }

        public Object getItemExp2() {
            return ItemExp2;
        }

        public void setItemExp2(Object ItemExp2) {
            this.ItemExp2 = ItemExp2;
        }

        public Object getItemExp3() {
            return ItemExp3;
        }

        public void setItemExp3(Object ItemExp3) {
            this.ItemExp3 = ItemExp3;
        }

        public int getMaterialID() {
            return MaterialID;
        }

        public void setMaterialID(int MaterialID) {
            this.MaterialID = MaterialID;
        }

        public String getMaterialName() {
            return MaterialName;
        }

        public void setMaterialName(String MaterialName) {
            this.MaterialName = MaterialName;
        }

        public int getPrice() {
            return Price;
        }

        public void setPrice(int Price) {
            this.Price = Price;
        }

        public int getTemplateID() {
            return TemplateID;
        }

        public void setTemplateID(int TemplateID) {
            this.TemplateID = TemplateID;
        }

        public int getTemplatePropertyID() {
            return TemplatePropertyID;
        }

        public void setTemplatePropertyID(int TemplatePropertyID) {
            this.TemplatePropertyID = TemplatePropertyID;
        }

        public String getTypeDesc() {
            return TypeDesc;
        }

        public void setTypeDesc(String TypeDesc) {
            this.TypeDesc = TypeDesc;
        }
    }

    public static class MaterialListBean {

        @com.google.gson.annotations.SerializedName("MaterialDesc")
        private String MaterialDescX;
        private int MaterialID;
        private String MaterialName;
        private int Sort;
        @com.google.gson.annotations.SerializedName("TypeID")
        private int TypeIDX;

        public String getMaterialDescX() {
            return MaterialDescX;
        }

        public void setMaterialDescX(String MaterialDescX) {
            this.MaterialDescX = MaterialDescX;
        }

        public int getMaterialID() {
            return MaterialID;
        }

        public void setMaterialID(int MaterialID) {
            this.MaterialID = MaterialID;
        }

        public String getMaterialName() {
            return MaterialName;
        }

        public void setMaterialName(String MaterialName) {
            this.MaterialName = MaterialName;
        }

        public int getSort() {
            return Sort;
        }

        public void setSort(int Sort) {
            this.Sort = Sort;
        }

        public int getTypeIDX() {
            return TypeIDX;
        }

        public void setTypeIDX(int TypeIDX) {
            this.TypeIDX = TypeIDX;
        }
    }
}
