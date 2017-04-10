package com.codepath.hungrybird.consumer.adapters;

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

import java.util.List;

/**
 * Created by dshah on 4/10/2017.
 */

public class GallerySnapListAdapter extends RecyclerView.Adapter<GallerySnapListAdapter.ViewHolder> {

    private List<Dish> mApps;
    private boolean mHorizontal;
    private boolean mPager;
    Context mContext;

    public GallerySnapListAdapter(Context context, boolean horizontal, boolean pager, List<Dish> apps) {
        mContext = context;
        mHorizontal = horizontal;
        mApps = apps;
        mPager = pager;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
//        if (mPager) {
//            return new ViewHolder(LayoutInflater.from(parent.getContext())
//                    .inflate(R.layout.adapter_pager, parent, false));
//        } else {
//            return mHorizontal ? new ViewHolder(LayoutInflater.from(parent.getContext())
//                    .inflate(R.layout.adapter, parent, false)) :
//                    new ViewHolder(LayoutInflater.from(parent.getContext())
//                            .inflate(R.layout.adapter_vertical, parent, false));
//        }
        return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.consumer_gallery_view_item, parent, false));
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Dish app = mApps.get(position);
        //holder.imageView.setImageResource(app.getDrawable());
        ParseFile parseFile = app.getPrimaryImage();
        if (parseFile != null && parseFile.getUrl() != null) {
            String imgUrl = parseFile.getUrl();
            Glide.with(mContext)
                    .load(imgUrl)
                    .placeholder(R.drawable.ic_launcher)
                    .fallback(R.drawable.futurama)
                    .into(holder.imageView);
        }
        holder.nameTextView.setText(app.getTitle());
        holder.ratingTextView.setText("$" + String.valueOf(app.getPrice()));
    }

    @Override
    public int getItemViewType(int position) {
        return super.getItemViewType(position);
    }

    @Override
    public int getItemCount() {
        return mApps.size();
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
            Log.d("App", mApps.get(getAdapterPosition()).getTitle());
        }
    }

}
