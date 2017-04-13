package com.codepath.hungrybird.model;

import java.util.List;

/**
 * Created by dshah on 4/10/2017.
 */

public class DishList {
    private String mText;
    private List<Dish> mApps;

    public DishList(int gravity, String text, List<Dish> apps) {
        mText = text;
        mApps = apps;
    }

    public String getText(){
        return mText;
    }

    public List<Dish> getApps(){
        return mApps;
    }
}
