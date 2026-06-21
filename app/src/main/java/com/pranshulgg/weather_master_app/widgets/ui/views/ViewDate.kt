package com.pranshulgg.weather_master_app.widgets.ui.views

import android.content.Context
import android.util.TypedValue
import android.widget.RemoteViews
import androidx.compose.runtime.Composable
import androidx.core.content.ContextCompat
import androidx.glance.LocalContext
import androidx.glance.appwidget.AndroidRemoteViews
import com.pranshulgg.weather_master_app.R

private fun createDate(
    context: Context,
    format: String,
    color: Int
): RemoteViews {

    return RemoteViews(
        context.packageName,
        R.layout.sys_date
    ).apply {
        setCharSequence(
            R.id.date,
            "setFormat12Hour",
            format
        )

        setCharSequence(
            R.id.date,
            "setFormat24Hour",
            format
        )
        val resolvedColor = ContextCompat.getColor(context, color)
        setTextColor(R.id.date, resolvedColor)

    }
}

@Composable
fun WidgetDate(
    format: String,
    context: Context,
    color: Int = R.color.white
) {
    AndroidRemoteViews(
        remoteViews =
            createDate(context, format, color)
    )
}