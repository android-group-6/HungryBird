package com.codepath.hungrybird.consumer.activities;

import android.content.Intent;
import android.content.res.Configuration;
import android.databinding.DataBindingUtil;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.transition.Fade;
import android.transition.Transition;
import android.transition.TransitionInflater;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.codepath.hungrybird.R;
import com.codepath.hungrybird.chef.adapters.DishArrayAdapter;
import com.codepath.hungrybird.common.Transitions.DetailsTransition;
import com.codepath.hungrybird.consumer.adapters.GallerySnapListAdapter;
import com.codepath.hungrybird.consumer.fragments.CartFragment;
import com.codepath.hungrybird.consumer.fragments.ConsumerCheckoutFragment;
import com.codepath.hungrybird.consumer.fragments.ConsumerChefDishesDetailFragment;
import com.codepath.hungrybird.consumer.fragments.ContactUsFragment;
import com.codepath.hungrybird.consumer.fragments.GalleryViewFragment;
import com.codepath.hungrybird.consumer.fragments.OrderDetailsFragment;
import com.codepath.hungrybird.consumer.fragments.OrderHistoryFramgent;
import com.codepath.hungrybird.databinding.ActivityGalleryBinding;
import com.codepath.hungrybird.model.Dish;
import com.codepath.hungrybird.model.Order;
import com.codepath.hungrybird.model.User;
import com.codepath.hungrybird.network.ParseClient;
import com.parse.ParseFile;
import com.parse.ParseInstallation;
import com.parse.ParseUser;

import jp.wasabeef.glide.transformations.CropCircleTransformation;

public class GalleryActivity extends AppCompatActivity implements
        GallerySnapListAdapter.GalleryDishSelectedListener,
        DishArrayAdapter.DishSelected,
        OrderHistoryFramgent.OnOrderSelected,
        ConsumerChefDishesDetailFragment.CartListener,
        CartFragment.CartFragmentListener,
        ConsumerCheckoutFragment.CheckoutFragmentListener {
    private ActivityGalleryBinding binding;
    private DrawerLayout mDrawer;
    private Toolbar toolbar;
    private NavigationView nvDrawer;
    private ActionBarDrawerToggle drawerToggle;

    private int SEARCH_ACTIVITY_RESULT_CODE = 300;
    private TextView userNameTv;
    private TextView userEmailTv;
    private ImageView imageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        User currentUser = new User(ParseUser.getCurrentUser());
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


        mDrawer.addDrawerListener(drawerToggle);
        setupDrawerContent(nvDrawer);

        // Set the default fragment
        FragmentManager fragmentManager = getSupportFragmentManager();
        addBackButtonToToolbar();
        fragmentManager.beginTransaction().replace(R.id.flContent, new GalleryViewFragment()).commit();
        // Highlight the selected item has been done by NavigationView
        nvDrawer.getMenu().getItem(0).setChecked(true);
        // Set action bar title
        setTitle(nvDrawer.getMenu().getItem(0).getTitle());
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
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.gallery_view_menu, menu);
        MenuItem searchItem = menu.findItem(R.id.action_search);
        final SearchView searchView = (SearchView) MenuItemCompat.getActionView(searchItem);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                // perform query here

                // workaround to avoid issues with some emulators and keyboard devices firing twice if a keyboard enter is used
                // see https://code.google.com/p/android/issues/detail?id=24599
//                fetchBooks(query);
                Intent searchActivityIntent = new Intent(GalleryActivity.this, SearchActivity.class);
                searchActivityIntent.putExtra("query", query);
                startActivityForResult(searchActivityIntent, SEARCH_ACTIVITY_RESULT_CODE);
//                startActivity(searchActivityIntent);
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == SEARCH_ACTIVITY_RESULT_CODE && resultCode == RESULT_OK && data != null) {
            String dishId = data.getStringExtra("dishId");
            ParseClient.getInstance().getDishById(dishId, new ParseClient.DishListener() {
                @Override
                public void onSuccess(Dish dish) {
                    onDishSelected(dish, false);
                }

                @Override
                public void onFailure(Exception e) {
                    e.printStackTrace();
                }
            });
        }

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
                fragmentClass = ConsumerCheckoutFragment.class;
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
                ParseInstallation.getCurrentInstallation().deleteInBackground();
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
        //DishDetailsFragment dishDetailsFragment = new DishDetailsFragment();
        ConsumerChefDishesDetailFragment dishDetailsFragment = new ConsumerChefDishesDetailFragment();
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
        bundle.putString(ConsumerChefDishesDetailFragment.DISH_ID, dish.getObjectId());
        bundle.putString(ConsumerChefDishesDetailFragment.CHEF_ID, dish.getChef().getObjectId());
        dishDetailsFragment.setArguments(bundle);
        bundle.putString("CHEF_NAME", dish.getChef().getUsername());
        fragmentManager.beginTransaction()
                .addSharedElement(dishImage, "dishImageTransition")
                .addSharedElement(dishTitle, "dishTitleTransition")
                .addSharedElement(dishPrice, "dishPriceTransition")
                .replace(R.id.flContent, dishDetailsFragment).addToBackStack(null).commit();
    }

    @Override
    public void onDishSelected(Dish dish, boolean fromChefPage) {
        // Todo: Send to dish detail
        ConsumerChefDishesDetailFragment dishDetailsFragment = new ConsumerChefDishesDetailFragment();
        FragmentManager fragmentManager = getSupportFragmentManager();
        Bundle bundle = new Bundle();
        bundle.putString(ConsumerChefDishesDetailFragment.DISH_ID, dish.getObjectId());
        bundle.putString(ConsumerChefDishesDetailFragment.CHEF_ID, dish.getChef().getObjectId());
        bundle.putString("CHEF_NAME", dish.getChef().getUsername());
        dishDetailsFragment.setArguments(bundle);
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction().replace(R.id.flContent, dishDetailsFragment);
        if (fromChefPage == false) {
            fragmentTransaction.addToBackStack(null);
        }
        fragmentTransaction.commit();
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

    @Override
    public void onCartPressed(Order order) {
        // Send to cart fragment for the given order
        FragmentManager fragmentManager = getSupportFragmentManager();
        CartFragment cartFragment = new CartFragment();
        Bundle bundle = new Bundle();
        bundle.putString(CartFragment.OBJECT_ID, order.getObjectId());
        cartFragment.setArguments(bundle);
        fragmentManager.beginTransaction().replace(R.id.flContent, cartFragment)
                .addToBackStack(null).commit();
    }

    @Override
    public void onCheckoutListener(String orderId, String price) {
        Toast.makeText(getApplicationContext(), orderId + " | " + price, Toast.LENGTH_SHORT).show();
        // Send to cart fragment for the given order
        FragmentManager fragmentManager = getSupportFragmentManager();
        ConsumerCheckoutFragment consumerCheckoutFragment = new ConsumerCheckoutFragment();
        Bundle bundle = new Bundle();
        bundle.putString(ConsumerCheckoutFragment.ORDER_ID, orderId);
        bundle.putString(ConsumerCheckoutFragment.TOTAL_PRICE, price);
        consumerCheckoutFragment.setArguments(bundle);
        fragmentManager.beginTransaction().replace(R.id.flContent, consumerCheckoutFragment).addToBackStack(null)
                .commit();
    }

    @Override
    public void onPaymentSuccessfully() {
        Intent i = new Intent(this, GalleryActivity.class);
        finish();
        startActivity(i);
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
