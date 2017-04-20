package com.codepath.hungrybird.model.postmates;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by ajasuja on 4/18/17.
 */

public class DeliveryResponse {

    private String deliveryId;
    private DeliveryStatus deliveryStatus;
    private double fee;
    private String manifestDescription;
    private boolean complete;
    private DeliveryContext pickUp;
    private DeliveryContext dropOff;

    public DeliveryResponse(JSONObject jsonObject) throws JSONException {
        this.deliveryId = jsonObject.getString("id");
        this.deliveryStatus = DeliveryStatus.fromString(jsonObject.getString("status"));
        this.fee = jsonObject.getDouble("fee");
        this.manifestDescription = jsonObject.getJSONObject("manifest").getString("description");
        this.complete = jsonObject.getBoolean("complete");
        this.pickUp = new DeliveryContext(jsonObject.getJSONObject("pickup"));
        this.dropOff = new DeliveryContext(jsonObject.getJSONObject("dropoff"));
    }

    public String getDeliveryId() {
        return deliveryId;
    }

    public DeliveryStatus getDeliveryStatus() {
        return deliveryStatus;
    }

    public double getFee() {
        return fee;
    }

    public String getManifestDescription() {
        return manifestDescription;
    }

    public boolean isComplete() {
        return complete;
    }

    public DeliveryContext getPickUp() {
        return pickUp;
    }

    public DeliveryContext getDropOff() {
        return dropOff;
    }
}

enum DeliveryStatus {
    PENDING("pending"),
    DELIVERED("delivered"),
    CANCELED("canceled");

    private String deliveryStatusValue;

    private DeliveryStatus(String deliveryStatusValue) {
        this.deliveryStatusValue = deliveryStatusValue;
    }

    public String getDeliveryStatusValue() {
        return deliveryStatusValue;
    }

    public static DeliveryStatus fromString(String deliveryStatusValue) {
        for (DeliveryStatus deliveryStatus : DeliveryStatus.values()) {
            if (deliveryStatus.deliveryStatusValue.equalsIgnoreCase(deliveryStatusValue)) {
                return deliveryStatus;
            }
        }
        return null;
    }
}

/**
 "dropoff": {
 "phone_number": "222-222-2222",
 "name": "xyz",
 "notes": "",
 "detailed_address": {
 "city": "San Francisco",
 "country": "US",
 "street_address_1": "101 Market Street",
 "street_address_2": "",
 "state": "CA",
 "zip_code": "94114"
 },
 "location": {
 "lat": 37.7931597,
 "lng": -122.3957005
 },
 "address": "101 Market Street"
 },
 */
class DeliveryContext {
    private String phoneNumber;
    private String name;
    private String notes;
    private String address;
    private Address addressDetails;
    private Location location;

    public DeliveryContext(JSONObject jsonObject) throws JSONException {
        this.phoneNumber = jsonObject.getString("phone_number");
        this.name = jsonObject.getString("name");
        this.notes = jsonObject.getString("notes");
        this.address = jsonObject.getString("address");
        this.location = new Location(jsonObject.getJSONObject("location"));
        this.addressDetails = new Address(jsonObject.getJSONObject("detailed_address"));
    }
}

class Address {
    private String streetAddress1;
    private String streetAddress2;
    private String city;
    private String state;
    private String country;
    private String zipCode;

    public Address(JSONObject jsonObject) throws JSONException {
        this.streetAddress1 = jsonObject.getString("street_address_1");
        this.streetAddress2 = jsonObject.getString("street_address_2");
        this.city = jsonObject.getString("city");
        this.state = jsonObject.getString("state");
        this.country = jsonObject.getString("country");
        this.zipCode = jsonObject.getString("zip_code");
    }
}

class Location {
    private double lat;
    private double lng;

    public Location(JSONObject jsonObject) throws JSONException {
        this.lat = jsonObject.getDouble("lat");
        this.lat = jsonObject.getDouble("lng");
    }
}