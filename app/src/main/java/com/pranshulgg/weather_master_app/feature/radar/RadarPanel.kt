package com.pranshulgg.weather_master_app.feature.radar

import android.graphics.Bitmap
import androidx.annotation.StringRes
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.focusGroup
import androidx.compose.foundation.gestures.awaitEachGesture
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.gestures.calculateZoom
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.LoadingIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TooltipAnchorPosition
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.focus.focusProperties
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.input.pointer.positionChange
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.clearAndSetSemantics
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.repeatOnLifecycle
import com.pranshulgg.weather_master_app.R
import com.pranshulgg.weather_master_app.core.model.domain.AppException
import com.pranshulgg.weather_master_app.core.model.domain.location.Location
import com.pranshulgg.weather_master_app.core.model.domain.radar.RadarFrame
import com.pranshulgg.weather_master_app.core.model.domain.toMessageRes
import com.pranshulgg.weather_master_app.core.prefs.LocalAppPrefs
import com.pranshulgg.weather_master_app.core.ui.components.ErrorContainer
import com.pranshulgg.weather_master_app.core.ui.components.LoadingPlaceholder
import com.pranshulgg.weather_master_app.core.ui.components.NavigateUpBtn
import com.pranshulgg.weather_master_app.core.ui.components.Symbol
import com.pranshulgg.weather_master_app.core.ui.components.Tooltip
import com.pranshulgg.weather_master_app.core.utils.formatters.to12HourTimeString
import com.pranshulgg.weather_master_app.core.utils.formatters.to24HourTimeString
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.sync.Semaphore
import kotlinx.coroutines.sync.withPermit
import kotlinx.coroutines.withContext
import kotlinx.coroutines.withTimeoutOrNull
import kotlin.math.abs

private const val MIN_WIDTH_KM = 25.0
private const val DEFAULT_WIDTH_KM = 250.0
private const val MAX_WIDTH_KM = 4000.0
private val ZOOM_PRESETS_KM = listOf(50.0, DEFAULT_WIDTH_KM, 1000.0) // double-tap steps
private val BORDER_WIDTH = 16.dp // left and right edges of the page
private const val OLD_FRAME_MILLI = 30 * 60 * 1000L
private const val LOOP_FRAME_SCALE = 0.5f // loop frames are drawn at half resolution
private const val LOOP_FRAME_MILLI = 500L
private const val LOOP_HOLD_LAST_MILLI = 1500L // the newest frame stays up longer
private const val PREPARE_DELAY_MILLI = 1000L // before drawing the closed page
private val ECHO_RETRY_DELAYS_MILLI = listOf(15_000L, 30_000L, 60_000L)

// Loop frames are drawn two at a time, shared by every run: a run cancelled by another zoom
// finishes its current frames alongside the next run's, so only a few bitmaps are held
private val loopRenders = Semaphore(2)

/**
 * Full-width radar page that slides in from the right over the main screen
 * Right swipe, back or the top bar arrow close it; pinch and double-tap zoom around the location
 *
 * Composed while closed (not drawn, hidden from accessibility), so it slides in already showing
 * the map and the latest radar image instead of building them during the swipe
 */
@Composable
fun RadarPanel(state: RadarPanelState, location: Location, viewModel: RadarViewModel) {
    val isVisible = state.isVisible

    val uiState = viewModel.uiState
    val frame = uiState.latest

    // Refresh while the page is visible and the app is started (not in the background); while
    // closed, the frames are fetched with the weather (once the radar has been opened)
    val lifecycle = LocalLifecycleOwner.current.lifecycle
    // Before the radar has been opened, a short peek that springs back requests nothing
    val shouldRefresh = isVisible && (state.isOpen || viewModel.hasOpenedRadar)
    LaunchedEffect(lifecycle, shouldRefresh) {
        if (shouldRefresh) lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) { viewModel.keepUpdated() }
    }
    // Opened, not just peeked at with a short swipe that springs back
    LaunchedEffect(state.isOpen) { if (state.isOpen) viewModel.onPageOpened() }

    var widthKm by rememberSaveable(location.id) { mutableStateOf(DEFAULT_WIDTH_KM) }
    var gestureScale by remember { mutableFloatStateOf(1f) }
    // A pinch cut short by closing the page never reaches onZoomEnd
    LaunchedEffect(state.isOpen) { if (!state.isOpen) gestureScale = 1f }
    val canZoom by rememberUpdatedState(frame != null)

    // Gestures are read on this fixed layer: reading them on the sliding page would measure the
    // finger relative to a page that moves with it. Only while open, so taps reach the main
    // screen during the close animation
    val pageGestures = if (!state.isOpen) Modifier else Modifier
        .pointerInput(location.id) {
            detectTapGestures(onDoubleTap = {
                if (!canZoom) return@detectTapGestures
                val current = ZOOM_PRESETS_KM.indices.minBy { abs(ZOOM_PRESETS_KM[it] - widthKm) }
                widthKm = ZOOM_PRESETS_KM[(current + 1) % ZOOM_PRESETS_KM.size]
            })
        }
        .radarPageGestures(
            key = location.id,
            onCloseDrag = state::dragBy,
            onCloseDragEnd = { velocity -> state.settle(velocity, opening = false) },
            onZoom = { zoom ->
                if (canZoom) gestureScale = (gestureScale * zoom)
                    .coerceIn((widthKm / MAX_WIDTH_KM).toFloat(), (widthKm / MIN_WIDTH_KM).toFloat())
            },
            onZoomEnd = {
                widthKm = (widthKm / gestureScale).coerceIn(MIN_WIDTH_KM, MAX_WIDTH_KM)
                gestureScale = 1f
            }
        )

    // Keyboard focus left inside when the page closes is cleared, so a hidden button can't be used
    var hasFocusInside by remember { mutableStateOf(false) }
    val focusManager = LocalFocusManager.current
    LaunchedEffect(isVisible) {
        if (!isVisible && hasFocusInside) focusManager.clearFocus(force = true)
    }

    Box(
        Modifier
            .fillMaxSize()
            .onFocusChanged { hasFocusInside = it.hasFocus }
            // While closed: not drawn, hidden from accessibility services and not reachable with
            // the keyboard
            .drawWithContent { if (isVisible) drawContent() }
            .then(
                if (isVisible) Modifier else Modifier
                    .clearAndSetSemantics {}
                    .focusProperties { onEnter = { cancelFocusChange() } }
                    .focusGroup()
            )
            .then(pageGestures)
    ) {
        Box(
            Modifier
                .fillMaxSize()
                .graphicsLayer { translationX = state.closedFraction * size.width }
                // Black borders on the left and right; the page fits between them
                .background(Color.Black)
                .padding(horizontal = BORDER_WIDTH)
        ) {
            RadarPage(
                location = location,
                frame = frame,
                loop = uiState.loop,
                isVisible = isVisible,
                error = uiState.error,
                widthKm = widthKm,
                gestureScale = { gestureScale },
                attribution = viewModel.attribution,
                renderEchoes = viewModel::renderEchoes,
                onToggleLoop = viewModel::toggleLoop,
                onRetry = viewModel::refreshNow,
                onClose = state::close
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun RadarPage(
    location: Location,
    frame: RadarFrame?,
    loop: List<RadarFrame>?,
    isVisible: Boolean,
    error: AppException?,
    widthKm: Double,
    gestureScale: () -> Float,
    @StringRes attribution: Int,
    renderEchoes: EchoRenderer,
    onToggleLoop: () -> Unit,
    onRetry: () -> Unit,
    onClose: () -> Unit
) {
    val is24Hr = LocalAppPrefs.current.is24HrTimeFormat
    val isLoopOn = loop != null

    // Everything drawn for one location starts afresh for the next (the device location keeps
    // its id when it moves)
    key(location.id, location.latitude, location.longitude) {
        // Measured whether or not there is a frame yet, so the layers have a size when one arrives
        var contentSize by remember { mutableStateOf(IntSize.Zero) }
        val layers = frame?.let { rememberRadarLayers(it, loop, location, widthKm, contentSize, isVisible, renderEchoes) }

        Scaffold(
            containerColor = MaterialTheme.colorScheme.surfaceContainerLowest,
            topBar = {
                TopAppBar(
                    colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.surfaceContainer),
                    title = {
                        Column {
                            Text(
                                stringResource(R.string.radar),
                                color = MaterialTheme.colorScheme.onSurface,
                                style = MaterialTheme.typography.titleLarge
                            )
                            if (frame != null) {
                                Text(
                                    formatRadarTime(frame.timeMilli, location, is24Hr, isOld(frame.timeMilli)),
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    style = MaterialTheme.typography.bodyMedium
                                )
                            }
                        }
                    },
                    navigationIcon = { NavigateUpBtn(onClick = onClose) },
                    actions = {
                        // Shows progress until the loop's frames are drawn
                        if (layers != null) LoopButton(isLoopOn, isPreparing = isLoopOn && layers.frames == null, onToggleLoop)
                    }
                )
            }
        ) { padding ->
            Box(
                Modifier
                    .padding(padding)
                    .fillMaxSize()
                    .onSizeChanged { contentSize = it },
                contentAlignment = Alignment.Center
            ) {
                when {
                    layers?.map != null -> RadarImage(layers, loop, location, is24Hr, widthKm, gestureScale, attribution)

                    error != null && frame == null -> ErrorContainer(
                        onRetry = onRetry,
                        errorDescription = stringResource(error.toMessageRes())
                    )

                    // Not while closed: its animation would keep running off screen
                    isVisible -> LoadingPlaceholder()
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3ExpressiveApi::class)
@Composable
private fun LoopButton(isLoopOn: Boolean, isPreparing: Boolean, onClick: () -> Unit) {
    val label = stringResource(if (isLoopOn) R.string.radar_pause_loop else R.string.radar_play_loop)
    Tooltip(label, preferredPosition = TooltipAnchorPosition.Below, spacing = 10.dp) {
        IconButton(onClick = onClick, shapes = IconButtonDefaults.shapes()) {
            if (isPreparing) {
                LoadingIndicator(Modifier.size(24.dp))
            } else {
                Symbol(if (isLoopOn) R.drawable.pause_24px else R.drawable.play_arrow_24px, desc = label)
            }
        }
    }
}

/** A bitmap and the map width it was drawn for, to scale it until the current width is drawn */
private class RenderedLayer(
    val bitmap: ImageBitmap,
    val widthKm: Double,
    val size: IntSize,
    val frame: RadarFrame? = null, // for the radar image: which frame it shows
    val isComplete: Boolean = true // for the radar image: no tiles missing
)

private class LoopFrame(val bitmap: ImageBitmap, val timeMilli: Long)

/** Draws a frame's radar tiles for a view, at a scale */
private typealias EchoRenderer = suspend (frame: RadarFrame, view: MercatorView, scale: Float) -> RenderedEchoes

/** Time-lapse frames, drawn at lower resolution for a map width */
private class LoopFrames(val frames: List<LoopFrame>, val widthKm: Double)

/** The bitmaps drawn for one location's page: map, latest echoes and the loop's frames */
@Stable
private class RadarLayers {
    var map by mutableStateOf<RenderedLayer?>(null)
    var echoes by mutableStateOf<RenderedLayer?>(null)
    var frames by mutableStateOf<LoopFrames?>(null)
}

/** Bitmaps are uploaded ahead, so the first frame of the slide-in doesn't wait for them */
private fun Bitmap.toPreparedImage(): ImageBitmap = also { it.prepareToDraw() }.asImageBitmap()

/**
 * Draws the [RadarLayers] for [frame] and [loop], also while the page is closed so it slides in
 * ready. While closed it waits a moment first, so this doesn't compete with the main screen
 */
@Composable
private fun rememberRadarLayers(
    frame: RadarFrame,
    loop: List<RadarFrame>?,
    location: Location,
    widthKm: Double,
    size: IntSize,
    isVisible: Boolean,
    renderEchoes: EchoRenderer
): RadarLayers {
    val context = LocalContext.current
    val density = LocalDensity.current.density
    val colorScheme = MaterialTheme.colorScheme
    val colors = remember(colorScheme) {
        RadarColors(
            coast = colorScheme.onSurfaceVariant.copy(alpha = 0.8f).toArgb(),
            countries = colorScheme.onSurfaceVariant.copy(alpha = 0.8f).toArgb(),
            provinces = colorScheme.outlineVariant.copy(alpha = 0.7f).toArgb(),
            water = colorScheme.primary.copy(alpha = 0.55f).toArgb(),
            roads = colorScheme.tertiary.copy(alpha = 0.45f).toArgb(),
            location = colorScheme.primary.toArgb(),
            locationRing = colorScheme.surface.toArgb()
        )
    }
    val layers = remember { RadarLayers() }
    val hasSize = size.width > 0 && size.height > 0
    val isVisibleNow by rememberUpdatedState(isVisible)
    // While closed, wait a moment (cut short if the page opens meanwhile)
    suspend fun waitIfClosed() {
        if (!isVisibleNow) withTimeoutOrNull(PREPARE_DELAY_MILLI) { snapshotFlow { isVisibleNow }.first { it } }
    }

    fun view() = MercatorView(location.latitude, location.longitude, widthKm, size.width, size.height)

    // The map doesn't depend on the radar frame. The outlines are only held while drawing (they
    // are cached softly)
    LaunchedEffect(widthKm, size, colors) {
        if (!hasSize) return@LaunchedEffect
        waitIfClosed()
        val bitmap = withContext(Dispatchers.Default) {
            RadarRenderer.renderMap(RadarOutlines.load(context), view(), density, colors).toPreparedImage()
        }
        layers.map = RenderedLayer(bitmap, widthKm, size)
    }

    LaunchedEffect(frame, widthKm, size) {
        if (!hasSize) return@LaunchedEffect
        waitIfClosed()
        // Tiles that couldn't be loaded (e.g. offline) are tried again a few times, showing what
        // there is meanwhile
        var retries = 0
        while (true) {
            val echoes = withContext(Dispatchers.Default) { renderEchoes(frame, view(), 1f) }
            layers.echoes = RenderedLayer(
                echoes.bitmap.toPreparedImage(), widthKm, size, frame, isComplete = echoes.missingTiles == 0
            )
            if (echoes.missingTiles == 0 || retries == ECHO_RETRY_DELAYS_MILLI.size) break
            delay(ECHO_RETRY_DELAYS_MILLI[retries++])
        }
    }

    LaunchedEffect(loop, widthKm, size) {
        // Drop the old frames first so two sets aren't held at once (the latest radar image shows meanwhile)
        layers.frames = null
        if (loop == null || !hasSize) return@LaunchedEffect
        val view = view()
        val rendered = withContext(Dispatchers.Default) {
            loop.map { loopFrame ->
                async {
                    // The latest frame is already drawn at this zoom: no need to download it again
                    val latest = layers.echoes?.takeIf {
                        it.frame == loopFrame && it.widthKm == widthKm && it.size == size && it.isComplete
                    }
                    val bitmap = latest?.bitmap ?: loopRenders.withPermit {
                        renderEchoes(loopFrame, view, LOOP_FRAME_SCALE).bitmap.asImageBitmap()
                    }
                    LoopFrame(bitmap, loopFrame.timeMilli)
                }
            }.awaitAll()
        }
        layers.frames = LoopFrames(rendered, widthKm)
    }

    return layers
}

/** Shows the [layers]: the map over the latest radar frame, or over the loop's frames while playing */
@Composable
private fun RadarImage(
    layers: RadarLayers,
    loop: List<RadarFrame>?,
    location: Location,
    is24Hr: Boolean,
    widthKm: Double,
    gestureScale: () -> Float,
    @StringRes attribution: Int
) {
    val map = layers.map ?: return
    val frames = layers.frames
    // Read only by the frame and its time, so the rest of the page isn't recomposed every frame
    val frameIndex = remember { mutableIntStateOf(0) }

    val frameCount = frames?.frames?.size ?: 0
    val lifecycle = LocalLifecycleOwner.current.lifecycle
    // A new loop starts from the oldest frame; a zoom keeps the position
    LaunchedEffect(loop) { frameIndex.intValue = 0 }
    LaunchedEffect(frameCount, lifecycle) {
        if (frameCount == 0) return@LaunchedEffect
        // Not while the app is in the background
        lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
            var index = frameIndex.intValue.coerceIn(0, frameCount - 1)
            while (true) {
                frameIndex.intValue = index
                delay(if (index == frameCount - 1) LOOP_HOLD_LAST_MILLI else LOOP_FRAME_MILLI)
                index = (index + 1) % frameCount
            }
        }
    }

    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        // The loop frame replaces the latest radar image while playing
        if (frames != null) {
            LoopFrameImage(frames, frameIndex::intValue, widthKm, gestureScale)
        } else {
            layers.echoes?.let { RadarLayerImage(it.bitmap, it.widthKm, widthKm, gestureScale) }
        }
        RadarLayerImage(
            map.bitmap, map.widthKm, widthKm, gestureScale,
            contentDescription = stringResource(R.string.radar)
        )

        // LibreWXR data is CC BY 4.0: credited on the map
        Text(
            stringResource(attribution),
            modifier = Modifier
                .align(Alignment.BottomStart)
                .padding(8.dp),
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            style = MaterialTheme.typography.labelSmall
        )

        if (frames != null) {
            LoopFrameTime(
                frames, frameIndex::intValue, location, is24Hr,
                Modifier
                    .align(Alignment.BottomCenter)
                    .padding(bottom = 16.dp)
            )
        }
    }
}

/** A layer drawn for [renderedWidthKm], scaled while pinching or until the current width is drawn */
@Composable
private fun RadarLayerImage(
    bitmap: ImageBitmap,
    renderedWidthKm: Double,
    widthKm: Double,
    gestureScale: () -> Float,
    contentDescription: String? = null
) {
    Image(
        bitmap = bitmap,
        contentDescription = contentDescription,
        contentScale = ContentScale.FillBounds,
        modifier = Modifier
            .fillMaxSize()
            .graphicsLayer {
                val scale = (renderedWidthKm / widthKm).toFloat() * gestureScale()
                scaleX = scale
                scaleY = scale
            }
    )
}

@Composable
private fun LoopFrameImage(frames: LoopFrames, frameIndex: () -> Int, widthKm: Double, gestureScale: () -> Float) {
    val frame = frames.frames.getOrNull(frameIndex()) ?: return
    RadarLayerImage(frame.bitmap, frames.widthKm, widthKm, gestureScale)
}

@Composable
private fun LoopFrameTime(
    frames: LoopFrames,
    frameIndex: () -> Int,
    location: Location,
    is24Hr: Boolean,
    modifier: Modifier
) {
    // Formatted once per loop, the same way for every frame: with the date only if the newest is old
    val labels = remember(frames, location, is24Hr) {
        val withDate = isOld(frames.frames.last().timeMilli)
        frames.frames.map { formatRadarTime(it.timeMilli, location, is24Hr, withDate) }
    }
    val label = labels.getOrNull(frameIndex()) ?: return
    Surface(
        modifier = modifier,
        shape = CircleShape,
        color = MaterialTheme.colorScheme.surfaceContainerHigh.copy(alpha = 0.9f)
    ) {
        Text(
            label,
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp),
            color = MaterialTheme.colorScheme.onSurface,
            style = MaterialTheme.typography.labelLarge
        )
    }
}

/** An old radar image is shown with its date, e.g. while the radar is down */
private fun isOld(timeMilli: Long) = System.currentTimeMillis() - timeMilli > OLD_FRAME_MILLI

/** Radar time in the location's time zone */
private fun formatRadarTime(milli: Long, location: Location, is24Hr: Boolean, withDate: Boolean): String =
    if (is24Hr) {
        to24HourTimeString(milli, location.timezone, if (withDate) "MMMdHHmm" else "HH:mm")
    } else {
        to12HourTimeString(milli, location.timezone, if (withDate) "MMMdhmma" else "h:mm a")
    }

/**
 * One finger drags the page (it only moves right, towards closing); two fingers zoom
 */
private fun Modifier.radarPageGestures(
    key: Any,
    onCloseDrag: (Float) -> Unit,
    onCloseDragEnd: (Float) -> Unit,
    onZoom: (Float) -> Unit,
    onZoomEnd: () -> Unit
): Modifier = pointerInput(key) {
    awaitEachGesture {
        val down = awaitFirstDown(requireUnconsumed = false)
        var zooming = false
        val velocity = trackHorizontalDrag(
            down,
            // Rightward and not too steep, so vertical swipes don't close the page
            accept = { total -> !zooming && total.x > 0 && abs(total.y) < 2 * total.x },
            onDrag = onCloseDrag,
            onMultiTouch = { event ->
                zooming = true
                val zoom = event.calculateZoom()
                if (zoom != 1f) onZoom(zoom)
                event.changes.forEach { if (it.positionChange() != Offset.Zero) it.consume() }
                MultiTouch.Handled
            }
        )
        if (velocity != null) onCloseDragEnd(velocity) else if (zooming) onZoomEnd()
    }
}
