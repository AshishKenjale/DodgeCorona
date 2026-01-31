package com.project.dodgekarona

import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.view.MotionEvent
import com.assignment.userinformationapp.PrefsHelper
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
    // Joystick settings (loaded from preferences)
    private var sizeMultiplier: Float = PrefsHelper.DEFAULT_JOYSTICK_SIZE
    private var opacity: Float = PrefsHelper.DEFAULT_JOYSTICK_OPACITY
    private var position: Int = PrefsHelper.DEFAULT_JOYSTICK_POSITION
    private var invertX: Boolean = PrefsHelper.DEFAULT_JOYSTICK_INVERT_X
    private var invertY: Boolean = PrefsHelper.DEFAULT_JOYSTICK_INVERT_Y

    // Joystick positioning - bottom corner of screen
    private var baseRadius: Float = 0f
    private var knobRadius: Float = 0f
    private var baseCenterX: Float = 0f
    private var baseCenterY: Float = 0f

    // Current knob position
    private var knobCenterX: Float = 0f
    private var knobCenterY: Float = 0f

    // Movement vectors (normalized -1 to 1)
    private var movingVectorX: Float = 0f
    private var movingVectorY: Float = 0f

    // Touch tracking
    private var isPressed: Boolean = false
    private var activePointerId: Int = -1

    // Paint objects for drawing
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

    init {
        loadSettings()
        calculateDimensions()
    }

    /**
     * Load joystick settings from preferences.
     */
    private fun loadSettings() {
        sizeMultiplier = PrefsHelper.read(PrefsHelper.JOYSTICK_SIZE, PrefsHelper.DEFAULT_JOYSTICK_SIZE)
        opacity = PrefsHelper.read(PrefsHelper.JOYSTICK_OPACITY, PrefsHelper.DEFAULT_JOYSTICK_OPACITY)
        position = PrefsHelper.read(PrefsHelper.JOYSTICK_POSITION, PrefsHelper.DEFAULT_JOYSTICK_POSITION) ?: PrefsHelper.DEFAULT_JOYSTICK_POSITION
        invertX = PrefsHelper.read(PrefsHelper.JOYSTICK_INVERT_X, PrefsHelper.DEFAULT_JOYSTICK_INVERT_X)
        invertY = PrefsHelper.read(PrefsHelper.JOYSTICK_INVERT_Y, PrefsHelper.DEFAULT_JOYSTICK_INVERT_Y)
        updatePaintOpacity()
    }

    /**
     * Recalculate joystick dimensions and position based on settings.
     */
    private fun calculateDimensions() {
        // Size joystick relative to screen (responsive design)
        val smallerDimension = min(screenWidth, screenHeight)
        baseRadius = smallerDimension * 0.12f * sizeMultiplier
        knobRadius = baseRadius * 0.5f

        // Position based on setting (0 = left, 1 = right, 2 = center)
        val padding = baseRadius * 1.5f
        baseCenterX = when (position) {
            0 -> padding + baseRadius // Left
            1 -> screenWidth - padding - baseRadius // Right
            2 -> screenWidth / 2f // Center
            else -> padding + baseRadius // Default to left
        }
        baseCenterY = screenHeight - padding - baseRadius

        // Initialize knob at center
        knobCenterX = baseCenterX
        knobCenterY = baseCenterY
    }

    /**
     * Update paint alpha values based on opacity setting.
     */
    private fun updatePaintOpacity() {
        val baseAlpha = (80 * opacity * 2).toInt().coerceIn(0, 255)
        val baseOutlineAlpha = (120 * opacity * 2).toInt().coerceIn(0, 255)
        val knobAlpha = (180 * opacity * 2).toInt().coerceIn(0, 255)
        val knobOutlineAlpha = (200 * opacity * 2).toInt().coerceIn(0, 255)

        basePaint.color = Color.argb(baseAlpha, 128, 128, 128)
        baseOutlinePaint.color = Color.argb(baseOutlineAlpha, 100, 100, 100)
        knobPaint.color = Color.argb(knobAlpha, 60, 60, 60)
        knobOutlinePaint.color = Color.argb(knobOutlineAlpha, 40, 40, 40)
    }

    /**
     * Reload settings from preferences. Call this when returning to game.
     */
    fun reloadSettings() {
        loadSettings()
        calculateDimensions()
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
        // Default: Invert X for intuitive left/right movement
        var rawVectorX = -(knobCenterX - baseCenterX) / baseRadius
        var rawVectorY = (knobCenterY - baseCenterY) / baseRadius

        // Apply user's inversion settings
        movingVectorX = if (invertX) -rawVectorX else rawVectorX
        movingVectorY = if (invertY) -rawVectorY else rawVectorY

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

    /**
     * Get the X coordinate of the joystick center.
     */
    fun getCenterX(): Float = baseCenterX

    /**
     * Get the Y coordinate of the joystick center.
     */
    fun getCenterY(): Float = baseCenterY

    /**
     * Get the radius of the joystick base.
     */
    fun getBaseRadius(): Float = baseRadius
}
