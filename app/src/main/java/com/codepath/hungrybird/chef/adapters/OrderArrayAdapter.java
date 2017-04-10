package com.codepath.hungrybird.chef.adapters;

import android.app.Activity;
import android.content.Context;
import android.databinding.DataBindingUtil;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import com.codepath.hungrybird.R;
import com.codepath.hungrybird.databinding.ChefOrderListItemBinding;
import com.codepath.hungrybird.databinding.ProgressItemBinding;
import com.codepath.hungrybird.model.Order;

import java.util.ArrayList;

/**
 * Created by gauravb on 3/15/17.
 */

public class OrderArrayAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private static final String TAG = OrderArrayAdapter.class.getSimpleName();
    ArrayList<Order> orderArrayList;
    Activity context;
    private final int VIEW_ITEM = 1;
    private final int VIEW_PROG = 0;
    OrderSelected orderSelected;

    public interface OrderSelected {
        void onOrderSelected(Order order);
    }

    public OrderArrayAdapter(Activity activity, ArrayList<Order> orderArrayList) {
        this.orderArrayList = orderArrayList;
        this.context = activity;
        orderSelected = (OrderSelected) activity;
        if (orderArrayList == null) {
            throw new NullPointerException("Article ArrayList Can't Be Null or Empty");
        }

    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        RecyclerView.ViewHolder viewHolder;
        LayoutInflater inflater = LayoutInflater.from(context);
        if (viewType == VIEW_ITEM) {
            // Inflate the custom layout
            ChefOrderListItemBinding binding =
                    DataBindingUtil.inflate(inflater, R.layout.chef_order_list_item, parent, false);

            // Return a new holder instance
            viewHolder = new ViewHolder(binding);
        } else {
            // Inflate the custom layout
            ProgressItemBinding binding =
                    DataBindingUtil.inflate(inflater, R.layout.progress_item, parent, false);
            viewHolder = new ProgressViewHolder(binding.getRoot());
        }
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int position) {
        //Get the data iterm from the current position
        if (viewHolder instanceof ViewHolder) {
            ViewHolder holder = (ViewHolder) viewHolder;
            final Order order = orderArrayList.get(position);
            holder.binding.chefOrderListItemOrderNameTv.setText(order.getOrderName());
            holder.binding.chefOrderListItemDishCountValueTv.setText("" + 1);
        } else {
            ((ProgressViewHolder) viewHolder).progressBar.setIndeterminate(true);
        }
    }

    @Override
    public int getItemViewType(int position) {
        return orderArrayList.get(position) != null ? VIEW_ITEM : VIEW_PROG;
    }

    @Override
    public int getItemCount() {
        return orderArrayList.size();
    }

    // Provide a direct reference to each of the views within a data item
    // Used to cache the views within the item layout for fast access
    public class ViewHolder extends RecyclerView.ViewHolder {
        // Your holder should contain a member variable
        // for any view that will be set as you render a row
        ChefOrderListItemBinding binding;

        // We also create a constructor that accepts the entire item row
        // and does the view lookups to find each subview
        public ViewHolder(ChefOrderListItemBinding binding) {
            // Stores the itemView in a public final member variable that can be used
            // to access the context from any ViewHolder instance.
            super(binding.getRoot());
            this.binding = binding;

            itemView.setOnClickListener(view -> {
                int position = getAdapterPosition();
                if (position != RecyclerView.NO_POSITION) { // Check if an item was deleted, but the user clicked it before the UI removed it
                    Order order = orderArrayList.get(position);
                    if (orderSelected != null) {
                        orderSelected.onOrderSelected(order);
                    }
                    // We can access the data within the views

                    /* No longer needed this code As we are Using Chrome Tab now

                    Intent i = new Intent(context, ArticleActivity.class);
                    i.putExtra("article", Parcels.wrap(article));
                    context.startActivity(i);
                    Log.d(TAG, "Message " + article + " clicked");

                    */
                }
            });
        }
    }

    public static class ProgressViewHolder extends RecyclerView.ViewHolder {
        public ProgressBar progressBar;

        public ProgressViewHolder(View v) {
            super(v);
            progressBar = (ProgressBar) v.findViewById(R.id.progressBar);
        }
    }
}
