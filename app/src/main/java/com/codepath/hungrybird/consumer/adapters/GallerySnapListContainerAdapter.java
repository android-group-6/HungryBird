package com.codepath.hungrybird.consumer.adapters;

import android.app.Activity;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.LinearSnapHelper;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.codepath.hungrybird.R;
import com.codepath.hungrybird.common.ColorChooser;
import com.codepath.hungrybird.model.DishList;

import java.util.ArrayList;

/**
 * Created by dshah on 4/10/2017.
 */

public class GallerySnapListContainerAdapter extends RecyclerView.Adapter<GallerySnapListContainerAdapter.ViewHolder> {

    public static final int VERTICAL = 0;
    public static final int HORIZONTAL = 1;
    Activity mActivity;
    ColorChooser colorChooser = new ColorChooser();

    private ArrayList<DishList> mDishes;
    // Disable touch detection for parent recyclerView if we use vertical nested recyclerViews
    private View.OnTouchListener mTouchListener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View v, MotionEvent event) {
            v.getParent().requestDisallowInterceptTouchEvent(true);
            return false;
        }
    };

    public GallerySnapListContainerAdapter(Activity activity) {
        mActivity = activity;
        mDishes = new ArrayList<>();
    }

    public void addSnap(DishList dish) {
        mDishes.add(dish);
    }

    @Override
    public int getItemViewType(int position) {
        return HORIZONTAL;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.consumer_gallery_view_item_container, parent, false);
        return new ViewHolder(view);

    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        DishList dishList = mDishes.get(position);
//        String color = colorChooser.getColor(dishList.getText());
//        if (color != null) {
//            holder.snapTextView.setBackgroundColor(Color.parseColor(color));
//        }
        StringBuilder sb = new StringBuilder();
        sb.append(dishList.getText());
        sb.setCharAt(0, Character.toUpperCase(sb.charAt(0)));
        holder.snapTextView.setText(sb.toString());
        LinearLayoutManager layoutManager = new LinearLayoutManager(holder.recyclerView.getContext(), LinearLayoutManager.HORIZONTAL, false);
        holder.recyclerView.setLayoutManager(layoutManager);
        holder.recyclerView.setOnFlingListener(null);
//        final int divider = holder.recyclerView.getContext().getResources().getDimension(R.dimen.di);
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(holder.recyclerView.getContext(),
                layoutManager.getOrientation());
        dividerItemDecoration.setDrawable(holder.recyclerView.getResources().getDrawable(R.drawable.divider_hor));
        holder.recyclerView.addItemDecoration(dividerItemDecoration);

        new LinearSnapHelper().attachToRecyclerView(holder.recyclerView);
        holder.recyclerView.setAdapter(new GallerySnapListAdapter(mActivity, mActivity.getApplicationContext(), true, false, dishList.getApps()));
    }

    @Override
    public int getItemCount() {
        return mDishes.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        public TextView snapTextView;
        public RecyclerView recyclerView;

        public ViewHolder(View itemView) {
            super(itemView);
            snapTextView = (TextView) itemView.findViewById(R.id.snapTextView);
            recyclerView = (RecyclerView) itemView.findViewById(R.id.recyclerView);
        }

    }
}