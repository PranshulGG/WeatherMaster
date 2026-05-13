package com.pranshulgg.weathermaster.feature.settings.language

import androidx.appcompat.app.AppCompatDelegate
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.core.os.LocaleListCompat
import androidx.navigation.NavController
import com.pranshulgg.weathermaster.R
import com.pranshulgg.weathermaster.core.ui.components.AvatarCheck
import com.pranshulgg.weathermaster.core.ui.components.AvatarMonogram
import com.pranshulgg.weathermaster.core.ui.components.LargeTopBarScaffold
import com.pranshulgg.weathermaster.core.ui.components.NavigateUpBtn
import com.pranshulgg.weathermaster.core.ui.components.SettingSection
import com.pranshulgg.weathermaster.core.ui.components.SettingTile
import com.pranshulgg.weathermaster.core.utils.locale.getAppLocalLocales
import com.pranshulgg.weathermaster.core.utils.locale.getCurrentAppLocale


@Composable
fun LanguageScreen(navController: NavController) {


    val languageList = getAppLocalLocales()
    val uriHandler = LocalUriHandler.current

    val currentAppLocale =
        remember {
            mutableStateOf(
                getCurrentAppLocale().toLanguageTag()
            )
        }


    LargeTopBarScaffold(
        title = stringResource(R.string.setting_language),
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
                        title = stringResource(R.string.setting_translate_app),
                        description = stringResource(R.string.setting_translate_app_secondary),
                        onClick = {
                            uriHandler.openUri("https://crowdin.com/project/weathermaster")
                        }
                    )
                ))

            SettingSection(
                tiles = languageList.map {
                    val selected = it.value == currentAppLocale.value
                    SettingTile.ActionTile(
                        leading = {
                            if (selected) AvatarCheck(
                                containerColor = MaterialTheme.colorScheme.secondary,
                                contentColor = MaterialTheme.colorScheme.onSecondary
                            ) else AvatarMonogram(it.code)
                        },
                        title = it.name,
                        description = it.nativeName,
                        colorDesc = if (selected) MaterialTheme.colorScheme.onSecondaryContainer.copy(
                            0.8f
                        ) else MaterialTheme.colorScheme.onSurfaceVariant,
                        selected = selected,
                        onClick = {
                            currentAppLocale.value = it.value
                            setLanguage(it.value)
                        }
                    )
                }
            )
        }
    }

}

private fun setLanguage(languageCode: String) {
    AppCompatDelegate.setApplicationLocales(LocaleListCompat.forLanguageTags(languageCode))
}