package com.example.weathermaster;

import android.Manifest;
import android.animation.ArgbEvaluator;
import android.animation.ObjectAnimator;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.provider.Settings;
import android.util.TypedValue;
import android.view.ContextThemeWrapper;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowInsets;
import android.view.WindowManager;
import android.webkit.JavascriptInterface;
import android.webkit.ValueCallback;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;


import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.res.ResourcesCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.material.snackbar.Snackbar;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;

public class SettingsActivity extends AppCompatActivity {

    private WebView webview;

    private ValueCallback<Uri[]> filePathCallback;
    private final static int FILECHOOSER_RESULTCODE = 1;
    private final static int EXPORT_REQUEST_CODE = 2;
    private static final int PERMISSION_REQUEST_CODE_SETTINGS = 3;
    private static final int SAVE_DOCUMENT_REQUEST_CODE = 2;
    private static final int IMPORT_REQUEST_CODE = 3;

    private String dataToSave;
    private boolean isFirstLoad = true;

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
        webview.addJavascriptInterface(new WebAppInterface(), "Android");
        webview.setBackgroundColor(getResources().getColor(R.color.yellowBg));

        webview.loadUrl("file:///android_asset/pages/settings.html");
        webSettings.setTextZoom(100);
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

                    int primaryColor = new ContextThemeWrapper(SettingsActivity.this, R.style.Base_Theme_WeatherMaster)
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


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == FILECHOOSER_RESULTCODE) {
            if (filePathCallback != null) {
                Uri[] results = null;
                if (resultCode == RESULT_OK && data != null) {
                    String dataString = data.getDataString();
                    if (dataString != null) {
                        results = new Uri[]{Uri.parse(dataString)};
                    }
                }
                filePathCallback.onReceiveValue(results);
                filePathCallback = null;
            }
        } else if (requestCode == SAVE_DOCUMENT_REQUEST_CODE) {
            if (resultCode == RESULT_OK && data != null && data.getData() != null) {
                saveToUri(data.getData());
            } else {
                Toast.makeText(this, "Error exporting", Toast.LENGTH_SHORT).show();
            }
        } else if (requestCode == IMPORT_REQUEST_CODE) {
            if (resultCode == RESULT_OK && data != null && data.getData() != null) {
                importFromUri(data.getData());
            } else {
                Toast.makeText(this, "Error importing file", Toast.LENGTH_SHORT).show();
            }
        }
    }


    private void restartApp() {
        Toast.makeText(this, "App is restarting...", Toast.LENGTH_SHORT).show();

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
                finish();
            }
        }, 500);
    }


    public class WebAppInterface {
        @JavascriptInterface
        public void saveFile(String data) {
            dataToSave = data;
            openFilePickerExport();
        }

        @JavascriptInterface
        public void importFile() {
            openFilePickerImport();  // Open file picker for importing JSON data
        }

        @JavascriptInterface
        public void reloadTheApp() {
            restartApp();  // Open file picker for importing JSON data
        }
    }


    private void openFilePickerExport() {
        String currentDate = new SimpleDateFormat("yyyy-MM-dd").format(new Date());
        String fileName = "WeatherMasterData_" + currentDate + ".json";
        Intent intent = new Intent(Intent.ACTION_CREATE_DOCUMENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.setType("application/json");
        intent.putExtra(Intent.EXTRA_TITLE, fileName);
        startActivityForResult(intent, SAVE_DOCUMENT_REQUEST_CODE);
    }

    private void openFilePickerImport() {
        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.setType("application/json");
        startActivityForResult(intent, IMPORT_REQUEST_CODE);
    }

    private void importFromUri(Uri uri) {
        try {
            InputStream inputStream = getContentResolver().openInputStream(uri);
            if (inputStream != null) {
                BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
                StringBuilder stringBuilder = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) {
                    stringBuilder.append(line);
                }
                reader.close();
                inputStream.close();

                String importedData = stringBuilder.toString();

                String escapedData = importedData.replace("'", "\\'").replace("\"", "\\\"");

                runOnUiThread(() -> {
                    String jsCode = "handleImportedData('" + escapedData + "');";
                    webview.evaluateJavascript(jsCode, null);
                });
            }
        } catch (Exception e) {
            Toast.makeText(this, "Error reading file", Toast.LENGTH_LONG).show();
            e.printStackTrace();
        }
    }


    private void saveToUri(Uri uri) {
        try {
            OutputStream outputStream = getContentResolver().openOutputStream(uri);
            if (outputStream != null) {
                outputStream.write(dataToSave.getBytes());
                outputStream.close();
                Toast.makeText(this, "Backup saved", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Error", Toast.LENGTH_SHORT).show();
            }
        } catch (Exception e) {
            Toast.makeText(this, "Error saving file", Toast.LENGTH_LONG).show();
            e.printStackTrace();
        }
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
            } else if ("short".equals(time)) {
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
            } else if (time.equals("short")) {
                duration = Toast.LENGTH_SHORT;
            }
            Toast.makeText(mContext, text, duration).show();
        }
    }

//    export or import data







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


                    if (color.equals("Scrolled")){
                        statusBarColor = 0xFF1d2024;
                        navigationBarColor = 0xFF111318;

                    } else if (color.equals("ScrollFalse")) {
                        statusBarColor = 0xFF111318;
                        navigationBarColor = 0xFF111318;

                    } else if (color.equals("DialogNotScrolled")) {
                        statusBarColor = 0xFF07080a;
                        navigationBarColor = 0xFF07080a;

                    } else if (color.equals("DialogScrolled")) {
                        statusBarColor = 0xFF0c0d0e;
                        navigationBarColor = 0xFF07080a;
                        systemUiVisibilityFlags = 0;

                    } else if(color.equals("orange_material_Scrolled")){
                        statusBarColor = 0xFF251e17;
                        navigationBarColor = 0xFF18120c;
                        systemUiVisibilityFlags = 0;

                    } else if (color.equals("orange_material_ScrollFalse")) {
                        statusBarColor = 0xFF18120c;
                        navigationBarColor = 0xFF18120c;
                        systemUiVisibilityFlags = 0;

                    } else if(color.equals("red_material_Scrolled")){
                        statusBarColor = 0xFF271d1b;
                        navigationBarColor = 0xFF1a110f;
                        systemUiVisibilityFlags = 0;

                    } else if (color.equals("red_material_ScrollFalse")) {
                        statusBarColor = 0xFF1a110f;
                        navigationBarColor = 0xFF1a110f;
                        systemUiVisibilityFlags = 0;

                    } else if(color.equals("pink_material_Scrolled")){
                        statusBarColor = 0xFF261d1f;
                        navigationBarColor = 0xFF191113;
                        systemUiVisibilityFlags = 0;

                    } else if (color.equals("pink_material_ScrollFalse")) {
                        statusBarColor = 0xFF191113;
                        navigationBarColor = 0xFF191113;
                        systemUiVisibilityFlags = 0;

                    } else if(color.equals("purple_material_Scrolled")){
                        statusBarColor = 0xFF241e22;
                        navigationBarColor = 0xFF171216;
                        systemUiVisibilityFlags = 0;

                    } else if (color.equals("purple_material_ScrollFalse")) {
                        statusBarColor = 0xFF171216;
                        navigationBarColor = 0xFF171216;
                        systemUiVisibilityFlags = 0;

                    } else if(color.equals("blue_material_Scrolled")){
                        statusBarColor = 0xFF1d2024;
                        navigationBarColor = 0xFF111318;
                        systemUiVisibilityFlags = 0;

                    } else if (color.equals("blue_material_ScrollFalse")) {
                        statusBarColor = 0xFF111318;
                        navigationBarColor = 0xFF111318;
                        systemUiVisibilityFlags = 0;

                    } else if(color.equals("yellow_material_Scrolled")){
                        statusBarColor = 0xFF222017;
                        navigationBarColor = 0xFF15130b;
                        systemUiVisibilityFlags = 0;

                    } else if (color.equals("yellow_material_ScrollFalse")) {
                        statusBarColor = 0xFF15130b;
                        navigationBarColor = 0xFF15130b;
                        systemUiVisibilityFlags = 0;

                    } else if(color.equals("green_material_Scrolled")){
                        statusBarColor = 0xFF1e201a;
                        navigationBarColor = 0xFF12140e;
                        systemUiVisibilityFlags = 0;

                    } else if (color.equals("green_material_ScrollFalse")) {
                        statusBarColor = 0xFF12140e;
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

//                        Light colors 😭 EWWW

                    } else if (color.equals("amoled_theme")) {
                        statusBarColor = 0xFF000000;
                        navigationBarColor = 0xFF000000;
                        systemUiVisibilityFlags = 0;
                    } else  if (color.equals("ReloadDynamicColors")){
                        isFirstLoad = true;
                        webview.reload();
                        return;
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
                    } else if (color.equals("openWeatherModels")){
                        openWeatherModels();
                        return;
                    } else if (color.equals("EditAppLayoutPage")){
                        EditAppLayoutPage();
                        return;
                    } else if (color.equals("AppUnitsActivity")){
                        AppUnitsActivity();
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

        public void openWeatherModels() {
            Intent intent = new Intent(sActivity, WeatherModels.class);
            sActivity.startActivity(intent);
        }

        public void EditAppLayoutPage() {
            Intent intent = new Intent(sActivity, ArrangeItems.class);
            sActivity.startActivity(intent);
        }
        public void AppUnitsActivity() {
            Intent intent = new Intent(sActivity, AppUnitsActivity.class);
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
                    url.startsWith("https://www.geonames.org/")||
                    url.startsWith("https://nominatim.org/") ||
                    url.startsWith("https://discord.gg/sSW2E4nqmn");


        }


    }
}