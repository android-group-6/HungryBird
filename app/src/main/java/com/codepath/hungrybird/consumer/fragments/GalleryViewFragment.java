package com.codepath.hungrybird.consumer.fragments;


import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.widget.LinearLayoutManager;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.codepath.hungrybird.R;
import com.codepath.hungrybird.consumer.adapters.GallerySnapListContainerAdapter;
import com.codepath.hungrybird.databinding.ConsumerGalleryViewBinding;
import com.codepath.hungrybird.model.App;
import com.codepath.hungrybird.model.Dish;
import com.codepath.hungrybird.model.Snap;
import com.codepath.hungrybird.network.ParseClient;

import java.util.ArrayList;
import java.util.List;

public class GalleryViewFragment extends Fragment {
    public static final String TAG = GalleryViewFragment.class.getSimpleName();

    public static final String FRAGMENT_TAG = "FILTER_FRAGMENT_TAG";

    ConsumerGalleryViewBinding binding;
    private boolean mHorizontal = true;
    List<Dish> mTopIndianDishes, mTopItalianDishes;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        mTopIndianDishes = new ArrayList<>();
        mTopItalianDishes = new ArrayList<>();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        //return inflater.inflate(R.layout.consumer_gallery_view, container, false);
        binding = DataBindingUtil.inflate(inflater, R.layout.consumer_gallery_view, container, false);
        binding.recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        binding.recyclerView.setHasFixedSize(true);
        loadTopIndianDishes();
        setupAdapter();
        return binding.getRoot();
    }

    private void setupAdapter() {
        List<App> apps = getApps();

        GallerySnapListContainerAdapter snapAdapter = new GallerySnapListContainerAdapter();
        if (mHorizontal) {
            snapAdapter.addSnap(new Snap(Gravity.CENTER_HORIZONTAL, "Trending", apps));
            snapAdapter.addSnap(new Snap(Gravity.START, "Nearby", apps));
            snapAdapter.addSnap(new Snap(Gravity.END, "Indian", apps));
            snapAdapter.addSnap(new Snap(Gravity.CENTER, "Italian", apps));
        } else {
            snapAdapter.addSnap(new Snap(Gravity.CENTER_VERTICAL, "Snap center", apps));
            snapAdapter.addSnap(new Snap(Gravity.TOP, "Snap top", apps));
            snapAdapter.addSnap(new Snap(Gravity.BOTTOM, "Snap bottom", apps));
        }

        binding.recyclerView.setAdapter(snapAdapter);
    }

    private void loadTopIndianDishes() {
        ParseClient.getInstance().getDishesByCuisine(Dish.Cuisine.INDIAN, new ParseClient.DishListListener() {
            @Override
            public void onSuccess(List<Dish> dishes) {
//                String firstItemId = dishes.get(0).getCuisine() + " " + dishes.get(0).getObjectId();
//                String firstItemId1 = dishes.get(1).getCuisine() + " " + dishes.get(1).getObjectId();
//                Log.e("top_indian_dish", firstItemId + "\n" + firstItemId1);
                mTopIndianDishes = dishes;
            }

            @Override
            public void onFailure(Exception e) {

            }
        });
        //return null;
    }

    private List<App> getApps() {
        List<App> apps = new ArrayList<>();
        apps.add(new App("Google+", R.drawable.ic_launcher, 4.6f));
        apps.add(new App("Gmail", R.drawable.ic_launcher, 4.8f));
        apps.add(new App("Inbox", R.drawable.ic_launcher, 4.5f));
        apps.add(new App("Google Keep", R.drawable.ic_launcher, 4.2f));
        apps.add(new App("Google Drive", R.drawable.ic_launcher, 4.6f));
        apps.add(new App("Hangouts", R.drawable.ic_launcher, 3.9f));
        apps.add(new App("Google Photos", R.drawable.ic_launcher, 4.6f));
        apps.add(new App("Messenger", R.drawable.ic_launcher, 4.2f));
        apps.add(new App("Sheets", R.drawable.ic_launcher, 4.2f));
        apps.add(new App("Slides", R.drawable.ic_launcher, 4.2f));
        apps.add(new App("Docs", R.drawable.ic_launcher, 4.2f));
        return apps;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.gallery_view_menu, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

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
