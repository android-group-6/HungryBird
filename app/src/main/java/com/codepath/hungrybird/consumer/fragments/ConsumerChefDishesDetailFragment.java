package com.codepath.hungrybird.consumer.fragments;

import android.app.Activity;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
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
import com.codepath.hungrybird.chef.adapters.DishArrayAdapter;
import com.codepath.hungrybird.databinding.ConsumerGalleryChefDishesDetailViewBinding;
import com.codepath.hungrybird.model.Dish;
import com.codepath.hungrybird.model.Order;
import com.codepath.hungrybird.model.OrderDishRelation;
import com.codepath.hungrybird.network.ParseClient;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by DhwaniShah on 4/13/17.
 */

public class ConsumerChefDishesDetailFragment extends Fragment implements DishArrayAdapter.DishSelected {

    public static final String TAG = ConsumerChefDishesDetailFragment.class.getSimpleName();
    public static final String DISH_ID = "DISH_ID";
    public static final String CHEF_ID = "CHEF_ID";

    ParseClient parseClient = ParseClient.getInstance();
    ConsumerGalleryChefDishesDetailViewBinding binding;

    Dish currentDish;
    Order currentOrder;

    DishArrayAdapter dishArrayAdapter;
    List<Dish> dishesArrayList = new ArrayList<>();

    @Override
    public void onDishSelected(Dish dish, boolean fromChefPage) {
        currentDish = dish;
        updateCurrentDishView();
    }


    public interface CartListener {
        void onCartPressed(Order order);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        setHasOptionsMenu(true);
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onResume() {
        super.onResume();
        String title = getArguments().getString("CHEF_NAME");
        ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle(title + "\'s Kitchen");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate(inflater, R.layout.consumer_gallery_chef_dishes_detail_view, container, false);
        binding.tvDishQuantity.setText(String.valueOf(1));

        dishArrayAdapter = new DishArrayAdapter(getActivity(), dishesArrayList, this);
        binding.content.chefMyoffersingsLv.setAdapter(dishArrayAdapter);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);
        binding.content.chefMyoffersingsLv.setLayoutManager(linearLayoutManager);
        //Added divider between line items
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(binding.content.chefMyoffersingsLv.getContext(), linearLayoutManager.getOrientation());
        binding.content.chefMyoffersingsLv.addItemDecoration(dividerItemDecoration);

        return binding.getRoot();
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.cart_menu, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public void onPrepareOptionsMenu(Menu menu) {
        menu.getItem(0).setVisible(false);
//        menu.getItem(1).setVisible(false);
        super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.mi_cart:
                Activity activity = getActivity();
                if (activity instanceof CartListener) {
                    CartListener cartListener = (CartListener) activity;

                    cartListener.onCartPressed(currentOrder);
                }
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setHasOptionsMenu(true);
        String dishId = getArguments().getString(DISH_ID);
        String chefId = getArguments().getString(CHEF_ID);
        String consumerId = ParseUser.getCurrentUser().getObjectId();
        setOrder(chefId, consumerId); // find order or create new order.
        binding.tvMinus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int oldQuantity = Integer.parseInt(binding.tvDishQuantity.getText().toString());
                int newQuantity = oldQuantity - 1;
                if (newQuantity > 0) {
                    binding.tvDishQuantity.setText(String.valueOf(newQuantity));
                }
            }
        });
        binding.tvPlus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int oldQuantity = Integer.parseInt(binding.tvDishQuantity.getText().toString());
                int newQuantity = oldQuantity + 1;
                binding.tvDishQuantity.setText(String.valueOf(newQuantity));
            }
        });
        binding.tvAddToCart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int quantity = Integer.parseInt(binding.tvDishQuantity.getText().toString());
                addOrUpdateOrderDishRelation(currentOrder, currentDish, quantity);
                Toast.makeText(getActivity(), "Added", Toast.LENGTH_SHORT).show();
            }
        });
        parseClient.getDishById(dishId, new ParseClient.DishListener() {
            @Override
            public void onSuccess(Dish dish) {
                Log.i(TAG, dish.toString());
                currentDish = dish;
                updateCurrentDishView();
            }

            @Override
            public void onFailure(Exception e) {
                Toast.makeText(getContext(), "There was an error getting data.", Toast.LENGTH_LONG).show();
            }
        });
        parseClient.getDishesByChefId(chefId, new ParseClient.DishListListener() {
            @Override
            public void onSuccess(List<Dish> dishes) {
                dishesArrayList.clear();
                dishesArrayList.addAll(dishes);
                dishArrayAdapter.notifyDataSetChanged();
                Log.d(TAG, "onSuccess: " + dishes);
            }

            @Override
            public void onFailure(Exception e) {

            }
        });
    }

    private void updateCurrentDishView() {
        ParseFile dishPic = currentDish.getPrimaryImage();
        if (dishPic != null && dishPic.getUrl() != null) {
            String imgUrl = dishPic.getUrl();
            Glide.with(getContext())
                    .load(imgUrl)
                    .placeholder(R.drawable.placeholder)
                    .into(binding.selectedDishPicIv);
        }

        binding.dishTitle.setText(currentDish.getTitle());
        binding.dishPrice.setText("$" + String.valueOf(currentDish.getPrice()));
        binding.dishServingSize.setText(String.valueOf(currentDish.getServingSize()));
        String description = currentDish.getDescription();
        if (description == null || description.isEmpty()) {
            binding.dishDescription.setVisibility(View.GONE);
        } else {
            binding.dishDescription.setVisibility(View.VISIBLE);
            binding.dishDescription.setText(description);
        }
//        binding.dishDescription.setText(description);
        binding.tvDishQuantity.setText(String.valueOf(1));
    }

    private void addOrUpdateOrderDishRelation(Order currentOrder, Dish currentDish, int quantity) {
        parseClient.getOrderDishRelationByOrderAndDishId(currentOrder.getObjectId(), currentDish.getObjectId(), new ParseClient.OrderDishRelationListener() {
            @Override
            public void onSuccess(OrderDishRelation orderDishRelation) {
                orderDishRelation.setQuantity(quantity);
                orderDishRelation.setPricePerItem(currentDish.getPrice());
                parseClient.addOrderDishRelation(orderDishRelation, new SaveCallback() {
                    @Override
                    public void done(ParseException e) {

                    }
                });
            }

            @Override
            public void onFailure(Exception e) {
                OrderDishRelation orderDishRelation = new OrderDishRelation();
                orderDishRelation.setOrder(currentOrder);
                orderDishRelation.setDish(currentDish);
                orderDishRelation.setQuantity(quantity);
                orderDishRelation.setPricePerItem(currentDish.getPrice());
                parseClient.addOrderDishRelation(orderDishRelation, new SaveCallback() {
                    @Override
                    public void done(ParseException e) {

                    }
                });
            }
        });
    }

    private void setOrder(final String chefId, final String consumerId) {
        parseClient.getOrderByConsumerIdAndChefId(consumerId, chefId, new ParseClient.OrderListener() {
            @Override
            public void onSuccess(Order order) {
                currentOrder = order;
            }

            @Override
            public void onFailure(Exception e) {
                parseClient.addOrder(consumerId, chefId, Order.Status.NOT_ORDERED, new ParseClient.OrderListener() {
                    @Override
                    public void onSuccess(Order order) {
                        currentOrder = order;
                    }

                    @Override
                    public void onFailure(Exception e) {
                        e.printStackTrace();
                    }
                });
            }
        });
    }
}