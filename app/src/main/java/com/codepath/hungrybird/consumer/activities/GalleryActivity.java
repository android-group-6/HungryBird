package com.codepath.hungrybird.consumer.activities;

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
import android.widget.EditText;
import android.widget.Toast;

import com.codepath.hungrybird.R;
import com.codepath.hungrybird.consumer.fragments.ConsumerCartFragment;
import com.codepath.hungrybird.consumer.fragments.ContactUsFragment;
import com.codepath.hungrybird.consumer.fragments.GalleryViewFragment;
import com.codepath.hungrybird.consumer.fragments.OrderHistoryFramgent;
import com.codepath.hungrybird.databinding.ActivityGalleryBinding;
import com.codepath.hungrybird.model.User;
import com.parse.ParseUser;
import com.stripe.android.Stripe;
import com.stripe.android.TokenCallback;
import com.stripe.android.model.Card;
import com.stripe.android.model.Token;

public class GalleryActivity extends AppCompatActivity {
    private ActivityGalleryBinding binding;
    private DrawerLayout mDrawer;
    private Toolbar toolbar;
    private NavigationView nvDrawer;
    ActionBarDrawerToggle drawerToggle;

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
                fragmentClass = ConsumerCartFragment.class;
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

    public void onPayNowButtonClick(View v) {
        EditText cardNumber = (EditText) v.getRootView().findViewById(R.id.cart_checkout_credit_num_et);
        Card card = new Card(cardNumber.getText().toString(), 12, 2018, "123");

        if (!card.validateCard()) {
            Toast.makeText(getApplicationContext(), "No ", Toast.LENGTH_LONG).show();
        } else {
            Toast.makeText(getApplicationContext(), "Done", Toast.LENGTH_LONG).show();
            Stripe stripe = new Stripe(getApplicationContext(), "pk_test_u4lZ9tWVhEoZVKVa6FFN5oei");
            stripe.createToken(
                    card,
                    new TokenCallback() {
                        public void onSuccess(Token token) {
                            // Send token to your server
                            Log.e("STRIPE_TOKEN", token.getId());
                            
                        }
                        public void onError(Exception error) {
                            // Show localized error message
                            Toast.makeText(getApplicationContext(),
                                    error.toString(),
                                    Toast.LENGTH_LONG
                            ).show();
                        }
                    }
            );
        }
    }

}
