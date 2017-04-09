package com.codepath.hungrybird.common;

import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Toast;

import com.codepath.hungrybird.R;
import com.codepath.hungrybird.chef.activities.ChefLandingActivity;
import com.codepath.hungrybird.databinding.ActivityLoginBinding;
import com.codepath.hungrybird.model.User;
import com.facebook.AccessToken;
import com.facebook.GraphRequest;
import com.facebook.HttpMethod;
import com.parse.ParseFacebookUtils;
import com.parse.ParseFile;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class LoginActivity extends AppCompatActivity {
    ActivityLoginBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_login);
        binding.activityLoginButton.setOnClickListener(v -> {
            loginWithFacebook();
//            if (binding.activityLoginLoginTypeChck.isChecked()) {
//                Intent i = new Intent(this, ChefLandingActivity.class);
//                startActivity(i);
//            } else {
//                Intent i = new Intent(this, GalleryActivity.class);
//                startActivity(i);
//            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        ParseFacebookUtils.onActivityResult(requestCode, resultCode, data);
    }

    private void loginWithFacebook() {
        ArrayList<String> permissions = new ArrayList();
        permissions.add("email");
        permissions.add("public_profile");
        ParseFacebookUtils.logInWithReadPermissionsInBackground(LoginActivity.this, permissions,
                (user, err) -> {
                    if (err != null) {
                        Log.d("MyApp", "Uh oh. Error occurred" + err.toString());
                    } else if (user == null) {
                        Log.d("MyApp", "Uh oh. The user cancelled the Facebook login.");
                    } else if (user.isNew()) {
                        Log.d("MyApp", "User signed up and logged in through Facebook!");
                        getUserDetailsFromFB();
                    } else {
                        Toast.makeText(LoginActivity.this, "Logged in", Toast.LENGTH_LONG).show();
                        Log.d("MyApp", "User logged in through Facebook!");
                        getUserDetailsFromParse();
                        Intent intent = new Intent(this, ChefLandingActivity.class);
                        startActivity(intent);
                    }
                });
    }

    private void getUserDetailsFromFB() {
        // Suggested by https://disqus.com/by/dominiquecanlas/
        Bundle parameters = new Bundle();
        parameters.putString("fields", "email,name,picture");

        new GraphRequest(
                AccessToken.getCurrentAccessToken(),
                "/me",
                parameters,
                HttpMethod.GET,
                response -> {
                    try {
                        JSONObject jsonObject = response.getJSONObject();

                        String email = jsonObject.getString("email");
//                            mEmailID.setText(email);

                        String name = jsonObject.getString("name");
//                            mUsername.setText(name);

                        JSONObject picture = jsonObject.getJSONObject("picture");
                        JSONObject data = picture.getJSONObject("data");

                        //  Returns a 50x50 profile picture
                        String pictureUrl = data.getString("url");

                        new ProfilePhotoAsync(name, email, pictureUrl).execute();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
        ).executeAsync();
    }

    private void getUserDetailsFromParse() {

        User user = (User) User.getCurrentUser();

//Fetch profile photo
        try {
            ParseFile parseFile = user.getProfileImage();
            if (parseFile != null) {
                byte[] data = parseFile.getData();
                Bitmap bitmap = BitmapFactory.decodeByteArray(data, 0, data.length);
//            mProfileImage.setImageBitmap(bitmap);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

//        mEmailID.setText(parseUser.getEmail());
//        mUsername.setText(parseUser.getUsername());

//        Toast.makeText(MainActivity.this, "Welcome back " + mUsername.getText().toString(), Toast.LENGTH_SHORT).show();
    }
}
