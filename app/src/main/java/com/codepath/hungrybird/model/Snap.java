package com.codepath.hungrybird.model;

import java.util.List;

/**
 * Created by dshah on 4/10/2017.
 */

public class Snap {
    private int mGravity;
    private String mText;
    private List<App> mApps;

    public Snap(int gravity, String text, List<App> apps) {
        mGravity = gravity;
        mText = text;
        mApps = apps;
    }

    public String getText(){
        return mText;
    }

    public int getGravity(){
        return mGravity;
    }

    public List<App> getApps(){
        return mApps;
    }
}
