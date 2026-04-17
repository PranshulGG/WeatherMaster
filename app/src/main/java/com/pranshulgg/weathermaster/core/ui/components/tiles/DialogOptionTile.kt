package com.pranshulgg.weathermaster.core.ui.components.tiles

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.ListItem
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.pranshulgg.weathermaster.core.ui.theme.ShapeRadius

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun <T> DialogOptionTile(
    headline: String,
    description: String? = null,
    options: List<T>,
    selectedOption: T?,
    onOptionSelected: (T) -> Unit,
    optionLabel: (T) -> String = { it.toString() },
    leading: @Composable (() -> Unit)? = null,
    shapes: RoundedCornerShape,
    dialogTitle: String? = null,
    itemBgColor: Color
) {
    var showDialog by remember { mutableStateOf(false) }

    Surface(
        modifier = Modifier.fillMaxWidth(),

        shape = shapes,
    ) {
        ListItem(

            modifier = Modifier
                .clickable { showDialog = true },
            colors = ListItemDefaults.colors(
                containerColor = itemBgColor
            ),
            leadingContent = leading,
            headlineContent = { Text(headline) },
            supportingContent = {
                if (description != null) Text(
                    description,
                )
                else selectedOption?.let {
                    Text(
                        optionLabel(it),
                        color = MaterialTheme.colorScheme.tertiary,
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            },
        )
    }

    if (showDialog) {
        var tempSelection by remember { mutableStateOf(selectedOption) }
        val listState = rememberLazyListState()

        val showTopDivider by remember {
            derivedStateOf {
                listState.firstVisibleItemIndex > 0 || listState.firstVisibleItemScrollOffset > 0
            }
        }

        val showBottomDivider by remember {
            derivedStateOf {
                val info = listState.layoutInfo
                val total = info.totalItemsCount
                val lastVisible = info.visibleItemsInfo.lastOrNull()?.index ?: -1
                total > 0 && lastVisible < total - 1
            }
        }

        Dialog(
            onDismissRequest = {
                showDialog = false
            }
        ) {

            Surface(
                modifier = Modifier
                    .width(300.dp)
                    .heightIn(max = 500.dp),
                shape = RoundedCornerShape(ShapeRadius.ExtraLarge),
                color = MaterialTheme.colorScheme.surfaceContainerHigh,
                shadowElevation = 6.dp
            ) {
                Column() {
                    Text(
                        dialogTitle ?: headline,
                        style = MaterialTheme.typography.headlineSmall,
                        color = MaterialTheme.colorScheme.onSurface,
                        modifier = Modifier.padding(top = 24.dp, start = 24.dp, end = 24.dp)
                    )
                    Spacer(Modifier.height(16.dp))
                    Box(
                        modifier = Modifier
                            .weight(1f, fill = false)
                            .fillMaxWidth()

                    ) {
                        LazyColumn(
                            state = listState,
                            modifier = Modifier
                                .fillMaxWidth()
                        ) {
                            items(options) { option ->
                                Row(
                                    modifier = Modifier
                                        .clickable { tempSelection = option }
                                        .fillMaxWidth(),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Spacer(modifier = Modifier.width(16.dp))
                                    RadioButton(
                                        selected = option == tempSelection,
                                        onClick = { tempSelection = option }
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(
                                        optionLabel(option),
                                        color = MaterialTheme.colorScheme.onSurface,
                                        style = MaterialTheme.typography.bodyLarge
                                    )
                                    Spacer(modifier = Modifier.width(16.dp))
                                }
                            }
                        }

                        androidx.compose.animation.AnimatedVisibility(
                            visible = showTopDivider,
                            modifier = Modifier.align(Alignment.TopCenter)
                        ) {
                            HorizontalDivider(modifier = Modifier.fillMaxWidth())
                        }

                        androidx.compose.animation.AnimatedVisibility(
                            visible = showBottomDivider,
                            modifier = Modifier.align(Alignment.BottomCenter)
                        ) {
                            HorizontalDivider(modifier = Modifier.fillMaxWidth())
                        }

                    }

                    Spacer(Modifier.height(16.dp))

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 24.dp, start = 24.dp, end = 24.dp),
                        horizontalArrangement = Arrangement.End
                    ) {
                        TextButton(
                            onClick = {
                                showDialog = false
                            },
                            shapes = ButtonDefaults.shapes()
                        ) {
                            Text("Cancel", style = MaterialTheme.typography.labelLarge)
                        }
                        Spacer(Modifier.width(8.dp))
                        TextButton(
                            onClick = {
                                tempSelection?.let { onOptionSelected(it) }
                                showDialog = false
                            },
                            shapes = ButtonDefaults.shapes()
                        ) {
                            Text("Save", style = MaterialTheme.typography.labelLarge)
                        }
                    }
                }
            }
        }
    }
}
