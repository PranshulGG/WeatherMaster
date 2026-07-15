package com.pranshulgg.weather_master_app.feature.locations.components

import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
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
                title = stringResource(R.string.action_delete),
                onClick = {
                    onDelete()
                }
            ),
            SettingTile.ActionTile(
                leading = { SettingsTileIcon(R.drawable.home_pin_24px) },
                title = stringResource(R.string.action_set_default),
                onClick = {
                    onSetAsDefault()
                }
            )
        )
    )
}