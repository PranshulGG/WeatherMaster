package com.pranshulgg.weather_master_app.widgets.ui.views

import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.res.Configuration
import android.graphics.Color
import android.provider.CalendarContract
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
    size: Float,
    hideShadow: Boolean
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


    val layoutId = if (hideShadow) R.layout.sys_date_no_shadow else R.layout.sys_date
    val viewId = if (hideShadow) R.id.date_no_shadow else R.id.date

    return RemoteViews(
        context.packageName,
        layoutId
    ).apply {
        setCharSequence(
            viewId,
            "setFormat12Hour",
            format
        )

        setCharSequence(
            viewId,
            "setFormat24Hour",
            format
        )
        setTextColor(viewId, resolvedColor)

        setTextViewTextSize(viewId, TypedValue.COMPLEX_UNIT_SP, size)

        val calendarIntent = Intent(
            Intent.ACTION_VIEW,
            CalendarContract.CONTENT_URI
        ).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK
        }

        val calendarPendingIntent = PendingIntent.getActivity(
            context,
            101,
            calendarIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        setOnClickPendingIntent(viewId, calendarPendingIntent)
    }
}

@Composable
fun WidgetDate(
    format: String,
    context: Context,
    color: Int?,
    size: Float,
    hideShadow: Boolean = false
) {
    AndroidRemoteViews(
        remoteViews =
            createDate(context, format, color, size, hideShadow)
    )
}