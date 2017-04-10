package com.codepath.hungrybird;

import android.app.Application;

import com.codepath.hungrybird.model.Dish;
import com.codepath.hungrybird.model.Order;
import com.codepath.hungrybird.model.OrderDishRelation;
import com.codepath.hungrybird.model.User;
import com.crashlytics.android.Crashlytics;
import com.parse.Parse;
import com.parse.ParseACL;
import com.parse.ParseUser;
import com.parse.interceptors.ParseLogInterceptor;

import io.fabric.sdk.android.Fabric;

import static com.parse.ParseObject.registerSubclass;

/**
 * Created by ajasuja on 4/4/17.
 */
public class HungryBirdApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        Fabric.with(this, new Crashlytics());
        // Use for troubleshooting -- remove this line for production
        Parse.setLogLevel(Parse.LOG_LEVEL_DEBUG);

        registerModels();

        // set applicationId, and server server based on the values in the Heroku settings.
        // clientKey is not needed unless explicitly configured
        // any network interceptors must be added with the Configuration Builder given this syntax
        String PARSE_SERVER_URL = "https://parse-demo-2.herokuapp.com/parse/";
        String PARSE_APP_ID = "parse-demo-2-app-id";
        Parse.initialize(new Parse.Configuration.Builder(this)
                .applicationId(PARSE_APP_ID) // should correspond to APP_ID env variable
                .clientKey(null)  // set explicitly unless clientKey is explicitly configured on Parse server
                .addNetworkInterceptor(new ParseLogInterceptor())
                .server(PARSE_SERVER_URL).build());

        ParseUser.enableAutomaticUser();
        ParseACL parseACL = new ParseACL();
        parseACL.setPublicReadAccess(true);
        parseACL.setPublicWriteAccess(true);
    }

    private void registerModels() {
        registerSubclass(User.class);
        registerSubclass(Dish.class);
        registerSubclass(Order.class);
        registerSubclass(OrderDishRelation.class);
    }
}
