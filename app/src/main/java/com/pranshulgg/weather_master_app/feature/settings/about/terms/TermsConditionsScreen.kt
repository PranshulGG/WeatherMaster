package com.pranshulgg.weather_master_app.feature.settings.about.terms

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
import androidx.compose.ui.text.font.FontStyle
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
import com.pranshulgg.weather_master_app.core.ui.theme.weatherMasterTitleFont

@Composable
fun TermsConditionsScreen(navController: NavController) {


    LargeTopBarScaffold(
        title = stringResource(R.string.about_terms_conditions),
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
                text = buildAnnotatedString {
                    append("These Terms & Conditions apply to the ")

                    withStyle(
                        style = SpanStyle(
                            fontWeight = FontWeight.Bold,
                            fontFamily = weatherMasterTitleFont
                        )
                    ) {
                        append("WeatherMaster")
                    }

                    append(" app (the \"Application\") for mobile devices.\n")

                    append("This app was created by ")

                    withStyle(
                        style = SpanStyle(fontWeight = FontWeight.Bold)
                    ) {
                        append("Pranshul")
                    }

                    append(" as an open-source. By using the Application, you agree to the following:")
                },
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurface
            )
            HorizontalDivider(Modifier.padding(vertical = 12.dp))
            Text(
                "Use of the Application",
                style = MaterialTheme.typography.titleLarge,
                color = MaterialTheme.colorScheme.primary
            )
            Gap(8.dp)
            Text(
                text = buildAnnotatedString {
                    append("- The Application is provided ")

                    withStyle(
                        style = SpanStyle(fontWeight = FontWeight.Bold)
                    ) {
                        append("as-is")
                    }

                    append(" ,free of charge, and without any guarantees of reliability, availability, or accuracy.")

                },
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurface
            )
            Gap(6.dp)
            Text(
                text = "- You may use, modify, and distribute the Application in accordance with its open-source license",
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurface
            )
            Gap(6.dp)
            Text(
                text = buildAnnotatedString {
                    append("- You may ")
                    withStyle(
                        style = SpanStyle(fontWeight = FontWeight.Bold)
                    ) {
                        append("not")
                    }
                    append(" misrepresent the origin of the Application or use its name/trademarks without permission.")
                },
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurface
            )
            HorizontalDivider(Modifier.padding(vertical = 12.dp))
            Text(
                "Data & Privacy",
                style = MaterialTheme.typography.titleLarge,
                color = MaterialTheme.colorScheme.primary
            )
            Gap(8.dp)
            Text(
                text = buildAnnotatedString {
                    append("- The Application does ")

                    withStyle(
                        style = SpanStyle(fontWeight = FontWeight.Bold)
                    ) {
                        append("not collect, store, or share")
                    }

                    append(" any personal information.")

                },
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurface
            )
            Gap(6.dp)
            Text(
                text = buildAnnotatedString {
                    append("- The only permission requested is ")
                    withStyle(
                        style = SpanStyle(fontWeight = FontWeight.Bold)
                    ) {
                        append("location access")
                    }
                    append(" ,which is optional and used solely within the Application to provide weather information. This data never leaves your device.")
                },
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurface
            )
            Gap(6.dp)
            Text(
                text = "- For more details, please see the Privacy Policy.",
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurface
            )
            HorizontalDivider(Modifier.padding(vertical = 12.dp))
            Text(
                "Liability",
                style = MaterialTheme.typography.titleLarge,
                color = MaterialTheme.colorScheme.primary
            )
            Gap(8.dp)
            Text(
                text = buildAnnotatedString {
                    append("- The Service Provider (Pranshul) is ")
                    withStyle(
                        style = SpanStyle(fontWeight = FontWeight.Bold)
                    ) {
                        append("not liable")
                    }
                    append(" for any direct or indirect damages, losses, or issues that may arise from using the Application.")
                },
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurface
            )
            Gap(6.dp)
            Text(
                text = "- You are responsible for ensuring your device is compatible and has sufficient internet and battery to use the Application.",
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurface
            )
            HorizontalDivider(Modifier.padding(vertical = 12.dp))
            Text(
                "Updates & Availability",
                style = MaterialTheme.typography.titleLarge,
                color = MaterialTheme.colorScheme.primary
            )
            Gap(8.dp)
            Text(
                text = "- The Application may be updated from time to time.",
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurface
            )
            Gap(6.dp)
            Text(
                text = "- There is no guarantee that the Application will always remain available, functional, or supported on all operating system versions.",
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurface
            )
            Gap(6.dp)
            Text(
                text = "- The Service Provider may discontinue the Application at any time without prior notice.",
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurface
            )
            HorizontalDivider(Modifier.padding(vertical = 12.dp))
            Text(
                "Changes to These Terms",
                style = MaterialTheme.typography.titleLarge,
                color = MaterialTheme.colorScheme.primary
            )
            Gap(8.dp)
            Text(
                text = "These Terms & Conditions may be updated in the future. Updates will be posted in the project repository or within the Application. Continued use of the Application means you accept any revised terms.",
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
                    append("If you have any questions about these Terms & Conditions, please contact ")
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
                    append(" for any direct or indirect damages, losses, or issues that may arise from using the Application.")
                },
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurface
            )
            Gap(WindowInsets.systemBars.asPaddingValues().calculateBottomPadding() + 30.dp)

        }
    }
}