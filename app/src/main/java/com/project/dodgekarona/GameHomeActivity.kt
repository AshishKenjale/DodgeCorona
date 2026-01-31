package com.project.dodgekarona

import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.project.dodgekarona.databinding.ActivityGameHomeBinding
import java.util.*

/**
 * Created by Ashish Kenjale on 5/05/20.
 */
class GameHomeActivity : AppCompatActivity() {
    private lateinit var binding: ActivityGameHomeBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityGameHomeBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        binding.startGameButton.setOnClickListener {
            val navigationIntent = Intent(this, GamePlayActivity::class.java)
            startActivity(navigationIntent)
        }

        binding.controlsButton.setOnClickListener(View.OnClickListener {
            AlertDialog.Builder(this)
                .setTitle(getString(R.string.controls_title))
                .setMessage(getString(R.string.controls_text))
                .setNegativeButton(
                    "OK"
                ) { dialog, which -> }.show()
        })

        binding.joystickControlButton.setOnClickListener {
            val navigationIntent = Intent(this, JoystickSettingsActivity::class.java)
            startActivity(navigationIntent)
        }

        binding.aboutButton.setOnClickListener(View.OnClickListener {
            AlertDialog.Builder(this)
                .setTitle(getString(R.string.about_dodgekarona_title))
                .setMessage(getLocaleBasedAboutUsMessage())
                .setNegativeButton(
                    "OK"
                ) { dialog, which -> }.show()
        })
    }

    private fun getLocaleBasedAboutUsMessage(): String {
        val message: String
        Log.i(TAG, "isMetric: ${Locale.getDefault().isMetric()}")
        if (Locale.getDefault().isMetric()) {
            message = String.format(getString(R.string.about_game_text), getString(R.string.safe_social_distance_in_meter))
        } else {
            message = String.format(getString(R.string.about_game_text), getString(R.string.safe_social_distance_in_feet))
        }
        return message
    }
}
