package com.lottttto.miner.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import kotlin.math.roundToInt

@Composable
fun VerticalSlider(
    value: Int,
    onValueChange: (Int) -> Unit,
    maxValue: Int = 100,
    modifier: Modifier = Modifier,
    height: Dp = 150.dp,
    width: Dp = 40.dp,
    thumbColor: Color = MaterialTheme.colorScheme.primary,
    activeTrackColor: Color = MaterialTheme.colorScheme.primary,
    inactiveTrackColor: Color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.2f)
) {
    val density = LocalDensity.current
    val sliderHeightPx = with(density) { height.toPx() }
    val thumbRadiusPx = with(density) { 12.dp.toPx() }
    val trackWidthPx = with(density) { 8.dp.toPx() }

    Box(
        modifier = modifier
            .width(width)
            .height(height)
            .pointerInput(Unit) {
                detectDragGestures(
                    onDrag = { change, _ ->
                        change.consume()
                        val y = change.position.y.coerceIn(0f, sliderHeightPx)
                        val newVal = ((sliderHeightPx - y) / sliderHeightPx * maxValue).roundToInt()
                        onValueChange(newVal.coerceIn(0, maxValue))
                    }
                )
            }
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val trackStart = Offset(width.toPx() / 2, 0f)
            val trackEnd = Offset(width.toPx() / 2, sliderHeightPx)
            drawLine(inactiveTrackColor, trackStart, trackEnd, trackWidthPx)

            val thumbY = sliderHeightPx * (1 - value / maxValue.toFloat())
            val activeTrackStart = Offset(width.toPx() / 2, sliderHeightPx)
            val activeTrackEnd = Offset(width.toPx() / 2, thumbY)
            drawLine(activeTrackColor, activeTrackStart, activeTrackEnd, trackWidthPx)

            drawCircle(thumbColor, thumbRadiusPx, center = Offset(width.toPx() / 2, thumbY))
        }
        Text(
            text = "$value%",
            modifier = Modifier.align(Alignment.TopCenter).padding(top = 4.dp),
            style = MaterialTheme.typography.labelSmall
        )
    }
}
