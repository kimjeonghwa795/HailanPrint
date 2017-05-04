package com.hailan.HaiLanPrint.models.wechat;

/**
 * Author : zhouyx
 * Date   : 2016/12/9
 * Description :
 */
public class WechatPayResult {

    private String AppID;
    private String NonceStr;
    private int OrderID;
    private String Package;
    private String PartnerID;
    private String PrepayID;
    private String Sign;
    private String TimeStamp;

    public String getAppID() {
        return AppID;
    }

    public void setAppID(String AppID) {
        this.AppID = AppID;
    }

    public String getNonceStr() {
        return NonceStr;
    }

    public void setNonceStr(String NonceStr) {
        this.NonceStr = NonceStr;
    }

    public int getOrderID() {
        return OrderID;
    }

    public void setOrderID(int OrderID) {
        this.OrderID = OrderID;
    }

    public String getPackage() {
        return Package;
    }

    public void setPackage(String Package) {
        this.Package = Package;
    }

    public String getPartnerID() {
        return PartnerID;
    }

    public void setPartnerID(String PartnerID) {
        this.PartnerID = PartnerID;
    }

    public String getPrepayID() {
        return PrepayID;
    }

    public void setPrepayID(String PrepayID) {
        this.PrepayID = PrepayID;
    }

    public String getSign() {
        return Sign;
    }

    public void setSign(String Sign) {
        this.Sign = Sign;
    }

    public String getTimeStamp() {
        return TimeStamp;
    }

    public void setTimeStamp(String TimeStamp) {
        this.TimeStamp = TimeStamp;
    }
}
