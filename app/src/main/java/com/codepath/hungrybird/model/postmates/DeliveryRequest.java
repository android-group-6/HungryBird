package com.codepath.hungrybird.model.postmates;

/**
 * Created by ajasuja on 4/18/17.
 */

/**
 *
 quote_id:dqt_LEMqr95gikvukF
 manifest:indian food
 pickup_name:abc
 pickup_address:"20 McAllister St, San Francisco, CA"
 pickup_phone_number:111-111-1111
 dropoff_name:xyz
 dropoff_address:"101 Market St, San Francisco, CA"
 dropoff_phone_number:222-222-2222
 */
public class DeliveryRequest {

    private String quoteId;
    private String manifest;
    private String pickUpName;
    private String pickUpAddress;
    private String pickUpPhoneNumber;
    private String dropOffName;
    private String dropOffAddress;
    private String dropOffPhoneNumber;

    public String getQuoteId() {
        return quoteId;
    }

    public void setQuoteId(String quoteId) {
        this.quoteId = quoteId;
    }

    public String getManifest() {
        return manifest;
    }

    public void setManifest(String manifest) {
        this.manifest = manifest;
    }

    public String getPickUpName() {
        return pickUpName;
    }

    public void setPickUpName(String pickUpName) {
        this.pickUpName = pickUpName;
    }

    public String getPickUpAddress() {
        return pickUpAddress;
    }

    public void setPickUpAddress(String pickUpAddress) {
        this.pickUpAddress = pickUpAddress;
    }

    public String getPickUpPhoneNumber() {
        return pickUpPhoneNumber;
    }

    public void setPickUpPhoneNumber(String pickUpPhoneNumber) {
        this.pickUpPhoneNumber = pickUpPhoneNumber;
    }

    public String getDropOffName() {
        return dropOffName;
    }

    public void setDropOffName(String dropOffName) {
        this.dropOffName = dropOffName;
    }

    public String getDropOffAddress() {
        return dropOffAddress;
    }

    public void setDropOffAddress(String dropOffAddress) {
        this.dropOffAddress = dropOffAddress;
    }

    public String getDropOffPhoneNumber() {
        return dropOffPhoneNumber;
    }

    public void setDropOffPhoneNumber(String dropOffPhoneNumber) {
        this.dropOffPhoneNumber = dropOffPhoneNumber;
    }
}
