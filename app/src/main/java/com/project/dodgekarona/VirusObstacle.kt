package com.project.dodgekarona

import android.content.Context
import android.graphics.*
import kotlin.random.Random

/**
 * Created by Ashish Kenjale on 5/05/20.
 */
class VirusObstacle(
    gameScene: GameScene,
    isTop: Boolean,
    yPostion: Int,
    context: Context
) {
    private var bitmap: Bitmap? = null
    val MINIMUM_SAFE_SOCIAL_DISTANCE = 40

    //center of bitmap
    var center: Point = Point(0, 0)
    var radius: Int = 50

    var safeSocialDistance = 0f

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
        //TODO in V2: set bitmap based on level. maybe set obstacle for level so and so on
        if (bitmap == null) bitmap =
            BitmapFactory.decodeResource(context.resources, R.drawable.obstacle)
        bitmap?.let {
            bitmap = Bitmap.createScaledBitmap(it, it.height / 2, it.height / 2, true)
        }

        bitmap?.let {
            this.radius = it.height / 2
        }
        this.safeSocialDistance = this.radius.toFloat() + MINIMUM_SAFE_SOCIAL_DISTANCE

        center.y = yPostion
        if (isTop) {
            val randomCenter1 = Random.nextInt(
                safeSocialDistance.toInt(),
                GameSurface.screenWidth / 2 - safeSocialDistance.toInt()
            )
            center.x = randomCenter1
        } else {
            val randomCenter2 = Random.nextInt(
                GameSurface.screenWidth / 2 + safeSocialDistance.toInt(),
                GameSurface.screenWidth - safeSocialDistance.toInt()
            )
            center.x = randomCenter2
        }
    }

    fun incrementY(y: Float) {
        center.y += y.toInt()
    }

    fun draw(canvas: Canvas) {
        this.bitmap?.let {
            val left = this.center.x - this.radius
            val top = this.center.y - this.radius


            Log.i(TAG, "Center: (X,Y): ${center.x} , ${center.y}")
            canvas.drawBitmap(it, left.toFloat(), top.toFloat(), null) //null

            canvas.drawCircle(
                this.center.x.toFloat(),
                this.center.y.toFloat(),
                radius.toFloat(),
                redLinePaint
            )
            //draw bounding circlce to depict 6ft social distance
            canvas.drawCircle(
                this.center.x.toFloat(),
                this.center.y.toFloat(),
                safeSocialDistance,
                greenLinePaint
            )
        }
    }
}