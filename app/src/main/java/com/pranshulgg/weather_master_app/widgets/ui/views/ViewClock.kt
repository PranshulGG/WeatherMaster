package com.pranshulgg.weather_master_app.widgets.ui.views

import android.content.Context
import android.util.TypedValue
import android.widget.RemoteViews
import androidx.compose.runtime.Composable
import androidx.glance.LocalContext
import androidx.glance.appwidget.AndroidRemoteViews
import com.pranshulgg.weather_master_app.R


private fun createClock(
    context: Context,
    sizeSp: Float
): RemoteViews {

    return RemoteViews(
        context.packageName,
        R.layout.sys_clock
    ).apply {

        setTextViewTextSize(
            R.id.clock,
            TypedValue.COMPLEX_UNIT_SP,
            sizeSp
        )
    }
}

@Composable
fun WidgetClock(
    size: Float,
    context: Context
) {

    AndroidRemoteViews(

        remoteViews =
            createClock(
                context,
                size
            )
    )
}