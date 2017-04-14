package com.codepath.hungrybird.consumer.fragments;

import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.codepath.hungrybird.R;
import com.codepath.hungrybird.chef.fragments.MyOfferingsFragment;
import com.codepath.hungrybird.databinding.ConsumerGalleryChefDishesDetailViewBinding;
import com.codepath.hungrybird.model.Dish;
import com.codepath.hungrybird.model.User;
import com.codepath.hungrybird.network.ParseClient;
import com.parse.ParseFile;

/**
 * Created by DhwaniShah on 4/13/17.
 */

public class ConsumerChefDishesDetailFragment extends Fragment {

    public static final String TAG = ConsumerChefDishesDetailFragment.class.getSimpleName();
    public static final String DISH_ID = "DISH_ID";
    public static final String CHEF_ID = "CHEF_ID";
    public static final String FRAGMENT_TAG = "FILTER_FRAGMENT_TAG";

    ParseClient parseClient = ParseClient.getInstance();
    ConsumerGalleryChefDishesDetailViewBinding binding;

    Dish currentDish;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate(inflater, R.layout.consumer_gallery_chef_dishes_detail_view, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        String id = getArguments().getString(DISH_ID);
        parseClient.getUserById(getArguments().getString(CHEF_ID), new ParseClient.UserListener() {
            @Override
            public void onSuccess(User user) {
                ParseFile chefProfilePic = user.getProfileImage();
                if (chefProfilePic != null && chefProfilePic.getUrl() != null) {
                    String imgUrl = chefProfilePic.getUrl();
                    Glide.with(getContext())
                            .load(imgUrl)
                            .into(binding.chefProfilePicIv);
                }
                binding.chefNameTv.setText(user.getUsername());
            }

            @Override
            public void onFailure(Exception e) {

            }
        });
        parseClient.getDishById(id, new ParseClient.DishListener() {
            @Override
            public void onSuccess(Dish dish) {
                Log.i(TAG, dish.toString());
                currentDish = dish;
                ParseFile dishPic = currentDish.getPrimaryImage();
                if (dishPic != null && dishPic.getUrl() != null) {
                    String imgUrl = dishPic.getUrl();
                    Glide.with(getContext())
                            .load(imgUrl)
                            .into(binding.selectedDishPicIv);
                }

                binding.dishTitle.setText(currentDish.getTitle());
                binding.dishPrice.setText("$" + String.valueOf(currentDish.getPrice()));
                binding.dishServingSize.setText(String.valueOf(currentDish.getServingSize()));
                //        binding.dishVegOrNonveg.setText(currentDish.getTitle());
                //        binding.dishAllergen.setText(currentDish.getTitle());
                //        binding.dishSpiceLevel.setText(currentDish.getTitle());
                binding.dishDescription.setText(currentDish.getDescription());
            }

            @Override
            public void onFailure(Exception e) {
                Toast.makeText(getContext(), "There was an error getting data.", Toast.LENGTH_LONG).show();
            }
        });

        MyOfferingsFragment myOfferingsFragment = new MyOfferingsFragment();
        FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
        Bundle bundle = new Bundle();
        bundle.putString(MyOfferingsFragment.CHEF_ID, getArguments().getString(CHEF_ID));
        myOfferingsFragment.setArguments(bundle);
        fragmentManager.beginTransaction()
                .replace(binding.moreDishesFromCurrentChefFl.getId(), myOfferingsFragment).commit();
    }
}