package com.pranshulgg.weather_master_app.feature.blocks.screens.humidity

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ListItem
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.navigation.NavController
import com.pranshulgg.weather_master_app.R
import com.pranshulgg.weather_master_app.core.model.weather.TemperatureUnit
import com.pranshulgg.weather_master_app.core.model.weather.uv.toColor
import com.pranshulgg.weather_master_app.core.model.weather.uv.toLabel
import com.pranshulgg.weather_master_app.core.ui.components.AvatarIcon
import com.pranshulgg.weather_master_app.core.ui.components.Gap
import com.pranshulgg.weather_master_app.core.ui.components.LargeTopBarScaffold
import com.pranshulgg.weather_master_app.core.ui.components.NavigateUpBtn
import com.pranshulgg.weather_master_app.core.ui.components.Symbol
import com.pranshulgg.weather_master_app.core.utils.formatters.toDateString
import com.pranshulgg.weather_master_app.core.utils.weather.forecast.findMatchingHourly
import com.pranshulgg.weather_master_app.feature.blocks.BlocksScreenViewModel
import com.pranshulgg.weather_master_app.feature.blocks.components.AboutCard
import com.pranshulgg.weather_master_app.feature.blocks.components.AboutCardText
import com.pranshulgg.weather_master_app.feature.blocks.components.NoHourlyDataAvailable
import com.pranshulgg.weather_master_app.feature.blocks.components.ScaleCard
import com.pranshulgg.weather_master_app.feature.blocks.screens.humidity.components.DewPointHourlyCard
import com.pranshulgg.weather_master_app.feature.blocks.screens.humidity.components.HumidityHourlyCard

@Composable
fun HumidityScreen(navController: NavController, index: Int = 0, locationId: String) {

    val viewModel: BlocksScreenViewModel = hiltViewModel()

    LaunchedEffect(Unit) {
        viewModel.getUnitsOnce()
        viewModel.getWeather(locationId)
    }


    val uiState = viewModel.uiState.value
    val weather = uiState.weather
    val hourly = weather?.hourly ?: return
    val units = uiState.units
    val time = if (index != 0) weather.daily[index].time else weather.current.time
    val context = LocalContext.current
    val data = findMatchingHourly(
        hourly,
        time,
        weather.location.source,
        weather.location.timezone,
        keepPastHour = index == 0


    )

    val dewPointScaleText = listOf(
        "Dry and comfortable", "Comfortable", "Sticky", "Muggy", "Uncomfortable"
    )


    val dewPointScaleTemps = listOf(
        Pair("~ < 10°C", "< 50°F"),
        Pair("~ 10-15°C", "~ 50-59°F"),
        Pair("~ 15-20°C:", "~ 59-68°F"),
        Pair("~ 20-24°C", "~ 68-75°F"),
        Pair("~ > 24°C", "~ > 75°F")
    )

    val dewPointScaleColors = listOf(
        Color(0xFF1565C0),
        Color(0xFF42A5F5),
        Color(0xFF66BB6A),
        Color(0xFFFDD835),
        Color(0xFFFB8C00)
    )

    val date = toDateString(weather.daily[index].time, weather.location.timezone)
    val dewPointData = data.map { it.dewPoint }

    val zoneId = weather.location.timezone
    val humidityData = data.map { it.humidity }


    LargeTopBarScaffold(
        title = stringResource(R.string.weather_humidity),
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
            if (humidityData.isNotEmpty() && !humidityData.contains(null)) {
                HumidityHourlyCard(data, zoneId)
            } else {
                NoHourlyDataAvailable()
            }
            Gap(14.dp)
            AboutCard {
                AboutCardText(stringResource(R.string.weather_about_humidity))
            }
            Gap(14.dp)
            DewPointHeader()
            if (dewPointData.isNotEmpty() && !dewPointData.contains(null)) {
                DewPointHourlyCard(data, zoneId, units.tempUnit, context)
            } else {
                NoHourlyDataAvailable()
            }
            Gap(14.dp)
            AboutCard {
                AboutCardText(stringResource(R.string.weather_about_dewpoint))
            }
            Gap(14.dp)
            ScaleCard {
                dewPointScaleTemps.forEachIndexed { index, pair ->
                    ListItem(
                        modifier = Modifier.height(45.dp),
                        colors = ListItemDefaults.colors(containerColor = Color.Transparent),
                        leadingContent = {
                            AvatarIcon(
                                R.drawable.wb_sunny_24px,
                                containerColor = dewPointScaleColors[index],
                                contentColor = Color.White
                            )
                        },
                        content = { Text(dewPointScaleText[index]) },
                        trailingContent = {
                            Text(
                                if (units.tempUnit == TemperatureUnit.FAHRENHEIT) pair.second else pair.first,
                                color = MaterialTheme.colorScheme.onSurface,
                                style = MaterialTheme.typography.bodyLarge
                            )
                        }
                    )
                }


            }
            Gap(WindowInsets.systemBars.asPaddingValues().calculateBottomPadding() + 30.dp)

        }
    }
}


@Composable
private fun DewPointHeader() {
    Row(
        horizontalArrangement = Arrangement.spacedBy(
            5.dp, alignment = Alignment.CenterHorizontally
        ),
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.padding(start = 18.dp, bottom = 6.dp)
    ) {
        Symbol(
            R.drawable.dew_point_24px,
            color = MaterialTheme.colorScheme.secondary
        )
        Text(
            stringResource(R.string.weather_dew_point),
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.secondary
        )
    }


}