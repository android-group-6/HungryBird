package com.codepath.hungrybird.model;

import com.parse.ParseClassName;
import com.parse.ParseObject;
import com.parse.ParseUser;

/**
 * Created by ajasuja on 4/4/17.
 */
@ParseClassName("Dish")
public class Dish extends ParseObject {

    public String getDishName() {
        return getString("dishName");
    }

    public void setDishName(String dishName) {
        put("dishName", dishName);
    }

    public ParseUser getChef() {
        return getParseUser("chef");
    }

    public void setChef(ParseUser parseUser) {
        put("chef", parseUser);
    }

    public void setCuisine(String cuisine) {
        put("cuisine", cuisine);
    }

    public String getCuisine() {
        return getString("cuisine");
    }

    public static enum Cuisine {
        INDIAN("indian"),
        ITALIAN("italian");

        private String cuisineValue;

        private Cuisine(String cuisineValue) {
            this.cuisineValue = cuisineValue;
        }

        public String getCuisineValue() {
            return cuisineValue;
        }
    }
}
