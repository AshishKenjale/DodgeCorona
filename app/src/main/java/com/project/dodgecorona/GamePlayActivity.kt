package com.project.dodgecorona

import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.ActivityInfo
import android.graphics.Point
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.Bundle
import android.view.MotionEvent
import android.view.View
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
import com.assignment.userinformationapp.PrefsHelper
import kotlin.math.absoluteValue


class GamePlayActivity : AppCompatActivity(), SensorEventListener, View.OnTouchListener {

    private var mSensorManager: SensorManager? = null
    private var mGravitySensor: Sensor? = null
    lateinit var mGameSurface: GameSurface
    val screenSize = Point()

    //For Filtering Accelerometer Data
    private val ADAPTIVE_ACCEL_FILTER = true
    var lastAccel = FloatArray(3)
    var accelFilter = FloatArray(3)

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
        setUpGravitySensor()
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
        registerGravitySensorListener()
        SoundManager.startSongLoop(SoundManager.GAME_MUSIC, baseContext)
    }

    override fun onStart() {
        super.onStart()
        Log.i(TAG, "in onStart()")
    }

    private fun registerGravitySensorListener() {
        mSensorManager?.registerListener(this, mGravitySensor, SensorManager.SENSOR_DELAY_NORMAL)
        Log.i(TAG, "registeredGravitySensorListener")
    }

    private fun setUpGravitySensor() {
        mSensorManager = getSystemService(Context.SENSOR_SERVICE) as SensorManager
        mGravitySensor = mSensorManager?.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)
    }

    private fun unregisterAllSensorListeners() {
        mSensorManager?.unregisterListener(this)
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {

    }

    fun onFilteredAccelerometer(
        accelX: Float,
        accelY: Float,
        accelZ: Float
    ) { // high pass filter
        val updateFreq = 25f // match this to your update speed
        val cutOffFreq = 0.9f
        val RC = 1.0f / cutOffFreq
        val dt = 1.0f / updateFreq
        val filterConstant = RC / (dt + RC)
        var alpha = filterConstant
        val kAccelerometerMinStep = 0.033f
        val kAccelerometerNoiseAttenuation = 3.0f
        if (ADAPTIVE_ACCEL_FILTER) {
            val d: Float = clamp(
                (
                        norm(
                            accelFilter[0],
                            accelFilter[1],
                            accelFilter[2]
                        ).toFloat() - norm(accelX, accelY, accelZ).toFloat()
                        ).absoluteValue / kAccelerometerMinStep - 1.0f, 0.0f, 1.0f
            )
            alpha =
                d * filterConstant / kAccelerometerNoiseAttenuation + (1.0f - d) * filterConstant
        }
        accelFilter[0] =
            (alpha * (accelFilter[0] + accelX - lastAccel[0]))
        accelFilter[1] =
            (alpha * (accelFilter[1] + accelY - lastAccel[1]))
        accelFilter[2] =
            (alpha * (accelFilter[2] + accelZ - lastAccel[2]))
        lastAccel[0] = accelX
        lastAccel[1] = accelY
        lastAccel[2] = accelZ

        val vectorX = lastAccel[0]
        val vectorY = lastAccel[1]

        Log.i(TAG, "Filtered alpha: $alpha vectorX: ${-vectorX}, vectorY:${vectorY}")

        mGameSurface.player?.setMovingVector(
            -(vectorX),
            (vectorY)
        )
    }

    fun clamp(v: Float, min: Float, max: Float): Float {
        return if (v > max) max else if (v < min) min else v
    }

    fun norm(x: Float, y: Float, z: Float): Double {
        return Math.sqrt((x * x + y * y + z * z).toDouble())
    }

    override fun onSensorChanged(event: SensorEvent?) {
        event?.let {
            //            for portrait mode
            val vectorX = (it.values[0]).toDouble()
            val vectorY = (it.values[1]).toDouble()
            Log.i(TAG, "vectorX: ${-vectorX}, vectorY:${vectorY}")

            onFilteredAccelerometer(it.values[0], it.values[1], it.values[2])
        }
    }

    override fun onPause() {
        super.onPause()
        Log.i(TAG, "in onPause()")
        mGameSurface.pause()
        unregisterAllSensorListeners()
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
