package com.pranshulgg.weather_master_app.feature.locations.components

import androidx.compose.runtime.Composable
import com.pranshulgg.weather_master_app.R
import com.pranshulgg.weather_master_app.core.ui.components.SettingSection
import com.pranshulgg.weather_master_app.core.ui.components.SettingTile
import com.pranshulgg.weather_master_app.core.ui.components.SettingsTileIcon

@Composable
fun LocationScreenSheetContent(
    locationName: String,
    onDelete: () -> Unit,
    onSetAsDefault: () -> Unit
) {
    SettingSection(
        isModalOption = true,
        title = locationName,
        tiles = listOf(
            SettingTile.ActionTile(
                leading = { SettingsTileIcon(R.drawable.delete_24px) },
                title = "Delete",
                onClick = {
                    onDelete()
                }
            ),
            SettingTile.ActionTile(
                leading = { SettingsTileIcon(R.drawable.home_pin_24px) },
                title = "Set as default",
                onClick = {
                    onSetAsDefault()
                }
            )
        )
    )
}