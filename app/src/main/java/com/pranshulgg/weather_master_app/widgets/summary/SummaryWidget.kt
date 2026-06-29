package com.pranshulgg.weather_master_app.widgets.summary

import android.content.Context
import android.os.Build
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.glance.ColorFilter
import androidx.glance.GlanceId
import androidx.glance.GlanceModifier
import androidx.glance.GlanceTheme
import androidx.glance.Image
import androidx.glance.ImageProvider
import androidx.glance.action.actionStartActivity
import androidx.glance.action.clickable
import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.appwidget.cornerRadius
import androidx.glance.appwidget.provideContent
import androidx.glance.background
import androidx.glance.currentState
import androidx.glance.layout.Box
import androidx.glance.layout.Column
import androidx.glance.layout.Row
import androidx.glance.layout.Spacer
import androidx.glance.layout.fillMaxSize
import androidx.glance.layout.height
import androidx.glance.layout.padding
import androidx.glance.layout.width
import androidx.glance.text.FontWeight
import androidx.glance.text.Text
import androidx.glance.text.TextStyle
import com.pranshulgg.weather_master_app.MainActivity
import com.pranshulgg.weather_master_app.R
import com.pranshulgg.weather_master_app.widgets.WeatherWidgetStateDefinition
import com.pranshulgg.weather_master_app.widgets.WeatherWidgetStateJson
import com.pranshulgg.weather_master_app.widgets.model.WidgetWeather
import com.pranshulgg.weather_master_app.widgets.params.getWidgetParams
import com.pranshulgg.weather_master_app.widgets.ui.ReloadButton
import kotlinx.serialization.json.Json


class SummaryWidget : GlanceAppWidget() {
    override val stateDefinition =
        WeatherWidgetStateDefinition

    override suspend fun provideGlance(
        context: Context,
        id: GlanceId
    ) {
        provideContent {
            val widgetState =
                currentState<WeatherWidgetStateJson>()

            val json = widgetState.json
            val state = json?.let {
                Json.decodeFromString<WidgetWeather>(it)
            }

            Box(
                GlanceModifier.fillMaxSize().appWidgetBackgroundShape()
                    .clickable(actionStartActivity<MainActivity>())
            ) {
                if (state != null) {
                    Column(GlanceModifier.padding(16.dp).fillMaxSize()) {
                        Row {
                            Image(
                                provider = ImageProvider(R.drawable.article_24px),
                                contentDescription = null,
                                colorFilter = ColorFilter.tint(GlanceTheme.colors.secondary)
                            )
                            Spacer(GlanceModifier.width(5.dp))
                            Text(
                                context.getString(R.string.setting_day_summary),
                                style = TextStyle(
                                    fontWeight = FontWeight.Bold,
                                    color = GlanceTheme.colors.secondary,
                                    fontSize = 16.sp
                                )
                            )
                        }
                        Spacer(GlanceModifier.height(5.dp))
                        Text(
                            state.summary,
                            style = TextStyle(
                                fontWeight = FontWeight.Medium,
                                color = GlanceTheme.colors.onSurface,
                                fontSize = 16.sp
                            ),

                            )
                    }
                } else {
                    ReloadButton()
                }
            }
        }
    }
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

