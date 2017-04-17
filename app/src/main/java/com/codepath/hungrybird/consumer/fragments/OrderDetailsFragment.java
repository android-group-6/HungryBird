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
import com.codepath.hungrybird.databinding.ConsumerOrderDetailsDishItemBinding;
import com.codepath.hungrybird.databinding.ConsumerOrderDetailsFragmentBinding;
import com.codepath.hungrybird.model.Dish;
import com.codepath.hungrybird.model.Order;
import com.codepath.hungrybird.model.OrderDishRelation;
import com.codepath.hungrybird.network.ParseClient;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by gauravb on 4/13/17.
 */

public class OrderDetailsFragment extends Fragment {
    public static final String OBJECT_ID = OrderDetailsFragment.class.getSimpleName();
    ConsumerOrderDetailsFragmentBinding binding;
    ArrayList<OrderDishRelation> orderDishRelations = new ArrayList<>();
    DateUtils dateUtils = new DateUtils();
    StringsUtils stringsUtils = new StringsUtils();

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        binding = DataBindingUtil.inflate(inflater, R.layout.consumer_order_details_fragment, container, false);
        Bundle bundle = getArguments();
        String orderObjectId = bundle.getString(OBJECT_ID);
        ParseClient parseClient = ParseClient.getInstance();

        final BaseItemHolderAdapter<OrderDishRelation> adapter =
                new BaseItemHolderAdapter(getContext(), R.layout.consumer_order_details_dish_item, orderDishRelations);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);
        binding.consumerOrderDetailsRv.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false));
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(binding.consumerOrderDetailsRv.getContext(),
                linearLayoutManager.getOrientation());
        binding.consumerOrderDetailsRv.addItemDecoration(dividerItemDecoration);
        binding.consumerOrderDetailsRv.setAdapter(adapter);
        parseClient.getOrderDishRelationsByOrderId(orderObjectId, new ParseClient.OrderDishRelationListListener() {
            @Override
            public void onSuccess(List<OrderDishRelation> orderDishRelations) {

                if (orderDishRelations.isEmpty() == false) {
                    Order order = orderDishRelations.get(0).getOrder();
                    if (order != null) {
                        try {
                            Date d = order.getCreatedAt();
                            String date = dateUtils.getDate(d);
                            binding.consumerOrderDateTv.setText(date);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        binding.consumerOrderCode.setText(order.getObjectId());
                        binding.consumerOrderStatus.setText(stringsUtils.displayStatusString(order));
                        binding.paymentType.setText(order.getPaymentType());
                        binding.deliveryAddress.setText(order.getDeliveryAddress());

                    }
                    double totalTax = 0.0;
                    double totalPriceBeforeTax = 0.0;
                    Double shippingAndService = order.getShippingFee();
                    for (OrderDishRelation o : orderDishRelations) {
                        totalTax += o.getQuantity() * o.getTaxPerItem();
                        totalPriceBeforeTax += o.getQuantity() * o.gePricePerItem();
                    }
                    double subtotal = totalTax + totalPriceBeforeTax;
                    double finalTotal = subtotal + shippingAndService;

                    binding.itemsTotalValue.setText("$" + totalPriceBeforeTax);
                    binding.itemsTotalTaxValue.setText("$" + Math.round(100 * totalTax) / 100.0);
                    String shipping = "FREE";
                    if (new Double(0.0).compareTo(shippingAndService) != 0) {
                        shipping = "$" + Math.round(100 * shippingAndService) / 100.0;
                    }
                    binding.shippingServiceValue.setText(shipping);
                    binding.itemsTotalSubtotalValue.setText("$" + subtotal);
                    binding.finalTotal.setText("$" + finalTotal);
                }

                OrderDetailsFragment.this.orderDishRelations.addAll(orderDishRelations);
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onFailure(Exception e) {

            }
        });

        adapter.setViewBinder((holder, item, position) -> {
            OrderDishRelation order = orderDishRelations.get(position);
            ConsumerOrderDetailsDishItemBinding binding = (ConsumerOrderDetailsDishItemBinding) (holder.binding);
            Dish dish = order.getDish();
            Glide.with(getActivity()).load(dish.getPrimaryImage().getUrl()).into(binding.itemImage);
            int count = order.getQuantity();

            Double pricePerItem = order.gePricePerItem();
            double totalPrice = count * pricePerItem;
            binding.consumerOrderDetailQuantityDetailsTv.setText("" + count + " at $" + pricePerItem + " each");
            binding.itemTotalPrice.setText("$" + totalPrice);
            binding.consumerOrderDetailDishNameTv.setText(dish.getDishName());


            //todo: get price from order
            //todo: get quantity and calculate
        });
        return binding.getRoot();
    }

}
