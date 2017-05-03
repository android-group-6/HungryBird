package com.codepath.hungrybird.common;

import com.codepath.hungrybird.model.Dish;
import com.codepath.hungrybird.model.OrderDishRelation;
import com.codepath.hungrybird.network.ParseClient;

import java.util.List;

import rx.Observable;
import rx.Subscriber;

/**
 * Created by gauravb on 5/3/17.
 */

public class HelperObservables {
    public static Observable<OrderRelationResponse> getOrderDishRelationsByOrderId(String orderObjectId, final OrderRelationResponse odr) {
        return Observable.create(new Observable.OnSubscribe<OrderRelationResponse>() {
            @Override
            public void call(Subscriber<? super OrderRelationResponse> subscriber) {

                ParseClient.getInstance().getOrderDishRelationsByOrderId(orderObjectId, new ParseClient.OrderDishRelationListListener() {
                    @Override
                    public void onSuccess(List<OrderDishRelation> orderDishRelations) {

                        odr.orderDishRelation = orderDishRelations;
                        if (orderDishRelations.isEmpty() == false) {
                            odr.order = orderDishRelations.get(0).getOrder();
                        }
                        subscriber.onNext(odr);
                        subscriber.onCompleted();
                    }

                    @Override
                    public void onFailure(Exception e) {
                        subscriber.onError(e);
                    }
                });
            }
        });
    }

    public static Observable<OrderRelationResponse> getDishesByChefId(String chefId, final OrderRelationResponse orderRelationResponse) {
        return Observable.create(new Observable.OnSubscribe<OrderRelationResponse>() {
            @Override
            public void call(Subscriber<? super OrderRelationResponse> subscriber) {
                ParseClient.getInstance().getDishesByChefId(chefId, new ParseClient.DishListListener() {
                    @Override
                    public void onSuccess(List<Dish> dishes) {
                        orderRelationResponse.dishes = dishes;
                        subscriber.onNext(orderRelationResponse);
                        subscriber.onCompleted();
                    }

                    @Override
                    public void onFailure(Exception e) {
                        subscriber.onError(e);
                    }
                });

            }
        });
    }
}
