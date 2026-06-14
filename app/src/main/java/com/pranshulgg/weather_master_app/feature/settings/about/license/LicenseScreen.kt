package com.pranshulgg.weather_master_app.feature.settings.about.license

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
fun LicenseScreen(navController: NavController) {

    LargeTopBarScaffold(
        title = stringResource(R.string.about_license),
        navigationIcon = { NavigateUpBtn(navController) },
    ) { paddingValues ->

        Column(
            Modifier
                .padding(
                    top = paddingValues.calculateTopPadding(),
                    start = 16.dp,
                    end = 16.dp
                )
                .verticalScroll(rememberScrollState())
        ) {

            Text(
                text = "GNU General Public License v3.0",
                style = MaterialTheme.typography.headlineSmall,
                color = MaterialTheme.colorScheme.primary
            )

            Gap(8.dp)

            Text(
                text =
                    "This application is licensed under the GNU General Public License Version 3 (GPLv3).\n\n" +
                            "You are free to use, study, modify, and distribute this software under the terms of the GPLv3 license.\n\n" +
                            "Any modified versions or redistributed copies must also remain open-source and include the same license.\n\n" +
                            "This software is provided \"as is\", without warranty of any kind, including merchantability or fitness for a particular purpose.",
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurface
            )

            HorizontalDivider(
                Modifier.padding(vertical = 12.dp)
            )

            Text(
                text = "Your Rights",
                style = MaterialTheme.typography.titleLarge,
                color = MaterialTheme.colorScheme.primary
            )

            Gap(6.dp)

            Text(
                text =
                    "• Use the software for any purpose\n" +
                            "• Access and modify the source code\n" +
                            "• Share copies of the software\n" +
                            "• Distribute modified versions under GPLv3",
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurface
            )

            HorizontalDivider(
                Modifier.padding(vertical = 12.dp)
            )

            Text(
                text = "Conditions",
                style = MaterialTheme.typography.titleLarge,
                color = MaterialTheme.colorScheme.primary
            )

            Gap(6.dp)

            Text(
                text =
                    "• You must include the GPLv3 license when redistributing\n" +
                            "• Modified versions must clearly state changes made\n" +
                            "• Derivative works must also be licensed under GPLv3\n" +
                            "• Source code must remain available",
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurface
            )

            HorizontalDivider(
                Modifier.padding(vertical = 12.dp)
            )

            Text(
                text = "Full License",
                style = MaterialTheme.typography.titleLarge,
                color = MaterialTheme.colorScheme.primary
            )

            Gap(6.dp)

            Text(
                text = buildAnnotatedString {
                    append("You can read the full GNU GPLv3 license at: ")
                    withLink(
                        LinkAnnotation.Url("https://www.gnu.org/licenses/gpl-3.0.en.html")
                    ) {
                        withStyle(
                            style = SpanStyle(
                                fontWeight = FontWeight.Bold,
                                textDecoration = TextDecoration.Underline,
                                color = MaterialTheme.colorScheme.tertiary
                            ),

                            ) {
                            append("https://www.gnu.org/licenses/gpl-3.0.en.html")
                        }
                    }
                },
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurface
            )

            Gap(WindowInsets.systemBars.asPaddingValues().calculateBottomPadding() + 30.dp)

        }
    }
}