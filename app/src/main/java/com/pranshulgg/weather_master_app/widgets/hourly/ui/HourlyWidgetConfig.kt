package com.pranshulgg.weather_master_app.widgets.hourly.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
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
import androidx.compose.runtime.mutableFloatStateOf
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
import com.pranshulgg.weather_master_app.core.ui.components.Gap
import com.pranshulgg.weather_master_app.core.ui.components.SettingSection
import com.pranshulgg.weather_master_app.core.ui.components.SettingTile
import com.pranshulgg.weather_master_app.core.ui.components.SettingsTileIcon
import com.pranshulgg.weather_master_app.core.ui.components.WeatherIconBox
import com.pranshulgg.weather_master_app.core.ui.components.tiles.DialogOption
import com.pranshulgg.weather_master_app.core.ui.theme.ShapeRadius
import com.pranshulgg.weather_master_app.widgets.config.WidgetConfig
import com.pranshulgg.weather_master_app.widgets.model.WidgetVariant
import com.pranshulgg.weather_master_app.widgets.ui.colors.WidgetTextTheme
import com.pranshulgg.weather_master_app.widgets.ui.colors.WidgetTheme
import kotlin.math.round
import kotlin.math.roundToInt


@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun HourlyWidgetConfig(onDone: (WidgetConfig) -> Unit = {}) {
    val btnSize = ButtonDefaults.MediumContainerHeight

    var selectedHourlyCount by remember { mutableFloatStateOf(6f) }
    var selectedFontSize by remember { mutableFloatStateOf(1f) }
    var widgetTheme by remember { mutableStateOf(WidgetTheme.AUTO) }
    var widgetTextTheme by remember { mutableStateOf(WidgetTextTheme.AUTO) }
    var selectedIconSize by remember { mutableFloatStateOf(1f) }

    val widgetThemeOptions =
        WidgetTheme.entries.filter { it != WidgetTheme.TRANSPARENT }
            .map { DialogOption(it.toString(), stringResource(it.label)) }
    val widgetTextThemeOptions =
        WidgetTextTheme.entries.map { DialogOption(it.toString(), stringResource(it.label)) }
    var showPrecipitationProbability by remember { mutableStateOf(false) }


    Scaffold(
        containerColor = MaterialTheme.colorScheme.surfaceContainer
    ) { paddingValues ->
        Column(
            Modifier
                .padding(bottom = paddingValues.calculateBottomPadding())
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
        ) {

            Surface(
                color = Color(0xFF787878),
                modifier = Modifier
                    .fillMaxWidth()
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.weight(1f)
                ) {
                    Gap(paddingValues.calculateTopPadding())
                    GlanceWidgetPreview(
                        selectedHourlyCount,
                        selectedFontSize,
                        selectedIconSize,
                        widgetTextTheme,
                        widgetTheme,
                        showPrecipitationProbability
                    )
                }
            }
            Gap(16.dp)
            SettingSection(
                title = stringResource(R.string.setting_appearance),
                tiles = listOf(
                    SettingTile.DialogSliderTile(
                        title = stringResource(R.string.settings_widget_hourly_forecast_count),
                        dialogTitle = stringResource(R.string.settings_widget_hourly_forecast_count),
                        leading = { SettingsTileIcon(R.drawable.date_range_24px) },
                        valueRange = 2f..12f,
                        description = stringResource(
                            R.string.time_hours,
                            "${selectedHourlyCount.roundToInt()}"
                        ),
                        isDescriptionAsValue = true,
                        initialValue = selectedHourlyCount,
                        labelFormatter = { "${it.roundToInt()}" },
                        steps = 9,
                        onValueSubmitted = {
                            selectedHourlyCount = it
                        }
                    ),
                    SettingTile.DialogSliderTile(
                        title = stringResource(R.string.settings_widget_font_size),
                        dialogTitle = stringResource(R.string.settings_widget_font_size),
                        leading = { SettingsTileIcon(R.drawable.format_size_24px) },
                        description = "${round(selectedFontSize * 10) / 10}",
                        isDescriptionAsValue = true,
                        valueRange = 0.1f..2f,
                        initialValue = selectedFontSize,
                        labelFormatter = { "${round(it * 10) / 10}" },
                        steps = 18,
                        onValueSubmitted = {
                            selectedFontSize = it
                        },
                    ),

                    SettingTile.DialogSliderTile(
                        title = stringResource(R.string.settings_widget_icon_size),
                        dialogTitle = stringResource(R.string.settings_widget_icon_size),
                        leading = { SettingsTileIcon(R.drawable.photo_size_select_large_24px) },
                        description = "${round(selectedIconSize * 10) / 10}",
                        isDescriptionAsValue = true,
                        valueRange = 0.1f..2f,
                        initialValue = selectedIconSize,
                        labelFormatter = { "${round(it * 10) / 10}" },
                        steps = 18,
                        onValueSubmitted = {
                            selectedIconSize = it
                        },
                    ),
                    SettingTile.SwitchTile(
                        leading = { SettingsTileIcon(R.drawable.rainy_light_24px) },
                        title = stringResource(R.string.settings_widget_show_precip_probability),
                        description = stringResource(R.string.settings_widget_show_precip_probability_secondary),
                        checked = showPrecipitationProbability,
                        onCheckedChange = {
                            showPrecipitationProbability = it
                        }
                    ),
                    SettingTile.DialogOptionTile(
                        leading = { SettingsTileIcon(R.drawable.palette_24px) },
                        title = stringResource(R.string.settings_widget_background),
                        options = widgetThemeOptions,
                        selectedOption = widgetTheme.toString(),
                        onOptionSelected = {
                            val selected = when (it) {
                                "AUTO" -> WidgetTheme.AUTO
                                "DARK" -> WidgetTheme.DARK
                                "LIGHT" -> WidgetTheme.LIGHT
                                else -> WidgetTheme.AUTO
                            }

                            widgetTheme = selected
                        }
                    ),
                    SettingTile.DialogOptionTile(
                        leading = { SettingsTileIcon(R.drawable.format_paint_24px) },
                        title = stringResource(R.string.settings_widget_text_color),
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
                    )
                )
            )


            Spacer(Modifier.weight(1f))
            Button(
                onClick = {
                    onDone(
                        WidgetConfig(
                            hourlyCount = selectedHourlyCount.roundToInt(),
                            fontSize = selectedFontSize,
                            iconSize = selectedIconSize,
                            widgetTheme = widgetTheme,
                            widgetTextTheme = widgetTextTheme,
                            showPrecipitationProbability = showPrecipitationProbability
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
                Text(
                    stringResource(R.string.action_create_widget),
                    style = ButtonDefaults.textStyleFor(btnSize)
                )
            }
        }
    }

}

@Composable
private fun GlanceWidgetPreview(
    hourlyCount: Float,
    fontSize: Float,
    iconSize: Float,
    textTheme: WidgetTextTheme,
    widgetTheme: WidgetTheme,
    showPrecipitationProbability: Boolean
) {
    val textColor = when (textTheme) {
        WidgetTextTheme.AUTO -> if (widgetTheme == WidgetTheme.DARK)
            Color.White else if (widgetTheme == WidgetTheme.LIGHT)
            Color.Black else MaterialTheme.colorScheme.onSurface

        WidgetTextTheme.BLACK -> Color.Black
        WidgetTextTheme.WHITE -> Color.White
    }


    val textColorSecondary = when (textTheme) {
        WidgetTextTheme.AUTO -> if (widgetTheme == WidgetTheme.DARK)
            Color(0xB3FFFFFF) else if (widgetTheme == WidgetTheme.LIGHT)
            Color(0x99000000) else MaterialTheme.colorScheme.onSurfaceVariant

        WidgetTextTheme.BLACK -> Color(0x99000000)
        WidgetTextTheme.WHITE -> Color(0xB3FFFFFF)
    }


    val widgetColor = when (widgetTheme) {
        WidgetTheme.AUTO -> MaterialTheme.colorScheme.surfaceContainerHighest
        WidgetTheme.DARK -> Color.Black
        WidgetTheme.LIGHT -> Color.White
        else -> MaterialTheme.colorScheme.surfaceContainerHighest
    }

    val times = listOf(
        "7PM",
        "8PM",
        "9PM",
        "10PM",
        "11PM",
        "12AM",
        "1AM",
        "2AM",
        "3AM",
        "4AM",
        "5AM",
        "6AM",
        "7AM"
    )
    val temps = listOf(15, 17, 18, 19, 20, 21, 22, 24, 26, 26, 27, 27, 28)
    val icons = listOf(
        R.drawable.weather_clear_night,
        R.drawable.weather_partly_cloudy_night,
        R.drawable.weather_partly_cloudy_night,
        R.drawable.weather_partly_cloudy_night,
        R.drawable.weather_clear_night,
        R.drawable.weather_clear_night,
        R.drawable.weather_clear_night,
        R.drawable.weather_mostly_clear_night,
        R.drawable.weather_mostly_clear_night,
        R.drawable.weather_mostly_clear_night,
        R.drawable.weather_clear_night,
        R.drawable.weather_clear_night,
        R.drawable.weather_clear_night
    )

    val hourlyIconSize = 22 * iconSize
    val hourlyTextSize = 14 * fontSize

    val hourlyItem: @Composable (String, Int, Int) -> Unit = { time, temp, icon ->
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .padding(horizontal = 5.dp, vertical = 10.dp)
        ) {
            Text(
                "${temp}°",
                fontSize = hourlyTextSize.sp,
                fontWeight = FontWeight.Medium,
                color = textColor
            )
            if (showPrecipitationProbability) {
                Text(
                    "${temp * 2}%",
                    fontSize = (hourlyTextSize * 0.9).sp,
                    color = textColorSecondary
                )
            }
            Gap(3.dp)
            WeatherIconBox(icon, hourlyIconSize.dp)
            Gap(3.dp)
            Text(
                time,
                fontSize = hourlyTextSize.sp,
                fontWeight = FontWeight.Medium,
                color = textColorSecondary
            )
        }
    }

    Row(
        verticalAlignment = Alignment.Bottom,
        horizontalArrangement = Arrangement.Center,
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
            .background(
                widgetColor,
                RoundedCornerShape(ShapeRadius.ExtraLarge)
            )
    ) {
        times.take(hourlyCount.roundToInt()).forEachIndexed { index, string ->

            hourlyItem(
                string,
                temps[index],
                icons[index]
            )
        }
    }

}
