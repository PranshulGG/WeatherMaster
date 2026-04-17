package com.pranshulgg.weathermaster.feature.main

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ListItem
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.pranshulgg.weathermaster.core.model.WeatherConditions
import com.pranshulgg.weathermaster.core.model.WeatherProviders
import com.pranshulgg.weathermaster.data.provider.WeatherRepositoryProvider
import com.pranshulgg.weathermaster.data.repository.WeatherRepository
import com.pranshulgg.weathermaster.feature.main.ui.FroggyCurrentWeatherCard
import com.pranshulgg.weathermaster.feature.main.ui.backgroundGradients
import com.pranshulgg.weathermaster.feature.shared.ui.HourlyCard

@Composable
fun MainScreen(navController: NavController) {

    val viewModel: MainScreenViewModel = hiltViewModel()
    val weather = viewModel.weather

    Scaffold { paddingValues ->

        Box() {

            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(brush = Brush.verticalGradient(backgroundGradients(WeatherConditions.CLEAR_SKY)))
            )


            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
            ) {
                FroggyCurrentWeatherCard(paddingValues, navController)

                Button(onClick = { viewModel.getWeather() }) {
                    Text(text = "Get Weather")
                }

                Text(
                    weather.toString()
                )
                HourlyCard()
            }
        }
    }
}



