package com.project.dodgekarona

import android.os.Bundle
import android.widget.SeekBar
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.assignment.userinformationapp.PrefsHelper
import com.project.dodgekarona.databinding.ActivityJoystickSettingsBinding

/**
 * Activity for configuring virtual joystick settings.
 */
class JoystickSettingsActivity : AppCompatActivity() {

    private lateinit var binding: ActivityJoystickSettingsBinding

    private var currentSize: Float = PrefsHelper.DEFAULT_JOYSTICK_SIZE
    private var currentOpacity: Float = PrefsHelper.DEFAULT_JOYSTICK_OPACITY
    private var currentPosition: Int = PrefsHelper.DEFAULT_JOYSTICK_POSITION

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityJoystickSettingsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = getString(R.string.joystick_settings_title)

        loadCurrentSettings()
        setupUI()
        updatePreview()
    }

    private fun loadCurrentSettings() {
        currentSize = PrefsHelper.read(PrefsHelper.JOYSTICK_SIZE, PrefsHelper.DEFAULT_JOYSTICK_SIZE)
        currentOpacity = PrefsHelper.read(PrefsHelper.JOYSTICK_OPACITY, PrefsHelper.DEFAULT_JOYSTICK_OPACITY)
        currentPosition = PrefsHelper.read(PrefsHelper.JOYSTICK_POSITION, PrefsHelper.DEFAULT_JOYSTICK_POSITION) ?: PrefsHelper.DEFAULT_JOYSTICK_POSITION
    }

    private fun setupUI() {
        // Size SeekBar (50% to 150%)
        binding.sizeSeekBar.progress = (currentSize * 100).toInt()
        binding.sizeValueText.text = "${(currentSize * 100).toInt()}%"

        binding.sizeSeekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                currentSize = progress / 100f
                binding.sizeValueText.text = "$progress%"
                updatePreview()
            }
            override fun onStartTrackingTouch(seekBar: SeekBar?) {}
            override fun onStopTrackingTouch(seekBar: SeekBar?) {}
        })

        // Opacity SeekBar (20% to 100%)
        binding.opacitySeekBar.progress = (currentOpacity * 100).toInt()
        binding.opacityValueText.text = "${(currentOpacity * 100).toInt()}%"

        binding.opacitySeekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                currentOpacity = progress / 100f
                binding.opacityValueText.text = "$progress%"
                updatePreview()
            }
            override fun onStartTrackingTouch(seekBar: SeekBar?) {}
            override fun onStopTrackingTouch(seekBar: SeekBar?) {}
        })

        // Position RadioGroup
        if (currentPosition == 0) {
            binding.positionLeft.isChecked = true
        } else {
            binding.positionRight.isChecked = true
        }

        binding.positionRadioGroup.setOnCheckedChangeListener { _, checkedId ->
            currentPosition = if (checkedId == R.id.positionLeft) 0 else 1
            updatePreview()
        }

        // Save Button
        binding.saveButton.setOnClickListener {
            saveSettings()
            Toast.makeText(this, R.string.settings_saved, Toast.LENGTH_SHORT).show()
            finish()
        }

        // Reset Button
        binding.resetButton.setOnClickListener {
            resetToDefaults()
        }
    }

    private fun updatePreview() {
        binding.joystickPreview.updateSettings(currentSize, currentOpacity, currentPosition)
    }

    private fun saveSettings() {
        PrefsHelper.write(PrefsHelper.JOYSTICK_SIZE, currentSize)
        PrefsHelper.write(PrefsHelper.JOYSTICK_OPACITY, currentOpacity)
        PrefsHelper.write(PrefsHelper.JOYSTICK_POSITION, currentPosition)
    }

    private fun resetToDefaults() {
        currentSize = PrefsHelper.DEFAULT_JOYSTICK_SIZE
        currentOpacity = PrefsHelper.DEFAULT_JOYSTICK_OPACITY
        currentPosition = PrefsHelper.DEFAULT_JOYSTICK_POSITION

        binding.sizeSeekBar.progress = (currentSize * 100).toInt()
        binding.opacitySeekBar.progress = (currentOpacity * 100).toInt()
        binding.positionLeft.isChecked = true

        updatePreview()
        Toast.makeText(this, R.string.settings_reset, Toast.LENGTH_SHORT).show()
    }

    override fun onSupportNavigateUp(): Boolean {
        finish()
        return true
    }
}
