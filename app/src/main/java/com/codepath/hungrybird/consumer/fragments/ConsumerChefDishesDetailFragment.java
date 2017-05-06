package com.codepath.hungrybird.consumer.fragments;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.app.Activity;
import android.content.Context;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.DecelerateInterpolator;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.codepath.hungrybird.R;
import com.codepath.hungrybird.chef.adapters.DishArrayAdapter;
import com.codepath.hungrybird.common.BaseItemHolderAdapter;
import com.codepath.hungrybird.common.HelperObservables;
import com.codepath.hungrybird.common.OrderRelationResponse;
import com.codepath.hungrybird.common.SimpleDividerItemDecoration;
import com.codepath.hungrybird.consumer.activities.GalleryActivity;
import com.codepath.hungrybird.databinding.ChefOfferingsDishesListItemBinding;
import com.codepath.hungrybird.databinding.ConsumerGalleryChefDishesDetailViewBinding;
import com.codepath.hungrybird.model.Dish;
import com.codepath.hungrybird.model.Order;
import com.codepath.hungrybird.model.OrderDishRelation;
import com.codepath.hungrybird.model.User;
import com.codepath.hungrybird.network.ParseClient;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import jp.wasabeef.glide.transformations.CropCircleTransformation;
import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

/**
 * Created by DhwaniShah on 4/13/17.
 */

public class ConsumerChefDishesDetailFragment extends Fragment implements DishArrayAdapter.DishSelected {

    public static final String TAG = ConsumerChefDishesDetailFragment.class.getSimpleName();
    public static final String DISH_ID = "DISH_ID";
    public static final String CHEF_ID = "CHEF_ID";
    public static final String ITEMS_COUNT = "ITEMS_COUNT";

    ParseClient parseClient = ParseClient.getInstance();
    ConsumerGalleryChefDishesDetailViewBinding binding;

    Dish currentDish;
    Order currentOrder;
    BaseItemHolderAdapter adapter;
    List<Dish> dishesArrayList = new ArrayList<>();
    private static DecimalFormat df = new DecimalFormat();
    OrderRelationResponse orderDishRelationResponse;
    int selectedPosition = 0;

    ImageView cartIcon;
    TextView cartTextView;

    static {
        df.setMinimumFractionDigits(2);
        df.setMaximumFractionDigits(2);
    }

    private int getItemsCount() {
        return getArguments().getInt(ITEMS_COUNT);
    }

    private void putItemsCount(int count) {
        getArguments().putInt(ITEMS_COUNT, count);
    }

    private Context context;

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
        ((GalleryActivity) getActivity()).setToolbarTitle("Chef's Menu");
        Toolbar toolbar = ((GalleryActivity) getActivity()).getToolbar();
        if (toolbar != null) {
            cartIcon = (ImageView) toolbar.findViewById(R.id.cartIcon);
            cartIcon.setOnClickListener(v -> {
                Activity activity = getActivity();
                if (activity instanceof CartListener) {
                    CartListener cartListener = (CartListener) activity;
                    if (currentOrder != null) {
                        cartListener.onCartPressed(currentOrder);
                    }
                }
            });
            cartTextView = (TextView) toolbar.findViewById(R.id.cart_text);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        ((GalleryActivity) getActivity()).setToolbarTitle("Chef's Menu");
        cartIcon.setVisibility(View.VISIBLE);
        updateItemCount();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        String chefId = getArguments().getString(CHEF_ID);
        String consumerId = ParseUser.getCurrentUser().getObjectId();
        binding = DataBindingUtil.inflate(inflater, R.layout.consumer_gallery_chef_dishes_detail_view, container, false);
        binding.tvDishQuantity.setText(String.valueOf(1));

        adapter = new BaseItemHolderAdapter<>(getContext(), R.layout.chef_offerings_dishes_list_item, dishesArrayList);

        binding.chefMyoffersingsLv.setAdapter(adapter);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);
        binding.chefMyoffersingsLv.setLayoutManager(linearLayoutManager);
        //Added divider between line items
        SimpleDividerItemDecoration simpleDividerItemDecoration = new SimpleDividerItemDecoration(binding.chefMyoffersingsLv.getContext(),
                R.drawable.divider_vert, (int) getResources().getDimension(R.dimen.divider_height));
        binding.chefMyoffersingsLv.addItemDecoration(simpleDividerItemDecoration);

        adapter.setViewBinder((holder, item, position) -> {
            Log.d(TAG, "onCreateView: setViewBinder " + orderDishRelationResponse);
            ChefOfferingsDishesListItemBinding binding = (ChefOfferingsDishesListItemBinding) (holder.binding);
            final Dish dish = dishesArrayList.get(position);
            binding.chefOfferingListItemDishNameTv.setText(dish.getTitle());
            binding.chefOfferingDishListItemServingSizeValueTv.setText("" + dish.getServingSize());
            binding.chefOfferingDishListItemPriceTv.setText(getRoundedTwoPlaces(dish.getPrice()));
            if (dish.getPrimaryImage() != null && dish.getPrimaryImage().getUrl() != null) {
                Glide.with(holder.binding.getRoot().getContext()).load(dish.getPrimaryImage().getUrl())
                        .placeholder(R.drawable.placeholder).fallback(R.drawable.ic_no_image_available).into(binding.chefOfferingListItemDishIv);
            }
//            if (selectedPosition == position) {
//                holder.itemView.setBackgroundResource(R.color.colorSelected);
//            } else {
//                holder.itemView.setBackgroundColor(Color.TRANSPARENT);
//            }

            if (orderDishRelationResponse != null) {

                if (orderDishRelationResponse.map.containsKey(dish.getObjectId())) {

                    OrderDishRelation r = orderDishRelationResponse.map.get(dish.getObjectId());
                    Log.d(TAG, "onBindViewHolder: contains");
                    binding.increaseDecreaseInclude.increaseDecrease.setVisibility(View.VISIBLE);
                    binding.rowCartInclude.cart.setVisibility(View.GONE);
                    if (r != null && r.getQuantity() > 1) {
                        binding.chefOfferingDishListItemCountTv.setVisibility(View.VISIBLE);
                        binding.chefOfferingDishListItemCountTv.setText("x " + r.getQuantity());
                    }
                } else {
                    Log.d(TAG, "onBindViewHolder: contains NOT");
                    binding.increaseDecreaseInclude.increaseDecrease.setVisibility(View.GONE);
                    binding.rowCartInclude.cart.setVisibility(View.VISIBLE);
                    binding.chefOfferingDishListItemCountTv.setVisibility(View.GONE);
                }
            }
            binding.rowCartInclude.add.setOnClickListener(v -> {
                OrderDishRelation odr = new OrderDishRelation();
                odr.setOrder(currentOrder);
                odr.setDish(dish);
                odr.setQuantity(1);
                odr.setPricePerItem(currentDish.getPrice());
                Log.d(TAG, "onCreateView: add to cart " + position);
                parseClient.addOrderDishRelation(odr, new SaveCallback() {
                    @Override
                    public void done(ParseException e) {
                        if (e == null) {
                            putItemsCount(getItemsCount() + 1);
                            updateItemCount();
                            Log.d(TAG, "onCreateView: added Successfully " + position);
                            orderDishRelationResponse.map.put(dish.getObjectId(), odr);
                            binding.rowCartInclude.cart.animate().alpha(0f).scaleX(0)
                                    .setInterpolator(new AccelerateInterpolator())
                                    .scaleY(1).setDuration(200).setListener(new Animator.AnimatorListener() {
                                @Override
                                public void onAnimationStart(Animator animation) {
                                }

                                @Override
                                public void onAnimationEnd(Animator animation) {
                                    binding.rowCartInclude.cart.setVisibility(View.GONE);
                                }

                                @Override
                                public void onAnimationCancel(Animator animation) {
                                }

                                @Override
                                public void onAnimationRepeat(Animator animation) {
                                }
                            }).start();

                            binding.increaseDecreaseInclude.increaseDecrease.
                                    animate().alpha(1f).scaleX(1).scaleY(1).setStartDelay(75)
                                    .setInterpolator(new DecelerateInterpolator())
                                    .setDuration(300).setListener(new Animator.AnimatorListener() {
                                @Override
                                public void onAnimationStart(Animator animation) {
                                }

                                @Override
                                public void onAnimationEnd(Animator animation) {
                                    binding.increaseDecreaseInclude.increaseDecrease.setVisibility(View.VISIBLE);
                                }

                                @Override
                                public void onAnimationCancel(Animator animation) {
                                }

                                @Override
                                public void onAnimationRepeat(Animator animation) {
                                }
                            }).start();

                        }
                    }
                });

            });
            binding.increaseDecreaseInclude.increase.setOnClickListener(v -> {
                Log.d(TAG, "onCreateView: " + "increase count");
                OrderDishRelation odr = orderDishRelationResponse.map.get(dish.getObjectId());
                if (odr != null) {
                    odr.setQuantity(odr.getQuantity() + 1);
                    parseClient.addOrderDishRelation(odr, new SaveCallback() {
                        @Override
                        public void done(ParseException e) {
                            if (e == null) {
                                putItemsCount(getItemsCount() + 1);
                                updateItemCount();
                                Log.d(TAG, "done: show items count");
                                ValueAnimator fadeAnim = ObjectAnimator.ofFloat(binding.chefOfferingDishListItemCountTv, "alpha", 0f, 1f);
                                fadeAnim.setInterpolator(new AccelerateDecelerateInterpolator());
                                fadeAnim.setDuration(500);
                                fadeAnim.start();
                                binding.chefOfferingDishListItemCountTv.setVisibility(View.VISIBLE);
                                binding.chefOfferingDishListItemCountTv.setText("x " + odr.getQuantity());
                            }
                        }
                    });
                }
            });
            binding.increaseDecreaseInclude.decrease.setOnClickListener(v -> {
                OrderDishRelation odr = orderDishRelationResponse.map.get(dish.getObjectId());
                if (odr != null) {
                    if (odr.getQuantity() > 1) {
                        odr.setQuantity(odr.getQuantity() - 1);
                        parseClient.addOrderDishRelation(odr, new SaveCallback() {
                            @Override
                            public void done(ParseException e) {
                                if (e == null) {
                                    putItemsCount(getItemsCount() - 1);
                                    updateItemCount();
                                    if (odr.getQuantity() > 1) {
                                        ValueAnimator fadeAnim = ObjectAnimator.ofFloat(binding.chefOfferingDishListItemCountTv, "alpha", 0f, 1f);
                                        fadeAnim.setInterpolator(new AccelerateDecelerateInterpolator());
                                        fadeAnim.setDuration(500);
                                        fadeAnim.start();
                                        binding.chefOfferingDishListItemCountTv.setText("x " + odr.getQuantity());
                                    } else if (odr.getQuantity() == 1) {
                                        ValueAnimator fadeAnim = ObjectAnimator.ofFloat(binding.chefOfferingDishListItemCountTv, "alpha", 1f, 0f);
                                        fadeAnim.setInterpolator(new AccelerateDecelerateInterpolator());
                                        fadeAnim.setDuration(500);
                                        fadeAnim.addListener(new Animator.AnimatorListener() {
                                            @Override
                                            public void onAnimationStart(Animator animation) {

                                            }

                                            @Override
                                            public void onAnimationEnd(Animator animation) {
                                                binding.chefOfferingDishListItemCountTv.setVisibility(View.GONE);
                                            }

                                            @Override
                                            public void onAnimationCancel(Animator animation) {

                                            }

                                            @Override
                                            public void onAnimationRepeat(Animator animation) {

                                            }
                                        });
                                        fadeAnim.start();
                                    }
                                }
                            }
                        });
                    } else {
                        odr.setQuantity(odr.getQuantity() - 1);
                        parseClient.delete(odr, new ParseClient.OrderDishRelationListener() {
                            @Override
                            public void onSuccess(OrderDishRelation orderDishRelation) {
                                putItemsCount(getItemsCount() - 1);
                                updateItemCount();
                                orderDishRelationResponse.map.remove(dish.getObjectId());
                                ValueAnimator fadeAnim = ObjectAnimator.ofFloat(binding.chefOfferingDishListItemCountTv, "alpha", 1f, 0f);
                                fadeAnim.setInterpolator(new AccelerateDecelerateInterpolator());
                                fadeAnim.setDuration(500);
                                fadeAnim.addListener(new Animator.AnimatorListener() {
                                    @Override
                                    public void onAnimationStart(Animator animation) {

                                    }

                                    @Override
                                    public void onAnimationEnd(Animator animation) {
                                        binding.chefOfferingDishListItemCountTv.setVisibility(View.GONE);
                                    }

                                    @Override
                                    public void onAnimationCancel(Animator animation) {

                                    }

                                    @Override
                                    public void onAnimationRepeat(Animator animation) {

                                    }
                                });

                                fadeAnim.start();
                                binding.increaseDecreaseInclude.increaseDecrease.animate().alpha(0f).scaleX(0)
                                        .setInterpolator(new AccelerateInterpolator())
                                        .scaleY(1).setDuration(200).setListener(new Animator.AnimatorListener() {
                                    @Override
                                    public void onAnimationStart(Animator animation) {

                                    }

                                    @Override
                                    public void onAnimationEnd(Animator animation) {
                                        binding.increaseDecreaseInclude.increaseDecrease.setVisibility(View.GONE);
                                    }

                                    @Override
                                    public void onAnimationCancel(Animator animation) {

                                    }

                                    @Override
                                    public void onAnimationRepeat(Animator animation) {

                                    }
                                }).start();

                                binding.rowCartInclude.cart.
                                        animate().alpha(1f).scaleX(1).setStartDelay(75)
                                        .setInterpolator(new DecelerateInterpolator())
                                        .scaleY(1).setDuration(300).setListener(new Animator.AnimatorListener() {
                                    @Override
                                    public void onAnimationStart(Animator animation) {

                                    }

                                    @Override
                                    public void onAnimationEnd(Animator animation) {
                                        binding.rowCartInclude.cart.setVisibility(View.VISIBLE);
                                    }

                                    @Override
                                    public void onAnimationCancel(Animator animation) {

                                    }

                                    @Override
                                    public void onAnimationRepeat(Animator animation) {

                                    }
                                }).start();
                            }

                            @Override
                            public void onFailure(Exception e) {

                            }
                        });
                    }

                }
            });
            holder.getBaseView().setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (position != RecyclerView.NO_POSITION) { // Check if an item was deleted, but the user clicked it before the UI removed it
                        Log.d(TAG, "onCreateView: dishSelected " + dish + " " + position);
                        currentDish = dish;
                        updateCurrentDishView();
                        int pre = selectedPosition;
                        selectedPosition = position;
                        adapter.notifyItemChanged(selectedPosition); // old position
                        adapter.notifyItemChanged(pre); // old position
                    }
                }
            });
        });

        getCurrentOrders(chefId, consumerId).

                subscribeOn(Schedulers.io()).

                observeOn(AndroidSchedulers.mainThread()).

                subscribe(new Subscriber<OrderRelationResponse>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onNext(OrderRelationResponse relationResponse) {
                        Log.d(TAG, "onNext: Downloaded " + relationResponse.order);
                        orderDishRelationResponse = relationResponse;
                        currentOrder = relationResponse.order;
                        dishesArrayList.clear();
                        dishesArrayList.addAll(orderDishRelationResponse.dishes);
                        adapter.notifyDataSetChanged();
                        updateItemCount();

                    }
                });
        return binding.getRoot();
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.cart_menu, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public void onPause() {
        super.onPause();
        cartTextView.setVisibility(View.GONE);
        cartIcon.setVisibility(View.GONE);
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
                    if (currentOrder != null) {
                        cartListener.onCartPressed(currentOrder);
                    }
                }
        }
        return super.onOptionsItemSelected(item);
    }

    private String getRoundedTwoPlaces(double val) {
        return "$" + df.format(Math.round(100 * val) / 100.0);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setHasOptionsMenu(true);
        String dishId = getArguments().getString(DISH_ID);
        String chefId = getArguments().getString(CHEF_ID);
        String consumerId = ParseUser.getCurrentUser().getObjectId();
        binding.tvMinus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int oldQuantity = Integer.parseInt(binding.tvDishQuantity.getText().toString());
                int newQuantity = oldQuantity - 1;
                if (newQuantity > 0) {
                    ValueAnimator fadeAnim = ObjectAnimator.ofFloat(binding.tvDishQuantity, "alpha", 0f, 1f);
                    fadeAnim.setInterpolator(new AccelerateDecelerateInterpolator());
                    fadeAnim.setDuration(500);
                    fadeAnim.start();

                    binding.tvDishQuantity.setText(String.valueOf(newQuantity));
                }
            }
        });
        binding.tvPlus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int oldQuantity = Integer.parseInt(binding.tvDishQuantity.getText().toString());
                int newQuantity = oldQuantity + 1;
                ValueAnimator fadeAnim = ObjectAnimator.ofFloat(binding.tvDishQuantity, "alpha", 0f, 1f);
                fadeAnim.setInterpolator(new AccelerateDecelerateInterpolator());
                fadeAnim.setDuration(500);
                fadeAnim.start();
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
        parseClient.getUserById(chefId, new ParseClient.UserListener() {
            @Override
            public void onSuccess(User user) {
                ParseFile chefProfilePic = user.getProfileImage();
                if (chefProfilePic != null && chefProfilePic.getUrl() != null) {
                    String imgUrl = chefProfilePic.getUrl();
                    Context context = getContext();
                    if (context != null) {
                        Glide.with(context)
                                .load(imgUrl)
                                .bitmapTransform(new CropCircleTransformation(context))
                                .into(binding.chefProfilePicIv);
                    }
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
                updateCurrentDishView();
            }

            @Override
            public void onFailure(Exception e) {
                Toast.makeText(getContext(), "There was an error getting data.", Toast.LENGTH_LONG).show();
            }
        });
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        this.context = context;
    }

    private void updateCurrentDishView() {
        ParseFile dishPic = currentDish.getPrimaryImage();
        if (dishPic != null && dishPic.getUrl() != null) {
            String imgUrl = dishPic.getUrl();
            Glide.with(this.context)
                    .load(imgUrl)
                    .placeholder(R.drawable.placeholder)
                    .into(binding.selectedDishPicIv);
        }
        Activity activity = getActivity();
        if (activity == null) return;
        ((GalleryActivity) activity).setToolbarTitle(currentDish.getTitle());
//        binding.dishTitle.setText(currentDish.getDescription());
        binding.dishPrice.setText(getRoundedTwoPlaces(currentDish.getPrice()));
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

    private void updateItemCount() {
        Activity activity = getActivity();
        if (activity == null || cartTextView == null) {
            return;
        }
        int count = getItemsCount();
        if (count == 0) {
            cartTextView.setVisibility(View.GONE);
        } else {
            cartTextView.setVisibility(View.VISIBLE);
            cartTextView.setText("" + count);
            Animation expandIn = AnimationUtils.loadAnimation(activity, R.anim.expand_in);
            cartTextView.startAnimation(expandIn);
        }
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
                        putItemsCount(quantity);
                        updateItemCount();
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
                        putItemsCount(quantity);
                        updateItemCount();
                    }
                });
            }
        });
    }

    Observable<OrderRelationResponse> getCurrentOrders(final String chefId, final String consumerId) {
        final OrderRelationResponse odr = new OrderRelationResponse();
        return getOrder(chefId, consumerId).flatMap(new Func1<Order, Observable<OrderRelationResponse>>() {
            @Override
            public Observable<OrderRelationResponse> call(Order order) {
                odr.order = order;
                return HelperObservables.getOrderDishRelationsByOrderId(order.getObjectId(), odr);
            }
        }).flatMap(new Func1<OrderRelationResponse, Observable<OrderRelationResponse>>() {
            @Override
            public Observable<OrderRelationResponse> call(final OrderRelationResponse orderRelationResponse) {
                return Observable.create(new Observable.OnSubscribe<OrderRelationResponse>() {
                    @Override
                    public void call(Subscriber<? super OrderRelationResponse> subscriber) {
                        HelperObservables.getDishesByChefId(chefId, odr).subscribeOn(Schedulers.io())
                                .subscribe(new Subscriber<OrderRelationResponse>() {
                                    @Override
                                    public void onCompleted() {
                                        subscriber.onCompleted();
                                    }

                                    @Override
                                    public void onError(Throwable e) {
                                        subscriber.onError(e);
                                    }

                                    @Override
                                    public void onNext(OrderRelationResponse dishes) {
                                        int dishCount = 0;

                                        for (OrderDishRelation r : orderRelationResponse.orderDishRelation) {
                                            Log.d(TAG, "onNext: " + r);
                                            Dish dish = r.getDish();
                                            dishCount += r.getQuantity();
                                            orderRelationResponse.map.put(dish.getObjectId(), r);
                                        }
                                        putItemsCount(dishCount);
                                        subscriber.onNext(orderRelationResponse);

                                    }
                                });
                    }
                });
            }
        });
    }

    Observable<Order> getOrder(final String chefId, final String consumerId) {
        return Observable.create(new Observable.OnSubscribe<Order>() {
            @Override
            public void call(Subscriber<? super Order> subscriber) {
                parseClient.getOrderByConsumerIdAndChefId(consumerId, chefId, new ParseClient.OrderListener() {
                    @Override
                    public void onSuccess(Order order) {
                        Log.d(TAG, "onSuccess: " + order);
                        subscriber.onNext(order);
                        subscriber.onCompleted();
                    }

                    @Override
                    public void onFailure(Exception e) {
                        Log.d(TAG, "onSuccess: ");
                        parseClient.addOrder(consumerId, chefId, Order.Status.NOT_ORDERED, new ParseClient.OrderListener() {
                            @Override
                            public void onSuccess(Order order) {
                                subscriber.onNext(order);
                                subscriber.onCompleted();
                            }

                            @Override
                            public void onFailure(Exception e) {
                                e.printStackTrace();
                            }
                        });
                    }
                });
            }
        });
    }


}