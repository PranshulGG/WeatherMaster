package com.pranshulgg.weather_master_app.feature.settings.about

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.navigation.NavController
import com.pranshulgg.weather_master_app.R
import com.pranshulgg.weather_master_app.core.ui.components.Gap
import com.pranshulgg.weather_master_app.core.ui.components.Symbol
import com.pranshulgg.weather_master_app.core.ui.components.WeatherIconBox
import com.pranshulgg.weather_master_app.core.ui.navigation.NavRoutes
import com.pranshulgg.weather_master_app.core.ui.snackbar.SnackbarManager
import com.pranshulgg.weather_master_app.core.ui.theme.weatherMasterTitleFont
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun AboutScreen(navController: NavController) {
    val context = LocalContext.current
    val packageInfo = context.packageManager.getPackageInfo(context.packageName, 0)
    val btnSize = ButtonDefaults.MediumContainerHeight
    val viewModel: AboutScreenViewModel = hiltViewModel()
    val scope = rememberCoroutineScope()

    val isLoadingNewVersion = viewModel.loading
    val uriHandler = LocalUriHandler.current

    Scaffold(
        containerColor = MaterialTheme.colorScheme.surfaceContainer
    ) { pad ->
        Box(Modifier.fillMaxSize()) {
            Column(
                Modifier
                    .align(Alignment.Center)
                    .padding(
                        start = 16.dp,
                        end = 16.dp,
                        top = pad.calculateTopPadding()
                    ),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                WeatherIconBox(
                    R.drawable.app_icon_512px,
                    size = 150.dp,
                    Modifier.border(
                        2.dp,
                        color = MaterialTheme.colorScheme.outlineVariant,
                        shape = CircleShape
                    )
                )
                Gap(12.dp)
                Text(
                    "WeatherMaster",
                    style = MaterialTheme.typography.displayMedium,
                    color = MaterialTheme.colorScheme.onSurface,
                    fontFamily = weatherMasterTitleFont
                )
                Gap(8.dp)

                packageInfo?.let {
                    VersionPill("v${it.versionName}", it.versionCode.toString())
                }
                Spacer(Modifier.fillMaxHeight(0.6f))
            }

            Column(
                Modifier
                    .align(Alignment.BottomCenter)
                    .padding(horizontal = 16.dp)
            ) {
                FlowRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedButton(
                        onClick = {
                            navController.navigate(NavRoutes.TERMS_CONDITIONS)
                        },
                        text = stringResource(R.string.about_terms_conditions),
                        icon = R.drawable.article_24px
                    )
                    OutlinedButton(
                        onClick = {
                            navController.navigate(NavRoutes.PRIVACY_POLICY)
                        },
                        text = stringResource(R.string.about_privacy_policy),
                        icon = R.drawable.policy_24px
                    )
                    OutlinedButton(
                        onClick = {
                            navController.navigate(NavRoutes.LICENSE)
                        },
                        text = stringResource(R.string.about_license),
                        icon = R.drawable.license_24px
                    )
                    OutlinedButton(
                        onClick = {
                            uriHandler.openUri("https://github.com/PranshulGG/WeatherMaster/issues/new")
                        },
                        text = stringResource(R.string.about_create_issue),
                        icon = R.drawable.bug_report_24px
                    )
                    OutlinedButton(
                        onClick = {
                            uriHandler.openUri("https://github.com/PranshulGG/WeatherMaster")
                        },
                        text = stringResource(R.string.about_view_on_github),
                        icon = R.drawable.github_invertocat_black
                    )
                }
                Gap(24.dp)
                Button(
                    onClick = {
                        scope.launch {
                            viewModel.isNewVersionAvailable(
                                "v${packageInfo.versionName}",
                                onAction = { uriHandler.openUri("https://github.com/PranshulGG/WeatherMaster/releases/latest") })
                        }
                    },
                    modifier = Modifier
                        .heightIn(btnSize)
                        .fillMaxWidth(),
                    contentPadding = ButtonDefaults.contentPaddingFor(btnSize),
                    shapes = ButtonDefaults.shapes(),
                    enabled = !isLoadingNewVersion
                ) {
                    Text(
                        stringResource(R.string.about_check_for_updates),
                        style = ButtonDefaults.textStyleFor(btnSize)
                    )
                }

                Gap(WindowInsets.systemBars.asPaddingValues().calculateBottomPadding() + 30.dp)
            }

            Background()
        }
    }
}


@Composable
private fun VersionPill(versionName: String, versionCode: String) {
    Row() {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .height(32.dp)
                .background(
                    MaterialTheme.colorScheme.tertiaryContainer, shape = CircleShape
                )
        ) {
            Text(
                versionName,
                modifier = Modifier.padding(horizontal = 10.dp),
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onTertiaryContainer
            )
        }
        Gap(horizontal = 5.dp)
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .size(32.dp)
                .background(
                    MaterialTheme.colorScheme.tertiary, shape = CircleShape
                )
        ) {
            Text(
                versionCode,
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onTertiary
            )
        }
    }
}


@Composable
private fun Background() {
    Box(modifier = Modifier.fillMaxSize()) {

        Box(
            Modifier
                .align(Alignment.TopStart)
                .rotate(60f)
                .alpha(0.2f)
        ) {
            WeatherIconBox(R.drawable.weather_very_hot, size = 150.dp)
        }

        Box(
            Modifier
                .align(Alignment.TopEnd)
                .offset(y = 100.dp)
                .rotate(60f)
                .alpha(0.1f)
        ) {
            WeatherIconBox(R.drawable.weather_clear_night, size = 150.dp)
        }

        Box(
            Modifier
                .align(Alignment.TopStart)
                .offset(y = 500.dp)
                .rotate(60f)
                .alpha(0.1f)
        ) {
            WeatherIconBox(R.drawable.weather_clear_day, size = 150.dp)
        }

        Box(
            Modifier
                .align(Alignment.BottomEnd)
                .rotate(60f)
                .alpha(0.1f)
        ) {
            WeatherIconBox(R.drawable.weather_very_cold, size = 150.dp)
        }
    }
}

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
private fun OutlinedButton(onClick: () -> Unit, text: String, icon: Int) {
    val btnSize = 48.dp

    OutlinedButton(
        onClick = onClick,
        modifier = Modifier
            .heightIn(btnSize),
        contentPadding = ButtonDefaults.contentPaddingFor(btnSize),
        shapes = ButtonDefaults.shapes()
    ) {
        Symbol(
            icon,
            size = ButtonDefaults.iconSizeFor(btnSize),
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Gap(horizontal = ButtonDefaults.iconSpacingFor(btnSize))
        Text(
            text,
            style = ButtonDefaults.textStyleFor(btnSize),
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}