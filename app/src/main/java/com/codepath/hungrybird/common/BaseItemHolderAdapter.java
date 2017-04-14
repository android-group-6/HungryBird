package com.codepath.hungrybird.common;

/**
 * Created by gauravb on 4/13/17.
 */

import android.content.Context;
import android.databinding.DataBindingUtil;
import android.databinding.ViewDataBinding;
import android.support.annotation.AnimRes;
import android.support.annotation.IdRes;
import android.support.annotation.LayoutRes;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;

import java.util.ArrayList;
import java.util.List;

public class BaseItemHolderAdapter<T> extends RecyclerView.Adapter<BaseItemHolderAdapter.ViewHolder> {
    protected Context context;

    private List<T> objects = new ArrayList<>();
    private LayoutInflater layoutInflater;
    private int lastShownItem = -1;
    private int layout;
    private View animatableView;
    private Animation animation;
    private View.OnClickListener onClickListener;
    private int animatableViewRes;
    private int animationRes;

    private AdapterViewBinder<T> viewBinder;

    public BaseItemHolderAdapter(Context context, @LayoutRes int layout, List<T> objects) {
        this(context, layout, null, 0, 0, objects);
    }

    public BaseItemHolderAdapter(Context context, AdapterViewBinder<T> viewBinder, @LayoutRes int layout, List<T> objects) {
        this(context, layout, viewBinder, 0, 0, objects);
    }

    public BaseItemHolderAdapter(Context context, @LayoutRes int layout, @Nullable AdapterViewBinder<T> viewBinder, @IdRes int animatableView, @AnimRes int animation, List<T> objects) {
        this.context = context;
        this.layoutInflater = LayoutInflater.from(context);
        this.objects = objects;
        this.layout = layout;
        this.animatableViewRes = animatableView;
        this.animationRes = animation;
        this.viewBinder = viewBinder;
    }

    public List<T> getObjects() {
        return objects;
    }

    public void setObjects(List<T> objects) {
        this.objects = objects;
        notifyDataSetChanged();
        lastShownItem = -1;
    }

    public void addObjects(ListPostion position, T... items){
        for (T item : items) {
            addObject(position,item);
        }
    }
//
    public void addObjects(ListPostion position,ArrayList<T> items){
        for (T item : items) {
            addObject(position,item);
        }
    }

    public void addObject(ListPostion position, T item) {
        if(objects.contains(item)) return;
        switch (position) {
            case TOP:
                this.objects.add(0, item);
                this.notifyItemInserted(0);
                break;
            case BOTTOM:
                this.objects.add(item);
                this.notifyItemInserted(this.objects.size() - 1);
                break;
        }
        //notifyDataSetChanged();
    }

    public boolean removeObjectAtPosition(int position) {
        if (position >= objects.size())
            return false;

        objects.remove(position);
        this.notifyItemRemoved(position);
        return true;
    }

    public T getItem(int position) {
        return objects.get(position);
    }

    @Override
    public int getItemCount() {
        return objects.size();
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    public BaseItemHolderAdapter<T> setViewBinder(AdapterViewBinder<T> viewBinder) {
        this.viewBinder = viewBinder;
        return this;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View convertView = layoutInflater.inflate(layout, parent, false);
        ViewDataBinding binding = DataBindingUtil.inflate(layoutInflater, layout, parent, false);
        convertView.setTag(new ViewHolder(binding));
        ViewHolder viewHolder = (ViewHolder) convertView.getTag();
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(BaseItemHolderAdapter.ViewHolder holder, int position) {
        initializeViews(getItem(position), holder, position);
    }

    private void initializeViews(final T object, final BaseItemHolderAdapter.ViewHolder holder, final int position) {
        if (animationRes != 0 && animationRes != 0) {
            setAnimatableView(holder, animatableViewRes, animationRes);
            setAnimation(position);
        }

        if (viewBinder != null)
            viewBinder.bind(holder, object, position);

        if (onClickListener != null)
            holder.getBaseView().setOnClickListener(onClickListener);
    }

    @Override
    public void onViewDetachedFromWindow(BaseItemHolderAdapter.ViewHolder holder) {
        super.onViewDetachedFromWindow(holder);
        if (animatableView != null)
            holder.clearAnimation(animatableView.getId());
    }

    public View.OnClickListener getOnClickListener() {
        return onClickListener;
    }

    public void setOnClickListener(View.OnClickListener onClickListener) {
        this.onClickListener = onClickListener;
    }

    private void setAnimation(int position) {
        if (animatableView != null && animation != null) {
            if (position > lastShownItem) {
                animatableView.startAnimation(animation);
                lastShownItem = position;
            }
        }
    }

    public void setAnimatableView(ViewHolder holder, @IdRes int viewId, @AnimRes int animationId) {
        animation = AnimationUtils.loadAnimation(context, animationId);
        animatableView = holder.getViewById(viewId);
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private View _baseView;
        public final ViewDataBinding binding;
        public ViewHolder(ViewDataBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
            _baseView = binding.getRoot();
        }

        public View getBaseView() {
            return _baseView;
        }

        public View getViewById(@IdRes int id) {
            return _baseView.findViewById(id);
        }

        public void clearAnimation(@IdRes int id) {
            _baseView.findViewById(id).clearAnimation();
        }
    }

    public interface AdapterViewBinder<T> {
        void bind(ViewHolder holder, T item, int position);
    }

    public enum ListPostion {
        TOP,
        BOTTOM
    }
}