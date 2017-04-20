package com.codepath.hungrybird.model;

import com.parse.ParseClassName;
import com.parse.ParseObject;

/**
 * Created by ajasuja on 4/4/17.
 */
@ParseClassName("Order")
public class Order extends ParseObject {
    private String shortDate;
    private String displayStatus;

    public String getDisplayStatus() {
        return displayStatus;
    }

    public void setDisplayStatus(String displayStatus) {
        this.displayStatus = displayStatus;
    }

    public String getDisplayId() {
        return "HB-" + getObjectId();
    }

    public void setShortDate(String shortDate) {
        this.shortDate = shortDate;
    }

    public String getShortDate() {
        return shortDate;
    }

    public String getOrderName() {
        return getString("orderName");
    }

    public void setOrderName(String orderName) {
        put("orderName", orderName);
    }

    public User getConsumer() {
        return new User(getParseUser("consumer"));

    }

    public void setConsumer(User user) {
        put("consumer", user.parseUser);
    }

    public User getChef() {
        return new User(getParseUser("chef"));
    }

    public void setChef(User user) {
        put("chef", user.parseUser);
    }

    public String getStatus() {
        return getString("status");
    }

    public void setStatus(String status) {
        put("status", status);
    }

    public Double getTotalPayment() {
        return getDouble("totalPayment");
    }

    public void setTotalPayment(Double totalPayment) {
        put("totalPayment", totalPayment);
    }

    public Double getShippingFee() {
        return getDouble("shippingFee");
    }

    public void setShippingFee(Double totalTax) {
        put("shippingFee", totalTax);
    }

    public String getPaymentType() {
        return getString("paymentType");
    }

    public void setPaymentType(String paymentType) {
        put("paymentType", paymentType);
    }

    public String getDeliveryAddress() {
        return getString("deliveryAddress");
    }

    public void setDeliveryAddress(String deliveryAddress) {
        put("deliveryAddress", deliveryAddress);
    }

    public boolean isDelivery() {
        return getBoolean("delivery");
    }

    public void setDelivery(boolean delivery) {
        put("delivery" , delivery);
    }

    public String getDeliveryQuoteId() {
        return getString("deliveryQuoteId");
    }

    public void setDeliveryQuoteId(String deliveryQuoteId) {
        put("deliveryQuoteId", deliveryQuoteId);
    }

    public String getDeliveryId() {
        return getString("deliveryId");
    }

    public void setDeliveryId(String deliveryId) {
        put("deliveryId", deliveryId);
    }

    public void setDeliveryFee(double deliveryFee) {
        put("deliveryFee", deliveryFee);
    }

    public Double getDeliveryFee() {
        return getDouble("deliveryFee");
    }
    public enum Status {
        NOT_ORDERED("NOT_ORDERED"),
        ORDERED("ORDERED"),
        IN_PROGRESS("IN_PROGRESS"),
        READY_FOR_PICKUP("READY_FOR_PICKUP"),
        OUT_FOR_DELIVERY("OUT_FOR_DELIVERY"),
        COMPLETE("COMPLETE"),
        CANCELLED("CANCELLED");

        private String statusValue;

        Status(String statusValue) {
            this.statusValue = statusValue;
        }

        public String getStatusValue() {
            return this.statusValue;
        }
    }
}
