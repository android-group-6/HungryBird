package com.codepath.hungrybird.common;

import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Toast;

import com.codepath.hungrybird.R;
import com.codepath.hungrybird.chef.activities.ChefLandingActivity;
import com.codepath.hungrybird.consumer.activities.GalleryActivity;
import com.codepath.hungrybird.databinding.ActivityLoginBinding;
import com.codepath.hungrybird.model.User;
import com.parse.LogInCallback;
import com.parse.ParseException;
import com.parse.ParseUser;
import com.parse.SignUpCallback;

public class LoginActivity extends AppCompatActivity {
    ActivityLoginBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_login);
        binding.buttonLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(LoginActivity.this, "Login", Toast.LENGTH_LONG).show();
                String userName = binding.editTextUsername.getText().toString();
                String password = binding.editTextPassword.getText().toString();
                boolean isChef = binding.activityLoginLoginTypeChck.isChecked();
                User.logInInBackground(userName, password, new LogInCallback() {
                    @Override
                    public void done(ParseUser parseUser, ParseException e) {
                        if (e == null) {
                            User user = (User) parseUser;
                            if (user.isChef() != isChef) {
                                if (isChef) {
                                    Toast.makeText(LoginActivity.this, "Login failed ... earlier consumer now chef", Toast.LENGTH_LONG).show();
                                } else {
                                    Toast.makeText(LoginActivity.this, "Login failed ... earlier chef now consumer", Toast.LENGTH_LONG).show();
                                }
                                return;
                            }
                            Toast.makeText(LoginActivity.this, "Login Successful ... " + user.getObjectId(), Toast.LENGTH_LONG).show();
                            //TODO save this user in shared pref.

                            // transfer him to the corrrect screen. according to isChef flag.
                            Intent intent;
                            if (isChef) {
                                intent = new Intent(LoginActivity.this, ChefLandingActivity.class);
                            } else {
                                intent = new Intent(LoginActivity.this, GalleryActivity.class);
                            }
                            startActivity(intent);
                        } else {
                            Toast.makeText(LoginActivity.this, "Login failed ... " + e.getMessage(), Toast.LENGTH_LONG).show();
                            e.printStackTrace();
                        }
                    }
                });
            }
        });
        binding.buttonSignup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(LoginActivity.this, "Signup", Toast.LENGTH_LONG).show();
                User newUser = new User();
                String userName = binding.editTextUsername.getText().toString();
                String password = binding.editTextPassword.getText().toString();
                boolean isChef = binding.activityLoginLoginTypeChck.isChecked();
                newUser.setUsername(userName);
                newUser.setPassword(password);
                newUser.setChef(isChef);
                newUser.signUpInBackground(new SignUpCallback() {
                    @Override
                    public void done(ParseException e) {
                        if (e == null) {
                            Toast.makeText(LoginActivity.this, "Signup Successful ... " + newUser.getObjectId(), Toast.LENGTH_LONG).show();
                            Intent intent;
                            if (isChef) {
                                intent = new Intent(LoginActivity.this, ChefLandingActivity.class);
                            } else {
                                intent = new Intent(LoginActivity.this, GalleryActivity.class);
                            }
                            startActivity(intent);
                        } else {
                            Toast.makeText(LoginActivity.this, "Signup Failed ... " + e.getMessage(), Toast.LENGTH_LONG).show();
                            e.printStackTrace();
                        }
                    }
                });
            }
        });
//        binding.activityLoginButton.setOnClickListener(v -> {
//            if (binding.activityLoginLoginTypeChck.isChecked()) {
//                Intent i = new Intent(this, ChefLandingActivity.class);
//                startActivity(i);
//            } else {
//                Intent i = new Intent(this, GalleryActivity.class);
//                startActivity(i);
//            }
//        });
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
