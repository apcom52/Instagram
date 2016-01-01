package com.apcom.instagramapp;
import android.graphics.Bitmap;
import android.widget.ImageView;
import android.util.Log;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by apcom on 28.12.2015.
 */
public class Profile {
    private static Profile instance;

    private static JSONObject object;
    private static String access_token = "";
    private static int id;
    private static String full_name;
    private static String username;
    private static String profile_picture_url;
    private static Bitmap profile_bitmap;
    private static String bio;
    private static String website;
    private static int media_count;
    private static int follows_count;
    private static int followed_by_count;

    public static void init(JSONObject obj) {
        object = obj;

        try {
            JSONObject data = object.getJSONObject("data");
            id = data.getInt("id");
            full_name = data.getString("full_name");
            username = data.getString("username");
            profile_picture_url = data.getString("profile_picture");
            bio = data.getString("bio");
            website = data.getString("website");

            JSONObject counts = data.getJSONObject("counts");
            media_count = counts.getInt("media");
            follows_count = counts.getInt("follows");
            followed_by_count = counts.getInt("followed_by");
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public static Profile getInstance() {
        return instance;
    }

    private Profile() {
        //empty
    }

    public static int getId() {
        return id;
    }

    public static String getFull_name() {
        return full_name;
    }

    public static String getUsername() {
        return username;
    }

    public static String getProfile_picture() {
        return profile_picture_url;
    }

    public static String getBio() {
        return bio;
    }

    public static String getWebsite() {
        return website;
    }

    public static int getMedia() {
        return media_count;
    }

    public static int getFollows() {
        return follows_count;
    }

    public static int getFollowed_by() {
        return followed_by_count;
    }

    public static String getAccess_token() {
        return access_token;
    }

    public static void setAccess_token(String access_token) {
        Profile.access_token = access_token;
    }
}
