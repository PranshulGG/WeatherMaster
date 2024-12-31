package com.example.weathermaster;

import android.Manifest;
import android.animation.ArgbEvaluator;
import android.animation.ObjectAnimator;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.InputType;
import android.util.TypedValue;
import android.view.ContextThemeWrapper;
import android.view.View;
import android.view.ViewGroup;
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
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.res.ResourcesCompat;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.google.android.material.color.DynamicColors;
import com.google.android.material.color.MaterialColors;
import com.google.android.material.snackbar.Snackbar;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;


public class MainActivity extends AppCompatActivity {

    private static final String FIXED_URL = "https://github.com/PranshulGG/WeatherMaster/releases";

    private WebView webview;

    private SwipeRefreshLayout swipeRefreshLayout;

    private static final int PERMISSION_REQUEST_CODE = 1;

    private boolean isPullToRefreshAllowed = true;

    private boolean doubleBackToExitPressedOnce = false;

    @Override
    public void onBackPressed() {
        if (webview.canGoBack()) {
            webview.goBack();
        } else {
                super.onBackPressed();

        }
    }


    //    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        setContentView(R.layout.main_page);
        swipeRefreshLayout = findViewById(R.id.swipeContainer);


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
        webview.addJavascriptInterface(new UpdateWidget1Interface(this), "UpdateWidget1Interface");
        webview.addJavascriptInterface(new ShowSnackInterface(this), "ShowSnackMessage");
        webview.setBackgroundColor(getResources().getColor(R.color.diffDefault));
        webview.getSettings().setGeolocationEnabled(true);
        webSettings.setTextZoom(100);

        webview.getSettings().setAllowFileAccess(true);
        webview.getSettings().setAllowContentAccess(true);
        webview.getSettings().setDomStorageEnabled(true);




        DynamicColors.applyToActivitiesIfAvailable(getApplication());


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
                callback.invoke(origin, true, false);
            }


        });

        webview.addJavascriptInterface(new Object() {
            @JavascriptInterface
            public void onScrollToTop(boolean isAtTop) {
                runOnUiThread(() -> {
                    if (isPullToRefreshAllowed) {
                        swipeRefreshLayout.setEnabled(isAtTop);
                    } else {
                        swipeRefreshLayout.setEnabled(false);
                    }
                });
            }
        }, "AndroidBridge");


        swipeRefreshLayout.setColorSchemeColors(Color.parseColor("#aac7ff"));
        swipeRefreshLayout.setProgressBackgroundColorSchemeColor(Color.parseColor("#282a2f"));



        webview.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);

                webview.evaluateJavascript(
                        "(function() {" +
                                "  var container = document.getElementById('weather_wrap');" +
                                "  function checkScroll() {" +
                                "    var isAtTop = container.scrollTop === 0;" +
                                "    AndroidBridge.onScrollToTop(isAtTop);" +
                                "  }" +
                                "  container.addEventListener('scroll', checkScroll);" +
                                "  checkScroll();" +
                                "})();",
                        null
                );
            }
        });


        swipeRefreshLayout.setOnRefreshListener(() -> {
            if (isPullToRefreshAllowed) {
                isPullToRefreshAllowed = false;

                webview.evaluateJavascript("refreshWeather();", null);


                new Handler(Looper.getMainLooper()).postDelayed(() -> {
                    isPullToRefreshAllowed = true;
                }, 3000);
            } else {

                swipeRefreshLayout.setRefreshing(false);
                webview.evaluateJavascript("WaitBeforeRefresh();", null);
            }
        });

        webview.loadUrl("file:///android_asset/index.html");


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

            TextView textView = snackbarView.findViewById(com.google.android.material.R.id.snackbar_text);
            textView.setTextColor(ContextCompat.getColor(mContext, R.color.snackbar_text));
            textView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 15);
            Typeface typeface = ResourcesCompat.getFont(mContext, R.font.outfit_medium);
            textView.setTypeface(typeface);

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



    private void enablePullToRefresh() {
        isPullToRefreshAllowed = true;
        swipeRefreshLayout.setEnabled(true);
    }

    private void disablePullToRefresh() {
        isPullToRefreshAllowed = false;
        swipeRefreshLayout.setEnabled(false);
    }

    private void stopRefreshLoader() {
        swipeRefreshLayout.setRefreshing(false);
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
                webview.getSettings().setGeolocationEnabled(true);
                webview.reload();
            } else {



            }
        }

    }

    public class UpdateWidget1Interface {
        private final Context mContext;

        public UpdateWidget1Interface(Context context) {
            this.mContext = context;
        }

        @JavascriptInterface
        public void UpdateWidget1(final String condition, final String locationWeather, final String mainTemp, final String iconData, final String highLow, final String hour_0_temp, final String hour_0_icon, final String hour_0_time, final String hour_1_temp, final String hour_1_icon, final String hour_1_time, final String hour_2_temp, final String hour_2_icon, final String hour_2_time, final String hour_3_temp, final String hour_3_icon, final String hour_3_time) {
            WeatherWidgetProvider.updateWeatherWidget(mContext, condition, locationWeather, mainTemp, iconData, highLow, hour_0_temp, hour_0_icon, hour_0_time, hour_1_temp, hour_1_icon, hour_1_time, hour_2_temp, hour_2_icon, hour_2_time, hour_3_temp, hour_3_icon, hour_3_time);

            WidgetProviderRound.updateWeatherWidgetRound(mContext, mainTemp, iconData);

            WidgetProviderPill.updateWeatherWidgetPill(mContext, mainTemp, iconData);

            WidgetProviderSquare.updateWeatherWidgetSquare(mContext, condition, locationWeather, mainTemp, iconData, highLow, hour_0_temp, hour_0_icon, hour_0_time, hour_1_temp, hour_1_icon, hour_1_time, hour_2_temp, hour_2_icon, hour_2_time, hour_3_temp, hour_3_icon, hour_3_time);

            WidgetProviderSquareV2.updateWeatherWidgetSquareV2(mContext, condition, locationWeather, mainTemp, iconData, highLow, hour_0_temp, hour_0_icon, hour_0_time, hour_1_temp, hour_1_icon, hour_1_time, hour_2_temp, hour_2_icon, hour_2_time, hour_3_temp, hour_3_icon, hour_3_time);

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
                            statusBarColor = 0xFF081938;
                            navigationBarColor = 0xFF081938;
                            systemUiVisibilityFlags = 0;
                        webview.setBackgroundColor(getResources().getColor(R.color.C02d));
                        setTheme(R.style.blue_caret);

                    } else if(color.equals("overcast")){
                        statusBarColor = 0xFF0c1822;
                        navigationBarColor = 0xFF0c1822;
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
                        statusBarColor = 0xFF16161d;
                        navigationBarColor = 0xFF16161d;
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
                    } else if (color.equals("openSunCondition")){
                        openSunCondition();
                        return;
                    } else if (color.equals("openHumidityCondition")){
                        openHumidityCondition();
                        return;
                    } else if (color.equals("openPressureCondition")){
                        openPressureCondition();
                        return;
                    } else if (color.equals("openVisibilityCondition")){
                        openVisibilityCondition();
                        return;
                    } else if (color.equals("openWindCondition")){
                        openWindCondition();
                        return;
                    } else if (color.equals("openUVcondition")){
                        openUVcondition();
                        return;
                    } else if (color.equals("openMoonCondition")){
                        openMoonCondition();
                        return;
                    } else if (color.equals("AddLocationPage")){
                        OpenSearchPage();
                        return;
                    } else if (color.equals("OpenClothingPage")){
                        OpenClothingPage();
                        return;
                    } else if (color.equals("OpenGithubReleaseLatest")){
                        openGithubReleases();
                        return;
                    } else if (color.equals("GoBack")) {
                        back();
                        return;
                    } else if  (color.equals("ReqLocation")) {
                        requestLocationPermissions();
                        return;
                    } else if  (color.equals("DisableSwipeRefresh")) {
                        disablePullToRefresh();
                        return;
                    } else if  (color.equals("EnableSwipeRefresh")) {
                        enablePullToRefresh();
                        return;
                    } else if  (color.equals("StopRefreshingLoader")) {
                        stopRefreshLoader();
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

//        -----------------------------
        public void openSunCondition() {
            Intent intent = new Intent(mActivity, sunCondition.class);
            mActivity.startActivity(intent);
        }

        public void openHumidityCondition() {
            Intent intent = new Intent(mActivity, humidityCondition.class);
            mActivity.startActivity(intent);
        }

        public void openPressureCondition() {
            Intent intent = new Intent(mActivity, pressureCondition.class);
            mActivity.startActivity(intent);
        }

        public void openVisibilityCondition() {
            Intent intent = new Intent(mActivity, visibilityCondition.class);
            mActivity.startActivity(intent);
        }

        public void openWindCondition() {
            Intent intent = new Intent(mActivity, WindCondition.class);
            mActivity.startActivity(intent);
        }

        public void openUVcondition() {
            Intent intent = new Intent(mActivity, UVIndexCondition.class);
            mActivity.startActivity(intent);
        }

        public void OpenSearchPage() {
            Intent intent = new Intent(mActivity, SearchPage.class);
            mActivity.startActivity(intent);
        }
        public void OpenClothingPage() {
            Intent intent = new Intent(mActivity, ClothingRecommendation.class);
            mActivity.startActivity(intent);
        }

        public void openMoonCondition() {
            Intent intent = new Intent(mActivity, MoonPhases.class);
            mActivity.startActivity(intent);
        }
    }

    private void openGithubReleases() {
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(FIXED_URL));
        if (intent.resolveActivity(getPackageManager()) != null) {
            startActivity(intent);
        } else {
            Toast.makeText(this, "No application available to open the link.", Toast.LENGTH_SHORT).show();
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


