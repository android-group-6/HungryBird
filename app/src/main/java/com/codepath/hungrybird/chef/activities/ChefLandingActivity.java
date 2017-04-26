package com.codepath.hungrybird.chef.activities;

import android.content.Intent;
import android.content.res.Configuration;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.codepath.hungrybird.R;
import com.codepath.hungrybird.chef.adapters.DishArrayAdapter;
import com.codepath.hungrybird.chef.fragments.ChefOrdersViewFragment;
import com.codepath.hungrybird.chef.fragments.ContactDetailsFragment;
import com.codepath.hungrybird.chef.fragments.DishAddEditFragment;
import com.codepath.hungrybird.chef.fragments.MyOfferingsFragment;
import com.codepath.hungrybird.chef.fragments.MyRegisterFragment;
import com.codepath.hungrybird.common.LoginActivity;
import com.codepath.hungrybird.consumer.fragments.OrderDetailsFragment;
import com.codepath.hungrybird.consumer.fragments.OrderHistoryFramgent;
import com.codepath.hungrybird.databinding.ActivityChefLandingBinding;
import com.codepath.hungrybird.model.Dish;
import com.codepath.hungrybird.model.User;
import com.parse.ParseFile;
import com.parse.ParseInstallation;
import com.parse.ParseUser;

import jp.wasabeef.glide.transformations.CropCircleTransformation;

public class ChefLandingActivity extends AppCompatActivity implements DishArrayAdapter.DishSelected, OrderHistoryFramgent.OnOrderSelected {
    public static final String TAG = ChefLandingActivity.class.getSimpleName();

    private ActivityChefLandingBinding binding;
    private DrawerLayout mDrawer;
    private Toolbar toolbar;
    private NavigationView nvDrawer;
    ActionBarDrawerToggle drawerToggle;
    private TextView userNameTv;
    private TextView userEmailTv;
    private ImageView imageView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        User currentUser = new User(ParseUser.getCurrentUser());
        Toast.makeText(ChefLandingActivity.this, "Welcome " + currentUser.getUsername(), Toast.LENGTH_LONG).show();
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

        //Set user id and image
        View headerLayout = nvDrawer.getHeaderView(0);
        imageView = (ImageView) headerLayout.findViewById(R.id.navigation_drawer_user_account_picture_profile);
        userNameTv = (TextView) headerLayout.findViewById(R.id.navigation_drawer_account_information_display_name);
        userEmailTv = (TextView) headerLayout.findViewById(R.id.navigation_drawer_account_information_email);
        userNameTv.setText(currentUser.getUsername());
        userEmailTv.setText(currentUser.getEmail());
        final ParseFile parseFile = currentUser.getProfileImage();
        if (parseFile != null && TextUtils.isEmpty(parseFile.getUrl()) == false) {
            Glide.with(this).load(parseFile.getUrl()).bitmapTransform(new CropCircleTransformation(this))
                    .into(imageView);
        }


        // Set the default fragment
        final FragmentManager fragmentManager = getSupportFragmentManager();
        addBackButtonToToolbar();
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
            // Insert the fragment by replacing any existing fragment
            fragmentManager.beginTransaction().replace(R.id.flContent, new DishAddEditFragment()).addToBackStack(null).commit();
        });
        mDrawer.closeDrawers();
    }

    private void addBackButtonToToolbar() {
        getSupportFragmentManager().addOnBackStackChangedListener(new FragmentManager.OnBackStackChangedListener() {
            @Override
            public void onBackStackChanged() {
                if (getSupportFragmentManager().getBackStackEntryCount() > 0) {
                    getSupportActionBar().setDisplayHomeAsUpEnabled(true); // show back button
                    toolbar.setNavigationOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            onBackPressed();
                        }
                    });
                } else {
                    //show hamburger
                    getSupportActionBar().setDisplayHomeAsUpEnabled(false);
                    drawerToggle.syncState();
                    toolbar.setNavigationOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            mDrawer.openDrawer(GravityCompat.START);
                        }
                    });
                }
            }
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
                        this.finish();
                        Intent i = new Intent(ChefLandingActivity.this, LoginActivity.class);

                        startActivity(i);
                    } else {
                        Toast.makeText(ChefLandingActivity.this, "Logout failed... " + e.getMessage(), Toast.LENGTH_LONG).show();
                        e.printStackTrace();
                    }
                });
                ParseInstallation.getCurrentInstallation().deleteInBackground();
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
    public void onDishSelected(Dish dish, boolean fromChefPage) {
        // Insert the fragment by replacing any existing fragment
        FragmentManager fragmentManager = getSupportFragmentManager();
        Bundle bundle = new Bundle();
        bundle.putString(DishAddEditFragment.DISH_ID, dish.getObjectId());
        DishAddEditFragment dishAddEditFragment = new DishAddEditFragment();
        dishAddEditFragment.setArguments(bundle);
        fragmentManager.beginTransaction().replace(R.id.flContent, dishAddEditFragment).addToBackStack(null).commit();

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
        orderDetailsFragment.setTargetFragment(fragmentManager.findFragmentById(R.id.flContent), 10);
        orderDetailsFragment.setArguments(bundle);
        fragmentManager.beginTransaction().add(R.id.flContent, orderDetailsFragment).addToBackStack(null).commit();

    }

    @Override
    public void onBackPressed() {
        if (mDrawer.isDrawerOpen(Gravity.LEFT)) {
            mDrawer.closeDrawers();
            return;
        }
        super.onBackPressed();
    }
}
