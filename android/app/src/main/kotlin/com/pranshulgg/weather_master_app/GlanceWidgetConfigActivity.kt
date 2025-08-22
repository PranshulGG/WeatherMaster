package com.pranshulgg.weather_master_app

import android.app.Activity
import android.app.WallpaperManager
import android.appwidget.AppWidgetManager
import android.content.Intent
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.widget.Button
import android.widget.CheckBox
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextClock
import android.widget.TextView
import com.google.android.material.slider.Slider

class GlanceWidgetConfigActivity : Activity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.widget_config_activity)


        val previewLayout = findViewById<LinearLayout>(R.id.widget_preview)



        val previewClock = findViewById<TextClock>(R.id.preview_text_clock)
        val previewDate = findViewById<TextView>(R.id.preview_date)
        val previewTemp = findViewById<TextView>(R.id.preview_temp)
        val previewCondition = findViewById<TextView>(R.id.preview_condition)
        val previewIcon = findViewById<ImageView>(R.id.preview_weather_icon)

        val showClockCheck = findViewById<CheckBox>(R.id.showClockCheck)
        val sizeSlider = findViewById<Slider>(R.id.clockSizeSeek)
        val sizeText = findViewById<TextView>(R.id.clockSizeText)
        val saveButton = findViewById<Button>(R.id.saveWidgetBtn)


        fun updatePreview() {
            previewClock.textSize = sizeSlider.value
            previewClock.visibility = if (showClockCheck.isChecked) TextView.VISIBLE else TextView.GONE
            previewTemp.text = "29° • "
            previewCondition.text = getString(R.string.clear_sky)
            previewDate.text = java.text.SimpleDateFormat("EEE, MMM d").format(System.currentTimeMillis())
        }

        sizeSlider.addOnChangeListener { _, _, _ -> updatePreview() }
        showClockCheck.setOnCheckedChangeListener { _, _ -> updatePreview() }

        updatePreview()



        val appWidgetId = intent.extras?.getInt(AppWidgetManager.EXTRA_APPWIDGET_ID, AppWidgetManager.INVALID_APPWIDGET_ID)
        if (appWidgetId == null || appWidgetId == AppWidgetManager.INVALID_APPWIDGET_ID) {
            finish()
            return
        }

        saveButton.setOnClickListener {
            val prefs = getSharedPreferences("HomeWidgetPreferences", MODE_PRIVATE)
            prefs.edit().apply {
                putBoolean("showClock", showClockCheck.isChecked)
                putFloat("clockSize", sizeSlider.value)
                apply()
            }


            val manager = AppWidgetManager.getInstance(this)
            GlanceWidgetProvider().onUpdate(this, manager, intArrayOf(appWidgetId))

            val result = Intent().apply { putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId) }
            setResult(RESULT_OK, result)
            finish()
        }
    }


}
