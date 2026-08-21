package com.pranshulgg.weather_master_app.feature.settings

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.BasicAlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ListItem
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.pranshulgg.weather_master_app.R
import com.pranshulgg.weather_master_app.core.ui.components.DialogBasic
import com.pranshulgg.weather_master_app.core.ui.components.LargeTopBarScaffold
import com.pranshulgg.weather_master_app.core.ui.components.NavigateUpBtn
import com.pranshulgg.weather_master_app.core.ui.components.SettingSection
import com.pranshulgg.weather_master_app.core.ui.components.SettingTile
import com.pranshulgg.weather_master_app.core.ui.components.SettingsTileIcon
import com.pranshulgg.weather_master_app.core.ui.components.WeatherIconBox
import com.pranshulgg.weather_master_app.core.ui.navigation.NavRoutes
import com.pranshulgg.weather_master_app.core.utils.locale.getCurrentAppLocale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(navController: NavController) {

    val uriHandler = LocalUriHandler.current

    var isDonationDialogOpen by remember { mutableStateOf(false) }

    LargeTopBarScaffold(
        title = stringResource(R.string.settings),
        navigationIcon = { NavigateUpBtn(navController) },
    ) { paddingValues ->
        Column(
            modifier =
                Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(paddingValues),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {


            SettingSection(
                tiles = listOf(
                    SettingTile.ActionTile(
                        leading = { SettingsTileIcon(R.drawable.format_paint_24px) },
                        title = stringResource(R.string.setting_appearance),
                        description = stringResource(R.string.setting_appearance_secondary),
                        onClick = { navController.navigate(NavRoutes.APPEARANCE) }
                    ),
                    SettingTile.ActionTile(
                        leading = { SettingsTileIcon(R.drawable.favorite_24px) },
                        title = "Support",
                        description = "If you enjoy the app, consider supporting its development",
                        onClick = { isDonationDialogOpen = true }
                    )

                )
            )
            SettingSection(
                tiles = listOf(
                    SettingTile.ActionTile(
                        leading = { SettingsTileIcon(R.drawable.sync_24px) },
                        title = stringResource(R.string.setting_background_updates),
                        description = stringResource(R.string.setting_background_updates_secondary),
                        onClick = { navController.navigate(NavRoutes.BACKGROUND_UPDATES) }
                    ),
                    SettingTile.ActionTile(
                        leading = { SettingsTileIcon(R.drawable.nest_farsight_weather_24px) },
                        title = stringResource(R.string.weather_sources),
                        description = stringResource(R.string.setting_weather_sources_secondary),
                        onClick = {
                            navController.navigate(NavRoutes.SOURCES)
                        }
                    ),
                    SettingTile.ActionTile(
                        leading = { SettingsTileIcon(R.drawable.language_24px) },
                        title = stringResource(R.string.setting_language),
                        description = getCurrentAppLocale().displayName,
                        onClick = { navController.navigate(NavRoutes.LANGUAGE) }
                    ),
                    SettingTile.ActionTile(
                        leading = { SettingsTileIcon(R.drawable.cloud_download_24px) },
                        title = stringResource(R.string.setting_backup_restore),
                        description = stringResource(R.string.setting_backup_restore_secondary),
                        onClick = { navController.navigate(NavRoutes.BACKUP_RESTORE) }
                    ),
                )
            )
            SettingSection(
                tiles = listOf(
                    SettingTile.ActionTile(
                        leading = { SettingsTileIcon(R.drawable.discord_symbol_black) },
                        title = stringResource(R.string.setting_join_discord),
                        onClick = {
                            uriHandler.openUri("https://discord.gg/sSW2E4nqmn")
                        }
                    ),


                    SettingTile.ActionTile(
                        leading = { SettingsTileIcon(R.drawable.info_24px) },
                        title = stringResource(R.string.setting_about_app),
                        description = stringResource(R.string.setting_about_app_secondary),
                        onClick = {
                            navController.navigate(NavRoutes.ABOUT)
                        }
                    ),
                )
            )
        }
    }

    DialogBasic(
        show = isDonationDialogOpen,
        title = "Donate",
        dismissText = stringResource(R.string.action_cancel),
        showOnlyDismissAction = true,
        onConfirm = {},
        onDismiss = { isDonationDialogOpen = false },
    ) {
        Column(
        ) {
            DonateListItem(
                "Ko-fi",
                "Ko-fi.com/pranshulgg",
                R.drawable.kofi_symbol
            ) { uriHandler.openUri("https://ko-fi.com/pranshulgg") }
            DonateListItem(
                "Liberapay",
                "liberapay.com/PranshulGG",
                R.drawable.liberapay_logo,
            ) { uriHandler.openUri("https://liberapay.com/PranshulGG") }
            DonateListItem(
                "Github sponsors",
                "github.com/sponsors/PranshulGG",
                R.drawable.gitub_logo_colored,
            ) { uriHandler.openUri("https://github.com/sponsors/PranshulGG?frequency=one-time&sponsor=PranshulGG") }
        }
    }
}

@Composable
private fun DonateListItem(headline: String, description: String, icon: Int, onClick: () -> Unit) {
    ListItem(
        colors = ListItemDefaults.colors(containerColor = Color.Transparent),
        leadingContent = { WeatherIconBox(icon, size = 28.dp) },
        content = { Text(headline) },
        supportingContent = { Text(description) },
        modifier = Modifier
            .clickable(onClick = onClick)
            .padding(horizontal = 14.dp)
    )
}