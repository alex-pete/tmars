package com.jetbrains.kmpapp.ui

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.jetbrains.kmpapp.board.ResourceKind
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.min
import kotlin.math.sin

@Composable
fun ResourceSymbol(
    resource: ResourceKind,
    modifier: Modifier = Modifier,
    tint: Color,
) {
    when (resource) {
        ResourceKind.MegaCredits -> MegaCreditSymbol(modifier)
        ResourceKind.Steel -> ToolSymbol(modifier, tint)
        ResourceKind.Titanium -> StarSymbol(modifier, tint)
        ResourceKind.Plants -> LeafSymbol(modifier, tint)
        ResourceKind.Energy -> BoltSymbol(modifier, tint)
        ResourceKind.Heat -> HeatSymbol(modifier, tint)
    }
}

@Composable
fun MegaCreditSymbol(modifier: Modifier = Modifier) {
    Box(
        modifier
            .clip(CircleShape)
            .background(BoardPalette.gold)
            .border(2.dp, BoardPalette.goldDeep, CircleShape),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text = "€",
            color = BoardPalette.ink,
            fontWeight = FontWeight.Black,
            fontSize = 14.sp,
        )
    }
}

@Composable
fun GlobeSymbol(modifier: Modifier = Modifier, tint: Color = BoardPalette.ink) {
    Canvas(modifier) {
        val radius = min(size.width, size.height) / 2f * 0.82f
        val center = Offset(size.width / 2f, size.height / 2f)
        drawCircle(tint, radius, center, style = Stroke(width = radius * 0.16f))
        drawLine(
            tint,
            Offset(center.x - radius, center.y),
            Offset(center.x + radius, center.y),
            strokeWidth = radius * 0.12f,
        )
        drawOval(
            tint,
            topLeft = Offset(center.x - radius * 0.42f, center.y - radius),
            size = androidx.compose.ui.geometry.Size(radius * 0.84f, radius * 2f),
            style = Stroke(width = radius * 0.1f),
        )
    }
}

@Composable
fun TreeSymbol(modifier: Modifier = Modifier, tint: Color) {
    Canvas(modifier) {
        val canopy = Path().apply {
            moveTo(size.width * 0.5f, size.height * 0.08f)
            lineTo(size.width * 0.88f, size.height * 0.62f)
            lineTo(size.width * 0.12f, size.height * 0.62f)
            close()
        }
        drawPath(canopy, tint)
        drawRect(
            tint,
            topLeft = Offset(size.width * 0.42f, size.height * 0.62f),
            size = androidx.compose.ui.geometry.Size(size.width * 0.16f, size.height * 0.28f),
        )
    }
}

@Composable
fun ThermometerSymbol(modifier: Modifier = Modifier, tint: Color) {
    Canvas(modifier) {
        val tubeLeft = size.width * 0.38f
        val tubeWidth = size.width * 0.24f
        drawRoundRect(
            tint,
            topLeft = Offset(tubeLeft, size.height * 0.08f),
            size = androidx.compose.ui.geometry.Size(tubeWidth, size.height * 0.62f),
            cornerRadius = androidx.compose.ui.geometry.CornerRadius(tubeWidth / 2f),
            style = Stroke(width = size.width * 0.08f),
        )
        drawCircle(tint, size.width * 0.22f, Offset(size.width / 2f, size.height * 0.78f))
    }
}

@Composable
private fun ToolSymbol(modifier: Modifier, tint: Color) {
    Canvas(modifier) {
        val stroke = size.width * 0.12f
        drawLine(
            tint,
            Offset(size.width * 0.22f, size.height * 0.28f),
            Offset(size.width * 0.78f, size.height * 0.28f),
            stroke,
            StrokeCap.Round,
        )
        drawLine(
            tint,
            Offset(size.width * 0.34f, size.height * 0.28f),
            Offset(size.width * 0.34f, size.height * 0.78f),
            stroke,
            StrokeCap.Round,
        )
        drawCircle(tint, size.width * 0.16f, Offset(size.width * 0.68f, size.height * 0.62f))
        drawCircle(
            Color.Transparent,
            size.width * 0.07f,
            Offset(size.width * 0.68f, size.height * 0.62f),
        )
    }
}

@Composable
private fun StarSymbol(modifier: Modifier, tint: Color) {
    Canvas(modifier) {
        drawPath(starPath(size.width, size.height), tint)
    }
}

@Composable
private fun LeafSymbol(modifier: Modifier, tint: Color) {
    Canvas(modifier) {
        val leaf = Path().apply {
            moveTo(size.width * 0.5f, size.height * 0.92f)
            quadraticTo(size.width * -0.05f, size.height * 0.45f, size.width * 0.5f, size.height * 0.08f)
            quadraticTo(size.width * 1.05f, size.height * 0.45f, size.width * 0.5f, size.height * 0.92f)
            close()
        }
        drawPath(leaf, tint)
        drawLine(
            Color.White.copy(alpha = 0.85f),
            Offset(size.width * 0.5f, size.height * 0.82f),
            Offset(size.width * 0.5f, size.height * 0.22f),
            strokeWidth = size.width * 0.06f,
            cap = StrokeCap.Round,
        )
    }
}

@Composable
private fun BoltSymbol(modifier: Modifier, tint: Color) {
    Canvas(modifier) {
        val bolt = Path().apply {
            moveTo(size.width * 0.58f, size.height * 0.06f)
            lineTo(size.width * 0.22f, size.height * 0.54f)
            lineTo(size.width * 0.48f, size.height * 0.54f)
            lineTo(size.width * 0.38f, size.height * 0.94f)
            lineTo(size.width * 0.8f, size.height * 0.4f)
            lineTo(size.width * 0.52f, size.height * 0.4f)
            close()
        }
        drawPath(bolt, tint)
    }
}

@Composable
private fun HeatSymbol(modifier: Modifier, tint: Color) {
    Canvas(modifier) {
        val stroke = size.width * 0.08f
        repeat(3) { index ->
            val x = size.width * (0.25f + index * 0.25f)
            val wave = Path().apply {
                moveTo(x, size.height * 0.1f)
                quadraticTo(x - size.width * 0.12f, size.height * 0.32f, x, size.height * 0.5f)
                quadraticTo(x + size.width * 0.12f, size.height * 0.68f, x, size.height * 0.9f)
            }
            drawPath(wave, tint, style = Stroke(width = stroke, cap = StrokeCap.Round))
        }
    }
}

private fun starPath(width: Float, height: Float): Path {
    val path = Path()
    val cx = width / 2f
    val cy = height / 2f
    val outer = min(width, height) / 2f
    val inner = outer * 0.42f
    for (point in 0 until 10) {
        val radius = if (point % 2 == 0) outer else inner
        val angle = (point * PI / 5.0) - PI / 2.0
        val x = cx + (cos(angle) * radius).toFloat()
        val y = cy + (sin(angle) * radius).toFloat()
        if (point == 0) path.moveTo(x, y) else path.lineTo(x, y)
    }
    path.close()
    return path
}
