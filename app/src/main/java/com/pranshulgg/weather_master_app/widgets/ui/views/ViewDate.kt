package com.pranshulgg.weather_master_app.widgets.ui.views

import android.content.Context
import android.content.res.Configuration
import android.graphics.Color
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
    color: Int?,
    size: Float
): RemoteViews {
    val color = color?.let { ContextCompat.getColor(context, it) }
    val resolvedColor = color ?: if (
        (context.resources.configuration.uiMode and
                Configuration.UI_MODE_NIGHT_MASK)
        == Configuration.UI_MODE_NIGHT_YES
    ) {
        Color.WHITE
    } else {
        Color.BLACK
    }
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
        setTextColor(R.id.date, resolvedColor)

        setTextViewTextSize(R.id.date, TypedValue.COMPLEX_UNIT_SP, size)

    }
}

@Composable
fun WidgetDate(
    format: String,
    context: Context,
    color: Int?,
    size: Float
) {
    AndroidRemoteViews(
        remoteViews =
            createDate(context, format, color, size)
    )
}