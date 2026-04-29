package com.pranshulgg.weathermaster.feature.locations.components

import androidx.compose.runtime.Composable
import com.pranshulgg.weathermaster.R
import com.pranshulgg.weathermaster.core.ui.components.SettingSection
import com.pranshulgg.weathermaster.core.ui.components.SettingTile
import com.pranshulgg.weathermaster.core.ui.components.SettingsTileIcon
import com.pranshulgg.weathermaster.core.ui.snackbar.SnackbarManager

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