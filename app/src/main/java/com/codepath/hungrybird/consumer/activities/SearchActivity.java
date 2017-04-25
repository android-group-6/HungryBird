package com.codepath.hungrybird.consumer.activities;

import android.app.Activity;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.codepath.hungrybird.R;
import com.codepath.hungrybird.chef.adapters.DishArrayAdapter;
import com.codepath.hungrybird.databinding.ActivitySearchBinding;
import com.codepath.hungrybird.model.Dish;
import com.codepath.hungrybird.network.ParseClient;

import java.util.ArrayList;
import java.util.List;

public class SearchActivity extends AppCompatActivity implements DishArrayAdapter.DishSelected {

    public static final String TAG = SearchActivity.class.getSimpleName();

    ActivitySearchBinding binding;
    private Toolbar toolbar;
    private DishArrayAdapter dishArrayAdapter;
    private List<Dish> dishesArrayList = new ArrayList<>();

    private String query;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        binding = DataBindingUtil.setContentView(this, R.layout.activity_search);
        // Find the toolbar view inside the activity layout
        toolbar = binding.activityGalleryToolbar.toolbar;
        // Sets the Toolbar to act as the ActionBar for this Activity window.
        // Make sure the toolbar exists in the activity and is not null
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        dishArrayAdapter = new DishArrayAdapter(SearchActivity.this, dishesArrayList);
        binding.content.chefMyoffersingsLv.setAdapter(dishArrayAdapter);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        binding.content.chefMyoffersingsLv.setLayoutManager(linearLayoutManager);
        //Added divider between line items
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(binding.content.chefMyoffersingsLv.getContext(), linearLayoutManager.getOrientation());
        binding.content.chefMyoffersingsLv.addItemDecoration(dividerItemDecoration);

        query = getIntent().getStringExtra("query");
        fetchDishes(query);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.gallery_view_menu, menu);
        MenuItem searchItem = menu.findItem(R.id.action_search);
        searchItem.expandActionView();
        final SearchView searchView = (SearchView) MenuItemCompat.getActionView(searchItem);

        MenuItemCompat.setOnActionExpandListener(searchItem, new MenuItemCompat.OnActionExpandListener() {
            @Override
            public boolean onMenuItemActionExpand(MenuItem item) {
                searchView.onActionViewExpanded();
                searchView.setQuery(query, true);
                return true;
            }

            @Override
            public boolean onMenuItemActionCollapse(MenuItem item) {
                return true;
            }
        });
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                fetchDishes(query);
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });
        return super.onCreateOptionsMenu(menu);
    }

    private void fetchDishes(String query) {
        ParseClient.getInstance().getDishesBySearchQuery(query, new ParseClient.DishListListener() {
            @Override
            public void onSuccess(List<Dish> dishes) {
                dishesArrayList.clear();
                dishesArrayList.addAll(dishes);
                dishArrayAdapter.notifyDataSetChanged();
                Log.d(TAG, "onSuccess: " + dishes);
            }

            @Override
            public void onFailure(Exception e) {
                e.printStackTrace();
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onDishSelected(Dish dish, boolean fromChefPage) {
        System.out.println(dish.getTitle());
        Intent resultIntent = new Intent();
        resultIntent.putExtra("dishId", dish.getObjectId());
        setResult(Activity.RESULT_OK, resultIntent);
        finish();
    }

}
