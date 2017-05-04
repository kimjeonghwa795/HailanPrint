package com.hailan.HaiLanPrint.enumobject;

import android.content.Context;

import com.hailan.HaiLanPrint.R;


/**
 * Created by yoghourt on 5/16/16.
 */
public enum OrderStatus {

    All(-1),            // 全部
    UnPaid(0),          // 未支付
    Paid(1),            // 已付款
    Delivered(2),       // 已发货
    Finished(4),        // 已完成
    Refunding(8),       // 退款中
    Refunded(16),       // 已退款
    RefundFail(32),     // 退款失败
    AllRefund(56);      // 所有退款订单(退款中 + 已退款 + 退款失败 = (8 + 16 + 32 = 56))

    private int value;

    private OrderStatus(int product) {
        this.value = product;
    }

    public int value() {
        return value;
    }

    public static OrderStatus fromValue(int value) {
        for (OrderStatus type : OrderStatus.values()) {
            if (type.value() == value) {
                return type;
            }
        }
     return null;
    }

    public static String getOrderStatus(Context context, OrderStatus orderStatus) {
        switch (orderStatus) {
            case UnPaid:
                return context.getString(R.string.not_paid);
            case Paid:
                return context.getString(R.string.already_paid);
            case Delivered:
                return context.getString(R.string.already_delivered);
            case Finished:
                return context.getString(R.string.already_finished);
            case Refunding:
                return context.getString(R.string.refunding);
            case Refunded:
                return context.getString(R.string.already_refunded);
            case RefundFail:
                return context.getString(R.string.refundFail);

        }
        return "";
    }
}
