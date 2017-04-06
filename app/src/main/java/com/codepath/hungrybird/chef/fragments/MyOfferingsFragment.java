package com.codepath.hungrybird.chef.fragments;


import android.content.Context;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.codepath.hungrybird.R;
import com.codepath.hungrybird.consumer.fragments.FilterFragment;
import com.codepath.hungrybird.databinding.ChefMyOfferingsFragmentBinding;

public class MyOfferingsFragment extends Fragment {
    public static final String TAG = MyOfferingsFragment.class.getSimpleName();
    OfferingSelected offeringSelected;

    public static interface OfferingSelected {
        //TODO: add model as parameter
        void onDishSelectedSelected();
    }

    public static final String FRAGMENT_TAG = "FILTER_FRAGMENT_TAG";

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        ChefMyOfferingsFragmentBinding binding = DataBindingUtil.inflate(inflater, R.layout.chef_my_offerings_fragment, container, false);
        binding.chefMyOfferingsTv.setOnClickListener(v -> {
            if (offeringSelected != null) {
                offeringSelected.onDishSelectedSelected();
            }
        });
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        offeringSelected = (OfferingSelected) getActivity();
    }

    @Override
    public void onDetach() {
        super.onDetach();
        offeringSelected = null;
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
