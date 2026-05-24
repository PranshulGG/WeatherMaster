package com.pranshulgg.weather_master_app.widgets.froggy.ui

import android.annotation.SuppressLint
import android.content.Context
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.glance.GlanceModifier
import androidx.glance.GlanceTheme
import androidx.glance.Image
import androidx.glance.ImageProvider
import androidx.glance.background
import androidx.glance.layout.Alignment
import androidx.glance.layout.Column
import androidx.glance.layout.Row
import androidx.glance.layout.Spacer
import androidx.glance.layout.fillMaxHeight
import androidx.glance.layout.fillMaxSize
import androidx.glance.layout.fillMaxWidth
import androidx.glance.layout.height
import androidx.glance.layout.padding
import androidx.glance.layout.size
import androidx.glance.layout.width
import androidx.glance.text.FontWeight
import androidx.glance.text.Text
import androidx.glance.text.TextStyle
import com.pranshulgg.weather_master_app.R
import com.pranshulgg.weather_master_app.widgets.model.WidgetWeather


@SuppressLint("StringFormatInvalid")
@Composable
fun FroggyWidgetContent(
    state: WidgetWeather?,
    context: Context
) {
    Column(
        modifier = GlanceModifier.background(GlanceTheme.colors.widgetBackground).fillMaxSize()
            .padding(top = 16.dp, bottom = 0.dp)
    ) {

        if (state != null) {

            Row(GlanceModifier.padding(start = 16.dp, end = 16.dp).height(65.dp).fillMaxWidth()) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = GlanceModifier.fillMaxHeight()
                ) {
                    Image(
                        provider = ImageProvider(state.currentIcon),
                        contentDescription = null,
                        modifier = GlanceModifier.size(48.dp)
                    )
                    Spacer(GlanceModifier.width(16.dp))
                    Column(
                        horizontalAlignment = Alignment.Start,
                        modifier = GlanceModifier,
                    ) {
                        Text(
                            state.currentTemp,
                            style = TextStyle(
                                fontSize = 36.sp,
                                color = GlanceTheme.colors.primary,
                                fontWeight = FontWeight.Bold,
                            )
                        )
                        Spacer(GlanceModifier.defaultWeight())
                        Text(
                            "${context.getString(R.string.weather_uv_index)} ${state.uvIndex}",
                            style = TextStyle(
                                color = GlanceTheme.colors.onSurfaceVariant,
                                fontWeight = FontWeight.Medium,
                                fontSize = 16.sp
                            )
                        )
                    }
                }
                Spacer(GlanceModifier.defaultWeight())
                Column(modifier = GlanceModifier.fillMaxHeight()) {
                    Text(
                        state.currentCondition,
                        style = TextStyle(
                            fontSize = 22.sp,
                            color = GlanceTheme.colors.onSurface,
                        )
                    )
                    Spacer(GlanceModifier.defaultWeight())
                    Text(
                        "${
                            context.getString(
                                R.string.temp_max,
                                state.daily[0].tempMax
                            )

                        } ${
                            context.getString(
                                R.string.temp_min,
                                state.daily[0].tempMin
                            )
                        }",
                        style = TextStyle(
                            color = GlanceTheme.colors.onSurfaceVariant,
                            fontSize = 16.sp
                        )
                    )
                }
            }
            Spacer(GlanceModifier.defaultWeight())
            Image(
                provider = ImageProvider(state.currentFrog),
                contentDescription = null,
                GlanceModifier.padding(top = 18.dp)
            )
        }
    }
}

