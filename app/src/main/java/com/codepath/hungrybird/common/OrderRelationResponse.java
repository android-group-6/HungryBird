package com.codepath.hungrybird.common;

import com.codepath.hungrybird.model.Dish;
import com.codepath.hungrybird.model.Order;
import com.codepath.hungrybird.model.OrderDishRelation;

import java.util.HashMap;
import java.util.List;

/**
 * Created by gauravb on 5/3/17.
 */

public class OrderRelationResponse {
    public Order order;
    public List<OrderDishRelation> orderDishRelation;
    public List<Dish> dishes;
    public HashMap<String, OrderDishRelation> map = new HashMap<>();
}
