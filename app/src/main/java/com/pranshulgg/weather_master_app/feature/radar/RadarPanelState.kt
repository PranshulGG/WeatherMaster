package com.pranshulgg.weather_master_app.feature.radar

import androidx.compose.animation.core.animate
import androidx.compose.animation.core.spring
import androidx.compose.foundation.gestures.awaitEachGesture
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.input.pointer.AwaitPointerEventScope
import androidx.compose.ui.input.pointer.PointerEvent
import androidx.compose.ui.input.pointer.PointerInputChange
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.input.pointer.positionChange
import androidx.compose.ui.input.pointer.util.VelocityTracker
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlin.math.abs

/**
 * Position of the full-width radar panel that slides in from the right
 * [closedFraction] is 0 when fully open and 1 when fully off screen
 */
@Stable
class RadarPanelState(private val scope: CoroutineScope) {

    var closedFraction by mutableFloatStateOf(1f)
        private set

    var isOpen by mutableStateOf(false)
        private set

    // Derived, so readers aren't recomposed on every frame of a slide
    val isVisible: Boolean by derivedStateOf { closedFraction < 1f }

    /** Fully open, covering the main screen */
    val isCovering: Boolean by derivedStateOf { closedFraction == 0f }

    private var animation: Job? = null

    fun open() = animateTo(0f)

    fun close() = animateTo(1f)

    fun snapClosed() {
        animation?.cancel()
        isOpen = false
        closedFraction = 1f
    }

    /** Moves the panel by [deltaFraction] of the screen width (positive = towards closed) */
    fun dragBy(deltaFraction: Float) {
        animation?.cancel()
        closedFraction = (closedFraction + deltaFraction).coerceIn(0f, 1f)
    }

    /**
     * Finishes a drag: a flick decides, otherwise moving the panel [SETTLE_DISTANCE] of the way
     * in the drag's direction ([opening] or closing) is enough. Velocity is in widths per second
     */
    fun settle(velocity: Float, opening: Boolean) {
        val target = when {
            velocity < -FLING_VELOCITY -> 0f
            velocity > FLING_VELOCITY -> 1f
            opening -> if (closedFraction < 1f - SETTLE_DISTANCE) 0f else 1f
            else -> if (closedFraction > SETTLE_DISTANCE) 1f else 0f
        }
        animateTo(target, velocity)
    }

    private fun animateTo(target: Float, initialVelocity: Float = 0f) {
        isOpen = target == 0f
        animation?.cancel()
        animation = scope.launch {
            animate(closedFraction, target, initialVelocity, spring(stiffness = 600f)) { value, _ ->
                closedFraction = value
            }
        }
    }

    private companion object {
        const val FLING_VELOCITY = 0.45f // widths per second
        const val SETTLE_DISTANCE = 0.3f
    }
}

@Composable
fun rememberRadarPanelState(): RadarPanelState {
    val scope = rememberCoroutineScope()
    return remember { RadarPanelState(scope) }
}

internal enum class MultiTouch { Abandon, Handled }

/**
 * Follows one finger's horizontal drag. Once it moves past touch slop, [accept] decides whether
 * the drag is taken; if so [onDrag] gets each horizontal step as a fraction of the width.
 * Events with two or more fingers before the drag starts go to [onMultiTouch].
 * Returns the release velocity in widths per second, or null if no drag was taken
 */
internal suspend fun AwaitPointerEventScope.trackHorizontalDrag(
    down: PointerInputChange,
    accept: (total: Offset) -> Boolean,
    onDrag: (deltaFraction: Float) -> Unit,
    onMultiTouch: (PointerEvent) -> MultiTouch = { MultiTouch.Abandon }
): Float? {
    val width = size.width.toFloat()
    val velocityTracker = VelocityTracker()
    velocityTracker.addPosition(down.uptimeMillis, down.position)
    var total = Offset.Zero
    var dragging = false
    var rejected = false // past slop but not taken: only multi-touch is still watched

    while (true) {
        val event = awaitPointerEvent()
        if (event.changes.none { it.pressed }) break

        if (!dragging && event.changes.count { it.pressed } >= 2) {
            if (onMultiTouch(event) == MultiTouch.Abandon) return null
            continue
        }

        val change = event.changes.firstOrNull { it.id == down.id && it.pressed }
            ?: if (dragging) break else continue

        if (rejected) continue
        if (!dragging) {
            if (change.isConsumed) return null // taken by a child, e.g. a scrolling row
            total += change.positionChange()
            // Slop per axis, like scrollable rows, so a row reaching its slop takes the drag first
            val slop = viewConfiguration.touchSlop
            if (abs(total.x) > slop || abs(total.y) > slop) {
                if (!accept(total)) {
                    rejected = true
                    continue
                }
                dragging = true
                onDrag(total.x / width)
                change.consume()
            }
        } else {
            onDrag(change.positionChange().x / width)
            change.consume()
        }
        velocityTracker.addPosition(change.uptimeMillis, change.position)
    }

    return if (dragging) velocityTracker.calculateVelocity().x / width else null
}

/**
 * Opens the radar panel with a left swipe. Right swipes and drags already taken by children
 * (e.g. the hourly forecast row) are left alone, so the locations drawer keeps working
 */
fun Modifier.radarOpenSwipe(state: RadarPanelState, enabled: Boolean): Modifier =
    if (!enabled) this else pointerInput(state) {
        awaitEachGesture {
            val down = awaitFirstDown(requireUnconsumed = false)
            val velocity = trackHorizontalDrag(
                down,
                accept = { total -> total.x < 0 && abs(total.x) > abs(total.y) },
                onDrag = state::dragBy
            )
            if (velocity != null) state.settle(velocity, opening = true)
        }
    }
