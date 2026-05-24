package com.pranshulgg.weather_master_app.widgets.current

import android.annotation.SuppressLint
import android.content.Context
import android.util.Log
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.glance.GlanceModifier
import androidx.glance.GlanceTheme
import androidx.glance.Image
import androidx.glance.ImageProvider
import androidx.glance.LocalContext
import androidx.glance.LocalSize
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


//@SuppressLint("StringFormatInvalid")
@SuppressLint("StringFormatInvalid")
@Composable
fun CurrentAndHourlyWidgetContent(
    state: WidgetWeather?,
    context: Context
) {

    Column(
        modifier = GlanceModifier.background(GlanceTheme.colors.widgetBackground).fillMaxSize()
            .padding(start = 16.dp, end = 16.dp, top = 16.dp, bottom = 16.dp)
    ) {
        if (state != null) {

            Row(
                modifier = GlanceModifier.fillMaxWidth().height(80.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = GlanceModifier.fillMaxHeight()) {
                    Text(
                        state.currentTemp,
                        style = TextStyle(
                            fontSize = 46.sp,
                            color = GlanceTheme.colors.primary,
                            fontWeight = FontWeight.Bold
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
                Spacer(
                    modifier = GlanceModifier.defaultWeight()
                )
                Column(
                    horizontalAlignment = Alignment.End,
                    modifier = GlanceModifier.fillMaxHeight()
                ) {
                    Image(
                        provider = ImageProvider(state.currentIcon),
                        contentDescription = null,
                        modifier = GlanceModifier.size(52.dp)
                    )
                    Spacer(GlanceModifier.defaultWeight())
                    Text(
                        state.currentCondition, style = TextStyle(
                            color = GlanceTheme.colors.onSurfaceVariant,
                            fontSize = 16.sp,
                        )
                    )
                }
            }
        }
        Spacer(
            modifier = GlanceModifier.defaultWeight()
        )

        Row(GlanceModifier.fillMaxWidth(), horizontalAlignment = Alignment.End) {
            state?.hourly?.take(4)?.forEach {
                HourlyItem(it.time, it.temp, it.conditionIcon)
            }
        }

    }
}

@Composable
private fun HourlyItem(
    time: String,
    temperature: String,
    icon: Int
) {

    Column(
        modifier = GlanceModifier.height(90.dp)
            .width(45.dp),

        verticalAlignment = Alignment.Bottom,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(GlanceModifier.height(5.dp))
        Text(
            temperature,
            style = TextStyle(
                color = GlanceTheme.colors.onSurface,
                fontSize = 16.sp
            )
        )
        Spacer(GlanceModifier.height(4.dp))
        Image(
            provider = ImageProvider(icon),
            contentDescription = null,
            GlanceModifier.size(28.dp)
        )
        Spacer(GlanceModifier.height(3.dp))
        Text(
            time,
            style = TextStyle(
                color = GlanceTheme.colors.onSurfaceVariant,
                fontSize = 14.sp
            )
        )
    }
}

