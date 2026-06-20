package com.pranshulgg.weather_master_app.widgets.glance

import android.annotation.SuppressLint
import android.content.Context
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.glance.GlanceId
import androidx.glance.GlanceModifier
import androidx.glance.Image
import androidx.glance.ImageProvider
import androidx.glance.LocalContext
import androidx.glance.LocalSize
import androidx.glance.action.actionStartActivity
import androidx.glance.action.clickable
import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.appwidget.SizeMode
import androidx.glance.appwidget.provideContent
import androidx.glance.currentState
import androidx.glance.layout.Box
import androidx.glance.layout.Column
import androidx.glance.layout.Row
import androidx.glance.layout.Spacer
import androidx.glance.layout.fillMaxSize
import androidx.glance.layout.fillMaxWidth
import androidx.glance.layout.height
import androidx.glance.layout.padding
import androidx.glance.layout.size
import androidx.glance.layout.width
import androidx.glance.text.Text
import androidx.glance.text.TextStyle
import androidx.glance.unit.ColorProvider
import com.pranshulgg.weather_master_app.MainActivity
import com.pranshulgg.weather_master_app.R
import com.pranshulgg.weather_master_app.widgets.WeatherWidgetStateDefinition
import com.pranshulgg.weather_master_app.widgets.WeatherWidgetStateJson
import com.pranshulgg.weather_master_app.widgets.model.WidgetWeather
import com.pranshulgg.weather_master_app.widgets.params.WidgetSizePoints
import com.pranshulgg.weather_master_app.widgets.ui.views.WidgetClock
import com.pranshulgg.weather_master_app.widgets.ui.views.WidgetDate
import kotlinx.serialization.json.Json

class GlanceWidget : GlanceAppWidget() {

    override val sizeMode = SizeMode.Responsive(
        sizes = WidgetSizePoints.SIZES

    )
    override val stateDefinition =
        WeatherWidgetStateDefinition

    @SuppressLint("RestrictedApi")
    override suspend fun provideGlance(
        context: Context,
        id: GlanceId
    ) {


        provideContent {
            val size = LocalSize.current
            val widgetState = currentState<WeatherWidgetStateJson>()
            val context = LocalContext.current

            val json = widgetState.json
            val state = json?.let {
                Json.decodeFromString<WidgetWeather>(it)
            }
            val config = widgetState.config
            Box(
                GlanceModifier.fillMaxSize()
                    .clickable(actionStartActivity<MainActivity>())
            ) {
                if (state != null)
                    Column(GlanceModifier.fillMaxWidth().padding(16.dp)) {
                        if (config.showClock) {
                            WidgetClock(config.clockSize, context)
                        }

                        WidgetDate(config.dateFormat, context)

                        Spacer(GlanceModifier.height(5.dp))
                        Row() {
                            Image(
                                provider = ImageProvider(state.currentIcon),
                                contentDescription = null,
                                modifier = GlanceModifier.size(24.dp)
                            )
                            Spacer(GlanceModifier.width(5.dp))
                            Box {
                                Text(
                                    text = "${state.currentTemp} • ",
                                    style = TextStyle(
                                        color = ColorProvider(R.color.shadow),
                                        fontSize = 18.sp,
                                    ),
                                    modifier = GlanceModifier.padding(top = 2.dp, start = 2.dp)
                                )
                                Text(
                                    "${state.currentTemp} • ",
                                    style = TextStyle(
                                        color = ColorProvider(R.color.white),
                                        fontSize = 18.sp
                                    )
                                )
                            }
                            Box {
                                Text(
                                    text = state.currentCondition,
                                    style = TextStyle(
                                        color = ColorProvider(R.color.shadow),
                                        fontSize = 18.sp,
                                    ),
                                    modifier = GlanceModifier.padding(top = 2.dp, start = 2.dp)
                                )
                                Text(
                                    state.currentCondition,
                                    style = TextStyle(
                                        color = ColorProvider(R.color.white),
                                        fontSize = 18.sp
                                    )
                                )
                            }
                        }
                    }
            }
        }
    }

}

