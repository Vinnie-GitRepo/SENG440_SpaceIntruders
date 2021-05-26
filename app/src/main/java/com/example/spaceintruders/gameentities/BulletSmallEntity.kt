package com.example.spaceintruders.gameentities

import android.content.res.Resources
import android.graphics.*
import com.example.spaceintruders.R

class BulletSmallEntity(private val screenX: Int, private val screenY: Int, res: Resources, startPositionY: Float) : Bullet(startPositionY) {
    private var height: Int = 0
    private var width: Int = 0
    private val paint: Paint = Paint()
    private var image: Bitmap = BitmapFactory.decodeResource(res, R.drawable.small_bullet)

    init {
        width = screenX/10
        height = screenX/10

        image = Bitmap.createScaledBitmap(image, width, height, false)
    }

    override fun updatePosition() {
        position -= 0.01f
    }

    override fun draw(canvas: Canvas) {
        canvas.drawBitmap(image, (screenX.toFloat()*startPosition)-(width/2), (screenY.toFloat()*position)-(height/2), paint)
    }

    override fun setAlpha(alpha: Int) {}

    override fun setColorFilter(colorFilter: ColorFilter?) {}

    override fun getOpacity(): Int {
        return PixelFormat.OPAQUE
    }
}