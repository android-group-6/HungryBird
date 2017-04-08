package com.codepath.hungrybird.model;

import com.parse.ParseClassName;
import com.parse.ParseUser;

/**
 * Created by ajasuja on 4/8/17.
 */
@ParseClassName("_User")
public class User extends ParseUser {
    public User() {
        //no-op
    }

    public boolean isChef() {
        return getBoolean("isChef");
    }

    public void setChef(boolean chef) {
        put("isChef", chef);
    }
}
