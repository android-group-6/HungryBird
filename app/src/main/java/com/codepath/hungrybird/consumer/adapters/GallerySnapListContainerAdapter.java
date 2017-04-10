package com.codepath.hungrybird.consumer.adapters;

import android.content.Context;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.LinearSnapHelper;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.codepath.hungrybird.R;
import com.codepath.hungrybird.model.DishList;
import com.github.rubensousa.gravitysnaphelper.GravitySnapHelper;

import java.util.ArrayList;

/**
 * Created by dshah on 4/10/2017.
 */

public class GallerySnapListContainerAdapter extends RecyclerView.Adapter<GallerySnapListContainerAdapter.ViewHolder> implements GravitySnapHelper.SnapListener {

    public static final int VERTICAL = 0;
    public static final int HORIZONTAL = 1;
    Context mContext;

    private ArrayList<DishList> mDishes;
    // Disable touch detection for parent recyclerView if we use vertical nested recyclerViews
    private View.OnTouchListener mTouchListener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View v, MotionEvent event) {
            v.getParent().requestDisallowInterceptTouchEvent(true);
            return false;
        }
    };

    public GallerySnapListContainerAdapter(Context context) {
        mContext = context;
        mDishes = new ArrayList<>();
    }

    public void addSnap(DishList dish) {
        mDishes.add(dish);
    }

    @Override
    public int getItemViewType(int position) {
//        Snap snap = mDishes.get(position);
//        switch (snap.getGravity()) {
//            case Gravity.CENTER_VERTICAL:
//                return VERTICAL;
//            case Gravity.CENTER_HORIZONTAL:
//                return HORIZONTAL;
//            case Gravity.START:
//                return HORIZONTAL;
//            case Gravity.TOP:
//                return VERTICAL;
//            case Gravity.END:
//                return HORIZONTAL;
//            case Gravity.BOTTOM:
//                return VERTICAL;
//        }
        return HORIZONTAL;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
//        View view = viewType == VERTICAL ? LayoutInflater.from(parent.getContext())
//                .inflate(R.layout.adapter_snap_vertical, parent, false)
//                : LayoutInflater.from(parent.getContext())
//                .inflate(R.layout.adapter_snap, parent, false);
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.consumer_gallery_view_item_container, parent, false);

//        if (viewType == VERTICAL) {
//            view.findViewById(R.id.recyclerView).setOnTouchListener(mTouchListener);
//        }

        return new ViewHolder(view);

    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        DishList dishList = mDishes.get(position);
        holder.snapTextView.setText(dishList.getText());

//        if (snap.getGravity() == Gravity.START || snap.getGravity() == Gravity.END) {
//            holder.recyclerView.setLayoutManager(new LinearLayoutManager(holder
//                    .recyclerView.getContext(), LinearLayoutManager.HORIZONTAL, false));
//            holder.recyclerView.setOnFlingListener(null);
//            new GravitySnapHelper(snap.getGravity(), false, this).attachToRecyclerView(holder.recyclerView);
//        } else if (snap.getGravity() == Gravity.CENTER_HORIZONTAL ||
//                snap.getGravity() == Gravity.CENTER_VERTICAL) {
//            holder.recyclerView.setLayoutManager(new LinearLayoutManager(holder
//                    .recyclerView.getContext(), snap.getGravity() == Gravity.CENTER_HORIZONTAL ?
//                    LinearLayoutManager.HORIZONTAL : LinearLayoutManager.VERTICAL, false));
//            holder.recyclerView.setOnFlingListener(null);
//            new LinearSnapHelper().attachToRecyclerView(holder.recyclerView);
//        } else if (snap.getGravity() == Gravity.CENTER) { // Pager snap
//            holder.recyclerView.setLayoutManager(new LinearLayoutManager(holder
//                    .recyclerView.getContext(), LinearLayoutManager.HORIZONTAL, false));
//            holder.recyclerView.setOnFlingListener(null);
//            new GravityPagerSnapHelper(Gravity.START).attachToRecyclerView(holder.recyclerView);
//        } else { // Top / Bottom
//            holder.recyclerView.setLayoutManager(new LinearLayoutManager(holder
//                    .recyclerView.getContext()));
//            holder.recyclerView.setOnFlingListener(null);
//            new GravitySnapHelper(snap.getGravity()).attachToRecyclerView(holder.recyclerView);
//        }
//
//
//        holder.recyclerView.setAdapter(new GallerySnapListAdapter(snap.getGravity() == Gravity.START
//                || snap.getGravity() == Gravity.END
//                || snap.getGravity() == Gravity.CENTER_HORIZONTAL,
//                snap.getGravity() == Gravity.CENTER, snap.getApps()));

        holder.recyclerView.setLayoutManager(new LinearLayoutManager(holder.recyclerView.getContext(), LinearLayoutManager.HORIZONTAL, false));
        holder.recyclerView.setOnFlingListener(null);
        new LinearSnapHelper().attachToRecyclerView(holder.recyclerView);
            holder.recyclerView.setAdapter(new GallerySnapListAdapter(mContext, true, false, dishList.getApps()));
    }

    @Override
    public int getItemCount() {
        return mDishes.size();
    }

    @Override
    public void onSnap(int position) {
        Log.d("Snapped: ", position + "");
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