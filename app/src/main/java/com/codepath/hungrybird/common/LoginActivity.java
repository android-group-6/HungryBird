package com.codepath.hungrybird.common;

import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.codepath.hungrybird.R;
import com.codepath.hungrybird.chef.activities.ChefLandingActivity;
import com.codepath.hungrybird.consumer.activities.GalleryActivity;
import com.codepath.hungrybird.databinding.ActivityLoginBinding;

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
    }

}
