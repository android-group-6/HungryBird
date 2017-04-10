package com.codepath.hungrybird.chef.fragments;


import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
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
import com.codepath.hungrybird.chef.adapters.OrderArrayAdapter;
import com.codepath.hungrybird.consumer.fragments.FilterFragment;
import com.codepath.hungrybird.databinding.ChefContactDetailsFragmentBinding;
import com.codepath.hungrybird.model.Order;

import java.util.ArrayList;

public class OrdersListFragment extends Fragment {
    public static final String TAG = OrdersListFragment.class.getSimpleName();

    public static final String FRAGMENT_TAG = "FILTER_FRAGMENT_TAG";
    RecyclerView ordersRv;
    ArrayList<Order> orderArrayList = new ArrayList<>();
    OrderArrayAdapter orderArrayAdapter;
    private LinearLayoutManager linearLayoutManager;

    public void update(ArrayList<Order> orderList) {
        orderArrayList.clear();
        orderArrayList.addAll(orderList);
        if (orderArrayAdapter != null) {
            orderArrayAdapter.notifyDataSetChanged();
        }
    }

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        ChefContactDetailsFragmentBinding binding = DataBindingUtil.inflate(inflater, R.layout.chef_contact_details_fragment, container, false);
        ordersRv = binding.chefOrderStatusLv;

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity(),
                LinearLayoutManager.VERTICAL, false);
        ordersRv.setLayoutManager(linearLayoutManager);

        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(ordersRv.getContext(),
                linearLayoutManager.getOrientation());
        ordersRv.addItemDecoration(dividerItemDecoration);

        orderArrayAdapter = new OrderArrayAdapter(getActivity(), orderArrayList);

        return binding.getRoot();
    }

    @Override
    public void onResume() {
        super.onResume();
        ordersRv.setAdapter(orderArrayAdapter);
        orderArrayAdapter.notifyDataSetChanged();
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.mi_filter:
                FragmentManager fm = getActivity().getSupportFragmentManager();
                FilterFragment filterFragment = new FilterFragment();
                filterFragment.show(fm, FRAGMENT_TAG);

                return true;
            default:
        }
        return super.onOptionsItemSelected(item);
    }
}
