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
    private String tabTitles[] = new String[]{"NEW", "IN PROGRESS", "COMPLETE"};
    private OrdersListFragment ordersListFragments[] = new OrdersListFragment[]{null, null, null};
    private ArrayList<Order>[] lists = new ArrayList[]{new ArrayList<>(), new ArrayList<>(), new ArrayList<>()};

    private Context context;
    RecyclerView.OnScrollListener onScrollListener;

    public ChefOrdersFragmentPagerAdapter(FragmentManager fm, Context context, RecyclerView.OnScrollListener onScrollListener) {
        super(fm);
        this.context = context;
        this.onScrollListener = onScrollListener;
    }

    public void updateOrders(List<Order> orders) {
        lists[0].clear();
        lists[1].clear();
        lists[2].clear();
        for (Order o : orders) {
            if (Order.Status.ORDERED.name().equals(o.getStatus())) {
                lists[0].add(o);
            } else if (Order.Status.IN_PROGRESS.name().equals(o.getStatus())) {
                lists[1].add(o);
            } else if (Order.Status.COMPLETE.name().equals(o.getStatus()) ||
                    Order.Status.CANCELLED.name().equals(o.getStatus()) ||
                    Order.Status.READY_FOR_PICKUP.name().equals(o.getStatus()) ||
                    Order.Status.OUT_FOR_DELIVERY.name().equals(o.getStatus())) {
                lists[2].add(o);
            }
        }
        updateOrder(ordersListFragments[0], lists[0]);
        updateOrder(ordersListFragments[1], lists[1]);
        updateOrder(ordersListFragments[2], lists[2]);
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
                ordersListFragments[position].update(lists[0]);
                break;
            case 1:
                ordersListFragments[position].update(lists[1]);
                break;
            case 2:
                ordersListFragments[position].update(lists[2]);
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
