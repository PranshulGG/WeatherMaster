package com.pranshulgg.weather_master_app.widgets.weather5.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.sizeIn
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
import com.pranshulgg.weather_master_app.core.model.weather.uv.UvIndex
import com.pranshulgg.weather_master_app.core.model.weather.uv.toColor
import com.pranshulgg.weather_master_app.core.ui.components.Gap
import com.pranshulgg.weather_master_app.core.ui.components.SettingSection
import com.pranshulgg.weather_master_app.core.ui.components.SettingTile
import com.pranshulgg.weather_master_app.core.ui.components.SettingsTileIcon
import com.pranshulgg.weather_master_app.core.ui.components.WeatherIconBox
import com.pranshulgg.weather_master_app.core.ui.components.tiles.DialogOption
import com.pranshulgg.weather_master_app.core.ui.theme.ShapeRadius
import com.pranshulgg.weather_master_app.widgets.config.WidgetConfig
import com.pranshulgg.weather_master_app.widgets.ui.colors.WidgetTextTheme
import com.pranshulgg.weather_master_app.widgets.ui.colors.WidgetTheme
import kotlin.math.round
import kotlin.math.sign


@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun Weather5WidgetConfig(onDone: (WidgetConfig) -> Unit = {}) {
    val btnSize = ButtonDefaults.MediumContainerHeight
    var widgetTheme by remember { mutableStateOf(WidgetTheme.AUTO) }
    var widgetTextTheme by remember { mutableStateOf(WidgetTextTheme.AUTO) }
    var selectedFontSize by remember { mutableFloatStateOf(1f) }
    var selectedIconSize by remember { mutableFloatStateOf(1f) }

    val widgetThemeOptions =
        WidgetTheme.entries.map { DialogOption(it.toString(), stringResource(it.label)) }
    val widgetTextThemeOptions =
        WidgetTextTheme.entries.map { DialogOption(it.toString(), stringResource(it.label)) }

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
                    Weather5WidgetPreview(
                        widgetTextTheme,
                        widgetTheme,
                        selectedFontSize,
                        selectedIconSize
                    )
                }
            }
            Gap(16.dp)
            SettingSection(
                title = stringResource(R.string.setting_appearance),
                tiles = listOf(
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
                                "TRANSPARENT" -> WidgetTheme.TRANSPARENT
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
                    )
                )
            )
            Spacer(Modifier.weight(1f))
            Button(
                onClick = {
                    onDone(
                        WidgetConfig(
                            widgetTheme = widgetTheme,
                            widgetTextTheme = widgetTextTheme,
                            fontSize = selectedFontSize,
                            iconSize = selectedIconSize
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
private fun Weather5WidgetPreview(
    textTheme: WidgetTextTheme,
    widgetTheme: WidgetTheme,
    fontSize: Float,
    iconSize: Float,
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
        WidgetTheme.TRANSPARENT -> Color(0xFF787878)
    }

    val mainTempFontSize = 42 * fontSize
    val conditionFontSize = 18 * fontSize
    val mainIconSize = 52 * iconSize
    val dailyFontSize = 16 * fontSize
    val dailyIconSize = 24 * iconSize


    Column(
        modifier = Modifier
            .sizeIn(226.dp, 226.dp)
            .padding(16.dp)
            .background(
                widgetColor,
                RoundedCornerShape(ShapeRadius.ExtraLarge)
            )
    ) {
        Row(Modifier.padding(16.dp)) {
            Column() {
                Text(
                    "Clear sky",
                    color = textColor,
                    fontSize = conditionFontSize.sp,
                    fontWeight = FontWeight.Medium
                )
                Text(
                    "27°",
                    color = textColor,
                    fontSize = mainTempFontSize.sp,
                    fontWeight = FontWeight.Bold
                )
            }
            Spacer(Modifier.weight(1f))
            WeatherIconBox(R.drawable.weather_clear_day, size = mainIconSize.dp)
        }
        DailyRow(
            "Wed",
            "27°/29°",
            dailyFontSize,
            dailyIconSize,
            R.drawable.weather_clear_day,
            textColor
        )
        Gap(5.dp)
        DailyRow(
            "Thu",
            "25°/31°",
            dailyFontSize,
            dailyIconSize,
            R.drawable.clear_with_rain,
            textColor
        )
        Gap(5.dp)
        DailyRow(
            "Fri",
            "26°/34°",
            dailyFontSize,
            dailyIconSize,
            R.drawable.clear_with_cloudy,
            textColor
        )

        Gap(16.dp)
    }

}

@Composable
private fun DailyRow(
    text: String,
    temps: String,
    fontSize: Float,
    iconSize: Float,
    icon: Int,
    textColor: Color
) {
    Row(
        Modifier
            .padding(horizontal = 16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(text, fontSize = fontSize.sp, color = textColor)
        Spacer(Modifier.weight(1f))
        WeatherIconBox(icon, size = iconSize.dp)
        Spacer(Modifier.width(8.dp))
        Text(temps, fontSize = fontSize.sp, fontWeight = FontWeight.Medium, color = textColor)
    }
}