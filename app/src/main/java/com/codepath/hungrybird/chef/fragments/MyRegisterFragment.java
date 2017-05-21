package com.codepath.hungrybird.chef.fragments;


import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.codepath.hungrybird.R;
import com.codepath.hungrybird.databinding.ChefMyRegisterFragmentBinding;

public class MyRegisterFragment extends Fragment {
    public static final String TAG = MyRegisterFragment.class.getSimpleName();

    public static final String FRAGMENT_TAG = "FILTER_FRAGMENT_TAG";

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        ChefMyRegisterFragmentBinding binding = DataBindingUtil.inflate(inflater, R.layout.chef_my_register_fragment, container, false);
        return binding.getRoot();
    }
}
