package com.pranshulgg.weathermaster.core.ui.components.tiles

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ListItem
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.pranshulgg.weathermaster.core.ui.components.Symbol

@Composable
fun CategoryTile(
    headline: String,
    description: String? = null,
    leading: Int,
    shapes: RoundedCornerShape,
    color: Color,
    iconColor: Color,
    onClick: () -> Unit,
    itemBgColor: Color
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = shapes,
    ) {
        ListItem(
            modifier = Modifier.clickable(
                onClick = onClick
            ),
            leadingContent = { IconContainer(color, icon = leading, iconColor = iconColor) },
            colors = ListItemDefaults.colors(
                containerColor = itemBgColor
            ),
            headlineContent = { Text(headline) },
            supportingContent = {
                if (description != null) {
                    Text(description)
                }
            }
        )
    }
}

@Composable
fun IconContainer(color: Color, icon: Int, iconColor: Color) {
    Surface(
        shape = RoundedCornerShape(50.dp),
    ) {
        Box(
            modifier = Modifier
                .width(40.dp)
                .height(40.dp)
                .background(color = color),
            contentAlignment = Alignment.Center
        ) {
            Symbol(icon, color = iconColor)
        }
    }

}