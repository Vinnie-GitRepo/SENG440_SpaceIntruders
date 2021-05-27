package com.example.spaceintruders.gameentities

import android.graphics.Canvas
import android.graphics.ColorFilter
import android.graphics.Paint
import android.graphics.PixelFormat
import android.graphics.drawable.Drawable
import java.text.ParsePosition

/**
 * Interface that objects that need to be updated each game tick should implement.
 */
abstract class Bullet(protected val screenX: Int, protected val screenY: Int, var positionX: Float, var positionY: Float) : Drawable() {
    protected var height: Int = 0
    protected var width: Int = 0
    protected val paint: Paint = Paint()

    /**
     * Changes attributes that update each game-tick.
     */
    abstract fun updatePosition()

    abstract fun travelComplete() : Boolean

    // Overriding methods from Drawable that aren't used.
    override fun setAlpha(alpha: Int) {}

    override fun setColorFilter(colorFilter: ColorFilter?) {}

    override fun getOpacity(): Int {
        return PixelFormat.OPAQUE
    }
}