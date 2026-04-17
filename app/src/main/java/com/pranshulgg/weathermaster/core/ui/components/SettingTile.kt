package com.pranshulgg.weathermaster.core.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.pranshulgg.weathermaster.core.ui.components.tiles.ActionTile
import com.pranshulgg.weathermaster.core.ui.components.tiles.CategoryTile
import com.pranshulgg.weathermaster.core.ui.components.tiles.DialogOptionTile
import com.pranshulgg.weathermaster.core.ui.components.tiles.DialogSliderTile
import com.pranshulgg.weathermaster.core.ui.components.tiles.DialogTextFieldTile
import com.pranshulgg.weathermaster.core.ui.components.tiles.SingleSwitchTile
import com.pranshulgg.weathermaster.core.ui.components.tiles.SwitchTile
import com.pranshulgg.weathermaster.core.ui.components.tiles.TextTile
import com.pranshulgg.weathermaster.core.ui.theme.ShapeRadius

sealed class SettingTile {
    abstract val title: String
    abstract val description: String?

    data class TextTile(
        override val title: String,
        override val description: String? = null,
        val descriptionMaxLines: Int = Int.MAX_VALUE,
        val leading: (@Composable (() -> Unit))? = null,
    ) : SettingTile()


    data class SwitchTile(
        override val title: String,
        override val description: String? = null,
        val leading: (@Composable (() -> Unit))? = null,
        val checked: Boolean,
        val onCheckedChange: (Boolean) -> Unit,
        val enabled: Boolean = true
    ) : SettingTile()

    data class SingleSwitchTile(
        override val title: String,
        override val description: String? = null,
        val leading: (@Composable (() -> Unit))? = null,
        val checked: Boolean,
        val onCheckedChange: (Boolean) -> Unit,
        val enabled: Boolean = true
    ) : SettingTile()

    data class CategoryTile(
        override val title: String,
        override val description: String? = null,
        val leading: Int,
        val color: Color,
        val onColor: Color,
        val onClick: () -> Unit

    ) : SettingTile()

    data class DialogOptionTile(
        override val title: String,
        override val description: String? = null,
        val leading: (@Composable (() -> Unit))? = null,
        val options: List<String>,
        val selectedOption: String?,
        val onOptionSelected: (String) -> Unit,
        val optionLabel: (String) -> String = { it },
        val dialogTitle: String? = null
    ) : SettingTile()

    data class ActionTile(
        override val title: String,
        override val description: String? = null,
        val leading: (@Composable (() -> Unit))? = null,
        val onClick: () -> Unit,
        val colorDesc: Color = Color.Unspecified,
        val danger: Boolean = false,
        val selected: Boolean = false,
        val trailing: (@Composable (() -> Unit))? = null,
    ) : SettingTile()


    data class DialogTextFieldTile(
        override val title: String,
        override val description: String? = null,
        val leading: (@Composable (() -> Unit))? = null,
        val onTextSubmitted: (String) -> Unit,
        val placeholder: String,
        val placeholderTextField: String,
        val initialText: String = ""
    ) : SettingTile()

    data class DialogSliderTile(
        override val title: String,
        override val description: String? = null,
        val leading: (@Composable (() -> Unit))? = null,
        val onValueSubmitted: (Float) -> Unit,
        val initialValue: Float = 0.5f,
        val valueRange: ClosedFloatingPointRange<Float> = 0f..1f,
        val steps: Int = 0,
        val labelFormatter: (Float) -> String = { it.toString() },
        val dialogTitle: String,
        val isDescriptionAsValue: Boolean = false

    ) : SettingTile()

}


@Composable
fun SettingSection(
    tiles: List<SettingTile?>,
    title: String? = null,
    primarySwitch: Boolean = false,
    noPadding: Boolean = false,
    errorTile: Boolean = false,
    isModalOption: Boolean = false,
) {

    val itemBgColor =
        if (isModalOption) MaterialTheme.colorScheme.surfaceContainerHigh else MaterialTheme.colorScheme.surfaceBright

    Column(
        modifier = Modifier
            .padding(horizontal = if (noPadding) 0.dp else 16.dp),
        verticalArrangement = Arrangement.spacedBy(2.dp)
    ) {
        title?.let {
            Text(
                text = it,
                modifier = Modifier.padding(bottom = 5.dp, top = 5.dp, start = 3.dp),
                fontSize = 16.sp,
                color = MaterialTheme.colorScheme.primary,
                fontWeight = FontWeight.W700
            )
        }


        val nonNullTiles = tiles.filterNotNull()

        nonNullTiles.forEachIndexed { index, tile ->
            val isFirst = index == 0
            val isLast = index == nonNullTiles.lastIndex
            val isOnly = nonNullTiles.size == 1
            val primarySwitch = primarySwitch


            val shape = when {
                primarySwitch -> RoundedCornerShape(ShapeRadius.Full)
                isOnly -> RoundedCornerShape(ShapeRadius.Large)
                isFirst -> RoundedCornerShape(
                    topStart = ShapeRadius.Large,
                    topEnd = ShapeRadius.Large,
                    bottomStart = ShapeRadius.ExtraSmall,
                    bottomEnd = ShapeRadius.ExtraSmall
                )

                isLast -> RoundedCornerShape(
                    topStart = ShapeRadius.ExtraSmall,
                    topEnd = ShapeRadius.ExtraSmall,
                    bottomStart = ShapeRadius.Large,
                    bottomEnd = ShapeRadius.Large
                )

                else -> RoundedCornerShape(ShapeRadius.ExtraSmall)
            }

            when (tile) {
                is SettingTile.TextTile -> TextTile(
                    headline = tile.title,
                    description = tile.description,
                    leading = tile.leading,
                    shapes = shape,
                    itemBgColor = itemBgColor,
                    descriptionMaxLines = tile.descriptionMaxLines
                )

                is SettingTile.CategoryTile -> CategoryTile(
                    headline = tile.title,
                    description = tile.description,
                    leading = tile.leading,
                    shapes = shape,
                    color = tile.color,
                    iconColor = tile.onColor,
                    onClick = tile.onClick,
                    itemBgColor = itemBgColor
                )


                is SettingTile.DialogOptionTile -> DialogOptionTile(
                    headline = tile.title,
                    description = tile.description,
                    leading = tile.leading,
                    shapes = shape,
                    options = tile.options,
                    selectedOption = tile.selectedOption,
                    onOptionSelected = tile.onOptionSelected,
                    optionLabel = tile.optionLabel,
                    dialogTitle = tile.dialogTitle,
                    itemBgColor = itemBgColor
                )

                is SettingTile.ActionTile -> ActionTile(
                    headline = tile.title,
                    description = tile.description,
                    leading = tile.leading,
                    shapes = shape,
                    onClick = tile.onClick,
                    colorDesc = tile.colorDesc,
                    danger = tile.danger,
                    itemBgColor = itemBgColor,
                    selected = tile.selected,
                    trailing = tile.trailing
                )

                is SettingTile.SwitchTile -> SwitchTile(
                    headline = tile.title,
                    description = tile.description,
                    leading = tile.leading,
                    checked = tile.checked,
                    onCheckedChange = tile.onCheckedChange,
                    shapes = shape,
                    switchEnabled = tile.enabled,
                    itemBgColor = itemBgColor
                )

                is SettingTile.SingleSwitchTile -> SingleSwitchTile(
                    headline = tile.title,
                    description = tile.description,
                    leading = tile.leading,
                    checked = tile.checked,
                    onCheckedChange = tile.onCheckedChange,
                    switchEnabled = tile.enabled,
                    itemBgColor = itemBgColor
                )

                is SettingTile.DialogTextFieldTile -> DialogTextFieldTile(
                    headline = tile.title,
                    description = tile.description,
                    leading = tile.leading,
                    shapes = shape,
                    onTextSubmitted = tile.onTextSubmitted,
                    placeholder = tile.placeholder,
                    placeholderTextField = tile.placeholderTextField,
                    initialText = tile.initialText,
                    itemBgColor = itemBgColor
                )

                is SettingTile.DialogSliderTile -> DialogSliderTile(
                    headline = tile.title,
                    description = tile.description,
                    leading = tile.leading,
                    shapes = shape,
                    onValueSubmitted = tile.onValueSubmitted,
                    initialValue = tile.initialValue,
                    valueRange = tile.valueRange,
                    steps = tile.steps,
                    labelFormatter = tile.labelFormatter,
                    dialogTitle = tile.dialogTitle,
                    isDescriptionAsValue = tile.isDescriptionAsValue,
                    itemBgColor = itemBgColor
                )
            }
        }
    }
}

