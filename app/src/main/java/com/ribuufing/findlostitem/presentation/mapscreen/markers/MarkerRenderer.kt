package com.ribuufing.findlostitem.presentation.mapscreen.markers

import android.content.Context
import android.graphics.*
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb

private const val MARKER_SIZE = 100

fun createClusterMarkerBitmap(
    context: Context,
    clusterSize: Int,
    backgroundColor: Color
): Bitmap {
    val bitmap = Bitmap.createBitmap(MARKER_SIZE, MARKER_SIZE, Bitmap.Config.ARGB_8888)
    val canvas = Canvas(bitmap)

    drawMarkerShadow(canvas, MARKER_SIZE)
    drawMarkerBackground(canvas, MARKER_SIZE, backgroundColor)
    drawMarkerText(canvas, MARKER_SIZE, clusterSize.toString())

    return bitmap
}

fun createSingleMarkerBitmap(
    context: Context,
    backgroundColor: Color
): Bitmap {
    val bitmap = Bitmap.createBitmap(MARKER_SIZE, MARKER_SIZE, Bitmap.Config.ARGB_8888)
    val canvas = Canvas(bitmap)

    drawPinShadow(canvas, MARKER_SIZE)
    drawPinShape(canvas, MARKER_SIZE, backgroundColor)
    drawPinCircle(canvas, MARKER_SIZE, backgroundColor)

    return bitmap
}

private fun drawMarkerShadow(canvas: Canvas, size: Int) {
    val shadowPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.FILL
        color = Color.Black.copy(alpha = 0.3f).toArgb()
        setShadowLayer(8f, 0f, 4f, Color.Black.copy(alpha = 0.4f).toArgb())
    }
    canvas.drawCircle(size / 2f, size / 2f, size / 2.5f, shadowPaint)
}

private fun drawMarkerBackground(canvas: Canvas, size: Int, backgroundColor: Color) {
    val paint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.FILL
        color = backgroundColor.copy(alpha = 0.9f).toArgb()
    }
    canvas.drawCircle(size / 2f, size / 2f, size / 2.5f, paint)
}

private fun drawMarkerText(canvas: Canvas, size: Int, text: String) {
    val textPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.White.toArgb()
        textAlign = Paint.Align.CENTER
        textSize = size / 2.5f
        typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
        setShadowLayer(4f, 0f, 2f, Color.Black.copy(alpha = 0.3f).toArgb())
    }

    val textHeight = textPaint.descent() - textPaint.ascent()
    val textOffset = textHeight / 2 - textPaint.descent()
    canvas.drawText(text, size / 2f, size / 2f + textOffset, textPaint)
}

// Pin marker çizim fonksiyonları...
// (Diğer çizim fonksiyonları buraya eklenecek) 

private fun drawPinShadow(canvas: Canvas, size: Int) {
    val shadowPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.FILL
        color = Color.Black.copy(alpha = 0.3f).toArgb()
        setShadowLayer(12f, 0f, 6f, Color.Black.copy(alpha = 0.4f).toArgb())
    }

    val path = createPinPath(size)
    canvas.drawPath(path, shadowPaint)
}

private fun drawPinShape(canvas: Canvas, size: Int, backgroundColor: Color) {
    val paint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.FILL
        color = backgroundColor.copy(alpha = 0.9f).toArgb()
    }

    val path = createPinPath(size)
    canvas.drawPath(path, paint)
}

private fun drawPinCircle(canvas: Canvas, size: Int, backgroundColor: Color) {
    val circlePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.FILL
        color = Color.White.copy(alpha = 0.9f).toArgb()
        setShadowLayer(4f, 0f, 2f, Color.Black.copy(alpha = 0.2f).toArgb())
    }

    val borderPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.STROKE
        strokeWidth = size * 0.03f
        color = backgroundColor.copy(alpha = 0.8f).toArgb()
    }

    val centerX = size * 0.5f
    val centerY = size * 0.45f
    val radius = size * 0.15f

    canvas.drawCircle(centerX, centerY, radius, circlePaint)
    canvas.drawCircle(centerX, centerY, radius, borderPaint)
}

private fun createPinPath(size: Int): Path {
    return Path().apply {
        val pinWidth = size * 0.6f
        val pinHeight = size * 0.7f
        val startY = size * 0.2f
        val centerX = size * 0.5f

        moveTo(centerX, startY + pinHeight)
        cubicTo(
            centerX - pinWidth/2, startY + pinHeight * 0.75f,
            centerX - pinWidth/2, startY,
            centerX, startY
        )
        cubicTo(
            centerX + pinWidth/2, startY,
            centerX + pinWidth/2, startY + pinHeight * 0.75f,
            centerX, startY + pinHeight
        )
        close()
    }
} 