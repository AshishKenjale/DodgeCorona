package com.project.dodgekarona

import android.annotation.SuppressLint
import android.content.pm.ActivityInfo
import android.graphics.Point
import android.os.Bundle
import android.view.MotionEvent
import android.view.View
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
import com.assignment.userinformationapp.PrefsHelper

/**
 * Created by Ashish Kenjale on 5/05/20.
 */
class GamePlayActivity : AppCompatActivity(), View.OnTouchListener {

    lateinit var mGameSurface: GameSurface
    val screenSize = Point()

    @SuppressLint("SourceLockedOrientationActivity")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        Log.i(TAG, "in onCreate()")

        doInit()
        setUpScreenOptions()

        mGameSurface = GameSurface(this, screenSize.x, screenSize.y)
        mGameSurface.setOnTouchListener(this)
        setContentView(mGameSurface)
    }

    private fun doInit() {
        PrefsHelper.init(this)
        SoundManager.init(baseContext)
    }

    @SuppressLint("SourceLockedOrientationActivity")
    private fun setUpScreenOptions() {
        supportActionBar?.hide()
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT

        //set full screen
        this.window.setFlags(
            WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN
        )
        //keep screen on
        this.window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)

        fetchScreenSize()
    }

    private fun fetchScreenSize() {
        val display = windowManager.defaultDisplay
        display.getSize(screenSize)

        Log.i(TAG, "Screen size: (${screenSize.x}, ${screenSize.y})")
    }

    override fun onResume() {
        super.onResume()
        Log.i(TAG, "in onResume()")
        mGameSurface.resume()
        SoundManager.startSongLoop(SoundManager.GAME_MUSIC, baseContext)
    }

    override fun onStart() {
        super.onStart()
        Log.i(TAG, "in onStart()")
    }

    override fun onPause() {
        super.onPause()
        Log.i(TAG, "in onPause()")
        mGameSurface.pause()
        SoundManager.endSongLoop(SoundManager.GAME_MUSIC)
    }

    override fun onStop() {
        super.onStop()
        Log.i(TAG, "in onStop()")
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.i(TAG, "in onDestroy()")
    }

    override fun onTouch(v: View?, event: MotionEvent?): Boolean {
        Log.i(TAG, "in onTouch((v: View?, event: MotionEvent?)")
        return mGameSurface.handleTouchEvent(event)
    }
}
