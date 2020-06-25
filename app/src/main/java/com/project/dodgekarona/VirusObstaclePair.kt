package com.project.dodgekarona

import android.content.Context
import android.graphics.Canvas

/**
 * Created by Ashish Kenjale on 5/05/20.
 */
class VirusObstaclePair(yStart: Int, gameScene: GameScene, context: Context) {
    var yStart: Int
    var virus1: VirusObstacle
    var virus2: VirusObstacle

    init {
        this.yStart = yStart
        virus1 = VirusObstacle(gameScene, true, yStart, context)
        virus2 = VirusObstacle(gameScene, false, yStart, context)
    }

    fun incrementY(y: Float) {
        virus1.incrementY(y)
        virus2.incrementY(y)
    }

    fun draw(canvas: Canvas) {
        virus1.draw(canvas)
        virus2.draw(canvas)
    }
}