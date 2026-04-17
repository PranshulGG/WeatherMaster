package com.pranshulgg.weathermaster.core.ui.components.tiles

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ListItem
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextOverflow

@Composable
fun TextTile(
    headline: String,
    description: String? = null,
    leading: @Composable (() -> Unit)? = null,
    shapes: RoundedCornerShape,
    itemBgColor: Color,
    descriptionMaxLines: Int = Int.MAX_VALUE
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = shapes,
    ) {
        ListItem(

            leadingContent = leading,
            colors = ListItemDefaults.colors(
                containerColor = itemBgColor
            ),
            headlineContent = { Text(headline) },
            supportingContent = {
                if (description != null) {
                    Text(
                        description,
                        maxLines = descriptionMaxLines,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }
        )
    }
}
