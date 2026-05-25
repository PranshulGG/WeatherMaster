package com.pranshulgg.weather_master_app.feature.settings.about.privacy

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.LinkAnnotation
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.withLink
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.pranshulgg.weather_master_app.R
import com.pranshulgg.weather_master_app.core.ui.components.Gap
import com.pranshulgg.weather_master_app.core.ui.components.LargeTopBarScaffold
import com.pranshulgg.weather_master_app.core.ui.components.NavigateUpBtn


@Composable
fun PrivacyPolicyScreen(navController: NavController) {


    LargeTopBarScaffold(
        title = stringResource(R.string.about_privacy_policy),
        navigationIcon = { NavigateUpBtn(navController) },
    ) { paddingValues ->

        Column(
            Modifier
                .padding(top = paddingValues.calculateTopPadding(), start = 16.dp, end = 16.dp)
                .verticalScroll(
                    rememberScrollState()
                )
        ) {
            Text(
                text = "WeatherMaster is an open-source application. \n\n" +
                        "We respect your privacy. The application itself does not:",
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurface
            )
            Gap(6.dp)
            Text(
                text = "- Collect, store, or share personal information \n" +
                        "- Track your IP address, usage data, or device identifiers \n" +
                        "- Send personal data to us directly",
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurface
            )
            HorizontalDivider(Modifier.padding(vertical = 12.dp))
            Text(
                "Third-Party Services & APIs",
                style = MaterialTheme.typography.titleLarge,
                color = MaterialTheme.colorScheme.primary
            )
            Gap(8.dp)
            Text(
                text = "WeatherMaster relies on external weather and geolocation services to provide forecasts and location-based functionality. These third-party services may process certain technical information, such as:",
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurface
            )
            Gap(6.dp)
            Text(
                text = "- IP address \n" +
                        "- Approximate/Precise location data \n" +
                        "- Device or network-related information required for API requests \n\n" +
                        "This data processing is handled by the respective service providers under their own privacy policies. WeatherMaster itself does not control or store this data.",
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurface
            )
            HorizontalDivider(Modifier.padding(vertical = 12.dp))
            Text(
                "Location Permission",
                style = MaterialTheme.typography.titleLarge,
                color = MaterialTheme.colorScheme.primary
            )
            Gap(8.dp)
            Text(
                text = "- The app may request access to your device’s location in order to display local weather information \n" +
                        "- Granting location permission is optional \n" +
                        "- Your precise location is used locally within the app to fetch weather data from external APIs and is not stored by WeatherMaster",
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurface
            )
            HorizontalDivider(Modifier.padding(vertical = 12.dp))
            Text(
                "Children",
                style = MaterialTheme.typography.titleLarge,
                color = MaterialTheme.colorScheme.primary
            )
            Gap(8.dp)
            Text(
                text = "This application is not directed toward children under 13 and does not knowingly collect personal information.",
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurface
            )
            HorizontalDivider(Modifier.padding(vertical = 12.dp))
            Text(
                "Changes",
                style = MaterialTheme.typography.titleLarge,
                color = MaterialTheme.colorScheme.primary
            )
            Gap(8.dp)
            Text(
                text = "This Privacy Policy may be updated from time to time. Continued use of the app after changes become effective means you accept the revised policy.",
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurface
            )
            HorizontalDivider(Modifier.padding(vertical = 12.dp))
            Text(
                "Contact",
                style = MaterialTheme.typography.titleLarge,
                color = MaterialTheme.colorScheme.primary
            )
            Gap(8.dp)
            Text(
                text = buildAnnotatedString {
                    append("If you have any questions about privacy, please contact ")
                    withLink(
                        LinkAnnotation.Url(
                            "mailto:pranshul.devmain@gmail.com"
                        )
                    ) {
                        withStyle(
                            style = SpanStyle(
                                fontWeight = FontWeight.Bold,
                                textDecoration = TextDecoration.Underline,
                                color = MaterialTheme.colorScheme.tertiary
                            ),

                            ) {
                            append("pranshul.devmain@gmail.com")
                        }
                    }
                },
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurface
            )
            Gap(WindowInsets.systemBars.asPaddingValues().calculateBottomPadding() + 30.dp)
        }
    }
}