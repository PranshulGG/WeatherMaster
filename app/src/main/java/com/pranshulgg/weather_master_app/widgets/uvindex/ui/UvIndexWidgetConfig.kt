package com.pranshulgg.weather_master_app.widgets.uvindex.ui

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
import androidx.compose.foundation.layout.size
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
import com.pranshulgg.weather_master_app.R
import com.pranshulgg.weather_master_app.core.model.weather.uv.UvIndex
import com.pranshulgg.weather_master_app.core.model.weather.uv.toColor
import com.pranshulgg.weather_master_app.core.model.weather.uv.toTextColor
import com.pranshulgg.weather_master_app.core.ui.components.Gap
import com.pranshulgg.weather_master_app.core.ui.theme.ShapeRadius
import com.pranshulgg.weather_master_app.widgets.config.WidgetConfig
import com.pranshulgg.weather_master_app.widgets.model.WidgetVariant


@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun UvIndexWidgetConfig(onDone: (WidgetConfig) -> Unit = {}) {
    val btnSize = ButtonDefaults.MediumContainerHeight

    var selectedVariant by remember { mutableStateOf(WidgetVariant.LARGE) }


    val variantFiltered = listOf(WidgetVariant.LARGE, WidgetVariant.COMPACT)

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
                variantFiltered.forEach { item ->
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


            Gap(12.dp)
            Text(
                text = "Make sure your currently selected source provides UV index, as some sources do not",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(horizontal = 16.dp)
            )

            Spacer(Modifier.weight(1f))
            Button(
                onClick = {
                    onDone(
                        WidgetConfig(
                            variant = selectedVariant,
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
    variant: WidgetVariant,
) {


    when (variant) {
        WidgetVariant.LARGE -> GlanceWidgetLargePreview(
        )

        else -> GlanceWidgetCompactPreview(
        )
    }
}

@Composable
private fun GlanceWidgetLargePreview(
) {


    Column(
        modifier = Modifier
            .padding(16.dp)
            .size(200.dp)
            .background(
                UvIndex.MODERATE.toColor(),
                RoundedCornerShape(ShapeRadius.ExtraLarge)
            )
    ) {
        Column(Modifier.padding(16.dp)) {
            Text("Current", color = Color(0xFF4A3900), fontSize = 20.sp)
            Text(
                "Moderate",
                color = UvIndex.MODERATE.toTextColor(),
                fontSize = 22.sp,
                fontWeight = FontWeight.Medium
            )
            Text(
                "4",
                color = UvIndex.MODERATE.toTextColor(),
                fontSize = 48.sp,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.weight(1f))
            Text("Today's max: 8", color = Color(0xFF4A3900), fontSize = 18.sp)
            Text("At around 12:00PM", color = Color(0xFF4A3900), fontSize = 16.sp)
        }

    }
}

@Composable
private fun GlanceWidgetCompactPreview(
) {
    Column(
        modifier = Modifier
            .padding(16.dp)
            .width(250.dp)
            .height(100.dp)
            .background(
                UvIndex.MODERATE.toColor(),
                RoundedCornerShape(ShapeRadius.ExtraLarge)
            ),
        verticalArrangement = Arrangement.Center,
    ) {
        Text(
            "Current",
            color = Color(0xFF4A3900),
            fontSize = 20.sp,
            modifier = Modifier.padding(start = 16.dp)
        )
        Gap(3.dp)
        Row(verticalAlignment = Alignment.Bottom, modifier = Modifier.padding(start = 16.dp)) {
            Text(
                "4",
                color = UvIndex.MODERATE.toTextColor(),
                fontSize = 32.sp,
                fontWeight = FontWeight.Bold
            )
            Gap(horizontal = 5.dp)
            Text(
                "Moderate",
                color = UvIndex.MODERATE.toTextColor(),
                fontSize = 18.sp,
                fontWeight = FontWeight.Medium
            )
        }
    }
}