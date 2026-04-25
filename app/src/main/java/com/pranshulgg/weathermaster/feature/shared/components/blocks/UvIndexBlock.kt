package com.pranshulgg.weathermaster.feature.shared.components.blocks

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asAndroidPath
import androidx.compose.ui.graphics.drawscope.scale
import androidx.compose.ui.graphics.vector.PathParser
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.pranshulgg.weathermaster.R
import com.pranshulgg.weathermaster.core.model.UvIndex
import com.pranshulgg.weathermaster.core.model.domain.Weather
import com.pranshulgg.weathermaster.core.model.getUvIndex
import com.pranshulgg.weathermaster.core.model.toLabel
import com.pranshulgg.weathermaster.core.ui.components.Symbol

@Composable
fun UvIndexBlock(weather: Weather) {
    val color = MaterialTheme.colorScheme.surface


    val uvIndex = weather.current.uvIndex.toInt()
    val uvIndexValue = getUvIndex(uvIndex)

    Box(modifier = Modifier
        .fillMaxSize()
        .aspectRatio(1f)) {
        Canvas(modifier = Modifier.matchParentSize()) {

            val path = PathParser().parsePathString(
                "M77.55,6.78C81.33,4.32 83.23,3.1 85.26,2.62C87.06,2.2 88.94,2.2 90.74,2.62C92.77,3.1 94.67,4.32 98.45,6.78L102.86,9.64C104.8,10.9 105.77,11.53 106.82,11.95C107.75,12.33 108.73,12.6 109.72,12.73C110.84,12.89 112,12.83 114.31,12.71L119.56,12.43C124.07,12.2 126.32,12.08 128.32,12.69C130.09,13.22 131.71,14.16 133.06,15.43C134.59,16.86 135.61,18.87 137.66,22.88L140.05,27.57C141.1,29.63 141.63,30.66 142.32,31.55C142.94,32.35 143.65,33.06 144.45,33.68C145.34,34.37 146.37,34.9 148.43,35.95L153.12,38.34C157.13,40.39 159.14,41.41 160.57,42.94C161.84,44.29 162.78,45.91 163.31,47.68C163.92,49.68 163.8,51.93 163.57,56.44L163.29,61.69C163.17,64 163.11,65.16 163.27,66.28C163.4,67.27 163.67,68.25 164.04,69.18C164.47,70.23 165.1,71.2 166.36,73.14L169.22,77.55C171.68,81.33 172.9,83.23 173.38,85.26C173.8,87.06 173.8,88.94 173.38,90.74C172.9,92.77 171.68,94.67 169.22,98.45L166.36,102.86C165.1,104.8 164.47,105.77 164.04,106.82C163.67,107.75 163.4,108.73 163.27,109.72C163.11,110.84 163.17,112 163.29,114.31L163.57,119.56C163.8,124.07 163.92,126.32 163.31,128.32C162.78,130.09 161.84,131.71 160.57,133.06C159.14,134.59 157.13,135.61 153.12,137.66L148.43,140.05C146.37,141.1 145.34,141.63 144.45,142.32C143.65,142.94 142.94,143.65 142.32,144.45C141.63,145.34 141.1,146.37 140.05,148.43L137.66,153.12C135.61,157.13 134.59,159.14 133.06,160.57C131.71,161.84 130.09,162.78 128.32,163.31C126.32,163.92 124.07,163.8 119.56,163.57L114.31,163.29C112,163.17 110.84,163.11 109.72,163.27C108.73,163.4 107.75,163.67 106.82,164.04C105.77,164.47 104.8,165.1 102.86,166.36L98.45,169.22C94.67,171.68 92.77,172.9 90.74,173.38C88.94,173.8 87.06,173.8 85.26,173.38C83.23,172.9 81.33,171.68 77.55,169.22L73.14,166.36C71.2,165.1 70.23,164.47 69.18,164.04C68.25,163.67 67.27,163.4 66.28,163.27C65.16,163.11 64,163.17 61.69,163.29L56.44,163.57C51.93,163.8 49.68,163.92 47.68,163.31C45.91,162.78 44.29,161.84 42.94,160.57C41.41,159.14 40.39,157.13 38.34,153.12L35.95,148.43C34.9,146.37 34.37,145.34 33.68,144.45C33.06,143.65 32.35,142.94 31.55,142.32C30.66,141.63 29.63,141.1 27.57,140.05L22.88,137.66C18.87,135.61 16.86,134.59 15.43,133.06C14.16,131.71 13.22,130.09 12.69,128.32C12.08,126.32 12.2,124.07 12.43,119.56L12.71,114.31C12.83,112 12.89,110.84 12.73,109.72C12.6,108.73 12.33,107.75 11.95,106.82C11.53,105.77 10.9,104.8 9.64,102.86L6.78,98.45C4.32,94.67 3.1,92.77 2.62,90.74C2.2,88.94 2.2,87.06 2.62,85.26C3.1,83.23 4.32,81.33 6.78,77.55L9.64,73.14C10.9,71.2 11.53,70.23 11.95,69.18C12.33,68.25 12.6,67.27 12.73,66.28C12.89,65.16 12.83,64 12.71,61.69L12.43,56.44C12.2,51.93 12.08,49.68 12.69,47.68C13.22,45.91 14.16,44.29 15.43,42.94C16.86,41.41 18.87,40.39 22.88,38.34L27.57,35.95C29.63,34.9 30.66,34.37 31.55,33.68C32.35,33.06 33.06,32.35 33.68,31.55C34.37,30.66 34.9,29.63 35.95,27.57L38.34,22.88C40.39,18.87 41.41,16.86 42.94,15.43C44.29,14.16 45.91,13.22 47.68,12.69C49.68,12.08 51.93,12.2 56.44,12.43L61.69,12.71C64,12.83 65.16,12.89 66.28,12.73C67.27,12.6 68.25,12.33 69.18,11.95C70.23,11.53 71.2,10.9 73.14,9.64L77.55,6.78Z"
            ).toPath()

            val bounds = android.graphics.RectF()
            path.asAndroidPath().computeBounds(bounds, true)

            val scaleX = size.width / bounds.width()
            val scaleY = size.height / bounds.height()

            scale(scaleX, scaleY, pivot = Offset((bounds.left), bounds.top)) {


                drawPath(
                    path = path,
                    color = color,
                )

                drawCircle(
                    Color(0xFF6DD58C),
                    radius = 8f,
                    center = Offset(31f, 121f),
                    alpha = if (uvIndexValue == UvIndex.LOW) 1f else 0.15f
                )
                drawCircle(
                    Color(0xFFFCC934),
                    radius = 6f,
                    center = Offset(54f, 145f),
                    alpha = if (uvIndexValue == UvIndex.MODERATE) 1f else 0.15f
                )
                drawCircle(
                    Color(0xFFFA903E),
                    radius = 6f,
                    center = Offset(88f, 155f),
                    alpha = if (uvIndexValue == UvIndex.HIGH) 1f else 0.15f
                )
                drawCircle(
                    Color(0xFFAF5CF7),
                    radius = 6f,
                    center = Offset(144f, 121f),
                    alpha = if (uvIndexValue == UvIndex.VERY_HIGH) 1f else 0.15f
                )
                drawCircle(
                    Color(0xFFEE675C),
                    radius = 6f,
                    center = Offset(120f, 145f),
                    alpha = if (uvIndexValue == UvIndex.EXTREME) 1f else 0.15f
                )
            }
        }

        Box(Modifier.align(Alignment.TopCenter)) {
            Header()
        }

        Text(
            "$uvIndex",
            style = MaterialTheme.typography.displayMedium,
            modifier = Modifier
                .padding()
                .align(Alignment.Center)
                .offset(y = 3.dp),
            color = MaterialTheme.colorScheme.onSurface,
        )


        Text(
            uvIndexValue.toLabel(),
            modifier = Modifier
                .align(Alignment.Center)
                .offset(y = 32.dp),
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Composable
private fun Header() {
    Row(
        horizontalArrangement = Arrangement.spacedBy(
            5.dp,
            alignment = Alignment.CenterHorizontally
        ),
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .padding(top = 32.dp, start = 12.dp, end = 12.dp)

    ) {
        Symbol(
            R.drawable.wb_sunny_24px,
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.9f),
        )
        Text(
            "UV index",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.9f)

        )
    }
}
