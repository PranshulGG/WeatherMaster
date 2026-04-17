package com.pranshulgg.weathermaster.core.ui.components.tiles

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Popup

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun DialogSliderTile(
    headline: String,
    description: String? = null,
    initialValue: Float = 0.5f,
    valueRange: ClosedFloatingPointRange<Float> = 0f..1f,
    steps: Int = 0,
    onValueSubmitted: (Float) -> Unit,
    leading: @Composable (() -> Unit)? = null,
    shapes: RoundedCornerShape,
    labelFormatter: (Float) -> String = { it.toString() },
    dialogTitle: String,
    isDescriptionAsValue: Boolean = false,
    itemBgColor: Color
) {
    var showDialog by remember { mutableStateOf(false) }
    var sliderValue by remember { mutableStateOf(initialValue) }

    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = shapes,
    ) {
        ListItem(
            modifier = Modifier.clickable { showDialog = true },
            colors = ListItemDefaults.colors(
                containerColor = itemBgColor
            ),
            leadingContent = leading,
            headlineContent = { Text(headline) },
            supportingContent = {
                if (description != null) {
                    Text(
                        description,
                        color = if (isDescriptionAsValue) MaterialTheme.colorScheme.tertiary else MaterialTheme.colorScheme.onSurfaceVariant
                    )
                } else {
                    Text(
                        text = labelFormatter(sliderValue),
                        color = MaterialTheme.colorScheme.tertiary
                    )
                }
            },
        )
    }

    if (showDialog) {
        AlertDialog(
            onDismissRequest = { showDialog = false },
            title = { Text(dialogTitle) },
            text = {
                Column(
                    modifier = Modifier.fillMaxWidth()
                ) {
                    LabeledSlider(
                        value = sliderValue,
                        onValueChange = { sliderValue = it },
                        valueRange = valueRange,
                        steps = steps,
                        labelFormatter = labelFormatter
                    )
                }
            },
            confirmButton = {

                TextButton(
                    onClick = {
                        onValueSubmitted(sliderValue)
                        showDialog = false
                    },
                    shapes = ButtonDefaults.shapes()
                ) {
                    Text("Save", style = MaterialTheme.typography.labelLarge)
                }
            },
            dismissButton = {
                TextButton(
                    onClick = { showDialog = false },
                    shapes = ButtonDefaults.shapes()
                ) {
                    Text("Cancel", style = MaterialTheme.typography.labelLarge)
                }

            }
        )
    }
}


@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun LabeledSlider(
    value: Float,
    onValueChange: (Float) -> Unit,
    valueRange: ClosedFloatingPointRange<Float>,
    steps: Int,
    labelFormatter: (Float) -> String = { it.toString() }
) {
    Box(
        modifier = Modifier.fillMaxWidth(),
        contentAlignment = Alignment.Center
    ) {
        var sliderWidth by remember { mutableIntStateOf(0) }
        var showLabel by remember { mutableStateOf(false) }

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .onSizeChanged { sliderWidth = it.width }
        ) {
            val interactionSource = remember { MutableInteractionSource() }

            Slider(
                value = value,
                onValueChange = {
                    onValueChange(it)
                    showLabel = true
                },
                valueRange = valueRange,
                steps = steps,
                interactionSource = interactionSource,
                modifier = Modifier.fillMaxWidth()
            )

            val fraction =
                (value - valueRange.start) / (valueRange.endInclusive - valueRange.start)
            val thumbOffsetPx = (fraction * sliderWidth).coerceIn(0f, sliderWidth.toFloat())

            val scale by animateFloatAsState(targetValue = if (showLabel) 1f else 0.8f)
            val alpha by animateFloatAsState(targetValue = if (showLabel) 1f else 0f)

            LaunchedEffect(value) {
                showLabel = true
                kotlinx.coroutines.delay(1000)
                showLabel = false
            }

            Popup(
                offset = IntOffset((thumbOffsetPx - 48).toInt(), -90)
            ) {
                Surface(
                    color = MaterialTheme.colorScheme.inverseSurface,
                    shape = RoundedCornerShape(50.dp),
                    shadowElevation = 2.dp,
                    modifier = Modifier.graphicsLayer {
                        this.scaleX = scale
                        this.scaleY = scale
                        this.alpha = alpha
                    }
                ) {
                    Text(
                        text = labelFormatter(value),
                        modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp),
                        fontSize = 14.sp,
                        color = MaterialTheme.colorScheme.inverseOnSurface
                    )
                }
            }
        }
    }
}
