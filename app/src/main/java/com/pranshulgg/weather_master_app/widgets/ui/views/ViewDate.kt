package com.pranshulgg.weather_master_app.widgets.ui.views

import android.content.Context
import android.util.TypedValue
import android.widget.RemoteViews
import androidx.compose.runtime.Composable
import androidx.glance.LocalContext
import androidx.glance.appwidget.AndroidRemoteViews
import com.pranshulgg.weather_master_app.R

private fun createDate(
    context: Context,
    format: String
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
    }
}

@Composable
fun WidgetDate(
    format: String,
    context: Context
) {
    AndroidRemoteViews(
        remoteViews =
            createDate(context, format)
    )
}