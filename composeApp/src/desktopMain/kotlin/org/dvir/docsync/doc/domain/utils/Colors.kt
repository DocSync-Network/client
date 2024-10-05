package org.dvir.docsync.doc.domain.utils

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb

object Colors {
    fun colorFromHex(hex: String): Color {
        val colorInt = hex.removePrefix("#").toLong(16).toInt()
        return Color(colorInt)
    }

    fun hexFromColor(color: Color) = String.format("#%08X", color.toArgb())
}