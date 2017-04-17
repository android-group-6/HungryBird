package com.codepath.hungrybird.consumer.fragments;


import android.content.Context;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.codepath.hungrybird.R;
import com.codepath.hungrybird.databinding.ConsumerCheckoutFragmentBinding;

public class ConsumerCheckoutFragment extends Fragment {

    public static final String ORDER_ID = "ORDER_ID";
    public static final String TOTAL_PRICE = "TOTAL_PRICE";

    ConsumerCheckoutFragmentBinding binding;

    CheckoutFragmentListener checkoutFragmentListener;

    public interface CheckoutFragmentListener {
        void onPayNowClickListener(View view, String orderId, String price);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof CheckoutFragmentListener) {
            checkoutFragmentListener = (CheckoutFragmentListener) context;
        } else {
            throw new ClassCastException(context.toString()
                    + " must implement ConsumerCheckoutFragment.CheckoutFragmentListener");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate(inflater, R.layout.consumer_checkout_fragment, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        binding.cartCheckoutPaynowBt.setText("PAY $" + getArguments().getString(TOTAL_PRICE) + " NOW");
        binding.cartCheckoutPaynowBt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                checkoutFragmentListener.onPayNowClickListener(view, getArguments().getString(ORDER_ID), getArguments().getString(TOTAL_PRICE));
            }
        });
    }
}
