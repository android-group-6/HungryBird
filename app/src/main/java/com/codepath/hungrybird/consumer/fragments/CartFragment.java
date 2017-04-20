package com.codepath.hungrybird.consumer.fragments;

import android.content.Context;
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
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.codepath.hungrybird.R;
import com.codepath.hungrybird.common.BaseItemHolderAdapter;
import com.codepath.hungrybird.databinding.ConsumerCartDetailsFragmentBinding;
import com.codepath.hungrybird.databinding.ConsumerOrderCartDishItemBinding;
import com.codepath.hungrybird.model.Dish;
import com.codepath.hungrybird.model.Order;
import com.codepath.hungrybird.model.OrderDishRelation;
import com.codepath.hungrybird.model.User;
import com.codepath.hungrybird.model.postmates.DeliveryQuoteRequest;
import com.codepath.hungrybird.model.postmates.DeliveryQuoteResponse;
import com.codepath.hungrybird.network.ParseClient;
import com.codepath.hungrybird.network.PostmatesClient;
import com.parse.ParseException;
import com.parse.SaveCallback;

import java.util.ArrayList;
import java.util.List;

import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

/**
 * Created by gauravb on 4/13/17.
 */

public class CartFragment extends Fragment {
    public static final String OBJECT_ID = "OBJECT_ID";
    ConsumerCartDetailsFragmentBinding binding;
    ArrayList<OrderDishRelation> orderDishRelations = new ArrayList<>();

    ParseClient parseClient;

    CartFragmentListener cartFragmentListener;

    private String deliveryQuoteId;

    public interface CartFragmentListener {
        public void onCheckoutListener(String orderId, String price);
    }


    double totalPriceBeforeTax = 0.0;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public void onPrepareOptionsMenu(Menu menu) {
        menu.getItem(0).setVisible(false);
        super.onPrepareOptionsMenu(menu);
    }

    @Override
    public void onResume() {
        super.onResume();
        ((AppCompatActivity)getActivity()).getSupportActionBar().setTitle("Your Cart");
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof CartFragmentListener) {
            cartFragmentListener = (CartFragmentListener) context;
        } else {
            throw new ClassCastException(context.toString()
                    + " must implement CartFragment.CartFragmentListener");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        binding = DataBindingUtil.inflate(inflater, R.layout.consumer_cart_details_fragment, container, false);
        Bundle bundle = getArguments();
        String orderObjectId = bundle.getString(OBJECT_ID);
        parseClient = ParseClient.getInstance();

        binding.radioButtonPickup.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    binding.tvAddress.setVisibility(View.GONE);
                    binding.tvDeliveryCost.setText("$ 0.00");
                }
            }
        });

        binding.radioButtonDelivery.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    binding.tvAddress.setVisibility(View.VISIBLE);
                    // Get Quote
                    String pickUpAddress = "20 McAllister St, San Francisco, CA"; // TODO integrate with real values
                    String dropOffAddress = "101 Market St, San Francisco, CA";   // TODO integrate with real values
                    DeliveryQuoteRequest deliveryQuoteRequest = new DeliveryQuoteRequest(pickUpAddress, dropOffAddress);
                    PostmatesClient.getInstance().deliveryQuotes(deliveryQuoteRequest, new PostmatesClient.DeliveryQuoteResponseListener() {
                        @Override
                        public void onSuccess(DeliveryQuoteResponse deliveryQuoteResponse) {
                            deliveryQuoteId = deliveryQuoteResponse.getQuoteId();
                            binding.tvDeliveryCost.setText("$ "  +  String.valueOf(deliveryQuoteResponse.getFee()));
                        }

                        @Override
                        public void onFailure(Exception e) {
                            binding.tvDeliveryCost.setText("$ 1.00");
                        }
                    });
                }
            }
        });

        binding.tvAddress.setText("101 San Fernando San Jose 95110"); // TODO integrate with real values

        final BaseItemHolderAdapter<OrderDishRelation> adapter =
                new BaseItemHolderAdapter(getContext(), R.layout.consumer_order_cart_dish_item, orderDishRelations);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);
        binding.consumerCartItemsRv.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false));
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(binding.consumerCartItemsRv.getContext(),
                linearLayoutManager.getOrientation());
        binding.consumerCartItemsRv.addItemDecoration(dividerItemDecoration);
        binding.consumerCartItemsRv.setAdapter(adapter);

        Observable.create(new Observable.OnSubscribe<Response>() {
            @Override
            public void call(Subscriber<? super Response> subscriber) {
                parseClient.getOrderById(orderObjectId, new ParseClient.OrderListener() {
                    @Override
                    public void onSuccess(Order order) {
                        Response res = new Response();
                        res.order = order;
                        subscriber.onNext(res);
                        subscriber.onCompleted();
                    }

                    @Override
                    public void onFailure(Exception e) {
                        subscriber.onError(e);
                    }
                });
            }
        }).flatMap(new Func1<Response, Observable<Response>>() {
            @Override
            public Observable<Response> call(Response res) {
                return Observable.create(new Observable.OnSubscribe<Response>() {
                    @Override
                    public void call(Subscriber<? super Response> subscriber) {
                        parseClient.getOrderDishRelationsByOrderId(orderObjectId, new ParseClient.OrderDishRelationListListener() {
                            @Override
                            public void onSuccess(List<OrderDishRelation> orderDishRelations) {
                                res.orderDishRelation = orderDishRelations;
                                subscriber.onNext(res);
                                subscriber.onCompleted();
                            }

                            @Override
                            public void onFailure(Exception e) {
                                subscriber.onError(e);
                            }
                        });
                    }
                });
            }
        }).observeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<Response>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onNext(Response response) {
                        updatePricing(response.orderDishRelation);
                        CartFragment.this.orderDishRelations.clear();
                        CartFragment.this.orderDishRelations.addAll(response.orderDishRelation);
                        adapter.notifyDataSetChanged();
                        User user = response.order.getChef();
                        if (user.getProfileImageUrl() != null) {
                            Glide.with(getContext()).load(user.getProfileImageUrl()).into(binding.consumerCartChefIv);
                        }

                        binding.consumerCartChefNameTv.setText(response.order.getChef().getUsername());
                        binding.checkoutButton.setOnClickListener(v -> {
                            boolean isDelivery = binding.rgDelivery.getCheckedRadioButtonId() == R.id.radioButtonDelivery;
                            response.order.setDelivery(isDelivery ? true : false);
                            if (isDelivery && deliveryQuoteId != null) {
                                response.order.setDeliveryQuoteId(deliveryQuoteId);
                            }
                            response.order.setTotalPayment(totalPriceBeforeTax);
                            parseClient.addOrder(response.order, new ParseClient.OrderListener() {
                                @Override
                                public void onSuccess(Order order) {
                                    String price = (binding.consumerCartPriceBeforeTax.getText().toString()).substring(1);
                                    //Double sentPrice = Double.parseDouble(price);
                                    Log.e("SFDSD", binding.consumerCartPriceBeforeTax.getText().toString() + " | " + price + " " + order.getTotalPayment());
                                    Toast.makeText(getContext(), " order Id " + response.order.getObjectId(), Toast.LENGTH_SHORT).show();
                                    cartFragmentListener.onCheckoutListener(response.order.getObjectId(), price);
                                }

                                @Override
                                public void onFailure(Exception e) {

                                }
                            });
                        });

                    }
                });
        adapter.setOnClickListener(v -> {
            v.findViewById(R.id.add_to_card_panel).setVisibility(View.GONE);
            ImageView iv = (ImageView) v.findViewById(R.id.reduce_cart);
            if (view != null) {
                view.setVisibility(View.GONE);
                int quantity = ((OrderDishRelation) view.getTag()).getQuantity();
                int index = (int) view.getTag(R.id.add_cart);
                adapter.notifyItemChanged(index);

            }
            view = null;
        });
        adapter.setViewBinder((holder, item, position) -> {
            OrderDishRelation order = orderDishRelations.get(position);
            ConsumerOrderCartDishItemBinding binding = (ConsumerOrderCartDishItemBinding) (holder.binding);
            Dish dish = order.getDish();
            if (dish.getPrimaryImage() != null && dish.getPrimaryImage().getUrl() != null) {
                Glide.with(getActivity()).load(dish.getPrimaryImage().getUrl()).into(binding.itemImage);
            }
            binding.dishItemName.setText(dish.getDishName());
            binding.dishItemServiceSize.setText("Serves " + dish.getServingSize());
            binding.dishPrice.setText("" + dish.getPrice());
            binding.itemsCount.setText("" + order.getQuantity());
            if (view != null) {
                view.setVisibility(View.VISIBLE);
            }
            binding.itemsCount.setOnClickListener(v -> {
                if (binding.addToCardPanel.getVisibility() == View.GONE) {
                    binding.addToCardPanel.setVisibility(View.VISIBLE);
                    binding.updatedCount.setText("" + order.getQuantity());
                    if (order.getQuantity() == 1) {
                        binding.reduceCart.setImageResource(R.drawable.ic_delete_black_24px);
                    }
                    if (view != null) {
                        view.setVisibility(View.GONE);
                        binding.itemsCount.setText("" + order.getQuantity());
                    }
                    view = binding.addToCardPanel;
                    view.setTag(order);
                    view.setTag(R.id.add_cart, position);
                } else {
                    binding.addToCardPanel.setVisibility(View.GONE);
                    view = null;
                }

            });
            binding.addCart.setOnClickListener(v -> {
                int val = order.getQuantity();
                if (val == 1) {
                    binding.reduceCart.setImageResource(R.drawable.ic_remove_circle_outline_black_24px);
                }
                order.setQuantity(val + 1);
                binding.updatedCount.setText("" + order.getQuantity());
                parseClient.addOrderDishRelation(order, new SaveCallback() {
                    @Override
                    public void done(ParseException e) {
                        if (e == null) {
                            updatePricing(orderDishRelations);
                            binding.itemsCount.setText("" + order.getQuantity());
                        } else {
                            binding.reduceCart.setImageResource(R.drawable.ic_delete_black_24px);
                        }
                    }
                });
            });
            binding.reduceCart.setOnClickListener(v -> {
                int val = order.getQuantity();
                if (val == 1) {
                    //Quantity already more than 1 so delete
                    parseClient.delete(order, new ParseClient.OrderDishRelationListener() {
                        @Override
                        public void onSuccess(OrderDishRelation orderDishRelation) {
                            orderDishRelations.remove(position);
                            adapter.notifyItemRemoved(position);
                            updatePricing(orderDishRelations);
                        }

                        @Override
                        public void onFailure(Exception e) {

                        }
                    });
                } else if (val > 1) {
                    if (val == 2) {
                        binding.reduceCart.setImageResource(R.drawable.ic_delete_black_24px);
                    }
                    order.setQuantity(val - 1);
                    binding.updatedCount.setText("" + order.getQuantity());
                    parseClient.addOrderDishRelation(order, new SaveCallback() {
                        @Override
                        public void done(ParseException e) {
                            if (e == null) {
                                updatePricing(orderDishRelations);
                                binding.itemsCount.setText("" + order.getQuantity());
                            }
                        }
                    });
                }
            });

        });
        return binding.getRoot();
    }

    private void updatePricing(List<OrderDishRelation> orderDishRelations) {
        double temp = 0.0;
        for (OrderDishRelation o : orderDishRelations) {
            temp += o.getQuantity() * o.getDish().getPrice();
        }
        totalPriceBeforeTax = temp;
        binding.consumerCartPriceBeforeTax.setText("$" + Math.round(totalPriceBeforeTax * 100.00) / 100.00);
    }

    View view = null;

    static class Response {
        Order order;
        List<OrderDishRelation> orderDishRelation;

    }
}
