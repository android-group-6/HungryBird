package com.codepath.hungrybird.model;

import com.parse.ParseClassName;
import com.parse.ParseObject;
import com.parse.ParseUser;

/**
 * Created by ajasuja on 4/4/17.
 */
@ParseClassName("Order")
public class Order extends ParseObject {

    public String getOrderName() {
        return getString("orderName");
    }

    public void setOrderName(String orderName) {
        put("orderName", orderName);
    }

    public ParseUser getConsumer() {
        return getParseUser("consumer");
    }

    public void setConsumer(ParseUser parseUser) {
        put("consumer", parseUser);
    }

    public ParseUser getChef() {
        return getParseUser("chef");
    }

    public void setChef(ParseUser parseUser) {
        put("chef", parseUser);
    }
}
