package com.example.weathermaster;


import android.animation.ArgbEvaluator;
import android.animation.ObjectAnimator;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.TypedValue;
import android.view.ContextThemeWrapper;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.webkit.JavascriptInterface;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;


import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.content.res.ResourcesCompat;

import com.google.android.material.snackbar.Snackbar;

public class WeatherModels extends AppCompatActivity {

    private WebView webview;
    private boolean isFirstLoad = true;
    public void onBackPressed() {
        super.onBackPressed();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        SharedPreferences prefs = getSharedPreferences("theme_prefs", MODE_PRIVATE);
        boolean isDarkMode = prefs.getBoolean("theme_mode", false);
        setAppTheme(this, isDarkMode);
        super.onCreate(savedInstanceState);




        setContentView(R.layout.activity_main);



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
        webview.addJavascriptInterface(new ShowSnackInterface(this), "ShowSnackMessage");
        webview.addJavascriptInterface(new BackActivityInterface(this), "BackActivityInterface");
        webview.addJavascriptInterface(new NavigateActivityInterface(this), "OpenActivityInterface");
        webSettings.setTextZoom(100);
        webSettings.setAllowFileAccess(true);
        webview.loadUrl("file:///android_asset/pages/change_om_model.html");

        webview.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);


                if (isFirstLoad) {
                    isFirstLoad = false;

                    int primaryColor = new ContextThemeWrapper(WeatherModels.this, R.style.Base_Theme_WeatherMaster)
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



            TextView textView = snackbarView.findViewById(com.google.android.material.R.id.snackbar_text);
            textView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 15);
            Typeface typeface = ResourcesCompat.getFont(mContext, R.font.outfit_medium);
            textView.setTypeface(typeface);


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

    public class NavigateActivityInterface {
        private final Context mContext;

        public NavigateActivityInterface(Context context) {
            this.mContext = context;
        }

        @JavascriptInterface
        public void OpenActivity(final String activityName) {
            Intent intent = null;

            switch (activityName) {
                case "--":
                    break;
                default:
                    Toast.makeText(mContext, "Activity not found", Toast.LENGTH_SHORT).show();
                    return;
            }

            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            mContext.startActivity(intent);

        }
    }

    public class BackActivityInterface {
        private final Activity gActivity;

        public BackActivityInterface(Activity activity) {
            this.gActivity = activity;
        }

        @JavascriptInterface
        public void CloseActivity() {
            gActivity.runOnUiThread(() -> gActivity.onBackPressed());
        }
    }
    public void goBack() {
        runOnUiThread(this::onBackPressed);
    }

    public class AndroidInterface {
        private WeatherModels mActivity;

        AndroidInterface(WeatherModels activity) {
            mActivity = activity;
        }

        @JavascriptInterface
        public void updateStatusBarColor(final String colorStatus, final String colorNav, final String UiFlag, final String FlagAnim) {
            mActivity.runOnUiThread(new Runnable() {
                @SuppressLint("ResourceType")
                @RequiresApi(api = Build.VERSION_CODES.O)
                @Override
                public void run() {
                    int statusBarColor;
                    int navigationBarColor;
                    int systemUiVisibilityFlags = View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR | View.SYSTEM_UI_FLAG_LIGHT_NAVIGATION_BAR;

                    if (colorStatus != null && !colorStatus.isEmpty()) {
                        statusBarColor = Color.parseColor(colorStatus);
                        navigationBarColor = Color.parseColor(colorNav);
                        if ("1".equals(UiFlag)) {
                            systemUiVisibilityFlags = View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR | View.SYSTEM_UI_FLAG_LIGHT_NAVIGATION_BAR;
                            setAppTheme(mActivity, false);
                        } else {
                            systemUiVisibilityFlags = 0;
                            setAppTheme(mActivity, true);
                        }
                    } else {
                        Toast.makeText(mActivity, "not found", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    int currentStatusBarColor = mActivity.getWindow().getStatusBarColor();
                    int currentNavigationBarColor = mActivity.getWindow().getNavigationBarColor();

                    int animationDuration = 0;
                    try {
                        animationDuration = Integer.parseInt(FlagAnim);
                    } catch (NumberFormatException e) {
                        animationDuration = 0;
                    }


                    ObjectAnimator statusBarAnimator = ObjectAnimator.ofObject(
                            mActivity.getWindow(),
                            "statusBarColor",
                            new ArgbEvaluator(),
                            currentStatusBarColor,
                            statusBarColor
                    );

                    statusBarAnimator.setDuration(animationDuration);
                    statusBarAnimator.start();

                    ObjectAnimator navBarAnimator = ObjectAnimator.ofObject(
                            mActivity.getWindow(),
                            "navigationBarColor",
                            new ArgbEvaluator(),
                            currentNavigationBarColor,
                            navigationBarColor
                    );

                    navBarAnimator.setDuration(animationDuration);
                    navBarAnimator.start();

                    mActivity.getWindow().setNavigationBarColor(navigationBarColor);

                    View decorView = mActivity.getWindow().getDecorView();
                    decorView.setSystemUiVisibility(systemUiVisibilityFlags);

                }
            });
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
                    url.startsWith("mailto:pranshul.devmain@gmail.com")||
                    url.startsWith("https://github.com/PranshulGG/WeatherMaster/releases");


        }


    }
    public void setAppTheme(Context context, boolean isDarkMode) {
        // Save theme preference
        SharedPreferences prefs = context.getSharedPreferences("theme_prefs", Context.MODE_PRIVATE);
        prefs.edit().putBoolean("theme_mode", isDarkMode).apply();

        // Apply the theme
        if (isDarkMode) {
            context.setTheme(R.style.ThemeMainBlackDark);
        } else {
            context.setTheme(R.style.ThemeMainBlackLight);
        }
    }
}