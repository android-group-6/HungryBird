package com.codepath.hungrybird.model;

import com.parse.ParseClassName;
import com.parse.ParseObject;

/**
 * Created by ajasuja on 4/4/17.
 */
@ParseClassName("OrderDishRelation")
public class OrderDishRelation extends ParseObject {

    public Order getOrder() {
        return (Order) getParseObject("order");
    }

    public void setOrder(Order order) {
        put("order", order);
    }

    public Dish getDish() {
        return (Dish) getParseObject("dish");
    }

    public void setDish(Dish dish) {
        put("dish", dish);
    }

    public void setQuantity(int quantity) {
        put("quantity", quantity);
    }

    public int getQuantity() {
        return getInt("quantity");
    }
}
