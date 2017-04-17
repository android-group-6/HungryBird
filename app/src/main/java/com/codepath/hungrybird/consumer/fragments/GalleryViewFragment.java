package com.codepath.hungrybird.consumer.fragments;


import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.widget.LinearLayoutManager;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.codepath.hungrybird.R;
import com.codepath.hungrybird.consumer.adapters.GallerySnapListContainerAdapter;
import com.codepath.hungrybird.databinding.ConsumerGalleryViewBinding;
import com.codepath.hungrybird.model.Dish;
import com.codepath.hungrybird.model.DishList;
import com.codepath.hungrybird.network.ParseClient;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GalleryViewFragment extends Fragment {
    public static final String TAG = GalleryViewFragment.class.getSimpleName();

    public static final String FRAGMENT_TAG = "FILTER_FRAGMENT_TAG";

    ConsumerGalleryViewBinding binding;
    private boolean mHorizontal = true;
    Map<Dish.Cuisine, List<Dish>> cuisine2Dishes = new HashMap<>();
    List<Dish.Cuisine> allCuisines;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        allCuisines = Arrays.asList(Dish.Cuisine.values());
        for (Dish.Cuisine cuisine : allCuisines) {
            cuisine2Dishes.put(cuisine, new ArrayList<>());
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate(inflater, R.layout.consumer_gallery_view, container, false);
        binding.recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        binding.recyclerView.setHasFixedSize(true);
        loadAllTopCuisineDishes();
        return binding.getRoot();
    }

    private void setupAdapter() {
        GallerySnapListContainerAdapter gallerySnapListContainerAdapter = new GallerySnapListContainerAdapter(getActivity());
        for (Dish.Cuisine cuisine : allCuisines) {
            List<Dish> dishes = cuisine2Dishes.get(cuisine);
            if (dishes != null && !dishes.isEmpty()) {
                gallerySnapListContainerAdapter.addSnap(new DishList(Gravity.CENTER_HORIZONTAL, cuisine.getCuisineValue(), dishes));
            }
        }
        binding.recyclerView.setAdapter(gallerySnapListContainerAdapter);
    }

    private void loadAllTopCuisineDishes() {
        for (Dish.Cuisine cuisine : allCuisines) {
            loadIndianTopCuisineDishes(cuisine);
        }
    }

    private void loadIndianTopCuisineDishes(Dish.Cuisine cuisine) {
        ParseClient.getInstance().getDishesByCuisine(cuisine, new ParseClient.DishListListener() {
            @Override
            public void onSuccess(List<Dish> dishes) {
                List<Dish> topDishes = cuisine2Dishes.get(cuisine);
                topDishes.clear();
                topDishes.addAll(dishes);
                setupAdapter();
            }

            @Override
            public void onFailure(Exception e) {

            }
        });

    }

//    @Override
//    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
//        inflater.inflate(R.menu.gallery_view_menu, menu);
//        super.onCreateOptionsMenu(menu, inflater);
//    }

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
