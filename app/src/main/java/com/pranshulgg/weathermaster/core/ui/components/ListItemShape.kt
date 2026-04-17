package com.pranshulgg.weathermaster.core.ui.components

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Shape
import com.pranshulgg.weathermaster.core.ui.theme.ShapeRadius

@Composable
fun listItemShape(isOnly: Boolean, isFirst: Boolean, isLast: Boolean): RoundedCornerShape {

    val shape = when {
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

    return shape

}
