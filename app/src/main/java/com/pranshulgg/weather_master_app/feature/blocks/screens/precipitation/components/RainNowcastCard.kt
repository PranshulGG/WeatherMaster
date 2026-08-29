package com.pranshulgg.weather_master_app.feature.blocks.screens.precipitation.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.pranshulgg.weather_master_app.R
import com.pranshulgg.weather_master_app.core.model.weather.PrecipitationUnit
import com.pranshulgg.weather_master_app.core.model.weather.rain.RainNowcast
import com.pranshulgg.weather_master_app.core.model.weather.rain.RainNowcastPoint
import com.pranshulgg.weather_master_app.core.model.weather.toName
import com.pranshulgg.weather_master_app.core.prefs.LocalAppPrefs
import com.pranshulgg.weather_master_app.core.ui.components.Gap
import com.pranshulgg.weather_master_app.core.ui.theme.ShadowElevation
import com.pranshulgg.weather_master_app.feature.shared.components.CardsHeader
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import kotlin.math.max
import kotlin.math.roundToInt

private const val NOWCAST_PAST_CONTEXT_MINUTES = 15L

/** Intensity color ramp modeled on buienradar's legend (mm/h). */
private fun barColor(intensityMmPerHour: Double): Color = when {
    intensityMmPerHour <= 0.0 -> Color(0xFFBDBDBD)   // no rain
    intensityMmPerHour < 0.3 -> Color(0xFFA9D5F5)    // drizzle
    intensityMmPerHour < 1.0 -> Color(0xFF6FB6EC)    // light
    intensityMmPerHour < 3.0 -> Color(0xFF3E8FE0)    // moderate
    intensityMmPerHour < 10.0 -> Color(0xFF2265C4)   // heavy
    intensityMmPerHour < 25.0 -> Color(0xFF1B3FA0)   // very heavy
    else -> Color(0xFF6A1B9A)                        // extreme
}

/**
 * Buienradar-style chart for the high-resolution rain nowcast: one bar per
 * 5-minute (radar) or 15-minute (model) interval, colored by intensity class
 * like buienradar.nl, with past bars dimmed and upcoming full-hour labels.
 */
@Composable
fun RainNowcastCard(
    nowcast: RainNowcast?,
    zoneId: String,
    unit: PrecipitationUnit
) {
    if (nowcast == null) return
    val context = LocalContext.current
    val prefs = LocalAppPrefs.current

    val zone = remember(zoneId) { ZoneId.of(zoneId) }
    val now = remember(nowcast.points) { System.currentTimeMillis() }

    val maxIntensity = max(nowcast.maxIntensity(), 0.5)

    val timeFormatter: (Long) -> String = { millis ->
        val zoned = Instant.ofEpochMilli(millis).atZone(zone)
        if (prefs.is24HrTimeFormat) {
            zoned.format(DateTimeFormatter.ofPattern("HH:mm"))
        } else {
            zoned.format(DateTimeFormatter.ofPattern("h:mm a"))
        }
    }

    val maxMmPerHour = nowcast.maxIntensity()
    val summaryIntensity = when {
        maxMmPerHour <= 0.0 -> stringResource(R.string.rain_nowcast_none_expected)
        maxMmPerHour < 1.0 -> stringResource(R.string.rain_nowcast_light)
        maxMmPerHour < 3.0 -> stringResource(R.string.rain_nowcast_moderate)
        maxMmPerHour < 10.0 -> stringResource(R.string.rain_nowcast_heavy)
        else -> stringResource(R.string.rain_nowcast_extreme)
    }

    Surface(
        color = MaterialTheme.colorScheme.surfaceBright,
        shape = MaterialTheme.shapes.extraLarge,
        shadowElevation = ShadowElevation.level2,
        modifier = Modifier.padding(horizontal = 16.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp)
        ) {
            CardsHeader(
                "${stringResource(R.string.rain_nowcast_title)} (${
                    unit.toName(context, true)
                })",
                R.drawable.water_drop_24px
            )
            Gap(6.dp)

            Text(
                text = summaryIntensity,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(horizontal = 16.dp)
            )

            Gap(10.dp)

            NowcastBarChart(
                points = nowcast.points,
                nowMilli = now,
                maxIntensity = maxIntensity
            )

            Gap(6.dp)

            // Time axis: "now" plus the next three full hours
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    timeFormatter(now),
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.SemiBold
                )
                var nextHour = Instant.ofEpochMilli(now).atZone(zone)
                    .plusMinutes((60 - Instant.ofEpochMilli(now).atZone(zone).minute).toLong())
                    .withSecond(0).withNano(0)
                repeat(3) {
                    Text(
                        timeFormatter(nextHour.toInstant().toEpochMilli()),
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    nextHour = nextHour.plusHours(1)
                }
            }

            Gap(10.dp)

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                NowcastLegendItem(Color(0xFFBDBDBD), stringResource(R.string.rain_nowcast_legend_none))
                NowcastLegendItem(Color(0xFF6FB6EC), stringResource(R.string.rain_nowcast_legend_light))
                NowcastLegendItem(Color(0xFF3E8FE0), stringResource(R.string.rain_nowcast_legend_moderate))
                NowcastLegendItem(Color(0xFF2265C4), stringResource(R.string.rain_nowcast_legend_heavy))
                NowcastLegendItem(Color(0xFF6A1B9A), stringResource(R.string.rain_nowcast_legend_extreme))
            }

            Gap(6.dp)
            Text(
                text = stringResource(R.string.rain_nowcast_provider, nowcast.providerName),
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(horizontal = 16.dp)
            )
        }
    }
}

@Composable
private fun NowcastLegendItem(color: Color, label: String) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Surface(
            color = color,
            shape = RoundedCornerShape(3.dp),
            modifier = Modifier.size(10.dp)
        ) {}
        Gap(3.dp)
        Text(
            label,
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

/**
 * One bar per 5 minutes from (now - 15 min) to the end of the data.
 * 5-minute radar points map 1:1 to bars; 15-minute model points are
 * expanded across their interval so the tail stays continuous.
 */
@Composable
private fun NowcastBarChart(
    points: List<RainNowcastPoint>,
    nowMilli: Long,
    maxIntensity: Double
) {
    val startMilli = nowMilli - NOWCAST_PAST_CONTEXT_MINUTES * 60000L
    val slotMillis = 5 * 60000L

    val bars = remember(points, nowMilli) {
        val list = mutableListOf<Triple<Long, Double, Boolean>>() // time, intensity, isPast
        for (p in points) {
            val end = p.time + p.intervalMinutes * 60000L
            var s = max(p.time, startMilli)
            while (s < end) {
                list.add(Triple(s, p.intensityMmPerHour, s < nowMilli))
                s += slotMillis
            }
        }
        list
    }

    if (bars.isEmpty()) return

    val maxBarHeight = 110

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(130.dp)
            .padding(horizontal = 16.dp),
        verticalAlignment = Alignment.Bottom,
        horizontalArrangement = Arrangement.spacedBy(2.dp)
    ) {
        bars.forEach { (time, intensity, isPast) ->
            val fraction = (intensity / maxIntensity).coerceIn(0.0, 1.0)
            val height = if (intensity <= 0.0) 4 else (fraction * maxBarHeight).roundToInt().coerceAtLeast(6)
            val color = barColor(intensity)
            val alpha = if (isPast) 0.45f else 1f

            Box(Modifier.weight(1f)) {
                Surface(
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .fillMaxWidth()
                        .height(height.dp),
                    color = color.copy(alpha = alpha),
                    shape = MaterialTheme.shapes.extraSmall
                ) {}
            }
        }
    }
}