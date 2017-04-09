package com.codepath.hungrybird.chef.adapters;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v7.widget.RecyclerView;

import com.codepath.hungrybird.chef.fragments.MyOfferingsFragment;
import com.codepath.hungrybird.chef.fragments.OrdersListFragment;

/**
 * Created by gauravb on 3/30/17.
 */

public class ChefOrdersFragmentPagerAdapter extends FragmentPagerAdapter {
    final int PAGE_COUNT = 3;
    private String tabTitles[] = new String[]{"NEW", "IN PROGRESS", "DONE"};
    private MyOfferingsFragment ordersListFragments[] = new MyOfferingsFragment[]{null,null, null};
    private Context context;
    RecyclerView.OnScrollListener onScrollListener;

    public ChefOrdersFragmentPagerAdapter(FragmentManager fm, Context context, RecyclerView.OnScrollListener onScrollListener) {
        super(fm);
        this.context = context;
        this.onScrollListener = onScrollListener;
    }

    @Override
    public int getCount() {
        return PAGE_COUNT;
    }

    @Override
    public Fragment getItem(int position) {
        if (ordersListFragments[position] == null) {
            ordersListFragments[position] = new MyOfferingsFragment();
        }
        return ordersListFragments[position];
    }

    @Override
    public CharSequence getPageTitle(int position) {
        // Generate title based on item position
        return tabTitles[position];
    }
}
