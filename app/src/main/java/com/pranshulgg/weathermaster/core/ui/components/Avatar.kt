package com.pranshulgg.weathermaster.core.ui.components

import android.graphics.drawable.shapes.Shape
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.pranshulgg.weathermaster.R
import com.pranshulgg.weathermaster.core.ui.theme.ShapeRadius

@Composable
fun AvatarMonogram(
    text: String,
    containerColor: Color = MaterialTheme.colorScheme.primaryContainer,
    contentColor: Color = MaterialTheme.colorScheme.onPrimaryContainer,
    cornerRadius: Dp = ShapeRadius.Full
) {

    Surface(
        color = containerColor,
        shape = RoundedCornerShape(cornerRadius),
    ) {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier.size(40.dp)

        ) {
            Text(
                text = text,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                lineHeight = 24.sp,
                color = contentColor
            )

        }
    }
}

@Composable
fun AvatarIcon(
    icon: Int,
    containerColor: Color = MaterialTheme.colorScheme.primaryContainer,
    contentColor: Color = MaterialTheme.colorScheme.onPrimaryContainer,
    cornerRadius: Dp = ShapeRadius.Full,
    avatarSize: Dp = 40.dp,
    iconSize: Dp = 24.dp
) {

    Surface(
        color = containerColor,
        shape = RoundedCornerShape(cornerRadius),
    ) {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier.size(avatarSize)

        ) {
            Symbol(
                icon,
                color = contentColor,
                size = iconSize
            )
        }
    }
}


@Composable
fun AvatarCheck() {
    Surface(
        color = MaterialTheme.colorScheme.primaryContainer,
        shape = CircleShape,
    ) {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier.size(40.dp)

        ) {
            Symbol(R.drawable.check_24px, color = MaterialTheme.colorScheme.onPrimaryContainer)
        }
    }

}