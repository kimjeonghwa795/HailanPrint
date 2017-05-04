package com.hailan.HaiLanPrint.models;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by yoghourt on 6/2/16.
 */

public class OrderInfo implements Parcelable {

    private int AddressID;

    private String AddressReceipt;

    private int AgentCommissionAmount;

    private int CouponFee;

    private int CouponID;

    private String CreatedTime;

    private int DeliveryFee;

    private String ExpressCompany;

    private String ExpressNo;

    private int IntegralFee;

    private String IntegralFeeString;

    private int OrderID;

    private String OrderNo;

    private int OrderStatus;

    private int PayType;

    private String Phone;

    private String PrepayID;

    private String Receiver;

    private String RefundDetail;

    private String RefundedTime;

    private int RefundFee;

    private String RefundID;

    private String RefundNo;

    private int RefundPhoto1ID;

    private int RefundPhoto1SID;

    private int RefundPhoto2ID;

    private int RefundPhoto2SID;

    private int RefundPhoto3ID;

    private int RefundPhoto3SID;

    private String RefundReason;

    private int SaleID;

    private int SalesCommissionAmount;

    private int TotalFee;

    private int TotalFeeReal;

    private String TradeExpriedTime;

    private String TradeTime;

    private String TransactionID;

    private int TypeID;

    private int UserID;

    public int getAddressID() {
        return AddressID;
    }

    public void setAddressID(int addressID) {
        AddressID = addressID;
    }

    public String getAddressReceipt() {
        return AddressReceipt;
    }

    public void setAddressReceipt(String addressReceipt) {
        AddressReceipt = addressReceipt;
    }

    public int getAgentCommissionAmount() {
        return AgentCommissionAmount;
    }

    public void setAgentCommissionAmount(int agentCommissionAmount) {
        AgentCommissionAmount = agentCommissionAmount;
    }

    public int getCouponFee() {
        return CouponFee;
    }

    public void setCouponFee(int couponFee) {
        CouponFee = couponFee;
    }

    public int getCouponID() {
        return CouponID;
    }

    public void setCouponID(int couponID) {
        CouponID = couponID;
    }

    public String getCreatedTime() {
        return CreatedTime;
    }

    public void setCreatedTime(String createdTime) {
        CreatedTime = createdTime;
    }

    public int getDeliveryFee() {
        return DeliveryFee;
    }

    public void setDeliveryFee(int deliveryFee) {
        DeliveryFee = deliveryFee;
    }

    public String getExpressCompany() {
        return ExpressCompany;
    }

    public void setExpressCompany(String expressCompany) {
        ExpressCompany = expressCompany;
    }

    public String getExpressNo() {
        return ExpressNo;
    }

    public void setExpressNo(String expressNo) {
        ExpressNo = expressNo;
    }

    public int getIntegralFee() {
        return IntegralFee;
    }

    public void setIntegralFee(int integralFee) {
        IntegralFee = integralFee;
    }

    public String getIntegralFeeString() {
        return IntegralFeeString;
    }

    public void setIntegralFeeString(String integralFeeString) {
        IntegralFeeString = integralFeeString;
    }

    public int getOrderID() {
        return OrderID;
    }

    public void setOrderID(int orderID) {
        OrderID = orderID;
    }

    public String getOrderNo() {
        return OrderNo;
    }

    public void setOrderNo(String orderNo) {
        OrderNo = orderNo;
    }

    public int getOrderStatus() {
        return OrderStatus;
    }

    public void setOrderStatus(int orderStatus) {
        OrderStatus = orderStatus;
    }

    public int getPayType() {
        return PayType;
    }

    public void setPayType(int payType) {
        PayType = payType;
    }

    public String getPhone() {
        return Phone;
    }

    public void setPhone(String phone) {
        Phone = phone;
    }

    public String getPrepayID() {
        return PrepayID;
    }

    public void setPrepayID(String prepayID) {
        PrepayID = prepayID;
    }

    public String getReceiver() {
        return Receiver;
    }

    public void setReceiver(String receiver) {
        Receiver = receiver;
    }

    public String getRefundDetail() {
        return RefundDetail;
    }

    public void setRefundDetail(String refundDetail) {
        RefundDetail = refundDetail;
    }

    public String getRefundedTime() {
        return RefundedTime;
    }

    public void setRefundedTime(String refundedTime) {
        RefundedTime = refundedTime;
    }

    public int getRefundFee() {
        return RefundFee;
    }

    public void setRefundFee(int refundFee) {
        RefundFee = refundFee;
    }

    public String getRefundID() {
        return RefundID;
    }

    public void setRefundID(String refundID) {
        RefundID = refundID;
    }

    public String getRefundNo() {
        return RefundNo;
    }

    public void setRefundNo(String refundNo) {
        RefundNo = refundNo;
    }

    public int getRefundPhoto1ID() {
        return RefundPhoto1ID;
    }

    public void setRefundPhoto1ID(int refundPhoto1ID) {
        RefundPhoto1ID = refundPhoto1ID;
    }

    public int getRefundPhoto1SID() {
        return RefundPhoto1SID;
    }

    public void setRefundPhoto1SID(int refundPhoto1SID) {
        RefundPhoto1SID = refundPhoto1SID;
    }

    public int getRefundPhoto2ID() {
        return RefundPhoto2ID;
    }

    public void setRefundPhoto2ID(int refundPhoto2ID) {
        RefundPhoto2ID = refundPhoto2ID;
    }

    public int getRefundPhoto2SID() {
        return RefundPhoto2SID;
    }

    public void setRefundPhoto2SID(int refundPhoto2SID) {
        RefundPhoto2SID = refundPhoto2SID;
    }

    public int getRefundPhoto3ID() {
        return RefundPhoto3ID;
    }

    public void setRefundPhoto3ID(int refundPhoto3ID) {
        RefundPhoto3ID = refundPhoto3ID;
    }

    public int getRefundPhoto3SID() {
        return RefundPhoto3SID;
    }

    public void setRefundPhoto3SID(int refundPhoto3SID) {
        RefundPhoto3SID = refundPhoto3SID;
    }

    public String getRefundReason() {
        return RefundReason;
    }

    public void setRefundReason(String refundReason) {
        RefundReason = refundReason;
    }

    public int getSaleID() {
        return SaleID;
    }

    public void setSaleID(int saleID) {
        SaleID = saleID;
    }

    public int getSalesCommissionAmount() {
        return SalesCommissionAmount;
    }

    public void setSalesCommissionAmount(int salesCommissionAmount) {
        SalesCommissionAmount = salesCommissionAmount;
    }

    public int getTotalFee() {
        return TotalFee;
    }

    public void setTotalFee(int totalFee) {
        TotalFee = totalFee;
    }

    public int getTotalFeeReal() {
        return TotalFeeReal;
    }

    public void setTotalFeeReal(int totalFeeReal) {
        TotalFeeReal = totalFeeReal;
    }

    public String getTradeExpriedTime() {
        return TradeExpriedTime;
    }

    public void setTradeExpriedTime(String tradeExpriedTime) {
        TradeExpriedTime = tradeExpriedTime;
    }

    public String getTradeTime() {
        return TradeTime;
    }

    public void setTradeTime(String tradeTime) {
        TradeTime = tradeTime;
    }

    public String getTransactionID() {
        return TransactionID;
    }

    public void setTransactionID(String transactionID) {
        TransactionID = transactionID;
    }

    public int getTypeID() {
        return TypeID;
    }

    public void setTypeID(int typeID) {
        TypeID = typeID;
    }

    public int getUserID() {
        return UserID;
    }

    public void setUserID(int userID) {
        UserID = userID;
    }

    protected OrderInfo(Parcel in) {
        AddressID = in.readInt();
        AddressReceipt = in.readString();
        AgentCommissionAmount = in.readInt();
        CouponFee = in.readInt();
        CouponID = in.readInt();
        CreatedTime = in.readString();
        DeliveryFee = in.readInt();
        ExpressCompany = in.readString();
        ExpressNo = in.readString();
        IntegralFee = in.readInt();
        OrderID = in.readInt();
        OrderNo = in.readString();
        OrderStatus = in.readInt();
        PayType = in.readInt();
        Phone = in.readString();
        PrepayID = in.readString();
        Receiver = in.readString();
        RefundDetail = in.readString();
        RefundedTime = in.readString();
        RefundFee = in.readInt();
        RefundID = in.readString();
        RefundNo = in.readString();
        RefundPhoto1ID = in.readInt();
        RefundPhoto1SID = in.readInt();
        RefundPhoto2ID = in.readInt();
        RefundPhoto2SID = in.readInt();
        RefundPhoto3ID = in.readInt();
        RefundPhoto3SID = in.readInt();
        RefundReason = in.readString();
        SaleID = in.readInt();
        SalesCommissionAmount = in.readInt();
        TotalFee = in.readInt();
        TotalFeeReal = in.readInt();
        TradeExpriedTime = in.readString();
        TradeTime = in.readString();
        TransactionID = in.readString();
        TypeID = in.readInt();
        UserID = in.readInt();
        IntegralFeeString = in.readString();
    }

    public static final Creator<OrderInfo> CREATOR = new Creator<OrderInfo>() {
        @Override
        public OrderInfo createFromParcel(Parcel in) {
            return new OrderInfo(in);
        }

        @Override
        public OrderInfo[] newArray(int size) {
            return new OrderInfo[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(AddressID);
        dest.writeString(AddressReceipt);
        dest.writeInt(AgentCommissionAmount);
        dest.writeInt(CouponFee);
        dest.writeInt(CouponID);
        dest.writeString(CreatedTime);
        dest.writeInt(DeliveryFee);
        dest.writeString(ExpressCompany);
        dest.writeString(ExpressNo);
        dest.writeInt(IntegralFee);
        dest.writeInt(OrderID);
        dest.writeString(OrderNo);
        dest.writeInt(OrderStatus);
        dest.writeInt(PayType);
        dest.writeString(Phone);
        dest.writeString(PrepayID);
        dest.writeString(Receiver);
        dest.writeString(RefundDetail);
        dest.writeString(RefundedTime);
        dest.writeInt(RefundFee);
        dest.writeString(RefundID);
        dest.writeString(RefundNo);
        dest.writeInt(RefundPhoto1ID);
        dest.writeInt(RefundPhoto1SID);
        dest.writeInt(RefundPhoto2ID);
        dest.writeInt(RefundPhoto2SID);
        dest.writeInt(RefundPhoto3ID);
        dest.writeInt(RefundPhoto3SID);
        dest.writeString(RefundReason);
        dest.writeInt(SaleID);
        dest.writeInt(SalesCommissionAmount);
        dest.writeInt(TotalFee);
        dest.writeInt(TotalFeeReal);
        dest.writeString(TradeExpriedTime);
        dest.writeString(TradeTime);
        dest.writeString(TransactionID);
        dest.writeInt(TypeID);
        dest.writeInt(UserID);
        dest.writeString(IntegralFeeString);
    }
}
