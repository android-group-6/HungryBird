package com.codepath.hungrybird.model;

import com.codepath.hungrybird.HungryBirdApplication;
import com.parse.ParseClassName;
import com.parse.ParseFile;
import com.parse.ParseObject;

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

    public void setTitle(String title) {
        put("title", title);
    }

    public String getTitle() {
        return getString("title");
    }

    public void setDescription(String description) {
        put("description", description);
    }

    public String getDescription() {
        return getString("description");
    }

    public void setPrice(double price) {
        put("price", price);
    }

    public double getPrice() {
        return getDouble("price");
    }

    public void setServingSize(int servingSize) {
        put("servingSize", servingSize);
    }

    public int getServingSize() {
        return getInt("servingSize");
    }

    public void setPrimaryImage(ParseFile imageFile) {
        put("primaryImage", imageFile);
    }

    public ParseFile getPrimaryImage() {
        return getParseFile("primaryImage");
    }

    public User getChef() {
        return  new User(getParseUser("chef"));
    }

    public void setChef(User user) {
        put("chef", user.parseUser);
    }

    public void setVeg(boolean isVeg) {
        put("isVeg", isVeg);
    }

    public boolean isVeg() {
        return getBoolean("isVeg");
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
