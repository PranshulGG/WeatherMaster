package com.pranshulgg.weather_master_app.feature.settings.appearance.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.core.graphics.toColorInt
import com.pranshulgg.weather_master_app.core.prefs.LocalAppPrefs
import com.pranshulgg.weather_master_app.core.ui.components.ActionBottomSheet

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ColorPickerBtn() {

    val prefs = LocalAppPrefs.current
    var selectedColor = prefs.customThemeColor
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    var isSheetOpen by remember { mutableStateOf(false) }

    Surface(
        shape = RoundedCornerShape(50.dp),
        border = BorderStroke(width = 1.dp, color = MaterialTheme.colorScheme.outlineVariant)
    ) {
        Box(
            modifier = Modifier
                .width(24.dp)
                .height(36.dp)
                .background(
                    color = Color(prefs.customThemeColor.toColorInt())
                )
                .clickable(
                    onClick = {
                        isSheetOpen = true
                    }
                ),
        ) {
        }
    }

    if (isSheetOpen)
        ActionBottomSheet(
            sheetState = sheetState,
            onCancel = {
                isSheetOpen = false
            },
            onConfirm = {
                prefs.setCustomThemeColor(selectedColor)
            },
        ) {
            SelectableThemeColors(onThemeColorChanged = { color ->
                selectedColor = color
            })
        }
}