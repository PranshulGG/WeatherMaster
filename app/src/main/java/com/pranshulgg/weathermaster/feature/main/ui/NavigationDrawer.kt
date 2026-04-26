package com.pranshulgg.weathermaster.feature.main.ui

import androidx.compose.foundation.layout.width
import androidx.compose.material3.DismissibleNavigationDrawer
import androidx.compose.material3.DrawerState
import androidx.compose.material3.PermanentNavigationDrawer
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun NavigationDrawer(
    drawerContent: @Composable () -> Unit,
    content: @Composable () -> Unit,
    drawerState: DrawerState,
    isTabletLike: Boolean
) {

    if (isTabletLike) {
        PermanentNavigationDrawer(
            drawerContent = drawerContent
        ) { content() }
    } else {
        DismissibleNavigationDrawer(
            drawerState = drawerState,
            drawerContent = drawerContent
        ) { content() }
    }

}