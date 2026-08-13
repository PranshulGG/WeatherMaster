package com.pranshulgg.weather_master_app.widgets.weather.components

import android.graphics.Color
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.glance.GlanceModifier
import androidx.glance.GlanceTheme
import androidx.glance.Image
import androidx.glance.ImageProvider
import androidx.glance.layout.Alignment
import androidx.glance.layout.Column
import androidx.glance.layout.Spacer
import androidx.glance.layout.height
import androidx.glance.layout.padding
import androidx.glance.layout.size
import androidx.glance.layout.width
import androidx.glance.text.FontWeight
import androidx.glance.text.Text
import androidx.glance.text.TextStyle
import androidx.glance.unit.ColorProvider

@Composable
fun WidgetHourlyItem(
    time: String,
    temp: String,
    icon: Int,
    fontSize: Float,
    iconSize: Float,
    textColor: ColorProvider,
    textColorVariant: ColorProvider,
    verticalPadding: Dp = 5.dp,
    precipitationProbability: Int? = null
) {

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = GlanceModifier.padding(horizontal = 5.dp, vertical = verticalPadding)
    ) {
        Text(
            temp,
            style = TextStyle(
                color = textColor,
                fontWeight = FontWeight.Medium,
                fontSize = fontSize.sp
            )
        )

        if (precipitationProbability != null) {
            Text(
                "${precipitationProbability}%",
                style = TextStyle(
                    color = textColorVariant,
                    fontSize = (fontSize * 0.9).sp
                )
            )
        }

        Spacer(GlanceModifier.height(3.dp))
        Image(
            provider = ImageProvider(icon),
            contentDescription = "",
            modifier = GlanceModifier.size(iconSize.dp)
        )
        Spacer(GlanceModifier.height(3.dp))
        Text(
            time,
            style = TextStyle(
                color = textColorVariant,
                fontWeight = FontWeight.Medium, fontSize = fontSize.sp
            )
        )
    }
}