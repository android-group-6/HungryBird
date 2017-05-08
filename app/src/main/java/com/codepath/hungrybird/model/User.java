package com.codepath.hungrybird.model;

import com.parse.ParseFile;
import com.parse.ParseUser;
import com.parse.SaveCallback;

/**
 * Created by ajasuja on 4/8/17.
 */
public class User {
    public final ParseUser parseUser;

    public User(ParseUser parseUser) {
        //no-op
        this.parseUser = parseUser;
    }

    public boolean isChef() {
        return parseUser.getBoolean("isChef");

    }

    public String getObjectId() {
        return parseUser.getObjectId();
    }

    public void setChef(boolean chef) {
        parseUser.put("isChef", chef);
    }

    public ParseFile getProfileImage() {
        return parseUser.getParseFile("profileImage");
    }

    public String getUserImage() {
        ParseFile parseFile = parseUser.getParseFile("userImage");
        if (parseFile != null) {
            return parseFile.getUrl();
        }
        return null;
    }

    public String getProfileImageUrl() {
        ParseFile parseFile = parseUser.getParseFile("profileImage");
        if (parseFile != null) {
            return parseFile.getUrl();
        }
        return null;
    }

    public void setProfileImage(ParseFile profileImage) {
        parseUser.put("profileImage", profileImage);

    }

    public final void saveInBackground(SaveCallback callback) {
        parseUser.saveInBackground(callback);

    }

    public void setUsername(String username) {
        parseUser.setUsername(username);
    }

    public String getUsername() {
        return parseUser.getUsername();
    }

    public String getEmail() {
        return parseUser.getEmail();
    }

    public String getPrimaryAddress() {
        return parseUser.getString("primaryAddress");
    }

    public void setPrimaryAddress(String address) {
        parseUser.put("primaryAddress", address);
    }


}
