package com.codepath.hungrybird.consumer.fragments;


import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.codepath.hungrybird.R;
import com.codepath.hungrybird.common.BaseItemHolderAdapter;
import com.codepath.hungrybird.common.DateUtils;
import com.codepath.hungrybird.databinding.ConsumerOrderHistoryViewItemBinding;
import com.codepath.hungrybird.databinding.OrderHistoryBinding;
import com.codepath.hungrybird.model.Order;
import com.codepath.hungrybird.model.User;
import com.codepath.hungrybird.network.ParseClient;
import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.List;

import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class OrderHistoryFramgent extends Fragment {
    OrderHistoryBinding binding;
    private ArrayList<Order> orders = new ArrayList<>();
    private DateUtils dateUtils = new DateUtils();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate(inflater, R.layout.order_history, container, false);

        final BaseItemHolderAdapter<Order> adapter =
                new BaseItemHolderAdapter<>(getContext(), R.layout.consumer_order_history_view_item, orders);
        adapter.setOnClickListener(v -> {
            Toast.makeText(getActivity(), "selected", Toast.LENGTH_SHORT).show();
        });
        binding.consumerOrderListRv.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false));
        binding.consumerOrderListRv.setAdapter(adapter);
        ParseClient.getInstance().getOrdersByConsumerId(ParseUser.getCurrentUser().getObjectId(), new ParseClient.OrderListListener() {
            @Override
            public void onSuccess(List<Order> os) {
                Observable.from(os)
                        .filter(order -> {
                            boolean ret = true;
                            try {
                                String shortDate = dateUtils.getDate(order.getCreatedAt());
                                order.setShortDate(shortDate);
                                if (Order.Status.NOT_ORDERED.name().equals(order.getStatus())) {
                                    order.setStatus("Not Ordered");
                                    ret = false;
                                } else if (Order.Status.DONE.name().equals(order.getStatus())) {
                                    order.setStatus("Completed");
                                } else if (Order.Status.IN_PROGRESS.name().equals(order.getStatus())) {
                                    order.setStatus("In Progress");
                                } else if (Order.Status.ORDERED.name().equals(order.getStatus())) {
                                    order.setStatus("Ordered");
                                } else if (Order.Status.OUT_FOR_DELIVERY.name().equals(order.getStatus())) {
                                    order.setStatus("Out for Delivery");
                                } else if (Order.Status.READY_FOR_PICKUP.name().equals(order.getStatus())) {
                                    order.setStatus("Ready For Pickup");
                                } else if (Order.Status.CANCELLED.name().equals(order.getStatus())) {
                                    order.setStatus("Cancelled");
                                }
                                ret = true;
                            } catch (Exception e) {
                                e.printStackTrace();
                                ret = false;
                            }
                            return ret;
                        })
                        .toList()
                        .subscribeOn(Schedulers.computation())
                        .observeOn(AndroidSchedulers.mainThread()).subscribe(orders1 -> {
                    orders.clear();
                    orders.addAll(orders1);
                    adapter.notifyDataSetChanged();

                });
            }

            @Override
            public void onFailure(Exception e) {

            }
        });
        adapter.setViewBinder((holder, item, position) -> {
            Order order = orders.get(position);
            ConsumerOrderHistoryViewItemBinding binding = (ConsumerOrderHistoryViewItemBinding) (holder.binding);
            binding.consumerOrderDateTv.setText(order.getShortDate());
            binding.consumerOrderStatus.setText(order.getStatus());
            binding.consumerOrderCode.setText(order.getObjectId());
            binding.consumerOrderDeliveryStatus.setText(order.getStatus());
            Object o = order.get("chef");
            ParseObject po = (ParseObject)o;
            String chefName = (String)(po.get("chefName"));
            binding.consumerOrderChefName.setText(chefName);

        });
        return binding.getRoot();

    }
}
