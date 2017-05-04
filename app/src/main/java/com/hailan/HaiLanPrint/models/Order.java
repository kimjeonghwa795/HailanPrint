package com.hailan.HaiLanPrint.models;

import java.util.ArrayList;

/**
 * Created by yoghourt on 6/2/16.
 */

public class Order {

    private OrderInfo OrderInfo;

    private ArrayList<OrderWork> OrderWorkList;

    public OrderInfo getOrderInfo() {
        return OrderInfo;
    }

    public void setOrderInfo(OrderInfo orderInfo) {
        OrderInfo = orderInfo;
    }

    public ArrayList<OrderWork> getOrderWorkList() {
        return OrderWorkList;
    }

    public void setOrderWorkList(ArrayList<OrderWork> orderWorkList) {
        OrderWorkList = orderWorkList;
    }
}
