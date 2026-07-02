package com.pranshulgg.weather_master_app.core.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.ExperimentalTextApi
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontVariation
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.pranshulgg.weather_master_app.R

@OptIn(ExperimentalTextApi::class)
val weatherMasterTitleFont = FontFamily(
    Font(
        R.font.google_sans_flex, FontWeight.Bold, variationSettings = FontVariation.Settings(
            FontVariation.Setting("ROND", 100f),
            FontVariation.Setting("wght", 1000f),
        )
    ),
)

@OptIn(ExperimentalTextApi::class)
val googleSansFlex = FontFamily(
    Font(
        R.font.google_sans_flex, FontWeight.Medium, variationSettings = FontVariation.Settings(
            FontVariation.Setting("ROND", 100f),
            FontVariation.Setting("wght", 500f),
        )
    ),
)


fun getAppTypography(useGoogleSans: Boolean): Typography {

    val selectedFamily = if (useGoogleSans) googleSansFlex else null

    val appTypography = Typography().run {
        copy(
            displayLarge = displayLarge.copy(
                fontWeight = FontWeight.Normal,
                fontSize = 57.sp,
                lineHeight = 64.sp,
                fontFamily = selectedFamily
            ),
            displayMedium = displayMedium.copy(
                fontWeight = FontWeight.Normal,
                fontSize = 45.sp,
                lineHeight = 52.sp,
                fontFamily = selectedFamily
            ),
            displaySmall = displaySmall.copy(
                fontWeight = FontWeight.Normal,
                fontSize = 36.sp,
                lineHeight = 44.sp,
                fontFamily = selectedFamily
            ),
            headlineLarge = headlineLarge.copy(
                fontWeight = FontWeight.Normal,
                fontSize = 32.sp,
                lineHeight = 40.sp,
                fontFamily = selectedFamily
            ),
            headlineMedium = headlineMedium.copy(
                fontWeight = FontWeight.Normal,
                fontSize = 28.sp,
                lineHeight = 36.sp,
                fontFamily = selectedFamily
            ),
            headlineSmall = headlineSmall.copy(
                fontWeight = FontWeight.Normal,
                fontSize = 24.sp,
                lineHeight = 32.sp,
                fontFamily = selectedFamily
            ),
            titleLarge = titleLarge.copy(
                fontWeight = FontWeight.Normal,
                fontSize = 22.sp,
                lineHeight = 28.sp,
                fontFamily = selectedFamily
            ),
            titleMedium = titleMedium.copy(
                fontWeight = FontWeight.Medium,
                fontSize = 16.sp,
                lineHeight = 24.sp,
                fontFamily = selectedFamily
            ),
            titleSmall = titleSmall.copy(
                fontWeight = FontWeight.Medium,
                fontSize = 14.sp,
                lineHeight = 20.sp,
                fontFamily = selectedFamily
            ),
            bodyLarge = bodyLarge.copy(
                fontWeight = FontWeight.Normal,
                fontSize = 16.sp,
                lineHeight = 24.sp,
                fontFamily = selectedFamily
            ),
            bodyMedium = bodyMedium.copy(
                fontWeight = FontWeight.Normal,
                fontSize = 14.sp,
                lineHeight = 20.sp,
                fontFamily = selectedFamily
            ),
            bodySmall = bodySmall.copy(
                fontWeight = FontWeight.Normal,
                fontSize = 12.sp,
                lineHeight = 16.sp,
                fontFamily = selectedFamily
            ),
            labelLarge = labelLarge.copy(
                fontWeight = FontWeight.Medium,
                fontSize = 14.sp,
                lineHeight = 20.sp,
                fontFamily = selectedFamily
            ),
            labelMedium = labelMedium.copy(
                fontWeight = FontWeight.Medium,
                fontSize = 12.sp,
                lineHeight = 16.sp,
                fontFamily = selectedFamily
            ),
            labelSmall = labelSmall.copy(
                fontWeight = FontWeight.Medium,
                fontSize = 11.sp,
                lineHeight = 16.sp,
                fontFamily = selectedFamily
            ),
        )
    }

    return appTypography
}

