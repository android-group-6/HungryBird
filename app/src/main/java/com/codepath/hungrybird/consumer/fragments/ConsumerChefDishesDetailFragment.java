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
import com.codepath.hungrybird.model.Order;
import com.codepath.hungrybird.model.OrderDishRelation;
import com.codepath.hungrybird.model.User;
import com.codepath.hungrybird.network.ParseClient;
import com.parse.ParseFile;
import com.parse.ParseUser;

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
    Order currentOrder;

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
        String dishId = getArguments().getString(DISH_ID);
        String chefId = getArguments().getString(CHEF_ID);
        String consumerId = ParseUser.getCurrentUser().getObjectId();
        setOrder(chefId, consumerId); // find order or create new order.
        binding.tvMinus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int oldQuantity = Integer.parseInt(binding.tvDishQuantity.getText().toString());
                int newQuantity = oldQuantity - 1;
                if (newQuantity >= 0) {
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
            }
        });
        parseClient.getUserById(chefId, new ParseClient.UserListener() {
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
        parseClient.getDishById(dishId, new ParseClient.DishListener() {
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
        bundle.putString(MyOfferingsFragment.CHEF_ID, chefId);
        myOfferingsFragment.setArguments(bundle);
        fragmentManager.beginTransaction()
                .replace(binding.moreDishesFromCurrentChefFl.getId(), myOfferingsFragment).commit();
    }

    private void addOrUpdateOrderDishRelation(Order currentOrder, Dish currentDish, int quantity) {
        parseClient.getOrderDishRelationByOrderAndDishId(currentOrder.getObjectId(), currentDish.getObjectId(), new ParseClient.OrderDishRelationListener() {
            @Override
            public void onSuccess(OrderDishRelation orderDishRelation) {
                orderDishRelation.setQuantity(quantity);
                orderDishRelation.setPricePerItem(currentDish.getPrice());
                parseClient.addOrderDishRelation(orderDishRelation);
            }

            @Override
            public void onFailure(Exception e) {
                OrderDishRelation orderDishRelation = new OrderDishRelation();
                orderDishRelation.setOrder(currentOrder);
                orderDishRelation.setDish(currentDish);
                orderDishRelation.setQuantity(quantity);
                orderDishRelation.setPricePerItem(currentDish.getPrice());
                parseClient.addOrderDishRelation(orderDishRelation);
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