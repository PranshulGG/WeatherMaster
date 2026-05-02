package com.pranshulgg.weathermaster.feature.settings.appearance

import android.app.Activity
import android.app.LocaleManager
import android.content.Context
import android.os.Build
import android.os.LocaleList
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
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.core.content.edit
import androidx.core.os.LocaleListCompat
import androidx.navigation.NavController
import com.pranshulgg.weathermaster.R
import com.pranshulgg.weathermaster.core.prefs.LocalAppPrefs
import com.pranshulgg.weathermaster.core.ui.components.LargeTopBarScaffold
import com.pranshulgg.weathermaster.core.ui.components.NavigateUpBtn
import com.pranshulgg.weathermaster.core.ui.components.SettingSection
import com.pranshulgg.weathermaster.core.ui.components.SettingTile
import com.pranshulgg.weathermaster.core.ui.components.SettingsTileIcon
import java.util.Locale

@Composable
fun AppearanceScreen(navController: NavController) {

    val prefs = LocalAppPrefs.current
    val context = LocalContext.current

    LargeTopBarScaffold(
        title = stringResource(R.string.setting_appearance),
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
                title = stringResource(R.string.setting_appearance),
                tiles = listOf(
                    SettingTile.DialogOptionTile(
                        leading = { SettingsTileIcon(R.drawable.palette_24px) },
                        title = stringResource(R.string.setting_app_theme),
                        options = listOf(
                            "Dark", "Light", "System"
                        ),
                        displayOptions = listOf(
                            stringResource(R.string.setting_dark_theme),
                            stringResource(R.string.setting_light_theme),
                            stringResource(R.string.setting_system_theme),
                        ),
                        selectedOption = prefs.appTheme,
                        onOptionSelected = {
                            prefs.setAppTheme(it)
                        }
                    ),
                )
            )

            Button(onClick = {
                setLanguage(context, "zh-CN")
                (context as? Activity)?.recreate()
            }) {
                Text("change lang")
            }

            Button(onClick = {
                setLanguage(context, "em")
                (context as? Activity)?.recreate()
            }) {
                Text("change lang eng")
            }
        }
    }

}

fun setLanguage(context: Context, languageCode: String) {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
        context.getSystemService(LocaleManager::class.java).applicationLocales =
            LocaleList.forLanguageTags(languageCode)
    } else {

        AppCompatDelegate.setApplicationLocales(LocaleListCompat.forLanguageTags(languageCode))
    }
    saveLanguage(context, languageCode)
    Log.d("language_saved", "${LocaleList.forLanguageTags(languageCode)}")
}

fun saveLanguage(context: Context, languageCode: String) {
    val pref = context.getSharedPreferences("language_pref", Context.MODE_PRIVATE)
    pref.edit { putString("language", languageCode) }
}