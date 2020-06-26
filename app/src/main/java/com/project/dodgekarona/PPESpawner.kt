package com.project.dodgekarona

import android.content.Context
import android.graphics.Canvas
import android.graphics.Point
import kotlin.random.Random

/**
 * Created by Ashish Kenjale on 5/05/20.
 */
class PPESpawner(gameScene: GameScene, context: Context) {
    private val gameScene: GameScene
    private val context: Context
    protected var maxPPECount: Int
    protected var existingPPE: Int
    var ppeList = mutableListOf<PPE>()
    var lastPPERemovalTime: Long

    fun spawnVirus() {
        var canSpawnPPE = false
        val now = System.currentTimeMillis()

        var elapsedTimeinSec = 0.0
        if (lastPPERemovalTime > -1) {
            elapsedTimeinSec = (now - lastPPERemovalTime) / 1000.0

            val PPE_SPAWN_TIME_GAP_IN_SECONDS = Random.nextInt(5, 10)
            if (elapsedTimeinSec >= PPE_SPAWN_TIME_GAP_IN_SECONDS) {
                canSpawnPPE = true
                lastPPERemovalTime = -1
            }
        }

        while (canSpawnPPE && ppeList.size < maxPPECount) {

            val startCoordinates = Point(GameSurface.screenWidth / 2, -95)
            val virus = PPE(gameScene, startCoordinates, 50, context)
            ppeList.add(0, virus)

            virus.registerSpawner(this)
        }
    }

    fun reportDeath(virus: PPE) {
        ppeList.remove(virus)
        updatePPERemovalTime()
    }

    private fun updatePPERemovalTime() {
        lastPPERemovalTime = System.currentTimeMillis()
    }

    fun updateAll(gameTick: Long) {
        ppeList.forEach { virus ->
            virus.update()
        }

        //check for exited from screen
        if (ppeList.size > 0) {
            val virusHost = ppeList[ppeList.size - 1]
            if (virusHost.center.y - 2 * virusHost.radius >= GameSurface.screenHeight) {
                ppeList.removeAt(ppeList.size - 1)
                updatePPERemovalTime()
            }
        }
    }

    fun drawAll(canvas: Canvas) {
        ppeList.forEach { virus ->
            virus.draw(canvas)
        }
    }

    init {
        this.gameScene = gameScene
        this.context = context
        maxPPECount = 1
        existingPPE = 0
        lastPPERemovalTime = System.currentTimeMillis()
    }
}