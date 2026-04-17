package com.pranshulgg.weathermaster.core.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import com.pranshulgg.weathermaster.R

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun ErrorContainer(
    showRetryAction: Boolean = true,
    onRetry: () -> Unit = {},
    containerColor: Color = MaterialTheme.colorScheme.surfaceContainer,
    errorDescription: String = "Something went wrong",
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .zIndex(100000f)
            .background(containerColor),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Symbol(R.drawable.info_24px, color = MaterialTheme.colorScheme.error, size = 54.dp)
        Gap(26.dp)
        Text(
            "Error occurred",
            fontSize = 26.sp,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center,
            color = MaterialTheme.colorScheme.onSurface
        )
        Gap(8.dp)
        Text(
            errorDescription,
            fontSize = 16.sp,
            fontWeight = FontWeight.Medium,
            textAlign = TextAlign.Center,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        if (showRetryAction) {
            Gap(34.dp)
            M3eButton(
                size = ButtonDefaults.MediumContainerHeight,
                text = "Try again",
                onClick = { onRetry() },
                icon = R.drawable.refresh_24px
            )
        }
    }
}