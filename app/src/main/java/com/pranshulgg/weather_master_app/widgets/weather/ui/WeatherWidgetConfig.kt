package com.pranshulgg.weather_master_app.widgets.weather.ui

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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.glance.GlanceModifier
import androidx.glance.layout.height
import androidx.glance.layout.width
import com.pranshulgg.weather_master_app.R
import com.pranshulgg.weather_master_app.core.ui.components.Gap
import com.pranshulgg.weather_master_app.core.ui.components.SettingSection
import com.pranshulgg.weather_master_app.core.ui.components.SettingTile
import com.pranshulgg.weather_master_app.core.ui.components.SettingsTileIcon
import com.pranshulgg.weather_master_app.core.ui.components.WeatherIconBox
import com.pranshulgg.weather_master_app.core.ui.theme.ShapeRadius
import com.pranshulgg.weather_master_app.widgets.config.WidgetConfig
import com.pranshulgg.weather_master_app.widgets.model.WidgetVariant
import kotlin.math.round
import kotlin.math.roundToInt


@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun WeatherWidgetConfig(onDone: (WidgetConfig) -> Unit = {}) {
    val btnSize = ButtonDefaults.MediumContainerHeight

    var selectedHourlyCount by remember { mutableFloatStateOf(6f) }
    var selectedVariant by remember { mutableStateOf(WidgetVariant.LARGE) }
    var selectedFontSize by remember { mutableFloatStateOf(1f) }

    var selectedIconSize by remember { mutableFloatStateOf(1f) }

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
                        selectedVariant,
                        selectedHourlyCount,
                        selectedFontSize,
                        selectedIconSize
                    )
                }
            }
            Gap(16.dp)
            Text(
                text = "Variant",
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
                WidgetVariant.entries.forEach { item ->
                    ToggleButton(
                        checked = selectedVariant == item,
                        onCheckedChange = {
                            selectedVariant = item
                        },
                        modifier = Modifier.semantics { role = Role.RadioButton },
                        shapes = ToggleButtonDefaults.shapes(),
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
                    SettingTile.DialogSliderTile(
                        title = "Hourly forecast count",
                        dialogTitle = "Hourly forecast count",
                        leading = { SettingsTileIcon(R.drawable.date_range_24px) },
                        valueRange = 2f..12f,
                        description = "${selectedHourlyCount.roundToInt()} hours",
                        isDescriptionAsValue = true,
                        initialValue = selectedHourlyCount,
                        labelFormatter = { "${it.roundToInt()}" },
                        steps = 10,
                        onValueSubmitted = {
                            selectedHourlyCount = it
                        }
                    ),
                    SettingTile.DialogSliderTile(
                        title = "Font size",
                        dialogTitle = "Font size",
                        leading = { SettingsTileIcon(R.drawable.format_size_24px) },
                        description = "${round(selectedFontSize * 10) / 10}",
                        isDescriptionAsValue = true,
                        valueRange = 0.1f..1f,
                        initialValue = selectedFontSize,
                        labelFormatter = { "${round(it * 10) / 10}" },
                        steps = 9,
                        onValueSubmitted = {
                            selectedFontSize = it
                        },
                    ),

                    SettingTile.DialogSliderTile(
                        title = "Icon size",
                        dialogTitle = "Icon size",
                        leading = { SettingsTileIcon(R.drawable.photo_size_select_large_24px) },
                        description = "${round(selectedIconSize * 10) / 10}",
                        isDescriptionAsValue = true,
                        valueRange = 0.1f..1f,
                        initialValue = selectedIconSize,
                        labelFormatter = { "${round(it * 10) / 10}" },
                        steps = 9,
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
                            hourlyCount = selectedHourlyCount.roundToInt(),
                            variant = selectedVariant,
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
                Text("Create Widget", style = ButtonDefaults.textStyleFor(btnSize))
            }
        }
    }

}

@Composable
private fun GlanceWidgetPreview(
    variant: WidgetVariant,
    hourlyCount: Float,
    fontSize: Float,
    iconSize: Float
) {

    when (variant) {
        WidgetVariant.LARGE -> LargeWidgetPreview(hourlyCount, fontSize, iconSize)
        WidgetVariant.COMPACT -> CompactWidgetPreview(fontSize, iconSize)
        else -> SmallWidgetPreview(fontSize, iconSize)
    }
}

@Composable
private fun LargeWidgetPreview(hourlyCount: Float, fontSize: Float, iconSize: Float) {

    val colorScheme = MaterialTheme.colorScheme

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
                .padding(horizontal = 5.dp, vertical = 5.dp)
        ) {
            Text(
                "${temp}°",
                fontSize = hourlyTextSize.sp,
                fontWeight = FontWeight.Medium,
                color = colorScheme.onSurface
            )
            Gap(3.dp)
            WeatherIconBox(icon, hourlyIconSize.dp)
            Gap(3.dp)
            Text(
                time,
                fontSize = hourlyTextSize.sp,
                fontWeight = FontWeight.Medium,
                color = colorScheme.onSurfaceVariant
            )
        }
    }

    val mainIconSize = 32 * iconSize
    val textFontSize = 18 * fontSize
    val locationFontSize = 16 * fontSize
    val tempFontSize = 42 * fontSize

    Column(
        modifier = Modifier
            .padding(16.dp)
            .fillMaxWidth()
            .height(230.dp)
            .background(
                MaterialTheme.colorScheme.surfaceContainerHigh,
                RoundedCornerShape(ShapeRadius.ExtraLarge)
            )
    ) {

        Column(
            Modifier.padding(18.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .fillMaxWidth()

            ) {
                Column() {
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        WeatherIconBox(R.drawable.weather_clear_night, mainIconSize.dp)
                        Gap(horizontal = 6.dp)
                        Text("Clear sky", color = colorScheme.onSurface, fontSize = textFontSize.sp)
                    }
                    Gap(6.dp)
                    Row() {
                        Text(
                            "26°",
                            color = colorScheme.onSurface,
                            fontWeight = FontWeight.Medium,
                            fontSize = textFontSize.sp
                        )
                        Gap(horizontal = 8.dp)
                        Text(
                            "14°",
                            color = colorScheme.onSurfaceVariant,
                            fontWeight = FontWeight.Medium,
                            fontSize = textFontSize.sp
                        )
                    }
                }
                Spacer(Modifier.weight(1f))
                Column(horizontalAlignment = Alignment.End) {
                    Text(
                        "Mountain View",
                        color = colorScheme.onSurfaceVariant,
                        fontSize = locationFontSize.sp,
                        fontWeight = FontWeight.Medium
                    )
                    Text(
                        "16°",
                        color = colorScheme.primary,
                        fontSize = tempFontSize.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
            Spacer(Modifier.weight(1f))
            Surface(
                color = MaterialTheme.colorScheme.surface,
                shape = RoundedCornerShape(16.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.Bottom,
                    horizontalArrangement = Arrangement.Center,
                    modifier = Modifier
                        .fillMaxWidth()
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
        }
    }
}

@Composable
private fun CompactWidgetPreview(fontSize: Float, iconSize: Float) {

    val colorScheme = MaterialTheme.colorScheme
    val mainIconSize = 28 * iconSize
    val textFontSize = 18 * fontSize
    val tempFontSize = 54 * fontSize

    Column(
        modifier = Modifier
            .padding(16.dp)
            .width(180.dp)
            .height(180.dp)
            .background(
                MaterialTheme.colorScheme.surfaceContainerHigh,
                RoundedCornerShape(ShapeRadius.ExtraLarge)
            )
    ) {

        Column(
            Modifier.padding(18.dp)
        ) {
            Row(
                horizontalArrangement = Arrangement.Start,
                verticalAlignment = Alignment.CenterVertically
            ) {
                WeatherIconBox(R.drawable.weather_clear_night, mainIconSize.dp)
                Spacer(Modifier.weight(1f))
                Text("Clear sky", color = colorScheme.onSurface, fontSize = textFontSize.sp)
            }
            Spacer(Modifier.weight(1f))

            Text(
                "16°",
                color = colorScheme.primary,
                fontSize = tempFontSize.sp,
                fontWeight = FontWeight.Bold
            )
            Gap(horizontal = 6.dp)
            Row() {
                Text(
                    "26°",
                    color = colorScheme.onSurface,
                    fontWeight = FontWeight.Medium,
                    fontSize = textFontSize.sp
                )
                Gap(horizontal = 8.dp)
                Text(
                    "14°",
                    color = colorScheme.onSurfaceVariant,
                    fontWeight = FontWeight.Medium,
                    fontSize = textFontSize.sp
                )
            }
        }
    }
}

@Composable
private fun SmallWidgetPreview(fontSize: Float, iconSize: Float) {

    val colorScheme = MaterialTheme.colorScheme
    val fontSize = 54 * fontSize
    val iconSize = 48 * iconSize

    Column(
        modifier = Modifier
            .padding(16.dp)
            .width(150.dp)
            .height(180.dp)
            .background(
                MaterialTheme.colorScheme.surfaceContainerHigh,
                RoundedCornerShape(ShapeRadius.ExtraLarge)
            )
    ) {

        Column(
            Modifier
                .padding(24.dp)
                .fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {

            WeatherIconBox(R.drawable.weather_clear_night, iconSize.dp)

            Gap(8.dp)
            Text(
                "16°",
                color = colorScheme.primary,
                fontSize = fontSize.sp,
                fontWeight = FontWeight.Bold
            )

        }
    }
}