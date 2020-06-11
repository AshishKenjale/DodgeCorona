package com.project.coronawars

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Log
import android.view.MotionEvent
import android.view.SurfaceHolder
import android.view.SurfaceView

class GameSurface(context: Context?, screenWidth: Int, screenHeight: Int) :
    SurfaceView(context), Runnable {
    var player: Player? = null
    var gameScene: GameScene? = null
    var surfaceHolder: SurfaceHolder

    // Here is the Thread and two control variables
    private lateinit var gameThread: Thread
    // This volatile variable can be accessed
    // from inside and outside the thread
    @Volatile
    var running: Boolean = false

    //can be accessed by all other classes
    companion object {
        var screenWidth: Int = 0
        var screenHeight: Int = 0
    }

    init {
        //GameSurface focusable so it can handle events
        this.isFocusable = true
        this.surfaceHolder = holder

        GameSurface.screenWidth = screenWidth
        GameSurface.screenHeight = screenHeight
    }

    // This function is called by GamePlayActivity
    // when the user quits the app
    fun pause() {
        running = false
        Log.i(TAG, "pausing new game loop pause() running $running")

        gameScene?.pause()
        try {
            // Stop the thread
            gameThread.join()
        } catch (e: InterruptedException) {
            Log.e("Error:", "joining thread")
        }

    }

    // This function is called by MainActivity
    // when the player starts the app
    fun resume() {
        running = true

        Log.i(TAG, "starting new game loop resume() running $running")

        // Initialize the instance of Thread
        gameThread = Thread(this)

        // Start the thread
        gameThread.start()
    }

    private fun setPlayer() {
        val chibiBitmap1 = BitmapFactory.decodeResource(this.resources, R.drawable.warrior_m)

        var maskBitmap = BitmapFactory.decodeResource(resources, R.drawable.mask3)
        maskBitmap?.let {
            maskBitmap = Bitmap.createScaledBitmap(it, 1 * it.height / 4, 1 * it.height / 4, true)
        }
        this.player =
            Player(
                context,
                this,
                chibiBitmap1,
                maskBitmap,
                screenWidth / 2 - 32,
                screenHeight - 300
            )
    }

    override fun run() {
        Log.i(TAG, "in run()")

        setPlayer()
        gameScene = GameScene(player, context, this, surfaceHolder)
        gameScene?.let {
            running = it.run()
            Log.i(TAG, "run() $running")
        }
    }

    fun togglePauseUnpause() {
        Log.i(TAG, "in togglePauseUnpause()")
        gameScene?.togglePauseUnpause()
    }

    fun startGame() {
        Log.i(TAG, "in startGame()")
        gameScene?.startGame()
    }

    fun handleTouchEvent(e: MotionEvent?): Boolean {
        Log.i(TAG, "in handleTouchEvent()")
        e?.let {
            val maskedAction = e.actionMasked
            when (maskedAction) {
                MotionEvent.ACTION_DOWN -> {
                    Log.i(TAG, "MotionEvent.ACTION_DOWN")
                }
                MotionEvent.ACTION_MOVE -> {
                    Log.i(TAG, "MotionEvent.ACTION_MOVE")
                }
                MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL -> {
                    Log.i(TAG, "MotionEvent.ACTION_UP or ACTION_CANCEL")

                    //TODO: test this case
                    if (gameScene?.gameState === GameState.LOST) {
                        resume()
                    } else if (gameScene?.gameState == GameState.WON) {
                        resume()
                    } else if (gameScene?.gameState === GameState.READY) {
                        startGame()
                    } else {
                        togglePauseUnpause()
                    }
                }
                else -> return false
            }
        }
        return true
    }
}