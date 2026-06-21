package com.pranshulgg.weather_master_app.widgets.weatherclockdaily.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.pranshulgg.weather_master_app.R
import com.pranshulgg.weather_master_app.core.ui.components.SettingSection
import com.pranshulgg.weather_master_app.core.ui.components.SettingTile
import com.pranshulgg.weather_master_app.core.ui.components.SettingsTileIcon
import com.pranshulgg.weather_master_app.core.ui.components.WeatherIconBox
import com.pranshulgg.weather_master_app.core.ui.components.tiles.DialogOption
import com.pranshulgg.weather_master_app.core.ui.theme.ShapeRadius
import com.pranshulgg.weather_master_app.core.utils.formatters.toSafeDouble
import com.pranshulgg.weather_master_app.widgets.config.WidgetConfig
import com.pranshulgg.weather_master_app.widgets.ui.colors.WidgetTextTheme
import com.pranshulgg.weather_master_app.widgets.ui.colors.WidgetTheme
import kotlin.math.roundToInt


@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun ClockDailyWidgetConfig(onDone: (WidgetConfig) -> Unit = {}) {

    var dateFormat by remember { mutableStateOf("EEE d MMM") }
    var widgetTheme by remember { mutableStateOf(WidgetTheme.AUTO) }
    var widgetTextTheme by remember { mutableStateOf(WidgetTextTheme.AUTO) }

    val formats = listOf("EEE d MMM", "EEE MMM d", "EEE MM-dd")

    val btnSize = ButtonDefaults.MediumContainerHeight


    val formatsOptions = formats.map { DialogOption(it, it) }
    val widgetThemeOptions =
        WidgetTheme.entries.map { DialogOption(it.toString(), stringResource(it.label)) }
    val widgetTextThemeOptions =
        WidgetTextTheme.entries.map { DialogOption(it.toString(), stringResource(it.label)) }


    Scaffold(
        containerColor = MaterialTheme.colorScheme.surfaceContainer
    ) { paddingValues ->
        Column(Modifier.padding(paddingValues)) {
            Column(Modifier.verticalScroll(rememberScrollState())) {
                Surface(
                    color = Color.Black,
                    shape = RoundedCornerShape(ShapeRadius.ExtraLarge),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                ) {
                    ClockDailyWidgetPreview(widgetTextTheme, widgetTheme, dateFormat)
                }

                SettingSection(
                    title = stringResource(R.string.setting_appearance),
                    tiles = listOf(
                        SettingTile.DialogOptionTile(
                            leading = { SettingsTileIcon(R.drawable.date_range_24px) },
                            title = "Date format",
                            options = formatsOptions,
                            selectedOption = dateFormat,
                            onOptionSelected = {
                                dateFormat = it
                            }
                        ),
                        SettingTile.DialogOptionTile(
                            leading = { SettingsTileIcon(R.drawable.palette_24px) },
                            title = "Widget background",
                            options = widgetThemeOptions,
                            selectedOption = widgetTheme.toString(),
                            onOptionSelected = {
                                val selected = when (it) {
                                    "AUTO" -> WidgetTheme.AUTO
                                    "DARK" -> WidgetTheme.DARK
                                    "LIGHT" -> WidgetTheme.LIGHT
                                    "TRANSPARENT" -> WidgetTheme.TRANSPARENT
                                    else -> WidgetTheme.AUTO
                                }

                                widgetTheme = selected
                            }
                        ),
                        SettingTile.DialogOptionTile(
                            leading = { SettingsTileIcon(R.drawable.format_paint_24px) },
                            title = "Widget text color",
                            options = widgetTextThemeOptions,
                            selectedOption = widgetTextTheme.toString(),
                            onOptionSelected = {
                                val selected = when (it) {
                                    "AUTO" -> WidgetTextTheme.AUTO
                                    "WHITE" -> WidgetTextTheme.WHITE
                                    "BLACK" -> WidgetTextTheme.BLACK
                                    else -> WidgetTextTheme.AUTO
                                }

                                widgetTextTheme = selected
                            }
                        ),
                    )
                )
            }

            Spacer(Modifier.weight(1f))
            Button(
                onClick = {
                    onDone(
                        WidgetConfig(
                            dateFormat = dateFormat,
                            widgetTheme = widgetTheme,
                            widgetTextTheme = widgetTextTheme
                        )
                    )
                },
                modifier = Modifier
                    .heightIn(btnSize)
                    .fillMaxWidth()
                    .padding(16.dp),
                contentPadding = ButtonDefaults.contentPaddingFor(btnSize),
                shapes = ButtonDefaults.shapes()
            ) {
                Text("Create Widget", style = ButtonDefaults.textStyleFor(btnSize))
            }
        }
    }

}

private data class Daily(
    val temps: String,
    val icon: Int,
    val day: String
)

@Composable
private fun ClockDailyWidgetPreview(
    textTheme: WidgetTextTheme,
    widgetTheme: WidgetTheme,
    format: String
) {

    val textColor = when (textTheme) {
        WidgetTextTheme.AUTO -> if (widgetTheme == WidgetTheme.TRANSPARENT)
            Color.White else if (widgetTheme == WidgetTheme.DARK)
            Color.White else if (widgetTheme == WidgetTheme.LIGHT)
            Color.Black else MaterialTheme.colorScheme.onSurface

        WidgetTextTheme.BLACK -> Color.Black
        WidgetTextTheme.WHITE -> Color.White
    }

    val widgetColor = when (widgetTheme) {
        WidgetTheme.AUTO -> MaterialTheme.colorScheme.surfaceContainerHighest
        WidgetTheme.DARK -> Color.Black
        WidgetTheme.LIGHT -> Color.White
        WidgetTheme.TRANSPARENT -> MaterialTheme.colorScheme.surfaceContainer
    }

    val date = when (format) {
        "EEE d MMM" -> "Thu 18 Jun"
        "EEE MMM d" -> "Thu Jun 18"
        "EEE MM-dd" -> "Thu 06-18"
        else -> "Thu 18 Jun"
    }

    val days = listOf(
        Daily("34°/23°", R.drawable.weather_partly_cloudy_day, "Sun"),
        Daily("31°/23°", R.drawable.weather_partly_cloudy_day, "Mon"),
        Daily("33°/24°", R.drawable.weather_partly_cloudy_day, "Tue"),
        Daily("35°/26°", R.drawable.weather_clear_day, "Wed"),
//        Daily("30°/25°", R.drawable.weather_overcast, "Fri"),

    )

    Column(
        Modifier
            .fillMaxWidth()
            .background(widgetColor)
            .height(220.dp)
            .padding(16.dp)
    ) {
        Row() {
            Text(
                "03:23",
                fontSize = 50.sp,
                color = textColor,
                fontWeight = FontWeight.Bold,
            )
            Spacer(Modifier.weight(1f))
            WeatherIconBox(R.drawable.weather_clear_day, size = 52.dp)
        }
        Row() {
            Text(
                date,
                fontSize = 18.sp,
                color = textColor
            )
            Spacer(Modifier.weight(1f))
            Text("31° Clear sky", color = textColor, fontSize = 18.sp)
        }
        Spacer(Modifier.weight(1f))
        Row(horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth()) {
            days.forEach {
                DailyItem(it.day, it.icon, it.temps, textColor)
            }
        }
    }

}

@Composable
private fun DailyItem(
    day: String,
    icon: Int,
    temps: String, textColor: Color
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .width(64.dp)
            .height(80.dp)
    ) {
        Text(day, color = textColor, fontWeight = FontWeight.Medium, fontSize = 15.sp)
        Spacer(Modifier.height(3.dp))
        WeatherIconBox(icon, size = 28.dp)
        Spacer(Modifier.height(3.dp))
        Text(temps, color = textColor, fontWeight = FontWeight.Medium, fontSize = 15.sp)

    }
}