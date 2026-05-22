package com.pranshulgg.weather_master_app.feature.settings.appearance.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
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
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.role
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import androidx.core.graphics.toColorInt
import com.pranshulgg.weather_master_app.core.prefs.LocalAppPrefs
import com.pranshulgg.weather_master_app.core.ui.theme.ThemeVariantType


private val scheme = listOf(
    "#f44336",
    "#ff5252",
    "#e91e63",
    "#ff4081",
    "#9c27b0",
    "#e040fb",
    "#673ab7",
    "#7c4dff",
    "#3f51b5",
    "#536dfe",
    "#2196f3",
    "#448aff",
    "#03a9f4",
    "#40c4ff",
    "#00bcd4",
    "#18ffff",
    "#009688",
    "#64ffda",
    "#4caf50",
    "#69f0ae",
    "#8bc34a",
    "#b2ff59",
    "#cddc39",
    "#eeff41",
    "#ffeb3b",
    "#ffff00",
    "#ffc107",
    "#ffd740",
    "#ff9800",
    "#ffab40",
    "#ff5722",
    "#ff6e40",
    "#795548",
    "#607d8b",
    "#9e9e9e",
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
            maxItemsInEachRow = 7,
            horizontalArrangement = Arrangement.spacedBy(6.dp),
            verticalArrangement = Arrangement.spacedBy(6.dp),
        ) {
            scheme.forEach { hex ->
                val isSelected = currentSelectedColor == hex

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
                        .clickable(
                            indication = null,
                            interactionSource = remember { MutableInteractionSource() }
                        ) {
                            currentSelectedColor = hex
                            onThemeColorChanged(hex)
                        },
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
                    shapes = ToggleButtonDefaults.shapes(),
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