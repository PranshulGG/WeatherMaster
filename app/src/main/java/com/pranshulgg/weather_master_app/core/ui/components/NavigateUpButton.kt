package com.pranshulgg.weather_master_app.core.ui.components

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.TooltipAnchorPosition
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.dp
import androidx.lifecycle.Lifecycle
import androidx.navigation.NavController
import com.pranshulgg.weather_master_app.R

@Composable
fun NavigateUpBtn(navController: NavController) {
    NavigateUpBtn(onClick = {
        if (navController.currentBackStackEntry?.lifecycle?.currentState == Lifecycle.State.RESUMED) {
            navController.popBackStack()
        }
    })
}

@OptIn(ExperimentalMaterial3ExpressiveApi::class, ExperimentalMaterial3Api::class)
@Composable
fun NavigateUpBtn(onClick: () -> Unit) {
    Tooltip(
        "Navigate up",
        preferredPosition = TooltipAnchorPosition.Below,
        spacing = 10.dp
    ) {
        IconButton(
            onClick = onClick, shapes = IconButtonDefaults.shapes()
        ) {
            Symbol(
                R.drawable.arrow_back_24px,
                desc = "arrow back icon",
                color = MaterialTheme.colorScheme.onSurface
            )
        }
    }

}