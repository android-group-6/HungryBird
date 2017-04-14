package com.codepath.hungrybird.network;

import android.util.Log;

import com.codepath.hungrybird.model.Dish;
import com.codepath.hungrybird.model.Order;
import com.codepath.hungrybird.model.OrderDishRelation;
import com.codepath.hungrybird.model.User;
import com.parse.FindCallback;
import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by ajasuja on 4/6/17.
 */
public class ParseClient {

    private static ParseClient instance;
    public static ParseClient getInstance() {
        if (instance == null) {
            instance = new ParseClient();
        }
        return instance;
    }

    private ParseClient() {
        // no=op
    }

    // ---------- Dish ---------------

    public interface DishListener {
        void onSuccess(Dish dish);
        void onFailure(Exception e);
    }

    public interface DishListListener {
        void onSuccess(List<Dish> dishes);
        void onFailure(Exception e);
    }

    public void addDish(Dish dish) {
        dish.saveInBackground();
    }

    public void getDishById(final String id, final DishListener listener) {
        ParseQuery<Dish> parseQuery = ParseQuery.getQuery(Dish.class);
        parseQuery.getInBackground(id, new GetCallback<Dish>() {
            @Override
            public void done(Dish dish, ParseException e) {
                if (e == null) {
                    listener.onSuccess(dish);
                } else {
                    listener.onFailure(e);
                }
            }
        });
    }

    public void getDishesByChefId(final String chefId, final DishListListener listener) {
        ParseQuery<ParseUser> innerQuery = ParseQuery.getQuery(ParseUser.class);
        innerQuery.getInBackground(chefId);
        ParseQuery<Dish> parseQuery = ParseQuery.getQuery(Dish.class);
        parseQuery.whereMatchesQuery("chef", innerQuery);
        parseQuery.findInBackground(new FindCallback<Dish>() {
            @Override
            public void done(List<Dish> dishes, ParseException e) {
                if (e == null) {
                    Log.d("debug-data", String.format("No. of dishes from chef %s are %s ", chefId, dishes.size()));
                    listener.onSuccess(dishes);
                } else {
                    Log.d("error-parse", "error while getting dish by chef id ... " + chefId);
                    listener.onFailure(e);
                }
            }
        });
    }

    public void getDishesByCuisine(Dish.Cuisine cuisine, final DishListListener listListener) {
        ParseQuery<Dish> parseQuery = ParseQuery.getQuery(Dish.class);
        parseQuery.whereMatches("cuisine", cuisine.getCuisineValue());
        parseQuery.findInBackground(new FindCallback<Dish>() {
            @Override
            public void done(List<Dish> objects, ParseException e) {
                if (e == null) {
                    listListener.onSuccess(objects);
                } else {
                    listListener.onFailure(e);
                }
            }
        });
    }

    public void getDishesByCuisines(List<Dish.Cuisine> cuisines, final DishListListener listListener) {
        ParseQuery<Dish> parseQuery = ParseQuery.getQuery(Dish.class);
        List<String> cuisineValues = new ArrayList<>();
        for (Dish.Cuisine cuisine : cuisines) {
            cuisineValues.add(cuisine.getCuisineValue())
;        }
        parseQuery.whereContainedIn("cuisine", cuisineValues);
        parseQuery.findInBackground(new FindCallback<Dish>() {
            @Override
            public void done(List<Dish> objects, ParseException e) {
                if (e == null) {
                    listListener.onSuccess(objects);
                } else {
                    listListener.onFailure(e);
                }
            }
        });
    }

    public void getDishesBySearchQuery(String query, final DishListListener listener) {
        ParseQuery<Dish> parseQuery = ParseQuery.getQuery(Dish.class);
        parseQuery.whereMatches("name", query);
        parseQuery.findInBackground(new FindCallback<Dish>() {
            @Override
            public void done(List<Dish> objects, ParseException e) {
                if (e == null) {
                    listener.onSuccess(objects);
                } else {
                    e.printStackTrace();
                }
            }
        });
    }

    // ---------- Order ---------------

    public interface OrderListener {
        void onSuccess(Order order);
        void onFailure(Exception e);
    }

    public interface OrderListListener {
        void onSuccess(List<Order> orders);
        void onFailure(Exception e);
    }

    public void addOrder(String consumerId, final String chefId, final Order.Status status, final OrderListener listener) {
        getUserById(consumerId, new UserListener() {
            @Override
            public void onSuccess(final User consumer) {
                getUserById(chefId, new UserListener() {
                    @Override
                    public void onSuccess(User chef) {
                        final Order order = new Order();
                        order.setChef(chef);
                        order.setConsumer(consumer);
                        order.setStatus(status.name());
                        order.saveInBackground(new SaveCallback() {
                            @Override
                            public void done(ParseException e) {
                                if (e == null) {
                                    listener.onSuccess(order);
                                } else {
                                    listener.onFailure(e);
                                }
                            }
                        });
                    }
                    @Override
                    public void onFailure(Exception e) {
                        listener.onFailure(e);
                    }
                });
            }
            @Override
            public void onFailure(Exception e) {
                listener.onFailure(e);
            }
        });
    }

    public void updateOrder(final Order order, final OrderListener listener) {
        order.saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                if (e == null) {
                    listener.onSuccess(order);
                } else {
                    listener.onFailure(e);
                }
            }
        });
    }

    public void upsertOrder(Order order, final OrderListener listener) {
        getOrderByConsumerIdAndChefId(order.getConsumer().getObjectId(), order.getChef().getObjectId(), new OrderListener() {
            @Override
            public void onSuccess(final Order updatedOrder) {
                updatedOrder.saveInBackground(new SaveCallback() {
                    @Override
                    public void done(ParseException e) {
                        if (e == null) {
                            listener.onSuccess(updatedOrder);
                        } else {
                            listener.onFailure(e);
                        }
                    }
                });
            }
            @Override
            public void onFailure(Exception e) {
                listener.onFailure(e);
            }
        });
    }

    public void getOrderById(final String orderId, final OrderListener listener) {
        ParseQuery<Order> parseQuery = ParseQuery.getQuery(Order.class);
        parseQuery.getInBackground(orderId, new GetCallback<Order>() {
            @Override
            public void done(Order order, ParseException e) {
                if (e == null) {
                    Log.d("debug-data", order.getOrderName());
                    listener.onSuccess(order);
                } else {
                    Log.d("error-parse", "error while getting dish by id ... " + orderId);
                    listener.onFailure(e);
                }
            }
        });
    }

    public void getOrdersByChefId(String chefId, final OrderListListener listener) {
        ParseQuery<ParseUser> innerQuery = ParseQuery.getQuery(ParseUser.class);
        innerQuery.getInBackground(chefId);
        ParseQuery<Order> parseQuery = ParseQuery.getQuery(Order.class);
        parseQuery.whereMatchesQuery("chef", innerQuery);
        parseQuery.findInBackground(new FindCallback<Order>() {
            @Override
            public void done(List<Order> objects, ParseException e) {
                if (e == null) {
                    listener.onSuccess(objects);
                } else {
                    listener.onFailure(e);
                }
            }
        });
    }

    public void getOrdersByConsumerId(String consumerId, final OrderListListener listener) {
        ParseQuery<ParseUser> innerQuery = ParseQuery.getQuery(ParseUser.class);
        innerQuery.getInBackground(consumerId);
        ParseQuery<Order> parseQuery = ParseQuery.getQuery(Order.class);
        parseQuery.whereMatchesQuery("consumer", innerQuery);
        parseQuery.include("chef");
        parseQuery.findInBackground(new FindCallback<Order>() {
            @Override
            public void done(List<Order> objects, ParseException e) {
                if (e == null) {
                    listener.onSuccess(objects);
                } else {
                    listener.onFailure(e);
                }
            }
        });
    }

    public void getOrderByConsumerIdAndChefId(final String consumerId, final String chefId, final OrderListener listener) {
        ParseQuery<ParseUser> innerQueryConsumer = ParseQuery.getQuery(ParseUser.class);
        innerQueryConsumer.getInBackground(consumerId);
        ParseQuery<ParseUser> innerQueryChef = ParseQuery.getQuery(ParseUser.class);
        innerQueryChef.getInBackground(chefId);
        ParseQuery<Order> parseQuery = ParseQuery.getQuery(Order.class);
        parseQuery.whereMatchesQuery("consumer", innerQueryConsumer);
        parseQuery.whereMatchesQuery("chef", innerQueryChef);
        parseQuery.getFirstInBackground(new GetCallback<Order>() {
            @Override
            public void done(Order object, ParseException e) {
                if (e == null) {
                    if (object == null) {
                        addOrder(consumerId, chefId, Order.Status.ORDERED, listener);
                    } else {
                        listener.onSuccess(object);
                    }
                } else {
                    listener.onFailure(e);
                }
            }
        });
    }

    // --------- OrderDishRelation -------

    public interface OrderDishRelationListListener {
        void onSuccess(List<OrderDishRelation> orderDishRelations);
        void onFailure(Exception e);
    }

    public void addOrderDishRelation(OrderDishRelation orderDishRelation) {
        orderDishRelation.saveInBackground();
    }

    public void getOrderDishRelationsByOrderId(String orderId, final OrderDishRelationListListener listener) {
        ParseQuery<Order> innerQuery = ParseQuery.getQuery(Order.class);
        innerQuery.getInBackground(orderId);
        ParseQuery<OrderDishRelation> parseQuery = ParseQuery.getQuery(OrderDishRelation.class);
        parseQuery.whereMatchesQuery("order", innerQuery);
        parseQuery.findInBackground(new FindCallback<OrderDishRelation>() {
            @Override
            public void done(List<OrderDishRelation> objects, ParseException e) {
                if (e == null) {
                    listener.onSuccess(objects);
                } else {
                    listener.onFailure(e);
                }
            }
        });
    }

    // --------- User ---------------

    public interface UserListener {
        void onSuccess(User user);
        void onFailure(Exception e);
    }

    public void saveUser(final User user, final UserListener listener) {
        user.saveInBackground(e -> {
            if (e == null) {
                listener.onSuccess(user);
            } else {
                listener.onFailure(e);
            }
        });
    }

    public void getUserById(String userId, final UserListener listener) {
        ParseQuery<ParseUser> parseQuery = ParseQuery.getQuery(ParseUser.class);
        parseQuery.getInBackground(userId, new GetCallback<ParseUser>() {
            @Override
            public void done(ParseUser parseUser, ParseException e) {
                if (e == null) {
                    listener.onSuccess(new User(parseUser));
                } else {
                    listener.onFailure(e);
                }
            }
        });
    }
}
