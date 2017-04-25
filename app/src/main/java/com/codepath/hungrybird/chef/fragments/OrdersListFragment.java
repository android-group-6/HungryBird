package com.codepath.hungrybird.chef.fragments;


import android.content.Context;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.codepath.hungrybird.R;
import com.codepath.hungrybird.common.BaseItemHolderAdapter;
import com.codepath.hungrybird.common.DateUtils;
import com.codepath.hungrybird.consumer.fragments.OrderHistoryFramgent;
import com.codepath.hungrybird.databinding.ChefContactDetailsFragmentBinding;
import com.codepath.hungrybird.databinding.ChefOrderListItemBinding;
import com.codepath.hungrybird.model.Order;
import com.parse.ParseObject;

import java.text.ParseException;
import java.util.ArrayList;

public class OrdersListFragment extends Fragment {
    public static final String TAG = OrdersListFragment.class.getSimpleName();

    public static final String FRAGMENT_TAG = "FILTER_FRAGMENT_TAG";
    RecyclerView ordersRv;
    ArrayList<Order> orderArrayList = new ArrayList<>();
    BaseItemHolderAdapter<Order> orderArrayAdapter;
    OrderHistoryFramgent.OnOrderSelected orderSelected;
    private DateUtils dateUtils = new DateUtils();
    boolean contentLoaded = false;
    ChefContactDetailsFragmentBinding binding;
    public void update(ArrayList<Order> orderList) {
        orderArrayList.clear();
        orderArrayList.addAll(orderList);
        contentLoaded = true;
        if (orderArrayAdapter != null) {
            orderArrayAdapter.notifyDataSetChanged();
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        orderSelected = (OrderHistoryFramgent.OnOrderSelected) getActivity();
    }

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate(inflater, R.layout.chef_contact_details_fragment, container, false);
        ordersRv = binding.chefOrderStatusLv;

        binding.titleText.setText(getArguments().getString("TITLE"));
        binding.detailText.setText(getArguments().getString("DETAIL"));

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity(),
                LinearLayoutManager.VERTICAL, false);

        orderArrayAdapter =
                new BaseItemHolderAdapter<>(getContext(), R.layout.chef_order_list_item, orderArrayList);
        orderArrayAdapter.setOnClickListener(v -> {
            if (orderSelected != null) {
                Object object = v.getTag();
                ParseObject parseObject = (ParseObject) object;
                orderSelected.onOrderSelected(parseObject.getObjectId());
            }
        });


        orderArrayAdapter.setViewBinder((holder, item, position) -> {
            Order order = orderArrayList.get(position);
            ChefOrderListItemBinding orderBinding = (ChefOrderListItemBinding) (holder.binding);
            orderBinding.chefOrderListItemOrderNameTv.setText(order.getDisplayId());
            String displayDate = null;
            try {
                displayDate = dateUtils.getDate(order.getUpdatedAt());
            } catch (ParseException e) {
                e.printStackTrace();
            }
            orderBinding.chefOrderListItemDishCountValueTv.setText(displayDate);
        });


        ordersRv.setLayoutManager(linearLayoutManager);

        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(ordersRv.getContext(),
                linearLayoutManager.getOrientation());
        ordersRv.addItemDecoration(dividerItemDecoration);
        return binding.getRoot();
    }
    private void onContentLoaded() {
        if (contentLoaded && orderArrayList.isEmpty()) {
            binding.noContent.setVisibility(View.VISIBLE);
            binding.chefOrderStatusLv.setVisibility(View.GONE);
        } else {
            binding.noContent.setVisibility(View.GONE);
            binding.chefOrderStatusLv.setVisibility(View.VISIBLE);
        }

    }

    @Override
    public void onResume() {
        super.onResume();
        ordersRv.setAdapter(orderArrayAdapter);
        orderArrayAdapter.notifyDataSetChanged();
        onContentLoaded();
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return super.onOptionsItemSelected(item);
    }
}
