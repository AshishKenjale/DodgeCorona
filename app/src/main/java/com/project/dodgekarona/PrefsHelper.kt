package com.assignment.userinformationapp

import android.content.Context
import android.content.SharedPreferences

/**
 * Created by Ashish Kenjale on 5/05/20.
 */
object PrefsHelper {

    private lateinit var prefs: SharedPreferences
    private const val PREFS_NAME = "params"
    const val HIGH_SCORE = "highScore"

    fun init(context: Context) {
        prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
    }

    fun read(key: String, value: String): String? {
        return prefs.getString(key, value)
    }

    fun read(key: String, value: Int): Int? {
        return prefs.getInt(key, value)
    }

    fun read(key: String, value: Long): Long? {
        return prefs.getLong(key, value)
    }

    fun write(key: String, value: String) {
        val prefsEditor: SharedPreferences.Editor = prefs.edit()
        with(prefsEditor) {
            putString(key, value)
            commit()
        }
    }

    fun write(key: String, value: Int) {
        val prefsEditor: SharedPreferences.Editor = prefs.edit()
        with(prefsEditor) {
            putInt(key, value)
            commit()
        }
    }

    fun write(key: String, value: Long) {
        val prefsEditor: SharedPreferences.Editor = prefs.edit()
        with(prefsEditor) {
            putLong(key, value)
            commit()
        }
    }

    fun read(key: String, value: Float): Float {
        return prefs.getFloat(key, value)
    }

    fun write(key: String, value: Float) {
        val prefsEditor: SharedPreferences.Editor = prefs.edit()
        with(prefsEditor) {
            putFloat(key, value)
            commit()
        }
    }

    fun read(key: String, value: Boolean): Boolean {
        return prefs.getBoolean(key, value)
    }

    fun write(key: String, value: Boolean) {
        val prefsEditor: SharedPreferences.Editor = prefs.edit()
        with(prefsEditor) {
            putBoolean(key, value)
            commit()
        }
    }

    // Joystick settings keys
    const val JOYSTICK_SIZE = "joystick_size"
    const val JOYSTICK_OPACITY = "joystick_opacity"
    const val JOYSTICK_POSITION = "joystick_position" // 0 = left, 1 = right
    const val JOYSTICK_INVERT_X = "joystick_invert_x"
    const val JOYSTICK_INVERT_Y = "joystick_invert_y"

    // Default values
    const val DEFAULT_JOYSTICK_SIZE = 1.0f
    const val DEFAULT_JOYSTICK_OPACITY = 0.5f
    const val DEFAULT_JOYSTICK_POSITION = 0 // left
    const val DEFAULT_JOYSTICK_INVERT_X = false
    const val DEFAULT_JOYSTICK_INVERT_Y = false
}