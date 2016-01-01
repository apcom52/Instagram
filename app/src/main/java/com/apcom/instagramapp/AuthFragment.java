package com.apcom.instagramapp;

/**
 * Created by apcom on 31.12.2015.
 */
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

public class AuthFragment extends Fragment {
    private WebView mWebView;
    private SharedPreferences preferences;
    private static final String APP_PREFERENCES = "InstagramSettings";
    private static final String APP_PREFERENCES_KEY = "access_token";
    public final String TAG = "SignInActivity";
    public final String CLIENT_ID = "c389c87fc6f644c2b699a246fc2446b8";
    public final String AUTH_URL = "https://api.instagram.com/oauth/authorize/?";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.activity_sign_in, container, false);
        mWebView = (WebView) rootView.findViewById(R.id.webView);
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

        String url = AUTH_URL + "client_id=" + CLIENT_ID + "&redirect_uri=http://example.com&response_type=token";

        mWebView.setVerticalScrollBarEnabled(false);
        mWebView.setHorizontalFadingEdgeEnabled(false);
        mWebView.getSettings().setJavaScriptEnabled(true);

        class myWebClient extends WebViewClient {
            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                super.onPageStarted(view, url, favicon);
            }

            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                view.loadUrl(url);
                return true;
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                String access_token = getAccessToken(url);
                if (!access_token.equals("")) {
                    if (Profile.getAccess_token().isEmpty()) {
                        Editor editor = preferences.edit();
                        editor.putString(APP_PREFERENCES_KEY, access_token);
                        editor.apply();

                        Profile.setAccess_token(access_token);
                        Fragment profileFragment = new ProfileFragment();
                        FragmentTransaction transaction = getFragmentManager().beginTransaction();
                        transaction.replace(R.id.fragment, new ProfileFragment());
                        transaction.addToBackStack(null);
                        transaction.commit();
                    }
                }
            }
        }

        mWebView.setWebViewClient(new myWebClient());
        mWebView.loadUrl(url);
    }

    private String getAccessToken(String url) {
        final String KEYWORD = "access_token";
        final int KEYWORD_LENGHT = KEYWORD.length();

        if (url.contains(KEYWORD)) {
            int startPosition = url.indexOf(KEYWORD);
            int access_tokenLen = KEYWORD_LENGHT;
            String access_token = url.substring(startPosition + KEYWORD_LENGHT + 1);
            return access_token;
        }
        return "";
    }
}
