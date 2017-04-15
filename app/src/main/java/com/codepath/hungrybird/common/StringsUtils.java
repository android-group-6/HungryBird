package com.codepath.hungrybird.common;

import com.codepath.hungrybird.model.Order;

/**
 * Created by gauravb on 4/14/17.
 */

public class StringsUtils {
    public String displayStatusString(Order order) {
        String ret = null;
        if (Order.Status.NOT_ORDERED.name().equals(order.getStatus())) {
            ret = "Not Ordered";
        } else if (Order.Status.DONE.name().equals(order.getStatus())) {
            ret = "Completed";
        } else if (Order.Status.IN_PROGRESS.name().equals(order.getStatus())) {
            ret = "In Progress";
        } else if (Order.Status.ORDERED.name().equals(order.getStatus())) {
            ret = "Ordered";
        } else if (Order.Status.OUT_FOR_DELIVERY.name().equals(order.getStatus())) {
            ret = "Out for Delivery";
        } else if (Order.Status.READY_FOR_PICKUP.name().equals(order.getStatus())) {
            ret = "Ready For Pickup";
        } else if (Order.Status.CANCELLED.name().equals(order.getStatus())) {
            ret = "Cancelled";
        }
        return ret;
    }

}