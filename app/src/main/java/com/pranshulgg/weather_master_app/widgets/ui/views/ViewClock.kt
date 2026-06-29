package com.pranshulgg.weather_master_app.widgets.ui.views

import android.content.Context
import android.content.res.Configuration
import android.graphics.Color
import android.util.TypedValue
import android.widget.RemoteViews
import androidx.compose.runtime.Composable
import androidx.core.content.ContextCompat
import androidx.glance.appwidget.AndroidRemoteViews
import com.pranshulgg.weather_master_app.R


private fun createClock(
    context: Context,
    size: Float,
    color: Int?
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
        R.layout.sys_clock_36
    ).apply {
        setTextColor(R.id.clock, resolvedColor)

        setTextViewTextSize(R.id.clock, TypedValue.COMPLEX_UNIT_SP, size)
    }
}

@Composable
fun WidgetClock(
    size: Float,
    context: Context,
    color: Int?
) {

    AndroidRemoteViews(

        remoteViews =
            createClock(
                context,
                size,
                color
            )
    )
}