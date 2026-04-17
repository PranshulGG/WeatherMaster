package com.pranshulgg.weathermaster.core.ui.components.tiles

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ListItem
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.pranshulgg.weathermaster.R
import com.pranshulgg.weathermaster.core.ui.components.Symbol

@Composable
fun SwitchTile(
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    headline: String,
    description: String? = null,
    leading: @Composable (() -> Unit)? = null,
    shapes: RoundedCornerShape,
    switchEnabled: Boolean = true,
    itemBgColor: Color
) {


    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = shapes,
    ) {
        ListItem(

            modifier = if (switchEnabled) {
                Modifier.clickable { onCheckedChange(!checked) }
            } else {
                Modifier
            },

            colors = ListItemDefaults.colors(
                containerColor = itemBgColor
            ),
            leadingContent = leading,
            headlineContent = { Text(headline) },
            supportingContent = {
                if (description != null) Text(description)
            },
            trailingContent = {
                Switch(
                    enabled = switchEnabled,
                    checked = checked,
                    onCheckedChange = { newChecked ->
                        if (checked != newChecked) {
                            onCheckedChange(newChecked)
                        }
                    },
                    thumbContent = if (checked) {
                        {
                            Symbol(
                                R.drawable.check_24px,
                                size = SwitchDefaults.IconSize,
                                color = MaterialTheme.colorScheme.primary
                            )

                        }
                    } else {
                        {
                            Symbol(
                                R.drawable.close_24px,
                                size = SwitchDefaults.IconSize,
                                color = MaterialTheme.colorScheme.surfaceContainerHighest
                            )

                        }
                    }
                )
            }
        )
    }

}


@Composable
fun SingleSwitchTile(
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    headline: String,
    description: String? = null,
    leading: @Composable (() -> Unit)? = null,
    switchEnabled: Boolean = true,
    itemBgColor: Color

) {

    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(50.dp),
    ) {
        ListItem(

            modifier = if (switchEnabled) {
                Modifier
                    .clickable { onCheckedChange(!checked) }
                    .padding(start = 10.dp)
            } else {
                Modifier.padding(start = 10.dp)
            },

            leadingContent = leading,
            headlineContent = { Text(headline) },
            supportingContent = {
                if (description != null) Text(description)
            },
            trailingContent = {
                Switch(
                    enabled = switchEnabled,
                    checked = checked,
                    onCheckedChange = { newChecked ->
                        if (checked != newChecked) {
                            onCheckedChange(newChecked)
                        }
                    },
                    thumbContent = if (checked) {
                        {
                            Symbol(
                                R.drawable.check_24px,
                                size = SwitchDefaults.IconSize,
                                color = MaterialTheme.colorScheme.primary
                            )

                        }
                    } else {
                        {
                            Symbol(
                                R.drawable.close_24px,
                                size = SwitchDefaults.IconSize,
                                color = itemBgColor
                            )

                        }
                    }
                )
            }
        )
    }

}