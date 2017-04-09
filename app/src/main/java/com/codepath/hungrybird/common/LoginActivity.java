package com.codepath.hungrybird.common;

import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

import com.codepath.hungrybird.R;
import com.codepath.hungrybird.chef.activities.ChefLandingActivity;
import com.codepath.hungrybird.consumer.activities.GalleryActivity;
import com.codepath.hungrybird.databinding.ActivityLoginBinding;
import com.codepath.hungrybird.model.Order;
import com.codepath.hungrybird.network.ParseClient;

public class LoginActivity extends AppCompatActivity {
    ActivityLoginBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_login);
        binding.activityLoginButton.setOnClickListener(v -> {
            if (binding.activityLoginLoginTypeChck.isChecked()) {
                Intent i = new Intent(this, ChefLandingActivity.class);
                startActivity(i);
            } else {
                Intent i = new Intent(this, GalleryActivity.class);
                startActivity(i);
            }
        });
//        ParseClient.OrderListener orderListener = new ParseClient.OrderListener() {
//            @Override
//            public void onSuccess(Order order) {
//                Toast.makeText(LoginActivity.this, "Success", Toast.LENGTH_SHORT).show();
//            }
//
//            @Override
//            public void onFailure(Exception e) {
//                Toast.makeText(LoginActivity.this, "Failure", Toast.LENGTH_SHORT).show();
//            }
//        };
//        ParseClient parseClient = ParseClient.getInstance();
//        parseClient.addOrder("ZkjdWTqmmC", "bLDkVaY7EF", Order.Status.IN_PROGRESS, orderListener);
//        parseClient.addOrder("ZkjdWTqmmC", "bLDkVaY7EF", Order.Status.ORDERED, orderListener);
//        parseClient.addOrder("ZkjdWTqmmC", "bLDkVaY7EF", Order.Status.DONE, orderListener);
    }

}
