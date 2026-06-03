package com.pranshulgg.weather_master_app.feature.blocks.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.pranshulgg.weather_master_app.R
import com.pranshulgg.weather_master_app.core.ui.components.Gap
import com.pranshulgg.weather_master_app.core.ui.theme.ShadowElevation


@Composable
fun MatBarChart(
    topValues: List<@Composable () -> Any>,
    bottomValues: List<@Composable () -> Any>,
    sideValues: List<Any>,
    values: List<Any>,
    barHeights: List<Int>,
    headerValue: String,
    headerSuffix: String,
    headerTitle: String = stringResource(R.string.text_average_for_day),
    barColor: List<Color> = emptyList(),
    chartHeight: Dp = 200.dp
) {
    Surface(
        color = MaterialTheme.colorScheme.surfaceBright,
        shape = MaterialTheme.shapes.extraLarge,
        shadowElevation = ShadowElevation.level2,
        modifier = Modifier.padding(horizontal = 16.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
        ) {
            Header(headerValue, headerSuffix, headerTitle)
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(chartHeight)
                    .padding(start = 16.dp)
            ) {

                Column() {
                    Row(
                        Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        topValues.forEach { value ->
                            value()
                        }
                    }

                    Column(
                        Modifier
                            .fillMaxHeight()
                            .padding(bottom = 16.dp),
                        verticalArrangement = Arrangement.Bottom
                    ) {
                        Row(
                            verticalAlignment = Alignment.Bottom,
                            horizontalArrangement = Arrangement.spacedBy(5.dp),
                        ) {
                            Column(
                                modifier = Modifier
                                    .height(chartHeight / 1.3f),
                                verticalArrangement = Arrangement.SpaceBetween
                            ) {
                                sideValues.forEach {
                                    Text(
                                        "$it",
                                        style = MaterialTheme.typography.labelMedium,
                                    )
                                }
                            }
                            values.forEachIndexed { index, _ ->
                                Column(
                                    modifier = Modifier
                                        .weight(1f)
                                ) {
                                    ChartBarItem(
                                        height = barHeights[index],
                                        barBackgroundColor = if (barColor.isEmpty()) MaterialTheme.colorScheme.primaryContainer else barColor[index]
                                    )
                                }
                                if (index == values.size - 1) Gap(horizontal = 16.dp)
                            }
                        }
                        Row(
                            Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceEvenly
                        ) {
                            bottomValues.forEach { value ->
                                value()
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun Header(text: String, suffix: String, heading: String) {
    Column(
        modifier = Modifier.padding(top = 18.dp, start = 18.dp, end = 16.dp)
    ) {
        Text(
            heading,
            style = MaterialTheme.typography.headlineSmall,
            color = MaterialTheme.colorScheme.onSurface
        )
        Row() {
            Text(
                text,
                color = MaterialTheme.colorScheme.primary,
                style = MaterialTheme.typography.displayMedium,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.alignByBaseline(),

                )
            Gap(horizontal = 6.dp)
            Text(
                suffix,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                style = MaterialTheme.typography.headlineMedium,
                modifier = Modifier.alignByBaseline(),
            )
        }
    }
}