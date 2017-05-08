

package com.codepath.hungrybird.consumer.adapters;

import android.app.Activity;
import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.codepath.hungrybird.R;
import com.codepath.hungrybird.model.Dish;
import com.parse.ParseFile;

import java.text.DecimalFormat;
import java.util.List;

import jp.wasabeef.glide.transformations.RoundedCornersTransformation;

/**
 * Created by dshah on 4/10/2017.
 */

public class GallerySnapListAdapter extends RecyclerView.Adapter<GallerySnapListAdapter.ViewHolder> {

    private List<Dish> mDishes;
    private boolean mHorizontal;
    private boolean mPager;
    Context mContext;
    GalleryDishSelectedListener galleryDishSelectedListener;
    private static DecimalFormat df = new DecimalFormat();

    static {
        df.setMinimumFractionDigits(2);
        df.setMaximumFractionDigits(2);
    }

    private String getRoundedTwoPlaces(double val) {
        return "$" + df.format(Math.round(100 * val) / 100.0);
    }


    public interface GalleryDishSelectedListener {
        void onDishSelected(View v, Dish dish);
    }

    public GallerySnapListAdapter(Activity activity, Context context, boolean horizontal, boolean pager, List<Dish> apps) {
        galleryDishSelectedListener = (GalleryDishSelectedListener) activity;
        mContext = context;
        mHorizontal = horizontal;
        mDishes = apps;
        mPager = pager;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.consumer_gallery_view_item, parent, false));
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Dish app = mDishes.get(position);
        ParseFile parseFile = app.getPrimaryImage();
        if (parseFile != null && parseFile.getUrl() != null) {
            String imgUrl = parseFile.getUrl();
            Glide.with(mContext)
                    .load(imgUrl)
                    .placeholder(R.drawable.placeholder)
                    .fallback(R.drawable.futurama)
                    .bitmapTransform(
                            new RoundedCornersTransformation(mContext, 5, 5))
                    .into(holder.imageView);
        }
        holder.nameTextView.setText(app.getTitle());
        holder.ratingTextView.setText(getRoundedTwoPlaces(app.getPrice()));
    }

    @Override
    public int getItemViewType(int position) {
        return super.getItemViewType(position);
    }

    @Override
    public int getItemCount() {
        return mDishes.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        public ImageView imageView;
        public TextView nameTextView;
        public TextView ratingTextView;

        public ViewHolder(View itemView) {
            super(itemView);
            itemView.setOnClickListener(this);
            imageView = (ImageView) itemView.findViewById(R.id.imageView);
            nameTextView = (TextView) itemView.findViewById(R.id.nameTextView);
            ratingTextView = (TextView) itemView.findViewById(R.id.ratingTextView);
        }

        @Override
        public void onClick(View v) {
            Log.d("App", mDishes.get(getAdapterPosition()).getObjectId());
            int position = getAdapterPosition();
            if (position != RecyclerView.NO_POSITION) { // Check if an item was deleted, but the user clicked it before the UI removed it
                Dish dish = mDishes.get(position);
                if (galleryDishSelectedListener != null) {
                    galleryDishSelectedListener.onDishSelected(v, dish);
                }
            }
        }
    }

}
