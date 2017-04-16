package com.codepath.hungrybird.chef.activities;

import android.content.Intent;
import android.content.res.Configuration;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.codepath.hungrybird.R;
import com.codepath.hungrybird.chef.adapters.DishArrayAdapter;
import com.codepath.hungrybird.chef.fragments.ChefOrdersViewFragment;
import com.codepath.hungrybird.chef.fragments.ContactDetailsFragment;
import com.codepath.hungrybird.chef.fragments.DishAddEditFragment;
import com.codepath.hungrybird.chef.fragments.DishDetailsFragment;
import com.codepath.hungrybird.chef.fragments.MyOfferingsFragment;
import com.codepath.hungrybird.chef.fragments.MyRegisterFragment;
import com.codepath.hungrybird.common.LoginActivity;
import com.codepath.hungrybird.consumer.fragments.OrderDetailsFragment;
import com.codepath.hungrybird.consumer.fragments.OrderHistoryFramgent;
import com.codepath.hungrybird.databinding.ActivityChefLandingBinding;
import com.codepath.hungrybird.model.Dish;
import com.codepath.hungrybird.model.User;
import com.parse.ParseUser;

public class ChefLandingActivity extends AppCompatActivity implements DishArrayAdapter.DishSelected, OrderHistoryFramgent.OnOrderSelected {
    public static final String TAG = ChefLandingActivity.class.getSimpleName();

    private ActivityChefLandingBinding binding;
    private DrawerLayout mDrawer;
    private Toolbar toolbar;
    private NavigationView nvDrawer;
    ActionBarDrawerToggle drawerToggle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        User currentUser = new User(ParseUser.getCurrentUser());
        Toast.makeText(ChefLandingActivity.this, "Current User ... " + currentUser.getUsername(), Toast.LENGTH_LONG).show();
        binding = DataBindingUtil.setContentView(this, R.layout.activity_chef_landing);

        // Find the toolbar view inside the activity layout
        toolbar = binding.activityChefLandingToolbar.toolbar;
        // Sets the Toolbar to act as the ActionBar for this Activity window.
        // Make sure the toolbar exists in the activity and is not null
        setSupportActionBar(toolbar);
        // Find our drawer view
        mDrawer = binding.drawerLayout;
        drawerToggle = setupDrawerToggle();
        nvDrawer = binding.nvView;
        mDrawer.addDrawerListener(drawerToggle);
        setupDrawerContent(nvDrawer);

        // Set the default fragment
        final FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction().replace(R.id.flContent, new ChefOrdersViewFragment()).commit();
        // Highlight the selected item has been done by NavigationView
        nvDrawer.getMenu().getItem(0).setChecked(true);
        // Set action bar title
        setTitle(nvDrawer.getMenu().getItem(0).getTitle());
        getSupportFragmentManager().addOnBackStackChangedListener(() -> {
            Log.i(TAG, "back stack changed ");
            int backCount = getSupportFragmentManager().getBackStackEntryCount();
            if (backCount == 0) {
                getSupportActionBar().setDisplayHomeAsUpEnabled(false);
                drawerToggle.setDrawerIndicatorEnabled(true);
                // block where back has been pressed. since backstack is zero.
                mDrawer.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED);
            }
        });
        binding.activityChefLandingAddDishFab.setOnClickListener(v -> {
            Toast.makeText(this, "Add Clicked", Toast.LENGTH_SHORT).show();
            // Insert the fragment by replacing any existing fragment
            fragmentManager.beginTransaction().replace(R.id.flContent, new DishAddEditFragment()).addToBackStack(null).commit();
        });
    }

    private ActionBarDrawerToggle setupDrawerToggle() {
        // NOTE: Make sure you pass in a valid toolbar reference.  ActionBarDrawToggle() does not require it
        // and will not render the hamburger icon without it.
        return new ActionBarDrawerToggle(this, mDrawer, toolbar, R.string.drawer_open, R.string.drawer_close);
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        // Sync the toggle state after onRestoreInstanceState has occurred.
        drawerToggle.syncState();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        // Pass any configuration change to the drawer toggles
        drawerToggle.onConfigurationChanged(newConfig);
    }

    private void setupDrawerContent(NavigationView navigationView) {
        navigationView.setNavigationItemSelectedListener(menuItem -> {
            selectDrawerItem(menuItem);
            return true;
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (drawerToggle.onOptionsItemSelected(item)) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public void selectDrawerItem(MenuItem menuItem) {
        // Create a new fragment and specify the fragment to show based on nav item clicked
        Fragment fragment = null;
        Class fragmentClass = ChefOrdersViewFragment.class;
        switch (menuItem.getItemId()) {
            case R.id.chef_drawer_my_offerings_mi:
                fragmentClass = MyOfferingsFragment.class;
                break;
            case R.id.chef_contact_details_mi:
                fragmentClass = ContactDetailsFragment.class;
                break;
            case R.id.chef_drawer_my_register_mi:
                fragmentClass = MyRegisterFragment.class;
                break;
            case R.id.chef_logout_mi:

                ParseUser.logOutInBackground(e -> {
                    if (e == null) {
                        Toast.makeText(ChefLandingActivity.this, "Logout Successful ... ", Toast.LENGTH_LONG).show();
                        this.finish();
                        Intent i = new Intent(ChefLandingActivity.this, LoginActivity.class);

                        startActivity(i);
                    } else {
                        Toast.makeText(ChefLandingActivity.this, "Logout failed ... " + e.getMessage(), Toast.LENGTH_LONG).show();
                        e.printStackTrace();
                    }
                });
                return;
            default:
                fragmentClass = ChefOrdersViewFragment.class;
        }

        try {
            fragment = (Fragment) fragmentClass.newInstance();
        } catch (Exception e) {
            e.printStackTrace();
        }

        // Insert the fragment by replacing any existing fragment
        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction().replace(R.id.flContent, fragment).commit();

        // Highlight the selected item has been done by NavigationView
        menuItem.setChecked(true);
        // Set action bar title
        setTitle(menuItem.getTitle());
        // Close the navigation drawer
        mDrawer.closeDrawers();
        mDrawer.addDrawerListener(new DrawerLayout.DrawerListener() {
            @Override
            public void onDrawerSlide(View drawerView, float slideOffset) {

            }

            @Override
            public void onDrawerOpened(View drawerView) {
//                drawerToggle.setDrawerIndicatorEnabled(true);
            }

            @Override
            public void onDrawerClosed(View drawerView) {
            }

            @Override
            public void onDrawerStateChanged(int newState) {

            }
        });

        mDrawer.setOnClickListener(v -> {
            Toast.makeText(this, "ToastText", Toast.LENGTH_SHORT).show();
        });
    }

    @Override
    public void onDishSelected(Dish dish) {
        // Insert the fragment by replacing any existing fragment
        FragmentManager fragmentManager = getSupportFragmentManager();
        Bundle bundle = new Bundle();
        bundle.putString(DishDetailsFragment.DISH_ID, dish.getObjectId());
        DishDetailsFragment dishDetailsFragment = new DishDetailsFragment();
        dishDetailsFragment.setArguments(bundle);
        fragmentManager.beginTransaction().replace(R.id.flContent, dishDetailsFragment).addToBackStack(null).commit();

        // update the actionbar to show the up carat/affordanced
//        drawerToggle.setDrawerIndicatorEnabled(false);
//        mDrawer.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
//        getSupportActionBar().setDisplayShowHomeEnabled(true);
//        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }


    @Override
    public void onOrderSelected(String orderId) {
        // Insert the fragment by replacing any existing fragment
        FragmentManager fragmentManager = getSupportFragmentManager();
        Bundle bundle = new Bundle();
        bundle.putString(OrderDetailsFragment.OBJECT_ID, orderId);
        OrderDetailsFragment orderDetailsFragment = new OrderDetailsFragment();
        orderDetailsFragment.setArguments(bundle);
        fragmentManager.beginTransaction().replace(R.id.flContent, orderDetailsFragment).addToBackStack(null).commit();

    }
}
