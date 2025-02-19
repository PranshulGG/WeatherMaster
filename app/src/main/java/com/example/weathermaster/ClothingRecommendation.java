package com.example.weathermaster;


import android.animation.ArgbEvaluator;
import android.animation.ObjectAnimator;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.view.ContextThemeWrapper;
import android.view.View;
import android.view.WindowManager;
import android.webkit.JavascriptInterface;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;
import android.widget.Toast;


import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

public class ClothingRecommendation extends AppCompatActivity {

    private WebView webview;
    private boolean isFirstLoad = true;
    public void onBackPressed() {
        super.onBackPressed();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);




        setContentView(R.layout.activity_main);



        // Webview stuff
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
        webview.setBackgroundColor(getResources().getColor(R.color.mainBG));
        setTheme(R.style.blue_caret);
        webSettings.setTextZoom(100);
        webSettings.setAllowFileAccess(true);
        webview.loadUrl("file:///android_asset/pages/ClothingRecommendation.html");

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

                    int primaryColor = new ContextThemeWrapper(ClothingRecommendation.this, R.style.Base_Theme_WeatherMaster)
                            .getTheme()
                            .obtainStyledAttributes(new int[]{com.google.android.material.R.attr.colorPrimary})
                            .getColor(0, 0);

                    String hexColor = String.format("#%06X", (0xFFFFFF & primaryColor));
                    String jsCodePrimaryColor = "CreateMaterialYouTheme('" + hexColor + "');";

                    webview.evaluateJavascript(jsCodePrimaryColor, null);
                }
            }
        });
    }




    public class AndroidInterface {
        private ClothingRecommendation aActivity;

        AndroidInterface(ClothingRecommendation activity) {
            aActivity = activity;
        }

        @JavascriptInterface
        public void updateStatusBarColor(final String color) {
            aActivity.runOnUiThread(new Runnable() {
                @SuppressLint("ResourceType")
                @RequiresApi(api = Build.VERSION_CODES.O)
                @Override
                public void run() {
                    int statusBarColor;
                    int navigationBarColor;
                    int systemUiVisibilityFlags = View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR | View.SYSTEM_UI_FLAG_LIGHT_NAVIGATION_BAR;


                    if(color.equals("Scrolled")){
                        statusBarColor = 0xFF1e2024;
                        navigationBarColor = 0xFF121317;
                        systemUiVisibilityFlags = 0;

                    } else if (color.equals("ScrollFalse")) {
                        statusBarColor = 0xFF121317;
                        navigationBarColor = 0xFF121317;
                        systemUiVisibilityFlags = 0;

                    } else if(color.equals("orange_material_Scrolled")){
                        statusBarColor = 0xFF251e17;
                        navigationBarColor = 0xFF18120c;
                        systemUiVisibilityFlags = 0;

                    } else if (color.equals("orange_material_ScrollFalse")) {
                        statusBarColor = 0xFF251e17;
                        navigationBarColor = 0xFF18120c;
                        systemUiVisibilityFlags = 0;

                    } else if(color.equals("red_material_Scrolled")){
                        statusBarColor = 0xFF271d1b;
                        navigationBarColor = 0xFF1a110f;
                        systemUiVisibilityFlags = 0;

                    } else if (color.equals("red_material_ScrollFalse")) {
                        statusBarColor = 0xFF271d1b;
                        navigationBarColor = 0xFF1a110f;
                        systemUiVisibilityFlags = 0;

                    } else if(color.equals("pink_material_Scrolled")){
                        statusBarColor = 0xFF261d1f;
                        navigationBarColor = 0xFF191113;
                        systemUiVisibilityFlags = 0;

                    } else if (color.equals("pink_material_ScrollFalse")) {
                        statusBarColor = 0xFF261d1f;
                        navigationBarColor = 0xFF191113;
                        systemUiVisibilityFlags = 0;

                    } else if(color.equals("purple_material_Scrolled")){
                        statusBarColor = 0xFF241e22;
                        navigationBarColor = 0xFF171216;
                        systemUiVisibilityFlags = 0;

                    } else if (color.equals("purple_material_ScrollFalse")) {
                        statusBarColor = 0xFF241e22;
                        navigationBarColor = 0xFF171216;
                        systemUiVisibilityFlags = 0;

                    } else if(color.equals("blue_material_Scrolled")){
                        statusBarColor = 0xFF1d2024;
                        navigationBarColor = 0xFF111318;
                        systemUiVisibilityFlags = 0;

                    } else if (color.equals("blue_material_ScrollFalse")) {
                        statusBarColor = 0xFF1d2024;
                        navigationBarColor = 0xFF111318;
                        systemUiVisibilityFlags = 0;

                    } else if(color.equals("yellow_material_Scrolled")){
                        statusBarColor = 0xFF222017;
                        navigationBarColor = 0xFF15130b;
                        systemUiVisibilityFlags = 0;

                    } else if (color.equals("yellow_material_ScrollFalse")) {
                        statusBarColor = 0xFF222017;
                        navigationBarColor = 0xFF15130b;
                        systemUiVisibilityFlags = 0;

                    } else if(color.equals("green_material_Scrolled")){
                        statusBarColor = 0xFF1e201a;
                        navigationBarColor = 0xFF12140e;
                        systemUiVisibilityFlags = 0;

                    } else if (color.equals("green_material_ScrollFalse")) {
                        statusBarColor = 0xFF1e201a;
                        navigationBarColor = 0xFF12140e;
                        systemUiVisibilityFlags = 0;

                    } else if(color.equals("mono_material_Scrolled")){
                        statusBarColor = 0xFF201f1f;
                        navigationBarColor = 0xFF141313;
                        systemUiVisibilityFlags = 0;

                    } else if (color.equals("mono_material_ScrollFalse")) {
                        statusBarColor = 0xFF141313;
                        navigationBarColor = 0xFF141313;
                        systemUiVisibilityFlags = 0;

                    } else if (color.equals("orange_material_DialogNotScrolled")){
                        statusBarColor = 0xFF0a0705;
                        navigationBarColor = 0xFF0a0705;
                        systemUiVisibilityFlags = 0;

                    } else if (color.equals("orange_material_DialogScrolled")) {
                        statusBarColor = 0xFF0f0c09;
                        navigationBarColor = 0xFF0a0705;
                        systemUiVisibilityFlags = 0;

                    } else if(color.equals("red_material_DialogNotScrolled")){
                        statusBarColor = 0xFF0b0706;
                        navigationBarColor = 0xFF0b0706;
                        systemUiVisibilityFlags = 0;

                    } else if (color.equals("red_material_DialogScrolled")) {
                        statusBarColor = 0xFF100c0b;
                        navigationBarColor = 0xFF0b0706;
                        systemUiVisibilityFlags = 0;

                    } else if(color.equals("pink_material_DialogNotScrolled")){
                        statusBarColor = 0xFF090708;
                        navigationBarColor = 0xFF090708;
                        systemUiVisibilityFlags = 0;

                    } else if (color.equals("pink_material_DialogScrolled")) {
                        statusBarColor = 0xFF0e0d0b;
                        navigationBarColor = 0xFF090708;
                        systemUiVisibilityFlags = 0;

                    } else if(color.equals("purple_material_DialogNotScrolled")){
                        statusBarColor = 0xFF090709;
                        navigationBarColor = 0xFF090709;
                        systemUiVisibilityFlags = 0;

                    } else if (color.equals("purple_material_DialogScrolled")) {
                        statusBarColor = 0xFF0e0c0e;
                        navigationBarColor = 0xFF090709;
                        systemUiVisibilityFlags = 0;

                    } else if(color.equals("blue_material_DialogNotScrolled")){
                        statusBarColor = 0xFF07080a;
                        navigationBarColor = 0xFF07080a;
                        systemUiVisibilityFlags = 0;

                    } else if (color.equals("blue_material_DialogScrolled")) {
                        statusBarColor = 0xFF0c0d0e;
                        navigationBarColor = 0xFF07080a;
                        systemUiVisibilityFlags = 0;

                    } else if(color.equals("yellow_material_DialogNotScrolled")){
                        statusBarColor = 0xFF080804;
                        navigationBarColor = 0xFF080804;
                        systemUiVisibilityFlags = 0;

                    } else if (color.equals("yellow_material_DialogScrolled")) {
                        statusBarColor = 0xFF0e0d09;
                        navigationBarColor = 0xFF080804;
                        systemUiVisibilityFlags = 0;

                    } else if(color.equals("green_material_DialogNotScrolled")){
                        statusBarColor = 0xFF070806;
                        navigationBarColor = 0xFF070806;
                        systemUiVisibilityFlags = 0;

                    } else if (color.equals("green_material_DialogScrolled")) {
                        statusBarColor = 0xFF0c0d0a;
                        navigationBarColor = 0xFF070806;
                        systemUiVisibilityFlags = 0;

                    } else if(color.equals("mono_material_DialogNotScrolled")){
                        statusBarColor = 0xFF060606;
                        navigationBarColor = 0xFF060606;
                        systemUiVisibilityFlags = 0;

                    } else if (color.equals("mono_material_DialogScrolled")) {
                        statusBarColor = 0xFF0d0c0c;
                        navigationBarColor = 0xFF060606;
                        systemUiVisibilityFlags = 0;

                    } else if (color.equals("GoBack")){
                        back();
                        return;
                    } else if (color.equals("OpenTermsConditions")){

                        return;
                    } else if (color.equals("OpenPrivacyPolicy")){

                        return;
                    } else if (color.equals("OpenLicenses")){

                        return;

                    } else if (color.equals("bluesetDef")) {

                        return;
                    } else if (color.equals("keepiton")) {
                        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
                        return;
                    } else if (color.equals("keepitoff")) {
                        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
                        return;
                    } else if (color.equals("itsOn")) {
                        Toast.makeText(aActivity, "Your device will stay awake", Toast.LENGTH_SHORT).show();
                        return;
                    } else if (color.equals("ItsOff")) {
                        Toast.makeText(aActivity, "Your device will go to sleep at the default time", Toast.LENGTH_SHORT).show();
                        return;


                    } else {
                        Toast.makeText(aActivity, "not found", Toast.LENGTH_SHORT).show();
                        return;
                    }


                    int currentStatusBarColor = aActivity.getWindow().getStatusBarColor();
                    int currentNavigationBarColor = aActivity.getWindow().getNavigationBarColor();

                    ObjectAnimator statusBarAnimator = ObjectAnimator.ofObject(
                            aActivity.getWindow(),
                            "statusBarColor",
                            new ArgbEvaluator(),
                            currentStatusBarColor,
                            statusBarColor
                    );

                    statusBarAnimator.setDuration(200);
                    statusBarAnimator.start();

                    ObjectAnimator navBarAnimator = ObjectAnimator.ofObject(
                            aActivity.getWindow(),
                            "navigationBarColor",
                            new ArgbEvaluator(),
                            currentNavigationBarColor,
                            navigationBarColor
                    );

                    navBarAnimator.setDuration(200);
                    navBarAnimator.start();

                    aActivity.getWindow().setNavigationBarColor(navigationBarColor);

                    View decorView = aActivity.getWindow().getDecorView();
                    decorView.setSystemUiVisibility(systemUiVisibilityFlags);


                }
            });
        }



    }

    public void back() {
        onBackPressed();
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
                    url.startsWith("mailto:pranshul.devmain@gmail.com")||
                    url.startsWith("https://github.com/PranshulGG/WeatherMaster/releases");


        }


    }
}