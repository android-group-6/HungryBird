package com.codepath.hungrybird.consumer.activities;

import android.content.res.Configuration;
import android.databinding.DataBindingUtil;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.transition.Fade;
import android.transition.Transition;
import android.transition.TransitionInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.codepath.hungrybird.R;
import com.codepath.hungrybird.chef.fragments.DishDetailsFragment;
import com.codepath.hungrybird.common.Transitions.DetailsTransition;
import com.codepath.hungrybird.consumer.adapters.GallerySnapListAdapter;
import com.codepath.hungrybird.consumer.fragments.ContactUsFragment;
import com.codepath.hungrybird.consumer.fragments.GalleryViewFragment;
import com.codepath.hungrybird.consumer.fragments.OrderDetailsFragment;
import com.codepath.hungrybird.consumer.fragments.OrderHistoryFramgent;
import com.codepath.hungrybird.consumer.fragments.SimpsonsFragment;
import com.codepath.hungrybird.databinding.ActivityGalleryBinding;
import com.codepath.hungrybird.model.Dish;
import com.codepath.hungrybird.model.User;
import com.parse.ParseUser;

public class GalleryActivity extends AppCompatActivity implements GallerySnapListAdapter.GalleryDishSelectedListener, OrderHistoryFramgent.OnOrderSelected {
    private ActivityGalleryBinding binding;
    private DrawerLayout mDrawer;
    private Toolbar toolbar;
    private NavigationView nvDrawer;
    private ActionBarDrawerToggle drawerToggle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        User currentUser = new User(ParseUser.getCurrentUser());
        Toast.makeText(GalleryActivity.this, "Current User ... " + currentUser.getUsername(), Toast.LENGTH_LONG).show();
        binding = DataBindingUtil.setContentView(this, R.layout.activity_gallery);

        // Find the toolbar view inside the activity layout
        toolbar = binding.activityGalleryToolbar.toolbar;
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
        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction().replace(R.id.flContent, new GalleryViewFragment()).commit();
        // Highlight the selected item has been done by NavigationView
        nvDrawer.getMenu().getItem(0).setChecked(true);
        // Set action bar title
        setTitle(nvDrawer.getMenu().getItem(0).getTitle());
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
        navigationView.setNavigationItemSelectedListener(
                new NavigationView.OnNavigationItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(MenuItem menuItem) {
                        selectDrawerItem(menuItem);
                        return true;
                    }
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
        Class fragmentClass;
        switch (menuItem.getItemId()) {
            case R.id.chef_drawer_my_offerings_mi:
                fragmentClass = OrderHistoryFramgent.class;
                break;
            case R.id.chef_contact_details_mi:
                fragmentClass = SimpsonsFragment.class;
                break;
            case R.id.chef_drawer_my_register_mi:
                fragmentClass = ContactUsFragment.class;
                break;
            case R.id.chef_logout_mi:
                ParseUser.logOutInBackground(e -> {
                    if (e == null) {
                        Toast.makeText(GalleryActivity.this, "Logout Successful", Toast.LENGTH_LONG).show();
                        // remove from shared preference
                        if (getFragmentManager().getBackStackEntryCount() == 0) {
                            this.finish();
                        } else {
                            getFragmentManager().popBackStack();
                        }
                    } else {
                        e.printStackTrace();
                    }
                });
                // break;
            default:
                fragmentClass = GalleryViewFragment.class;
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
    }

    @Override
    public void onDishSelected(View v, Dish dish) {
        ImageView dishImage = (ImageView) v.findViewById(R.id.imageView);
        TextView dishTitle = (TextView) v.findViewById(R.id.nameTextView);
        TextView dishPrice = (TextView) v.findViewById(R.id.ratingTextView);
        DishDetailsFragment dishDetailsFragment = new DishDetailsFragment();
        GalleryViewFragment galleryViewFragment = new GalleryViewFragment();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Transition explodeTransform = TransitionInflater.from(this).
                    inflateTransition(android.R.transition.explode);
            dishImage.setTransitionName(getString(R.string.activity_image_trans));
            dishTitle.setTransitionName(getString(R.string.activity_text_trans));
            dishPrice.setTransitionName(getString(R.string.activity_mixed_trans));
            dishDetailsFragment.setSharedElementEnterTransition(new DetailsTransition());
            dishDetailsFragment.setEnterTransition(new Fade());
            // TODO: Exit transaction changes image size on second frag. Why?
            dishDetailsFragment.setExitTransition(explodeTransform);
            dishDetailsFragment.setSharedElementReturnTransition(new DetailsTransition());
        }

        FragmentManager fragmentManager = getSupportFragmentManager();
        Bundle bundle = new Bundle();
        bundle.putString(DishDetailsFragment.DISH_ID, dish.getObjectId());
        dishDetailsFragment.setArguments(bundle);
        fragmentManager.beginTransaction()
                .addSharedElement(dishImage, "dishImageTransition")
                .addSharedElement(dishTitle, "dishTitleTransition")
                .addSharedElement(dishPrice, "dishPriceTransition")
                .replace(R.id.flContent, dishDetailsFragment).addToBackStack(null).commit();
    }

    @Override
    public void onOrderSelected(String orderId) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        OrderDetailsFragment orderDetailsFragment = new OrderDetailsFragment();
        Bundle bundle = new Bundle();
        bundle.putString(OrderDetailsFragment.OBJECT_ID, orderId);
        orderDetailsFragment.setArguments(bundle);
        fragmentManager.beginTransaction().replace(R.id.flContent, orderDetailsFragment)
                .addToBackStack(null).commit();
    }
}
