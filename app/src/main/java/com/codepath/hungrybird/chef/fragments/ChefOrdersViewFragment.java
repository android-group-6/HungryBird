package com.codepath.hungrybird.chef.fragments;


import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.codepath.hungrybird.R;
import com.codepath.hungrybird.chef.adapters.ChefOrdersFragmentPagerAdapter;
import com.codepath.hungrybird.consumer.fragments.FilterFragment;
import com.codepath.hungrybird.databinding.ChefOrdersViewFragmentBinding;
import com.codepath.hungrybird.model.Order;
import com.codepath.hungrybird.model.User;
import com.codepath.hungrybird.network.ParseClient;
import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.List;

public class ChefOrdersViewFragment extends Fragment {
    public static final String TAG = ChefOrdersViewFragment.class.getSimpleName();
    ParseClient parseClient = ParseClient.getInstance();
    public static final String FRAGMENT_TAG = "FILTER_FRAGMENT_TAG";
    ChefOrdersFragmentPagerAdapter sampleFragmentPagerAdapter;
    ArrayList<Order> allOrders = new ArrayList<>();

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        ChefOrdersViewFragmentBinding binding = DataBindingUtil.inflate(inflater, R.layout.chef_orders_view_fragment, container, false);
        sampleFragmentPagerAdapter = new ChefOrdersFragmentPagerAdapter(getActivity().getSupportFragmentManager(),
                getActivity(), null);
        User currentUser = new User(ParseUser.getCurrentUser());
        parseClient.getOrdersByChefId(currentUser.getObjectId(), new ParseClient.OrderListListener() {
            @Override
            public void onSuccess(List<Order> orders) {
                allOrders.clear();
                allOrders.addAll(orders);
                sampleFragmentPagerAdapter.updateOrders(allOrders);
                sampleFragmentPagerAdapter.notifyDataSetChanged();
            }

            @Override
            public void onFailure(Exception e) {

            }
        });

        binding.viewpager.setAdapter(sampleFragmentPagerAdapter);
        binding.slidingTabs.setupWithViewPager(binding.viewpager);
        return binding.getRoot();
    }

    public void refresh(Order order) {
        if (order != null) {
            for (Order o : allOrders) {
                if (order.getObjectId().equals(o.getObjectId())) {
                    o.setStatus(order.getStatus());
                    break;
                }
            }
        }
        sampleFragmentPagerAdapter.updateOrders(allOrders);
        sampleFragmentPagerAdapter.notifyDataSetChanged();
    }

    @Override
    public void onResume() {
        super.onResume();
        refresh(null);
    }


}
