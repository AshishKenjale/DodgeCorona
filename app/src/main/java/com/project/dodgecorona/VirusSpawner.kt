package com.project.dodgecorona

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Point
import com.assignment.userinformationapp.PrefsHelper

class VirusSpawner(gameScene: GameScene, context: Context) {

    private val gameScene: GameScene
    private val context: Context

    val obstacles = mutableListOf<VirusObstaclePair>()
    private val OBSTACLE_GAP = 450
    private val MAX_SPEED = 2f

    private val scoreTextPaint: Paint
    private val scoreLocationOffset: Point

    private var hiScoreText = ""
    private val hiScoreTextPaint: Paint
    private val hiScoreLocationOffset: Point

    private val DELTA_TIME = 40
    private val VICTORY_SCORE = 500
    private var score = 0

    init {
        this.gameScene = gameScene
        this.context = context

        scoreTextPaint = Paint()
        scoreTextPaint.color = Color.MAGENTA
        scoreTextPaint.textSize = 80f
        scoreLocationOffset = Point(50, 30)

        hiScoreTextPaint = Paint()
        hiScoreTextPaint.color = Color.DKGRAY
        hiScoreTextPaint.textSize = 60f
        hiScoreLocationOffset = Point(-50, 30)

        populateVirusObstacles()

        hiScoreText = "HI " + PrefsHelper.read(PrefsHelper.HIGH_SCORE, 0)
    }

    fun populateVirusObstacles() {
        var currY = -GameSurface.screenHeight - 60
        while (currY < 0) {
            val virusPair = VirusObstaclePair(currY, gameScene, context)
            obstacles.add(VirusObstaclePair(currY, gameScene, context))
            currY += virusPair.virus1.safeSocialDistance.toInt() + OBSTACLE_GAP
        }
    }

    fun update(gameTick: Long) {
        var speed: Float =
                    Math.sqrt((gameTick) / 100.0).toFloat() * GameSurface.screenHeight / 10000.0f

        if (speed > MAX_SPEED) {
            speed = MAX_SPEED
        }
        Log.i(TAG, "Speed: $speed Elapsed Time: $DELTA_TIME D: ${speed * DELTA_TIME}")
        for (ob in obstacles) {
            ob.incrementY(speed * DELTA_TIME)
        }
        val obstacle = obstacles[obstacles.size - 1].virus1
        if (obstacle.center.y - 2 * obstacle.safeSocialDistance >= GameSurface.screenHeight) {
            val yStart =
                (obstacles[0].virus1.center.y + obstacles[0].virus1.safeSocialDistance - 2 * obstacles[0].virus1.safeSocialDistance).toInt() - OBSTACLE_GAP
            val virusPair = VirusObstaclePair(yStart, gameScene, context)

            obstacles.add(0, virusPair)
            obstacles.removeAt(obstacles.size - 1)

            Log.i(TAG, "obstacles count: ${obstacles.size}")

            //Increment score ONLY if Player if alive
            if (gameScene.player?.alive ?: return) {
                score++

                if (score == VICTORY_SCORE) {
                    gameScene.setVictoryState()
                }
                if (score > PrefsHelper.read(PrefsHelper.HIGH_SCORE, 0)!!) {
                    PrefsHelper.write(PrefsHelper.HIGH_SCORE, score)
                }
            }
        }

        hiScoreText = "HI " + PrefsHelper.read(PrefsHelper.HIGH_SCORE, 0)
    }

    fun draw(canvas: Canvas) {
        for (ob in obstacles)
            ob.draw(canvas)

        canvas.drawText(
            score.toString(),
            scoreLocationOffset.x.toFloat(),
            scoreLocationOffset.y + scoreTextPaint.descent() - scoreTextPaint.ascent(),
            scoreTextPaint
        )

        canvas.drawText(
            hiScoreText,
            canvas.getWidth() - hiScoreTextPaint.measureText(hiScoreText) + hiScoreLocationOffset.x,
            hiScoreLocationOffset.y + (hiScoreTextPaint.descent() - hiScoreTextPaint.ascent()),
            hiScoreTextPaint
        )
    }
}