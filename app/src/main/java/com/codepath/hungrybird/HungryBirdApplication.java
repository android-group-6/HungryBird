package com.codepath.hungrybird;

import android.app.Application;

import com.codepath.hungrybird.model.Dish;
import com.codepath.hungrybird.model.Order;
import com.codepath.hungrybird.model.OrderDishRelation;
import com.codepath.hungrybird.model.User;
import com.crashlytics.android.Crashlytics;
import com.facebook.FacebookSdk;
import com.parse.Parse;
import com.parse.ParseACL;
import com.parse.ParseFacebookUtils;
import com.parse.interceptors.ParseLogInterceptor;

import io.fabric.sdk.android.Fabric;
import uk.co.chrisjenx.calligraphy.CalligraphyConfig;

import static com.parse.ParseObject.registerSubclass;

/**
 * Created by ajasuja on 4/4/17.
 */
public class HungryBirdApplication extends Application {
    private User user;
    private static HungryBirdApplication _instance;

    public static HungryBirdApplication Instance() {
        return _instance;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public User getUser() {
        return user;
    }


    @Override
    public void onCreate() {
        super.onCreate();

        CalligraphyConfig.initDefault(new CalligraphyConfig.Builder()
                .setDefaultFontPath("fonts/Rubik-Regular.ttf")
                .setFontAttrId(R.attr.fontPath)
                .build()
        );

        _instance = this;
        Fabric.with(this, new Crashlytics());
        // Use for troubleshooting -- remove this line for production
        Parse.setLogLevel(Parse.LOG_LEVEL_DEBUG);

        registerModels();

        // set applicationId, and server server based on the values in the Heroku settings.
        // clientKey is not needed unless explicitly configured
        // any network interceptors must be added with the Configuration Builder given this syntax
        String PARSE_SERVER_URL = "https://parse-demo-2.herokuapp.com/parse/";
        String PARSE_APP_ID = "parse-demo-2-app-id";

//        String PARSE_SERVER_URL = "https://hungry-bird.herokuapp.com/parse/";
//        String PARSE_APP_ID = "hungry-bird-app-id";

        Parse.initialize(new Parse.Configuration.Builder(this)
                .applicationId(PARSE_APP_ID) // should correspond to APP_ID env variable
                .clientKey(null)  // set explicitly unless clientKey is explicitly configured on Parse server
                .addNetworkInterceptor(new ParseLogInterceptor())
                .server(PARSE_SERVER_URL).enableLocalDataStore().build());

//        ParseUser.enableAutomaticUser();
        ParseACL parseACL = new ParseACL();
        parseACL.setPublicReadAccess(true);
        parseACL.setPublicWriteAccess(true);
        FacebookSdk.sdkInitialize(getApplicationContext());
        ParseFacebookUtils.initialize(getApplicationContext());
    }

    private void registerModels() {
        registerSubclass(Dish.class);
        registerSubclass(Order.class);
        registerSubclass(OrderDishRelation.class);
    }
}
