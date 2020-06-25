package com.project.dodgekarona

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.view.SurfaceHolder
import java.util.*

/**
 * Created by Ashish Kenjale on 5/05/20.
 */
class GameScene {
    // Properties
    val tickRate = 25
    val context: Context
    var player: Player? = null
    var ppeSpawner: PPESpawner? = null
    var virusSpawner: VirusSpawner? = null
    val gameSurface: GameSurface
    var gameState = GameState.READY
    private var previousGameState = GameState.RUNNING
    var gameTick: Long = 0
    /** Used to figure out elapsed time between frames  */
    private var mLastTime: Long = 0

    // Graphics Members
    private var paint: Paint
    lateinit var surfaceHolder: SurfaceHolder
    var canvasWidth = 0
    var canvasHeight = 0

    private val canvasBorderPaint = Paint().apply {
        color = Color.RED
        strokeWidth = 5f
        style = Paint.Style.STROKE
    }

    constructor(
        player: Player?,
        context: Context,
        gameSurface: GameSurface,
        surfaceHolder: SurfaceHolder
    ) {
        this.context = context
        this.surfaceHolder = surfaceHolder
        this.gameSurface = gameSurface
        this.player = player
        this.player?.currentGameScene = this
        this.paint = Paint()

        ppeSpawner = PPESpawner(this, context)
        virusSpawner = VirusSpawner(this, context)

        mLastTime = System.currentTimeMillis() + 100
    }

    fun pause() {
        if (gameState !== GameState.PAUSED) {
            previousGameState = gameState
            gameState = GameState.PAUSED
        }
    }

    fun unpause() {
        if (gameState === GameState.PAUSED) {
            gameState = previousGameState
        }
    }

    fun startGame() {
        Log.i(TAG, "in startGame()")
        if (gameState !== GameState.RUNNING) {
            gameState = GameState.RUNNING
        }
    }

    fun togglePauseUnpause() {
        Log.i(TAG, "in togglePauseUnpause")

        if (gameState === GameState.PAUSED) {
            mLastTime = System.currentTimeMillis() + 100
            gameState = previousGameState
        } else if (gameState !== GameState.PAUSED) {
            previousGameState = gameState
            gameState = GameState.PAUSED
        }
    }

    //The Game Loop
    fun run(): Boolean {
        Log.i(TAG, "gamesurface.running ${gameSurface.running}")
        var framesThisSecond = 0
        var thisSecondStart = System.currentTimeMillis()
        while (gameState !== GameState.LOST && gameState !== GameState.WON && gameSurface.running) {
            val tickStart = System.currentTimeMillis()
            if (gameState !== GameState.PAUSED && gameState !== GameState.READY) {
                update()
            } else {
                mLastTime = System.currentTimeMillis()
            }
            draw()
            control(tickStart)
            framesThisSecond++
            if (System.currentTimeMillis() - 2000 > thisSecondStart) {
                println("FPS: " + framesThisSecond / 2)
                thisSecondStart = System.currentTimeMillis()
                framesThisSecond = 0
            }
        }
        if (gameSurface.running) {
            when (gameState) {
                GameState.WON -> {
                    return false
                }
                GameState.LOST -> {
                    return false
                }
                else -> throw RuntimeException("ILLEGAL GAME STATE AT END OF LOOP")
            }
        }
        return false
    }

    fun setVictoryState() {
        if (gameState !== GameState.WON) {
            gameState = GameState.WON
        }
    }

    private fun update() {
        gameTick++

        val now = System.currentTimeMillis()

        // Do nothing if mLastTime is in the future.
        // This allows the game-start to delay the start of the physics
        // by 100ms or whatever.
        // Do nothing if mLastTime is in the future.
        // This allows the game-start to delay the start of the physics
        // by 100ms or whatever.
        if (mLastTime > now) return

        val elapsed: Long = (now - mLastTime)

        Log.i(TAG, "GameScene update() elapsed: $elapsed")

        player?.let {
            it.update(elapsed)
            if (!it.alive) {
                gameState = GameState.LOST
                Log.i(TAG, "updating state to GameState.LOST")
            } else {
                virusSpawner?.update(gameTick)
                ppeSpawner?.updateAll(elapsed)
                ppeSpawner?.spawnVirus()
            }
        }
        mLastTime = now
    }

    // This draw function will be responsible for drawing each frame
    private fun draw() {
        if (surfaceHolder.getSurface().isValid()) { // acquire the canvas

            var canvas: Canvas? = null
            try { //get canvas from holder and lock it
                canvas = surfaceHolder.lockCanvas()
                //Synchronized
                canvas?.let {
                    doDraw(canvas)
                }
            } catch (e: Exception) { //Do nothing
            } finally {
                if (canvas != null) { //unlock canvas
                    surfaceHolder.unlockCanvasAndPost(canvas)
                }
            }
        }
    }

    private fun doDraw(screenCanvas: Canvas) {
        canvasWidth = screenCanvas.width // so that we only have to do this once
        canvasHeight = screenCanvas.height

        screenCanvas.drawColor(Color.WHITE)
        screenCanvas.drawRect(
            0f,
            0f,
            canvasWidth.toFloat(),
            canvasHeight.toFloat(),
            canvasBorderPaint
        )
        
        player?.draw(screenCanvas)
        virusSpawner?.draw(screenCanvas)
        ppeSpawner?.drawAll(screenCanvas)

        Log.i(TAG, "gameState: ${gameState.toString()}")
        if (gameState === GameState.READY) {
            paint.setColor(Color.RED)
            paint.setTextSize(100f)
            var readyMessage = context.getString(R.string.get_ready)
            var xPos = canvasWidth / 2 - (paint.measureText(readyMessage) / 2)
            var yPos = (canvasHeight / 2 - (paint.descent() + paint.ascent()) / 2)
            screenCanvas.drawText(readyMessage, xPos, yPos, paint)
            paint.setColor(Color.CYAN)
            paint.setTextSize(50f)
            readyMessage = context.getString(R.string.tap_screen_to_start)
            xPos = canvasWidth / 2 - (paint.measureText(readyMessage) / 2)
            yPos = (canvasHeight / 2 + 200 - (paint.descent() + paint.ascent()) / 2)
            screenCanvas.drawText(readyMessage, xPos, yPos, paint)
        } else if (gameState === GameState.PAUSED) {
            paint.setColor(Color.RED)
            paint.setTextSize(100f)
            var pausedMessage = context.getString(R.string.game_paused)
            var xPos = canvasWidth / 2 - (paint.measureText(pausedMessage) / 2)
            var yPos = (canvasHeight / 2 - (paint.descent() + paint.ascent()) / 2)
            screenCanvas.drawText(pausedMessage, xPos, yPos, paint)
            paint.setColor(Color.CYAN)
            paint.setTextSize(50f)
            pausedMessage = context.getString(R.string.tap_screen_to_resume)
            xPos = canvasWidth / 2 - (paint.measureText(pausedMessage) / 2)
            yPos = (canvasHeight / 2 + 200 - (paint.descent() + paint.ascent()) / 2)
            screenCanvas.drawText(pausedMessage, xPos, yPos, paint)
        } else if (gameState === GameState.WON) {
            paint.setColor(Color.GREEN)
            paint.setTextSize(100f)
            var victoryMessage = context.getString(R.string.victory)
            var xPos = canvasWidth / 2 - (paint.measureText(victoryMessage) / 2)
            var yPos = (canvasHeight / 2 - (paint.descent() + paint.ascent()) / 2)
            screenCanvas.drawText(victoryMessage, xPos, yPos, paint)
            paint.setColor(Color.CYAN)
            paint.setTextSize(50f)
            victoryMessage = context.getString(R.string.survived_the_pandemic)
            xPos = canvasWidth / 2 - (paint.measureText(victoryMessage) / 2)
            yPos = (canvasHeight / 2 + 200 - (paint.descent() + paint.ascent()) / 2)
            screenCanvas.drawText(victoryMessage, xPos, yPos, paint)
        } else if (gameState === GameState.LOST) {
            Log.i(TAG, "drawing GameState.LOST")
            paint.setColor(Color.RED)
            paint.setTextSize(100f)
            var victoryMessage = context.getString(R.string.game_over)
            var xPos = canvasWidth / 2 - (paint.measureText(victoryMessage) / 2)
            var yPos = (canvasHeight / 2 - (paint.descent() + paint.ascent()) / 2)
            screenCanvas.drawText(victoryMessage, xPos, yPos, paint)

            paint.setColor(Color.BLUE)
            paint.setTextSize(60f)
            victoryMessage = context.getString(R.string.you_got_infected)
            xPos = canvasWidth / 2 - (paint.measureText(victoryMessage) / 2)
            yPos = (canvasHeight / 2 + 150 - (paint.descent() + paint.ascent()) / 2)
            screenCanvas.drawText(victoryMessage, xPos, yPos, paint)

            victoryMessage = getLocaleBasedMessage()
            xPos = canvasWidth / 2 - (paint.measureText(victoryMessage) / 2)
            yPos = (canvasHeight / 2 + 220 - (paint.descent() + paint.ascent()) / 2)
            screenCanvas.drawText(victoryMessage, xPos, yPos, paint)

            victoryMessage = context.getString(R.string.stay_home)
            xPos = canvasWidth / 2 - (paint.measureText(victoryMessage) / 2)
            yPos = (canvasHeight / 2 + 290 - (paint.descent() + paint.ascent()) / 2)
            screenCanvas.drawText(victoryMessage, xPos, yPos, paint)

            paint.setColor(Color.CYAN)
            paint.setTextSize(50f)
            victoryMessage = context.getString(R.string.tap_screen_to_restart)
            xPos = canvasWidth / 2 - (paint.measureText(victoryMessage) / 2)
            yPos = (canvasHeight / 2 + 370 - (paint.descent() + paint.ascent()) / 2)
            screenCanvas.drawText(victoryMessage, xPos, yPos, paint)
        }

        paint.reset()
    }

    private fun getLocaleBasedMessage(): String {
        val message: String
        Log.i(TAG, "isMetric: ${Locale.getDefault().isMetric()}")
        if (Locale.getDefault().isMetric()) {
            message = String.format(context.getString(R.string.too_close_to_infected_person), context.getString(R.string.safe_social_distance_in_meter))
        } else {
            message = String.format(context.getString(R.string.too_close_to_infected_person), context.getString(R.string.safe_social_distance_in_feet))
        }
        return message
    }

    private fun control(start: Long) {
        try {
            val remaining: Long =
                1000 / tickRate - (System.currentTimeMillis() - start)
            Log.i(
                TAG,
                "remaining: $remaining Time per frame: ${System.currentTimeMillis() - start}"
            )
            if (remaining > 0) {
                Thread.sleep(remaining)
            }
        } catch (e: InterruptedException) {
            e.printStackTrace()
        }
    }
}