package com.project.dodgekarona

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.view.MotionEvent
import android.view.SurfaceHolder
import android.view.SurfaceView

/**
 * Created by Ashish Kenjale on 5/05/20.
 */
class GameSurface(context: Context?, screenWidth: Int, screenHeight: Int) :
    SurfaceView(context), Runnable {
    var player: Player? = null
    var gameScene: GameScene? = null
    var surfaceHolder: SurfaceHolder

    // Virtual joystick for player control
    val virtualJoystick: VirtualJoystick

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

        // Initialize virtual joystick
        virtualJoystick = VirtualJoystick(screenWidth, screenHeight)
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

        // Reload joystick settings in case they were changed
        virtualJoystick.reloadSettings()

        // Initialize the instance of Thread
        gameThread = Thread(this)

        // Start the thread
        gameThread.start()
    }

    private fun setPlayer() {
        val chibiBitmap1 = BitmapFactory.decodeResource(this.resources, R.drawable.hagger)

        var maskBitmap = BitmapFactory.decodeResource(resources, R.drawable.mask)
        maskBitmap?.let {
            maskBitmap = Bitmap.createScaledBitmap(it, 1 * it.height / 4, 1 * it.height / 4, true)
        }

        // Position player above the joystick
        val joystickCenterX = virtualJoystick.getCenterX()
        val joystickTopY = virtualJoystick.getCenterY() - virtualJoystick.getBaseRadius()
        val playerStartX = (joystickCenterX - 32).toInt()
        val playerStartY = (joystickTopY - 150).toInt() // 150 pixels above joystick

        this.player =
            Player(
                context,
                this,
                chibiBitmap1,
                maskBitmap,
                playerStartX,
                playerStartY
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
            // During active gameplay, let joystick handle touch first
            if (gameScene?.gameState === GameState.RUNNING) {
                if (virtualJoystick.handleTouchEvent(it)) {
                    // Update player movement from joystick
                    player?.setMovingVector(
                        virtualJoystick.getMovingVectorX(),
                        virtualJoystick.getMovingVectorY()
                    )
                    return true
                }
            }

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