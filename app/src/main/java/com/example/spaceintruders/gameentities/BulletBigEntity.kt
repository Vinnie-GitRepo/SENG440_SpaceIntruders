package com.example.spaceintruders.gameentities

import android.content.res.Resources
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import com.example.spaceintruders.R

class BulletBigEntity(screenX: Int, screenY: Int, res: Resources, startPositionX: Float) : Bullet(screenX, screenY, startPositionX, 0.95f) {
    private var image: Bitmap = BitmapFactory.decodeResource(res, R.drawable.big_bullet)

    init {
        width = screenX/10
        height = screenX/10

        image = Bitmap.createScaledBitmap(image, width, height, false)
    }

    override fun updatePosition() {
        positionY -= -0.005f
    }

    override fun travelComplete(): Boolean {
        return positionY < 0
    }

    override fun draw(canvas: Canvas) {
        canvas.drawBitmap(image, (screenX.toFloat()*positionX)-(width/2), (screenY.toFloat()*positionY)-(height/2), paint)
    }
}