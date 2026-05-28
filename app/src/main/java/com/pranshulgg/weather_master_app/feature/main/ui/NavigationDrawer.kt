package com.pranshulgg.weather_master_app.feature.main.ui

import androidx.compose.material3.DismissibleNavigationDrawer
import androidx.compose.material3.DrawerState
import androidx.compose.material3.PermanentNavigationDrawer
import androidx.compose.runtime.Composable

@Composable
fun NavigationDrawer(
    drawerContent: @Composable () -> Unit,
    content: @Composable () -> Unit,
    drawerState: DrawerState,
) {

    DismissibleNavigationDrawer(
        drawerState = drawerState,
        drawerContent = drawerContent
    ) { content() }

}