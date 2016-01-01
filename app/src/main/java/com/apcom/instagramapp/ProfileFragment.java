package com.apcom.instagramapp;

import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by apcom on 31.12.2015.
 */
public class ProfileFragment extends Fragment {
    private static final String TAG = "ProfileActivity";
    private static final String APP_PREFERENCES = "InstagramSettings";
    private static final String APP_PREFERENCES_KEY = "access_token";
    private static final String APP_PREFERENCES_FULLNAME = "full_name";
    private static final String APP_PREFERENCES_USERNAME = "username";
    private static final String APP_PREFERENCES_PROFILE_PHOTO = "profile_photo";
    private static final String APP_PREFERENCES_WEBSITE = "website";
    private static final String APP_PREFERENCES_BIO = "bio";
    private int TIMER_DELAY = 1000;
    private int TIMER_TIME = 2000;
    private String access_token = "";
    private SharedPreferences preferences;
    private String imageUrl;
    private ArrayList<GalleryItem> galleryItems;
    private Timer timer;
    private MyTimerTask myTimerTask;
    private int imagesLoaded = 0;

    private TextView mFullnameTextView;
    private TextView mUsernameTextView;
    private ImageView mProfileImageView;
    private TextView mProfileBio;
    private TextView mProfileWebSite;
    private CustomGridView mGallery;
    private RequestQueue queue;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.activity_profile, container, false);
        mFullnameTextView = (TextView)rootView.findViewById(R.id.profileFullName);
        mUsernameTextView = (TextView)rootView.findViewById(R.id.profileUsername);
        mProfileImageView = (ImageView)rootView.findViewById(R.id.profilePhoto);
        mProfileBio = (TextView)rootView.findViewById(R.id.profileBio);
        mProfileWebSite = (TextView)rootView.findViewById(R.id.profileWebSite);
        mGallery = (CustomGridView)rootView.findViewById(R.id.gallery);

        access_token = Profile.getAccess_token();
        queue = Volley.newRequestQueue(getActivity().getApplicationContext());
        return rootView;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        preferences = getActivity().getSharedPreferences(APP_PREFERENCES, Context.MODE_PRIVATE);
    }

    @Override
    public void onStart() {
        super.onStart();
        loadImages();
        String url = "https://api.instagram.com/v1/users/self/?access_token=" + access_token;
        imageUrl = "https://api.instagram.com/v1/users/self/media/recent/?access_token=" + access_token + "&count=5";

        if (isNetworkConnected()) {
            StringRequest stringRequest = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    try {
                        JSONObject obj = new JSONObject(response);
                        Profile.init(obj);

                        Log.w(TAG, "startRequest");

                        mFullnameTextView.setText(Profile.getFull_name());
                        mUsernameTextView.setText("@" + Profile.getUsername());
                        mProfileBio.setText(Profile.getBio());
                        mProfileWebSite.setText(Profile.getWebsite());

                        SharedPreferences.Editor editor = preferences.edit();
                        editor.putString(APP_PREFERENCES_FULLNAME, Profile.getFull_name());
                        editor.putString(APP_PREFERENCES_USERNAME, Profile.getUsername());
                        editor.putString(APP_PREFERENCES_PROFILE_PHOTO, Profile.getProfile_picture());
                        editor.putString(APP_PREFERENCES_WEBSITE, Profile.getWebsite());
                        editor.putString(APP_PREFERENCES_BIO, Profile.getBio());
                        editor.apply();

                        Picasso.with(getActivity().getApplicationContext()).load(Profile.getProfile_picture()).into(mProfileImageView);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Log.w(TAG, "Возникла ошибка при загрузке пользовательских данных");
                }
            });
            queue.add(stringRequest);
        } else {
            String profilePicture = preferences.getString(APP_PREFERENCES_PROFILE_PHOTO, "");
            Picasso.with(getActivity().getApplicationContext()).load(profilePicture).into(mProfileImageView);

            mFullnameTextView.setText(preferences.getString(APP_PREFERENCES_FULLNAME, ""));
            mUsernameTextView.setText("@" + preferences.getString(APP_PREFERENCES_USERNAME, ""));
            mProfileBio.setText(preferences.getString(APP_PREFERENCES_BIO, "Нет данных"));
            mProfileWebSite.setText(preferences.getString(APP_PREFERENCES_WEBSITE, "Не указан"));

            Toast.makeText(getActivity().getApplicationContext(), "Чтобы получить галерею, подключитесь к сети", Toast.LENGTH_LONG).show();
        }
    }

    private void loadImages() {
        imagesLoaded = 0;
        galleryItems = new ArrayList<>();
        String url = "https://api.instagram.com/v1/users/self/media/recent/?access_token=" + access_token;
        Log.w(TAG, url);

        if (timer != null) {
            timer.cancel();
        }

        timer = new Timer();
        myTimerTask = new MyTimerTask();

        timer.schedule(myTimerTask, TIMER_DELAY, TIMER_TIME);

        mGallery.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                GalleryItem current = galleryItems.get(position);
                Intent intent = new Intent(getActivity().getApplicationContext(), ImageActivity.class);
                intent.putExtra("id", current.getId());
                startActivity(intent);
            }
        });
    }

    private void uploadToGrid() {
        Log.w("PhotosUpload", "Загружаем партию фоток");
        imagesLoaded += 5;
        StringRequest loadImagesRequest = new StringRequest(Request.Method.GET, imageUrl, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.w(TAG, "Запустился loadImages()");
                try {
                    JSONObject obj = new JSONObject(response);
                    JSONArray data = obj.getJSONArray("data");
                    JSONObject pagination = obj.getJSONObject("pagination");


                    Log.w(TAG, "Длина массива: " + data.length());
                    for (int i = 0; i < data.length(); i++) {
                        JSONObject item = data.getJSONObject(i);
                        String id = item.getString("id");
                        String imageUrl = item.getJSONObject("images").getJSONObject("thumbnail").getString("url");
                        Log.w(TAG, imageUrl);
                        galleryItems.add(new GalleryItem(imageUrl, id));
                    }
                    mGallery.setAdapter(new GalleryAdapter(getActivity().getApplicationContext(), galleryItems));

                    imageUrl = pagination.getString("next_url");
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.w(TAG, "Возникла ошибка при загрузке галереи");
            }
        });
        if (imagesLoaded >= 20) { //т.к. максимум получаемых фоток 20, то ограничиваем до этого. Если ограничения нет, то получаем максимум из Profile.getMedia();
            timer.cancel();
            timer = null;
        }
        queue.add(loadImagesRequest);
    }

    class MyTimerTask extends TimerTask {
        @Override
        public void run() {
            uploadToGrid();
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        Log.w("ProfileFragment", "Состояние сохранено");
        outState.putBoolean("imagesLoading", true);
    }

    private boolean isNetworkConnected() {
        ConnectivityManager cm = (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
        return cm.getActiveNetworkInfo() != null;
    }
}
