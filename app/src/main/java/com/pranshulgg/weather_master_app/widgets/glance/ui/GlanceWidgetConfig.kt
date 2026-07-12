package com.pranshulgg.weather_master_app.widgets.glance.ui

import androidx.compose.foundation.layout.Box
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
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
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
import com.pranshulgg.weather_master_app.core.utils.formatters.toSafeDouble
import com.pranshulgg.weather_master_app.widgets.config.WidgetConfig
import com.pranshulgg.weather_master_app.widgets.model.WidgetVariant
import com.pranshulgg.weather_master_app.widgets.ui.colors.WidgetTextTheme
import com.pranshulgg.weather_master_app.widgets.ui.colors.WidgetTheme
import kotlin.math.roundToInt

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun GlanceWidgetConfig(onDone: (WidgetConfig) -> Unit = {}) {

    var clockSize by remember { mutableFloatStateOf(36f) }
    var showClock by remember { mutableStateOf(true) }
    var dateFormat by remember { mutableStateOf("EEE d MMM") }

    val formats = listOf("EEE d MMM", "EEE MMM d", "EEE MM-dd")
    val clockSizes = listOf(
        20f,
        28f,
        36f,
        44f,
        56f,
        72f,
        88f,
        104f,
        120f
    )
    val btnSize = ButtonDefaults.MediumContainerHeight


    val formatsOptions = formats.map { DialogOption(it, it) }
    val clockSizeOptions = clockSizes.map { DialogOption(it.toString(), "${it.roundToInt()}.sp") }

    var widgetTextTheme by remember { mutableStateOf(WidgetTextTheme.WHITE) }
    val widgetTextThemeOptions =
        WidgetTextTheme.entries.filter { it != WidgetTextTheme.AUTO }
            .map { DialogOption(it.toString(), stringResource(it.label)) }


    Scaffold(
        containerColor = MaterialTheme.colorScheme.surfaceContainer
    ) { paddingValues ->
        Column(
            Modifier
                .padding(paddingValues)
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
        ) {
            Surface(
                color = Color.Black,
                shape = RoundedCornerShape(ShapeRadius.ExtraLarge),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                GlanceWidgetPreview(clockSize, showClock, dateFormat, widgetTextTheme)
            }

            SettingSection(
                title = stringResource(R.string.setting_appearance),
                tiles = listOf(
                    SettingTile.DialogOptionTile(
                        leading = { SettingsTileIcon(R.drawable.format_size_24px) },
                        title = "Clock size",
                        options = clockSizeOptions,
                        selectedOption = clockSize.toString(),
                        onOptionSelected = {
                            clockSize = it.toSafeDouble()?.toFloat() ?: 36f
                        }
                    ),
                    SettingTile.SwitchTile(
                        title = "Show clock",
                        leading = { SettingsTileIcon(R.drawable.schedule_48px) },
                        checked = showClock,
                        onCheckedChange = {
                            showClock = it
                        }
                    ),
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
                        leading = { SettingsTileIcon(R.drawable.format_paint_24px) },
                        title = "Widget text color",
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
                            clockSize = clockSize,
                            showClock = showClock,
                            dateFormat = dateFormat,
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

@Composable
private fun GlanceWidgetPreview(
    clockSize: Float = 32f,
    showClock: Boolean = true,
    format: String,
    textTheme: WidgetTextTheme
) {

    val textColor = when (textTheme) {
        WidgetTextTheme.BLACK -> Color.Black
        WidgetTextTheme.WHITE -> Color.White
        else -> Color.White
    }
    val style = TextStyle(
        shadow = Shadow(
            color = Color.Black.copy(alpha = 0.8f),
            blurRadius = 4f,
            offset = Offset(2f, 2f)
        )
    )

    val date = when (format) {
        "EEE d MMM" -> "Thu 18 Jun"
        "EEE MMM d" -> "Thu Jun 18"
        "EEE MM-dd" -> "Thu 06-18"
        else -> "Thu 18 Jun"
    }



    Column(Modifier.padding(16.dp)) {
        if (showClock) {
            Text(
                "03:23",
                fontSize = clockSize.sp,
                color = textColor,
                fontWeight = FontWeight.Bold,
                style = style
            )
        }
        Text(
            date,
            fontSize = 20.sp,
            color = textColor,
            style = style
        )
        Gap(5.dp)
        Row(verticalAlignment = Alignment.CenterVertically) {
            WeatherIconBox(R.drawable.weather_partly_cloudy_day, size = 24.dp)
            Gap(horizontal = 5.dp)
            Text("29° • ", color = textColor, fontSize = 18.sp, style = style)
            Text("Clear sky", color = textColor, fontSize = 18.sp, style = style)
        }
    }
}