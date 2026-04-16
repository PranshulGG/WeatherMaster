package com.pranshulgg.weathermaster.core.ui.navigation

import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
@OptIn(ExperimentalMaterial3ExpressiveApi::class)
object NavTransitions {

    private const val FADE_IN = 350
    private const val FADE_OUT = 200

    fun enter(): EnterTransition =
        slideInHorizontally(
            initialOffsetX = { 1 * it }
        ) + fadeIn(tween(FADE_IN))

    fun exit(): ExitTransition =
        slideOutHorizontally(
            targetOffsetX = { 1 * -it / 4 }
        ) + fadeOut(tween(FADE_OUT))

    @OptIn(ExperimentalMaterial3ExpressiveApi::class)
    fun popEnter(): EnterTransition =
        slideInHorizontally(initialOffsetX = { 1 * -it / 4 }) + fadeIn(tween(FADE_IN))

    @OptIn(ExperimentalMaterial3ExpressiveApi::class)
    fun popExit(): ExitTransition =
        slideOutHorizontally(targetOffsetX = { 1 * it }) + fadeOut(tween(FADE_OUT))
}