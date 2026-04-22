package com.pranshulgg.weathermaster.feature.shared.components.blocks

import android.graphics.PathMeasure
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.DurationBasedAnimationSpec
import androidx.compose.animation.core.InfiniteRepeatableSpec
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.rememberTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.PaintingStyle.Companion.Stroke
import androidx.compose.ui.graphics.asComposePath
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.core.graphics.PathParser
import com.pranshulgg.weathermaster.R
import com.pranshulgg.weathermaster.core.ui.theme.ShadowElevation
import kotlin.math.cos
import kotlin.math.sin

@Composable
fun SunBlock() {

    val progress = 10f

    Surface(
        color = MaterialTheme.colorScheme.surface,
        shape = MaterialTheme.shapes.extraLarge,
        shadowElevation = ShadowElevation.level2
    ) {
        Box(Modifier.size(160.dp)) {
//            val progress by rememberInfiniteTransition().animateFloat(
//                initialValue = 0f,
//                targetValue = 1f,
//                animationSpec = infiniteRepeatable(animation = tween(2000))
//            )

//            val progress = remember { Animatable(0f) }
//
//            LaunchedEffect(Unit) {
//                progress.animateTo(
//                    targetValue = 1f,
//                    animationSpec = tween(1000),
//                )
//            }


            Image(
                painter = painterResource(id = R.drawable.sun_moon_arc),
                contentDescription = "",
                modifier = Modifier
                    .fillMaxWidth()
                    .align(Alignment.BottomCenter),
                colorFilter = ColorFilter.tint(MaterialTheme.colorScheme.tertiaryContainer)
            )


        }
    }
}


