package com.pranshulgg.weathermaster.core.ui.components

import androidx.compose.foundation.layout.heightIn
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun M3eButton(
    modifier: Modifier = Modifier,
    text: String,
    size: Dp = ButtonDefaults.MinHeight,
    onClick: () -> Unit,
    icon: Int? = null,
    containerColor: Color = MaterialTheme.colorScheme.primary,
    contentColor: Color = MaterialTheme.colorScheme.onPrimary,
    disabled: Boolean = false
) {

    val disabledContentColor = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.38f)

    Button(
        modifier = modifier.heightIn(size),
        enabled = !disabled,
        onClick = { onClick() },
        shapes = ButtonDefaults.shapes(),
        colors = ButtonDefaults.buttonColors(
            containerColor = containerColor,
            contentColor = contentColor
        ),
        contentPadding =
            ButtonDefaults.contentPaddingFor(size, hasStartIcon = icon != null),
    ) {
        if (icon != null) {
            Symbol(
                icon,
                size = ButtonDefaults.iconSizeFor(size),
                color = if (disabled) disabledContentColor else contentColor
            )
            Gap(horizontal = ButtonDefaults.iconSpacingFor(size))
        }
        Text(
            text,
            style = ButtonDefaults.textStyleFor(size),
            color = if (disabled) disabledContentColor else contentColor
        )
    }
}


//--------------------------------
// OUTLINED
//--------------------------------

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun M3eOutlinedButton(
    modifier: Modifier = Modifier,
    text: String,
    size: Dp = ButtonDefaults.MinHeight,
    onClick: () -> Unit,
    icon: Int? = null,
    disabled: Boolean = false
) {
    OutlinedButton(
        modifier = modifier.heightIn(size),
        onClick = { onClick() },
        shapes = ButtonDefaults.shapes(),
        enabled = !disabled,
        contentPadding =
            ButtonDefaults.contentPaddingFor(size, hasStartIcon = icon != null),
    ) {
        if (icon != null) {
            Symbol(
                icon,
                size = ButtonDefaults.iconSizeFor(size),
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Gap(horizontal = ButtonDefaults.iconSpacingFor(size))
        }
        Text(
            text,
            style = ButtonDefaults.textStyleFor(size),
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

//--------------------------------
// FILLED TONAL
//--------------------------------

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun M3eFilledTonalButton(
    modifier: Modifier = Modifier,
    text: String,
    size: Dp = ButtonDefaults.MinHeight,
    onClick: () -> Unit,
    icon: Int? = null,
    containerColor: Color = MaterialTheme.colorScheme.primary,
    contentColor: Color = MaterialTheme.colorScheme.onPrimary,
    disabled: Boolean = false
) {
    FilledTonalButton(
        modifier = modifier.heightIn(size),
        onClick = { onClick() },
        shapes = ButtonDefaults.shapes(),
        enabled = !disabled,
        colors = ButtonDefaults.buttonColors(
            containerColor = containerColor,
            contentColor = contentColor
        ),
        contentPadding =
            ButtonDefaults.contentPaddingFor(size, hasStartIcon = icon != null),
    ) {
        if (icon != null) {
            Symbol(
                icon,
                size = ButtonDefaults.iconSizeFor(size),
                color = contentColor
            )
            Gap(horizontal = ButtonDefaults.iconSpacingFor(size))
        }
        Text(text, style = ButtonDefaults.textStyleFor(size), color = contentColor)
    }
}