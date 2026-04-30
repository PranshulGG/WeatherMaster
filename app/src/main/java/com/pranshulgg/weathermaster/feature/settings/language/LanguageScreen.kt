package com.pranshulgg.weathermaster.feature.settings.language

import android.util.Log
import androidx.appcompat.app.AppCompatDelegate
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.unit.dp
import androidx.core.os.LocaleListCompat
import androidx.navigation.NavController
import com.pranshulgg.weathermaster.core.ui.components.LargeTopBarScaffold
import com.pranshulgg.weathermaster.core.ui.components.NavigateUpBtn
import com.pranshulgg.weathermaster.core.ui.components.SettingSection
import com.pranshulgg.weathermaster.core.ui.components.SettingTile
import java.util.Locale


@Composable
fun LanguageScreen(navController: NavController) {


    val languageList = getLanguages()
    val configuration = LocalConfiguration.current

    val currentLanguageTag = remember {
        mutableStateOf(
            AppCompatDelegate.getApplicationLocales()[0]?.toLanguageTag() ?: "en"
        )
    }



    LargeTopBarScaffold(
        title = "Language",
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
                tiles = languageList.map {
                    SettingTile.ActionTile(
                        title = it.name,
                        description = it.nativeName,
                        selected = it.value == currentLanguageTag.value,
                        onClick = {
                            currentLanguageTag.value = it.value
                            setLanguage(it.value)
                        }
                    )
                }
            )
        }
    }

}

private fun setLanguage(lang: String) {
    val localeList = LocaleListCompat.forLanguageTags(lang)
    AppCompatDelegate.setApplicationLocales(localeList)
}
