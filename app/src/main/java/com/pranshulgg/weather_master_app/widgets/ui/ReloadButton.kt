package com.pranshulgg.weather_master_app.widgets.ui

import android.content.Context
import android.os.Build
import androidx.compose.runtime.Composable
import androidx.glance.Button
import androidx.glance.GlanceId
import androidx.glance.GlanceModifier
import androidx.glance.GlanceTheme
import androidx.glance.ImageProvider
import androidx.glance.action.ActionParameters
import androidx.glance.appwidget.action.ActionCallback
import androidx.glance.appwidget.action.actionRunCallback
import androidx.glance.appwidget.cornerRadius
import androidx.glance.background
import androidx.glance.layout.Alignment
import androidx.glance.layout.Column
import androidx.glance.layout.fillMaxSize
import androidx.glance.text.Text
import com.pranshulgg.weather_master_app.R
import com.pranshulgg.weather_master_app.data.worker.widgets.WidgetReload
import dagger.hilt.EntryPoint
import dagger.hilt.InstallIn
import dagger.hilt.android.EntryPointAccessors
import dagger.hilt.components.SingletonComponent
import jakarta.inject.Inject


@Composable
fun ReloadButton() {
    Column(
        modifier = GlanceModifier.fillMaxSize().appWidgetBackgroundShape(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Button(text = "Reload", onClick = actionRunCallback<ReloadAction>())
    }
}

class ReloadAction : ActionCallback {
    @Inject

    override suspend fun onAction(
        context: Context,
        glanceId: GlanceId,
        parameters: ActionParameters
    ) {
        val widgetReload = EntryPointAccessors.fromApplication(
            context.applicationContext,
            WidgetReloadEntryPoint::class.java
        ).widgetReload()

        widgetReload.reload(context)
    }
}

@EntryPoint
@InstallIn(SingletonComponent::class)
interface WidgetReloadEntryPoint {
    fun widgetReload(): WidgetReload
}

@Composable
private fun GlanceModifier.appWidgetBackgroundShape(): GlanceModifier {
    return if (Build.VERSION.SDK_INT >= 31) {
        this
            .cornerRadius(android.R.dimen.system_app_widget_background_radius)
            .background(GlanceTheme.colors.widgetBackground)
    } else {
        this
            .background(ImageProvider(R.drawable.weather_widget_background))
    }
}

