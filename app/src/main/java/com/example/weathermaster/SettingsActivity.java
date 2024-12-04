package com.example.weathermaster;

import android.animation.ArgbEvaluator;
import android.animation.ObjectAnimator;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.webkit.JavascriptInterface;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;
import android.widget.Toast;


import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.android.material.snackbar.Snackbar;

public class SettingsActivity extends AppCompatActivity {

    private WebView webview;

    public void onBackPressed() {
        if (webview.canGoBack()) {
            webview.goBack();
        } else {
            super.onBackPressed();
        }
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
        webview.addJavascriptInterface(new ShowToastInterface(this), "ToastAndroidShow");
        webview.addJavascriptInterface(new ShowSnackInterface(this), "ShowSnackMessage");
        webview.setBackgroundColor(getResources().getColor(R.color.yellowBg));

        webview.loadUrl("file:///android_asset/pages/settings.html");
        webSettings.setTextZoom(100);

    }

    private void requestNotificationPermissions() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.POST_NOTIFICATIONS)
                    != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.POST_NOTIFICATIONS}, 100);
            }
        }
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 100) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {


            } else {
                webview.evaluateJavascript("handlePermissionDenied()", null);

            }
        }
    }

    public class ShowSnackInterface {
        private final Context mContext;

        public ShowSnackInterface(Context context) {
            this.mContext = context;
        }

        @JavascriptInterface
        public void ShowSnack(final String text, final String time) {
            int duration = Snackbar.LENGTH_SHORT;
            if ("long".equals(time)) {
                duration = Snackbar.LENGTH_LONG;
            } else if ("short".equals(time)){
                duration = Snackbar.LENGTH_SHORT;
            }


            Snackbar snackbar = Snackbar.make(((Activity) mContext).findViewById(android.R.id.content), text, duration);

            View snackbarView = snackbar.getView();


            snackbarView.setBackgroundResource(R.drawable.snackbar_background);


            snackbar.setTextColor(ContextCompat.getColor(mContext, R.color.snackbar_text));


            ViewGroup.LayoutParams params = snackbar.getView().getLayoutParams();
            if (params instanceof ViewGroup.MarginLayoutParams) {
                ViewGroup.MarginLayoutParams marginParams = (ViewGroup.MarginLayoutParams) params;
                marginParams.bottomMargin = 34;
                marginParams.leftMargin = 26;
                marginParams.rightMargin = 26;
                snackbar.getView().setLayoutParams(marginParams);
            }


            snackbar.show();
        }
    }

    public class ShowToastInterface {
        private final Context mContext;

        public ShowToastInterface(Context context) {
            this.mContext = context;
        }

        @JavascriptInterface
        public void ShowToast(final String text, final String time) {
            int duration = Toast.LENGTH_SHORT;
            if (time.equals("long")) {
                duration = Toast.LENGTH_LONG;
            } else if(time.equals("short")){
                duration = Toast.LENGTH_SHORT;
            }
            Toast.makeText(mContext, text, duration).show();
        }
    }

    public class AndroidInterface {
        private SettingsActivity sActivity;

        AndroidInterface(SettingsActivity activity) {
            sActivity = activity;
        }

        @JavascriptInterface
        public void updateStatusBarColor(final String color) {
            sActivity.runOnUiThread(new Runnable() {
                @SuppressLint("ResourceType")
                @RequiresApi(api = Build.VERSION_CODES.O)
                @Override
                public void run() {
                    int statusBarColor;
                    int navigationBarColor;
                    int systemUiVisibilityFlags = View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR | View.SYSTEM_UI_FLAG_LIGHT_NAVIGATION_BAR;


                    if(color.equals("Scrolled")){
                        statusBarColor = 0xFF1d2024;
                        navigationBarColor = 0xFF111318;
                        systemUiVisibilityFlags = 0;

                    } else if (color.equals("ScrollFalse")) {
                        statusBarColor = 0xFF111318;
                        navigationBarColor = 0xFF111318;
                        systemUiVisibilityFlags = 0;

                    } else if (color.equals("DialogNotScrolled")) {
                        statusBarColor = 0xFF07080a;
                        navigationBarColor = 0xFF07080a;
                        systemUiVisibilityFlags = 0;

                    } else if (color.equals("DialogScrolled")) {
                        statusBarColor = 0xFF0c0d0e;
                        navigationBarColor = 0xFF07080a;
                        systemUiVisibilityFlags = 0;

                    } else if (color.equals("GoBack")){
                        back();
                        return;
                    } else if (color.equals("OpenAboutPage")){
                        openAboutPage();
                        return;
                    } else if (color.equals("OpenLanguagesPage")){
                        openLanguagesPage();
                        return;
                    } else if (color.equals("openHomelocationPage")){
                        openHomelocationPage();
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
                        Toast.makeText(sActivity, "Your device will stay awake", Toast.LENGTH_SHORT).show();
                        return;
                    } else if (color.equals("ItsOff")) {
                        Toast.makeText(sActivity, "Your device will go to sleep at the default time", Toast.LENGTH_SHORT).show();
                        return;
                    } else if(color.equals("ReqNotification")) {
                        requestNotificationPermissions();
                        return;
                    } else {
                        Toast.makeText(sActivity, "not found", Toast.LENGTH_SHORT).show();
                        return;
                    }


                    int currentStatusBarColor = sActivity.getWindow().getStatusBarColor();
                    int currentNavigationBarColor = sActivity.getWindow().getNavigationBarColor();

                    ObjectAnimator statusBarAnimator = ObjectAnimator.ofObject(
                            sActivity.getWindow(),
                            "statusBarColor",
                            new ArgbEvaluator(),
                            currentStatusBarColor,
                            statusBarColor
                    );

                    statusBarAnimator.setDuration(200);
                    statusBarAnimator.start();

                    ObjectAnimator navBarAnimator = ObjectAnimator.ofObject(
                            sActivity.getWindow(),
                            "navigationBarColor",
                            new ArgbEvaluator(),
                            currentNavigationBarColor,
                            navigationBarColor
                    );

                    navBarAnimator.setDuration(200);
                    navBarAnimator.start();

                    sActivity.getWindow().setNavigationBarColor(navigationBarColor);

                    View decorView = sActivity.getWindow().getDecorView();
                    decorView.setSystemUiVisibility(systemUiVisibilityFlags);


                }
            });
        }



        public void openAboutPage() {
            Intent intent = new Intent(sActivity, AboutPage.class);
            sActivity.startActivity(intent);
        }
        public void openLanguagesPage() {
            Intent intent = new Intent(sActivity, LanguagePage.class);
            sActivity.startActivity(intent);
        }

        public void openHomelocationPage() {
            Intent intent = new Intent(sActivity, Homelocations.class);
            sActivity.startActivity(intent);
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
                    url.startsWith("https://www.visualcrossing.com/") ||
                    url.startsWith("https://open-meteo.com/") ||
                    url.startsWith("https://opencagedata.com/api") ||
                    url.startsWith("https://fonts.google.com/specimen/Poppins?query=poppins") ||
                    url.startsWith("https://github.com/material-components/material-web") ||
                    url.startsWith("https://app-privacy-policy-generator.nisrulz.com/") ||
                    url.startsWith("https://github.com/PranshulGG/WeatherMaster") ||
                    url.startsWith("mailto:pranshul.devmain@gmail.com")||
                    url.startsWith("https://ko-fi.com/pranshulgg")||
                    url.startsWith("https://www.openstreetmap.org/") ||
                    url.startsWith("https://leafletjs.com/")||
                    url.startsWith("https://www.rainviewer.com/")||
                    url.startsWith("https://carto.com/")||
                    url.startsWith("https://github.com/PranshulGG/WeatherMaster/releases") ||
                    url.startsWith("https://discord.gg/sSW2E4nqmn");


        }


    }
}