package com.codepath.hungrybird.chef.adapters;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v7.widget.RecyclerView;

import com.codepath.hungrybird.chef.fragments.OrdersListFragment;
import com.codepath.hungrybird.model.Order;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by gauravb on 3/30/17.
 */

public class ChefOrdersFragmentPagerAdapter extends FragmentPagerAdapter {
    final int PAGE_COUNT = 3;
    private String tabTitles[] = new String[]{"NEW", "IN PROGRESS", "DONE"};
    private OrdersListFragment ordersListFragments[] = new OrdersListFragment[]{null, null, null};
    private Context context;
    RecyclerView.OnScrollListener onScrollListener;
    ArrayList<Order> newOrders = new ArrayList<>();
    ArrayList<Order> inProgressOrders = new ArrayList<>();
    ArrayList<Order> doneOrders = new ArrayList<>();

    public ChefOrdersFragmentPagerAdapter(FragmentManager fm, Context context, RecyclerView.OnScrollListener onScrollListener) {
        super(fm);
        this.context = context;
        this.onScrollListener = onScrollListener;
    }

    public void updateOrders(List<Order> orders) {
        newOrders.clear();
        inProgressOrders.clear();
        doneOrders.clear();
        for (Order o : orders) {
            if (Order.Status.ORDERED.name().equals(o.getStatus())) {
                newOrders.add(o);
            } else if (Order.Status.IN_PROGRESS.name().equals(o.getStatus())) {
                inProgressOrders.add(o);
            } else if (Order.Status.DONE.name().equals(o.getStatus())) {
                doneOrders.add(o);
            }
        }
        updateOrder(ordersListFragments[0], newOrders);
        updateOrder(ordersListFragments[1], inProgressOrders);
        updateOrder(ordersListFragments[2], doneOrders);
    }

    private void updateOrder(OrdersListFragment fragment, ArrayList<Order> list) {
        if (fragment != null) {
            fragment.update(list);
        }
    }

    @Override
    public int getCount() {
        return PAGE_COUNT;
    }

    @Override
    public Fragment getItem(int position) {
        if (ordersListFragments[position] == null) {
            ordersListFragments[position] = new OrdersListFragment();
        }
        switch (position) {
            case 0:
                ordersListFragments[position].update(newOrders);
                break;
            case 1:
                ordersListFragments[position].update(inProgressOrders);
                break;
            case 2:
                ordersListFragments[position].update(doneOrders);
                break;
        }
        return ordersListFragments[position];
    }

    @Override
    public CharSequence getPageTitle(int position) {
        // Generate title based on item position
        return tabTitles[position];
    }
}
