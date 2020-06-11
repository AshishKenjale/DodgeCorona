package com.project.coronawars

import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_game_home.*

class GameHomeActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_game_home)

        startGameButton.setOnClickListener {
            val navigationIntent = Intent(this, GamePlayActivity::class.java)
            startActivity(navigationIntent)
        }

        aboutButton.setOnClickListener(View.OnClickListener {
            AlertDialog.Builder(this)
                .setTitle(getString(R.string.about_corona_wars))
                .setMessage(getString(R.string.about_game_text)                )
                .setNegativeButton(
                    "OK"
                ) { dialog, which -> }.show()
        })
    }
}
