package com.codepath.hungrybird.common;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.util.Log;

import com.codepath.hungrybird.HungryBirdApplication;
import com.codepath.hungrybird.model.User;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;

/**
 * Created by ajasuja on 4/8/17.
 */

public class ProfilePhotoAsync extends AsyncTask<String, String, String> {
    public Bitmap bitmap;
    String url;
    String name;
    String email;

    public ProfilePhotoAsync(String name, String email, String url) {
        this.url = url;
        this.name = name;
        this.email = email;
    }

    @Override
    protected String doInBackground(String... params) {
        // Fetching data from URI and storing in bitmap
        bitmap = downloadImageBitmap(url);
        return null;
    }

    @Override
    protected void onPostExecute(String s) {
        super.onPostExecute(s);
//        mProfileImage.setImageBitmap(bitmap);

        saveNewUser(name, email, bitmap);
    }

    public Bitmap downloadImageBitmap(String url) {
        Bitmap bm = null;
        try {
            URL aURL = new URL(url);
            URLConnection conn = aURL.openConnection();
            conn.connect();
            InputStream is = conn.getInputStream();
            BufferedInputStream bis = new BufferedInputStream(is);
            bm = BitmapFactory.decodeStream(bis);
            bis.close();
            is.close();
        } catch (IOException e) {
            Log.e("IMAGE", "Error getting bitmap", e);
        }
        return bm;
    }

    private void saveNewUser(final String name, String email, Bitmap bitmap) {
        User user = new User(ParseUser.getCurrentUser());
        user.setUsername(name);
        user.parseUser.setEmail(email);
        HungryBirdApplication.Instance().setUser(user);

//        Saving profile photo as a ParseFile
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
//        Bitmap bitmap = ((BitmapDrawable) mProfileImage.getDrawable()).getBitmap();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 70, stream);
        byte[] data = stream.toByteArray();
        String thumbName = user.getUsername().replaceAll("\\s+", "");
        final ParseFile parseFile = new ParseFile(thumbName + "_thumb.jpg", data);

        parseFile.saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                user.setProfileImage(parseFile);
//                user.put("profileThumb", parseFile);

                //Finally save all the user details
                user.saveInBackground(e1 -> {
//                        Toast.makeText(MainActivity.this, "New user:" + name + " Signed up", Toast.LENGTH_SHORT).show();
                    Log.d("flow", "New user:" + name + " Signed up");
                });
            }
        });

    }
}