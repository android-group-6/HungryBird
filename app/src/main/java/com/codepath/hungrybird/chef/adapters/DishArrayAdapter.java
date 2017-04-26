package com.codepath.hungrybird.chef.adapters;

import android.app.Activity;
import android.content.Context;
import android.databinding.DataBindingUtil;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import com.bumptech.glide.Glide;
import com.codepath.hungrybird.R;
import com.codepath.hungrybird.databinding.ChefOfferingsDishesListItemBinding;
import com.codepath.hungrybird.databinding.ProgressItemBinding;
import com.codepath.hungrybird.model.Dish;

import java.text.DecimalFormat;
import java.util.List;

/**
 * Created by gauravb on 3/15/17.
 */

public class DishArrayAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private static final String TAG = DishArrayAdapter.class.getSimpleName();
    List<Dish> dishArrayList;
    Context context;
    private final int VIEW_ITEM = 1;
    private final int VIEW_PROG = 0;
    DishSelected dishSelected;
    private int selectedPosition = 0;
    private static DecimalFormat df = new DecimalFormat();
    static {
        df.setMinimumFractionDigits(2);
        df.setMaximumFractionDigits(2);
    }

    public interface DishSelected {
        void onDishSelected(Dish dish, boolean fromChefPage);
    }

    public DishArrayAdapter(Activity activity, List<Dish> dishArrayList) {
        this(activity, dishArrayList, (DishSelected) activity);
    }

    public DishArrayAdapter(Activity activity, List<Dish> dishArrayList, DishSelected dishSelected) {
        this.dishArrayList = dishArrayList;
        this.context = activity;
        this.dishSelected = dishSelected;
        if (dishArrayList == null) {
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
            ChefOfferingsDishesListItemBinding binding =
                    DataBindingUtil.inflate(inflater, R.layout.chef_offerings_dishes_list_item, parent, false);

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
            final Dish dish = dishArrayList.get(position);
            holder.binding.chefOfferingListItemDishNameTv.setText(dish.getTitle());
            holder.binding.chefOfferingDishListItemServingSizeValueTv.setText("" + dish.getServingSize());
            holder.binding.chefOfferingDishListItemPriceTv.setText(getRoundedTwoPlaces(dish.getPrice()));
            if (dish.getPrimaryImage() != null && dish.getPrimaryImage().getUrl() != null) {
                Glide.with(holder.binding.getRoot().getContext()).load(dish.getPrimaryImage().getUrl()).into(holder.binding.chefOfferingListItemDishIv);
            }
            if(selectedPosition == position){
                holder.itemView.setBackgroundResource(R.color.colorSelected);
            } else{
                holder.itemView.setBackgroundColor(Color.TRANSPARENT);
            }
        } else {
            ((ProgressViewHolder) viewHolder).progressBar.setIndeterminate(true);
        }
    }

    @Override
    public int getItemViewType(int position) {
        return dishArrayList.get(position) != null ? VIEW_ITEM : VIEW_PROG;
    }

    private String getRoundedTwoPlaces(double val) {
        return "$" + df.format(Math.round(100 * val) / 100.0);
    }

    @Override
    public int getItemCount() {
        return dishArrayList.size();
    }


    // Provide a direct reference to each of the views within a data item
    // Used to cache the views within the item layout for fast access
    public class ViewHolder extends RecyclerView.ViewHolder {
        // Your holder should contain a member variable
        // for any view that will be set as you render a row
        ChefOfferingsDishesListItemBinding binding;

        // We also create a constructor that accepts the entire item row
        // and does the view lookups to find each subview
        public ViewHolder(ChefOfferingsDishesListItemBinding binding) {
            // Stores the itemView in a public final member variable that can be used
            // to access the context from any ViewHolder instance.
            super(binding.getRoot());
            this.binding = binding;

            itemView.setOnClickListener(view -> {
                int position = getAdapterPosition();
                if (position != RecyclerView.NO_POSITION) { // Check if an item was deleted, but the user clicked it before the UI removed it
                    Dish dish = dishArrayList.get(position);
                    if (dishSelected != null) {
                        dishSelected.onDishSelected(dish, true);
                    }
                    notifyItemChanged(selectedPosition); // old position
                    selectedPosition = getLayoutPosition();
                    notifyItemChanged(selectedPosition); // new position
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
