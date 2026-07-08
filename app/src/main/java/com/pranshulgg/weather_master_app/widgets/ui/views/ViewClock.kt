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
    color: Int?,
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

    val layoutId = if (hideShadow) R.layout.sys_clock_no_shadow else R.layout.sys_clock
    val viewId = if (hideShadow) R.id.clock_no_shadow else R.id.clock

    return RemoteViews(
        context.packageName,
        layoutId
    ).apply {
        setTextColor(viewId, resolvedColor)

        setTextViewTextSize(viewId, TypedValue.COMPLEX_UNIT_SP, size)


    }
}

@Composable
fun WidgetClock(
    size: Float,
    context: Context,
    color: Int?,
    hideShadow: Boolean = false
) {

    AndroidRemoteViews(

        remoteViews =
            createClock(
                context,
                size,
                color,
                hideShadow
            )
    )
}