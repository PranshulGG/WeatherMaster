package com.pranshulgg.weather_master_app.widgets.weather4.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.ToggleButton
import androidx.compose.material3.ToggleButtonDefaults
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
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.role
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.PlatformTextStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.LineHeightStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.pranshulgg.weather_master_app.R
import com.pranshulgg.weather_master_app.core.ui.components.Gap
import com.pranshulgg.weather_master_app.core.ui.components.SettingSection
import com.pranshulgg.weather_master_app.core.ui.components.SettingTile
import com.pranshulgg.weather_master_app.core.ui.components.SettingsTileIcon
import com.pranshulgg.weather_master_app.core.ui.components.WeatherIconBox
import com.pranshulgg.weather_master_app.core.ui.components.tiles.DialogOption
import com.pranshulgg.weather_master_app.widgets.config.WidgetConfig
import com.pranshulgg.weather_master_app.widgets.model.WidgetVariant
import com.pranshulgg.weather_master_app.widgets.ui.colors.WidgetTextTheme
import kotlin.math.round
import kotlin.math.roundToInt


@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun Weather4Config(onDone: (WidgetConfig) -> Unit = {}) {
    val btnSize = ButtonDefaults.MediumContainerHeight

    var selectedVariant by remember { mutableStateOf(WidgetVariant.DATE) }
    var selectedFontSize by remember { mutableFloatStateOf(1f) }
    var showDaily by remember { mutableStateOf(false) }
    var selectedIconSize by remember { mutableFloatStateOf(1f) }

    val widgetVariantOptions = WidgetVariant.entries.filter {
        it != WidgetVariant.LARGE && it != WidgetVariant.COMPACT && it != WidgetVariant.SMALL
    }

    val formats = when (selectedVariant) {
        WidgetVariant.DATE_PILL -> listOf("EEE d MMMM", "EEE, MMMM d", "EEE MMM-dd")
        else -> listOf("EEEE, d MMMM", "EEEE, MMMM d", "EEEE MM-dd")
    }

    var dateFormat by remember(selectedVariant) { mutableStateOf(if (selectedVariant == WidgetVariant.DATE_PILL) "EEE, MMMM d" else "EEEE, MMMM d") }
    val formatsOptions = formats.map { DialogOption(it, it) }
    var widgetTextTheme by remember { mutableStateOf(WidgetTextTheme.WHITE) }
    val widgetTextThemeOptions =
        WidgetTextTheme.entries.filter { it != WidgetTextTheme.AUTO }
            .map { DialogOption(it.toString(), stringResource(it.label)) }


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
                    WidgetPreviews(
                        selectedVariant,
                        widgetTextTheme,
                        selectedFontSize,
                        selectedIconSize, showDaily,
                        dateFormat
                    )
                }
            }
            Gap(16.dp)
            Text(
                text = stringResource(R.string.label_variant),
                modifier = Modifier.padding(bottom = 5.dp, top = 5.dp, start = 3.dp + 16.dp),
                fontSize = 16.sp,
                color = MaterialTheme.colorScheme.primary,
                fontWeight = FontWeight.W700
            )

            FlowRow(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                widgetVariantOptions.forEach { item ->
                    ToggleButton(
                        checked = selectedVariant == item,
                        onCheckedChange = {
                            selectedVariant = item
                        },
                        modifier = Modifier.semantics { role = Role.RadioButton },
                        colors = ToggleButtonDefaults.toggleButtonColors(
                            checkedContainerColor = MaterialTheme.colorScheme.tertiary,
                            checkedContentColor = MaterialTheme.colorScheme.onTertiary,
                            containerColor = MaterialTheme.colorScheme.surfaceContainerHighest,
                        )
                    ) {
                        Text(item.label)
                    }
                }
            }


            SettingSection(
                title = stringResource(R.string.setting_appearance),
                tiles = listOf(
                    SettingTile.DialogOptionTile(
                        leading = { SettingsTileIcon(R.drawable.date_range_24px) },
                        title = stringResource(R.string.settings_date_format),
                        options = formatsOptions,
                        selectedOption = dateFormat,
                        onOptionSelected = {
                            dateFormat = it
                        }
                    ),
                    SettingTile.SwitchTile(
                        title = stringResource(R.string.settings_widget_show_daily_weather),
                        description = stringResource(R.string.settings_widget_show_daily_weather_secondary),
                        leading = { SettingsTileIcon(R.drawable.date_range_24px) },
                        checked = showDaily,
                        onCheckedChange = {
                            showDaily = it
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
                    SettingTile.DialogOptionTile(
                        leading = { SettingsTileIcon(R.drawable.format_paint_24px) },
                        title = stringResource(R.string.settings_widget_text_color),
                        options = widgetTextThemeOptions,
                        selectedOption = widgetTextTheme.toString(),
                        onOptionSelected = {
                            val selected = when (it) {
                                "WHITE" -> WidgetTextTheme.WHITE
                                "BLACK" -> WidgetTextTheme.BLACK
                                else -> WidgetTextTheme.WHITE
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
                            variant = selectedVariant,
                            fontSize = selectedFontSize,
                            iconSize = selectedIconSize,
                            widgetTextTheme = widgetTextTheme,
                            showDailyInsteadOfCurrent = showDaily,
                            dateFormat = dateFormat
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
private fun WidgetPreviews(
    variant: WidgetVariant,
    widgetTextTheme: WidgetTextTheme,
    fontSize: Float,
    iconSize: Float,
    showDaily: Boolean,
    format: String
) {

    val textColor = when (widgetTextTheme) {
        WidgetTextTheme.BLACK -> Color.Black
        WidgetTextTheme.WHITE -> Color.White
        else -> Color.White
    }
    val date = when (format) {
        "EEE, d MMMM" -> "Wed, 18 June"
        "EEE, MMMM d" -> "Wed, June 18"
        "EEE MM-dd" -> "Wed 06-18"
        "EEEE, d MMMM" -> "Wednesday, 9 July"
        "EEEE, MMMM d" -> "Wednesday, July 9"
        "EEEE MM-dd" -> "Wednesday 05-29"
        else -> "Thu 18 Jun"
    }

    when (variant) {
        WidgetVariant.DATE -> DateWidgetPreview(textColor, fontSize, iconSize, showDaily, date)
        WidgetVariant.DATE_PILL -> DatePillWidgetPreview(fontSize, iconSize, showDaily, date)
        WidgetVariant.CLOCK_DATE -> ClockDateWidgetPreview(
            textColor,
            fontSize,
            iconSize,
            showDaily,
            date
        )

        WidgetVariant.CLOCK_VERTICAL -> ClockVerticalWidgetPreview(
            textColor,
            fontSize,
            iconSize,
            showDaily, date
        )

        WidgetVariant.CLOCK_HORIZONTAL -> ClockHorizontalWidgetPreview(
            textColor,
            fontSize,
            iconSize, showDaily, date
        )

        else -> DateWidgetPreview(textColor, fontSize, iconSize, showDaily, date)
    }
}

@Composable
private fun DateWidgetPreview(
    textColor: Color, fontSize: Float,
    iconSize: Float,
    showDaily: Boolean,
    date: String
) {

    val icon = if (showDaily) R.drawable.rain_with_clear else R.drawable.weather_mostly_clear_day

    val iconSize = 24 * iconSize
    val textSize = 20 * fontSize
    val temp = if (showDaily) "28°" else "24°"



    Row(Modifier.height(120.dp), verticalAlignment = Alignment.CenterVertically) {
        Text(
            "$date | ",
            fontSize = textSize.sp,
            color = textColor,
            fontWeight = FontWeight.Medium
        )
        WeatherIconBox(icon, size = iconSize.dp)
        Gap(horizontal = 5.dp)
        Text(temp, fontSize = textSize.sp, color = textColor, fontWeight = FontWeight.Medium)
    }
}

@Composable
private fun DatePillWidgetPreview(
    fontSize: Float,
    iconSize: Float,
    showDaily: Boolean,
    date: String
) {
    val icon = if (showDaily) R.drawable.rain_with_clear else R.drawable.weather_mostly_clear_day
    val condition = if (showDaily) "Rain with clear" else "Mostly clear"

    val iconSize = 48 * iconSize
    val textSize = 16 * fontSize
    val temp = if (showDaily) "28°" else "24°"


    Row(Modifier.height(120.dp), verticalAlignment = Alignment.CenterVertically) {
        Surface(
            Modifier.height(90.dp),
            color = MaterialTheme.colorScheme.surfaceContainerHighest,
            shape = CircleShape
        ) {
            Column(
                Modifier
                    .weight(1f)
                    .padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    date,
                    fontWeight = FontWeight.Medium,
                    fontSize = textSize.sp,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    "$temp • $condition",
                    fontWeight = FontWeight.Medium,
                    fontSize = textSize.minus(2).sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
        Gap(horizontal = 8.dp)
        Surface(
            Modifier.size(90.dp),
            color = MaterialTheme.colorScheme.surfaceContainerHighest,
            shape = CircleShape
        ) {
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                WeatherIconBox(icon, size = iconSize.dp)
            }
        }
    }
}


@Composable
private fun ClockDateWidgetPreview(
    textColor: Color, fontSize: Float,
    iconSize: Float,
    showDaily: Boolean,
    date: String
) {

    val icon = if (showDaily) R.drawable.rain_with_clear else R.drawable.weather_mostly_clear_day
    val iconSize = 24 * iconSize
    val clockSize = 64 * fontSize
    val textSize = 20 * fontSize
    val temp = if (showDaily) "21° / 28°" else "24°"

    Column(
        Modifier
            .heightIn(150.dp)
            .padding(bottom = 12.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            "03:23",
            color = textColor,
            fontSize = clockSize.sp,
            fontWeight = FontWeight.Bold,
            style = TextStyle(
                platformStyle = PlatformTextStyle(
                    includeFontPadding = false
                )
            )
        )
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
                "$date • ",
                fontSize = textSize.sp,
                color = textColor,
                fontWeight = FontWeight.Medium
            )
            WeatherIconBox(icon, size = iconSize.dp)
            Gap(horizontal = 5.dp)
            Text(temp, fontSize = textSize.sp, color = textColor, fontWeight = FontWeight.Medium)
        }
    }
}

@Composable
private fun ClockVerticalWidgetPreview(
    textColor: Color, fontSize: Float,
    iconSize: Float,
    showDaily: Boolean,
    date: String
) {

    val iconSize = 20 * iconSize
    val clockSize = 64 * fontSize
    val textSize = 20 * fontSize
    val secondaryTextSize = 16 * fontSize
    val icon = if (showDaily) R.drawable.rain_with_clear else R.drawable.weather_mostly_clear_day
    val condition = if (showDaily) "Rain with clear" else "Mostly clear"
    val temp = if (showDaily) "21° / 28°" else "24°"

    Column(
        Modifier
            .heightIn(150.dp)
            .padding(bottom = 12.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            "03",
            color = textColor,
            fontSize = clockSize.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier
                .height(70.dp * fontSize),
            style = TextStyle(
                platformStyle = PlatformTextStyle(
                    includeFontPadding = false
                )
            )
        )
        Text(
            "23",
            color = textColor,
            fontSize = clockSize.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier
                .height(70.dp * fontSize),
            textAlign = TextAlign.Center,
            style = TextStyle(
                platformStyle = PlatformTextStyle(
                    includeFontPadding = false
                )
            )

        )

        Text(
            date,
            fontSize = textSize.sp,
            color = textColor,
            fontWeight = FontWeight.Medium
        )

        Row(verticalAlignment = Alignment.CenterVertically) {
            WeatherIconBox(icon, size = iconSize.dp)
            Gap(horizontal = 5.dp)
            Text(
                "$condition • ",
                fontSize = secondaryTextSize.sp,
                color = textColor,
                fontWeight = FontWeight.Medium
            )
            Text(
                temp,
                fontSize = secondaryTextSize.sp,
                color = textColor,
                fontWeight = FontWeight.Medium
            )
        }
    }
}

@Composable
private fun ClockHorizontalWidgetPreview(
    textColor: Color, fontSize: Float,
    iconSize: Float,
    showDaily: Boolean,
    date: String
) {

    val iconSize = 24 * iconSize
    val clockSize = 56 * fontSize
    val textSize = 18 * fontSize

    val icon = if (showDaily) R.drawable.rain_with_clear else R.drawable.weather_mostly_clear_day

    val temp = if (showDaily) "21° / 28°" else "24°"

    Row(
        Modifier
            .heightIn(150.dp)
            .padding(bottom = 12.dp, start = 16.dp, end = 16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            "03:23",
            color = textColor,
            fontSize = clockSize.sp,
            fontWeight = FontWeight.Bold,
            style = TextStyle(
                platformStyle = PlatformTextStyle(
                    includeFontPadding = false
                )
            )
        )
        Spacer(Modifier.weight(1f))
        Column(horizontalAlignment = Alignment.End) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                WeatherIconBox(icon, size = iconSize.dp)
                Gap(horizontal = 5.dp)
                Text(
                    temp,
                    fontSize = textSize.sp,
                    color = textColor,
                    fontWeight = FontWeight.Medium
                )
            }
            Text(
                date,
                fontSize = textSize.sp,
                color = textColor,
                fontWeight = FontWeight.Medium
            )
        }
    }
}
