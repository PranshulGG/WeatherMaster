package com.pranshulgg.weathermaster.feature.search.ui

import android.R.attr.type
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.FloatingToolbarDefaults
import androidx.compose.material3.FloatingToolbarDefaults.ScreenOffset
import androidx.compose.material3.FloatingToolbarScrollBehavior
import androidx.compose.material3.HorizontalFloatingToolbar
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusManager
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import com.pranshulgg.weathermaster.R
import com.pranshulgg.weathermaster.core.ui.components.Symbol
import com.pranshulgg.weathermaster.feature.search.SearchScreenViewModel

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun SearchFloatingToolbar(
    scrollBehaviorToolbar: FloatingToolbarScrollBehavior,
    query: String,
    viewModel: SearchScreenViewModel
) {
    val focusManager = LocalFocusManager.current
    val focusRequester = remember { FocusRequester() }
    val systemInsets = WindowInsets.systemBars.asPaddingValues()

    Box(
        Modifier
            .fillMaxWidth()
            .imePadding()
    ) {

        HorizontalFloatingToolbar(
            scrollBehavior = scrollBehaviorToolbar,
            contentPadding = PaddingValues(top = 0.dp, bottom = 0.dp, start = 10.dp),
            modifier = Modifier
                .padding(
                    bottom = systemInsets.calculateBottomPadding() + ScreenOffset,
                    top = ScreenOffset
                )
                .height(64.dp)
                .align(Alignment.BottomCenter)
                .zIndex(1f),
            colors = FloatingToolbarDefaults.vibrantFloatingToolbarColors(),
            expanded = true,
            floatingActionButton = {
                FloatingToolbarDefaults.VibrantFloatingActionButton(
                    onClick = {
                        viewModel.search(query)
                        focusManager.clearFocus()
                    }
                ) {
                    Symbol(
                        R.drawable.search_24px,
                        color = MaterialTheme.colorScheme.onTertiaryContainer
                    )
                }
            },
            content = {
                SearchFloatingBarContent(
                    viewModel,
                    query,
                    focusRequester,
                    focusManager
                )
            }
        )
    }
}

@Composable
private fun SearchFloatingBarContent(
    viewModel: SearchScreenViewModel,
    query: String,
    focusRequester: FocusRequester,
    focusManager: FocusManager,
) {
    TextField(
        value = query,
        onValueChange = viewModel::updateQuery,
        textStyle = TextStyle(
            color = MaterialTheme.colorScheme.onPrimaryContainer,
            fontSize = 16.sp
        ),
        modifier = Modifier
            .width(220.dp)
            .focusRequester(focusRequester),
        placeholder = {
            Text(
                "Search…",
                color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.8f)
            )
        },
        singleLine = true,
        keyboardOptions = KeyboardOptions(
            imeAction = ImeAction.Search
        ),
        keyboardActions = KeyboardActions(
            onSearch = {
                viewModel.search(query)
                focusManager.clearFocus()
            }
        ),

        trailingIcon = {
            if (query.isNotEmpty()) {
                Box(Modifier.padding(end = 6.dp)) {
                    IconButton(
                        onClick = {
                            viewModel.updateQuery("")
                            focusManager.clearFocus()
                        }
                    ) {
                        Symbol(
                            R.drawable.close_24px,
                            color = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                    }
                }
            }
        },
        colors = TextFieldDefaults.colors(
            focusedIndicatorColor = Color.Transparent,
            unfocusedIndicatorColor = Color.Transparent,
            unfocusedContainerColor = Color.Transparent,
            focusedContainerColor = Color.Transparent,
            cursorColor = MaterialTheme.colorScheme.onPrimaryContainer
        ),
    )

}