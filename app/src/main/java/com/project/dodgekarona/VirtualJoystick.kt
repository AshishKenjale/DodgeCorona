package com.project.dodgekarona

import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.view.MotionEvent
import kotlin.math.min
import kotlin.math.sqrt

/**
 * Virtual joystick for touch-based player control.
 * Provides smooth, responsive movement vectors for the player.
 */
class VirtualJoystick(
    private val screenWidth: Int,
    private val screenHeight: Int
) {
    // Joystick positioning - bottom center of screen
    private val baseRadius: Float
    private val knobRadius: Float
    private val baseCenterX: Float
    private val baseCenterY: Float

    // Current knob position
    private var knobCenterX: Float
    private var knobCenterY: Float

    // Movement vectors (normalized -1 to 1)
    private var movingVectorX: Float = 0f
    private var movingVectorY: Float = 0f

    // Touch tracking
    private var isPressed: Boolean = false
    private var activePointerId: Int = -1

    // Paint objects for drawing
    private val basePaint = Paint().apply {
        color = Color.argb(80, 128, 128, 128)
        style = Paint.Style.FILL
        isAntiAlias = true
    }

    private val baseOutlinePaint = Paint().apply {
        color = Color.argb(120, 100, 100, 100)
        style = Paint.Style.STROKE
        strokeWidth = 4f
        isAntiAlias = true
    }

    private val knobPaint = Paint().apply {
        color = Color.argb(180, 60, 60, 60)
        style = Paint.Style.FILL
        isAntiAlias = true
    }

    private val knobOutlinePaint = Paint().apply {
        color = Color.argb(200, 40, 40, 40)
        style = Paint.Style.STROKE
        strokeWidth = 3f
        isAntiAlias = true
    }

    init {
        // Size joystick relative to screen (responsive design)
        val smallerDimension = min(screenWidth, screenHeight)
        baseRadius = smallerDimension * 0.12f
        knobRadius = baseRadius * 0.5f

        // Position at bottom-left with padding
        val padding = baseRadius * 1.5f
        baseCenterX = padding + baseRadius
        baseCenterY = screenHeight - padding - baseRadius

        // Initialize knob at center
        knobCenterX = baseCenterX
        knobCenterY = baseCenterY
    }

    /**
     * Handle touch events for joystick control.
     * Returns true if the event was consumed by the joystick.
     */
    fun handleTouchEvent(event: MotionEvent): Boolean {
        when (event.actionMasked) {
            MotionEvent.ACTION_DOWN -> {
                // Check if touch is within joystick area (with generous touch zone)
                val touchZone = baseRadius * 2f
                val dx = event.x - baseCenterX
                val dy = event.y - baseCenterY
                val distance = sqrt(dx * dx + dy * dy)

                if (distance <= touchZone) {
                    isPressed = true
                    activePointerId = event.getPointerId(0)
                    updateKnobPosition(event.x, event.y)
                    return true
                }
            }

            MotionEvent.ACTION_POINTER_DOWN -> {
                // Handle multi-touch - check if new pointer is on joystick
                val pointerIndex = event.actionIndex
                val touchZone = baseRadius * 2f
                val dx = event.getX(pointerIndex) - baseCenterX
                val dy = event.getY(pointerIndex) - baseCenterY
                val distance = sqrt(dx * dx + dy * dy)

                if (!isPressed && distance <= touchZone) {
                    isPressed = true
                    activePointerId = event.getPointerId(pointerIndex)
                    updateKnobPosition(event.getX(pointerIndex), event.getY(pointerIndex))
                    return true
                }
            }

            MotionEvent.ACTION_MOVE -> {
                if (isPressed && activePointerId != -1) {
                    val pointerIndex = event.findPointerIndex(activePointerId)
                    if (pointerIndex != -1) {
                        updateKnobPosition(event.getX(pointerIndex), event.getY(pointerIndex))
                        return true
                    }
                }
            }

            MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL -> {
                if (isPressed) {
                    resetJoystick()
                    return true
                }
            }

            MotionEvent.ACTION_POINTER_UP -> {
                val pointerIndex = event.actionIndex
                val pointerId = event.getPointerId(pointerIndex)
                if (pointerId == activePointerId) {
                    resetJoystick()
                    return true
                }
            }
        }
        return false
    }

    /**
     * Update knob position based on touch coordinates.
     * Constrains knob within base circle and calculates movement vectors.
     */
    private fun updateKnobPosition(touchX: Float, touchY: Float) {
        val dx = touchX - baseCenterX
        val dy = touchY - baseCenterY
        val distance = sqrt(dx * dx + dy * dy)

        if (distance <= baseRadius) {
            // Touch is within base - knob follows directly
            knobCenterX = touchX
            knobCenterY = touchY
        } else {
            // Touch is outside base - constrain knob to edge
            val ratio = baseRadius / distance
            knobCenterX = baseCenterX + dx * ratio
            knobCenterY = baseCenterY + dy * ratio
        }

        // Calculate normalized movement vectors (-1 to 1)
        // Invert X for intuitive left/right movement
        movingVectorX = -(knobCenterX - baseCenterX) / baseRadius
        movingVectorY = (knobCenterY - baseCenterY) / baseRadius

        // Apply dead zone for small movements (prevents drift)
        val deadZone = 0.1f
        if (kotlin.math.abs(movingVectorX) < deadZone) movingVectorX = 0f
        if (kotlin.math.abs(movingVectorY) < deadZone) movingVectorY = 0f
    }

    /**
     * Reset joystick to center position.
     */
    private fun resetJoystick() {
        isPressed = false
        activePointerId = -1
        knobCenterX = baseCenterX
        knobCenterY = baseCenterY
        movingVectorX = 0f
        movingVectorY = 0f
    }

    /**
     * Draw the joystick on the canvas.
     */
    fun draw(canvas: Canvas) {
        // Draw base circle
        canvas.drawCircle(baseCenterX, baseCenterY, baseRadius, basePaint)
        canvas.drawCircle(baseCenterX, baseCenterY, baseRadius, baseOutlinePaint)

        // Draw knob
        canvas.drawCircle(knobCenterX, knobCenterY, knobRadius, knobPaint)
        canvas.drawCircle(knobCenterX, knobCenterY, knobRadius, knobOutlinePaint)
    }

    /**
     * Get current X movement vector (-1 to 1).
     */
    fun getMovingVectorX(): Float = movingVectorX

    /**
     * Get current Y movement vector (-1 to 1).
     */
    fun getMovingVectorY(): Float = movingVectorY

    /**
     * Check if joystick is currently being touched.
     */
    fun isActive(): Boolean = isPressed
}
