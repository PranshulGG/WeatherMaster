package com.pranshulgg.weather_master_app.feature.settings.about

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.ListItem
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.navigation.NavController
import com.pranshulgg.weather_master_app.R
import com.pranshulgg.weather_master_app.core.ui.components.Gap
import com.pranshulgg.weather_master_app.core.ui.components.LargeTopBarScaffold
import com.pranshulgg.weather_master_app.core.ui.components.NavigateUpBtn
import com.pranshulgg.weather_master_app.core.ui.components.SettingSection
import com.pranshulgg.weather_master_app.core.ui.components.SettingTile
import com.pranshulgg.weather_master_app.core.ui.components.SettingsTileIcon
import com.pranshulgg.weather_master_app.core.ui.components.Symbol
import com.pranshulgg.weather_master_app.core.ui.navigation.NavRoutes
import com.pranshulgg.weather_master_app.core.ui.theme.ShapeRadius
import kotlinx.coroutines.launch

@Composable
fun AboutScreen(navController: NavController) {
    val context = LocalContext.current
    val packageInfo = context.packageManager.getPackageInfo(context.packageName, 0)
    val viewModel: AboutScreenViewModel = hiltViewModel()
    val scope = rememberCoroutineScope()

    val isLoadingNewVersion = viewModel.loading
    val uriHandler = LocalUriHandler.current
    LargeTopBarScaffold(
        title = stringResource(R.string.setting_about_app),
        navigationIcon = { NavigateUpBtn(navController) },
    ) { paddingValues ->
        Column(
            modifier =
                Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(paddingValues)
        ) {
            AppVersionTile(
                description = "v${packageInfo.versionName} • ${packageInfo.versionCode}",
                isLoadingNewVersion = isLoadingNewVersion,
                onClick = {
                    scope.launch {
                        viewModel.isNewVersionAvailable(
                            "v${packageInfo.versionName}",
                            onAction = { uriHandler.openUri("https://github.com/PranshulGG/WeatherMaster/releases/latest") })
                    }
                }
            )
            Gap(10.dp)
            SettingSection(
                tiles = listOf(
                    SettingTile.ActionTile(
                        leading = { SettingsTileIcon(R.drawable.article_24px) },
                        title = stringResource(R.string.about_terms_conditions),
                        onClick = { navController.navigate(NavRoutes.TERMS_CONDITIONS) }
                    ),
                    SettingTile.ActionTile(
                        leading = { SettingsTileIcon(R.drawable.policy_24px) },
                        title = stringResource(R.string.about_privacy_policy),
                        onClick = { navController.navigate(NavRoutes.PRIVACY_POLICY) }
                    ),
                    SettingTile.ActionTile(
                        leading = { SettingsTileIcon(R.drawable.license_24px) },
                        title = stringResource(R.string.about_license),
                        onClick = { navController.navigate(NavRoutes.LICENSE) }
                    ),
                    SettingTile.ActionTile(
                        leading = { SettingsTileIcon(R.drawable.bug_report_24px) },
                        title = stringResource(R.string.about_create_issue),
                        onClick = { uriHandler.openUri("https://github.com/PranshulGG/WeatherMaster/issues/new") }
                    ),
                    SettingTile.ActionTile(
                        leading = { SettingsTileIcon(R.drawable.github_invertocat_black) },
                        title = stringResource(R.string.about_changelog),
                        onClick = { uriHandler.openUri("https://github.com/PranshulGG/WeatherMaster/releases/latest") }
                    ),
                    SettingTile.ActionTile(
                        leading = { SettingsTileIcon(R.drawable.mail_24px) },
                        title = "Email",
                        onClick = { uriHandler.openUri("mailto:pranshul.devmain@gmail.com") }
                    ),

                    )
            )
        }
    }
}

@Composable
private fun AppVersionTile(
    description: String,
    onClick: () -> Unit,
    isLoadingNewVersion: Boolean = false
) {

    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        shape = RoundedCornerShape(ShapeRadius.Large),
    ) {
        ListItem(
            leadingContent = {
                Image(
                    painter = painterResource(R.drawable.app_icon_512px),
                    contentDescription = null,
                    Modifier.size(40.dp)
                )
            },
            colors = ListItemDefaults.colors(
                containerColor = MaterialTheme.colorScheme.tertiaryContainer
            ),
            headlineContent = {
                Text(
                    "WeatherMaster",
                    color = MaterialTheme.colorScheme.onTertiaryContainer,
                    style = MaterialTheme.typography.bodyLarge
                )
            },
            supportingContent = {
                Text(
                    description,
                    color = MaterialTheme.colorScheme.onTertiaryContainer.copy(alpha = 0.7f),
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Bold
                )
            },
            trailingContent = {
                IconButton(
                    enabled = !isLoadingNewVersion,
                    onClick = onClick,
                    colors = IconButtonDefaults.iconButtonColors(
                        containerColor = MaterialTheme.colorScheme.tertiary
                    ),
                    shapes = IconButtonDefaults.shapes()
                ) {
                    Symbol(R.drawable.refresh_24px, color = MaterialTheme.colorScheme.onTertiary)
                }
            }
        )
    }
}