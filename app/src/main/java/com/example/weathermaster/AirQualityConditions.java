package com.example.weathermaster;

import android.animation.ArgbEvaluator;
import android.animation.ObjectAnimator;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.ContextThemeWrapper;
import android.view.View;
import android.view.WindowManager;
import android.webkit.JavascriptInterface;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import java.util.HashMap;
import java.util.Map;

public class AirQualityConditions extends AppCompatActivity {

    private WebView webview;
    private boolean isFirstLoad = true;

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Webview setup
        setupWebView();
    }

    private void setupWebView() {
        webview = findViewById(R.id.webView);
        WebSettings webSettings = webview.getSettings();
        webSettings.setJavaScriptEnabled(true);
        webSettings.setDomStorageEnabled(true);
        webSettings.setAllowFileAccessFromFileURLs(true);
        webSettings.setAllowUniversalAccessFromFileURLs(true);
        webSettings.setAllowContentAccess(true);
        webview.setVerticalScrollBarEnabled(true);
        webview.setHorizontalScrollBarEnabled(false);
        webview.setOverScrollMode(WebView.OVER_SCROLL_NEVER);
        webview.setWebViewClient(new WebViewClientDemo());
        AndroidInterface androidInterface = new AndroidInterface(this);
        webview.addJavascriptInterface(androidInterface, "AndroidInterface");
        webview.setBackgroundColor(getResources().getColor(R.color.yellowBg));
        webSettings.setTextZoom(100);
        webSettings.setAllowFileAccess(true);
        webview.loadUrl("file:///android_asset/pages/conditions_pages/airQuality_forecast.html");

        webview.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                view.getContext().startActivity(intent);
                return true;
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);

                if (isFirstLoad) {
                    isFirstLoad = false;
                    applyThemeToWebView();
                }
            }
        });
    }

    private void applyThemeToWebView() {
        int primaryColor = new ContextThemeWrapper(AirQualityConditions.this, R.style.Base_Theme_WeatherMaster)
                .getTheme()
                .obtainStyledAttributes(new int[]{com.google.android.material.R.attr.colorPrimary})
                .getColor(0, 0);

        String hexColor = String.format("#%06X", (0xFFFFFF & primaryColor));
        String jsCodePrimaryColor = "CreateMaterialYouTheme('" + hexColor + "');";

        webview.evaluateJavascript(jsCodePrimaryColor, null);
    }

    public class AndroidInterface {
        private AirQualityConditions aActivity;

        private static final Map<String, Integer[]> colorMap = new HashMap<String, Integer[]>() {{
            put("Scrolled", new Integer[]{0xFF1d2024, 0xFF111318, 0});
            put("ScrollFalse", new Integer[]{0xFF111318, 0xFF111318, 0});
            put("orange_material_Scrolled", new Integer[]{0xFF251e17, 0xFF18120c, 0});
            // Add other color mappings here...
        }};

        AndroidInterface(AirQualityConditions activity) {
            aActivity = activity;
        }

        @JavascriptInterface
        public void updateStatusBarColor(final String color) {
            aActivity.runOnUiThread(new Runnable() {
                @SuppressLint("ResourceType")
                @RequiresApi(api = Build.VERSION_CODES.O)
                @Override
                public void run() {
                    Integer[] colors = colorMap.get(color);
                    if (colors != null) {
                        int statusBarColor = colors[0];
                        int navigationBarColor = colors[1];
                        int systemUiVisibilityFlags = colors[2];

                        animateStatusBarColor(statusBarColor);
                        animateNavigationBarColor(navigationBarColor);
                        setSystemUiVisibility(systemUiVisibilityFlags);
                    } else {
                        handleSpecialCases(color);
                    }
                }
            });
        }

        private void animateStatusBarColor(int color) {
            int currentStatusBarColor = aActivity.getWindow().getStatusBarColor();
            ObjectAnimator statusBarAnimator = ObjectAnimator.ofObject(
                    aActivity.getWindow(),
                    "statusBarColor",
                    new ArgbEvaluator(),
                    currentStatusBarColor,
                    color
            );
            statusBarAnimator.setDuration(200);
            statusBarAnimator.start();
        }

        private void animateNavigationBarColor(int color) {
            int currentNavigationBarColor = aActivity.getWindow().getNavigationBarColor();
            ObjectAnimator navBarAnimator = ObjectAnimator.ofObject(
                    aActivity.getWindow(),
                    "navigationBarColor",
                    new ArgbEvaluator(),
                    currentNavigationBarColor,
                    color
            );
            navBarAnimator.setDuration(200);
            navBarAnimator.start();
        }

        private void setSystemUiVisibility(int flags) {
            View decorView = aActivity.getWindow().getDecorView();
            decorView.setSystemUiVisibility(flags);
        }

        private void handleSpecialCases(String color) {
            switch (color) {
                case "GoBack":
                    aActivity.onBackPressed();
                    break;
                case "keepiton":
                    aActivity.getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
                    showToast("Your device will stay awake");
                    break;
                case "keepitoff":
                    aActivity.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
                    showToast("Your device will go to sleep at the default time");
                    break;
                default:
                    showToast("not found");
                    break;
            }
        }

        private void showToast(String message) {
            Toast.makeText(aActivity, message, Toast.LENGTH_SHORT).show();
        }
    }

    private static class WebViewClientDemo extends WebViewClient {

        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            if (shouldOpenInBrowser(url)) {
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                view.getContext().startActivity(intent);
                return true;
            } else {
                view.loadUrl(url);
                return true;
            }
        }

        private boolean shouldOpenInBrowser(String url) {
            return url.startsWith("https://fonts.google.com/specimen/Outfit?query=outfit") ||
                    url.startsWith("https://openweathermap.org/") ||
                    url.startsWith("https://fonts.google.com/specimen/Poppins?query=poppins") ||
                    url.startsWith("https://github.com/material-components/material-web") ||
                    url.startsWith("https://app-privacy-policy-generator.nisrulz.com/") ||
                    url.startsWith("https://github.com/PranshulGG/WeatherMaster") ||
                    url.startsWith("mailto:pranshul.devmain@gmail.com") ||
                    url.startsWith("https://github.com/PranshulGG/WeatherMaster/releases") ||
                    url.startsWith("https://leafletjs.com/");
        }
    }
}
