package com.pranshulgg.weather_master_app.widgets.ui.views

import android.content.Context
import android.util.TypedValue
import android.widget.RemoteViews
import androidx.compose.runtime.Composable
import androidx.core.content.ContextCompat
import androidx.glance.appwidget.AndroidRemoteViews
import com.pranshulgg.weather_master_app.R


private fun createClock(
    context: Context,
    size: Float,
    color: Int
): RemoteViews {

    val layoutId = when (size) {
        20f -> R.layout.sys_clock_20
        28f -> R.layout.sys_clock_28
        36f -> R.layout.sys_clock_36
        44f -> R.layout.sys_clock_44
        56f -> R.layout.sys_clock_56
        72f -> R.layout.sys_clock_72
        88f -> R.layout.sys_clock_88
        104f -> R.layout.sys_clock_104
        else -> R.layout.sys_clock_120
    }

    return RemoteViews(
        context.packageName,
        layoutId
    ).apply {
        val resolvedColor = ContextCompat.getColor(context, color)
        setTextColor(R.id.clock, resolvedColor)
    }
}

@Composable
fun WidgetClock(
    size: Float,
    context: Context,
    color: Int = R.color.white
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