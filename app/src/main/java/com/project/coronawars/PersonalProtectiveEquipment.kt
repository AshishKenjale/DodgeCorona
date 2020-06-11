package com.project.coronawars

import android.content.Context
import android.graphics.*
import android.util.Log
import kotlin.random.Random

class PersonalProtectiveEquipment(
    gameScene: GameScene,
    var center: Point, //center of the bitmap
    var radius: Int,
    context: Context
) {

    private var bitmap: Bitmap? = null
    var speed = Point()
    val MINIMUM_SAFE_SOCIAL_DISTANCE = 0

    var safeSocialDistance = 0f
    var spawner: PPESpawner? = null

    private val greenLinePaint = Paint().apply {
        color = Color.GREEN
        strokeWidth = 5f
        style = Paint.Style.STROKE
    }

    private val redLinePaint = Paint().apply {
        color = Color.RED
        strokeWidth = 5f
        style = Paint.Style.STROKE
    }

    init {
        //TODO: set bitmap based on level. maybe set virus for level so and so on
        if (bitmap == null) bitmap =
            BitmapFactory.decodeResource(context.resources, R.drawable.mask3)
        bitmap?.let {
            bitmap = Bitmap.createScaledBitmap(it, 2 * it.height / 3, 2 * it.height / 3, true)
        }

        bitmap?.let {
            this.radius = it.height / 2
        }
        this.safeSocialDistance = this.radius.toFloat() + MINIMUM_SAFE_SOCIAL_DISTANCE

        val lowerFactor = -25
        val upperFactor = 25

        speed.x = if (Random.nextBoolean()) Random.nextInt(
            lowerFactor,
            upperFactor
        ) + 1 else -Random.nextInt(lowerFactor, upperFactor) - 1
        speed.y = 25
        Log.i(TAG, "Speed: (${speed.x}, ${speed.y})")
    }

    fun update() {
        center.x += speed.x
        center.y += speed.y
        checkHorizontalScreenBounds()
    }

    //check circles touching bounds of screen
    fun checkHorizontalScreenBounds() {
        val screenWidth = GameSurface.screenWidth

        if (center.x >= screenWidth - radius) {
            center.x = screenWidth - radius
            speed.x = speed.x * -1
        }
        if (center.x <= radius) {
            center.x = radius
            speed.x = speed.x * -1
        }
    }

    fun die() {
        spawner?.reportDeath(this)
        Log.i(TAG, "Received Mask.")
    }

    fun registerSpawner(PPESpawner: PPESpawner) {
        this.spawner = PPESpawner
    }

    fun draw(canvas: Canvas) {
        this.bitmap?.let {
            val left = this.center.x - this.radius
            val top = this.center.y - this.radius

            canvas.drawBitmap(it, left.toFloat(), top.toFloat(), null) //null
        }
    }

}