package com.codepath.hungrybird.consumer.fragments;

import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.codepath.hungrybird.R;
import com.codepath.hungrybird.common.BaseItemHolderAdapter;
import com.codepath.hungrybird.databinding.ConsumerOrderDetailsDishItemBinding;
import com.codepath.hungrybird.databinding.ConsumerOrderDetailsFragmentBinding;
import com.codepath.hungrybird.model.Dish;
import com.codepath.hungrybird.model.OrderDishRelation;
import com.codepath.hungrybird.network.ParseClient;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by gauravb on 4/13/17.
 */

public class OrderDetailsFragment extends Fragment {
    public static final String OBJECT_ID = OrderDetailsFragment.class.getSimpleName();
    ConsumerOrderDetailsFragmentBinding binding;
    ArrayList<OrderDishRelation> orderDishRelations = new ArrayList<>();

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
        binding.consumerOrderDetailsRv.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false));
        binding.consumerOrderDetailsRv.setAdapter(adapter);
        parseClient.getOrderDishRelationsByOrderId(orderObjectId, new ParseClient.OrderDishRelationListListener() {
            @Override
            public void onSuccess(List<OrderDishRelation> orderDishRelations) {
                OrderDetailsFragment.this.orderDishRelations.clear();
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
            binding.consumerOrderDetailDishNameTv.setText(dish.getDishName());

            //todo: get price from order
            //todo: get quantity and calculate
        });
        return binding.getRoot();
    }

}
