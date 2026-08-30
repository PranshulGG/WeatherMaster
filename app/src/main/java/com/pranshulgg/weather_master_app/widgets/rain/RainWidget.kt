package com.pranshulgg.weather_master_app.widgets.rain

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Paint
import android.os.Build
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.glance.ColorFilter
import androidx.glance.GlanceId
import androidx.glance.GlanceModifier
import androidx.glance.GlanceTheme
import androidx.glance.Image
import androidx.glance.ImageProvider
import androidx.glance.LocalSize
import androidx.glance.action.ActionParameters
import androidx.glance.action.actionStartActivity
import androidx.glance.action.clickable
import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.appwidget.SizeMode
import androidx.glance.appwidget.action.ActionCallback
import androidx.glance.appwidget.action.actionRunCallback
import androidx.glance.appwidget.cornerRadius
import androidx.glance.appwidget.provideContent
import androidx.glance.background
import androidx.glance.currentState
import androidx.glance.layout.Alignment
import androidx.glance.layout.Box
import androidx.glance.layout.Column
import androidx.glance.layout.Row
import androidx.glance.layout.Spacer
import androidx.glance.layout.fillMaxWidth
import androidx.glance.layout.height
import androidx.glance.layout.padding
import androidx.glance.layout.size
import androidx.glance.layout.width
import androidx.glance.text.FontWeight
import androidx.glance.text.Text
import androidx.glance.text.TextAlign
import androidx.glance.text.TextStyle
import com.pranshulgg.weather_master_app.MainActivity
import com.pranshulgg.weather_master_app.R
import com.pranshulgg.weather_master_app.core.model.weather.PrecipitationUnit
import com.pranshulgg.weather_master_app.core.utils.formatters.to12HourTimeString
import com.pranshulgg.weather_master_app.core.utils.formatters.to24HourTimeString
import com.pranshulgg.weather_master_app.data.worker.widgets.WidgetReload
import com.pranshulgg.weather_master_app.data.worker.widgets.nowcastBarColorArgb
import com.pranshulgg.weather_master_app.widgets.WeatherWidgetStateDefinition
import com.pranshulgg.weather_master_app.widgets.WeatherWidgetStateJson
import com.pranshulgg.weather_master_app.widgets.model.RainWidgetState
import com.pranshulgg.weather_master_app.widgets.model.WidgetWeather
import com.pranshulgg.weather_master_app.widgets.ui.ReloadButton
import com.pranshulgg.weather_master_app.widgets.ui.WidgetReloadEntryPoint
import dagger.hilt.EntryPoint
import dagger.hilt.InstallIn
import dagger.hilt.android.EntryPointAccessors
import dagger.hilt.components.SingletonComponent
import jakarta.inject.Inject
import kotlinx.serialization.json.Json
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import kotlin.math.ceil

/**
 * Homescreen widget showing the rain bar chart for the default / home
 * location: the high-resolution nowcast when it has coverage, otherwise the
 * hourly precipitation forecast (the in-app fallback).
 *
 * Rendered with [SizeMode.Exact] plus a [android.graphics.Bitmap] chart so
 * the number of ticks can scale with the widget width: wider widgets use
 * finer buckets and show more data instead of stretching the same bars.
 */
class RainWidget : GlanceAppWidget() {

    override val sizeMode = SizeMode.Exact
    override val stateDefinition = WeatherWidgetStateDefinition

    override suspend fun provideGlance(
        context: Context,
        id: GlanceId
    ) {
        provideContent {
            val appState = currentState<WeatherWidgetStateJson>()
            val rainState = appState.rainJson?.let {
                try {
                    Json.decodeFromString<RainWidgetState>(it)
                } catch (e: Exception) {
                    null
                }
            }
            // Current-conditions snapshot (temp, icon, wind, city) shared with
            // the main weather widget; may be absent right after install.
            val weatherState = appState.json?.let {
                try {
                    Json.decodeFromString<WidgetWeather>(it)
                } catch (e: Exception) {
                    null
                }
            }

            Box(
                GlanceModifier
                    .fillMaxWidth()
                    .appWidgetBackgroundShape()
                    .clickable(actionStartActivity<MainActivity>())
            ) {
                if (rainState != null && rainState.hasData()) {
                    RainWidgetContent(context, rainState, weatherState)
                } else {
                    ReloadButton()
                }
            }
        }
    }
}

@Composable
private fun RainWidgetContent(
    context: Context,
    state: RainWidgetState,
    weatherState: WidgetWeather?
) {
    Column(
        modifier = GlanceModifier.fillMaxWidth().padding(horizontal = 12.dp, vertical = 10.dp)
    ) {
        // On the narrowest widget sizes the promoted header cluster gets
        // crammed and the single line would truncate (losing the wind info);
        // the city name is the first thing to drop there. ~300dp already
        // truncates on this layout, 480dp fits comfortably.
        val showCityName = LocalSize.current.width.value >= 340f

        // Header without a widget title: rain icon, then all info text
        // left-bound (city · temp · condition icon · wind, promoted to
        // title-like bold text); only the source attribution + refresh
        // button are pinned to the right edge. When no current-conditions
        // snapshot exists yet, fall back to the plain "Rain (mm)" label so
        // the header is never empty.
        if (weatherState != null) {
            Row(
                modifier = GlanceModifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Left cluster, wrapped in a nested Row: a Glance Row drops
                // trailing children when it has >10 direct children, which
                // silently removed the refresh button in the flat layout.
                // Left cluster + weighted spacer = only 4 direct children.
                Row(
                    modifier = GlanceModifier.defaultWeight(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Image(
                        provider = ImageProvider(R.drawable.water_drop_24px),
                        contentDescription = null,
                        colorFilter = ColorFilter.tint(GlanceTheme.colors.secondary),
                        modifier = GlanceModifier.width(16.dp).height(16.dp)
                    )
                    Spacer(GlanceModifier.width(5.dp))
                    Text(
                        text = listOf(
                            if (showCityName) weatherState.locationName else "",
                            weatherState.currentTemp
                        ).filter { it.isNotBlank() }.joinToString(" · "),
                        maxLines = 1,
                        style = TextStyle(
                            fontWeight = FontWeight.Bold,
                            color = GlanceTheme.colors.secondary,
                            fontSize = 14.sp
                        )
                    )
                    Spacer(GlanceModifier.width(5.dp))
                    Image(
                        provider = ImageProvider(weatherState.currentIcon),
                        contentDescription = weatherState.currentCondition,
                        colorFilter = ColorFilter.tint(GlanceTheme.colors.onSurfaceVariant),
                        modifier = GlanceModifier.width(14.dp).height(14.dp)
                    )
                    if (weatherState.windSpeedText.isNotBlank() ||
                        weatherState.windDirectionLabel.isNotBlank()
                    ) {
                        Spacer(GlanceModifier.width(4.dp))
                        Text(
                            text = listOf(
                                weatherState.windSpeedText,
                                weatherState.windDirectionLabel
                            ).filter { it.isNotBlank() }.joinToString(" "),
                            maxLines = 1,
                            style = TextStyle(
                                color = GlanceTheme.colors.onSurfaceVariant,
                                fontSize = 11.sp
                            )
                        )
                    }
                }
                // Attribution + refresh, pinned right (license: CC BY)
                if (state.attribution.isNotBlank()) {
                    Text(
                        text = state.attribution,
                        maxLines = 1,
                        style = TextStyle(
                            color = GlanceTheme.colors.onSurfaceVariant,
                            fontSize = 8.sp
                        )
                    )
                    Spacer(GlanceModifier.width(7.dp))
                }
                RefreshPill(context)
            }
        } else {
            Row(
                modifier = GlanceModifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Image(
                    provider = ImageProvider(R.drawable.water_drop_24px),
                    contentDescription = null,
                    colorFilter = ColorFilter.tint(GlanceTheme.colors.secondary),
                    modifier = GlanceModifier.width(16.dp).height(16.dp)
                )
                Spacer(GlanceModifier.width(5.dp))
                Text(
                    text = "${context.getString(R.string.rain_nowcast_title)} (${state.unitLabel})",
                    style = TextStyle(
                        fontWeight = FontWeight.Bold,
                        color = GlanceTheme.colors.secondary,
                        fontSize = 14.sp
                    ),
                    modifier = GlanceModifier.defaultWeight()
                )
                // Attribution, pinned left of the refresh button (license: CC BY)
                if (state.attribution.isNotBlank()) {
                    Text(
                        text = state.attribution,
                        maxLines = 1,
                        style = TextStyle(
                            color = GlanceTheme.colors.onSurfaceVariant,
                            fontSize = 8.sp
                        ),
                        modifier = GlanceModifier.padding(end = 7.dp)
                    )
                }
                RefreshPill(context)
            }
        }

        Spacer(GlanceModifier.height(8.dp))

        val chartHeightDp = 64.dp
        if (state.isNowcast && state.slots.isNotEmpty()) {
            NowcastChartImage(
                context = context,
                state = state,
                chartHeightDp = chartHeightDp,
                labelColor = GlanceTheme.colors.onSurfaceVariant.getColor(context)
            )
        } else {
            // No-nowcast fallback OR a pre-slots state (older format / no
            // refresh yet): render the pre-formatted hourly bars.
            HourlyBars(state, chartHeightDp)
        }
    }
}

/**
 * Circular refresh button (right end of the header); triggers a widget
 * data reload. Extracted so both header variants can pin it to the right.
 */
@Composable
private fun RefreshPill(context: Context) {
    Box(
        modifier = GlanceModifier
            .size(28.dp)
            .background(
                ImageProvider(R.drawable.refresh_button_background),
                colorFilter = ColorFilter.tint(GlanceTheme.colors.secondaryContainer)
            )
            .clickable(actionRunCallback<RainWidgetRefreshAction>()),
        contentAlignment = Alignment.Center
    ) {
        Image(
            provider = ImageProvider(R.drawable.refresh_24px),
            contentDescription = context.getString(R.string.widget_refresh),
            colorFilter = ColorFilter.tint(GlanceTheme.colors.onSecondaryContainer),
            modifier = GlanceModifier.size(16.dp)
        )
    }
}

/**
 * Chart for the nowcast: re-aggregates the raw 5-minute slots into a bucket
 * size chosen from the current widget width (wider -> more ticks, down to
 * 5-minute resolution), then draws the bars + sparse time labels as a bitmap.
 */
@Composable
private fun NowcastChartImage(
    context: Context,
    state: RainWidgetState,
    chartHeightDp: androidx.compose.ui.unit.Dp,
    labelColor: Color
) {
    val widgetWidthDp = LocalSize.current.width.value
    // 5-min data: bucket size by width; keep the slot count sane for tiny sizes
    val slotMinutes = when {
        widgetWidthDp < 110f -> 30L  // ~6 bars
        widgetWidthDp < 160f -> 20L  // ~9 bars
        widgetWidthDp < 230f -> 15L  // ~12 bars
        widgetWidthDp < 300f -> 10L  // ~18 bars
        else -> 5L                   // ~36 bars
    }

    val density = context.resources.displayMetrics.density
    val widthPx = (LocalSize.current.width.value * density).toInt()
        .coerceAtLeast(1) - (2 * 12.dp.value * density).toInt() // minus horizontal padding
    val heightPx = (chartHeightDp.value * density).toInt().coerceAtLeast(1)

    val bitmap = remember(state, slotMinutes, widthPx, heightPx, labelColor) {
        drawNowcastBitmap(
            state = state,
            slotMinutes = slotMinutes,
            widthPx = widthPx,
            heightPx = heightPx,
            labelColor = labelColor,
            density = density
        )
    }

    Image(
        provider = ImageProvider(bitmap),
        contentDescription = null,
        modifier = GlanceModifier.fillMaxWidth().height(chartHeightDp + 20.dp)
    )
}

/**
 * Draws the nowcast chart: bottom-aligned bars per [slotMinutes] bucket, a
 * hairline baseline and sparse HH:mm time labels under the bars.
 */
private fun drawNowcastBitmap(
    state: RainWidgetState,
    slotMinutes: Long,
    widthPx: Int,
    heightPx: Int,
    labelColor: Color,
    density: Float
): Bitmap {
    // Extra headroom at the top so the topmost y-axis label is not clipped:
    // its text ascent reaches ~3dp above the tick's baseline, which sits
    // 2dp below the bar area's top edge. The canvas is translated down by
    // this amount so the bar area starts below the reserved band.
    val yLabelHeadroomPx = (4 * density).toInt()
    val bitmap = Bitmap.createBitmap(
        widthPx,
        yLabelHeadroomPx + heightPx + (LABEL_AREA_PX_SP * density).toInt(),
        Bitmap.Config.ARGB_8888
    )
    val canvas = Canvas(bitmap)
    canvas.translate(0f, yLabelHeadroomPx.toFloat())
    val paint = Paint(Paint.ANTI_ALIAS_FLAG)

    // Aggregate the 5-min slots
    data class Bucket(val time: Long, val mm: Double)
    val buckets = mutableListOf<Bucket>()
    val slotMillis = slotMinutes * 60_000L
    val byBucket = linkedMapOf<Long, Double>()
    for (slot in state.slots) {
        val bucket = slot.time - (slot.time % slotMillis)
        byBucket[bucket] = (byBucket[bucket] ?: 0.0) + slot.mm
    }
    for ((t, mm) in byBucket.entries.sortedBy { it.key }) {
        buckets.add(Bucket(t, mm))
    }
    if (buckets.isEmpty()) return bitmap

    val maxMm = buckets.maxOf { it.mm }
    val barAreaHeightPx = heightPx

    // ---- y-axis (same nice-step ladder as the in-app card) -----------------
    // peak mm/h of the buckets, converted to the display unit
    val peak = maxMm * 60.0 / slotMinutes * state.mmToUnitFactor
    val axisStep = when {
        peak <= 1.0 -> 0.25
        peak <= 2.5 -> 0.5
        peak <= 5.0 -> 1.0
        peak <= 12.0 -> 2.0
        peak <= 30.0 -> 5.0
        peak <= 60.0 -> 10.0
        else -> 25.0
    }
    val topOfScale = ceil(peak / axisStep) * axisStep
    val tickDecimals = when {
        state.mmToUnitFactor != 1.0 -> 3 // inch
        axisStep < 1.0 -> 2
        else -> 1
    }
    val axisLabelWidthPx = (30 * density).toInt()
    val chartWidthPx = widthPx - axisLabelWidthPx
    val slotCount = buckets.size
    val slotWidthPx = chartWidthPx.toFloat() / slotCount
    // narrow bar with a small gap, buienradar-style
    val barWidthPx = (slotWidthPx * 0.72f).coerceAtLeast(1f)
    val axisPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        textSize = 8 * density
        color = labelColor.copy(alpha = 0.7f).toArgb()
        textAlign = Paint.Align.RIGHT
    }

    // ---- bars -------------------------------------------------------------
    val topOfScaleMmPerHour = topOfScale / state.mmToUnitFactor
    buckets.forEachIndexed { index, bucket ->
        val mmPerHour = bucket.mm * 60.0 / slotMinutes
        paint.color = nowcastBarColorArgb(mmPerHour).toInt()
        val fraction = (mmPerHour / topOfScaleMmPerHour).coerceIn(0.0, 1.0)
        val barHeightPx = if (bucket.mm <= 0.0) {
            (2 * density).toInt().coerceAtLeast(2)
        } else {
            (fraction * (barAreaHeightPx - 2 * density)).toInt().coerceAtLeast((3 * density).toInt())
        }
        val left = axisLabelWidthPx + index * slotWidthPx + (slotWidthPx - barWidthPx) / 2f
        canvas.drawRoundRect(
            left,
            (barAreaHeightPx - barHeightPx).toFloat(),
            left + barWidthPx,
            barAreaHeightPx.toFloat(),
            barWidthPx / 2.2f,
            barWidthPx / 2.2f,
            paint
        )
    }

    // ---- y-axis tick labels + baseline -------------------------------------
    // All bar/tick geometry keeps using barAreaHeightPx as the bar-area
    // bottom; the bitmap's top band (yLabelHeadroomPx) stays empty so the
    // topmost label's ascent fits.
    val tickCount = (topOfScale / axisStep).toInt() + 1
    for (i in tickCount - 1 downTo 0) {
        val value = i * axisStep
        val y = barAreaHeightPx - (value / topOfScale * (barAreaHeightPx - 2 * density)).toFloat()
        canvas.drawText(
            String.format(java.util.Locale.US, "%.${tickDecimals}f", value),
            axisLabelWidthPx - 4 * density,
            y + axisPaint.textSize / 3f,
            axisPaint
        )
    }
    paint.color = labelColor.copy(alpha = 0.4f).toArgb()
    canvas.drawRect(
        axisLabelWidthPx.toFloat(),
        barAreaHeightPx - 1f,
        widthPx.toFloat(),
        barAreaHeightPx.toFloat(),
        paint
    )

    // ---- sparse time labels ------------------------------------------------
    paint.color = labelColor.copy(alpha = 0.85f).toArgb()
    paint.textSize = LABEL_TEXT_SP * density
    paint.isAntiAlias = true
    val zone = runCatching { ZoneId.of(state.zoneId) }.getOrElse { ZoneId.systemDefault() }
    val formatter = DateTimeFormatter.ofPattern(if (state.is24h) "HH:mm" else "h:mm")
    // keep labels readable: minimum spacing on screen
    val minLabelSpacingPx = 46 * density
    val labelEvery = ceil(minLabelSpacingPx / slotWidthPx).toInt().coerceAtLeast(1)
    buckets.forEachIndexed { index, bucket ->
        if (index % labelEvery != 0) return@forEachIndexed
        val text = formatter.format(Instant.ofEpochMilli(bucket.time).atZone(zone))
        val textWidth = paint.measureText(text)
        val x = (axisLabelWidthPx + index * slotWidthPx + slotWidthPx / 2f - textWidth / 2f)
            .coerceIn(axisLabelWidthPx.toFloat(), widthPx - textWidth)
        canvas.drawText(text, x, barAreaHeightPx + LABEL_TEXT_SP * density + 4 * density, paint)
    }

    return bitmap
}

private const val LABEL_TEXT_SP = 9
private const val LABEL_AREA_PX_SP = 14f  /** label strip height, in sp */

@Composable
private fun HourlyBars(state: RainWidgetState, chartHeightDp: androidx.compose.ui.unit.Dp) {
    val widgetWidthDp = LocalSize.current.width.value
    val hoursToShow = when {
        widgetWidthDp < 110f -> 4
        widgetWidthDp < 160f -> 6
        widgetWidthDp < 230f -> 8
        widgetWidthDp < 300f -> 10
        else -> 12
    }

    val shown = state.bars.take(hoursToShow)
    if (shown.isEmpty()) return

    val maxAmount = shown.maxOf { it.amount }.coerceAtLeast(0.1)

    Column(modifier = GlanceModifier.fillMaxWidth()) {
        // Bar chart: equal-width bottom-aligned bars
        Row(
            modifier = GlanceModifier.fillMaxWidth().height(chartHeightDp),
            verticalAlignment = Alignment.Bottom
        ) {
            shown.forEach { bar ->
                Box(
                    modifier = GlanceModifier.defaultWeight().height(chartHeightDp),
                    contentAlignment = Alignment.BottomCenter
                ) {
                    val fraction = (bar.amount / maxAmount).coerceIn(0.05, 1.0)
                    Box(
                        modifier = GlanceModifier
                            .padding(horizontal = 1.dp)
                            .fillMaxWidth()
                            .height((fraction * chartHeightDp.value).dp)
                            .background(Color(bar.colorArgb))
                    ) {}
                }
            }
        }

        Spacer(GlanceModifier.height(3.dp))

        // Time labels under the bars
        Row(modifier = GlanceModifier.fillMaxWidth()) {
            shown.forEach { bar ->
                Text(
                    text = bar.time,
                    maxLines = 1,
                    style = TextStyle(
                        color = GlanceTheme.colors.onSurfaceVariant,
                        fontSize = 9.sp,
                        textAlign = TextAlign.Center
                    ),
                    modifier = GlanceModifier.defaultWeight().padding(horizontal = 1.dp)
                )
            }
        }
    }
}

@Composable
private fun GlanceModifier.appWidgetBackgroundShape(): GlanceModifier {
    return if (Build.VERSION.SDK_INT >= 31) {
        this
            .cornerRadius(android.R.dimen.system_app_widget_background_radius)
            .background(GlanceTheme.colors.widgetBackground)
    } else {
        this
            .background(ImageProvider(R.drawable.weather_widget_background))
    }
}

/**
 * Refresh button action: re-fetches weather + rain data for the default
 * location and updates the widgets, without opening the app.
 */
class RainWidgetRefreshAction : ActionCallback {
    @Inject
    override suspend fun onAction(
        context: Context,
        glanceId: GlanceId,
        parameters: ActionParameters
    ) {
        val widgetReload = EntryPointAccessors.fromApplication(
            context.applicationContext,
            WidgetReloadEntryPoint::class.java
        ).widgetReload()

        widgetReload.reload(context)
    }
}