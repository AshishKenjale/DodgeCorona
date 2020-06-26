/*
 * simulates character in the game
 */
package com.project.dodgekarona

import android.content.Context
import android.graphics.*
import androidx.core.content.ContextCompat
import kotlin.math.sqrt

/**
 * Created by Ashish Kenjale on 5/05/20.
 */
class Player(
    context: Context,
    private val gameSurface: GameSurface,
    image: Bitmap?,
    maskBitmap: Bitmap?,
    x: Int,
    y: Int
) :
    GameObject(image, 4, 3, x, y) {
    private val context: Context
    //Row index of image being used
    private var rowUsing = ROW_BOTTOM_TO_TOP
    private var colUsing = 1
    //arrays for each row and motion of chara
    private val leftToRights: Array<Bitmap?>
    private val rightToLefts: Array<Bitmap?>
    private val topToBottoms: Array<Bitmap?>
    private val bottomToTops: Array<Bitmap?>
    private var movingVectorX = 0f
    private var movingVectorY = 0f

    var currentGameScene: GameScene? = null
    var rect: Rect = Rect()

    val startX: Int
    val startY: Int

    var alive = true
    var guarded = false
    var guardedTimeStamp: Long = -1

    val maskBitmap: Bitmap?

    val INVINCIBLE_TIME_IN_SECONDS = 5

    private val playerOutlinePaint = Paint().apply {
        color = Color.GREEN
        strokeWidth = 5f
        style = Paint.Style.STROKE
    }
    private val metallicGoldLinePaint = Paint().apply {
        color = ContextCompat.getColor(context, R.color.metallicGold)
        strokeWidth = 5f
        style = Paint.Style.STROKE
    }

    companion object {
        //bitmap subImage rows actions
        private const val ROW_BOTTOM_TO_TOP = 0
        private const val ROW_LEFT_TO_RIGHT = 1
        private const val ROW_TOP_TO_BOTTOM = 2
        private const val ROW_RIGHT_TO_LEFT = 3
        //velocity of game character (pixel/millisecond)
        const val VELOCITY = 0.3f
    }

    //constructor
    init {
        this.context = context
        this.startX = x
        this.startY = y
        leftToRights = arrayOfNulls(colCount) //assigns rows of chibi to moves
        rightToLefts = arrayOfNulls(colCount)
        topToBottoms = arrayOfNulls(colCount)
        bottomToTops = arrayOfNulls(colCount)
        for (col in 0 until colCount) {
            topToBottoms[col] = createSubImageAt(
                ROW_TOP_TO_BOTTOM,
                col
            ) //array of each individual chibi movement
            rightToLefts[col] =
                createSubImageAt(ROW_RIGHT_TO_LEFT, col)
            leftToRights[col] =
                createSubImageAt(ROW_LEFT_TO_RIGHT, col)
            bottomToTops[col] =
                createSubImageAt(ROW_BOTTOM_TO_TOP, col)
        }

        this.maskBitmap = maskBitmap
    }

    val moveBitmaps: Array<Bitmap?>?
        get() = when (rowUsing) {
            ROW_BOTTOM_TO_TOP -> bottomToTops
            ROW_LEFT_TO_RIGHT -> leftToRights
            ROW_RIGHT_TO_LEFT -> rightToLefts
            ROW_TOP_TO_BOTTOM -> topToBottoms
            else -> null
        }

    //returns current movement
    val currentMoveBitmap: Bitmap?
        get() { //returns current movement
            val bitmaps = moveBitmaps
            return bitmaps?.get(colUsing)
        }

    fun update(gameTick: Long) { //loops walking, alternates walk
        if (alive) {
            colUsing++
            if (colUsing >= colCount) {
                colUsing = 0
            }

            val deltaTime = gameTick

            //distance moved
            val distance = VELOCITY * deltaTime

            //calculate the new position of the game character
            val dx = (distance * movingVectorX).toInt()
            val dy = (distance * movingVectorY).toInt()

            Log.i(TAG, "dx: $dx dy: $dy")

            //update player position ONLY if player does not touch horizontal edges of screen
            if (x + dx < 0) {
                x = 0
            } else if (x + dx > gameSurface.width - width) {
            } else {
                x += dx
            }
            //update player position ONLY if player does not touch vertical edges of screen
            if (y + dy < 0) {
                y = 0
            } else if (y + dy > gameSurface.height - height) {
            } else {
                y += dy
            }
            //chose what bitmap row to use if diagonal vector
            if (movingVectorX > 0) {
                if (movingVectorY > 0 && Math.abs(movingVectorX) < Math.abs(
                        movingVectorY
                    )
                ) {
                    rowUsing = ROW_TOP_TO_BOTTOM
                } else if (movingVectorY < 0 && Math.abs(movingVectorX) < Math.abs(
                        movingVectorY
                    )
                ) {
                    rowUsing = ROW_BOTTOM_TO_TOP
                } else {
                    rowUsing = ROW_LEFT_TO_RIGHT
                }
            } else {
                if (movingVectorY > 0 && Math.abs(movingVectorX) < Math.abs(
                        movingVectorY
                    )
                ) {
                    rowUsing = ROW_TOP_TO_BOTTOM
                } else if (movingVectorY < 0 && Math.abs(movingVectorX) < Math.abs(
                        movingVectorY
                    )
                ) {
                    rowUsing = ROW_BOTTOM_TO_TOP
                } else {
                    rowUsing = ROW_RIGHT_TO_LEFT
                }
            }
            detectCollision()
        }
    }

    private fun detectCollision() {
        Log.i(TAG, rect.toShortString())

        val now = System.currentTimeMillis()

        var elapsedTimeinSec = 0.0
        if (guardedTimeStamp > -1) {
            elapsedTimeinSec = (now - guardedTimeStamp) / 1000.0

            if (elapsedTimeinSec >= INVINCIBLE_TIME_IN_SECONDS) {
                guarded = false
                guardedTimeStamp = -1
            }
        }

        Log.i(TAG, "elapsed Time: $elapsedTimeinSec")

        //check collision with obstacles
        if (!guarded) {
            this.currentGameScene?.virusSpawner?.obstacles?.forEach { virusPair ->

                val c1 = virusPair.virus1.center
                val r1 = virusPair.virus1.safeSocialDistance

                val c2 = virusPair.virus2.center
                val r2 = virusPair.virus2.safeSocialDistance

                if (detectCollision(c1, r1) || detectCollision(c2, r2)) {
                    SoundManager.playSound(SoundManager.PLAYER_DEATH)
                    alive = false
                }
            }
        }
        //check collision with power-ups
        this.currentGameScene?.ppeSpawner?.ppeList?.forEach { ppeItem ->

            val c1 = ppeItem.center
            val r1 = ppeItem.safeSocialDistance

            if (detectCollision(c1, r1)) {
                ppeItem.die()
                guarded = true
                guardedTimeStamp = System.currentTimeMillis()
                SoundManager.playSound(SoundManager.PLAYER_POWER_UP)
            }
        }
    }

    private fun detectCollision(c: Point, r: Float): Boolean {
        return checkCircleRectCollision(
            c.x,
            c.y,
            r,
            rect.left,
            rect.top,
            rect.width(),
            rect.height()
        )
    }

    private fun checkCircleRectCollision(
        cx: Int,
        cy: Int,
        radius: Float,
        rx: Int,
        ry: Int,
        rw: Int,
        rh: Int
    ): Boolean { // temporary variables to set edges for testing
        var didCollide = false

        var testX = cx
        var testY = cy
        // which edge is closest?
        if (cx < rx) testX = rx // test left edge
        else if (cx > rx + rw) testX = rx + rw // right edge
        if (cy < ry) testY = ry // top edge
        else if (cy > ry + rh) testY = ry + rh // bottom edge
        // get distance from closest edges
        val distX = cx - testX
        val distY = cy - testY
        val distance: Double = sqrt((distX * distX + distY * distY).toDouble())
        // if the distance is less than the radius, collision!
        if (distance <= radius) {
            didCollide = true
        }
        return didCollide
    }

    fun draw(canvas: Canvas) {
        val bitmap = currentMoveBitmap
        //makes white pixels transparent.
        bitmap?.let {
            for (x in 0 until it.width) {
                for (y in 0 until it.height) {
                    if (it.getPixel(x, y) == Color.WHITE) {
                        it.setPixel(x, y, Color.TRANSPARENT)
                    }
                }
            }
            canvas.drawBitmap(it, x.toFloat(), y.toFloat(), null) //null
            Log.i(TAG, "x, y : (${x}, ${y}) width: ${it.getWidth()} height: ${it.getHeight()}")

            rect.set(x, y, x + it.getWidth(), y + it.getHeight())
            rect.inset(5, 4)

            if (guarded) {
                maskBitmap?.let {
                    val left = rect.right - it.width / 2
                    val top = rect.top - it.height / 2

                    val maskRect = Rect()
                    maskRect.set(left, top, left + it.getWidth(), top + it.getHeight())
                    canvas.drawRect(rect, metallicGoldLinePaint)
                    canvas.drawBitmap(it, left.toFloat(), top.toFloat(), null) //null
                }
            } else {
                if (alive) {
                    playerOutlinePaint.color = Color.GREEN
                } else {
                    playerOutlinePaint.color = Color.RED
                }
                canvas.drawRect(rect, playerOutlinePaint)
            }
        }
    }

    fun setMovingVector(movingVectorX: Float, movingVectorY: Float) {
        this.movingVectorX = movingVectorX
        this.movingVectorY = movingVectorY
        Log.i(TAG, "setMovingVector()")
    }
}