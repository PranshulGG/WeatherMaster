package com.example.weathermaster;

import android.Manifest;
import android.animation.ArgbEvaluator;
import android.animation.ObjectAnimator;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.text.InputType;
import android.view.View;
import android.view.WindowManager;
import android.webkit.GeolocationPermissions;
import android.webkit.JavascriptInterface;
import android.webkit.JsPromptResult;
import android.webkit.JsResult;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;


public class MainActivity extends AppCompatActivity {


    private WebView webview;


    private static final int PERMISSION_REQUEST_CODE = 1;


    private boolean doubleBackToExitPressedOnce = false;

    @Override
    public void onBackPressed() {
        if (webview.canGoBack()) {
            webview.goBack();
        } else {
            if (doubleBackToExitPressedOnce) {
                super.onBackPressed();
                return;
            }

            this.doubleBackToExitPressedOnce = true;
            Toast.makeText(this, "Press again to exit", Toast.LENGTH_SHORT).show();

            new Handler().postDelayed(() -> doubleBackToExitPressedOnce = false, 2000);
        }
    }


    //    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        setContentView(R.layout.activity_main);


        webview = findViewById(R.id.webView);
        WebSettings webSettings = webview.getSettings();
        webSettings.setJavaScriptEnabled(true);
        webSettings.setDomStorageEnabled(true);
        webSettings.setAllowFileAccessFromFileURLs(true);
        webSettings.setAllowUniversalAccessFromFileURLs(true);
        webSettings.setAllowContentAccess(true);
        webview.setOverScrollMode(WebView.OVER_SCROLL_NEVER);
        webview.setVerticalScrollBarEnabled(false);
        webview.setHorizontalScrollBarEnabled(false);
        webview.setWebViewClient(new WebViewClientDemo());

        webSettings.setGeolocationEnabled(true);

        AndroidInterface androidInterface = new AndroidInterface(this);
        webview.addJavascriptInterface(androidInterface, "AndroidInterface");
        webview.addJavascriptInterface(new ShowToastInterface(this), "ToastAndroidShow");
        webview.setBackgroundColor(getResources().getColor(R.color.diffDefault));





        webview.setWebChromeClient(new WebChromeClient() {
            @Override
            public boolean onJsAlert(WebView view, String url, String message, JsResult result) {
                new AlertDialog.Builder(view.getContext())
                        .setTitle("Input error")
                        .setMessage(message)
                        .setPositiveButton("OK", (DialogInterface dialog, int which) -> result.confirm())
                        .setOnDismissListener((DialogInterface dialog) -> result.confirm())
                        .create()
                        .show();
                return true;
            }


            @Override
            public boolean onJsConfirm(WebView view, String url, String message, JsResult result) {
                new AlertDialog.Builder(view.getContext())
                        .setTitle("Confirm delete")
                        .setMessage(message)
                        .setPositiveButton("OK", (DialogInterface dialog, int which) -> result.confirm())
                        .setNegativeButton("Cancel", (DialogInterface dialog, int which) -> result.cancel())
                        .setOnDismissListener((DialogInterface dialog) -> result.cancel())
                        .create()
                        .show();
                return true;
            }

            @Override
            public boolean onJsPrompt(WebView view, String url, String message, String defaultValue, JsPromptResult result) {
                final EditText input = new EditText(view.getContext());
                input.setInputType(InputType.TYPE_CLASS_TEXT);
                input.setText(defaultValue);
                new AlertDialog.Builder(view.getContext())
                        .setTitle("Confirm Delete")
                        .setMessage(message)
                        .setView(input)
                        .setPositiveButton("OK", (DialogInterface dialog, int which) -> result.confirm(input.getText().toString()))
                        .setNegativeButton("Cancel", (DialogInterface dialog, int which) -> result.cancel())
                        .setOnDismissListener((DialogInterface dialog) -> result.cancel())
                        .create()
                        .show();
                return true;
            }


            @Override
            public void onGeolocationPermissionsShowPrompt(String origin, GeolocationPermissions.Callback callback) {
                // Automatically grant permission
                callback.invoke(origin, true, false);
            }


        });


        webview.loadUrl("file:///android_asset/index.html");


    }


    private void requestLocationPermissions() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, PERMISSION_REQUEST_CODE);
        }
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission granted, proceed with enabling geolocation in WebView
                webview.getSettings().setGeolocationEnabled(true);
                webview.reload();
            } else {



            }
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
        private MainActivity mActivity;

        AndroidInterface(MainActivity activity) {
            mActivity = activity;
        }

        @JavascriptInterface
        public void updateStatusBarColor(final String color) {
            mActivity.runOnUiThread(new Runnable() {
                @SuppressLint("ResourceType")
                @RequiresApi(api = Build.VERSION_CODES.O)
                @Override
                public void run() {
                    int statusBarColor;
                    int navigationBarColor;
                    int systemUiVisibilityFlags = View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR | View.SYSTEM_UI_FLAG_LIGHT_NAVIGATION_BAR;


                    if(color.equals("clear-day")){
                        statusBarColor = 0xFF00043d;
                        navigationBarColor = 0xFF00043d;
                        systemUiVisibilityFlags = 0;
                        webview.setBackgroundColor(getResources().getColor(R.color.C01d));
                        setTheme(R.style.blue_caret);

                    } else if (color.equals("clear-night")) {
                        statusBarColor = 0xFF01023d;
                        navigationBarColor = 0xFF01023d;
                        systemUiVisibilityFlags = 0;
                        webview.setBackgroundColor(getResources().getColor(R.color.C01n));
                        setTheme(R.style.blue_caret);

                    } else if(color.equals("cloudy")){
                            statusBarColor = 0xFF0e1d2a;
                            navigationBarColor = 0xFF0e1d2a;
                            systemUiVisibilityFlags = 0;
                        webview.setBackgroundColor(getResources().getColor(R.color.C02d));
                        setTheme(R.style.blue_caret);

                    } else if(color.equals("overcast")){
                        statusBarColor = 0xFF0e1d2a;
                        navigationBarColor = 0xFF0e1d2a;
                        systemUiVisibilityFlags = 0;
                        webview.setBackgroundColor(getResources().getColor(R.color.C03d));
                        setTheme(R.style.blue_caret);

                    } else if(color.equals("rain")){
                        statusBarColor = 0xFF0e1d2a;
                        navigationBarColor = 0xFF0e1d2a;
                        systemUiVisibilityFlags = 0;
                        webview.setBackgroundColor(getResources().getColor(R.color.C04d));
                        setTheme(R.style.blue_caret);

                    } else if (color.equals("thunder")) {
                        statusBarColor = 0xFF231225;
                        navigationBarColor = 0xFF231225;
                        systemUiVisibilityFlags = 0;
                        webview.setBackgroundColor(getResources().getColor(R.color.C04n));
                        setTheme(R.style.blue_caret);

                    } else if(color.equals("snow")){
                        statusBarColor = 0xFF1f1f30;
                        navigationBarColor = 0xFF1f1f30;
                        systemUiVisibilityFlags = 0;
                        webview.setBackgroundColor(getResources().getColor(R.color.C09d));
                        setTheme(R.style.blue_caret);

                    } else if (color.equals("fog")) {
                        statusBarColor = 0xFF191209;
                        navigationBarColor = 0xFF191209;
                        systemUiVisibilityFlags = 0;
                        webview.setBackgroundColor(getResources().getColor(R.color.C09n));
                        setTheme(R.style.orange_caret);

                    } else if (color.equals("OpenSettings")){
                        openSettingsActivity();
                        return;
                    } else if (color.equals("Open8Forecast")){
                        openForecastPage();
                        return;
                    } else if (color.equals("OpenAlertsPage")){
                        OpenAlertsPage();
                        return;
                    } else if (color.equals("OpenMoonPhasesPage")){
                        OpenMoonPhasesPage();
                        return;
                    } else if (color.equals("OpenAboutPage")){
                        openAboutPage();
                        return;
                    } else if (color.equals("OpenRadar")){
                        openLiveRadarPage();
                        return;
                    } else if (color.equals("GoBack")) {
                        back();
                        return;
                    } else if  (color.equals("ReqLocation")) {
                        requestLocationPermissions();
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
                        Toast.makeText(mActivity, "Your device will stay awake", Toast.LENGTH_SHORT).show();
                        return;
                    } else if (color.equals("ItsOff")) {
                        Toast.makeText(mActivity, "Your device will go to sleep at the default time", Toast.LENGTH_SHORT).show();
                        return;



                    } else {
                        Toast.makeText(mActivity, "not found", Toast.LENGTH_SHORT).show();
                        return;
                    }


                    int currentStatusBarColor = mActivity.getWindow().getStatusBarColor();
                    int currentNavigationBarColor = mActivity.getWindow().getNavigationBarColor();

                    ObjectAnimator statusBarAnimator = ObjectAnimator.ofObject(
                            mActivity.getWindow(),
                            "statusBarColor",
                            new ArgbEvaluator(),
                            currentStatusBarColor,
                            statusBarColor
                    );

                    statusBarAnimator.setDuration(200);
                    statusBarAnimator.start();

                    ObjectAnimator navBarAnimator = ObjectAnimator.ofObject(
                            mActivity.getWindow(),
                            "navigationBarColor",
                            new ArgbEvaluator(),
                            currentNavigationBarColor,
                            navigationBarColor
                    );

                    navBarAnimator.setDuration(200);
                    navBarAnimator.start();

                    mActivity.getWindow().setNavigationBarColor(navigationBarColor);

                    View decorView = mActivity.getWindow().getDecorView();
                    decorView.setSystemUiVisibility(systemUiVisibilityFlags);


                }
            });
        }

                public void openSettingsActivity() {
            Intent intent = new Intent(mActivity, SettingsActivity.class);
                    mActivity.startActivity(intent);
        }

        public void openForecastPage(){
            Intent intent = new Intent(mActivity, ForecastPage.class);
            mActivity.startActivity(intent);
        }

        public void OpenAlertsPage(){
            Intent intent = new Intent(mActivity, AlertPage.class);
            mActivity.startActivity(intent);
        }

        public void OpenMoonPhasesPage(){
            Intent intent = new Intent(mActivity, MoonPhases.class);
            mActivity.startActivity(intent);
        }
        public void openAboutPage() {
            Intent intent = new Intent(mActivity, AboutPage.class);
            mActivity.startActivity(intent);
        }
        public void openLiveRadarPage() {
            Intent intent = new Intent(mActivity, LiveRadar.class);
            mActivity.startActivity(intent);
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
                    url.startsWith("https://github.com/PranshulGG/WeatherMaster/releases")||
                    url.startsWith("https://leafletjs.com/");


        }


    }
}


