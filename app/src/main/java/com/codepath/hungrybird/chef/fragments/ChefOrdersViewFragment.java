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

import com.codepath.hungrybird.HungryBirdApplication;
import com.codepath.hungrybird.R;
import com.codepath.hungrybird.chef.adapters.ChefOrdersFragmentPagerAdapter;
import com.codepath.hungrybird.consumer.fragments.FilterFragment;
import com.codepath.hungrybird.databinding.ChefOrdersViewFragmentBinding;
import com.codepath.hungrybird.model.Order;
import com.codepath.hungrybird.network.ParseClient;
import com.parse.Parse;
import com.parse.ParseUser;

import java.util.List;

public class ChefOrdersViewFragment extends Fragment {
    public static final String TAG = ChefOrdersViewFragment.class.getSimpleName();
    ParseClient parseClient = ParseClient.getInstance();
    public static final String FRAGMENT_TAG = "FILTER_FRAGMENT_TAG";

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        ChefOrdersViewFragmentBinding binding = DataBindingUtil.inflate(inflater, R.layout.chef_orders_view_fragment, container, false);
        final ChefOrdersFragmentPagerAdapter sampleFragmentPagerAdapter = new ChefOrdersFragmentPagerAdapter(getActivity().getSupportFragmentManager(),
                getActivity(), null);
        ParseUser parseUser = ParseUser.getCurrentUser();
        parseClient.getOrdersByChefId(parseUser.getObjectId(), new ParseClient.OrderListListener () {

            @Override
            public void onSuccess(List<Order> orders) {
                sampleFragmentPagerAdapter.updateOrders(orders);
            }

            @Override
            public void onFailure(Exception e) {

            }
        });

        binding.viewpager.setAdapter(sampleFragmentPagerAdapter);
        binding.slidingTabs.setupWithViewPager(binding.viewpager);
        return binding.getRoot();
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
