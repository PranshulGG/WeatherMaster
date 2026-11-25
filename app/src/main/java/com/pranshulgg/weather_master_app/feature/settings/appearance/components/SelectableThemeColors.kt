package com.pranshulgg.weather_master_app.feature.settings.appearance.components

import androidx.annotation.StringRes
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.MaterialShapes
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MaterialTheme.motionScheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.ToggleButton
import androidx.compose.material3.ToggleButtonDefaults
import androidx.compose.material3.toShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.role
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import androidx.core.graphics.toColorInt
import com.pranshulgg.weather_master_app.R
import com.pranshulgg.weather_master_app.core.prefs.LocalAppPrefs
import com.pranshulgg.weather_master_app.core.ui.theme.ThemeVariantType


private class ThemeColor(val hex: String, @StringRes val name: Int, val isAccent: Boolean = false)

private val scheme = listOf(
    ThemeColor("#f44336", R.string.color_red),
    ThemeColor("#ff5252", R.string.color_red, isAccent = true),
    ThemeColor("#e91e63", R.string.color_pink),
    ThemeColor("#ff4081", R.string.color_pink, isAccent = true),
    ThemeColor("#9c27b0", R.string.color_purple),
    ThemeColor("#e040fb", R.string.color_purple, isAccent = true),
    ThemeColor("#673ab7", R.string.color_deep_purple),
    ThemeColor("#7c4dff", R.string.color_deep_purple, isAccent = true),
    ThemeColor("#3f51b5", R.string.color_indigo),
    ThemeColor("#536dfe", R.string.color_indigo, isAccent = true),
    ThemeColor("#2196f3", R.string.color_blue),
    ThemeColor("#448aff", R.string.color_blue, isAccent = true),
    ThemeColor("#03a9f4", R.string.color_light_blue),
    ThemeColor("#40c4ff", R.string.color_light_blue, isAccent = true),
    ThemeColor("#00bcd4", R.string.color_cyan),
    ThemeColor("#18ffff", R.string.color_cyan, isAccent = true),
    ThemeColor("#009688", R.string.color_teal),
    ThemeColor("#64ffda", R.string.color_teal, isAccent = true),
    ThemeColor("#4caf50", R.string.color_green),
    ThemeColor("#69f0ae", R.string.color_green, isAccent = true),
    ThemeColor("#8bc34a", R.string.color_light_green),
    ThemeColor("#b2ff59", R.string.color_light_green, isAccent = true),
    ThemeColor("#cddc39", R.string.color_lime),
    ThemeColor("#eeff41", R.string.color_lime, isAccent = true),
    ThemeColor("#ffeb3b", R.string.color_yellow),
    ThemeColor("#ffff00", R.string.color_yellow, isAccent = true),
    ThemeColor("#ffc107", R.string.color_amber),
    ThemeColor("#ffd740", R.string.color_amber, isAccent = true),
    ThemeColor("#ff9800", R.string.color_orange),
    ThemeColor("#ffab40", R.string.color_orange, isAccent = true),
    ThemeColor("#ff5722", R.string.color_deep_orange),
    ThemeColor("#ff6e40", R.string.color_deep_orange, isAccent = true),
    ThemeColor("#795548", R.string.color_brown),
    ThemeColor("#607d8b", R.string.color_blue_grey),
    ThemeColor("#9e9e9e", R.string.color_grey),
);

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun SelectableThemeColors(onThemeColorChanged: (String) -> Unit) {
    val prefs = LocalAppPrefs.current
    val currentMotionScheme = motionScheme
    val motionScheme = remember(currentMotionScheme) { currentMotionScheme }
    var currentSelectedColor by remember { mutableStateOf(prefs.customThemeColor) }


    Column(
        modifier = Modifier
            .verticalScroll(rememberScrollState())
            .padding(12.dp)
            .fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        FlowRow(
            modifier = Modifier.selectableGroup(),
            maxItemsInEachRow = 7,
            horizontalArrangement = Arrangement.spacedBy(6.dp),
            verticalArrangement = Arrangement.spacedBy(6.dp),
        ) {
            scheme.forEach { themeColor ->
                val hex = themeColor.hex
                val isSelected = currentSelectedColor == hex
                val colorName = stringResource(themeColor.name)
                val label = if (themeColor.isAccent) stringResource(R.string.color_accent, colorName) else colorName

                val scale by animateFloatAsState(
                    targetValue = if (isSelected) 1.15f else 1f,
                    label = "scale",
                    animationSpec = motionScheme.defaultSpatialSpec()
                )

                val alpha by animateFloatAsState(
                    targetValue = if (isSelected) 1f else 0.85f,
                    label = "alpha",
                )

                Surface(
                    modifier = Modifier
                        .size(40.dp)
                        .graphicsLayer {
                            scaleX = scale
                            scaleY = scale
                            this.alpha = alpha
                        }
                        .selectable(
                            selected = isSelected,
                            indication = null,
                            interactionSource = remember { MutableInteractionSource() },
                            role = Role.RadioButton
                        ) {
                            currentSelectedColor = hex
                            onThemeColorChanged(hex)
                        }
                        .semantics { contentDescription = label },
                    border = BorderStroke(
                        width = 1.dp,
                        color = MaterialTheme.colorScheme.outlineVariant
                    ),
                    color = Color(hex.toColorInt()),
                    shape = if (isSelected)
                        MaterialShapes.Cookie4Sided.toShape()
                    else
                        CircleShape
                ) {}
            }
        }
        Box(Modifier.height(16.dp))

        val options = listOf(
            "Tonal Spot" to ThemeVariantType.TONAL_SPOT,
            "Neutral" to ThemeVariantType.NEUTRAL,
            "Vibrant" to ThemeVariantType.VIBRANT,
            "Expressive" to ThemeVariantType.EXPRESSIVE,
        )


        var selected by remember { mutableStateOf(prefs.themeVariantType) }

        FlowRow(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp),
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            options.forEach { (label, variant) ->
                ToggleButton(
                    checked = selected == variant,
                    onCheckedChange = {
                        prefs.setThemeVariantType(variant)
                        selected = variant
                    },
                    modifier = Modifier.semantics { role = Role.RadioButton },
                    colors = ToggleButtonDefaults.toggleButtonColors(
                        checkedContainerColor = MaterialTheme.colorScheme.tertiary,
                        checkedContentColor = MaterialTheme.colorScheme.onTertiary,
                        containerColor = MaterialTheme.colorScheme.surfaceContainerHigh,
                    )
                ) {
                    Text(label)
                }
            }
        }

    }
}