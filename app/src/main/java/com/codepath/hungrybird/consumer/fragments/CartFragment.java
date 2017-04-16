package com.codepath.hungrybird.consumer.fragments;

import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bumptech.glide.Glide;
import com.codepath.hungrybird.R;
import com.codepath.hungrybird.common.BaseItemHolderAdapter;
import com.codepath.hungrybird.common.DateUtils;
import com.codepath.hungrybird.common.StringsUtils;
import com.codepath.hungrybird.databinding.ConsumerCartDetailsFragmentBinding;
import com.codepath.hungrybird.databinding.ConsumerOrderCartDishItemBinding;
import com.codepath.hungrybird.model.Dish;
import com.codepath.hungrybird.model.Order;
import com.codepath.hungrybird.model.OrderDishRelation;
import com.codepath.hungrybird.network.ParseClient;
import com.parse.ParseException;
import com.parse.SaveCallback;

import java.util.ArrayList;
import java.util.List;

import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

/**
 * Created by gauravb on 4/13/17.
 */

public class CartFragment extends Fragment {
    public static final String OBJECT_ID = CartFragment.class.getSimpleName();
    ConsumerCartDetailsFragmentBinding binding;
    ArrayList<OrderDishRelation> orderDishRelations = new ArrayList<>();
    DateUtils dateUtils = new DateUtils();
    StringsUtils stringsUtils = new StringsUtils();
    double finalPricing = 0;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        binding = DataBindingUtil.inflate(inflater, R.layout.consumer_cart_details_fragment, container, false);
        Bundle bundle = getArguments();
        String orderObjectId = bundle.getString(OBJECT_ID);
        ParseClient parseClient = ParseClient.getInstance();

        final BaseItemHolderAdapter<OrderDishRelation> adapter =
                new BaseItemHolderAdapter(getContext(), R.layout.consumer_order_cart_dish_item, orderDishRelations);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);
        binding.consumerCartItemsRv.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false));
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(binding.consumerCartItemsRv.getContext(),
                linearLayoutManager.getOrientation());
        binding.consumerCartItemsRv.addItemDecoration(dividerItemDecoration);
        binding.consumerCartItemsRv.setAdapter(adapter);

        Observable.create(new Observable.OnSubscribe<Response>() {
            @Override
            public void call(Subscriber<? super Response> subscriber) {
                parseClient.getOrderById(orderObjectId, new ParseClient.OrderListener() {
                    @Override
                    public void onSuccess(Order order) {
                        Response res = new Response();
                        res.order = order;
                        subscriber.onNext(res);
                        subscriber.onCompleted();
                    }

                    @Override
                    public void onFailure(Exception e) {
                        subscriber.onError(e);
                    }
                });
            }
        }).flatMap(new Func1<Response, Observable<Response>>() {
            @Override
            public Observable<Response> call(Response res) {
                return Observable.create(new Observable.OnSubscribe<Response>() {
                    @Override
                    public void call(Subscriber<? super Response> subscriber) {
                        parseClient.getOrderDishRelationsByOrderId(orderObjectId, new ParseClient.OrderDishRelationListListener() {
                            @Override
                            public void onSuccess(List<OrderDishRelation> orderDishRelations) {
                                res.orderDishRelation = orderDishRelations;
                                subscriber.onNext(res);
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
        }).observeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<Response>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onNext(Response response) {
                        updatePricing(response.orderDishRelation);
                        CartFragment.this.orderDishRelations.clear();
                        CartFragment.this.orderDishRelations.addAll(response.orderDishRelation);
                        adapter.notifyDataSetChanged();
                        Glide.with(getContext()).load(response.order.getChef().getProfileImage().getUrl()).into(binding.consumerCartChefIv);
                        binding.consumerCartChefNameTv.setText(response.order.getChef().getUsername());
                    }
                });
        adapter.setOnClickListener(v -> {
            v.findViewById(R.id.add_to_card_panel).setVisibility(View.GONE);
            if (view != null) {
                view.setVisibility(View.GONE);
                int quantity = ((OrderDishRelation) view.getTag()).getQuantity();
                int index = (int) view.getTag(R.id.add_cart);
                adapter.notifyItemChanged(index);

            }
            view = null;
        });
        adapter.setViewBinder((holder, item, position) -> {
            OrderDishRelation order = orderDishRelations.get(position);
            ConsumerOrderCartDishItemBinding binding = (ConsumerOrderCartDishItemBinding) (holder.binding);
            Dish dish = order.getDish();
            Glide.with(getActivity()).load(dish.getPrimaryImage().getUrl()).into(binding.itemImage);
            binding.dishItemName.setText(dish.getDishName());
            binding.dishItemServiceSize.setText("Serves " + dish.getServingSize());
            binding.dishPrice.setText("" + dish.getPrice());
            binding.itemsCount.setText("" + order.getQuantity());
            if (view != null) {
                view.setVisibility(View.VISIBLE);
            }
            binding.itemsCount.setOnClickListener(v -> {
                if (binding.addToCardPanel.getVisibility() == View.GONE) {
                    binding.addToCardPanel.setVisibility(View.VISIBLE);
                    binding.updatedCount.setText("" + order.getQuantity());
                    if (view != null) {
                        view.setVisibility(View.GONE);
                        binding.itemsCount.setText("" + order.getQuantity());
                        adapter.notifyItemChanged(position);
                    }
                    view = binding.addToCardPanel;
                    view.setTag(order);
                    view.setTag(R.id.add_cart, position);
                } else {
                    binding.addToCardPanel.setVisibility(View.GONE);
                    view = null;
                }

            });
            binding.addCart.setOnClickListener(v -> {
                int val = order.getQuantity();
                order.setQuantity(val + 1);
                binding.updatedCount.setText("" + order.getQuantity());
                parseClient.addOrderDishRelation(order, new SaveCallback() {
                    @Override
                    public void done(ParseException e) {
                        if (e == null) {
                            updatePricing(orderDishRelations);
                            binding.itemsCount.setText("" + order.getQuantity());
                        }
                    }
                });
            });
            binding.reduceCart.setOnClickListener(v -> {
                int val = order.getQuantity();
                if (val > 0) {
                    order.setQuantity(val - 1);
                    binding.updatedCount.setText("" + order.getQuantity());
                    parseClient.addOrderDishRelation(order, new SaveCallback() {
                        @Override
                        public void done(ParseException e) {
                            if (e == null) {
                                updatePricing(orderDishRelations);
                                binding.itemsCount.setText("" + order.getQuantity());
                            }
                        }
                    });
                }
            });

        });
        return binding.getRoot();
    }

    private void updatePricing(List<OrderDishRelation> orderDishRelations) {
        if (orderDishRelations.isEmpty() == false) {
            double totalPriceBeforeTax = 0.0;
            for (OrderDishRelation o : orderDishRelations) {
                totalPriceBeforeTax += o.getQuantity() * o.getDish().getPrice();
            }
            binding.consumerCartPriceBeforeTax.setText("$" + Math.round(totalPriceBeforeTax * 100.00) / 100.00);
        }
    }

    View view = null;

    static class Response {
        Order order;
        List<OrderDishRelation> orderDishRelation;

    }
}
