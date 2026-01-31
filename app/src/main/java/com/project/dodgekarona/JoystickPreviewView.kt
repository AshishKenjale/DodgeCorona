package com.project.dodgekarona

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.util.AttributeSet
import android.view.View
import kotlin.math.min

/**
 * Preview view for joystick settings configuration.
 * Shows a visual representation of the joystick with current settings.
 */
class JoystickPreviewView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    private var joystickSize: Float = 1.0f
    private var joystickOpacity: Float = 0.5f
    private var joystickPosition: Int = 0 // 0 = left, 1 = right

    private val basePaint = Paint().apply {
        style = Paint.Style.FILL
        isAntiAlias = true
    }

    private val baseOutlinePaint = Paint().apply {
        style = Paint.Style.STROKE
        strokeWidth = 4f
        isAntiAlias = true
    }

    private val knobPaint = Paint().apply {
        style = Paint.Style.FILL
        isAntiAlias = true
    }

    private val knobOutlinePaint = Paint().apply {
        style = Paint.Style.STROKE
        strokeWidth = 3f
        isAntiAlias = true
    }

    fun updateSettings(size: Float, opacity: Float, position: Int) {
        joystickSize = size
        joystickOpacity = opacity
        joystickPosition = position
        invalidate()
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        val viewWidth = width.toFloat()
        val viewHeight = height.toFloat()

        // Calculate joystick dimensions based on size setting
        val smallerDimension = min(viewWidth, viewHeight)
        val baseRadius = smallerDimension * 0.2f * joystickSize
        val knobRadius = baseRadius * 0.5f

        // Calculate position (0 = left, 1 = right, 2 = center)
        val padding = baseRadius * 1.5f
        val centerX = when (joystickPosition) {
            0 -> padding + baseRadius // Left
            1 -> viewWidth - padding - baseRadius // Right
            2 -> viewWidth / 2f // Center
            else -> padding + baseRadius // Default to left
        }
        val centerY = viewHeight - padding - baseRadius

        // Apply opacity to paints
        val baseAlpha = (80 * joystickOpacity * 2).toInt().coerceIn(0, 255)
        val baseOutlineAlpha = (120 * joystickOpacity * 2).toInt().coerceIn(0, 255)
        val knobAlpha = (180 * joystickOpacity * 2).toInt().coerceIn(0, 255)
        val knobOutlineAlpha = (200 * joystickOpacity * 2).toInt().coerceIn(0, 255)

        basePaint.color = Color.argb(baseAlpha, 128, 128, 128)
        baseOutlinePaint.color = Color.argb(baseOutlineAlpha, 100, 100, 100)
        knobPaint.color = Color.argb(knobAlpha, 60, 60, 60)
        knobOutlinePaint.color = Color.argb(knobOutlineAlpha, 40, 40, 40)

        // Draw base circle
        canvas.drawCircle(centerX, centerY, baseRadius, basePaint)
        canvas.drawCircle(centerX, centerY, baseRadius, baseOutlinePaint)

        // Draw knob at center
        canvas.drawCircle(centerX, centerY, knobRadius, knobPaint)
        canvas.drawCircle(centerX, centerY, knobRadius, knobOutlinePaint)
    }
}
