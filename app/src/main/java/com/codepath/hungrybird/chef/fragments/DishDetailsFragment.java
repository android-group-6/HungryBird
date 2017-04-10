package com.codepath.hungrybird.chef.fragments;


import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.codepath.hungrybird.R;
import com.codepath.hungrybird.consumer.fragments.FilterFragment;
import com.codepath.hungrybird.databinding.ChefDishDetailsFragmentBinding;
import com.codepath.hungrybird.model.Dish;
import com.codepath.hungrybird.network.ParseClient;
import com.parse.ParseFile;

public class DishDetailsFragment extends Fragment {
    public static final String TAG = DishDetailsFragment.class.getSimpleName();

    public static final String DISH_ID = "DISH_ID";
    public static final String FRAGMENT_TAG = "FILTER_FRAGMENT_TAG";

    ParseClient parseClient = ParseClient.getInstance();
    ChefDishDetailsFragmentBinding binding;

    Dish currentDish;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate(inflater, R.layout.chef_dish_details_fragment, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        String id = getArguments().getString(DISH_ID);
        // TODO: Get id from the last screen and pass here
        parseClient.getDishById(id, new ParseClient.DishListener() {
            @Override
            public void onSuccess(Dish dish) {
                Log.i(TAG, dish.toString());
                currentDish = dish;
                ParseFile parseFile = currentDish.getPrimaryImage();
                if (parseFile != null && parseFile.getUrl() != null) {
                    String imgUrl = parseFile.getUrl();
                    Glide.with(getContext())
                            .load(imgUrl)
                            .into(binding.dishImage);
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
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
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
