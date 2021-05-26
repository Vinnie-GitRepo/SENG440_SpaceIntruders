package com.example.spaceintruders.gameentities

import android.graphics.drawable.Drawable

/**
 * Interface that objects that need to be updated each game tick should implement.
 */
abstract class Bullet(val startPosition: Float) : Drawable() {
    /**
     * Changes attributes that update each game-tick.
     */
    var position: Float = 0.9f

    abstract fun updatePosition()
}