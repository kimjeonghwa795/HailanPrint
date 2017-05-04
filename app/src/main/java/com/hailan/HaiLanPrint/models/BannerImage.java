package com.hailan.HaiLanPrint.models;

public class BannerImage implements Comparable<BannerImage> {

    private int PhotoID;
    private int PhotoSID;
    private int Sort;

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

    public int getSort() {
        return Sort;
    }

    public void setSort(int sort) {
        Sort = sort;
    }

    @Override
    public int compareTo(BannerImage another) {
        return (this.getSort() - another.getSort());
    }
}
