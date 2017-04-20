package com.codepath.hungrybird.model.postmates;

/**
 * Created by ajasuja on 4/19/17.
 */

public class DeliveryQuoteRequest {

    private String pickUpAddress;
    private String dropOffAddress;

    public DeliveryQuoteRequest(String pickUpAddress, String dropOffAddress) {
        this.pickUpAddress = pickUpAddress;
        this.dropOffAddress = dropOffAddress;
    }

    public String getPickUpAddress() {
        return pickUpAddress;
    }

    public void setPickUpAddress(String pickUpAddress) {
        this.pickUpAddress = pickUpAddress;
    }

    public String getDropOffAddress() {
        return dropOffAddress;
    }

    public void setDropOffAddress(String dropOffAddress) {
        this.dropOffAddress = dropOffAddress;
    }
}
