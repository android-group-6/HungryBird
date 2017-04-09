package com.codepath.hungrybird.chef.fragments;


import android.content.Context;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.codepath.hungrybird.R;
import com.codepath.hungrybird.chef.adapters.DishArrayAdapter;
import com.codepath.hungrybird.consumer.fragments.FilterFragment;
import com.codepath.hungrybird.databinding.ChefMyOfferingsFragmentBinding;
import com.codepath.hungrybird.model.Dish;
import com.codepath.hungrybird.network.ParseClient;

import java.util.ArrayList;
import java.util.List;

public class MyOfferingsFragment extends Fragment {
    public static final String TAG = MyOfferingsFragment.class.getSimpleName();
    ParseClient parseClient = ParseClient.getInstance();
    RecyclerView myOfferingsRView;
    ArrayList<Dish> dishesArrayList = new ArrayList<>();
    DishArrayAdapter dishArrayAdapter;

    public static final String FRAGMENT_TAG = "FILTER_FRAGMENT_TAG";
    private LinearLayoutManager linearLayoutManager;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        dishArrayAdapter = new DishArrayAdapter(getActivity(), dishesArrayList);
        parseClient.getDishesByChefId("ZkjdWTqmmC", new ParseClient.DishListListener() {
            @Override
            public void onSuccess(List<Dish> dishes) {
                dishesArrayList.addAll(dishes);
                dishArrayAdapter.notifyDataSetChanged();
                Log.d(TAG, "onSuccess: " + dishes);
            }

            @Override
            public void onFailure(Exception e) {

            }
        });


    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        ChefMyOfferingsFragmentBinding binding = DataBindingUtil.inflate(inflater, R.layout.chef_my_offerings_fragment, container, false);
        myOfferingsRView = binding.content.chefMyoffersingsLv;

        myOfferingsRView.setAdapter(dishArrayAdapter);
        // Set layout manager to position the items
        linearLayoutManager = new LinearLayoutManager(getActivity(),
                LinearLayoutManager.VERTICAL, false);
        myOfferingsRView.setLayoutManager(linearLayoutManager);
        //Set Brand Icon
        //Added divider between line items
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(myOfferingsRView.getContext(),
                linearLayoutManager.getOrientation());
        myOfferingsRView.addItemDecoration(dividerItemDecoration);

        return binding.getRoot();
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Override
    public void onDetach() {
        super.onDetach();
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
