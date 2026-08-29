package com.pranshulgg.weather_master_app.widgets.rain

import android.content.Context
import android.os.Build
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
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
import androidx.glance.appwidget.SizeMode
import androidx.glance.appwidget.cornerRadius
import androidx.glance.appwidget.provideContent
import androidx.glance.background
import androidx.glance.currentState
import androidx.glance.layout.Alignment
import androidx.glance.layout.Box
import androidx.glance.layout.Column
import androidx.glance.layout.Row
import androidx.glance.layout.Spacer
import androidx.glance.layout.fillMaxWidth
import androidx.glance.layout.height
import androidx.glance.layout.padding
import androidx.glance.layout.width
import androidx.glance.text.FontWeight
import androidx.glance.text.Text
import androidx.glance.text.TextAlign
import androidx.glance.text.TextStyle
import com.pranshulgg.weather_master_app.MainActivity
import com.pranshulgg.weather_master_app.R
import com.pranshulgg.weather_master_app.widgets.WeatherWidgetStateJson
import com.pranshulgg.weather_master_app.widgets.WeatherWidgetStateDefinition
import com.pranshulgg.weather_master_app.widgets.model.RainWidgetState
import com.pranshulgg.weather_master_app.widgets.ui.ReloadButton
import kotlinx.serialization.json.Json

/**
 * Homescreen widget showing the rain bar chart for the default / home
 * location: the high-resolution nowcast when it has coverage, otherwise the
 * hourly precipitation forecast (the in-app fallback).
 */
class RainWidget : GlanceAppWidget() {

    override val sizeMode = SizeMode.Exact
    override val stateDefinition = WeatherWidgetStateDefinition

    override suspend fun provideGlance(
        context: Context,
        id: GlanceId
    ) {
        provideContent {
            val appState = currentState<WeatherWidgetStateJson>()
            val rainState = appState.rainJson?.let {
                try {
                    Json.decodeFromString<RainWidgetState>(it)
                } catch (e: Exception) {
                    null
                }
            }

            Box(
                GlanceModifier
                    .fillMaxWidth()
                    .appWidgetBackgroundShape()
                    .clickable(actionStartActivity<MainActivity>())
            ) {
                if (rainState != null && rainState.bars.isNotEmpty()) {
                    RainWidgetContent(context, rainState)
                } else {
                    ReloadButton()
                }
            }
        }
    }
}

private const val MAX_BAR_HEIGHT_DP = 64

@Composable
private fun RainWidgetContent(context: Context, state: RainWidgetState) {
    Column(
        modifier = GlanceModifier.fillMaxWidth().padding(horizontal = 12.dp, vertical = 10.dp)
    ) {
        // Header: icon + title + unit, source label right-aligned
        Row(modifier = GlanceModifier.fillMaxWidth()) {
            Image(
                provider = ImageProvider(R.drawable.water_drop_24px),
                contentDescription = null,
                colorFilter = ColorFilter.tint(GlanceTheme.colors.secondary),
                modifier = GlanceModifier.width(16.dp).height(16.dp)
            )
            Spacer(GlanceModifier.width(5.dp))
            Text(
                text = "${context.getString(R.string.rain_nowcast_title)} (${state.unitLabel})",
                style = TextStyle(
                    fontWeight = FontWeight.Bold,
                    color = GlanceTheme.colors.secondary,
                    fontSize = 14.sp
                )
            )
            Spacer(modifier = GlanceModifier.defaultWeight())
            Text(
                text = state.sourceLabel,
                style = TextStyle(
                    color = GlanceTheme.colors.onSurfaceVariant,
                    fontSize = 11.sp
                )
            )
        }

        Spacer(GlanceModifier.height(8.dp))

        // Bar chart: equal-width bottom-aligned bars
        Row(
            modifier = GlanceModifier.fillMaxWidth().height(MAX_BAR_HEIGHT_DP.dp),
            verticalAlignment = Alignment.Bottom
        ) {
            val maxAmount = state.bars.maxOf { it.amount }.coerceAtLeast(0.1)
            state.bars.forEach { bar ->
                Box(
                    modifier = GlanceModifier.defaultWeight().height(MAX_BAR_HEIGHT_DP.dp),
                    contentAlignment = Alignment.BottomCenter
                ) {
                    val fraction = (bar.amount / maxAmount).coerceIn(0.05, 1.0)
                    Box(
                        modifier = GlanceModifier
                            .padding(horizontal = 1.dp)
                            .fillMaxWidth()
                            .height((fraction * MAX_BAR_HEIGHT_DP).dp)
                            .background(Color(bar.colorArgb))
                    ) {}
                }
            }
        }

        Spacer(GlanceModifier.height(3.dp))

        // Time labels under the bars
        Row(modifier = GlanceModifier.fillMaxWidth()) {
            state.bars.forEach { bar ->
                Text(
                    text = bar.time,
                    maxLines = 1,
                    style = TextStyle(
                        color = GlanceTheme.colors.onSurfaceVariant,
                        fontSize = 9.sp,
                        textAlign = TextAlign.Center
                    ),
                    modifier = GlanceModifier.defaultWeight().padding(horizontal = 1.dp)
                )
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