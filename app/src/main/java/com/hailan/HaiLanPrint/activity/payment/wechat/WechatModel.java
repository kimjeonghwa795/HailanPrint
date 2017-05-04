package com.hailan.HaiLanPrint.activity.payment.wechat;

public class WechatModel {

    public static final int PAY_TYPE_1 = 1; // 客户端获取prepayId
    public static final int PAY_TYPE_2 = 2; // 服务端获取prepayId

    private int payType = 1;

    private String prepayId;  // 服务端获取加密的prepayId
    private String totalFee;  // 总费用
    private String notifyUrl; // 回调URL
    private String body;
    private String nonceStr;  // 服务端获取加密的随机数字符串
    private String timeStamp; // 服务端获取的交易时间戳
    private String appSign;   // 服务端获取加密的订单信息

    public String getPrepayId() {
        return prepayId;
    }

    public void setPrepayId(String prepayId) {
        this.prepayId = prepayId;
    }

    public int getPayType() {
        return payType;
    }

    public void setPayType(int payType) {
        this.payType = payType;
    }

    public String getNotifyUrl() {
        return notifyUrl;
    }

    public void setNotifyUrl(String notifyUrl) {
        this.notifyUrl = notifyUrl;
    }

    public String getTotalFee() {
        return totalFee;
    }

    public void setTotalFee(String totalFee) {
        this.totalFee = totalFee;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public String getNonceStr() {
        return nonceStr;
    }

    public void setNonceStr(String nonceStr) {
        this.nonceStr = nonceStr;
    }

    public String getTimeStamp() {
        return timeStamp;
    }

    public void setTimeStamp(String timeStamp) {
        this.timeStamp = timeStamp;
    }

    public String getAppSign() {
        return appSign;
    }

    public void setAppSign(String appSign) {
        this.appSign = appSign;
    }

    @Override
    public String toString() {
        return "WechatModel{" +
                "payType=" + payType +
                ", prepayId='" + prepayId + '\'' +
                ", totalFee='" + totalFee + '\'' +
                ", notifyUrl='" + notifyUrl + '\'' +
                ", body='" + body + '\'' +
                ", nonceStr='" + nonceStr + '\'' +
                ", timeStamp='" + timeStamp + '\'' +
                ", appSign='" + appSign + '\'' +
                '}';
    }
}
