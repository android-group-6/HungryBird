package com.codepath.hungrybird.consumer.fragments;

import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.codepath.hungrybird.R;
import com.codepath.hungrybird.databinding.GalleryViewFilterFragmentBinding;

/**
 * Created by gauravb on 3/15/17.
 */


public class FilterFragment extends android.support.v4.app.DialogFragment {
    private static final String TAG = FilterFragment.class.getSimpleName();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        GalleryViewFilterFragmentBinding binding = DataBindingUtil.inflate(inflater, R.layout.gallery_view_filter_fragment, container, false);
        getDialog().setTitle("Filter");
        binding.filterDialogTvSave.setOnClickListener(v -> {
//            Toast.makeText(getActivity(), "Saved", Toast.LENGTH_SHORT).show();
            FilterFragment.this.dismiss();
        });
        return binding.getRoot();
    }
}
