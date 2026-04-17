package com.pranshulgg.weathermaster.feature.locations

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.ExpandedFullScreenSearchBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SearchBarDefaults
import androidx.compose.material3.SearchBarState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.pranshulgg.weathermaster.R
import com.pranshulgg.weathermaster.core.ui.components.Symbol
import com.pranshulgg.weathermaster.core.ui.navigation.NavRoutes

@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun LocationsScreen(
    searchBarState: SearchBarState,
    onBack: () -> Unit,
    navController: NavController
) {

    ExpandedFullScreenSearchBar(
        state = searchBarState,
        inputField = { TopBar(onBack) },
        colors = SearchBarDefaults.colors(
            dividerColor = Color.Transparent,
            containerColor = MaterialTheme.colorScheme.surfaceContainer
        )
    ) {

        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(bottom = 16.dp)
        ) {
            // TODO: SHOW SAVED + CURRENT LOCATION


            FloatingActionButton(
                onClick = { navController.navigate(NavRoutes.SEARCH) },
                containerColor = MaterialTheme.colorScheme.primaryContainer,
                modifier = Modifier
                    .size(96.dp)
                    .align(Alignment.BottomCenter),
                shape = CircleShape
            ) {
                Symbol(
                    R.drawable.search_24px,
                    color = MaterialTheme.colorScheme.onPrimaryContainer,
                    size = 36.dp
                )
            }
        }


    }

}

@Composable
private fun TopBar(onBack: () -> Unit) {
    TopAppBar(
        colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.surfaceContainer),
        title = {
            Text(
                "Locations",
                color = MaterialTheme.colorScheme.onSurface,
                style = MaterialTheme.typography.titleLarge
            )
        },
        navigationIcon = {
            IconButton(onClick = { onBack() }) {
                Symbol(
                    R.drawable.arrow_back_24px,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }
        }
    )
}