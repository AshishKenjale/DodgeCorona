/*
 * objects of the game is extended from this abstract class
 * Bitmap 3 columns, 4 rows
 */
package com.project.coronawars

import android.graphics.Bitmap

abstract class GameObject(
    protected var image: Bitmap?,
    protected val rowCount: Int,
    protected val colCount: Int,
    var x: Int,
    var y: Int
) {
    protected var WIDTH : Int = 0
    protected var HEIGHT : Int = 0
    var width: Int = 0
    var height: Int = 0
    protected fun createSubImageAt(
        row: Int,
        col: Int
    ): Bitmap? { //createBitmap(bitmap, x, y, width, height) --> position in image
        return image?.let { Bitmap.createBitmap(it, col * width, row * height, width, height)  }
    }

    init {
        image?.let {
            WIDTH = it.width //width of whole image
            HEIGHT = it.height //height of whole image
            width = WIDTH / colCount //width of character
            height = HEIGHT / rowCount //height of character
        }
    }
}