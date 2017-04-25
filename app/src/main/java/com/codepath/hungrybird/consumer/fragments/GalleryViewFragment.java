package com.codepath.hungrybird.consumer.fragments;


import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.airbnb.lottie.LottieComposition;
import com.airbnb.lottie.OnCompositionLoadedListener;
import com.codepath.hungrybird.R;
import com.codepath.hungrybird.consumer.activities.GalleryActivity;
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

import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

public class GalleryViewFragment extends Fragment {
    public static final String TAG = GalleryViewFragment.class.getSimpleName();

    ConsumerGalleryViewBinding binding;
    Map<String, List<Dish>> cuisine2Dishes = new HashMap<>();
    List<Dish.Cuisine> allCuisines;

    private static final String TRENDING = "trending";
    private static final String NEARBY = "nearby";

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        allCuisines = Arrays.asList(Dish.Cuisine.values());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate(inflater, R.layout.consumer_gallery_view, container, false);
        binding.recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        binding.recyclerView.setHasFixedSize(true);
        binding.animationView.setVisibility(View.VISIBLE);
        String assetName = "lottie_loading.json";
        LottieComposition.Factory.fromAssetFileName(this.getContext(), assetName,
                new OnCompositionLoadedListener() {
                    @Override public void onCompositionLoaded(LottieComposition composition) {
                        setComposition(composition, assetName);
                    }
                });
        loadAllTopCuisineDishes();

        return binding.getRoot();
    }

    void setComposition(LottieComposition composition, String name) {
        binding.animationView.setComposition(composition);
        binding.animationView.playAnimation();
        binding.animationView.loop(true);
    }

    @Override
    public void onResume() {
        super.onResume();
        ((GalleryActivity) getActivity()).setToolbarTitle("What's your favorite cuisine?");
    }

    private void setupAdapter(Map<String, List<Dish>> map) {
        GallerySnapListContainerAdapter gallerySnapListContainerAdapter = new GallerySnapListContainerAdapter(getActivity());
        List<String> orderedSnaps = Arrays.asList(
                TRENDING,
                NEARBY,
                Dish.Cuisine.INDIAN.getCuisineValue(),
                Dish.Cuisine.ITALIAN.getCuisineValue(),
                Dish.Cuisine.MEXICAN.getCuisineValue(),
                Dish.Cuisine.CHINESE.getCuisineValue());
        for (String snap : orderedSnaps) {
            List<Dish> dishes = map.get(snap);
            if (dishes != null && !dishes.isEmpty()) {
                gallerySnapListContainerAdapter.addSnap(new DishList(Gravity.CENTER_HORIZONTAL, snap, dishes));
            }
        }
        binding.recyclerView.setAdapter(gallerySnapListContainerAdapter);
    }

    private void loadAllTopCuisineDishes() {
        Observable.create(new Observable.OnSubscribe<List<Dish>>() {
            @Override
            public void call(Subscriber<? super List<Dish>> subscriber) {
                ParseClient.getInstance().getDishesByCuisines(allCuisines, new ParseClient.DishListListener() {
                    @Override
                    public void onSuccess(List<Dish> dishes) {
                        subscriber.onNext(dishes);
                        subscriber.onCompleted();
                    }

                    @Override
                    public void onFailure(Exception e) {
                        subscriber.onError(e);
                    }
                });
            }
        }).flatMap(new Func1<List<Dish>, Observable<Map<String, List<Dish>>>>() {
            @Override
            public Observable<Map<String, List<Dish>>> call(List<Dish> dishes) {
                Map<String, List<Dish>> map = new HashMap<String, List<Dish>>();
                map.put(TRENDING, new ArrayList<>());
                map.put(NEARBY, new ArrayList<>());
                for (Dish d : dishes) {
                    List<Dish> list = map.get(d.getCuisine());
                    if (list == null) {
                        list = new ArrayList<>();
                        map.put(d.getCuisine(), list);
                    }
                    list.add(d);
                    if (isTrending(d)) {
                        map.get(TRENDING).add(d);
                    }
                    if (isNearBy(d)) {
                        map.get(NEARBY).add(d);
                    }
                }
                return Observable.just(map);

            }
        }).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<Map<String, List<Dish>>>() {
                    @Override
                    public void onCompleted() {
                        binding.animationView.loop(false);
                        binding.animationView.setVisibility(View.GONE);
                        binding.nestedScroll.setVisibility(View.VISIBLE);
                    }

                    @Override
                    public void onError(Throwable e) {
                        binding.animationView.loop(false);
                        binding.animationView.setVisibility(View.GONE);
                        binding.nestedScroll.setVisibility(View.VISIBLE);
                    }

                    @Override
                    public void onNext(Map<String, List<Dish>> map) {
                        setupAdapter(map);
                    }
                });
    }

    private boolean isTrending(Dish d) {
        if (d == null || d.getTitle() == null) {
            return false;
        }
        return (d.getTitle().length() % 2 != 0);
    }

    private boolean isNearBy(Dish d) {
        if (d == null || d.getTitle() == null) {
            return false;
        }
        return (d.getTitle().length() % 2 == 0);
    }
}
