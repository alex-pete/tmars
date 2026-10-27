package com.jetbrains.kmpapp.ui

import androidx.compose.ui.graphics.Color
import com.jetbrains.kmpapp.board.PlayerColor

object BoardPalette {
    val frame = Color(0xFF3E2C28)
    val metal = Color(0xFFB7BCC3)
    val metalDark = Color(0xFF8E949C)
    val track = Color(0xFF8A5A34)
    val cell = Color(0xFFF3F0E8)
    val negativeCell = Color(0xFFE7D3D0)
    val ink = Color(0xFF1E1E1E)
    val gold = Color(0xFFE2A31A)
    val goldDeep = Color(0xFFC48A12)
    val steel = Color(0xFF6E4B30)
    val titanium = Color(0xFF1A1C24)
    val plant = Color(0xFF2F8F45)
    val energy = Color(0xFF6940B5)
    val heat = Color(0xFFD24A32)
    val silver = Color(0xFFD5DCE3)
    val bronze = Color(0xFFB87333)
}

fun cubeColor(color: PlayerColor): Color = when (color) {
    PlayerColor.Red -> Color(0xFFC62828)
    PlayerColor.Blue -> Color(0xFF1565C0)
    PlayerColor.Green -> Color(0xFF2E7D32)
    PlayerColor.Yellow -> Color(0xFFF9A825)
    PlayerColor.Black -> Color(0xFF212121)
}

fun onCubeColor(color: PlayerColor): Color = when (color) {
    PlayerColor.Yellow -> BoardPalette.ink
    else -> Color.White
}
