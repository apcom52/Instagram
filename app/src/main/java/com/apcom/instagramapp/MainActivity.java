package com.apcom.instagramapp;

import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";
    private static final String APP_PREFERENCES = "InstagramSettings";
    private static final String APP_PREFERENCES_KEY = "access_token";
    private SharedPreferences preferences;
    private FragmentTransaction fragmentTransaction;
    boolean access_token_result = false;
    Fragment profileFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        preferences = getSharedPreferences(APP_PREFERENCES, Context.MODE_PRIVATE);
        fragmentTransaction = getFragmentManager().beginTransaction();

        profileFragment = new ProfileFragment();
        if (savedInstanceState == null) {
            if (isNetworkConnected() && hasValidAccessToken()) { //Если есть сеть и токен валиден, то переходим сразу к профилю
                fragmentTransaction.add(R.id.fragment, profileFragment).commit();
            } else if (!isNetworkConnected() && preferences.getString("access_token", "") != "") { //если сети нет, то токен есть, то переходим к профилю и будем подгружать данные из кэша
                fragmentTransaction.add(R.id.fragment, profileFragment).commit();
            } else if (isNetworkConnected() && !hasValidAccessToken()) { //если сеть есть, но токен не валидный, то отправляем к авторизации
                fragmentTransaction.add(R.id.fragment, new AuthFragment()).commit();
            } else { //Во всех остальных случаях показываем ошибку
                Toast.makeText(getApplicationContext(), "Подключитесь к сети, чтобы войти в приложение!", Toast.LENGTH_LONG).show();
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private boolean isNetworkConnected() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        return cm.getActiveNetworkInfo() != null;
    }

    private boolean hasValidAccessToken() {
        String access_token = preferences.getString(APP_PREFERENCES_KEY, "");

        if (access_token == "") {
            return false;
        }

        /* Проверяем код ответа от Инстаграмма. Если 200 - токен валидный */
        RequestQueue queue = Volley.newRequestQueue(this);
        String url = "https://api.instagram.com/v1/users/self/?access_token=" + access_token;
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject obj = new JSONObject(response);
                    JSONObject meta = obj.getJSONObject("meta");

                    if (meta.getInt("code") == 200) {
                        Log.w(TAG, "Токен действителен");
                        access_token_result = true;
                    }
                } catch (JSONException e) {
                    //do smth
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                //do smth
            }
        });
        queue.add(stringRequest);
        return access_token_result;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onStop() {
        super.onStop();
    }
}
