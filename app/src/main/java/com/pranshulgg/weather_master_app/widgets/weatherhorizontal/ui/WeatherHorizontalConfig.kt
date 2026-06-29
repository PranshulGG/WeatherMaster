package com.pranshulgg.weather_master_app.widgets.weatherhorizontal.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.pranshulgg.weather_master_app.R
import com.pranshulgg.weather_master_app.core.ui.components.Gap
import com.pranshulgg.weather_master_app.core.ui.components.SettingSection
import com.pranshulgg.weather_master_app.core.ui.components.SettingTile
import com.pranshulgg.weather_master_app.core.ui.components.SettingsTileIcon
import com.pranshulgg.weather_master_app.core.ui.components.WeatherIconBox
import com.pranshulgg.weather_master_app.core.ui.theme.ShapeRadius
import com.pranshulgg.weather_master_app.widgets.config.WidgetConfig
import com.pranshulgg.weather_master_app.widgets.model.WidgetVariant
import com.pranshulgg.weather_master_app.widgets.ui.colors.WidgetTheme
import kotlin.math.round
import kotlin.math.roundToInt

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun WeatherHorizontalConfig(onDone: (WidgetConfig) -> Unit = {}) {
    val btnSize = ButtonDefaults.MediumContainerHeight

    var selectedVariant by remember { mutableStateOf(WidgetVariant.LARGE) }
    var selectedFontSize by remember { mutableFloatStateOf(1f) }
    var isTransparent by remember { mutableStateOf(false) }


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
                    WidgetPreview(selectedVariant, selectedFontSize, isTransparent)
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
                    SettingTile.SwitchTile(
                        title = "Transparent",
                        leading = { SettingsTileIcon(R.drawable.opacity_24px) },
                        checked = isTransparent,
                        onCheckedChange = {
                            isTransparent = it
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
                            widgetTheme = if (isTransparent) WidgetTheme.TRANSPARENT else WidgetTheme.AUTO
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
private fun WidgetPreview(variant: WidgetVariant, fontSize: Float, isTransparent: Boolean) {
    when (variant) {
        WidgetVariant.LARGE -> LargeWidgetPreview(fontSize, isTransparent)
        WidgetVariant.COMPACT -> WidgetCompactPreview(fontSize, isTransparent)
        else -> WidgetSmallPreview(fontSize, isTransparent)
    }

}


@Composable
private fun LargeWidgetPreview(fontSize: Float, isTransparent: Boolean) {

    val size = 18 * fontSize
    val fontSizeLocation = 16 * fontSize
    val tempSize = 40 * fontSize

    val textColor = if (isTransparent) Color.White else MaterialTheme.colorScheme.onSurface
    val widgetColor =
        if (isTransparent) Color.Transparent else MaterialTheme.colorScheme.surfaceContainerHigh


    Column(
        Modifier
            .padding(16.dp)
            .fillMaxWidth()
            .height(90.dp)
            .background(
                widgetColor,
                RoundedCornerShape(ShapeRadius.ExtraLarge)
            ),
        verticalArrangement = Arrangement.Center
    ) {
        Row(
            Modifier
                .padding(18.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            WeatherIconBox(R.drawable.weather_clear_day, size = 48.dp)
            Gap(horizontal = 6.dp)
            Column() {
                Text(
                    "Mountain View",
                    fontSize = fontSizeLocation.sp,
                    color = textColor.copy(alpha = 0.8f)
                )
                Text("Clear Sky", fontSize = size.sp, color = textColor)
            }

            Spacer(Modifier.weight(1f))

            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    "28°",
                    fontWeight = FontWeight.Bold,
                    fontSize = tempSize.sp,
                    color = textColor
                )
                Gap(horizontal = 6.dp)
                Column() {
                    Text(
                        "26°",
                        color = textColor,
                        fontWeight = FontWeight.Medium,
                        fontSize = size.sp
                    )
                    Gap(horizontal = 8.dp)
                    Text(
                        "14°",
                        color = textColor.copy(alpha = 0.8f),
                        fontWeight = FontWeight.Medium,
                        fontSize = size.sp
                    )
                }
            }
        }
    }
}


@Composable
private fun WidgetCompactPreview(fontSize: Float, isTransparent: Boolean) {
    val size = 18 * fontSize
    val tempSize = 24 * fontSize

    val textColor = if (isTransparent) Color.White else MaterialTheme.colorScheme.onSurface
    val widgetColor =
        if (isTransparent) Color.Transparent else MaterialTheme.colorScheme.surfaceContainerHigh
    Column(
        Modifier
            .padding(16.dp)
            .fillMaxWidth()
            .height(90.dp)
            .background(
                widgetColor,
                RoundedCornerShape(ShapeRadius.ExtraLarge)
            ),
        verticalArrangement = Arrangement.Center
    ) {
        Row(
            Modifier
                .padding(18.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            WeatherIconBox(R.drawable.weather_clear_day, size = 48.dp)
            Gap(horizontal = 6.dp)
            Column() {
                Text("29°", fontSize = tempSize.sp, color = textColor)
                Text("Clear Sky", fontSize = size.sp, color = textColor)
            }
        }
    }
}

@Composable
private fun WidgetSmallPreview(fontSize: Float, isTransparent: Boolean) {
    val tempSize = 40 * fontSize

    val textColor = if (isTransparent) Color.White else MaterialTheme.colorScheme.onSurface
    val widgetColor =
        if (isTransparent) Color.Transparent else MaterialTheme.colorScheme.surfaceContainerHigh
    Column(
        Modifier
            .padding(16.dp)
            .fillMaxWidth()
            .height(90.dp)
            .background(
                widgetColor,
                RoundedCornerShape(ShapeRadius.ExtraLarge)
            ),
        verticalArrangement = Arrangement.Center
    ) {
        Row(
            Modifier
                .padding(18.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            WeatherIconBox(R.drawable.weather_clear_day, size = 48.dp)
            Gap(horizontal = 6.dp)

            Text("29°", fontSize = tempSize.sp, color = textColor)

        }
    }
}