package com.apcom.instagramapp;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by apcom on 02.01.2016.
 */
public class ImageActivity extends Activity {
    private static final String TAG = "ImageActivity";
    private String id;
    private RequestQueue queue;
    private ImageView mMedia;
    private TextView mDescription;
    private TextView mCounters;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image);

        Intent intent = getIntent();
        id = intent.getStringExtra("id");
        queue = Volley.newRequestQueue(getApplicationContext());

        mMedia = (ImageView)findViewById(R.id.mediaImage);
        mDescription = (TextView)findViewById(R.id.mediaDescription);
        mCounters = (TextView)findViewById(R.id.mediaLikes);

        mCounters.setTextColor(Color.argb(180, 255, 255, 255));

        String url = "https://api.instagram.com/v1/media/" + id + "?access_token=" + Profile.getAccess_token();
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject obj = new JSONObject(response);
                    JSONObject data = obj.getJSONObject("data");

                    String imageUrl = data.getJSONObject("images").getJSONObject("standard_resolution").getString("url");

                    String description = "Нет описания";
                    if (!data.isNull("caption")) {
                        description = data.getJSONObject("caption").getString("text");
                    }
                    int likesCount = data.getJSONObject("likes").getInt("count");
                    int commentsCount = data.getJSONObject("comments").getInt("count");

                    mDescription.setText(description);
                    mCounters.setText(likesCount + " likes\n" + commentsCount + " comments");

                    Picasso.with(getApplicationContext()).load(imageUrl).into(mMedia);
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
    }
}
