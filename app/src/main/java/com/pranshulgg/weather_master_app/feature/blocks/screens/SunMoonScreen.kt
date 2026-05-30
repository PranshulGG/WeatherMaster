package com.pranshulgg.weather_master_app.feature.blocks.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.navigation.NavController
import com.pranshulgg.weather_master_app.R
import com.pranshulgg.weather_master_app.core.prefs.LocalAppPrefs
import com.pranshulgg.weather_master_app.core.ui.components.Gap
import com.pranshulgg.weather_master_app.core.ui.components.LargeTopBarScaffold
import com.pranshulgg.weather_master_app.core.ui.components.NavigateUpBtn
import com.pranshulgg.weather_master_app.core.ui.theme.ShadowElevation
import com.pranshulgg.weather_master_app.core.utils.formatters.to12HourTimeString
import com.pranshulgg.weather_master_app.core.utils.formatters.to24HourTimeString
import com.pranshulgg.weather_master_app.core.utils.formatters.toDateString
import com.pranshulgg.weather_master_app.feature.blocks.BlocksScreenViewModel
import com.pranshulgg.weather_master_app.feature.blocks.components.AboutCard
import com.pranshulgg.weather_master_app.feature.blocks.components.AboutCardText
import com.pranshulgg.weather_master_app.feature.shared.components.blocks.MoonBlock
import com.pranshulgg.weather_master_app.feature.shared.components.blocks.SunBlock
import java.util.concurrent.TimeUnit


@Composable
fun SunMoonScreen(navController: NavController, index: Int, locationId: String) {
    val viewModel: BlocksScreenViewModel = hiltViewModel()

    LaunchedEffect(Unit) {
        viewModel.getUnitsOnce()
        viewModel.getWeather(locationId)
    }


    val uiState = viewModel.uiState.value
    val weather = uiState.weather
    val daily = uiState.weather?.daily ?: return
    val prefs = LocalAppPrefs.current
    val is24hr = prefs.is24HrTimeFormat

    val date = toDateString(daily[index].time, weather.location.timezone)

    val duskFormatted = if (is24hr) to24HourTimeString(
        daily[index].dusk,
        weather.location.timezone
    ) else to12HourTimeString(
        daily[index].dusk,
        weather.location.timezone,
        "hh:mm a"
    )
    val dawnFormatted = if (is24hr) to24HourTimeString(
        daily[index].dawn,
        weather.location.timezone
    ) else to12HourTimeString(
        daily[index].dawn,
        weather.location.timezone,
        "hh:mm a"
    )


    val dayLength = daily[index].sunset.minus(daily[index].sunrise)
    val dayLengthHrs = TimeUnit.MILLISECONDS.toHours(dayLength)


    LargeTopBarScaffold(
        title = stringResource(R.string.weather_sun_moon),
        navigationIcon = { NavigateUpBtn(navController) },
        actions = {
            Text(
                date,
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(end = 16.dp)
            )
        }
    ) { paddingValues ->
        Column(
            modifier =
                Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(paddingValues)
        ) {
            Surface(
                color = MaterialTheme.colorScheme.surfaceBright,
                shape = MaterialTheme.shapes.extraLarge,
                shadowElevation = ShadowElevation.level2,
                modifier = Modifier.padding(horizontal = 16.dp)
            ) {
                Row(
                    modifier = Modifier
                        .padding(16.dp)
                        .fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                        TextHeader(
                            header = stringResource(R.string.text_dawn),
                            text = dawnFormatted
                        )
                        TextHeader(
                            header = stringResource(R.string.text_dusk),
                            text = duskFormatted
                        )
                        TextHeader(
                            header = stringResource(R.string.text_day_length),
                            text = "$dayLengthHrs hr"
                        )
                    }
                    Box(Modifier.size(160.dp)) {
                        SunBlock(weather, index, prefs) {}
                    }
                }
            }
            Gap(14.dp)
            Surface(
                color = MaterialTheme.colorScheme.surfaceBright,
                shape = MaterialTheme.shapes.extraLarge,
                shadowElevation = ShadowElevation.level2,
                modifier = Modifier.padding(horizontal = 16.dp)
            ) {
                Row(
                    modifier = Modifier
                        .padding(16.dp)
                        .fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column(
                        verticalArrangement = Arrangement.spacedBy(6.dp),
                        modifier = Modifier.weight(1f),
                    ) {
                        TextHeader(
                            header = stringResource(R.string.moon_phase),
                            text = stringResource(daily[index].moonPhase.displayName)
                        )
                        Gap(4.dp)
                        Surface(
                            color = MaterialTheme.colorScheme.primary,
                            shape = CircleShape,
                            modifier = Modifier.size(80.dp),
                            border = BorderStroke(
                                width = 1.dp,
                                MaterialTheme.colorScheme.outlineVariant
                            )
                        ) {
                            Image(
                                painter = painterResource(daily[index].moonPhase.icon),
                                contentDescription = "",
                                colorFilter = ColorFilter.tint(
                                    MaterialTheme.colorScheme.surface
                                )
                            )
                        }
                    }
                    Box(Modifier.size(160.dp)) {
                        MoonBlock(weather, index, prefs) {}
                    }
                }
            }

            Gap(14.dp)
            AboutCard {
                AboutCardText(stringResource(R.string.weather_about_sun_moon_rise_set))
                AboutCardText(stringResource(R.string.weather_about_dawn_dusk))
            }
        }
    }
}

@Composable
private fun TextHeader(header: String, text: String) {
    Column() {
        Text(
            text = header,
            fontSize = 16.sp,
            color = MaterialTheme.colorScheme.primary,
            fontWeight = FontWeight.W700
        )
        Text(text, color = MaterialTheme.colorScheme.onSurface)
    }
}