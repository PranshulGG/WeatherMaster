package com.pranshulgg.weather_master_app.feature.blocks.screens.precipitation.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.gestures.awaitEachGesture
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.layout.fillMaxHeight
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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.AwaitPointerEventScope
import androidx.compose.ui.input.pointer.PointerEventPass
import androidx.compose.ui.input.pointer.PointerInputScope
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import com.pranshulgg.weather_master_app.R
import com.pranshulgg.weather_master_app.core.model.weather.PrecipitationUnit
import com.pranshulgg.weather_master_app.core.model.weather.rain.RainNowcast
import com.pranshulgg.weather_master_app.core.model.weather.rain.RainNowcastPoint
import com.pranshulgg.weather_master_app.core.model.weather.toName
import com.pranshulgg.weather_master_app.core.prefs.LocalAppPrefs
import com.pranshulgg.weather_master_app.core.ui.components.Gap
import com.pranshulgg.weather_master_app.core.ui.theme.ShadowElevation
import com.pranshulgg.weather_master_app.core.utils.formatters.formatLocalizedNumber
import com.pranshulgg.weather_master_app.core.utils.locale.getCurrentAppLocale
import com.pranshulgg.weather_master_app.feature.shared.components.CardsHeader
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import kotlin.math.abs
import kotlin.math.ceil
import kotlin.math.max

private const val NOWCAST_PAST_CONTEXT_MINUTES = 15L

/**
 * True while a finger is scrubbing across the nowcast bars. Read by the main
 * screen to disable the navigation drawer's pull-in gesture for the duration
 * of the drag, so dragging left/right on the chart doesn't open the drawer.
 */
object NowcastDragState {
    var isDragging by mutableStateOf(false)
        private set

    internal fun set(value: Boolean) {
        isDragging = value
    }
}

/** Intensity color ramp modeled on buienradar's legend (mm/h). */
internal fun barColor(intensityMmPerHour: Double): Color = when {
    intensityMmPerHour <= 0.0 -> Color(0xFFBDBDBD)   // no rain
    intensityMmPerHour < 0.3 -> Color(0xFFA9D5F5)    // drizzle
    intensityMmPerHour < 1.0 -> Color(0xFF6FB6EC)    // light
    intensityMmPerHour < 3.0 -> Color(0xFF3E8FE0)    // moderate
    intensityMmPerHour < 10.0 -> Color(0xFF2265C4)   // heavy
    intensityMmPerHour < 25.0 -> Color(0xFF1B3FA0)   // very heavy
    else -> Color(0xFF6A1B9A)                        // extreme
}

/** Nice step for y-axis ticks given the max intensity (mm/h). */
private fun yAxisStep(maxIntensity: Double): Double = when {
    maxIntensity <= 1.0 -> 0.25
    maxIntensity <= 2.5 -> 0.5
    maxIntensity <= 5.0 -> 1.0
    maxIntensity <= 12.0 -> 2.0
    maxIntensity <= 30.0 -> 5.0
    maxIntensity <= 60.0 -> 10.0
    else -> 25.0
}

/**
 * Buienradar-style chart for the high-resolution rain nowcast: one bar per
 * 5-minute (radar) or 15-minute (model) interval, with a y-axis, hour
 * separators and tap-to-inspect: tapping a bar selects it and shows the
 * exact time and intensity (converted to the user's precipitation unit).
 */
@Composable
fun RainNowcastCard(
    nowcast: RainNowcast?,
    zoneId: String,
    unit: PrecipitationUnit,
    modifier: Modifier = Modifier
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

    val formatter: (Double) -> Double? = {
        PrecipitationUnit.MM.convert(it, unit)
    }

    val maxMmPerHour = nowcast.maxIntensity()

    Surface(
        color = MaterialTheme.colorScheme.surfaceBright,
        shape = MaterialTheme.shapes.extraLarge,
        shadowElevation = ShadowElevation.level2,
        modifier = modifier
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

            NowcastBarChart(
                points = nowcast.points,
                nowMilli = now,
                maxIntensity = maxIntensity,
                unit = unit,
                timeFormatter = timeFormatter,
                formatValue = { mmh ->
                    val converted = formatter(mmh)
                    val decimals = when {
                        unit == PrecipitationUnit.INCH -> 3
                        maxMmPerHour < 1.0 -> 2
                        else -> 1
                    }
                    formatLocalizedNumber(
                        getCurrentAppLocale(),
                        converted ?: mmh,
                        decimals
                    )
                }
            )

            Gap(6.dp)

            // Time axis: current time plus upcoming full-hour labels
            TimeAxis(nowMilli = now, zone = zone, timeFormatter = timeFormatter)

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
 * One bar per 5 minutes from (now - 15 min) to the end of the data, with a
 * y-axis on the left. 5-minute radar points map 1:1 to bars; 15-minute model
 * points are expanded across their interval so the tail stays continuous.
 * Tapping a bar selects it and shows the exact time + intensity above.
 */
@Composable
private fun NowcastBarChart(
    points: List<RainNowcastPoint>,
    nowMilli: Long,
    maxIntensity: Double,
    unit: PrecipitationUnit,
    timeFormatter: (Long) -> String,
    formatValue: (Double) -> String
) {
    val startMilli = nowMilli - NOWCAST_PAST_CONTEXT_MINUTES * 60000L
    val slotMillis = 5 * 60000L

    data class Bar(
        val time: Long,
        val intensity: Double,
        val isPast: Boolean,
        val timeLabel: String
    )

    val bars = remember(points, nowMilli) {
        val list = mutableListOf<Bar>()
        for (p in points) {
            val end = p.time + p.intervalMinutes * 60000L
            var s = max(p.time, startMilli)
            while (s < end) {
                list.add(
                    Bar(
                        time = s,
                        intensity = p.intensityMmPerHour,
                        isPast = s < nowMilli,
                        timeLabel = timeFormatter(s)
                    )
                )
                s += slotMillis
            }
        }
        list
    }

    if (bars.isEmpty()) return

    val density = LocalDensity.current

    // selection state: which bar index is inspected; -1 = none.
    // Set by a tap, or by pressing-and-dragging across the chart (scrubbing);
    // cleared when the finger is lifted.
    var selectedIndex by remember(points, nowMilli) { mutableIntStateOf(-1) }
    val activeIndex = if (selectedIndex in bars.indices) selectedIndex else -1
    val selected = bars.getOrNull(activeIndex)
    var barsSize by remember { mutableStateOf(IntSize.Zero) }

    // gap between bars in(px), resolved once per composition from the density
    val barGapPx = with(density) { 2.dp.toPx() }

    fun barIndexAt(x: Float): Int {
        if (bars.isEmpty() || barsSize.width == 0) return -1
        // bars row is spacedBy(2.dp); bar k spans [k*(w+gap), k*(w+gap)+w)
        val slot = barsSize.width.toDouble() / bars.size
        val barWidth = slot - barGapPx
        if (x < 0 || barWidth <= 0) return -1
        val idx = (x / (barWidth + barGapPx)).toInt()
        return if (idx in bars.indices) idx else -1
    }

    val maxBarHeight = 120
    val step = yAxisStep(maxIntensity)
    val topOfScale = ceil(maxIntensity / step) * step
    // decimals for tick/tooltip labels: sub-1 mm/h needs 2, INCH needs 3
    val tickDecimals = when {
        unit == PrecipitationUnit.INCH -> 3
        step < 1.0 -> 2
        else -> 1
    }
    val formatTick: (Double) -> String = { mmh ->
        val converted = PrecipitationUnit.MM.convert(mmh, unit)
        formatLocalizedNumber(getCurrentAppLocale(), converted ?: mmh, tickDecimals)
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
    ) {
        // ---- y-axis labels -------------------------------------------------
        Column(
            modifier = Modifier
                .width(30.dp)
                .height((maxBarHeight + 20).dp),
            verticalArrangement = Arrangement.SpaceBetween,
            horizontalAlignment = Alignment.End
        ) {
            val tickCount = (topOfScale / step).toInt() + 1
            for (i in tickCount - 1 downTo 0) {
                val value = i * step
                Text(
                    text = formatTick(value),
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    textAlign = TextAlign.End
                )
            }
        }
        Gap(4.dp)

        // ---- bars ----------------------------------------------------------
        val tooltipContext = LocalContext.current
        Column {
            // selection tooltip row (fixed height so layout doesn't jump),
            // aligned to the top-left as requested
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(20.dp),
                contentAlignment = Alignment.TopStart
            ) {
                if (selected != null) {
                    Text(
                        "${selected.timeLabel} · ${formatValue(selected.intensity)} ${unit.toName(tooltipContext, true)}",
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.primary,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(maxBarHeight.dp)
                    .onSizeChanged { barsSize = it }
                    .pointerInput(bars) {
                        awaitEachGesture {
                            val down = awaitFirstDown(pass = PointerEventPass.Initial)
                            // Observe the first horizontal move before children
                            // see it; once the finger moves horizontally we
                            // claim the drag: mark scrubbing active and consume
                            // all subsequent events so the drawer edge-swipe
                            // and any scroll never take over.
                            var scrubbing = false
                            selectedIndex = barIndexAt(down.position.x)
                            while (true) {
                                val event = awaitPointerEvent(PointerEventPass.Initial)
                                if (event.changes.isEmpty()) break
                                val change = event.changes.first()
                                if (change.pressed) {
                                    if (!scrubbing && !(change.position.x - down.position.x).let { abs(it) < 4f }) {
                                        scrubbing = true
                                        NowcastDragState.set(true)
                                    }
                                    if (scrubbing) {
                                        selectedIndex = barIndexAt(change.position.x)
                                        event.changes.forEach { it.consume() }
                                    }
                                } else {
                                    // finger lifted: clear selection
                                    selectedIndex = -1
                                    break
                                }
                            }
                            if (scrubbing) NowcastDragState.set(false)
                        }
                    },
                verticalAlignment = Alignment.Bottom,
                horizontalArrangement = Arrangement.spacedBy(2.dp)
            ) {
                bars.forEachIndexed { index, bar ->
                    val fraction = (bar.intensity / topOfScale).coerceIn(0.0, 1.0)
                    val height = if (bar.intensity <= 0.0) 4 else (fraction * maxBarHeight).toInt().coerceAtLeast(6)
                    val alpha = when {
                        bar.isPast -> 0.45f
                        index == activeIndex -> 1f
                        activeIndex != -1 -> 0.55f // dim others while inspecting
                        else -> 1f
                    }

                    Box(
                        Modifier
                            .weight(1f)
                            .fillMaxHeight()
                    ) {
                        Surface(
                            modifier = Modifier
                                .align(Alignment.BottomCenter)
                                .fillMaxWidth()
                                .height(height.dp),
                            color = barColor(bar.intensity).copy(alpha = alpha),
                            shape = MaterialTheme.shapes.extraSmall
                        ) {}
                    }
                }
            }
        }
    }
}

/**
 * "now" label on the left, the upcoming full-hour labels spread after it.
 */
@Composable
private fun TimeAxis(
    nowMilli: Long,
    zone: java.time.ZoneId,
    timeFormatter: (Long) -> String
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 46.dp), // 16 chart padding + 30 y-axis
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            timeFormatter(nowMilli),
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.primary,
            fontWeight = FontWeight.SemiBold
        )
        val zoneNow = Instant.ofEpochMilli(nowMilli).atZone(zone)
        var nextHour = zoneNow.plusMinutes((60 - zoneNow.minute).toLong())
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
}