package com.codepath.hungrybird.chef.fragments;


import android.content.Context;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.bumptech.glide.Glide;
import com.codepath.hungrybird.R;
import com.codepath.hungrybird.common.BaseItemHolderAdapter;
import com.codepath.hungrybird.common.DateUtils;
import com.codepath.hungrybird.consumer.fragments.OrderHistoryFramgent;
import com.codepath.hungrybird.databinding.ChefContactDetailsFragmentBinding;
import com.codepath.hungrybird.databinding.ChefOrderListItemBinding;
import com.codepath.hungrybird.model.Order;
import com.codepath.hungrybird.model.User;
import com.parse.ParseObject;

import java.text.DecimalFormat;
import java.text.ParseException;
import java.util.ArrayList;

import jp.wasabeef.glide.transformations.CropCircleTransformation;

public class OrdersListFragment extends Fragment {
    public static final String TAG = OrdersListFragment.class.getSimpleName();

    public static final String FRAGMENT_TAG = "FILTER_FRAGMENT_TAG";
    RecyclerView ordersRv;
    ArrayList<Order> orderArrayList = new ArrayList<>();
    BaseItemHolderAdapter<Order> orderArrayAdapter;
    OrderHistoryFramgent.OnOrderSelected orderSelected;
    private DateUtils dateUtils = new DateUtils();
    boolean contentLoaded = false;
    Context context;
    ChefContactDetailsFragmentBinding binding;
    private static DecimalFormat df = new DecimalFormat();

    public void update(ArrayList<Order> orderList) {
        orderArrayList.clear();
        orderArrayList.addAll(orderList);
        contentLoaded = true;
        if (orderArrayAdapter != null) {
            orderArrayAdapter.notifyDataSetChanged();
        }
        if (orderArrayList.isEmpty() == false) {
            onContentLoaded();
        }
    }

    static {
        df.setMinimumFractionDigits(2);
        df.setMaximumFractionDigits(2);
    }

    private String getRoundedTwoPlaces(double val) {
        return "$" + df.format(Math.round(100 * val) / 100.0);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        orderSelected = (OrderHistoryFramgent.OnOrderSelected) getActivity();
        this.context = context;
    }

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate(inflater, R.layout.chef_contact_details_fragment, container, false);
        ordersRv = binding.chefOrderStatusLv;

        binding.titleText.setText(getArguments().getString("TITLE"));
        binding.detailText.setText(getArguments().getString("DETAIL"));

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity(),
                LinearLayoutManager.VERTICAL, false);

        orderArrayAdapter =
                new BaseItemHolderAdapter<>(getContext(), R.layout.chef_order_list_item, orderArrayList);
        orderArrayAdapter.setOnClickListener(v -> {
            if (orderSelected != null) {
                Object object = v.getTag();
                ParseObject parseObject = (ParseObject) object;
                orderSelected.onOrderSelected(parseObject.getObjectId());
            }
        });


        orderArrayAdapter.setViewBinder((holder, item, position) -> {
            Order order = orderArrayList.get(position);
            ChefOrderListItemBinding orderBinding = (ChefOrderListItemBinding) (holder.binding);
            User consumer = order.getConsumer();
            if (consumer != null) {
                String url = consumer.getProfileImageUrl();
                if (TextUtils.isEmpty(url) == false) {
                    Glide.with(context).load(url)
                            .bitmapTransform(new CropCircleTransformation(getContext())).
                    into(orderBinding.chefOfferingListItemDishIv);
                }
            }

            orderBinding.consumerNameTv.setText(order.getConsumer().getUsername());
            orderBinding.chefOrderListItemOrderNameTv.setText(getRoundedTwoPlaces(order.getTotalPayment()));//order.getDisplayId());
            String displayDate = null;
            try {
                displayDate = dateUtils.getDate(order.getUpdatedAt());
            } catch (ParseException e) {
                e.printStackTrace();
            }
            orderBinding.chefOrderListItemDishCountValueTv.setText(displayDate);
        });


        ordersRv.setLayoutManager(linearLayoutManager);

        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(ordersRv.getContext(),
                linearLayoutManager.getOrientation());
        ordersRv.addItemDecoration(dividerItemDecoration);
        return binding.getRoot();
    }

    private void onContentLoaded() {
        if (binding == null) return;
        if (contentLoaded && orderArrayList.isEmpty()) {
            binding.noContent.setVisibility(View.VISIBLE);
            binding.chefOrderStatusLv.setVisibility(View.GONE);
        } else {
            binding.noContent.setVisibility(View.GONE);
            binding.chefOrderStatusLv.setVisibility(View.VISIBLE);
        }

    }

    @Override
    public void onResume() {
        super.onResume();
        ordersRv.setAdapter(orderArrayAdapter);
        orderArrayAdapter.notifyDataSetChanged();
        onContentLoaded();
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return super.onOptionsItemSelected(item);
    }
}
