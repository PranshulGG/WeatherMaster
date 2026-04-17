package com.pranshulgg.weathermaster.core.ui.components.tiles

import androidx.compose.foundation.clickable
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
import com.pranshulgg.weathermaster.core.ui.theme.ShapeRadius

@Composable
fun ActionTile(
    headline: String,
    description: String? = null,
    leading: @Composable (() -> Unit)? = null,
    shapes: RoundedCornerShape,
    onClick: () -> Unit,
    colorDesc: Color = MaterialTheme.colorScheme.onSurfaceVariant,
    danger: Boolean = false,
    itemBgColor: Color,
    selected: Boolean = false,
    trailing: @Composable (() -> Unit)? = null
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = if (selected) RoundedCornerShape(ShapeRadius.Large) else shapes,
    ) {
        ListItem(
            modifier = Modifier.clickable(
                onClick = onClick
            ),
            leadingContent = leading,
            colors = ListItemDefaults.colors(
                containerColor = if (selected) MaterialTheme.colorScheme.secondaryContainer else if (danger) MaterialTheme.colorScheme.errorContainer else itemBgColor
            ),
            headlineContent = {
                Text(
                    headline,
                    color = if (selected) MaterialTheme.colorScheme.onSecondaryContainer else if (danger) MaterialTheme.colorScheme.onErrorContainer else MaterialTheme.colorScheme.onSurface,
                    style = MaterialTheme.typography.bodyLarge
                )
            },
            supportingContent = {
                if (description != null) {
                    Text(
                        description,
                        color = colorDesc,
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            },
            trailingContent = trailing
        )
    }
}
