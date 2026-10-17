package com.pranshulgg.weather_master_app.feature.main.ui

import androidx.compose.foundation.layout.width
import androidx.compose.material3.DismissibleNavigationDrawer
import androidx.compose.material3.DrawerState
import androidx.compose.material3.PermanentNavigationDrawer
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.pranshulgg.weather_master_app.feature.blocks.screens.precipitation.components.NowcastDragState

@Composable
fun NavigationDrawer(
    drawerContent: @Composable () -> Unit,
    content: @Composable () -> Unit,
    drawerState: DrawerState,
) {

    DismissibleNavigationDrawer(
        drawerState = drawerState,
        gesturesEnabled = !NowcastDragState.isDragging,
        drawerContent = drawerContent
    ) { content() }

}